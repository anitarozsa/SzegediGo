package com.example.szegedimenetrend;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings); // vagy ahogy a layout fájlod neve van

        // Toolbar beállítása
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Gombok és kép inicializálása
        Button deleteButton = findViewById(R.id.deleteScheduleButton);
        Button formoreButton = findViewById(R.id.formoreButton);
        ImageView formoreImage = findViewById(R.id.formoreImageView);

        // Animáció betöltése és elindítása
        Animation rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate);
        deleteButton.startAnimation(rotateAnimation);
        formoreButton.startAnimation(rotateAnimation);
        formoreImage.startAnimation(rotateAnimation);

        // Törlés gomb működése
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(SettingsActivity.this)
                        .setTitle("Törlés megerősítése")
                        .setMessage("Szeretnéd törölni a mentett járatok közül néhányat?")
                        .setPositiveButton("Igen", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(SettingsActivity.this, HomePageActivity.class);
                                intent.putExtra("delete_mode", true);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton("Mégsem", null)
                        .show();
            }
        });

        // További infó gomb működése
        formoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://szkt.hu/"; // Weboldal URL
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(android.net.Uri.parse(url));
                startActivity(intent);
            }
        });
    }
}
