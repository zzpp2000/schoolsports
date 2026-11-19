package com.yyg.schoolsports.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Environment;

public class PlayAudio {
    private static MediaPlayer umediaPlayer = new MediaPlayer();
    public static String path;

    public static void playaudio(int play){
        try{
            switch (play){
                case 1:
                    path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/download/voice/欢迎使用元谷运动体质测试仪.wav";
                    umediaPlayer = new MediaPlayer();
                    umediaPlayer.setDataSource(path);
                    umediaPlayer.setVolume(1.0f,1.0f);
                    umediaPlayer.prepare();
                    umediaPlayer.start();
                    break;
                case 2:
                    path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/download/voice/请读卡.wav";
                    umediaPlayer = new MediaPlayer();
                    umediaPlayer.setDataSource(path);
                    umediaPlayer.setVolume(1.0f,1.0f);
                    umediaPlayer.prepare();
                    umediaPlayer.start();
                    break;
                case 3:
                    path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/download/voice/请刷二维码.wav";
                    umediaPlayer = new MediaPlayer();
                    umediaPlayer.setDataSource(path);
                    umediaPlayer.setVolume(1.0f,1.0f);
                    umediaPlayer.prepare();
                    umediaPlayer.start();
                    break;
                case 4:
                    path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/download/voice/开始测试.wav";
                    umediaPlayer = new MediaPlayer();
                    umediaPlayer.setDataSource(path);
                    umediaPlayer.setVolume(1.0f,1.0f);
                    umediaPlayer.prepare();
                    umediaPlayer.start();
                    break;
                case 5:
                    path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/download/voice/测试完毕，谢谢使用.wav";
                    umediaPlayer = new MediaPlayer();
                    umediaPlayer.setDataSource(path);
                    umediaPlayer.setVolume(1.0f,1.0f);
                    umediaPlayer.prepare();
                    umediaPlayer.start();
                    break;
                case 6:
                    path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/download/voice/开始测试.wav";
                    umediaPlayer = new MediaPlayer();
                    umediaPlayer.setDataSource(path);
                    umediaPlayer.setVolume(1.0f,1.0f);
                    umediaPlayer.prepare();
                    umediaPlayer.start();
                    break;
                case 7:
                    path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/download/voice/第二次测试.wav";
                    umediaPlayer = new MediaPlayer();
                    umediaPlayer.setDataSource(path);
                    umediaPlayer.setVolume(1.0f,1.0f);
                    umediaPlayer.prepare();
                    umediaPlayer.start();
                    break;
                case 8:
                    path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/download/voice/bugscaner-tts-auido8.mp3";
                    umediaPlayer = new MediaPlayer();
                    umediaPlayer.setDataSource(path);
                    umediaPlayer.setVolume(1.0f,1.0f);
                    umediaPlayer.prepare();
                    umediaPlayer.start();
                    break;
                case 10:
                    path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/download/voice/bugscaner-tts-auido10.mp3";
                    umediaPlayer = new MediaPlayer();
                    umediaPlayer.setDataSource(path);
                    umediaPlayer.setVolume(1.0f,1.0f);
                    umediaPlayer.prepare();
                    umediaPlayer.start();
                    break;
                case 11:
                    path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/download/voice/bugscaner-tts-auido11.mp3";
                    umediaPlayer = new MediaPlayer();
                    umediaPlayer.setDataSource(path);
                    umediaPlayer.setVolume(2.0f,2.0f);
                    umediaPlayer.prepare();
                    umediaPlayer.start();
                    break;
            }
        }catch(Exception e){
            System.out.println("出现异常");
        }
    }

    //判断是否正在播放中
    public static boolean isplay() {
        return umediaPlayer.isPlaying();
    }

    public static void Stop(){
        umediaPlayer.stop();
    }


    protected void onDestroy() {
        // TODO Auto-generated method stub

        if (umediaPlayer != null) {
            umediaPlayer.release();
            umediaPlayer = null;
        }
    }

}
