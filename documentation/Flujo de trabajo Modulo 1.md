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
```java
private Long categoryId;
private String categoryName;
```

# 🧩 Paso 3: Controller (Entrada HTTP)
## 🎯 Responsabilidad:
- Recibir request 
- Validar datos 
- Delegar al service
```java
@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponseDTO>> createProduct(
            @RequestBody @Valid ProductRequestDTO requestDTO) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(productService.createProduct(requestDTO));
    }
}
```

# 🧩 Paso 4: Service (EL CEREBRO)
## 🎯 Responsabilidad:
Aquí pasa TODO lo importante.

### 🔥 Flujo mental para crear un producto
Antes de escribir código, piensa así:
1. ¿La categoría existe?
2. Si no → error
3. Crear producto
4. Guardar en DB
5. Convertir a DTO
6. Devolver respuesta
```java
public ApiResponse<ProductResponseDTO> createProduct(ProductRequestDTO requestDTO) {

    // 1. Validar que la categoría exista
    CategoryEntity category = categoryRepository.findById(requestDTO.getCategoryId())
            .orElseThrow(() -> new ResourceNotFoundException("CATEGORIA NO EXISTE"));

    // 2. Crear entidad
    ProductEntity product = new ProductEntity();
    product.setName(requestDTO.getName());
    product.setCode(requestDTO.getCode());
    product.setStatus(true);
    product.setCategory(category);

    // 3. Guardar
    productRepository.save(product);

    // 4. Convertir a DTO
    ProductResponseDTO dto = ConvertHelper.convertProductEntityToProductResponseDTO(product);

    // 5. Respuesta
    ApiResponse<ProductResponseDTO> response = new ApiResponse<>();
    response.setSuccess(true);
    response.setMessage("PRODUCTO CREADO");
    response.setData(dto);

    return response;
}
```

# 🧩 Paso 5: Repository
```java
public interface ProductRepository extends JpaRepository<ProductEntity, Long>
```

# 🧩 Paso 6: ConvertHelper
## 🎯 Responsabilidad:

Convertir Entity → DTO

## 🧠 Por qué existe:
- Evita duplicar código 
- Centraliza conversiones 
- Facilita mantenimiento

```java
    public static ProductResponseDTO convertProductEntityToProductResponseDTO(ProductEntity productEntity){
        ProductResponseDTO productResponseDTO = new ProductResponseDTO();
        productResponseDTO.setId(productEntity.getId());
        productResponseDTO.setCode(productEntity.getCode());
        productResponseDTO.setName(productEntity.getName());
        productResponseDTO.setStatus(productEntity.getStatus());
        productResponseDTO.setCategoryId(productEntity.getCategory().getId());
        return productResponseDTO;
    }
```

# 🔁 Flujo Completo (VISIÓN REAL)
📌 Crear Producto
1. Cliente envía:
```json
{
  "name": "Laptop",
  "code": "LP123",
  "categoryId": 1
}
```

2. Controller
- Recibe request
- Valida (@Valid)
- Llama al service

3. Service
- Busca categoría 
- Crea entidad 
- Guarda en DB

4. Repository
- Hibernate ejecuta INSERT

5. ConvertHelper
- Convierte a DTO

6. Respuesta final
```json
{
  "success": true,
  "message": "PRODUCTO CREADO",
  "data": {
    "id": 1,
    "name": "Laptop",
    "code": "LP123",
    "status": true,
    "categoryId": 1,
    "categoryName": "Tecnología"
  }
}
```



# ⚠️ Manejo de Errores

Gracias a GlobalExceptionHandler:
Si la categoría no existe devuelve:
```json
{
  "success": false,
  "message": "NO SE ENCONTRO LA CATEGORIA",
  "data": null
}
```

# 🧩 Resumen Final
```json
NO copies → ENTIENDE

Controller = entrada
Service = lógica
Repository = DB
DTO = contrato limpio
Entity = modelo interno
```