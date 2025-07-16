package com.example.sudokuclassicwithcamera.utils;

import android.content.Context;
import android.widget.Toast;
import java.util.Random;

public class SudokuSolverUtils {

    /* Takes a partially filled-in grid and attempts to assign values to all unassigned locations
    in such a way to meet the requirements for Sudoku solution (non-duplication across rows, columns, and boxes) */
    public static boolean solveSudoku(int grid[][], int row, int col) {
        /*
        * DEFAULT SOLUTION
        1 2 3 4 5 6 7 8 9
        4 5 6 7 8 9 1 2 3
        7 8 9 1 2 3 4 5 6
        2 1 4 3 6 5 8 9 7
        3 6 5 8 9 7 2 1 4
        8 9 7 2 1 4 3 6 5
        5 3 1 6 4 2 9 7 8
        6 4 2 9 7 8 5 3 1
        9 7 8 5 3 1 6 4 2

        */
        // If we've reached the last cell, the Sudoku is solved
        if (row == 9 - 1 && col == 9) {
            return true;
        }

        // Move to the next row if the column becomes 9
        if (col == 9) {
            row++;
            col = 0;
        }

        // If the current cell is already filled, move to the next cell
        if (grid[row][col] != 0) {
            return solveSudoku(grid, row, col + 1);
        }

        // Try placing numbers from 1 to 9 in the current empty cell
        for (int num = 1; num <= 9; num++) {
            if (isSafe(grid, row, col, num)) {
                // Place the number in the grid
                grid[row][col] = num;

                // Recursively try solving the next cell
                if (solveSudoku(grid, row, col + 1)) {
                    return true;
                }

                // Backtrack if the number doesn't lead to a solution
                grid[row][col] = 0;
            }
        }

        // If no number works, trigger backtracking
        return false;
    }

    // Check if placing a number in a specific cell is safe
    public static boolean isSafe(int[][] grid, int row, int col, int num) {
        // Check the row (ignore the current position)
        for (int x = 0; x < 9; x++) {
            if (x != col && grid[row][x] == num) {
                return false;
            }
        }

        // Check the column (ignore the current position)
        for (int x = 0; x < 9; x++) {
            if (x != row && grid[x][col] == num) {
                return false;
            }
        }

        // Check the 3x3 subgrid (ignore the current position)
        int startRow = row - row % 3;
        int startCol = col - col % 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if ((i + startRow != row || j + startCol != col) && grid[i + startRow][j + startCol] == num) {
                    return false;
                }
            }
        }

        return true;
    }

    // Validate the input grid to check for conflicts
    public static boolean validateInput(int[][] grid, Context context) {
        // STEP 1: Check for valid numbers and existing conflicts
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                int num = grid[i][j];

                // STEP 2: Check if the initial number causes a conflict (ignore 0 as it's unassigned)
                if (num != 0 && !isSafe(grid, i, j, num)) {
                    // Show a detailed error message with row and column
                    String errorMessage = "Conflict at Row " + (i + 1) + ", Column " + (j + 1);
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        }
        return true;
    }

    // Generate a random Sudoku puzzle based on the specified difficulty
    public static int[][] generateRandomSudoku(String difficulty) {
        int[][] board = new int[9][9];
        Random random = new Random();

        int digitran = random.nextInt(9) + 1;
        int rowran = random.nextInt(9);
        int colran = random.nextInt(9);

        board[rowran][colran] = digitran;

        System.out.println("Filled Row: "+rowran+" Col: "+colran+" Digit: "+digitran);
        fillSudoku(board);

        System.out.println("Filled Sudoku: ");
        GridAndButtonUtils.printGrid(board);

        removeNumbers(board, difficulty);
        System.out.println("Removed Numbers: ");
        GridAndButtonUtils.printGrid(board);
        return board;
    }

    // Fill the Sudoku board using backtracking
    private static boolean fillSudoku(int[][] grid) {
        System.out.println("Utils: fillSudoku");
        GridAndButtonUtils.printGrid(grid);
        return solveSudoku(grid, 0, 0);
    }

    // Remove numbers from the filled Sudoku board based on difficulty
    private static void removeNumbers(int[][] board, String difficulty) {
        int count;
        switch (difficulty.toLowerCase()) {
            case "easy":
                count = 36; // Remove 36 numbers for an easy puzzle
                break;
            case "medium":
                count = 45; // Remove 45 numbers for a medium puzzle
                break;
            case "hard":
                count = 54; // Remove 54 numbers for a hard puzzle
                break;
            default:
                throw new IllegalArgumentException("Invalid difficulty level");
        }

        Random random = new Random();
        while (count != 0) {
            int i = random.nextInt(9);
            int j = random.nextInt(9);
            // If the cell is not already empty, remove the number
            if (board[i][j] != 0) {
                board[i][j] = 0; // Set the cell to empty
                count--;
            }
        }
    }
}
