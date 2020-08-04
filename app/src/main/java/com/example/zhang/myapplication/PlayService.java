package com.example.zhang.myapplication;

import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;

import com.example.zhang.myapplication.utils.Constant;
import com.example.zhang.myapplication.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

public class PlayService extends Service {

    //声明music是MediaPlayer变量
    private MediaPlayer music;

    //当前播放的音乐位于list的位置
    private int playing;

    //音乐列表
    private LinkedList <File> musiclist;

    //定义中间人
    IBinder a = new MyBinder();

    //声明定时器，用于持续播放音乐，调整进度
    Timer mtimer = null;

    /**
     * 定义中间人对象，向外提供服务内的功能
     */
    public class MyBinder extends Binder {
        //开始播放
        public void start() {
            //调用歌曲开启方法
            PlayService.this.start();

            //更新时间条
            PlayService.this.updataSeekbar();
        }

        //暂停
        public void pause() {
            //调用暂停方法
            PlayService.this.pause();

            if(mtimer != null){
                //线程暂停
                mtimer.cancel();
            }
        }

        //更新歌曲
        public void updataMediaplay(int i) {
            PlayService.this.updataMediaplay(i);
        }

        //播放下一首歌
        public void next(){
            PlayService.this.next();
        }

        //播放上一首歌
        public void pre(){
            PlayService.this.pre();
        }

        //播放点击指定位置的歌曲
        public void playNumSong(int x) throws IOException{
            PlayService.this.playNumSong(x);
        }
    }

    /**
     * 调用服务
     * 这是Service必须实现的方法，该方法会返回一个IBinder对象，APP通过该对象与Service组件进行通信。
     */
    @Override
    public IBinder onBind(Intent intent) {
        // 设置初始音乐文件路径,正在播放的
        try{
            /**
             * setDataSource（）得到当前歌曲的路径
             * getAbsolutePath()返回抽象路径名的绝对路径名字符串
             */
            // 给播放器设置路径
            music.setDataSource(musiclist.get(playing).getAbsolutePath());
            //初始化
            music.prepare();

        }catch (IOException e){
            e.printStackTrace();
        }
        return a;
    }

    /**
     * 解除绑定(需要将媒体对象释放)
     */
    @Override
    public void unbindService(ServiceConnection con){
        super.unbindService(con);
        if(music != null){
            /**
             * 如果还有线程，则从内存清除
             * release()是一个释放捕捉
             */
            music.release();
            music = null;
        }
    }

    /**
     * 服务启动，创建媒体对象 MediaPlayer
     * 当Service第一次被创建后立即调用该方法，该方法在Service的生命周期里只被调用一次！
     */
    @Override
    public void onCreate() {
        super.onCreate();
        //创建媒体对象
        music = new MediaPlayer();
        //获取歌曲列表
        musiclist = Utils.getBeanFromSp(this, Constant.MUSICLISTKEY);
        //获取正在播放的序号
        playing = Utils.getInt(this, Constant.PLAYINGNUM, 0);
    }

   /**
    * 服务内部的开始播放函数
    */
    public void start() {

        music.start();
    }

    /**
     * 服务内部的暂停播放函数
     */
    public void pause() {

        music.pause();
    }

    /**
     * 歌曲不断播放，不断发消息至PlayActivity进行视图更新
     * 服务内部的更新进度条函数(发送消息至PlayActivity，PlayActivity对其进行更新)
     */
    public void updataSeekbar(){

        //TimerTask可用于计划要一次运行的任务或定期运行的任务
        TimerTask task = new TimerTask(){

            @Override
            public void run() {
                //时间赋值
                int time = music.getDuration();

                //赋值文件当前时间
                int currentPosition = music.getCurrentPosition();

                //对象可以重复使用,可以免除一直new Message对象造成无谓的内存压力(不断新建销毁对象)
                Message message = Message.obtain();

                //msg.what = 0是给成员变量what赋值，接受消息也就是在handleMessage里可以通过判断传入的值不同，做不同的操作
                message.what = 0;

                //传递数据的“容器”
                Bundle bundle = new Bundle();

                //第一个参数为key，第二个参数为值
                bundle.putInt("duration",time);
                bundle.putInt("currentPosition",currentPosition);

                //给message赋值
                message.setData(bundle);

                //给PlayActivity传递数据
                PlayActivity.hander.sendMessage(message);
            }
        };

        mtimer = new Timer();
        //每1秒运行一次
        mtimer.schedule(task,0,1000);
    }

    /**
     * 更新歌曲进度
     */
    public void updataMediaplay(int process) {


        /**
         * android使用 mediaPlayer 播放video视频过程中, 当用户退出当前播放，再从后台恢复播放时，需要跳转到之前退出的时间点继续播放
         * 使用的方法基本都是 SeekTo 之前的时间点
         */
        music.seekTo(process);
    }

    /**
     * 下一首
     */
    public void next(){
        try {
            //获取播放方式
            int mode = Utils.getInt(this,Constant.MODE,0);
            switch (mode){
                case 0:
                    //全部循环，先将此地址赋值播放，在将播放列表值加一，用来下一次播放下一首歌
                    playNumSong((++playing) % musiclist.size());
                    break;
                case 1:
                    //单曲循环，一直传递playing值以便实现单曲循环
                    playNumSong(playing);
                    break;
                case 2:
                    //随机播放，采用随机值，以便实现随机播放功能
                    int x = (int) (Math.random() * (musiclist.size() - 1));
                    playNumSong(x);
                    break;
            }
        }catch (IOException e){

        }
    }

    /**
     * 播放指定歌曲，歌曲地址传值以上方法
     */
    public void playNumSong(int x) throws IOException {

        //定时发送消息任务(PlayerActivity更新视图)取消
        if(mtimer != null){
            mtimer.cancel();
        }

        //存储当前播放位置
        Utils.putInt(this, Constant.PLAYINGNUM, x);

        //媒体播放器停止
        music.stop();

        //媒体播放器释放
        music.release();
        music = null;

        //创建新的歌曲播放进程
        music = new MediaPlayer();

        //给媒体播放器设置路径
        music.setDataSource(musiclist.get(x).getAbsolutePath());

        //初始化
        music.prepare();

        //创建消息，使得PlayerActivity进行视图更新
        //对象可以重复使用,可以免除一直new Message对象造成无谓的内存压力(不断新建销毁对象)
        Message message = Message.obtain();
        message.what = 1;

        //数据传递容器
        Bundle bundle = new Bundle();
        bundle.putInt(Constant.PLAYINGNUM,x);
        message.setData(bundle);
        PlayActivity.hander.sendMessage(message);

        //歌曲播放
        start();

        //开启定时任务，视图开始更新
        updataSeekbar();
    }

    public void pre(){
        try{
            //获取当前播放方式的MODE值
            int mode = Utils.getInt(this, Constant.MODE, 0);

            //根据播放方式选取上一首歌曲
            switch (mode){
                case 0:
                    //全部循环,通过减音乐位置调整为上一首
                    playNumSong((--playing + musiclist.size()) % musiclist.size());
                    break;
                case 1:
                    //单曲循环,通过同一个音乐位置实参来实现上一首为同一首
                    playNumSong(playing);
                    break;
                case 2:
                    //随机播放，通过随机函数random实现上一首为随机播放
                    int x = (int) (Math.random() * (musiclist.size() - 1));
                    playNumSong(x);
                    break;
            }

        }catch (IOException e){

        }
    }

    /**
     * 当Service被关闭时调用该方法，该方法只被调用一次！
     */
    @Override
    public void onDestroy(){
        super.onDestroy();
        if(mtimer != null){
            mtimer.cancel();
        }
        if(music != null){
            music.release();
            music = null;
        }
    }
}
