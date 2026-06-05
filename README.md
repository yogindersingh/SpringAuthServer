# SpringAuthServer

A custom OAuth2 Authorization Server built with Spring Authorization Server, Spring Boot 3.4.3, and Java 17. It supports multiple OAuth2 grant types, OpenID Connect 1.0, JWT token generation with role claims, and a MySQL-backed user/role management system.

## Features

- OAuth2 Authorization Server with OpenID Connect 1.0 support
- Four pre-configured OAuth2 clients (Client Credentials, Authorization Code, PKCE)
- JWT (self-contained) and opaque (reference) token types
- RSA-2048 signed JWTs with custom role claims
- MySQL-backed user and role management
- Custom authentication provider with delegating password encoder
- Password breach detection via [HaveIBeenPwned](https://haveibeenpwned.com/Passwords) API

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.4.3 |
| Auth | Spring Authorization Server |
| ORM | Spring Data JPA / Hibernate |
| Database | MySQL |
| Build | Maven |

## Project Structure

```
src/main/java/com/learning/springAuthServer/
├── SpringAuthServerApplication.java        # Entry point
├── config/
│   ├── ProjectSecurityConfig.java          # OAuth2 & security configuration
│   └── CustomerUserDetailsService.java     # DB-backed UserDetailsService
├── entity/
│   ├── Customer.java                       # User entity (UUID PK)
│   └── CustomerRoles.java                  # Role entity (UUID PK)
├── repository/
│   ├── CustomerRepo.java                   # findByEmail (with @PostAuthorize)
│   └── CustomerRolesRepo.java              # findAllByCustomerId
└── provider/
    └── CustomerProvider.java               # Custom AuthenticationProvider
```

## OAuth2 Clients

| Client ID | Grant Type | Token Type | Scopes |
|---|---|---|---|
| `SpringSecLabAPI` | Client Credentials | JWT | OPENID, ADMIN, USER |
| `SpringSecLabIntrospect` | Client Credentials | Opaque | OPENID |
| `SpringSecLabAuthCode` | Authorization Code + Refresh | JWT | OPENID, EMAIL |
| `SpringSecLabPkce` | Authorization Code (PKCE) | JWT | OPENID, EMAIL |

All clients use `https://oauth.pstmn.io/v1/callback` as the redirect URI. Access tokens expire after **10 minutes**; refresh tokens after **8 hours**.

## Endpoints

| Endpoint | Description |
|---|---|
| `POST /oauth2/token` | Token endpoint |
| `GET /oauth2/authorize` | Authorization endpoint |
| `GET /oauth2/jwks` | JWK Set (public keys) |
| `POST /oauth2/introspect` | Token introspection |
| `POST /oauth2/revoke` | Token revocation |
| `GET /.well-known/openid-configuration` | OIDC discovery document |
| `GET /login` | Form login page |

## Prerequisites

- Java 17+
- Maven 3.6+
- MySQL 8+

## Getting Started

### 1. Configure the database

Create a MySQL user or use the defaults. The database `springSecurityDB` is created automatically on first run.

Set environment variables to override defaults:

```bash
export DATABASE_HOST=localhost
export DATABASE_PORT=3306
export DATABASE_NAME=springSecurityDB
export DATABASE_USERNAME=root
export DATABASE_PASSWORD=root@1234
```

### 2. Build and run

```bash
./mvnw clean package
java -jar target/springAuthServer-0.0.1-SNAPSHOT.jar
```

The server starts on **port 8101**.

### 3. Seed a user

Insert a user into the `customer` table and a matching row in `customer_roles`. Use a BCrypt-encoded password prefixed with `{bcrypt}`.

## Example: Client Credentials Flow

```bash
curl -X POST http://localhost:8101/oauth2/token \
  -u "SpringSecLabAPI:VxubZgAXyyTq9lGjj3qGvWNsHtE4SqTq" \
  -d "grant_type=client_credentials&scope=OPENID"
```

## Example: Authorization Code Flow

Direct a browser to:

```
http://localhost:8101/oauth2/authorize
  ?response_type=code
  &client_id=SpringSecLabAuthCode
  &redirect_uri=https://oauth.pstmn.io/v1/callback
  &scope=openid email
```

Exchange the code for a token:

```bash
curl -X POST http://localhost:8101/oauth2/token \
  -u "SpringSecLabAuthCode:Qw3rTy6UjMnB9zXcV2pL0sKjHn5TxQqB" \
  -d "grant_type=authorization_code&code=<code>&redirect_uri=https://oauth.pstmn.io/v1/callback"
```

## Token Claims

Access tokens contain a `roles` claim derived from the authenticated principal's granted authorities:

```json
{
  "sub": "user@example.com",
  "roles": ["ADMIN", "USER"],
  "scope": "openid",
  "iss": "http://localhost:8101",
  "exp": 1234567890
}
```
