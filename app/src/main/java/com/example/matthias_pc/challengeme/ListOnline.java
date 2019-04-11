package com.example.matthias_pc.challengeme;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.matthias_pc.challengeme.model.Challenge;
import com.example.matthias_pc.challengeme.model.ChallengeAttribute;
import com.example.matthias_pc.challengeme.model.ChallengeType;
import com.example.matthias_pc.challengeme.model.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.util.List;

public class ListOnline extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener, OnMapReadyCallback,  MapboxMap.OnMapClickListener, PermissionsListener {

    //Firebase
    DatabaseReference onlineRef, currentUserRef, counterRef, locations, challengeRef;
    FirebaseRecyclerAdapter<User, ListOnlineViewHolder> adapter;
    FirebaseRecyclerOptions<User> options =
            new FirebaseRecyclerOptions.Builder<User>()
                    .setQuery(counterRef, User.class)
                    .build();

    //View
    RecyclerView listOnline;
    RecyclerView.LayoutManager layoutManager;

    //Location
    private static final int MY_PERMISSION_REQUEST_CODE = 7171;
    private static final int PLAY_SERVICES_RES_REQUEST = 7172;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mgGoogleApiClient;
    private Location mLastLocation;
    private static int UPDATE_INTERVAL = 5000;
    private static int FASTEST_INTERVAL = 3000;
    private static int DISTANCE = 10;
    private FusedLocationProviderClient mFusedLocationClient;

    private double lon;
    private double lat;

    private MapView mapView;
    private MapboxMap mapboxMap;

    private PermissionsManager permissionsManager;
    private LocationComponent locationComponent;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_online);
        setTitle("Users Online");

        Mapbox.getInstance(this, "pk.eyJ1IjoiY2hhbGxlbmdlLW1lIiwiYSI6ImNqdHI5bjF4MTBubTU0NHBhM3Qzam85MjcifQ.RbitFEoz8u1-ID8N-4ZSIw");

        mapView = new MapView(this);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        //init view
        listOnline = (RecyclerView) findViewById(R.id.listOnline);
        listOnline.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        listOnline.setLayoutManager(layoutManager);


        //Set toolbar and Logout / Join menu
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Users Online");
        setSupportActionBar(toolbar);


        //Firebase
        locations = FirebaseDatabase.getInstance().getReference();
        onlineRef = FirebaseDatabase.getInstance().getReference().child(".info/connected");
        counterRef = FirebaseDatabase.getInstance().getReference("lastOnline"); // Create new child
        currentUserRef = FirebaseDatabase.getInstance().getReference("lastOnline")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()); // create new child in Lastonline with key is Uid
        challengeRef = FirebaseDatabase.getInstance().getReference();



        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, MY_PERMISSION_REQUEST_CODE);
        } else {
            if (checkPlayServices()) {
                buildGoogleApiClient();
                createLocationRequest();
                displayLocation();
            }
        }
        setupSystem();
        //After setup system, just load all user from counterRef and display on RecyclerView

        updateList();


        //Button
        final RelativeLayout container =(RelativeLayout)findViewById(R.id.list_Online_layout);
        final RelativeLayout rl = new RelativeLayout(getApplicationContext());
        final RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        );
        final Button btnStartRun = findViewById(R.id.buttonCreateChallenge);
        final Button btnStopRun = new Button(this);
        final Button btnCreateChallenge = new Button(this);
        final Button btnCancel = new Button(this);
        final RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        final RelativeLayout.LayoutParams lp3 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );



        btnStartRun.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                    rl.setLayoutParams(lp);

                    final TextView tv = new TextView(getApplicationContext());

                    RelativeLayout.LayoutParams lp_tv = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

                    lp_tv.addRule(RelativeLayout.CENTER_IN_PARENT);

                    tv.setLayoutParams(lp_tv);
                    tv.setTextSize(30);
                    tv.setTextColor(Color.parseColor("#FFFFEE00"));
                    tv.setTypeface(tv.getTypeface(), Typeface.BOLD_ITALIC);

                    rl.addView(tv);

                    setContentView(rl);

                    btnCancel.setText("Cancel");
                    btnStopRun.setText("Stop Run");
                    btnCreateChallenge.setText("Create Challenge");

                    btnCancel.setLayoutParams(lp3);
                    btnCancel.setBackgroundResource(R.drawable.btn_rounded_black);
                    btnCancel.setTextColor(Color.parseColor("#FFFFEE00"));
                    btnCancel.setId(R.id.btnCancel);

                    btnStopRun.setLayoutParams(lp2);
                    btnStopRun.setBackgroundResource(R.drawable.btn_rounded);
                    btnStopRun.setTextColor(Color.parseColor("#000000"));
                    btnStopRun.setId(R.id.btnStopRun);

                    btnCreateChallenge.setLayoutParams(lp2);
                    btnCreateChallenge.setBackgroundResource(R.drawable.btn_rounded);
                    btnCreateChallenge.setId(R.id.btnCreateChallenge);
                    btnCreateChallenge.setTextColor(Color.parseColor("#000000"));

                    lp2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    lp2.setMargins(15,0,15,15);

                    lp3.addRule(RelativeLayout.ABOVE, R.id.btnCreateChallenge);
                    lp3.setMargins(15,0,15,15);


                    //container.addView(tv);
                    new CountDownTimer(3000, 1000) {
                        final TextView text = tv;

                        public void onTick(long millisUntilFinished) {
                            tv.setText("Start in: " + millisUntilFinished / 1000);
                        }

                        public void onFinish() {
                            tv.setText(null);
                           // btnStartRun.setText("Stop run");
                            container.addView(btnStopRun);
                            container.removeView(btnStartRun);
                            setContentView(container);
                            mapView.onResume();
                            container.addView(mapView);

                        }
                    }.start();
                }
        });
        btnStopRun.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                btnStartRun.setText("Create Challenge");
                container.removeView(btnStopRun);
                container.addView(btnCreateChallenge);
                container.addView(btnCancel);
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        openDialog(v);
                    }
                });
            }});

        btnCreateChallenge.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                btnStartRun.setText("Start run");
                container.removeView(btnCancel);
                container.removeView(btnCreateChallenge);
                container.addView(btnStartRun);
                createNewChallenge();
            }
        });
    }



    private void displayLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


            return;

        }
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    com.example.matthias_pc.challengeme.model.Location newLocation = new com.example.matthias_pc.challengeme.model.Location
                            (String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));

                    locations.child("Locations").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(newLocation);
                    lon = Double.valueOf(location.getLongitude());
                    lat = Double.valueOf(location.getLatitude());
                    } else {
                    Toast.makeText(getParent(), "Couldn't get the location", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setSmallestDisplacement(DISTANCE);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    private void buildGoogleApiClient() {
        mgGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mgGoogleApiClient.connect();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RES_REQUEST).show();
            } else {
                Toast.makeText(this, "This device is not supported", Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;
    }

    private void updateList() {
        adapter = new FirebaseRecyclerAdapter<User, ListOnlineViewHolder>(options) {
            @NonNull
            @Override
            public ListOnlineViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                return null;
            }

            @Override
            protected void onBindViewHolder(@NonNull ListOnlineViewHolder holder, int position, @NonNull User model) {
                holder.txtEmail.setText(model.getEmail());
            }


        };
        adapter.notifyDataSetChanged();
        listOnline.setAdapter(adapter);

    }

    private void setupSystem() {
        onlineRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(Boolean.class)) {
                    currentUserRef.onDisconnect().removeValue();//Delete old value
                    //Set online user in list
                    counterRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(new User(FirebaseAuth.getInstance().getCurrentUser().getEmail(), "Online"));

                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        counterRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    User user = postSnapshot.getValue(User.class);
                    Log.d("Log", "" + user.getEmail() + " is " + user.getStatus());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (checkPlayServices()) {
                        buildGoogleApiClient();
                        createLocationRequest();
                        displayLocation();
                        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
                    }
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_join:
                counterRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .setValue(new User(FirebaseAuth.getInstance().getCurrentUser().getEmail(), "Online"));
                break;

            case R.id.action_logout:
                currentUserRef.removeValue();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mgGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mgGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;
        displayLocation();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mgGoogleApiClient != null) {
            mgGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        if (mgGoogleApiClient != null) {
            mgGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        checkPlayServices();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //mapView.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    public void openDialog(final View view){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure you want to cancel");
        alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                Toast.makeText(ListOnline.this,"You clicked yes button",Toast.LENGTH_LONG).show();
                startActivity(new Intent(ListOnline.this,ListOnline.class));
            }
        });

        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }




    private void createNewChallenge() {
                    long ts = System.currentTimeMillis()/1000;
                    ChallengeAttribute challengeAttributeLat = new ChallengeAttribute("Latitude");
                    ChallengeAttribute challengeAttributeLon = new ChallengeAttribute("Longitude");
                    ChallengeType challengeType = new ChallengeType(lat, challengeAttributeLat, lon, challengeAttributeLon);

                    User user = new User(FirebaseAuth.getInstance().getCurrentUser().getEmail(), null);
                    Challenge challengeNew = new Challenge(ts, ts,"running challenge", challengeType, user);

                    challengeRef.child("Challenges").push().setValue(challengeNew);

            }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;

        //mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 2000);

        mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lon), 15.5f), 2000, null);

        mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                enableLocationComponent(style);
            }
        });
    }

    @Override
    public boolean onMapClick(@NonNull LatLng point) {
        return false;
    }

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            // Activate the MapboxMap LocationComponent to show user location
            // Adding in LocationComponentOptions is also an optional parameter
            locationComponent = mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(this, loadedMapStyle);
            locationComponent.setLocationComponentEnabled(true);
            // Set the component's camera mode
            //locationComponent.setCameraMode(CameraMode.TRACKING);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
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
}

