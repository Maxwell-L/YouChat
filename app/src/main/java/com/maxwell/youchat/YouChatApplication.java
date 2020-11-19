package com.maxwell.youchat;

import android.app.Application;

import com.maxwell.youchat.entity.DaoMaster;
import com.maxwell.youchat.entity.DaoSession;

import org.greenrobot.greendao.database.Database;

public class YouChatApplication extends Application {

    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "youchat-db");
        Database db = helper.getWritableDb();

        daoSession = new DaoMaster(db).newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}
