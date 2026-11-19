
package com.yyg.schoolsports.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.yyg.schoolsports.R;
import com.yyg.schoolsports.utils.PlayAudio;

import androidx.appcompat.app.AppCompatActivity;

public class SLJActivity extends AppCompatActivity {
    private Button sljtestsetupBtn;
    private Button sljTestStartBtn;
    private Button sljtestmodeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slj);     //立定跳远

        sljTestStartBtn = (Button) this.findViewById(R.id.sljteststart_btn);
        sljTestStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SLJActivity.this, ReadCardActivity.class);
                intent.putExtra("testitem","SLJ");
                startActivity(intent);
            }
        });

        sljtestsetupBtn = (Button) findViewById(R.id.sljsetup_btn);
        sljtestsetupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        sljtestmodeBtn = (Button) findViewById(R.id.sljtestmode_btn);
        sljtestmodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SLJActivity.this, SLJTestActivity.class);
                intent.putExtra("testcardid","无卡号");
                intent.putExtra("TESTMODE", 2);  //测试模式
                startActivity(intent);
            }
        });

        PlayAudio.playaudio(1);
    }
}
