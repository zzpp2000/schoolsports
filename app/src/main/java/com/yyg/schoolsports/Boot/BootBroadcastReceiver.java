package com.yyg.schoolsports.Boot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.yyg.schoolsports.Activity.MainActivity;
import com.yyg.schoolsports.Log.*;

//开机启动
public class BootBroadcastReceiver extends BroadcastReceiver {
    static final String action_boot="android.intent.action.BOOT_COMPLETED";


    @Override
    public void onReceive(final Context context, Intent intent) {
        if (intent.getAction().contains(action_boot)){
            Log4j.getLogger("Boot").info("监听到开机广播");

            Thread splashThread = new Thread(){   //创建子线程
                @Override
                public void run(){
                    try{
                        sleep(5000);    //延迟5秒开机
                        Intent ootStartIntent=new Intent(context,MainActivity.class);
                        ootStartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(ootStartIntent);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            splashThread.start();//启动线程
        }

    }
}
