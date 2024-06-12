package com.example.afs.flightdataapi.testutils;

public class TestConstants {

    public static final String POSTGRES_DOCKER_IMAGE = "postgres:16-alpine";
    public static final String INIT_SCRIPT_PATH = "scripts/init-db.sql";
    public static final String POPULATE_SCRIPT_PATH = "classpath:scripts/test-aircrafts-data-populate.sql";
    public static final String PROPERTIES_DB_REPLACE_NONE = "spring.test.database.replace=none";

}
