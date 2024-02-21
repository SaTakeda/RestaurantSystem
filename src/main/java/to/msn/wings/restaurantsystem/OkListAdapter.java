package to.msn.wings.restaurantsystem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

//注文済リストへ表示するアダプター
public class OkListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<ListItem> data;
    private int resource;
    private LayoutInflater inflater;
    public OkListAdapter(Context context, ArrayList<ListItem> data, int resource) {
        this.context = context;
        this.data = data;
        this.resource = resource;
        inflater = (LayoutInflater) this.context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE
        );
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ListItem item =(ListItem) getItem(position);
        View sview = (convertView != null) ? convertView:
                inflater.inflate(resource,null);
        //f_idがnullでなければf_idを表示する
        if(item.getF_id() != null){
            ((TextView)sview.findViewById(R.id.tvFid2)).setText(item.getF_id()+" ");
        //f_idがnullならば何も表示しない
        }else{
            ((TextView)sview.findViewById(R.id.tvFid2)).setText("");
        }
        ((TextView)sview.findViewById(R.id.tvFname2)).setText(item.getF_name());
        ((TextView)sview.findViewById(R.id.tvFprice2)).setText(item.getF_price()+"円 ");

        //od_quantityが0でなければod_quantityを表示
        if(item.getOd_quantity() != 0){
            ((TextView)sview.findViewById(R.id.tvFquo2)).setText(item.getOd_quantity()+"個");
            //od_quantityが0ならば何も表示しない
        }else{
            ((TextView)sview.findViewById(R.id.tvFquo2)).setText("");
        }
        ((TextView)sview.findViewById(R.id.tvFmemo2)).setText(item.getOd_memo());

        //od_stateの値により表示内容を変更
        String os = "";
        switch (item.getOd_state()){
            case 0 : os = "";
                break;
            case 1 : os = "調理中";
                break;
            case 2 : os = "調理済";
                break;
            case 3 : os = "配膳済";
                break;
        }
        ((TextView)sview.findViewById(R.id.tvState)).setText(os);
        ((TextView)sview.findViewById(R.id.tvTime)).setText(item.getTime());
        return sview;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return data.get(position).getId();
    }


}
