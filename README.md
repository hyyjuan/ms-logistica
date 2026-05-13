# ms-logistica — InnovaTech Consulting

Microservicio de seguimiento de pedidos para la plataforma **InnovaTech Consulting**.
Encargado de generar códigos de tracking, gestionar estados de envío y consumir el evento `Pedido_Pagado` desde RabbitMQ.

---

## Stack

| Tecnología      | Uso                              |
|-----------------|----------------------------------|
| Spring Boot 3.2 | Framework principal              |
| PostgreSQL 15   | Base de datos exclusiva (DB-per-Service) |
| RabbitMQ 3.12   | Mensajería asíncrona             |
| Docker          | Contenedorización                |
| Lombok          | Reducción de boilerplate         |

---

## Levantar el proyecto

### Con Docker Compose (recomendado)

```bash
# Construir y levantar todo (ms + postgres + rabbitmq)
docker-compose up --build

# En segundo plano
docker-compose up --build -d

# Ver logs
docker-compose logs -f ms-logistica

# Detener
docker-compose down
```

### En local (sin Docker)

1. Tener PostgreSQL corriendo en `localhost:5432` con base de datos `logistica_db`
2. Tener RabbitMQ corriendo en `localhost:5672`
3. Ejecutar:

```bash
mvn spring-boot:run
```

---

## Endpoints REST

Base URL: `http://localhost:8084/api/v1/logistica`

| Método   | Ruta                       | Descripción                                 |
|----------|----------------------------|---------------------------------------------|
| `POST`   | `/`                        | Crear envío manualmente                     |
| `GET`    | `/`                        | Listar todos los envíos                     |
| `GET`    | `/{id}`                    | Obtener envío por ID                        |
| `GET`    | `/pedido/{pedidoId}`       | Obtener tracking por pedido ← más usado     |
| `PUT`    | `/{id}`                    | Actualizar datos del envío                  |
| `PATCH`  | `/{id}/estado`             | Cambiar estado del envío                    |
| `DELETE` | `/{id}`                    | Eliminar envío                              |

---

## Estados del envío

```
PENDIENTE → PREPARANDO → DESPACHADO → EN_TRANSITO → EN_REPARTO → ENTREGADO
                                                               ↘ FALLIDO → DEVUELTO
```

| Estado       | Descripción                                 |
|--------------|---------------------------------------------|
| `PENDIENTE`  | Pedido pagado, aún sin procesar             |
| `PREPARANDO` | En bodega, empaquetando                     |
| `DESPACHADO` | Salió del almacén                           |
| `EN_TRANSITO`| En camino al destino                        |
| `EN_REPARTO` | Con el repartidor, última milla             |
| `ENTREGADO`  | Confirmación exitosa (registra fecha real)  |
| `FALLIDO`    | Intento de entrega fallido                  |
| `DEVUELTO`   | Paquete devuelto al origen                  |

---

## Ejemplos de uso

### Crear envío manualmente

```json
POST /api/v1/logistica
{
  "pedidoId": 1,
  "usuarioId": 5,
  "nombreDestinatario": "Juan Vargas",
  "direccionDestino": "Av. Providencia 1234",
  "ciudadDestino": "Santiago",
  "regionDestino": "Metropolitana",
  "telefonoContacto": "+56912345678"
}
```

### Cambiar estado

```json
PATCH /api/v1/logistica/1/estado
{
  "estado": "EN_TRANSITO",
  "observaciones": "Salió desde bodega central"
}
```

### Tracking por pedido

```
GET /api/v1/logistica/pedido/1
```

---

## Integración RabbitMQ

El microservicio escucha la cola `pedido.pagado.queue`.

Cuando ms-pedidos confirma un pago, publica un evento con esta estructura:

```json
{
  "pedidoId": 1,
  "usuarioId": 5,
  "nombreDestinatario": "Juan Vargas",
  "direccionDestino": "Av. Providencia 1234",
  "ciudadDestino": "Santiago",
  "regionDestino": "Metropolitana",
  "telefonoContacto": "+56912345678",
  "fechaPago": "2026-04-10T15:30:00"
}
```

ms-logistica crea el envío automáticamente con estado `PENDIENTE` y genera el código de tracking (formato `IT-XXXXXXXX`).

---

## Código de seguimiento

Generado automáticamente con el formato:

```
IT-A3F9C21B
```

`IT` = InnovaTech + 8 caracteres hexadecimales en mayúsculas únicos por UUID.

---

## Variables de entorno

| Variable            | Default       | Descripción              |
|---------------------|---------------|--------------------------|
| `DB_HOST`           | `localhost`   | Host PostgreSQL           |
| `DB_PORT`           | `5432`        | Puerto PostgreSQL          |
| `DB_NAME`           | `logistica_db`| Nombre de la base         |
| `DB_USER`           | `postgres`    | Usuario DB                |
| `DB_PASSWORD`       | `postgres`    | Contraseña DB             |
| `RABBITMQ_HOST`     | `localhost`   | Host RabbitMQ             |
| `RABBITMQ_PORT`     | `5672`        | Puerto RabbitMQ           |
| `RABBITMQ_USER`     | `guest`       | Usuario RabbitMQ          |
| `RABBITMQ_PASSWORD` | `guest`       | Contraseña RabbitMQ       |

---

## Tests

```bash
mvn test
```

Los tests unitarios cubren:
- Creación exitosa de envío
- Detección de duplicados (mismo pedidoId)
- Creación desde evento RabbitMQ
- Listar y buscar por ID
- Actualización de estado (incluyendo registro de fecha real al marcar ENTREGADO)
- Eliminación

---

## Estructura del proyecto

```
ms-logistica/
├── src/main/java/com/innovatech/logistica/
│   ├── MsLogisticaApplication.java
│   ├── controller/       EnvioController.java
│   ├── service/          EnvioService.java / EnvioServiceImpl.java
│   ├── repository/       EnvioRepository.java
│   ├── model/
│   │   ├── entity/       Envio.java
│   │   └── enums/        EstadoEnvio.java
│   ├── dto/              EnvioRequestDTO / ResponseDTO / ActualizarEstadoDTO
│   ├── messaging/
│   │   ├── consumer/     PedidoPagadoConsumer.java
│   │   └── event/        PedidoPagadoEvent.java
│   ├── exception/        GlobalExceptionHandler + excepciones
│   └── config/           RabbitMQConfig.java
└── src/test/             EnvioServiceTest.java
```

## Healthcheck e integraci�n
- Ruta base: /api/v1/logistica
- Healthcheck recomendado: /actuator/health

