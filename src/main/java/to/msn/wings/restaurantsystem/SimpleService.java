package to.msn.wings.restaurantsystem;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.Provider;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SimpleService extends Service {
    String IPAddress = "10.1.1.34"; //開発環境のIP　Address
    private ScheduledExecutorService schedule;
    public static final String ACTION = "SimpleService Action";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    //3秒ごとに受注食品リストを更新する
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        schedule = Executors.newSingleThreadScheduledExecutor();
        schedule.scheduleAtFixedRate(()->{
            StringBuilder result = new StringBuilder();
            try{
                URL url = new URL("http://" + IPAddress + "/list_JSON_kitchin.php");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        con.getInputStream(), StandardCharsets.UTF_8
                ));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);

                }

            }catch (IOException e){
                e.printStackTrace();
            }

            Intent i = new Intent(ACTION);
            i.putExtra("message",result.toString());
            sendBroadcast(i);
        },0,3000, TimeUnit.MILLISECONDS);
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        schedule.shutdown();
    }
}
