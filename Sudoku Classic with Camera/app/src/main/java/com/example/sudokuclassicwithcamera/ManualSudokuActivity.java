package com.example.sudokuclassicwithcamera;

import static com.example.sudokuclassicwithcamera.RandomSudokuActivity.usergrid;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
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

import com.example.sudokuclassicwithcamera.ml.Model;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;

public class ManualSudokuActivity extends AppCompatActivity {

    // NOTE: Also add permission in Manifest
    private static final int CAMERA_REQUEST_CODE = 101;
    private static final int GALLERY_REQUEST_CODE = 100;
    private static final int CAMERA_PERMISSION_CODE = 200;
    private Bitmap img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Declaring Variables
        Button btsub;
        Button btr;
        ImageButton imgbtgal;
        ImageButton imgbtcam;


        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manual_sudoku);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        GridAndButtonUtils gridAndButtonUtils = new GridAndButtonUtils();
        SudokuSolverUtils sudokuSolverUtils = new SudokuSolverUtils();

        btsub = findViewById(R.id.buttons);
        btr = findViewById(R.id.buttonr);
        imgbtcam = findViewById(R.id.imageButton);
        imgbtgal = findViewById(R.id.imageButton2);

        // Main Logic for Button 1 to 89
        for (int i = 1; i <= 89; i++) {
            // Generate the button ID dynamically
            int resID = getResources().getIdentifier("button" + i, "id", getPackageName());
            if (resID != 0) {
                Button button = findViewById(resID);
                if (button != null) {
                    button.setOnClickListener(v -> {
                        gridAndButtonUtils.handleSudokuDigits(button,this,"ques");
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
                        if(gridAndButtonUtils.lastClickedButton==null){
                            Toast.makeText(this, "Please select a digit", Toast.LENGTH_SHORT).show();
                        }else{
                            gridAndButtonUtils.handleInputsButton(button,this);
                        }
                    });
                }
            }
        }

        // Reset
        btr.setOnClickListener(v -> {
            gridAndButtonUtils.resetGrid(usergrid,this);
        });

        //Main Logic for submit button
        btsub.setOnClickListener(v -> {
            gridAndButtonUtils.getGridInput(usergrid,this);

//            For Debugging
//            for(int row=0;row<9;row++){
//                for(int col=0;col<9;col++){
//                    System.out.println(row+" "+col+" "+grid[row][col]);
//                }
//            }

            if(sudokuSolverUtils.validateInput(usergrid,this)){
                if (sudokuSolverUtils.solveSudoku(usergrid, 0, 0)) {
                    gridAndButtonUtils.setGridOutput(usergrid,this,"enable");
                }
                else {
                    Toast.makeText(this, "No Solution exists", Toast.LENGTH_SHORT).show();
                }
            }

//            For Debugging
//            System.out.println("Output");
//            for(int row=0;row<9;row++){
//                for(int col=0;col<9;col++){
//                    System.out.println(row+" "+col+" "+grid[row][col]);
//                }
//            }

        });

        imgbtgal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_REQUEST_CODE);
            }
        });

        //Camera
        imgbtcam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(ManualSudokuActivity.this, android.Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Request camera permission
                    ActivityCompat.requestPermissions(ManualSudokuActivity.this,
                            new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
                } else {
                    // Open camera if permission is already granted
                    openCamera();
                }
            }
        });


    }

    // Open camera
    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
        }
    }

    // Handle activity results for both gallery and camera
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            // Handle image selection from gallery
            if (requestCode == GALLERY_REQUEST_CODE && data != null) {
                Uri selectedImageUri = data.getData();
//                imgView.setImageURI(selectedImageUri);  //Use if image view is used

                try {
                    img = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                    // Convert to ARGB_8888
                    img = img.copy(Bitmap.Config.ARGB_8888, true);
                    if(img==null){
                        Toast.makeText(ManualSudokuActivity.this, "Please select an image", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        mlModel();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // Handle image capture from camera
            else if (requestCode == CAMERA_REQUEST_CODE && data != null) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                if (imageBitmap != null) {
                    // Convert the captured image to ARGB_8888 format
                    img = imageBitmap.copy(Bitmap.Config.ARGB_8888, true);
//                    imgView.setImageBitmap(img);
                    if(img==null){
                        Toast.makeText(ManualSudokuActivity.this, "Please select an image", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        mlModel();
                    }
                }
            }
        }
    }

    // Handle runtime permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void mlModel(){


//                Default template
//                try {
//                    Model model = Model.newInstance(context);
//
//                    // Creates inputs for reference.
//                    TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 32, 32, 3}, DataType.FLOAT32);
//                    inputFeature0.loadBuffer(byteBuffer);
//
//                    // Runs model inference and gets result.
//                    Model.Outputs outputs = model.process(inputFeature0);
//                    TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
//
//                    // Releases model resources if no longer used.
//                    model.close();
//                } catch (IOException e) {
//                    // TODO Handle the exception
//                }

        if (img==null){
            Toast.makeText(ManualSudokuActivity.this, "Please select an image", Toast.LENGTH_SHORT).show();
            return;
        }
        img = Bitmap.createScaledBitmap(img, 32, 32, true);

        try {

            Model model = Model.newInstance(getApplicationContext());

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 32, 32, 3}, DataType.FLOAT32);

            TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
            tensorImage.load(img);
            ByteBuffer byteBuffer = tensorImage.getBuffer();

            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            Model.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            // Releases model resources if no longer used.
            model.close();

            //For Debugging
            int count=0;
            for(int i=0;i<80;i++){
                System.out.println(i+" "+outputFeature0.getFloatArray()[i]);
                count++;
            }
            System.out.println(count);

//            //For Debugging (While using TextView)
//            tv.setText("Data:\n"
//                    +outputFeature0.getFloatArray()[0]
//                    + "\n"+outputFeature0.getFloatArray()[1]
//                    + "\n"+outputFeature0.getFloatArray()[2]
//                    + "\n"+outputFeature0.getFloatArray()[3]
//                    + "\n"+outputFeature0.getFloatArray()[4]
//                    + "\n"+outputFeature0.getFloatArray()[5]
//                    + "\n"+outputFeature0.getFloatArray()[6]
//                    + "\n"+outputFeature0.getFloatArray()[7]
//                    + "\n"+outputFeature0.getFloatArray()[8]
//                    + "\n"+outputFeature0.getFloatArray()[9]
//                    + "\n"+outputFeature0.getFloatArray()[10]
//                    + "\n"+outputFeature0.getFloatArray()[11]
//                    + "\n"+outputFeature0.getFloatArray()[12]);

        } catch (IOException e) {
            // TODO Handle the exception
        }
    }
}