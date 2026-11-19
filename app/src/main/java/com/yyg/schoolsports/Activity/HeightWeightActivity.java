package com.yyg.schoolsports.Activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;

import com.yyg.schoolsports.R;
import com.yyg.schoolsports.utils.MediaManager;
import com.yyg.schoolsports.utils.PlayAudio;

import androidx.appcompat.app.AppCompatActivity;

public class HeightWeightActivity extends AppCompatActivity {
    private Button TestStartBtn;
    private Button hwtestsetupBtn;
    private Button testmodeBtn;
    private Button hwtesthelpBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heightweight);     //身高测试

        TestStartBtn = (Button) findViewById(R.id.hwteststart_btn);
        TestStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HeightWeightActivity.this, ReadCardActivity.class);
                intent.putExtra("testitem","HWB");
                startActivity(intent);
            }
        });

        hwtestsetupBtn = (Button) findViewById(R.id.hwsetup_btn);
        hwtestsetupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        testmodeBtn = (Button) findViewById(R.id.hwtestmode_btn);
        testmodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HeightWeightActivity.this, HeightWTestActivity.class);
                intent.putExtra("testcardid","无卡号");
                intent.putExtra("TESTMODE", 2);  //测试模式
                startActivity(intent);
            }
        });

        hwtesthelpBtn = (Button)findViewById(R.id.hwtesthelp_btn);
        hwtesthelpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HeightWeightActivity.this, HeightWHelpActivity.class);
                startActivity(intent);
            }
        });

        PlayAudio.playaudio(1);
    }

}
