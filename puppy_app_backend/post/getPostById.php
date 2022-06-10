<?php
require "../DataBase.php";
$db = new DataBase();
if (isset($_POST['id'])) {
    if ($db->dbConnect()) {
        $data                = $db->getPostById($_POST['id']);
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
    $response['data']    = "id Reqired";
    echo json_encode($response);
}
