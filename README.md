# taskflow-api

API REST para gestión de tareas con **Spring Boot 4**, **Java 25**, **PostgreSQL** y autenticación **JWT**. Incluye paginación, filtros, ordenación, documentación OpenAPI (Swagger) y pruebas con Testcontainers.

## Stack

- **Java 25** · **Spring Boot 4.0.2**
- **PostgreSQL 15** (Docker)
- **JWT** (jjwt) para autenticación
- **Springdoc OpenAPI 3** (Swagger UI)
- **MapStruct** · **Lombok** · **JPA/Hibernate**
- **Testcontainers** para tests de integración

## Requisitos

- **JDK 25**
- **Maven** (o usar el wrapper `./mvnw` incluido)
- **Docker** y **Docker Compose** (para PostgreSQL y para los tests de integración)

## Cómo ejecutar

### 1. Levantar PostgreSQL

```bash
docker compose up -d
```

Base de datos: `demo_db`, usuario `demo_user`, contraseña `demo_pass`, puerto `5432`.

### 2. Arrancar la aplicación

```bash
./mvnw spring-boot:run
```

La API queda en **http://localhost:8080**.

### 3. Documentación e interfaz Swagger

- **Swagger UI:** http://localhost:8080/swagger-ui.html  
- **OpenAPI JSON:** http://localhost:8080/v3/api-docs  

## API

### Autenticación

| Método | Ruta | Descripción |
|--------|------|-------------|
| `POST` | `/api/v1/auth/login` | Login. Body: `{"username","password"}`. Devuelve `token` y `type: "Bearer"`. |

**Usuarios de prueba** (cargados al arrancar si no existen):

| Usuario | Contraseña |
|---------|------------|
| `user`  | `user123`  |
| `admin` | `admin123` |

El resto de endpoints requieren el header: `Authorization: Bearer <token>`.

### Tareas

| Método | Ruta | Descripción |
|--------|------|-------------|
| `GET`  | `/api/v1/tasks` | Lista paginada con filtros y ordenación. |
| `GET`  | `/api/v1/tasks/{id}` | Obtiene una tarea por ID. |
| `POST` | `/api/v1/tasks` | Crea una tarea. Body: `title` (obligatorio), `description`, `completed`. |
| `PUT`  | `/api/v1/tasks/{id}` | Actualiza una tarea. |
| `DELETE` | `/api/v1/tasks/{id}` | Elimina una tarea (204). |

#### Listado (GET /api/v1/tasks)

- **Paginación:** `page` (default 0), `size` (default 20, mín. 10, máx. 100).
- **Ordenación:** `sort=campo,dirección` (ej. `createdAt,desc`). Campos: `id`, `title`, `description`, `completed`, `createdAt`, `updatedAt`.
- **Filtros (todos opcionales):**
  - `title` — texto en el título (contiene).
  - `description` — texto en la descripción (contiene).
  - `completed` — `true` o `false`.
  - `createdAtAfter`, `createdAtBefore` — fechas ISO-8601.
  - `updatedAtAfter`, `updatedAtBefore` — fechas ISO-8601.

La respuesta es un objeto con `content` (lista de tareas), `totalElements`, `totalPages`, `size`, `number`, `first`, `last`.

### Ejemplo rápido (curl)

```bash
# Login
curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user","password":"user123"}'

# Listar tareas (sustituir TOKEN por el valor de "token" del login)
curl -s http://localhost:8080/api/v1/tasks \
  -H "Authorization: Bearer TOKEN"
```

## Tests

```bash
./mvnw verify
```

- **Tests unitarios:** `TaskServiceTest`, `TaskControllerTest`.
- **Tests de integración:** `TaskControllerIntegrationTest`, `DemoApplicationTests` (usan Testcontainers con PostgreSQL).

Para que pasen los de integración, Docker debe estar en marcha.

## CI (GitHub Actions)

En cada push o pull request a `main` o `master` se ejecuta:

- Checkout, JDK 25, Maven
- `./mvnw -B verify` (compilación y tests)

Configuración en `.github/workflows/ci.yml`.

## Estructura del proyecto

```
src/main/java/com/back/demo/
├── config/          # OpenAPI, DataLoader (usuarios y tareas de ejemplo)
├── controller/     # AuthController, TaskController
├── dto/            # Request/Response, PagedResponse, TaskFilter
├── exception/      # GlobalExceptionHandler, ResourceNotFoundException
├── mapper/         # MapStruct TaskMapper
├── model/          # Task, User
├── repository/     # JPA + TaskSpecifications (filtros)
├── security/       # JWT (JwtService, JwtAuthFilter), SecurityConfig
└── service/        # AuthService, TaskService
```

## Licencia

MIT.
