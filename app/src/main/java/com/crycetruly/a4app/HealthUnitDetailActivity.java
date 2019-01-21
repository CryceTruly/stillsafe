package com.crycetruly.a4app;

import android.*;
import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.crycetruly.a4app.MapActivity;
import com.crycetruly.a4app.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;

public class HealthUnitDetailActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 1;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 3;
    Toolbar toolbar;
    String key;
    String name, pic, desc, opentime, district;
    TextView names, des, open;
    ImageView imageView;
    TextView button, dist;
    String lat, lng, phone;
    RelativeLayout it;
    private static final String TAG = "HealthUnitDetailActivit";
    private boolean mPermissionGranted = false;

    private Double lat1, lng1;
    FusedLocationProviderClient client;
    TextView dista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_unit_detail);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("HealthUnit Detail");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        name = getIntent().getStringExtra("cname");
        desc = getIntent().getStringExtra("cprofile");
        pic = getIntent().getStringExtra("cpic");
        opentime = getIntent().getStringExtra("copen");
        district = getIntent().getStringExtra("district");
        phone = getIntent().getStringExtra("phone");
        dista = findViewById(R.id.distance);
        getLocationPermission();
        getDeviceLocation();
        it = findViewById(R.id.it)
        ;
        dist = findViewById(R.id.district);
        dist.setText(district);

        lat = getIntent().getStringExtra("lat");
        lng = getIntent().getStringExtra("lng");
        Log.d(TAG, "onCreate: recieved cods" + lat + lng);

        checkPermissions();
        getDeviceLocation
                ();
        imageView = findViewById(R.id.healthunitphoto);
        open = findViewById(R.id.open);
        open.setText(opentime);
        names = findViewById(R.id.name);
        des = findViewById(R.id.description);
        Glide.with(getBaseContext()).load(pic).into(imageView);
        names.setText(name);
        des.setText(desc);
        key = getIntent().getStringExtra("snap");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.unitdetail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            case R.id.one:
                if (mPermissionGranted)
                    Log.d(TAG, "onOptionsItemSelected: call");
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + phone));

                try {
                    if (!phone.equals("")) {
                        if (intent.resolveActivity(getPackageManager()) != null) {

                            String dial = "tel:" + phone;
                            try {
                                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(dial)));
                            } catch (ActivityNotFoundException e) {
                                Toast.makeText(this, "No apps", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(this, "Unable to handle data", Toast.LENGTH_SHORT).show();
                        }


                        Log.d(TAG, "onOptionsItemSelected: calling");
                        break;

                    } else {
                        Toast.makeText(this, "Unavailable", Toast.LENGTH_SHORT).show();
                    }
                } catch (NullPointerException e) {

                }

                break;

            case R.id.two:

//todo send gps too

                if (!TextUtils.isEmpty(phone)) {
                    Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phone));
                    smsIntent.putExtra("sms_body", "Hi i need PEP services");
                    if (smsIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(smsIntent);
                    } else {
                        Toast.makeText(this, "No apps", Toast.LENGTH_SHORT).show();
                    }

                }
                break;
            case R.id.three:
                Intent i = new Intent(getBaseContext(), MapActivity.class);
                i.putExtra("healthunitid", key);
                i.putExtra("name", name);
                i.putExtra("desc", desc);
                i.putExtra("lat", lat);
                i.putExtra("lng", lng);
                startActivity(i);
                break;
        }


        return super.onOptionsItemSelected(item);

    }



    private void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CALL_PHONE,
                            Manifest.permission.SEND_SMS}
                    , REQUEST_CODE);


        }
    }

    public void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        client = LocationServices.getFusedLocationProviderClient(getBaseContext());
        if (mPermissionGranted) {

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
            final Task location = client.getLastLocation();
                location.addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "onComplete: found location!");
                        Location currentLocation = (Location) task.getResult();
                        if (currentLocation!=null){
                            lat1=currentLocation.getLatitude();
                            lng1=currentLocation.getLongitude();
                            Log.d(TAG, "onSuccess: my own "+lat1+lng1);
                            dista.setVisibility(View.VISIBLE);
                            Location location2=new Location("");
                            location2.setLatitude(Double.parseDouble(lat));
                            location2.setLongitude(Double.parseDouble(lng));


                            Location location3=new Location("");
                            location3.setLongitude(lng1);
                            location3.setLatitude(lat1);

                            float distinMteters= Float.parseFloat(String.format("%.2f",location2.distanceTo(location3)));

                            float distance1=(distinMteters>1000)? distinMteters:distinMteters/1000;
                            String newd=String.valueOf(distance1);
                            String prefix=(distinMteters>1000)? "Meters":"Kilometers";
                            String locationfinal= "You are  "+newd+ " "+prefix+" from this Health center";
                            dista.setText(locationfinal);

                            //dist.setText(String.format("%.2",locationfinal));




                        }else {
                            dista.setVisibility(View.GONE);
                        }
                    }
                });
            }

        }
    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.CALL_PHONE};

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mPermissionGranted = true;

            } else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mPermissionGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mPermissionGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mPermissionGranted = true;


                }
            }
        }

    }
}
