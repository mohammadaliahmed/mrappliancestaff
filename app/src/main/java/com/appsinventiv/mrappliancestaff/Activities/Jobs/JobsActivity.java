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
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.appsinventiv.mrappliancestaff.Activities.Login;
import com.appsinventiv.mrappliancestaff.R;

import org.w3c.dom.Text;


public class JobsActivity extends AppCompatActivity {

    ImageView back;
    ImageView newAppointment;
    RelativeLayout appointments, finishedJobs, recomplaints, deliveries, pendingJobs, diagnostic, earnings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobs);
        newAppointment = findViewById(R.id.newAppointment);
        finishedJobs = findViewById(R.id.finishedJobs);
        recomplaints = findViewById(R.id.recomplaints);
        deliveries = findViewById(R.id.deliveries);
        pendingJobs = findViewById(R.id.pendingJobs);
        diagnostic = findViewById(R.id.diagnostic);
        earnings = findViewById(R.id.earnings);
        appointments = findViewById(R.id.appointments);

        recomplaints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(JobsActivity.this, AppointmentListStatus.class);
                intent.putExtra("appointmentStatus", "ReComplaint");
                startActivity(intent);
            }
        });
        deliveries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(JobsActivity.this, AppointmentListStatus.class);
                intent.putExtra("appointmentStatus", "Deliveries");
                startActivity(intent);
            }
        });
        pendingJobs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(JobsActivity.this, AppointmentListStatus.class);
                intent.putExtra("appointmentStatus", "Pending");
                startActivity(intent);
            }
        });
        diagnostic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(JobsActivity.this, AppointmentListStatus.class);
                intent.putExtra("appointmentStatus", "Diagnostic");
                startActivity(intent);
            }
        });
        finishedJobs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(JobsActivity.this, AppointmentListStatus.class);
                intent.putExtra("appointmentStatus", "Finished");
                startActivity(intent);
            }
        });


        newAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(JobsActivity.this, NewAppointment.class));

            }
        });
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

        appointments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(JobsActivity.this, AppointmentList.class));
            }
        });

    }
}
