package com.example.sudokuclassicwithcamera;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LevelSelectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_level_select);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Check the current UI mode (dark or light)
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                findViewById(R.id.main).setBackgroundResource(R.drawable.dark_bg);
                break;
            case Configuration.UI_MODE_NIGHT_NO:
            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                findViewById(R.id.main).setBackgroundResource(R.drawable.light_bg);
                break;
        }

        // Buttons
        Button bte = findViewById(R.id.buttoneasy);
        Button btm = findViewById(R.id.buttonmed);
        Button bth = findViewById(R.id.buttonhard);

        setupButton(bte, "easy");
        setupButton(btm, "medium");
        setupButton(bth, "hard");
    }

    private void setupButton(Button button, String difficulty) {
        button.setOnClickListener(v -> {
            Intent intent = new Intent(this, RandomSudokuActivity.class);
            intent.putExtra("difficulty", difficulty);
            startActivity(intent);
        });
    }
}