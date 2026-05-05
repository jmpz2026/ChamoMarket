# ⚠️ Manejo de Excepciones y Flujo (MUY IMPORTANTE)

## 🧠 ¿Por qué usamos excepciones?

En vez de hacer esto:

```java
if (producto == null) {
return "error";
}
```

Usamos excepciones porque:
- Separan la lógica del flujo normal 
- Evitan código repetitivo 
- Centralizan errores en un solo lugar 
- Son estándar en aplicaciones profesionales

# 🏗️ Estructura de Excepciones del Proyecto

## Tenemos 3 piezas clave:
1. ApiException (Base)
```java
public class ApiException extends RuntimeException {

    private final int status;

    public ApiException(String message, int status) {
        super(message);
        this.status = status;
    }
}
```

2. Excepciones personalizadas - 📄 ResourceNotFoundException
```java
public class ResourceNotFoundException extends ApiException {

    public ResourceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND.value());
    }
}
```
📄 ResourceExistsException
```java
public class ResourceExistsException extends ApiException {

    public ResourceExistsException(String message) {
        super(message, HttpStatus.CONFLICT.value());
    }
}
```

## 🧠 Por qué existen
Para representar errores reales del negocio:
### Situación - Excepción
#### No existe producto - ResourceNotFoundException()
#### Ya existe algo - ResourceExistsException()

## 🌍 GlobalExceptionHandler (MAGIA)
```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<?>> handleApiException(ApiException exception) {

        ApiResponse<Object> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setMessage(exception.getMessage());
        response.setData(null);

        return ResponseEntity
                .status(exception.getStatus())
                .body(response);
    }
}
```

## 🧠 Qué hace esto
👉 Intercepta TODAS las excepciones automáticamente
No necesitas hacer try-catch en cada método.


# 🔁 Flujo REAL de una Excepción
1. Cliente envía:
```json
{
  "name": "Laptop",
  "code": "LP123",
  "categoryId": 999
}
```

2. Service ejecuta:
```java
CategoryEntity category = categoryRepository.findById(requestDTO.getCategoryId())
    .orElseThrow(() -> new ResourceNotFoundException("NO SE ENCONTRO LA CATEGORIA"));
```

## 💥 Aquí pasa esto:
- No encuentra la categoría entonces:
- - Se lanza excepción:
```java
throw new ResourceNotFoundException(...)
```
- El método se detiene inmediatamente

3. Spring detecta la excepción  
Busca quién la maneja → encuentra:
```java
@ExceptionHandler(ApiException.class)
```

4. GlobalExceptionHandler responde:
```json
{
  "success": false,
  "message": "NO SE ENCONTRO LA CATEGORIA",
  "data": null
}
```

# 🔥 Flujo completo visual
```json
Controller → Service → (ERROR)
                     ↓
              throw Exception
                     ↓
       GlobalExceptionHandler
                     ↓
               ApiResponse
                     ↓
                  Cliente
```