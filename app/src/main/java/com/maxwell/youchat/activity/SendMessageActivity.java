package com.maxwell.youchat.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.maxwell.youchat.R;

public class SendMessageActivity extends AppCompatActivity {

    private EditText editText;

    private Button button;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_message);
        editText = findViewById(R.id.edit_text);
        button = findViewById(R.id.send_button);
        button.setOnClickListener((v -> {
            String text = editText.getText().toString();
            if(text == null || text.length() == 0) {
                editText.setHint("发送内容不能为空");
            } else {
                editText.setHint("");
                // TODO
                // 1. 将消息保存到数据库/发送到对方
                // 2. 添加到 ListView 显示
                // 3. 更新最后一条消息在 HomeFragment 显示
                // 4. 清空 editText
                editText.setText("");
            }
        }));
    }


}
