<?php
require "../DataBase.php";
$response = array();

$db = new DataBase();
if (
    isset($_POST['title']) &&
    isset($_POST['price']) &&
    isset($_POST['description']) &&
    isset($_POST['gender']) &&
    isset($_POST['age']) &&
    isset($_POST['date']) &&
    isset($_POST['location']) &&
    isset($_POST['imageName']) &&
    isset($_POST['encodedImage']) &&
    isset($_POST['status']) &&
    isset($_POST['breed_id']) &&
    isset($_POST['owner_id'])
) {
    if ($db->dbConnect()) {
        if ($db->submitPost($_POST['title'],
            $_POST['price'],
            $_POST['description'],
            $_POST['gender'],
            $_POST['age'],
            $_POST['date'],
            $_POST['location'],
            $_POST['imageName'],
            $_POST['encodedImage'],
            $_POST['status'],
            $_POST['breed_id'],
            $_POST['owner_id'])) {

            $response['success'] = 1;
            $response['message'] = "Post submited successfully";
            echo json_encode($response);
            //  echo "Error: Database connection";
        } else {
            $response['success'] = 0;
            $response['message'] = "Post submition Failed";
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
