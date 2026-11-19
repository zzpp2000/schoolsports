package com.yyg.schoolsports.Activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.yyg.schoolsports.R;
import com.yyg.schoolsports.utils.XmlUtil;

public class ProjectSetupActivity extends AppCompatActivity {

    private static final String TAG = "ProjectSetup";

    // UI 控件
    private View llProjectSelector; // 包裹层的 View
    private TextView tvProjectName;
    private EditText etGroupCount, etGroupPeople, etParams, etChannel, etGroupAddress;
    private Button btnSave, btnCancel;

    // 下拉菜单数据 (显示的中文)
    private final String[] projectNames = {
            "身高体重", "握力", "肺活量", "坐位体前屈", "屈臂悬垂",
            "仰卧起坐", "仰卧举腿", "立定跳远", "引体向上", "跳绳",
            "实心球", "排球垫球", "篮球运球", "足球运球"
    };

    // 对应的设备代码 (必须与 DeviceMapping 中的常量对应，用于写入 XML)
    private final String[] projectCodes = {
            "HWB", "GL", "VC", "SFB", "QBXT",
            "YWQZ", "YWJT", "LDTY", "YTXS", "TS",
            "SXQ", "PQD", "LQYQ", "ZQYQ"
    };

    private int selectedProjectIndex = 0; // 当前选中的索引

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_setup);

        // 动态设置弹窗的宽度和高度比例 (例如屏幕宽度的 80%，最大不超过 800dp)
        if (getWindow() != null) {
            // 获取屏幕宽度
            int screenWidth = getResources().getDisplayMetrics().widthPixels;
            // 设置弹窗宽度为屏幕宽度的 80%
            int dialogWidth = (int) (screenWidth * 0.8);
            // 防止在超大平板上弹窗过宽
            if (dialogWidth > 800) dialogWidth = 800;

            // 设置弹窗宽度和高度（高度设为 WRAP_CONTENT，防止内容太少时弹窗过高）
            getWindow().setLayout(dialogWidth, WindowManager.LayoutParams.WRAP_CONTENT);
        }

        initViews();
        loadCurrentConfig();
        setupListeners();
    }

    private void initViews() {
        llProjectSelector = findViewById(R.id.ll_project_selector);
        tvProjectName = findViewById(R.id.tv_project_name);
        etGroupCount = findViewById(R.id.et_group_count);
        etGroupPeople = findViewById(R.id.et_group_people);
        etParams = findViewById(R.id.et_params);
        etChannel = findViewById(R.id.et_channel);
        etGroupAddress = findViewById(R.id.et_group_address);
        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);
    }

    private void loadCurrentConfig() {
        // 读取当前配置的设备代码，反推显示的中文
        String currentCode = XmlUtil.getDeviceItem();
        if (currentCode != null) {
            for (int i = 0; i < projectCodes.length; i++) {
                if (projectCodes[i].equals(currentCode)) {
                    selectedProjectIndex = i;
                    tvProjectName.setText(projectNames[i]);
                    break;
                }
            }
        }
    }

    private void setupListeners() {
        // 绑定点击事件到 LinearLayout 包裹层
        if (llProjectSelector != null) {
            llProjectSelector.setOnClickListener(v -> {
                Log.d(TAG, "下拉框被点击了");
                showProjectSelectionDialog();
            });
        } else {
            Log.e(TAG, "严重错误：找不到 ll_project_selector！");
        }

        // 点击保存
        btnSave.setOnClickListener(v -> saveConfig());

        // 点击放弃更改 (直接关闭页面)
        btnCancel.setOnClickListener(v -> finish());
    }

    private void showProjectSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProjectSetupActivity.this);
        builder.setTitle("选择测试项目");

        builder.setSingleChoiceItems(projectNames, selectedProjectIndex, (dialog, which) -> {
            selectedProjectIndex = which;
            tvProjectName.setText(projectNames[which]);
            dialog.dismiss(); // 选中后自动关闭弹窗
        });

        builder.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void saveConfig() {
        String selectedCode = projectCodes[selectedProjectIndex];

        // 写入 XML 配置 (利用你现有的 XmlUtil)
        XmlUtil.setDeviceItem(selectedCode);

        Log.i(TAG, "保存成功: " + projectNames[selectedProjectIndex] + " (" + selectedCode + ")");
        Toast.makeText(this, "项目设置已保存", Toast.LENGTH_SHORT).show();

        // 可选：保存成功后退出当前页面
        // finish();
    }
}