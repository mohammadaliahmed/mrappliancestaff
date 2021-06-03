package com.appsinventiv.mrappliancestaff.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appsinventiv.mrappliancestaff.Models.InvoiceItemModel;
import com.appsinventiv.mrappliancestaff.Models.PaymentModel;
import com.appsinventiv.mrappliancestaff.R;

import java.util.ArrayList;

public class PaymentItemsAdapter extends RecyclerView.Adapter<PaymentItemsAdapter.ViewHolder> {
    Context context;
    ArrayList<PaymentModel> itemList;
    PaymentItemAdapterCallback callback;

    public PaymentItemsAdapter(Context context, ArrayList<PaymentModel> itemList, PaymentItemAdapterCallback callback) {
        this.context = context;
        this.itemList = itemList;
        this.callback = callback;
    }

    public void setItemList(ArrayList<PaymentModel> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.payments_item_layout, parent, false);
        PaymentItemsAdapter.ViewHolder viewHolder = new PaymentItemsAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final PaymentModel model = itemList.get(position);
        holder.date.setText(model.getDate() + " (" + model.getPaymentMode() + ")");
        holder.amount.setText("AED " + model.getPrice());
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
        TextView date, amount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            amount = itemView.findViewById(R.id.amount);
            date = itemView.findViewById(R.id.date);
        }
    }

    public interface PaymentItemAdapterCallback {
        public void onDelete(PaymentModel model);
    }
}
