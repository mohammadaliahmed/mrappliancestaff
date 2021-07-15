package com.appsinventiv.mrappliancestaff.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appsinventiv.mrappliancestaff.Activities.Invoices.InvoicesActivity;
import com.appsinventiv.mrappliancestaff.Activities.Jobs.AppointmentModel;
import com.appsinventiv.mrappliancestaff.Activities.Jobs.NewAppointment;
import com.appsinventiv.mrappliancestaff.Models.CustomInvoiceModel;
import com.appsinventiv.mrappliancestaff.R;
import com.appsinventiv.mrappliancestaff.Utils.CommonUtils;

import java.util.ArrayList;

public class AppointmentListAdapter extends RecyclerView.Adapter<AppointmentListAdapter.ViewHolder> {
    Context context;
    ArrayList<AppointmentModel> itemList;

    public AppointmentListAdapter(Context context, ArrayList<AppointmentModel> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    public void setItemList(ArrayList<AppointmentModel> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.appoitnment_item_layout, parent, false);
        AppointmentListAdapter.ViewHolder viewHolder = new AppointmentListAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final AppointmentModel model = itemList.get(position);
        holder.details.setText((position + 1) + ") " + "Customer: " + model.getCustomerName() + " " + model.getPhone()
                + "\n     Job: " + model.getTitle() + " " + model.getModel()
                + "\n     Date Created: " + CommonUtils.getFormattedDate(model.getTime()) +
                "\n     Due Date: " + model.getTimeSelected() + " " + model.getDate());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, NewAppointment.class);
                i.putExtra("appointmentId", model.getId());
                context.startActivity(i);
            }
        });
        holder.status.setText(model.getAppointmentStatus());


    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView details, status, dueDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            details = itemView.findViewById(R.id.details);
            status = itemView.findViewById(R.id.status);
        }
    }

}
