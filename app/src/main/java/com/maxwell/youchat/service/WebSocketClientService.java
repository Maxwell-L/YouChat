package com.maxwell.youchat.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.maxwell.youchat.YouChatApplication;
import com.maxwell.youchat.client.UserWebSocketClient;

import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

public class WebSocketClientService extends Service {

    private static final String WEB_SOCKET_HOST = "ws://10.0.2.2:8080/message";

    private static final String TAG = "WebSocketClient";

    private boolean isConnected;

    private static UserWebSocketClient client;

    @Override
    public void onCreate() {
        client = ((YouChatApplication) getApplication()).getClient();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("start running service...");
        Long userId = intent.getLongExtra("userId", 1L);
        if (client == null) {
            connectServer(userId);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        System.out.println("stop running service...");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new WebSocketBinder();
    }

    private void connectServer(Long userId) {
        URI uri = null;
        ((YouChatApplication) getApplication()).setUserId(userId);
        try {
            uri = new URI(WEB_SOCKET_HOST + "/" + userId);
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
                Intent intent = new Intent();
                intent.putExtra("message", message);
                intent.setAction("com.maxwell.youchat.service.WebSocketClientService");
                sendBroadcast(intent);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                ((YouChatApplication)getApplication()).setClient(null);
                Log.d("webSocketClient", reason);
                client = null;
            }

            @Override
            public void onError(Exception ex) {
                ((YouChatApplication)getApplication()).setClient(null);
                Log.d("webSocket", ex.toString());
                client = null;
            }
        };

        try {
            boolean connection = client.connectBlocking();
            if(connection) {
                ((YouChatApplication)getApplication()).setClient(client);
                Toast.makeText(this, "登录成功", Toast.LENGTH_LONG).show();
                this.isConnected = true;
            } else {
                Toast.makeText(this, "连接不上服务器...", Toast.LENGTH_LONG).show();
                client = null;
                this.isConnected = false;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d("webSocketClient", e.toString());
        }
        this.isConnected = false;
    }

    public class WebSocketBinder extends Binder {
        public WebSocketClientService getService() {
            return WebSocketClientService.this;
        }
    }
}
