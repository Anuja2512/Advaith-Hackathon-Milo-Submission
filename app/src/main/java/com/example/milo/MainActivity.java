package com.example.milo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.*;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LocationListener {
    LocationManager locationManager;
    private GoogleMap mMap;
    Button btn;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    public static final int REQUEST_CODE = 101;
    Geocoder geocoder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://source.unsplash.com/random");
                    Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @SuppressLint("Range")
                        @Override
                        public void run() {
                            LinearLayout relativeLayout = findViewById(R.id.layout);
                            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
                            relativeLayout.setBackground(new BitmapDrawable(image));
                            relativeLayout.getBackground().setAlpha(200);
                            //profiledp.setImageBitmap(image);
                        }
                    });
                } catch(IOException e) {
                    System.out.println(e);
                }
            }
        });
        thread1.start();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLastLocation();
        geocoder = new Geocoder(this);
        //Runtime permissions
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            },100);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences=getSharedPreferences(SignInActivity.PREFS_NAME, 0);
                boolean hasLoggedIn = sharedPreferences.getBoolean("hasLoggedIn", false);
                if(hasLoggedIn)
                {
                    Intent intent = new Intent(MainActivity.this, Dashboard.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        },1500);

    }

    @SuppressLint("MissingPermission")
    private void getLocation() {


        try {
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,5,MainActivity.this);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(this, ""+location.getLatitude()+","+location.getLongitude(), Toast.LENGTH_SHORT).show();
        try {
            Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            String address = addresses.get(0).getAddressLine(0);
            Toast.makeText(this, "hiiii", Toast.LENGTH_SHORT).show();


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //https://github.com/Harsh-Singh-007/Essential_Services/blob/6903972795e0116a33f568add4dd6633121cf9d5/app/src/main/java/com/example/find/maps.java

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null)
                {
                    currentLocation = location;
                   // Toast.makeText(getApplicationContext(),currentLocation.getLatitude()+" "+currentLocation.getLongitude(),Toast.LENGTH_SHORT).show();
                    Double latitude=currentLocation.getLatitude();
                    Double longit=currentLocation.getLongitude();
                }
            }
        });
    }

}