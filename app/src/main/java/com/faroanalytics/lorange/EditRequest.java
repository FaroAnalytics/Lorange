package com.faroanalytics.lorange;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import java.util.HashMap;
import java.util.Map;

public class EditRequest extends StringRequest {

    private static final String REGISTER_REQUEST_URL = "http://faroanalytics.com/edit.php";
    private Map<String, String> params;

    public EditRequest(String userID, String email, String phone, String job, String residence, String password, Response.Listener<String> listener) {
        super(Method.POST, REGISTER_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("userID", userID);
        params.put("email", email);
        params.put("phone", phone);
        params.put("job", job);
        params.put("residence", residence);
        params.put("password", password);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
