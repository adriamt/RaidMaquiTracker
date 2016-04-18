package com.amt.raidmaquitracker.httpTask;


/*
 * Handler class per a cridar l'Async Task de connexió HTTP
*/

public abstract class HttpHandler {

    /*
     * Funció abstracte que es cridat quan l'Async Task acaba
     * @param result String que conté la resposta de l'Async Task
     */
    public abstract void onResponse(String result);

    /*
     * Funció pública de la classe HttpHandler que passa els paràmetres
     * necessaris per a fer login a través d'un Async Task
     * @param user String per indicar l'usuari amb el que es vol fer login
     * @param token String que indica el token per a poder fer login amb l'usuari indicat.
     */
    public void login(String user, String token){
        new AsyncHttpTask(this).execute("Login",user,token);
    }

    /*
    *  Funció pública de la classe HttpHandler que passa els paràmetres
    *  necessaris per a crear sessió a través d'un Async Task
    *  @param user_id String per indicar l'id d'usuari amb el que es vol crear la sessió
    */
    public void createSession(String user_id){
        new AsyncHttpTask(this).execute("CreateSession", user_id);
    }

    /*
    * Funció pública de la classe HttpHandler que passa els paràmetres
    * necessaris per a enviar la localització a través d'un Async Task
    * @param latitude String per indicar la latitud de la posició a enviar
    * @param longitude String per indicar la longitud de la posició a enviar
    * @param session_id String que indica l'id de sessió a la qual enviar la localització
    * @param batery String per indicar la bateria del dispositiu que envia la localització
    */
    public void sendLocation(String latitude, String longitude, String session_id, String battery){
        new AsyncHttpTask(this).execute("SendLocation", latitude, longitude, session_id, battery);
    }

}