package com.example.sudokuclassicwithcamera.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Button;
import com.example.sudokuclassicwithcamera.R;

public class GridAndButtonUtils {

    private static final String TAG = "GridAndButtonUtils";
    private static TypedValue typedValue = new TypedValue();
    private static TypedValue typedValue1 = new TypedValue();

    // --Handle the sudoku digits
    public static Button handleSudokuDigits(Button clickedButton, Context context, String tag, Button lastClickedButton) {
        try {
            // Reset the last clicked button if it's not null and is not the current button
            if (lastClickedButton != null && lastClickedButton != clickedButton) {
                resetButtonAppearance(lastClickedButton, context);
            }

            // Style the newly clicked button
            clickedButton.setTextSize(24);
            clickedButton.setBackgroundColor(context.getResources().getColor(R.color.bg_clicked));

            if ("ans".equals(tag)) {
                clickedButton.setTextColor(context.getResources().getColor(R.color.blue));
            } else {
                clickedButton.setTextColor(context.getResources().getColor(R.color.black));
            }

            return clickedButton;
        } catch (Exception e) {
            Log.e(TAG, "Error in handleSudokuDigits", e);
            return lastClickedButton;
        }
    }

    //-- Handle the inputs for the buttons
    public static void handleInputsButton(Button clickedButton, Context context, Button lastClickedButton, int[][] userGrid) {
        if (lastClickedButton == null) return;

        try {
            String input = getInputFromButton(clickedButton);
            if (input == null) return;

            // Validate input
            if (!isValidInput(input)) {
                return;
            }

            lastClickedButton.setText(input);

            // Update the grid if provided
            if (userGrid != null) {
                int[] coords = getGridCoordinatesFromButton(lastClickedButton);
                if (coords != null) {
                    if (!input.trim().isEmpty() && !input.equals(" ")) {
                        userGrid[coords[0]][coords[1]] = Integer.parseInt(input);
                    } else {
                        userGrid[coords[0]][coords[1]] = 0;
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in handleInputsButton", e);
        }
    }

    // --Get input value from button ID
    private static String getInputFromButton(Button button) {
        int id = button.getId();

        // Use resource names for reliable identification
        String resourceName = "";
        try {
            resourceName = button.getResources().getResourceName(id);
        } catch (Exception e) {
            return null;
        }

        if (resourceName.contains("buttona")) return "1";
        if (resourceName.contains("buttonb")) return "2";
        if (resourceName.contains("buttonc")) return "3";
        if (resourceName.contains("buttond")) return "4";
        if (resourceName.contains("buttone")) return "5";
        if (resourceName.contains("buttonf")) return "6";
        if (resourceName.contains("buttong")) return "7";
        if (resourceName.contains("buttonh")) return "8";
        if (resourceName.contains("buttoni")) return "9";
        if (resourceName.contains("buttonj")) return " ";

        return null;
    }

    // --Validate input
    public static boolean isValidInput(String input) {
        if (input.equals(" ") || input.isEmpty()) {
            return true; // Clear cell
        }
        try {
            int value = Integer.parseInt(input);
            return value >= 1 && value <= 9;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // --Get grid coordinates from button using explicit mapping
    public static int[] getGridCoordinatesFromButton(Button button) {
        if (button == null) return null;

        int[] coords = new int[2];
        String resourceName = "";

        try {
            resourceName = button.getResources().getResourceName(button.getId());
            Log.d(TAG, "Processing: " + resourceName);
        } catch (Exception e) {
            Log.e(TAG, "Error getting resource name", e);
            return null;
        }

        // Extract button number from resource name
        try {
            String buttonNumStr = resourceName.substring(resourceName.lastIndexOf("button") + 6);
            int buttonNum = Integer.parseInt(buttonNumStr);

            Log.d(TAG, "Processing button" + buttonNum);

            // Validate button number range
            if (buttonNum < 1 || buttonNum > 89) {
                Log.w(TAG, "Button number " + buttonNum + " out of range");
                return null;
            }

            // Skip invalid button numbers (10, 20, 30, etc.)
            if (buttonNum % 10 == 0) {
                Log.w(TAG, "Button number " + buttonNum + " is skipped in layout");
                return null;
            }

            // Calculate mapping directly (more efficient)
            // Since we skip every 10th button, we need to adjust the calculation
            int skippedButtons = buttonNum / 10; // Number of 10th buttons we've passed
            int actualPosition = buttonNum - skippedButtons;

            // Convert to grid coordinates (0-8, 0-8)
            int row = (actualPosition - 1) / 9;
            int col = (actualPosition - 1) % 9;

            // Validate coordinates
            if (row >= 0 && row < 9 && col >= 0 && col < 9) {
                coords[0] = row;
                coords[1] = col;
                Log.d(TAG, String.format("Button%d -> Grid[%d][%d]", buttonNum, row, col));
                return coords;
            } else {
                Log.w(TAG, "Calculated invalid coordinates: row=" + row + ", col=" + col);
                return null;
            }
        } catch (NumberFormatException e) {
            Log.e(TAG, "Invalid button number format", e);
            return null;
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error in coordinate calculation", e);
            return null;
        }
    }

    // --Create mapping for our specific layout (buttons 1-89 with gaps)
    private static int[][] createButtonToGridMapping() {
        int[][] mapping = new int[9][9];
        int buttonIndex = 1;

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                mapping[row][col] = buttonIndex;
                buttonIndex++;

                // Skip every 10th button (your layout has gaps at 10,20,30,...,90)
                if (buttonIndex % 10 == 0) {
                    buttonIndex++;
                }

                // Safety check - don't exceed 89
                if (buttonIndex > 89) {
                    Log.w(TAG, "Button index exceeded 89 at row=" + row + ", col=" + col);
                    return mapping;
                }
            }
        }
        return mapping;
    }

    // --Reset button to default appearance
    private static void resetButtonAppearance(Button button, Context context) {
        button.setTextSize(20);
        context.getTheme().resolveAttribute(android.R.attr.colorForeground, typedValue, true);
        context.getTheme().resolveAttribute(android.R.attr.colorBackground, typedValue1, true);
        int colorForeground = typedValue.data;
        int colorBackground = typedValue1.data;

        button.setTextColor(colorForeground);
        button.setBackgroundColor(colorBackground);
    }

    // --Reset the grid
    public static void resetGrid(int grid[][], Context context, Button lastClickedButton) {
        try {
            for (int i = 1; i <= 89; i++) {
                // Skip every 10th button
                if (i % 10 == 0) continue;

                int resID = context.getResources().getIdentifier("button" + i, "id", context.getPackageName());
                if (resID != 0) {
                    Button button = ((Activity)context).findViewById(resID);
                    if (button != null) {
                        button.setText("");
                        button.setEnabled(true);
                        resetButtonAppearance(button, context);

                        // Also reset the grid array
                        if (grid != null) {
                            int[] coords = getGridCoordinatesFromButton(button);
                            if (coords != null) {
                                grid[coords[0]][coords[1]] = 0;
                            }
                        }
                    }
                }
            }

            // Reset last clicked button appearance
            if (lastClickedButton != null) {
                resetButtonAppearance(lastClickedButton, context);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in resetGrid", e);
        }
    }

    // --Print the grid
    public static void printGrid(int grid[][]) {
        if (grid == null) {
            Log.d(TAG, "Grid is null");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Sudoku Grid:\n");
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                sb.append(grid[row][col]).append(" ");
            }
            sb.append("\n");
        }
        Log.d(TAG, sb.toString());
    }

    // --Get the grid input from UI
    public static void getGridInput(int grid[][], Context context) {
        if (grid == null) return;

        try {
            for (int i = 1; i <= 89; i++) {
                if (i % 10 == 0) continue; // Skip every 10th button

                int resID = context.getResources().getIdentifier("button" + i, "id", context.getPackageName());
                if (resID != 0) {
                    Button button = ((Activity)context).findViewById(resID);
                    if (button != null) {
                        int[] coords = getGridCoordinatesFromButton(button);
                        if (coords != null) {
                            String btText = button.getText().toString();
                            try {
                                if (btText.equals("") || btText.equals(" ")) {
                                    grid[coords[0]][coords[1]] = 0;
                                } else {
                                    grid[coords[0]][coords[1]] = Integer.parseInt(btText);
                                }
                            } catch (NumberFormatException e) {
                                grid[coords[0]][coords[1]] = 0;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in getGridInput", e);
        }
    }

    // --Set the grid output to UI
    public static void setGridOutput(int grid[][], Context context, String tag) {
        if (grid == null) return;

        try {
            for (int i = 1; i <= 89; i++) {
                if (i % 10 == 0) continue; // Skip every 10th button

                int resID = context.getResources().getIdentifier("button" + i, "id", context.getPackageName());
                if (resID != 0) {
                    Button button = ((Activity)context).findViewById(resID);
                    if (button != null) {
                        int[] coords = getGridCoordinatesFromButton(button);
                        if (coords != null) {
                            int gridValue = grid[coords[0]][coords[1]];
                            if (gridValue == 0) {
                                button.setText("");
                            } else {
                                button.setText(String.valueOf(gridValue));
                            }

                            if ("disable".equals(tag)) {
                                if (gridValue != 0) {
                                    button.setEnabled(false);
                                    button.setTextColor(context.getResources().getColor(R.color.blue));
                                } else {
                                    button.setEnabled(true);
                                }
                            } else {
                                button.setEnabled(true);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in setGridOutput", e);
        }
    }

    // --Check if the grid contains any zero
    public static boolean noZeroInGrid(int grid[][]) {
        if (grid == null) return false;

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (grid[row][col] < 1 || grid[row][col] > 9) {
                    return false;
                }
            }
        }
        return true;
    }

    // --Compare two grids
    public static boolean compareGrid(int grid1[][], int grid2[][]) {
        if (grid1 == null || grid2 == null) return false;

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (grid1[row][col] != grid2[row][col]) {
                    return false;
                }
            }
        }
        return true;
    }

    // --Copy grid
    public static int[][] copyGrid(int[][] source) {
        if (source == null) return null;
        int[][] copy = new int[9][9];
        for (int i = 0; i < 9; i++) {
            System.arraycopy(source[i], 0, copy[i], 0, 9);
        }
        return copy;
    }

    // --Flatten grid for saving state
    public static int[] flattenGrid(int[][] grid) {
        if (grid == null) return new int[81];
        int[] flat = new int[81];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                flat[i * 9 + j] = grid[i][j];
            }
        }
        return flat;
    }

    // --Unflatten grid for restoring state
    public static int[][] unflattenGrid(int[] flat) {
        if (flat == null || flat.length != 81) return new int[9][9];
        int[][] grid = new int[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                grid[i][j] = flat[i * 9 + j];
            }
        }
        return grid;
    }

    // --For Debugging: Check if all buttons are mapped correctly
    public static boolean validateAllButtons(Context context) {
        int validButtons = 0;
        int totalButtons = 0;

        for (int i = 1; i <= 89; i++) {
            if (i % 10 == 0) continue;
            totalButtons++;

            int resID = context.getResources().getIdentifier("button" + i, "id", context.getPackageName());
            if (resID != 0) {
                Button button = ((Activity)context).findViewById(resID);
                if (button != null) {
                    int[] coords = getGridCoordinatesFromButton(button);
                    if (coords != null) {
                        validButtons++;
                    }
                }
            }
        }

        Log.d(TAG, "Button validation: " + validButtons + "/" + totalButtons + " buttons mapped correctly");
        return validButtons == 81; // Should have exactly 81 valid buttons for 9x9 grid
    }

    // --For Debugging: Test method to verify the coordinate calculation for key buttons
    public static void testCoordinateCalculation() {
        Log.d(TAG, "=== COORDINATE CALCULATION VERIFICATION ===");

        // Test key button numbers to verify the mapping
        int[][] testCases = {
                {1, 0, 0},   // Button1 -> Grid[0][0]
                {9, 0, 8},   // Button9 -> Grid[0][8]
                {11, 1, 0},  // Button11 -> Grid[1][0] (skipped button10)
                {19, 1, 8},  // Button19 -> Grid[1][8]
                {21, 2, 0},  // Button21 -> Grid[2][0] (skipped button20)
                {29, 2, 8},  // Button29 -> Grid[2][8]
                {31, 3, 0},  // Button31 -> Grid[3][0] (skipped button30)
                {81, 8, 0},  // Button81 -> Grid[8][0]
                {89, 8, 8}   // Button89 -> Grid[8][8]
        };

        for (int[] testCase : testCases) {
            int buttonNum = testCase[0];
            int expectedRow = testCase[1];
            int expectedCol = testCase[2];

            int skippedButtons = buttonNum / 10;
            int actualPosition = buttonNum - skippedButtons;
            int row = (actualPosition - 1) / 9;
            int col = (actualPosition - 1) % 9;

            boolean correct = (row == expectedRow && col == expectedCol);
            Log.d(TAG, String.format("Button%d -> Grid[%d][%d] %s (skipped:%d, actualPos:%d)",
                    buttonNum, row, col, correct ? "✓" : "✗", skippedButtons, actualPosition));
        }
    }

    // For Debugging: Enhanced debug method with grid visualization
    public static void debugFullButtonMapping(Context context) {
        Log.d(TAG, "=== COMPREHENSIVE BUTTON MAPPING DEBUG ===");

        int validMappings = 0;
        int[][] buttonGrid = new int[9][9]; // Store button numbers for each grid position
        String[][] textGrid = new String[9][9]; // Store button text for each grid position

        // Test all buttons 1-89
        for (int buttonNum = 1; buttonNum <= 89; buttonNum++) {
            if (buttonNum % 10 == 0) {
                Log.d(TAG, "Button" + buttonNum + ": SKIPPED (every 10th button)");
                continue;
            }

            try {
                int resID = context.getResources().getIdentifier("button" + buttonNum, "id", context.getPackageName());
                if (resID != 0) {
                    Button button = ((Activity)context).findViewById(resID);
                    if (button != null) {
                        int[] coords = getGridCoordinatesFromButton(button);
                        if (coords != null) {
                            String text = button.getText().toString();
                            int row = coords[0];
                            int col = coords[1];

                            buttonGrid[row][col] = buttonNum;
                            textGrid[row][col] = text.isEmpty() ? "empty" : text;
                            validMappings++;

                            Log.d(TAG, String.format("Button%d -> Grid[%d][%d] Text: '%s'",
                                    buttonNum, row, col, text));
                        } else {
                            Log.w(TAG, "Button" + buttonNum + ": NO MAPPING");
                        }
                    } else {
                        Log.w(TAG, "Button" + buttonNum + ": NOT FOUND IN LAYOUT");
                    }
                } else {
                    Log.w(TAG, "Button" + buttonNum + ": NO RESOURCE ID");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error debugging button" + buttonNum, e);
            }
        }

        Log.d(TAG, "Valid mappings found: " + validMappings + "/81");

        // Display the grid with button numbers
        Log.d(TAG, "=== BUTTON NUMBER GRID LAYOUT ===");
        StringBuilder gridDisplay = new StringBuilder();
        for (int row = 0; row < 9; row++) {
            StringBuilder rowStr = new StringBuilder();
            for (int col = 0; col < 9; col++) {
                rowStr.append(String.format("%3d", buttonGrid[row][col]));
            }
            gridDisplay.append("Row ").append(row).append(": ").append(rowStr.toString()).append("\n");
        }
        Log.d(TAG, gridDisplay.toString());

        // Display the grid with current text values
        Log.d(TAG, "=== CURRENT TEXT VALUES GRID ===");
        StringBuilder textDisplay = new StringBuilder();
        for (int row = 0; row < 9; row++) {
            StringBuilder rowStr = new StringBuilder();
            for (int col = 0; col < 9; col++) {
                String text = textGrid[row][col];
                rowStr.append(String.format("%6s", text));
            }
            textDisplay.append("Row ").append(row).append(": ").append(rowStr.toString()).append("\n");
        }
        Log.d(TAG, textDisplay.toString());
    }
}