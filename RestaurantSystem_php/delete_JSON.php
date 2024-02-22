<?php

$recv = json_decode(file_get_contents('php://input'),true);

$s = new PDO("mysql:host=localhost;dbname=Restaurant","root","root");

foreach($recv as $rows){
    $Sid = $rows['s_id'];
    $Ost = $rows['o_state'];
}
$sql = "UPDATE order_basic SET o_state = :o_state WHERE (o_s_id = :o_s_id) and (o_state = '0')";
            $stmt = $s->prepare($sql);
 
            $stmt->execute([":o_state" => $Ost, ":o_s_id" => $Sid]);

            $dbh = null;    
?>