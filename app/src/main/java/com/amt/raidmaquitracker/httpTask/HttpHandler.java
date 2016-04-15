package com.amt.raidmaquitracker.httpTask;


/*
    Handler class per a cridar l'Async Task de connexió HTTP

 */

public abstract class HttpHandler {

    /*
     * Metode abstracte que es cridat quan l'Async Task acaba
     * @return String - conte la resposta de l'Async Task
     */
    public abstract void onResponse(String result);

    public void login(String user, String token){
        new AsyncHttpTask(this).execute("Login",user,token);
    }

    public void createSession(String user_id){
        new AsyncHttpTask(this).execute("CreateSession", user_id);
    }

    public void sendLocation(String latitude, String longitude, String session_id, String battery){
        new AsyncHttpTask(this).execute("SendLocation", latitude, longitude, session_id, battery);
    }

}