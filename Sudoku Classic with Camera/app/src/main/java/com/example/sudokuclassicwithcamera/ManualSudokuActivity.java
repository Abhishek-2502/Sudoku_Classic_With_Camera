package com.example.sudokuclassicwithcamera;

import static com.example.sudokuclassicwithcamera.Prompts.sudoku_prompt;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sudokuclassicwithcamera.utils.GridAndButtonUtils;
import com.example.sudokuclassicwithcamera.utils.SudokuSolverUtils;

import java.io.IOException;

public class ManualSudokuActivity extends AppCompatActivity {

    private static final String TAG = "ManualSudokuActivity";
    private static final int CAMERA_REQUEST_CODE = 101;
    private static final int GALLERY_REQUEST_CODE = 100;
    private static final int CAMERA_PERMISSION_CODE = 200;

    private int[][] userGrid = new int[9][9];
    private Button lastClickedButton = null;
    private Bitmap img_bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manual_sudoku);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeGame(savedInstanceState);
        setupGridButtons();
        setupInputButtons();
        setupControlButtons();
        setupCameraGalleryButtons();
    }


    private void initializeGame(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            userGrid = GridAndButtonUtils.unflattenGrid(savedInstanceState.getIntArray("userGrid"));
            if (userGrid[0][0] != 0) {
                GridAndButtonUtils.setGridOutput(userGrid, this, "enable");
                Log.d(TAG, "Manual game state restored");
            }
        }
    }

    private void setupGridButtons() {
        for (int i = 1; i <= 89; i++) {
            int resID = getResources().getIdentifier("button" + i, "id", getPackageName());
            if (resID != 0) {
                Button button = findViewById(resID);
                if (button != null) {
                    button.setOnClickListener(v -> {
                        lastClickedButton = GridAndButtonUtils.handleSudokuDigits(button,
                                ManualSudokuActivity.this, "ques", lastClickedButton);
                    });
                }
            }
        }
    }

    private void setupInputButtons() {
        for (char c = 'a'; c <= 'j'; c++) {
            String buttonId = "button" + c;
            int resID = getResources().getIdentifier(buttonId, "id", getPackageName());
            if (resID != 0) {
                Button button = findViewById(resID);
                if (button != null) {
                    button.setOnClickListener(v -> {
                        if (lastClickedButton == null) {
                            Toast.makeText(ManualSudokuActivity.this,
                                    "Please select a cell first", Toast.LENGTH_SHORT).show();
                        } else {
                            GridAndButtonUtils.handleInputsButton(button,
                                    ManualSudokuActivity.this, lastClickedButton, userGrid);
                        }
                    });
                }
            }
        }
    }

    private void setupControlButtons() {
        Button btsub = findViewById(R.id.buttons);
        Button btr = findViewById(R.id.buttonr);

        btr.setOnClickListener(v -> {
            GridAndButtonUtils.resetGrid(userGrid, this, lastClickedButton);
            lastClickedButton = null;
            Toast.makeText(this, "Grid cleared", Toast.LENGTH_SHORT).show();
        });

        btsub.setOnClickListener(v -> solveManualSudoku());
    }

    private void setupCameraGalleryButtons() {
        ImageButton imgbtcam = findViewById(R.id.imageButton);
        ImageButton imgbtgal = findViewById(R.id.imageButton2);

        imgbtgal.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, GALLERY_REQUEST_CODE);
        });

        imgbtcam.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(ManualSudokuActivity.this,
                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ManualSudokuActivity.this,
                        new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
            } else {
                openCamera();
            }
        });
    }

    private void solveManualSudoku() {
        try {
            GridAndButtonUtils.getGridInput(userGrid, this);

            if (SudokuSolverUtils.validateInput(userGrid, this)) {
                int[][] tempGrid = GridAndButtonUtils.copyGrid(userGrid);
                if (SudokuSolverUtils.solveSudoku(tempGrid)) {
                    GridAndButtonUtils.setGridOutput(tempGrid, this, "enable");
                    Toast.makeText(this, "Sudoku solved!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "No solution exists", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error solving manual Sudoku", e);
            Toast.makeText(this, "Error solving Sudoku", Toast.LENGTH_SHORT).show();
        }
    }

    private void openCamera() {
        try {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            } else {
                Toast.makeText(this, "No camera app found", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error opening camera", e);
            Toast.makeText(this, "Error opening camera", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            img_bitmap = null;

            try {
                if (requestCode == GALLERY_REQUEST_CODE && data != null) {
                    Uri selectedImageUri = data.getData();
                    if (selectedImageUri != null) {
                        img_bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                    }
                } else if (requestCode == CAMERA_REQUEST_CODE && data != null) {
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        img_bitmap = (Bitmap) extras.get("data");
                    }
                }
            } catch (IOException e) {
                Log.e(TAG, "Error loading image", e);
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                return;
            }

            if (img_bitmap != null) {
                processImageWithGemini();
            } else {
                Toast.makeText(this, "Failed to capture image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void processImageWithGemini() {
        try {
            System.out.println("Got Image");
            img_bitmap = img_bitmap.copy(Bitmap.Config.ARGB_8888, true);

            // Reset Sudoku grid before processing
            GridAndButtonUtils.resetGrid(userGrid, this, lastClickedButton);
            lastClickedButton = null;

            Toast.makeText(this, "Processing image with Gemini...", Toast.LENGTH_SHORT).show();

            // Process image with Gemini
            GenAI.getGeminiResponse(sudoku_prompt, img_bitmap, new GenAI.GenAIResponseCallback() {
                @Override
                public void onResponse(String result) {
                    System.out.println("Gemini Response: " + result);

                    if (result == null || result.trim().isEmpty() ||
                            result.equalsIgnoreCase("NULL") ||
                            result.contains("Error") ||
                            result.contains("Exception") ||
                            !result.trim().startsWith("[[")) {

                        runOnUiThread(() -> Toast.makeText(
                                ManualSudokuActivity.this,
                                "The uploaded image doesn't seem to contain a Sudoku grid.",
                                Toast.LENGTH_SHORT
                        ).show());
                        return;
                    }

                    int[][] matrix = GenAI.parseMatrix(result);
                    if (matrix != null) {
                        runOnUiThread(() -> {
                            userGrid = GridAndButtonUtils.copyGrid(matrix);
                            GridAndButtonUtils.setGridOutput(matrix, ManualSudokuActivity.this, "enable");
                            Toast.makeText(ManualSudokuActivity.this, "Sudoku grid extracted successfully!", Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        runOnUiThread(() -> Toast.makeText(
                                ManualSudokuActivity.this,
                                "Failed to parse Sudoku grid. Please try another image.",
                                Toast.LENGTH_SHORT
                        ).show());
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error processing image with Gemini", e);
            Toast.makeText(this, "Error processing image", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntArray("userGrid", GridAndButtonUtils.flattenGrid(userGrid));
        Log.d(TAG, "Manual game state saved");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "Manual game state restored");
    }
}