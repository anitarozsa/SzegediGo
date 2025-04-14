package com.example.szegedimenetrend;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class ScheduleListingActivity extends AppCompatActivity {
    private static final String LOG_TAG = ScheduleListingActivity.class.getName();

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
        //TODO: még nincs a kiválasztás funkció megvalósítva

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Járatok");


        ListView listView = findViewById(R.id.scheduleListView);
        String[] scheduleNumbers = {
                "1", "1A", "2", "3", "3F", "4", "5", "6", "8", "9", "10", "19", "7F", "20", "21", "24",
                "32", "36", "60", "60Y", "67", "67Y", "70", "71", "71A", "72", "72A", "73", "73Y", "74",
                "74Y", "75", "76", "77", "77A", "77Y", "78", "78A", "79H", "84", "90", "90F", "90H",
                "91E", "92E", "93E"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, scheduleNumbers);
        listView.setAdapter(adapter);





    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.schedule_list_menu, menu);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null && auth.getCurrentUser().isAnonymous()) {
            // Elrejtjük a kijelentkezés és beállítások menüpontokat
            menu.findItem(R.id.logOut).setVisible(false);
            menu.findItem(R.id.settings).setVisible(false);

            // Megjelenítjük a "Bejelentkezés" menüpontot
            menu.findItem(R.id.login).setVisible(true);
        }

        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.logOut) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;

        } else if (itemId == R.id.settings) {
            // TODO: beállítások???
            return true;

        } else if (itemId == R.id.login) {
            // Visszavisz a bejelentkező képernyőre
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;

        } else {
            return super.onOptionsItemSelected(item);
        }
    }



}