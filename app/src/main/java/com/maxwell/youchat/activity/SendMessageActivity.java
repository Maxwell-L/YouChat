package com.maxwell.youchat.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.maxwell.youchat.R;
import com.maxwell.youchat.adapter.MessageAdapter;
import com.maxwell.youchat.viewmodel.SendMessageActivityViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SendMessageActivity extends AppCompatActivity {

    private EditText editText;
    private Button button;
    private ListView listView;
    private SendMessageActivityViewModel viewModel;
    private List<HashMap<String, Object>> itemList;
    private MessageAdapter messageAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_message);
        viewModel = new ViewModelProvider(this).get(SendMessageActivityViewModel.class);
        editText = findViewById(R.id.edit_text);
        button = findViewById(R.id.send_button);
        listView = findViewById(R.id.message_list);
        itemList = viewModel.getItemList();
        messageAdapter = new MessageAdapter(this, itemList);
        listView.setAdapter(messageAdapter);
        button.setOnClickListener((v -> {
            String text = editText.getText().toString();
            if(text == null || text.length() == 0) {
                editText.setHint("发送内容不能为空");
            } else {
                editText.setHint("");
                // TODO
                // 1. 将消息保存到数据库/发送到对方

                // 2. 添加到 ListView 显示
                HashMap<String, Object> newItem = new HashMap<>();
                newItem.put("content", text);
                newItem.put("image", R.drawable.smile_face_24dp);
//                itemList.add(newItem);
                viewModel.addItem(newItem);
                messageAdapter.notifyDataSetChanged();
                // 3. 更新最后一条消息在 HomeFragment 显示
                editText.setText("");
            }
        }));
    }


}
