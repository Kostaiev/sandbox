# SimpleAdminApp

Learning Spring Boot Admin server that centralizes monitoring dashboards for the sandbox services.

The goal of this module is to keep a lightweight admin console backed by Prometheus + Grafana so that every experimental Spring Boot app can expose health, metrics, and logs in one place.

---

## Tech stack
- Java 21
- Spring Boot 3.5.x
- Spring Boot Admin Server 3.4.x
- Spring Boot Actuator
- Micrometer + Prometheus registry
- Docker Compose (Prometheus + Grafana)
- Maven

---

## Modules / structure
- `SimpleAdminApp` — Spring Boot Admin server on port `8088` with every actuator endpoint exposed.
- `docker-compose.yml` — starts Prometheus and Grafana on the same host network for zero-config local access.
- `prometheus.yml` — scrapes `/actuator/prometheus` from local services (default target: `localhost:8080`).
- `grafana/provisioning/datasources/datasource.yml` — preloads Prometheus as the default Grafana datasource.

---

## What you get
- Central UI for registered Spring Boot applications (status, metrics, log streaming).
- Ready-to-use metrics pipeline (Micrometer → Prometheus → Grafana) with persistent volumes.
- Quick health overview for experiments such as `spring/ai/connection` or any other actuator-enabled app.

---

## Local setup
### Prerequisites
- JDK 21+
- Maven 3.9+
- Docker Engine with the Compose plugin

### Steps
1. Start the admin server (from the repo root):
   ```bash
   mvn -pl spring/admin spring-boot:run
   ```
2. In a second terminal, launch Prometheus + Grafana:
   ```bash
   docker compose -f spring/admin/docker-compose.yml up -d
   ```
   > If you start the app from an IDE, Spring Boot's Docker Compose integration uses
   > `spring.docker.compose.file=./spring/admin/docker-compose.yml` and
   > `spring.docker.compose.stop.command=down` from `application.properties`, so the
   > monitoring stack can spin up and shut down automatically without opening a
   > separate terminal.
3. Open `http://localhost:8088` to access the Spring Boot Admin UI.
4. Prometheus listens on `http://localhost:9090`, Grafana on `http://localhost:3000` (default credentials `admin` / `admin`).

To stop the monitoring stack, run `docker compose -f spring/admin/docker-compose.yml down -v`.

---

## Registering client applications
Add the Spring Boot Admin client starter dependency and expose actuator endpoints in any app you want to observe. Example `application.yml` fragment:
```yaml
spring:
  application:
    name: sandbox-connection
  boot:
    admin:
      client:
        url: http://localhost:8088
        instance:
          management-base-url: http://localhost:8080
management:
  endpoints:
    web:
      exposure:
        include: "*"
```
> Replace `management-base-url` and port values with the real address of your service.

### Metrics scraping
Ensure the client app adds `micrometer-registry-prometheus`, then expose metrics via `/actuator/prometheus`. Update `spring/admin/prometheus.yml` targets if your service is not on `localhost:8080`.

---

## Verification
- `curl http://localhost:8088/actuator/health` — admin server health.
- `curl http://localhost:8088/applications` — list of registered applications.
- Grafana → Explore tab should list the Prometheus data source and show metrics such as `jvm_memory_used_bytes` after at least one client app is running.
