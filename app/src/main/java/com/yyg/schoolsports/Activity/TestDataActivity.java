package com.yyg.schoolsports.Activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yyg.schoolsports.Adapter.TestDataAdapter;
import com.yyg.schoolsports.Bean.BeanData;
import com.yyg.schoolsports.Bean.RosterBean;
import com.yyg.schoolsports.MyApplication;
import com.yyg.schoolsports.R;
import com.yyg.schoolsports.Socket.DateConnect;
import com.yyg.schoolsports.greendao.BeanDataDao;

import com.yyg.schoolsports.greendao.DaoSession;
import com.yyg.schoolsports.greendao.RosterBeanDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TestDataActivity extends AppCompatActivity {

    private static final String TAG = "TestDataActivity";

    private RecyclerView recyclerView;
    private TestDataAdapter adapter;
    private List<BeanData> dataList = new ArrayList<>();
    private DaoSession daoSession;
    private DateConnect dateConnect; // ⭐ 成员变量，千万别写成局部变量
    private RosterBeanDao rosterDao; // GreenDAO 的 Dao



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testdata);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // ⭐⭐ 关键修复 1：这里不要写 DateConnect，直接赋值给成员变量
        dateConnect = DateConnect.getInstance();
        dateConnect.DataConnect();

        daoSession = MyApplication.getDaoSession();  // 确保这行有
        rosterDao = daoSession.getRosterBeanDao();   // ⭐ 确保这行也有

        // 右上角菜单
        ImageView ivMenu = findViewById(R.id.iv_menu);
        ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });

        // 设置 Socket 数据监听
        if (dateConnect != null) {
            dateConnect.setOnDataReceiveListener(new DateConnect.OnDataReceiveListener() {
                @Override
                public void onDataReceived(byte[] buffer) {
                    // 收到名单数据，转到主线程处理
                    runOnUiThread(() -> handleRosterData(buffer));
                }
            });
        }

        loadDataFromDB();
    }

    private void loadDataFromDB() {
        try {
            BeanDataDao beanDataDao = daoSession.getBeanDataDao();

            // 1. 查询数据库
            dataList = beanDataDao.queryBuilder()
                    .orderDesc(BeanDataDao.Properties.Date)
                    .list();

            // ================= 临时造数据测试UI =================
            if (dataList.isEmpty()) {
                Log.d(TAG, "数据库为空，开始插入临时测试数据...");

                beanDataDao.deleteAll();

                String time = "2026/09/20 17:09:01";

                dataList.add(new BeanData(null, time, "587", "张三", "", "", "12", "", "", "", "", "", "", "", "", "", "肺活量", "433", "未上传"));
                dataList.add(new BeanData(null, time, "587", "张三", "", "", "12", "", "", "", "", "", "", "", "", "", "身高", "165", "未上传"));
                dataList.add(new BeanData(null, time, "587", "张三", "", "", "12", "", "", "", "", "", "", "", "", "", "50米", "3.42", "未上传"));
                dataList.add(new BeanData(null, time, "584", "李四", "", "", "111", "", "", "", "", "", "", "", "", "", "肺活量", "481", "未上传"));
                dataList.add(new BeanData(null, time, "584", "李四", "", "", "111", "", "", "", "", "", "", "", "", "", "身高", "168", "未上传"));

                beanDataDao.insertInTx(dataList);
                Log.d(TAG, "临时数据插入完成，共插入 " + dataList.size() + " 条");
            }
            // ====================================================

            // 2. Adapter
            adapter = new TestDataAdapter(dataList);
            recyclerView.setAdapter(adapter);

            // 3. 底部数量
            TextView tvCount = findViewById(R.id.tv_count);
            if (tvCount != null) {
                tvCount.setText("测试数量：" + dataList.size());
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "查询数据失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        getMenuInflater().inflate(R.menu.menu_testdata, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.menu_upload) {
                    uploadData();
                    return true;
                } else if (itemId == R.id.menu_reupload) {
                    Toast.makeText(TestDataActivity.this, "点击了：重新上传失败数据", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (itemId == R.id.menu_delete_selected) {
                    Set<Long> selectedIds = adapter.getSelectedIds();
                    if (selectedIds == null || selectedIds.isEmpty()) {
                        Toast.makeText(TestDataActivity.this, "请先点击选中要删除的记录！", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    try {
                        daoSession.getBeanDataDao().deleteByKeyInTx(selectedIds);
                        Toast.makeText(TestDataActivity.this, "成功删除 " + selectedIds.size() + " 条记录", Toast.LENGTH_SHORT).show();
                        adapter.clearSelection();
                        loadDataFromDB();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(TestDataActivity.this, "删除失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    return true;
                } else if (itemId == R.id.menu_delete_all) {
                    daoSession.getBeanDataDao().deleteAll();
                    loadDataFromDB();
                    return true;
                } else if (itemId == R.id.menu_export) {
                    Toast.makeText(TestDataActivity.this, "点击了：导出测试数据", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (itemId == R.id.menu_recover) {
                    Toast.makeText(TestDataActivity.this, "点击了：恢复测试数据", Toast.LENGTH_SHORT).show();
                    return true;
                }else if(itemId == R.id.menu_download_roster) {
                    downloadRoster();
                    return true;
                }

                return false;
            }
        });

        popupMenu.show();
    }


    // 发送下载请求
    private void downloadRoster() {
        if (dateConnect == null || !dateConnect.isConnected()) {
            Toast.makeText(this, "服务器未连接，无法下载", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "正在向服务器请求名单...", Toast.LENGTH_SHORT).show();

        // 发送命令：0xA2 (请求下载名单)，参数全填0
        dateConnect.SendBuff(0xA2, 0, 0);
    }

    // 解析收到的名单并存入数据库
    private void handleRosterData(byte[] buffer) {
        try {
            // 1. 打印收到的原始字节长度
            Log.i("RosterDownload", "<<< 收到服务器名单数据包，长度: " + buffer.length + " 字节");

            String dataStr = new String(buffer, "UTF-8");
            if (TextUtils.isEmpty(dataStr)) {
                Log.e("RosterDownload", "解析失败：收到的字符串为空");
                return;
            }

            // 2. 打印原始数据的预览（截取前 200 个字符，防止刷屏卡顿）
            if (dataStr.length() > 200) {
                Log.i("RosterDownload", "原始数据预览: " + dataStr.substring(0, 200) + "...");
            } else {
                Log.i("RosterDownload", "原始数据完整内容: " + dataStr);
            }

            String[] rosterArray = dataStr.split("\\|");
            Log.i("RosterDownload", "按照 '|' 分隔符拆分出 " + rosterArray.length + " 个片段");

            List<RosterBean> rosterList = new ArrayList<>();

            for (String item : rosterArray) {
                if (TextUtils.isEmpty(item)) continue;
                String[] fields = item.split(",");
                if (fields.length >= 3) {
                    String cardNum = fields[0];
                    String studentId = fields[1];
                    String studentName = fields[2];
                    rosterList.add(new RosterBean(cardNum, studentId, studentName));
                } else {
                    Log.w("RosterDownload", "格式异常的行，已跳过: " + item);
                }
            }

            if (!rosterList.isEmpty()) {
                // 判空保护
                if (rosterDao == null) {
                    daoSession = MyApplication.getDaoSession();
                    rosterDao = daoSession.getRosterBeanDao();
                }

                if (rosterDao != null) {
                    // 3. 打印即将入库的前 5 条数据供核对
                    Log.i("RosterDownload", "=========== 解析成功，即将入库的前 5 条数据 ===========");
                    for (int i = 0; i < Math.min(5, rosterList.size()); i++) {
                        RosterBean b = rosterList.get(i);
                        Log.i("RosterDownload", "第" + (i+1) + "条 -> 卡号:" + b.getCardNum() + " | 学号:" + b.getStudentId() + " | 姓名:" + b.getStudentName());
                    }
                    Log.i("RosterDownload", "=======================================================");

                    // 清空旧名单并插入新名单
                    //rosterDao.deleteAll();
                    rosterDao.insertOrReplaceInTx(rosterList);

                    // 4. 打印实际存入数据库后的总条数
                    long totalCount = rosterDao.count();
                    Log.i("RosterDownload", "入库完毕！数据库 ROSTER_BEAN 表当前总记录数: " + totalCount);

                    Toast.makeText(this, "下载成功！共更新 " + rosterList.size() + " 名学生信息", Toast.LENGTH_LONG).show();
                } else {
                    Log.e("RosterDownload", "严重错误：rosterDao 为空！");
                    Toast.makeText(this, "严重错误：RosterDao 初始化失败", Toast.LENGTH_LONG).show();
                }
            } else {
                Log.w("RosterDownload", "解析完毕，但未提取到有效名单");
                Toast.makeText(this, "服务器返回的名单为空", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("RosterDownload", "解析名单发生异常: " + e.getMessage());
            Toast.makeText(this, "解析名单失败", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 上传未上传的测试数据
     */
    private void uploadData() {
        // ⭐⭐ 关键修复 2：筛选"未上传"的记录
        List<BeanData> unUploadList = new ArrayList<>();
        for (BeanData bean : dataList) {
            if (!"已上传".equals(bean.getStatus())) { // 只要不是"已上传"的，就上传
                unUploadList.add(bean);
            }
        }

        if (unUploadList.isEmpty()) {
            Toast.makeText(this, "没有需要上传的数据", Toast.LENGTH_SHORT).show();
            return;
        }

        // ⭐⭐ 关键修复 3：检查 socket 是否已连接
        if (dateConnect == null || !dateConnect.isConnected()) {
            Toast.makeText(this, "服务器未连接，无法上传", Toast.LENGTH_LONG).show();
            Log.e(TAG, "上传失败：dateConnect=" + dateConnect + ", isConnected=" + (dateConnect != null && dateConnect.isConnected()));
            return;
        }

        Toast.makeText(this, "开始上传 " + unUploadList.size() + " 条数据...", Toast.LENGTH_SHORT).show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                int successCount = 0;

                for (BeanData bean : unUploadList) {
                    int command = getCommandByProject(bean.getTestProject());
                    if (command == 0) {
                        Log.e("Upload", "未知测试项目：" + bean.getTestProject() + "，跳过");
                        continue;
                    }

                    int cardId = 0;
                    try {
                        cardId = Integer.parseInt(bean.getCardNum());
                    } catch (Exception e) {
                        Log.e("Upload", "卡号格式错误：" + bean.getCardNum() + "，跳过");
                        continue;
                    }

                    int result = 0;
                    try {
                        // 注意：根据你们协议决定是否需要 * 100
                        result = (int) Float.parseFloat(bean.getTestData());
                    } catch (Exception e) {
                        Log.e("Upload", "成绩格式错误：" + bean.getTestData() + "，跳过");
                        continue;
                    }

                    if (dateConnect != null) {
                        dateConnect.SendBuff(command, cardId, result);
                        successCount++;
                        try { Thread.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); }
                    }
                }

                final int finalCount = successCount;
                if (finalCount > 0) {
                    for (BeanData bean : unUploadList) {
                        bean.setStatus("已上传");
                    }
                    daoSession.getBeanDataDao().updateInTx(unUploadList);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(TestDataActivity.this, "数据上传成功！共上传 " + finalCount + " 条", Toast.LENGTH_LONG).show();
                            loadDataFromDB();
                        }
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(TestDataActivity.this, "没有成功发送任何数据，请检查日志", Toast.LENGTH_SHORT).show());
                }
            }
        }).start();
    }

    private int getCommandByProject(String project) {
        if (project == null) return 0;
        switch (project) {
            case "肺活量": return 1;
            case "身高": return 2;
            case "体重": return 3;
            case "坐位体前屈": return 4;
            case "仰卧起坐": return 5;
            case "立定跳远": return 6;
            case "引体向上": return 7;
            case "50米": return 8;
            case "800/1000米": return 9;
            case "跳绳": return 10;
            case "握力": return 11;
            case "台阶": return 12;
            case "单脚站立": return 13;
            case "俯卧撑": return 14;
            case "纵跳": return 15;
            case "反应时": return 16;
            case "篮球": return 17;
            case "足球": return 18;
            case "实心球": return 19;
            case "排球颠球": return 20;
            case "足球颠球": return 21;
            case "背力": return 22;
            case "踢毽子": return 23;
            case "三级跳": return 24;
            case "摸高": return 25;
            case "50米X8": return 58;
            case "BMI": return 70;
            default: return 0;
        }
    }


}