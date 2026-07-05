-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               8.0.33 - MySQL Community Server - GPL
-- Server OS:                    Win64
-- HeidiSQL Version:             12.4.0.6659
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dumping database structure for techmart_online
CREATE DATABASE IF NOT EXISTS `techmart_online` /*!40100 DEFAULT CHARACTER SET utf8mb3 */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `techmart_online`;

-- Dumping structure for table techmart_online.admin
CREATE TABLE IF NOT EXISTS `admin` (
  `admin_id` int NOT NULL AUTO_INCREMENT,
  `email` varchar(100) NOT NULL,
  `password` varchar(100) NOT NULL,
  PRIMARY KEY (`admin_id`),
  UNIQUE KEY `email_UNIQUE` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb3;

-- Dumping data for table techmart_online.admin: ~1 rows (approximately)
INSERT INTO `admin` (`admin_id`, `email`, `password`) VALUES
	(1, 'admin@gmail.com', 'test=123');

-- Dumping structure for table techmart_online.cart
CREATE TABLE IF NOT EXISTS `cart` (
  `cart_id` int NOT NULL AUTO_INCREMENT,
  `customer_id` int NOT NULL,
  `status` varchar(20) NOT NULL DEFAULT 'ACTIVE',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`cart_id`),
  UNIQUE KEY `customer_customer_id_UNIQUE` (`customer_id`),
  KEY `fk_cart_customer1_idx` (`customer_id`),
  CONSTRAINT `fk_cart_customer1` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb3;

-- Dumping data for table techmart_online.cart: ~7 rows (approximately)
INSERT INTO `cart` (`cart_id`, `customer_id`, `status`, `updated_at`) VALUES
	(1, 1, 'CHECKED_OUT', '2026-07-04 08:25:00'),
	(2, 2, 'CHECKED_OUT', '2026-06-30 17:39:13'),
	(3, 3, 'ACTIVE', '2026-06-21 15:12:36'),
	(4, 4, 'ACTIVE', '2026-06-21 15:12:36'),
	(5, 5, 'ACTIVE', '2026-06-21 15:12:36'),
	(6, 6, 'ACTIVE', '2026-06-21 15:12:36'),
	(8, 7, 'ACTIVE', '2026-06-28 17:33:11');

-- Dumping structure for table techmart_online.cart_item
CREATE TABLE IF NOT EXISTS `cart_item` (
  `cart_item_id` int NOT NULL AUTO_INCREMENT,
  `cart_id` int NOT NULL,
  `product_id` int NOT NULL,
  `quantity` int NOT NULL DEFAULT '1',
  PRIMARY KEY (`cart_item_id`),
  UNIQUE KEY `uq_cart_item_cart_product` (`cart_id`,`product_id`) /*!80000 INVISIBLE */,
  KEY `fk_cart_item_cart1_idx` (`cart_id`),
  KEY `fk_cart_item_product1_idx` (`product_id`) /*!80000 INVISIBLE */,
  CONSTRAINT `fk_cart_item_cart1` FOREIGN KEY (`cart_id`) REFERENCES `cart` (`cart_id`),
  CONSTRAINT `fk_cart_item_product1` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb3;

-- Dumping data for table techmart_online.cart_item: ~6 rows (approximately)
INSERT INTO `cart_item` (`cart_item_id`, `cart_id`, `product_id`, `quantity`) VALUES
	(3, 3, 7, 2),
	(4, 4, 2, 1),
	(5, 5, 8, 1),
	(6, 6, 9, 1),
	(27, 2, 6, 2),
	(28, 1, 2, 1);

-- Dumping structure for table techmart_online.category
CREATE TABLE IF NOT EXISTS `category` (
  `category_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`category_id`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb3;

-- Dumping data for table techmart_online.category: ~6 rows (approximately)
INSERT INTO `category` (`category_id`, `name`, `description`) VALUES
	(1, 'Gaming Consoles', 'Home and handheld video game consoles like PlayStation, Xbox, and Nintendo Switch.'),
	(2, 'PC Gaming', 'High-performance gaming desktops, laptops, components, and dedicated peripherals.'),
	(3, 'Video Games', 'Physical and digital games across all major platforms and genres.'),
	(4, 'Gaming Accessories', 'Controllers, headsets, gaming mice, keyboards, and streaming gear.'),
	(5, 'Virtual Reality', 'VR headsets, motion controllers, and immersive gaming accessories.'),
	(6, 'Home Entertainment', 'Smart TVs, soundbars, projectors, and media streaming devices.');

-- Dumping structure for table techmart_online.customer
CREATE TABLE IF NOT EXISTS `customer` (
  `customer_id` int NOT NULL AUTO_INCREMENT,
  `first_name` varchar(50) NOT NULL,
  `last_name` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password_hash` varchar(100) NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`customer_id`),
  UNIQUE KEY `email_UNIQUE` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb3;

-- Dumping data for table techmart_online.customer: ~7 rows (approximately)
INSERT INTO `customer` (`customer_id`, `first_name`, `last_name`, `email`, `password_hash`, `created_at`) VALUES
	(1, 'Keanu', 'Reeves', 'keanu.reeves@cybermail.com', 'Keanu123', '2026-06-21 15:11:55'),
	(2, 'Taylor', 'Swift', 'taylor.swift@erastour.com', 'Taylor123', '2026-06-21 15:11:55'),
	(3, 'Ryan', 'Reynolds', 'ryan@deadpool.com', 'Ryan123', '2026-06-21 15:11:55'),
	(4, 'Margot', 'Robbie', 'margot@barbieworld.io', 'Margot123', '2026-06-21 15:11:55'),
	(5, 'Pedro', 'Pascal', 'pedro.pascal@pedromail.com', 'Pedro123', '2026-06-21 15:11:55'),
	(6, 'Tom', 'Holland', 'tom.holland@spideyweb.net', 'Tom123', '2026-06-21 15:11:55'),
	(7, 'Lord', 'Dracula', 'dracula@gmail.com', 'DraculaTrans2026', '2026-06-28 17:33:05');

-- Dumping structure for table techmart_online.customer_order
CREATE TABLE IF NOT EXISTS `customer_order` (
  `order_id` int NOT NULL AUTO_INCREMENT,
  `customer_id` int NOT NULL,
  `status` varchar(20) NOT NULL DEFAULT 'PENDING',
  `total_amount` decimal(10,2) NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`order_id`),
  KEY `fk_customer_order_customer1_idx` (`customer_id`),
  CONSTRAINT `fk_customer_order_customer1` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb3;

-- Dumping data for table techmart_online.customer_order: ~17 rows (approximately)
INSERT INTO `customer_order` (`order_id`, `customer_id`, `status`, `total_amount`, `created_at`) VALUES
	(1, 1, 'CONFIRMED', 1008900.00, '2026-06-21 15:16:31'),
	(2, 2, 'SHIPPED', 113900.00, '2026-06-21 15:16:31'),
	(3, 3, 'DELIVERED', 525000.00, '2026-06-21 15:16:31'),
	(4, 4, 'PENDING', 23500.00, '2026-06-21 15:16:31'),
	(5, 5, 'DELIVERED', 795000.00, '2026-06-21 15:16:31'),
	(6, 6, 'CANCELLED', 179900.00, '2026-06-21 15:16:31'),
	(7, 1, 'PENDING', 2649500.00, '2026-06-22 10:27:37'),
	(8, 1, 'PENDING', 49000.00, '2026-06-22 10:37:36'),
	(9, 1, 'PENDING', 24500.00, '2026-06-22 10:46:11'),
	(10, 1, 'PENDING', 113900.00, '2026-06-22 10:55:38'),
	(11, 1, 'PENDING', 113900.00, '2026-06-22 11:00:35'),
	(12, 1, 'PENDING', 339000.00, '2026-06-22 12:49:20'),
	(13, 1, 'CANCELLED', 124000.00, '2026-06-26 13:00:55'),
	(15, 1, 'PENDING', 26500.00, '2026-06-30 16:53:35'),
	(16, 2, 'PENDING', 669900.00, '2026-06-30 17:37:31'),
	(17, 2, 'PENDING', 47000.00, '2026-06-30 17:39:13'),
	(18, 1, 'PENDING', 113900.00, '2026-07-04 08:25:00');

-- Dumping structure for table techmart_online.inventory
CREATE TABLE IF NOT EXISTS `inventory` (
  `inventory_id` int NOT NULL AUTO_INCREMENT,
  `product_id` int NOT NULL,
  `warehouse_id` int NOT NULL,
  `quantity_on_hand` int NOT NULL DEFAULT '0',
  `reorder_threshold` int NOT NULL DEFAULT '5',
  `last_updated` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`inventory_id`),
  UNIQUE KEY `uq_inventory_product_warehouse` (`product_id`,`warehouse_id`),
  KEY `fk_inventory_product1_idx` (`product_id`),
  KEY `fk_inventory_warehouse1_idx` (`warehouse_id`),
  CONSTRAINT `fk_inventory_product1` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`),
  CONSTRAINT `fk_inventory_warehouse1` FOREIGN KEY (`warehouse_id`) REFERENCES `warehouse` (`warehouse_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb3;

-- Dumping data for table techmart_online.inventory: ~10 rows (approximately)
INSERT INTO `inventory` (`inventory_id`, `product_id`, `warehouse_id`, `quantity_on_hand`, `reorder_threshold`, `last_updated`) VALUES
	(1, 1, 1, 49, 5, '2026-06-22 12:49:20'),
	(2, 2, 2, 31, 5, '2026-07-04 08:25:00'),
	(3, 3, 1, 10, 5, '2026-06-30 10:59:49'),
	(4, 4, 5, 18, 5, '2026-06-21 15:09:20'),
	(5, 7, 3, 14, 5, '2026-06-22 10:46:11'),
	(6, 10, 6, 14, 5, '2026-06-30 17:37:31'),
	(7, 5, 2, 3, 5, '2026-06-30 16:53:35'),
	(8, 6, 4, 4, 5, '2026-06-30 17:39:13'),
	(9, 8, 4, 8, 5, '2026-06-26 13:00:56'),
	(10, 9, 6, 15, 5, '2026-06-22 12:00:20');

-- Dumping structure for table techmart_online.notification
CREATE TABLE IF NOT EXISTS `notification` (
  `notification_id` int NOT NULL AUTO_INCREMENT,
  `customer_id` int DEFAULT NULL,
  `type` varchar(30) NOT NULL,
  `message` varchar(500) NOT NULL,
  `sent_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`notification_id`),
  KEY `fk_notification_customer1_idx` (`customer_id`),
  CONSTRAINT `fk_notification_customer1` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb3;

-- Dumping data for table techmart_online.notification: ~4 rows (approximately)
INSERT INTO `notification` (`notification_id`, `customer_id`, `type`, `message`, `sent_at`) VALUES
	(1, 2, 'ORDER_CONFIRMATION', 'Your TechMart order #16 has been confirmed.', '2026-06-30 17:37:34'),
	(2, NULL, 'LOW_STOCK', 'LOW STOCK ALERT: productId=6 has only 4 units remaining.', '2026-06-30 17:39:13'),
	(3, 2, 'ORDER_CONFIRMATION', 'Your TechMart order #17 has been confirmed.', '2026-06-30 17:39:16'),
	(4, 1, 'ORDER_CONFIRMATION', 'Your TechMart order #18 has been confirmed.', '2026-07-04 08:25:03');

-- Dumping structure for table techmart_online.order_item
CREATE TABLE IF NOT EXISTS `order_item` (
  `order_item_id` int NOT NULL AUTO_INCREMENT,
  `order_id` int NOT NULL,
  `product_id` int NOT NULL,
  `quantity` int NOT NULL,
  `unit_price` decimal(10,2) NOT NULL,
  PRIMARY KEY (`order_item_id`),
  KEY `fk_order_item_customer_order1_idx` (`order_id`),
  KEY `fk_order_item_product1_idx` (`product_id`),
  CONSTRAINT `fk_order_item_customer_order1` FOREIGN KEY (`order_id`) REFERENCES `customer_order` (`order_id`),
  CONSTRAINT `fk_order_item_product1` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb3;

-- Dumping data for table techmart_online.order_item: ~19 rows (approximately)
INSERT INTO `order_item` (`order_item_id`, `order_id`, `product_id`, `quantity`, `unit_price`) VALUES
	(1, 1, 1, 1, 339000.00),
	(2, 1, 10, 1, 669900.00),
	(3, 2, 2, 1, 113900.00),
	(4, 3, 3, 1, 525000.00),
	(5, 4, 6, 1, 23500.00),
	(6, 5, 4, 1, 795000.00),
	(7, 6, 9, 1, 179900.00),
	(8, 7, 7, 1, 24500.00),
	(9, 7, 3, 5, 525000.00),
	(10, 8, 7, 2, 24500.00),
	(11, 9, 7, 1, 24500.00),
	(12, 10, 2, 1, 113900.00),
	(13, 11, 2, 1, 113900.00),
	(14, 12, 1, 1, 339000.00),
	(15, 13, 8, 2, 62000.00),
	(17, 15, 5, 1, 26500.00),
	(18, 16, 10, 1, 669900.00),
	(19, 17, 6, 2, 23500.00),
	(20, 18, 2, 1, 113900.00);

-- Dumping structure for table techmart_online.product
CREATE TABLE IF NOT EXISTS `product` (
  `product_id` int NOT NULL AUTO_INCREMENT,
  `category_id` int NOT NULL,
  `name` varchar(100) NOT NULL,
  `brand` varchar(50) NOT NULL,
  `description` text,
  `base_price` decimal(10,2) NOT NULL,
  `image_path` varchar(255) NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `active` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`product_id`),
  KEY `fk_product_category_idx` (`category_id`),
  CONSTRAINT `fk_product_category` FOREIGN KEY (`category_id`) REFERENCES `category` (`category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb3;

-- Dumping data for table techmart_online.product: ~10 rows (approximately)
INSERT INTO `product` (`product_id`, `category_id`, `name`, `brand`, `description`, `base_price`, `image_path`, `created_at`, `active`) VALUES
	(1, 1, 'PlayStation 5 Pro', 'Sony', 'High-performance home video game console with advanced ray tracing.', 339000.00, 'images/products/1.jpg', '2026-06-20 23:33:07', 1),
	(2, 1, 'Nintendo Switch OLED', 'Nintendo', 'Handheld and home hybrid console with a vibrant OLED screen.', 113900.00, 'images/products/2.jpg', '2026-06-20 23:33:07', 1),
	(3, 2, 'Apex Alpha Gaming Desktop', 'TechMart', 'Pre-built gaming PC with RTX 4070, 32GB RAM, and 1TB NVMe SSD.', 525000.00, 'images/products/3.jpg', '2026-06-20 23:33:07', 1),
	(4, 2, 'Zephyrus G16 Gaming Laptop', 'ASUS', 'Slim and powerful gaming laptop featuring an Intel i9 and RTX 4080.', 795000.00, 'images/products/4.jpg', '2026-06-20 23:33:07', 1),
	(5, 3, 'Elden Ring: Shadow of the Erdtree', 'Bandai Namco', 'Physical edition containing the base game and the critically acclaimed expansion.', 26500.00, 'images/products/5.jpg', '2026-06-20 23:33:07', 1),
	(6, 3, 'The Legend of Zelda: Tears of the Kingdom', 'Nintendo', 'Epic open-world action-adventure game for the Nintendo Switch.', 23500.00, 'images/products/6.jpg', '2026-06-20 23:33:07', 1),
	(7, 4, 'DualSense Wireless Controller', 'Sony', 'Immersive haptic feedback controller for PlayStation 5.', 24500.00, 'images/products/7.jpg', '2026-06-20 23:33:07', 1),
	(8, 4, 'BlackShark V2 Pro Headset', 'Razer', 'Wireless esports gaming headset with ultra-clear microphone.', 62000.00, 'images/products/8.jpg', '2026-06-20 23:33:07', 1),
	(9, 5, 'Meta Quest 3 128GB', 'Meta', 'Breakthrough mixed reality headset with dual RGB cameras.', 179900.00, 'images/products/9.jpg', '2026-06-20 23:33:07', 1),
	(10, 6, 'OLED C4 65-Inch Smart TV', 'LG', '4K Smart TV featuring a 144Hz refresh rate, ideal for next-gen gaming.', 669900.00, 'images/products/10.jpg', '2026-06-20 23:33:07', 1);

-- Dumping structure for table techmart_online.warehouse
CREATE TABLE IF NOT EXISTS `warehouse` (
  `warehouse_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `region` varchar(50) NOT NULL,
  `address` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`warehouse_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb3;

-- Dumping data for table techmart_online.warehouse: ~6 rows (approximately)
INSERT INTO `warehouse` (`warehouse_id`, `name`, `region`, `address`) VALUES
	(1, 'Colombo Central MegaHub', 'Western Province', '45 Galle Road, Colombo 03'),
	(2, 'Kandy Tech Fulfillment', 'Central Province', '122 Peradeniya Road, Kandy'),
	(3, 'Galle Coastal Logistics', 'Southern Province', '89 Matara Road, Galle'),
	(4, 'Jaffna Northern Center', 'Northern Province', '210 Kandy Road, Jaffna'),
	(5, 'Negombo Air-Cargo Hub', 'Western Province', '14 Colombo-Chilaw Road, Negombo'),
	(6, 'Gampaha Distribution Yard', 'Western Province', '77 Ja-Ela Road, Gampaha');

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
