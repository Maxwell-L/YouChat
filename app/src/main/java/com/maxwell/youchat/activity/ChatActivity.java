package com.maxwell.youchat.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONObject;
import com.maxwell.youchat.R;
import com.maxwell.youchat.YouChatApplication;
import com.maxwell.youchat.adapter.ChatMessageAdapter;
import com.maxwell.youchat.client.UserWebSocketClient;
import com.maxwell.youchat.entity.ChatMessage;
import com.maxwell.youchat.entity.ChatMessageDao;
import com.maxwell.youchat.entity.DaoSession;
import com.maxwell.youchat.entity.Friend;
import com.maxwell.youchat.entity.FriendDao;

import org.java_websocket.handshake.ServerHandshake;


import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ChatActivity extends AppCompatActivity {

    private Context context;
    private EditText editText;
    private Button button;
    private ListView listView;

    private List<ChatMessage> messageList;
    private ChatMessageAdapter chatMessageAdapter;

    private DaoSession daoSession;
    private ChatMessageDao messageDao;
    private FriendDao friendDao;

    private MessageReceiver receiver;

    private UserWebSocketClient client;
    private Long userId;
    private Long friendId;
//    private String defaultServerAddress = "ws://8.135.101.106:80/message";
    private String defaultServerAddress = "ws://10.0.2.2:8080/message";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        client = ((YouChatApplication)getApplication()).getClient();
        userId = ((YouChatApplication)getApplication()).getUserId();
        friendId = userId ^ 1L;
        receiver = new MessageReceiver();
        initDb();
        initView();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.maxwell.youchat.service.WebSocketClientService");
        this.registerReceiver(receiver, filter);
    }

    /**
     * 初始化数据库相关
     */
    private void initDb() {
        daoSession = ((YouChatApplication) getApplication()).getDaoSession();
        messageDao = daoSession.getChatMessageDao();
        friendDao = daoSession.getFriendDao();
    }

    /**
     * 初始化视图
     */
    private void initView() {
        context = this;
        editText = findViewById(R.id.edit_text);
        button = findViewById(R.id.send_button);
        listView = findViewById(R.id.message_list);
        // 从数据库加载消息并进行消息展示
        setListViewAdapter();
        button.setOnClickListener(new ButtonOnClickListener());
    }

    /**
     * 给 ListView 绑定 adapter
     *
     */
    private void setListViewAdapter() {
        String whereSQL = "WHERE RECEIVE_USER_ID = " + userId + " OR SEND_USER_ID = " + userId;
        messageList = messageDao.queryRaw(whereSQL);
        chatMessageAdapter = new ChatMessageAdapter(this, messageList);
        listView.setAdapter(chatMessageAdapter);
    }

    /**
     * 发送按键点击事件 成员内部类
     */
    private class ButtonOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String text = editText.getText().toString();
            if(text == null || text.length() == 0) {
                editText.setHint("发送内容不能为空");
            } else {
                editText.setHint("");
                // TODO
                // 1. 将消息保存到数据库
                ChatMessage chatMessage = new ChatMessage(null, userId, userId ^ 1L, null, new Date().getTime(), text);
                Long messageId = messageDao.insert(chatMessage);
                // 把消息发送给服务器
                if(client != null) {
                    client.send(chatMessage.toString());
                } else {
                    Toast.makeText(context, "登录过期，请重新登录...", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ChatActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
                // 2. 添加到 ListView 显示
                messageList.add(chatMessage);
                chatMessageAdapter.notifyDataSetChanged();
                // 3. 更新最后一条消息在 HomeFragment 显示
                List<Friend> friendList = friendDao.queryRaw("WHERE _id = " + friendId);
                if(friendList == null || friendList.size() == 0) {
                    Friend friend = new Friend(friendId, "用户" + friendId, 0, null, messageId);
                    friendDao.insert(friend);
                } else {
                    Friend friend = friendList.get(0);
                    friend.setLastMessageId(messageId);
                    friendDao.update(friend);
                }
                editText.setText("");
            }
        }
    }

    private class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            String message = bundle.getString("message");
            ChatMessage chatMessage = JSONObject.parseObject(message, ChatMessage.class);
            chatMessage.setContent("服务器:" + chatMessage.getContent());
            Long messageId = messageDao.insert(chatMessage);

            // 1. 添加到 ListView 显示
            messageList.add(chatMessage);
            chatMessageAdapter.notifyDataSetChanged();
            // 3. 更新最后一条消息在 HomeFragment 显示
            List<Friend> friendList = friendDao.queryRaw("WHERE _id = " + friendId);
            if(friendList == null || friendList.size() == 0) {
                Friend friend = new Friend(friendId, "用户" + friendId, 0, null, messageId);
                friendDao.insert(friend);
            } else {
                Friend friend = friendList.get(0);
                friend.setLastMessageId(messageId);
                friendDao.update(friend);
            }
        }
    }

}
