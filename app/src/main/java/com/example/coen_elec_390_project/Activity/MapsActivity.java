package com.example.coen_elec_390_project.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.coen_elec_390_project.Database.DatabaseHelper;
import com.example.coen_elec_390_project.Model.User;
import com.example.coen_elec_390_project.PermissionUtils;
import com.example.coen_elec_390_project.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.location.LocationRequest;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        LocationListener {

    private GoogleMap mMap;
    private Polyline Polyline;
    private LatLng lastKnownLatLng;
    private static final int LOCATION_PERMISSION_REQUEST_CODE =101;
    Location currentlocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    ArrayList<Polyline> polylines;
    ArrayList<LatLng> allLatLngs;
    LocationManager lm;
    DatabaseHelper databaseHelper;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        email = getIntent().getStringExtra("email");

        databaseHelper = new DatabaseHelper(this);
        final User user = databaseHelper.getUser(email);

        polylines = new ArrayList<>();
        allLatLngs = new ArrayList<>();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLastlocation();
        //Toast.makeText(this, "Current location:\n" + currentlocation, Toast.LENGTH_SHORT).show();
        Log.e("Tag","<Map> "+"Current location:\n" + currentlocation);
        setUpBottomNavigationView();
        lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
    }

    private void fetchLastlocation(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location!=null){
                    currentlocation=location;
                    Toast.makeText(getApplicationContext(),currentlocation.getLatitude()+""+currentlocation.getLongitude(),Toast.LENGTH_SHORT).show();
                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map_activity);
                    mapFragment.getMapAsync(MapsActivity.this);
                }
            }
        });
    }

    @Override
    public void onProviderEnabled(String a){}

    @Override
    public void onProviderDisabled(String a){}

    @Override
    public void onStatusChanged(String a,int b,Bundle c){

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.CYAN);
        polylineOptions.width(5);
        polylines.add(mMap.addPolyline(polylineOptions));
        LatLng latLng = new LatLng(currentlocation.getLatitude(),currentlocation.getLongitude());
        allLatLngs.add(latLng);
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("I am here");
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
        mMap.addMarker(markerOptions);
    }


    private void enableMyLocation() {
        // [START maps_check_location_permission]
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
            }
        } else {
            // Permission to access the location is missing. Show rationale and request permission
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        }
        // [END maps_check_location_permission]
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onLocationChanged(Location location) {
        //Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
        Log.e("Tag","<Map> "+"Current location:\n" + location);
        lastKnownLatLng = new LatLng(currentlocation.getLatitude(), currentlocation.getLongitude());

        PolylineOptions lineOptions = new PolylineOptions()
                .add(new LatLng(currentlocation.getLatitude(), currentlocation.getLongitude()))
                .add(new LatLng(location.getLatitude(), location.getLongitude()))
                .color(Color.CYAN)
                .width(5);
        // add the polyline to the map
        Polyline polyline = mMap.addPolyline(lineOptions);
        // set the zindex so that the poly line stays on top of my tile overlays
        polyline.setZIndex(1000);
        // add the poly line to the array so they can all be removed if necessary
        polylines.add(polyline);
        // add the latlng from this point to the array
        allLatLngs.add(new LatLng(location.getLatitude(),location.getLongitude()));

        currentlocation=location;
    }

    protected void startLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e("Tag","<Map> Permission issue");
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

    }

//    private void updateTrack() {
//
//        points.add(lastKnownLatLng);
//        gpsTrack.setPoints(points);
//    }

    private void setUpBottomNavigationView() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.map);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Intent intent;

                switch (menuItem.getItemId()){

                    case R.id.home:
                        intent = new Intent(new Intent(MapsActivity.this, MainActivity.class));
                        intent.putExtra("email", email);
                        startActivity(intent);
                        break;

                    case R.id.statistics:
                        intent = new Intent(new Intent(MapsActivity.this, StatisticsActivity.class));
                        intent.putExtra("email", email);
                        startActivity(intent);
                        break;

                    case R.id.profile:
                        intent = new Intent(new Intent(MapsActivity.this, ProfileActivity.class));
                        intent.putExtra("email", email);
                        startActivity(intent);
                        break;

                    case R.id.logout:
                        startActivity(new Intent(MapsActivity.this, StartActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        break;
                }

                return true;
            }
        });
    }
}