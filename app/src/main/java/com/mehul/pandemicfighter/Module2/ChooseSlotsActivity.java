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
        String uid_retailer = getIntent().getStringExtra("UID");
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
            if(slot_id == 1)
            {
                if(timeSlot.getSlot1() < 20)
                {
                    // update count
                    TimeSlot updated_timeSlot = new TimeSlot(timeSlot.getSlot1() + 1, timeSlot.getSlot2(), timeSlot.getSlot3(), timeSlot.getSlot4(), timeSlot.getSlot1UID() + uid + " ", timeSlot.getSlot2UID(), timeSlot.getSlot3UID(), timeSlot.getSlot4UID());
                    databaseReference.child("Slots").setValue(updated_timeSlot);

                    Toast.makeText(ChooseSlotsActivity.this, "Slot1 (8:00 - 9:00) confirmed!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(ChooseSlotsActivity.this, "Slot full. Try again!", Toast.LENGTH_SHORT).show();
                }
            }
            else if(slot_id == 2)
            {
                if(timeSlot.getSlot2() < 20)
                {
                    // update count
                    TimeSlot updated_timeSlot = new TimeSlot(timeSlot.getSlot1(), timeSlot.getSlot2() + 1, timeSlot.getSlot3(), timeSlot.getSlot4(), timeSlot.getSlot1UID(), timeSlot.getSlot2UID() + uid + " ", timeSlot.getSlot3UID(), timeSlot.getSlot4UID());
                    databaseReference.child("Slots").setValue(updated_timeSlot);

                    Toast.makeText(ChooseSlotsActivity.this, "Slot2 (9:00 - 10:00) confirmed!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(ChooseSlotsActivity.this, "Slot full. Try again!", Toast.LENGTH_SHORT).show();
                }
            }
            else if(slot_id == 3)
            {
                if(timeSlot.getSlot3() < 20)
                {
                    // update count
                    TimeSlot updated_timeSlot = new TimeSlot(timeSlot.getSlot1(), timeSlot.getSlot2(), timeSlot.getSlot3() + 1, timeSlot.getSlot4(), timeSlot.getSlot1UID(), timeSlot.getSlot2UID(), timeSlot.getSlot3UID() + uid + " ", timeSlot.getSlot4UID());
                    databaseReference.child("Slots").setValue(updated_timeSlot);

                    Toast.makeText(ChooseSlotsActivity.this, "Slot3 (10:00 - 11:00) confirmed!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(ChooseSlotsActivity.this, "Slot full. Try again!", Toast.LENGTH_SHORT).show();
                }
            }
            else if(slot_id == 4)
            {
                if(timeSlot.getSlot4() < 20)
                {
                    // update count
                    TimeSlot updated_timeSlot = new TimeSlot(timeSlot.getSlot1(), timeSlot.getSlot2(), timeSlot.getSlot3(), timeSlot.getSlot4() + 1, timeSlot.getSlot1UID(), timeSlot.getSlot2UID(), timeSlot.getSlot3UID(), timeSlot.getSlot4UID() + uid + " ");
                    databaseReference.child("Slots").setValue(updated_timeSlot);

                    Toast.makeText(ChooseSlotsActivity.this, "Slot4 (11:00 - 12:00) confirmed!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(ChooseSlotsActivity.this, "Slot full. Try again!", Toast.LENGTH_SHORT).show();
                }
            }

            // go back to previous activity
        });
    }
}