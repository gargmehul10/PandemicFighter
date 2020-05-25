package com.mehul.pandemicfighter.Module1;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.ceylonlabs.imageviewpopup.ImagePopup;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mehul.pandemicfighter.R;
import java.util.ArrayList;
import java.util.HashMap;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class AddressRequestAdapter extends RecyclerView.Adapter<AddressRequestAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<String> list_aadhaar;
    private ArrayList<String> list_address;
    private ArrayList<String> list_idUrl;
    private Activity activity;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView aadhaarNumber, address;
        public ImageView wrong, accept, viewId;
        public MyViewHolder(View itemView) {
            super(itemView);
            aadhaarNumber = itemView.findViewById(R.id.aadhaar);
            address = itemView.findViewById(R.id.address);
            wrong = itemView.findViewById(R.id.wrong);
            accept = itemView.findViewById(R.id.accept);
            viewId = itemView.findViewById(R.id.view);
        }
    }

    public AddressRequestAdapter(Context context, ArrayList<String> list_aadhaar, ArrayList<String> list_address, ArrayList<String> list_idUrl, Activity activity)
    {
        this.context = context;
        this.list_aadhaar = list_aadhaar;
        this.list_address = list_address;
        this.list_idUrl = list_idUrl;
        this.activity = activity;
    }

    @Override
    public AddressRequestAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, final int position) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_address_verify_request,parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return list_aadhaar.size();
    }

    @Override
    public void onBindViewHolder(final AddressRequestAdapter.MyViewHolder holder, final int position) {
        String currAadhaarNumber = list_aadhaar.get(position);
        String currAddress = list_address.get(position).substring(5);
        String idUrl = list_idUrl.get(position);
        holder.aadhaarNumber.setText(currAadhaarNumber);
        holder.address.setText(currAddress);

        SessionManager sm = new SessionManager(activity);
        HashMap<String, String> details = sm.getUserDetails();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(details.get("state")).child(details.get("district"));

        holder.wrong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Are you sure?")
                        .setConfirmText("Yes, reject!")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                databaseReference.child("admin").child("requests").child(currAadhaarNumber).removeValue();

                                list_aadhaar.remove(position);
                                list_address.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, getItemCount());

                                sDialog
                                        .setTitleText("Rejected!")
                                        .setConfirmText("OK")
                                        .setConfirmClickListener(null)
                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                            }
                        })
                        .show();
            }
        });

        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Are you sure?")
                        .setConfirmText("Yes, accept!")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                databaseReference.child("admin").child("requests").child(currAadhaarNumber).removeValue();
                                databaseReference.child(currAadhaarNumber).child("address").setValue(currAddress);

                                list_aadhaar.remove(position);
                                list_address.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, getItemCount());

                                sDialog
                                        .setTitleText("Accepted!")
                                        .setConfirmText("OK")
                                        .setConfirmClickListener(null)
                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                            }
                        })
                        .show();
            }
        });

        holder.viewId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SweetAlertDialog pDialog = new SweetAlertDialog(activity, SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                pDialog.setTitleText("Loading ID...");
                pDialog.setCancelable(false);
                pDialog.show();

                ImagePopup imagePopup = new ImagePopup(activity);
                imagePopup.initiatePopupWithGlide(idUrl);
                pDialog.cancel();
                imagePopup.viewPopup();
            }
        });
    }
}