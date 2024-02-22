<?php

$recv = json_decode(file_get_contents('php://input'),true);

$s = new PDO("mysql:host=localhost;dbname=Restaurant","root","root");
$Oid = "";
foreach($recv as $rows){
    $Fid = $rows['od_f_id'];
    $Odq = $rows['od_quantity'];
    $Odm = $rows['od_memo'];
    $Sid = $rows['s_id'];
    $Time = $rows['time'];
    $Ods = $rows['od_state'];

$r2 = $s -> query("SELECT o_id FROM order_basic WHERE (o_s_id = '$Sid') and (o_state = 0)");

while($re = $r2 -> fetch()){
    $Oid = $re["o_id"];
}

$s -> query("INSERT INTO order_detail(od_o_id,od_f_id,od_quantity,od_memo,od_time,od_state)
VALUES('$Oid','$Fid','$Odq','$Odm','$Time','$Ods')");
}

$re2 = $s -> query("SELECT ob.o_id,f.f_id,f.f_name,f.f_price,od.od_quantity,(f.f_price*od.od_quantity)as sum,
od.od_state,od.od_time,od.od_memo,s.s_id,od.od_id,ob.o_state 
FROM order_basic as ob INNER JOIN order_detail as od ON ob.o_id = od.od_o_id 
INNER JOIN seats as s on ob.o_s_id = s.s_id 
INNER JOIN foods as f on f.f_id = od.od_f_id 
WHERE s.s_id = '$Sid' AND ob.o_state = 0 ORDER BY od.od_id;");
           
    $rrows = array();
    while($rrow = $re2 -> fetch(PDO::FETCH_ASSOC)){
        $rrows[] = $rrow;
    }
    header('Content-Type: application/json;charset=utf-8');
    echo json_encode($rrows);          

?>