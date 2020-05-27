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
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class UidDetailsAdapter extends RecyclerView.Adapter<UidDetailsAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<String> list_name;
    private ArrayList<String> list_aadhaar_number;
    private Activity activity;
    private String state,district;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView name, aadhaarNumber;
        public ImageView sellButton;

        public MyViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            aadhaarNumber = itemView.findViewById(R.id.aadhaar);
            sellButton = itemView.findViewById(R.id.sell_icon);
        }
    }

    public UidDetailsAdapter(Context context, ArrayList<String> list_name, ArrayList<String> list_aadhaar_number, Activity activity, String state, String district)
    {
        this.context = context;
        this.list_name = list_name;
        this.list_aadhaar_number = list_aadhaar_number;
        this.activity = activity;
        this.state = state;
        this.district = district;
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
        final String currName = list_name.get(position).trim();
        final String currAadhaar = list_aadhaar_number.get(position).trim();
        holder.name.setText(currName);
        holder.aadhaarNumber.setText(currAadhaar);
        holder.sellButton.setOnClickListener(v -> {

            DatabaseReference myRef1 = FirebaseDatabase.getInstance().getReference().child("Users").child(state).child(district).child("Transactions").child(currAadhaar);
            myRef1.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Transaction lastTransaction1 = dataSnapshot.getValue(Transaction.class);
                    Toast.makeText(context,"Sell!!",Toast.LENGTH_SHORT).show();
                    if(lastTransaction1 != null)
                    {
                        // Set up the alert builder
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        String[] items = {"Rice"+"("+lastTransaction1.getRice()+") Kg/Lt", "Flour"+"("+lastTransaction1.getFlour()+") Kg/Lt", "Cooking Oil"+"("+lastTransaction1.getCookingOil()+") Kg/Lt", "Sugar"+"("+lastTransaction1.getSugar()+") Kg/Lt"};
                        builder.setTitle("List of Items")

                                .setItems(items, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(context, items[which] + " is clicked", Toast.LENGTH_SHORT).show();
                                    }
                                });

                        // Add OK and Cancel buttons
                        builder.setPositiveButton("Sell", (dialog, which) -> {
                            // The user clicked OK
                            lastTransaction1.setComplete(true);
                            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                            String currDate = df.format(Calendar.getInstance().getTime());
                            lastTransaction1.setTimestamp(currDate);
                            myRef1.setValue(lastTransaction1);

                            new SweetAlertDialog(activity, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Items Sold!!")
                                    .show();
                        });
                        builder.setNegativeButton("Cancel", null);

                        // Create and show the alert dialog
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                    else{
                        Toast.makeText(context,"no transaction",Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        });
    }
}