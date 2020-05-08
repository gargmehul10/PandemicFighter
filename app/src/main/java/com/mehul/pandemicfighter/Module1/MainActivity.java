package com.mehul.pandemicfighter.Module1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.mehul.pandemicfighter.Module2.ConsumerActivity;
import com.mehul.pandemicfighter.Module2.RetailerActivity;
import com.mehul.pandemicfighter.Module2.ngoActivity;
import com.mehul.pandemicfighter.R;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                SessionManager sm = new SessionManager(getApplicationContext());
                HashMap<String, String> details = sm.getUserDetails();
                Toast.makeText(MainActivity.this,details.get("Aadhar"),Toast.LENGTH_SHORT).show();
                Toast.makeText(MainActivity.this,details.get("role"),Toast.LENGTH_SHORT).show();
                Toast.makeText(MainActivity.this,details.get("district"),Toast.LENGTH_SHORT).show();
                if(details.get("Aadhar").equals("Not Found")){
                    Intent mainIntent = new Intent(MainActivity.this, LoginActivity.class);
                    MainActivity.this.startActivity(mainIntent);
                    MainActivity.this.finish();
                }
                else{
                    //redirect
                    if(details.get("role").equals("retailer")){
                        startActivity(new Intent(MainActivity.this, RetailerActivity.class));
                    }
                    else if(details.get("role").equals("consumer")){
                        startActivity(new Intent(MainActivity.this, ConsumerActivity.class));
                    }
                    else{
                        startActivity(new Intent(MainActivity.this, ngoActivity.class));
                    }
                }
            }
        }, 1000);
    }
}
