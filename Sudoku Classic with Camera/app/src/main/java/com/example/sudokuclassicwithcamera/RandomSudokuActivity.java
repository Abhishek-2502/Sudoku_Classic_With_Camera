package com.example.sudokuclassicwithcamera;


import android.os.Bundle;
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

    public static int[][] usergrid = new int[9][9];
    public static int[][] gengrid = new int[9][9];

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Declaring Variables
        Button btsub;
        Button btr;
        Button btsolve;

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_random_sudoku);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        btsub = findViewById(R.id.buttons);
        btr = findViewById(R.id.buttonr);
        btsolve = findViewById(R.id.buttonsolve);

        /*
        //For Debugging
        int[][] solvedsudoku = {
                {4, 5, 3, 8, 2, 6, 1, 9, 7},
                {8, 9, 2, 5, 7, 1, 6, 3, 4},
                {1, 6, 7, 4, 9, 3, 5, 2, 8},
                {7, 1, 4, 9, 5, 2, 8, 6, 3},
                {5, 8, 6, 1, 3, 7, 2, 4, 9},
                {3, 2, 9, 6, 8, 4, 7, 5, 1},
                {9, 3, 5, 2, 1, 8, 4, 7, 6},
                {6, 7, 1, 3, 4, 5, 9, 8, 2},
                {2, 4, 8, 7, 6, 9, 3, 1, 5}
        };
        int buttonIndex = 0;
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                buttonIndex++;

                if (buttonIndex % 10 == 0) {
                    buttonIndex++;
                }

                // Check if button exists and update its text
                int resID = getResources().getIdentifier("button" + buttonIndex, "id", getPackageName());
                if (resID != 0) {
                    Button button = findViewById(resID);
                    if (button != null) {
                        int gridValue = solvedsudoku[row][col];
                        button.setText(String.valueOf(gridValue));
                    }
                }
            }
        }

        */

        // Obtain extras from the Intent
        String value="";
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            value = extras.getString("difficulty");
            System.out.println(value);
        }

        if(value.equals("easy")){
            gengrid=SudokuSolverUtils.generateRandomSudoku("easy");
            GridAndButtonUtils.setGridOutput(gengrid,this,"disable");
        }
        else if(value.equals("medium")){
            gengrid=SudokuSolverUtils.generateRandomSudoku("medium");
            GridAndButtonUtils.setGridOutput(gengrid,this,"disable");
        }
        else if(value.equals("hard")){
            gengrid=SudokuSolverUtils.generateRandomSudoku("hard");
            GridAndButtonUtils.setGridOutput(gengrid,this,"disable");
        }
        else{
            Toast.makeText(this, "Key Error", Toast.LENGTH_SHORT).show();
        }


        // Main Logic for Button 1 to 89
        for (int i = 1; i <= 89; i++) {
            // Generate the button ID dynamically
            int resID = getResources().getIdentifier("button" + i, "id", getPackageName());
            if (resID != 0) {
                Button button = findViewById(resID);
                if (button != null) {
                    button.setOnClickListener(v -> {
                        GridAndButtonUtils.handleSudokuDigits(button,this,"ans");
                    });
                }
            }
        }

        // Main Logic for Button a to j
        for (char c = 'a'; c <= 'j'; c++) {
            String buttonId = "button" + c;
            int resID = getResources().getIdentifier(buttonId, "id", getPackageName());
            if (resID != 0) {
                Button button = findViewById(resID);
                if (button != null) {
                    button.setOnClickListener(v -> {
                        if(GridAndButtonUtils.lastClickedButton==null){
                            Toast.makeText(this, "Please select a digit", Toast.LENGTH_SHORT).show();
                        }else{
                            GridAndButtonUtils.handleInputsButton(button,this);
                        }
                    });
                }
            }
        }

        // Reset
        btr.setOnClickListener(v -> {
            GridAndButtonUtils.setGridOutput(gengrid,this,"disable");
        });


        btsub.setOnClickListener(v -> {
            GridAndButtonUtils.getGridInput(usergrid, this);
            if (GridAndButtonUtils.noZeroGrid(usergrid)) {
                if (SudokuSolverUtils.validateInput(usergrid, this)) {
                    Toast.makeText(this, "You Won", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(this, "Contain Empty Space", Toast.LENGTH_SHORT).show();
            }
        });

        //Main Logic for solve button
        btsolve.setOnClickListener(v -> {
            GridAndButtonUtils.getGridInput(usergrid,this);

//            For Debugging
//            GridAndButtonUtils.printGrid(usergrid);

            if(SudokuSolverUtils.validateInput(usergrid,this)){
                if (SudokuSolverUtils.solveSudoku(usergrid, 0, 0)) {
                    GridAndButtonUtils.setGridOutput(usergrid,this,"enable");
                }
                else {
                    Toast.makeText(this, "No Solution exists", Toast.LENGTH_SHORT).show();
                }
            }

//            For Debugging
//            GridAndButtonUtils.printGrid(usergrid);

        });

    }


}