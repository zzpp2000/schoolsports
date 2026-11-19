package com.yyg.schoolsports.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.yyg.schoolsports.R;
import com.yyg.schoolsports.utils.PlayAudio;

import androidx.appcompat.app.AppCompatActivity;

public class VJUMPActivity extends AppCompatActivity{
    private Button vjumptestsetupBtn;
    private Button vjumpTestStartBtn;
    private Button vjumptestmodeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vjump);    //纵跳测试

        vjumpTestStartBtn = (Button) this.findViewById(R.id.vjumpteststart_btn);
        vjumpTestStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VJUMPActivity.this, ReadCardActivity.class);
                intent.putExtra("testitem","VJUMP");
                startActivity(intent);
            }
        });

        vjumptestsetupBtn = (Button) findViewById(R.id.vjumpsetup_btn);
        vjumptestsetupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        vjumptestmodeBtn = (Button) findViewById(R.id.vjumptestmode_btn);
        vjumptestmodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VJUMPActivity.this, VJUMPTestActivity.class);
                intent.putExtra("testcardid","无卡号");
                intent.putExtra("TESTMODE", 2);  //测试模式
                startActivity(intent);
            }
        });

        PlayAudio.playaudio(1);
    }
}
