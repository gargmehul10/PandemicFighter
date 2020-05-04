package com.mehul.pandemicfighter.Module2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.mehul.pandemicfighter.R;


public class UploadActivity extends AppCompatDialogFragment {
    private EditText editTextShopName;
    private EditText editTextOwnerName;
    private ExampleDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_upload, null);

        builder.setView(view)
                .setTitle("Enter Details")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String shopname = editTextShopName.getText().toString();
                        String ownername = editTextOwnerName.getText().toString();
                        listener.applyTexts(shopname, ownername);
                    }
                });

        editTextShopName = view.findViewById(R.id.enterShopName);
        editTextOwnerName = view.findViewById(R.id.enterOwnerName);

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (ExampleDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement ExampleDialogListener");
        }
    }

    public interface ExampleDialogListener {
        void applyTexts(String name1, String name2);
    }
}
