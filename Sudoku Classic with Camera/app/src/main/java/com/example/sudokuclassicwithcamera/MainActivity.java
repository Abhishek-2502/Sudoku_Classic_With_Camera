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

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
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
        Button btplay = findViewById(R.id.buttonplay);
        Button btplay2 = findViewById(R.id.buttonplay2);

        setupButton(btplay, LevelSelectActivity.class);
        setupButton(btplay2, ManualSudokuActivity.class);
    }

    private void setupButton(Button button, Class<?> activityClass) {
        button.setOnClickListener(v -> startActivity(new Intent(this, activityClass)));
    }
}