package com.yyg.schoolsports.Socket;

/**
 * Socket 回调接口（仅保留接口，Socket 连接逻辑已迁移到 SocketConnect 中）
 */
public class SocketBase {

    public interface SocketCallback {
        /** 连接成功 */
        void connected();

        /** 收到数据 */
        void receive(byte[] buffer);

        /** 连接断开 */
        void disconnect();
    }
}