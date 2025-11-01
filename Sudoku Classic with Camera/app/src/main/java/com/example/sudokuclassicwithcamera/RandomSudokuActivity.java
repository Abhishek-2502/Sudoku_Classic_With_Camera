package com.example.sudokuclassicwithcamera;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sudokuclassicwithcamera.utils.GridAndButtonUtils;
import com.example.sudokuclassicwithcamera.utils.SudokuSolverUtils;

public class RandomSudokuActivity extends AppCompatActivity {

    private static final String TAG = "RandomSudokuActivity";

    private int[][] userGrid = new int[9][9];
    private int[][] genGrid = new int[9][9];
    private int[][] solutionGrid = new int[9][9];
    private Button lastClickedButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_random_sudoku);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeGame(savedInstanceState);
        setupGridButtons();
        setupInputButtons();
        setupControlButtons();

        // Validate button mapping
        GridAndButtonUtils.testCoordinateCalculation();
        boolean allValid = GridAndButtonUtils.validateAllButtons(this);
        GridAndButtonUtils.debugFullButtonMapping(this);

        if (!allValid) {
            Log.e(TAG, "WARNING: Not all buttons are properly mapped!");
            Toast.makeText(this, "Button mapping issue detected", Toast.LENGTH_SHORT).show();
        }
    }


    private void initializeGame(Bundle savedInstanceState) {
        // Restore state if available
        if (savedInstanceState != null) {
            userGrid = GridAndButtonUtils.unflattenGrid(savedInstanceState.getIntArray("userGrid"));
            genGrid = GridAndButtonUtils.unflattenGrid(savedInstanceState.getIntArray("genGrid"));
            solutionGrid = GridAndButtonUtils.unflattenGrid(savedInstanceState.getIntArray("solutionGrid"));

            if (genGrid[0][0] != 0) {
                // Restore UI state
                GridAndButtonUtils.setGridOutput(genGrid, this, "disable");
                GridAndButtonUtils.setGridOutput(userGrid, this, "enable");
                Log.d(TAG, "Game state restored");
                return;
            }
        }

        // Generate new game
        String difficulty = getIntent().getStringExtra("difficulty");
        if (difficulty == null) difficulty = "easy";

        Log.d(TAG, "Starting new game with difficulty: " + difficulty);
        generateNewSudoku(difficulty);
    }

    private void generateNewSudoku(String difficulty) {
        try {
            genGrid = SudokuSolverUtils.generateRandomSudoku(difficulty);
            solutionGrid = GridAndButtonUtils.copyGrid(genGrid);

            // Ensure we have the solution
            if (!SudokuSolverUtils.solveSudoku(solutionGrid)) {
                Log.e(TAG, "Failed to solve generated puzzle!");
                Toast.makeText(this, "Error generating puzzle", Toast.LENGTH_SHORT).show();
                return;
            }

            // Reset user grid to initial puzzle
            userGrid = GridAndButtonUtils.copyGrid(genGrid);

            // Update UI
            GridAndButtonUtils.setGridOutput(genGrid, this, "disable");
            Log.d(TAG, "New game generated successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error generating Sudoku", e);
            Toast.makeText(this, "Error generating puzzle", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupGridButtons() {
        for (int i = 1; i <= 89; i++) {
            int resID = getResources().getIdentifier("button" + i, "id", getPackageName());
            if (resID != 0) {
                Button button = findViewById(resID);
                if (button != null) {
                    final int buttonNum = i;
                    button.setOnClickListener(v -> {
                        lastClickedButton = GridAndButtonUtils.handleSudokuDigits(button,
                                RandomSudokuActivity.this, "ans", lastClickedButton);
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
                            Toast.makeText(RandomSudokuActivity.this,
                                    "Please select a cell first", Toast.LENGTH_SHORT).show();
                        } else {
                            GridAndButtonUtils.handleInputsButton(button,
                                    RandomSudokuActivity.this, lastClickedButton, userGrid);
                        }
                    });
                }
            }
        }
    }

    private void setupControlButtons() {
        Button btsub = findViewById(R.id.buttons);
        Button btr = findViewById(R.id.buttonr);
        Button btsolve = findViewById(R.id.buttonsolve);

        // Submit Button - Check solution
        btsub.setOnClickListener(v -> checkSolution());

        // Reset Button
        btr.setOnClickListener(v -> resetGame());

        // Solve Button
        btsolve.setOnClickListener(v -> solveCurrentPuzzle());
    }

    private void checkSolution() {
        try {
            // Update userGrid from UI
            GridAndButtonUtils.getGridInput(userGrid, this);

            if (GridAndButtonUtils.noZeroInGrid(userGrid)) {
                if (SudokuSolverUtils.validateInput(userGrid, this)) {
                    if (GridAndButtonUtils.compareGrid(userGrid, solutionGrid)) {
                        Toast.makeText(this, "Congratulations! You solved it!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Solution incorrect. Keep trying!", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(this, "Please fill all cells", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error checking solution", e);
            Toast.makeText(this, "Error checking solution", Toast.LENGTH_SHORT).show();
        }
    }

    private void resetGame() {
        try {
            // Reset to original generated puzzle
            userGrid = GridAndButtonUtils.copyGrid(genGrid);
            GridAndButtonUtils.setGridOutput(genGrid, this, "disable");
            lastClickedButton = null;
            Toast.makeText(this, "Game reset", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Error resetting game", e);
        }
    }

    private void solveCurrentPuzzle() {
        try {
            Log.d(TAG, "=== SOLVER DEBUG START ===");

            int[][] tempGrid = GridAndButtonUtils.copyGrid(userGrid);

            if (SudokuSolverUtils.validateInput(tempGrid, this)) {
                if (SudokuSolverUtils.solveSudoku(tempGrid)) {
                    GridAndButtonUtils.setGridOutput(tempGrid, this, "enable");
                    Toast.makeText(this, "Puzzle solved!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "No solution exists", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error solving puzzle", e);
            Toast.makeText(this, "Error solving puzzle", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntArray("userGrid", GridAndButtonUtils.flattenGrid(userGrid));
        outState.putIntArray("genGrid", GridAndButtonUtils.flattenGrid(genGrid));
        outState.putIntArray("solutionGrid", GridAndButtonUtils.flattenGrid(solutionGrid));
        Log.d(TAG, "Game state saved");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "Game state restored");
    }
}