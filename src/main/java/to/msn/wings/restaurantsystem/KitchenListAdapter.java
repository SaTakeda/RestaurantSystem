package to.msn.wings.restaurantsystem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

//受注食品リストに表示するアダプター
public class KitchenListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<ListItem> data;
    private int resource;
    private LayoutInflater inflater;
    public KitchenListAdapter(Context context, ArrayList<ListItem> data, int resource) {
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

        ((TextView)sview.findViewById(R.id.tvFid3)).setText(item.getF_id());
        ((TextView)sview.findViewById(R.id.tvFname3)).setText(item.getF_name());
        ((TextView)sview.findViewById(R.id.tvFprice3)).setText(item.getF_price()+"円");
        ((TextView)sview.findViewById(R.id.tvFquo3)).setText(item.getOd_quantity()+"個");
        ((TextView)sview.findViewById(R.id.tvFmemo3)).setText(item.getOd_memo());
        //od_stateの値により表示内容を変更
        String os = "";
        switch (item.getOd_state()){
            case 0 : os = "なし";
                break;
            case 1 : os = "調理中";
                break;
            case 2 : os = "調理済";
                break;
            case 3 : os = "配膳済";
                break;
        }
        ((TextView)sview.findViewById(R.id.tvState3)).setText(os);
        ((TextView)sview.findViewById(R.id.tvTime3)).setText(item.getTime());
        ((TextView)sview.findViewById(R.id.tvseat)).setText(item.getS_id());

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
