package com.appsinventiv.mrappliancestaff.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appsinventiv.mrappliancestaff.Models.ExpensesModel;
import com.appsinventiv.mrappliancestaff.Models.InvoiceItemModel;
import com.appsinventiv.mrappliancestaff.R;

import java.util.ArrayList;

public class ExpensesListAdapter extends RecyclerView.Adapter<ExpensesListAdapter.ViewHolder> {
    Context context;
    ArrayList<ExpensesModel> itemList;
    ExpensesItemAdapterCallback callback;

    public ExpensesListAdapter(Context context, ArrayList<ExpensesModel> itemList, ExpensesItemAdapterCallback callback) {
        this.context = context;
        this.itemList = itemList;
        this.callback = callback;
    }

    public void setItemList(ArrayList<ExpensesModel> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.expense_item_layout, parent, false);
        ExpensesListAdapter.ViewHolder viewHolder = new ExpensesListAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final ExpensesModel model = itemList.get(position);
        holder.serial.setText((position + 1) + ")");
        holder.title.setText(model.getTitle() + "\n" + model.getCategory() + "\n" + model.getDescription());
        holder.status.setText(model.getStatus());
        holder.amount.setText("AED " + model.getPrice());
        holder.date.setText(model.getDate());

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
        TextView title, status, amount, date, serial;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            amount = itemView.findViewById(R.id.amount);
            serial = itemView.findViewById(R.id.serial);
            status = itemView.findViewById(R.id.status);
            title = itemView.findViewById(R.id.title);

        }
    }

    public interface ExpensesItemAdapterCallback {
        public void onDelete(ExpensesModel model);
    }
}
