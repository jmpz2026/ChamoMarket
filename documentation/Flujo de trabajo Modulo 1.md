# 📦 Módulo Product – Guía de Implementación (Enseñanza + Flujo Real)

## 🧠 Objetivo

Implementar correctamente el módulo **Product** siguiendo la arquitectura del proyecto, **entendiendo el por qué de cada capa**, no solo copiando código.

---

# 🏗️ Arquitectura Base

El proyecto sigue este flujo:

Request → Controller → Service → Repository → DB
↓
DTO
↓
ApiResponse


## 🔍 Filosofía

- **Controller** → recibe HTTP
- **Service** → lógica de negocio (AQUÍ PASA TODO)
- **Repository** → acceso a datos
- **DTO** → contrato con frontend
- **Entity** → modelo de base de datos

---

# ⚠️ Antes de empezar (ERRORES COMUNES)

## ❌ Error 1: Usar Entity en el Controller

Nunca hagas esto:

```java
public ProductEntity crear(...)
```

✔ Correcto:
public ApiResponse<ProductResponseDTO>

## ❌ Error 2: Mandar objetos completos innecesarios

El frontend NO debe enviar una categoría completa, solo su ID.

## ❌ Error 3: Lógica en el Controller

Si ves lógica en el controller → está mal.

# 🧩 Paso 1: Diseñar el Request (INPUT)
## 🎯 Pregunta clave:

### ¿Qué datos necesito para crear un producto?

#### Respuesta:
- name 
- code 
- categoryId

```java
@Getter
@Setter
public class ProductRequestDTO {

    @NotEmpty
    private String name;

    @NotEmpty
    private String code;

    @NotNull
    private Long categoryId;
}
```

# 🧩 Paso 2: Diseñar el Response (OUTPUT)
## Actualmente tienes esto:
```java
private CategoryEntity category;
```