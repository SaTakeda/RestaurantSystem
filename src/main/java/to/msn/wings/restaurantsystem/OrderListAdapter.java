package to.msn.wings.restaurantsystem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

//注文リストへ表示するアダプター
public class OrderListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<ListItem> data;
    private int resource;
    private LayoutInflater inflater;
    public OrderListAdapter(Context context, ArrayList<ListItem> data, int resource) {
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
        ((TextView)sview.findViewById(R.id.tvFid)).setText(item.getF_id());
        ((TextView)sview.findViewById(R.id.tvFname)).setText(item.getF_name());
        ((TextView)sview.findViewById(R.id.tvFprice)).setText(item.getF_price()+"円　");
        ((TextView)sview.findViewById(R.id.tvFquo)).setText(item.getOd_quantity()+"個");
        ((TextView)sview.findViewById(R.id.tvFmemo)).setText(item.getOd_memo());

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
