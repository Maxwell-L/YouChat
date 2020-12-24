package com.maxwell.youchat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.maxwell.youchat.R;
import com.maxwell.youchat.YouChatApplication;
import com.maxwell.youchat.client.UserWebSocketClient;
import com.maxwell.youchat.entity.ChatMessageDao;
import com.maxwell.youchat.entity.DaoSession;
import com.maxwell.youchat.entity.FriendDao;
import com.maxwell.youchat.service.WebSocketClientService;

import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

public class LoginActivity extends BaseActivity {

    private static final String USERNAME_ME = "admin";
    private static final String PASSWORD_ME = "admin";

    private static final String USERNAME_YOU = "user";
    private static final String PASSWORD_YOU = "user";

    private Button loginButton;

    private EditText editTextUsername;

    private EditText editTextPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // 已登录状态直接跳转到主页面
        if(((YouChatApplication)getApplication()).getClient() != null) {
            startMainActivity();
        }
        super.onCreate(savedInstanceState);
        // 绑定页面控件
        setContentView(R.layout.activity_login);
        View view = findViewById(R.id.login_container);
        view.getBackground().setAlpha(200);
        loginButton = findViewById(R.id.login_button);
        editTextUsername = findViewById(R.id.username);
        editTextPassword = findViewById(R.id.password);

        loginButton.setOnClickListener(v -> {
            // 1. 验证用户名密码
            String username = editTextUsername.getText().toString();
            String password = editTextPassword.getText().toString();
            // 2. 未输入用户名密码时按钮设置不可用
            Long userId = null;
            userId = identityVerification(username, password);
            if (userId == null) {
                return;
            }
            // 把 userId 传给 Service
            Intent service = new Intent(this, WebSocketClientService.class);
            service.putExtra("userId", userId);
            this.startService(service);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    /**
     * 跳转 MainActivity
     */
    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 身份验证
     * @param username
     * @param password
     * @return 用户 ID
     */
    private Long identityVerification(String username, String password) {
        if (Objects.equals(username, USERNAME_ME) && Objects.equals(password, PASSWORD_ME)) {
            return 0L;
        }
        if (Objects.equals(username, USERNAME_YOU) && Objects.equals(password, PASSWORD_YOU)) {
            return 1L;
        }
        if (Objects.equals(username, null) || Objects.equals(password, null)) {
            Toast.makeText(this, "用户名或密码不能为空!", Toast.LENGTH_LONG).show();
            return null;
        }
        Toast.makeText(this, "用户名或密码错误!", Toast.LENGTH_LONG).show();
        return null;
    }
}
