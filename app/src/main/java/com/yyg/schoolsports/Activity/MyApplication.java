package com.yyg.schoolsports.Activity;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.yyg.schoolsports.greendao.DaoMaster;
import com.yyg.schoolsports.greendao.DaoSession;

public class MyApplication extends Application {
    private static DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        // App启动时初始化一次数据库
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "sports.db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        daoSession = new DaoMaster(db).newSession();
    }

    public static DaoSession getDaoSession() {
        return daoSession;
    }
}