package com.example.chen.ex9;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Chen on 2016/11/25.
 */

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder> {
    private ArrayList<Weather> weather_list;
    private LayoutInflater layoutInflater;

    public interface OnItemClickListener {
        void onItemClick(View view, int postion, Weather item);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public WeatherAdapter(Context context) {
        super();
        this.weather_list = new ArrayList<Weather>();
        this.layoutInflater = LayoutInflater.from(context);
    }

    public void AddWeather(Weather weather) {
        weather_list.add(weather);
        notifyDataSetChanged();
    }

    public void ClearWeather() {
        weather_list.clear();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = layoutInflater.inflate(R.layout.weather_item, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        holder.Date = (TextView)view.findViewById(R.id.Date);
        holder.Weather_description = (TextView)view.findViewById(R.id.Weather);
        holder.Temperature = (TextView) view.findViewById(R.id.Temperature);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        viewHolder.Date.setText(weather_list.get(i).getDate());
        viewHolder.Weather_description.setText(weather_list.get(i).getWeather());
        viewHolder.Temperature.setText(weather_list.get(i).getTemperature());


        if (onItemClickListener != null) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(viewHolder.itemView, i, weather_list.get(i));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return weather_list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView Date, Weather_description, Temperature;
        public ImageView Icon;
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
