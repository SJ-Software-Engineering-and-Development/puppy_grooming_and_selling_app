<?php
require "../DataBase.php";
$db = new DataBase();
if (isset($_POST['status'])) {
    if ($db->dbConnect()) {
        $data                = $db->getPosts($_POST['status']);
        $response['success'] = 1;
        $response['data']    = $data;
        echo json_encode($response);
    } else {
        $response['success'] = 0;
        $response['data']    = "";
        echo json_encode($response);
    }
} else {
    $response['success'] = 0;
    $response['data']    = "status Reqired";
    echo json_encode($response);
}
