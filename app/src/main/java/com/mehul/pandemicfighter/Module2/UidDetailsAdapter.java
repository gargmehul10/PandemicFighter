package com.mehul.pandemicfighter.Module2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.mehul.pandemicfighter.Module3.Transaction;
import com.mehul.pandemicfighter.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class UidDetailsAdapter extends RecyclerView.Adapter<UidDetailsAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<String> list_name;
    private ArrayList<String> list_aadhaar_number;
    private Activity activity;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView name, aadhaarNumber;
        public ImageView infoButton, sellButton;

        public MyViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            aadhaarNumber = itemView.findViewById(R.id.aadhaar);
            infoButton = itemView.findViewById(R.id.info_icon);
            sellButton = itemView.findViewById(R.id.sell_icon);
        }
    }

    public UidDetailsAdapter(Context context, ArrayList<String> list_name, ArrayList<String> list_aadhaar_number, Activity activity)
    {
        this.context = context;
        this.list_name = list_name;
        this.list_aadhaar_number = list_aadhaar_number;
        this.activity = activity;
    }

    @Override
    public UidDetailsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, final int position) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_uid_details,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return list_name.size();
    }

    @Override
    public void onBindViewHolder(final UidDetailsAdapter.MyViewHolder holder, final int position) {
        final String currName = list_name.get(position);
        final String currAadhaar = list_aadhaar_number.get(position);
        holder.name.setText(String.valueOf(currName));
        holder.aadhaarNumber.setText(String.valueOf(currAadhaar));
        holder.infoButton.setOnClickListener(v -> {
            Toast.makeText(activity, currName, Toast.LENGTH_LONG).show();

            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("Transactions").child(currAadhaar);
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Transaction lastTransaction = dataSnapshot.getValue(Transaction.class);
                    if(lastTransaction == null)
                    {
                        // Toast.makeText(activity, "Hello", Toast.LENGTH_LONG).show();
                        new SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE)
                                .setContentText("No transaction done yet!")
                                .show();
                    }
                    else
                    {
                        String body =  "\nItems purchased: ";
                        if(lastTransaction.getRice() > 0)
                            body = body + "Rice, ";
                        if(lastTransaction.getPulses() > 0)
                            body = body + "Pulses, ";
                        if(lastTransaction.getFlour() > 0)
                            body = body + "Flour, ";
                        if(lastTransaction.getCookingOil() > 0)
                            body = body + "Cooking Oil, ";
                        if(lastTransaction.getSpices() > 0)
                            body = body + "Spices, ";
                         int pos = body.lastIndexOf(',');
                         body = body.substring(0, pos) + ".\n";

                        new SweetAlertDialog(activity)
                                .setTitleText("Last Transaction: " + lastTransaction.getTimestamp())
                                .setContentText(body)
                                .show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        });
        holder.sellButton.setOnClickListener(v -> {

            DatabaseReference myRef1 = FirebaseDatabase.getInstance().getReference().child("Transactions").child(currAadhaar);
            myRef1.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Transaction lastTransaction1 = dataSnapshot.getValue(Transaction.class);
                    int flag = 0;
                    if(lastTransaction1 != null)
                    {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                        Date firstDate = null;
                        try {
                            firstDate = sdf.parse(lastTransaction1.getTimestamp());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Date secondDate = null;
                        try {
                            secondDate = sdf.parse(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        long diffInMillies = Math.abs(secondDate.getTime() - firstDate.getTime());
                        long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

                        if(diff >= 7.0)
                        {
                            flag = 1;
                        }
                        else
                        {
                            new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Do not sell !")
                                    .setContentText(currName + " made last transaction " + (int)diff + " days earlier.")
                                    .show();
                        }
                    }
                    if(lastTransaction1 == null || flag == 1)
                    {
                        // Set up the alert builder
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setTitle("Choose items purchased: ");

                        // Add a checkbox list
                        String[] animals = {"Rice", "Pulses", "Flour", "Cooking Oil", "Spices"};
                        boolean[] checkedItems = {false, false, false, false, false};
                        builder.setMultiChoiceItems(animals, checkedItems, (dialog, which, isChecked) -> {
                            // The user checked or unchecked a box
                            // Toast.makeText(activity, "" + which + " " + isChecked, Toast.LENGTH_SHORT).show();
                            checkedItems[which] = isChecked;
                        });

                        // Add OK and Cancel buttons
                        builder.setPositiveButton("OK", (dialog, which) -> {
                            // The user clicked OK
                            String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
                            Transaction newTransaction = new Transaction(date, 0, 0, 0, 0, 0);
                            if(checkedItems[0] == true)
                                newTransaction.setRice(1);
                            if(checkedItems[1] == true)
                                newTransaction.setPulses(1);
                            if(checkedItems[2] == true)
                                newTransaction.setFlour(1);
                            if(checkedItems[3] == true)
                                newTransaction.setCookingOil(1);
                            if(checkedItems[4] == true)
                                newTransaction.setSpices(1);

                            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("Transactions").child(currAadhaar);
                            myRef.setValue(newTransaction);

                            new SweetAlertDialog(activity, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Items purchased !!")
                                    .show();
                        });
                        builder.setNegativeButton("Cancel", null);

                        // Create and show the alert dialog
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        });
    }
}