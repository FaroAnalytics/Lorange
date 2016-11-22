package com.faroanalytics.lorange;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private EditText emailBox, phoneBox, jobBox, residenceBox, passwordBox, confirmPasswordBox;
    private int userID;
    private int CALL_CAMERA_REQUEST_CODE = 991;
    private int CROP_PICTURE_REQUEST_CODE = 992;
    private String encodedString;
    private Uri uri;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        view = this.getCurrentFocus();

        emailBox = (EditText) findViewById(R.id.etEmail);
        phoneBox = (EditText)findViewById(R.id.etPhone);
        jobBox = (EditText) findViewById(R.id.etJob);
        residenceBox = (EditText) findViewById(R.id.etResidence);
        passwordBox = (EditText) findViewById(R.id.etPassword);
        confirmPasswordBox = (EditText)findViewById(R.id.etPasswordConfirm);

        userID = getIntent().getIntExtra("userID", 0);
        FillFieldsToEdit fillFieldsToEdit = new FillFieldsToEdit();
        fillFieldsToEdit.execute(userID);

        ImageButton position = (ImageButton)findViewById(R.id.bPosition);
        position.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditProfileActivity.this.startActivity(new Intent(EditProfileActivity.this, MapsActivity.class));
            }
        });

        ImageButton edit = (ImageButton) findViewById(R.id.bEdit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailBox.getText().toString();
                String phone = phoneBox.getText().toString();
                String job = jobBox.getText().toString();
                String residence = residenceBox.getText().toString();
                String password = passwordBox.getText().toString();
                String confirmPassword = confirmPasswordBox.getText().toString();

                if (!password.equals(confirmPassword)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
                    builder.setMessage("Your password confirmation doesn't match your password!")
                            .setNegativeButton("Retry", null).create().show();
                } else {
                    EditDB editDB = new EditDB(EditProfileActivity.this, userID);
                    editDB.execute(userID+"", email, phone, job, residence, password);
                }
            }
        });

        Button back = (Button)findViewById(R.id.bGoProfile);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
                intent.putExtra("userID", userID);
                EditProfileActivity.this.startActivity(intent);
            }
        });

        ImageButton udatePicture = (ImageButton)findViewById(R.id.bCamera);
        udatePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TakePicture();
            }
        });

        Switch notification = (Switch)findViewById(R.id.sNotification);

        SharedPreferences sharedPreferences = getSharedPreferences("lorange", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("notifications", false)) {
            notification.setChecked(true);
        } else {notification.setChecked(false);}

        notification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                SharedPreferences sharedPreferences = getSharedPreferences("lorange", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (b) {

                    BirthdayNotifications birthdayNotifications = new BirthdayNotifications(EditProfileActivity.this);
                    birthdayNotifications.execute();

                    editor.putBoolean("notifications", true);
                    editor.apply();

                    Toast.makeText(EditProfileActivity.this,
                            "you will now be notified about birthdays", Toast.LENGTH_SHORT).show();
                } else {

                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancelAll();

                    editor.putBoolean("notifications", false);
                    editor.apply();

                    Toast.makeText(EditProfileActivity.this,
                            "notifications off", Toast.LENGTH_SHORT).show();
                }
            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                             LOADING PROFILE INFO TO LAYOUT                                 //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public class FillFieldsToEdit extends AsyncTask<Integer, String[], Void> {
        String[] profileData = {"0", "1", "2", "3", "4"};

        @Override
        protected Void doInBackground(Integer... params) {
            int userID = params[0];
            FillProfileRequest fillProfileRequest = new FillProfileRequest(userID,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.getBoolean("success")) {
                                    profileData[0] = jsonObject.getString("email");
                                    profileData[1] = jsonObject.getString("phone");
                                    profileData[2] = jsonObject.getString("job");
                                    profileData[3] = jsonObject.getString("residence");
                                    profileData[4] = jsonObject.getString("password");
                                    publishProgress(profileData);
                                }
                            } catch (JSONException e) {e.printStackTrace();}
                        }
                    });
            RequestQueue queue = Volley.newRequestQueue(EditProfileActivity.this);
            queue.add(fillProfileRequest);
            return null;
        }

        @Override
        protected void onProgressUpdate(String[]... values) {
            emailBox.setText(values[0][0]);
            phoneBox.setText(values[0][1]);
            jobBox.setText(values[0][2]);
            residenceBox.setText(values[0][3]);
            passwordBox.setText(values[0][4]);
            confirmPasswordBox.setText(values[0][4]);
        }
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                             CHANGING THE PROFILE PICTURE                                   //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void TakePicture() {
        Intent callCameraIntent = new Intent();
        callCameraIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(callCameraIntent, CALL_CAMERA_REQUEST_CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CALL_CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            uri = data.getData();
            performCrop(uri);
        }
        else if (requestCode == CROP_PICTURE_REQUEST_CODE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap photoCaptured = (Bitmap)extras.get("data");
            EncodeImage encodeImage = new EncodeImage(photoCaptured);
            encodeImage.execute();
        }
    }

    private void performCrop(Uri picUri) {
        try {
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(picUri, "image/*");
            intent.putExtra("crop", "true");
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("outputX", 100);
            intent.putExtra("outputY", 100);
            intent.putExtra("return-data", true);
            startActivityForResult(intent, CROP_PICTURE_REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    private class EncodeImage extends AsyncTask<Void, Void, Void> {
        Bitmap bitmap;

        protected EncodeImage (Bitmap bitmap) {this.bitmap = bitmap;}

        @Override
        protected Void doInBackground(Void... params) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            this.bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

            byte[] array = stream.toByteArray();
            encodedString = Base64.encodeToString(array, 0);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            makeRequest();
        }

        private void makeRequest() {
            RequestQueue requestQueue = Volley.newRequestQueue(EditProfileActivity.this);
            StringRequest request = new StringRequest(Request.Method.POST, "http://faroanalytics.com/uploadImage.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {}
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {}
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> map = new HashMap<>();
                    String timeStamp = new SimpleDateFormat("yyyy_MM_dd_HHmmss").format(new Date());
                    String imageName = "1_" + timeStamp + ".jpg";
                    map.put("userID", userID+"");
                    map.put("encodedString", encodedString);
                    map.put("imageName", imageName);
                    return map;
                }
            };
            requestQueue.add(request);
        }
    }
}