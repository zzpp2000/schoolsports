package com.yyg.schoolsports.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.yyg.schoolsports.R;
import com.yyg.schoolsports.utils.PlayAudio;

import androidx.appcompat.app.AppCompatActivity;

public class SargJumpActivity extends AppCompatActivity {
    private Button sgjtestsetupBtn;
    private Button sgjTestStartBtn;
    private Button sgjtestmodeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sgj);  //摸高

        sgjTestStartBtn = (Button) this.findViewById(R.id.sgjteststart_btn);
        sgjTestStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SargJumpActivity.this, ReadCardActivity.class);
                intent.putExtra("testitem","SGJ");
                startActivity(intent);
            }
        });

        sgjtestsetupBtn = (Button) findViewById(R.id.sgjsetup_btn);
        sgjtestsetupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        sgjtestmodeBtn = (Button) findViewById(R.id.sgjtestmode_btn);
        sgjtestmodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SargJumpActivity.this, SargJumpTestActivity.class);
                intent.putExtra("testcardid","无卡号");
                intent.putExtra("TESTMODE", 2);  //测试模式
                startActivity(intent);
            }
        });

        PlayAudio.playaudio(1);
    }
}
