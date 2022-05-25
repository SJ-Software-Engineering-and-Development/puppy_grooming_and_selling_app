<?php
require "DataBase.php";
$db = new DataBase();
if (isset($_POST['nic'])) {
    if ($db->dbConnect()) {
        echo $db->getLoginId($_POST['nic']);
    } else {
        echo "0";
    }
} else {
    echo "NIC is required";
}
