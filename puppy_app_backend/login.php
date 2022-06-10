<?php
require "DataBase.php";
$db = new DataBase();

$response = array("success" => 0, "data" => "Login failed");
if (isset($_POST['idno']) && isset($_POST['password'])) {
    if ($db->dbConnect()) {

        $logIn_res = $db->logIn("users", $_POST['idno'], $_POST['password']);

        switch ($logIn_res) {
            case "login_failed":
                $response['success'] = 0;
                $response['data']    = "Login failed";
                break;
            case "suspended":
                $response['success'] = 0;
                $response['data']    = "Account suspended";
                break;
            case "active":
                $response['success'] = 1;
                $response['data']    = "Login success";
                break;
        }

        echo json_encode($response);

    } else {
        echo json_encode($response);
    }

} else {
    echo json_encode($response);
}
