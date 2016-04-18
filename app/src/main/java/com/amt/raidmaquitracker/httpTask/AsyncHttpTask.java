package com.amt.raidmaquitracker.httpTask;

import android.os.AsyncTask;

/*
 * Classe per a fer les conexions http asíncrones
 * Accepta com a paràmetres diferents String i retorna un string
 */

public class AsyncHttpTask extends AsyncTask<String, Void, String>{

    private HttpHandler httpHandler;

    /*
     * Funció de handler per a l'Async Task
     * @param httpHandler Handler del tipus HttpHandler
     */
    public AsyncHttpTask(HttpHandler httpHandler){
        this.httpHandler = httpHandler;
    }

    /*
     * Funció que s'executa en background de l'Async Task. Es fa servir per cridar
     * les diferents accions de connexió.
     * @params com a paràmetres accepta tants Strings com es vulgui.
     */
    @Override
    protected String doInBackground(String... arg0) {
        String resposta = "";

        switch (arg0[0]){
            case "Login":
                // Accions a executar quan es vol fer login
                // arg0[1] = user arg0[2] = token
                resposta = new AsyncLogin().Login(arg0[1],arg0[2]);
                return resposta;
            case "CreateSession":
                // Accions a executar quan es vol crear sessió
                // arg0[1] = user_id
                resposta = new AsyncCreateSession().CreateSession(arg0[1]);
                return resposta;
            case "SendLocation":
                // Accions a executar quan es vol enviar localització
                // arg0[1] = latitude arg0[2] = longitude arg0[3] = session_id arg0[4] = battery
                resposta = new AsyncSendLocation().SendLocation(arg0[1],arg0[2],arg0[3],arg0[4]);
                return resposta;
            default:
                return resposta;
        }
    }

    /*
     * Funció de la classe Async task que s'executa quan la tasca en background finalitza.
     * @param result és el String que es retorna en el doInBackground
     */
    @Override
    protected void onPostExecute(String result) {
        httpHandler.onResponse(result);
    }
}