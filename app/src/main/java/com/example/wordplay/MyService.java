package com.example.wordplay;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.widget.Toast;

public class MyService extends Service {
    private static String TAG = "";
    MediaPlayer player;

    public static String getTAG(){
        return TAG;
    }

    public void setTAG(String tag){
        this.TAG = tag;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
//        Toast.makeText(this, "My Service Created", Toast.LENGTH_LONG).show();

        player = MediaPlayer.create(this, R.raw.bg);
        player.setLooping(true); // зацикливаем
        setTAG("started");
    }

    @Override
    public void onDestroy() {
//        Toast.makeText(this, "My Service Stopped", Toast.LENGTH_LONG).show();
        player.stop();
        setTAG("interrupted");
    }

    @Override
    public void onStart(Intent intent, int startid) {
//        Toast.makeText(this, "My Service Started", Toast.LENGTH_LONG).show();
        player.start();
        TAG = "started";
    }
}
