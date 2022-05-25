
<?php
require "DataBaseConfig.php";

class DataBase
{
    public $connect;
    public $data;
    private $sql;
    protected $servername;
    protected $username;
    protected $password;
    protected $databasename;

    public function __construct()
    {
        $this->connect      = null;
        $this->data         = null;
        $this->sql          = null;
        $dbc                = new DataBaseConfig();
        $this->servername   = $dbc->servername;
        $this->username     = $dbc->username;
        $this->password     = $dbc->password;
        $this->databasename = $dbc->databasename;
    }

    public function dbConnect()
    {
        $this->connect = mysqli_connect($this->servername, $this->username, $this->password, $this->databasename);
        return $this->connect;
    }

    public function prepareData($data)
    {
        return mysqli_real_escape_string($this->connect, stripslashes(htmlspecialchars($data)));
    }

    public function logIn($user, $idno, $password)
    {
        $idno     = $this->prepareData($idno);
        $password = $this->prepareData($password);

        //  $this->sql = "select * from " .`login_user_register` . " where `Nic Number`= '" . $idno . "'";
        $this->sql = "select * from login_user_register  where `Nic Number`= '{$idno}'";
        $result    = mysqli_query($this->connect, $this->sql);
        if ($result) {
            $row = mysqli_fetch_assoc($result);
            if (mysqli_num_rows($result) != 0) {
                $dbidno     = $row['Nic Number'];
                $dbpassword = $row['Password'];

                if ($dbidno == $idno && $password == $dbpassword) {
                    $login = true;
                } else {
                    $login = false;
                }
            } else {
                $login = false;
            }
        } else {
            $login = false;
        }

        return $login;
    }

    public function signUp(
        $FullName,
        $city,
        $NICNumber,
        $emausertype,
        $email,
        $contactno,
        $password) {
        $FullName    = $this->prepareData($FullName);
        $NICNumber   = $this->prepareData($NICNumber);
        $city        = $this->prepareData($city);
        $emausertype = $this->prepareData($emausertype);
        $email       = $this->prepareData($email);
        $contactno   = $this->prepareData($contactno);
        $password    = $this->prepareData($password);

        $this->sql =
            " INSERT INTO `login_user_register` (`Full Name`, `NIC Number`, `City`, `User Type`, `E-mail`, `Contact No`, `Password`) VALUES('" . $FullName . "','" . $NICNumber . "','" . $city . "','" . $emausertype . "', '" . $email . "', " . $contactno . ",'" . $password . "')";
        if (mysqli_query($this->connect, $this->sql)) {
            return true;
        } else {
            return false;
        }

    }

    public function getUser($id)
    {
        $response = "";

        $this->sql = "SELECT * FROM login_user_register WHERE id = {$id} LIMIT 1";
        $result    = mysqli_query($this->connect, $this->sql);
        if ($result) {

            if (mysqli_num_rows($result) != 0) {
                $response = mysqli_fetch_assoc($result);
            } else {
                $response = "";
            }
        } else {
            $response = "";
        }
        return $response;
    }

    public function Booking(
        $PackageType,
        $Date,
        $Time,
        $NicNumber) {
        $PackageType = $this->prepareData($PackageType);
        $Date        = $this->prepareData($Date);
        $Time        = $this->prepareData($Time);
        $NicNumber   = $this->prepareData($NicNumber);

        $this->sql =
            " INSERT INTO `bath_house_booking` (`Package Type`, `Date`, `Time`,  `Nic Number`) VALUES('" . $PackageType . "','" . $Date . "','" . $Time . "','" . $NicNumber . "')";
        if (mysqli_query($this->connect, $this->sql)) {
            return true;
        } else {
            return false;
        }

    }

    public function getLoginId($nic)
    {
        $nic      = $this->prepareData($nic);
        $login_id = 0;
        //  $this->sql = "select * from " .`login_user_register` . " where `Nic Number`= '" . $idno . "'";
        $this->sql = "select id from login_user_register  where `Nic Number`= '{$nic}'";
        $result    = mysqli_query($this->connect, $this->sql);
        if ($result) {
            $row = mysqli_fetch_assoc($result);
            if (mysqli_num_rows($result) != 0) {
                $login_id = $row['id'];
            } else {
                $login_id = 0;
            }
        } else {
            $login_id = 0;
        }
        return $login_id;
    }

    public function submitPost($title, $price, $description, $gender, $age, $date, $location, $imageName, $encodedImage, $status, $owner_id)
    {
        //  $image = $_POST["image"];
        $decodedImage = base64_decode("$encodedImage");
        $return       = file_put_contents("uploadedFiles/" . $imageName . ".JPG", $decodedImage);

        $saveImgName = $imageName . ".JPG";

        if ($return !== false) {
            // upload success
        }
        $sql = "INSERT INTO `post` (`title`, `price`, `description`, `gender`, `age`, `date`, `location`, `imageUrl`, `status`, `owner_id`) VALUES ";

        $sql .= " ('{$title}', '{$price}', '{$description}', '{$gender}', '{$age}', '{$date}', '{$location}', '{$saveImgName}', '{$status}', {$owner_id} ) ";

        $this->sql = $sql;
        $result    = mysqli_query($this->connect, $this->sql);
        if ($result) {
            $response = true;
        } else {
            $response = false;
        }

        return $response;
    }

    public function getPosts($status)
    {
        $response = array();

        $this->sql = "SELECT * FROM post WHERE status = '{$status}'";
        $result    = mysqli_query($this->connect, $this->sql);
        if ($result) {

            if (mysqli_num_rows($result) != 0) {
                while ($row = mysqli_fetch_assoc($result)) {
                    array_push($response, $row);
                }
            } else {
                $response = "";
            }
        } else {
            $response = "";
        }
        return $response;
    }

    public function getPostById($id)
    {
        $response = "";

        $this->sql = "SELECT * FROM post WHERE id = {$id} LIMIT 1";
        $result    = mysqli_query($this->connect, $this->sql);
        if ($result) {

            if (mysqli_num_rows($result) != 0) {
                $response = mysqli_fetch_assoc($result);
            } else {
                $response = "";
            }
        } else {
            $response = "";
        }
        return $response;
    }

    public function updatePostStatus($id, $status)
    {
        $response = false;

        $this->sql = "UPDATE post SET status='{$status}' WHERE id={$id} LIMIT 1";
        $result    = mysqli_query($this->connect, $this->sql);
        if ($result) {
            $response = true;
        } else {
            $response = false;
        }
        return $response;
    }

    public function getBookingSlots()
    {
        $response = array();

        $this->sql = "SELECT * FROM booking_slots";
        $result    = mysqli_query($this->connect, $this->sql);
        if ($result) {

            if (mysqli_num_rows($result) != 0) {
                while ($row = mysqli_fetch_assoc($result)) {
                    array_push($response, $row);
                }
            } else {
                $response = "";
            }
        } else {
            $response = "";
        }
        return $response;
    }

    public function getBookedSlots($date)
    {
        $response = array();

        $this->sql = "SELECT * FROM bath_house_booking WHERE date = '{$date}'";
        $result    = mysqli_query($this->connect, $this->sql);
        if ($result) {

            if (mysqli_num_rows($result) != 0) {
                while ($row = mysqli_fetch_assoc($result)) {
                    array_push($response, $row);
                }
            } else {
                $response = "";
            }
        } else {
            $response = "";
        }
        return $response;
    }

    public function bookSlot($package_type, $date, $user_id, $booking_slot_id)
    {
        $response = false;

        $sql = "INSERT INTO bath_house_booking (package_type,date,user_id,booking_slot_id ) ";
        $sql .= "VALUES ( '{$package_type}', '{$date}', {$user_id}, {$booking_slot_id} ) ";

        $this->sql = $sql;
        $result    = mysqli_query($this->connect, $this->sql);
        if ($result) {
            $response = true;
        } else {
            $response = false;
        }
        return $response;
    }
}

?>
