

<?php


require "DataBase.php";
$db = new DataBase();
if(isset($_POST['submit'])){
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
}

?>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
</head>
<body>
    <form method="POST" action="signup.php">
 <input type="text" name="FullName" value="abcc"/>
 <input type="text" name="city" value="abcc"/>
 <input type="text" name="NICNumber" value="122"/>
 <input type="text" name="usertype" value="abcc"/>
 <input type="text" name="email" value="abcc"/>
 <input type="text" name="contactno" value="123"/>
 <input type="text" name="password" value="abcc"/>
        <input type="submit" name="submit">
    </form>
</body>
</html>

                           



