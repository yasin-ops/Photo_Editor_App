package com.muhammadyaseenfatimamazharsarfarz.photoeditorapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.dsphotoeditor.sdk.activity.DsPhotoEditorActivity;
import com.dsphotoeditor.sdk.utils.DsPhotoEditorConstants;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.muhammadyaseenfatimamazharsarfarz.photoeditorapp.databinding.ActivityMainBinding;

import java.io.ByteArrayOutputStream;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    int IMAGE_REQUEST_CODE = 45;
    int CAMERA_REQUEST_CODE = 14;
    int RESULT_CODE = 200;
    Animation bttone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        AdRequest adRequest = new AdRequest.Builder().build();
        binding.adViewMainActivity.loadAd(adRequest);
        bttone = AnimationUtils.loadAnimation(this, R.anim.bttone);
        binding.titlepage.startAnimation(bttone);


        binding.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, IMAGE_REQUEST_CODE);
            }

        });
        binding.cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 32);


                } else {
                    Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(camera, CAMERA_REQUEST_CODE);

                }
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == IMAGE_REQUEST_CODE) {

                if (data.getData() != null) {
                    Uri filePath = data.getData();
                    Intent dsPhotoEditorIntent = new Intent(this, DsPhotoEditorActivity.class);

                    dsPhotoEditorIntent.setData(filePath);
                    dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_OUTPUT_DIRECTORY, "Photo Editor");
                    Toast.makeText(this, data.getData().toString(), Toast.LENGTH_SHORT).show();
                    int[] toolsToHide = {DsPhotoEditorActivity.TOOL_ORIENTATION, DsPhotoEditorActivity.TOOL_CROP};
                    dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_TOOLS_TO_HIDE, toolsToHide);
                    startActivityForResult(dsPhotoEditorIntent, RESULT_CODE);

                }
            }
            if (requestCode == RESULT_CODE) {
                Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                Toasty.info(this, "Image Saved", Toasty.LENGTH_LONG).show();
                intent.setData(data.getData());
                startActivity(intent);
            } else if (requestCode == CAMERA_REQUEST_CODE) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                Uri uri = getImageUri(photo);
                Intent dsPhotoEditorIntent = new Intent(this, DsPhotoEditorActivity.class);

                dsPhotoEditorIntent.setData(uri);
                dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_OUTPUT_DIRECTORY, "Photo Editor");

                int[] toolsToHide = {DsPhotoEditorActivity.TOOL_ORIENTATION, DsPhotoEditorActivity.TOOL_CROP};
                dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_TOOLS_TO_HIDE, toolsToHide);
                startActivityForResult(dsPhotoEditorIntent, RESULT_CODE);


            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public Uri getImageUri(Bitmap bitmap) {

        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, arrayOutputStream);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "tittle", null);
        return Uri.parse(path);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
