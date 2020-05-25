package com.mehul.pandemicfighter.Module1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mehul.pandemicfighter.Module2.ConsumerActivity;
import com.mehul.pandemicfighter.Module2.RetailerActivity;
import com.mehul.pandemicfighter.Module2.ngoActivity;
import com.mehul.pandemicfighter.Module3.User;
import com.mehul.pandemicfighter.R;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    //defining views
    private EditText editTextAdhar;
    private AutoCompleteTextView stateinput;
    private AutoCompleteTextView districtInput;
    private Spinner types;
    private EditText aadhar;
    DatabaseReference databaseUser;
    //progress dialog
    private ProgressDialog progressDialog;
    private String[] states = {"Andhra Pradesh",
            "Arunachal Pradesh","Assam","Bihar","Chhattisgarh","Goa","Gujarat","Haryana","Himachal Pradesh",
            "Jammu and Kashmir","Jharkhand","Karnataka","Kerala","Madhya Pradesh","Maharashtra","Manipur","Meghalaya",
            "Mizoram","Nagaland","Odisha","Punjab","Rajasthan","Sikkim","Tamil Nadu","Telangana","Tripura","Uttarakhand",
            "Uttar Pradesh","West Bengal","Andaman and Nicobar Islands","Chandigarh","Dadra and Nagar Haveli",
            "Daman and Diu","Delhi","Lakshadweep","Puducherry"};
    private String[] districts = {};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, states);
        //ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,
          //      android.R.layout.simple_dropdown_item_1line, districts);
        stateinput = (AutoCompleteTextView)findViewById(R.id.stateInp);
        districtInput = (AutoCompleteTextView)findViewById(R.id.districtInp);
        aadhar = findViewById(R.id.editTextAadhar);
        stateinput.setThreshold(1);
        //districtInput.setThreshold(3);
        stateinput.setAdapter(adapter1);
        //stateinput.setAdapter(adapter2);
        types = findViewById(R.id.typesLogin);
        types.setOnItemSelectedListener(this);
    }

    public void SignupUser(View view) {
        Intent i=new Intent(LoginActivity.this, UserRegisterActivity.class);
        startActivity(i);
        finish();
    }

    public void SignInButton(View view) {
        databaseUser = FirebaseDatabase.getInstance().getReference().child("Users");
        String dis = districtInput.getText().toString().trim().toLowerCase();
        String state = stateinput.getText().toString().trim().toLowerCase();
        String number = aadhar.getText().toString().trim();
        String role = types.getSelectedItem().toString().trim().toLowerCase();

        if(role.equals("admin")){
            SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("Logging");
            pDialog.setCancelable(false);
            pDialog.show();
            databaseUser.child(state).child(dis).child("admin").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        // admin exists
                        DatabaseReference newDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(state).child(dis);
                        newDatabaseUser.child("admin").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {

                                for(DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()){
                                    String key = dataSnapshot2.getValue(String.class);

                                    if(key.equals(number)){
                                        pDialog.cancel();
                                        SessionManager sessionManager = new SessionManager(getApplicationContext());
                                        sessionManager.createLoginSession(number, role, state, dis);

                                        startActivity(new Intent(LoginActivity.this, AdminActivity.class));
                                        finish();
                                    }
                                    else{
                                        pDialog.cancel();
                                        Toast.makeText(LoginActivity.this, "Admin passkey invalid!", Toast.LENGTH_LONG).show();
                                    }

                                    // iterating loop only once since we only want key from admin
                                    break;
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                    else{
                        pDialog.cancel();
                        Toast.makeText(LoginActivity.this, "Admin not registered!", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else {
            databaseUser.child(state).child(dis).child(number).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                        // check if address contains -9999
                        String address = dataSnapshot.getValue(User.class).getAddress();
                        if (address.substring(0, 5).equals("-9999")) {
                            new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Admin verification still pending!")
                                    .show();
                        } else {
                            SessionManager sessionManager = new SessionManager(getApplicationContext());
                            sessionManager.createLoginSession(number, role, state, dis);
                            if (role.equals("retailer")) {
                                startActivity(new Intent(LoginActivity.this, RetailerActivity.class));
                                finish();
                            } else if (role.equals("consumer")) {
                                startActivity(new Intent(LoginActivity.this, ConsumerActivity.class));
                                finish();
                            } else {
                                startActivity(new Intent(LoginActivity.this, ngoActivity.class));
                                finish();
                            }
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Please register before login!", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
