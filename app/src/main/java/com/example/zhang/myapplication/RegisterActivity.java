package com.example.zhang.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.zhang.myapplication.utils.Constant;
import com.example.zhang.myapplication.utils.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    //声明变量
    private EditText mEtPassword;
    private EditText mEtPassword1;
    private EditText mEtEmail;
    private EditText mEtMobile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //给对象赋予对象

        //初始化用户名输入框
        initUserNameEditText();

        //初始化密码输入框
        initPasswordEditText();

        //初始化图片点击（清除用户名，密码，显示隐藏密码）
        initImageButton();

        //初始化登录按钮
        initRegistButton();

        //初始化邮箱输入框
        initUserEmailEditText();
    }

    //初始化邮箱输入框
    private void initUserEmailEditText() {
        mEtEmail = findViewById(R.id.user_email);
        mEtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            //输入后显示图标
            @Override
            public void afterTextChanged(Editable s) {
                ImageView mIvCleanEmail = findViewById(R.id.clean_email);
                if (!TextUtils.isEmpty(s) && mIvCleanEmail.getVisibility() == View.GONE) {
                    mIvCleanEmail.setVisibility(View.VISIBLE);
                } else if (TextUtils.isEmpty(s)) {
                    mIvCleanEmail.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * 初始化注册界面
     */
    private void initRegistButton() {
        //声明对象
        final ProgressBar mProgressBar = findViewById(R.id.progressBar2);
        final Button register = findViewById(R.id.btn_regist);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(mEtMobile.getText())){

                    //Toast默认的有两个LENGTH_LONG(长)和LENGTH_SHORT(短)
                    Toast.makeText(RegisterActivity.this,"用户名为空",Toast.LENGTH_LONG).show();

                }else if(TextUtils.isEmpty(mEtEmail.getText()) || !isEmail(mEtEmail.getText().toString())){

                    //Toast默认的有两个LENGTH_LONG(长)和LENGTH_SHORT(短)
                    Toast.makeText(RegisterActivity.this,"请输入正确的邮箱",Toast.LENGTH_LONG).show();

                }else if (TextUtils.isEmpty(mEtPassword.getText()) || TextUtils.isEmpty(mEtPassword1.getText())){

                    //Toast默认的有两个LENGTH_LONG(长)和LENGTH_SHORT(短)
                    Toast.makeText(RegisterActivity.this,"请输入正确的密码",Toast.LENGTH_LONG).show();

                } else if (!mEtPassword.getText().toString().trim().equals(mEtPassword1.getText().toString().trim())){

                    //Toast默认的有两个LENGTH_LONG(长)和LENGTH_SHORT(短)
                    Toast.makeText(RegisterActivity.this,"两次输入的密码不一致",Toast.LENGTH_LONG).show();

                }else {

                    //储存用户名
                    Utils.putString(RegisterActivity.this,Constant.userNameSPKey,mEtMobile.getText().toString());

                    //存储密码
                    Utils.putString(RegisterActivity.this, Constant.userPasswordSPKey, mEtPassword.getText().toString());

                    //储存邮箱
                    Utils.putString(RegisterActivity.this, Constant.userEmail, mEtEmail.getText().toString());

                    //显示注册成功
                    Toast.makeText(RegisterActivity.this,"注册成功!",Toast.LENGTH_LONG).show();

                    //切换转向登录页面
                    Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                    startActivity(intent);

                    //结束该页面
                    RegisterActivity.this.finish();
                }
            }
        });
    }

    /**
     * 初始化图片点击（清除用户名，密码，显示隐藏密码）
     */
    private void initImageButton() {
        findViewById(R.id.iv_clean_phone).setOnClickListener(this);
        findViewById(R.id.clean_password).setOnClickListener(this);
        findViewById(R.id.iv_show_pwd).setOnClickListener(this);

        findViewById(R.id.cleanr_password).setOnClickListener(this);
        findViewById(R.id.ri_show_pwd).setOnClickListener(this);
        findViewById(R.id.clean_email).setOnClickListener(this);
    }

    /**
     * 初始化密码输入框
     */
    private void initPasswordEditText() {
        mEtPassword = findViewById(R.id.et_password);
        mEtPassword1 = findViewById(R.id.right_password);
        mEtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            /**
             * 输入完检测
             * @param s
             */
            @Override
            public void afterTextChanged(Editable s) {
                ImageView mCleanPassword = findViewById(R.id.clean_password);
                if (!TextUtils.isEmpty(s) && mCleanPassword.getVisibility() == View.GONE){
                    mCleanPassword.setVisibility(View.VISIBLE);
                }else if (TextUtils.isEmpty(s)) {
                    mCleanPassword.setVisibility(View.GONE);
                }
                if (s.toString().isEmpty()) {
                    return;
                }
                if (!s.toString().matches("[A-Za-z0-9]+")){
                    String temp = s.toString();
                    Toast.makeText(RegisterActivity.this, "请输入数字或字母", Toast.LENGTH_SHORT).show();
                    s.delete(temp.length() - 1, temp.length());

                    //取消当前的输入
                    mEtPassword.setSelection(s.length());
                }
            }
        });

        mEtPassword1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            /**
             * 输入完检测
             * @param s
             */
            @Override
            public void afterTextChanged(Editable s) {
                ImageView mCleanPassword = findViewById(R.id.cleanr_password);
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
                    Toast.makeText(RegisterActivity.this, "请输入数字或字母", Toast.LENGTH_SHORT).show();
                    s.delete(temp.length() - 1, temp.length());

                    //取消当前的输入
                    mEtPassword1.setSelection(s.length());
                }
            }
        });
    }

    /**
     * 初始化用户名输入框
     */
    private void initUserNameEditText() {
        mEtMobile = findViewById(R.id.et_mobile);

        mEtMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            /**
             * 输入后显示
            */
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

    //判断邮箱是否合乎格式
    public static boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";

        //根据正则表达式模式判断邮箱是否输入正确
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }


    @Override
    public void onClick(View view){
        switch(view.getId()){
            //点击图标，即把输入匡清空
            case R.id.iv_clean_phone:
                mEtMobile.setText("");
                break;
            case R.id.clean_password:
                mEtPassword.setText("");
                break;
            case R.id.cleanr_password:
                mEtPassword1.setText("");
                break;
            case R.id.clean_email:
                mEtEmail.setText("");
                break;

            case R.id.iv_show_pwd:
                if (mEtPassword.getInputType() != InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    mEtPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    ImageView mIvShowPwd = findViewById(R.id.iv_show_pwd);
                    mIvShowPwd.setImageResource(R.drawable.pass_visuable);
                } else {
                    mEtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    ImageView mIvShowPwd = findViewById(R.id.iv_show_pwd);
                    mIvShowPwd.setImageResource(R.drawable.pass_gone);
                }
                String pwd = mEtPassword.getText().toString();
                if (!TextUtils.isEmpty(pwd))
                    mEtPassword.setSelection(pwd.length());
                break;

            case R.id.ri_show_pwd:
                if (mEtPassword.getInputType() != InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    mEtPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    ImageView mIvShowPwd = findViewById(R.id.iv_show_pwd);
                    mIvShowPwd.setImageResource(R.drawable.pass_visuable);
                } else {
                    mEtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    ImageView mIvShowPwd = findViewById(R.id.iv_show_pwd);
                    mIvShowPwd.setImageResource(R.drawable.pass_gone);
                }
                String pwd1 = mEtPassword.getText().toString();
                if (!TextUtils.isEmpty(pwd1))
                    mEtPassword.setSelection(pwd1.length());
                break;
        }
    }


}
