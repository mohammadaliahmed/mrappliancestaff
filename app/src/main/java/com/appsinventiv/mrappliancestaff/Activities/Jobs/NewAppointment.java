package com.appsinventiv.mrappliancestaff.Activities.Jobs;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.appsinventiv.mrappliancestaff.Activities.Invoices.EditInvoice;
import com.appsinventiv.mrappliancestaff.Activities.Invoices.InvoicesActivity;
import com.appsinventiv.mrappliancestaff.Activities.Invoices.InvoicesList;
import com.appsinventiv.mrappliancestaff.Activities.Login;
import com.appsinventiv.mrappliancestaff.Activities.PaymentsList;
import com.appsinventiv.mrappliancestaff.Models.User;
import com.appsinventiv.mrappliancestaff.R;
import com.appsinventiv.mrappliancestaff.Utils.CommonUtils;
import com.appsinventiv.mrappliancestaff.Utils.SharedPrefs;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.Executor;


public class NewAppointment extends AppCompatActivity {

    TextView location, title, brand, model, status, report, payment, paymentType, customerName, phone, address;
    Button save, finishAppointment;
    ImageView back;
    DatabaseReference mDatabase;
    public static String id;
    private AppointmentModel appointmentModel;
    private long time;
    TextView pickDate, pickTime;
    private String pickedDate;
    private String timeSelected;
    Button newInvoice;
    public static User newAppointmentUsr;
    private long invoiceId;
    RadioButton pending, recomplaint, diagnostic, deliveries;
    private String appointmentStatus = "Pending";
    Button addWorkHistory;
    TextView currentStatus;
    RadioGroup statusGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_appointment);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        id = getIntent().getStringExtra("appointmentId");
        pending = findViewById(R.id.pending);
        recomplaint = findViewById(R.id.recomplaint);
        statusGroup = findViewById(R.id.statusGroup);
        diagnostic = findViewById(R.id.diagnostic);
        location = findViewById(R.id.location);
        brand = findViewById(R.id.brand);
        newInvoice = findViewById(R.id.newInvoice);
        deliveries = findViewById(R.id.deliveries);
        model = findViewById(R.id.model);
        address = findViewById(R.id.address);
        title = findViewById(R.id.title);
        status = findViewById(R.id.status);
        pickDate = findViewById(R.id.pickDate);
        finishAppointment = findViewById(R.id.finishAppointment);
        pickTime = findViewById(R.id.pickTime);
        addWorkHistory = findViewById(R.id.addWorkHistory);
        currentStatus = findViewById(R.id.currentStatus);
        report = findViewById(R.id.report);
        payment = findViewById(R.id.payment);
        paymentType = findViewById(R.id.paymentType);
        customerName = findViewById(R.id.customerName);
        phone = findViewById(R.id.phone);

        save = findViewById(R.id.save);
        back = findViewById(R.id.back);

        pickedDate = getIntent().getStringExtra("pickedDate");
        timeSelected = getIntent().getStringExtra("timeSelected");
        if (pickedDate != null) {
            pickTime.setText("Time: " + timeSelected);
            pickDate.setText("Date: " + pickedDate);
        }
        finishAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFinishALert();

            }
        });

        addWorkHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(NewAppointment.this, WorkHistory.class);
                i.putExtra("appointmentId", id);
                startActivity(i);
            }
        });


        pending.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    appointmentStatus = "Pending";
                }
            }
        });
        recomplaint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    appointmentStatus = "ReComplaint";
                }
            }
        });
        deliveries.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    appointmentStatus = "Deliveries";
                }
            }
        });
        diagnostic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    appointmentStatus = "Diagnostic";
                }
            }
        });

        newInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (customerName.getText().length() < 5) {
                    customerName.setError("Enter full name");
                } else if (phone.getText().length() < 5) {
                    phone.setError("Enter phone");
                } else if (address.getText().length() < 5) {
                    address.setError("Enter address");
                } else {
                    if (appointmentModel.getInvoiceId() != null) {
                        Intent i = new Intent(NewAppointment.this, InvoicesActivity.class);
                        i.putExtra("invoiceId", appointmentModel.getInvoiceId());
                        startActivity(i);
                    } else {
                        String[] fullname = customerName.getText().toString().split(" ");
                        try {
                            newAppointmentUsr = new User(

                                    customerName.getText().toString().split(" ")[0],
                                    customerName.getText().toString().split(" ")[1],
                                    (customerName.getText().toString().replace(" ", "")).toLowerCase(),
                                    (customerName.getText().toString().replace(" ", "")).toLowerCase(),
                                    customerName.getText().toString().replace(" ", "") + "@gmail.com",
                                    phone.getText().toString(),
                                    phone.getText().toString(),
                                    address.getText().toString(),
                                    "",
                                    System.currentTimeMillis(),
                                    true
                            );

                            Intent i = new Intent(NewAppointment.this, InvoicesActivity.class);
                            i.putExtra("invoiceId", "" + (invoiceId + 1));
                            startActivity(i);


                        } catch (Exception e) {
                            CommonUtils.showToast("Please enter full name");
                            customerName.requestFocus();
                            customerName.setError("Enter full name");
                        }
                    }
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        pickTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(NewAppointment.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                        timeSelected = selectedHour + ":" + selectedMinute;
                        pickTime.setText("Time: " + timeSelected);
                    }
                }, hour, minute, true);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });
        pickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                final int mYear = c.get(Calendar.YEAR);
                final int mMonth = c.get(Calendar.MONTH);
                final int mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(NewAppointment.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                pickedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                pickDate.setText("Date: " + pickedDate);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();


            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> map = new HashMap<>();

                map.put("time", time);
                map.put("id", id);
                map.put("brand", brand.getText().toString());
                map.put("model", model.getText().toString());
                map.put("location", location.getText().toString());
                map.put("status", status.getText().toString());
                map.put("report", report.getText().toString());
                map.put("title", title.getText().toString());
                map.put("date", pickedDate);
                map.put("timeSelected", timeSelected);
                map.put("payment", payment.getText().toString());
                map.put("paymentType", paymentType.getText().toString());
                map.put("customerName", customerName.getText().toString());
                map.put("phone", phone.getText().toString());
                map.put("address", address.getText().toString());
                map.put("appointmentStatus", appointmentStatus);
                map.put("staff", SharedPrefs.getUser().getUsername());
                mDatabase.child("Appointments").child(id).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        CommonUtils.showToast("Saved");

                    }
                });
            }
        });

        if (id != null) {

            getDataFromServer();
        } else {
            id = mDatabase.push().getKey();
            time = System.currentTimeMillis();
        }


    }

    private void showFinishALert() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert");
        builder.setMessage("Do you want to mark appointment as finished? ");

        // add the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                appointmentStatus = "Finished";
                mDatabase.child("Appointments").child(id).child("appointmentStatus").setValue(appointmentStatus).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        CommonUtils.showToast("Completed");
                        finish();
                    }
                });

            }
        });
        builder.setNegativeButton("Cancel", null);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void getInvoicesFromDB() {
        mDatabase.child("Invoices").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {

                    invoiceId = dataSnapshot.getChildrenCount();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getDataFromServer() {
        mDatabase.child("Appointments").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    appointmentModel = dataSnapshot.getValue(AppointmentModel.class);
                    location.setText(appointmentModel.getLocation());
                    brand.setText(appointmentModel.getBrand());
                    model.setText(appointmentModel.getModel());
                    pickDate.setText("Date: " + appointmentModel.getDate());
                    pickTime.setText("Time: " + appointmentModel.getTimeSelected());
                    status.setText(appointmentModel.getStatus());
                    report.setText(appointmentModel.getReport());
                    payment.setText(appointmentModel.getPayment());
                    paymentType.setText(appointmentModel.getPaymentType());
                    title.setText(appointmentModel.getTitle());
                    address.setText(appointmentModel.getAddress());
                    phone.setText(appointmentModel.getPhone());
                    currentStatus.setText("Current Appointment Status: " + appointmentModel.getAppointmentStatus());

                    customerName.setText(appointmentModel.getCustomerName());

                    if (appointmentModel.getAppointmentStatus() != null) {
                        appointmentStatus = appointmentModel.getAppointmentStatus();
                        if (appointmentModel.getAppointmentStatus().equalsIgnoreCase("pending")) {
                            pending.setChecked(true);
                        } else if (appointmentModel.getAppointmentStatus().equalsIgnoreCase("recomplaint")) {
                            recomplaint.setChecked(true);
                        } else if (appointmentModel.getAppointmentStatus().equalsIgnoreCase("diagnostic")) {
                            diagnostic.setChecked(true);
                        }

                    }
                    timeSelected = appointmentModel.getTimeSelected();
                    pickedDate = appointmentModel.getDate();
                    time = appointmentModel.getTime();
                    newInvoice.setVisibility(View.VISIBLE);
                    addWorkHistory.setVisibility(View.VISIBLE);
                    finishAppointment.setVisibility(View.VISIBLE);
                    save.setText("Update Appointment");
                    getInvoicesFromDB();
                    if (appointmentModel.getAppointmentStatus().equalsIgnoreCase("finished")) {
                        finishAppointment.setVisibility(View.GONE);
                        save.setVisibility(View.GONE);
                        statusGroup.setVisibility(View.GONE);
                        addWorkHistory.setVisibility(View.GONE);
                    }
                    if (appointmentModel.getInvoiceId() != null) {
                        newInvoice.setText("View Invoice #: " + appointmentModel.getInvoiceId());
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
