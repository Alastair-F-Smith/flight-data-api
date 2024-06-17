create table if not exists aircrafts_data
(
    aircraft_code char(3) not null constraint aircrafts_pkey primary key,
    model         jsonb   not null,
    range         integer not null constraint aircrafts_range_check check (range > 0)
);

CREATE TABLE IF NOT EXISTS seats (
   aircraft_code character(3) NOT NULL,
   seat_no character varying(4) NOT NULL,
   fare_conditions character varying(10) NOT NULL,
   CONSTRAINT seats_fare_conditions_check CHECK (((fare_conditions)::text = ANY (ARRAY[('Economy'::character varying)::text, ('Comfort'::character varying)::text, ('Business'::character varying)::text])))
);

CREATE TABLE IF NOT EXISTS airports_data (
   airport_code character(3) NOT NULL,
   airport_name jsonb NOT NULL,
   city jsonb NOT NULL,
   coordinates point NOT NULL,
   timezone text NOT NULL
);

CREATE TABLE IF NOT EXISTS bookings (
  book_ref character(6) NOT NULL,
  book_date timestamp with time zone NOT NULL,
  total_amount numeric(10,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS tickets (
         ticket_no character(13) NOT NULL,
         book_ref character(6) NOT NULL,
         passenger_id character varying(20) NOT NULL,
         passenger_name text NOT NULL,
         contact_data jsonb
);

-- ALTER TABLE ONLY aircrafts_data
--     ADD CONSTRAINT aircrafts_pkey PRIMARY KEY (aircraft_code);

ALTER TABLE ONLY airports_data
    ADD CONSTRAINT airports_data_pkey PRIMARY KEY (airport_code);

ALTER TABLE ONLY seats
    ADD CONSTRAINT seats_pkey PRIMARY KEY (aircraft_code, seat_no);

ALTER TABLE ONLY seats
    ADD CONSTRAINT seats_aircraft_code_fkey FOREIGN KEY (aircraft_code) REFERENCES aircrafts_data(aircraft_code) ON DELETE CASCADE;

ALTER TABLE ONLY bookings
    ADD CONSTRAINT bookings_pkey PRIMARY KEY (book_ref);

ALTER TABLE ONLY tickets
    ADD CONSTRAINT tickets_pkey PRIMARY KEY (ticket_no);

ALTER TABLE ONLY tickets
    ADD CONSTRAINT tickets_book_ref_fkey FOREIGN KEY (book_ref) REFERENCES bookings(book_ref);