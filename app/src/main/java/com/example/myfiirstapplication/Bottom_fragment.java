package com.example.myfiirstapplication;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import Entity.Book;
import httpUtils.helper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.myfiirstapplication.Main3Activity.user_id;
import static com.example.myfiirstapplication.Main5Activity.cookie;


public class Bottom_fragment extends Fragment {
  private   SwipeRecyclerView swipeRecyclerView;
    private   MySwipeViewAdapter mySwipeViewAdapter;
    MySwipeViewAdapter getMySwipeViewAdapter() {
        return mySwipeViewAdapter;
    }
    public void setMySwipeViewAdapter(MySwipeViewAdapter mySwipeViewAdapter) {
        this.mySwipeViewAdapter = mySwipeViewAdapter;
    }
 //不new会报错
    private List<Book> books=new ArrayList<>();
//fragment生命周期重写oncreate方法
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SyncPost();//此时books数据已经更新了
    }

    @Nullable
    @Override
    public  View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         View convertView=inflater.inflate(R.layout.lend_book_info, container, false);
         swipeRecyclerView= convertView.findViewById(R.id.book_recycler_view);
           initMenu();//必须在下面之前设置
         swipeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
         Log.i("碎片的",""+books.size());
          mySwipeViewAdapter = new MySwipeViewAdapter(books);
         swipeRecyclerView.setAdapter(mySwipeViewAdapter);
         return convertView;
    }
    @Override
    public void onStart() {
        super.onStart();
    }
    class MySwipeViewAdapter extends RecyclerView.Adapter<MySwipeViewAdapter.MyViewholder>{
        //传入list集合对象
        List<Book> list;
        //构造函数
        MySwipeViewAdapter(List<Book> bookList) {
            this.list=bookList;
        }
//        public void delete(List<Book> bookList ,int position){
//            this.notifyItemMoved(p);
//            this.list=bookList;
//        }
        @NonNull
        @Override
        public MySwipeViewAdapter.MyViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v1=View.inflate(getContext(),R.layout.recyclerview_item_layout,null);
            return new MyViewholder(v1);
        }
        @Override
        public void onBindViewHolder(@NonNull MySwipeViewAdapter.MyViewholder holder, int position) {
            if(list.size()==0){
                holder.lend_name.setText("您还没有在借的书呢");
                holder.lend_name.setTextSize(20);
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
            Log.i("fragment book长度",""+list.size());
            return list.size();
        }
        //自己定义
        class  MyViewholder extends RecyclerView.ViewHolder{
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
    private void initMenu() {
        //添加右边的还书菜单
        SwipeMenuCreator menuCreator = new SwipeMenuCreator() {
            @Override
            public void onCreateMenu(SwipeMenu leftMenu, SwipeMenu rightMenu, int position) {
                int width = 400;
                int height = ViewGroup.LayoutParams.MATCH_PARENT;
                SwipeMenuItem return_book = new SwipeMenuItem(getContext())
                        .setText("一键还书")
                        .setTextSize(20)
                        .setBackgroundColor(Color.RED)
                        .setTextColor(Color.WHITE)
                        .setHeight(height).setWidth(width);
                //添加右边的还书菜单
                rightMenu.addMenuItem(return_book);
            }
        };
        swipeRecyclerView.setSwipeMenuCreator(menuCreator);

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
        swipeRecyclerView.setOnItemMenuClickListener(itemMenuClickListener);
    }
    private void showMsgDialog(final int position) {
        //创建AlertDialog构造器Builder对象
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
                String  bkID=mySwipeViewAdapter.list.get(position).getBkID();//删除之前保存书号
                System.out.println("点击之前书号"+bkID);
                mySwipeViewAdapter.list.remove(position);
                mySwipeViewAdapter.notifyItemRemoved(position);
                //调用还书
                back_book(bkID);//逻辑其实可以改一下，先判断返回是真还是假在执行删除list
            }
        });
        //添加取消按钮
        builder.setNegativeButton("取消",null);
        //创建并显示对话框
        builder.show();
    }
    private void back_book( String bkID){
        System.out.println("借收到的"+bkID);
        String url="http:192.168.31.83:8080/xhy/Return_book_Servlet";
        helper.postRequest(url, new FormBody.Builder()
                .add("rdID",user_id)
                .add("bkID", bkID).add("DateLendAct", record_back_time()), new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Toast.makeText(getContext(),"网络错误",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Toast.makeText(getContext(),"提示:"+response.body().string(),Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        },getContext());
    }
    private String record_back_time(){
        @SuppressLint("SimpleDateFormat")
        String now_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
        return now_time;
    }
    private void SyncPost() {
        String url = "http:192.168.31.83:8080/xhy/Find_lend_book_Servlet";
        helper.postRequest(url, new FormBody.Builder().add("rdID", user_id), new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
               Log.i("errors","请求失败");
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String lend_books = response.body().string();
                if(lend_books.length()!=11){
                    books= new Gson().fromJson(lend_books,new TypeToken<List<Book>>(){}.getType());//这里不需要去主线程更新books
                    System.out.println("bottom"+books);
                }
            }
        },getContext());
    }

}
