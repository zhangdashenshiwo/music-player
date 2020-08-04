package com.example.zhang.myapplication;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import com.example.zhang.myapplication.Fragment.MessageFragment;
import com.example.zhang.myapplication.Fragment.SettingFragment;
import com.example.zhang.myapplication.Fragment.SingFragment;
import com.example.zhang.myapplication.utils.ActivityManager;
import com.example.zhang.myapplication.utils.Constant;
import com.example.zhang.myapplication.utils.Utils;
import com.jaeger.library.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

import eu.long1.spacetablayout.SpaceTabLayout;

public class BasicActivity extends SuperActivity {


    /**
     * 记录当前返回键按下的时间撮
     * 默认为0，保证第一次按下后能对当前时间进行记录
     */
    private long mTime = 0;

    /**
     * 底部TabLayout控件
     * 可以存储fragment，默认为3个
     * 自定义类
     */
    private SpaceTabLayout mBottomTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic);

        //状态栏透明划
        StatusBarUtil.setTranslucent(this);

        //获取Fragment列表，包含所以fragmentList
        List<Fragment> fragmentList = getFragmentList();

        //获取viewPager控件
        ViewPager viewPager = findViewById(R.id.Main_viewPager);

        //设置viewPager预加载fragment的数量（数量存储于常量池中）
        viewPager.setOffscreenPageLimit(Constant.MAINFRAGMENTPAGENUM);

        //获取底部TabLayout
        mBottomTabLayout = findViewById(R.id.mainActivity_bottomSliderTabLayout);

        //初始化底部TabLayout
        mBottomTabLayout.initialize(viewPager, getSupportFragmentManager(),
                fragmentList, savedInstanceState);

        //获取用户名
        String username = Utils.getString(BasicActivity.this,Constant.userNameSPKey,"")+"欢迎你";

        //弹出欢迎语句
        Toast.makeText(BasicActivity.this,username,Toast.LENGTH_LONG).show();


    }

    /**
     * 获取所有fragment
     * 包含MainFragment（歌曲播放主界面），MessageFragment（消息界面），MineFragment（其他功能）
     * @return 全体fragment List
     */
    private List<Fragment> getFragmentList() {

        /**
        * 用于存储主页面的3个Fragment
        * ArrayList 去重
        * 利用HashSet里面的元素不可重复
        * 利用list里面contains方法比较是否存在去重
         */
        List<Fragment> fragmentList = new ArrayList<>();

        //歌曲播放主界面
        fragmentList.add(new SingFragment());

        //消息界面
        fragmentList.add(new MessageFragment());

        //个人中心界面
        fragmentList.add(new SettingFragment());

        return fragmentList;
    }


    /**
     * 页面意外关闭数据恢复
     * 主要对底部TabLayout进行恢复
     *  Bundle是用来传递数据的“容器”，它保存的数据，是以key-value(键值对)的形式存在的。
     *  我们经常使用Bundle在Activity之间传递数据，
     *  传递的数据可以是boolean、byte、int、long、float、double、string等基本类型或它们对应的数组，
     *  也可以是对象或对象数组。当Bundle传递的是对象或对象数组时，必须实现Serializable 或Parcelable接口。
     * @param outState 保存的数据，用于还原页面
     */

    protected void onSaveInstanceState(Bundle outState){
        mBottomTabLayout.saveState(outState);
        super.onSaveInstanceState(outState);
    }

    /**
     * 返回键处理
     * 主要实现了再按一次退出程序的功能
     * 记录连续两次的时间戳，如果小于2S，进行退出。
     * 注意：此退出清理完所以活动。
     */
    @Override
    public void onBackPressed(){
        //获取第一次按键时间
        long mNowTime = System.currentTimeMillis();
        //比较两次按键时间差
        if ((mNowTime - mTime) > 2000) {
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_LONG).show();

            //记录当前时间戳，为下次计算做准备
            mTime = mNowTime;
        } else {

            //退出程序（清除所有的活动）
            ActivityManager.getInstance().exit();
        }

    }

}
