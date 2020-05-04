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
import com.mehul.pandemicfighter.R;

import java.util.ArrayList;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class RetailerActivity extends AppCompatActivity {

    //private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private String AadharNumber;
    private DatabaseReference databaseReference, databaseReference1;
    private ProgressBar spinner;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retailer);

        SessionManager sm = new SessionManager(getApplicationContext());
        HashMap<String, String> details = sm.getUserDetails();
        AadharNumber = details.get("Aadhar");

        spinner = (ProgressBar)findViewById(R.id.progressBar);
        swipeRefreshLayout=findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //code this method
                //updateList(false);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        //code this method
        //updateList(true);

        //mRecyclerView=findViewById(R.id.events_list);
        /// Retailer can see and edit slot lists here here @ mehul
        /////////////////////////////////////////////////////////////////////////////////////////
        /*spinner.setVisibility(View.VISIBLE);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference1 = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("users").child(resultemail).child("events").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    String eventCode = dataSnapshot1.getKey();
                    final Event currentEvent = dataSnapshot1.getValue(Event.class);
                    DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference();
                    databaseReference2.child("events").child(eventCode).child("count").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                            currentEvent.setCount((Long) dataSnapshot2.getValue());
                            list.add(currentEvent);

                            mAdapter=new EventAdapter(getApplicationContext(),list, resultemail, EventManager.this);
                            mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            mRecyclerView.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();
                            spinner.setVisibility(View.GONE);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    // list.add(currentEvent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }); */
///////////////////////////////////////////////////////////////////////////////////////////////////////////
        //Retailer can add slots using this button @ mehul
        FloatingActionButton floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Intent i=new Intent(getApplicationContext(), Welcome.class);
                //startActivity(i);
            }
        });
        //////////////////////////////////////////////////////////////////////////////////////////////
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
    //////////////////function for updating slots
    /*private void updateList(final boolean spin)
    {
        if(spin==true)
            spinner.setVisibility(View.VISIBLE);

        mRecyclerView=findViewById(R.id.events_list);
        final ArrayList<Event> list= new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference1 = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("users").child(resultemail).child("events").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    String eventCode = dataSnapshot1.getKey();
                    final Event currentEvent = dataSnapshot1.getValue(Event.class);
                    DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference();
                    databaseReference2.child("events").child(eventCode).child("count").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                            currentEvent.setCount((Long) dataSnapshot2.getValue());
                            list.add(currentEvent);

                            mAdapter=new EventAdapter(getApplicationContext(),list, resultemail, EventManager.this);
                            mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            mRecyclerView.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();
                            // spinner.setVisibility(View.GONE);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    // list.add(currentEvent);
                }

//                mAdapter=new EventAdapter(getApplicationContext(),list, resultemail, EventManager.this);
//                mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//                mRecyclerView.setAdapter(mAdapter);
//                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if(spin==true)
            spinner.setVisibility(View.GONE);
    }*/
}
