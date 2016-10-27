package com.faroanalytics.lorange;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClassMapActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    int userID, positionLat, positionLng;
    ArrayList<Alumni> classPositionList;
    HashMap<Marker, Alumni> markerMate = new HashMap <Marker, Alumni>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        userID = intent.getIntExtra("userID", 0);
        positionLat = intent.getIntExtra("positionLat", 0);
        positionLng = intent.getIntExtra("positionLng", 0);

        final SharedPreferences sharedPreferences = getSharedPreferences("lorange", MODE_PRIVATE);

        Button classList = (Button)findViewById(R.id.bList);
        classList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClassMapActivity.this.startActivity(new Intent(ClassMapActivity.this, ClassListActivity.class));
            }
        });

        ImageButton myProfile = (ImageButton)findViewById(R.id.bMyProfile);
        myProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ClassMapActivity.this, ProfileActivity.class);
                intent.putExtra("userID", sharedPreferences.getInt("userID", 0));
                ClassMapActivity.this.startActivity(intent);
            }
        });

        MarkerListForPosition markerListForPosition = new MarkerListForPosition();
        markerListForPosition.execute(userID, positionLat, positionLng);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent = new Intent(ClassMapActivity.this, ProfileActivity.class);
                intent.putExtra("userID", markerMate.get(marker).getUserID());
                ClassMapActivity.this.startActivity(intent);
            }
        });
    }





    public class MarkerListForPosition extends AsyncTask<Integer, Alumni, String> {
        ArrayList<Alumni> classPositionList;
        ArrayList<Marker> classMarkerList;


        @Override
        protected String doInBackground(Integer... params) {
            int userID = params[0];
            int positionLat = params[1];
            int positionLng = params[2];
            MarkerRequest markerRequest = new MarkerRequest(userID, positionLat, positionLng,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                classPositionList = new ArrayList<>();
                                for (int i=0; i<jsonArray.length(); i++) {
                                    classPositionList.add(new Alumni(
                                            jsonArray.optJSONObject(i).getInt("userID"),
                                            jsonArray.optJSONObject(i).getString("firstName"),
                                            jsonArray.optJSONObject(i).getString("lastName"),
                                            jsonArray.optJSONObject(i).getString("phone"),
                                            jsonArray.optJSONObject(i).getString("email"),
                                            jsonArray.optJSONObject(i).getString("job"),
                                            jsonArray.optJSONObject(i).getString("birthDate"),
                                            jsonArray.optJSONObject(i).getString("residence"),
                                            jsonArray.optJSONObject(i).getString("password"),
                                            jsonArray.optJSONObject(i).getInt("positionLat"),
                                            jsonArray.optJSONObject(i).getInt("positionLng"),
                                            jsonArray.optJSONObject(i).getString("picture")));
                                    publishProgress(classPositionList.get(i));
                                }
                            } catch (JSONException e) {e.printStackTrace();}
                        }
                    });
            RequestQueue queue = Volley.newRequestQueue(ClassMapActivity.this);
            queue.add(markerRequest);
            return "all good to go";
        }

        @Override
        protected void onProgressUpdate(Alumni... values) {
            markerMate.put(
                    mMap.addMarker(new MarkerOptions()
                    .position(values[0].getPosition())
                    .title(values[0].getName() + " >>")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))),
                    values[0]);
        }

        @Override
        protected void onPostExecute(String result){
            if (result.equals("all good to go")) {
                //Toast.makeText(ClassMapActivity.this, "All members located", Toast.LENGTH_SHORT).show();
            }
        }
    }




}
