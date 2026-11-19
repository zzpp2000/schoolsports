
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

public class SuTestActivity extends AppCompatActivity{
    private String TAG = "VitalcapacityTestActivity";
    private TextView text_sucardid;
    private TextView text_sutestnum;
    private Button btn_cacel;
    private Button btn_confirm;
    private Button btn_complete;

    private int sudata;
    private int sudata2;
    private String testid;
    private int testmode;
    private int testcount;   //测试次数
    SerialDriver serialDriver = new SerialDriver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sutest);

        Intent intent =getIntent();
        testid = intent.getStringExtra("testcardid");
        testmode = intent.getIntExtra("TESTMODE",0);
        text_sucardid = (TextView) findViewById(R.id.sucardid);
        text_sutestnum = (TextView) findViewById(R.id.sutestnum);
        btn_cacel = (Button) findViewById(R.id.sutestcan);
        btn_confirm = (Button) findViewById(R.id.sutestconfirm);
        btn_complete = (Button) findViewById(R.id.sutestcomplete);
        text_sucardid.setText(testid);
        testcount = 1;
        if(testmode == 2){
            btn_confirm.setVisibility(View.INVISIBLE);
        }else{
            btn_confirm.setVisibility(View.VISIBLE);
        }

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
                SuTestActivity.this.runOnUiThread(new Runnable(){
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
                                    sudata = buff[2] * 256 + (buff[3]&0xff);
                                    text_sutestnum.setText(Integer.toString(sudata));
                                }else if(testcount == 2){
                                    btn_confirm.setVisibility(View.GONE);
                                    sudata2 = buff[2] * 256 + (buff[3]&0xff);
                                    text_sutestnum.setText(Integer.toString(sudata2));
                                    testcount = 0;
                                    if(sudata2 >= sudata){
                                        sudata = sudata2;
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
                text_sutestnum.setText("0");
                testcount = 2;
                mhandler.postDelayed(mrunnable, 1000);
            }
        });

        btn_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (testmode == 0) {   //刷卡模式
                    Intent intent = new Intent(SuTestActivity.this, WriteCardActivity.class);
                    intent.putExtra("testitem", "VC");
                    intent.putExtra("testcardid", testid);
                    intent.putExtra("testresult", Integer.toString(sudata));
                    startActivity(intent);
                    finish();
                } else if (testmode == 1) {   //二维码模式

                } else if (testmode == 2) {  //测试模式
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
