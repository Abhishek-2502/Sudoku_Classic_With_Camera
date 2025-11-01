package com.example.sudokuclassicwithcamera.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.example.sudokuclassicwithcamera.Constants;

public class SudokuSolverUtils {

    private static final String TAG = "SudokuSolver";
    private static Random random = new Random();

    // --Solve Sudoku with backtracking
    public static boolean solveSudoku(int grid[][], int row, int col) {
        if (grid == null) return false;

        // If we've reached the end, the Sudoku is solved
        if (row == 9) {
            return true;
        }

        // Move to next row if at end of column
        if (col == 9) {
            return solveSudoku(grid, row + 1, 0);
        }

        // Skip already filled cells
        if (grid[row][col] != 0) {
            return solveSudoku(grid, row, col + 1);
        }

        // Try numbers 1-9 in random order for variety
        List<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= 9; i++) numbers.add(i);
        Collections.shuffle(numbers);

        for (int num : numbers) {
            if (isSafe(grid, row, col, num)) {
                grid[row][col] = num;
                if (solveSudoku(grid, row, col + 1)) {
                    return true;
                }
                grid[row][col] = 0; // backtrack
            }
        }

        return false;
    }

    // --Overloaded method for easier calling
    public static boolean solveSudoku(int[][] grid) {
        return grid != null && solveSudoku(grid, 0, 0);
    }

    // --Check if placing a number is safe
    public static boolean isSafe(int[][] grid, int row, int col, int num) {
        if (grid == null) return false;

        // Check row
        for (int x = 0; x < 9; x++) {
            if (grid[row][x] == num) {
                return false;
            }
        }

        // Check column
        for (int x = 0; x < 9; x++) {
            if (grid[x][col] == num) {
                return false;
            }
        }

        // Check 3x3 box
        int startRow = row - row % 3;
        int startCol = col - col % 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (grid[i + startRow][j + startCol] == num) {
                    return false;
                }
            }
        }

        return true;
    }

    // --Validate the input grid for conflicts
    public static boolean validateInput(int[][] grid, Context context) {
        if (grid == null) {
            Toast.makeText(context, "Grid is null", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check for conflicts in filled cells
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                int num = grid[i][j];
                if (num == 0) continue; // Skip empty cells

                if (!isSafeForValidation(grid, i, j, num)) {
                    String errorMessage = "Conflict at Row " + (i + 1) + ", Column " + (j + 1);
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        }
        return true;
    }

    // --Validation that ignores the current cell
    private static boolean isSafeForValidation(int[][] grid, int row, int col, int num) {
        // Check row (ignore current position)
        for (int x = 0; x < 9; x++) {
            if (x != col && grid[row][x] == num) {
                return false;
            }
        }

        // Check column (ignore current position)
        for (int x = 0; x < 9; x++) {
            if (x != row && grid[x][col] == num) {
                return false;
            }
        }

        // Check 3x3 box (ignore current position)
        int startRow = row - row % 3;
        int startCol = col - col % 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int currentRow = i + startRow;
                int currentCol = j + startCol;
                if ((currentRow != row || currentCol != col) && grid[currentRow][currentCol] == num) {
                    return false;
                }
            }
        }

        return true;
    }

    // --Generate random Sudoku puzzle
    public static int[][] generateRandomSudoku(String difficulty) {
        Log.d(TAG, "Generating " + difficulty + " Sudoku...");

        // Generate a complete solved Sudoku
        int[][] solved = generateSolvedSudoku();
        if (solved == null) {
            Log.e(TAG, "Failed to generate solved Sudoku, using fallback");
            return getDefaultSudoku();
        }

        Log.d(TAG, "Generated solved Sudoku:");
        GridAndButtonUtils.printGrid(solved);

        // Create puzzle by removing numbers
        int[][] puzzle = GridAndButtonUtils.copyGrid(solved);
        removeNumbersWithUniqueness(puzzle, difficulty);

        Log.d(TAG, "Generated puzzle:");
        GridAndButtonUtils.printGrid(puzzle);

        return puzzle;
    }

    // --Generate a complete solved Sudoku
    private static int[][] generateSolvedSudoku() {
        int[][] grid = new int[9][9];

        // Start with a valid base pattern and solve from there
        int[][] base = {
                {0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0}
        };

        // Fill diagonal boxes (they are independent)
        fillDiagonalBoxes(base);

        // Then solve the rest
        if (solveSudoku(base)) {
            return base;
        }
        return null;
    }

    // --Fill the diagonal 3x3 boxes
    private static void fillDiagonalBoxes(int[][] grid) {
        for (int box = 0; box < 3; box++) {
            fillBox(grid, box * 3, box * 3);
        }
    }

    // --Fill a 3x3 box with random numbers
    private static void fillBox(int[][] grid, int startRow, int startCol) {
        List<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= 9; i++) numbers.add(i);
        Collections.shuffle(numbers);

        int index = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                grid[startRow + i][startCol + j] = numbers.get(index++);
            }
        }
    }

    // --Remove numbers while ensuring unique solution
    private static void removeNumbersWithUniqueness(int[][] grid, String difficulty) {
        int cellsToRemove = getRemovalCount(difficulty);
        int removed = 0;
        int attempts = 0;
        int maxAttempts = 100;

        while (removed < cellsToRemove && attempts < maxAttempts) {
            int row = random.nextInt(9);
            int col = random.nextInt(9);

            if (grid[row][col] != 0) {
                int backup = grid[row][col];
                grid[row][col] = 0;

                // Check if solution remains unique
                int[][] gridCopy = GridAndButtonUtils.copyGrid(grid);
                if (countSolutions(gridCopy, 0, 0) == 1) {
                    removed++;
                } else {
                    // Restore if not unique
                    grid[row][col] = backup;
                }
            }
            attempts++;
        }

        Log.d(TAG, "Removed " + removed + " cells after " + attempts + " attempts");
    }

    // --Count number of solutions
    private static int countSolutions(int[][] grid, int row, int col) {
        if (row == 9) {
            return 1;
        }
        if (col == 9) {
            return countSolutions(grid, row + 1, 0);
        }
        if (grid[row][col] != 0) {
            return countSolutions(grid, row, col + 1);
        }

        int solutions = 0;
        for (int num = 1; num <= 9 && solutions < 2; num++) {
            if (isSafe(grid, row, col, num)) {
                grid[row][col] = num;
                solutions += countSolutions(grid, row, col + 1);
                grid[row][col] = 0;
            }
        }
        return solutions;
    }

    private static int getRemovalCount(String difficulty) {
        switch (difficulty.toLowerCase()) {
            case "easy":
                return Constants.easy;
            case "medium":
                return Constants.medium;
            case "hard":
                return Constants.hard;
            default:
                return Constants.easy;
        }
    }

    // --Fallback default Sudoku
    private static int[][] getDefaultSudoku() {
        return new int[][] {
                {5, 3, 0, 0, 7, 0, 0, 0, 0},
                {6, 0, 0, 1, 9, 5, 0, 0, 0},
                {0, 9, 8, 0, 0, 0, 0, 6, 0},
                {8, 0, 0, 0, 6, 0, 0, 0, 3},
                {4, 0, 0, 8, 0, 3, 0, 0, 1},
                {7, 0, 0, 0, 2, 0, 0, 0, 6},
                {0, 6, 0, 0, 0, 0, 2, 8, 0},
                {0, 0, 0, 4, 1, 9, 0, 0, 5},
                {0, 0, 0, 0, 8, 0, 0, 7, 9}
        };
    }
}