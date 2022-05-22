
<?php

require "DataBase.php";
$db = new DataBase();

   if (
    isset($_POST['PackageType']) &&
    isset($_POST['Date']) &&
    isset($_POST['Time']) &&
    isset($_POST['NicNumber']) ) {
    if ($db->dbConnect()) {
        if ($db->Booking(
        $_POST['PackageType'],
        $_POST['Date'],
        $_POST['Time'],
        $_POST['NicNumber'])) {
            echo "booking Success";
        } else echo "booking Failed";
    } else echo "Error: Database connection";
} else echo "All fields are required_test.";

?>




