# Spring Sandbox

This folder holds several Maven modules that show different Spring ideas.

## Modules
### `spring/admin`
- Spring Boot Admin server used to watch health, metrics, and logs for every sandbox service.
- Runs on port `8088` and already includes Prometheus and Grafana via `spring/admin/docker-compose.yml`.
- When you start it from an IDE the Docker stack also starts automatically because of the `spring.docker.compose.*` settings in `application.properties`.
- Detailed setup is in `spring/admin/README.md`.

### `spring/ai`
- Group of Spring AI demos such as `connection`, `memory`, `roles`, and `...`.
- Each demo has its own README with provider notes and curl examples.
- Run a specific demo with `mvn -pl spring/ai/<module> spring-boot:run`. Point the Admin module and Prometheus scrape targets at the demo’s actuator port.

### `spring/monitoring-client-starter`
- Shared starter that adds Actuator, Admin client, and Prometheus registry so any module can attach to the Admin server quickly.
