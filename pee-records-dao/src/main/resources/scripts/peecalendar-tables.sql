
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


LOCK TABLES `patients` WRITE;
INSERT INTO patients (id, first_name, last_name, date_of_birth, registration_date, email, password) 
	VALUES (1, 'John', 'Doe', '2020-04-17', '2021-08-11', 'johndoe@gmail.com', '123');
UNLOCK TABLES;

CREATE TABLE IF NOT EXISTS `pee_cycles` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `patient_id` INT NOT NULL,
  `start_date` DATE NOT NULL,
  `end_date` DATE NOT NULL,
  PRIMARY KEY (`id`),
  INDEX (`patient_id`),
  FOREIGN KEY (`patient_id`) REFERENCES `patients` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB; 


CREATE TABLE IF NOT EXISTS `pee_cycle_events` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `pee_cycle_id` INT NOT NULL,
  `when_peed` DATETIME NOT NULL,
  `pee_type` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX (`pee_cycle_id`),
  FOREIGN KEY (`pee_cycle_id`) REFERENCES `pee_cycles` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB;
