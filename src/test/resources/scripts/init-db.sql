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