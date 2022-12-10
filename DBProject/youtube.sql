-- MySQL dump 10.13  Distrib 8.0.31, for Win64 (x86_64)
--
-- Host: localhost    Database: youtube
-- ------------------------------------------------------
-- Server version	8.0.31

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `genre`
--
DROP DATABASE IF EXISTS `youtube`;
CREATE DATABASE `youtube`;
USE `youtube`;

DROP TABLE IF EXISTS `genre`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `genre` (
  `genre_index` int NOT NULL,
  `genre_name` varchar(50) NOT NULL,
  PRIMARY KEY (`genre_index`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `genre`
--

LOCK TABLES `genre` WRITE;
/*!40000 ALTER TABLE `genre` DISABLE KEYS */;
INSERT INTO `genre` VALUES (0,'Romance'),(1,'Action'),(2,'Comedy'),(3,'Horror'),(4,'Noir'),(5,'Kids'),(6,'Mukbang');
/*!40000 ALTER TABLE `genre` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `genre_of`
--

DROP TABLE IF EXISTS `genre_of`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `genre_of` (
  `vid_id` int NOT NULL,
  `gen_idx` int NOT NULL,
  PRIMARY KEY (`vid_id`,`gen_idx`),
  KEY `genre_of_ibfk_2` (`gen_idx`),
  CONSTRAINT `genre_of_ibfk_1` FOREIGN KEY (`vid_id`) REFERENCES `video` (`video_id`),
  CONSTRAINT `genre_of_ibfk_2` FOREIGN KEY (`gen_idx`) REFERENCES `genre` (`genre_index`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `genre_of`
--

LOCK TABLES `genre_of` WRITE;
/*!40000 ALTER TABLE `genre_of` DISABLE KEYS */;
INSERT INTO `genre_of` VALUES (0,0),(1,0),(3,0),(4,0),(2,1),(3,1),(2,2),(3,2),(5,5),(6,6);
/*!40000 ALTER TABLE `genre_of` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `includes`
--

DROP TABLE IF EXISTS `includes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `includes` (
  `playlist_master` varchar(50) NOT NULL,
  `playlist_name` varchar(50) NOT NULL,
  `vid_id` int NOT NULL,
  `includes_index` int NOT NULL,
  PRIMARY KEY (`playlist_master`,`playlist_name`,`vid_id`),
  KEY `includes_ibfk_2` (`vid_id`),
  CONSTRAINT `includes_ibfk_1` FOREIGN KEY (`playlist_master`, `playlist_name`) REFERENCES `playlist` (`Master_id`, `Listname`),
  CONSTRAINT `includes_ibfk_2` FOREIGN KEY (`vid_id`) REFERENCES `video` (`video_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `includes`
--

LOCK TABLES `includes` WRITE;
/*!40000 ALTER TABLE `includes` DISABLE KEYS */;
/*!40000 ALTER TABLE `includes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `manager`
--

DROP TABLE IF EXISTS `manager`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `manager` (
  `Name` varchar(50) NOT NULL,
  `Index` int NOT NULL,
  `Sex` varchar(5) NOT NULL,
  `Ssn` int NOT NULL,
  `Age` int NOT NULL,
  PRIMARY KEY (`Ssn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `manager`
--

LOCK TABLES `manager` WRITE;
/*!40000 ALTER TABLE `manager` DISABLE KEYS */;
INSERT INTO `manager` VALUES ('Jaeguk',0,'M',12345,23);
/*!40000 ALTER TABLE `manager` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mgr_phonenumber`
--

DROP TABLE IF EXISTS `mgr_phonenumber`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mgr_phonenumber` (
  `Mgr_ssn` int NOT NULL,
  `Mgr_phonenum` varchar(50) NOT NULL,
  PRIMARY KEY (`Mgr_ssn`),
  CONSTRAINT `mgr_phonenumber_ibfk_1` FOREIGN KEY (`Mgr_ssn`) REFERENCES `manager` (`Ssn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mgr_phonenumber`
--

LOCK TABLES `mgr_phonenumber` WRITE;
/*!40000 ALTER TABLE `mgr_phonenumber` DISABLE KEYS */;
INSERT INTO `mgr_phonenumber` VALUES (12345,'01048036722');
/*!40000 ALTER TABLE `mgr_phonenumber` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `playlist`
--

DROP TABLE IF EXISTS `playlist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `playlist` (
  `Master_id` varchar(50) NOT NULL,
  `Listname` varchar(50) NOT NULL,
  `is_shared` tinyint(1) NOT NULL,
  `list_index` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`Master_id`,`Listname`),
  CONSTRAINT `playlist_ibfk_1` FOREIGN KEY (`Master_id`) REFERENCES `user` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `playlist`
--

LOCK TABLES `playlist` WRITE;
/*!40000 ALTER TABLE `playlist` DISABLE KEYS */;
/*!40000 ALTER TABLE `playlist` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `ID` varchar(50) NOT NULL,
  `Password` varchar(50) NOT NULL,
  `ChannelName` varchar(50) DEFAULT NULL,
  `Age` int NOT NULL,
  `Sex` varchar(5) DEFAULT NULL,
  `Address` varchar(100) DEFAULT NULL,
  `Email` varchar(50) NOT NULL,
  `PhoneNumber` varchar(50) NOT NULL,
  `Mgr_ssn` int NOT NULL DEFAULT '12345',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `ChannelName` (`ChannelName`),
  KEY `user_ibfk_1` (`Mgr_ssn`),
  CONSTRAINT `user_ibfk_1` FOREIGN KEY (`Mgr_ssn`) REFERENCES `manager` (`Ssn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES ('jk6722','qmffhdn33!',NULL,23,'M','Seoul','jk6722@naver.com','01048036722',12345),('John','12345','JohnTV',21,'M','LA','john@hanyang.ac.kr','01042032021',12345),('movie','12345','GoodMovie',25,'F','Seoul','movie@hanyang.ac.kr','01042123011',12345);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `video`
--

DROP TABLE IF EXISTS `video`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `video` (
  `Title` varchar(100) NOT NULL,
  `video_id` int NOT NULL,
  `length` varchar(50) NOT NULL,
  `like_count` int NOT NULL DEFAULT '0',
  `uploader_id` varchar(50) NOT NULL,
  `view_count` int NOT NULL DEFAULT '0',
  `video_index` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`video_id`),
  UNIQUE KEY `video_index` (`video_index`),
  KEY `video_ibfk_1` (`uploader_id`),
  CONSTRAINT `video_ibfk_1` FOREIGN KEY (`uploader_id`) REFERENCES `user` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `video`
--

LOCK TABLES `video` WRITE;
/*!40000 ALTER TABLE `video` DISABLE KEYS */;
INSERT INTO `video` VALUES ('Titanic',0,'1:25:30',0,'movie',0,0),('About time',1,'1:30:15',0,'movie',0,1),('Avengers',2,'1:22:58',0,'movie',0,2),('Spiderman',3,'1:15:43',0,'movie',0,3),('Love Rosie',4,'1:40:01',0,'movie',0,4),('Pororo',5,'48:20',0,'movie',0,5),('Chicken Mukbang',6,'20:15',0,'John',0,6);
/*!40000 ALTER TABLE `video` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `watches`
--

DROP TABLE IF EXISTS `watches`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `watches` (
  `uid` varchar(50) NOT NULL,
  `vid` int NOT NULL,
  PRIMARY KEY (`uid`,`vid`),
  KEY `watches_ibfk_2` (`vid`),
  CONSTRAINT `watches_ibfk_1` FOREIGN KEY (`uid`) REFERENCES `user` (`ID`),
  CONSTRAINT `watches_ibfk_2` FOREIGN KEY (`vid`) REFERENCES `video` (`video_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `watches`
--

LOCK TABLES `watches` WRITE;
/*!40000 ALTER TABLE `watches` DISABLE KEYS */;
/*!40000 ALTER TABLE `watches` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `watches_genre`
--

DROP TABLE IF EXISTS `watches_genre`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `watches_genre` (
  `user_id` varchar(50) NOT NULL,
  `gen_idx` int NOT NULL,
  `gen_count` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`user_id`,`gen_idx`),
  KEY `watches_genre_ibfk_2` (`gen_idx`),
  CONSTRAINT `watches_genre_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`ID`),
  CONSTRAINT `watches_genre_ibfk_2` FOREIGN KEY (`gen_idx`) REFERENCES `genre` (`genre_index`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `watches_genre`
--

LOCK TABLES `watches_genre` WRITE;
/*!40000 ALTER TABLE `watches_genre` DISABLE KEYS */;
/*!40000 ALTER TABLE `watches_genre` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-12-08 15:48:07
