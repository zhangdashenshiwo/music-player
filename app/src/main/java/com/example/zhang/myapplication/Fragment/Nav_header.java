package com.example.zhang.myapplication.Fragment;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.zhang.myapplication.R;
import com.example.zhang.myapplication.ui.RoundImageView;


public class Nav_header extends AppCompatActivity implements View.OnClickListener {

    private RoundImageView img_round;

    private ImageView image_choose;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_header_main);

        img_round =  findViewById(R.id.img_round);
        image_choose = findViewById(R.id.image);
        image_choose.setOnClickListener(this);


        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.qq_icon_1);

        img_round.setBitmap(bitmap);

        if (bitmap.isRecycled()) {
            bitmap.recycle();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image: {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        "image/*");
                startActivityForResult(intent, 0x1);
                break;
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (requestCode == 0x1 && resultCode == RESULT_OK) {
            if (data != null) {
                image_choose.setImageURI(data.getData());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
