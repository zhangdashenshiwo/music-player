package com.example.zhang.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.zhang.myapplication.utils.ActivityManager;

public class SuperActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //注册SuperActivity类
        ActivityManager.getInstance().addActivity(this);
    }
}
