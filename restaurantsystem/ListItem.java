package to.msn.wings.restaurantsystem;

public class ListItem {
    private int id = 0;
    private String f_id = null;
    private String f_name = null;
    private String od_memo = null;
    private int f_price = 0;
    private int od_quantity = 0;
    private  String s_id = null;
    private int o_id = 0;
    private int od_id = 0;
    private String time = null;
    private int od_state = 0;
    public int getOd_id() {
        return od_id;
    }
    public void setOd_id(int od_id) {
        this.od_id = od_id;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public int getOd_state() {
        return od_state;
    }
    public void setOd_state(int od_state) {
        this.od_state = od_state;
    }
    public int getO_id() {
        return o_id;
    }
    public void setO_id(int o_id) {
        this.o_id = o_id;
    }
    public String getS_id() {
        return s_id;
    }
    public void setS_id(String s_id) {
        this.s_id = s_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public String getF_id() {
        return f_id;
    }
    public void setF_id(String f_id) {
        this.f_id = f_id;
    }
    public String getF_name() {
        return f_name;
    }
    public void setF_name(String f_name) {
        this.f_name = f_name;
    }
    public String getOd_memo() {
        return od_memo;
    }
    public void setOd_memo(String od_memo) {
        this.od_memo = od_memo;
    }
    public int getF_price() {
        return f_price;
    }
    public void setF_price(int f_price) {
        this.f_price = f_price;
    }
    public int getOd_quantity() {
        return od_quantity;
    }
    public void setOd_quantity(int od_quantity) {
        this.od_quantity = od_quantity;
    }
}
