package com.faroanalytics.lorange;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ClassListActivity extends AppCompatActivity {
    ArrayList<Alumni> classPositionList;
    ListView listView;
    ClassListAdapter classListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_list);

        Button goMap = (Button)findViewById(R.id.bMap);
        goMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClassListActivity.this.startActivity(new Intent(ClassListActivity.this, ClassMapActivity.class));
            }
        });

        listView = (ListView)ClassListActivity.this.findViewById(R.id.lvClass);
        classListAdapter = new ClassListAdapter(ClassListActivity.this, R.layout.class_mate_layout);

        FillList fillList = new FillList();
        fillList.execute();

    }

    public class FillList extends AsyncTask<Void, Alumni, String> {

        @Override
        protected String doInBackground(Void... voids) {
            ClassListRequest classListRequest = new ClassListRequest(
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                ArrayList<Alumni> classPositionList = new ArrayList<>();
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
            RequestQueue queue = Volley.newRequestQueue(ClassListActivity.this);
            queue.add(classListRequest);
            return "all good to go";
        }

        @Override
        protected void onProgressUpdate(Alumni... values) {classListAdapter.add(values[0]);}

        @Override
        protected void onPostExecute(String result) {
            if(result.equals("all good to go"))
                //Toast.makeText(ClassListActivity.this, "Class listed", Toast.LENGTH_SHORT).show();
            listView.setAdapter(classListAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long l) {

                    Intent intent = new Intent(ClassListActivity.this, ProfileActivity.class);
                    intent.putExtra("userID", ((Alumni)parent.getItemAtPosition(position)).getUserID());
                    ClassListActivity.this.startActivity(intent);
                }
            });
        }
    }
}
