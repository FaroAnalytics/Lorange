package com.faroanalytics.lorange;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import java.util.HashMap;
import java.util.Map;

public class FillProfileRequest extends StringRequest {

    private static final String LOGIN_REQUEST_URL = "http://strwberry.io/db_files/profile.php";
    private Map<String, String> params;

    public FillProfileRequest(int userID, Response.Listener<String> listener) {
        super(Request.Method.POST, LOGIN_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("userID", userID + "");
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
