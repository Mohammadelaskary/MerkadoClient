package com.merkado.merkadoclient.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.merkado.merkadoclient.Model.DepartmentNames;
import com.merkado.merkadoclient.R;

import java.util.List;

public class DepartmentNamesAdapter extends RecyclerView.Adapter<DepartmentNamesAdapter.DepartmentNamesViewHolder> {
    Context context;
    List<DepartmentNames> departmentNames;

    public DepartmentNamesAdapter(Context context, List<DepartmentNames> departmentNames) {
        this.context = context;
        this.departmentNames = departmentNames;
    }

    @NonNull
    @Override
    public DepartmentNamesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.department_name_item, parent, false);
        return new DepartmentNamesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DepartmentNamesViewHolder holder, int position) {
        String departmentName = departmentNames.get(position).getDepName();
        holder.depName.setText(departmentName);
    }

    @Override
    public int getItemCount() {
        return departmentNames.size();
    }

    static class DepartmentNamesViewHolder extends RecyclerView.ViewHolder {
        TextView depName;

        public DepartmentNamesViewHolder(@NonNull View itemView) {
            super(itemView);
            depName = itemView.findViewById(R.id.dep_name);
        }
    }
}
