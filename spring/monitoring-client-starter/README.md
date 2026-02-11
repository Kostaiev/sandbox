# Monitoring Client Starter

This module helps every sandbox service connect to the Admin monitoring module. It already packs Spring Boot Actuator, the Admin client, and the Prometheus registry plus default properties, so you do not copy the same config again and again.

---

## What is inside
- Maven profile named `monitoring-client-starter` that turns on when the Spring profile `monitoring` is active.
- Dependencies you normally add by hand: `spring-boot-starter-actuator`, `spring-boot-admin-starter-client`, `micrometer-registry-prometheus`.
- The `monitoring.properties` file that opens all actuator endpoints, shows full health info, and points the Admin client to `http://localhost:8088`.

---

## When to use it
Use this starter whenever you want a module to appear in the Admin UI and in Prometheus/Grafana. It is great for the small AI demos or any new Spring Boot experiment.

---

## How to enable it
### 0. Add the dependency
Put the starter into your module `pom.xml`:
```xml
<dependency>
    <groupId>com.sandbox</groupId>
    <artifactId>monitoring-client-starter</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

### 1. Import the shared properties
Tell Spring to load the defaults from the classpath:
```
spring.config.import=optional:classpath:monitoring.properties
```

### 2. Run with the monitoring profile
From the repo root (example `spring/ai/connection`):
```bash
mvn -pl spring/ai/connection spring-boot:run -Dspring-boot.run.profiles=monitoring
```
This activates the `monitoring` Spring profile which in turn activates the Maven profile inside this starter.

Prefer to enable the Maven profile directly? Pass:
```bash
mvn -pl spring/ai/connection -P monitoring-client-starter spring-boot:run
```

### 3. Or use environment variables
```bash
export SPRING_PROFILES_ACTIVE=monitoring
mvn -pl spring/ai/connection spring-boot:run
```

### 4. Customize defaults (optional)
Create `.yml` or `.properties` and override the values you need (for example a different Admin URL).

---

## Check that it works
- Application logs list `monitoring` inside the active profiles.
- `http://localhost:<service-port>/actuator/health` shows detailed output.
- The Admin UI (`http://localhost:8088`) displays the service shortly after it starts.
- `curl http://localhost:<service-port>/actuator/prometheus | head` prints Micrometer metrics for Prometheus.
