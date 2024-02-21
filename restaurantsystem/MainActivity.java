package to.msn.wings.restaurantsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.HandlerCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    ListItem item = new ListItem(); //選択された情報
    ListItem upItem;    //変更情報を格納する
    ArrayList<ListItem> listItems = new ArrayList<>(); //注文リストに表示するリスト
    ArrayList<String> listF = new ArrayList<>();  //スピナーに表示するメニュー情報
    ArrayList<String> listS = new ArrayList<>();  //スピナーに表示する席情報
    ArrayList<String> listQ = new ArrayList<>();  //スピナーに表示する個数情報
    ArrayList<FoodItem> foods = new ArrayList<>(); //メニュー情報のrawデータ
    ArrayList<SeatItem> seats = new ArrayList<>(); //席情報のrawデータ
    ArrayList<ListItem> searchList = new ArrayList<>(); //注文済みリストに表示するリスト
    int selectedSeats = 0;  //席情報が選択されたかどうかを判定
    int menuIdx = 0; //注文リストの選択された位置を保持
    boolean menuSelected = false; //注文リストが選択されている状態か否かを判別
    public View v ; //注文リストのViewの内容を保持
    boolean check = true;   //会計可能かどうかを判定
    ListView lvList;    //注文リストの表示
    ListView okList;    //注文済リストの表示
    Spinner spnF;   //メニュー情報のスピナー
    Spinner spnS;   //席情報のスピナー
    Spinner spnQ;   //個数情報のスピナー
    EditText etMemo;    //備考欄
    Button btnMenu; //メニュー追加/変更ボタン
    Button btnSend; //注文送信ボタン
    Button btnSeat; //空席照会ボタン
    Button btnSearch;   //注文照会ボタン
    Button btnCheck;    //会計ボタン





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvList = findViewById(R.id.list);
        okList = findViewById(R.id.OKlist);

        spnF = findViewById(R.id.spnFoods);
        spnS = findViewById(R.id.spnSeats);
        spnQ = findViewById(R.id.spnQuo);
        etMemo = findViewById(R.id.etMemo);

        btnMenu= findViewById(R.id.btnMenu);
        btnSend = findViewById(R.id.btnSend);
        btnSeat = findViewById(R.id.btnSeat);
        btnSearch = findViewById(R.id.btnSearch);
        btnCheck = findViewById(R.id.btnCheck);

        //席情報・メニュー情報・個数情報の作成
        downloadSeats();
        downloadFoods();
        createSpnQuo();
        setSpnQuo();
        //スピナーが選択された際の処理
        spnFoodsSelected();
        spnSeatsSelected();
        spnQuoSelected();


        //追加ボタンが押されたとき
        btnSend.setOnClickListener(v -> {
            if(btnSend.getText().toString().equals("追加") ){

                //席情報が選択されていない場合トースト表示
                if(selectedSeats == 0){
                    Toast.makeText(this,R.string.noSeat,Toast.LENGTH_SHORT).show();
                    //個数情報が選択されていない場合トースト表示（席情報も選択されていない場合は席情報トーストが優先）
                }else if(item.getOd_quantity() == 0){
                    Toast.makeText(this,R.string.zero,Toast.LENGTH_SHORT).show();
                    //上記以外は注文リストに表示
                }else if(item.getOd_quantity() != 0 && selectedSeats != 0 ){
                        item.setOd_memo(etMemo.getText().toString());
                        item.setTime(getNowDate());
                        item.setOd_state(1);
                        listItems.add(item);
                        createOrderList();
                }
                //変更ボタンが押されたとき
            }else if(btnSend.getText().toString().equals("変更")){
                //選択された注文リストから席情報を取得
                int idx = spnS.getSelectedItemPosition();
                upItem.setOd_memo(etMemo.getText().toString());
                upItem.setTime(getNowDate());
                upItem.setS_id(seats.get(idx-1).getS_id());

                //フード情報を取得
                idx = spnF.getSelectedItemPosition();
                upItem.setF_id(foods.get(idx).getF_id());
                upItem.setF_name(foods.get(idx).getF_name());
                upItem.setF_price(foods.get(idx).getF_price());

                //個数情報を取得
                idx = spnQ.getSelectedItemPosition();
                upItem.setOd_quantity(idx);

                //個数情報が”0”でなければ変更後の情報を注文リストへ表示　
                if(upItem.getOd_quantity() != 0){
                listItems.set(menuIdx - 1, upItem);
                btnSend.setText(R.string.btnSend_add);
                createOrderList();

                //”0”かつitemsの要素数が1以下であればクリア
                }else if(listItems.size() <= 1 ){
                    listItems.clear();
                    createOrderList();
                    createFoods();
                    createSeats();
                    setSpnQuo();
                    etMemo.setText("");
                    btnSend.setText(R.string.btnSend_add);

                    //”0”かつitemsの要素数が上記以外の場合は、該当インデックスのみ削除
                }else{
                    listItems.remove(menuIdx - 1);
                    createOrderList();
                    createFoods();
                    createSeats();
                    setSpnQuo();
                    etMemo.setText("");
                    btnSend.setText(R.string.btnSend_add);
                }


            }

            //席情報が選択されているか、個数情報が0ではない場合は、席情報以外のスピナーをリセットする
            if(!(selectedSeats == 0 || item.getOd_quantity() == 0)) {
                int s = 0;
                switch (item.getS_id()) {
                    case "000":
                        s = 0;
                        break;

                    case "001":
                        s = 1;
                        break;
                    case "002":
                        s = 2;
                        break;
                    case "003":
                        s = 3;
                        break;
                    case "004":
                        s = 4;
                        break;
                    case "005":
                        s = 5;
                        break;
                }
                item = new ListItem();
                createFoods();
                createSeats();
                spnS.setSelection(s);
                setSpnQuo();
                etMemo.setText("");
            }

        });

        //注文送信ボタンを押したとき
        btnMenu.setOnClickListener(v1 -> {
            int n = lvList.getCount();
            //送信リストに何も入っていなければトースト表示
            if(n == 0){
                Toast.makeText(this,R.string.menuNull,Toast.LENGTH_SHORT).show();
            }else{
            //注文リストの表示をクリア
            ArrayList<ListItem> li = new ArrayList<>();
            OrderListAdapter adapter = new OrderListAdapter(this,li,R.layout.list_item);
            lvList.setAdapter(adapter);

            //注文済みリストに送信された注文を表示し、注文情報をリセット
            searchList.clear();

            sendNewData();

            createFoods();
            createSeats();
            setSpnQuo();
            etMemo.setText("");
            }
        });

        //空席照会ボタンを押したとき
        btnSeat.setOnClickListener(v2 -> {
            listS.clear();
            seats.clear();
            downloadSeats();
            createSeats();
        });

        //注文リストを選択したとき
        lvList.setOnItemClickListener((av, view, position, id)->{
            //何も選択されていない状態のときは新たに該当行を選択状態とする
            if(menuIdx == 0 || menuSelected == false) {
                view.setBackgroundColor(Color.parseColor("#808080"));
                menuSelected = true;
                btnSend.setText(R.string.btnSend_change);
                menuIdx = position + 1;
            //選択済みの項目をもう一度タップしたときは選択状態を解除する
            }else if(menuIdx == (position + 1) && menuSelected == true){
                v.setBackground(null);
                btnSend.setText(R.string.btnSend_add);
                menuSelected = false;
            //選択済みの項目以外をタップしたときは選択状態を解除し、新たにタップされた行を選択状態とする
            }else if(menuIdx != (position + 1) && menuSelected == true){
                v.setBackground(null);
                view.setBackgroundColor(Color.parseColor("#808080"));
                btnSend.setText(R.string.btnSend_change);
                menuIdx = position + 1;
                menuSelected = true;
            }
            //選択状態が解除された際は選択欄をリセットする
            if(!menuSelected){
                createFoods();
                createSeats();
                setSpnQuo();
                etMemo.setText("");

            //選択状態になったときに選択欄に該当行の情報を表示させる
            }else{
            upItem = new ListItem();
            upItem = listItems.get(position);
            String f = String.format(upItem.getF_id() + " " + upItem.getF_name() + " " + upItem.getF_price() + "円");
            String q = String.format(upItem.getOd_quantity()+"");
            int s = 0;
            switch (upItem.getS_id()){
                case "000":s = 0;
                break;

                case "001":s = 1;
                break;
                case "002":s = 2;
                    break;
                case "003":s = 3;
                    break;
                case "004":s = 4;
                    break;
                case "005":s = 5;
                    break;
            }
            spnF.setSelection(listF.indexOf(f));
            spnS.setSelection(s);
            spnQ.setSelection(listQ.indexOf(q));
            etMemo.setText(upItem.getOd_memo());}
            v = view; //viewの情報を保持
        });

        //注文照会ボタンが押されたとき
        btnSearch.setOnClickListener(v1 -> {
            searchList.clear();
            //席が選択されていない場合は全注文情報を表示
            if(spnS.getSelectedItemPosition() == 0){
            StringBuilder result = new StringBuilder();
            searchList.clear();
            Executors.newSingleThreadExecutor().execute(()->{
                try{
                    URL urlSeats = new URL("http://10.1.1.34/list_JSON_all.php");
                    HttpURLConnection con =(HttpURLConnection) urlSeats.openConnection();
                    con.setRequestMethod("GET");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                            con.getInputStream(), StandardCharsets.UTF_8));
                    String line;
                    while((line = reader.readLine()) != null){
                        result.append(line);
                    }
                    try{
                        JSONArray ary = new JSONArray(result.toString());
                        for(int i = 0; i < ary.length();i++){
                            JSONObject ln = ary.getJSONObject(i);
                            ListItem listmm = new ListItem();
                            listmm.setF_id(ln.getString("f_id"));
                            listmm.setF_name(ln.getString("f_name"));
                            listmm.setF_price(ln.getInt("f_price"));
                            listmm.setOd_quantity(ln.getInt("od_quantity"));
                            listmm.setOd_state(ln.getInt("od_state"));
                            listmm.setTime(ln.getString("od_time"));
                            listmm.setOd_memo(ln.getString("od_memo"));
                            listmm.setS_id(ln.getString("s_id"));
                            listmm.setOd_id(ln.getInt("od_id"));
                            listmm.setO_id(ln.getInt("o_id"));
                            searchList.add(listmm);
                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    HandlerCompat.createAsync(getMainLooper()).post(()->{
                        OkListAdapter okadapter = new OkListAdapter(this,searchList,R.layout.list_ok_item);
                        ListView ok = findViewById(R.id.OKlist);
                        ok.setAdapter(okadapter);
                    });
                }catch (IOException e){
                    e.printStackTrace();
                }
            });

            //席が選択されている場合は、その席の注文情報を表示する
            }else {
                sendSeatData();
            }
        });

        //注文済みリストを選択したとき
        okList.setOnItemClickListener((av, view, position, id)->{
            StringBuilder result = new StringBuilder();
            ListItem sendOkitem = searchList.get(position);
            int spnIdx = 0;
            spnIdx = spnS.getSelectedItemPosition();

            //o_stateが”調理済み”の場合は”配膳済み”とし、データベースへ登録

            if(sendOkitem.getOd_state() == 2 ) {
                if(spnIdx == 0){
                    Executors.newSingleThreadExecutor().execute(() -> {
                        try {
                            URL url = new URL("http://10.1.1.34/update_JSON.php");
                            HttpURLConnection con = (HttpURLConnection) url.openConnection();
                            con.setRequestMethod("POST");
                            con.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
                            con.setDoOutput(true);
                            OutputStream os = con.getOutputStream();
                            PrintStream ps = new PrintStream(os);
                            String msg = "";
                            try {
                                JSONArray ary = new JSONArray();
                                JSONObject jobj = new JSONObject();
                                jobj.put("s_id", sendOkitem.getS_id());
                                jobj.put("od_id", sendOkitem.getOd_id());
                                jobj.put("od_state", 3);
                                ary.put(jobj);
                                msg = ary.toString();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            ps.print(msg);
                            ps.close();
                            BufferedReader reader = new BufferedReader(new InputStreamReader(
                                    con.getInputStream(), StandardCharsets.UTF_8));
                            String line;
                            while ((line = reader.readLine()) != null) {
                                result.append(line);
                            }
                            HandlerCompat.createAsync(getMainLooper()).post(() -> {
                                searchList.clear();
                                try {
                                    JSONArray ary = new JSONArray(result.toString());
                                    for (int i = 0; i < ary.length(); i++) {
                                        JSONObject ln = ary.getJSONObject(i);
                                        ListItem listmm = new ListItem();
                                        listmm.setF_id(ln.getString("f_id"));
                                        listmm.setS_id(ln.getString("s_id"));
                                        listmm.setF_name(ln.getString("f_name"));
                                        listmm.setF_price(ln.getInt("f_price"));
                                        listmm.setOd_quantity(ln.getInt("od_quantity"));
                                        listmm.setOd_state(ln.getInt("od_state"));
                                        listmm.setTime(ln.getString("od_time"));
                                        listmm.setOd_memo(ln.getString("od_memo"));
                                        listmm.setOd_id(ln.getInt("od_id"));
                                        listmm.setO_id(ln.getInt("o_id"));
                                        searchList.add(listmm);
                                    }
                                    OkListAdapter okadapter = new OkListAdapter(this, searchList, R.layout.list_ok_item);
                                    okList.setAdapter(okadapter);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                  });
            }else {
                Executors.newSingleThreadExecutor().execute(() -> {
                    try {
                        URL url = new URL("http://10.1.1.34/update_seat_order_JSON.php");
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setRequestMethod("POST");
                        con.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
                        con.setDoOutput(true);
                        OutputStream os = con.getOutputStream();
                        PrintStream ps = new PrintStream(os);
                        String msg = "";
                        try {
                            JSONArray ary = new JSONArray();
                            JSONObject jobj = new JSONObject();
                            jobj.put("s_id", sendOkitem.getS_id());
                            jobj.put("od_id", sendOkitem.getOd_id());
                            jobj.put("od_state", 3);
                            ary.put(jobj);
                            msg = ary.toString();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ps.print(msg);
                        ps.close();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(
                                con.getInputStream(), StandardCharsets.UTF_8));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            result.append(line);
                        }
                        HandlerCompat.createAsync(getMainLooper()).post(() -> {
                            searchList.clear();
                            try {
                                JSONArray ary = new JSONArray(result.toString());
                                int sum = 0;
                                for (int i = 0; i < ary.length(); i++) {
                                    JSONObject ln = ary.getJSONObject(i);
                                    ListItem listmm = new ListItem();
                                    listmm.setF_id(ln.getString("f_id"));
                                    listmm.setS_id(ln.getString("s_id"));
                                    listmm.setF_name(ln.getString("f_name"));
                                    listmm.setF_price(ln.getInt("f_price"));
                                    listmm.setOd_quantity(ln.getInt("od_quantity"));
                                    listmm.setOd_state(ln.getInt("od_state"));
                                    //od_stateが”3”以外のものがあるかどうかを判別
                                    if(ln.getInt("od_state") != 3){
                                        //check = true　-> 会計可能
                                        //"3"以外のものが1つでもあればcheck = false -> 会計不可
                                        check = false;
                                    }else{
                                        check = true;
                                    }
                                    listmm.setTime(ln.getString("od_time"));
                                    listmm.setOd_memo(ln.getString("od_memo"));
                                    listmm.setOd_id(ln.getInt("od_id"));
                                    listmm.setO_id(ln.getInt("o_id"));
                                    sum += ln.getInt("sum");
                                    searchList.add(listmm);

                                }
                                ListItem last = new ListItem();
                                last.setF_price(sum);
                                last.setF_name("             合計");
                                searchList.add(last);

                                OkListAdapter okadapter = new OkListAdapter(this,searchList,R.layout.list_ok_item);
                                okList.setAdapter(okadapter);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                }
            }

        });

        //会計ボタンを押したとき
        btnCheck.setOnClickListener(v1 -> {
            searchList.clear();
            //席が選択されていない場合はトースト表示
            if(spnS.getSelectedItemPosition() == 0){
                Toast.makeText(this,R.string.noSeat,Toast.LENGTH_SHORT).show();
            //席が選択されている場合は改めて最新の席情報を取得
            }else{
                sendSeatData();
            //すべて配膳済みの場合は”合計　0”の表示をし、o_stateを”1”へ変更で会計済み
            if(check){
                StringBuilder result = new StringBuilder();
                SeatItem sendOkitem = seats.get(spnS.getSelectedItemPosition()-1);

                Executors.newSingleThreadExecutor().execute(() -> {

                    try {
                        URL url = new URL("http://10.1.1.34/delete_JSON.php");
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setRequestMethod("POST");
                        con.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
                        con.setDoOutput(true);
                        OutputStream os = con.getOutputStream();
                        PrintStream ps = new PrintStream(os);

                        String msg = "";
                        try {
                            JSONArray ary = new JSONArray();
                            JSONObject jobj = new JSONObject();
                            jobj.put("s_id", sendOkitem.getS_id());
                            jobj.put("o_state", 1);
                            ary.put(jobj);
                            msg = ary.toString();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ps.print(msg);
                        ps.close();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(
                                con.getInputStream(), StandardCharsets.UTF_8));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            result.append(line);
                        }
                        HandlerCompat.createAsync(getMainLooper()).post(() -> {
                            searchList.clear();
                            ListItem clearList = new ListItem();
                            clearList.setF_name("             合計");
                            clearList.setF_price(0);
                            searchList.add(clearList);
                            OkListAdapter okadapter = new OkListAdapter(this,searchList,R.layout.list_ok_item);
                            okList.setAdapter(okadapter);
                        });

                        check = false;

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
               });
                createFoods();
                createSeats();
                setSpnQuo();
                etMemo.setText("");

                //配膳済になっていないものがあればトースト表示
            }else {
                Toast.makeText(this,R.string.checkError,
                        Toast.LENGTH_SHORT).show();
            }}
        });
    }

    //席情報をデータベースから取得
    private void downloadSeats(){
        StringBuilder result = new StringBuilder();
        Executors.newSingleThreadExecutor().execute(()->{
            try{
                URL urlSeats = new URL("http://10.1.1.34/list_seats.php");
                HttpURLConnection con =(HttpURLConnection) urlSeats.openConnection();
                con.setRequestMethod("GET");
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        con.getInputStream(), StandardCharsets.UTF_8));
                String line;
                while((line = reader.readLine()) != null){
                    result.append(line);
                }
                //スピナーに表示するリストの先頭文を追加
                listS.add("タップして席を選択");
                String s = "";
                try{
                    JSONArray ary = new JSONArray(result.toString());
                    for(int i = 0; i < ary.length(); i++){
                        JSONObject ln = ary.getJSONObject(i);
                        SeatItem seat = new SeatItem();
                        //s_idが重複している場合、最新のデータが登録される
                        if(s.equals(ln.getString("s_id"))){
                        }else{
                            seat.setO_id(ln.getInt("o_id"));
                            seat.setS_capacity(ln.getInt("s_capacity"));
                            seat.setS_id(ln.getString("s_id"));
                            seats.add(seat);
                        }
                        s = ln.getString("s_id");
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
                HandlerCompat.createAsync(getMainLooper()).post(()->{
                    createSeatsString();
                });
            }catch (IOException e){
                e.printStackTrace();
            }
        });
    }

    //スピナーの席情報を作成
    private void createSeatsString(){
        for(int i = 0; i < seats.size() ; i++){
            SeatItem makeSeat = seats.get(i);
            //食事中か否かを判別　”0”の場合「空き」
            if(makeSeat.getO_id() != 0){
                listS.add(makeSeat.getS_id() + "席 "
                        + "(" + makeSeat.getS_capacity() + "人用)  食事中");
            }else{
                listS.add(makeSeat.getS_id() + "席 "
                        + "(" + makeSeat.getS_capacity() + "人用)  空き");
            }
        }
        createSeats();
    }
    //席情報をスピナーに表示する
    private void createSeats(){
       ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,listS);
        spnS.setAdapter(adapter);

    }

    //メニュー情報をデータベースからダウンロード
    private void downloadFoods(){
        StringBuilder result = new StringBuilder();
        Executors.newSingleThreadExecutor().execute(()->{
            try{
                URL urlSeats = new URL("http://10.1.1.34/list_foods.php");
                HttpURLConnection con =(HttpURLConnection) urlSeats.openConnection();
                con.setRequestMethod("GET");
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        con.getInputStream(), StandardCharsets.UTF_8));
                String line;
                while((line = reader.readLine()) != null){
                    result.append(line);
                }
           try{
                    JSONArray ary = new JSONArray(result.toString());
                    for(int i = 0; i < ary.length(); i++){
                        JSONObject ln = ary.getJSONObject(i);
                        FoodItem food = new FoodItem();
                        food.setF_id(ln.getString("f_id"));
                        food.setF_name(ln.getString("f_name"));
                        food.setF_price(ln.getInt("f_price"));
                        foods.add(food);
                        listF.add(ln.getString("f_id") + " "
                                + ln.getString("f_name") + " "
                                + ln.getInt("f_price")+"円");
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
                HandlerCompat.createAsync(getMainLooper()).post(()->{
                    createFoods();
                });
            }catch (IOException e){
                e.printStackTrace();
            }
        });
    }

    //スピナーにメニュー情報を表示
    public void createFoods(){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,listF);
        spnF.setAdapter(adapter);

    }

    //個数情報を作成
    private void createSpnQuo(){
        for(int i = 0; i < 11 ;i++){
            listQ.add(i + "");
        }
    }
    //個数情報をスピナーに登録
    private void setSpnQuo(){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,listQ);
        spnQ.setAdapter(adapter);
        spnQ.setSelection(1);
    }

    //席情報が選択されたとき
    public void spnSeatsSelected(){
        spnS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                StringBuilder result = new StringBuilder();
                ListItem sendOkitem = item;
                spnS = (Spinner) parent;
                int spinsIdx = 0;
                String set = "";
                spinsIdx = spnS.getSelectedItemPosition();

                //選択位置が”0”でなければ情報登録
                if(spinsIdx != 0){
                    item.setS_id(seats.get(spinsIdx-1).getS_id());
                    item.setO_id(seats.get(spinsIdx-1).getO_id());

                    //選択された席が”空き”であった場合食事中へ変更
                    set = seats.get(spinsIdx-1).getS_id() + "席 "
                            + "(" + seats.get(spinsIdx-1).getS_capacity() +"人用)  食事中";
                    listS.set(spinsIdx,set);
                    //席が選択されているか否かを登録　1　＝　選択済み
                    selectedSeats = 1;
                    sendSeatData();
                    searchList.clear();

                    //”空き”であった場合データベースへ基本情報を登録する（o_idを生成する）
                    Executors.newSingleThreadExecutor().execute(() -> {

                        try {
                            URL url = new URL("http://10.1.1.34/insert_order_basic_JSON.php");
                            HttpURLConnection con = (HttpURLConnection) url.openConnection();
                            con.setRequestMethod("POST");
                            con.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
                            con.setDoOutput(true);
                            OutputStream os = con.getOutputStream();
                            PrintStream ps = new PrintStream(os);

                            String msg = "";
                            try {
                                JSONArray ary = new JSONArray();
                                JSONObject jobj = new JSONObject();
                                jobj.put("s_id", sendOkitem.getS_id());
                                jobj.put("o_id", sendOkitem.getO_id());
                                ary.put(jobj);
                                msg = ary.toString();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            ps.print(msg);
                            ps.close();
                            BufferedReader reader = new BufferedReader(new InputStreamReader(
                                    con.getInputStream(), StandardCharsets.UTF_8));
                            String line;
                            while ((line = reader.readLine()) != null) {
                                result.append(line);
                            }
                            HandlerCompat.createAsync(getMainLooper()).post(() -> {

                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    searchList.clear();
                }
                else{
                    selectedSeats = 0;
                }
        }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    //メニューが選択されたとき
    public void spnFoodsSelected(){
        spnF.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spnF = (Spinner) parent;
                int idx = spnF.getSelectedItemPosition();
                item.setF_id(foods.get(idx).getF_id());
                item.setF_name(foods.get(idx).getF_name());
                item.setF_price(foods.get(idx).getF_price());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });}

    //個数情報が選択されたとき
    public void spnQuoSelected(){
        spnQ.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spnQ = (Spinner) parent;
                int idx = spnQ.getSelectedItemPosition();
                item.setOd_quantity(idx);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    //データベースへ注文リストを登録
    private void sendNewData(){
        StringBuilder result = new StringBuilder();
        Executors.newSingleThreadExecutor().execute(()->{
            try{
                URL url = new URL("http://10.1.1.34/insert_JSON.php");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type","text/plain; charset=utf-8");
                con.setDoOutput(true);
                OutputStream os = con.getOutputStream();
                PrintStream ps = new PrintStream(os);

                String msg = "";
                try{
                    JSONArray ary = new JSONArray();
                    for(int i = 0; i < listItems.size(); i++) {
                        JSONObject jobj = new JSONObject();
                        ListItem senditem = listItems.get(i);
                        jobj.put("od_f_id", senditem.getF_id());
                        jobj.put("od_quantity", senditem.getOd_quantity());
                        jobj.put("od_memo", senditem.getOd_memo());
                        jobj.put("s_id", senditem.getS_id());
                        jobj.put("f_price", senditem.getF_price());
                        jobj.put("time", senditem.getTime());
                        jobj.put("od_state", senditem.getOd_state());
                        ary.put(jobj);
                    }
                    msg = ary.toString();
                }catch (JSONException e){
                    e.printStackTrace();
                }
                ps.print(msg);
                ps.close();
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        con.getInputStream(),StandardCharsets.UTF_8));
                String line;
                while ((line = reader.readLine()) != null){
                    result.append(line);
                }
                HandlerCompat.createAsync(getMainLooper()).post(() ->{
                    try{
                        JSONArray ary = new JSONArray(result.toString());
                        int sum = 0;
                        for(int i = 0; i < ary.length(); i++){
                            JSONObject ln = ary.getJSONObject(i);
                            ListItem listmm = new ListItem();
                            listmm.setF_id(ln.getString("f_id"));
                            listmm.setO_id(ln.getInt("o_id"));
                            listmm.setS_id(ln.getString("s_id"));
                            listmm.setF_name(ln.getString("f_name"));
                            listmm.setF_price(ln.getInt("f_price"));
                            listmm.setOd_quantity(ln.getInt("od_quantity"));
                            listmm.setOd_state(ln.getInt("od_state"));
                            listmm.setTime(ln.getString("od_time"));
                            listmm.setOd_memo(ln.getString("od_memo"));
                            listmm.setOd_id(ln.getInt("od_id"));
                            sum += ln.getInt("sum");
                            searchList.add(listmm);
                        }
                        //注文済みリストの末尾に合計金額を見出しとともに表示する
                        ListItem last = new ListItem();
                        last.setF_price(sum);
                        last.setF_name("             合計");
                        searchList.add(last);
                        OkListAdapter okadapter = new OkListAdapter(this,searchList,R.layout.list_ok_item);
                        okList.setAdapter(okadapter);
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                });
                    listItems.clear();
            }catch(IOException e){
                e.printStackTrace();
            }
        });
    }
    //選択した席情報から、注文済みリストを呼び出す
    private void sendSeatData(){
        StringBuilder result = new StringBuilder();
        ListItem sendSeatitem = item;

        Executors.newSingleThreadExecutor().execute(()->{
            try{
                URL url = new URL("http://10.1.1.34/seat_menulist_JSON.php");
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
                    jobj.put("s_id",sendSeatitem.getS_id());
                    ary.put(jobj);
                    msg = ary.toString();
                }catch (JSONException e){
                    e.printStackTrace();
                }
                ps.print(msg);
                ps.close();
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        con.getInputStream(),StandardCharsets.UTF_8));
                String line;
                while ((line = reader.readLine()) != null){
                    result.append(line);
                }
                HandlerCompat.createAsync(getMainLooper()).post(() ->{

                    try{
                        JSONArray ary = new JSONArray(result.toString());
                        int sum = 0;
                        for(int i = 0; i < ary.length(); i++){
                            JSONObject ln = ary.getJSONObject(i);
                            ListItem listmm = new ListItem();
                            listmm.setF_id(ln.getString("f_id"));
                            listmm.setO_id(ln.getInt("o_id"));
                            listmm.setS_id(ln.getString("s_id"));

                            listmm.setF_name(ln.getString("f_name"));
                            listmm.setF_price(ln.getInt("f_price"));
                            listmm.setOd_quantity(ln.getInt("od_quantity"));
                            listmm.setOd_state(ln.getInt("od_state"));

                            //od_stateが”3”以外のものがあるかどうかを判別
                            if(ln.getInt("od_state") != 3){
                                //check = true　-> 会計可能
                                //"3"以外のものが1つでもあればcheck = false -> 会計不可
                                check = false;
                            }else{
                                check = true;
                            }

                            listmm.setTime(ln.getString("od_time"));
                            listmm.setOd_memo(ln.getString("od_memo"));
                            listmm.setOd_id(ln.getInt("od_id"));
                            sum += ln.getInt("sum");
                            searchList.add(listmm);
                        }

                        //注文済みリストの末尾に合計金額を見出しとともに表示する
                        ListItem last = new ListItem();
                        last.setF_price(sum);
                        last.setF_name("             合計");
                        searchList.add(last);

                        OkListAdapter okadapter = new OkListAdapter(this,searchList,R.layout.list_ok_item);
                        okList.setAdapter(okadapter);

                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                });
            }catch(IOException e){
                e.printStackTrace();
            }
        });
    }
    //注文リストを表示する
    public void createOrderList(){
        OrderListAdapter adapter = new OrderListAdapter(this, listItems,R.layout.list_item);
        lvList.setAdapter(adapter);
    }

    //現在日時を取得
    public static String getNowDate(){
        final DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        final Date date = new Date(System.currentTimeMillis());
        return df.format(date);
    }

    //オプションメニューを作成
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu,menu);
        return true;
    }

    //メニュークリックで厨房アプリへ遷移
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int orderId = item.getOrder();
        if(orderId == 0) {
            searchList.clear();
            OkListAdapter okadapter = new OkListAdapter(this,searchList,R.layout.list_ok_item);
            okList.setAdapter(okadapter);
            Intent i = new Intent(this, KitchenActivity.class);
       startActivity(i);
        }

        return true;
    }
}