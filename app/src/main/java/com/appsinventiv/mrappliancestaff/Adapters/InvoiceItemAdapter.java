package com.appsinventiv.mrappliancestaff.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appsinventiv.mrappliancestaff.Activities.PicturesSlider;
import com.appsinventiv.mrappliancestaff.Models.InvoiceItemModel;
import com.appsinventiv.mrappliancestaff.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class InvoiceItemAdapter extends RecyclerView.Adapter<InvoiceItemAdapter.ViewHolder> {
    Context context;
    ArrayList<InvoiceItemModel> itemList;
    InvoiceItemAdapterCallback callback;
    boolean value;

    public InvoiceItemAdapter(Context context, ArrayList<InvoiceItemModel> itemList, InvoiceItemAdapterCallback callback) {
        this.context = context;
        this.itemList = itemList;
        this.callback = callback;
    }

    public void setItemList(ArrayList<InvoiceItemModel> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.invoice_item_layout, parent, false);
        InvoiceItemAdapter.ViewHolder viewHolder = new InvoiceItemAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final InvoiceItemModel model = itemList.get(position);
        holder.name.setText((position + 1) + ") " + model.getDescription());
        int total = 0;
        total = total + (model.getQuantity() * model.getPrice());
        holder.costing.setText(model.getQuantity() + " x AED " + model.getPrice() + "\nAED " + total);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                callback.onDelete(model);
                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, costing;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            costing = itemView.findViewById(R.id.costing);
        }
    }

    public interface InvoiceItemAdapterCallback {
        public void onDelete(InvoiceItemModel model);
    }
}
