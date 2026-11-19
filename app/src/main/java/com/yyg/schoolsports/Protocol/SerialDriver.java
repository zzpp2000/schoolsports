package com.yyg.schoolsports.Protocol;

import android.util.Log;

import com.paydevice.smartpos.sdk.serialport.SerialPort;
import com.yyg.schoolsports.utils.PlayAudio;
import com.yyg.schoolsports.utils.XmlUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.util.Arrays;

public class SerialDriver {
    private String TAG = "SerialDriver";
    private SerialPort serialPort = null;

    private static final int baudrate = 38400;//串口波特率
    private static final String path = "/dev/ttyS4";    //232串口
    private static final int timeOut = 10000;    //超时
    private static final int interval = 200;    //轮训间隔

    public boolean threadStatus; //线程状态，为了安全终止线程
    public InputStream inputStream = null;
    public OutputStream outputStream = null;
    private boolean busy = false;
    private SerialData serialData = new SerialData();

    public void openSerialPort() {
        if (serialPort == null) {
            try {
                serialPort = new SerialPort(new File(path), baudrate);
                outputStream = serialPort.getOutputStream();
                inputStream = serialPort.getInputStream();
                new SerialDriver.ReadThread().start(); //开始线程监控是否有数据要接收
                Log.d(TAG, "open DB9 at:" + baudrate + "bps");
            } catch (SecurityException e) {
                Log.d(TAG, "open DB9 fail:SecurityException");
            } catch (IOException e) {
                Log.d(TAG, "open DB9 fail:IOException");
            } catch (InvalidParameterException e) {
                Log.d(TAG, "open DB9 fail:InvalidParameterException");
            }
        }
    }

    private void closeSerialPort() {
        if (serialPort != null) {
            serialPort.close();
            serialPort = null;
            outputStream = null;
            inputStream = null;
            Log.d(TAG, "close DB9");
        }
    }

    /**
     * 单开一线程，来读数据
     */
    private class ReadThread extends Thread{
        @Override
        public void run() {
            super.run();
            //判断进程是否在运行，更安全的结束进程
            while (!threadStatus){
                //Log.i(TAG, "进入线程run");
                //64   1024
                byte[] buffer = new byte[256];
                int order = 0;    //指令
                int status = 0;       //电机状态
                int size;
                String testdata;   //测试成绩
                try {
                    size = 0; //读取数据的大小
                    Thread.sleep(200);
                    if(inputStream.available()>0) {

                        size = inputStream.read(buffer);
                        if (size > 0){
                            Log.i(TAG, "run: 接收到了数据大小：" + size);
                            //Log.i(TAG, "run: 接收到了数据：" + Arrays.toString(buffer));

                            // 将 byte[] 转换成 16进制字符串，以空格分隔
                            StringBuilder hexString = new StringBuilder();
                            for (byte b : buffer) {
                                // %02X 表示转换为2位大写16进制，不足补0
                                hexString.append(String.format("%02X ", b));
                            }

                            // 打印结果
                            Log.i(TAG, "run: 接收到了数据(HEX)：" + hexString.toString());

                         //   onDataReceiveListener.onQrDataReceive(buffer,size);
                            if(buffer[0] == (byte)0xaa && buffer[1] == 0x08){
                                if(buffer[2] == 0x01){  //cmd 01
                                    if(buffer[4] == 0x01){
                                        sendtest(1);
                                        PlayAudio.playaudio(4);
                                    }
                                }else if(buffer[2] == 0x02){  //cmd 02
                                    if(buffer[4] == 0x01){

                                    }
                                }
                            }else if(buffer[0] == (byte)0xaa && buffer[1] == 0x0e){
                                if(buffer[2] == 0x03){  //cmd 03
                                    if(buffer[5] == 0x01){
                                  //      testdata = Float.toString((float)(buffer[8] * 256 + (buffer[9]&0xff))/10);
                                        onDataReceiveListener.onQrDataReceive(buffer,size);
                                    }
                                }
                            }else if((buffer[0]&0xff) == 0x68 && (buffer[1]&0xff) == 0x81){
                                System.out.println("1111111");
                                onDataReceiveListener.onQrDataReceive(buffer,size);
                            }else if((buffer[0]&0xff) == 0x68 && (buffer[1]&0xff) == 0x82){
                                System.out.println("2222222");
                                onDataReceiveListener.onQrDataReceive(buffer,size);
                            }else if((buffer[0]&0xff) == 0x68 && (buffer[1]&0xff) == 0x84){
                                System.out.println("333333");
                                onDataReceiveListener.onQrDataReceive(buffer,size);
                            }
                            else if((buffer[0]&0xff) == 0xFC && ((buffer[1]&0xff) == 0x03 || (buffer[1]&0xff) == 0x83) && (buffer[2]&0xff) == 0x02){
                                System.out.println("获取panid");
                                onDataReceiveListener.onQrDataReceive(buffer,size);
                            }
                            else if((buffer[0]&0xff) == 0xFC && ((buffer[1]&0xff) == 0x06 || (buffer[1]&0xff) == 0x86) && (buffer[2]&0xff) == 0x02){
                                System.out.println("修改panid");
                                onDataReceiveListener.onQrDataReceive(buffer,size);
                            }
                        }
                    }

                } catch (IOException e) {
                    Log.i(TAG, "run: 数据读取异常：" +e.toString());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public void sendconnectOldStatus(){
        try {
            outputStream.write(serialData.SerialOldConnect(0x01));
            //System.out.println(Arrays.toString(serialData.SerialOldConnect(0x01)));
            outputStream.flush();
        } catch (Exception e) {
            Log.e(TAG, "出现错误 返回状态 失败");
        }
    }


    public void sendZigbeeQueryCmd(byte cmd){
        try {
            outputStream.write(serialData.SerialZigbeeQueryCmd(cmd));
            outputStream.flush();
        } catch (Exception e) {
            Log.e(TAG, "出现错误 返回状态 失败");
        }
    }

    public void sendZigbeeSetPanidCmdCmd(byte cmd,byte highbyte,byte lowbyte){//修改panid指令
        try {
            outputStream.write(serialData.SerialZigbeeSetPanidCmd(cmd,highbyte,lowbyte));
            outputStream.flush();
        } catch (Exception e) {
            Log.e(TAG, "出现错误 返回状态 失败");
        }
    }

    public void sendconnectStatus(){
        try {
            outputStream.write(serialData.SerialConnect(1, 1, 2));
            //System.out.println(Arrays.toString(serialData.SerialConnect(1, 1, 2)));

            outputStream.flush();
        } catch (Exception e) {
            Log.e(TAG, "出现错误 返回状态 失败");
        }

    }

    public void sendoldtest(){
        try {
            outputStream.write(serialData.SerialOldConnect(0x02));
            //System.out.println(Arrays.toString(serialData.SerialOldConnect(0x02)));
            outputStream.flush();
        }catch (Exception e){
            Log.e(TAG, "出现错误 返回状态 失败");
        }
    }

    public void sendtest(int subdev){
        try {
            outputStream.write(serialData.SerialConnect(2,1,subdev));
            //System.out.println(Arrays.toString(serialData.SerialConnect(1,1,subdev)));
            outputStream.flush();
        }catch (Exception e){
            Log.e(TAG, "出现错误 返回状态 失败");
        }
    }

    //这是写了一监听器来监听接收数据
    public static OnDataReceiveListener onDataReceiveListener = null;
    public static interface OnDataReceiveListener {
        public void onQrDataReceive(byte[] buff,int size);
    }

    public static void setOnDataReceiveListener(OnDataReceiveListener dataReceiveListener) {
        onDataReceiveListener = dataReceiveListener;
    }
}
