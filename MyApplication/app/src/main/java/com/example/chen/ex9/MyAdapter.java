package com.example.chen.ex9;

import android.content.ContentValues;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chen on 2016/11/24.
 */

public class MyAdapter extends BaseAdapter {
    private ArrayList<Indicator> indicators;
    private Context context;
    public MyAdapter(Context context) {
        indicators = new ArrayList<>();
        indicators.add(new Indicator("紫外线指数", "暂无数据"));
        indicators.add(new Indicator("感冒指数", "暂无数据"));
        indicators.add(new Indicator("穿衣指数", "暂无数据"));
        indicators.add(new Indicator("洗车指数", "暂无数据"));
        indicators.add(new Indicator("运动指数", "暂无数据"));
        indicators.add(new Indicator("空气污染指数", "暂无数据"));
        this.context = context;
    }

    public void ResetData() {
        for (int i = 0; i < indicators.size(); i++) {
            SetData(i, "暂无数据");
        }
        notifyDataSetChanged();
    }

    public void SetData(int postion, String value) {
        indicators.get(postion).SetValue(value);
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return indicators.size();
    }

    @Override
    public Object getItem(int position) {
        return indicators.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.indicatorlayout, null);
        }
        TextView IndicatorName = (TextView)convertView.findViewById(R.id.IndicatorName);
        TextView IndicatorValue = (TextView)convertView.findViewById(R.id.IndicatorValue);
        IndicatorName.setText(indicators.get(position).getName());
        IndicatorValue.setText(indicators.get(position).getValue());
        return convertView;
    }
}
