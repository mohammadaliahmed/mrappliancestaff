package com.appsinventiv.mrappliancestaff.Activities.Jobs;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appsinventiv.mrappliancestaff.Adapters.AppointmentListAdapter;
import com.appsinventiv.mrappliancestaff.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class AppointmentListStatus extends AppCompatActivity {

    RecyclerView recycler;
    ImageView back;
    AppointmentListAdapter adapter;
    private ArrayList<AppointmentModel> itemList = new ArrayList<>();
    DatabaseReference mDatabase;

    TextView serviceName;
    String appointmentStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_list);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        serviceName = findViewById(R.id.serviceName);
        back = findViewById(R.id.back);
        appointmentStatus = getIntent().getStringExtra("appointmentStatus");
        serviceName.setText("List of "+appointmentStatus+" appointments");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        adapter = new AppointmentListAdapter(this, itemList);

        recycler.setAdapter(adapter);
        getdataFromServer();


    }

    private void getdataFromServer() {
        mDatabase.child("Appointments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                itemList.clear();
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        AppointmentModel model = snapshot.getValue(AppointmentModel.class);
                        if (model != null) {
                            if (model.getAppointmentStatus().equalsIgnoreCase(appointmentStatus)) {
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
}
