package com.faroanalytics.lorange;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import java.util.HashMap;
import java.util.Map;


public class MarkerRequest extends StringRequest {

    private static final String LOGIN_REQUEST_URL = "http://strwberry.io/db_files/marker.php";
    private Map<String, String> params;

    public MarkerRequest(int userID, int positionLat, int positionLng, Response.Listener<String> listener) {
        super(Request.Method.POST, LOGIN_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("userID", userID +"");
        params.put("positionLat", positionLat +"");
        params.put("positionLng", positionLng +"");
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }

}
