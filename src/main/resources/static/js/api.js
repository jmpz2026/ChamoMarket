/* ═══════════════════════════════════════════════════
   ChamoMarket – API Layer
   Backend: http://localhost:8081
   ═══════════════════════════════════════════════════ */

const API_BASE = 'http://localhost:8081';

function _getToken() {
    return localStorage.getItem('cm_token');
}

async function _fetch(path, options = {}) {
    const token = _getToken();
    const headers = { 'Content-Type': 'application/json' };
    if (token) headers['Authorization'] = `Bearer ${token}`;

    let res;
    try {
        res = await fetch(`${API_BASE}${path}`, {
            ...options,
            headers: { ...headers, ...options.headers }
        });
    } catch (e) {
        throw new Error('No se pudo conectar con el servidor. ¿Está corriendo en el puerto 8081?');
    }

    if (res.status === 401) {
        window.App?.onUnauthorized?.();
        throw new Error('Sesión expirada. Inicia sesión nuevamente.');
    }

    if (res.status === 204) return null;

    const text = await res.text();

    // Some endpoints return plain strings (e.g., proveedores/entrada)
    if (!text || text.trim() === '') return null;

    let data;
    try {
        data = JSON.parse(text);
    } catch {
        // plain string response
        if (!res.ok) throw new Error(text || `Error ${res.status}`);
        return text;
    }

    if (!res.ok) {
        throw new Error(data?.message || data?.error || `Error ${res.status}`);
    }

    return data;
}

// Unwrap ApiResponse<T> → T
function _unwrap(promise) {
    return promise.then(r => r?.data ?? r);
}

/* ── Auth ─────────────────────────────────────── */
const AuthAPI = {
    login: (username, password) =>
        _fetch('/auth/login', { method: 'POST', body: JSON.stringify({ username, password }) }),

    tokenData: (token) =>
        _fetch('/auth/data', { method: 'POST', body: JSON.stringify({ token }) }),

    refresh: (token) =>
        _fetch('/auth/refresh', { method: 'POST', body: JSON.stringify({ token }) }),
};

/* ── Categories ───────────────────────────────── */
const CategoriesAPI = {
    all: () => _unwrap(_fetch('/category')),
    getById: (id) => _unwrap(_fetch(`/category/${id}`)),
    create: (name) => _unwrap(_fetch('/category', { method: 'POST', body: JSON.stringify({ name }) })),
    update: (id, name, status) => _unwrap(_fetch('/category', {
        method: 'PUT',
        body: JSON.stringify({ id, name, status })
    })),
    delete: (id) => _fetch(`/category/${id}`, { method: 'DELETE' }),
};

/* ── Products ─────────────────────────────────── */
const ProductsAPI = {
    getById: (id) => _unwrap(_fetch(`/product/${id}`)),
    create: (data) => _unwrap(_fetch('/product', { method: 'POST', body: JSON.stringify(data) })),
    update: (data) => _unwrap(_fetch('/product', { method: 'PUT', body: JSON.stringify(data) })),
    delete: (id) => _fetch(`/product/${id}`, { method: 'DELETE' }),
    addStock: (id, quantity) => _unwrap(_fetch(`/product/${id}/add-stock`, {
        method: 'PUT', body: JSON.stringify({ quantity })
    })),
    removeStock: (id, quantity) => _unwrap(_fetch(`/product/${id}/remove-stock`, {
        method: 'PUT', body: JSON.stringify({ quantity })
    })),
};

/* ── Sales ────────────────────────────────────── */
const SalesAPI = {
    create: (employeeId, items) => _unwrap(_fetch('/sales', {
        method: 'POST',
        body: JSON.stringify({ employeeId, items })
    })),
};

/* ── Employees ────────────────────────────────── */
const EmployeesAPI = {
    // Uses /auth/register (public) since it handles username+password
    register: (data) => _fetch('/auth/register', { method: 'POST', body: JSON.stringify(data) }),

    search: (role, start, end) => {
        const p = new URLSearchParams();
        if (role) p.set('role', role);
        if (start) p.set('start', start);
        if (end) p.set('end', end);
        return _fetch(`/employee/search${p.toString() ? '?' + p : ''}`);
    },
};

/* ── Suppliers ────────────────────────────────── */
const SuppliersAPI = {
    create: (nit, nombre) =>
        _fetch('/proveedores', { method: 'POST', body: JSON.stringify({ nit, nombre }) }),

    getById: (id) => _fetch(`/proveedores/${id}`),

    stockEntry: (productoId, proveedorId, cantidad) =>
        _fetch(`/proveedores/entrada?productoId=${productoId}&proveedorId=${proveedorId}&cantidad=${cantidad}`, {
            method: 'POST'
        }),
};
