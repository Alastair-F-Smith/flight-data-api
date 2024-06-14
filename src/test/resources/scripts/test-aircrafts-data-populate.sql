DELETE FROM aircrafts_data;
DELETE FROM seats;
DELETE FROM airports_data;
INSERT INTO aircrafts_data VALUES ('773', JSON '{"en": "Boeing", "ru": "Boeing"}', 11100);
INSERT INTO seats VALUES ('773', '43G', 'Economy');
INSERT INTO seats VALUES ('773', '11D', 'Comfort');

INSERT INTO airports_data VALUES ('SGC', JSON '{"en": "Surgut Airport", "ru": "Сургут"}', JSON	'{"en": "Surgut", "ru": "Сургут"}',	'(73.4018020629882812,61.3437004089355469)',	'Asia/Yekaterinburg');
INSERT INTO airports_data VALUES ('BZK', JSON	'{"en": "Bryansk Airport", "ru": "Брянск"}', JSON '{"en": "Bryansk", "ru": "Брянск"}',	'(34.1763992309999978,53.2141990661999955)',	'Europe/Moscow');