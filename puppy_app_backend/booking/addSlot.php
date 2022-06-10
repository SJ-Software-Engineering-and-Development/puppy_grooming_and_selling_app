<?php
require "../DataBase.php";
$response = array();

$db = new DataBase();
if (isset($_POST['from_time']) && isset($_POST['to_time']) && isset($_POST['description'])) {
    if ($db->dbConnect()) {
        if ($db->addBookingSlot($_POST['from_time'], $_POST['to_time'], $_POST['description'])) {
            $response['success'] = 1;
            $response['message'] = "Slot inserted successfully";
            echo json_encode($response);
        } else {
            $response['success'] = 0;
            $response['message'] = "Slot inserted Failed";
            echo json_encode($response);
        }
    } else {
        $response['success'] = 0;
        $response['message'] = "Error: Database connection";
        echo json_encode($response);
    }

} else {
    $response['success'] = 0;
    $response['message'] = "from_time, to_time, description are required";
    echo json_encode($response);
}
