<?php
require "../DataBase.php";
$db = new DataBase();
if (isset($_POST['id'])) {
    if ($db->dbConnect()) {
        if ($db->deleteVideo($_POST['id'])) {
            $response['success'] = 1;
            $response['data']    = "success";
            echo json_encode($response);
        } else {
            $response['success'] = 0;
            $response['data']    = "error";
            echo json_encode($response);
        }
    } else {
        $response['success'] = 0;
        $response['data']    = "";
        echo json_encode($response);
    }
} else {
    $response['success'] = 0;
    $response['data']    = "ID is Reqired";
    echo json_encode($response);
}
