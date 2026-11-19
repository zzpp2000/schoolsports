package com.yyg.schoolsports.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.yyg.schoolsports.Bean.BeanData;
import com.yyg.schoolsports.MyApplication;
import com.yyg.schoolsports.Protocol.SerialDriver;
import com.yyg.schoolsports.R;
import com.yyg.schoolsports.Socket.DateConnect;
import com.yyg.schoolsports.greendao.BeanDataDao;
import com.yyg.schoolsports.greendao.BeanDataDaoMaster;
import com.yyg.schoolsports.utils.PlayAudio;
import com.yyg.schoolsports.utils.XmlUtil;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class HeightWTestActivity extends AppCompatActivity {
    private String TAG = "HeightWTestActivity";
    private TextView text_cardid;
    private TextView text_heitestnum;
    private TextView text_weitestnum;
    private TextView text_bmitestnum;
    private Button btn_cacel;
    private Button btn_confirm;

    private int heightdata;
    private int weightdata;
    private int bmidata;
    private int testmode;
    private String testid;

    // 是否已出成绩
    private boolean hasResult = false;

    SerialDriver serialDriver = new SerialDriver();
    DateConnect dateConnect = DateConnect.getInstance();


    private static List<BeanData> hwbList;

    // 后台线程与 Handler
    private HandlerThread mHandlerThread;
    private Handler mBackgroundHandler;

    // 循环任务
    private Runnable mrunnable;
    private Runnable srunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heightwtest);

        Intent intent = getIntent();
        testid = intent.getStringExtra("testcardid");
        testmode = intent.getIntExtra("TESTMODE", 0);
        text_cardid = findViewById(R.id.cardid);
        text_heitestnum = findViewById(R.id.heitestnum);
        text_weitestnum = findViewById(R.id.weitestnum);
        text_bmitestnum = findViewById(R.id.bmitestnum);
        btn_cacel = findViewById(R.id.hwtestcan);
        btn_confirm = findViewById(R.id.hwtestconfirm);
        text_cardid.setText(testid);

        // 初始状态：无成绩，确认键不可用
        hasResult = false;
        btn_confirm.setEnabled(false);

        dateConnect.DataConnect();  // 或者保留 dateConnect.DataConnect()，我单例版里保留了兼容

        // 初始化后台线程
        mHandlerThread = new HandlerThread("SerialWorker");
        mHandlerThread.start();
        mBackgroundHandler = new Handler(mHandlerThread.getLooper());

        // 初始化循环任务
        initRunnables();

        // 启动握手循环
        mBackgroundHandler.postDelayed(mrunnable, 1000);
        serialDriver.openSerialPort();

        serialDriver.setOnDataReceiveListener(new SerialDriver.OnDataReceiveListener() {
            @Override
            public void onQrDataReceive(final byte[] buff, int size) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (XmlUtil.ProtocolType == 0) {
                            if ((buff[0] & 0xff) == 0x68 && (buff[1] & 0xff) == 0x81) {
                                mBackgroundHandler.removeCallbacks(mrunnable);
                                mBackgroundHandler.postDelayed(srunnable, 1000);
                            } else if ((buff[0] & 0xff) == 0x68 && (buff[1] & 0xff) == 0x82) {
                                Log.i(TAG, "开始测试");
                                mBackgroundHandler.removeCallbacks(mrunnable);
                                mBackgroundHandler.removeCallbacks(srunnable);
                                PlayAudio.playaudio(6);
                            } else if ((buff[0] & 0xff) == 0x68 && (buff[1] & 0xff) == 0x84) {
                                Log.i(TAG, "测试成绩");
                                float heightfloat = (float) (buff[2] * 256 + (buff[3] & 0xff)) / 10;
                                float weightfloat = (float) (buff[4] * 256 + (buff[5] & 0xff)) / 10;
                                float bmifloat = (float) (weightfloat * 100000) / (heightfloat * heightfloat) / 10;
                                DecimalFormat decimalFormat = new DecimalFormat("0.0");
                                heightdata = buff[2] * 256 + (buff[3] & 0xff);
                                weightdata = buff[4] * 256 + (buff[5] & 0xff);
                                bmidata = (weightdata * 1000000) / (heightdata * heightdata);
                                text_heitestnum.setText(Float.toString(heightfloat));
                                text_weitestnum.setText(Float.toString(weightfloat));
                                text_bmitestnum.setText(decimalFormat.format(bmifloat));
                                PlayAudio.playaudio(5);

                                // 已有成绩，启用确认键
                                hasResult = true;
                                btn_confirm.setEnabled(true);

                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                                Date currentdate = new Date();
                                String CurrentDate = simpleDateFormat.format(currentdate);
                                //BeanDataDaoMaster.insertGood(new BeanData(CurrentDate, "", "", "", "", testid, "", "", "", "", "", "", "", "", "", Float.toString(heightfloat), Float.toString(weightfloat), decimalFormat.format(bmifloat)));
                            }
                        }
                    }
                });
            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 未出成绩，不允许确认
                if (!hasResult) {
                    return;
                }
                // 防止重复点击
                btn_confirm.setEnabled(false);

                // 在后台线程执行发送操作，避免阻塞 UI
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (testmode == 0) {   // 刷卡模式
                            dateConnect.SendBuff(2, Integer.parseInt(testid), heightdata);
                            dateConnect.SendBuff(3, Integer.parseInt(testid), weightdata);

                            // ================= 2. 直接在这里写入 SQLite 数据库 =================
                            // 生成当前时间
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                            Date currentdate = new Date();
                            String currentDate = simpleDateFormat.format(currentdate);

                            String cardNumStr = String.valueOf(testid);
                            String status = "未上传";

                            // 构建 3 条记录（注意第一个参数传 null，代表自增主键）
                            BeanData heightBean = new BeanData(null, currentDate, cardNumStr, "", "", "", cardNumStr, "", "", "", "","", "", "", "", "", "身高", Integer.toString(heightdata), status);
                            BeanData weightBean = new BeanData(null, currentDate, cardNumStr, "", "", "", cardNumStr, "", "", "", "","", "", "", "", "", "体重", Integer.toString(weightdata), status);
                            BeanData bmiBean = new BeanData(null, currentDate, cardNumStr, "", "", "", cardNumStr, "", "", "", "", "", "", "","", "", "BMI", String.valueOf(bmidata), status);

                            // 获取 Dao 并批量插入
                            try {
                                BeanDataDao beanDataDao = MyApplication.getDaoSession().getBeanDataDao();
                                beanDataDao.insertInTx(heightBean, weightBean, bmiBean);
                                Log.i("TAG", "身高、体重、BMI 已存入数据库");
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e("TAG", "插入数据库失败: " + e.getMessage());
                            }
                            // ==================================================================

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(HeightWTestActivity.this, WriteCardActivity.class);
                                    intent.putExtra("testitem", "HWB");
                                    intent.putExtra("testcardid", testid);
                                    intent.putExtra("testheight", Integer.toString(heightdata));
                                    intent.putExtra("testweight", Integer.toString(weightdata));
                                    intent.putExtra("testbmi", Integer.toString(bmidata));
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        } else if (testmode == 1) {  // 二维码模式
                            dateConnect.SendBuff(2, Integer.parseInt(testid), heightdata);
                            dateConnect.SendBuff(3, Integer.parseInt(testid), weightdata);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    finish();
                                }
                            });
                        } else if (testmode == 2) {  // 测试模式
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    finish();
                                }
                            });
                        }
                    }
                }).start();
            }
        });

        btn_cacel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBackgroundHandler.removeCallbacks(mrunnable);
                mBackgroundHandler.removeCallbacks(srunnable);
                if (mHandlerThread != null) {
                    mHandlerThread.quitSafely();
                }
                finish();
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
                    serialDriver.sendconnectOldStatus();
                    mBackgroundHandler.postDelayed(this, 1000);
                } else if (XmlUtil.ProtocolType == 1) {
                    serialDriver.sendconnectStatus();
                    mBackgroundHandler.postDelayed(this, 500);
                }
            }
        };

        srunnable = new Runnable() {
            @Override
            public void run() {
                if (XmlUtil.ProtocolType == 0) {
                    serialDriver.sendoldtest();
                } else if (XmlUtil.ProtocolType == 1) {
                    serialDriver.sendconnectStatus();
                }
                mBackgroundHandler.postDelayed(this, 1000);
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBackgroundHandler != null) {
            mBackgroundHandler.removeCallbacks(mrunnable);
            mBackgroundHandler.removeCallbacks(srunnable);
        }
        if (mHandlerThread != null) {
            mHandlerThread.quitSafely();
        }
    }
}