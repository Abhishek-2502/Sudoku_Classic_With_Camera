package com.example.sudokuclassicwithcamera;

import android.app.Activity;
import android.content.Context;
import android.util.TypedValue;
import android.widget.Button;

public class GridAndButtonUtils {

    public Button lastClickedButton = null;
    // Resolve the color foreground from the current theme
    TypedValue typedValue = new TypedValue();
    TypedValue typedValue1 = new TypedValue();
    int colorForeground;
    int colorBackground;

    public void handleSudokuDigits(Button clickedButton, Context context,String tag) {
        // Reset the last clicked button if it's not null and is not the current button
        if (lastClickedButton != null && lastClickedButton != clickedButton) {
            lastClickedButton.setTextSize(20);

            // Resolve the color foreground from the current theme
            context.getTheme().resolveAttribute(android.R.attr.colorForeground, typedValue, true);
            context.getTheme().resolveAttribute(android.R.attr.colorBackground, typedValue1, true);
            colorForeground = typedValue.data;
            colorBackground = typedValue1.data;

            if(tag.equals("ans")){
                clickedButton.setTextColor(context.getResources().getColor(R.color.blue));
            }else{
                lastClickedButton.setTextColor(colorForeground);
            }
            lastClickedButton.setBackgroundColor(colorBackground);
        }

        // Change the color and size of the clicked button
        clickedButton.setTextSize(24);
        clickedButton.setBackgroundColor(context.getResources().getColor(R.color.bg_clicked));
        if(tag.equals("ans")){
            clickedButton.setTextColor(context.getResources().getColor(R.color.blue));

        }else{
            clickedButton.setTextColor(context.getResources().getColor(R.color.black));

        }

        // Update the lastClickedButton reference
        lastClickedButton = clickedButton;
    }

    public void handleInputsButton(Button clickedButton, Context context) {
        if (clickedButton.getId() == R.id.buttona) {
            lastClickedButton.setText("1");
        } else if (clickedButton.getId() == R.id.buttonb) {
            lastClickedButton.setText("2");
        } else if (clickedButton.getId() == R.id.buttonc) {
            lastClickedButton.setText("3");
        } else if (clickedButton.getId() == R.id.buttond) {
            lastClickedButton.setText("4");
        } else if (clickedButton.getId() == R.id.buttone) {
            lastClickedButton.setText("5");
        } else if (clickedButton.getId() == R.id.buttonf) {
            lastClickedButton.setText("6");
        } else if (clickedButton.getId() == R.id.buttong) {
            lastClickedButton.setText("7");
        } else if (clickedButton.getId() == R.id.buttonh) {
            lastClickedButton.setText("8");
        } else if (clickedButton.getId() == R.id.buttoni) {
            lastClickedButton.setText("9");
        } else if (clickedButton.getId() == R.id.buttonj) {
            lastClickedButton.setText(" ");
        }
    }

    public void resetGrid(int grid[][],Context context){
        for (int i = 1; i <= 89; i++) {
            // Generate the button ID dynamically
            int resID = context.getResources().getIdentifier("button" + i, "id", context.getPackageName());
            if (resID != 0) {
                Button button = ((Activity)context).findViewById(resID);
                button.setText("");
                context.getTheme().resolveAttribute(android.R.attr.colorForeground, typedValue, true);
                colorForeground = typedValue.data;
                button.setTextColor(colorForeground);
            }
        }
    }

    public void getGridInput(int grid[][],Context context) {
        int buttonIndex = 0;
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                buttonIndex++;
                if (buttonIndex % 10 == 0) {
                    buttonIndex++;
                }
//                System.out.println("Bi Input"+buttonIndex);

                int resID = context.getResources().getIdentifier("button" + buttonIndex, "id", context.getPackageName());
                if (resID != 0) {
                    Button button = ((Activity)context).findViewById(resID);
                    if (button != null) {
                        String btText = button.getText().toString();
                        try {
                            if(btText.equals("") || btText.equals(" ")){
                                grid[row][col] = 0;
                                continue;
                            }
                            grid[row][col] = Integer.parseInt(btText);
                        }
                        catch (NumberFormatException e) {
                            grid[row][col] = 0;
                        }
                    }
                }
            }
        }
    }

    public void setGridOutput(int grid[][],Context context,String tag){
        int buttonIndex = 0;
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                buttonIndex++;

                if (buttonIndex % 10 == 0) {
                    buttonIndex++;
                }

                // Check if button exists and update its text
                int resID = context.getResources().getIdentifier("button" + buttonIndex, "id", context.getPackageName());
                if (resID != 0) {
                    Button button = ((Activity)context).findViewById(resID);
                    if (button != null) {
                        int gridValue = grid[row][col];
                        if(gridValue==0){
                            button.setText("");
                            continue;
                        }
                        button.setText(String.valueOf(gridValue));
                        if(tag.equals("disable")){
                            button.setEnabled(false);
                        }
                    }
                }
            }
        }
    }

    public boolean noZeroGrid(int grid[][]) {
        for(int row=0;row<9;row++){
            for(int col=0;col<9;col++){
                if(grid[row][col]<1 || grid[row][col]>9){
                    return false;
                }
            }
        }
        return true;
    }

    public boolean compareGrid(int grid1[][],int grid2[][]){
        for(int row=0;row<9;row++){
            for(int col=0;col<9;col++){
                if(grid1[row][col]!=grid2[row][col]){
                    return false;
                }
            }
        }
        return true;
    }


}
