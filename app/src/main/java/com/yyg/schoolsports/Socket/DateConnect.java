package com.yyg.schoolsports.Socket;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.yyg.schoolsports.utils.XmlUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class DateConnect {

    private static final String TAG = "DateConnect";

    private static final int DEFAULT_PORT = 8899;
    private static final int HEARTBEAT_COMMAND = 0xA1;     // 心跳命令符
    private static final long HEARTBEAT_INTERVAL = 30_000L;

    private static volatile DateConnect instance;

    private DateConnect() {
        iphost = XmlUtil.getServerIP();
        if (iphost == null || iphost.isEmpty()) iphost = "192.168.3.21";
        Log.i(TAG, "DateConnect 初始化，服务器 IP = " + iphost);
    }

    public static DateConnect getInstance() {
        if (instance == null) {
            synchronized (DateConnect.class) {
                if (instance == null) instance = new DateConnect();
            }
        }
        return instance;
    }

    // ============ 连接状态 ============
    private SocketConnect connect;
    private String iphost = "";
    private final int ipport = DEFAULT_PORT;

    private volatile boolean isConnected = false;
    /** 连接代号：改 IP 时 +1，用于让旧线程的回调失效 */
    private volatile int connectionId = 0;

    // ============ 数据接收相关 ============
    public interface OnDataReceiveListener {
        void onDataReceived(byte[] buffer);
    }
    private OnDataReceiveListener dataListener;
    public void setOnDataReceiveListener(OnDataReceiveListener listener) {
        this.dataListener = listener;
    }
    // 接收数据缓冲区
    private final List<Byte> receiveBuffer = new ArrayList<>();

    // ============ UI 通知用主线程 Handler ============
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    // ============ 心跳在后台线程执行 ============
    private final ScheduledExecutorService heartbeatExecutor =
            Executors.newSingleThreadScheduledExecutor();

    // ============ 数据发送线程池 (避免主线程网络异常) ============
    private final ScheduledExecutorService sendExecutor =
            Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> heartbeatFuture;

    // ============ 监听器 ============
    public interface OnConnectionChangeListener {
        void onConnectionChanged(boolean connected);
    }

    private final List<OnConnectionChangeListener> listeners = new CopyOnWriteArrayList<>();

    public void addListener(OnConnectionChangeListener l) {
        if (l != null && !listeners.contains(l)) listeners.add(l);
    }

    public void removeListener(OnConnectionChangeListener l) {
        if (l != null) listeners.remove(l);
    }

    private void notifyListeners(final boolean connected) {
        mainHandler.post(() -> {
            for (OnConnectionChangeListener l : listeners) {
                try {
                    l.onConnectionChanged(connected);
                } catch (Exception ignored) {}
            }
        });
    }

    public boolean isConnected() {
        return isConnected;
    }

    // ============ 连接管理 ============

    public synchronized void connect() {
        if (connect != null) {
            Log.d(TAG, "已有连接线程，忽略 connect()");
            return;
        }
        startNewConnect();
    }

    public void DataConnect() {
        connect();
    }

    public synchronized void reconnect() {
        Log.i(TAG, "重连：关闭旧线程，启动新线程");
        if (connect != null) {
            connect.close();
            connect = null;
        }
        connectionId++;
        isConnected = false;
        stopHeartbeat();
        notifyListeners(false);
        startNewConnect();
    }

    public synchronized void shutdown() {
        if (connect != null) {
            connect.close();
            connect = null;
        }
        connectionId++;
        isConnected = false;
        stopHeartbeat();
        notifyListeners(false);
    }

    /** 真正的启动逻辑 */
    private void startNewConnect() {
        final int myId = ++connectionId;

        String ip = XmlUtil.getServerIP();
        if (ip == null || ip.isEmpty()) ip = "192.168.3.21";
        this.iphost = ip;

        final SocketConnect sc = new SocketConnect(new SocketBase.SocketCallback() {
            @Override
            public void connected() {
                if (myId != connectionId) return;
                Log.i(TAG, "连接状态 → 已连接");
                isConnected = true;
                notifyListeners(true);
                startHeartbeat();
            }

            @Override
            public void receive(byte[] buffer) {
                synchronized (receiveBuffer) {
                    for (byte b : buffer) receiveBuffer.add(b);

                    Log.d(TAG, "收到Socket数据，当前缓冲区大小: " + receiveBuffer.size());

                    // 必须至少有 7 个字节才能满足一个基础包的解析
                    while (receiveBuffer.size() >= 7) {
                        // 1. 找包头 (FE FF)
                        if ((receiveBuffer.get(0) & 0xFF) == 0xFE && (receiveBuffer.get(1) & 0xFF) == 0xFF) {
                            int command = receiveBuffer.get(2) & 0xFF;

                            if (command == 0xA2) { // 长包：名单数据
                                if (receiveBuffer.size() < 5) break; // 如果连长度都不够，先等

                                int lenH = receiveBuffer.get(3) & 0xFF;
                                int lenL = receiveBuffer.get(4) & 0xFF;
                                int dataLen = (lenH << 8) | lenL;

                                // 判断是否收全
                                if (receiveBuffer.size() < 5 + dataLen) {
                                    Log.d(TAG, "名单包尚未接收完整，当前: " + receiveBuffer.size() + " / 目标: " + (5 + dataLen) + "，等待下一包...");
                                    break; // 跳出循环，保留数据，等待下一次底层 read
                                }

                                // 收全了，提取数据
                                byte[] payload = new byte[dataLen];
                                for (int i = 0; i < dataLen; i++) {
                                    payload[i] = receiveBuffer.get(5 + i);
                                }

                                Log.i(TAG, "成功拼包！提取到完整名单，长度: " + dataLen);
                                if (dataListener != null) {
                                    dataListener.onDataReceived(payload);
                                }

                                // 移除已处理的名单包
                                for (int i = 0; i < 5 + dataLen; i++) receiveBuffer.remove(0);
                                continue; // 继续看后面还有没有数据

                            } else {
                                // ⭐ 核心修复：如果是心跳包（0xA1）或其他7字节短包
                                // 不要用 remove(0) 一个一个删，直接跳过 7 个字节，防止误删后面的名单包头！
                                for (int i = 0; i < 7; i++) receiveBuffer.remove(0);
                                continue;
                            }
                        }
                        // 如果连包头 FE FF 都对不上，说明是纯垃圾数据，每次剔除 1 个字节继续找
                        receiveBuffer.remove(0);
                    }
                }
            }

            @Override
            public void disconnect() {
                if (myId != connectionId) return;
                Log.i(TAG, "连接状态 → 已断开（SocketConnect 内部 10 秒后自动重试）");
                isConnected = false;
                stopHeartbeat();
                notifyListeners(false);
            }
        });

        sc.setRemoteAddress(ip, ipport);
        connect = sc;
        new Thread(sc).start();
    }

    // ============ 心跳 ============
    private void startHeartbeat() {
        stopHeartbeat();
        heartbeatFuture = heartbeatExecutor.scheduleAtFixedRate(() -> {
            if (isConnected) {
                SendBuff(HEARTBEAT_COMMAND, 0, 0);
                Log.d(TAG, "♥ 心跳发送");
            }
        }, HEARTBEAT_INTERVAL, HEARTBEAT_INTERVAL, TimeUnit.MILLISECONDS);
    }

    private void stopHeartbeat() {
        if (heartbeatFuture != null) {
            heartbeatFuture.cancel(false);
            heartbeatFuture = null;
        }
    }

    // ============ 发送数据 ============
  /*  public void SendBuff(int command, int cardId, int result) {
        SocketConnect c = connect;
        if (!isConnected || c == null) {
            Log.e(TAG, "SendBuff 失败：未连接");
            return;
        }

        byte[] data = new byte[7];
        data[0] = (byte) 0xFE;
        data[1] = (byte) 0xFF;
        data[2] = (byte) command;
        data[3] = (byte) (cardId / 256);
        data[4] = (byte) (cardId % 256);
        data[5] = (byte) (result / 256);
        data[6] = (byte) (result % 256);

        c.write(data);
        Log.i(TAG, "发送数据：" + Arrays.toString(data));
    }*/

    // ============ 发送数据 ============
    public void SendBuff(int command, int cardId, int result) {
        SocketConnect c = connect;
        if (!isConnected || c == null) {
            Log.e(TAG, "SendBuff 失败：未连接");
            return;
        }

        byte[] data = new byte[7];
        data[0] = (byte) 0xFE;
        data[1] = (byte) 0xFF;
        data[2] = (byte) command;
        data[3] = (byte) (cardId / 256);
        data[4] = (byte) (cardId % 256);
        data[5] = (byte) (result / 256);
        data[6] = (byte) (result % 256);

        // ⭐ 核心修复：把 write 放到子线程去执行
        sendExecutor.execute(() -> {
            c.write(data);
            Log.i(TAG, "发送数据：" + Arrays.toString(data));
        });
    }
}