package com.example.szegedimenetrend;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class RouteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route);

        TextView routeTitle = findViewById(R.id.routeTitleTextView);
        ListView stopsListView = findViewById(R.id.stopsListView);

        // Átvett Schedule példány lekérése
        Schedule schedule = getIntent().getParcelableExtra("selectedSchedule");

        if (schedule != null) {
            String title = schedule.getScheduleNumber() + " - " + schedule.getRouteName();
            routeTitle.setText(title);

            List<String> stops = schedule.getStops();
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, stops);
            stopsListView.setAdapter(adapter);
        }
    }
}
