package com.amt.raidmaquitracker.httpTask;


import android.util.Log;


import com.amt.raidmaquitracker.LogWriter;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.zip.GZIPInputStream;

public class AsyncCreateSession {
    InputStream inputStream;

    private static final Object GZIP_CONTENT_TYPE = "gzip";

    public String CreateSession(String user_id){

        String resposta = "";
        String codi_resposta = "";

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String formattedDate = df.format(c.getTime());
        LogWriter lw = new LogWriter();

        try {
            String postData = "{" +
                    "\"user_id\":" + user_id +
                    "}";
            //lw.writeToFile("["+formattedDate+"] "+"Sent: " + postData);

            URL myURL = new URL("http://www.raidmaqui.com/live/api/session/create");
            HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
            myURLConnection.setRequestMethod("POST");
            myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
            myURLConnection.setRequestProperty("Content-Type", "application/json");
            myURLConnection.setRequestProperty("Connection","Keep-Alive");
            myURLConnection.setRequestProperty("Accept-Encoding","gzip");
            myURLConnection.setRequestProperty("Content-Length", "" + Integer.toString(postData.getBytes().length));
            myURLConnection.setDoInput(true);
            myURLConnection.setDoOutput(true);
            byte[] outputInBytes = postData.getBytes("UTF-8");
            OutputStream os = myURLConnection.getOutputStream();
            os.write(outputInBytes);
            os.close();

            codi_resposta = (String.valueOf(myURLConnection.getResponseCode()));


            if (GZIP_CONTENT_TYPE.equals(myURLConnection.getContentEncoding())){
                inputStream = new GZIPInputStream(myURLConnection.getInputStream());
            }else{
                inputStream =  myURLConnection.getInputStream();
            }


            if(codi_resposta.equals("200")) {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder sb = new StringBuilder();
                    String response ;
                    while((response = reader.readLine()) != null) {
                        sb.append(response);
                    }
                    //lw.writeToFile("["+formattedDate+"] "+"Received: " + (String.valueOf(sb)));
                    JSONObject jo = new JSONObject((String.valueOf(sb)));
                    JSONObject data = jo.getJSONObject("data");
                    resposta = (data.getString("id"));
                } catch (IOException e) {
                    lw.writeToFile("AsyncCreateSession:[" + formattedDate + "] " + e.getMessage());
                    e.printStackTrace();
                }
            }else {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder sb = new StringBuilder();
                String response;
                while((response = reader.readLine()) != null) {
                    sb.append(response);
                }
                lw.writeToFile("AsyncCreateSession:[" + formattedDate + "] " + "Received: " + (String.valueOf(sb)));
                resposta = (String.valueOf(sb));
            }
            return resposta;
        } catch (Exception e) {
            lw.writeToFile("[" + formattedDate + "] " + e.getMessage());
            Log.d("AsyncCSession: ", e.getLocalizedMessage());
            return resposta ;
        }
    }
}
