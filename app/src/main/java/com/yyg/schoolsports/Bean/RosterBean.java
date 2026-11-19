package com.yyg.schoolsports.Bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class RosterBean {
    @Id
    private String cardNum;     // 卡号（主键）
    private String studentId;   // 学籍号
    private String studentName; // 姓名
    @Generated(hash = 1296497056)
    public RosterBean(String cardNum, String studentId, String studentName) {
        this.cardNum = cardNum;
        this.studentId = studentId;
        this.studentName = studentName;
    }
    @Generated(hash = 688501132)
    public RosterBean() {
    }
    public String getCardNum() {
        return this.cardNum;
    }
    public void setCardNum(String cardNum) {
        this.cardNum = cardNum;
    }
    public String getStudentId() {
        return this.studentId;
    }
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
    public String getStudentName() {
        return this.studentName;
    }
    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }
}