<?php

require "DataBase.php";
$db = new DataBase();

$response = $db->getMaxStatus();
echo "Response= ";
print_r($response);

if ($_SERVER['REQUEST_METHOD'] == 'POST' && isset($_POST['submit'])) {

}?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>

    <form action="#" method="POST">
        <label for="">Enter video URL</label>
        <input type="text" name="url" paceholder="Enter video url">
        <input type="submit" name="submit" value="submit">
    </form>
</head>
<body>

</body>
</html>
