package com.appsinventiv.mrappliancestaff.Activities.Expenses;

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

import com.appsinventiv.mrappliancestaff.Activities.Login;
import com.appsinventiv.mrappliancestaff.Activities.PaymentsList;
import com.appsinventiv.mrappliancestaff.Adapters.ExpensesListAdapter;
import com.appsinventiv.mrappliancestaff.Models.ExpensesModel;
import com.appsinventiv.mrappliancestaff.R;
import com.appsinventiv.mrappliancestaff.Utils.CommonUtils;
import com.appsinventiv.mrappliancestaff.Utils.SharedPrefs;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;


public class ExpnesesList extends AppCompatActivity {
    RecyclerView recycler;
    ImageView addExpense;
    DatabaseReference mDatabase;
    ExpensesListAdapter adapter;

    ImageView back;
    private ArrayList<ExpensesModel> itemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses_list);
        addExpense = findViewById(R.id.addExpense);
        back = findViewById(R.id.back);
        recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        adapter = new ExpensesListAdapter(this, itemList, new ExpensesListAdapter.ExpensesItemAdapterCallback() {
            @Override
            public void onDelete(ExpensesModel model) {
                showDeleteAlert(model);
            }
        });
        recycler.setAdapter(adapter);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        mDatabase = FirebaseDatabase.getInstance().getReference();
        addExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shwoExpenseAlert();
            }
        });
        getDataFromServer();
    }

    private void getDataFromServer() {
        mDatabase.child("Expenses").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    itemList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ExpensesModel model = snapshot.getValue(ExpensesModel.class);
                        if (model != null) {
                            if(model.getStaffMember().equalsIgnoreCase(SharedPrefs.getUser().getUsername())) {
                                itemList.add(model);
                            }
                        }
                    }
                    adapter.setItemList(itemList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showDeleteAlert(final ExpensesModel model) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert");
        builder.setMessage("Do you want to delete this expense? ");

        // add the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mDatabase.child("Expenses").child(model.getId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        CommonUtils.showToast("Deletedd");
                    }
                });

            }
        });
        builder.setNegativeButton("Cancel", null);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void shwoExpenseAlert() {
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout = layoutInflater.inflate(R.layout.alert_dialog_add_expenses, null);

        dialog.setContentView(layout);

        Button saveExpense = layout.findViewById(R.id.saveExpense);
        final EditText title = layout.findViewById(R.id.title);
        final EditText description = layout.findViewById(R.id.description);
        final EditText amount = layout.findViewById(R.id.amount);
        final TextView pickDate = layout.findViewById(R.id.pickDate);
        Spinner spinner = layout.findViewById(R.id.spinner);
        final String[] categorySelected = {""};
        pickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        final Calendar c = Calendar.getInstance();
        final int mYear = c.get(Calendar.YEAR);
        final int mMonth = c.get(Calendar.MONTH);
        final int mDay = c.get(Calendar.DAY_OF_MONTH);
        final String[] dateSelected = {""};
        pickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(ExpnesesList.this,
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

        final ArrayList<String> items = new ArrayList<>();

        items.add("Select Category");
        items.add("Food");
        items.add("Fuel");
        items.add("Traveling");
        items.add("Stationary");
        items.add("Bill");


        final ArrayAdapter<String> adaptera = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        spinner.setAdapter(adaptera);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                categorySelected[0] = items.get(i);


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        saveExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (title.getText().length() == 0) {
                    title.setError("Enter title");
                } else if (description.getText().length() == 0) {
                    description.setError("Enter description");
                } else if (amount.getText().length() == 0) {
                    amount.setError("Enter title");
                } else if (dateSelected[0].equalsIgnoreCase("")) {
                    CommonUtils.showToast("Select date");
                } else if (categorySelected[0].equalsIgnoreCase("Select Category")) {
                    CommonUtils.showToast("Select category");
                } else {
                    String key = mDatabase.push().getKey();
                    ExpensesModel model = new ExpensesModel(key,
                            title.getText().toString(),
                            description.getText().toString(),
                            categorySelected[0],
                            dateSelected[0],
                            "Pending",
                            SharedPrefs.getUser().getUsername(),
                            Integer.parseInt(amount.getText().toString())
                    );
                    mDatabase.child("Expenses").child(key).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            CommonUtils.showToast("Expense added");
                            dialog.cancel();
                        }
                    });
                }
            }
        });


        dialog.show();
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
