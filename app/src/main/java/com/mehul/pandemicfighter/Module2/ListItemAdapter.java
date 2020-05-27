package com.mehul.pandemicfighter.Module2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
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
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ListItemAdapter extends RecyclerView.Adapter<ListItemAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<String> list_items;
    private ArrayList<Double> list_quantity;
    private Activity activity;
    private String state;
    private String district;
    private String aadhar;

    public ListItemAdapter(Context applicationContext, Transaction currTransaction, Activity activity, String state, String district, String aadhar) {
        this.context = applicationContext;
        this.activity = activity;
        list_items = new ArrayList<>(Arrays.asList("Rice","Wheat Flour","Cooking Oil","Sugar"));
        list_quantity = new ArrayList<>();
        list_quantity.add(currTransaction.getRice());
        list_quantity.add(currTransaction.getFlour());
        list_quantity.add(currTransaction.getCookingOil());
        list_quantity.add(currTransaction.getSugar());
        this.state = state;
        this.district = district;
        this.aadhar = aadhar;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView item, quantity;
        public ImageView addbutton;

        public MyViewHolder(View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.item);
            quantity = itemView.findViewById(R.id.quantityFin);
            addbutton = itemView.findViewById(R.id.add_icon);
        }
    }

    @Override
    public ListItemAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, final int position) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_create_list,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return list_items.size();
    }

    @Override
    public void onBindViewHolder(final ListItemAdapter.MyViewHolder holder, final int position) {
        final String currItem = list_items.get(position);
        final String currQ = list_quantity.get(position).toString()+" Kg/Lt";
        holder.item.setText(String.valueOf(currItem));
        holder.quantity.setText(String.valueOf(currQ));
        holder.addbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context,"hi",Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("Enter Quantity");

                final EditText input = new EditText(context);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                builder.setView(input);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Double quan = Double.parseDouble(input.getText().toString().trim());
                        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("Users").child(state).child(district).child("Transactions").child(aadhar);
                        switch(position){
                            case 0:
                                myRef.child("rice").setValue(quan.doubleValue());
                                break;
                            case 1:
                                myRef.child("flour").setValue(quan.doubleValue());
                                break;
                            case 2:
                                myRef.child("cookingOil").setValue(quan.doubleValue());
                                break;
                            case 3:
                                myRef.child("sugar").setValue(quan.doubleValue());
                                break;

                        }
                        new SweetAlertDialog(activity, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Item Added, Refresh to see changes !!")
                                .show();
                    }
                });
                builder.show();
            }
        });
    }
}