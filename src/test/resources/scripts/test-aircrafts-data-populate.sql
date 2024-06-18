DELETE FROM ticket_flights;
DELETE FROM tickets;
DELETE FROM bookings;
DELETE FROM flights;
DELETE FROM aircrafts_data;
DELETE FROM seats;
DELETE FROM airports_data;
ALTER SEQUENCE flights_flight_id_seq RESTART;

INSERT INTO aircrafts_data VALUES ('773', JSON '{"en": "Boeing", "ru": "Boeing"}', 11100);
INSERT INTO seats VALUES ('773', '43G', 'Economy');
INSERT INTO seats VALUES ('773', '11D', 'Comfort');

INSERT INTO airports_data VALUES ('SGC', JSON '{"en": "Surgut Airport", "ru": "Сургут"}', JSON	'{"en": "Surgut", "ru": "Сургут"}',	'(73.4018020629882812,61.3437004089355469)',	'Asia/Yekaterinburg');
INSERT INTO airports_data VALUES ('BZK', JSON	'{"en": "Bryansk Airport", "ru": "Брянск"}', JSON '{"en": "Bryansk", "ru": "Брянск"}',	'(34.1763992309999978,53.2141990661999955)',	'Europe/Moscow');

INSERT INTO bookings VALUES ('000374',	'2017-08-12 10:13:00+03', 136200.00);
INSERT INTO bookings VALUES ('00044D',	'2017-07-30 00:24:00+03', 6000.00);

INSERT INTO tickets VALUES ('0005435990692',	'000374',	'9943 768646',	'EVGENIY MOROZOV',	JSON '{"phone": "+70184611504"}');
INSERT INTO tickets VALUES ('0005435990693',	'00044D',	'9527 761134',	'VITALIY BELOV',	JSON '{"email": "belov.vitaliy.20071970@postgrespro.ru", "phone": "+70454686855"}');

INSERT INTO flights VALUES (nextval('flights_flight_id_seq'),	'PG0405',	'2017-07-16 09:35:00+03',	'2017-07-16 10:30:00+03',	'SGC',	'BZK',	'Arrived',	'773',	'2017-07-16 09:44:00+03',	'2017-07-16 10:39:00+03');
INSERT INTO flights VALUES (nextval('flights_flight_id_seq'),	'PG0404',	'2017-08-05 19:05:00+03',	'2017-08-05 20:00:00+03',	'BZK',	'SGC',	'Arrived',	'773',	'2017-08-05 19:06:00+03',	'2017-08-05 20:01:00+03');

INSERT INTO ticket_flights VALUES ('0005435990692',	2,	'Business',	42100.00);
INSERT INTO ticket_flights VALUES ('0005435990693',	1,	'Business',	42100.00);