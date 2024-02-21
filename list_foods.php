<?php
$s = new PDO("mysql:host=localhost;dbname=Restaurant","root","root");
    $re = $s -> query("SELECT * FROM foods");
    
    $rows = array();
    while($row = $re -> fetch(PDO::FETCH_ASSOC)){
        $rows[] = $row;
    }
    header('Content-Type: application/json;charset=utf-8');
    echo json_encode($rows);

    ?>