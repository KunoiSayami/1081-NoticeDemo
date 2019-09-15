/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

-- Dumping structure for table accounts
CREATE TABLE IF NOT EXISTS `accounts` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Unique ID identify each user',
  `username` varchar(16) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `password` varchar(128) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT 'Password should hashed before stored (use sha512 to store password)',
  `nickname` varchar(16) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `description` varchar(256) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `privileges` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `enabled` enum('Y','N') CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL DEFAULT 'Y',
  `register_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `last_login` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Description for privileges:\nlevel 0 means not login user\nlevel 1 means nomal user\nlevel 2-63 is Reserved\nlevel 64 can broadcast\nlevel 65-127 is Reserved';

DELETE FROM `accounts`;
INSERT INTO `accounts` (`id`, `username`, `password`, `nickname`, `description`, `privileges`, `enabled`, `register_time`, `last_login`) VALUES
	(0, 'anonymous', 'cf83e1357eefb8bdf1542850d66d8007d620e4050b5715dc83f4a921d36ce9ce47d0d13c5d85f2b0ff8318d2877eec2f63b931bd47417a81a538327af927da3e', '', '', 0, 'Y', '2019-09-15 20:40:53', NULL);

-- Dumping structure for table firebasetoken
CREATE TABLE IF NOT EXISTS `firebasetoken` (
  `_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(10) unsigned NOT NULL,
  `toekn` varchar(500) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `register_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping structure for table log_login
CREATE TABLE IF NOT EXISTS `log_login` (
  `attempt_user` varchar(16) COLLATE utf8_unicode_ci NOT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `success` enum('Y','N') COLLATE utf8_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;


/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
