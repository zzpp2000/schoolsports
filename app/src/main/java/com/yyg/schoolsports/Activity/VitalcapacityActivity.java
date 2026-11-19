package com.yyg.schoolsports.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.yyg.schoolsports.R;
import com.yyg.schoolsports.Socket.DateConnect;
import com.yyg.schoolsports.utils.PlayAudio;

import androidx.appcompat.app.AppCompatActivity;

public class VitalcapacityActivity extends AppCompatActivity{
    private Button vctestsetupBtn;
    private Button vcTestStartBtn;
    private Button vctestmodeBtn;
    private Button vctesthelpBtn;

    DateConnect dateConnect = DateConnect.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vitalcapacity);        //肺活量测试

        dateConnect.DataConnect();


        vcTestStartBtn = (Button) this.findViewById(R.id.vcteststart_btn);
        vcTestStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VitalcapacityActivity.this, ReadCardActivity.class);
                intent.putExtra("testitem","VC");
                startActivity(intent);
            }
        });

        vctestsetupBtn = (Button) findViewById(R.id.vcsetup_btn);
        vctestsetupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        vctestmodeBtn = (Button) findViewById(R.id.vctestmode_btn);
        vctestmodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VitalcapacityActivity.this, VitalcapacityTestActivity.class);
                intent.putExtra("testcardid","无卡号");
                intent.putExtra("TESTMODE", 2);  //测试模式
                startActivity(intent);
            }
        });

        vctesthelpBtn = (Button)findViewById(R.id.vctesthelp_btn);
        vctesthelpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateConnect.SendBuff(1,39,1000);
            }
        });

        PlayAudio.playaudio(1);
    }
}
