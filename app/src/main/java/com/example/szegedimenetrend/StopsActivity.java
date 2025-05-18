package com.example.szegedimenetrend;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class StopsActivity extends AppCompatActivity {
    private static final String TAG = "StopsActivity";
    private FirebaseFirestore db;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> stopsList = new ArrayList<>();
    private ListView stopsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("StopsActivity", "StopsActivity elindult");
        setContentView(R.layout.activity_stops);

        db = FirebaseFirestore.getInstance();
        stopsListView = findViewById(R.id.stopsListView);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, stopsList);
        stopsListView.setAdapter(adapter);

        String scheduleName = getIntent().getStringExtra("scheduleName");
        if (scheduleName != null) {
            loadStopsFromFirestore(scheduleName);
        } else {
            Toast.makeText(this, "Hiányzó járatinformáció.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadStopsFromFirestore(String scheduleName) {
        db.collection("schedules")
                .whereEqualTo("name", scheduleName)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                        List<String> stops = (List<String>) doc.get("stops");
                        if (stops != null) {
                            stopsList.clear();
                            stopsList.addAll(stops);
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(this, "Nincsenek megállók a járathoz.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Hiba megállók lekérdezésénél: ", e);
                    Toast.makeText(this, "Hiba történt.", Toast.LENGTH_SHORT).show();
                });
    }
}
