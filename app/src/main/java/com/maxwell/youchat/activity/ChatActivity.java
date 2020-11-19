package com.maxwell.youchat.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.maxwell.youchat.R;
import com.maxwell.youchat.YouChatApplication;
import com.maxwell.youchat.adapter.MessageAdapter;
import com.maxwell.youchat.entity.DaoSession;
import com.maxwell.youchat.entity.Message;
import com.maxwell.youchat.entity.MessageDao;

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
    private MessageDao messageDao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        daoSession = ((YouChatApplication) getApplication()).getDaoSession();
        messageDao = daoSession.getMessageDao();

        editText = findViewById(R.id.edit_text);
        button = findViewById(R.id.send_button);
        listView = findViewById(R.id.message_list);
        // 从数据库加载消息并进行消息展示
        itemList = new ArrayList<>();
        List<Message> messageList = messageDao.queryRaw("WHERE RECEIVE_USER_ID = 1");
        for(Message message : messageList) {
            HashMap<String, Object> newItem = new HashMap<>();
            newItem.put("content", message.getContent());
            newItem.put("image", R.drawable.smile_face_24dp);
            itemList.add(newItem);
        }
        messageAdapter = new MessageAdapter(this, itemList);
        listView.setAdapter(messageAdapter);
        button.setOnClickListener((v -> {
            String text = editText.getText().toString();
            if(text == null || text.length() == 0) {
                editText.setHint("发送内容不能为空");
            } else {
                editText.setHint("");
                // TODO
                // 1. 将消息保存到数据库
                Message message = new Message(null, 0L, 1L, null, new Date().getTime(), text);
                messageDao.insert(message);
                // 把消息发送给对方/服务器
                // 2. 添加到 ListView 显示
                HashMap<String, Object> newItem = new HashMap<>();
                newItem.put("content", text);
                newItem.put("image", R.drawable.smile_face_24dp);
                itemList.add(newItem);
                messageAdapter.notifyDataSetChanged();
                // 3. 更新最后一条消息在 HomeFragment 显示
                editText.setText("");
            }
        }));
    }


}
