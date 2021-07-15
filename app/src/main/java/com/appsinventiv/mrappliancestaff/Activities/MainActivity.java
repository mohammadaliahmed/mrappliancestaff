package com.appsinventiv.mrappliancestaff.Activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.appsinventiv.mrappliancestaff.Activities.Calender.CalenderView;
import com.appsinventiv.mrappliancestaff.Activities.Calender.NewCalenderView;
import com.appsinventiv.mrappliancestaff.Activities.Expenses.ExpnesesList;
import com.appsinventiv.mrappliancestaff.Activities.Invoices.InvoicesActivity;
import com.appsinventiv.mrappliancestaff.Activities.Invoices.InvoicesList;
import com.appsinventiv.mrappliancestaff.Activities.Jobs.AppointmentModel;
import com.appsinventiv.mrappliancestaff.Activities.Jobs.JobsActivity;
import com.appsinventiv.mrappliancestaff.Utils.CommonUtils;
import com.appsinventiv.mrappliancestaff.Utils.SharedPrefs;

import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appsinventiv.mrappliancestaff.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.AbstractSequentialList;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    ImageView nav;
    private DrawerLayout drawer;
    RelativeLayout currentAssignments, myratings, assignmentHistory, createInvoice, expenses, calender;
    TextView username;
    DatabaseReference mDatabase;
    TextView name;
    public static ArrayList<AppointmentModel> appointmentList = new ArrayList<>();
    private double lng, lat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        getPermissions();
//        updateFcmKeyAndCoordinates();


        username = findViewById(R.id.username);
        username.setText(SharedPrefs.getUser().getName());

        nav = findViewById(R.id.nav);
        expenses = findViewById(R.id.expenses);
        currentAssignments = findViewById(R.id.currentAssignments);
        calender = findViewById(R.id.calender);
        createInvoice = findViewById(R.id.createInvoice);
        myratings = findViewById(R.id.myratings);
//        complaints = findViewById(R.id.complaints);
        assignmentHistory = findViewById(R.id.assignmentHistory);
        name = findViewById(R.id.name);
        name.setText("Welcome, " + SharedPrefs.getUser().getName());
        calender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (appointmentList.size() > 0) {
                    startActivity(new Intent(MainActivity.this, NewCalenderView.class));
                } else {
                    CommonUtils.showToast("Plz wait");
                }
//                startActivity(new Intent(MainActivity.this, CalenderView.class));
            }
        });

        createInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, InvoicesList.class));
            }
        });
        expenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ExpnesesList.class));
            }
        });

        currentAssignments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, JobsActivity.class));

            }
        });
        assignmentHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AssignmentHistory.class));

            }
        });

        myratings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MyAccount.class));
            }
        });

        getDataFromServer();
        initDrawer();
    }

    private void updateFcmKeyAndCoordinates() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("fcmKey", FirebaseInstanceId.getInstance().getToken());
        map.put("latitude", lat);
        map.put("longitude", lng);
        mDatabase.child("Servicemen").child(SharedPrefs.getUser().getUsername()).updateChildren(map);
    }


    private void getDataFromServer() {

        mDatabase.child("Appointments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                appointmentList.clear();
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        AppointmentModel model = snapshot.getValue(AppointmentModel.class);
                        if (model != null) {
                            appointmentList.add(model);
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getPermissions() {


        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,


        };

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        } else {
//            Intent intent = new Intent(MapsActivity.this, GPSTrackerActivity.class);
//            startActivityForResult(intent, 1);
        }
    }


    public boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                } else {
                    Intent intent = new Intent(MainActivity.this, GPSTrackerActivity.class);
                    startActivityForResult(intent, 1);
                }
            }
        }
        return true;
    }

    private void getAdminFCMkey() {
        mDatabase.child("Admin").child("fcmKey").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    String adminFcmKey = dataSnapshot.getValue(String.class);
                    SharedPrefs.setAdminFcmKey(adminFcmKey);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initDrawer() {

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.openDrawer(GravityCompat.START);
                } else {
                }
            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data != null) {
                Bundle extras = data.getExtras();
                lng = extras.getDouble("Longitude");
                lat = extras.getDouble("Latitude");
                if (lng > 10) {
                    updateFcmKeyAndCoordinates();
                }


            }

        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_about) {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://firebasestorage.googleapis.com/v0/b/fixitservices-b8de0.appspot.com/o/pdf%2FAbout%20Us-MrAppliance.pdf?alt=media&token=bc2ead68-19ee-49fb-9ec4-e1fe97a1e292"));
            startActivity(i);
        } else if (id == R.id.nav_terms) {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://firebasestorage.googleapis.com/v0/b/fixitservices-b8de0.appspot.com/o/pdf%2FPrivacy%20Policy-MrAppliance.pdf?alt=media&token=7c13ef05-e899-4056-aa07-c1975de51229"));
            startActivity(i);
        } else if (id == R.id.nav_help) {

        } else if (id == R.id.nav_logout) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Alert");
            builder.setMessage("Sure to logout?");

            // add the buttons
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    SharedPrefs.logout();
                    Intent intent = new Intent(MainActivity.this, Splash.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });
            builder.setNegativeButton("Cancel", null);

            // create and show the alert dialog
            AlertDialog dialog = builder.create();
            dialog.show();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
