package to.msn.wings.restaurantsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.HandlerCompat;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.Executors;

public class KitchenActivity extends AppCompatActivity {
    //開発環境のIP　Address
    String IPAddress = "10.1.1.34";
    //サービスのインテント
    Intent sv;
    //受注食品リストを表示
    public ListView lvOrderToCook;
    //受注食品リスト
    public final ArrayList<ListItem> orderToCookList = new ArrayList<>();
    //受注食品リスト用アダプター
    public KitchenListAdapter adapterOrderToCook;
    //スクロール停止フラグ
    public boolean stopScroll = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kitchen);

        lvOrderToCook = findViewById(R.id.lvKitchin);

        //アクションバーに「戻るボタン」を追加
        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);

        //レシーバーを登録
        SimpleReceiver receiver = new SimpleReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(SimpleService.ACTION);
        registerReceiver(receiver,filter);
        //サービスを開始
        sv = new Intent(this,SimpleService.class);
        startService(sv);

        //受注食品リストを表示
        adapterOrderToCook = new KitchenListAdapter(this,orderToCookList,R.layout.list_kitchen);
        lvOrderToCook.setAdapter(adapterOrderToCook);

        //受注食品リストを選択したとき
        lvOrderToCook.setOnItemClickListener((av,view,position,id)->{
            ListItem sendSeatitem = orderToCookList.get(position);
            StringBuilder result = new StringBuilder();
            //od_stateを2（調理済）へ変更
            Executors.newSingleThreadExecutor().execute(()->{
                try{
                    URL url = new URL("http://" + IPAddress + "/update_seat_order_JSON.php");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Content-Type","text/plain; charset=utf-8");
                    con.setDoOutput(true);
                    OutputStream os = con.getOutputStream();
                    PrintStream ps = new PrintStream(os);
                    String msg = "";
                    try{
                        JSONArray ary = new JSONArray();
                        JSONObject jobj = new JSONObject();
                        jobj.put("od_id",sendSeatitem.getOd_id());
                        jobj.put("s_id",sendSeatitem.getS_id());
                        jobj.put("od_state",2);
                        ary.put(jobj);
                        msg = ary.toString();
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    ps.print(msg);
                    ps.close();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                            con.getInputStream(), StandardCharsets.UTF_8));
                    String line;
                    while ((line = reader.readLine()) != null){
                        result.append(line);
                    }
                    HandlerCompat.createAsync(getMainLooper()).post(() ->{
                    });
                }catch(IOException e){
                    e.printStackTrace();
                }
            });
        });

        //スクロールの停止及び開始
        Button btnscl = findViewById(R.id.btnscl);
        btnscl.setOnClickListener(v -> {
            if(stopScroll){
                stopScroll = false;
                btnscl.setText(R.string.sclStop);
            }else{
                stopScroll = true;
                btnscl.setText(R.string.sclStart);
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        stopService(sv);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(sv);
    }

    //戻るボタンでアクティビティを閉じる
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return true;
    }
}