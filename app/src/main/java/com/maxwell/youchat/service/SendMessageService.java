package com.maxwell.youchat.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.maxwell.youchat.client.UserWebSocketClient;

import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class SendMessageService extends Service {

    private URI uri = URI.create("ws://127.0.0.1:8080/message"); // ws://127.0.0.1:8080/message
    public UserWebSocketClient webSocketClient;
    private UserWebSocketClientBinder binder = new UserWebSocketClientBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class UserWebSocketClientBinder extends Binder {
        public SendMessageService getService() {
            return SendMessageService.this;
        }
    }

    /**
     * 初始化 WebSocket
     */
    private void initWebSocket() {
        webSocketClient = new UserWebSocketClient(uri) {
            @Override
            public void onMessage(String message) {
                Log.e("SendMessageService", "message");
                Intent intent = new Intent();
                intent.setAction("");
            }

            @Override
            public void onOpen(ServerHandshake handshakedata) {
                super.onOpen(handshakedata);
            }
        };
    }
}
