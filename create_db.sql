CREATE DATABASE `lsec` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `lsec`.`listeners` (
  `raddr` VARCHAR(32) NOT NULL,
  `port` INT(10) UNSIGNED NOT NULL,
  `protocol` VARCHAR(16) NOT NULL,
  `type` VARCHAR(45) NOT NULL,
  `name` VARCHAR(64) NOT NULL,
  `status` TINYINT(4) NOT NULL,
  `last` TIMESTAMP NOT NULL,
  PRIMARY KEY (`raddr`, `port`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `lsec`.`events` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `created` DATETIME NOT NULL,
  `severity` VARCHAR(32) NOT NULL,
  `type` VARCHAR(24) NOT NULL,
  `source` VARCHAR(128) NOT NULL,
  `state` VARCHAR(45) NOT NULL,
  `data` TEXT NOT NULL,
  `digest` VARCHAR(200) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `lsec`.`hosts` (
  `addr` VARCHAR(64) NOT NULL,
  `name` VARCHAR(120) NOT NULL,
  PRIMARY KEY (`addr`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `lsec`.`blacklist` (
  `category` varchar(64) NOT NULL,
  `keyword` varchar(120) NOT NULL,
  PRIMARY KEY (`keyword`,`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

create user `lsec` identified by 'Passw0rd';
grant all privileges on `lsec`.* to 'lsec'@'%';
