package com.mehul.pandemicfighter.Module2;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mehul.pandemicfighter.Module1.SessionManager;
import com.mehul.pandemicfighter.Module3.Shop;
import com.mehul.pandemicfighter.Module3.TimeSlot;
import com.mehul.pandemicfighter.R;

import java.util.HashMap;

public class MapsMarkShop extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback, UploadActivity.ExampleDialogListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest locationRequest;
    private final int REQUEST_CHECK_SETTINGS = 100;
    private final int REQUEST_LOCATION_PERMISSION = 500;
    private final int REQUEST_CODE_AUTOCOMPLETE = 600;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastLocation;
    private LocationCallback locationCallback;
    private float DEFAULT_ZOOM = 16.5f;
    private LatLng latLng;
    private Marker marker;
    private String access_token = "pk.eyJ1IjoibmlzaGNoYWwiLCJhIjoiY2swMHZxeXNqMHE3NjNkc2N5NTJndnN2dCJ9.O2DHCiqvsvdRulclqUYxmg";
    private FloatingActionButton floatingActionButton;
    private Button buttonConfirm;
    private String MOB_NUMBER;
    private DatabaseReference myRef;
    private String shopName, ownerName;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent x = new Intent(MapsMarkShop.this, RetailerActivity.class);
        x.addFlags(x.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(x);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_mark_shop);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        Mapbox.getInstance(this, access_token);

        FirebaseApp.initializeApp(this);
        myRef = FirebaseDatabase.getInstance().getReference("food-request");

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        mGoogleApiClient.connect();
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        floatingActionButton = findViewById(R.id.floatingActionButton);
        buttonConfirm = findViewById(R.id.buttonConfirm);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                UploadActivity exampleDialog = new UploadActivity();
                exampleDialog.show(getSupportFragmentManager(), "example dialog");            }
        });
    }

    @Override
    public void applyTexts(String name1, String name2) {
        shopName = name1;
        ownerName =name2;
        double location[] = new double[2];
        location[0] = latLng.latitude;
        location[1] = latLng.longitude;
        Shop shop = new Shop(shopName, ownerName, location[0], location[1]);
        SessionManager sm = new SessionManager(getApplicationContext());
        HashMap<String, String> details = sm.getUserDetails();
        myRef = FirebaseDatabase.getInstance().getReference("Users").child(details.get("state")).child(details.get("district")).child(details.get("Aadhar"));
        myRef.child("ShopDetails").setValue(shop);

        // creating a slots node when marker is confirmed
         TimeSlot timeSlot = new TimeSlot(0, 0, 0, 0, "", "", "", "");
         myRef.child("Slots").setValue(timeSlot);

        Toast.makeText(MapsMarkShop.this, "Shop Registered!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(MapsMarkShop.this,RetailerActivity.class));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                this, R.raw.style_json));
        if (!success) {
            Log.e("TAG", "Style parsing failed.");
        }
        mMap = googleMap;
//		mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabledDuringRotateOrZoom(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.setBuildingsEnabled(true);
        mMap.setIndoorEnabled(true);
        mMap.setTrafficEnabled(true);
        mMap.setPadding(0, 100, 0, 0);
        enableMyLocation();

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                DEFAULT_ZOOM = mMap.getCameraPosition().zoom;
                getDeviceLocation();
                return false;
            }
        });

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
            }

            @Override
            public void onMarkerDrag(Marker marker) {
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                DEFAULT_ZOOM = mMap.getCameraPosition().zoom;
                latLng = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker1) {
                if (marker1.getTitle().equals("Food Here")) ;
                {
                    //openDialog(marker1);
                }
            }
        });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng ll) {
                latLng = ll;
                marker.remove();
                marker = mMap.addMarker(new MarkerOptions().position(latLng).title("Drag to adjust...").draggable(true));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlaceAutocomplete.clearRecentHistory(getApplicationContext());
                Intent intent = new PlaceAutocomplete.IntentBuilder()
                        .placeOptions(PlaceOptions.builder().backgroundColor(Color.WHITE).proximity(Point.fromLngLat(latLng.longitude, latLng.latitude)).country("IN").build())
                        .accessToken(access_token)
                        .build(MapsMarkShop.this);
                startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
            }
        });
    }

   /* private void showFood() {
        mMap.clear();
        marker = mMap.addMarker(new MarkerOptions().position(marker.getPosition()).title(marker.getTitle()).draggable(true).snippet(marker.getSnippet()));
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Food post = postSnapshot.getValue(Food.class);
                    assert post != null;
                    LatLng loc = new LatLng(post.getLatitude(), post.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(loc).title("Food Here").draggable(false).snippet("\nTime: " + postSnapshot.getKey())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }*/

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            getDeviceLocation();
        } else {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        PendingResult<com.google.android.gms.location.LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        builder.build()
                );

        result.setResultCallback(this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onResult(@NonNull Result result) {
        final Status status = result.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                getDeviceLocation();
                break;

            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                try {
                    status.startResolutionForResult(this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                // Location settings are unavailable so not possible to show any dialog now
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS) {

            if (resultCode == RESULT_OK) {

                Toast.makeText(getApplicationContext(), "GPS enabled", Toast.LENGTH_SHORT).show();
                getDeviceLocation();
            } else {

                Toast.makeText(getApplicationContext(), "GPS is not enabled", Toast.LENGTH_SHORT).show();
            }

        } else if (resultCode == MapsMarkShop.RESULT_OK && requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            CarmenFeature feature = PlaceAutocomplete.getPlace(data);
            Toast.makeText(this, feature.text(), Toast.LENGTH_LONG).show();
            latLng = new LatLng((feature.bbox().north() + feature.bbox().south()) / 2.0, (feature.bbox().east() + feature.bbox().west()) / 2.0);
            marker.remove();
            marker = mMap.addMarker(new MarkerOptions().title(feature.placeName()).snippet("Searched place").position(latLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)).draggable(true));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
        }
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
                                marker = mMap.addMarker(new MarkerOptions().position(latLng).title("Drag to adjust...").draggable(true));
                                mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
                            }
                        };
                        mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
                    }
                    marker = mMap.addMarker(new MarkerOptions().position(new LatLng(-35.016, 143.321)).title("Drag to adjust...").draggable(true));
                    //showFood();
                } else {
                    Toast.makeText(MapsMarkShop.this, "Unable to get Last Location", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
