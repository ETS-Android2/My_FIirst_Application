package com.example.myfiirstapplication;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;

import android.os.Bundle;

import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yanzhenjie.recyclerview.OnItemMenuClickListener;
import com.yanzhenjie.recyclerview.SwipeMenu;
import com.yanzhenjie.recyclerview.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import com.yanzhenjie.recyclerview.touch.OnItemMoveListener;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import Entity.Book;
import httpUtils.helper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Response;

import static com.example.myfiirstapplication.Main3Activity.user_id;

/*
* 1获取到recycleview对象
* 2设置数据适配器
* 3、设置recycle布局管理器
* */
public class Main4Activity extends AppCompatActivity {
    private SwipeRecyclerView my_recycle;
    private MyAdapter myAdapter;
    //    private OnItemMoveListener onItemMoveListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.lend_book_info);
        TextView textView = findViewById(R.id.on_borrow);
        textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        //开始查询借书记录请求
        String url = "http:192.168.31.83:8080/xhy/Find_lend_book_Servlet";
        helper.postRequest(url, new FormBody.Builder().add("rdID",user_id), new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Toast.makeText(Main4Activity.this,"网络错误",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String lend_book = response.body().string();
                if (lend_book.length()!=11) {
                    //转list <Book>对象
                    final List<Book> lend_books = new Gson().fromJson(lend_book, new TypeToken<List<Book>>() {
                    }.getType());
                    //去主线程进行
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            my_recycle = findViewById(R.id.book_recycler_view);
                            //初始化item菜单
                            initMenu();
                            //垂直排列不反转
                            my_recycle.setLayoutManager(new LinearLayoutManager(Main4Activity.this, LinearLayoutManager.VERTICAL, false));
                            myAdapter = new MyAdapter(lend_books);
                            my_recycle.setAdapter(myAdapter);
                        }
                    });
                }
            }
        },this);
    }
    //添加右边菜单&菜单监听，公开的
   public void initMenu() {
       //添加右边的还书菜单
       SwipeMenuCreator menuCreator = new SwipeMenuCreator() {
           @Override
           public void onCreateMenu(SwipeMenu leftMenu, SwipeMenu rightMenu, int position) {
               int width = 400;
               int height = ViewGroup.LayoutParams.MATCH_PARENT;
               SwipeMenuItem return_book = new SwipeMenuItem(Main4Activity.this)
                       .setText("一键还书")
                       .setTextSize(20)
                       .setBackgroundColor(Color.RED)
                       .setTextColor(Color.WHITE)
                       .setHeight(height).setWidth(width);
               //添加右边的还书菜单
               rightMenu.addMenuItem(return_book);
           }
       };
       my_recycle.setSwipeMenuCreator(menuCreator);

       //adapterposition 是item 位置
       //               Toast.makeText(Main4Activity.this, "左右方向" + direction + "item位置" + adapterPosition + " 菜单在item中的位置" + menuPosition, Toast.LENGTH_SHORT).show();
       //可以弹出一个对话框
       OnItemMenuClickListener itemMenuClickListener = new OnItemMenuClickListener() {
           @Override
           public void onItemClick(SwipeMenuBridge menuBridge, int adapterPosition) {
               //关闭菜单任何操作必须先关闭菜单，否则可能出现Item菜单打开状态错乱
               menuBridge.closeMenu();
               //左右方向// 左侧还是右侧菜单：
               int direction = menuBridge.getDirection();
               // // 菜单在Item中的Position：0
               int menuPosition = menuBridge.getPosition();
               //adapterposition 是item 位置
//               Toast.makeText(Main4Activity.this, "左右方向" + direction + "item位置" + adapterPosition + " 菜单在item中的位置" + menuPosition, Toast.LENGTH_SHORT).show();
               //可以弹出一个对话框是否确认还书
               showMsgDialog(adapterPosition);
           }
       };
       //添加菜单监听
       my_recycle.setOnItemMenuClickListener(itemMenuClickListener);
//       onItemMoveListener=new OnItemMoveListener() {
//           @Override
//           public boolean onItemMove(RecyclerView.ViewHolder srcHolder, RecyclerView.ViewHolder targetHolder) {
//               // 此方法在Item拖拽交换位置时被调用。
//               // 第一个参数是要交换为之的Item，第二个是目标位置的Item。
//               int fromPosition = srcHolder.getAdapterPosition();
//               int toPosition=targetHolder.getAdapterPosition();
//               Collections.swap(myAdapter.list,fromPosition,toPosition);
//               //交换数据并更新adapter
//               myAdapter.notifyItemMoved(fromPosition, toPosition);
//               return true;
//           }
//           @Override
//           //侧滑删除数据时调用;
//           public void onItemDismiss(RecyclerView.ViewHolder srcHolder) {
//                  int position=srcHolder.getAdapterPosition();
//                  myAdapter.list.remove(position);
//                  myAdapter.notifyItemRemoved(position);
//           }
//       };
//       my_recycle.setOnItemMoveListener(onItemMoveListener);
//   }
   }
  private void back_book(){
        String url="http:192.168.31.83:8080/xhy/Return_book_Servlet";
        helper.postRequest(url, new FormBody.Builder()
                .add("rdID", user_id).add("DateLendAct", record_back_time()), new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Toast.makeText(Main4Activity.this,"网络错误",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Toast.makeText(Main4Activity.this,"提示:"+response.body().string(),Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        },this);
  }
   private String record_back_time(){
           @SuppressLint("SimpleDateFormat")
           String now_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
           return now_time;
       }
    //显示提示消息对话框
    private void showMsgDialog(final int position) {
        //创建AlertDialog构造器Builder对象
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //设置对话框标题
        builder.setTitle("提示信息对话框");
        //设置提示信息
        builder.setMessage("是否确认还书！");
        //设置对话框图标
        builder.setIcon(R.mipmap.ic_launcher);
        //添加确定按钮
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                      myAdapter.list.remove(position);
                      myAdapter.notifyItemRemoved(position);//通知适配器item删除
                      //调用还书
                      back_book();
            }
        });
        //添加取消按钮
        builder.setNegativeButton("取消",null);
        //创建并显示对话框
        builder.show();
    }
    //自定义数据适配器，其实也没多大改变
    class MyAdapter extends SwipeRecyclerView.Adapter<MyAdapter.MyViewholder>{
        //传入list集合对象
        private List<Book> list;
        private  MyAdapter(List<Book> bookList) {
            this.list=bookList;
        }
        @NonNull
        @Override
        public MyAdapter.MyViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v1=View.inflate(Main4Activity.this,R.layout.recyclerview_item_layout,null);
            return new MyViewholder(v1);
        }
        @Override
        public void onBindViewHolder(@NonNull MyAdapter.MyViewholder holder, int position) {
            if(list.size()==0){
                holder.lend_name.setText("您还没有在借的书呢");
                return;
            }
            holder.lend_name.setText(list.get(position).getBkName());
            holder.lend_author.setText(list.get(position).getBkAuthor());
            holder.lend_press.setText(list.get(position).getBkPress());
            holder.lend_status.setText(list.get(position).getBkStatus());
            Glide.with(holder.IV).load("http:192.168.31.83:8080/xhy/"+list.get(position).getBkURL()).into(holder.IV);
        }
        @Override
        public int getItemCount() {
            return list.size();
        }
        //自己定义
        class  MyViewholder extends SwipeRecyclerView.ViewHolder{
            //定义需要复制的控件
           TextView lend_name;
           TextView lend_author;
           TextView lend_press;
           TextView lend_status;
           ImageView IV;
            private MyViewholder(@NonNull View itemView) {
                super(itemView);
                IV = itemView.findViewById(R.id.IV);
                lend_name=itemView.findViewById(R.id.lend_name);
                lend_status =itemView.findViewById(R.id.lend_status);
                lend_press = itemView.findViewById(R.id.lend_press);
                lend_author = itemView.findViewById(R.id.lend_author);
            }
        }
    }
}
