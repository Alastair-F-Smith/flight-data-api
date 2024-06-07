create table if not exists aircrafts_data
(
    aircraft_code char(3) not null constraint aircrafts_pkey primary key,
    model         jsonb   not null,
    range         integer not null constraint aircrafts_range_check check (range > 0)
);