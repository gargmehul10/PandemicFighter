package com.mehul.pandemicfighter.Module1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;
import com.mehul.pandemicfighter.Module3.User;
import com.mehul.pandemicfighter.R;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class ScanDetails extends Activity {

    private CodeScanner mCodeScanner;
    private ProgressDialog progressDialog;
    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_details);
        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        Toast.makeText(ScanDetails.this , "Scan QR code on Aadhar Card", Toast.LENGTH_LONG).show();
        progressDialog=new ProgressDialog(this);
        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                ScanDetails.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.setMessage("Verifying...");
                        progressDialog.show();
                        Document doc = null;
                        try {
                            doc = loadXMLFromString(result.getText());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        NodeList info = doc.getElementsByTagName("PrintLetterBarcodeData");
                        for (int temp = 0; temp < info.getLength(); temp++)
                        {
                            Node node = info.item(temp);
                            if (node.getNodeType() == Node.ELEMENT_NODE)
                            {
                                Element eElement = (Element) node;
                                String scannedAadhar = eElement.getAttribute("uid").trim().toLowerCase();
                                String scannedName = eElement.getAttribute("name").trim().toLowerCase();
                                String scannedHouse = eElement.getAttribute("house").trim().toLowerCase();
                                String scannedStreet = eElement.getAttribute("street").trim().toLowerCase();
                                String scannedArea = eElement.getAttribute("lm").trim().toLowerCase();
                                String scannedDistrict = eElement.getAttribute("dist").trim().toLowerCase();
                                String scannedState = eElement.getAttribute("state").trim().toLowerCase();
                                Toast.makeText(ScanDetails.this , scannedAadhar, Toast.LENGTH_SHORT).show();
                                Toast.makeText(ScanDetails.this , scannedName, Toast.LENGTH_SHORT).show();
                                Toast.makeText(ScanDetails.this , scannedHouse, Toast.LENGTH_SHORT).show();
                                Toast.makeText(ScanDetails.this , scannedStreet, Toast.LENGTH_SHORT).show();
                                Toast.makeText(ScanDetails.this , scannedArea, Toast.LENGTH_SHORT).show();
                                Toast.makeText(ScanDetails.this , scannedDistrict, Toast.LENGTH_SHORT).show();
                                Toast.makeText(ScanDetails.this , scannedState, Toast.LENGTH_SHORT).show();
                                // process address
                                //verify
                                Bundle bundle = getIntent().getExtras();
                                User user = bundle.getParcelable("Module3.User");
                                if(user.getAadharNumber() != scannedAadhar){
                                    progressDialog.setMessage("Aadhar Verification Failed");
                                    progressDialog.cancel();
                                    startActivity(new Intent(ScanDetails.this, LoginActivity.class));
                                }
                                String address = scannedHouse+" "+scannedStreet+" "+scannedArea+" "+scannedDistrict+" "+scannedState;
                                user.setAddress(address);
                                //add user to firebase
                                databaseReference.child("Users").child(scannedState).child(scannedDistrict).child(scannedAadhar).setValue(user);
                                Toast.makeText(ScanDetails.this, "User verified !!",Toast.LENGTH_LONG).show();

                                // for first user in an district create an admin
                                DatabaseReference adminRef = FirebaseDatabase.getInstance().getReference().child("Users").child(scannedState).child(scannedDistrict);
                                adminRef.child("admin").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot snapshot) {
                                        if (snapshot.getValue() == null) {
                                            // Admin doesn't exist so create one

                                            DatabaseReference newAdminRef = FirebaseDatabase.getInstance().getReference().child("Users").child(scannedState).child(scannedDistrict);
                                            // create a random 6 digit secret key to be shared with admin
                                            Random rnd = new Random();
                                            int key = rnd.nextInt(999999);
                                            newAdminRef.child("admin").child("key").setValue(String.format("%06d", key));
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                startActivity(new Intent(ScanDetails.this, LoginActivity.class));
                            }
                        }
                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
    }

    public static Document loadXMLFromString(String xml) throws Exception
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xml));
        return builder.parse(is);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }
}
