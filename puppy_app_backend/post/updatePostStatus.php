<?php
require "../DataBase.php";
$db = new DataBase();
if (isset($_POST['id']) && isset($_POST['status'])) {
    if ($db->dbConnect()) {
        if ($db->updatePostStatus($_POST['id'], $_POST['status'])) {
            $response['success'] = 1;
            $response['data']    = "updated";
            echo json_encode($response);
        } else {
            $response['success'] = 0;
            $response['data']    = "update error";
            echo json_encode($response);
        }
    } else {
        $response['success'] = 0;
        $response['data']    = "db connect error";
        echo json_encode($response);
    }
} else {
    $response['success'] = 0;
    $response['data']    = "status Reqired";
    echo json_encode($response);
}
