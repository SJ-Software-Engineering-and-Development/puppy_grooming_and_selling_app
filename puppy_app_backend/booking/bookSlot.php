<?php
require "../DataBase.php";
$response = array();

$db = new DataBase();
if (
    isset($_POST['package_type']) &&
    isset($_POST['date']) &&
    isset($_POST['user_id']) &&
    isset($_POST['booking_slot_id'])
) {
    if ($db->dbConnect()) {
        if ($db->bookSlot($_POST['package_type'],
            $_POST['date'],
            $_POST['user_id'],
            $_POST['booking_slot_id'])) {

            $response['success'] = 1;
            $response['message'] = "Image Uploaded Successfully with Retrofit";
            echo json_encode($response);
            //  echo "Error: Database connection";
        } else {
            $response['success'] = 0;
            $response['message'] = "Image Uploaded Failed";
            echo json_encode($response);
            //  echo "Error: Database connection";
        }
    } else {
        // echo "Error: Database connection";
        $response['success'] = 0;
        $response['message'] = "Error: Database connection";
        echo json_encode($response);
    }

} else {
    $response['success'] = 0;
    $response['message'] = "All fields are required";
    // echo "Error: Database connection";
    echo json_encode($response);
}
