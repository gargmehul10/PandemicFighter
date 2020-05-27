package com.mehul.pandemicfighter.Module2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mehul.pandemicfighter.Module1.SessionManager;
import com.mehul.pandemicfighter.Module3.TimeSlot;
import com.mehul.pandemicfighter.R;
import java.util.HashMap;

public class ChooseSlotsActivity extends Activity {
    private RadioGroup radioGroup;
    private RadioButton radioButton1, radioButton2, radioButton3, radioButton4;
    private Button okButton;
    private TimeSlot timeSlot;
    private String uid_retailer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_slot);

        radioGroup = findViewById(R.id.radioGroup);
        radioButton1 = findViewById(R.id.radioButton1);
        radioButton2 = findViewById(R.id.radioButton2);
        radioButton3 = findViewById(R.id.radioButton3);
        radioButton4 = findViewById(R.id.radioButton4);
        okButton = findViewById(R.id.button);

        // fetching details from the current shop
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> details = sessionManager.getUserDetails();
        uid_retailer = getIntent().getStringExtra("UID");
        String uid = details.get("Aadhar");
        String state = details.get("state");
        String district = details.get("district");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(state).child(district).child(uid_retailer);
        databaseReference.child("Slots").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                timeSlot = dataSnapshot.getValue(TimeSlot.class);
                radioButton1.setText("Slot1 (8:00 - 9:00) Available: "+(20-timeSlot.getSlot1()));
                radioButton2.setText("Slot2 (9:00 - 10:00) Available: "+(20-timeSlot.getSlot2()));
                radioButton3.setText("Slot3 (10:00 - 11:00) Available: "+(20-timeSlot.getSlot3()));
                radioButton4.setText("Slot4 (11:00 - 12:00) Available: "+(20-timeSlot.getSlot4()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        okButton.setOnClickListener(v -> {
            int selectedId= radioGroup.getCheckedRadioButtonId();
            RadioButton selectedRadioButton = (RadioButton)findViewById(selectedId);
            Toast.makeText(ChooseSlotsActivity.this, selectedRadioButton.getText(), Toast.LENGTH_SHORT).show();

            int slot_id = selectedRadioButton.getText().charAt(4) - '0';
            move_to_next_Activity(slot_id,timeSlot);
            // go back to previous activity
        });
    }

    void move_to_next_Activity(int slot_id, TimeSlot ts){
        Intent i = new Intent(ChooseSlotsActivity.this,CreateItemList.class);
        i.putExtra("slotId", slot_id);
        i.putExtra("timeslot",ts);
        i.putExtra("UID_retailer",uid_retailer);
        startActivity(i);
    }
}