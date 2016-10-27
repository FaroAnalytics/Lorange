package com.faroanalytics.lorange;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.Calendar;
import static android.content.Context.ALARM_SERVICE;

public class BirthdayNotifications extends AsyncTask<Void, Alumni, Void> {

    private AlarmManager alarmManager;
    private ArrayList<PendingIntent> pendingIntent = new ArrayList<>();
    private ArrayList<Intent> intent = new ArrayList<>();
    private ArrayList<Calendar> birthday = new ArrayList<>();
    private Context context;
    private int i = 0;

    public BirthdayNotifications(Context context) {
        this.context = context;
        alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
    }

    @Override
    protected Void doInBackground(Void... voids) {
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
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(classListRequest);
        return null;
    }

    @Override
    protected void onProgressUpdate(Alumni... values) {

        birthday.add(getBirthday(values[0]));

        intent.add(new Intent(context, NotificationReceiver.class));
        intent.get(i).putExtra("name", values[0].getName());
        intent.get(i).putExtra("phone", values[0].getPhone());
        intent.get(i).putExtra("picture", values[0].getPicture());

        pendingIntent.add(PendingIntent.getBroadcast(context, i, intent.get(i),
                PendingIntent.FLAG_UPDATE_CURRENT));

        alarmManager.set(AlarmManager.RTC_WAKEUP, birthday.get(i).getTimeInMillis(), pendingIntent.get(i));

        i++;
    }

    private Calendar getBirthday(Alumni alumni) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, Integer.parseInt(alumni.getBirthDate().substring(5, 7)) - 1);
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(alumni.getBirthDate().substring(8)));

        while (Calendar.getInstance().getTimeInMillis() > calendar.getTimeInMillis()) {
            calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 1);
        }

        return calendar;
    }
}

