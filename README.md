# 🏨 Hotel Search Microservice - RIU

Microservicio de alto rendimiento desarrollado en **Java 21** y **Spring Boot 3.2** bajo **Arquitectura Hexagonal (Puertos y Adaptadores)**. Diseñado para gestionar registros de búsquedas hoteleras, procesar eventos asíncronos mediante **Apache Kafka**, acelerar lecturas y conteos atómicos con **Redis** (patrón *Write-Behind Cache*), y persistir información histórica en **Oracle Database 23c Free**, aprovechando al máximo los **Virtual Threads (Project Loom)**.

---

## 🏛️ Arquitectura del Sistema

El proyecto sigue los principios de **Clean Architecture / Hexagonal Architecture**:

```
msvc-search-riu/
├── domain/                    # Núcleo de negocio puro (sin dependencias externas)
│   ├── model/                # Modelos inmutables (SearchSession)
│   ├── port/                 # Puertos Inbound (Casos de uso) y Outbound (SPI)
│   └── exception/            # Excepciones de negocio
├── application/               # Capa de aplicación y orquestación
│   ├── service/              # Implementación de casos de uso y Scheduler de sincronización
│   ├── mapper/               # Mapeadores a nivel de aplicación
│   └── exception/            # Excepciones de aplicación
└── infrastructure/            # Adaptadores de entrada y salida
    ├── adapter/
    │   ├── in/web/           # Controladores REST, DTOs, Validadores personalizados
    │   ├── in/messaging/     # Consumidor Kafka con Virtual Threads
    │   ├── out/persistence/  # Adaptador JPA, Entidades y Repositorios Oracle
    │   ├── out/cache/        # Adaptador Redis con operaciones atómicas
    │   └── out/messaging/    # Publicador Kafka con KafkaTemplate
    ├── config/               # Configuración de Beans (Kafka, OpenAPI, Jackson)
    └── exception/            # GlobalExceptionHandler con respuestas RFC 7807 estructuradas
```

---

## 🚀 Tecnologías Principales

* **Java 21** con **Virtual Threads** (`spring.threads.virtual.enabled=true`).
* **Spring Boot 3.2.0** (Spring Web, Spring Data JPA, Spring Kafka, Spring Data Redis, Validation).
* **Oracle Database 23c Free** (`gvenzl/oracle-free:23-slim-faststart`).
* **Apache Kafka (KRaft Mode)** (`apache/kafka:latest`).
* **Kafka UI** (`provectuslabs/kafka-ui:latest`) para monitoreo de tópicos y mensajes.
* **Redis** (`redis:alpine`) como caché en memoria de baja latencia.
* **SpringDoc OpenAPI 3 / Swagger UI** para documentación viva de la API.
* **Docker & Docker Compose** para containerización y orquestación local.
* **JUnit 5, AssertJ, Mockito & JaCoCo** para testing exhaustivo y métricas de cobertura.

---

## 🐳 Despliegue con Docker Compose

El archivo [docker-compose.yaml](file:///Users/facundo/Desktop/workspace/msvc-seach-riu/msvc-search/msvc-search-riu/docker-compose.yaml) orquesta todos los componentes necesarios para levantar el entorno completo de forma automatizada.

### 1. Construir la Imagen Docker de la Aplicación

Antes de iniciar los contenedores, genera la imagen local:

```bash
docker build -t faccordoba/msvc-search-riu:latest .
```

### 2. Iniciar Todos los Servicios

Ejecuta el siguiente comando para arrancar la base de datos Oracle, el broker de Kafka, Kafka UI, Redis y la aplicación Spring Boot en segundo plano:

```bash
docker compose up -d
```

> **Nota:** La base de datos Oracle incluye un mecanismo de `healthcheck`. El contenedor `app` esperará automáticamente a que Oracle esté en estado `healthy` antes de iniciar.

### 3. Verificar el Estado de los Contenedores

```bash
docker compose ps
```

Deberías ver los 5 contenedores en estado `running`:
* `oracle-db` (`1521:1521`)
* `kafka-broker` (`9092:9092`, `9093:9093`)
* `kafka-ui` (`8080:8080`)
* `redis-cache` (`6379:6379`)
* `hotel-search-app` (`8081:8080`)

### 4. Monitorear los Logs

Para ver los logs de la aplicación en tiempo real:

```bash
docker compose logs -f app
```

Para ver los logs de todos los servicios:

```bash
docker compose logs -f
```

### 5. Detener los Servicios

Para detener los contenedores:

```bash
docker compose down
```

Para detener y eliminar los volúmenes de datos persistentes:

```bash
docker compose down -v
```

---

## 🌐 Puertos y Accesos Rápidos

| Servicio | URL / Host:Puerto | Descripción |
| :--- | :--- | :--- |
| **API Swagger UI** | [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html) | Interfaz interactiva de la API |
| **OpenAPI Docs (JSON)** | [http://localhost:8081/v3/api-docs](http://localhost:8081/v3/api-docs) | Especificación OpenAPI 3 |
| **Kafka UI** | [http://localhost:8080](http://localhost:8080) | Panel de administración de Kafka |
| **Oracle Database** | `localhost:1521/freepdb1` | Usuario: `hotel_user` \| Pass: `hotel_password` |
| **Redis Cache** | `localhost:6379` | Servidor Redis |

---

## 📡 Referencia de la API REST

### 1. Registrar Nueva Búsqueda
Crea una nueva sesión de búsqueda, genera un `searchId` único y publica el evento en el tópico `hotel-search-events` de Kafka.

* **Método:** `POST`
* **URL:** `http://localhost:8081/search`
* **Headers:** `Content-Type: application/json`

#### Request Body
```json
{
  "hotelId": "HOTEL001",
  "checkIn": "01/10/2026",
  "checkOut": "05/10/2026",
  "ages": [30, 28, 5]
}
```

#### Response Body (`200 OK`)
```json
{
  "searchId": "a1b2c3d"
}
```

#### Ejemplo con cURL
```bash
curl -X POST http://localhost:8081/search \
  -H "Content-Type: application/json" \
  -d '{
    "hotelId": "HOTEL001",
    "checkIn": "01/10/2026",
    "checkOut": "05/10/2026",
    "ages": [30, 28, 5]
  }'
```

---

### 2. Consultar Detalle y Conteo de Visitas
Obtiene los detalles de la búsqueda y la cantidad acumulada de veces que ha sido consultada (incrementando el contador de forma atómica en Redis).

* **Método:** `GET`
* **URL:** `http://localhost:8081/count?searchId={searchId}`

#### Response Body (`200 OK`)
```json
{
  "searchId": "a1b2c3d",
  "search": {
    "hotelId": "HOTEL001",
    "checkIn": "01/10/2026",
    "checkOut": "05/10/2026",
    "ages": [30, 28, 5]
  },
  "count": 1
}
```

#### Ejemplo con cURL
```bash
curl -X GET "http://localhost:8081/count?searchId=a1b2c3d"
```

---

## ⚙️ Variables de Entorno

| Variable | Valor por Defecto | Descripción |
| :--- | :--- | :--- |
| `SPRING_DATASOURCE_URL` | `jdbc:oracle:thin:@//localhost:1521/freepdb1` | URL JDBC de conexión a Oracle |
| `SPRING_DATASOURCE_USERNAME` | `system` | Usuario de base de datos |
| `SPRING_DATASOURCE_PASSWORD` | `hotel_password` | Contraseña de base de datos |
| `SPRING_KAFKA_BOOTSTRAP_SERVERS` | `localhost:9092` | Dirección del cluster Kafka |
| `SPRING_KAFKA_TOPIC_NAME` | `hotel-search-events` | Tópico de eventos de búsqueda |
| `SPRING_DATA_REDIS_HOST` | `localhost` | Host de Redis |
| `SPRING_DATA_REDIS_PORT` | `6379` | Puerto de Redis |
| `SPRING_THREADS_VIRTUAL_ENABLED` | `true` | Activa Virtual Threads de Java 21 |

---

## 🧪 Pruebas y Cobertura de Código (QA)

El proyecto cuenta con una suite completa de pruebas unitarias y de integración en todas sus capas, implementadas bajo el estándar de nombres descriptivos `*Should*Test` (ej. `newSearchSessionShouldBeCreatedTest`, `countShouldReturnUpdatedSessionWhenSessionExistsInCacheTest`).

### Ejecutar Todas las Pruebas

```bash
./mvnw clean test
```

### Generar y Consultar el Reporte de Cobertura JaCoCo

```bash
./mvnw jacoco:report
```

El reporte interactivo HTML estará disponible en:
```
target/site/jacoco/index.html
```

### Resumen de Cobertura Obtenida

* **Cobertura de Instrucciones:** ~99%
* **Cobertura de Ramas (Branches):** ~95%
* **Cobertura de Clases:** 100% (28 de 28 clases)
* **Total de Pruebas Unitarias & Integración:** 84 pruebas pasando exitosamente sin fallos.

---

## 👨‍💻 Autor y Soporte
* **Facundo Cordoba** (`faccordoba@riu.com`)
* Licencia: Apache 2.0
