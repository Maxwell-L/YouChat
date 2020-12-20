package com.maxwell.youchat.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSONObject;
import com.maxwell.youchat.YouChatApplication;
import com.maxwell.youchat.client.UserWebSocketClient;
import com.maxwell.youchat.entity.ChatMessage;
import com.maxwell.youchat.entity.Friend;

import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class UserWebSocketClientService extends Service {

    private static final String WEB_SOCKET_HOST = "ws://10.0.2.2:8080/message";

    private static final String TAG = "WebSocketClient";

    private static UserWebSocketClient client;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        connectServer();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new WebSocketBinder();
    }

    public void connectServer() {
        URI uri = null;
        try {
            uri = new URI(WEB_SOCKET_HOST);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            Log.d(TAG, e.toString());
        }

        client = new UserWebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                Log.d(TAG, "HttpStatus:" + String.valueOf(handshakedata.getHttpStatus()) + " Connect Success");
            }

            @Override
            public void onMessage(String message) {
                Log.d(TAG, message);

            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.d(TAG, reason);
                client = null;
            }

            @Override
            public void onError(Exception ex) {
                Log.d(TAG, ex.toString());
                client = null;
            }
        };

        try {
            boolean connection = client.connectBlocking();
            if(connection) {
                
                ((YouChatApplication) getApplication()).setClient(client);
                Toast.makeText(this, "登录成功", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "连接不上服务器...", Toast.LENGTH_LONG).show();
                client = null;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d("webSocketClient", e.toString());
        }
    }

    public class WebSocketBinder extends Binder {
        public UserWebSocketClientService getService() {
            return UserWebSocketClientService.this;
        }
    }
}
