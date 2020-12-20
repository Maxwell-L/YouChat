package com.maxwell.youchat.activity;

import android.content.Context;
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

    private UserWebSocketClient client;
//    private String defaultServerAddress = "ws://8.135.101.106:80/message";
    private String defaultServerAddress = "ws://10.0.2.2:8080/message";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initDb();
        initView();
        client = ((YouChatApplication)getApplication()).getClient();
//        connectServer();
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
        messageList = messageDao.queryRaw("WHERE RECEIVE_USER_ID = 1 OR SEND_USER_ID = 1");
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
                ChatMessage chatMessage = new ChatMessage(null, 0L, 1L, null, new Date().getTime(), text);
                Long messageId = messageDao.insert(chatMessage);
                // 把消息发送给对方/服务器
                if(client != null) {
                    client.send(chatMessage.toString());
                } else {
                    Toast.makeText(context, "not connect", Toast.LENGTH_LONG).show();
                }
                // 2. 添加到 ListView 显示
                messageList.add(chatMessage);
                chatMessageAdapter.notifyDataSetChanged();
                // 3. 更新最后一条消息在 HomeFragment 显示
                // 引入好友功能后修改
                List<Friend> friendList = friendDao.queryRaw("WHERE _id = 1");
                if(friendList == null || friendList.size() == 0) {
                    Friend friend = new Friend(null, "凉皮", 0, null, messageId);
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

    private void connectServer() {
        URI uri = null;
        try {
            uri = new URI(defaultServerAddress);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            Log.d("webSocketClient", e.toString());
        }

        client = new UserWebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                Log.d("webSocketClient", String.valueOf(handshakedata.getHttpStatus()));
            }

            @Override
            public void onMessage(String message) {
                Log.d("webSocketClient", message);

                ChatMessage chatMessage = JSONObject.parseObject(message, ChatMessage.class);
                chatMessage.setContent("服务器:" + chatMessage.getContent());
                Long messageId = messageDao.insert(chatMessage);

                // 1. 添加到 ListView 显示
                messageList.add(chatMessage);
                chatMessageAdapter.notifyDataSetChanged();
                // 3. 更新最后一条消息在 HomeFragment 显示
                // 引入好友功能后修改
                List<Friend> friendList = friendDao.queryRaw("WHERE _id = 1");
                if(friendList == null || friendList.size() == 0) {
                    Friend friend = new Friend(null, "凉皮", 0, null, messageId);
                    friendDao.insert(friend);
                } else {
                    Friend friend = friendList.get(0);
                    friend.setLastMessageId(messageId);
                    friendDao.update(friend);
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.d("webSocketClient", reason);
                client = null;
            }

            @Override
            public void onError(Exception ex) {
                Log.d("webSocket", ex.toString());
                client = null;
            }
        };

        try {
            boolean connection = client.connectBlocking(1000, TimeUnit.MICROSECONDS);
            if(connection) {
                Toast.makeText(this, "connect success", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "connect fail", Toast.LENGTH_LONG).show();
                client = null;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d("webSocketClient", e.toString());
        }
    }

}
