package com.example.myfiirstapplication;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import static com.example.myfiirstapplication.Main3Activity.UserIamge;
import static com.example.myfiirstapplication.Main3Activity.user_id;
import static com.example.myfiirstapplication.Main3Activity.user_name;

public class Personal_fragment extends Fragment {
    private RelativeLayout relativeLayout;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.personal_center, container, false);
        ImageView imageView = view.findViewById(R.id.user_picture);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(),"知道你长得帅",Toast.LENGTH_SHORT).show();
            }
        });
        TextView textView = view.findViewById(R.id.login_name);
        textView.setText(user_name);
        relativeLayout = view.findViewById(R.id.user_background);
        //给相对布局加载图片
        Glide.with(this)
                .load(UserIamge + user_id + ".png").apply(RequestOptions.centerCropTransform())
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
        Glide.with(imageView).load(UserIamge + user_id + ".png").apply(RequestOptions.bitmapTransform(new CircleCrop())).into(imageView);
        return view;
    }
}
