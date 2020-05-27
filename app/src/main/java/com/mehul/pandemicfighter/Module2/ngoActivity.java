package com.mehul.pandemicfighter.Module2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.optimization.v1.MapboxOptimization;
import com.mapbox.api.optimization.v1.models.OptimizationResponse;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation;
import com.mehul.pandemicfighter.Module1.LoginActivity;
import com.mehul.pandemicfighter.Module1.SessionManager;
import com.mehul.pandemicfighter.Module3.Problem;
import com.mehul.pandemicfighter.Module3.Shop;
import com.mehul.pandemicfighter.Module3.User;
import com.mehul.pandemicfighter.R;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import static com.mapbox.core.constants.Constants.PRECISION_6;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;

public class ngoActivity extends AppCompatActivity implements OnMapReadyCallback, PermissionsListener{

    private static final String FIRST = "first";
    private static final String ANY = "any";
    private static final String TEAL_COLOR = "#23D2BE";
    private static final float POLYLINE_WIDTH = 5;
    private MapView mapView;
    private MapboxMap mapboxMap;
    private DirectionsRoute optimizedRoute;
    private MapboxOptimization optimizedClient;
    private List<Point> stops = new ArrayList<>();
    private Point origin;

    private PermissionsManager permissionsManager;
    private String mobNo;
    private DatabaseReference myRef,problemPoints,shopPickedRef;
    private LatLng usrStart;
    private double usrRange;
    private User usr;
    IconFactory iconFactory;
    private HashMap<String, String> details;
    Icon icon;

    private MapboxNavigation navigation;
    private String district;
    private String aadharNumber;
    private String state;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("See nearby!!");

        // Mapbox access token is configured here. This needs to be called either in your application
        // object or in the same activity which contains the mapview.
        Mapbox.getInstance(this, getString(R.string.access_token));
        navigation = new MapboxNavigation(getApplicationContext(), getString(R.string.access_token));

        // This contains the MapView in XML and needs to be called after the access token is configured.
        setContentView(R.layout.activity_ngo);

        iconFactory = IconFactory.getInstance(ngoActivity.this);
        icon = iconFactory.fromResource(R.drawable.map_marker_dark);

        //mobNo=getIntent().getStringExtra("MOB_NUMBER");
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        details = sessionManager.getUserDetails();
        FirebaseApp.initializeApp(this);
        aadharNumber = details.get("Aadhar");
        state = details.get("state");
        district = details.get("district");
        myRef= FirebaseDatabase.getInstance().getReference("Users").child(state).child(district).child(aadharNumber);
        shopPickedRef=FirebaseDatabase.getInstance().getReference("shop-picked");
        problemPoints= FirebaseDatabase.getInstance().getReference("Users").child(state).child(district).child("Problems");
        // Toast.makeText(ngoActivity.this, "references are made ",Toast.LENGTH_SHORT).show();
        mapView = findViewById(R.id.mapView);
        // to get Driver's start point and other data
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    usr = dataSnapshot.getValue(User.class);
                    //Log.e("TAG","In range "+usr.getName()+ " "+usr.getAddress()+" "+usr.getLatitude()+" "+usr.getLongitude());
                    assert usr != null;
                    usrStart = new LatLng(usr.getLocLatitude(), usr.getLocLongitude());
                    usrRange = usr.getRange()*1000.0;
                    mapView.onCreate(savedInstanceState);
                    mapView.getMapAsync(ngoActivity.this);
                }catch(Exception e) {
                    Toast.makeText(getApplicationContext(),"User data is corrupt!",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"Some error occurred!",Toast.LENGTH_LONG).show();
                finish();
            }
        });

        // Setup the MapView

    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(Style.MAPBOX_STREETS, style -> {

            enableLocationComponent(style);
            initOptimizedRouteLineLayer(style);
            // Toast.makeText(ngoActivity.this, "shoe shop point called ",Toast.LENGTH_SHORT).show();
            showShopPoints();
            // Toast.makeText(ngoActivity.this, "show shop point call backed ",Toast.LENGTH_SHORT).show();
            mapboxMap.setOnInfoWindowClickListener(marker -> {
                if(marker.getTitle().substring(marker.getTitle().indexOf(' ')+1).equals("shop Here"));
                {
                    ////////////////////////////////////////////
                }
                return false;
            });
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        //Display menu to user
        switch (item.getItemId()) {
            case R.id.logout:
                new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Are you sure you want to logout?")
                        .setConfirmText("Yes")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                clearTable();
                                saveTable();
                                SessionManager sm = new SessionManager(getApplicationContext());
                                sm.logoutUser();
                                Intent i=new Intent(getApplicationContext(), LoginActivity.class);
                                i.addFlags(i.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                                finish();
                                sDialog.dismissWithAnimation();
                            }
                        })
                        .setCancelButton("No", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                            }
                        })
                        .show();
                return true;
//                clearTable();
//                saveTable();
//                Intent intent=new Intent(ngoActivity.this,MainActivity.class);
//                startActivity(intent);
//                finish();
            case R.id.action_settings:
                Intent i = new Intent(getApplicationContext(), AboutUs.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showShopPoints() {
        problemPoints.addValueEventListener(new ValueEventListener() {
            @SuppressLint("LogNotTimber")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // Add the origin Point to the list
                addFirstStopToStopsList();
                mapboxMap.clear();

                Log.e("TAG", "GB count " + dataSnapshot.getChildrenCount());
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    Problem post = postSnapshot.getValue(Problem.class);
                    String key = postSnapshot.getKey();
                    LatLng shopLatLng = new LatLng(post.getLat(), post.getLng());
                    if (shopLatLng.distanceTo(usrStart) <= usrRange) {
                        Log.e("TAG", "In range " + postSnapshot.getKey());

                        // Optimization API is limited to 12 coordinate sets
                        if (alreadyTwelveMarkersOnMap()) {
                            Toast.makeText(ngoActivity.this, "Only 12 steps allowed", Toast.LENGTH_LONG).show();
                        } else {
                            Style style = mapboxMap.getStyle();
                            if (style != null) {
                                mapboxMap.addMarker(new MarkerOptions()
                                        .position(shopLatLng)
                                        .title("Contact : " +post.getContact())
                                        .snippet("\n" + "Description : " + post.getDescription() + "\n Posted on : " + post.getTimestamp()))
                                        .setIcon(icon);
                                stops.add(Point.fromLngLat(shopLatLng.getLongitude(), shopLatLng.getLatitude()));
                                if (stops.size() >= 2) {
                                    getOptimizedRoute(style, stops);
                                }
                            }
                        }
                    } else
                        Log.e("TAG", "Not in range " + postSnapshot.getKey());
                }

                //Log.e("TAG","In range "+post.getDescription()+ " "+post.getSuffFor()+" "+post.getUrl()+" "+post.getLatitude()+" "+post.getLongitude());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initOptimizedRouteLineLayer(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addSource(new GeoJsonSource("optimized-route-source-id"));
        loadedMapStyle.addLayerBelow(new LineLayer("optimized-route-layer-id", "optimized-route-source-id")
                .withProperties(
                        lineColor(Color.parseColor(TEAL_COLOR)),
                        lineWidth(POLYLINE_WIDTH)
                ), "icon-layer-id");
    }

    private boolean alreadyTwelveMarkersOnMap() {
        return stops.size() == 12;
    }

    @SuppressLint("LogNotTimber")
    private void addFirstStopToStopsList() {
        // clear existing list
        stops.clear();
        // Set first stop

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        assert locationManager != null;
        Location location = locationManager.getLastKnownLocation(Objects.requireNonNull(locationManager.getBestProvider(criteria, false)));
        if (location != null) {
            double lat = location.getLatitude();
            double longi = location.getLongitude();
            origin=Point.fromLngLat(longi,lat);
            stops.add(origin);
            Log.d("NSP", "zoomMyCuurentLocation: location not null");
        } else {
            setMyLastLocation();
        }
    }

    @SuppressLint("LogNotTimber")
    private void setMyLastLocation() {
        Log.d("NSP", "setMyLastLocation: excecute, and get last location");
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null){
                double lat = location.getLatitude();
                double longi = location.getLongitude();
                LatLng latLng = new LatLng(lat,longi);
                origin=Point.fromLngLat(longi,lat);
                stops.add(origin);
                Log.d("NSP", "MyLastLocation coordinate :"+latLng);
            }
        });
    }

    private void getOptimizedRoute(@NonNull final Style style, List<Point> coordinates) {
        optimizedClient = MapboxOptimization.builder()
                .source(FIRST)
                .destination(ANY)
                .coordinates(coordinates)
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .profile(DirectionsCriteria.PROFILE_DRIVING)
                .accessToken(Mapbox.getAccessToken() != null ? Mapbox.getAccessToken() : getString(R.string.access_token))
                .build();

        optimizedClient.enqueueCall(new Callback<OptimizationResponse>() {
            @SuppressLint("LogNotTimber")
            @Override
            public void onResponse(@NotNull Call<OptimizationResponse> call, @NotNull Response<OptimizationResponse> response) {
                if (!response.isSuccessful()) {
                    Log.e("NSP","NO SUCCESS");
                    Toast.makeText(ngoActivity.this, "NO SUCCESS", Toast.LENGTH_SHORT).show();
                } else {
                    if (response.body() != null) {
                        List<DirectionsRoute> routes = response.body().trips();
                        if (routes != null) {
                            if (routes.isEmpty()) {
                                Log.e("NSP","%s size = %s SUCCESSFUL BUT NO ROUTES "+routes.size());
                                Toast.makeText(ngoActivity.this, "SUCCESSFUL BUT NO ROUTES",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                // Get most optimized route from API response
                                optimizedRoute = routes.get(0);
                                drawOptimizedRoute(style, optimizedRoute);
//								if(optimizedRoute==null) {
//									Log.e("NSP","is null");
//								} else {
//									Log.e("NSP","is NOT null "+optimizedRoute.toString());
//									ReplayRouteLocationEngine replayEngine = new ReplayRouteLocationEngine();
//									replayEngine.assign(optimizedRoute);
//
//									navigation.setLocationEngine(replayEngine);
//									navigation.startNavigation(optimizedRoute);
//								}
                            }
                        } else {
                            Log.e("NSP","list of routes in the response is null");
                            Toast.makeText(ngoActivity.this, String.format("NULL RESPONSE",
                                    "The Optimization API response's list of routes"), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("NSP","response.body() is null");
                        Toast.makeText(ngoActivity.this, String.format("NULL RESPONSE",
                                "The Optimization API response's body"), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @SuppressLint("LogNotTimber")
            @Override
            public void onFailure(@NotNull Call<OptimizationResponse> call, @NotNull Throwable throwable) {
                Log.e("NSP","Error: %s "+throwable.getMessage());
            }
        });
    }

    private void drawOptimizedRoute(@NonNull Style style, DirectionsRoute route) {
        GeoJsonSource optimizedLineSource = style.getSourceAs("optimized-route-source-id");
        if (optimizedLineSource != null) {
            optimizedLineSource.setGeoJson(FeatureCollection.fromFeature(Feature.fromGeometry(
                    LineString.fromPolyline(Objects.requireNonNull(route.geometry()), PRECISION_6))));
        }
    }
    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent(Style style) {

        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            // Get an instance of the component
            LocationComponent locationComponent = mapboxMap.getLocationComponent();

            // Activate with a built LocationComponentActivationOptions object
            locationComponent.activateLocationComponent(LocationComponentActivationOptions.builder(this, style).build());

            // Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);

            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);

            // Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);

        } else {

            permissionsManager = new PermissionsManager(this);

            permissionsManager.requestLocationPermissions(this);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocationComponent(mapboxMap.getStyle());
        } else {
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
            finish();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cancel the directions API request
        if (optimizedClient != null) {
            optimizedClient.cancelCall();
        }
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    public void openDialog(final Marker marker1) {

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        TextView title = new TextView(this);

        title.setText("");
        title.setPadding(150, 10, 10, 10);   // Set Position
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.BLACK);
        title.setTextSize(20);
        alertDialog.setCustomTitle(title);

        TextView msg = new TextView(this);

        msg.setText("    IS THIS PLACE CLEAN");
        msg.setTextColor(Color.BLACK);
        msg.setTextSize(20);
        alertDialog.setView(msg);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, " YES ", (dialogInterface, i) -> {
            ProgressDialog pd=new ProgressDialog(ngoActivity.this);  // To show progress dialog
            pd.setMessage("Picking...");
            pd.setCancelable(false);
            pd.show();
            String key1=marker1.getSnippet().substring(marker1.getSnippet().lastIndexOf(':')+2);
            problemPoints.child(key1).addListenerForSingleValueEvent(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Shop shop=dataSnapshot.getValue(Shop.class);

                    Date date=new Date((new Timestamp(Long.parseLong(dataSnapshot.getKey()))).getTime());
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
                    String usedate=formatter.format(date);
                    System.out.println(usedate);
                    shopPickedRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                            if(dataSnapshot1.child(usedate).exists()) {
                                shopPickedRef.child(usedate).setValue(dataSnapshot1.child(usedate).getValue(Integer.class)+1);
                                problemPoints.child(key1).setValue(null);
                                showShopPoints();
                                pd.cancel();
                            } else {
                                shopPickedRef.child(usedate).setValue(1);
                                problemPoints.child(key1).setValue(null);
                                problemPoints.child(key1).setValue(null);
                                showShopPoints();
                                pd.cancel();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(getApplicationContext(),"Some error occurred",Toast.LENGTH_LONG).show();
                            pd.cancel();
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        });

        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL,"NO   ", (dialog, which) -> {
            // No need to write anything here
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
    }

    /*private void increment(int n) {
        DatabaseReference dataRef=FirebaseDatabase.getInstance().getReference("graph-data");
        dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String path="";
                switch (n)
                {
                    case 1:
                        path="usranic";
                        break;
                    case 2:
                        path="inusranic";
                        break;
                    case 3:
                        path="severe";
                        break;
                    case 4:
                        path="notsevere";
                        break;
                }
                if(dataSnapshot!=null) {
                    if (dataSnapshot.child(path).exists()) {
                        int temp = dataSnapshot.child(path).getValue(Integer.class);
                        dataRef.child(path).setValue(temp + 1);
                    } else {
                        dataRef.child(path).setValue(1);
                    }
                } else {
                    dataRef.child(path).setValue(1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }*/

    private void clearTable()
    {
        SharedPreferences preferences = getSharedPreferences("usersave", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

    private void saveTable()
    {
        SharedPreferences sharedPreferences=getSharedPreferences("usersave",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("User","no");
        editor.apply();
    }
}

