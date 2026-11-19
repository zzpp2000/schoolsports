package com.yyg.schoolsports.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.yyg.schoolsports.R;
import com.yyg.schoolsports.Socket.DateConnect;
import com.yyg.schoolsports.utils.DeviceMapping;
import com.yyg.schoolsports.utils.XmlUtil;

import android.app.Dialog;
import android.widget.MediaController;
import android.os.Environment;
import android.widget.VideoView;
import java.io.File;

public class MainActivity extends AppCompatActivity
        implements DateConnect.OnConnectionChangeListener {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE_STORAGE = 100;
    private static final String ADMIN_PASSWORD = "1";

    private LinearLayout networkStatusBar;
    private TextView tvNetworkStatus;

    private DrawerLayout drawerLayout;
    private ImageView ivMenu;
    private NavigationView navigationView;

    private TextView tvDeviceTitle;
    private View btnStart;
    private String currentDeviceCode = null;

    private DateConnect dateConnect;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private volatile boolean lastConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        ivMenu = findViewById(R.id.iv_setupmenu);
        navigationView = findViewById(R.id.nav_view);

        tvDeviceTitle = findViewById(R.id.tv_device_title);
        btnStart = findViewById(R.id.btn_start);

        networkStatusBar = findViewById(R.id.network_status_bar);
        tvNetworkStatus = findViewById(R.id.tv_network_status);

        if (checkStoragePermission()) {
            loadConfigAndUpdateUI();
        }

        if (btnStart != null) {
            btnStart.setOnClickListener(v -> autoJumpToDevice());
        }

        if (ivMenu != null) {
            ivMenu.setOnClickListener(v -> showPasswordDialog());
        }

        View btnDemo = findViewById(R.id.btn_demo);
        if (btnDemo != null) {
            btnDemo.setOnClickListener(v -> showDemoVideo());
        }

        View btnQuery = findViewById(R.id.btn_query);
        if (btnQuery != null) {
            btnQuery.setOnClickListener(v -> {
                Log.i(TAG, "点击查询，跳转到测试数据管理界面");
                startActivity(new Intent(MainActivity.this, TestDataActivity.class));
            });
        }

        initButtons();

        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(item -> {
                int id = item.getItemId();

                if (id == R.id.nav_project) {
                    showProjectSetupDialog();
                } else if (id == R.id.nav_basic) {
                    showBasicSetupDialog();
                } else if (id == R.id.nav_home) {
                    Log.i(TAG, "侧滑菜单：点击了首页");
                } else if (id == R.id.nav_system) {
                    Log.i(TAG, "侧滑菜单：点击了系统设置");
                } else if (id == R.id.nav_data) {
                    Log.i(TAG, "侧滑菜单：点击了测试数据管理");
                } else if (id == R.id.nav_user) {
                    Log.i(TAG, "侧滑菜单：点击了用户管理");
                }

                if (drawerLayout != null) {
                    drawerLayout.closeDrawer(GravityCompat.END);
                }
                return true;
            });
        }

        dateConnect = DateConnect.getInstance();
        dateConnect.addListener(this);
        dateConnect.connect();

        if (dateConnect.isConnected()) {
            updateNetworkStatus(true, "服务器已连接");
        }
    }

    private void showPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("管理员验证");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_password, null);
        final EditText etPassword = dialogView.findViewById(R.id.et_password);
        builder.setView(dialogView);

        builder.setPositiveButton("确定", null);
        builder.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());

        final AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String inputPwd = etPassword.getText().toString().trim();
            if (TextUtils.isEmpty(inputPwd)) {
                Toast.makeText(MainActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                return;
            }

            if (ADMIN_PASSWORD.equals(inputPwd)) {
                dialog.dismiss();
                if (drawerLayout != null) {
                    drawerLayout.openDrawer(GravityCompat.END);
                }
            } else {
                Toast.makeText(MainActivity.this, "密码错误，请重新输入", Toast.LENGTH_SHORT).show();
                etPassword.setText("");
            }
        });
    }

    private boolean checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE);
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadConfigAndUpdateUI();
            } else {
                Toast.makeText(this, "需要存储权限才能读取配置文件", Toast.LENGTH_LONG).show();
                if (tvDeviceTitle != null) tvDeviceTitle.setText("无权限读取配置");
            }
        }
    }

    private void loadConfigAndUpdateUI() {
        if (tvDeviceTitle == null) return;

        currentDeviceCode = XmlUtil.getDeviceItem();

        if (currentDeviceCode == null || currentDeviceCode.isEmpty()) {
            tvDeviceTitle.setText("未找到配置");
            Toast.makeText(this, "未找到 Download/DisposeCode.xml 或解析失败", Toast.LENGTH_LONG).show();
            return;
        }

        String deviceName = DeviceMapping.getDeviceName(currentDeviceCode);
        tvDeviceTitle.setText(deviceName);

        if (deviceName.length() >= 7) {
            tvDeviceTitle.setTextSize(60);
        } else {
            tvDeviceTitle.setTextSize(72);
        }
        Log.i(TAG, "配置加载成功: " + currentDeviceCode + " -> " + deviceName);
    }

    private void autoJumpToDevice() {
        if (currentDeviceCode == null || currentDeviceCode.isEmpty()) {
            currentDeviceCode = XmlUtil.getDeviceItem();
            if (currentDeviceCode == null) {
                Toast.makeText(this, "无效的设备配置，无法跳转", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        Class<?> targetClass = DeviceMapping.getActivityClass(currentDeviceCode);
        if (targetClass != null) {
            startActivity(new Intent(this, targetClass));
        } else {
            Toast.makeText(this, "未在 DeviceMapping 中配置该设备: " + currentDeviceCode, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionChanged(boolean connected) {
        Log.i(TAG, "连接状态变化: " + connected);
        updateNetworkStatus(connected, connected ? "服务器已连接" : "服务器已断开连接");
    }

    private void updateNetworkStatus(boolean connected, String text) {
        if (networkStatusBar == null || tvNetworkStatus == null) return;

        int targetVisibility = connected ? View.GONE : View.VISIBLE;

        if (lastConnected == connected && networkStatusBar.getVisibility() == targetVisibility) {
            return;
        }
        lastConnected = connected;

        if (connected) {
            networkStatusBar.setVisibility(View.GONE);
        } else {
            networkStatusBar.setVisibility(View.VISIBLE);
            networkStatusBar.setBackgroundColor(Color.parseColor("#E53935"));
            tvNetworkStatus.setText(text);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dateConnect != null) {
            dateConnect.removeListener(this);
        }
    }

    private void bindButton(int id, String deviceCode, Class<?> targetClass) {
        View view = findViewById(id);
        if (view == null) return;
        view.setOnClickListener(v -> {
            XmlUtil.setDeviceItem(deviceCode);
            startActivity(new Intent(this, targetClass));
        });
    }

    private void initButtons() {
        bindButton(R.id.HeightWeight_btn, DeviceMapping.DEVICE_HWB, HeightWeightActivity.class);
        bindButton(R.id.vitalcapacity_btn, DeviceMapping.DEVICE_VC, VitalcapacityActivity.class);
        bindButton(R.id.meter50_btn, DeviceMapping.DEVICE_50M, Meter50Activity.class);
        bindButton(R.id.sitandreach_btn, DeviceMapping.DEVICE_SFB, SFBActivity.class);
        bindButton(R.id.situp_btn, DeviceMapping.DEVICE_SU, SuActivity.class);
        bindButton(R.id.meter508_btn, DeviceMapping.DEVICE_508M, Meter508Activity.class);
        bindButton(R.id.standinglongjump_btn, DeviceMapping.DEVICE_SLJ, SLJActivity.class);
        bindButton(R.id.jump_btn, DeviceMapping.DEVICE_VJUMP, VJUMPActivity.class);
        bindButton(R.id.pullup_btn, DeviceMapping.DEVICE_PLU, PLUActivity.class);
        bindButton(R.id.medicineball_btn, DeviceMapping.DEVICE_MDB, MedballActivity.class);
        bindButton(R.id.TestDataMangmnt_btn, DeviceMapping.DEVICE_TestData, TestDataActivity.class);
        bindButton(R.id.DeviceSetup_btn, DeviceMapping.DEVICE_SET, SetupActivity.class);
    }

    private void showProjectSetupDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View dialogView = getLayoutInflater().inflate(R.layout.activity_project_setup, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
            int screenWidth = getResources().getDisplayMetrics().widthPixels;
            int targetWidth = (int) (screenWidth * 0.5);
            android.view.WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.width = targetWidth;
            params.height = android.view.WindowManager.LayoutParams.WRAP_CONTENT;
            params.gravity = android.view.Gravity.CENTER;
            dialog.getWindow().setAttributes(params);
            dialog.getWindow().getDecorView().setPadding(0, 0, 0, 0);
        }

        TextView tvProjectName = dialogView.findViewById(R.id.tv_project_name);
        View llProjectSelector = dialogView.findViewById(R.id.ll_project_selector);
        Button btnSave = dialogView.findViewById(R.id.btn_save);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);

        final String[] projectNames = {"身高体重", "握力", "肺活量", "坐位体前屈", "屈臂悬垂", "仰卧起坐", "仰卧举腿", "立定跳远", "引体向上", "跳绳", "实心球", "排球垫球", "篮球运球", "足球运球"};
        final String[] projectCodes = {"HWB", "GL", "VC", "SFB", "QBXT", "YWQZ", "YWJT", "LDTY", "YTXS", "TS", "SXQ", "PQD", "LQYQ", "ZQYQ"};
        final int[] selectedIndex = {0};

        String currentCode = XmlUtil.getDeviceItem();
        if (currentCode != null) {
            for (int i = 0; i < projectCodes.length; i++) {
                if (projectCodes[i].equals(currentCode)) {
                    selectedIndex[0] = i;
                    tvProjectName.setText(projectNames[i]);
                    break;
                }
            }
        }

        llProjectSelector.setOnClickListener(v -> {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("选择测试项目")
                    .setSingleChoiceItems(projectNames, selectedIndex[0], (d, which) -> {
                        selectedIndex[0] = which;
                        tvProjectName.setText(projectNames[which]);
                        d.dismiss();
                    })
                    .setNegativeButton("取消", null)
                    .show();
        });

        btnSave.setOnClickListener(v -> {
            String selectedCode = projectCodes[selectedIndex[0]];
            XmlUtil.setDeviceItem(selectedCode);
            Toast.makeText(MainActivity.this, "项目设置已保存", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
    }

    private void showBasicSetupDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_basic_setup, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
            int screenWidth = getResources().getDisplayMetrics().widthPixels;
            int targetWidth = (int) (screenWidth * 0.5);
            android.view.WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.width = targetWidth;
            params.height = android.view.WindowManager.LayoutParams.WRAP_CONTENT;
            params.gravity = android.view.Gravity.CENTER;
            dialog.getWindow().setAttributes(params);
            dialog.getWindow().getDecorView().setPadding(0, 0, 0, 0);
        }

        TextView tvInputMode = dialogView.findViewById(R.id.tv_input_mode);
        TextView tvAttachment = dialogView.findViewById(R.id.tv_attachment);
        androidx.appcompat.widget.SwitchCompat switchPrint = dialogView.findViewById(R.id.switch_print);
        androidx.appcompat.widget.SwitchCompat switchPush = dialogView.findViewById(R.id.switch_push);
        TextView tvImportPhoto = dialogView.findViewById(R.id.tv_import_photo);
        TextView tvRegisterFace = dialogView.findViewById(R.id.tv_register_face);
        EditText etServerIp = dialogView.findViewById(R.id.et_server_ip);
        Button btnSave = dialogView.findViewById(R.id.btn_basic_save);
        Button btnCancel = dialogView.findViewById(R.id.btn_basic_cancel);

        String currentServerIp = XmlUtil.getServerIP();
        if (currentServerIp != null && !currentServerIp.isEmpty()) {
            etServerIp.setText(currentServerIp);
            etServerIp.setSelection(currentServerIp.length());
        }

        final String[] inputModes = {"体测卡", "身份证", "人脸识别", "二维码"};
        tvInputMode.setOnClickListener(v -> {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("选择输入模式")
                    .setSingleChoiceItems(inputModes, -1, (d, which) -> {
                        tvInputMode.setText(inputModes[which]);
                        d.dismiss();
                    })
                    .setNegativeButton("取消", null)
                    .show();
        });

        final String[] attachments = {"ZIGBEE", "蓝牙", "2.4G"};
        tvAttachment.setOnClickListener(v -> {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("选择附件连接")
                    .setSingleChoiceItems(attachments, -1, (d, which) -> {
                        tvAttachment.setText(attachments[which]);
                        d.dismiss();
                    })
                    .setNegativeButton("取消", null)
                    .show();
        });

        tvImportPhoto.setOnClickListener(v -> Toast.makeText(MainActivity.this, "导入学生照片功能", Toast.LENGTH_SHORT).show());
        tvRegisterFace.setOnClickListener(v -> Toast.makeText(MainActivity.this, "人脸库注册功能", Toast.LENGTH_SHORT).show());

        btnSave.setOnClickListener(v -> {
            String newIp = etServerIp.getText().toString().trim();
            if (TextUtils.isEmpty(newIp)) {
                Toast.makeText(MainActivity.this, "IP不能为空", Toast.LENGTH_SHORT).show();
                return;
            }

            XmlUtil.setServerIP(newIp);

            if (dateConnect != null) {
                Log.i(TAG, "IP地址已更新为: " + newIp + "，正在重新连接服务器...");
                dateConnect.reconnect();   // ⭐ 只调这一个
                Toast.makeText(MainActivity.this, "IP已保存，正在重新连接服务器...", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
    }

    private void showDemoVideo() {
        if (currentDeviceCode == null || currentDeviceCode.isEmpty()) {
            currentDeviceCode = XmlUtil.getDeviceItem();
            if (currentDeviceCode == null) {
                Toast.makeText(this, "未获取到当前项目配置，无法播放演示", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        File videoDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/video");
        File videoFile = new File(videoDir, currentDeviceCode + ".mp4");

        if (!videoFile.exists()) {
            Log.e(TAG, "视频文件不存在: " + videoFile.getAbsolutePath());
            Toast.makeText(this, "未找到该项目的演示视频，请检查：" + videoFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
            return;
        }

        Dialog videoDialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        videoDialog.setContentView(R.layout.dialog_video);

        VideoView videoView = videoDialog.findViewById(R.id.video_view);
        ImageView ivClose = videoDialog.findViewById(R.id.iv_close_video);

        videoView.setVideoPath(videoFile.getAbsolutePath());
        videoView.setMediaController(new MediaController(this));
        videoView.start();
        videoView.requestFocus();

        videoView.setOnCompletionListener(mp -> videoDialog.dismiss());
        videoView.setOnErrorListener((mp, what, extra) -> {
            Toast.makeText(MainActivity.this, "视频播放失败", Toast.LENGTH_LONG).show();
            videoDialog.dismiss();
            return true;
        });

        ivClose.setOnClickListener(v -> {
            if (videoView.isPlaying()) {
                videoView.stopPlayback();
            }
            videoDialog.dismiss();
        });

        videoDialog.show();
    }

}