    <?php
    $s = new PDO("mysql:host=localhost;dbname=Restaurant","root","root");
    $re = $s -> query("SELECT f.f_id,f.f_name,f.f_price,od.od_quantity,od.od_state,od.od_time,od.od_memo,s.s_id,od.od_id 
    FROM order_basic as ob INNER JOIN order_detail as od ON ob.o_id = od.od_o_id 
    INNER JOIN seats as s on ob.o_s_id = s.s_id INNER JOIN foods as f on f.f_id = od.od_f_id WHERE od.od_state = '1';");
    
    $rows = array();
    while($row = $re -> fetch(PDO::FETCH_ASSOC)){
        $rows[] = $row;
    }
    header('Content-Type: application/json;charset=utf-8');
    echo json_encode($rows);



    ?>
  