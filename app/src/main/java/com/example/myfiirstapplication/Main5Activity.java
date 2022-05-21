package com.example.myfiirstapplication;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import android.view.KeyEvent;

import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import android.widget.BaseAdapter;

import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import java.util.ArrayList;

import java.util.List;


import Entity.Book;

import httpUtils.helper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Response;

import static com.example.myfiirstapplication.Main3Activity.user_id;

public class Main5Activity extends AppCompatActivity implements View.OnClickListener {
  public final static String url="http:192.168.43.51:8080/xhy/Find_bookServlet";
  public final static String url2="http:192.168.43.51:8080/xhy/Find_lend_book_Servlet";
  public  final static String bookUrl="http:192.168.10.228:8080/xhy/";
    //声明一个long类型变量：用于存放上一点击“返回键”的时刻
    private long mExitTime;
    List<Book> new_books=new ArrayList<>();//必须得分配地址空间？
   public static String cookie="2";
    String book_all="";
    //Gson对象
    private  Gson gson=new Gson();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final ViewPager viewPager;
        final BottomNavigationView bottomNavigationView;
        super.onCreate(savedInstanceState);
        //设置全屏显示
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_main5);
        viewPager=findViewById(R.id.my_viewPager);
        bottomNavigationView=findViewById(R.id.bottom_navigation);
        Intent intent=this.getIntent();
        //拿到json字符串  第一次登录拿到的所有数据
        book_all=intent.getStringExtra("book_all");
        //转成list<Book>对象
        final List<Book> list= gson.fromJson(book_all, new TypeToken<List<Book>>() {
        }.getType());
        initPager(viewPager);

        //监听滑动
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
            }
            @Override
        public void onPageSelected(int position) {
        switch (position){
            case 0:
       //设置第一个bottommenu选中
       bottomNavigationView.getMenu().getItem(0).setChecked(true);
                break;
                    case 1:
                        final SwipeRecyclerView recyclerView=findViewById(R.id.book_recycler_view);
                        final SwipeRefreshLayout refreshLayout=findViewById(R.id.lend_refresh);
                        refreshLayout.setColorSchemeResources(R.color.red);
                        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                            @Override
                            public void onRefresh() {
                                 Http_Refresh();//更新 newbooks内容
                                Toast.makeText(Main5Activity.this,"正在刷新",Toast.LENGTH_SHORT).show();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(new_books!=null){
                                            if(recyclerView!=null){
                                                System.out.println("可以转换");
                                     Bottom_fragment bottom_fragment= (Bottom_fragment) getViewPagerFragment(R.id.my_viewPager,1);//拿到第二个fragment
                                                bottom_fragment.getMySwipeViewAdapter().list.clear();//拿到原来的适配器，更新list数据源
                                                bottom_fragment.getMySwipeViewAdapter().list.addAll(new_books);
                                                bottom_fragment.getMySwipeViewAdapter().notifyDataSetChanged();//通知更改
//                                                //创建一个新的适配器
//                                        mySwipeViewAdapter=bottom_fragment.new MySwipeViewAdapter(new_books);
//                                        recyclerView.setAdapter(mySwipeViewAdapter);
//                                        mySwipeViewAdapter.notifyDataSetChanged();
                                            }
                                            System.out.println("要刷时候的"+new_books.size());
                                            refreshLayout.setRefreshing(false);
                                            System.out.println("借书刷新所在线程"+Thread.currentThread().getId());//主线程
                                            Toast.makeText(Main5Activity.this,"刷新借书列表成功",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                },2000);
                            }
                        });
                        //设置第二个选中
                        bottomNavigationView.setSelectedItemId(R.id.action_message);
                            break;
                        case 2:
                            bottomNavigationView.setSelectedItemId(R.id.personal_center);
                            break;
                    }
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
//
      //设置bottom navigation监听
        //选中监听事件
      bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
          @Override
          public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
              int itemId = menuItem.getItemId();
              switch (itemId){
                  case R.id.action_search:
                      //点击时候设置对应viewpager
                      viewPager.setCurrentItem(0);
                      Toast.makeText(Main5Activity.this,"别点了",Toast.LENGTH_SHORT).show();
                      break;
                  case R.id.action_message:
                      viewPager.setCurrentItem(1);
//                      Intent intent2=new Intent(Main5Activity.this,Main4Activity.class);
//                      startActivity(intent2);
                     break;
                  case R.id.personal_center:
                      viewPager.setCurrentItem(2);
//                      Intent intent=new Intent(Main5Activity.this,Main3Activity.class);
//                      startActivity(intent);
                      break;
                  default:
                      //执行不到其实
                      throw new IllegalStateException("Unexpected value: " + itemId);
              }
              //返回false代表未选择，不会改变图标与颜色，true才会改变图标颜色
              return true;
          }
      });
    }
    //当前所在Viewpager fragment
private Fragment getViewPagerFragment(int viewpagerId,int position) {
        return getSupportFragmentManager().findFragmentByTag("android:switcher:"
                + viewpagerId + ":" + position);
    }
    //初始化viewpager
private void initPager(ViewPager viewPager){
        List<Fragment>fragmentList=new ArrayList<>();
        TOP_fragment top_fragment= new TOP_fragment();
        Bottom_fragment bottom_fragment=new Bottom_fragment();
        Personal_fragment personal_fragment=new Personal_fragment();
        fragmentList.add(top_fragment);
        fragmentList.add(bottom_fragment);
        fragmentList.add(personal_fragment);

      FragmentManager fragmentManager=getSupportFragmentManager();

      MyViewPagerAdapter myViewPagerAdapter=new MyViewPagerAdapter(fragmentManager,fragmentList);

      viewPager.setAdapter(myViewPagerAdapter);
//设置第一个页面显示 默认是没有数据的
      viewPager.setCurrentItem(0);
}

    //重写onStart方法 fragment生命周期的问题
    /*不能直接在onCreate中通过获得view对象或者直接通过findViewById获得该控件，
    因为fragment与activity关联的时候fragment的函数调用方式为Activity的onCreate，
    fragment的 onAttach onCreateonCreateView onActivityCreated Activity的onStart，
    fragment的 onStartonResume onPause onStop onDestoryView onDestory onDetach。
    也就是说，activity的onCreate在调用的时候，fragment的onCreateView还没有调用，导致没有生成view对象，
    所以如果在activity中的onCreate通过fragment对象的getView返回的是一个null，
    而在activity的onStart调用的时候fragment的onCreateView已经调用，当然就能生成一个view对象了。
    fragment也已经加载到Activity中，
    也就可以直接通过findViewById获取fragment中的组件对象了。*/
    @Override
    //this 加上重写方法
    public void onClick(View view) {

    }
    //数据适配器加载listview内容 基础数据适配器
    class MyListView_Adapter extends BaseAdapter implements Filterable {
       private List<Book> books;//原始数据
        private List<Book> backData;//用于备份原始数据
        MyFilter filter;
        MyListView_Adapter(List<Book> books){
            this.books=books;
            this.backData=books;

        }
        @Override
        public int getCount() {
            return books.size();//list长度
        }
        @Override
        public Object getItem(int position) {
            return books.get(position);//一个item
        }
        @Override
        public long getItemId(int position) {//item位置
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup){
         //Listview优化写法
            Viewholder holder;
            if (convertView == null) {
                convertView = View.inflate(Main5Activity.this, R.layout.listview_item_layout, null);
                holder = new Viewholder();
                holder.name = convertView.findViewById(R.id.name);
                holder.ISBN = convertView.findViewById(R.id.bkID);
                holder.author = convertView.findViewById(R.id.author);
                holder.press = convertView.findViewById(R.id.press);
                holder.price = convertView.findViewById(R.id.price);
                holder.status = convertView.findViewById(R.id.status);
                holder.IV = convertView.findViewById(R.id.IV);
                convertView.setTag(holder);
            } else {
                holder = (Viewholder) convertView.getTag();
            }
            if(books!=null&&books.size()>0){
                holder.name.setText(books.get(position).getBkName());
                holder.ISBN.setText(books.get(position).getBkID());
                holder.author.setText(books.get(position).getBkAuthor());
                holder.press.setText(books.get(position).getBkPress());
                holder.price.setText(books.get(position).getBkPrice());
                if(books.get(position).getBkStatus().equals("借出"))
                {
                    holder.status.setText(books.get(position).getBkStatus());
                    holder.status.setTextColor(Color.RED);
                }
                else {
                    holder.status.setText(books.get(position).getBkStatus());
                }
                //glide 第三方图片加载库，不用原生的因为那个setbackgroundResoure 只能接受int参数
                Glide.with(holder.IV).load(bookUrl+books.get(position).getBkURL()).into(holder.IV);
            }
            return convertView;
        }
        @Override
        public Filter getFilter() {
             if(filter==null){
                 filter=new MyFilter();
             }
            return filter;
        }
        class MyFilter extends Filter{
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults=new FilterResults();
                List<Book> search_books;
                ////当过滤关键词未空我们显示所有数据
                if(TextUtils.isEmpty(charSequence)){
                    search_books=backData;
                }else {
                    search_books=new ArrayList<>();
                    for(Book book:backData){
                        if(book.getBkName().contains(charSequence)||
                                book.getBkID().contains(charSequence) ||book.getBkStatus().contains(charSequence)){
                            search_books.add(book);//添加进来
                        }
                    }
                }
                filterResults.values = search_books;//将得到的集合保存到FilterResult 的value变量中
                filterResults.count = search_books.size();//将集合的大小保存到FilterResult的count变量中
                return filterResults;
            }
            @Override
            //这个方法告诉适配器更新界面
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                //noinspection unchecked
                 books=(List<Book>)filterResults.values;
                if(filterResults.count>0){
                       notifyDataSetChanged();
                    }
                else {
                    notifyDataSetInvalidated();
                }
            }
        }
    }
    static class Viewholder {
        TextView name;
        TextView ISBN;
        TextView author;
        TextView press;
        TextView price;
        TextView status;
        ImageView IV;
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
    private void Http_Refresh(){

        helper.postRequest(url2, new FormBody.Builder().add("rdID",user_id), new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String new_all_books=response.body().string();
                System.out.println("接受的"+new_all_books);
                new_books= new Gson().fromJson(new_all_books, new TypeToken<List<Book>>() {
                }.getType());
                System.out.println("http运行线程"+Thread.currentThread().getId());
                System.out.println("赋值的"+new_books);
            }
        },this);
    }

}
