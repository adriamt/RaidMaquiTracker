package com.amt.raidmaquitracker;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amt.raidmaquitracker.httpTask.HttpHandler;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Button btnLogin;
    Button btnCreateSession;
    EditText etUserMail;

    Button mStartUpdatesButton;
    Button mStopUpdatesButton;

    String userMail = "";
    String user_id = "";
    String session_id = "";

    public static final String PREFS_NAME = "GPS_PREFS";

    private static final String INTERVAL_MILLIS = "60000";

    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnCreateSession = (Button) findViewById(R.id.btnCreateSession);
        etUserMail = (EditText)findViewById(R.id.etUserMail);
        mStartUpdatesButton = (Button) findViewById(R.id.start_updates_button);
        mStopUpdatesButton = (Button) findViewById(R.id.stop_updates_button);

        sharedPref = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        String user_mail = sharedPref.getString("user_mail", "NULL");
        if(!user_mail.equals("NULL")){
            etUserMail.setText(user_mail);
        }

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("interval_millis", INTERVAL_MILLIS);
        editor.apply();

        final LogWriter lw = new LogWriter();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userMail = etUserMail.getText().toString();
                btnCreateSession.setEnabled(true);

                if (!userMail.equals("")) {
                    new HttpHandler() {
                        @Override
                        public void onResponse(String result) {
                            user_id = result;
                            String temp ;
                            Calendar c = Calendar.getInstance();
                            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                            String formattedDate = df.format(c.getTime());
                            if (!result.equals("")) {
                                temp = "Login OK!";
                                lw.writeToFile("[" + formattedDate + "] " + "Login OK. Session ID: " + session_id + "\\r\\n");
                            } else {
                                temp = "Login Error!";
                                lw.writeToFile("[" + formattedDate + "] " + "Login Error.");
                            }
                            DialogFragment back_dialog = new GeneralDialogFragment();
                            Bundle args = new Bundle();
                            args.putString("msg", temp);
                            back_dialog.setArguments(args);
                            back_dialog.show(getFragmentManager(), "Info msg");

                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("user_mail", userMail);
                            editor.apply();


                        }
                    }.login(userMail,"test");
                }else{
                    DialogFragment back_dialog = new GeneralDialogFragment();
                    Bundle args = new Bundle();
                    args.putString("msg", getResources().getString(R.string.userMissingMsg));
                    back_dialog.setArguments(args);
                    back_dialog.show(getFragmentManager(), "Info msg");
                }
            }
        });




        btnCreateSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                new HttpHandler() {
                    @Override
                    public void onResponse(String result) {
                        session_id = result;
                        String temp ;
                        Calendar c = Calendar.getInstance();
                        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                        String formattedDate = df.format(c.getTime());
                        if (!result.equals("")){
                            temp = getResources().getString(R.string.create_session_ok_msg);
                            mStartUpdatesButton.setEnabled(true);
                            lw.writeToFile("[" + formattedDate + "] " + "CreateSession OK. Session ID: " + session_id);
                        }else{
                            temp = getResources().getString(R.string.create_session_no_ok_msg);
                            lw.writeToFile("[" + formattedDate + "] " + "CreateSession Error.");
                        }
                        DialogFragment back_dialog = new GeneralDialogFragment();
                        Bundle args = new Bundle();
                        args.putString("msg", temp);
                        back_dialog.setArguments(args);
                        back_dialog.show(getFragmentManager(), "Info msg");

                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("session_id", session_id);
                        editor.apply();

                    }
                }.createSession(user_id);
            }
        });

        mStartUpdatesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startUpdates(v);
            }
        });

        mStopUpdatesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopUpdates(v);
            }
        });
    }

    public void startUpdates(View v){
        Log.e("StartSvc", "Button Click ");
        if(canGetLocation()) {
            Intent i = new Intent(v.getContext(), BackgroundLocationService.class);
            i.putExtra("foo", "bar");
            mStartUpdatesButton.setEnabled(false);
            mStopUpdatesButton.setEnabled(true);
            Toast.makeText(getBaseContext(), getResources().getString(R.string.service_started_msg), Toast.LENGTH_SHORT).show();
            ComponentName service = v.getContext().startService(i);
            if (null == service) {
                // something really wrong here
                mStartUpdatesButton.setEnabled(true);
                mStopUpdatesButton.setEnabled(false);
                Log.e("StartSvc", "Could not start service ");
            }
        }else{
            showSettingsAlert();
        }
    }

    public void stopUpdates(View v){
        Log.e("StopSvc", "Button Click ");
        Intent i = new Intent(v.getContext(), BackgroundLocationService.class);
        i.putExtra("foo", "bar");
        mStartUpdatesButton.setEnabled(true);
        mStopUpdatesButton.setEnabled(false);
        Toast.makeText(getBaseContext(),getResources().getString(R.string.service_stopped_msg),Toast.LENGTH_SHORT).show();
        v.getContext().stopService(i);
    }

    public boolean canGetLocation(){
        LocationManager locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     * */
    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle(getResources().getString(R.string.gps_settings_msg_tittle));

        // Setting Dialog Message
        alertDialog.setMessage(getResources().getString(R.string.gps_settings_msg));

        // On pressing Settings button
        alertDialog.setPositiveButton(getResources().getString(R.string.gps_settings_btn_settings), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton(getResources().getString(R.string.gps_settings_btn_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }
}
