package com.appsinventiv.mrappliancestaff.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appsinventiv.mrappliancestaff.Activities.Invoices.InvoicesActivity;
import com.appsinventiv.mrappliancestaff.Models.CustomInvoiceModel;
import com.appsinventiv.mrappliancestaff.Models.InvoiceItemModel;
import com.appsinventiv.mrappliancestaff.R;
import com.appsinventiv.mrappliancestaff.Utils.CommonUtils;

import java.util.ArrayList;

public class InvoiceListAdapter extends RecyclerView.Adapter<InvoiceListAdapter.ViewHolder> {
    Context context;
    ArrayList<CustomInvoiceModel> itemList;
    InvoicesCallback callback;

    public InvoiceListAdapter(Context context, ArrayList<CustomInvoiceModel> itemList, InvoicesCallback callback) {
        this.context = context;
        this.callback = callback;
        this.itemList = itemList;
    }

    public void setItemList(ArrayList<CustomInvoiceModel> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_invoice_item_layout, parent, false);
        InvoiceListAdapter.ViewHolder viewHolder = new InvoiceListAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final CustomInvoiceModel model = itemList.get(position);
        holder.name.setText(model.getUser().getFirstname() + " " + model.getUser().getLastname()
                + "\nInvoice: " + model.getInvoiceId()+"\nCustomer approval : "+model.getCustomerStatus());
        holder.date.setText(CommonUtils.getFormattedDate(model.getTime()) + "\nStatus: " + model.getStatus());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, InvoicesActivity.class);
                i.putExtra("invoiceId", model.getInvoiceId());
                context.startActivity(i);
            }
        });
        holder.approved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onApproved(model);
            }
        });
        holder.rejected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onReject(model);
            }
        });


    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, date;
        Button rejected, approved;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            date = itemView.findViewById(R.id.date);
            rejected = itemView.findViewById(R.id.rejected);
            approved = itemView.findViewById(R.id.approved);
        }
    }

    public interface InvoicesCallback {
        public void onApproved(CustomInvoiceModel model);

        public void onReject(CustomInvoiceModel model);
    }

}
