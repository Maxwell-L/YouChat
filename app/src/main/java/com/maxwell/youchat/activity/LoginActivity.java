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

public class LoginActivity extends AppCompatActivity {

    private static final String USERNAME_ME = "admin";
    private static final String PASSWORD_ME = "admin";

    private static final String USERNAME_YOU = "user";
    private static final String PASSWORD_YOU = "user";

    private Button loginButton;

    private EditText editTextUsername;

    private EditText editTextPassword;

    private DaoSession daoSession;
    private ChatMessageDao messageDao;
    private FriendDao friendDao;

    private String defaultServerAddress = "ws://10.0.2.2:8080/message";

    private UserWebSocketClient client;

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
     * 初始化数据库相关
     */
    private void initDb() {
        daoSession = ((YouChatApplication) getApplication()).getDaoSession();
        messageDao = daoSession.getChatMessageDao();
        friendDao = daoSession.getFriendDao();
    }

//    private boolean connectServer(String username, String password) {
//        // 简化服务端，不使用数据库，两个账号写死在代码中
//        if (!((Objects.equals(username, USERNAME_ME) && Objects.equals(password, PASSWORD_ME)) ||
//                (Objects.equals(username, USERNAME_YOU) && Objects.equals(password, PASSWORD_YOU)))) {
//            Toast.makeText(this, "用户名或密码错误!", Toast.LENGTH_LONG).show();
//            return false;
//        }
//        URI uri = null;
//        final Long userId = username.equals(USERNAME_ME) ? 0L : 1L;
//        ((YouChatApplication) getApplication()).setUserId(userId);
//        try {
//            uri = new URI(defaultServerAddress + "/" + userId);
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//            Log.d("webSocketClient", e.toString());
//        }
//
//
//        client = new UserWebSocketClient(uri) {
//            @Override
//            public void onOpen(ServerHandshake handshakedata) {
//                Log.d("webSocketClient", String.valueOf(handshakedata.getHttpStatus()));
//            }
//
//            @Override
//            public void onMessage(String message) {
//                Log.d("webSocketClient", message);
//
//            }
//
//            @Override
//            public void onClose(int code, String reason, boolean remote) {
//                ((YouChatApplication)getApplication()).setClient(null);
//                Log.d("webSocketClient", reason);
//                client = null;
//            }
//
//            @Override
//            public void onError(Exception ex) {
//                ((YouChatApplication)getApplication()).setClient(null);
//                Log.d("webSocket", ex.toString());
//                client = null;
//            }
//        };
//
//        try {
//            boolean connection = client.connectBlocking();
//            if(connection) {
//                ((YouChatApplication)getApplication()).setClient(client);
//                Toast.makeText(this, "登录成功", Toast.LENGTH_LONG).show();
//                return true;
//            } else {
//                Toast.makeText(this, "连接不上服务器...", Toast.LENGTH_LONG).show();
//                client = null;
//                return false;
//            }
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//            Log.d("webSocketClient", e.toString());
//        }
//        return false;
//    }

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
