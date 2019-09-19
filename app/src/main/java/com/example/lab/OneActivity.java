package com.example.lab;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class OneActivity extends AppCompatActivity {

    ImageView image;
    Button savebtn;

    FirebaseStorage storage;
    StorageReference reference;

    private static final int PERMISSION_REQUEST_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Save Image");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        image = findViewById(R.id.image);
        savebtn = findViewById(R.id.save_btn);

        storage = FirebaseStorage.getInstance();
        reference = storage.getReferenceFromUrl("gs://ocula-ed5be.appspot.com/uploads").child("Ocula.jpeg");

        try {
            final File file = File.createTempFile("image", "jpeg");

            reference.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    image.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(OneActivity.this, "image failed to load", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        savebtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(OneActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                    Toast.makeText(OneActivity.this, "you should grant access", Toast.LENGTH_SHORT).show();
                    requestPermissions(new String[]{}, PERMISSION_REQUEST_CODE);
                    return;
                }else{

                    BitmapDrawable drawable = (BitmapDrawable)image.getDrawable();
                    Bitmap bitmap = drawable.getBitmap();

                    FileOutputStream outputStream = null;

                    File sdcard = Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES);
//                    File dir = new File(sdcard.getAbsolutePath() + "Ocula");
//                    dir.mkdir();
                    String filename = String.format("%d.jpeg", System.currentTimeMillis());
                    File outFile = new File(sdcard, filename);


                    Toast.makeText(OneActivity.this, "Image saved", Toast.LENGTH_SHORT).show();

                    try {

                        outputStream = new FileOutputStream(outFile);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                        outputStream.flush();
                        outputStream.close();

                        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        intent.setData(Uri.fromFile(outFile));
                        sendBroadcast(intent);

                    }catch (FileNotFoundException e){
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case PERMISSION_REQUEST_CODE:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "permission granted", Toast.LENGTH_SHORT).show();
                    //saveImage();
                }else {
                    Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
                }
            }
            break;

        }
    }


}
