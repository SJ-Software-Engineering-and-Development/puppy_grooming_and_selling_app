<?php
require "../DataBase.php";
$response = array();

$db = new DataBase();
if (
    isset($_POST['title']) &&
    isset($_POST['city']) &&
    isset($_POST['address']) &&
    isset($_POST['contact']) &&
    isset($_POST['open_close_times']) &&
    isset($_POST['longitude']) &&
    isset($_POST['latitude'])
) {
    if ($db->dbConnect()) {
        if ($db->addVeterinary($_POST['title'],
            $_POST['city'],
            $_POST['address'],
            $_POST['contact'],
            $_POST['open_close_times'],
            $_POST['longitude'],
            $_POST['latitude'])) {

            $response['success'] = 1;
            $response['message'] = "New veterinary added Successfully!";
            echo json_encode($response);
            //  echo "Error: Database connection";
        } else {
            $response['success'] = 0;
            $response['message'] = "Operation Failed";
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
