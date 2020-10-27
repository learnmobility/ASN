package com.asn.asna;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class MainActivity_HomeScreenToPlay extends AppCompatActivity {
    EditText txtAPIRequest;
    TextView lblResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtAPIRequest = findViewById(R.id.txtRequestAPI);
        lblResponse = findViewById(R.id.lblResponse);
    }

    public void onClick_GetRequest(View view) {
    // Instantiate the RequestQueue.
            RequestQueue queue = Volley.newRequestQueue(this);
            String url ="https://httpbin.org/get";

    // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Display the first 500 characters of the response string.
                            lblResponse.setText(response.toString());
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    lblResponse.setText("That didn't work!");
                }
            });

    // Add the request to the RequestQueue.
            queue.add(stringRequest);
    }


}