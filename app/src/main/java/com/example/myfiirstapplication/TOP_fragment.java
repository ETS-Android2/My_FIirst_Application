package com.example.myfiirstapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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

import static com.example.myfiirstapplication.Main5Activity.url;

public class TOP_fragment extends Fragment  {
    private List<Book> bookList=new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HttpRefresh();
    }

    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View convertView= inflater.inflate(R.layout.top_layout, container, false);
        final ListView listView = convertView.findViewById(R.id.my_list);
        final Button button=convertView.findViewById(R.id.btn_submmit);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(),"努力搜索中",Toast.LENGTH_SHORT).show();
            }
        });
       listView.setTextFilterEnabled(true);//开启过滤
       final MyListViewAdapter myListViewAdapter = new MyListViewAdapter(bookList);
        listView.setAdapter(myListViewAdapter);
        SearchView searchView = convertView.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String newText) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                if(TextUtils.isEmpty(newText)){
                    listView.clearTextFilter();
                    myListViewAdapter.getFilter().filter("");
                }else {
                    myListViewAdapter.getFilter().filter(newText);
                }
                return true;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long positionID) {
                Toast.makeText(getActivity(),"即将前往详情页",Toast.LENGTH_SHORT).show();
                Intent info_intent=new Intent(getActivity(),Main2Activity.class);
                //点击的item传入详情activity
                Book book = bookList.get(position);
                String clicked_item = new Gson().toJson(book);
                info_intent.putExtra("book_clicked",clicked_item);
                startActivity(info_intent);
            }
        });
        final SwipeRefreshLayout swipeRefreshLayout=convertView.findViewById(R.id.refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.red);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                HttpRefresh();
                Toast.makeText(getActivity(),"正在刷新",Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(bookList!=null){
                            myListViewAdapter.books.clear();
                            myListViewAdapter.books.addAll(bookList);
                            myListViewAdapter.notifyDataSetChanged();
                            swipeRefreshLayout.setRefreshing(false);
                            Toast.makeText(getContext(),"刷新书籍列表成功",Toast.LENGTH_SHORT).show();
                        }
                    }
                },3000);
            }
        });
        return convertView;
    }

    private void HttpRefresh() {
        helper.postRequest(url, new FormBody.Builder(), new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String new_all_books=response.body().string();
                System.out.println("接受的"+new_all_books);
                bookList= new Gson().fromJson(new_all_books, new TypeToken<List<Book>>() {
                }.getType());
                System.out.println("http2运行线程"+Thread.currentThread().getId());
                System.out.println("赋值的"+bookList);
            }
        }, getContext());
    }

    class MyListViewAdapter extends BaseAdapter implements Filterable {
    private List<Book> books;//原始数据
    private List<Book> backData;//用于备份原始数据
    MyFilter filter;
    MyListViewAdapter(List<Book> books) {
        this.books = books;
        this.backData=books;
    }
    @Override
    public int getCount() {
        return books.size();
    }

    @Override
    public Object getItem(int position) {

        return books.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position ;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        MyViewHolder2 holder;
        if(convertView==null){
            convertView = View.inflate(getContext(), R.layout.listview_item_layout, null);
             holder=new MyViewHolder2();
            holder.name = convertView.findViewById(R.id.name);
            holder.ISBN = convertView.findViewById(R.id.bkID);
            holder.author = convertView.findViewById(R.id.author);
            holder.press = convertView.findViewById(R.id.press);
            holder.price = convertView.findViewById(R.id.price);
            holder.status = convertView.findViewById(R.id.status);
            holder.IV = convertView.findViewById(R.id.IV);
            convertView.setTag(holder);
        }
        else {
            holder = (MyViewHolder2)convertView.getTag();
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
            Glide.with(holder.IV).load("http:192.168.31.83:8080/xhy/"+books.get(position).getBkURL()).into(holder.IV);
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
    static class MyViewHolder2{
        TextView name;
        TextView ISBN;
        TextView author;
        TextView press;
        TextView price;
        TextView status;
        ImageView IV;
    }
}
