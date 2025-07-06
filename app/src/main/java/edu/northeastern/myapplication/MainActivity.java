package edu.northeastern.myapplication;

import android.content.Intent; // For opening new screens
import android.os.Bundle;
import android.view.View;
import android.widget.Button; // For buttons
import android.widget.TextView; // For text on screen

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // For full screen look
        setContentView(R.layout.activity_main); // Link to main layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button aboutMeButton = findViewById(R.id.aboutMeButton); // Get "About Me" button
        TextView helloTextView = findViewById(R.id.helloTextView); // Get "Hello World" text


        // What happens when "About Me" button is tapped
        aboutMeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AboutMeActivity.class); // Plan to open AboutMeActivity
                startActivity(intent); // Open AboutMeActivity
            }
        });

        Button quickCalcButton = findViewById(R.id.quickCalcButton);
        quickCalcButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, QuickCalcActivity.class);
            startActivity(intent);
        });

        Button linkCollectorButton = findViewById(R.id.linkCollectorButton); // Get "Link Collector" button
        // What happens when "Link Collector" button is tapped
        linkCollectorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LinkCollectorActivity.class); // Plan to open LinkCollectorActivity
                startActivity(intent); // Open LinkCollectorActivity
            }
        });

        Button btnPrimeSearch = findViewById(R.id.btnPrimeSearch);
        btnPrimeSearch.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PrimeSearchActivity.class);
            startActivity(intent);
        });

        Button locationButton = findViewById(R.id.locationButton);
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LocationActivity.class);
                startActivity(intent); // Launch the new activity
            }
        });
    }
}