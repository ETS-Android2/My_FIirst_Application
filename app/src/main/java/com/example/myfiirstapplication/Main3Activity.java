package com.example.myfiirstapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.net.URL;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class Main3Activity extends AppCompatActivity implements View.OnClickListener {
    //静态全局变量保存用户名,密码
public static String user_id;
public static String user_password;
public static String user_name;
    RelativeLayout relativeLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置全屏显示
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.personal_center);
        ImageView imageView = findViewById(R.id.user_picture);
        relativeLayout=findViewById(R.id.user_background);
        //给相对布局加载图片
        Glide.with(this)
                .load("http:192.168.31.83:8080/xhy/User_Images/"+user_id+".png").apply(RequestOptions.centerCropTransform())
                .into(new CustomTarget<Drawable>() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        // Do something with the Drawable here
                        relativeLayout.setBackground(resource);
                    }
                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // 从任何视图中删除onResourceReady中提供的Drawable，并确保不保留对它的引用。
                    }
                });
        //加载并显示圆形用户头像图片
//        Glide.with(imageView).load("sc").apply(RequestOptions.bitmapTransform())
        Glide.with(imageView).load("http:192.168.31.83:8080/xhy/User_Images/"+user_id+".png").apply(RequestOptions.bitmapTransform(new CircleCrop())).into(imageView);
        TextView textView = findViewById(R.id.login_name);
        textView.setText(user_name);
        imageView.setOnClickListener(this);
        SpannableStringBuilder spannableStringBuilder=new SpannableStringBuilder("《隐私协议》《软件许可及服务协议》");
         spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.BLUE),0,17, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        spannableStringBuilder.setSpan(new URLSpan("https://consumer.huawei.com/cn/support/service-privacy-notice/"),0,17, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        TextView policy = findViewById(R.id.policy);
         policy.setText(spannableStringBuilder);
    }
         @Override
        public void onClick(View view) {
           if(view.getId()==R.id.user_picture){
               Toast.makeText(this,"别点了,知道你长得好看",Toast.LENGTH_SHORT).show();
           }
    }
}