# Keycloak Manager

A Spring Boot microservice for managing users in a Keycloak IAM server. Provides REST endpoints for creating, retrieving, and deleting Keycloak users, resetting user passwords, and rolling back user creation as part of a distributed Saga workflow.

## Prerequisites

- Java 17
- Maven 3.6+
- A running Keycloak instance (default: `http://localhost:9000`)

## Configuration

The service is configured via `application.yml`. All values can be overridden with environment variables.

| Environment Variable         | Default                      | Description                                      |
|------------------------------|------------------------------|--------------------------------------------------|
| `KEYCLOAK_AUTH_URL`          | `http://localhost:9000`      | Keycloak server URL                              |
| `KEYCLOAK_REALM`             | `system`                     | Keycloak realm name                              |
| `KEYCLOAK_MANAGER_CLIENT`    | `system-manager-service`     | Client ID used to authenticate with Keycloak     |
| `KEYCLOAK_MANAGER_SECRET`    | `pdjofnwondhwinskwe`         | Client secret for the above client               |
| `USER_MANAGER_URL`           | `http://localhost:8080`      | Base URL of the downstream User Manager service  |

The service runs on port **8210**.

## Running the Service

```bash
mvn spring-boot:run
```

Or with environment overrides:

```bash
KEYCLOAK_AUTH_URL=http://keycloak:9000 KEYCLOAK_REALM=myrealm mvn spring-boot:run
```

## API

Base path: `/v1/user`

Interactive API documentation is available via Swagger UI at `http://localhost:8210/swagger-ui.html` when the service is running.

### POST `/v1/user`

Creates a new user in Keycloak and assigns them to the `system-users` group. A passphrase is generated and returned — this is the only time it is available.

**Request body:**

```json
{
  "systemUserId": "550e8400-e29b-41d4-a716-446655440000",
  "firstName": "Jane",
  "middleName": "A.",
  "lastName": "Doe",
  "email": "jane.doe@example.com"
}
```

**Response `201 Created`:**

```json
{
  "systemUserId": "550e8400-e29b-41d4-a716-446655440000",
  "password": "apple-mango-granite-beyond"
}
```

---

### GET `/v1/user/{email}`

Retrieves a user's details from Keycloak by their email address.

**Response `200 OK`:**

```json
{
  "keycloakId": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
  "username": "jane.doe@example.com",
  "email": "jane.doe@example.com",
  "firstName": "Jane",
  "lastName": "Doe",
  "emailVerified": true,
  "enabled": true
}
```

---

### PUT `/v1/user/{email}/password`

Generates a new passphrase and sets it on the user's Keycloak account.

**Response `200 OK`:**

```json
{
  "password": "river-jacket-spiral-motion"
}
```

---

### DELETE `/v1/user/rollback/{email}`

Deletes a user from Keycloak. Intended for use as a Saga compensation step to roll back a prior `POST /v1/user` call.

**Response `200 OK`** (empty body)

---

## Password Generation

Passwords are generated as hyphen-separated English passphrases (e.g. `apple-mango-granite-beyond`). The generation rules are:

- Minimum total length: **17 characters**
- Minimum number of words: **3**
- Minimum length per word: **4 characters**
- Default number of words: **4**
- No duplicate words (case-insensitive)
- Up to **5** generation attempts before giving up

The word list is loaded from `src/main/resources/data/english-words.json` at startup. Word lookup uses a binary search over a pre-sorted index for efficient random selection.

## Error Responses

All error responses use the following structure:

```json
{
  "message": "Validation failed",
  "errors": {
    "email": ["must be a well-formed email address"]
  }
}
```

| Status | Cause                                      |
|--------|--------------------------------------------|
| `400`  | Invalid request body or path variable      |
| `404`  | User not found in Keycloak                 |
| `409`  | User already exists in Keycloak            |
| `503`  | Could not communicate with Keycloak        |
| `500`  | Unexpected server error                    |

## Running Tests

```bash
mvn test
```

Tests use JUnit 5 and Mockito. No running Keycloak instance is required.
