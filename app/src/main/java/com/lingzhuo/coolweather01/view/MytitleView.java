package com.lingzhuo.coolweather01.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.lingzhuo.coolweather01.R;

/**
 * 我的自定义的标题栏控件
 * Created by Wang on 2016/4/20.
 */
public class MytitleView extends LinearLayout {
    public MytitleView(final Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.layout_title,this);
    }
}
