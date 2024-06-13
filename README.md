# Flight Data API

Provides a REST API that allows access to a demo set of global flight data from a single month in 2017.

This project is currently under construction and is not functional.

## Dependencies

- JDK 21
- PostgreSQL
- Docker

## Set-up

After cloning the repository to your local machine, there are a few steps required to get the application up and running.

### Accessing the data

The flight data is available free of charge at https://postgrespro.com/community/demodb. The database is available in 3 sizes. To initialise the small database, run the script using psql:

```shell
psql -f demo_small_YYYYMMDD.sql -U postgres
```

### RSA keys for self-signed JWT tokens

The application is secured using JWTs that are self-signed using an RSA key pair. By default, it expects these keys to be in the folder `src/main/resources/certs`. To store the keys elsewhere, edit the values in the `application.properties` file. These keys can be generated using openssl as follows:

```shell
# create rsa key pair
openssl genrsa -out keypair.pem 2048

# extract public key
openssl rsa -in keypair.pem -pubout -out public.pem

# create private key in PKCS#8 format
openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt -in keypair.pem -out private.pem
```

### Add a properties file

Create a file called `application.properties` in `src/main/resources` and enter the following information, substituting in the username and password for your database:

```properties
spring.datasource.url=jdbc:postgresql:demo
spring.datasource.username=<your_database_username>
spring.datasource.password=<your_database_password>
rsa.private-key=classpath:certs/private.pem
rsa.public-key=classpath:certs/public.pem
```

## Usage

Run the `FlightDataApiApplication` class to start up the server, which by default will run on localhost at port 8080.

### Authentication

To access any end points, users must be logged in. This is achieved by sending a valid username and password using the HTTP basic protocol to the `/api/token` endpoint. If authentication is successful, the response will contain a JWT token, which should then be included as a bearer token in subsequent API requests.

For development and demonstration purposes, two users are currently available:

- a user with username `user` and password `password`
- a user with username `admin` and password `password`

The admin user also has access to the admin role, which will allow them to perform actions that mutate the underlying database such as creating, updating or deleting data. In contrast the user is only able to perform read-only actions.

### Reading data

To read data, send GET requests to the appropriate endpoint, including the bearer token obtained in the previous step. The following endpoints are currently available:

- `api/aircraft` - details of the aircraft models available in the dataset
- `api/aircraft/{aircraftCode}/seats` - details of the seats present in the aircraft model specified by the aircraft code



