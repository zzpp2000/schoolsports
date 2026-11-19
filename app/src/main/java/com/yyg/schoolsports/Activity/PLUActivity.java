package com.yyg.schoolsports.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.yyg.schoolsports.R;
import com.yyg.schoolsports.utils.PlayAudio;

import androidx.appcompat.app.AppCompatActivity;

public class PLUActivity extends AppCompatActivity{
    private Button plutestsetupBtn;
    private Button pluTestStartBtn;
    private Button plutestmodeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plu);   //引体向上

        pluTestStartBtn = (Button) this.findViewById(R.id.pluteststart_btn);
        pluTestStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PLUActivity.this, ReadCardActivity.class);
                intent.putExtra("testitem","PLU");
                startActivity(intent);
            }
        });

        plutestsetupBtn = (Button) findViewById(R.id.plusetup_btn);
        plutestsetupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        plutestmodeBtn = (Button)findViewById(R.id.plutestmode_btn);
        plutestmodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PLUActivity.this, PLUTestActivity.class);
                intent.putExtra("testcardid","无卡号");
                intent.putExtra("TESTMODE", 2);  //测试模式
                startActivity(intent);
            }
        });

        PlayAudio.playaudio(1);
    }
}
