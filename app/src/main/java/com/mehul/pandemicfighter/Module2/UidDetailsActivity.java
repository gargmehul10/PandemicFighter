package com.mehul.pandemicfighter.Module2;

import android.os.Bundle;
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
import com.mehul.pandemicfighter.Module1.SessionManager;
import com.mehul.pandemicfighter.Module3.User;
import com.mehul.pandemicfighter.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

public class UidDetailsActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private ProgressBar spinner;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String currUidDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uid_details_view);

        currUidDetails = getIntent().getExtras().getString("UID_Details");

        spinner = (ProgressBar) findViewById(R.id.progressBar);
        swipeRefreshLayout=findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateList(false);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        updateList(true);

        mRecyclerView=findViewById(R.id.uid_list);

        spinner.setVisibility(View.VISIBLE);

        SessionManager sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> details = sessionManager.getUserDetails();
        String state = details.get("state");
        String district = details.get("district");

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Users").child(state).child(district);

        ArrayList<String> list_aadhaarNumber = new ArrayList<>();
        ArrayList<String> list_name = new ArrayList<>();

        StringTokenizer k = new StringTokenizer(currUidDetails);
        int countTokens = k.countTokens(),i1;
        for(i1=0;i1<countTokens;i1++)
        {
            String currUid = k.nextToken();

            myRef.child(currUid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User currUser = dataSnapshot.getValue(User.class);
                    list_aadhaarNumber.add(currUser.getAadharNumber());
                    list_name.add(currUser.getName());

                    mAdapter=new UidDetailsAdapter(getApplicationContext(), list_name, list_aadhaarNumber, UidDetailsActivity.this, state, district);
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

    private void updateList(final boolean spin)
    {
        if(spin==true)
            spinner.setVisibility(View.VISIBLE);

        mRecyclerView=findViewById(R.id.uid_list);

        spinner.setVisibility(View.VISIBLE);

        SessionManager sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> details = sessionManager.getUserDetails();
        String state = details.get("state");
        String district = details.get("district");

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Users").child(state).child(district);

        ArrayList<String> list_aadhaarNumber = new ArrayList<>();
        ArrayList<String> list_name = new ArrayList<>();

        StringTokenizer k = new StringTokenizer(currUidDetails);
        int countTokens = k.countTokens(),i1;
        for(i1=0;i1<countTokens;i1++)
        {
            String currUid = k.nextToken();

            myRef.child(currUid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User currUser = dataSnapshot.getValue(User.class);
                    list_aadhaarNumber.add(currUser.getAadharNumber());
                    list_name.add(currUser.getName());

                    mAdapter=new UidDetailsAdapter(getApplicationContext(), list_name, list_aadhaarNumber, UidDetailsActivity.this, state, district);
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

        if(spin==true)
            spinner.setVisibility(View.GONE);
    }
}