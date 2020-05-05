package com.mehul.pandemicfighter.Module2;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mehul.pandemicfighter.Module1.LoginActivity;
import com.mehul.pandemicfighter.Module1.SessionManager;
import com.mehul.pandemicfighter.Module3.TimeSlot;
import com.mehul.pandemicfighter.R;

import java.util.ArrayList;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class RetailerActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private DatabaseReference myRef;
    private ProgressBar spinner;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retailer);

        SessionManager sm = new SessionManager(getApplicationContext());
        HashMap<String, String> details = sm.getUserDetails();
        myRef = FirebaseDatabase.getInstance().getReference("Users").child(details.get("state")).child(details.get("district")).child(details.get("Aadhar"));

        spinner = (ProgressBar)findViewById(R.id.progressBar);
        swipeRefreshLayout=findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateList(false);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        mRecyclerView=findViewById(R.id.slot_list);
        final ArrayList<Integer> list_count= new ArrayList<>();
        final ArrayList<String> list_timeSlot= new ArrayList<>();

        spinner.setVisibility(View.VISIBLE);

        list_timeSlot.add("8:00 - 9:00");
        list_timeSlot.add("9:00 - 10:00");
        list_timeSlot.add("10:00 - 11:00");
        list_timeSlot.add("11:00 - 12:00");

        myRef.child("Slots").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TimeSlot timeSlot = dataSnapshot.getValue(TimeSlot.class);

                list_count.add(timeSlot.getSlot1());
                list_count.add(timeSlot.getSlot2());
                list_count.add(timeSlot.getSlot3());
                list_count.add(timeSlot.getSlot4());

                mAdapter=new TimeSlotAdapter(getApplicationContext(),list_count, list_timeSlot, RetailerActivity.this);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                spinner.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.logout:
                new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Are you sure you want to logout?")
                        .setConfirmText("Yes")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                SessionManager sm = new SessionManager(getApplicationContext());
                                sm.logoutUser();
                                Intent i=new Intent(getApplicationContext(), LoginActivity.class);
                                i.addFlags(i.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                                finish();
                                sDialog.dismissWithAnimation();
                            }
                        })
                        .setCancelButton("No", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                            }
                        })
                        .show();
                return true;
            case R.id.action_settings:
                // Change this about us
               // Intent i = new Intent(getApplicationContext(), AboutUs.class);
                //startActivity(i);
                return true;
            case R.id.mark:
                Intent i1 = new Intent(getApplicationContext(), MapsMarkShop.class);
                startActivity(i1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateList(final boolean spin)
    {
        if(spin==true)
            spinner.setVisibility(View.VISIBLE);

        mRecyclerView=findViewById(R.id.slot_list);
        final ArrayList<Integer> list_count= new ArrayList<>();
        final ArrayList<String> list_timeSlot= new ArrayList<>();

        spinner.setVisibility(View.VISIBLE);

        list_timeSlot.add("8:00 - 9:00");
        list_timeSlot.add("9:00 - 10:00");
        list_timeSlot.add("10:00 - 11:00");
        list_timeSlot.add("11:00 - 12:00");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TimeSlot timeSlot = dataSnapshot.getValue(TimeSlot.class);

                list_count.add(timeSlot.getSlot1());
                list_count.add(timeSlot.getSlot2());
                list_count.add(timeSlot.getSlot3());
                list_count.add(timeSlot.getSlot4());

                mAdapter=new TimeSlotAdapter(getApplicationContext(),list_count, list_timeSlot, RetailerActivity.this);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                // spinner.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        if(spin==true)
            spinner.setVisibility(View.GONE);
    }
}
