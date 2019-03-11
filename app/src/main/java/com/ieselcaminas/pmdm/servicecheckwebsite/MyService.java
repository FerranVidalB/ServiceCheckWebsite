package com.ieselcaminas.pmdm.servicecheckwebsite;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class MyService extends Service {
    public MyService() {
    }
List<TaskCheck> taskChecks=null;
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate(){
        super.onCreate();

        taskChecks=new ArrayList<>();



    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        //taskCheck.onProgressUpdate(intent.getExtras().getString("website"));
        if(taskChecks==null){
            taskChecks= new ArrayList<TaskCheck>();
        }
        TaskCheck t = new TaskCheck();
t.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,intent.getExtras().getString("website"),intent.getExtras().getString("seconds"));
taskChecks.add(t);
        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        for(TaskCheck t :taskChecks) {
            t.cancel(true);
        }
    }

    class TaskCheck extends AsyncTask<String, String, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            int seconds = Integer.parseInt(strings[1]);
            String website = strings[0];
            while (!isCancelled()) {
                if (!openHttpConnection(website)) {
                    publishProgress(website);
                } else {
                    publishProgress(null);
                }
                try {
                    Thread.sleep(seconds * 1000);
                } catch (InterruptedException ex) {
                    //
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String ... website) {
            if (website==null) { // everything ok
                Log.d("Check", "connected Ok");
            } else {
                Toast.makeText(getApplicationContext(), "Not connected to " + website[0],                    Toast.LENGTH_LONG).show();
            }
        }

        private boolean openHttpConnection(String urlString)  {
            int response;

            try{
                URL url = new URL(urlString);
                URLConnection conn = url.openConnection();
                if (!(conn instanceof HttpURLConnection))
                    throw new IOException("Not an HTTP connection");
                HttpURLConnection httpConn = (HttpURLConnection) conn;
                httpConn.setInstanceFollowRedirects(true);
                httpConn.connect();
                response = httpConn.getResponseCode();
                httpConn.disconnect();
                if (response == HttpURLConnection.HTTP_OK) {
                    return true;
                }
            }
            catch (Exception ex) {

            }
            return false;
        }
    }
}

