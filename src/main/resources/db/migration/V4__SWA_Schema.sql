USE findlunch;

-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Table `findlunch`.`swa_sales_person`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `findlunch`.`swa_sales_person` (
  `id` INT(11) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `first_name` VARCHAR(60) NOT NULL,
  `second_name` VARCHAR(60) NOT NULL,
  `street` VARCHAR(60) NOT NULL,
  `street_number` VARCHAR(11) NOT NULL,
  `zip` VARCHAR(5) NOT NULL,
  `city` VARCHAR(60) NOT NULL,
  `phone` VARCHAR(60) NOT NULL,
  `country_code` VARCHAR(2),
  `email` VARCHAR(60) NOT NULL,
  `iban` VARCHAR(60) NOT NULL,
  `bic` VARCHAR(60) NOT NULL,
  `salary_percentage` DECIMAL(2,2) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

-- -----------------------------------------------------
-- Table `findlunch`.`swa_todo_request_typ`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `findlunch`.`swa_todo_request_typ` (
  `id` INT(11) NOT NULL,
  `todo_request_typ` VARCHAR(60) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

-- -----------------------------------------------------
-- Table `findlunch`.`swa_todo_list`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `findlunch`.`swa_todo_list` (
  `id` INT(11) NOT NULL,
  `sales_person_id` INT(11) NOT NULL,
  `restaurant_id` INT(11) NOT NULL,
  `offer_id` INT(11),
  `todo_request_typ_id` INT(11) NOT NULL,
  `datetime` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_salesperson_todo`
    FOREIGN KEY (`sales_person_id`)
    REFERENCES `findlunch`.`swa_sales_person` (`id`)
    ON DELETE NO ACTION
    ON UPDATE RESTRICT,
  CONSTRAINT `fk_restaurant_todo`
    FOREIGN KEY (`restaurant_id`)
    REFERENCES `findlunch`.`restaurant` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT `fk_offer_todo`
    FOREIGN KEY (`offer_id`)
    REFERENCES `findlunch`.`offer` (`id`)
    ON DELETE SET NULL
    ON UPDATE RESTRICT,
  CONSTRAINT `fk_requesttyp_todo`
    FOREIGN KEY (`todo_request_typ_id`)
    REFERENCES `findlunch`.`swa_todo_request_typ` (`id`)
    ON DELETE NO ACTION
    ON UPDATE RESTRICT)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

-- -----------------------------------------------------
-- Table `findlunch`.`offer` erweitern.
-- -----------------------------------------------------
ALTER TABLE `findlunch`.`offer` ADD COLUMN (
  `swa_change_request_id` INT(11) DEFAULT -1,
  `swa_comment_of_last_change` TEXT DEFAULT NULL,
  `swa_last_changed_by_sales_person_id` INT(11),
  `swa_change_request` TINYINT(1) DEFAULT 0);

ALTER TABLE `findlunch`.`offer`
ADD CONSTRAINT `fk_salesperson_offer`
  FOREIGN KEY (`swa_last_changed_by_sales_person_id`)
  REFERENCES `findlunch`.`swa_sales_person` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `findlunch`.`offer`
MODIFY `course_type` INT(11);

-- -----------------------------------------------------
-- Table `findlunch`.`restaurant` erweitern.
-- -----------------------------------------------------
ALTER TABLE `findlunch`.`restaurant` ADD COLUMN (
  `swa_offer_modify_permission` TINYINT(1) NOT NULL DEFAULT 0,
  `swa_blocked` TINYINT(1) NOT NULL DEFAULT 1,
  `swa_sales_person_id` INT(11));

ALTER TABLE `findlunch`.`restaurant`
ADD CONSTRAINT `fk_salesperson_restaurant`
  FOREIGN KEY (`swa_sales_person_id`)
  REFERENCES `findlunch`.`swa_sales_person` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

-- -----------------------------------------------------
-- Table `findlunch`.`reservation_offers` ändern.
-- -----------------------------------------------------
ALTER TABLE `findlunch`.`reservation_offers`
MODIFY `offer_id` INT(11);

-- -----------------------------------------------------
-- Abschluss des Skriptes
-- -----------------------------------------------------

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
