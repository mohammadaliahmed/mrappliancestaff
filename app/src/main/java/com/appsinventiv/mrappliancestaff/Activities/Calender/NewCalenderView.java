package com.appsinventiv.mrappliancestaff.Activities.Calender;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appsinventiv.mrappliancestaff.Activities.Login;
import com.appsinventiv.mrappliancestaff.Adapters.CalanderDaysAdapter;
import com.appsinventiv.mrappliancestaff.R;
import com.appsinventiv.mrappliancestaff.Utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;


public class NewCalenderView extends AppCompatActivity {
    RecyclerView recycler;
    private List<Long> itemList = new ArrayList<>();

    CalanderDaysAdapter adapter;
    TextView date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_calander);
        date = findViewById(R.id.date);
        date.setText(CommonUtils.getMonthAndYear(System.currentTimeMillis()));
        recycler = findViewById(R.id.recycler);
        itemList.add(System.currentTimeMillis() - (86400000 * 3));
        itemList.add(System.currentTimeMillis() - (86400000 * 2));
        itemList.add(System.currentTimeMillis() - (86400000));
        itemList.add(System.currentTimeMillis());
        itemList.add(System.currentTimeMillis() + (86400000));
        itemList.add(System.currentTimeMillis() + (86400000 * 2));
        itemList.add(System.currentTimeMillis() + (86400000 * 3));

        adapter = new CalanderDaysAdapter(this, itemList);
        recycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        recycler.setAdapter(adapter);


    }
}
