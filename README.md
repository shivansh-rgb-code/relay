# Relay — Real-Time Chat Backend

Relay is a Spring Boot backend for authenticated, real-time messaging. It combines REST APIs for account management and message history with WebSockets using STOMP for live message delivery.

## Features

- User registration and login
- Password hashing with BCrypt
- JWT-based authentication
- Protected user discovery
- REST endpoints to send messages and retrieve conversation history
- Real-time messaging through WebSockets and STOMP
- PostgreSQL persistence
- Validation and authenticated WebSocket connections

## Tech Stack

- Java 25
- Spring Boot
- Spring Security
- Spring Data JPA / Hibernate
- PostgreSQL
- WebSocket / STOMP
- JSON Web Tokens (JWT)
- Maven

## Prerequisites

- JDK 25
- PostgreSQL
- Git

## Configuration

Configure the following environment variables before running the application:

| Variable | Description |
|---|---|
| `DB_URL` | PostgreSQL JDBC connection URL |
| `DB_USERNAME` | Database username |
| `DB_PASSWORD` | Database password |
| `JWT_SECRET` | Strong secret used to sign JWTs |
| `JWT_EXPIRATION` | Token lifetime in milliseconds; `86400000` is 24 hours |

See `.env.example` for the expected variable names. It is a template; Spring Boot does not automatically load it.

## Run Locally

1. Clone the repository.
2. Create a PostgreSQL database.
3. Set the required environment variables.
4. Run the application:

   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

The application uses the port configured by Spring Boot, normally `8080`.

## REST API

Base URL: `http://localhost:8080`

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/auth/register` | Register a user |
| POST | `/api/auth/login` | Authenticate and obtain a JWT |
| GET | `/api/users` | List other users |
| POST | `/api/messages` | Send a message |
| GET | `/api/messages/{userId}` | Retrieve conversation history |

Protected REST endpoints require:

```http
Authorization: Bearer <JWT>
```

### Send a Message

Example request body:

```json
{
  "receiverId": 2,
  "content": "Hello from Relay!"
}
```

Use the JWT of the authenticated sender.

## WebSocket Messaging

- Endpoint: `/ws`
- Protocol: STOMP over WebSocket
- Application destination: `/app/chat.send`
- Recipient subscription: `/user/queue/messages`

The WebSocket handshake requires a valid JWT in the `Authorization` header:

```http
Authorization: Bearer <JWT>
```

Connect using a STOMP client, authenticate, and subscribe to `/user/queue/messages`. Send messages to `/app/chat.send` with a JSON body containing `receiverId` and `content`.

Messages are persisted and delivered to the recipient's user-specific queue.

## Database

The application stores users and messages in PostgreSQL. Message records include sender, receiver, content, and creation timestamp.

Hibernate schema updates are configured for development. Review database migration and schema-management settings before production use.

## Security Notes

- Never commit real passwords, JWT secrets, or production credentials.
- Use a strong, randomly generated JWT secret.
- Restrict allowed WebSocket origins before production deployment.
- Use HTTPS/WSS in production.
- Configure production database access securely.

## Deployment

The backend can be deployed to a Java-compatible hosting provider such as Render. Configure the database and all required environment variables in the hosting dashboard. Use the deployed HTTPS URL for REST requests and the corresponding secure WebSocket URL for WebSocket connections.

## Current Status

Core REST messaging, message persistence, JWT-authenticated STOMP connections, and real-time message delivery have been tested locally.
