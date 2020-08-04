package com.example.zhang.myapplication.viewpage;


import android.content.Context;
import android.widget.ImageView;

public class ImageLoadFactory {

    private static ImageLoadFactory instence;

    public static ImageLoadFactory getInstence() {
        if (instence == null) {
            //关键字synchronized可以保证在同一时刻，
            // 只有一个线程可以执行某个方法或某个代码块，
            // 同时synchronized可以保证一个线程的变化可见（可见性）
            synchronized (ImageLoadFactory.class) {
                if (instence == null) {
                    instence = new ImageLoadFactory();
                }

            }
        }
        return instence;
    }

    private ImageLoadClient mClient;
    public void setImageClient(ImageLoadClient client) {
        mClient = client;
    }

    public void loadImage(ImageView imageView, Object obj, Context context) {
        mClient.loadImage(imageView, obj, context);
    }
}
