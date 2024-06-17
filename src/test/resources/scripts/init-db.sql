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

CREATE TABLE flights (
         flight_id integer NOT NULL,
         flight_no character(6) NOT NULL,
         scheduled_departure timestamp with time zone NOT NULL,
         scheduled_arrival timestamp with time zone NOT NULL,
         departure_airport character(3) NOT NULL,
         arrival_airport character(3) NOT NULL,
         status character varying(20) NOT NULL,
         aircraft_code character(3) NOT NULL,
         actual_departure timestamp with time zone,
         actual_arrival timestamp with time zone,
         CONSTRAINT flights_check CHECK ((scheduled_arrival > scheduled_departure)),
         CONSTRAINT flights_check1 CHECK (((actual_arrival IS NULL) OR ((actual_departure IS NOT NULL) AND (actual_arrival IS NOT NULL) AND (actual_arrival > actual_departure)))),
         CONSTRAINT flights_status_check CHECK (((status)::text = ANY (ARRAY[('On Time'::character varying)::text, ('Delayed'::character varying)::text, ('Departed'::character varying)::text, ('Arrived'::character varying)::text, ('Scheduled'::character varying)::text, ('Cancelled'::character varying)::text])))
);

CREATE SEQUENCE flights_flight_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE flights_flight_id_seq OWNED BY flights.flight_id;

ALTER TABLE ONLY flights ALTER COLUMN flight_id SET DEFAULT nextval('flights_flight_id_seq'::regclass);

CREATE TABLE ticket_flights (
        ticket_no character(13) NOT NULL,
        flight_id integer NOT NULL,
        fare_conditions character varying(10) NOT NULL,
        amount numeric(10,2) NOT NULL,
        CONSTRAINT ticket_flights_amount_check CHECK ((amount >= (0)::numeric)),
        CONSTRAINT ticket_flights_fare_conditions_check CHECK (((fare_conditions)::text = ANY (ARRAY[('Economy'::character varying)::text, ('Comfort'::character varying)::text, ('Business'::character varying)::text])))
);


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

ALTER TABLE ONLY flights
    ADD CONSTRAINT flights_flight_no_scheduled_departure_key UNIQUE (flight_no, scheduled_departure);

ALTER TABLE ONLY flights
    ADD CONSTRAINT flights_pkey PRIMARY KEY (flight_id);

ALTER TABLE ONLY flights
    ADD CONSTRAINT flights_aircraft_code_fkey FOREIGN KEY (aircraft_code) REFERENCES aircrafts_data(aircraft_code);

ALTER TABLE ONLY flights
    ADD CONSTRAINT flights_arrival_airport_fkey FOREIGN KEY (arrival_airport) REFERENCES airports_data(airport_code);

ALTER TABLE ONLY flights
    ADD CONSTRAINT flights_departure_airport_fkey FOREIGN KEY (departure_airport) REFERENCES airports_data(airport_code);