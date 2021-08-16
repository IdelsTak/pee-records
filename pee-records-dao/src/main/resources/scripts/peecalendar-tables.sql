
CREATE TABLE IF NOT EXISTS `doctors` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `first_name` VARCHAR(45) NOT NULL,
  `last_name` VARCHAR(45) NOT NULL,
  `email` VARCHAR(100) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `active` TINYINT DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;


LOCK TABLES `doctors` WRITE;
INSERT INTO `doctors` VALUES (1,'admin','admin','admin@gmail.com','admin',1);
UNLOCK TABLES;


CREATE TABLE IF NOT EXISTS `patients` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `first_name` VARCHAR(45) NOT NULL,
  `last_name` VARCHAR(45) NOT NULL,
  `date_of_birth` DATE NOT NULL,
  `registration_date` DATE NOT NULL,
  `email` VARCHAR(100) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;


CREATE TABLE IF NOT EXISTS `pee_records` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `start_date` DATE NOT NULL,
  `patient_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX (`patient_id`),
  FOREIGN KEY (`patient_id`) REFERENCES `patients` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB; 


CREATE TABLE IF NOT EXISTS `pee_record_events` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `pee_record_id` INT NOT NULL,
  `when_peed` DATETIME NOT NULL,
  `pee_type` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX (`pee_record_id`),
  FOREIGN KEY (`pee_record_id`) REFERENCES `pee_records` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB;
