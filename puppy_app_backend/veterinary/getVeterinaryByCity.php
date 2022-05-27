<?php
require "../DataBase.php";
$db = new DataBase();
if (isset($_POST['city'])) {
    if ($db->dbConnect()) {
        $data                = $db->getVeterinaryByCity($_POST['city']);
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
    $response['data']    = "city is Reqired";
    echo json_encode($response);
}
