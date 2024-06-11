DELETE FROM aircrafts_data;
DELETE FROM seats;
INSERT INTO aircrafts_data VALUES ('773', JSON '{"en": "Boeing", "ru": "Boeing"}', 11100);
INSERT INTO seats VALUES ('773', '43G', 'Economy');
INSERT INTO seats VALUES ('773', '11D', 'Comfort');