package com.yyg.schoolsports.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.yyg.schoolsports.Protocol.SerialDriver;
import com.yyg.schoolsports.R;
import com.yyg.schoolsports.utils.XmlUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class SetupActivity extends AppCompatActivity {

    private Button searchidBtn;
    private Button modifyidBtn;
    private Button devicesetupBtn;
    private Button searchipBtn;
    private Button modifyipBtn;

    private TextView searchIdTvw;

    private EditText modifyidEdt;
    private EditText modifyipEdt;

    SerialDriver serialDriver = new SerialDriver();

    // 后台线程与 Handler
    private HandlerThread mHandlerThread;
    private Handler mBackgroundHandler;
    private Runnable mrunnable;

    private String TAG = "SetupActivity";

    // 存储权限请求码
    private static final int REQUEST_STORAGE_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);     // 系统设置

        // 绑定控件
        searchidBtn = findViewById(R.id.searchid_btn);
        modifyidBtn = findViewById(R.id.modifyid_btn);
        devicesetupBtn = findViewById(R.id.DvSetup_btn);
        searchIdTvw = findViewById(R.id.searchid_tVw);
        modifyidEdt = findViewById(R.id.modifyid_edt);

        searchipBtn = findViewById(R.id.searchip_btn);
        modifyipBtn = findViewById(R.id.modifyip_btn);
        modifyipEdt = findViewById(R.id.modifyip_edt);

        // 初始化后台线程
        mHandlerThread = new HandlerThread("SetupSerialWorker");
        mHandlerThread.start();
        mBackgroundHandler = new Handler(mHandlerThread.getLooper());

        // 初始化循环任务
        initRunnables();

        // ================== 检索ID ==================
        searchidBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 启动检索id循环
                mBackgroundHandler.postDelayed(mrunnable, 500);
                serialDriver.openSerialPort();
            }
        });

        // ================== 修改ID ==================
        modifyidBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputStr = modifyidEdt.getText().toString().trim();

                if (inputStr.isEmpty()) {
                    Toast.makeText(SetupActivity.this, "请输入PANID", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (inputStr.length() != 4) {
                    Toast.makeText(SetupActivity.this, "PANID必须为4位16进制字符（如1234）", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    int panIdInt = Integer.parseInt(inputStr, 16);
                    byte highPanid = (byte) ((panIdInt >> 8) & 0xFF);
                    byte lowPanid = (byte) (panIdInt & 0xFF);

                    String debugStr = String.format("高字节: 0x%02X, 低字节: 0x%02X", highPanid, lowPanid);
                    Log.d("PANID_TEST", debugStr);

                    serialDriver.sendZigbeeSetPanidCmdCmd((byte) 0x02, highPanid, lowPanid);
                    serialDriver.openSerialPort();

                } catch (NumberFormatException e) {
                    Toast.makeText(SetupActivity.this, "输入格式错误，请输入16进制字符(0-9, A-F)", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // ================== 检索IP ==================
        searchipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!hasStoragePermission()) {
                    requestStoragePermission();
                    Toast.makeText(SetupActivity.this, "请先授予存储权限", Toast.LENGTH_SHORT).show();
                    return;
                }

                String ip = readServerIpFromDisposeCodeXml();
                if (ip == null || ip.isEmpty()) {
                    Toast.makeText(SetupActivity.this,
                            "未读取到 ServerIP，请检查 Download/DisposeCode.xml",
                            Toast.LENGTH_SHORT).show();
                } else {
                    modifyipEdt.setText(ip);
                    Toast.makeText(SetupActivity.this, "当前IP：" + ip, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // ================== 修改IP ==================
        modifyipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!hasStoragePermission()) {
                    requestStoragePermission();
                    Toast.makeText(SetupActivity.this, "请先授予存储权限", Toast.LENGTH_SHORT).show();
                    return;
                }

                String ip = modifyipEdt.getText().toString().trim();

                if (!isValidIp(ip)) {
                    Toast.makeText(SetupActivity.this,
                            "请输入正确的IP地址，例如：192.168.3.21",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean ok = saveServerIpToDisposeCodeXml(ip);
                if (ok) {
                    Toast.makeText(SetupActivity.this, "IP修改成功", Toast.LENGTH_SHORT).show();

                    // 如果 XmlUtil 里缓存了 ServerIP，请同步更新
                    // 例如：XmlUtil.ServerIP = ip;
                    // 或者重新调用 XmlUtil 的加载方法
                } else {
                    Toast.makeText(SetupActivity.this,
                            "IP修改失败，请检查存储权限或文件是否存在",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        // ================== 设置按钮 ==================
        devicesetupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // ================== 串口数据接收 ==================
        serialDriver.setOnDataReceiveListener(new SerialDriver.OnDataReceiveListener() {
            @Override
            public void onQrDataReceive(final byte[] buff, int size) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (XmlUtil.ProtocolType == 0) {
                            if ((buff[0] & 0xff) == 0xFC && (buff[1] & 0xff) == 0x03 && (buff[2] & 0xff) == 0x02) {// 得到了panid
                                mBackgroundHandler.removeCallbacks(mrunnable);

                                int panId = ((buff[5] & 0xFF) << 8) | (buff[4] & 0xFF);
                                String panIdStr = String.format("%04X", panId);

                                runOnUiThread(() -> searchIdTvw.setText(panIdStr));

                            } else if ((buff[0] & 0xff) == 0xFC && (buff[1] & 0xff) == 0x06 && (buff[2] & 0xff) == 0x02) {
                                Toast.makeText(SetupActivity.this, "panid 修改成功", Toast.LENGTH_SHORT).show();
                            } else if ((buff[0] & 0xff) == 0xFC && (buff[1] & 0xff) == 0x86 && (buff[2] & 0xff) == 0x02) {
                                Toast.makeText(SetupActivity.this, "panid 修改失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });
    }

    /**
     * 初始化循环任务
     */
    private void initRunnables() {
        mrunnable = new Runnable() {
            @Override
            public void run() {
                if (XmlUtil.ProtocolType == 0) {
                    serialDriver.sendZigbeeQueryCmd((byte) 0x02);
                    mBackgroundHandler.postDelayed(this, 1000);
                } else if (XmlUtil.ProtocolType == 1) {
                    // serialDriver.sendconnectStatus();
                    // mBackgroundHandler.postDelayed(this, 500);
                }
            }
        };
    }

    // ============================================================
    // 以下为 IP 检索 / 修改相关方法
    // ============================================================

    /**
     * 获取 DisposeCode.xml 文件
     */
    private File getDisposeCodeFile() {
        // 标准 Download 目录：/storage/emulated/0/Download/DisposeCode.xml
        File downloadDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS);
        File file = new File(downloadDir, "DisposeCode.xml");

        if (file.exists()) {
            return file;
        }

        // 兼容小写 download 目录
        File lowerFile = new File(Environment.getExternalStorageDirectory(),
                "download/DisposeCode.xml");
        if (lowerFile.exists()) {
            return lowerFile;
        }

        // 默认仍返回标准 Download 路径，用于创建文件
        return file;
    }

    /**
     * 读取 ServerIP
     */
    private String readServerIpFromDisposeCodeXml() {
        File file = getDisposeCodeFile();

        if (!file.exists()) {
            Log.e(TAG, "DisposeCode.xml 不存在：" + file.getAbsolutePath());
            return null;
        }

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("ServerIP");
            if (nodeList.getLength() > 0) {
                return nodeList.item(0).getTextContent().trim();
            }
        } catch (Exception e) {
            Log.e(TAG, "读取 ServerIP 失败", e);
        }

        return null;
    }

    /**
     * 写入 ServerIP
     */
    private boolean saveServerIpToDisposeCodeXml(String ip) {
        File file = getDisposeCodeFile();

        try {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document doc;
            Element root;

            if (file.exists()) {
                doc = builder.parse(file);
                doc.getDocumentElement().normalize();
                root = doc.getDocumentElement();
            } else {
                doc = builder.newDocument();
                root = doc.createElement("DisposeCode");
                doc.appendChild(root);

                Element deviceItem = doc.createElement("DeviceItem");
                deviceItem.setTextContent("50M");
                root.appendChild(deviceItem);

                Element serverIp = doc.createElement("ServerIP");
                serverIp.setTextContent(ip);
                root.appendChild(serverIp);

                Element version = doc.createElement("Version");
                version.setTextContent("1");
                root.appendChild(version);
            }

            NodeList nodeList = doc.getElementsByTagName("ServerIP");
            Element serverIpElement;

            if (nodeList.getLength() > 0) {
                serverIpElement = (Element) nodeList.item(0);
            } else {
                serverIpElement = doc.createElement("ServerIP");
                root.appendChild(serverIpElement);
            }

            serverIpElement.setTextContent(ip);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            transformer.transform(new DOMSource(doc), new StreamResult(file));

            Log.d(TAG, "ServerIP 保存成功：" + ip + " -> " + file.getAbsolutePath());
            return true;

        } catch (Exception e) {
            Log.e(TAG, "写入 ServerIP 失败", e);
            return false;
        }
    }

    /**
     * 校验 IP 地址格式
     */
    private boolean isValidIp(String ip) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }

        String[] parts = ip.split("\\.");
        if (parts.length != 4) {
            return false;
        }

        for (String part : parts) {
            if (part.isEmpty() || part.length() > 3) {
                return false;
            }

            int num;
            try {
                num = Integer.parseInt(part);
            } catch (NumberFormatException e) {
                return false;
            }

            if (num < 0 || num > 255) {
                return false;
            }
        }

        return true;
    }

    // ============================================================
    // 存储权限相关
    // ============================================================

    private boolean hasStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            return ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(
                        Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_STORAGE_CODE);
            } catch (Exception e) {
                Intent intent = new Intent(
                        Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, REQUEST_STORAGE_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },
                    REQUEST_STORAGE_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "存储权限已授予", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "存储权限被拒绝，无法读写IP配置", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBackgroundHandler != null) {
            mBackgroundHandler.removeCallbacks(mrunnable);
        }
        if (mHandlerThread != null) {
            mHandlerThread.quitSafely();
        }
    }
}