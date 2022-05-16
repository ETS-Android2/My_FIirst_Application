package com.example.myfiirstapplication;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import Entity.Book;
import httpUtils.helper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Response;

import static com.example.myfiirstapplication.Main3Activity.user_id;

public class Main2Activity extends AppCompatActivity implements View.OnClickListener {
    private  static  String  lend_time,lend_plan_time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置全屏显示
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //接受意图,接受值
        Intent intent=this.getIntent();
        String book_clicked = intent.getStringExtra("book_clicked");
         Gson gson = new Gson();
        final Book book = gson.fromJson(book_clicked, new TypeToken<Book>() {
        }.getType());
        //setcontentview 一定要在findbyid之前调用
        setContentView(R.layout.activity_main2);
        Button confirm_lend=findViewById(R.id.confirm_borrow);
        Button like=findViewById(R.id.like);
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Main2Activity.this,"看看就得了,还当真了",Toast.LENGTH_SHORT).show();
            }
        });
        //设置书的封面
        ImageView imageView = findViewById(R.id.resume);
        assert book != null;
        Glide.with(imageView).load("http:192.168.31.83:8080/xhy/"+ book.getBkURL()).into(imageView);
        TextView textView = findViewById(R.id.book_info);
        //书的简介
        textView.setText(book.getBkResume());
        final TextView name = findViewById(R.id.single_name);
        name.setText(book.getBkName());
        TextView author = findViewById(R.id.single_author);
        author.setText(book.getBkAuthor());
        TextView ISBN = findViewById(R.id.single_isbn);
        ISBN.setText(book.getBkID());
        TextView press = findViewById(R.id.single_press);
        press.setText(book.getBkPress());
        TextView price = findViewById(R.id.single_cost);
        price.setText(book.getBkPrice());
        TextView status = findViewById(R.id.single_status);
        if(book.getBkStatus().equals("借出")){
            status.setText(book.getBkStatus());
            status.setTextColor(Color.RED);
        }
        else {
            status.setText(book.getBkStatus());
        }
        ///借书按钮
        confirm_lend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(book.getBkStatus().equals("借出")){
                    final AlertDialog alertDialog=new AlertDialog.Builder(Main2Activity.this).setTitle("WARNING")
                            .setMessage("该书已被借阅了,换本书试试吧")
                            .setIcon(R.mipmap.ic_launcher)
                            .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).create();
                    alertDialog.show();
                    alertDialog.setCanceledOnTouchOutside(false);
                }
                else {
                    AlertDialog alertDialog=new AlertDialog.Builder(Main2Activity.this).setTitle("温馨提示")
                            .setMessage("确定要借阅《"+book.getBkName()+"》这本书吗")
                            .setIcon(R.mipmap.ic_launcher)
                            .setPositiveButton("是的", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    choose_date(book);
                                }
                            }).setNegativeButton("我再想想",null).create();
                    alertDialog.show();
                    alertDialog.setCanceledOnTouchOutside(false);
                }
            }
        });
    }
    private void choose_date(final Book book){
        @SuppressLint("InflateParams")
        View inflate = getLayoutInflater().inflate(R.layout.borrow_dialog, null);
        //dilog里的两个textview监听
        final TextView lend_time=inflate.findViewById(R.id.actual_lend_time);
        //借书时间监听
        lend_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show_choose_dialog(lend_time);
            }
        });
        final TextView plan_time=inflate.findViewById(R.id.lend_plan_time);
        //还书时间监听
        plan_time.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show_choose_dialog(plan_time);
            }
        });
        AlertDialog builder=new AlertDialog.Builder(Main2Activity.this)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String url="http:192.168.31.83:8080/xhy/Borrow_book_Servlet";
                        helper.postRequest(url, new FormBody.Builder()
                                .add("rdID",user_id)
                                .add("bkID",book.getBkID())
                                .add("DateBorrow", lend_time.getText().toString()+" "+system_timeMills())
                                .add("DateLendPlan",plan_time.getText().toString()+" "+system_timeMills()), new Callback() {
                            @Override
                            public void onFailure(@NotNull Call call, @NotNull IOException e) {

                            }
                            @Override
                            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                final String info_msg = response.body().string();
                                if(info_msg.equals("借书成功!")){
                                    //或者runonUITHREAD
                                    Looper.prepare();
                                    Apprise();
//                                    Toast.makeText(Main2Activity.this,info_msg,Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }
                                else
                                {  runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(Main2Activity.this,info_msg,Toast.LENGTH_SHORT).show();
                                    }
                                });

                                }

                            }
                        },Main2Activity.this);
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }).create();
        builder.setTitle("信息提示框");
        //加载布局
        builder.setView(inflate);
        //设置对话框图标
        builder.setIcon(R.mipmap.ic_launcher);
        //弹出来
        builder.show();
        builder.setCanceledOnTouchOutside(false);//点击其他地方不会关闭对话框
    }
    //
    @Override
    public void onClick(View view) {
     //
    }
    //日期选择框
private void show_choose_dialog(final TextView textView){
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog dialog = new DatePickerDialog(Main2Activity.this,
                        new DatePickerDialog.OnDateSetListener(){
                            @SuppressLint("DefaultLocale")
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                              textView.setText(String.format("%d-%d-%d", year, month+1, dayOfMonth));
                            }
                        },
                        //设置初始日期
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                dialog.show();
                dialog.setCanceledOnTouchOutside(false);//点击其他地方不会关闭对话框
    }
@RequiresApi(api = Build.VERSION_CODES.O)
private String system_timeMills(){
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
    LocalTime localTime = LocalTime.now();
    return dtf.format(localTime);            // 16:37:15
}


private void Apprise() {

    /**
     * 通知栏（兼容android 8.0以上）
     */
    boolean isVibrate = true;//是否震动
    //1.获取消息服务
    NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    //默认通道是default
    String channelId = "default";
    //2.如果是android8.0以上的系统，则新建一个消息通道
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        channelId = "chat";
        /*
         通道优先级别：
         * IMPORTANCE_NONE 关闭通知
         * IMPORTANCE_MIN 开启通知，不会弹出，但没有提示音，状态栏中无显示
         * IMPORTANCE_LOW 开启通知，不会弹出，不发出提示音，状态栏中显示
         * IMPORTANCE_DEFAULT 开启通知，不会弹出，发出提示音，状态栏中显示
         * IMPORTANCE_HIGH 开启通知，会弹出，发出提示音，状态栏中显示
         */
        NotificationChannel channel = new NotificationChannel(channelId, "消息提醒", NotificationManager.IMPORTANCE_HIGH);
        //设置该通道的描述（可以不写）
        //channel.setDescription("重要消息，请不要关闭这个通知。");
        //是否绕过勿打扰模式
        channel.setBypassDnd(true);
        //是否允许呼吸灯闪烁
        channel.enableLights(false);
        //桌面launcher的消息角标
        channel.canShowBadge();
        //设置是否应在锁定屏幕上显示此频道的通知
        //channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        //创建消息通道
        manager.createNotificationChannel(channel);
    }
    //3.实例化通知
    NotificationCompat.Builder nc = new NotificationCompat.Builder(this, channelId);
    //通知默认的声音 震动 呼吸灯
    nc.setDefaults(NotificationCompat.DEFAULT_ALL);
    //通知标题
    nc.setContentTitle("你有一条新消息");
    //通知内容
    nc.setContentText("借书成功!");
    //设置通知的小图标
    nc.setSmallIcon(R.drawable.note_msg);
    //设置通知的大图标
    nc.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
    //设定通知显示的时间
    nc.setWhen(System.currentTimeMillis());
    //设置通知的优先级
    nc.setPriority(NotificationCompat.PRIORITY_MAX);
    //设置点击通知之后通知是否消失
    nc.setAutoCancel(true);
    //4.创建通知，得到build
    Notification notification = nc.build();
    //5.发送通知
    manager.notify(1, notification);
}

}




