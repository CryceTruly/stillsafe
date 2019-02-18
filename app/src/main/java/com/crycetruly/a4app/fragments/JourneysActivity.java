package com.crycetruly.a4app.fragments;


import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.crycetruly.a4app.R;
import com.crycetruly.a4app.remote.Common;
import com.crycetruly.a4app.remote.iGoogleApi;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class JourneysActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap map;
    private LatLng myLocation;
    private Button goBtn;
    private List<LatLng> polylineList;
    private Marker marker;
    private float v;
    private EditText place;
    private int index, next;
    private String destination;
    private Handler handler;
    private double lat, lng;
    private LatLng startPosition, endPosition;
    private Polyline blackPolyline, greyPolyline;
    private PolylineOptions polylineOptions, blackPolylineOptions;
    MapFragment supportMapFragment;
    iGoogleApi mService;
    private static final String TAG = "JourneysActivity";

    private Context context = this;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_journeys);
        supportMapFragment = (MapFragment)
                getFragmentManager().findFragmentById(R.id.mapFragment);
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                LatLng here = new LatLng(-0.61440, 30.65692);
                map.addMarker(new MarkerOptions().position(here).title("Here"));
                map.moveCamera(CameraUpdateFactory.newLatLng(here));
            }
        });


        polylineList = new ArrayList<>();

        goBtn = findViewById(R.id.search);
        place = findViewById(R.id.searchUserField);
        goBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                destination = place.getText().toString().trim();
                destination = destination.replace(" ", "+");

                supportMapFragment.getMapAsync(JourneysActivity.this);
            }
        });


        mService = Common.getGoogleApi();


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.setTrafficEnabled(false);
        map.setIndoorEnabled(false);
        map.setBuildingsEnabled(false);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                .target(googleMap.getCameraPosition().target
                ).bearing(30).zoom(17).tilt(45)
                .build()));

        final LatLng myLoc = new LatLng(-0.61440, 30.65692);

        String requestUrl = "";





        try {
            requestUrl = "https://maps.googleapis.com/maps/api/directions/json?"
                    + "mode=driving&"
                    + "transit_routing_preference=less_driving&"
                    + "origin=" + myLoc.latitude
                    + myLoc.longitude + ""
                    + "&"
                    + "destination=" + "Faculty of Development Studies, Mbarara, Uganda"
                    + "&"
                    + "key=AIzaSyA4Z_BkaPI3GhzqVR_OciyoYikrVgs-YXA"
            ;
//            requestUrl="https://maps.googleapis.com/maps/api/directions/json?" +
//                    "mode=driving&" +
//                    "transit_routing_preference=less_driving&" +
//                    "origin=Faculty of Development Studies, Mbarara, Uganda&"
//                    +"&"
//                    +"destination=MetLife+Stadium+1+MetLife+Stadium+Dr+East+Rutherford,+NJ+07073&"
//                    +
//                    "key=AIzaSyA4Z_BkaPI3GhzqVR_OciyoYikrVgs-YXA";

            Log.d(TAG, "onMapReady: URL " + requestUrl);

            mService.getDataFromGoogleApi(requestUrl).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Log.d(TAG, "onResponse: "+response.toString());

                    try {
                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        JSONArray jsonArray = jsonObject.getJSONArray("routes");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject route = jsonArray.getJSONObject(i);
                            final JSONObject poly = route.getJSONObject("overview_polyline");
                            String dpoly = poly.getString("points");

                            polylineList = decodePoly(dpoly);


                            LatLngBounds.Builder builder = new LatLngBounds.Builder();

                            for (LatLng latLng : polylineList) {
                                builder.include(latLng);
                                LatLngBounds bounds = builder.build();
                                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 2);

                                map.animateCamera(cameraUpdate);

                                polylineOptions = new PolylineOptions();
                                polylineOptions.color(Color.GRAY);
                                polylineOptions.width(5);
                                polylineOptions.startCap(new SquareCap());

                                polylineOptions.endCap(new SquareCap());
                                polylineOptions.jointType(JointType.ROUND);
                                polylineOptions.addAll(polylineList);
                                greyPolyline = map.addPolyline(polylineOptions);


                                blackPolylineOptions = new PolylineOptions();
                                blackPolylineOptions.color(Color.BLUE);
                                blackPolylineOptions.width(5);
                                blackPolylineOptions.startCap(new SquareCap());

                                blackPolylineOptions.endCap(new SquareCap());
                                blackPolylineOptions.jointType(JointType.ROUND);
                                blackPolylineOptions.addAll(polylineList);
                                blackPolyline = map.addPolyline(polylineOptions);
                                map.addMarker(new MarkerOptions().position(polylineList.get(polylineList.size() - 1)));


                                ValueAnimator valueAnimator = ValueAnimator.ofInt(0, 100);
                                valueAnimator.setDuration(2000).setInterpolator(new LinearInterpolator());
                                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                        List<LatLng> points = greyPolyline.getPoints();
                                        int percentValue = (int) valueAnimator.getAnimatedValue();
                                        int size = points.size();
                                        int newPoints = (int) (size * (percentValue / 100.0f));
                                        List<LatLng> p = points.subList(0, newPoints);
                                        blackPolyline.setPoints(p);
                                    }
                                });

                                valueAnimator.start();


//todo do boy girl
                                marker = map.addMarker(new MarkerOptions().position(myLoc)
                                        .flat(true)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_walk)));


                                handler = new Handler();
                                index = -1;
                                next = 1;

                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (index < polylineList.size() - 1) {
                                            index++;
                                            next = index + 1;

                                        }

                                        if (index < polylineList.size() - 1) {
                                            startPosition = polylineList.get(index);
                                            endPosition = polylineList.get(next);
                                        }


                                        ValueAnimator polyLineAnimator = ValueAnimator.ofFloat(0, 1);
                                        polyLineAnimator.setDuration(3000).setInterpolator(new LinearInterpolator());
                                        polyLineAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                            @Override
                                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                                v = valueAnimator.getAnimatedFraction();
                                                lng = v * endPosition.longitude + (1 - v) * startPosition.longitude;
                                                lat = v * endPosition.latitude + (1 - v) * endPosition.latitude;

                                                LatLng newPos = new LatLng(lat, lng);
                                                marker.setPosition(newPos);

                                                marker.setAnchor(0.5f, 0.5f);
                                                marker.setRotation(getBearing(startPosition, newPos));

                                                map.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(
                                                        newPos
                                                        )
                                                                .zoom(15.5f).build()


                                                ));


                                            }
                                        });

                                        polyLineAnimator.start();
                                        handler.postDelayed(this, 3000);

                                    }
                                }, 3000);


                            }
                        }
                    } catch (Exception e) {

                    }

                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.d(TAG, "onFailure: "+t);
                    Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e)

        {
            e.printStackTrace();

        }

    }

    private float getBearing(LatLng startPosition, LatLng newPos) {
        Log.d(TAG, "getBearing: getting bearing from " + startPosition + "to" + newPos);
        double lat = Math.abs(startPosition.latitude - newPos.latitude);
        double lng = Math.abs(startPosition.longitude - newPos.longitude);

        if (startPosition.latitude < newPos.latitude && startPosition.longitude < newPos.longitude)
            return (float) Math.toDegrees(Math.atan(lng / lat));
         else if (startPosition.latitude >= newPos.latitude && startPosition.longitude < newPos.longitude)
            return (float) ((90 - Math.toDegrees(90 - Math.atan(lng / lat))) + 90);
         else if (startPosition.latitude >= newPos.latitude && startPosition.longitude >= newPos.longitude)
            return (float) Math.toDegrees(Math.atan(lng / lat) + 180);
         else if (startPosition.latitude < newPos.latitude && startPosition.longitude >= newPos.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 270);


            return -1;


        }


    private List<LatLng> decodePoly(String encoded) {
        Log.d(TAG, "decodePoly: decording polygons");

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
}
