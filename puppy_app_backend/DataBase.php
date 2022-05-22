
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
        $this->connect = null;
        $this->data = null;
        $this->sql = null;
        $dbc = new DataBaseConfig();
        $this->servername = $dbc->servername;
        $this->username = $dbc->username;
        $this->password = $dbc->password;
        $this->databasename = $dbc->databasename;
    }

    function dbConnect()
    {
        $this->connect = mysqli_connect($this->servername, $this->username, $this->password, $this->databasename);
        return $this->connect;
    }

    function prepareData($data)
    {
        return mysqli_real_escape_string($this->connect, stripslashes(htmlspecialchars($data)));
    }

    function logIn($user, $idno, $password)
    {
        $idno = $this->prepareData($idno);
        $password = $this->prepareData($password);

      //  $this->sql = "select * from " .`login_user_register` . " where `Nic Number`= '" . $idno . "'";
      $this->sql = "select * from login_user_register  where `Nic Number`= '{$idno}'";
      $result = mysqli_query($this->connect, $this->sql);
        if($result){
            $row = mysqli_fetch_assoc($result);
            if (mysqli_num_rows($result) != 0) {
                $dbidno = $row['Nic Number'];
                $dbpassword = $row['Password'];

                if ($dbidno == $idno && $password == $dbpassword) {
                    $login = true;
                } else{
                    $login = false;
                }
            } else {
                $login = false;
            }
        }else{
            $login = false;
        }

        return $login;
    }


    function signUp(
    $FullName,
    $city,
    $NICNumber,  
    $emausertype,
    $email,
    $contactno,
    $password)
    {
        $FullName = $this->prepareData($FullName);
        $NICNumber = $this->prepareData($NICNumber);
        $city = $this->prepareData($city);
        $emausertype = $this->prepareData($emausertype);
        $email = $this->prepareData($email);
        $contactno = $this->prepareData($contactno);
        $password = $this->prepareData($password);
        
        $this->sql =
            " INSERT INTO `login_user_register` (`Full Name`, `NIC Number`, `City`, `User Type`, `E-mail`, `Contact No`, `Password`) VALUES('" . $FullName . "','" . $NICNumber . "','" . $city . "','" . $emausertype . "', '". $email . "', ". $contactno . ",'". $password ."')";
        if (mysqli_query($this->connect, $this->sql)) {
            return true;
        } else return false;
    }


    function Booking(
        $PackageType,
        $Date,
        $Time,  
        $NicNumber)
        {
            $PackageType = $this->prepareData($PackageType);
            $Date = $this->prepareData($Date);
            $Time = $this->prepareData($Time);
            $NicNumber = $this->prepareData($NicNumber);
            
            $this->sql =
                " INSERT INTO `bath_house_booking` (`Package Type`, `Date`, `Time`,  `Nic Number`) VALUES('" . $PackageType . "','" . $Date . "','" . $Time . "','". $NicNumber ."')";
            if (mysqli_query($this->connect, $this->sql)) {
                return true;
            } else return false;
        }



















}

?>
