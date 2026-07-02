# ms-logistica

## Estado de evidencia

| Categoria | Estado |
| --- | --- |
| Implementado | CRUD de envios, tracking, consumidor RabbitMQ, DLQ, JWT, Actuator |
| Configurado | MySQL, RabbitMQ, perfiles `local/aws`, Docker |
| Validado | compilacion |
| Pendiente runtime | consumo real extremo a extremo del evento `Pedido_Pagado` y despliegue AWS |
| No evidenciado | operacion AMQP completa en AWS |

## 1. Descripcion general

Microservicio encargado de envios y tracking de pedidos. Expone operaciones REST para consultar y actualizar envios y, ademas, consume eventos de RabbitMQ relacionados con pedidos pagados.

## 2. Rol dentro de la arquitectura

- API Gateway: entrada oficial para `/api/v1/logistica/**`.
- BFF: fuente de datos para tracking y dashboard agregado.
- Persistencia: MySQL propia.
- RabbitMQ: consumidor de eventos con cola principal y DLQ.

Flujo base:

`Frontend -> API Gateway -> Logistica -> MySQL`

Flujo asincrono:

`Pedidos -> RabbitMQ -> Logistica -> MySQL / DLQ`

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
- Docker

## 4. Puerto y exposicion

| Item | Valor |
| --- | --- |
| Puerto interno | `8084` |
| Configuracion | `${SERVER_PORT:8084}` |
| Exposicion publica oficial | via API Gateway |
| Exposicion directa recomendada | no |

## 5. Perfiles soportados

| Perfil | Uso | Estado |
| --- | --- | --- |
| `local` | Docker local / desarrollo | Configurado |
| `aws` | ECS Fargate + RDS/RabbitMQ | Configurado |

Notas:

- El perfil por defecto es `local`.
- `ddl-auto` se mantiene en `update`.
- RabbitMQ se configura por variables en ambos perfiles.

## 6. Variables de entorno requeridas

### Comunes

| Variable | Uso |
| --- | --- |
| `SERVER_PORT` | puerto HTTP |
| `JWT_SECRET` | secreto JWT |
| `APP_SECURITY_DOCS_PUBLIC` | docs publicas en local |

### Base de datos

| Variable | Local | AWS |
| --- | --- | --- |
| `DB_HOST` | opcional, default `localhost` | requerida |
| `DB_PORT` | opcional, default `3306` | requerida |
| `DB_NAME` | opcional, default `innovatech_logistica` | requerida |
| `DB_USERNAME` | opcional | requerida |
| `DB_PASSWORD` | opcional | requerida |

Compatibilidad adicional:

- `LOGISTICA_MYSQL_HOST`
- `LOGISTICA_MYSQL_PORT`
- `LOGISTICA_MYSQL_DATABASE`
- `LOGISTICA_MYSQL_USERNAME`
- `LOGISTICA_MYSQL_PASSWORD`

### RabbitMQ

| Variable | Uso |
| --- | --- |
| `RABBITMQ_HOST` | host broker |
| `RABBITMQ_PORT` | puerto broker |
| `RABBITMQ_USERNAME` | usuario broker |
| `RABBITMQ_PASSWORD` | password broker |

## 7. Endpoints principales

| Metodo | Ruta | Uso |
| --- | --- | --- |
| `GET` | `/api/v1/logistica/**` | consultas de tracking/envios |
| `POST/PUT/DELETE` | `/api/v1/logistica/**` | operaciones del dominio |
| `GET` | `/actuator/health` | healthcheck |
| `GET` | `/actuator/info` | informacion operativa |

## 8. Integracion y dependencias

| Componente | Tipo | Estado |
| --- | --- | --- |
| API Gateway | HTTP entrante | Evidenciado |
| BFF | HTTP interno | Evidenciado |
| MySQL | persistencia | Evidenciado |
| RabbitMQ | consumo AMQP | Evidenciado por configuracion y codigo |
| DLQ | manejo de fallos AMQP | Evidenciado por configuracion |

## 9. Docker y build

- `Dockerfile` presente y validado.
- Imagen preparada para `SPRING_PROFILES_ACTIVE=aws` por defecto en contenedor.
- `docker-compose.yml` local inyecta `DB_*` y `RABBITMQ_*` con perfil `local`.

Comandos utiles:

```bash
./mvnw.cmd -q -DskipTests compile
docker build -t innovatech-logistica .
```

## 10. Estado actual de validacion

- `Validado`: compilacion.
- `Configurado`: perfiles `local/aws`, MySQL, RabbitMQ, DLQ, Docker.
- `Pendiente runtime`: consumo real extremo a extremo desde Pedidos y verificacion operativa en AWS.
