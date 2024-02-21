package to.msn.wings.restaurantsystem;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import androidx.recyclerview.widget.LinearLayoutManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SimpleReceiver extends BroadcastReceiver {
    KitchenActivity act;
    //受注食品リストを受け取る
    @Override
    public void onReceive(Context context, Intent intent) {
        String msg = intent.getStringExtra("message");
        act = (KitchenActivity)context;
        try{
            JSONArray ary = new JSONArray(msg);
            act.orderToCookList.clear();
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
                act.orderToCookList.add(listmm);
            }
            //サービス更新をアダプターへ知らせる
            act.adapterOrderToCook.notifyDataSetChanged();
            //スクロール停止になっていなければ、リストの末尾まで自動スクロール
            if(!act.stopScroll){
                act.lvOrderToCook.setSelection(act.lvOrderToCook.getCount() -1 );
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
}
