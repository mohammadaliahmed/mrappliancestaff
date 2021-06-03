package com.appsinventiv.mrappliancestaff.Activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appsinventiv.mrappliancestaff.Adapters.PaymentItemsAdapter;
import com.appsinventiv.mrappliancestaff.Models.CustomInvoiceModel;
import com.appsinventiv.mrappliancestaff.Models.InvoiceModel;
import com.appsinventiv.mrappliancestaff.Models.PaymentModel;
import com.appsinventiv.mrappliancestaff.Models.User;
import com.appsinventiv.mrappliancestaff.R;
import com.appsinventiv.mrappliancestaff.Utils.CommonUtils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;


public class PaymentsList extends AppCompatActivity {

    ImageView back;
    Button addPayment;
    DatabaseReference mDatabase;

    String invoiceId;
    private ArrayList<PaymentModel> itemList = new ArrayList<>();
    PaymentItemsAdapter adapter;
    RecyclerView recycler;
    private CustomInvoiceModel invoiceModel;
    TextView total, paid, balance;
    TextView invoiceIdTv;
    private String paymentMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        this.setTitle("Payments");
        invoiceId = getIntent().getStringExtra("invoiceId");
        total = findViewById(R.id.total);
        invoiceIdTv = findViewById(R.id.invoiceIdTv);
        invoiceIdTv.setText("Invoice: " + invoiceId);
        paid = findViewById(R.id.paid);
        balance = findViewById(R.id.balance);
        back = findViewById(R.id.back);
        recycler = findViewById(R.id.recycler);
        addPayment = findViewById(R.id.addPayment);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mDatabase = FirebaseDatabase.getInstance().getReference();
        addPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogNow();
            }
        });
        recycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        adapter = new PaymentItemsAdapter(this, itemList, new PaymentItemsAdapter.PaymentItemAdapterCallback() {
            @Override
            public void onDelete(PaymentModel model) {
                showDeleteAlert(model);
            }
        });
        recycler.setAdapter(adapter);
        getPaymentsFromDb();
    }

    private void showDeleteAlert(final PaymentModel model) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert");
        builder.setMessage("Do you want to delete this payment? ");

        // add the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                mDatabase.child("Invoices").child(invoiceId).child("payments").child(model.getId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        CommonUtils.showToast("Removed");
                    }
                });
            }
        });
        builder.setNegativeButton("Cancel", null);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showDialogNow() {
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout = layoutInflater.inflate(R.layout.alert_payment, null);
        dialog.setContentView(layout);

        final TextView pickDate = layout.findViewById(R.id.pickDate);
        Button addPayment = layout.findViewById(R.id.addPayment);
        final Spinner spinner = layout.findViewById(R.id.spinner);
        final EditText amount = layout.findViewById(R.id.amount);
        final Calendar c = Calendar.getInstance();
        final int mYear = c.get(Calendar.YEAR);
        final int mMonth = c.get(Calendar.MONTH);
        final int mDay = c.get(Calendar.DAY_OF_MONTH);
        final String[] dateSelected = {""};
        pickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(PaymentsList.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                dateSelected[0] = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                pickDate.setText(dateSelected[0]);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }

        });
        addPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dateSelected[0].equalsIgnoreCase("")) {
                    CommonUtils.showToast("Please select Date");
                } else if (amount.getText().length() == 0) {
                    amount.setError("Enter Amount");
                } else if (paymentMode.equalsIgnoreCase("Select Payment method")) {
                   CommonUtils.showToast("Please select payment method");
                } else {
                    String key = mDatabase.push().getKey();
                    PaymentModel model = new PaymentModel(key, Integer.parseInt(amount.getText().toString()), dateSelected[0], paymentMode);
                    mDatabase.child("Invoices").child(invoiceId).child("payments").child(key).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            CommonUtils.showToast("Added");
                            dialog.cancel();
                        }
                    });
                }
            }
        });
        final ArrayList<String> items = new ArrayList<>();

        items.add("Select Payment method");
        items.add("Cash");
        items.add("Bank");
        items.add("Credit Card");
        items.add("Online");
        items.add("Other");


        final ArrayAdapter<String> adaptera = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        spinner.setAdapter(adaptera);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


                paymentMode = items.get(i);


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        dialog.show();
    }

    private void getPaymentsFromDb() {
        mDatabase.child("Invoices").child(invoiceId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    itemList.clear();
                    invoiceModel = dataSnapshot.getValue(CustomInvoiceModel.class);

                    for (DataSnapshot snapshot : dataSnapshot.child("payments").getChildren()) {
                        PaymentModel model = snapshot.getValue(PaymentModel.class);
                        if (model != null) {
                            itemList.add(model);
                        }
                    }
                    adapter.setItemList(itemList);
                    setupdata();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setupdata() {
        total.setText("AED " + invoiceModel.getTotal());
        calcualteNow();
    }

    private void calcualteNow() {
        int totalPaid = 0;
        for (PaymentModel model : itemList) {
            totalPaid = totalPaid + model.getPrice();
        }
        paid.setText("AED " + totalPaid);
        balance.setText("AED " + (invoiceModel.getTotal() - totalPaid));


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {


            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
