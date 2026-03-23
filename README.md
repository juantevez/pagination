# Pagination Demo — Hexagonal Architecture

Demo de paginación con Java 17, Spring Boot y PostgreSQL usando arquitectura hexagonal.
Compara dos estrategias: **Offset-Based** y **Cursor-Based (Keyset)** con sorting dinámico.

---

## Estructura del proyecto

```
src/main/java/com/demo/product/
├── domain/
│   ├── model/
│   │   ├── Product.java            # Entidad del dominio (sin JPA)
│   │   ├── PagedResult.java        # Modelo de respuesta paginada
│   │   └── SortCriteria.java       # Sorting tipado con validación
│   └── port/
│       └── ProductRepository.java  # Puerto de salida (interfaz)
├── application/
│   └── GetProductsUseCase.java     # Orquestación + validaciones
└── infrastructure/
    ├── persistence/
    │   ├── entity/
    │   │   └── ProductEntity.java           # Entidad JPA
    │   ├── mapper/
    │   │   └── ProductMapper.java           # ProductEntity ↔ Product
    │   ├── adapter/
    │   │   ├── JpaProductRepositoryAdapter  # Offset con Spring Data
    │   │   └── JdbcProductRepositoryAdapter # Cursor con JdbcClient
    │   └── SpringDataProductRepository.java
    └── web/
        ├── ProductController.java
        └── dto/
            └── PagedResponse.java
```

---

## Requisitos

- Java 17
- Maven 3.8+
- Docker + Docker Compose

---

## Levantar el proyecto

```bash
# 1. Iniciar PostgreSQL
docker-compose up -d

# 2. Correr la aplicación
# Flyway crea la tabla, índices y carga 500 productos automáticamente
mvn spring-boot:run
```

---

## Endpoints

### Offset-Based
```
GET /api/v1/products/paged
```

| Parámetro       | Tipo    | Default | Descripción                          |
|-----------------|---------|---------|--------------------------------------|
| `page`          | int     | 0       | Número de página (0-based)           |
| `size`          | int     | 10      | Elementos por página (máx. 100)      |
| `sortField`     | String  | id      | `id`, `name`, `price`, `category`, `createdAt` |
| `sortDirection` | String  | ASC     | `ASC` o `DESC`                       |
| `category`      | String  | -       | Filtro opcional por categoría        |

**Respuesta:**
```json
{
  "content": [...],
  "page": {
    "current": 0,
    "size": 10,
    "totalElements": 500,
    "totalPages": 50
  },
  "navigation": {
    "hasNext": true,
    "hasPrevious": false
  }
}
```

---

### Cursor-Based (Keyset)
```
GET /api/v1/products/cursor
```

| Parámetro       | Tipo    | Default | Descripción                          |
|-----------------|---------|---------|--------------------------------------|
| `cursor`        | Long    | -       | ID del último elemento visto. Omitir en la primera página |
| `size`          | int     | 10      | Elementos por página (máx. 100)      |
| `sortField`     | String  | id      | `id`, `name`, `price`, `category`, `createdAt` |
| `sortDirection` | String  | ASC     | `ASC` o `DESC`                       |

**Respuesta:**
```json
{
  "content": [...],
  "page": {
    "size": 10
  },
  "navigation": {
    "hasNext": true,
    "nextCursor": "42"
  }
}
```

**Flujo de navegación:**
```
GET /cursor              → { nextCursor: "10", hasNext: true }
GET /cursor?cursor=10    → { nextCursor: "20", hasNext: true }
GET /cursor?cursor=490   → { hasNext: false }
```

---

## Comparación de estrategias

| | Offset | Cursor (Keyset) |
|---|---|---|
| SQL | `LIMIT ? OFFSET ?` | `WHERE id > ? ORDER BY ? LIMIT ?` |
| Queries ejecutadas | 2 (datos + COUNT) | 1 |
| `totalElements` | ✅ disponible | ❌ no disponible |
| Salto a página arbitraria | ✅ | ❌ |
| Performance en tablas grandes | ❌ se degrada | ✅ constante |
| Consistencia ante inserts | ❌ puede saltar filas | ✅ estable |
| Implementación | Spring Data `Pageable` | JDBC manual |
| Caso de uso ideal | UI con numeración de páginas | Scroll infinito, feeds, transacciones |

---

## Sorting dinámico

Campos permitidos: `id`, `name`, `price`, `category`, `createdAt`.

La validación ocurre en `SortCriteria.of()` dentro del dominio — antes de llegar al adaptador. Cualquier campo no permitido lanza `IllegalArgumentException`.

El adaptador JPA traduce el campo del dominio al campo de la entidad en `mapDomainFieldToEntity()`. Esto desacopla los nombres del dominio de los nombres de la base de datos.

---

## Índices en PostgreSQL

La migración crea índices compuestos optimizados para keyset pagination.
El campo `id` siempre se incluye al final como tiebreaker para garantizar orden determinístico ante valores duplicados:

```sql
CREATE INDEX idx_products_price_id ON products (price ASC, id ASC);
CREATE INDEX idx_products_name_id  ON products (name  ASC, id ASC);
```

---

## Colección Postman

Importar `pagination-demo.postman_collection.json` en Postman.
Incluye requests para ambas estrategias, sorting dinámico, filtros y casos de error.