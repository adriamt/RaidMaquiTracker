package com.amt.raidmaquitracker.httpTask;

import android.os.AsyncTask;


public class AsyncHttpTask extends AsyncTask<String, Void, String>{

    private HttpHandler httpHandler;

    public AsyncHttpTask(HttpHandler httpHandler){
        this.httpHandler = httpHandler;
    }

    // Operation , XML
    @Override
    protected String doInBackground(String... arg0) {
        String resposta = "";

        switch (arg0[0]){
            case "Login":
                resposta = new AsyncLogin().Login(arg0[1],arg0[2]);
                return resposta;
            case "CreateSession":
                resposta = new AsyncCreateSession().CreateSession(arg0[1]);
                return resposta;
            case "SendLocation":
                resposta = new AsyncSendLocation().SendLocation(arg0[1],arg0[2],arg0[3],arg0[4]);
                return resposta;
            default:
                return resposta;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        httpHandler.onResponse(result);
    }
}