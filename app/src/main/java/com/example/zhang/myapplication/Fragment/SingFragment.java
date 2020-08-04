package com.example.zhang.myapplication.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.zhang.myapplication.LoadingActivity;
import com.example.zhang.myapplication.MusicListActivity;
import com.example.zhang.myapplication.R;
import com.example.zhang.myapplication.SearchActivity;
import com.example.zhang.myapplication.utils.Constant;
import com.example.zhang.myapplication.viewpage.BezierRoundView;
import com.example.zhang.myapplication.viewpage.BezierViewPager;
import com.example.zhang.myapplication.viewpage.CardPagerAdapter;
import com.example.zhang.myapplication.viewpage.GlideImageClient;
import com.example.zhang.myapplication.viewpage.ImageLoadFactory;
import com.hanks.htextview.evaporate.EvaporateTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SingFragment extends Fragment {


    //整体fragment控件，用于获取内部控件
    private View layout;


    //作为本页面的上下文
    private Activity activity;


    //记录问候语播放的位置
    int index = 0;

    /**
     * 定时任务器
     * 作为全局的作用：进入下个页面的时候将其停止，
     * 防止发生空指针异常
     * Timer类来进行定时调用
     */
    Timer textTimer = null;
    private Timer mtimer;

    //声明文本域变量
    private EvaporateTextView mCvaporateTextView = null;

    //TimerTask呢只是一个实现了run方法的一个类
    private TimerTask textTimerTask;

    @Override
    public void onStop(){
        if (textTimer != null){
            textTimer.cancel();
        }
        super.onStop();
    }

    @Override
    public void onStart(){
        //设置动态字体
        initDynamicText();
        //超类
        super.onStart();
    }

    /**
     * 初始化动态字体
     */
    private void initDynamicText() {

        if (mCvaporateTextView == null) {

            //设置变化字体
            mCvaporateTextView = layout.findViewById(R.id.mainActivity_evaporateTextView);
        }

        //创建新的Timer类
        if(textTimer == null){
            textTimer = new Timer();
        }

        if (textTimerTask == null) {
            textTimerTask = new TimerTask() {
                @Override
                public void run() {
                    //动态字体方法调用
                    mCvaporateTextView.animateText(Constant.GREETINGSENTENCES[(index++) % Constant.GREETINGSENTENCES.length]);
                }
            };
            //开启任务，2.5S更换
            textTimer.schedule(textTimerTask, 0, Constant.DELAY_TIME);
        }
    }

    /**
     * 编译器可以给你验证@Override下面的方法名是否是你父类中所有的，如果没有则报错。
     * @Nullable 来声明一个方法是否是空指针安全的。
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container , @Nullable Bundle savedInstanceState){
        //获取整体fragment控件
        layout = View.inflate(getActivity().getApplicationContext(), R.layout.fragment_sing, null);

        //获取activity,方便后续使用
        activity = getActivity();

        //将页面返回
        return layout;
    }

    /**
     * 主activity的方法实现
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //初始化动态字体
        initDynamicText();

        //初始化ViewPage（动态滚动栏）
        new Thread(new Runnable() {
            @Override
            public void run() {
                InitViewPage();
            }
        }).start();

        //初始化按钮监听事件
        initButton();

    }


    /**
     * 初始化各类button控件
     * 包括本地音乐、我的下载、最近播放
     */
    private void initButton() {
        //本地音乐
        layout.findViewById(R.id.item_activity_main_entryPlayActivityButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, MusicListActivity.class);
                startActivity(intent);

                //关闭定时字体任务，不然会空指针异常
                textTimer.cancel();
                activity.overridePendingTransition(R.anim.next_in, R.anim.next_out);
            }
        });

        //我的下载
        layout.findViewById(R.id.item_activity_main_myDownLoadButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, LoadingActivity.class);
                startActivity(intent);

                //关闭定时字体任务，不然会空指针异常
                textTimer.cancel();
                activity.overridePendingTransition(R.anim.next_in, R.anim.next_out);
            }
        });

        //最近播放
        layout.findViewById(R.id.item_activity_main_recentPlayButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, LoadingActivity.class);
                startActivity(intent);

                //关闭定时字体任务，不然会空指针异常
                textTimer.cancel();
                activity.overridePendingTransition(R.anim.next_in, R.anim.next_out);
            }
        });


        //跳转搜索本地歌曲
        layout.findViewById(R.id.sousuo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity,SearchActivity.class);
                startActivity(intent);

                //关闭定时字体任务，不然会空指针异常
                textTimer.cancel();
                activity.overridePendingTransition(R.anim.next_in, R.anim.next_out);
            }
        });


    }

    /**
     * 初始化ViewPage（动态滚动栏）
     * 使用的是网络图片链接
     */
    private void InitViewPage() {

        //实现添加网络图片
        ImageLoadFactory.getInstence().setImageClient(new GlideImageClient());

        //添加网络图片链接
        final List<Object> imgList;
        imgList = new ArrayList<>();
        imgList.add(Constant.IMAGE_URL_1);
        imgList.add(Constant.IMAGE_URL_2);
        imgList.add(Constant.IMAGE_URL_3);
        imgList.add(Constant.IMAGE_URL_4);

        //将图片资源链接List加入Adapter中
        CardPagerAdapter cardAdapter = new CardPagerAdapter(activity);
        cardAdapter.addImgUrlList(imgList);

        //设置阴影大小
        //设置阴影大小，即vPage  左右两个图片相距边框  maxFactor + 0.3*CornerRadius   *2
        //设置阴影大小，即vPage 上下图片相距边框  maxFactor*1.5f + 0.3*CornerRadius
        int mWidth = activity.getWindowManager().getDefaultDisplay().getWidth();
        float heightRatio = 0.565f;  //高是宽的 0.565 ,根据图片比例
        int maxFactor = mWidth / 25;
        cardAdapter.setMaxElevationFactor(maxFactor);


        //因为我们adapter里的cardView CornerRadius已经写死为10dp，所以0.3*CornerRadius=3
        //设置Elevation之后，控件宽度要减去 (maxFactor + dp2px(3)) * heightRatio
        //heightMore 设置Elevation之后，控件高度 比  控件宽度* heightRatio  多出的部分
        int mWidthPading = mWidth / 8;
        float heightMore = (1.5f * maxFactor + dp2px(3)) - (maxFactor + dp2px(3)) * heightRatio;
        int mHeightPading = (int) (mWidthPading * heightRatio - heightMore);

        //获取滚动viewpager
        final BezierViewPager viewPager = layout.findViewById(R.id.mainActivity_viewPage);

        //初始化viewpager
        viewPager.setLayoutParams(new RelativeLayout.LayoutParams(mWidth, (int) (mWidth * heightRatio)));
        viewPager.setPadding(mWidthPading, mHeightPading, mWidthPading, mHeightPading);
        viewPager.setClipToPadding(false);
        viewPager.setAdapter(cardAdapter);
        viewPager.showTransformer(0.2f);

        //初始化viewpager顶部的滑动圆点
        BezierRoundView bezRound = layout.findViewById(R.id.mainActivity_viewPageBezRound);
        bezRound.attach2ViewPage(viewPager);

        //设置当前页面
        viewPager.setCurrentItem(0);

        //执行无限循环任务（不断自己滑动图片）
        mtimer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int currentItem = viewPager.getCurrentItem();
                        if (currentItem == imgList.size() - 1) {

                            //已经到达最后一张，从头开始划动
                            viewPager.setCurrentItem(0);

                        } else {

                            //滚动至下一张
                            viewPager.setCurrentItem(currentItem + 1);
                        }
                    }
                });

            }
        };

        //开始执行
        mtimer.schedule(task, Constant.DELAY_TIME, Constant.DELAY_TIME);
    }




    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public int dp2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
