<?php
require "../DataBase.php";
$db = new DataBase();
if (isset($_POST['id'], $_POST['status'])) {
    if ($db->dbConnect()) {
        if ($db->updateUserAccountStatus($_POST['id'], $_POST['status'])) {
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
    $response['data']    = "user-id and status Reqired";
    echo json_encode($response);
}
