package com.lingzhuo.coolweather01.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lingzhuo.coolweather01.R;

import java.util.List;

/**
 * Created by Wang on 2016/4/19.
 */
public class MyListAdapter extends BaseAdapter {
    private List<String> dataList;
    private Context context;

    public MyListAdapter(List<String> dataList_countyName, Context context) {
        this.dataList = dataList_countyName;
        this.context = context;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView==null){
            viewHolder=new ViewHolder();
            convertView= LayoutInflater.from(context).inflate(R.layout.layout_item_city,null);
            viewHolder.textView= (TextView) convertView.findViewById(R.id.textView_city);
            convertView.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) convertView.getTag();
        }
        viewHolder.textView.setText(dataList.get(position));
        return convertView;
    }

    class ViewHolder{
        TextView textView;
    }

}
