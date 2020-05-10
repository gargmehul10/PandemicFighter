package com.mehul.pandemicfighter.Module2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mehul.pandemicfighter.Module1.SessionManager;
import com.mehul.pandemicfighter.Module3.User;
import com.mehul.pandemicfighter.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Integer> list_count;
    private ArrayList<String> list_timeSlot;
    private ArrayList<String> list_uidDetails;
    private Activity activity;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView timeSlot, count;
        public ImageView uidDetails;

        public MyViewHolder(View itemView) {
            super(itemView);
            timeSlot = itemView.findViewById(R.id.time_slot);
            count = itemView.findViewById(R.id.count);
            uidDetails = itemView.findViewById(R.id.uid_icon);
        }
    }

    public TimeSlotAdapter(Context context, ArrayList<Integer> list_count, ArrayList<String> list_timeSlot, Activity activity, ArrayList<String> list_uidDetails)
    {
        this.context = context;
        this.list_count = list_count;
        this.list_timeSlot = list_timeSlot;
        this.activity = activity;
        this.list_uidDetails = list_uidDetails;
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
        holder.uidDetails.setOnClickListener(v -> {
            Toast.makeText(activity, list_uidDetails.get(position), Toast.LENGTH_LONG).show();

            if(list_uidDetails.equals(""))
            {
                Toast.makeText(activity, "No people in this slot!", Toast.LENGTH_LONG).show();
            }
            else
            {
                // intent to UidDetailsActivity
                Intent i = new Intent(context, UidDetailsActivity.class);
                i.putExtra("UID_Details", list_uidDetails.get(position));
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        });
    }
}