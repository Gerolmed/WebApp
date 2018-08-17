-- Initiale Daten für Sales-Webapp.

USE findlunch;

-- Vertriebsmitarbeiter
INSERT IGNORE INTO `findlunch`.`swa_sales_person` (`id`, `password`, `first_name`, `second_name`, `street`, `street_number`, `zip`, `city`, `phone`, `country_code`, `email`, `iban`, `bic`, `salary_percentage`) VALUES
  (1, "5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5", "Alexander", "Carl", "Lothstr", "666", "87654", "StackOverflowCity", "666/1234 1234", "DE", "carl@hm.edu", "DE123456789012312312312312", "THEBIC1234567890", 0.33),
  (2, "5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5", "Alex2nd", "Karl", "Lotharstr.", "999", "12345", "StackOverflowCity", "999/1234 1234", "DE", "alexander.carl@ymail.com", "DE123456789012312312312312", "THEBIC1234567890", 0.33);

-- Aufgabentypen
INSERT IGNORE INTO `findlunch`.`swa_todo_request_typ` (`id`, `todo_request_typ`) VALUES
  (1, 'Änderung freigeben'),
  (2, 'Umsatzwarnung'),
  (3, 'Besuchsaufforderung');