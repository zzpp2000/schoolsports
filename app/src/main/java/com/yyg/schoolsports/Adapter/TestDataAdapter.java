package com.yyg.schoolsports.Adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yyg.schoolsports.Bean.BeanData;
import com.yyg.schoolsports.R;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TestDataAdapter extends RecyclerView.Adapter<TestDataAdapter.ViewHolder> {

    private List<BeanData> dataList;

    // 核心：用这个集合记录所有被选中记录的 ID（Long 类型，因为你的主键是 Long id）
    private Set<Long> selectedIds = new HashSet<>();

    public TestDataAdapter(List<BeanData> dataList) {
        this.dataList = dataList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCardNum, tvName, tvProject, tvData, tvTime, tvStatus;

        public ViewHolder(View itemView) {
            super(itemView);
            tvCardNum = itemView.findViewById(R.id.tv_card_num);
            tvName = itemView.findViewById(R.id.tv_name);
            tvProject = itemView.findViewById(R.id.tv_project);
            tvData = itemView.findViewById(R.id.tv_data);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvStatus = itemView.findViewById(R.id.tv_status);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_test_data, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BeanData bean = dataList.get(position);

        // 1. 绑定常规数据
        holder.tvCardNum.setText(bean.getCardNum() != null ? bean.getCardNum() : "--");
        holder.tvName.setText(bean.getStudentName() != null ? bean.getStudentName() : "--");
        holder.tvProject.setText(bean.getTestProject() != null ? bean.getTestProject() : "--");
        holder.tvData.setText(bean.getTestData() != null ? bean.getTestData() : "--");

        String timeStr = bean.getDate();
        if (timeStr != null && timeStr.contains(" ")) {
            String[] parts = timeStr.split(" ");
            holder.tvTime.setText(parts[0] + "\n" + parts[1]);
        } else {
            holder.tvTime.setText(timeStr != null ? timeStr : "--");
        }
        holder.tvStatus.setText(bean.getStatus() != null ? bean.getStatus() : "未上传");

        // 2. 核心：根据是否选中，设置背景颜色
        Long id = bean.getId();
        if (id != null && selectedIds.contains(id)) {
            // 选中：浅蓝色高亮（类似截图中的蓝色）
            holder.itemView.setBackgroundColor(Color.parseColor("#90CAF9"));
        } else {
            // 未选中：白色
            holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }

        // 3. 核心：设置点击事件，切换选中状态
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (id == null) return; // 防止空指针

                if (selectedIds.contains(id)) {
                    // 已选中，取消选中
                    selectedIds.remove(id);
                } else {
                    // 未选中，添加选中
                    selectedIds.add(id);
                }

                // 局部刷新当前条目，立刻更新背景色
                notifyItemChanged(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

    // ================= 对外提供的方法（下一步“删除选中记录”会用到） =================

    /**
     * 获取所有被选中的记录 ID
     */
    public Set<Long> getSelectedIds() {
        return selectedIds;
    }

    /**
     * 清空所有选中状态（例如删除成功后调用）
     */
    public void clearSelection() {
        selectedIds.clear();
        notifyDataSetChanged();
    }
}