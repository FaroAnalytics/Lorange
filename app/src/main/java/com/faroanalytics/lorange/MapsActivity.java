package com.faroanalytics.lorange;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    SharedPreferences sharedPreferences;
    double realPositionLat = 46.519962;
    double realPositionLng = 6.633597;
    private EditText address;
    private boolean secure = false;
    private int ZOOM = 13;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        sharedPreferences = getSharedPreferences("lorange", MODE_PRIVATE);
        if (!sharedPreferences.getBoolean("loggedIn", false))
            MapsActivity.this.startActivity(new Intent(MapsActivity.this, LoginActivity.class));

        Button logout = (Button) findViewById(R.id.bLogout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("loggedIn", false);
                editor.apply();
                MapsActivity.this.startActivity(new Intent(MapsActivity.this, LoginActivity.class));
            }
        });

        address = (EditText) findViewById(R.id.tfAddress);
        ImageButton search = (ImageButton) findViewById(R.id.bSearch);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSearch(view);
            }
        });

        ImageButton ImHere = (ImageButton) findViewById(R.id.bIAmHere);
        ImHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapsActivity.this, ClassMapActivity.class);
                int storePositionLat = (int)(realPositionLat*1000000);
                int storePositionLng = (int)(realPositionLng*1000000);
                int userID = sharedPreferences.getInt("userID",-2);
                intent.putExtra("userID", userID);
                intent.putExtra("positionLat", storePositionLat);
                intent.putExtra("positionLng",storePositionLng);
                MapsActivity.this.startActivity(intent);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        locationEnabled();

        mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                secure = true;
            }
        });

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                if (secure)
                    updateTextField(mMap.getCameraPosition().target);
            }
        });

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
    }

    // goes to the location passed
    public void goToLocation(LatLng ll) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, ZOOM));
    }

    // goes to the address written on the textfield
    public void onSearch(View view) {
        EditText newAddress = (EditText) findViewById(R.id.tfAddress);
        String location = newAddress.getText().toString();
        List<Address> addressList = null;
        if (!location.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Address address = null;
            if (addressList != null) {
                address = addressList.get(0);
                LatLng addressCoord = new LatLng(address.getLatitude(), address.getLongitude());
                goToLocation(addressCoord);
            }
        }
    }

    // sets the text field to the address passed
    public void updateTextField(LatLng latLng) {
        // sets the text above to the name of your current location
        Geocoder geocoder = new Geocoder(this);
        realPositionLat = latLng.latitude;
        realPositionLng = latLng.longitude;
        List<Address> estimate = null;
        try {
            estimate = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String remotePlace = "gone fishing...";
        try {
            address.setText(estimate.get(0).getLocality());
        } catch(Exception e) {
            address.setText(remotePlace);
        }
    }

    // enables the current location
    public void locationEnabled() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.
                    permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        mMap.setMyLocationEnabled(true);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                             ENABLING SPONTANEOUS LOCALIZATION                              //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(3600 * 1000);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.
                    permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location == null) {
            Toast.makeText(MapsActivity.this, "Can't get current location", Toast.LENGTH_LONG).show();
        } else {
            LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
            realPositionLat = position.latitude;
            realPositionLng = position.longitude;
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(position, ZOOM);
            mMap.animateCamera(update);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(MapsActivity.this, "Connection problems...", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(MapsActivity.this, "Connection failed...", Toast.LENGTH_LONG).show();
    }

}
