package com.maxwell.youchat.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.maxwell.youchat.R;
import com.maxwell.youchat.YouChatApplication;
import com.maxwell.youchat.adapter.MessageAdapter;
import com.maxwell.youchat.client.UserWebSocketClient;
import com.maxwell.youchat.entity.ChatMessage;
import com.maxwell.youchat.entity.ChatMessageDao;
import com.maxwell.youchat.entity.DaoSession;
import com.maxwell.youchat.entity.Friend;
import com.maxwell.youchat.entity.FriendDao;
import com.maxwell.youchat.entity.MessageDao;
import com.maxwell.youchat.service.SendMessageService;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private EditText editText;
    private Button button;
    private ListView listView;
    private List<HashMap<String, Object>> itemList;
    private MessageAdapter messageAdapter;
    private DaoSession daoSession;
    private ChatMessageDao messageDao;
    private FriendDao friendDao;

    private UserWebSocketClient client;
    private SendMessageService.UserWebSocketClientBinder binder;
    private SendMessageService sendMessageService;


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e("ChatActivity", "绑定服务");
            binder = (SendMessageService.UserWebSocketClientBinder) service;
            sendMessageService = binder.getService();
            client = sendMessageService.webSocketClient;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initDb();
        initView();

//        bindService();
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
        editText = findViewById(R.id.edit_text);
        button = findViewById(R.id.send_button);
        listView = findViewById(R.id.message_list);
        // 从数据库加载消息并进行消息展示
        itemList = new ArrayList<>();
        setListViewAdapter();
        button.setOnClickListener(new ButtonOnClickListener());
    }

    /**
     * 给 ListView 绑定 adapter
     *
     */
    private void setListViewAdapter() {
        List<ChatMessage> chatMessageList = messageDao.queryRaw("WHERE RECEIVE_USER_ID = 1");
        for(ChatMessage chatMessage : chatMessageList) {
            HashMap<String, Object> newItem = new HashMap<>();
            newItem.put("content", chatMessage.getContent());
            newItem.put("image", R.drawable.smile_face_24dp);
            itemList.add(newItem);
        }
        messageAdapter = new MessageAdapter(this, itemList);
        listView.setAdapter(messageAdapter);
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
                // 2. 添加到 ListView 显示
                HashMap<String, Object> newItem = new HashMap<>();
                newItem.put("content", text);
                newItem.put("image", R.drawable.smile_face_24dp);
                itemList.add(newItem);
                messageAdapter.notifyDataSetChanged();
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

    /**
     * 绑定 Service
     */
    private void bindService() {
        Intent intent = new Intent(this, SendMessageService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }
}
