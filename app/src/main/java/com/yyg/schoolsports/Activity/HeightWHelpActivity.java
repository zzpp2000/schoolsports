package com.yyg.schoolsports.Activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;

import com.yyg.schoolsports.Bean.BeanData;
import com.yyg.schoolsports.R;
import com.yyg.schoolsports.greendao.BeanDataDaoMaster;
import com.yyg.schoolsports.utils.ExcelUtil;
import com.yyg.schoolsports.utils.UDiskReceiver;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class HeightWHelpActivity extends AppCompatActivity {
    private VideoView mVideoView;
    private List<String> mVideoList;
    private int mCurrentPosition;
    UDiskReceiver uDiskReceiver = new UDiskReceiver();
    private int permission;
    private Button btn_OpenVideo;
    private Button btn_import;
    private Button btn_export;

    private String filePath =  Environment.getExternalStorageDirectory().getAbsolutePath()+"/Download";
    private String excelFilePath;
    private String FileName;
    String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private int REQUEST_PERMISSION_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heightwhelp);
        requestPermission();
        mVideoView = findViewById(R.id.hwvideo_view);
        btn_OpenVideo = findViewById(R.id.Btn_hwvideo);
        btn_import = findViewById(R.id.Btn_hwimport);
        btn_export = findViewById(R.id.Btn_hwexport);

        FileName = ExcelUtil.getNowTime();

        mVideoList = new ArrayList<>();

        btn_OpenVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("打开视频");
                if(uDiskReceiver.uDiskFlag == true){
                    mVideoList = uDiskReceiver.getVideo(HeightWHelpActivity.this);
                    btn_import.setVisibility(View.GONE);
                    btn_OpenVideo.setVisibility(View.GONE);
                    btn_export.setVisibility(View.GONE);
                    if(mVideoList.size()>0){
                        playVideo(mVideoList.get(0));
                        mCurrentPosition = 0;
                        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                mCurrentPosition++;
                                if (mCurrentPosition < mVideoList.size()) {
                                    playVideo(mVideoList.get(mCurrentPosition));
                                } else {
                                   // finish();
                                    btn_import.setVisibility(View.VISIBLE);
                                    btn_OpenVideo.setVisibility(View.VISIBLE);
                                    btn_export.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                    }
                }
            }
        });

        btn_import.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                excelFilePath = filePath +"/" +FileName+".xls";
                System.out.println(excelFilePath);
                ExcelUtil.readExcelToAssets(HeightWHelpActivity.this,excelFilePath);
            }
        });

        btn_export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exportExcel(HeightWHelpActivity.this);
            }
        });
    }

    private void playVideo(String videoUrl) {
        mVideoView.setVideoPath(videoUrl);
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT > 23) {
            if (ContextCompat.checkSelfPermission(HeightWHelpActivity.this,
                    permissions[0])
                    == PackageManager.PERMISSION_GRANTED) {
                //授予权限
                Log.i("requestPermission:", "用户之前已经授予了权限！");
            } else {
                //未获得权限
                Log.i("requestPermission:", "未获得权限，现在申请！");
                requestPermissions(permissions
                        , REQUEST_PERMISSION_CODE);
            }
        }

    }

    private void exportExcel(Context context) {
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }

        String[] title = {"时间", "学籍号", "姓名", "年级编号", "班级编号", "卡号", "年龄", "性别", "民族代码", "照片", "身份证号", "出生年月", "联系方式", "学生来源", "家庭住址", "身高", "体重", "BMI"};
        String sheetName = "学生成绩表";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Date currentdate = new Date();
        String format = simpleDateFormat.format(currentdate);

      //  List<BeanData> demoBeanList = new ArrayList<>();
      //  BeanData demoBean1 = new BeanData(format, "10", "张三","一年级","102","1","8","男","01","02","3301102010098712","2010-10","1234578790","10","浙江省杭州市萧山区宁围街道摩吉电商产业园2幢305","120.9","40.6","12.7");
      //  demoBeanList.add(demoBean1);

        List<BeanData> demoBeanList = BeanDataDaoMaster.queryGoodAll();

        excelFilePath = filePath +"/" +FileName+".xls";
     //   filePath = UDiskReceiver.getuDiskDirPath() +"/" +excelFileName+".xls";

        ExcelUtil.initExcel(excelFilePath, sheetName, title);

        ExcelUtil.writeObjListToExcel(demoBeanList, excelFilePath, context);
    }



    @Override
    public void onBackPressed() {
        if (mVideoView.isPlaying()) {
            mVideoView.stopPlayback();
        }
        super.onBackPressed();
    }
}
