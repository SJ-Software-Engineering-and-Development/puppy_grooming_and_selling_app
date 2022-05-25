<?php
require "../DataBase.php";
$db = new DataBase();
if (isset($_POST['date'])) {
    if ($db->dbConnect()) {
        $data                = $db->getBookedSlots($_POST['date']);
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
    $response['data']    = "date Reqired";
    echo json_encode($response);
}
