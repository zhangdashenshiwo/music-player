package com.example.zhang.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zhang.myapplication.utils.Constant;
import com.example.zhang.myapplication.utils.Utils;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {


    /**
     * 声明变量
     */
    //定义搜索图标
    private ImageView myimageView;

    //定义加载动画
    private AVLoadingIndicatorView myavi;

    //定义搜索标题
    private TextView mtextView;

    //定义文件列表
    LinkedList <File> musicList;

    /**
     *  Handler传递消息
     */
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){

        @Override
        /**
         * 创建函数传递消息
         */
        public void handleMessage(Message message){
            /**
             * 搜索完成
             */
            //隐藏进度条
            myavi.hide();

            //显示搜索按钮
            myimageView.setVisibility(View.VISIBLE);

            //弹出搜索到歌曲数量
            Toast.makeText(SearchActivity.this,"共搜索到"+musicList.size()+"歌曲",Toast.LENGTH_LONG).show();

            /**
             * 创建子线程
             */
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //自定义的歌曲储存方法
                    Utils.saveBeanToSp(SearchActivity.this,musicList,Constant.MUSICLISTKEY);
                }
            }).start();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        /**
         * 创建对向
         */

        //搜索图标
        myimageView = findViewById(R.id.searchMusicActivity_searchImageView);

        //进度条
        myavi = findViewById(R.id.searchMusicActivity_loadingView);

        //搜索文字显示
        mtextView = findViewById(R.id.searchMusicActivity_textView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            /**
             * 检查该权限是否已经获取
             * 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
             */
            List<String> permissionList = new ArrayList<>();
            if (ContextCompat.checkSelfPermission(this, Manifest.
                    permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.
                    permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(Manifest.permission.READ_PHONE_STATE);
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.
                    permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (!permissionList.isEmpty()) {
                String[] permissions = permissionList.toArray(new String[permissionList.size()]);
                ActivityCompat.requestPermissions(this, permissions, 1);
            } else {

            }
        }
    }

    /**
     * 左上返回按钮
     */
    public void back(View view) {
        Intent intent = new Intent(SearchActivity.this, MusicListActivity.class);
        startActivity(intent);
        this.finish();
        overridePendingTransition(R.anim.pre_in, R.anim.pre_out);
    }

    /**
     * 搜索按钮(图片)点击事件
     */
    public void search(View view) {


        //显示搜索图
        myimageView.setVisibility(View.GONE);


        //显示进度条
        myavi.setVisibility(View.VISIBLE);
        myavi.show();

        //开启线程进行歌曲的搜索
        new Thread(new Runnable() {

            @Override
            public void run() {

                //储存歌曲位置
                musicList = new LinkedList<>();

                //   /storage/emulated/0目录就是机身存储的外部存储路径
                File file = new File("/storage/emulated/0");

                //遍历根目录
                if(file.exists()) {

                    //存储File路径
                    LinkedList<File> list = new LinkedList<>();

                    //创建对象
                    File [] file1 = file.listFiles();

                    //将搜索到的列表文件名作为final类型赋值给file2
                    for(final File file2 : file1) {

                        //isDirectory()是检查一个对象是否是文件夹
                        if(file2.isDirectory()){

                            //储存在list中
                            list.add(file2);
                        } else {

                            //toLowerCase() 方法将字符串转换为小写
                            String string = file2.getName().toLowerCase();

                            //判断是否为音乐文件，固定音乐文件扩展名
                            if(string.endsWith(".mp3") || string.endsWith(".mpeg") || string.endsWith(".wma")
                                    || string.endsWith(".midi") || string.endsWith(".mpeg-4")|| string.endsWith(".flac")){

                                //剔除小于1MB的文件
                                if (file2.length() / (1024 * 1024) >= 1) {
                                    //将其加入音乐列表
                                    musicList.add(file2);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            //更新文本
                                            mtextView.setText(file2.getName());
                                        }
                                    });
                                }
                            }

                        }
                    }

                    /**
                     * 对上面储存到list中的文件夹的子目录进行查找、遍历
                     */
                    //定义变量
                    File subfile;

                    /**
                     * list中一直存在文件夹时
                     * 进行上面同样操纵
                     */
                    while(!list.isEmpty()){

                        //返回此列表的第一个元素
                        subfile = list.removeFirst();

                        file1 = subfile.listFiles();
                        for(final File file2 : file1){

                            if(file2.isDirectory()){

                                list.add(file2);
                            }else{

                                //toLowerCase() 方法将字符串转换为小写
                                String string = file2.getName().toLowerCase();
                                //判断是否为音乐文件，固定音乐文件扩展名
                                if(string.endsWith(".mp3") || string.endsWith(".mpeg") || string.endsWith(".wma")
                                        || string.endsWith(".midi") || string.endsWith(".mpeg-4")|| string.endsWith(".flac")){

                                    //剔除小于1MB的文件
                                    if (file2.length() / (1024 * 1024) >= 1) {
                                        //将其加入音乐列表
                                        musicList.add(file2);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                //更新文本
                                                mtextView.setText(file2.getName());
                                            }
                                        });
                                    }

                                }
                            }
                        }
                    }
                }
                //发生通知给主线程，表示搜索已完成
                Message msg = Message.obtain();
                SearchActivity.this.handler.sendMessage(msg);
            }
        }).start();
    }
}
