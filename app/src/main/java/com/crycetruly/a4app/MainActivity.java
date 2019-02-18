package com.crycetruly.a4app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.crycetruly.a4app.fragments.JourneysActivity;
import com.crycetruly.a4app.utils.GetCurTime;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationItemView;
    private FrameLayout frameLayout;
    private Fragment home;

    private CardView card1,card2,card3;
    CardView card4;
    private static final String TAG = "MainActivity";
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private boolean mLocationPermissionsGranted;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 11;
    Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainn);
        initWidgets();
        initLocationStuff();


        setUpToolbar();
    }


        private void initWidgets() {
        FirebaseDatabase.getInstance().getReference().child("chat").child("GdsBLKbZWoa5LJhDMxOGeEUZDv03").
                child("GdsBLKbZWoa5LJhDMxOGeEUZDv03").removeValue();

        FirebaseFirestore.getInstance().collection("messages").document("GdsBLKbZWoa5LJhDMxOGeEUZDv03").
                collection("GdsBLKbZWoa5LJhDMxOGeEUZDv03").document().delete();
            mContext=MainActivity.this;
            card1=findViewById(R.id.card1);
            card2=findViewById(R.id.card2);
            card3=findViewById(R.id.card3);
            card4=findViewById(R.id.card6);
            card4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i=new Intent(mContext,SerchActivity.class);
                    MainActivity.this.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                    i.putExtra("key","vht");
                    startActivity(i);

                }
            });
            card1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i=new Intent(mContext,DetailActivity.class);
                    MainActivity.this.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                    i.putExtra("key","treat");
                    startActivity(i);

                }
            });

            card2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i=new Intent(mContext,DetailActivity.class);
                    MainActivity.this.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                    i.putExtra("key","council");
                    startActivity(i);

                }
            });

            card3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                    CharSequence [] op={"Report for myself","Report for someone else"};

                    final FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();


                    builder.setItems(op, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (i==0){
                                if (user==null){
                                    Intent intent=new Intent(getBaseContext(),AuthActivity.class);
                                    SharedPreferences sharedPreferences= getSharedPreferences("reportby",MODE_PRIVATE);
                                    SharedPreferences.Editor editor=sharedPreferences.edit();
                                    editor.putString("reportedby","self");
                                    editor.apply();
                                    startActivity(intent);
                                }else{
                                    startActivity(new Intent(getBaseContext(),StartActivity.class));
                                }


                            }else {
                                if (user==null){
                                Intent intent=new Intent(getBaseContext(),AuthActivity.class);
                                SharedPreferences sharedPreferences= getSharedPreferences("reportby",MODE_PRIVATE);
                                SharedPreferences.Editor editor=sharedPreferences.edit();
                                editor.putString("reportedby","other");
                                editor.apply();
                                startActivity(intent);

                            }else{
                                startActivity(new Intent(getBaseContext(),Start2Activity.class));
                            }
                            }
                        }
                    }).setCancelable(true).show();
                }
            });

        }

 public void initLocationStuff() {
getLocationPermission();
getDeviceLocation();

    }
    public  void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getBaseContext());

        try {
            if (mLocationPermissionsGranted) {

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();
                            Geocoder geocoder = new Geocoder(getBaseContext());
                            try {
                                try {

                                    List<Address> addresses = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);

                                    try {
                                        Log.d(TAG, "onComplete: " + currentLocation.getLatitude() + currentLocation.getLongitude());
                                    } catch (NullPointerException e) {
                                        Log.d(TAG, "onComplete: current location is null");

                                    }
                                    Address place = addresses.get(0);
                                    Log.d(TAG, "onComplete: " + place.toString());
                                    Log.d(TAG, "onComplete: " + place.getLocality());
                                    SharedPreferences preferences = getSharedPreferences("locality", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putString("locality", place.getLocality());
                                    try {
                                        editor.putString("aaddress", place.getAddressLine(1));
                                    } catch (Exception e) {

                                    }

                                    try {
                                        editor.putString("adminarea", place.getAdminArea());
                                    } catch (Exception e) {

                                    }
                                    try {
                                        editor.putString("sublocality", place.getSubLocality());
                                    } catch (Exception e) {

                                    }
                                    try {

                                        editor.putString("feature", place.getFeatureName());
                                        editor.putString("subadminarea", place.getSubAdminArea());
                                    } catch (Exception E) {

                                    }
                                    editor.apply();
                                } catch (NullPointerException e) {
                                }


                            } catch (IOException e) {
                                e.printStackTrace();
                                Log.d(TAG, "onComplete: something wrong happemed" + e.getMessage());
                            }


                        } else {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(getBaseContext(), "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;

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
    protected void onStop() {
        try{
            FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().
                    getCurrentUser().getUid()).child("online").setValue(ServerValue.TIMESTAMP);
        }catch (NullPointerException e){

        }
        super.onStop();
    }

    @Override
    protected void onStart() {
        try{
            FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().
                    getCurrentUser().getUid()).child("online").setValue("true");
        }catch (NullPointerException e){

        }
        super.onStart();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;


                }
            }
        }

    }

    private void setUpToolbar() {
        Log.d(TAG, "setUpToolbar: ");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setSubtitle("Dont Die with it");
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainthree,menu);

        if (FirebaseAuth.getInstance().getCurrentUser()==null){
            menu.getItem(1).setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()){
            case R.id.circle:
                Intent i=new Intent(this,CircleActivity.class);
                i.putExtra("key","circle");

                startActivity(i);
                break;

            case R.id.ambulance:
                Intent intent=new Intent(this,JourneysActivity.class);
                intent.putExtra("key","amb");
                startActivity(intent);
                break;

            case R.id.logout:
                AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

startActivity(new Intent(MainActivity.this,AppIntroActivity.class));
                    }
                });
                break;

            case R.id.about:
                Intent intent1=new Intent(mContext,DetailActivity.class);
                MainActivity.this.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                intent1.putExtra("key","about");
                startActivity(intent1);

                break;
            case R.id.settings:
                Intent intent2=new Intent(mContext,SettingsActivity.class);
                MainActivity.this.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);

                startActivity(intent2);

                break;

        }
        return super.onOptionsItemSelected(item);
    }
}
