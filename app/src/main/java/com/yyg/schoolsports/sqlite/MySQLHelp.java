package com.yyg.schoolsports.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MySQLHelp extends SQLiteOpenHelper {
    private String module = "create table module(" +
            "id Integer PRIMARY KEY AUTOINCREMENT,"+
            "date text," +//时间
            "studentID text," +//学籍号
            "studentName text," +//姓名
            "gradeNum text," +//年级编号
            "classNum text," +//班级编号
            "cardNum text," +//卡号
            "age text," +//年龄
            "sex text," +//性别
            "ethnic text," +//民族代码
            "photo text," +//照片
            "ID text," +//身份证号
            "birthDate text," +//出生年月
            "phone text," +//联系方式
            "studentSource text," +//学生来源
            "studentAddress text," +//家庭住址
            "height text," +//身高
            "weight text," +//体重
            "HeightweiScore text," +//身高体重分
            "bmi text," +//BMI
            "bmiScore text," +//BMI成绩
            "bmiRank text," +//BMI等级
            "vital text," +//肺活量
            "vitalScore text," +//肺活量成绩
            "vitalRank text," +//肺活量等级
            "meter50 text," +//50米跑
            "meter50Score text," +//50米跑成绩
            "meter50Rank text," +//50米跑等级
            "sitreach text," +//坐位体前屈
            "sitreachScore text," +//坐位体前屈成绩
            "sitreachRank text," +//坐位体前屈等级
            "skip text," +//跳绳
            "skipScore text," +//跳绳成绩
            "skipRank text," +//跳绳等级
            "situp text," +//仰卧起坐
            "situpScore text," +//仰卧起坐成绩
            "situppRank text," +//仰卧起坐等级
            "meter508 text," +//仰50mX8往返跑
            "meter508Score text," +//50mX8往返跑成绩
            "meter508Rank text," +//50mX8往返跑等级
            "longjump text," +//立定跳远
            "longjumpScore text," +//立定跳远成绩
            "longjumpRank text," +//立定跳远等级
            "meter800 text," +//50mX8往返跑
            "meter800Score text," +//50mX8往返跑成绩
            "meter800Rank text," +//50mX8往返跑等级
            "pullup text," +//引体向上
            "pullupScore text," +//引体向上成绩
            "pullupRank text," +//引体向上等级
            "steptest text," +//台阶测试
            "steptestScore text," +//台阶测试成绩
            "steptestRank text," +//台阶测试等级
            "grip text," +//握力
            "gripScore text," +//握力成绩
            "gripRank text," +//握力等级
            "leapfrog text," +//三级蛙跳
            "leapfrogScore text," +//三级蛙跳成绩
            "leapfrogRank text," +//三级蛙跳等级
            "finger text," +//指脉
            "fingerScore text," +//指脉成绩
            "fingerRank text," +//指脉等级
            "stand text," +//单脚站立
            "standScore text," +//单脚站立成绩
            "standRank text," +//单脚站立等级
            "pushup text," +//俯卧撑
            "pushupScore text," +//俯卧撑成绩
            "pushupRank text," +//俯卧撑等级
            "verJump text," +//纵跳
            "verJumpScore text," +//纵跳成绩
            "verJumpRank text," +//纵跳等级
            "reaction text," +//反应时
            "reactionScore text," +//反应时成绩
            "reactionRank text," +//反应时等级
            "basketball text," +//篮球
            "basketballScore text," +//篮球成绩
            "basketballRank text," +//篮球等级
            "football text," +//足球
            "footballScore text," +//足球成绩
            "footballRank text," +//足球等级
            "medicine text," +//实心球
            "medicineScore text," +//实心球成绩
            "medicineRank text," +//实心球等级
            "volleyball text," +//排球
            "volleyballScore text," +//排球成绩
            "volleyballRank text," +//排球等级
            "footkick text," +//足球颠球
            "footkickScore text," +//足球颠球成绩
            "footkickRank text," +//足球颠球等级
            "back text," +//背力
            "backScore text," +//背力成绩
            "backRank text," +//背力等级
            "shuttlecock text," +//踢毽子
            "shuttlecockScore text," +//踢毽子成绩
            "shuttlecockRank text," +//踢毽子等级
            "sargentjump text," +//摸高
            "sargentjumpScore text," +//摸高成绩
            "sargentjumpRank text);";//摸高等级

    public MySQLHelp(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(module);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
