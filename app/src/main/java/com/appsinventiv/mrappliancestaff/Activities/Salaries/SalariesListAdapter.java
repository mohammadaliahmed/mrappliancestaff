package com.appsinventiv.mrappliancestaff.Activities.Salaries;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.appsinventiv.mrappliancestaff.Models.SalaryModel;
import com.appsinventiv.mrappliancestaff.R;

import java.util.ArrayList;

public class SalariesListAdapter extends RecyclerView.Adapter<SalariesListAdapter.ViewHolder> {
    Context context;
    ArrayList<SalaryModel> itemList;
    SalaryItemAdapterCallback callback;

    public SalariesListAdapter(Context context, ArrayList<SalaryModel> itemList, SalaryItemAdapterCallback callback) {
        this.context = context;
        this.itemList = itemList;
        this.callback = callback;
    }

    public void setItemList(ArrayList<SalaryModel> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.salary_item_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final SalaryModel model = itemList.get(position);
        holder.serial.setText((position + 1) + ")");
        holder.name.setText("Name: " + model.getServiceman().getName());
        holder.salaryStatus.setText("" + model.getStatus());
        holder.total.setText("   Total Salary: AED " + model.getTotal());
        holder.date.setText(model.getDay() + "/" + model.getMonth() + "/" + model.getYear());


        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                callback.onDelete(model);
                return false;

            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(context, ViewSalary.class);
                i.putExtra("salaryId",model.getId());
                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, amount, date, serial, deduction, total,salaryStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            date = itemView.findViewById(R.id.date);
            salaryStatus = itemView.findViewById(R.id.salaryStatus);
            serial = itemView.findViewById(R.id.serial);
            total = itemView.findViewById(R.id.total);


        }
    }

    public interface SalaryItemAdapterCallback {
        public void onDelete(SalaryModel model);


    }
}
