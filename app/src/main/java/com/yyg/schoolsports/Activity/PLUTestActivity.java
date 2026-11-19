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

public class PLUTestActivity extends AppCompatActivity{
    private String TAG = "PLUTestActivity";
    private TextView text_plucardid;
    private TextView text_plutestnum;
    private Button btn_cacel;
    private Button btn_complete;

    private int pludata;
    private String testid;
    private int testmode;
    SerialDriver serialDriver = new SerialDriver();
    DateConnect dateConnect = DateConnect.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plutest);

        Intent intent =getIntent();
        testid = intent.getStringExtra("testcardid");
        testmode = intent.getIntExtra("TESTMODE",0);
        text_plucardid = (TextView) findViewById(R.id.plucardid);
        text_plutestnum = (TextView) findViewById(R.id.plutestnum);
        btn_cacel = (Button) findViewById(R.id.plutestcan);
        btn_complete = (Button) findViewById(R.id.plutestcomplete);
        text_plucardid.setText(testid);
        System.out.println("testmode:"+testmode);

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
                PLUTestActivity.this.runOnUiThread(new Runnable(){
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
                                pludata = buff[2] * 256 + (buff[3]&0xff);
                                text_plutestnum.setText(Integer.toString(pludata));
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
                    dateConnect.SendBuff(7,Integer.parseInt(testid),pludata);
                    Intent intent = new Intent(PLUTestActivity.this, WriteCardActivity.class);
                    intent.putExtra("testitem","PLU");
                    intent.putExtra("testcardid", testid);
                    intent.putExtra("testresult", Integer.toString(pludata));
                    startActivity(intent);
                    finish();
                }else if(testmode ==1) {   //二维码模式
                    dateConnect.SendBuff(7,Integer.parseInt(testid),pludata);
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
