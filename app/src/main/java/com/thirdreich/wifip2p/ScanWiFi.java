package com.thirdreich.wifip2p;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.VideoView;

import java.util.List;


public class ScanWiFi extends ActionBarActivity {
    private final static String TAG = "ScanWifi";

    WifiManager mainWifiObj;
    WifiScanReceiver wifiReciever;
    ListView list;
    String wifis[];

    private static ProgressDialog progressDialog;

    String videourl="https://archive.org/download/ksnn_compilation_master_the_internet/ksnn_compilation_master_the_internet_512kb.mp4";
    VideoView videoView ;
Boolean c;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_wi_fi);
        list = (ListView) findViewById(R.id.listView1);


        mainWifiObj = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        c=mainWifiObj.isWifiEnabled();
        switchwifi(true,c);

        wifiReciever = new WifiScanReceiver();
        mainWifiObj.startScan();
        videoView = (VideoView) findViewById(R.id.videoView);


        progressDialog = ProgressDialog.show(ScanWiFi.this, "", "Buffering video...", true);
        progressDialog.setCancelable(true);


        PlayVideo();

        Log.i(TAG, "Oncreate");
    }


    private void PlayVideo()
    {
        try
        {
            getWindow().setFormat(PixelFormat.TRANSLUCENT);
            MediaController mediaController = new MediaController(ScanWiFi.this);
            mediaController.setAnchorView(videoView);

            Uri video = Uri.parse(videourl);
            videoView.setMediaController(mediaController);
            videoView.setVideoURI(video);
            videoView.requestFocus();
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
            {

                public void onPrepared(MediaPlayer mp)
                {
                    progressDialog.dismiss();
                    videoView.start();
                }
            });


        }
        catch(Exception e)
        {
            progressDialog.dismiss();
            System.out.println("Video Play Error :"+e.toString());
            finish();
        }

    }



    private void switchwifi(Boolean b,Boolean c) {
        Log.i(TAG, "switchwifi");

        if (!c) {
            this.mainWifiObj.setWifiEnabled(b);

        }
    }

    protected void onPause() {
        Log.i(TAG, "onPause");


        unregisterReceiver(wifiReciever);


        switchwifi(false,c);

        super.onPause();

    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "OnDestroy");

        switchwifi(false,c);
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "onStop");

        switchwifi(false,c);
        super.onStop();
    }

    protected void onResume() {
        Log.i(TAG, "onResume");


        switchwifi(true,c);
        registerReceiver(wifiReciever, new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        super.onResume();
    }

    class WifiScanReceiver extends BroadcastReceiver {
        @SuppressLint("UseValueOf")
        public void onReceive(Context c, Intent intent) {
            List<ScanResult> wifiScanList = mainWifiObj.getScanResults();
            wifis = new String[wifiScanList.size()];
            for (int i = 0; i < wifiScanList.size(); i++) {
                wifis[i] = ((wifiScanList.get(i)).toString());
            }

            list.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
                    android.R.layout.simple_list_item_1, wifis));
        }
    }

}
