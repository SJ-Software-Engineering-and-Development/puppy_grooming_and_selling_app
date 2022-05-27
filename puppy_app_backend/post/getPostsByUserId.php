<?php
require "../DataBase.php";
$db = new DataBase();
if (isset($_POST['status']) && isset($_POST['user_id'])) {
    if ($db->dbConnect()) {
        $data                = $db->getPostsByUserId($_POST['status'], $_POST['user_id']);
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
    $response['data']    = "user_id, status are Reqired";
    echo json_encode($response);
}
