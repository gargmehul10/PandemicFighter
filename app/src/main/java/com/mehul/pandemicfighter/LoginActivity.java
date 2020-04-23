package com.mehul.pandemicfighter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginActivity extends AppCompatActivity {

    //defining views
    private EditText editTextEmail;
    private EditText editTextPassword;
    DatabaseReference databaseUser;

    //firebase auth object
    private FirebaseAuth firebaseAuth;

    //progress dialog
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //getting firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null)
        {
            SessionManager sessionManager = new SessionManager(getApplicationContext());
            if(sessionManager.getUserDetails().get("role").equals("Retailer")){
                Intent i=new Intent(getApplicationContext(), Retailer.class);
                startActivity(i);
                finish();
            }
            else {
                // Map work
//                Intent i = new Intent(getApplicationContext(), OrgActivity.class);
//                startActivity(i);
//                finish();
            }
        }
        databaseUser= FirebaseDatabase.getInstance().getReference("Users");

        //initializing views
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        progressDialog = new ProgressDialog(this);
    }

    public void SignInButton(View view) {
        final String email = editTextEmail.getText().toString().trim();
        String password  = editTextPassword.getText().toString().trim();
        final RadioGroup radioGroup = findViewById(R.id.radioGP1);

        //checking if email and passwords are empty
        if(TextUtils.isEmpty(email)){
            new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Oops...")
                    .setContentText("Please enter email!")
                    .show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Oops...")
                    .setContentText("Please enter password!")
                    .show();
            return;
        }

        //if the email and password are not empty
        //displaying a progress dialog

        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        //logging in the user
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        //if the task is successfull
                        if(task.isSuccessful() && RadioButtonSelect(radioGroup.getCheckedRadioButtonId()).equals("Retailer")){
                            //start the profile activity
                            finish();

                            SessionManager sm = new SessionManager(getApplicationContext());
                            sm.createLoginSession(email,"Retailer");

                            Intent i=new Intent(getApplicationContext(), Retailer.class);
                            startActivity(i);
                            finish();
                        }
                        // Map work
//                        else if(task.isSuccessful() && RadioButtonSelect(radioGroup.getCheckedRadioButtonId()).equals("NGO")){
//                            finish();
//
//                            SessionManager sm = new SessionManager(getApplicationContext());
//                            sm.createLoginSession(email, "NGO");
//
//                            Intent i=new Intent(getApplicationContext(), OrgActivity.class);
//                            startActivity(i);
//                            finish();
//                        }
                        else{
                            //display some message here
                            new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Oops...")
                                    .setContentText("Invalid email or password!")
                                    .show();
                        }
                    }
                });
    }

    private String RadioButtonSelect(int id){
        RadioButton radioButton = findViewById(id);
        return radioButton.getText().toString();
    }

    public void SignUpTextNGO(View view) {
        // @Kartik Mahendru map wala register ka code yahan par aayega
//        Intent i=new Intent(LoginActivity.this, OrgRegisterActivity.class);
//        startActivity(i);
//        finish();
    }

    public void SignUpTextRetailer(View view) {
        Intent i=new Intent(LoginActivity.this,SignupActivity.class);
        startActivity(i);
        finish();
    }
}
