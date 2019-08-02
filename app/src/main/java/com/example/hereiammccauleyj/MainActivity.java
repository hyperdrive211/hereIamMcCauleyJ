package com.example.hereiammccauleyj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "MainActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 5f;
    private static final String HOME_DESTINATION ="Europe/London";

    //variables
    private Boolean mLocationPermissionGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location currentLocation;
    private String apiLink ="https://maps.googleapis.com/maps/api/timezone/";
    private Button dataButton;


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (mLocationPermissionGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            setMarker(mMap);
        } //Location Permission Granted
    }// On Map Ready


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getLocationPermission();
    }


    //Set Marker of Soft Spot Using hardcoded values.
    private void setMarker (GoogleMap mMap){
        LatLng magee = new LatLng(55.006, -7.19);
        mMap.addMarker(new MarkerOptions().position(magee).title("Soft Spot HQ"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(magee));
    }


    private void getDeviceLocation(){
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        try{
            if(mLocationPermissionGranted){
                Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            //Set up the camera position to the current location on the map.
                            currentLocation = (Location) task.getResult();
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM);
                            getButtonText(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));


                        }else{
                          Toast.makeText(MainActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }

        } catch (SecurityException e){
            Log.e("Exception: %s", e.getMessage());
        }

    }

    private void moveCamera(LatLng latLng, float zoom){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                zoom));
    }

    private void initMap(){
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(MainActivity.this);
    }

    private void getLocationPermission(){
        String [] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),FINE_LOCATION)
        == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED){
              mLocationPermissionGranted = true;
              initMap();
            }else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
       mLocationPermissionGranted = false;

       switch(requestCode){
           case LOCATION_PERMISSION_REQUEST_CODE : {
               if(grantResults.length > 0){
                 for(int i = 0; i < grantResults.length; i++){
                     if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                         mLocationPermissionGranted = false;
                         return;
                     }
                 }
                 mLocationPermissionGranted = true;
                 //initialise our map
                 //initMap();
               }
           }
       }
    }

    private void getButtonText(LatLng latLng){

        dataButton = findViewById(R.id.text_button);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(apiLink).addConverterFactory(GsonConverterFactory.create()).build();
        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        Call<TimeZoneData> call = jsonPlaceHolderApi.getTimeZone(getApiLink(latLng.latitude,
                latLng.longitude, getResources().getString(R.string.api_timezone_key)));
        call.enqueue(new Callback<TimeZoneData>() {
            @Override
            public void onResponse(Call<TimeZoneData> call, Response<TimeZoneData> response) {
            if(!response.isSuccessful()){
                dataButton.setText("Request from server: " + response.code());
                return;
            }

                TimeZoneData locationTimeZone = response.body();
                String timezoneString = locationTimeZone.getTimeZoneId();
                Log.d("Retrofit " , "Value expected;  " + timezoneString);
                dataButton.setText( textOutput(timeZoneText(timezoneString), timezoneString, timeZoneText(HOME_DESTINATION)));
            }

            @Override
            public void onFailure(Call<TimeZoneData> call, Throwable t) {
             dataButton.setText("Problem getting Data/n "+ t.getMessage());
            }
        });
    }

    private String getApiLink(double lat, double lng, String apiKey){
        return "json?location="+lat+","+lng+"&timestamp=1331161200&key="+apiKey;
    }

    private String timeZoneText(String timezone){
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone(timezone));
        Log.d(TAG, "timeZoneText: " + timezone);
        int hour12 = cal.get(Calendar.HOUR_OF_DAY); // 0..11
        int minutes = cal.get(Calendar.MINUTE); // 0..59
        int seconds = cal.get(Calendar.SECOND); // 0..59
        return formatTime(hour12)+":"+formatTime(minutes)+":"+formatTime(seconds);
    }

    private String textOutput(String destTime, String timezone, String homeTime){
        return "Time now : " + destTime +"\n TimeZone : " + timezone + "\n Spoft Spot HQ Time: " + homeTime;
    }

    private String formatTime(int time){
        return time < 10 ? "0"+ String.valueOf(time) : String.valueOf(time);
    }


}

