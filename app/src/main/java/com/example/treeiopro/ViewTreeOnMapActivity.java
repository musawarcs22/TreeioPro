package com.example.treeiopro;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;

public class ViewTreeOnMapActivity extends AppCompatActivity {

    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_tree_on_map);

        supportMapFragment=(SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.google_map);
        client = LocationServices.getFusedLocationProviderClient(this);

        Dexter.withContext(getApplicationContext())
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        getmylocation();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    public void getmylocation() {
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

        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(final Location location) {
                supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        // Code breakes due to following line if locations is not turned on in the mobile//
                        LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
                        MarkerOptions markerOptions=new MarkerOptions().position(latLng).title("Tree is heare");
                        googleMap.addMarker(markerOptions);

                        // Experimentation Adding info window to marker//

                        final LatLng melbourneLatLng = new LatLng(33.483, 73.101);
                        Marker melbourne = googleMap.addMarker(
                                new MarkerOptions()
                                        .position(melbourneLatLng)
                                        .title("Bahria Town"));
                        melbourne.showInfoWindow();




                        // Experimentation Adding one additional marker start//
                        Log.i("LOC", "onMapReady: "+latLng);
                        LatLng latLng1 = new LatLng(33.483475,73.101640);
                        MarkerOptions markerOption2=new MarkerOptions().position(latLng1).title("Tree is heare");
                        googleMap.addMarker(markerOption2);
                        // Experimentation Adding one additional marker ends//

                        // Experimentation Adding multiple markers start//
                        MarkerOptions options = new MarkerOptions();
                        ArrayList<LatLng> latlngs = new ArrayList<>();
                        latlngs.add(new LatLng(33.483475,73.101700)); //some latitude and logitude value
                        latlngs.add(new LatLng(33.483485,73.101660)); //some latitude and logitude value
                        latlngs.add(new LatLng(33.483475,73.101750)); //some latitude and logitude value
                        latlngs.add(new LatLng(33.483490,73.101880)); //some latitude and logitude value
                        latlngs.add(new LatLng(33.483495,73.101990)); //some latitude and logitude value

                        for (LatLng point : latlngs) {
                            options.position(point);
                            options.title("someTitle");
                            options.snippet("someDesc");
                            googleMap.addMarker(options);
                        }
                        // Experimentation Adding multiple markers ends//
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlngs.get(0),16)); // For Zooming in to a particular loactiona


                        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(Marker marker) {
                                String markerTitle = marker.getTitle();

                                Log.i("MMB", "onMarkerClick: "+markerTitle);
                                Toast.makeText(ViewTreeOnMapActivity.this, marker.getPosition()+"", Toast.LENGTH_SHORT).show();


                                // We can assign unique ids to trees and pass those ids here to some other
                                // activity to fetch data from fire base of that particular tree and display
                                //details for a single tree.

                                //Intent intent = new Intent(ViewTreeOnMapActivity.this,SomeOtherActivity.class);
                                //intent.putExtra("title",markerTitle);
                                //startActivity(intent);


                                return false;
                            }
                        });
                    }
                });
            }
        });
    }
}