# Relay — Real-Time Chat Backend

Relay is a Spring Boot backend for authenticated, real-time messaging. It combines REST APIs for account management and message history with WebSockets using STOMP for live message delivery. The application uses PostgreSQL for persistence and JWTs for authentication.

## Live Demo

- **Frontend:** https://graceful-snickerdoodle-1af48c.netlify.app/
- **Backend:** https://relay-7sl3.onrender.com/
- **Source Code:** https://github.com/shivansh-rgb-code/relay

## Features

- User registration and login
- Password hashing with BCrypt
- JWT-based authentication
- Protected user discovery
- REST APIs for sending messages and retrieving conversation history
- Real-time messaging using WebSockets and STOMP
- PostgreSQL persistence for users and messages
- Request validation and authenticated WebSocket connections
- Sender and recipient message delivery through user-specific queues

## Tech Stack

- **Language:** Java 25
- **Framework:** Spring Boot
- **Security:** Spring Security, JWT, BCrypt
- **Database:** PostgreSQL
- **Persistence:** Spring Data JPA / Hibernate
- **Real-Time Communication:** WebSocket / STOMP
- **Build Tool:** Maven

## Prerequisites

- JDK 25
- PostgreSQL (for local development)
- Git

## Configuration

Configure the required environment variables before running the application locally or deploying it.

| Variable | Description |
|---|---|
| `DB_URL` | PostgreSQL JDBC connection URL |
| `DB_USERNAME` | Database username |
| `DB_PASSWORD` | Database password |
| `JWT_SECRET` | Strong secret used to sign JWTs |
| `JWT_EXPIRATION` | JWT lifetime in milliseconds; `86400000` equals 24 hours |
| `ALLOWED_ORIGINS` | Comma-separated list of permitted frontend origins for CORS and WebSocket connections |

See `.env.example` for the expected configuration names. It is a template; Spring Boot does not automatically load a `.env` file. Supply the variables through your environment or hosting provider's configuration settings.

Never commit real credentials or production secrets.

## Run Locally

1. Clone the repository:

   ```powershell
   git clone https://github.com/shivansh-rgb-code/relay.git
   cd relay
   ```

2. Create a PostgreSQL database.
3. Configure the required environment variables using your local environment.
4. Start the application:

   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

The application normally runs at `http://localhost:8080`, unless configured otherwise.

## REST API

### Base URLs

- **Local:** `http://localhost:8080`
- **Deployed:** `https://relay-7sl3.onrender.com`

| Method | Endpoint | Purpose | Authentication |
|---|---|---|---|
| `POST` | `/api/auth/register` | Register a user | Not required |
| `POST` | `/api/auth/login` | Authenticate and obtain a JWT | Not required |
| `GET` | `/api/users` | List other users | Bearer token |
| `POST` | `/api/messages` | Send a message | Bearer token |
| `GET` | `/api/messages/{userId}` | Retrieve conversation history | Bearer token |

### Register

**Endpoint:** `POST /api/auth/register`

Example request body:

```json
{
  "name": "Relay User",
  "email": "user@example.com",
  "password": "ChangeThisPassword123!"
}
```

Successful registration returns `201 Created` with the user's `id`, `name`, `email`, and a message.

### Login

**Endpoint:** `POST /api/auth/login`

Example request body:

```json
{
  "email": "user@example.com",
  "password": "ChangeThisPassword123!"
}
```

Successful login returns a JWT:

```json
{
  "token": "<JWT_TOKEN>"
}
```

### Authentication

Protected REST endpoints require the JWT returned by the login endpoint. Include it in the request header:

```http
Authorization: Bearer <JWT_TOKEN>
```

### List Users

**Endpoint:** `GET /api/users`

Returns users other than the currently authenticated user. Each user contains an `id`, `name`, and `email`.

### Send a Message

**Endpoint:** `POST /api/messages`

Example request body:

```json
{
  "receiverId": 2,
  "content": "Hello from Relay!"
}
```

Use the authenticated sender's JWT and replace `receiverId` with the ID of the intended recipient.

The response contains the saved message's `id`, `senderId`, `receiverId`, `content`, and `createdAt`.

### Retrieve Conversation History

**Endpoint:** `GET /api/messages/{userId}`

Replace `{userId}` with the other participant's numeric user ID. The endpoint returns the conversation history between the authenticated user and that participant.

## Real-Time WebSocket Messaging

Relay supports real-time message delivery through WebSockets using the STOMP protocol.

| Setting | Value |
|---|---|
| WebSocket endpoint | `/ws` |
| Deployed WebSocket URL | `wss://relay-7sl3.onrender.com/ws` |
| Protocol | STOMP over WebSocket |
| Application destination | `/app/chat.send` |
| User-specific subscription | `/user/queue/messages` |

### Authentication and Message Flow

JWT authentication takes place in the STOMP `CONNECT` frame, using a native `Authorization` header. It is not performed through the initial HTTP WebSocket handshake.

Use the following header in the STOMP `CONNECT` frame:

```text
Authorization: Bearer <JWT_TOKEN>
```

A typical messaging flow is:

1. Establish a WebSocket connection to `/ws`.
2. Send a STOMP `CONNECT` frame containing the JWT authorization header.
3. Subscribe to `/user/queue/messages`.
4. Send a STOMP `SEND` frame to `/app/chat.send`.

Example message body:

```json
{
  "receiverId": 2,
  "content": "Hello in real time!"
}
```

The backend persists the message and delivers the saved message to both the recipient and the sender through their user-specific queues.

For production use, connect through the secure `wss://` endpoint.

## Database

Relay uses PostgreSQL to store user accounts and messages. Message records include the sender, receiver, content, and creation timestamp.

Hibernate schema updates are configured for development. Review the schema-management configuration before production use; a database migration tool is preferable for controlled production schema changes.

## Testing with Postman

A Postman collection is provided in the repository at `postman/Relay_Postman_Collection.json` 

To test the REST API:

1. Register two users with different email addresses.
2. Log in with one account to obtain a JWT.
3. List users and identify the other user's ID.
4. Send a message to that user.
5. Retrieve the conversation history.
6. Test protected endpoints with a missing or invalid token.

Use separate authenticated sessions to test real-time messaging between two users.

## Security Notes

- Never commit passwords, JWT secrets, database credentials, or real JWTs.
- Use a strong, randomly generated JWT secret.
- Keep production database access restricted.
- Configure permitted frontend origins carefully.
- Use HTTPS and WSS in production.
- Review authentication, authorization, and database configuration before production use.

## Deployment

The backend is deployed on Render, and the frontend is hosted on Netlify.

- **Backend URL:** https://relay-7sl3.onrender.com/
- **Frontend URL:** https://graceful-snickerdoodle-1af48c.netlify.app/

Production configuration, including database credentials, JWT settings, and allowed origins, should be managed through the hosting provider's environment settings rather than committed to the repository.

## Project Status

The backend and frontend are deployed, and real-time messaging has been verified.
