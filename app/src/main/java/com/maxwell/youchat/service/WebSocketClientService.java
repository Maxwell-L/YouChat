package com.maxwell.youchat.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.maxwell.youchat.YouChatApplication;
import com.maxwell.youchat.activity.ChatActivity;
import com.maxwell.youchat.activity.LoginActivity;
import com.maxwell.youchat.client.UserWebSocketClient;
import com.maxwell.youchat.controller.ActivityController;

import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketClientService extends Service {

    private static final String WEB_SOCKET_HOST = "ws://8.135.21.168/message";
//    private static final String WEB_SOCKET_HOST = "ws://10.0.2.2/message";

    private static final String TAG = "WebSocketClient";

    private boolean isConnected;

    private ConcurrentHashMap<Long, Boolean> isFriendOnlineMap;

    private static UserWebSocketClient client;

    private Thread heartbeatCheckThread;

    @Override
    public void onCreate() {
        client = ((YouChatApplication) getApplication()).getClient();
        isFriendOnlineMap = ((YouChatApplication) getApplication()).getIsFriendOnlineMap();
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
        return null;
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
                Log.d(TAG, String.valueOf(handshakedata.getHttpStatus()));
            }

            @Override
            public void onMessage(String message) {
                if (message.charAt(0) != '{') {
                    Boolean isFriendOnline = Boolean.valueOf(message);
                    isFriendOnlineMap.put(userId ^ 1L, isFriendOnline);
                }
                Intent intent = new Intent();
                intent.putExtra("message", message);
                intent.setAction("com.maxwell.youchat.service.WebSocketClientService");
                sendBroadcast(intent);
                if (message.charAt(0) == '{' && !ActivityController.isActivityExist(ChatActivity.class)) {
                    Log.d(TAG, "message -> queue");
                    ((YouChatApplication) getApplication()).getTempMessageQueue().offer(message);
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                heartbeatCheckThread.interrupt();
                ((YouChatApplication)getApplication()).setClient(null);
                Log.d(TAG, reason);
                client = null;
                ActivityController.removeAllActivity();
            }

            @Override
            public void onError(Exception ex) {
                heartbeatCheckThread.interrupt();
                ((YouChatApplication)getApplication()).setClient(null);
                Log.d(TAG, ex.toString());
                client = null;
            }
        };

        try {
            boolean connection = client.connectBlocking();
            if(connection) {
                ((YouChatApplication) getApplication()).setClient(client);
                ((YouChatApplication) getApplication()).setUserId(userId);
                Toast.makeText(this, "登录成功", Toast.LENGTH_LONG).show();
                this.isConnected = true;
                // 开启子线程进行心跳检测, 测试好友是否在线
                heartbeatCheckThread = new Thread(() -> {
                    while (client != null && !Thread.currentThread().isInterrupted()) {
                        try {
                            client.send(String.valueOf(userId ^ 1L));
                        } catch (Exception e) {
                            break;
                        }
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            Log.d(TAG, "Stop Heartbeat Check...");
                            break;
                        }
                    }
                });
                heartbeatCheckThread.start();
            } else {
                Toast.makeText(this, "连接不上服务器...", Toast.LENGTH_LONG).show();
                client = null;
                this.isConnected = false;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d(TAG, e.toString());
        }
        this.isConnected = false;
    }

}
