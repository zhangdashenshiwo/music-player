package com.example.zhang.myapplication;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ToxicBakery.viewpager.transforms.ZoomInTransformer;
import com.example.zhang.myapplication.utils.ActivityManager;
import com.example.zhang.myapplication.utils.Constant;
import com.example.zhang.myapplication.utils.ParseTime;
import com.example.zhang.myapplication.utils.Utils;
import com.zcy.rotateimageview.RotateImageView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import me.zhengken.lyricview.LyricView;

public class PlayActivity extends SuperActivity implements View.OnClickListener {

    //RotateImageView 实现旋转的ImageView
    private RotateImageView mrotateImageView;

    private MusicLine musicLine;

    //背景
    private static ImageView background;

    //标题与作者
    private static TextView music_title,music_artist;

    private static int playing;
    private static LinkedList<File> musiclist;

    //通过自定义View实现的LyricView歌词显示控件
    static LyricView mLyricView = null;

    private static TextView endTime;
    private static TextView startTime;


    //通过ServiceConnection 获取Service
    private ServiceConnection mycon;

    //声明中间人
    private static PlayService.MyBinder myBinder;

    //定义int类型，用于判断是否在播放
    private int isPlaying = 0;

    //实现个性化seekbar
    static SeekBar mseekBar = null;


    ImageView playButton = null;

    //定义view数组
    private List <View> mviewList;

    //定义对应的viewPager
    private ViewPager mviewPager;

    private View view1 , view2;

    private ImageView mylist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        //获取歌曲列表
        musiclist = Utils.getBeanFromSp(PlayActivity.this,Constant.MUSICLISTKEY);

        //得到正在播放的序号
        playing = Utils.getInt(PlayActivity.this,Constant.PLAYINGNUM,0);

        //寻找相应的id接口
        background = findViewById(R.id.playActivity_background);
        music_title = findViewById(R.id.playActivity_music_title);
        music_artist = findViewById(R.id.playActivity_music_artist);

        //示波器显示
        mviewPager = findViewById(R.id.container);

        //将歌词和旋转图片存入mViewList
        //LayoutInflater当XML布局资源被解析并转换成View对象时会用到
        LayoutInflater inflater = getLayoutInflater();

        //第一个传入的参数resourse是你想要加载的布局资源。
        //第二个传入的参数是指当前载入的视图要将要放入在层级结构中的根视图
        view1 = inflater.inflate(R.layout.rotateimageview,null);
        view2 = inflater.inflate(R.layout.lyricview_layout,null);

        // 将要分页显示的View装入数组中
        mviewList = new ArrayList<>();

        mviewList.add(view1);
        mviewList.add(view2);

        //设置适配器
        PagerAdapter mpagerAdapter = new PagerAdapter() {

            @Override
            public int getCount() {
                return mviewList.size();
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
                return view == o;
            }

            @Override
            public void destroyItem(ViewGroup container, int position,
                                    Object object) {
                container.removeView(mviewList.get(position));
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(mviewList.get(position));
                return mviewList.get(position);
            }
        } ;

        //传入mpagerAdapter参数，用于生成返回的页面
        mviewPager.setAdapter(mpagerAdapter);

        //实现切换效果
        mviewPager.setPageTransformer(true,new ZoomInTransformer());

        //个性化进度条
        mseekBar = findViewById(R.id.music_seek_bar);

        //获取歌词，显示界面为view1
        mLyricView = mviewList.get(1).findViewById(R.id.playActivity_lyric_view);

        //歌词装填
        File f = songToLrc(musiclist.get(playing));
        mLyricView.setLyricFile(f);


        //设置按钮事件
        findViewById(R.id.playActivity_preSong).setOnClickListener(this);
        findViewById(R.id.playActivity_pauseSong).setOnClickListener(this);
        findViewById(R.id.playActivity_nextSong).setOnClickListener(this);
        findViewById(R.id.liebiao).setOnClickListener(this);


        playButton = findViewById(R.id.playActivity_pauseSong);

        //结束时间设置
        endTime = findViewById(R.id.end_time);

        //开始时间设置
        startTime = findViewById(R.id.start_time);

        //示波器
        musicLine = mviewList.get(0).findViewById(R.id.shiboqi_imagevie);

        //旋转view的设定
        mrotateImageView = mviewList.get(0).findViewById(R.id.rotate_imageview);

        //速度
        mrotateImageView.setSpeed(70);

        //停止旋转
        mrotateImageView.setRotate(false);

        //设定服务意图
        Intent intent = new Intent(PlayActivity.this, PlayService.class);

        //服务开启，获取服务内的功能，存储于muBinder中
        mycon = new ServiceConnection(){

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {

                myBinder = (PlayService.MyBinder) service;

            }

            //服务断开
            @Override
            public void onServiceDisconnected(ComponentName name) {

            }

        };

        // 绑定服务,参数三确定服务需要被销毁
        bindService(intent, mycon, BIND_AUTO_CREATE);

        //给歌词设置监听器(用于监听用户调整歌词位置)
        mLyricView.setOnPlayerClickListener(new LyricView.OnPlayerClickListener() {
            @Override
            public void onPlayerClicked(final long progress, String content) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //防止抖动，得先暂停
                        myBinder.pause();

                        //设定播放进度条的位置
                        mseekBar.setProgress((int) progress);

                        //将服务内的正在播放的音乐滑动至当前进度
                        myBinder.updataMediaplay((int) progress);

                        //设定歌词的进度位置
                        mLyricView.setCurrentTimeMillis(progress);

                        //歌曲继续播放
                        myBinder.start();

                        //设定为暂停按钮(现在正在播放)
                        playButton.setImageResource(R.drawable.btn_pause_selector);

                        //歌曲正在播放标志(用于按钮图标的设定，如暂停与播放按钮)
                        isPlaying = 1;

                        //旋转图片设定旋转状态
                        mrotateImageView.setRotate(true);
                    }
                });
            }
        });

        /**
         * 实现个性化seekbar(可拖动部件)
         */
        mseekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            //当拖动条发生变化时调用该方法
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            //当用户开始滑动滑块时调用该方法（即按下鼠调用一次)
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            //当用户结束对滑块滑动时,调用该方法（即松开鼠标）
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                //防止抖动，先暂停
                myBinder.pause();

                //设置播放进度条的位置
                seekBar.setProgress(seekBar.getProgress());

                //将服务内的正在播放的音乐滑动至当前进度
                myBinder.updataMediaplay(seekBar.getProgress());

                //设定歌词的进度位置
                mLyricView.setCurrentTimeMillis(seekBar.getProgress());

                //歌曲继续播放
                myBinder.start();

                //设定为暂停按钮(现在正在播放)
                playButton.setImageResource(R.drawable.btn_pause_selector);

                //歌曲正在播放标志(用于按钮图标的设定，如暂停与播放按钮)
                isPlaying = 1;

                //旋转图片设定旋转状态
                mrotateImageView.setRotate(true);


            }
        });

        /**
         * 播放模式的设定
         */
        final ImageView modeImage = findViewById(R.id.btn_mode);

        //获取粗存的当前的播放模式代表值
        final int mode = Utils.getInt(PlayActivity.this, Constant.MODE, 0);

        switch (mode) {

            case 0:
                //全部循环
                modeImage.setImageResource(R.drawable.btn_all_repeat_selector);
                break;
            case 1:
                //单曲循环
                modeImage.setImageResource(R.drawable.btn_one_repeat_selector);
                break;
            case 2:
                //随机播放
                modeImage.setImageResource(R.drawable.btn_shuffle_selector);
                break;
        }

        //模式按钮监听器设定
        modeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //获取当前模式
                int mode1 = Utils.getInt(PlayActivity.this, Constant.MODE, 0);

                //下一个模式
                mode1++;
                mode1 %= 3;

                //存储SP里
                Utils.putInt(PlayActivity.this, Constant.MODE, mode1);

                //图片更换
                switch (mode1) {

                    //全部循环更改图标并弹出“全部循环”信息
                    case 0:
                        Toast.makeText(PlayActivity.this,"全部循环",Toast.LENGTH_LONG).show();
                        modeImage.setImageResource(R.drawable.btn_all_repeat_selector);
                        break;

                    //单曲循环更改图标并弹出“单曲循环”信息
                    case 1:
                        Toast.makeText(PlayActivity.this,"单曲循环",Toast.LENGTH_LONG).show();
                        modeImage.setImageResource(R.drawable.btn_one_repeat_selector);
                        break;

                    //随机播放更改图标并弹出“随机播放”信息
                    case 2:
                        Toast.makeText(PlayActivity.this,"随机播放",Toast.LENGTH_LONG).show();
                        modeImage.setImageResource(R.drawable.btn_shuffle_selector);
                        break;
                }
            }
        });

        //更新歌曲名字，歌曲背景，歌手信息
        upTextViewData();

        //将此活动加入总活动管理
        ActivityManager.getInstance().addActivity(this);
    }


    /**
     * 点击事件处理
     * @param v
     */
    @Override
    public void onClick(View v) {
         Timer timer = new Timer();

        //设置定时器
        TimerTask timerTask =  new TimerTask() {
            @Override
            public void run() {
                musicLine.onStart();
            }
        };
        switch (v.getId()) {

            case R.id.playActivity_preSong:
                //点击了上一首按钮
                //歌曲设定上一首
                myBinder.pre();
                break;

            case R.id.playActivity_pauseSong:
                //点击了暂停，播放按钮
                //0表示没有播放，点击播放后将图标置成暂停状态
                if (isPlaying == 0) {

                    //播放状态置为1
                    isPlaying = 1;

                    //开始播放
                    myBinder.start();

                    //更改图标样式
                    playButton.setImageResource(R.drawable.btn_pause_selector);

                    //开始转动
                    mrotateImageView.setRotate(true);

                    timer.schedule(timerTask,0,100);



                } else {

                    //播放状态置为0
                    isPlaying = 0;

                    //暂停
                    myBinder.pause();

                    //图标更改为暂停图标形式
                    playButton.setImageResource(R.drawable.btn_play_selector);

                    //转动停止，图片静止
                    mrotateImageView.setRotate(false);


                }
                break;

            case R.id.playActivity_nextSong:

                //点击了下一首按钮
                //歌曲设定下一首
                myBinder.next();
                break;

            case R.id.liebiao:

                //切换到列表页面
                Intent intent = new Intent(PlayActivity.this,MusicListActivity.class);
                startActivity(intent);
                break;

            default:
                break;
        }
    }

    //在Activity销毁时,调用解绑服务方法,
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //解绑服务
        this.unbindService(mycon);
    }

    /**
     * 返回按钮按下
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PlayActivity.this, MusicListActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.pre_in, R.anim.pre_out);
        //        super.onBackPressed();
    }

    /**
     * 左上角返回按钮按下
     * @param view
     */
    public void back(View view) {
        onBackPressed();
    }



    //接受数据
    @SuppressLint("HandlerLeak")
    public static Handler hander = new Handler(){
        @Override
        public void handleMessage(Message message){

            //更新进度
            if (message.what == 0 ){

                //获取来自PlayService的数据,在PlayService中已经给message.what赋值为0时传递歌曲时间，歌曲播放当前时间
                Bundle data = message.getData();

                //获取歌曲总时间赋值给duration
                int duration = data.getInt("duration");

                //获取歌曲播放当前时间赋值给currentPosition，1024字节为1秒
                int currentPosition = data.getInt("currentPosition");

                //设定进度条的位置
                mseekBar.setProgress(currentPosition);

                /**
                 *  ParseTime为自定义的时间返回表现形式
                 */
                //设定当前播放的位置时间（歌曲已播放了多久）
                startTime.setText(ParseTime.msToString(currentPosition));


                //设定歌曲结束时间(即歌曲有多少分钟)
                endTime.setText(ParseTime.msToString(duration));

                //设定进度条最大进度
                mseekBar.setMax(duration);

                //歌词进度
                mLyricView.setCurrentTimeMillis(currentPosition);


                //判断是否到达结尾，到达的话进入下一首
                if (duration - 2000 <= currentPosition) {

                    //调用服务内的下一首歌曲
                    myBinder.next();
                }

                //在PlayService给message.what赋值为1代表传递即将播放新的歌曲的序号
            }else if(message.what == 1){
                //更新正在播放的位置(list)
                //切换歌曲
                playing = message.getData().getInt(Constant.PLAYINGNUM);

                //设定新歌词
                mLyricView.setLyricFile(songToLrc(musiclist.get(playing)));

                //更新页面信息(歌曲名字，歌曲背景，歌手信息)
                upTextViewData();
            }

        }
    };

    /**
     * 根据歌曲文件，该后缀为lrc
     */
    private static File songToLrc(File file){

        //将文件的绝对路径赋值给string
        String string = file.getAbsolutePath();

        //判断该播放歌曲的路径的文件扩展名是否为lrc
        string = string.substring(0,string.length()-3) + "lrc";

        //创建歌词文件对象，打开歌词文件，并返回其变量
        File f =new File(string);
        return f;
    }

    /**
     * 更新歌曲名字，歌曲背景，歌手信息
     */
    private static void upTextViewData(){

        //将正在播放的歌曲名字赋值给String 类型的song_name
        String song_name ;

        String name = musiclist.get(playing).getName();

        String n = musiclist.get(playing).getName();


        int m = n.indexOf('-');

        //显示歌手
        if(m != -1){
            song_name = n.substring(0,m-1);

            name = n.substring(m+2,name.length()-4);
        }else{
            song_name = "未知歌手";
            name = n.substring(0,name.length()-4);
        }



        //显示歌名
        music_title.setText(name);

        //显示歌手名
        music_artist.setText(song_name);

    }

    /**
     * 用于音乐列表点击了当前非正在播放的歌曲，曲需要将其歌切换
     *
     */
    @Override
    protected void onRestart(){
        super.onRestart();

        //获取播放歌曲在列表中的位置
        int anInt = Utils.getInt(this, Constant.PLAYINGNUM, 0);

        //如果选定歌曲和当前不一样，需要进行更换
        if(anInt != playing) {
            playing = anInt;

            //将当前更改的歌曲序号储存起来
            Utils.putInt(PlayActivity.this,Constant.PLAYINGNUM,playing);

            if (myBinder != null){
                try{
                    myBinder.playNumSong(playing);
                }catch (IOException e){

                }
            }
        }
    }

}
