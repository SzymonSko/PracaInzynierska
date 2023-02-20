package com.example.inzynierka;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import java.io.ByteArrayOutputStream;

public class ScannerActivity extends AppCompatActivity {

    Button btnSelect, btnCamera, btnCount;
    ImageView target_image_view;
    TextView score, myImageViewText;
    int SELECT_IMAGE_CODE = 1, SELECT_CAMERA_CODE = 2;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        btnSelect = findViewById(R.id.btnSelect);
        btnCount = findViewById(R.id.btnCount);
        btnCamera = findViewById(R.id.btnCamera);
        target_image_view = findViewById(R.id.target_image_view);

        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(this));

        }
        final ProgressDialog progress = new ProgressDialog(this);


        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Pick Image"), SELECT_IMAGE_CODE);

            }
        });



        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, SELECT_CAMERA_CODE);

            }
        });
        btnCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    convertImage();
                    Python py = Python.getInstance();
                    PyObject pyobj2 = py.getModule("scanner");
                    PyObject obj2 = pyobj2.callAttr("check");
                    PyObject obj3 = pyobj2.callAttr("points");


                    progress.setTitle("Processing photo");
                    progress.setMessage("Please hold ...");
                    progress.show();


                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progress.cancel();
                           startActivity(new Intent(ScannerActivity.this, ScannedActivity.class));
                        }
                    }, 2000);
                }catch (Exception e){
                    Toast.makeText(ScannerActivity.this, "I Cant work with that image", Toast.LENGTH_LONG).show();
                }



            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == SELECT_IMAGE_CODE && resultCode == RESULT_OK && data != null) {
            myImageViewText = findViewById(R.id.myImageViewText);
            myImageViewText.setText("");

            final ProgressDialog progress = new ProgressDialog(this);
            progress.setTitle("Uploading Photo");
            progress.setMessage("Please hold ...");
            progress.show();



            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Uri uri = data.getData();
                    target_image_view.setImageURI(uri);

                    progress.cancel();
                }
            }, 2000);

        }
        if (requestCode == SELECT_CAMERA_CODE && resultCode == RESULT_OK && data != null) {
            myImageViewText = findViewById(R.id.myImageViewText);

            myImageViewText.setText("");

            final ProgressDialog progress = new ProgressDialog(this);
            progress.setTitle("Uploading Photo");
            progress.setMessage("Please hold ...");
            progress.show();



            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

            Bitmap image = (Bitmap) data.getExtras().get("data");
            target_image_view.setImageBitmap(image);
            score.setText("Points: ");

                    progress.cancel();
                }
            }, 2000);

        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    public void convertImage(){
        BitmapDrawable bd = (BitmapDrawable) target_image_view.getDrawable();
        Bitmap bitmap = bd.getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 1, stream);
        byte[] array = stream.toByteArray();
//        bitmap.recycle();
        Python py = Python.getInstance();
        PyObject pyobj = py.getModule("scanner");
        PyObject obj = pyobj.callAttr("process", array);

    }
}