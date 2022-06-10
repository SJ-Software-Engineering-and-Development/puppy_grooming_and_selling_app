
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
                $status     = $row['status'];

                if ($dbidno == $idno && $password == $dbpassword) {
                    $response = $status;
                } else {
                    $response = $status;
                }
            } else {
                $response = "login_failed";
            }
        } else {
            $response = "login_failed";
        }

        return $response;
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

    public function updateUserAccountStatus($id, $status)
    {
        $response = "";

        $this->sql = "UPDATE login_user_register SET status='{$status}' WHERE id = {$id} LIMIT 1";
        $result    = mysqli_query($this->connect, $this->sql);
        if ($result) {
            $response = true;
        } else {
            $response = false;
        }
        return $response;
    }

    public function getUsers()
    {
        $response = array();

        $this->sql = "SELECT * FROM login_user_register WHERE `User Type` !='admin' ";
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

    public function submitPost($title, $price, $description, $gender, $age, $date, $location, $imageName, $encodedImage, $status, $breed_id, $owner_id)
    {
        $target_dir = "./uploadedFiles/";

        if (is_dir("./uploadedFiles")) {
            $target_dir = "./uploadedFiles/";
        } else {
            mkdir('./uploadedFiles', 0777, true);
            $target_dir = "./uploadedFiles/";
        }

        $decodedImage = base64_decode("$encodedImage");
        $return       = file_put_contents($target_dir . $imageName . ".JPG", $decodedImage);

        $saveImgName = $imageName . ".JPG";

        if ($return !== false) {
            // upload success
            $sql = "INSERT INTO `post` (`title`, `price`, `description`, `gender`, `age`, `date`, `location`, `imageUrl`, `status`, `breed_id`, `owner_id`) VALUES ";

            $sql .= " ('{$title}', '{$price}', '{$description}', '{$gender}', '{$age}', '{$date}', '{$location}', '{$saveImgName}', '{$status}', {$breed_id}, {$owner_id} ) ";

            $this->sql = $sql;
            $result    = mysqli_query($this->connect, $this->sql);
            if ($result) {
                $response = true;
            } else {
                $response = false;
            }
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

    public function getPostsByUserId($status, $user_id)
    {
        $response = array();

        $this->sql = "SELECT * FROM post WHERE status = '{$status}' AND owner_id={$user_id} ";
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

    public function getBookedSlotsByUserId($date, $login_id)
    {
        $response = array();
        //booking_slots
        // SELECT bath_house_booking.*, booking_slots.from_time, booking_slots.to_time FROM bath_house_booking
        // INNER JOIN booking_slots
        // ON bath_house_booking.booking_slot_id = booking_slots.id

        $sql = "SELECT  bath_house_booking.*, booking_slots.from_time, booking_slots.to_time FROM bath_house_booking ";
        $sql .= " INNER JOIN booking_slots ";
        $sql .= " ON bath_house_booking.booking_slot_id = booking_slots.id ";
        $sql .= " WHERE bath_house_booking.date = '{$date}' AND bath_house_booking.user_id = $login_id ";

        $this->sql = $sql;

        $result = mysqli_query($this->connect, $this->sql);
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

    public function getBookedSlotsByDate($date)
    {
        $response = array();
        //booking_slots
        // SELECT bath_house_booking.*, booking_slots.from_time, booking_slots.to_time FROM bath_house_booking
        // INNER JOIN booking_slots
        // ON bath_house_booking.booking_slot_id = booking_slots.id

        $sql = "SELECT  bath_house_booking.*, booking_slots.from_time, booking_slots.to_time FROM bath_house_booking ";
        $sql .= " INNER JOIN booking_slots ";
        $sql .= " ON bath_house_booking.booking_slot_id = booking_slots.id ";
        $sql .= " WHERE bath_house_booking.date = '{$date}' ";

        $this->sql = $sql;

        $result = mysqli_query($this->connect, $this->sql);
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

    public function addBookingSlot($from_time, $to_time, $description)
    {
        $response = false;

        $sql = "INSERT INTO booking_slots (from_time, to_time, description ) ";
        $sql .= "VALUES ( '{$from_time}', '{$to_time}', '{$description}' ) ";

        $this->sql = $sql;
        $result    = mysqli_query($this->connect, $this->sql);
        if ($result) {
            $response = true;
        } else {
            $response = false;
        }
        return $response;
    }

    public function deleteBookingSlot($id)
    {
        $response = false;

        $sql = "DELETE FROM booking_slots WHERE id = {$id} ";

        $this->sql = $sql;
        $result    = mysqli_query($this->connect, $this->sql);
        if ($result) {
            $response = true;
        } else {
            $response = false;
        }
        return $response;
    }

    public function addVeterinary(
        $title,
        $city,
        $address,
        $contact,
        $open_close_times,
        $longitude,
        $latitude) {

        $title            = $this->prepareData($title);
        $city             = $this->prepareData($city);
        $address          = $this->prepareData($address);
        $contact          = $this->prepareData($contact);
        $open_close_times = $this->prepareData($open_close_times);
        $longitude        = $this->prepareData($longitude);
        $latitude         = $this->prepareData($latitude);

        $sql = "INSERT INTO `veterinary` (`title`,`city`,`address`,`contact`,`open_close_times`,`longitude`,`latitude`) VALUES ";
        $sql .= "('{$title}','{$city}','{$address}','{$contact}','{$open_close_times}','{$longitude}','{$latitude}' ) ";

        $this->sql = $sql;

        if (mysqli_query($this->connect, $this->sql)) {
            return true;
        } else {
            return false;
        }

    }

    public function getVeterinaries()
    {
        $response = array();

        $sql       = "SELECT  * FROM veterinary ";
        $this->sql = $sql;

        $result = mysqli_query($this->connect, $this->sql);
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

    public function getVeterinaryByCity($city)
    {
        $response = array();

        $sql = "";
        if ($city == "") {
            $sql = "SELECT * FROM veterinary";
        } else {
            $sql = "SELECT * FROM veterinary WHERE city = '{$city}' ";
        }
        $this->sql = $sql;

        $result = mysqli_query($this->connect, $this->sql);
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

    public function getVeterinaryById($id)
    {
        $response = "";

        $sql = "SELECT  * FROM veterinary WHERE id = {$id} LIMIT 1";

        $this->sql = $sql;

        $result = mysqli_query($this->connect, $this->sql);
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

    public function getBreeds()
    {
        $response = array();

        $this->sql = "SELECT * FROM dog_breed";
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

    public function addVideo($url)
    {
        $sql = "INSERT INTO `youtube_video` (`url`,`title`,`published_time`,`duration`,`views`,`likes`,`comments`) VALUES ";
        $sql .= " ('{$url}', '-', '-', '-',0,0,0) ";

        $this->sql = $sql;
        $result    = mysqli_query($this->connect, $this->sql);
        if ($result) {
            $response = true;
        } else {
            $response = false;
        }

        return $response;
    }

    public function deleteVideo($id)
    {
        $sql = "DELETE FROM youtube_video WHERE id= {$id} LIMIT 1";

        $this->sql = $sql;
        $result    = mysqli_query($this->connect, $this->sql);
        if ($result) {
            $response = true;
        } else {
            $response = false;
        }

        return $response;
    }

    public function updateStatistics($id, $data)
    {
        $title        = $data->items['0']->snippet->title;
        $publishedAt  = $data->items['0']->snippet->publishedAt;
        $duration     = $data->items['0']->contentDetails->duration;
        $viewCount    = $data->items['0']->statistics->viewCount;
        $likeCount    = $data->items['0']->statistics->likeCount;
        $commentCount = $data->items['0']->statistics->commentCount;

        $sql = "UPDATE youtube_video SET ";
        $sql .= "title = '{$title}', ";
        $sql .= "published_time = '{$publishedAt}', ";
        $sql .= "duration = '{$duration}', ";
        $sql .= "views = {$viewCount}, ";
        $sql .= "likes = {$likeCount}, ";
        $sql .= "comments = {$commentCount} ";
        $sql .= "WHERE id = {$id}";

        $this->sql = $sql;
        $result    = mysqli_query($this->connect, $this->sql);
        if ($result) {
            $response = true;
        } else {
            $response = false;
        }
        if ($data != "") {

        }
        // return $response;
    }

    public function getStatistics($id, $url)
    {
        $queryString = parse_url($url, PHP_URL_QUERY);
        parse_str($queryString, $params);

        if (isset($params['v']) && strlen($params['v']) > 0) {
            $vidEoID = $params['v'];

            $api_key = "AIzaSyDFliJ2_ca04wz6Y5cM3vLqM9nxrFyOG-o"; //your API Key
            $api_ur  = 'https://www.googleapis.com/youtube/v3/videos?part=snippet%2CcontentDetails%2Cstatistics&id=' . $vidEoID . '&key=' . $api_key;

            $data = json_decode(file_get_contents($api_ur));

            $this->updateStatistics($id, $data);
            // return $data;
        } else {
            // return "";
        }
    }

    public function getVideos()
    {
        $response = array();

        $this->sql = "SELECT * FROM youtube_video";
        $result    = mysqli_query($this->connect, $this->sql);
        if ($result) {
            if (mysqli_num_rows($result) != 0) {
                while ($row = mysqli_fetch_assoc($result)) {
                    $this->getStatistics($row['id'], $row['url']);
                }
                //....................
                $this->sql = "SELECT * FROM youtube_video";
                $result_2  = mysqli_query($this->connect, $this->sql);
                if ($result_2) {
                    if (mysqli_num_rows($result_2) != 0) {
                        while ($row = mysqli_fetch_assoc($result_2)) {
                            array_push($response, $row);
                        }
                    }
                }
            }
        }
        return $response;
    }

    public function getMaxStatus()
    {
        $response = array("max_likes_video" => "", "max_views_video" => "");

        $this->sql = "SELECT * FROM youtube_video";
        $result    = mysqli_query($this->connect, $this->sql);
        if ($result) {
            if (mysqli_num_rows($result) != 0) {

                //max likes
                $this->sql = "SELECT * from youtube_video WHERE likes = (SELECT MAX(likes) FROM youtube_video LIMIT 1)";
                $result_2  = mysqli_query($this->connect, $this->sql);
                if ($result_2) {
                    if (mysqli_num_rows($result_2) != 0) {
                        $row                         = mysqli_fetch_assoc($result_2);
                        $response['max_likes_video'] = $row;
                    }
                }

                //max views
                $this->sql = "SELECT * from youtube_video WHERE views = (SELECT MAX(views) FROM youtube_video LIMIT 1)";
                $result_3  = mysqli_query($this->connect, $this->sql);
                if ($result_3) {
                    if (mysqli_num_rows($result_3) != 0) {
                        $row                         = mysqli_fetch_assoc($result_3);
                        $response['max_views_video'] = $row;
                    }
                }
            }
        }
        return $response;
    }

    public function getMostDemandBreed()
    {
        $response = array("max_breed_id" => 0, "max_breed_name" => "", "no_of_post" => 0);

        $sql       = "SELECT breed_id, COUNT(breed_id) as count FROM post GROUP BY breed_id";
        $this->sql = $sql;
        $result    = mysqli_query($this->connect, $this->sql);
        if ($result) {
            if (mysqli_num_rows($result) != 0) {
                $max_breed_id = 0;
                $max_count    = 0; // intval() => convert string to int
                while ($row = mysqli_fetch_assoc($result)) {
                    if (intval($row['count']) > $max_count) {
                        $max_count    = intval($row['count']);
                        $max_breed_id = intval($row['breed_id']);
                    }
                }
                $response['max_breed_id'] = $max_breed_id;
                $response['no_of_post']   = $max_count;
                //  $response = "{'breed_id':" . $max_breed_id . ",'no_of_post':" . $max_count . "}";
                // $response = mysqli_fetch_assoc($result);
                $sql       = "SELECT breed FROM dog_breed WHERE id= $max_breed_id"; //
                $this->sql = $sql;
                $result_3  = mysqli_query($this->connect, $this->sql);
                if ($result_3) {
                    $row                        = mysqli_fetch_assoc($result_3);
                    $response['max_breed_name'] = $row['breed'];
                }
            } else {
                $response = "";
            }
        } else {
            $response = "";
        }
        return $response;
    }

    public function getPostCountStatistics()
    {
        $response = array("total_online" => 0, "total_pending" => 0);

        $this->sql = "SELECT count(id) as total_online FROM post WHERE status='active'";
        $result    = mysqli_query($this->connect, $this->sql);

        if ($result) {

            if (mysqli_num_rows($result) != 0) {
                $row                      = mysqli_fetch_assoc($result);
                $response['total_online'] = $row['total_online'];
            }

            $this->sql = "SELECT count(id) as total_pending FROM post WHERE status='pending'";
            $result    = mysqli_query($this->connect, $this->sql);
            if ($result) {
                if (mysqli_num_rows($result) != 0) {
                    $row                       = mysqli_fetch_assoc($result);
                    $response['total_pending'] = $row['total_pending'];
                }
            }
        }
        return $response;
    }

}

?>
