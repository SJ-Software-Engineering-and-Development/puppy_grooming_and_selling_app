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


CREATE TABLE `login_user_register` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `Full Name` varchar(100) DEFAULT NULL,
  `Nic Number` varchar(30) NOT NULL,
  `City` varchar(50) DEFAULT NULL,
  `User Type` varchar(20) DEFAULT NULL,
  `E-mail` varchar(100) NOT NULL,
  `Contact No` int(10) DEFAULT NULL,
  `Password` varchar(10) DEFAULT NULL,
   UNIQUE KEY `Nic Number` (`Nic Number`),
   UNIQUE KEY `E-mail` (`E-mail`),
   PRIMARY KEY (`id`)
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

CREATE TABLE `post` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(100) DEFAULT NULL,
  `price` varchar(100) DEFAULT NULL,
  `description` varchar(250) DEFAULT NULL,
  `gender` varchar(100) DEFAULT NULL,
  `age` varchar(100) DEFAULT NULL,
  `date` varchar(100) DEFAULT NULL,
  `location` varchar(100) DEFAULT NULL,
  `imageUrl` varchar(250) DEFAULT NULL,
  `status` enum('pending','active','denied','deactive','expired') NOT NULL,
  `owner_id` int(11) NOT NULL,
   PRIMARY KEY (`id`),
   FOREIGN KEY (`owner_id`) REFERENCES `login_user_register` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `booking_slots` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `from_time` varchar(10) DEFAULT NULL,
  `to_time` varchar(10) DEFAULT NULL,
  `description` varchar(100) DEFAULT NULL,
   PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE `bath_house_booking` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `package_type` varchar(100) NOT NULL,
  `date` DATE NOT NULL,
  `user_id` int(11) NOT NULL,
  `booking_slot_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`user_id`) REFERENCES `login_user_register` (`id`),
  FOREIGN KEY (`booking_slot_id`) REFERENCES `booking_slots` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;