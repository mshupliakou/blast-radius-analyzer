# ⚛️ Architecture Blast Radius Analyzer

A full-stack visualization and analysis tool designed to map microservice ecosystems, track team ownership, and calculate the "blast radius" (impact zone) of potential service failures using graph data modeling with Neo4j.

## ✨ Capabilities

### 🔬 Blast Radius Analysis
When a microservice fails, which other services are affected? The tool traverses the dependency graph to find every upstream service that depends (directly or transitively) on the failing one — showing the full impact zone. This helps prioritize which services to fix first and communicate risk to stakeholders.

### 🗺️ Interactive Topology Mapping
Visually build your system architecture node by node. Add microservices (Java, Go, Node.js, Python, etc.), databases (PostgreSQL, Redis, MongoDB), and caches. Drag to create connections, reorganize the graph, and export the visualization. Built with Vis.js for smooth physics-based rendering.

### 👥 Organizational Context
Register teams and add engineers. Assign each microservice to its owning team. The graph color-codes services by team, making it easy to see who is responsible for what and how team boundaries align (or misalign) with dependency chains.

### 🏷️ Clusters & Documentation
Group related microservices into colored clusters (e.g. "Payment Domain", "User Platform"). Attach floating sticky notes or node-anchored notes to document architectural decisions, known issues, or migration plans — all persisted in the graph database.

### 🔐 Authentication & Multi-tenant Isolation
JWT-based authentication with per-user project isolation. Each user's graph data is fully separated — no cross-user leakage. Register and log in via the API.

## 🛠️ Tech Stack

**Backend:**
* Java 21
* Spring Boot 3 (Web, Security, RESTful APIs)
* Neo4j Java Driver (Cypher Query Language)
* JWT (jjwt) for authentication
* Testcontainers for integration testing

**Frontend:**
* HTML5 / CSS3
* Vanilla JavaScript
* Tailwind CSS (glassmorphism UI)
* Vis.js Network (graph rendering)
* FontAwesome 6

## ✅ Testing

The project includes both unit and integration tests:

| Layer | Test | What it covers |
|---|---|---|
| `ProjectServiceTest` | 5 unit tests | Project CRUD, ownership validation, user isolation |
| `JwtServiceTest` | 7 unit tests | Token generation, validation, uniqueness, edge cases |
| `AuthControllerTest` | 5 integration tests | Registration, duplicate detection, login, wrong password, non-existent user — uses mocked repository |
| `BlastRadiusAnalyzerApplicationTests` | 1 smoke test | Application context loads with all beans |
| `MicroserviceRepositoryTest` | 7 integration tests | CRUD operations, blast radius traversal, dependency chaining, clustering, project isolation — uses Testcontainers with real Neo4j |

**Running tests:**

```bash
# Unit tests + integration tests with mocked Neo4j (no database needed)
./mvnw test

# Full suite including Testcontainers integration tests (requires Docker)
./mvnw verify -Pintegration
```

## ⚙️ Getting Started

### Prerequisites
* Java 21+
* Maven
* Docker (optional — for containerized setup or integration tests)

### Option A: Docker Compose (recommended)

```bash
docker compose up --build
```

This starts both Neo4j and the application. The app will be available at http://localhost:8080.

### Option B: Local development

1. Start a Neo4j instance (local or via Docker):

```bash
docker run -d \
  --name neo4j \
  -p 7687:7687 \
  -e NEO4J_AUTH=neo4j/neo4j \
  -e NEO4J_dbms_default__database=microservices \
  neo4j:5
```

2. Run the application:

```bash
./mvnw spring-boot:run
```

The application will be available at http://localhost:8080.

### Configuration

All settings have sensible defaults and can be overridden via environment variables:

| Variable | Default | Description |
|---|---|---|
| `NEO4J_URI` | `neo4j://localhost:7687` | Neo4j connection URI |
| `NEO4J_USERNAME` | `neo4j` | Database user |
| `NEO4J_PASSWORD` | `neo4j` | Database password |
| `NEO4J_DATABASE` | `microservices` | Database name |
| `JWT_SECRET` | *(embedded)* | JWT signing secret |

## 🧠 How it Works (Cypher Example)

The blast radius calculation leverages Neo4j's graph traversal. When analyzing a target service, the backend finds all upstream services that transitively depend on it:

```cypher
MATCH (m:Microservice)-[:DEPENDS_ON*1..]->(target {id: $targetId})
RETURN DISTINCT m.id AS id, m.name AS name, m.language AS language
```

## 🐳 Docker

| File | Purpose |
|---|---|
| `Dockerfile` | Multi-stage build — compiles with Maven, runs with JRE 21 |
| `docker-compose.yml` | Orchestrates Neo4j 5 + app with health checks and networking |
