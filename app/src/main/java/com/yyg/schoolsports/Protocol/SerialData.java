package com.yyg.schoolsports.Protocol;

import java.util.Arrays;

import static com.yyg.schoolsports.utils.Utils.bytesToHexString;

public class SerialData {
    private static final String TAG= " SerialData";
    public class SerialPack {

        public byte[] Head;
        public byte[] length;
        public byte[] Command;
        public byte[] testnum;  //项目编号
        public byte[] maindevnum;  //主设备编号
        public byte[] subdevive;  //子设备主编号
        public byte[] subdevivenum;  //子设备子编号
        public byte[] crc;
        public byte[] end;

        public byte[] serialtx;
        public int serialLen;
        public byte[] tempdata;

        public SerialPack() {
            Head = new byte[1];
            length = new byte[1];
            Command = new byte[1];
            testnum = new byte[1];
            maindevnum = new byte[1];
            subdevive = new byte[1];
            subdevivenum = new byte[1];
            crc = new byte[1];
            end = new byte[1];

            serialtx = new byte[128];
            serialLen = 0;
            tempdata = new byte[128];
        }

        @Override
        public String toString() {
            return "SerialPack{" +
                    "Head=" + bytesToHexString(Head) +
                    ", length=" + bytesToHexString(length) +
                    ", Command=" + bytesToHexString(Command) +
                    ", testnum=" + bytesToHexString(testnum) +
                    ", maindevnum=" + bytesToHexString(maindevnum) +
                    ", subdevive=" + bytesToHexString(subdevive) +
                    ", subdevivenum=" + bytesToHexString(subdevivenum) +
                    ", crc=" + bytesToHexString(crc) +
                    ", end=" + bytesToHexString(end) +
                    ", serialLen=" + serialLen +
                    ", serialtx=" + bytesToHexString(serialtx) +
                    '}';
        }
    }

    public byte[] SerialConnect(int type,int Testnum,int Subdevive){
        SerialPack serialPack = new SerialPack();

        byte [] snedrx = new byte[9];
        Arrays.fill(serialPack.serialtx,(byte)0);
        Arrays.fill(serialPack.tempdata,(byte)0);
        Arrays.fill(snedrx,(byte)0);

        serialPack.Head[0] = (byte)0xaa;
        serialPack.Command[0] = (byte)type;
        serialPack.testnum[0] = (byte)Testnum;
        serialPack.subdevive[0] = (byte)Testnum;
        serialPack.subdevivenum[0] = (byte)Subdevive;
        serialPack.end[0] = (byte)0xbb;
        serialPack.length[0] = 0x08;

        System.arraycopy(serialPack.Head,0,serialPack.serialtx,0,1);
        System.arraycopy(serialPack.length,0,serialPack.serialtx,1,1);
        System.arraycopy(serialPack.Command,0,serialPack.serialtx,2,1);
        System.arraycopy(serialPack.testnum,0,serialPack.serialtx,3,1);
        System.arraycopy(serialPack.maindevnum,0,serialPack.serialtx,4,1);
        System.arraycopy(serialPack.subdevive,0,serialPack.serialtx,5,1);
        System.arraycopy(serialPack.subdevivenum,0,serialPack.serialtx,6,1);

        serialPack.crc[0] = (byte)XorCRC(serialPack.serialtx,7);
        System.arraycopy(serialPack.crc,0,serialPack.serialtx,7,1);
        System.arraycopy(serialPack.end,0,serialPack.serialtx,8,1);
        System.arraycopy(serialPack.serialtx,0,snedrx,0,9);



        StringBuilder hexStringBuilder = new StringBuilder();
        hexStringBuilder.append("[");

        for (byte b : snedrx) {
            // 将 byte 转换为 int，然后当作无符号数处理
            int unsignedByte = b & 0xFF; // 通过与 0xFF 进行位与操作来确保结果是无符号的
            String hex = Integer.toHexString(unsignedByte).toUpperCase();
            if (hex.length() == 1) {
                // 如果只有一个字符（即 0-F），则在前面添加 '0'
                hex = "0" + hex;
            }
            hexStringBuilder.append(hex);
            if (hexStringBuilder.length() > 1) {
                //hexStringBuilder.append(", ");
            }
        }

        hexStringBuilder.append("]");
        String hexString = hexStringBuilder.toString();
        System.out.println(hexString);

        return snedrx;
    }

    public byte[] SerialOldConnect(int type){
        byte [] snedrx = new byte[7];

        snedrx[0] = (byte)0xfd;
        snedrx[1] = (byte)0x02;
        snedrx[2] = (byte)0x01;
        snedrx[3] = (byte)0x00;
        snedrx[4] = (byte)0x68;
        snedrx[5] = (byte)type;

        snedrx[6] = (byte)type;
        StringBuilder hexStringBuilder = new StringBuilder();
        hexStringBuilder.append("[");

        for (byte b : snedrx) {
            // 将 byte 转换为 int，然后当作无符号数处理
            int unsignedByte = b & 0xFF; // 通过与 0xFF 进行位与操作来确保结果是无符号的
            String hex = Integer.toHexString(unsignedByte).toUpperCase();
            if (hex.length() == 1) {
                // 如果只有一个字符（即 0-F），则在前面添加 '0'
                hex = "0" + hex;
            }
            hexStringBuilder.append(hex);
            if (hexStringBuilder.length() > 1) {
                hexStringBuilder.append(", ");
            }
        }

        hexStringBuilder.append("]");
        String hexString = hexStringBuilder.toString();
        System.out.println(hexString);


        return snedrx;
    }


    public byte[] SerialZigbeeQueryCmd(byte cmd){
        byte [] snedrx = new byte[7];

        snedrx[0] = (byte)0xfc;
        snedrx[1] = (byte)0x03;
        snedrx[2] = (byte)cmd;
        snedrx[3] = (byte)0x00;
        snedrx[4] = (byte)0x00;
        snedrx[5] = (byte)0x00;
        snedrx[6] = CRC(snedrx,6);

        StringBuilder hexStringBuilder = new StringBuilder();
        hexStringBuilder.append("[");

        for (byte b : snedrx) {
            // 将 byte 转换为 int，然后当作无符号数处理
            int unsignedByte = b & 0xFF; // 通过与 0xFF 进行位与操作来确保结果是无符号的
            String hex = Integer.toHexString(unsignedByte).toUpperCase();
            if (hex.length() == 1) {
                // 如果只有一个字符（即 0-F），则在前面添加 '0'
                hex = "0" + hex;
            }
            hexStringBuilder.append(hex);
            if (hexStringBuilder.length() > 1) {
                hexStringBuilder.append(", ");
            }
        }

        hexStringBuilder.append("]");
        String hexString = hexStringBuilder.toString();
        System.out.println(hexString);


        return snedrx;
    }



    public byte[] SerialZigbeeSetPanidCmd(byte cmd,byte highbyte,byte lowbyte){ //panid highbyte lowbyte
        byte [] snedrx = new byte[7];

        snedrx[0] = (byte)0xfc;
        snedrx[1] = (byte)0x06;
        snedrx[2] = (byte)cmd;
        snedrx[3] = (byte)0x00;
        snedrx[4] = (byte)lowbyte;
        snedrx[5] = (byte)highbyte;
        snedrx[6] = CRC(snedrx,6);

        StringBuilder hexStringBuilder = new StringBuilder();
        hexStringBuilder.append("[");

        for (byte b : snedrx) {
            // 将 byte 转换为 int，然后当作无符号数处理
            int unsignedByte = b & 0xFF; // 通过与 0xFF 进行位与操作来确保结果是无符号的
            String hex = Integer.toHexString(unsignedByte).toUpperCase();
            if (hex.length() == 1) {
                // 如果只有一个字符（即 0-F），则在前面添加 '0'
                hex = "0" + hex;
            }
            hexStringBuilder.append(hex);
            if (hexStringBuilder.length() > 1) {
                hexStringBuilder.append(", ");
            }
        }

        hexStringBuilder.append("]");
        String hexString = hexStringBuilder.toString();
        System.out.println(hexString);


        return snedrx;
    }


    /**
     * 异或校验
     * @param data 需要校验的数据
     * @return 校验码
     */
    public static int XorCRC(byte[] data,int length) {
        byte[] buf = new byte[length];// 存储需要产生校验码的数据
        for (int i = 0; i < length; i++) {   //去掉包头
            buf[i] = data[i+1];
        }
        int len = buf.length;
        int crc = 0;
        for (int pos = 0; pos < len; pos++) {
            crc ^= (int) buf[pos]; // XOR byte into least sig. byte of crc
        }
        return crc;
    }

    /**
     * 异或校验
     * @param data 需要校验的数据
     * @return 校验码
     */
    public static byte CRC(byte[] data,int length) {

        int len = length;
        byte crc = 0;
        for (int pos = 0; pos < len; pos++) {
            crc ^= (int) data[pos]; // XOR byte into least sig. byte of crc
        }
        return crc;
    }
}
