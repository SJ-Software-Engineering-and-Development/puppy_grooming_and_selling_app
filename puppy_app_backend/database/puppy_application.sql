-- phpMyAdmin SQL Dump
-- version 5.1.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: May 21, 2022 at 07:02 PM
-- Server version: 10.4.21-MariaDB
-- PHP Version: 8.0.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `puppy_application`
--

-- --------------------------------------------------------

--
-- Table structure for table `bath_house_booking`
--

CREATE TABLE `bath_house_booking` (
  `Package Type` varchar(100) NOT NULL,
  `Date` varchar(100) NOT NULL,
  `Time` varchar(100) NOT NULL,
  `Nic Number` varchar(30) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `bath_house_booking`
--

INSERT INTO `bath_house_booking` (`Package Type`, `Date`, `Time`, `Nic Number`) VALUES
('Full Groom(Rs.4900)', '05/06/22', '2:24 AM', '444'),
('Bath and Dry(Rs.2000)', '05/20/22', '1:35 PM', '444'),
('Basic Groom(Rs.3500)', '05/21/22', '4:34 AM', '444'),
('Full Groom(Rs.4900)', '05/20/22', '9:35 AM', '973240315');

-- --------------------------------------------------------

--
-- Table structure for table `login_user_register`
--

CREATE TABLE `login_user_register` (
  `Full Name` varchar(100) DEFAULT NULL,
  `Nic Number` varchar(30) NOT NULL,
  `City` varchar(50) DEFAULT NULL,
  `User Type` varchar(20) DEFAULT NULL,
  `E-mail` varchar(100) NOT NULL,
  `Contact No` int(10) DEFAULT NULL,
  `Password` varchar(10) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `login_user_register`
--

INSERT INTO `login_user_register` (`Full Name`, `Nic Number`, `City`, `User Type`, `E-mail`, `Contact No`, `Password`) VALUES
('asdjnhj', '444', 'sdfr', 'fythj', 'fyguh', 888, 'ggg'),
('shanuka', '973240315', 'panadura', 'seller', 'shanuka_h@gmail.com', 760776981, '123a');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `login_user_register`
--
ALTER TABLE `login_user_register`
  ADD PRIMARY KEY (`E-mail`),
  ADD UNIQUE KEY `Nic Number` (`Nic Number`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
