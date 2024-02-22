<?php

$recv = json_decode(file_get_contents('php://input'),true);

$s = new PDO("mysql:host=localhost;dbname=Restaurant","root","root");
$Oid = "";

foreach($recv as $rows){
    
    $Sid = $rows['s_id'];
    

$r1 = $s -> query("SELECT o_state FROM order_basic WHERE o_s_id = '$Sid'");

$sum = 0;
$cnt = 0;
$rer;
while($re1 = $r1 -> fetch()){
    $rer = $re1['o_state'];
    $sum += $re1['o_state'];
    $cnt++;
}


if($cnt == 0){
    $s -> query("INSERT INTO order_basic(o_date,o_s_id)VALUES(now(),'$Sid')");

}else if(($sum > 0) and ($rer == 1)){
    $s -> query("INSERT INTO order_basic(o_date,o_s_id)VALUES(now(),'$Sid')");

}

}
?>