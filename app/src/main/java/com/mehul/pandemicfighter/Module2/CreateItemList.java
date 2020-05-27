package com.mehul.pandemicfighter.Module2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mehul.pandemicfighter.Module1.SessionManager;
import com.mehul.pandemicfighter.Module3.TimeSlot;
import com.mehul.pandemicfighter.Module3.Transaction;
import com.mehul.pandemicfighter.Module3.User;
import com.mehul.pandemicfighter.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class CreateItemList extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private ProgressBar spinner;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String retailerUID, state, district, aadhar;
    private int slotId;
    private TimeSlot timeSlot;
    private DatabaseReference myRef;
    private Transaction latestTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_list_view);

        retailerUID = getIntent().getExtras().getString("UID_retailer");
        slotId = getIntent().getExtras().getInt("slotId");
        Bundle bundle = getIntent().getExtras();
        timeSlot = bundle.getParcelable("timeslot");
        spinner = (ProgressBar) findViewById(R.id.progressBarFin);
        swipeRefreshLayout=findViewById(R.id.swiperefreshFin);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateList(false);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        //updateList(true);
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> details = sessionManager.getUserDetails();
        state = details.get("state");
        district = details.get("district");
        aadhar = details.get("Aadhar");
        myRef = FirebaseDatabase.getInstance().getReference("Users").child(state).child(district);
        myRef.child("Transactions").child(aadhar).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                latestTransaction = dataSnapshot.getValue(Transaction.class);
                if(latestTransaction != null){
                    TransactionBranch1();
                }
                else{
                    TransactionBranch2();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void createSlot(){
        if(slotId == 1)
        {
            if(timeSlot.getSlot1() < 20)
            {
                // update count
                TimeSlot updated_timeSlot = new TimeSlot(timeSlot.getSlot1() + 1, timeSlot.getSlot2(), timeSlot.getSlot3(), timeSlot.getSlot4(), timeSlot.getSlot1UID() + aadhar + " ", timeSlot.getSlot2UID(), timeSlot.getSlot3UID(), timeSlot.getSlot4UID());
                myRef.child(retailerUID).child("Slots").setValue(updated_timeSlot);

                Toast.makeText(CreateItemList.this, "Slot1 (8:00 - 9:00) confirmed!", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(CreateItemList.this, "Slot full. Try again!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(CreateItemList.this,ConsumerActivity.class));
            }
        }
        else if(slotId == 2)
        {
            if(timeSlot.getSlot2() < 20)
            {
                // update count
                TimeSlot updated_timeSlot = new TimeSlot(timeSlot.getSlot1(), timeSlot.getSlot2() + 1, timeSlot.getSlot3(), timeSlot.getSlot4(), timeSlot.getSlot1UID(), timeSlot.getSlot2UID() + aadhar+ " ", timeSlot.getSlot3UID(), timeSlot.getSlot4UID());
                myRef.child(retailerUID).child("Slots").setValue(updated_timeSlot);

                Toast.makeText(CreateItemList.this, "Slot2 (9:00 - 10:00) confirmed!", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(CreateItemList.this, "Slot full. Try again!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(CreateItemList.this,ConsumerActivity.class));
            }
        }
        else if(slotId == 3)
        {
            if(timeSlot.getSlot3() < 20)
            {
                // update count
                TimeSlot updated_timeSlot = new TimeSlot(timeSlot.getSlot1(), timeSlot.getSlot2(), timeSlot.getSlot3() + 1, timeSlot.getSlot4(), timeSlot.getSlot1UID(), timeSlot.getSlot2UID(), timeSlot.getSlot3UID() + aadhar + " ", timeSlot.getSlot4UID());
                myRef.child(retailerUID).child("Slots").setValue(updated_timeSlot);

                Toast.makeText(CreateItemList.this, "Slot3 (10:00 - 11:00) confirmed!", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(CreateItemList.this, "Slot full. Try again!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(CreateItemList.this,ConsumerActivity.class));
            }
        }
        else if(slotId == 4)
        {
            if(timeSlot.getSlot4() < 20)
            {
                // update count
                TimeSlot updated_timeSlot = new TimeSlot(timeSlot.getSlot1(), timeSlot.getSlot2(), timeSlot.getSlot3(), timeSlot.getSlot4() + 1, timeSlot.getSlot1UID(), timeSlot.getSlot2UID(), timeSlot.getSlot3UID(), timeSlot.getSlot4UID() + aadhar + " ");
                myRef.child(retailerUID).child("Slots").setValue(updated_timeSlot);

                Toast.makeText(CreateItemList.this, "Slot4 (11:00 - 12:00) confirmed!", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(CreateItemList.this, "Slot full. Try again!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(CreateItemList.this,ConsumerActivity.class));
            }
        }

    }
    private void TransactionBranch1(){
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(Calendar.getInstance().getTime());
        String lastTime = latestTransaction.getTimestamp();
        SimpleDateFormat parser = new SimpleDateFormat("dd/MM/yyyy");
        Date date1 = null;
        try {
            date1 = parser.parse(formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date2 = null;
        try {
            date2 = parser.parse(lastTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long difference = date1.getTime()-date2.getTime();
        long differenceDates = difference / (24 * 60 * 60 * 1000);
        if(differenceDates<7){
            Toast.makeText(getApplicationContext(), "Please wait for "+(7-differenceDates)+"days",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(CreateItemList.this,ConsumerActivity.class));
        }
        else{
            createSlot();
            myRef.child("Transactions").child(aadhar).setValue(latestTransaction);
            updateList(true);
        }
    }
    private void TransactionBranch2(){
        createSlot();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(Calendar.getInstance().getTime());
        Toast.makeText(getApplicationContext(),"First Time!!",Toast.LENGTH_SHORT).show();
        latestTransaction = new Transaction(formattedDate,0.0,0.0,0.0,0.0);
        myRef.child("Transactions").child(aadhar).setValue(latestTransaction);
        updateList(true);
    }
    private void updateList(final boolean spin)
    {
        if(spin==true)
            spinner.setVisibility(View.VISIBLE);

        mRecyclerView=findViewById(R.id.item_list);

        spinner.setVisibility(View.VISIBLE);
        myRef.child("Transactions").child(aadhar).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Transaction currTransaction = dataSnapshot.getValue(Transaction.class);

                mAdapter=new ListItemAdapter(getApplicationContext(), currTransaction, CreateItemList.this, state, district, aadhar);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                spinner.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if(spin==true)
            spinner.setVisibility(View.GONE);
    }

    public void AddTrasaction(View view) {
        Toast.makeText(getApplicationContext(),"Items sent to shop", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(CreateItemList.this, ConsumerActivity.class));
    }
}