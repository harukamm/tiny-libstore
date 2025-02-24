# Tiny Libstore

This is a test project designed for learning Jooq and Flyway.

## Subprojects

- `database`: Responsible for generating Jooq classes and managing database migration using Jooq and Flyway.
- `web`: Spring Boot application that serves REST API for managing library store.

## Commands to run

Start the database:

```
docker-compose up
```

To start the web application, execute the following command:

```
./gradlew :web:bootRun
```

This command will also generate Jooq classes under the `database/build/generated-sources/jooq` directory.

The application runs on 8080 port by default.
You can access the Swagger UI at:

```
http://localhost:8080/swagger-ui.html
```