
<?php

require "DataBase.php";
$db = new DataBase();

   if (
    isset($_POST['FullName']) &&
    isset($_POST['city']) &&
    isset($_POST['NICNumber']) &&
    isset($_POST['usertype']) &&
    isset($_POST['email']) &&
    isset($_POST['contactno']) &&
    isset($_POST['password']) ) {
    if ($db->dbConnect()) {
        if ($db->signUp(
        $_POST['FullName'],
        $_POST['city'],
        $_POST['NICNumber'],
        $_POST['usertype'],
        $_POST['email'],
        $_POST['contactno'],
        $_POST['password'])) {
            echo "Sign Up Success";
        } else echo "Sign up Failed";
    } else echo "Error: Database connection";
} else echo $_REQUEST['password']."All fields are required.";

?>




