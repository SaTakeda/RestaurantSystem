<?php

$recv = json_decode(file_get_contents('php://input'),true);

$s = new PDO("mysql:host=localhost;dbname=Restaurant","root","root");


foreach($recv as $rows){
    $Ost = $rows['od_state'];
    $Odid = $rows['od_id'];
    $Sid = $rows['s_id'];
}

$sql = "UPDATE order_detail SET od_state = :od_state WHERE od_id = :od_id";
            $stmt = $s->prepare($sql);
 
            $stmt->execute([":od_state" => $Ost, ":od_id" => $Odid]);
            $dbh = null;
            
            $re = $s -> query("SELECT ob.o_id,f.f_id,f.f_name,f.f_price,od.od_quantity,(f.f_price*od.od_quantity)as sum,
            od.od_state,od.od_time,od.od_memo,s.s_id,od.od_id,ob.o_state 
            FROM order_basic as ob INNER JOIN order_detail as od ON ob.o_id = od.od_o_id 
            INNER JOIN seats as s on ob.o_s_id = s.s_id 
            INNER JOIN foods as f on f.f_id = od.od_f_id 
            WHERE s.s_id = '$Sid' AND ob.o_state = 0 ORDER BY od.od_id;");
                       

        
   
                $rows = array();
                while($row = $re -> fetch(PDO::FETCH_ASSOC)){
                    $rows[] = $row;
                }
                header('Content-Type: application/json;charset=utf-8');
                echo json_encode($rows);            
             

?>