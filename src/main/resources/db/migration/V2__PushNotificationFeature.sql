USE findlunch;

-- Spalte `push_notification_enabled` in Tabelle "User" ergänzen.
ALTER TABLE `findlunch`.`user`
ADD `push_notification_enabled` TINYINT(1) DEFAULT 0
AFTER `password`;

-- Tabellen rund um push_notification löschen.
DROP TABLE `findlunch`.`push_notification_has_kitchen_type`;
DROP TABLE `findlunch`.`push_notification_has_day_of_week`;
DROP TABLE `findlunch`.`push_notification`;
