<?php
$s = new PDO("mysql:host=localhost;dbname=Restaurant","root","root");
    $re = $s -> query("SELECT DISTINCT s.s_id,ob.o_id,s.s_capacity,ob.o_state,
    if((((od.od_state != '0') or (od.od_state is null)) and (ob.o_state = '0'))  ,ob.o_id,0) as o_id,
    ob.o_date
    FROM 
    order_basic as ob LEFT OUTER JOIN order_detail as od ON ob.o_id = od.od_o_id
    RIGHT OUTER JOIN seats as s on ob.o_s_id = s.s_id ORDER BY s.s_id,o_date DESC;");
    

    //od_stateが0以外でo_stateが0の席　＝　食事中
    
    $rows = array();
    while($row = $re -> fetch(PDO::FETCH_ASSOC)){


        $rows[] = $row;
    }
    header('Content-Type: application/json;charset=utf-8');
    echo json_encode($rows);

    ?>