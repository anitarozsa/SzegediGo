package com.example.szegedimenetrend;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class ScheduleListingActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseUser user;
    private ArrayList<String> currentFavorites;
    private ArrayList<String> scheduleList = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    private static final String TAG = "ScheduleListingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_schedule_listing);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Járatok");

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        currentFavorites = getIntent().getStringArrayListExtra("currentFavorites");
        if (currentFavorites == null) {
            currentFavorites = new ArrayList<>();
        }

        ListView listView = findViewById(R.id.scheduleListView);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, scheduleList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedSchedule = scheduleList.get(position);

            if (user != null) {
                db.collection("favorites").document(user.getUid()).get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                // Dokumentum létezik → update
                                db.collection("favorites").document(user.getUid())
                                        .update("favoriteSchedules", FieldValue.arrayUnion(selectedSchedule))
                                        .addOnSuccessListener(aVoid -> {
                                            Intent resultIntent = new Intent();
                                            resultIntent.putExtra("selectedSchedule", selectedSchedule);
                                            setResult(RESULT_OK, resultIntent);
                                            finish();
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e(TAG, "Hiba a kedvenc mentésénél: ", e);
                                            Toast.makeText(ScheduleListingActivity.this, "Nem sikerült a kedvencekhez adni.", Toast.LENGTH_SHORT).show();
                                        });
                            } else {
                                // Dokumentum nem létezik → létrehozzuk
                                ArrayList<String> initialList = new ArrayList<>();
                                initialList.add(selectedSchedule);

                                db.collection("favorites").document(user.getUid())
                                        .set(new HashMap<String, Object>() {{
                                            put("favoriteSchedules", initialList);
                                        }})
                                        .addOnSuccessListener(aVoid -> {
                                            Intent resultIntent = new Intent();
                                            resultIntent.putExtra("selectedSchedule", selectedSchedule);
                                            setResult(RESULT_OK, resultIntent);
                                            finish();
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e(TAG, "Hiba új dokumentum létrehozásánál: ", e);
                                            Toast.makeText(ScheduleListingActivity.this, "Nem sikerült a kedvencekhez adni.", Toast.LENGTH_SHORT).show();
                                        });
                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Hiba a dokumentum lekérdezésekor: ", e);
                            Toast.makeText(ScheduleListingActivity.this, "Nem sikerült a kedvencekhez adni.", Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(this, "Nincs bejelentkezett felhasználó!", Toast.LENGTH_SHORT).show();
            }
        });

        loadSchedulesFromFirestore();
    }

    private void loadSchedulesFromFirestore() {
        db.collection("schedules").get()
                .addOnSuccessListener(querySnapshot -> {
                    scheduleList.clear();
                    for (DocumentSnapshot doc : querySnapshot) {
                        String name = doc.getString("name");
                        if (name != null) {
                            scheduleList.add(name);
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Hiba a járatok lekérésekor: ", e);
                    Toast.makeText(this, "Nem sikerült betölteni a járatokat.", Toast.LENGTH_SHORT).show();
                });
    }
}
