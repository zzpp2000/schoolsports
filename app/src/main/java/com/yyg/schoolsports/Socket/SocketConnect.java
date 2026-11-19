package com.yyg.schoolsports.Socket;

import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * 只维护一个线程：
 *   连接 → 读数据 → 断开 → 睡10秒 → 再连接
 * 心跳由 DateConnect 的后台线程负责
 */
public class SocketConnect implements Runnable {

    private static final String TAG = "SocketConnect";

    private volatile boolean isRunning = true;
    private volatile boolean connected = false;
    private Socket socket;
    private InputStream in;
    private OutputStream out;
    private final Object writeLock = new Object();

    private final SocketBase.SocketCallback callback;
    private String ip;
    private int port;

    public SocketConnect(SocketBase.SocketCallback callback) {
        this.callback = callback;
    }

    public void setRemoteAddress(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public boolean isConnected() {
        return connected;
    }

    @Override
    public void run() {
        while (isRunning) {
            try {
                // ============ 1. 连接（5秒超时）============
                Log.i(TAG, "开始连接 " + ip + ":" + port);
                socket = new Socket();
                socket.connect(new InetSocketAddress(ip, port), 5000);
                in = socket.getInputStream();
                out = socket.getOutputStream();
                connected = true;

                Log.i(TAG, "连接成功");
                if (callback != null) callback.connected();

                // ============ 2. 阻塞读取（直到断开）============
                byte[] buffer = new byte[1024];
                while (isRunning) {
                    int len = in.read(buffer);
                    if (len == -1) {
                        Log.i(TAG, "服务器关闭连接");
                        break;
                    }
                    if (callback != null) {
                        byte[] data = new byte[len];
                        System.arraycopy(buffer, 0, data, 0, len);
                        callback.receive(data);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "连接/读取异常: " + e.getMessage());
            } finally {
                connected = false;
                closeQuietly();
                if (callback != null) callback.disconnect();
            }

            // ============ 3. 睡 10 秒再重试 ============
            if (isRunning) {
                Log.i(TAG, "10 秒后重试...");
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
        Log.i(TAG, "SocketConnect 线程已退出");
    }

    /** 外部唯一需要调用的关闭方法 */
    public void close() {
        isRunning = false;
        closeQuietly();
    }

    private void closeQuietly() {
        try { if (in != null) in.close(); } catch (Exception ignored) {}
        try { if (out != null) out.close(); } catch (Exception ignored) {}
        try { if (socket != null) socket.close(); } catch (Exception ignored) {}
        in = null;
        out = null;
        socket = null;
    }

    /** 发送数据（可从任意线程调用） */
    public void write(byte[] data) {
        synchronized (writeLock) {
            // 先取局部引用，防止中途被 closeQuietly 置空引发 NPE
            OutputStream o = out;
            if (o == null) {
                Log.e(TAG, "发送失败：连接已断开");
                return;
            }
            try {
                o.write(data);
                o.flush();
            } catch (Exception e) {
                Log.e(TAG, "发送失败: " + e.getClass().getSimpleName(), e);
                // 发送失败说明连接坏了，异步触发关闭，让 read() 抛异常退出
                new Thread(this::closeQuietly).start();
            }
        }
    }
}