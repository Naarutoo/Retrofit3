package com.example.retrofit3;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
private ImageView mIvGallery;
private Button mBtnOpenGallery;
private  Button mBtnUploadImage;
private  String imagePath;


/*
open gallery Activity and capture the result .. Result is nothing but selectd image from gallery.
*/

private ActivityResultLauncher<Intent> resultFromGalleryActivity = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {             // image is present in result           uri is unique code for selected image
                Uri selectedImageUri = result.getData().getData();// result .getData will give us Intent & inside that intent we will get uri named getdata
            // now i want to set this image to my imageview so for that we have to convert this uri into inputStream
                try {
                    InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);    //so from uri we have to generate a stream of data
                    mIvGallery.setImageBitmap(BitmapFactory.decodeStream(inputStream));     //from stream of data generate bitmap
                getPathFromUri(selectedImageUri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
);

    private Cursor getPathFromUri(Uri selectedUri) {         //get the actual path of image from the selected Uri
        String[] filePath = {MediaStore.Images.Media.DATA};
        Cursor c = getContentResolver().query(selectedUri, filePath,
                null, null, null);
        c.moveToFirst();
        int columnIndex = c.getColumnIndex(filePath[0]);
        imagePath = c.getString(columnIndex);
        return c;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    initview();
    }

    private void initview() {
    mIvGallery = findViewById(R.id.imageView);
    mBtnOpenGallery = findViewById(R.id.btnGallery);
    mBtnUploadImage = findViewById(R.id.btnUpload);
  mBtnOpenGallery.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
          if (isPermissionGranted()){
              openGallery();
          }
          else{
              requestPermission();
          }
      }
  });

  /*1 got the imgae uri
  * 2 from the uri we got actual path
  * 3 from the path , generate a file */
  mBtnUploadImage.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
          ApiService apiService = Network.getInstance().create(ApiService.class);
          File file = new File(imagePath);
          RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"),file);   // here image repersents that i am uploading an image
          MultipartBody.Part part = MultipartBody.Part.createFormData("image",file.getName(),requestBody);           // here image repersents key of image
      apiService.uploadImage(part).enqueue(new Callback<ResponseDTO>() {
          @Override
          public void onResponse(Call<ResponseDTO> call, Response<ResponseDTO> response) {

          }

          @Override
          public void onFailure(Call<ResponseDTO> call, Throwable t) {

          }
      });
      }
  });
    }

    private void requestPermission() {
        String []permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(MainActivity.this,permissions,101);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);      // can copy this line
   resultFromGalleryActivity.launch(intent);
    }



    private boolean isPermissionGranted(){
        return ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults[0]==PackageManager.PERMISSION_GRANTED){
            openGallery();
        }
        else{
            Toast.makeText(MainActivity.this,"Permission Denied",Toast.LENGTH_SHORT).show();
        }
    }
}