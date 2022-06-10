<?php
require "../DataBase.php";
$response = array();

$db = new DataBase();
if (isset($_POST['id'])) {
    if ($db->dbConnect()) {
        if ($db->deleteBookingSlot($_POST['id'])) {
            $response['success'] = 1;
            $response['message'] = "Slot delete successfully";
            echo json_encode($response);
        } else {
            $response['success'] = 0;
            $response['message'] = "Slot delete Failed";
            echo json_encode($response);
        }
    } else {
        $response['success'] = 0;
        $response['message'] = "Error: Database connection";
        echo json_encode($response);
    }

} else {
    $response['success'] = 0;
    $response['message'] = "ID is required";
    echo json_encode($response);
}
