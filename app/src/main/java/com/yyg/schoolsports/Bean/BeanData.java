package com.yyg.schoolsports.Bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;
import java.util.Date;
@Entity
public class BeanData implements Serializable {
    private static final long serialVersionUID = 1L;  //必须在@ID前面定义
    @Id(autoincrement = true)  // <--- 加上自增主键
    private Long id;              //null（自增主键）
    private String date;          //currentDate（时间）
    private String studentID;     //（学号）
    private String studentName;   //（姓名）
    private String gradeNum;      //（年级）
    private String classNum;      //（班级）
    private String cardNum;       //cardNumStr（卡号/测试ID）
    private String age;           //（年龄）
    private String sex;           //（性别）
    private String ethnic;        //（民族）
    private String photo;         //（照片）
    private String ID;            //（身份证号）
    private String birthDate;     //（出生日期）
    private String phone;         //（电话）
    private String studentSource; //（生源）
    private String studentAddress; //（地址）
    private String testProject;  // 测试项目（如：身高、肺活量、50米跑）
    private String testData;     // 成绩数据（核心成绩）
    private String status;       // status（上传状态）


    @Generated(hash = 360903985)
    public BeanData(Long id, String date, String studentID, String studentName,
            String gradeNum, String classNum, String cardNum, String age,
            String sex, String ethnic, String photo, String ID, String birthDate,
            String phone, String studentSource, String studentAddress,
            String testProject, String testData, String status) {
        this.id = id;
        this.date = date;
        this.studentID = studentID;
        this.studentName = studentName;
        this.gradeNum = gradeNum;
        this.classNum = classNum;
        this.cardNum = cardNum;
        this.age = age;
        this.sex = sex;
        this.ethnic = ethnic;
        this.photo = photo;
        this.ID = ID;
        this.birthDate = birthDate;
        this.phone = phone;
        this.studentSource = studentSource;
        this.studentAddress = studentAddress;
        this.testProject = testProject;
        this.testData = testData;
        this.status = status;
    }
    @Generated(hash = 1639626848)
    public BeanData() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getDate() {
        return this.date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getStudentID() {
        return this.studentID;
    }
    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }
    public String getStudentName() {
        return this.studentName;
    }
    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }
    public String getGradeNum() {
        return this.gradeNum;
    }
    public void setGradeNum(String gradeNum) {
        this.gradeNum = gradeNum;
    }
    public String getClassNum() {
        return this.classNum;
    }
    public void setClassNum(String classNum) {
        this.classNum = classNum;
    }
    public String getCardNum() {
        return this.cardNum;
    }
    public void setCardNum(String cardNum) {
        this.cardNum = cardNum;
    }
    public String getAge() {
        return this.age;
    }
    public void setAge(String age) {
        this.age = age;
    }
    public String getSex() {
        return this.sex;
    }
    public void setSex(String sex) {
        this.sex = sex;
    }
    public String getEthnic() {
        return this.ethnic;
    }
    public void setEthnic(String ethnic) {
        this.ethnic = ethnic;
    }
    public String getPhoto() {
        return this.photo;
    }
    public void setPhoto(String photo) {
        this.photo = photo;
    }
    public String getID() {
        return this.ID;
    }
    public void setID(String ID) {
        this.ID = ID;
    }
    public String getBirthDate() {
        return this.birthDate;
    }
    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }
    public String getPhone() {
        return this.phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getStudentSource() {
        return this.studentSource;
    }
    public void setStudentSource(String studentSource) {
        this.studentSource = studentSource;
    }
    public String getStudentAddress() {
        return this.studentAddress;
    }
    public void setStudentAddress(String studentAddress) {
        this.studentAddress = studentAddress;
    }
    public String getTestProject() {
        return this.testProject;
    }
    public void setTestProject(String testProject) {
        this.testProject = testProject;
    }
    public String getTestData() {
        return this.testData;
    }
    public void setTestData(String testData) {
        this.testData = testData;
    }
    public String getStatus() {
        return this.status;
    }
    public void setStatus(String status) {
        this.status = status;
    }






}
