-- Initiale Daten.

USE findlunch;

-- Länder
INSERT IGNORE INTO `findlunch`.`country` (`country_code`, `name`) VALUES
  ('DE', 'Deutschland'),
  ('UK', 'United Kingdom');

-- User-Typen
INSERT IGNORE INTO `findlunch`.`user_type` (`id`,`name`) VALUES
  (1, "Anbieter"),
  (2, "Kunde"),
  (3, "Manager"),
  (4, "Admin"),
  (5, "Scout");

-- Euro pro Punkt
INSERT IGNORE INTO `findlunch`.`euro_per_point` (`id`,`euro`) VALUES (1, 1.0);

-- Minimaler Profit
INSERT IGNORE INTO `findlunch`.`minimum_profit` (`id`,`profit`) VALUES (1, 10);

-- Konten
INSERT IGNORE INTO `findlunch`.`account_type` (`id`,`name`) VALUES
  (1, 'Forderungskonto'),
  (2, 'Kundenkonto');

-- Buchungsgründe
INSERT IGNORE INTO `findlunch`.`booking_reason` (`id`,`reason`) VALUES
  (1, 'Forderung'),
  (2, 'Einzahlung');

-- Wochentage
INSERT IGNORE INTO `findlunch`.`day_of_week` (`id`, `name`, `day_number`) VALUES
  (1, 'Montag', 2),
  (2, 'Dienstag', 3),
  (3, 'Mittwoch', 4),
  (4, 'Donnerstag', 5),
  (5, 'Freitag', 6),
  (6, 'Samstag', 7),
  (7, 'Sonntag', 1);

-- Bestellstatus
INSERT IGNORE INTO `findlunch`.`reservation_status` (`id`, `status`, `statKey`) VALUES
  (0, 'neu angelegt', 0),
  (1, 'bestätigt', 1),
  (2, 'abgelehnt, Der Anbieter hat die Bestellung abgelehnt.', 2),
  (3, 'abgelehnt, Die gewünschte Wartezeit kann nicht eingehalten werden.', 2),
  (4, 'abgelehnt, Das Produkt ist leider ausverkauft.', 2),
  (5, 'abgelehnt, Der Anbieter hat momentan geschlossen. ', 2),
  (6, 'abgelehnt, Der Anbieter ist bereits ausgebucht.', 2),
  (7, 'abgelehnt, Der Anbieter ist im Urlaub.', 2),
  (8, 'abgelehnt, Das Restaurant bei dem Sie bestellen ist umgezogen.', 2),
  (9, 'nicht abgeholt', 3);

-- Küchenarten
INSERT IGNORE INTO `findlunch`.`kitchen_type` (`id`, `name`) VALUES
  (1, 'Italienisch'),
  (2, 'Indisch'),
  (3, 'Griechisch'),
  (4, 'Asiatisch'),
  (5, 'Bayerisch');

-- Restauranttypen
INSERT IGNORE INTO `findlunch`.`restaurant_type` (`id`, `name`) VALUES
  (1, 'Imbiss'),
  (2, 'Restaurant'),
  (3, 'Bäckerei'),
  (4, 'Sonstiges');

-- Zusätze
INSERT IGNORE INTO `additives` (`id`, `name`, `description`, `short`) VALUES
  (1, 'Farbstoffe E 100 - E 180', 'mit Farbstoff', 'a'),
  (2, 'Konservierungsstoffe E 200 - E 219, E 230 - E 235, E 239, E 249 - E 252, E 280 - E 285, E 1105', 'mit Konservierungsstoff', 'b'),
  (3, 'Geschmacksverstärker E 620 - E 635', 'mit Geschmacksverstärker', 'c'),
  (4, 'Schwefeldioxid/Sulfite E 220 - E 228 ab 10 mg/kg', 'geschwefelt', 'd'),
  (5, 'Eisensalze E 579, E 585', 'geschwärzt', 'e'),
  (6, 'Stoffe zur Oberflächenbehandlung E 901 - E 904, E 912, E 914', 'gewachst', 'f'),
  (7, 'Süßstoffe E 950 - E 952, E 954, E 955, E 957, E 959, E 962', 'mit Süßungsmittel(n)', 'g'),
  (8, 'andere Süßungsmittel mit mehr als 10% Gehalt (Zuckeralkohole) E 420, E 421, E 953, E 965 - E 967', 'kann bei übermäßigem Verzehr abführend wirken', 'h'),
  (9, 'Phosphate E 338 - 341, E 450 - E 452', 'mit Phosphat', 'i'),
  (10, 'Coffein', 'coffeinhaltig', 'j'),
  (11, 'Chinin, Chininsalze', 'chininhaltig', 'k'),
  (12, 'Taurin', 'taurinhaltig', 'l'),
  (13, 'Zutaten mit gentechnisch veränderten Organismen', 'gentechnisch verändert', 'm'),
  (14, 'Gentechnisch veränderte Organismen', 'enthält Sojaöl, aus gentechnisch veränderter Soja hergestellt', 'n');

-- Allergene
INSERT IGNORE INTO `allergenic` (`id`, `name`, `description`, `short`) VALUES
  (1, 'Getreideprodukte (Glutenhaltig)', 'Weizen, Roggen, Gerste, Hafer, Dinkel, Kamut und daraus hergestellte Erzeugnisse, also Stärke, Brot, Nudeln, Panaden, Wurstwaren, Desserts etc.. Ausgenommen sind Glukosesirup auf Weizen- und Gerstenbasis. ', '1'),
  (2, 'Fisch', 'Betroffen sind alle Süß- und Salzwasserfischarten, Kaviar, Fischextrakte, Würzpasten, Saucen etc.. Ganz genau genommen müsste auch ausgewiesen werden, wenn Produkte von Tieren verarbeitet werden, die mit Fischmehl gefüttert wurden.', '2'),
  (3, 'Krebstiere', 'Garnelen, Hummer, Krebse, Scampi, Shrimps, Langusten und sämtliche daraus gewonnenen Erzeugnisse. Wer also in seinen Gerichten asiatische Gewürzmischung oder Paste mit Extrakten aus Krebstieren verwendet, muss das deklarieren. ', '3'),
  (4, 'Schwefeldioxide und Sulfite', 'Wie sie in Softdrinks, Bier, Wein, Essig, Trockenfrüchten und bei diversen Fleisch-, Fisch- und Gemüseprodukten entstehen oder zugesetzt werden, in Konzentrationen von mehr als 10 mg/Kg oder 10 mg/l als insgesamt vorhandenes Schwefeldioxid.', '4'),
  (5, 'Sellerie', 'Sowohl Knolle als auch Staude müssen deklariert werden, egal in welchem Aggregatzustand sie zum Gast wandern. Als Gewürz in Fertiggerichten, in Dressings, Ketchup, Saucen.', '5'),
  (6, 'Milch und Laktose', 'Erzeugnisse wie Butter, Käse, Margarine etc. und Produkte, in denen Milch und /oder Laktose vorkommt, also etwa Brot-, Backwaren, Wurstwaren, Pürees, Suppen oder Saucen. Ausgenommen sind Molke zur Herstellung von alkoholischen Destillaten und Lactit.', '6'),
  (7, 'Sesamsamen', 'Sesam, egal ob im Rohzustand, als Öl oder Paste, in Gebäck, Marinaden, Dressings, Falafel, Müsli, Hummus.', '7'),
  (8, 'Nüsse', 'Mandeln, Haselnüsse, Walnüsse, Kaschunüsse / Cashewnüsse, Pekannüsse, Paranüsse, Pistazien, Macadamia- / Queenslandnüsse sowie sämtliche daraus gewonnenen Erzeugnisse außer Nüssen zur Herstellung von alkoholischen Destillaten.', '8'),
  (9, 'Erdnüsse', 'Alle Erzeugnisse aus Erdnüssen wie Erdnussöl und Butter; Vorkommen in Gebäck und Kuchen, Desserts, vorfrittierten Produkten wie Pommes Frites oder Rösti, Aufstrichen, Füllungen etc. ', '9'),
  (10, 'Eier', 'Als Flüssigei, Lecithin oder (Ov) Albumin und so, wie es in Mayonnaise, Panaden, Dressings, Kuchen, Suppen, Saucen, Nudeln, Glasuren und natürlich generell bei allen Eier-Speisen vorkommt.', '10'),
  (11, 'Lupinen', 'Lupinensamen, Lupinenmehl, Milch, Tofu und Konzentrat, wie es sich in Brot- und Backwaren, Nudeln, Gewürzen, Würsten, Aufstrichen oder Süßspeisen findet. ', '11'),
  (12, 'Senf', 'Betroffen sind hier sowohl Senfkörner als auch Pulver und alle daraus gewonnenen Erzeugnisse wie Dressings, Marinaden, Currys, Wurstwaren, Aufstriche, Gewürzmischungen etc.', '12'),
  (13, 'Soja', 'Sojabohnen und daraus gewonnene Erzeugnisse, also etwa Miso, Sojasauce, Sojaöl, Gebäck, Marinaden, Kaffeeweißer, Suppen, Saucen, Dressings. Ausgenommen ist vollständig raffiniertes Sojabohnen-Öl und -Fett.', '13'),
  (14, 'Weichtiere', 'Schnecken, Tintenfisch, Austern, Muscheln und alle Erzeugnisse, in denen Weichtiere oder Spuren von ihnen enthalten sind, also Gewürzmischungen, Saucen, asiatische Spezialitäten, Salate oder Pasten.', '14');