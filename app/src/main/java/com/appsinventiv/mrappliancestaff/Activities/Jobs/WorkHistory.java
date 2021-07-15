package com.appsinventiv.mrappliancestaff.Activities.Jobs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.solver.ArrayLinkedVariables;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appsinventiv.mrappliancestaff.Activities.Expenses.ExpnesesList;
import com.appsinventiv.mrappliancestaff.Activities.Login;
import com.appsinventiv.mrappliancestaff.Adapters.AppointmentLogsAdapter;
import com.appsinventiv.mrappliancestaff.Models.LogsModel;
import com.appsinventiv.mrappliancestaff.Models.ServicemanModel;
import com.appsinventiv.mrappliancestaff.R;
import com.appsinventiv.mrappliancestaff.Utils.CommonUtils;
import com.appsinventiv.mrappliancestaff.Utils.CompressImage;
import com.appsinventiv.mrappliancestaff.Utils.SharedPrefs;
import com.bumptech.glide.Glide;
import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;


public class WorkHistory extends AppCompatActivity {

    Button pickDateAndTime;
    ImageView back;
    private String dateSelected;
    private String timeSelected;
    TextView timeee;
    Button saveRecord;
    String appointmentId;
    EditText logs;
    DatabaseReference mDatabase;
    private String assignToStaffName = "";
    RecyclerView recycler;
    private ArrayList<LogsModel> itemList = new ArrayList<>();
    AppointmentLogsAdapter adapter;
    ArrayList<ServicemanModel> servicemenList = new ArrayList<ServicemanModel>();
    private Spinner spinner;
    private String serviceMenSelected;
    private int positionSelected;
    ImageView pickImage;
    private String imageUrl;
    StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_history);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        appointmentId = getIntent().getStringExtra("appointmentId");
        timeee = findViewById(R.id.timeee);
        pickDateAndTime = findViewById(R.id.pickDateAndTime);
        spinner = findViewById(R.id.spinner);
        pickImage = findViewById(R.id.pickImage);
        logs = findViewById(R.id.logs);
        saveRecord = findViewById(R.id.saveRecord);
        recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        adapter = new AppointmentLogsAdapter(this, itemList);
        recycler.setAdapter(adapter);

        getDataFromServer();
        getServicemenFromDB();
        pickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Options options = Options.init()
                        .setRequestCode(100)                                           //Request code for activity results
                        .setCount(1)                                                   //Number of images to restict selection count
                        .setExcludeVideos(true)                                       //Option to exclude videos
                        .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)     //Orientaion
                        ;                                       //Custom Path For media Storage

                Pix.start(WorkHistory.this, options);
            }
        });

        saveRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (logs.getText().length() < 5) {
                    logs.setError("Enter logs");
                } else {
                    if (timeSelected != null) {
                        if (imageUrl != null) {
                            putPictures(imageUrl);
                        } else {
                            saveDataToDb();
                        }
                    } else {
                        CommonUtils.showToast("Please select date and time");
                    }
                }
            }
        });

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        final Calendar c = Calendar.getInstance();
        final int mYear = c.get(Calendar.YEAR);
        final int mMonth = c.get(Calendar.MONTH);
        final int mDay = c.get(Calendar.DAY_OF_MONTH);
        pickDateAndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(WorkHistory.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                dateSelected = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                showTimePicker();

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });


    }

    public void putPictures(String path) {
        CommonUtils.showToast("Image Uploading");
        String imgName = Long.toHexString(Double.doubleToLongBits(Math.random()));


        Uri file = Uri.fromFile(new File(path));

        mStorageRef = FirebaseStorage.getInstance().getReference();

        StorageReference riversRef = mStorageRef.child("Photos").child(imgName);

        riversRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    @SuppressWarnings("VisibleForTests")
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!urlTask.isSuccessful()) ;
                        Uri downloadUrl = urlTask.getResult();
                        imageUrl = "" + downloadUrl;
                        CommonUtils.showToast("Image Uploaded");
                        saveDataToDb();


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                    }
                });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 100) {
            ArrayList<String> mSelected = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            Glide.with(this).load(mSelected.get(0)).into(pickImage);
            CompressImage compressImage = new CompressImage(this);
            imageUrl = compressImage.compressImage(mSelected.get(0));

        }


    }

    private void getServicemenFromDB() {
        mDatabase.child("Servicemen").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ServicemanModel model = snapshot.getValue(ServicemanModel.class);
                        if (model != null) {
                            servicemenList.add(model);
                        }
                    }
                    setupSpinner();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getDataFromServer() {
        mDatabase.child("Appointments").child(appointmentId).child("logs").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    itemList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        LogsModel model = snapshot.getValue(LogsModel.class);
                        if (model != null) {
                            itemList.add(model);
                        }
                    }

                    adapter.setItemList(itemList);
                    recycler.scrollToPosition(itemList.size() - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setupSpinner() {

        final String[] items = new String[servicemenList.size() + 1];
        items[0] = "Select Serviceman To Assign";
        for (int i = 0; i < servicemenList.size(); i++) {
            items[i + 1] = servicemenList.get(i).getName() + " - " + servicemenList.get(i).getRole();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                serviceMenSelected = items[i];
                positionSelected = i - 1;

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void saveDataToDb() {
        String key = mDatabase.push().getKey();
        LogsModel model = new LogsModel(key,
                logs.getText().toString(),
                System.currentTimeMillis(),
                timeSelected + " " + dateSelected,
                SharedPrefs.getUser().getUsername(),
                assignToStaffName, "");
        if (positionSelected != -1) {
            model.setAssignTo(servicemenList.get(positionSelected).getUsername());
        }
        if (imageUrl != null) {
            model.setImageUrl(imageUrl);
        }
        mDatabase.child("Appointments").child(appointmentId).child("logs").child(key).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                logs.setText("");
                CommonUtils.showToast("Saved");
                Glide.with(WorkHistory.this).load(R.drawable.ic_menu_gallery).into(pickImage);
            }
        });
    }

    private void showTimePicker() {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int selectedMinute) {


                String AM_PM = " AM";
                String mm_precede = "";
                if (hourOfDay >= 12) {
                    AM_PM = " PM";
                    if (hourOfDay >= 13 && hourOfDay < 24) {
                        hourOfDay -= 12;
                    } else {
                        hourOfDay = 12;
                    }
                } else if (hourOfDay == 0) {
                    hourOfDay = 12;
                }
                if (selectedMinute < 10) {
                    mm_precede = "0";
                }
                timeee.setVisibility(View.VISIBLE);
                timeSelected = hourOfDay + ":" + mm_precede + selectedMinute + " " + AM_PM;
                timeee.setText("Date Time Chosen: " + timeSelected + " " + dateSelected);
            }
        }, hour, minute, false);
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

}
