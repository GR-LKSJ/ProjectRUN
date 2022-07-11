package com.example.walking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class IMGUploadActivity extends AppCompatActivity{
    private ImageView imageView1;
    private final int GET_IMAGE_FOR_IMAGEVIEW = 201;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        imageView1= findViewById(R.id.imageView1);

        imageView1.setOnClickListener(new View.OnClickListener(){
            @Override
                public void onClick(View view){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GET_IMAGE_FOR_IMAGEVIEW);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultcode, Intent data){
        Uri selectedImageUri;
        RequestOptions option1 = new RequestOptions().centerCrop();

        super.onActivityResult(requestCode, resultcode, data);
        if(resultcode == RESULT_OK && data != null && data.getData() != null){
            switch(requestCode){
                case GET_IMAGE_FOR_IMAGEVIEW:
                    selectedImageUri = data.getData();
                    Glide.with(getApplicationContext()).load(selectedImageUri).apply(option1).into(imageView1);
                    break;
            }
        }
    }
}
