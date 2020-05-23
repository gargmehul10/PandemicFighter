package com.mehul.pandemicfighter.Module1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mehul.pandemicfighter.Module3.User;
import com.mehul.pandemicfighter.R;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class UserRegisterActivity extends FragmentActivity implements OnMapReadyCallback, AdapterView.OnItemSelectedListener {

	private GoogleMap mMap;
	private LatLng latLng;
	private Spinner types;
	private float DEFAULT_ZOOM;
	private ProgressDialog progressDialog;
	private EditText editName,editMob,editAadhar,editRange;
	private TextInputLayout nameLay,mobLay,addLay,passLay;
	private Button btnGoBack,btnSignup;
	private FusedLocationProviderClient mFusedLocationProviderClient;
	private final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
	private FirebaseAuth firebaseAuth;
	private Location mLastLocation;
	private LocationCallback locationCallback;
	private Marker marker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_register);

		firebaseAuth = FirebaseAuth.getInstance();
		editName=findViewById(R.id.editName);
		editMob=(EditText) findViewById(R.id.editMob);
		editAadhar=findViewById(R.id.editAadhar);
		editRange=findViewById(R.id.editRange);
		types = findViewById(R.id.types);
        types.setOnItemSelectedListener(this);
//		btnGoBack=findViewById(R.id.btnGoBack);
		btnSignup=findViewById(R.id.btnSignup);

		nameLay=findViewById(R.id.name_text_input1);
		mobLay=findViewById(R.id.name_text_input2);
		addLay=findViewById(R.id.name_text_input3);
		passLay=findViewById(R.id.name_text_input4);


		//progressDialog.setCancelable(false);

		FirebaseApp.initializeApp(this);
		mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
		// Obtain the SupportMapFragment and get notified when the map is ready to be used.
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
		DEFAULT_ZOOM=14.0f;
	}

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
	public void onMapReady(GoogleMap googleMap) {
		mMap = googleMap;
		mMap.getUiSettings().setAllGesturesEnabled(true);
		mMap.getUiSettings().setScrollGesturesEnabledDuringRotateOrZoom(true);
		mMap.getUiSettings().setCompassEnabled(true);
		mMap.getUiSettings().setMyLocationButtonEnabled(true);
		mMap.getUiSettings().setMapToolbarEnabled(true);
		mMap.setBuildingsEnabled(true);
		mMap.setIndoorEnabled(true);
		mMap.setTrafficEnabled(true);
		mMap.setMyLocationEnabled(true);

		// Add a marker in Prayagraj and move the camera
		// add a function for current location
		getDeviceLocation();
//		mMap.addMarker(new MarkerOptions().position(latLng).title("Marker")).setDraggable(true);
//		mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,DEFAULT_ZOOM));

		mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
			@Override
			public void onMapLongClick(LatLng latLng1) {
				DEFAULT_ZOOM=mMap.getCameraPosition().zoom;
				mMap.clear();
				latLng = latLng1;
				mMap.addMarker(new MarkerOptions().position(latLng).title("Marker in Prayagraj")).setDraggable(true);
				mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,DEFAULT_ZOOM));
			}
		});
		mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
			@Override
			public void onMarkerDragStart(Marker marker) {
				DEFAULT_ZOOM=mMap.getCameraPosition().zoom;
			}

			@Override
			public void onMarkerDrag(Marker marker) {

			}

			@Override
			public void onMarkerDragEnd(Marker marker) {
				latLng=marker.getPosition();
				mMap.addMarker(new MarkerOptions().position(latLng).title("Marker in Prayagraj")).setDraggable(true);
				mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,DEFAULT_ZOOM));
			}
		});
		mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
			@Override
			public void onCameraMove() {
				DEFAULT_ZOOM=mMap.getCameraPosition().zoom;
			}
		});

//		btnGoBack.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				finish();
//			}
//		});

		btnSignup.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new SweetAlertDialog(UserRegisterActivity.this, SweetAlertDialog.WARNING_TYPE)
						.setTitleText("Aadhaar Verification")
						.setContentText("Do you currently live at address on Aadhaar?")
						.setConfirmText("Yes")
						.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
							@Override
							public void onClick(SweetAlertDialog sDialog) {
								checkInputs();
								sDialog.dismissWithAnimation();
							}
						})
						.setCancelText("No")
						.showCancelButton(true)
						.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
							@Override
							public void onClick(SweetAlertDialog sDialog) {
								// take another address proof
								checkInputs1();
								sDialog.cancel();
							}
						})
						.show();
			}
		});

	}

	private void checkInputs() {
		String name,mobNo,add,pass,repPass,type;
		Double range;
		name=editName.getText().toString();
		mobNo=editMob.getText().toString().trim();
		add=editAadhar.getText().toString().trim();
		range=Double.parseDouble(editRange.getText().toString());
        type = types.getSelectedItem().toString();
		if(name.isEmpty())
		{
			nameLay.setError("Enter valid name");
			editName.requestFocus();
			return;
		}
		if(mobNo.isEmpty()) {
			mobLay.setError("Enter valid email");
			editMob.requestFocus();
			return;
		}
		if(add.isEmpty())
		{
			addLay.setError("Enter valid address");
			editAadhar.requestFocus();
			return;
		}

		if(latLng.equals(new LatLng(25.494635, 81.867338))) {
			Toast.makeText(this,"Please select starting location!", Toast.LENGTH_LONG).show();
			return;
		}

        User newUser = new User(name,add,mobNo,type,latLng.latitude,latLng.longitude, range);
        Intent intent = new Intent(UserRegisterActivity.this, ScanDetails.class);
        intent.putExtra("Module3.User", newUser);
        startActivity(intent);
	}

	private void checkInputs1() {
		String name,mobNo,add,pass,repPass,type;
		Double range;
		name=editName.getText().toString();
		mobNo=editMob.getText().toString().trim();
		add=editAadhar.getText().toString().trim();
		range=Double.parseDouble(editRange.getText().toString());
		type = types.getSelectedItem().toString();
		if(name.isEmpty())
		{
			nameLay.setError("Enter valid name");
			editName.requestFocus();
			return;
		}
		if(mobNo.isEmpty()) {
			mobLay.setError("Enter valid email");
			editMob.requestFocus();
			return;
		}
		if(add.isEmpty())
		{
			addLay.setError("Enter valid address");
			editAadhar.requestFocus();
			return;
		}

		if(latLng.equals(new LatLng(25.494635, 81.867338))) {
			Toast.makeText(this,"Please select starting location!", Toast.LENGTH_LONG).show();
			return;
		}

		User newUser = new User(name,add,mobNo,type,latLng.latitude,latLng.longitude, range);
		Intent intent = new Intent(UserRegisterActivity.this, UploadIdActivity.class);
		intent.putExtra("Module3.UserDetails", newUser);
		startActivity(intent);
	}

	private void getDeviceLocation() {
		mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
			@Override
			public void onComplete(@NonNull Task<Location> task) {
				if (task.isSuccessful()) {
					mLastLocation = task.getResult();
					if (mLastLocation != null) {
						latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
						mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
						marker = mMap.addMarker(new MarkerOptions().position(latLng).title("Drag to adjust...").draggable(true));
					} else {
						final LocationRequest locationRequest = LocationRequest.create();
						locationRequest.setInterval(10000);
						locationRequest.setFastestInterval(5000);
						locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
						locationCallback = new LocationCallback() {
							@Override
							public void onLocationResult(LocationResult locationResult) {
								super.onLocationResult(locationResult);
								if (locationResult == null) {
									return;
								}
								mLastLocation = locationResult.getLastLocation();
								latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
								mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
								mMap.addMarker(new MarkerOptions().position(latLng).title("Marker")).setDraggable(true);
								mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,DEFAULT_ZOOM));
								mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
							}
						};
						mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
					}
					marker = mMap.addMarker(new MarkerOptions().position(new LatLng(-35.016, 143.321)).title("Drag to adjust...").draggable(true));
					//showFood();
				} else {
					Toast.makeText(UserRegisterActivity.this, "Unable to get Last Location", Toast.LENGTH_SHORT).show();
				}

			}
		});
	}

	/*private void register(String name, final String mobNo, String add, double range, double latitude, double longitude) {

		String number = "+91" + mobNo;
		//creating a new user
		firebaseAuth.createUserWithEmailAndPassword(semail, spassword)
				.addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
					@Override
					public void onComplete(@NonNull Task<AuthResult> task) {

						System.out.println(task.getException());

						//checking if success
						if(task.isSuccessful()){
							// finish();
							Toast.makeText(UserRegisterActivity.this,"Registered successfully!", Toast.LENGTH_LONG).show();
							FirebaseAuth currentFirebaseUser = FirebaseAuth.getInstance();

							MessageDigest digest = null;
							try {
								digest = MessageDigest.getInstance("SHA-256");
							} catch (NoSuchAlgorithmException e) {
								e.printStackTrace();
							}
							assert digest != null;
							String pass1 = pass;
							byte[] tempPass = digest.digest(pass1.getBytes(StandardCharsets.UTF_8));
							pass1 = Arrays.toString(tempPass);    // pass is now Hashed

							final Organization organization=new Organization(add,latitude,longitude,mobNo.replaceAll("[-+.^:,@*]",""),name,range);

							databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
								@Override
								public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
									if(dataSnapshot.child(organization.getMobNo()).exists()) {
										progressDialog.cancel();
										openDialog(organization);
									}
									else {
										String resultemail = organization.getMobNo();
										databaseReference.child(resultemail).setValue(organization);
										Toast.makeText(UserRegisterActivity.this,"Organization added!", Toast.LENGTH_LONG).show();
										progressDialog.cancel();
										currentFirebaseUser.signOut();
										startActivity(new Intent(UserRegisterActivity.this, LoginActivity.class));
										finish();
									}
								}

								@Override
								public void onCancelled(@NonNull DatabaseError databaseError) {
									progressDialog.cancel();
									Toast.makeText(UserRegisterActivity.this,"Some error occurred!", Toast.LENGTH_LONG).show();
								}
							});

						}else{
							//display some message here
							new SweetAlertDialog(UserRegisterActivity.this, SweetAlertDialog.ERROR_TYPE)
									.setTitleText("Oops...")
									.setContentText("Registration error!")
									.show();

							FirebaseAuthException e = (FirebaseAuthException)task.getException();
							Toast.makeText(UserRegisterActivity.this, "Failed Registration: "+e.getMessage(), Toast.LENGTH_SHORT).show();
						}
						progressDialog.dismiss();
					}
				});
	}

	private void openDialog(final Organization organization) {
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();

		TextView title = new TextView(this);

		title.setText("");
		title.setPadding(10, 10, 10, 10);   // Set Position
		title.setGravity(Gravity.CENTER);
		title.setTextColor(Color.BLACK);
		title.setTextSize(20);
		alertDialog.setCustomTitle(title);

		TextView msg = new TextView(this);

		msg.setText("    ORGANIZATION ALREADY EXISTS");
		msg.setTextColor(Color.BLACK);
		msg.setTextSize(20);
		alertDialog.setView(msg);

		alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, " UPDATE DATA ", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				databaseReference.child(organization.getMobNo()).setValue(organization);
				Toast.makeText(UserRegisterActivity.this,"Data Updated", Toast.LENGTH_LONG).show();
				finish();
			}
		});

		alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL,"CANCEL  ", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// No need to write anything here
			}
		});
		new Dialog(getApplicationContext());
		alertDialog.show();

		// Set Properties for OK Button
		final Button okBT = alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
		LinearLayout.LayoutParams neutralBtnLP = (LinearLayout.LayoutParams) okBT.getLayoutParams();
		neutralBtnLP.gravity = Gravity.FILL_HORIZONTAL;
		okBT.setPadding(50, 10, 10, 10);   // Set Position
		okBT.setTextColor(Color.BLUE);
		okBT.setLayoutParams(neutralBtnLP);

		final Button cancelBT = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
		LinearLayout.LayoutParams negBtnLP = (LinearLayout.LayoutParams) okBT.getLayoutParams();
		negBtnLP.gravity = Gravity.FILL_HORIZONTAL;
		cancelBT.setTextColor(Color.RED);
		cancelBT.setLayoutParams(negBtnLP);
	}*/
}
