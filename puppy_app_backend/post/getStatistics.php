<?php
require "../DataBase.php";
$db = new DataBase();
if ($db->dbConnect()) {
    $data                = $db->getPostCountStatistics();
    $response['success'] = 1;
    $response['data']    = $data;
    echo json_encode($response);
} else {
    $response['success'] = 0;
    $response['data']    = "";
    echo json_encode($response);
}
