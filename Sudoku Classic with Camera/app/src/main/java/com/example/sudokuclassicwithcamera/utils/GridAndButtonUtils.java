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

    // Handle the sudoku digits
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

    // Handle the inputs for the buttons
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

    // Get input value from button ID
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

    // Validate input
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

    // Get grid coordinates from button using explicit mapping
    public static int[] getGridCoordinatesFromButton(Button button) {
        int[] coords = new int[2];
        String resourceName = "";

        try {
            resourceName = button.getResources().getResourceName(button.getId());
        } catch (Exception e) {
            return null;
        }

        // Extract button number from resource name
        try {
            String buttonNumStr = resourceName.substring(resourceName.lastIndexOf("button") + 6);
            int buttonNum = Integer.parseInt(buttonNumStr);

            Log.d(TAG, "Processing button" + buttonNum);

            // Map button number to grid coordinates (1-89 to 0-8,0-8)
            // Buttons 10, 20, 30, 40, 50, 60, 70, 80 are skipped in the layout!
            // So we need a custom mapping

            // The layout skips every 10th button (10,20,30,40,50,60,70,80,90)
            // Let's create a mapping table
            int[][] buttonToGridMap = createButtonToGridMapping();

            // Find the button in our mapping
            for (int row = 0; row < 9; row++) {
                for (int col = 0; col < 9; col++) {
                    if (buttonToGridMap[row][col] == buttonNum) {
                        coords[0] = row;
                        coords[1] = col;
                        Log.d(TAG, String.format("Button%d -> Grid[%d][%d]", buttonNum, row, col));
                        return coords;
                    }
                }
            }

            Log.d(TAG, "Button" + buttonNum + " not found in mapping");
            return null;
        } catch (NumberFormatException e) {
            return null; // Not a grid button
        }
    }

    // Create mapping for our specific layout (buttons 1-89 with gaps)
    private static int[][] createButtonToGridMapping() {
        int[][] mapping = new int[9][9];
        int buttonIndex = 1;

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                mapping[row][col] = buttonIndex;
                buttonIndex++;

                // Skip every 10th button (your layout has gaps)
                if (buttonIndex % 10 == 0) {
                    buttonIndex++;
                }
            }
        }
        return mapping;
    }

    // Reset button to default appearance
    private static void resetButtonAppearance(Button button, Context context) {
        button.setTextSize(20);
        context.getTheme().resolveAttribute(android.R.attr.colorForeground, typedValue, true);
        context.getTheme().resolveAttribute(android.R.attr.colorBackground, typedValue1, true);
        int colorForeground = typedValue.data;
        int colorBackground = typedValue1.data;

        button.setTextColor(colorForeground);
        button.setBackgroundColor(colorBackground);
    }

    // Reset the grid
    public static void resetGrid(int grid[][], Context context, Button lastClickedButton) {
        try {
            for (int i = 1; i <= 89; i++) {
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

    // Print the grid
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

    // Get the grid input from UI
    public static void getGridInput(int grid[][], Context context) {
        if (grid == null) return;

        try {
            for (int i = 1; i <= 89; i++) {
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

    // Set the grid output to UI
    public static void setGridOutput(int grid[][], Context context, String tag) {
        if (grid == null) return;

        try {
            for (int i = 1; i <= 89; i++) {
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

    // Check if the grid contains any zero
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

    // Compare two grids
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

    // Copy grid
    public static int[][] copyGrid(int[][] source) {
        if (source == null) return null;
        int[][] copy = new int[9][9];
        for (int i = 0; i < 9; i++) {
            System.arraycopy(source[i], 0, copy[i], 0, 9);
        }
        return copy;
    }

    // Flatten grid for saving state
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

    // Unflatten grid for restoring state
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

    // For Debugging
    public static void debugFullButtonMapping(Context context) {
        Log.d(TAG, "=== FULL BUTTON MAPPING DEBUG ===");

        int[][] mapping = createButtonToGridMapping();

        for (int row = 0; row < 9; row++) {
            StringBuilder rowStr = new StringBuilder();
            for (int col = 0; col < 9; col++) {
                int buttonNum = mapping[row][col];
                rowStr.append(String.format("B%d ", buttonNum));
            }
            Log.d(TAG, "Row " + row + ": " + rowStr.toString());
        }

        // Check last 3x3 specifically
        Log.d(TAG, "=== LAST 3x3 BUTTONS ===");
        for (int row = 6; row < 9; row++) {
            StringBuilder rowStr = new StringBuilder();
            for (int col = 6; col < 9; col++) {
                int buttonNum = mapping[row][col];
                int resID = context.getResources().getIdentifier("button" + buttonNum, "id", context.getPackageName());
                Button button = ((Activity)context).findViewById(resID);
                String text = button != null ? button.getText().toString() : "NULL";
                rowStr.append(String.format("B%d('%s') ", buttonNum, text));
            }
            Log.d(TAG, "Row " + row + ": " + rowStr.toString());
        }
    }
}