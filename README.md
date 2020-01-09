## Event-Central

Event-Central is a Open Source Tools for Snmp trap and Syslog events Listener. It help your easy to monitor IT environment.

Requirements:

- Latest Java 1.8
- A MySQL (like) database
- A servlet container like  Tomcat (8.x or later)

### Build/Install
```shell
$ cd EventCentral
$ ant
$ cd dist   -- All build files in dist folder:
  lsecs.*   -- event-central server site.
  lsecc.war -- event-central web client.
```

### Usage

#### 1. Create database 'lsec' and user 'lsec' for event-central. 

Event-Central Server site log received events and listeners status into database, client site get information from database and display them.

```sql
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

```

#### 2. prepare database source configuration file 

Event-Central use database configuration file with fixed name datasource.xml.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration>
	<DataSource>
		<Driver>com.mysql.cj.jdbc.Driver</Driver>
		<Url>jdbc:mysql://127.0.0.1/lsec</Url>
		<User>lsec</User>
		<Password>Passw0rd</Password>
		<Properties>useSSL=false;useUnicode=true;characterEncoding=UTF-8;autoReconnect=true</Properties>
	</DataSource>
</Configuration>
```

Note:
- Server site configuration file location in server site running directory.
- Client site configuration file specify by ${your-web-deployments-dir}/WEB-INF/web.xml

```xml
  ...
  <context-param>
	<param-name>ds_config</param-name>
	<param-value>D:\product\lsec\datasource.xml</param-value> 
  </context-param>
  ...
```

### 3. Deploy and run it

```
  Server site: ./lsecs.cmd
  Client site: deploy lsecc.war
```

  


