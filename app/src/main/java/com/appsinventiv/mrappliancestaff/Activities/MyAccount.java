package com.appsinventiv.mrappliancestaff.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.appsinventiv.mrappliancestaff.Activities.Salaries.SalariesList;
import com.appsinventiv.mrappliancestaff.Models.RatingModel;
import com.appsinventiv.mrappliancestaff.Models.ServicemanModel;
import com.appsinventiv.mrappliancestaff.Models.User;
import com.appsinventiv.mrappliancestaff.R;
import com.appsinventiv.mrappliancestaff.Utils.SharedPrefs;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.AbstractSequentialList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class MyAccount extends AppCompatActivity {

    TextView name, salary, skills, profession, phone;
    ImageView back;
    CircleImageView picture;
    DatabaseReference mDatabase;
    Button viewRatings;
    private List<RatingModel> ratingsList = new ArrayList<>();
    RatingBar ratingBar;
    Button viewSalaries;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        name = findViewById(R.id.name);
        viewSalaries = findViewById(R.id.viewSalaries);
        ratingBar = findViewById(R.id.ratingBar);
        picture = findViewById(R.id.picture);
        phone = findViewById(R.id.phone);
        back = findViewById(R.id.back);
        viewRatings = findViewById(R.id.viewRatings);
        salary = findViewById(R.id.salary);
        skills = findViewById(R.id.skills);
        profession = findViewById(R.id.profession);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        viewSalaries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyAccount.this, SalariesList.class));
            }
        });

        getDataFromServer();
        viewRatings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyAccount.this, MyRatings.class));
            }
        });
        ratingBar.setFocusable(false);
        ratingBar.setIsIndicator(true);


    }

    private void getDataFromServer() {
        mDatabase.child("Servicemen").child(SharedPrefs.getUser().getUsername()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    ServicemanModel user = dataSnapshot.getValue(ServicemanModel.class);
                    if (user != null) {
                        phone.setText(user.getMobile());
                        name.setText(user.getName());
                        profession.setText(user.getRole());
                        salary.setText("AED " + user.getSalary());
                        Glide.with(MyAccount.this).load(user.getImageUrl()).placeholder(R.drawable.ic_menu_gallery).into(picture);
                        skills.setText(user.getSkills());
                        getRatingsFromServer();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getRatingsFromServer() {
        mDatabase.child("Ratings").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        RatingModel model = snapshot.getValue(RatingModel.class);
                        if (model != null) {
                            if (model.getRatingTo().equalsIgnoreCase(SharedPrefs.getUser().getUsername())) {
                                ratingsList.add(model);
                            }
                        }
                    }
                    calculateRatings();


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void calculateRatings() {
        float totalRatings = 0;
        for (RatingModel model : ratingsList) {
            totalRatings = model.getRating() + totalRatings;
        }
        totalRatings = totalRatings / ratingsList.size();
        ratingBar.setRating(totalRatings);

    }
}
