package com.yyg.schoolsports.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.yyg.schoolsports.R;
import com.yyg.schoolsports.utils.PlayAudio;

import androidx.appcompat.app.AppCompatActivity;

public class MedballActivity extends AppCompatActivity{
    private Button mdbtestsetupBtn;
    private Button mdbTestStartBtn;
    private Button mdbtestmodeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medball); //实心球

        mdbTestStartBtn = (Button) this.findViewById(R.id.mdbteststart_btn);
        mdbTestStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MedballActivity.this, ReadCardActivity.class);
                intent.putExtra("testitem","MDB");
                startActivity(intent);
            }
        });

        mdbtestsetupBtn = (Button) findViewById(R.id.mdbsetup_btn);
        mdbtestsetupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mdbtestmodeBtn = (Button)findViewById(R.id.mdbtestmode_btn);
        mdbtestmodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MedballActivity.this, MedballTestActivity.class);
                intent.putExtra("testcardid","无卡号");
                intent.putExtra("TESTMODE", 2);  //测试模式
                startActivity(intent);
            }
        });

        PlayAudio.playaudio(1);
    }
}
