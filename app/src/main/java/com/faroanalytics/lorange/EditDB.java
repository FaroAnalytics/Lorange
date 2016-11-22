package com.faroanalytics.lorange;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

public class EditDB extends AsyncTask<String, String, Void> {
    int userID;
    Context context;
    Activity activity;

    public EditDB (Context context, int userID) {
        this.userID = userID;
        this.context = context;
        this.activity = (Activity)context;
    }

    @Override
    protected Void doInBackground(String... params) {
        EditRequest editRequest = new EditRequest(params[0], params[1], params[2], params[3], params[4], params[5],
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {
                                publishProgress("yes");
                            } else {publishProgress("no");}
                        } catch (JSONException e) {e.printStackTrace();}
                    }
                });
        RequestQueue queue = Volley.newRequestQueue(activity);
        queue.add(editRequest);
        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        if (values[0].equals("yes")) {
            Toast.makeText(activity, "Profile successfully edited", Toast.LENGTH_SHORT).show();
        } else if(values[0].equals("no")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage("Edit failed").setNegativeButton("Retry", null).create().show();
        }
    }
}
