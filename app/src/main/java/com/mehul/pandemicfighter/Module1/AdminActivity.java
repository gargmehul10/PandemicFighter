package com.mehul.pandemicfighter.Module1;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mehul.pandemicfighter.Module2.AboutUs;
import com.mehul.pandemicfighter.Module3.User;
import com.mehul.pandemicfighter.R;
import java.util.ArrayList;
import java.util.HashMap;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class AdminActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private DatabaseReference myRef;
    private ProgressBar spinner;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        getSupportActionBar().setTitle("Admin");

        SessionManager sm = new SessionManager(getApplicationContext());
        HashMap<String, String> details = sm.getUserDetails();
        myRef = FirebaseDatabase.getInstance().getReference("Users").child(details.get("state")).child(details.get("district")).child("admin");

        spinner = (ProgressBar)findViewById(R.id.progressBar);
        swipeRefreshLayout=findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateList(false);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        updateList(true);

        mRecyclerView=findViewById(R.id.requests_list);
        final ArrayList<String> list_aadhaar= new ArrayList<>();
        final ArrayList<String> list_address= new ArrayList<>();
        final ArrayList<String> list_idUrl= new ArrayList<>();

        spinner.setVisibility(View.VISIBLE);
        myRef.child("requests").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    String aadhaar = dataSnapshot1.getKey();
                    String idUrl = dataSnapshot1.getValue(String.class);

                    DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("Users").child(details.get("state")).child(details.get("district"));
                    databaseReference2.child(aadhaar).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                            list_address.add(dataSnapshot2.getValue(User.class).getAddress());
                            list_aadhaar.add(aadhaar);
                            list_idUrl.add(idUrl);
                            mAdapter=new AddressRequestAdapter(getApplicationContext(),list_aadhaar,list_address,list_idUrl,AdminActivity.this);
                            mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            mRecyclerView.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();
                            spinner.setVisibility(View.GONE);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.admin_main_menu, menu);
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
                Intent i = new Intent(getApplicationContext(), AboutUs.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateList(final boolean spin)
    {
        if(spin==true)
            spinner.setVisibility(View.VISIBLE);

        SessionManager sm = new SessionManager(getApplicationContext());
        HashMap<String, String> details = sm.getUserDetails();
        myRef = FirebaseDatabase.getInstance().getReference("Users").child(details.get("state")).child(details.get("district")).child("admin");

        mRecyclerView=findViewById(R.id.requests_list);
        final ArrayList<String> list_aadhaar= new ArrayList<>();
        final ArrayList<String> list_address= new ArrayList<>();
        final ArrayList<String> list_idUrl= new ArrayList<>();

        spinner.setVisibility(View.VISIBLE);
        myRef.child("requests").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    String aadhaar = dataSnapshot1.getKey();
                    String idUrl = dataSnapshot1.getValue(String.class);

                    DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("Users").child(details.get("state")).child(details.get("district"));
                    databaseReference2.child(aadhaar).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                            list_address.add(dataSnapshot2.getValue(User.class).getAddress());
                            list_aadhaar.add(aadhaar);
                            list_idUrl.add(idUrl);
                            mAdapter=new AddressRequestAdapter(getApplicationContext(),list_aadhaar,list_address,list_idUrl,AdminActivity.this);
                            mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            mRecyclerView.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();
                            spinner.setVisibility(View.GONE);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if(spin==true)
            spinner.setVisibility(View.GONE);
    }
}