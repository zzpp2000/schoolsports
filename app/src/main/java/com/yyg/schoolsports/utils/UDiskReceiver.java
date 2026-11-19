package com.yyg.schoolsports.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Android Studio.
 * U盘操示例
 *
 * @author: linruihang
 * @Date: 2021/10/8
 * @Time: 16:11
 */

public class UDiskReceiver extends BroadcastReceiver {
    private String TAG="MyUsbReceiver";
    private List<String> pathList = new ArrayList<>();
    private  static String uDiskDirPath;
    public  static boolean uDiskFlag;   //U盘挂载标志
    private String xlspath;

    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_MEDIA_MOUNTED.equals(intent.getAction())) {
            Log.i(TAG, "U盘挂载成功，可以进行读写操作！");
            //获取U盘挂载虚拟路径
            uDiskFlag = true;
            uDiskDirPath = intent.getData().getPath();
            System.out.println(uDiskDirPath);
            //U盘根目录，可以进行文件操作
            File uDiskDir = new File(uDiskDirPath);
            if (uDiskDir.canRead()) {
                //执行读操作
                
            }
            if (uDiskDir.canWrite()) {
                //执行写操作
            }

        } else if (Intent.ACTION_MEDIA_REMOVED.equals(intent.getAction())) {
            Log.d(TAG, "U盘移除！");
            uDiskFlag = false;
        }
    }

    public List<String> getVideo(Context context){
        System.out.println(uDiskDirPath);

        File uDiskDir1 = new File(uDiskDirPath);
        if (!uDiskDir1.exists()) {
            Toast.makeText(context, "文件夹不存在", Toast.LENGTH_SHORT).show();
        }
        File[] subfile = uDiskDir1.listFiles();
        for (int iFileLength = 0; iFileLength < subfile.length; iFileLength++) {
            if (!subfile[iFileLength].isDirectory()) {
                String filename = subfile[iFileLength].getName();
                System.out.println("---name = " + filename);
                Log.i(TAG, filename);
                Log.i(TAG, String.valueOf(iFileLength));

                if (filename.trim().toLowerCase().endsWith(".mp4") || filename.trim().toLowerCase().endsWith(".3gp") ||
                        filename.trim().toLowerCase().endsWith(".avi") || filename.trim().toLowerCase().endsWith(".flv") ||
                        filename.trim().toLowerCase().endsWith(".mpg") || filename.trim().toLowerCase().endsWith(".rm")) {
                    pathList.add(uDiskDirPath + "/" + filename);
                }
            }
        }

        return pathList;
    }

    public String getExecl(Context context){
        System.out.println(uDiskDirPath);

        File uDiskDir1 = new File(uDiskDirPath);
        if (!uDiskDir1.exists()) {
            Toast.makeText(context, "文件夹不存在", Toast.LENGTH_SHORT).show();
        }
        File[] file = uDiskDir1.listFiles();
        for (int iFileLength = 0; iFileLength < file.length; iFileLength++) {
            if (!file[iFileLength].isDirectory()) {
                String filename = file[iFileLength].getName();
                System.out.println("---name = " + filename);
                Log.i(TAG, filename);
                Log.i(TAG, String.valueOf(iFileLength));

                if (filename.trim().toLowerCase().endsWith(".xls")) {
                    xlspath = uDiskDirPath + "/" + filename;
                }
            }
        }
        return  xlspath;
    }

    public static String getuDiskDirPath(){
        return uDiskDirPath;
    }

}
