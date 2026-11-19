package com.yyg.schoolsports.utils;

import com.yyg.schoolsports.Activity.HeightWeightActivity;
import com.yyg.schoolsports.Activity.MedballActivity;
import com.yyg.schoolsports.Activity.Meter50Activity;
import com.yyg.schoolsports.Activity.Meter508Activity;
import com.yyg.schoolsports.Activity.PLUActivity;
import com.yyg.schoolsports.Activity.SFBActivity;
import com.yyg.schoolsports.Activity.SLJActivity;
import com.yyg.schoolsports.Activity.SargJumpActivity;
//import com.yyg.schoolsports.Activity.SightActivity;  // 假设你未来会实现，暂用占位
import com.yyg.schoolsports.Activity.SuActivity;
import com.yyg.schoolsports.Activity.VJUMPActivity;
import com.yyg.schoolsports.Activity.VitalcapacityActivity;
import com.yyg.schoolsports.Activity.SetupActivity;

import java.util.HashMap;
import java.util.Map;

public class DeviceMapping {

    // 设备编码常量
    public static final String DEVICE_HWB = "HWB";
    public static final String DEVICE_VC = "VC";
    public static final String DEVICE_50M = "50M";
    public static final String DEVICE_SFB = "SFB";
    public static final String DEVICE_SU = "SU";
    public static final String DEVICE_508M = "508M";
    public static final String DEVICE_SLJ = "SLJ";
    public static final String DEVICE_VJUMP = "VJUMP";
    public static final String DEVICE_800M = "800M";
    public static final String DEVICE_SKIP = "Skip";
    public static final String DEVICE_PLU = "PLU";
    public static final String DEVICE_MDB = "MDB";
    public static final String DEVICE_SGJ = "SGJ";
    public static final String DEVICE_SIGHT = "Sight";  // 新增
    public static final String DEVICE_TestData = "Tdata"; //测试数据
    public static final String DEVICE_SET = "DSET";

    // 1. 用于跳转的 Map (你提供的代码片段)
    private static final Map<String, Class<?>> DEVICE_ACTIVITY_MAP = new HashMap<>();

    // 2. 用于显示中文名称的 Map
    private static final Map<String, String> DEVICE_NAME_MAP = new HashMap<>();

    static {
        DEVICE_ACTIVITY_MAP.put(DEVICE_HWB, HeightWeightActivity.class);
        DEVICE_ACTIVITY_MAP.put(DEVICE_VC, VitalcapacityActivity.class);
        DEVICE_ACTIVITY_MAP.put(DEVICE_50M, Meter50Activity.class);
        DEVICE_ACTIVITY_MAP.put(DEVICE_SFB, SFBActivity.class);
        DEVICE_ACTIVITY_MAP.put(DEVICE_SU, SuActivity.class);
        DEVICE_ACTIVITY_MAP.put(DEVICE_508M, Meter508Activity.class);
        DEVICE_ACTIVITY_MAP.put(DEVICE_SLJ, SLJActivity.class);
        DEVICE_ACTIVITY_MAP.put(DEVICE_VJUMP, VJUMPActivity.class);
        DEVICE_ACTIVITY_MAP.put(DEVICE_PLU, PLUActivity.class);
        DEVICE_ACTIVITY_MAP.put(DEVICE_MDB, MedballActivity.class);
        DEVICE_ACTIVITY_MAP.put(DEVICE_SGJ, SargJumpActivity.class);
        //DEVICE_ACTIVITY_MAP.put(DEVICE_SIGHT, SightActivity.class); // 暂映射，请确保有 SightActivity
        // 800M 和 Skip 原代码未实现，暂留空，你可按需添加
        DEVICE_ACTIVITY_MAP.put(DEVICE_TestData, SargJumpActivity.class);
        DEVICE_ACTIVITY_MAP.put(DEVICE_SET, SetupActivity.class);

        // 初始化名称 Map
        DEVICE_NAME_MAP.put(DEVICE_HWB, "身高体重测试仪");
        DEVICE_NAME_MAP.put(DEVICE_50M, "50米测试仪");
        DEVICE_NAME_MAP.put(DEVICE_VC, "肺活量测试仪");
    }

    public static Class<?> getActivityClass(String deviceCode) {
        return DEVICE_ACTIVITY_MAP.get(deviceCode);
    }

    // 根据设备代码获取中文显示名称 (默认返回 "未知测试仪")
    public static String getDeviceName(String deviceCode) {
        return DEVICE_NAME_MAP.containsKey(deviceCode) ? DEVICE_NAME_MAP.get(deviceCode) : "未知测试仪";
    }
}