<?php
require "../DataBase.php";
$response = array();

$db = new DataBase();
if (isset($_POST['url'])) {
    if ($db->dbConnect()) {
        if ($db->addVideo($_POST['url'])) {

            $response['success'] = 1;
            $response['message'] = "Video url inserted successfully";
            echo json_encode($response);
            //  echo "Error: Database connection";
        } else {
            $response['success'] = 0;
            $response['message'] = "Video url inserted Failed";
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
    $response['message'] = "URL is required";
    // echo "Error: Database connection";
    echo json_encode($response);
}
