package com.amt.raidmaquitracker;


import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class LogWriter {

    public void writeToFile(String data) {
        File storage = new File(Environment.getExternalStorageDirectory(), "TrackerTest");
        if (! storage.exists()){
            if (! storage.mkdirs()){
                Log.d("TrackerTest", "Failed to create directory");
            }
        }

        try {
            FileWriter fileW = new FileWriter(Environment.getExternalStorageDirectory().getPath() + "/TrackerTest/Log.txt",true);
            fileW.append(data);
            fileW.append("\r\n");
            fileW.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

}
