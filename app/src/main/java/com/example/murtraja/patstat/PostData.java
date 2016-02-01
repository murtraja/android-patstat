package com.example.murtraja.patstat;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;

public class PostData extends AsyncTask<String, Integer, Long> {

    @Override
    protected Long doInBackground(String... params) {
        postSensorData(params[0], params[1], params[2]);
        return null;
    }

    void postSensorData(String serverURL, String Name, String lightData) {
        System.out.println("now starting...");
        String data = "";
        try {
            data = URLEncoder.encode("name", "UTF-8")
                    + "=" + URLEncoder.encode(Name, "UTF-8");
            data += "&" + URLEncoder.encode("data", "UTF-8") + "="
                    + URLEncoder.encode(lightData, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String text = "";
        BufferedReader reader = null;

        // Send data
        try {

            // Defined URL  where to send data
            Log.d("MMR", "defining url");

            URL url = new URL("http://" + serverURL);

            // Send POST data request
            Log.d("MMR", "just before open connection");
            URLConnection conn = url.openConnection();
            Log.d("MMR", "opened Connection");
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();
            Log.d("MMR", "sent data");

            // Get the server response
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;

            // Read Server Response
            while ((line = reader.readLine()) != null) {
                // Append server response in string
                sb.append(line + "\n");
            }


            text = sb.toString();
            System.out.println(text);
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d("MMR", "exception" + ex.getMessage());
        } finally {
            try {

                reader.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }


    }

}
