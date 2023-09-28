package com.suhun.takepicture;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private String tag = MainActivity.class.getSimpleName();
    private ImageView img;
    private File saveDir;
    private ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == RESULT_OK){
                        Intent intent = result.getData();
                        Bundle bundle = intent.getExtras();
                        Bitmap bitmap = (Bitmap) bundle.get("data");
                        img.setImageBitmap(bitmap);
                    }
                }
            });
    private ActivityResultLauncher<Intent> takePicResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                    if(result.getResultCode() == RESULT_OK){
                        Bitmap bitmap = BitmapFactory.decodeFile(new File(saveDir + "/suhun.jpg").getAbsolutePath());
                        img.setImageBitmap(bitmap);
                    }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        if(checkUserPermissionAboutCamera()){
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 123);
        }else{
            initCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 123){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED
                && grantResults[2] == PackageManager.PERMISSION_GRANTED){
                initCamera();
            }else{
                finish();

            }
        }
    }
    private boolean checkUserPermissionAboutCamera(){
        boolean result = false;
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
            return true;
        }
        return result;
    }

    private void initCamera(){

    }

    private void initView(){
        img = findViewById(R.id.lid_img);
    }

    public void takePic1Fun(View view){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        resultLauncher.launch(intent);
    }

    public void takePic2Fun(View view){
        saveDir = Environment.getExternalStoragePublicDirectory("Hiskio");
        Uri uri = FileProvider.getUriForFile(this, getPackageName()+".fileprovider",
                new File(saveDir + "/suhun.jpg"));
        Log.d(tag, "-----Get uri from fileprovider" + uri + "-----");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        takePicResultLauncher.launch(intent);
    }
}