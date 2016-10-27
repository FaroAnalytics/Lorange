package com.faroanalytics.lorange;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity {
    ImageView imageBox;
    TextView birthdayBox, emailBox, phoneBox, residenceBox, workBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        imageBox = (ImageView)findViewById(R.id.ivProfilePicture);
        birthdayBox = (TextView)findViewById(R.id.tvBirthday);
        emailBox = (TextView)findViewById(R.id.tvEmail);
        phoneBox = (TextView)findViewById(R.id.tvPhone);
        residenceBox = (TextView)findViewById(R.id.tvHome);
        workBox = (TextView)findViewById(R.id.tvWork);
        ImageButton edit = (ImageButton)findViewById(R.id.bEdit);
        Button map = (Button)findViewById(R.id.bGoMap);
        ImageButton whatsapp = (ImageButton)findViewById(R.id.bWhatsapp);

        final SharedPreferences sharedPreferences = getSharedPreferences("lorange", MODE_PRIVATE);

        imageBox.setImageResource(R.drawable.profile_pic);

        FillProfile fillProfile = new FillProfile();
        final int userID = getIntent().getIntExtra("userID", 0);
        fillProfile.execute(userID);

        if (sharedPreferences.getInt("userID", -3) == userID) {
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                    intent.putExtra("userID", userID);
                    ProfileActivity.this.startActivity(intent);
                }
            });
        } else {edit.setVisibility(View.GONE);}

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProfileActivity.this.startActivity(new Intent(ProfileActivity.this, ClassMapActivity.class));
            }
        });

        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWhatsappContact(phoneBox.getText().toString());
            }
        });
    }

    void openWhatsappContact(String number) {
        Uri uri = Uri.parse("smsto:" + number);
        Intent i = new Intent(Intent.ACTION_SENDTO, uri);
        i.setPackage("com.whatsapp");
        startActivity(Intent.createChooser(i, ""));
    }

    public class FillProfile extends AsyncTask<Integer, String[], String> {
        String[] profileData = {"0", "1", "2", "3", "4", "5"};

        @Override
        protected String doInBackground(Integer... params) {
            int userID = params[0];
            FillProfileRequest fillProfileRequest = new FillProfileRequest(userID,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.getBoolean("success")) {
                                    profileData[0] = jsonObject.getString("birthDate");
                                    profileData[1] = jsonObject.getString("email");
                                    profileData[2] = jsonObject.getString("phone");
                                    profileData[3] = jsonObject.getString("residence");
                                    profileData[4] = jsonObject.getString("job");
                                    profileData[5] = jsonObject.getString("picture");
                                    publishProgress(profileData);
                                }
                            } catch (JSONException e) {e.printStackTrace();}
                        }
                    });
            RequestQueue queue = Volley.newRequestQueue(ProfileActivity.this);
            queue.add(fillProfileRequest);
            return "all good to go";
        }

        @Override
        protected void onProgressUpdate(String[]... values) {
            birthdayBox.setText(values[0][0]);
            emailBox.setText(values[0][1]);
            phoneBox.setText(values[0][2]);
            residenceBox.setText(values[0][3]);
            workBox.setText(values[0][4]);
            DownloadImage downloadImage = new DownloadImage(imageBox);
            downloadImage.execute(values[0][5]);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("all good to go")) {
                // Toast.makeText(ProfileActivity.this, "profile loaded", Toast.LENGTH_SHORT).show();
            }
        }
    }
}