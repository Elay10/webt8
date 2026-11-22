# Event Registration API

A Spring Boot REST API that stores events in PostgreSQL using Spring Data JPA.

## Requirements

- JDK 17 or later
- PostgreSQL
- Gradle Wrapper (included)

## Database setup

Create an empty PostgreSQL database named `event_db`. Configure the following environment variables before starting the application:

- `DB_USER`: a local PostgreSQL account that can connect to `event_db` and create tables
- `DB_PASSWORD`: that account's password
- `DB_URL` (optional): defaults to `jdbc:postgresql://localhost:5432/event_db`

PowerShell example:

```powershell
$env:DB_USER = "your_local_db_user"
$env:DB_PASSWORD = "your_local_db_password"
.\gradlew.bat bootRun
```

The placeholders above are examples only. Do not commit real credentials. The application uses Hibernate `ddl-auto=update` for this local development database; do not use that schema-management setting in production.

## Build and test

```powershell
.\gradlew.bat test
.\gradlew.bat bootRun
```

## Endpoints

| Method and path | Behavior |
|---|---|
| `GET /api/events` | List all persisted events |
| `GET /api/events?title=quiz` | Case-insensitive partial-title search |
| `GET /api/events/{id}` | Get one event; returns 404 if missing |
| `POST /api/events` | Create an event; returns 201 and a database-generated ID |
| `PUT /api/events/{id}` | Update title and location; preserves the existing ID |
| `DELETE /api/events/{id}` | Delete an event; returns 204, or 404 if missing |
| `GET /api/hello` | Welcome message |
| `GET /api/info` | Application, course, and semester information |

POST and PUT JSON:

```json
{
  "title": "Java Quiz",
  "location": "Room 105"
}
```

Blank or missing titles/locations are rejected with HTTP 400. Any `id` included in the request body is ignored.

## Verify persistence

Create an event with POST and note the returned ID. Verify its values in PostgreSQL:

```sql
SELECT id, title, location FROM events;
```

Restart the application and request `GET /api/events/{id}` to confirm the event remains. DELETE the event, verify the 204 response, and run the query again to confirm its row is gone.

## Project structure

- `model/Event`: JPA entity mapped to the `events` table; PostgreSQL generates its primary key.
- `repository/EventRepository`: `JpaRepository` CRUD operations and case-insensitive title search.
- `service/EventService`: validates input and performs event operations through the repository.
- `controller/RegistrationController`: exposes the REST endpoints and delegates to the service.
