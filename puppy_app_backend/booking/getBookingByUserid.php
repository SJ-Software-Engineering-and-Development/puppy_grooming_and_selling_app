<?php
require "../DataBase.php";
$db = new DataBase();
if (isset($_POST['date']) && isset($_POST['user_id'])) {
    if ($db->dbConnect()) {
        $data                = $db->getBookedSlotsByUserId($_POST['date'], $_POST['user_id']);
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
    $response['data']    = "date and user_id Reqired";
    echo json_encode($response);
}
