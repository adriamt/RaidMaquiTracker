package com.amt.raidmaquitracker.httpTask;

import android.util.Log;

import com.amt.raidmaquitracker.LogWriter;

import org.json.JSONArray;
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

public class AsyncLogin {

    InputStream inputStream;

    private static final Object GZIP_CONTENT_TYPE = "gzip";

    public String Login(String user, String token){

        String resposta = "";
        String codi_resposta;

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String formattedDate = df.format(c.getTime());
        LogWriter lw = new LogWriter();
        String postData ;

        try {
            postData = "{" +
                    "\"mail\":\""+user+"\"," +
                    "\"token\":\""+token+"\"" +
                    "}";

            URL myURL = new URL("http://www.raidmaqui.com/live/api/account/login");
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
                    JSONArray data = jo.getJSONArray("data");
                    JSONObject data1 = new JSONObject(data.getString(0));
                    resposta = (data1.getString("id"));
                } catch (IOException e) {
                    lw.writeToFile("AsyncLogin: [" + formattedDate + "] " + "Exception: " + e.getMessage());
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
                lw.writeToFile("AsyncLogin: [" + formattedDate + "] " + "Received: " + (String.valueOf(sb)));
                lw.writeToFile("    Data Sent: " + postData);
                resposta = (String.valueOf(sb));
            }
            return resposta;
        } catch (Exception e) {
            lw.writeToFile("AsyncLogin: [" + formattedDate + "] " + e.getLocalizedMessage());
            return resposta ;
        }
    }
}

