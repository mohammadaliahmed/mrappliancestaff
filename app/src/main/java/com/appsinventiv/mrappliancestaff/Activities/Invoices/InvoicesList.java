package com.appsinventiv.mrappliancestaff.Activities.Invoices;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.appsinventiv.mrappliancestaff.Adapters.InvoiceListAdapter;
import com.appsinventiv.mrappliancestaff.Models.CustomInvoiceModel;
import com.appsinventiv.mrappliancestaff.R;
import com.appsinventiv.mrappliancestaff.Utils.CommonUtils;
import com.appsinventiv.mrappliancestaff.Utils.SharedPrefs;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class InvoicesList extends AppCompatActivity {

    RecyclerView recycler;
    InvoiceListAdapter adapter;
    private ArrayList<CustomInvoiceModel> itemList = new ArrayList<>();
    DatabaseReference mDatabase;

    ImageView createInvoice;
    long invoiceId = 0;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoices_list);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        this.setTitle("Invoices List");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        back = findViewById(R.id.back);
        createInvoice = findViewById(R.id.createInvoice);
        recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        adapter = new InvoiceListAdapter(this, itemList, new InvoiceListAdapter.InvoicesCallback() {
            @Override
            public void onApproved(CustomInvoiceModel model) {
                showApproveRejectAlert(model, "approved");
            }

            @Override
            public void onReject(CustomInvoiceModel model) {
                showApproveRejectAlert(model, "rejected");
            }
        });
        recycler.setAdapter(adapter);
        createInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(InvoicesList.this, InvoicesActivity.class);
                i.putExtra("invoiceId", "" + (invoiceId + 1));
                startActivity(i);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        getDataFromServer();

    }

    private void showApproveRejectAlert(CustomInvoiceModel model, String approved) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert");
        builder.setMessage("Did customer " + approved + " this invoice ? ");

        // add the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                mDatabase.child("Invoices").child(model.getInvoiceId()).child("customerStatus").setValue(approved).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        CommonUtils.showToast(approved);
                        dialogInterface.cancel();
                    }
                });

            }
        });
        builder.setNegativeButton("Cancel", null);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void getDataFromServer() {
        itemList.clear();
        mDatabase.child("Invoices").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    itemList.clear();
                    invoiceId = dataSnapshot.getChildrenCount();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        CustomInvoiceModel model = snapshot.getValue(CustomInvoiceModel.class);
                        if (model.getStaffMember().equalsIgnoreCase(SharedPrefs.getUser().getUsername())) {
                            if (model != null) {
                                itemList.add(model);
                            }
                        }
                    }
                    Collections.sort(itemList, new Comparator<CustomInvoiceModel>() {
                        @Override
                        public int compare(CustomInvoiceModel listData, CustomInvoiceModel t1) {
                            Long ob1 = listData.getTime();
                            Long ob2 = t1.getTime();
                            return ob2.compareTo(ob1);

                        }
                    });
                    adapter.setItemList(itemList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
