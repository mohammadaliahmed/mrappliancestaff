package com.appsinventiv.mrappliancestaff.Activities.Jobs;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appsinventiv.mrappliancestaff.Activities.Login;
import com.appsinventiv.mrappliancestaff.Adapters.AppointmentListAdapter;
import com.appsinventiv.mrappliancestaff.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class AppointmentList extends AppCompatActivity {

    RecyclerView recycler;
    ImageView back;
    AppointmentListAdapter adapter;
    private ArrayList<AppointmentModel> itemList = new ArrayList<>();
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_list);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        back = findViewById(R.id.back);
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
        gedataFromServer();


    }

    private void gedataFromServer() {
        mDatabase.child("Appointments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                itemList.clear();
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        AppointmentModel model = snapshot.getValue(AppointmentModel.class);
                        if (model != null) {
                            itemList.add(model);
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
