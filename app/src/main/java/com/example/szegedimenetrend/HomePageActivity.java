package com.example.szegedimenetrend;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class HomePageActivity extends AppCompatActivity {

    private static final String LOG_TAG = HomePageActivity.class.getName();
    private FirebaseUser user;
    private FirebaseFirestore db;
    private static final int REQUEST_CODE = 1;
    private ArrayList<String> favoriteSchedules = new ArrayList<>();
    private FavoriteAdapter adapter;
    private TextView infoTextView;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Kedvenc járataim");

        infoTextView = findViewById(R.id.searchBarEditText);
        listView = findViewById(R.id.favoriteListView);

        boolean deleteMode = getIntent().getBooleanExtra("delete_mode", false);

        adapter = new FavoriteAdapter(this, favoriteSchedules, deleteMode);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setAdapter(adapter);

        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        if (user != null) {
            loadFavoritesFromFirestore();
        } else {
            Log.d(LOG_TAG, "Unauthenticated user!");
            finish();
        }

        if (deleteMode) {
            findViewById(R.id.addFavoriteButton).setVisibility(View.GONE);
            findViewById(R.id.deleteButton).setVisibility(View.VISIBLE);
            Toast.makeText(this, "Válaszd ki a törlendő járatokat!", Toast.LENGTH_LONG).show();
        } else {
            findViewById(R.id.addFavoriteButton).setVisibility(View.VISIBLE);
            findViewById(R.id.deleteButton).setVisibility(View.GONE);
        }

        if (!deleteMode) {
            listView.setOnItemClickListener((parent, view, position, id) -> {
                String selectedSchedule = favoriteSchedules.get(position);
                Intent intent = new Intent(this, StopsActivity.class); // Módosítva: StopsActivity indítása
                intent.putExtra("scheduleName", selectedSchedule);
                startActivity(intent);
            });
        } else {
            listView.setOnItemClickListener((parent, view, position, id) -> {
                adapter.toggleSelection(position);
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (user != null) {
            loadFavoritesFromFirestore();
        }
    }

    private void loadFavoritesFromFirestore() {
        db.collection("favorites").document(user.getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    favoriteSchedules.clear();
                    if (documentSnapshot.exists()) {
                        List<String> data = (List<String>) documentSnapshot.get("favoriteSchedules");
                        if (data != null) {
                            favoriteSchedules.addAll(data);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    infoTextView.setVisibility(favoriteSchedules.isEmpty() ? View.VISIBLE : View.GONE);
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Hiba történt: ", e));
    }

    public void openScheduleListing(View view) {
        Intent intent = new Intent(this, ScheduleListingActivity.class);
        intent.putStringArrayListExtra("currentFavorites", favoriteSchedules);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            String newSchedule = data.getStringExtra("selectedSchedule");
            if (newSchedule != null && !favoriteSchedules.contains(newSchedule)) {
                favoriteSchedules.add(newSchedule);
                adapter.notifyDataSetChanged();

                db.collection("favorites").document(user.getUid())
                        .set(new HashMap<String, Object>() {{
                            put("favoriteSchedules", favoriteSchedules);
                        }})
                        .addOnFailureListener(e -> Log.e(LOG_TAG, "Nem sikerült elmenteni a kedvenceket.", e));

                infoTextView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.schedule_list_menu, menu);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null || currentUser.isAnonymous()) {
            menu.findItem(R.id.login).setVisible(true);
            menu.findItem(R.id.logOut).setVisible(false);
            menu.findItem(R.id.settings).setVisible(false);
        } else {
            menu.findItem(R.id.login).setVisible(false);
            menu.findItem(R.id.logOut).setVisible(true);
            menu.findItem(R.id.settings).setVisible(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.logOut) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.login) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void deleteFavorites(View view) {
        Set<Integer> selectedItems = adapter.getSelectedItems();
        if (selectedItems.isEmpty()) {
            Toast.makeText(this, "Nincs kijelölt elem a törléshez", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Integer> selectedList = new ArrayList<>(selectedItems);
        Collections.sort(selectedList, Collections.reverseOrder());
        for (int position : selectedList) {
            favoriteSchedules.remove(position);
        }
        adapter.clearSelection();
        adapter.notifyDataSetChanged();

        db.collection("favorites").document(user.getUid())
                .set(new HashMap<String, Object>() {{
                    put("favoriteSchedules", favoriteSchedules);
                }})
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Kijelölt kedvencek törölve", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(HomePageActivity.this, HomePageActivity.class);
                    intent.putExtra("delete_mode", false);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Hiba történt a törlés során", Toast.LENGTH_SHORT).show();
                    Log.e(LOG_TAG, "Nem sikerült frissíteni a Firestore-t.", e);
                });

        if (favoriteSchedules.isEmpty()) {
            infoTextView.setVisibility(View.VISIBLE);
        }
    }
}
