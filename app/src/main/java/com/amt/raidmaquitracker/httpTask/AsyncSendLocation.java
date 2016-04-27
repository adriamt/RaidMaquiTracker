package com.amt.raidmaquitracker.httpTask;

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
import java.util.Locale;
import java.util.zip.GZIPInputStream;

public class AsyncSendLocation {
    InputStream inputStream;

    private static final Object GZIP_CONTENT_TYPE = "gzip";

    public String SendLocation(String latitude, String longitude, String session_id,String battery){

        String resposta = "";
        String codi_resposta ;

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String formattedDate = df.format(c.getTime());
        LogWriter lw = new LogWriter();

        try {
            String postData = "{" +
                    "\"lat\":" + latitude + "," +
                    "\"lon\":" + longitude + "," +
                    "\"session_id\":" + session_id + "," +
                    "\"battery\":" + battery +
                    "}";

            URL myURL = new URL("http://www.raidmaqui.com/live/api/session/log");
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
                        System.out.println(response);
                    }

                    JSONObject jo = new JSONObject((String.valueOf(sb)));
                    JSONObject data = jo.getJSONObject("data");
                    resposta = (data.getString("interval"));
                } catch (IOException e) {
                    lw.writeToFile("AsyncSendLocation:[" + formattedDate + "] " + e.getMessage());
                    lw.writeToFile("    Data Sent: " + postData);
                    e.printStackTrace();
                }
            }else {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder sb = new StringBuilder();
                String response;
                while((response = reader.readLine()) != null) {
                    sb.append(response);
                }
                lw.writeToFile("["+formattedDate+"] AsyncSednLocation: "+"Received: " + (String.valueOf(sb)));
                lw.writeToFile("    Data Sent: " + postData);
                //resposta = (String.valueOf(sb));
                resposta = "ERROR";
            }
            return resposta;
        } catch (Exception e) {
            lw.writeToFile("[" + formattedDate + "] AsyncSendLocation: " + e.getMessage());
            return "ERROR" ;
        }
    }

}
