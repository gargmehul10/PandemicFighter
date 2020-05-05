package com.mehul.pandemicfighter.Module2;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.mehul.pandemicfighter.R;
import java.util.ArrayList;

public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Integer> list_count;
    private ArrayList<String> list_timeSlot;
    private String resultEmail;
    private Activity activity;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView timeSlot, count;

        public MyViewHolder(View itemView) {
            super(itemView);
            timeSlot = itemView.findViewById(R.id.time_slot);
            count = itemView.findViewById(R.id.count);
        }
    }

    public TimeSlotAdapter(Context context, ArrayList<Integer> list_count, ArrayList<String> list_timeSlot, Activity activity)
    {
        this.context = context;
        this.list_count = list_count;
        this.list_timeSlot = list_timeSlot;
        this.activity = activity;
    }

    @Override
    public TimeSlotAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, final int position) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_timeslot,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return list_count.size();
    }

    @Override
    public void onBindViewHolder(final TimeSlotAdapter.MyViewHolder holder, final int position) {
        final int count = list_count.get(position);
        final String timeSlot = list_timeSlot.get(position);
        holder.timeSlot.setText(String.valueOf(timeSlot));
        holder.count.setText(String.valueOf(count));
    }
}