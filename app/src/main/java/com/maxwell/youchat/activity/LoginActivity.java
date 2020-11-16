package com.maxwell.youchat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.maxwell.youchat.R;

public class LoginActivity extends AppCompatActivity {

    private Button loginButton;

    private EditText editTextUsername;

    private EditText editTextPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        View view = findViewById(R.id.login_container);
        view.getBackground().setAlpha(200);
        loginButton = (Button)findViewById(R.id.login_button);
        // TODO
        // 1. 验证用户名密码
        // 2. 未输入用户名密码时按钮设置不可用
        // 3. 引入数据库后加入注册功能
        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
