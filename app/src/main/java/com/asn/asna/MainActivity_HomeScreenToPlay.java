package com.asn.asna;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.asn.asna.firebasepoc.Upload;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity_HomeScreenToPlay extends AppCompatActivity {
    EditText txtAPIRequest;
    TextView lblResponse;
    MediaPlayer player;
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


    public void onClick_UploadPOC(View pocButtonView){
        Intent i = new Intent(this, Upload.class);
        startActivity(i);
    }


    /// Nikhil POC

    public void onClick_Play(View v) {
        String audioFileName = getExternalCacheDir().getAbsolutePath();
        audioFileName += "/audiorecordtest.3gp";
        try {
            downloadFileFromServer(audioFileName,
                    "https://firebasestorage.googleapis.com/v0/b/talks-6e7be.appspot.com/o/poc%2Faudiorecordtest.3gp?alt=media&token=c52e4054-bc37-4c45-a808-8e3810b2cf6d");
        }catch (MalformedURLException mue) {
            mue.printStackTrace();
        }catch (IOException ioe) {
            ioe.printStackTrace();
        }

        player = new MediaPlayer();
        try {
            player.setDataSource(audioFileName);
            player.prepare();
            player.start();
        } catch (IOException e) {
            Log.i("talks", "prepare() failed");
        }
    }
        /**
         * This function download the large files from the server
         * @param filename
         * @param urlString
         * @throws MalformedURLException
         * @throws IOException
         */
        public static void downloadFileFromServer(String filename, String urlString) throws MalformedURLException, IOException
        {
            final String fileName = filename;
            final String stringURL = urlString;
            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    try  {
                        BufferedInputStream in = null;
                        FileOutputStream fout = null;
                        try
                        {
                            URL url = new URL(stringURL);

                            in = new BufferedInputStream(url.openStream());
                            fout = new FileOutputStream(fileName);

                            byte data[] = new byte[1024];
                            int count;
                            while ((count = in.read(data, 0, 1024)) != -1)
                            {
                                fout.write(data, 0, count);
                                System.out.println(count);
                            }
                        }
                        finally
                        {
                            if (in != null)
                                in.close();
                            if (fout != null)
                                fout.close();
                        }
                        System.out.println("Done");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            thread.start();

        }
    }