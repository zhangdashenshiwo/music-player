package com.example.zhang.myapplication;


import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.zhang.myapplication.ui.CircularAnim;
import com.example.zhang.myapplication.utils.Constant;
import com.example.zhang.myapplication.utils.Utils;


public class LoginActivity extends SuperActivity implements View.OnClickListener{


    //密码输入框
    private EditText mEtPassword;

    //账户输入框
    private EditText mEtMobile;

    //需要注册
    private static final int REQUEST_CODE_GO_TO_REGIST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //初始化用户名输入框
        initUserNameEditText();

        //初始化密码输入框
        initPasswordEditText();

        //初始化图片点击（清除用户名，密码，显示隐藏密码）
        initImageButton();

        //初始化登录按钮
        initLoginButton();



    }

    /**
     初始化图片点击（清除用户名，密码，显示隐藏密码）
     */
    private void initImageButton() {
        //定义点击事件
        findViewById(R.id.iv_clean_phone).setOnClickListener(this);
        findViewById(R.id.clean_password).setOnClickListener(this);
        findViewById(R.id.iv_show_pwd).setOnClickListener(this);
    }

    /**
     初始化密码输入框
     */
    private void initPasswordEditText() {
        mEtPassword = findViewById(R.id.et_password);

        /**
         * 设置输入监听事件
         */
        mEtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            /**
             * 输入后事件
             * @param s 输入的字符
             */
            @Override
            public void afterTextChanged(Editable s) {

                //获取一键删除图标
                ImageView mCleanPassword = findViewById(R.id.clean_password);

                if (!TextUtils.isEmpty(s) && mCleanPassword.getVisibility() == View.GONE) {
                    mCleanPassword.setVisibility(View.VISIBLE);
                } else if (TextUtils.isEmpty(s)) {
                    mCleanPassword.setVisibility(View.GONE);
                }
                if (s.toString().isEmpty()) {
                    return;
                }
                if (!s.toString().matches("[A-Za-z0-9]+")) {
                    String temp = s.toString();
                    Toast.makeText(LoginActivity.this, "请输入数字或字母", Toast.LENGTH_SHORT).show();
                    s.delete(temp.length() - 1, temp.length());
                    //取消当前的输入
                    mEtPassword.setSelection(s.length());
                }
            }
        });
    }

    /**
     初始化用户名输入框
     */
    private void initUserNameEditText() {
        mEtMobile = findViewById(R.id.et_mobile);

        /**
         * 设置监听事件
         */
        mEtMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ImageView mIvCleanPhone = findViewById(R.id.iv_clean_phone);
                if (!TextUtils.isEmpty(s) && mIvCleanPhone.getVisibility() == View.GONE) {
                    mIvCleanPhone.setVisibility(View.VISIBLE);
                } else if (TextUtils.isEmpty(s)) {
                    mIvCleanPhone.setVisibility(View.GONE);
                }
            }
        });
    }


    /**
     第一个参数为请求码，即调用startActivityForResult()传递过去的值
     第二个参数为结果码，结果码用于标识返回数据来自哪个新Activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_GO_TO_REGIST:
                //判断注册是否成功  如果注册成功
                if (resultCode == RESULT_OK) {
                    //则获取data中的账号和密码  动态设置到EditText中
                    String username = data.getStringExtra("user_name");
                    mEtMobile.setText(username);
                }
                break;
        }
    }


    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_clean_phone:
                mEtMobile.setText("");
                break;
            case R.id.clean_password:
                mEtPassword.setText("");
                break;
            case R.id.iv_show_pwd:
                //如果输入的密码是不可见的
                if (mEtPassword.getInputType() != InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    //设置密码可见
                    mEtPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    //创建mIvShowPwd对象
                    ImageView mIvShowPwd = findViewById(R.id.iv_show_pwd);
                    mIvShowPwd.setImageResource(R.drawable.pass_visuable);
                } else {
                    //密码设为不可见状态
                    mEtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    ImageView mIvShowPwd = findViewById(R.id.iv_show_pwd);
                    //图标变换为不可见图标
                    mIvShowPwd.setImageResource(R.drawable.pass_gone);
                }
                String pwd = mEtPassword.getText().toString();
                if (!TextUtils.isEmpty(pwd))
                    mEtPassword.setSelection(pwd.length());
                break;
        }
    }


    /**
    初始化登录按钮
     */
    private void initLoginButton() {

        //获取对象
        final ProgressBar mProgressBar = findViewById(R.id.progressBar22);
        final Button login = findViewById(R.id.btn_login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果用户名输入域为空
                if (TextUtils.isEmpty(mEtMobile.getText())) {

                    //未输入用户名
                    Toast.makeText(LoginActivity.this, "用户名不能为空", Toast.LENGTH_LONG).show();

                    //如果密码输入域为空
                } else if (TextUtils.isEmpty(mEtPassword.getText())) {

                    //未输入密码
                    Toast.makeText(LoginActivity.this, "密码不能为空", Toast.LENGTH_LONG).show();

                } else {
                    //判断输入账号、密码是否与注册账号、密码一致
                    if (Utils.getString(LoginActivity.this, Constant.userNameSPKey, "").equals(mEtMobile.getText().toString())
                            && Utils.getString(LoginActivity.this, Constant.userPasswordSPKey, "").equals(mEtPassword.getText().toString())) {

                        //等待条设计
                        CircularAnim.hide(login)
                                .endRadius(mProgressBar.getHeight() / 2)
                                //监听事件
                                .go(new CircularAnim.OnAnimationEndListener() {
                                    @Override
                                    public void onAnimationEnd() {

                                        //按钮消失
                                        login.setVisibility(View.GONE);

                                        //进度条变成可见状态
                                        mProgressBar.setVisibility(View.VISIBLE);

                                        //消息延迟
                                        mProgressBar.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {

                                                //设置默认的动画部署器
                                                CircularAnim.fullActivity(LoginActivity.this, mProgressBar).colorOrImageRes(R.color.theme).go(new CircularAnim.OnAnimationEndListener() {
                                                    @Override
                                                    public void onAnimationEnd() {
                                                        Toast.makeText(LoginActivity.this, "登录成功!", Toast.LENGTH_LONG).show();

                                                        //登录成功将已经登录的标志存入SP中，下次进入直接进主页，无需登录
                                                        Utils.putBool(LoginActivity.this, Constant.isLogOn, true);

                                                        //创建intent对象
                                                        Intent intent = new Intent();

                                                        //通过intent开启一个activity，并将这个activity放至栈底或者清空栈后再把这个activity压进栈去
                                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                                                        //跳转主页面
                                                        intent.setClass(LoginActivity.this, BasicActivity.class);

                                                        //启动intent
                                                        startActivity(intent);

                                                        //结束此页面
                                                        finish();
                                                    }
                                                });
                                            }
                                        }, 3000);
                                    }
                                });
                    } else {

                        //密码错误，弹出吐司
                        Toast toast = Toast.makeText(getApplicationContext(), "账户不存在或密码错误~，请重试或尝试注册~", Toast.LENGTH_LONG);

                        //设置提示框显示的位置
                        toast.setGravity(Gravity.CENTER, 0, 1080);

                        //显示消息
                        toast.show();
                    }
                }
            }
        });

        //注册
        findViewById(R.id.regist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //跳转注册页面
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);

                //开始跳转事件
                startActivity(intent);
            }
        });

    }

}
