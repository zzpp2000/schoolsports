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
import com.yyg.schoolsports.utils.PlayAudio;
import com.yyg.schoolsports.utils.Utils;
import com.yyg.schoolsports.utils.XmlUtil;

import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;

public class SargJumpTestActivity extends AppCompatActivity{
    private String TAG = "SFBTestActivity";
    private TextView text_sgjcardid;
    private TextView text_sgjtestnum;
    private Button btn_cacel;
    private Button btn_complete;

    private int sgjdata;
    private String testid;
    private int testmode;
    SerialDriver serialDriver = new SerialDriver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sgjtest);

        Intent intent =getIntent();
        testid = intent.getStringExtra("testcardid");
        testmode = intent.getIntExtra("TESTMODE",0);
        text_sgjcardid = (TextView) findViewById(R.id.sgjcardid);
        text_sgjtestnum = (TextView) findViewById(R.id.sgjtestnum);
        btn_cacel = (Button) findViewById(R.id.sgjtestcan);
        btn_complete = (Button) findViewById(R.id.sgjtestcomplete);
        text_sgjcardid.setText(testid);
        System.out.println("testmode:"+testmode);

        btn_cacel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        serialDriver.openSerialPort();
        mhandler.postDelayed(mrunnable, 1000);

        serialDriver.setOnDataReceiveListener(new SerialDriver.OnDataReceiveListener() {
            @Override
            public void onQrDataReceive(final byte[] buff,int size) {
                SargJumpTestActivity.this.runOnUiThread(new Runnable(){
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
                                sgjdata = buff[2] * 256 + (buff[3]&0xff);
                                float sgjfloat = (float)(buff[2] * 256 + (buff[3]&0xff))/10;
                                text_sgjtestnum.setText(Float.toString(sgjfloat));
                                PlayAudio.playaudio(5);
                            }
                        }
//
                    }
                });

            }
        });


        btn_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(testmode == 0) {   //刷卡模式
                    Intent intent = new Intent(SargJumpTestActivity.this, WriteCardActivity.class);
                    intent.putExtra("testitem","SGJ");
                    intent.putExtra("testcardid", testid);
                    intent.putExtra("testresult", Integer.toString(sgjdata));
                    startActivity(intent);
                    finish();
                }else if(testmode ==1) {   //二维码模式

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
