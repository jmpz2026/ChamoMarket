# Migración: Sistema de Validación de Roles

## Contexto

### Cómo funciona AHORA (ChamoMarket)
```
Request → JwtValidationFilter (order=1) → RoleValidationFilter (order=2) → Controller
```
- `RoleValidationFilter` tiene un mapa hardcodeado con rutas y roles permitidos
- Revisa el prefijo de la ruta con `path.startsWith(entry.getKey())`
- El rol viaja en el JWT como **String** (`"ADMINISTRADOR"`, `"CAJERO"`)
- Para agregar una ruta nueva, hay que editar el filtro manualmente

### Cómo funciona el MODELO (chamomarketMaik)
```
Request → JwtValidationFilter (order=1) → RequiresRoleInterceptor → Controller
```
- Se elimina `RoleValidationFilter`
- El rol se valida en el interceptor leyendo la anotación `@RequiresRole` del método
- El rol viaja en el JWT como **Long ID** (`1L`, `2L`)
- Para proteger un endpoint solo se agrega `@RequiresRole({RoleEnum.X})` al método

### Diferencia clave
| Aspecto | Antes | Después |
|---|---|---|
| Dónde se configura el rol | Mapa en `RoleValidationFilter` | Anotación `@RequiresRole` en el controller |
| Cómo se valida | Filtro (prefijo de ruta) | Interceptor (por método exacto) |
| Rol en JWT | `String` nombre | `Long` id |
| Agregar nueva ruta | Editar el filtro | Solo poner `@RequiresRole` |

---

## División de Trabajo

---

### Persona 1 — Anotación + Interceptor
**Crear la infraestructura del nuevo sistema de roles**

**Archivos a crear:**

**`security/RequiresRole.java`** — anotación personalizada
```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresRole {
    EmployeeRole[] value();
}
```

**`security/RequiresRoleInterceptor.java`** — interceptor que la lee
```java
@Component
public class RequiresRoleInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod method)) return true;

        RequiresRole annotation = method.getMethodAnnotation(RequiresRole.class);
        if (annotation == null) annotation = method.getBeanType().getAnnotation(RequiresRole.class);
        if (annotation == null) return true;

        Object rol = request.getAttribute("rolId");
        if (!(rol instanceof Long rolId)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Usuario no autenticado\"}");
            return false;
        }

        boolean hasRole = Arrays.stream(annotation.value())
                .anyMatch(role -> role.getId() == rolId);

        if (!hasRole) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"No tienes permisos para esta acción\"}");
            return false;
        }
        return true;
    }
}
```

**`config/WebConfig.java`** — registra el interceptor
```java
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final RequiresRoleInterceptor requiresRoleInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requiresRoleInterceptor);
    }
}
```

---

### Persona 2 — Limpieza de Filtros
**Eliminar `RoleValidationFilter` y actualizar `FilterConfig`**

**Archivos a modificar/eliminar:**

1. **Eliminar** `filter/RoleValidationFilter.java` — ya no se necesita
2. **Modificar** `config/FilterConfig.java` — quitar el bean del filtro eliminado:

```java
// QUITAR todo este bloque:
@Bean
FilterRegistrationBean<RoleValidationFilter> roleFilter(RoleValidationFilter roleValidationFilter) {
    ...
}
```

3. **Modificar** `filter/JwtValidationFilter.java` — cambiar el atributo que se setea de `role` (String) a `rolId` (Long):

```java
// ANTES:
String role = jwtService.extractRole(token);
request.setAttribute("role", role);

// DESPUÉS:
Long rolId = jwtService.extractRolId(token);
request.setAttribute("rolId", rolId);
```

> El interceptor de Persona 1 espera `request.getAttribute("rolId")` como Long.

---

### Persona 3 — JWT + EmployeeRole
**Cambiar cómo el rol viaja en el token: de String nombre a Long ID**

**Archivos a modificar:**

**`enums/EmployeeRole.java`** — asegurar que tiene `getId()` (ya existe, solo verificar):
```java
@Getter
public enum EmployeeRole {
    ADMINISTRADOR(1L),
    CAJERO(2L);
    private final long id;
    EmployeeRole(long id) { this.id = id; }
}
```

**`service/JwtService.java`** — cambiar `generateToken` para guardar `rolId` como Long:
```java
// ANTES:
public String generateToken(Long employeeId, String role, String username)
// el token guardaba: claims.put("role", role) // String

// DESPUÉS:
public String generateToken(Long employeeId, Long rolId, String username)
// el token guarda: claims.put("rolId", rolId) // Long
```

Agregar método extractor:
```java
public Long extractRolId(String token) {
    return extractClaims(token, claims -> claims.get("rolId", Long.class));
}
```

**`service/AuthService.java`** — pasar el ID del rol en lugar del nombre:
```java
// ANTES:
jwtService.generateToken(employee.getId(), employee.getRole().name(), employee.getUsername())

// DESPUÉS:
jwtService.generateToken(employee.getId(), employee.getRole().getId(), employee.getUsername())
```

---

### Persona 4 — Anotaciones en Controllers
**Proteger cada endpoint con `@RequiresRole`**

**Archivos a modificar:** todos los controllers protegidos

Importar en cada controller:
```java
import com.chamo.chamomarket.security.RequiresRole;
import com.chamo.chamomarket.enums.EmployeeRole;
```

**`CategoryController`** — solo ADMINISTRADOR:
```java
@RequiresRole({EmployeeRole.ADMINISTRADOR})
@GetMapping
public ResponseEntity<?> getAll() { ... }

@RequiresRole({EmployeeRole.ADMINISTRADOR})
@PostMapping
public ResponseEntity<?> create(...) { ... }

// repetir en PUT, DELETE, GET/{id}
```

**`ProductController`** — ADMINISTRADOR o CAJERO:
```java
@RequiresRole({EmployeeRole.ADMINISTRADOR, EmployeeRole.CAJERO})
@GetMapping("/{id}")
public ResponseEntity<?> getById(...) { ... }

// repetir en POST, PUT, DELETE, add-stock, remove-stock
```

**`ProveedorController`** — solo ADMINISTRADOR:
```java
@RequiresRole({EmployeeRole.ADMINISTRADOR})
// en todos los endpoints
```

**`EmployeeController`** — solo ADMINISTRADOR:
```java
@RequiresRole({EmployeeRole.ADMINISTRADOR})
// en todos los endpoints
```

**`SaleController`** — ADMINISTRADOR o CAJERO:
```java
@RequiresRole({EmployeeRole.ADMINISTRADOR, EmployeeRole.CAJERO})
// en todos los endpoints
```

> `AuthController` no lleva `@RequiresRole` — rutas públicas.

---

## Orden de integración recomendado

1. Persona 3 termina primero (cambia el JWT — las demás dependen de esto)
2. Persona 1 y Persona 2 trabajan en paralelo
3. Persona 4 trabaja en paralelo con 1 y 2, solo necesita la anotación creada por Persona 1
4. Al juntar todo: verificar que `request.getAttribute("rolId")` devuelve `Long` (no `String`)
