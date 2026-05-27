# ms-logistica

## 1. Descripcion general
`ms-logistica` es el microservicio encargado de gestionar envios y tracking de pedidos dentro de InnovaTech. Expone operaciones REST para crear, consultar, actualizar y eliminar envios, y ademas consume eventos RabbitMQ asociados a pagos de pedidos.

## 2. Rol dentro de la arquitectura
El servicio es consumido a traves del API Gateway en `/api/v1/logistica/**` y tambien por el BFF para componer vistas de tracking. Persiste informacion en MySQL y consume eventos AMQP desde RabbitMQ.

Flujo simple:

`Cliente/Frontend -> API Gateway -> ms-logistica -> Base de datos / RabbitMQ`

Relaciones evidenciadas:

- API Gateway enruta hacia `http://logistica:8084` en Docker.
- BFF consulta este servicio mediante `MS_LOGISTICA_URL`.
- El servicio usa MySQL en desarrollo y produccion.
- El servicio consume mensajes con `@RabbitListener` desde la cola configurada en `logistica.rabbitmq.queue`.

## 3. Stack tecnico
- Java 21
- Spring Boot 3.5.14
- Maven Wrapper
- Spring Web
- Spring Data JPA
- Spring Security
- JWT
- Spring AMQP / RabbitMQ
- MySQL
- Spring Boot Actuator
- Springdoc OpenAPI
- Docker

## 4. Puerto del servicio

| Concepto | Valor |
|---|---|
| Puerto esperado | 8084 |
| Puerto configurado | `${SERVER_PORT:8084}` |
| Archivo donde se define | `src/main/resources/application.yml`, `Dockerfile` |
| Variable de entorno asociada | `SERVER_PORT` |

## 5. Variables de entorno

| Variable | Descripcion | Valor por defecto | Obligatoria | Riesgo/observacion |
|---|---|---|---|---|
| `SERVER_PORT` | Puerto HTTP del microservicio | `8084` | No | Debe mantenerse alineado con Gateway y Docker. |
| `SPRING_PROFILES_ACTIVE` | Perfil activo de Spring | `dev` | No | Cambia datasource, JPA y docs. |
| `JWT_SECRET` | Secreto para validacion JWT | Sin valor por defecto | Si en entornos no locales | No debe versionarse. |
| `APP_SECURITY_DOCS_PUBLIC` | Habilita documentacion publica | `false` | No | En produccion debe mantenerse controlado. |
| `LOGISTICA_MYSQL_HOST` | Host MySQL | `localhost` en dev | Si | En Docker local del repo puede venir desde compose. |
| `LOGISTICA_MYSQL_PORT` | Puerto MySQL | `3306` | No | Debe coincidir con la infraestructura. |
| `LOGISTICA_MYSQL_DATABASE` | Base de datos MySQL | `logistica_db` | Si | Se evidencia estrategia de base dedicada. |
| `LOGISTICA_MYSQL_USERNAME` | Usuario MySQL | `logistica_local` en dev | Si | No incluir secretos reales. |
| `LOGISTICA_MYSQL_PASSWORD` | Password MySQL | `logistica_local_password` en dev | Si | No incluir secretos reales. |
| `RABBITMQ_HOST` | Host RabbitMQ | `localhost` en dev | Si cuando RabbitMQ esta integrado | En prod se define por entorno. |
| `RABBITMQ_PORT` | Puerto RabbitMQ | `5672` | No | Debe coincidir con la infraestructura. |
| `RABBITMQ_USERNAME` | Usuario RabbitMQ | `rabbit_local_user` en dev | Si en prod | No usar `guest` en produccion. |
| `RABBITMQ_PASSWORD` | Password RabbitMQ | `rabbit_local_password` en dev | Si en prod | No incluir secretos reales. |
| `LOGISTICA_RABBITMQ_EXCHANGE` | Exchange principal de pedidos pagados | `pedido.exchange` | No | Se evidencia como configurable. |
| `LOGISTICA_RABBITMQ_QUEUE` | Cola principal de consumo | `pedido.pagado.queue` | No | Debe existir o ser declarada por la app. |
| `LOGISTICA_RABBITMQ_ROUTING_KEY` | Routing key principal | `pedido.pagado` | No | Debe alinearse con el productor. |
| `LOGISTICA_RABBITMQ_DLX` | Dead-letter exchange | `pedido.dlx` | No | Permite aislar mensajes fallidos. |
| `LOGISTICA_RABBITMQ_DLQ` | Dead-letter queue | `pedido.pagado.dlq` | No | Util para diagnostico operativo. |
| `LOGISTICA_RABBITMQ_DLK` | Dead-letter routing key | `pedido.pagado.dead` | No | Debe alinearse con la topologia AMQP. |

## 6. Base de datos
El servicio usa MySQL tanto en desarrollo como en produccion, con parametros distintos por perfil.

| Elemento | Valor |
|---|---|
| Motor | MySQL |
| Base de datos | `logistica_db` |
| Entidades | `Envio` |
| Repositories | `EnvioRepository` |
| ddl-auto | `update` en dev, `validate` en prod |
| show-sql | `true` en dev, `false` en prod |

Riesgos o pendientes:

- En produccion el esquema debe existir previamente porque el servicio usa `validate`.
- No se evidencian migraciones Flyway o Liquibase en este repositorio.

## 7. Endpoints principales

| Metodo | Endpoint | Descripcion | Auth requerida | Request | Response |
|---|---|---|---|---|---|
| `POST` | `/api/v1/logistica` | Crea un envio manualmente | Si | `EnvioRequestDTO` | `EnvioResponseDTO` |
| `GET` | `/api/v1/logistica` | Lista todos los envios | Si | No aplica | `List<EnvioResponseDTO>` |
| `GET` | `/api/v1/logistica/{id}` | Obtiene un envio por identificador | Si | No aplica | `EnvioResponseDTO` |
| `GET` | `/api/v1/logistica/pedido/{pedidoId}` | Busca envio por pedido | Si | No aplica | `EnvioResponseDTO` |
| `PUT` | `/api/v1/logistica/{id}` | Actualiza datos del envio | Si | `EnvioRequestDTO` | `EnvioResponseDTO` |
| `PATCH` | `/api/v1/logistica/{id}/estado` | Actualiza estado del envio | Si | `ActualizarEstadoDTO` | `EnvioResponseDTO` |
| `DELETE` | `/api/v1/logistica/{id}` | Elimina un envio | Si | No aplica | No evidenciado |

## 8. Seguridad
- Usa Spring Security: si.
- Valida JWT: si.
- Depende del Gateway: el flujo oficial es via Gateway, aunque el servicio tambien protege sus endpoints.
- Endpoints publicos: `GET /actuator/health`, `GET /actuator/info`, Swagger/OpenAPI solo cuando `APP_SECURITY_DOCS_PUBLIC=true`.
- Endpoints protegidos: los endpoints `/api/v1/logistica/**`.
- Riesgos detectados:
  - Si se expone directamente fuera de la red interna, el trafico podria evitar el Gateway aunque seguiria bajo JWT.
  - La documentacion publica depende de variable y perfil.

## 9. Integraciones

| Origen | Destino | Tipo | URL/variable | Estado |
|---|---|---|---|---|
| API Gateway | `ms-logistica` | HTTP | `http://logistica:8084` en Docker | Evidenciado |
| BFF | `ms-logistica` | HTTP | `MS_LOGISTICA_URL` | Evidenciado |
| `ms-logistica` | Base de datos propia | JDBC/JPA | `LOGISTICA_MYSQL_*` | Evidenciado |
| RabbitMQ | `ms-logistica` | AMQP consumo | `RABBITMQ_HOST`, `RABBITMQ_PORT`, `RABBITMQ_USERNAME`, `RABBITMQ_PASSWORD` | Evidenciado |

## 10. Eventos RabbitMQ

| Evento | Exchange | Routing key | Queue | Productor/Consumidor | Estado |
|---|---|---|---|---|---|
| `Pedido_Pagado` | `pedido.exchange` | `pedido.pagado` | `pedido.pagado.queue` | Consumidor | Evidenciado |

Observaciones:

- Se evidencia una DLQ configurada con `pedido.dlx`, `pedido.pagado.dlq` y `pedido.pagado.dead`.
- El productor del evento no se evidencia dentro de este repositorio.

## 11. Ejecucion local

```bash
./mvnw clean package
./mvnw spring-boot:run
```

Perfil de produccion:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

Con Docker Compose local del repositorio:

```bash
docker compose up --build
```

Puntos utiles:

- Swagger UI: `http://localhost:8084/swagger-ui.html` cuando la documentacion publica esta habilitada.
- OpenAPI: `http://localhost:8084/api-docs`
- Health: `http://localhost:8084/actuator/health`
- Info: `http://localhost:8084/actuator/info`
