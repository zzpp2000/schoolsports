package com.yyg.schoolsports.Activity;

import android.content.Intent;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.yyg.schoolsports.Protocol.SerialDriver;
import com.yyg.schoolsports.R;
import com.yyg.schoolsports.Socket.DateConnect;
import com.yyg.schoolsports.utils.PlayAudio;
import com.yyg.schoolsports.utils.Utils;
import com.yyg.schoolsports.utils.XmlUtil;

import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;

public class MedballTestActivity extends AppCompatActivity{
    private String TAG = "MedballTestActivity";
    private TextView text_mdbcardid;
    private TextView text_mdbtestnum;
    private Button btn_cacel;
    private Button btn_confirm;
    private Button btn_complete;

    private int mdbdata;
    private int mdbdata2;
    private String testid;
    private int testmode;
    private int testcount;   //测试次数
    SerialDriver serialDriver = new SerialDriver();
    DateConnect dateConnect = DateConnect.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medballtest);

        Intent intent =getIntent();
        testid = intent.getStringExtra("testcardid");
        testmode = intent.getIntExtra("TESTMODE",0);
        text_mdbcardid = (TextView) findViewById(R.id.mdbcardid);
        text_mdbtestnum = (TextView) findViewById(R.id.mdbtestnum);
        btn_cacel = (Button) findViewById(R.id.mdbtestcan);
        btn_confirm = (Button) findViewById(R.id.mdbtestconfirm);
        btn_complete = (Button) findViewById(R.id.mdbtestcomplete);
        text_mdbcardid.setText(testid);
        System.out.println("testmode:"+testmode);
        testcount = 1;
        if(testmode == 2){
            btn_confirm.setVisibility(View.INVISIBLE);
        }else{
            btn_confirm.setVisibility(View.VISIBLE);
        }
        btn_cacel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        dateConnect.DataConnect();
        serialDriver.openSerialPort();
        mhandler.postDelayed(mrunnable, 1000);

        serialDriver.setOnDataReceiveListener(new SerialDriver.OnDataReceiveListener() {
            @Override
            public void onQrDataReceive(final byte[] buff,int size) {
                MedballTestActivity.this.runOnUiThread(new Runnable(){
                    public void run(){
                        if(XmlUtil.ProtocolType == 0){
                            if((buff[0]&0xff) == 0x68 && (buff[1]&0xff) == 0x81){
                                mhandler.removeCallbacks(mrunnable);
                                shandler.postDelayed(srunnable, 1000);
                            }else if((buff[0]&0xff) == 0x68 && (buff[1]&0xff) == 0x82){
                                Log.i(TAG,"开始测试");
                                mhandler.removeCallbacks(mrunnable);   //再次关闭
                                shandler.removeCallbacks(srunnable);
                                PlayAudio.playaudio(6);
                            }else if((buff[0]&0xff) == 0x68 && (buff[1]&0xff) == 0x84){
                                Log.i(TAG,"测试成绩");
                                if(testcount == 1){
                                    mdbdata = buff[2] * 256 + (buff[3]&0xff);
                                    text_mdbtestnum.setText(Integer.toString(mdbdata));
                                }else if(testcount == 2){
                                    btn_confirm.setVisibility(View.INVISIBLE);
                                    mdbdata2 = buff[2] * 256 + (buff[3]&0xff);
                                    text_mdbtestnum.setText(Integer.toString(mdbdata2));
                                    testcount = 0;
                                    if(mdbdata2 >= mdbdata){
                                        mdbdata = mdbdata2;
                                    }
                                }
                                PlayAudio.playaudio(5);
                            }
                        }
//
                    }
                });

            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlayAudio.playaudio(7);
                text_mdbtestnum.setText("0");
                testcount = 2;
                mhandler.postDelayed(mrunnable, 1000);
            }
        });

        btn_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(testmode == 0) {   //刷卡模式
                    dateConnect.SendBuff(19,Integer.parseInt(testid),mdbdata);
                    Intent intent = new Intent(MedballTestActivity.this, WriteCardActivity.class);
                    intent.putExtra("testitem","MDB");
                    intent.putExtra("testcardid", testid);
                    intent.putExtra("testresult", Integer.toString(mdbdata));
                    startActivity(intent);
                    finish();
                }else if(testmode ==1) {   //二维码模式
                    dateConnect.SendBuff(19,Integer.parseInt(testid),mdbdata);
                    finish();
                }else if(testmode ==2) {  //测试模式
                    finish();
                }
            }
        });

    }

    Handler mhandler=new Handler();
    Runnable mrunnable=new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            //要做的事情
            mhandler.postDelayed(this, 500);
            if(XmlUtil.ProtocolType == 0){
                serialDriver.sendconnectOldStatus();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }else if(XmlUtil.ProtocolType == 1){
                serialDriver.sendconnectStatus();
            }

        }
    };

    Handler shandler=new Handler();
    Runnable srunnable=new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            shandler.postDelayed(this, 1000);
            if(XmlUtil.ProtocolType == 0){
                serialDriver.sendoldtest();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }else if(XmlUtil.ProtocolType == 1){
                serialDriver.sendconnectStatus();
            }

        }
    };


}
