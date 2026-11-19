package com.yyg.schoolsports.greendao;

import com.yyg.schoolsports.MyApplication;  // 导入你的全局Application
import com.yyg.schoolsports.Bean.BeanData;

import java.util.List;

public class BeanDataDaoMaster {

    // 全部改成 MyApplication.getDaoSession()
    public static void insertGood(BeanData entry) {
        MyApplication.getDaoSession().getBeanDataDao().insertOrReplace(entry);
    }

    public static void insertGood(List<BeanData> list) {
        MyApplication.getDaoSession().getBeanDataDao().insertOrReplaceInTx(list);
    }

    public static void deleteGood(List<BeanData> list) {
        MyApplication.getDaoSession().getBeanDataDao().deleteInTx(list);
    }

    public static void deleteGood(BeanData entry) {
        MyApplication.getDaoSession().getBeanDataDao().delete(entry);
    }

    public static void updateGood(BeanData entry) {
        MyApplication.getDaoSession().getBeanDataDao().update(entry);
    }

    public static void updateGood(List<BeanData> list) {
        MyApplication.getDaoSession().getBeanDataDao().updateInTx(list);
    }

    public static List<BeanData> queryGoodAll() {
        return MyApplication.getDaoSession().getBeanDataDao().loadAll();
    }
}