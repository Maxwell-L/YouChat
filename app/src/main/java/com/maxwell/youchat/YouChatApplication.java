package com.maxwell.youchat;

import android.app.Application;

import com.maxwell.youchat.client.UserWebSocketClient;
import com.maxwell.youchat.entity.DaoMaster;
import com.maxwell.youchat.entity.DaoSession;

import org.greenrobot.greendao.database.Database;

public class YouChatApplication extends Application {

    private DaoSession daoSession;

    private UserWebSocketClient client;

    private Long userId;

    private static YouChatApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "youchat-db");
        Database db = helper.getWritableDb();

        daoSession = new DaoMaster(db).newSession();
        instance = this;
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public void setClient(UserWebSocketClient client) {
        this.client = client;
    }

    public UserWebSocketClient getClient() {
        return client;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public static YouChatApplication getInstance() {
        return instance;
    }
}
