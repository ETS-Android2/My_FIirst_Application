package com.example.myfiirstapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Objects;

import httpUtils.helper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    //声明一个long类型变量：用于存放上一点击“返回键”的时刻
    private long mExitTime;
    Button btn_login;
    private EditText username, psd;
    String rdid,pwd;
    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //加载布局
        setContentView(R.layout.activity_login);
        username = findViewById(R.id.username);
        btn_login=findViewById(R.id.btn_login);
        ImageView gif = findViewById(R.id.gif);
        //加载Gif文件
        Glide.with(gif).load(R.drawable.campus).into(gif);
        psd = findViewById(R.id.psd);
        /*1、MainActivity这个类传入this实现onclick方法 click事件按钮3注册点击事件*/
//        btn_three.setOnClickListener(this);
//        /*2、按钮1匿名内部类实现点击事件监听*/
//        btn_one.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                btn_one.setText("按钮1已被点击");
//            }
//        });
    }
    /* 按钮2xml文件中声明属性onclick 绑定事件*/
//    public void click(View view) {
//        btn_two.setText("按钮2已被点击");
//        Intent intent = new Intent(LoginActivity.this, Main2Activity.class);
////         寻求result意图，请求码为666
//        startActivityForResult(intent,666);
//    }

    /*按钮3 activity实现onclicklisenter接口*/
    @Override
    public void onClick(View V) {

    }
//登录按钮点击事件
    public void click_in(View view) {
        //输入框的username
        rdid = username.getText().toString().trim();
        pwd = psd.getText().toString().trim();

        if (view.getId() == R.id.btn_login) {
            ///判断是否输入为空
            if (rdid.equals("")) {
                username.setHint("用户名不能为空");//提示框
                username.setHintTextColor(Color.RED);
                Toast.makeText(LoginActivity.this, "学号或者密码不能为空", Toast.LENGTH_SHORT).show();
            }else if(pwd.equals("")){
                psd.setHint("密码不能为空");
                psd.setHintTextColor(Color.RED);
            }
            else {
                //发送post请求
                String url = "http:192.168.31.83:8080/xhy/Login";
                helper.postRequest(url, new FormBody.Builder()
                        .add("rdID",rdid)
                        .add("rdpassword",pwd),new Callback() {
                    //回调方法还是在子线程中,还得回到UI线程
                    @Override
                    //请求失败回调函数
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        runOnUiThread(new Runnable() {
                           @Override
                             public void run() {
                               Toast.makeText(getApplicationContext(),"网络错误,请检查网络",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    @Override
        public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                        //Logcat.i方法msg长度超过4kb都会被丢弃，会被截断
                         //Log.i("info","嘿嘿嘿"+response.body().string());
//                        Log.i("response",response.body().string());
                        //servlet后面用的是println 有\r\n换行符所以equals一直是错误的，我丢
                        final String json_string = Objects.requireNonNull(response.body()).string();
                        final String cookie = response.header("Cookie");//一直会有值
                        Log.i("回传的cookie",""+cookie);
                        Log.i("初始list长度", " "+json_string.length() );//长度为6加上那个\r\n
                        if(json_string.length()==6){
                           LoginActivity.this.runOnUiThread(new Runnable() {
                               @Override
                               public void run() {
                                   Toast.makeText(getApplicationContext(),"用户不存在或密码错误",Toast.LENGTH_SHORT).show();
                               }
                           });
                        }
                        else {
                          runOnUiThread(new Runnable() {
                              @Override
                              public void run() {
                                  try {
                                    String rdName = URLDecoder.decode(response.header("rdName"),"UTF-8");
                                      Log.i("名字",rdName);//居然是问号
                                      Main3Activity.user_id=rdid;
                                      Main3Activity.user_password=pwd;
                                      Main3Activity.user_name=rdName;
                                      Main5Activity.cookie=cookie;
                                      Intent intent = new Intent(getApplicationContext(), Main5Activity.class);
                                      intent.putExtra("book_all",json_string);
                                      startActivity(intent);
                                      Toast.makeText(LoginActivity.this,"亲爱的"+rdName+"你好,欢迎使用",Toast.LENGTH_SHORT).show();
                                      finish();
                                  } catch (UnsupportedEncodingException e) {
                                      e.printStackTrace();
                                  }

                              }
                          });
                        }
                    }
                },this);
            }
        }
        else {
            Toast.makeText(this,"你不会真的以为我写了吧",Toast.LENGTH_SHORT).show();
//            throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }
    @Override
    //再按两次返回键退出应用
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //判断用户是否点击了“返回键”
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //与上次点击返回键时刻作差
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                //大于2000ms则认为是误操作，使用Toast进行提示
                Toast.makeText(this, "再按一次退出图书管理系统", Toast.LENGTH_SHORT).show();
                //并记录下本次点击“返回键”的时刻，以便下次进行判断
                mExitTime = System.currentTimeMillis();
            } else {
                //小于2000ms则认为是用户确实希望退出程序-调用System.exit()方法进行退出
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}