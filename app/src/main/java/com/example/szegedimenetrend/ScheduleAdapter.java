package com.example.szegedimenetrend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> implements Filterable {

    private ArrayList<Schedule> mScheduleData = new ArrayList<>();
    private ArrayList<Schedule> mScheduleDataAll = new ArrayList<>();
    private Context mContext;
    private int lastPosition = -1;

    public ScheduleAdapter(ArrayList<Schedule> mScheduleData, Context mContext) {
        this.mScheduleData = mScheduleData;
        this.mScheduleDataAll = mScheduleData;
        this.mContext = mContext;
    }


    @Override
    public ScheduleAdapter.ViewHolder onCreateViewHolder(
            ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.route, parent, false));
    }

    @Override
    public void onBindViewHolder( ScheduleAdapter.ViewHolder holder, int position) {
        Schedule currentSchedule = mScheduleData.get(position);
        holder.bindTo(currentSchedule);

    }

    @Override
    public int getItemCount() {
        return mScheduleData.size();
    }

    @Override
    public Filter getFilter() {
        return null;
    }

    class ViewHolder extends RecyclerView.ViewHolder {


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void bindTo(Schedule currentSchedule) {
        }
    }


}
