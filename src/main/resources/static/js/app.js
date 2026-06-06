/* ═══════════════════════════════════════════════════
   ChamoMarket – SPA Logic
   ═══════════════════════════════════════════════════ */

// ── State ──────────────────────────────────────────
const state = {
    token: null,
    role: null,
    employeeId: null,
    username: null,
    cart: [],
    cachedCategories: [],
    _refreshTimer: null,
};

const isAdmin = () => state.role === 'ADMINISTRADOR';

// ── Boot ───────────────────────────────────────────
window.App = {
    onUnauthorized() {
        _clearSession();
        _showScreen('login');
    }
};

async function _boot() {
    const token = localStorage.getItem('cm_token');
    if (!token) { _showScreen('login'); return; }

    try {
        const data = await AuthAPI.tokenData(token);
        state.token = token;
        state.role = localStorage.getItem('cm_role') || data.role;
        state.employeeId = data.employeeId;
        state.username = data.username;
        _startApp();
    } catch {
        _clearSession();
        _showScreen('login');
    }
}

// ── Session ────────────────────────────────────────
function _clearSession() {
    if (state._refreshTimer) clearInterval(state._refreshTimer);
    state.token = null; state.role = null; state.employeeId = null; state.username = null;
    ['cm_token', 'cm_role', 'cm_employee_id', 'cm_username'].forEach(k => localStorage.removeItem(k));
}

function _startTokenRefresh() {
    if (state._refreshTimer) clearInterval(state._refreshTimer);
    state._refreshTimer = setInterval(async () => {
        try {
            const res = await AuthAPI.refresh(state.token);
            state.token = res.token;
            localStorage.setItem('cm_token', res.token);
        } catch {
            clearInterval(state._refreshTimer);
        }
    }, 3 * 60 * 1000);
}

// ── Screens ────────────────────────────────────────
function _showScreen(name) {
    const login = document.getElementById('screen-login');
    const app   = document.getElementById('screen-app');
    login.classList.toggle('d-none', name !== 'login');
    app.classList.toggle('d-none', name !== 'app');
}

// ── App Setup ──────────────────────────────────────
function _startApp() {
    _showScreen('app');
    document.getElementById('sidebar-username').textContent = state.username || '';
    document.getElementById('sidebar-role').textContent = state.role || '';

    if (!isAdmin()) {
        document.querySelectorAll('.admin-only').forEach(el => el.classList.add('d-none'));
    } else {
        document.querySelectorAll('.admin-only').forEach(el => el.classList.remove('d-none'));
    }

    document.querySelectorAll('.sidebar-link').forEach(link => {
        link.addEventListener('click', e => {
            e.preventDefault();
            _showView(link.dataset.view);
        });
    });

    document.getElementById('btn-logout').addEventListener('click', () => {
        _clearSession();
        _showScreen('login');
    });

    _startTokenRefresh();
    _showView('products');
}

function _showView(name) {
    document.querySelectorAll('.view').forEach(v => v.classList.add('d-none'));
    const el = document.getElementById(`view-${name}`);
    if (!el) return;
    el.classList.remove('d-none');

    document.querySelectorAll('.sidebar-link').forEach(l => {
        l.classList.toggle('active', l.dataset.view === name);
    });

    switch (name) {
        case 'products':   _renderProducts();   break;
        case 'categories': _renderCategories(); break;
        case 'sales':      _renderSales();      break;
        case 'employees':  _renderEmployees();  break;
        case 'suppliers':  _renderSuppliers();  break;
    }
}

// ═══════════════════════════════════════════════════
// LOGIN
// ═══════════════════════════════════════════════════
document.getElementById('form-login').addEventListener('submit', async e => {
    e.preventDefault();
    const username = document.getElementById('login-username').value.trim();
    const password = document.getElementById('login-password').value;
    const btn = document.getElementById('btn-login');
    const errEl = document.getElementById('login-error');

    btn.disabled = true;
    btn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Entrando...';
    errEl.classList.add('d-none');

    try {
        const res = await AuthAPI.login(username, password);
        const data = await AuthAPI.tokenData(res.token);

        state.token = res.token;
        state.role = res.role;
        state.employeeId = data.employeeId;
        state.username = data.username;

        localStorage.setItem('cm_token', res.token);
        localStorage.setItem('cm_role', res.role);
        localStorage.setItem('cm_employee_id', data.employeeId);
        localStorage.setItem('cm_username', data.username);

        _startApp();
    } catch (err) {
        errEl.textContent = err.message;
        errEl.classList.remove('d-none');
    } finally {
        btn.disabled = false;
        btn.innerHTML = '<i class="bi bi-box-arrow-in-right me-2"></i>Iniciar Sesión';
    }
});

// ═══════════════════════════════════════════════════
// PRODUCTS VIEW
// ═══════════════════════════════════════════════════
function _renderProducts() {
    const el = document.getElementById('view-products');
    el.innerHTML = `
        <div class="view-header">
            <h4 class="view-title"><i class="bi bi-box-seam"></i>Productos</h4>
            <button class="btn btn-primary" onclick="openCreateProduct()">
                <i class="bi bi-plus-lg me-1"></i>Nuevo
            </button>
        </div>

        <div class="row g-2 mb-3">
            <div class="col-auto">
                <div class="input-group">
                    <input type="number" id="prod-search-id" class="form-control" placeholder="Buscar por ID" min="1" style="width:150px">
                    <button class="btn btn-outline-secondary" onclick="searchProductById()">
                        <i class="bi bi-search"></i>
                    </button>
                </div>
            </div>
            ${isAdmin() ? `
            <div class="col-auto">
                <button class="btn btn-outline-primary" onclick="loadAllProducts()">
                    <i class="bi bi-arrow-clockwise me-1"></i>Cargar todos
                </button>
            </div>` : ''}
        </div>

        <div id="prods-content">
            <div class="empty-state">
                <i class="bi bi-search"></i>
                <p>Busca un producto por ID${isAdmin() ? ' o usa "Cargar todos"' : ''}.</p>
            </div>
        </div>
    `;
}

async function loadAllProducts() {
    const el = document.getElementById('prods-content');
    el.innerHTML = `<div class="loading-box"><div class="spinner-border text-primary"></div></div>`;
    try {
        const cats = await CategoriesAPI.all();
        state.cachedCategories = cats;
        const products = [];
        cats.forEach(cat => {
            (cat.products || []).forEach(p => {
                products.push({ ...p, categoryName: cat.name, categoryId: cat.id });
            });
        });
        _renderProductsTable(products, el);
    } catch (err) {
        el.innerHTML = `<div class="alert alert-danger">${escHtml(err.message)}</div>`;
    }
}

async function searchProductById() {
    const id = document.getElementById('prod-search-id').value;
    if (!id) return;
    const el = document.getElementById('prods-content');
    el.innerHTML = `<div class="loading-box"><div class="spinner-border text-primary"></div></div>`;
    try {
        const p = await ProductsAPI.getById(id);
        _renderProductsTable([p], el);
    } catch (err) {
        el.innerHTML = `<div class="alert alert-danger">${escHtml(err.message)}</div>`;
    }
}

function _renderProductsTable(products, container) {
    if (!products.length) {
        container.innerHTML = `<div class="empty-state"><i class="bi bi-inbox"></i><p>Sin productos.</p></div>`;
        return;
    }
    container.innerHTML = `
        <div class="table-wrap">
            <table class="table">
                <thead>
                    <tr>
                        <th>ID</th><th>Código</th><th>Nombre</th><th>Categoría</th>
                        <th>Precio</th><th>Stock</th><th>Estado</th><th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    ${products.map(p => `
                    <tr>
                        <td class="text-muted small">${p.id}</td>
                        <td><code>${p.code || '—'}</code></td>
                        <td class="fw-semibold">${escHtml(p.name)}</td>
                        <td class="text-muted small">${p.categoryName || (p.categoryId ? `#${p.categoryId}` : '—')}</td>
                        <td class="text-money">${p.price != null ? '$' + p.price.toFixed(2) : '—'}</td>
                        <td>
                            <span class="${p.quantity <= 5 ? 'stock-low' : 'stock-ok'}">${p.quantity}</span>
                        </td>
                        <td>
                            <span class="badge ${p.status ? 'bg-success' : 'bg-secondary'}">
                                ${p.status ? 'Activo' : 'Inactivo'}
                            </span>
                        </td>
                        <td>
                            <button class="btn btn-action btn-outline-primary" onclick="openEditProduct(${p.id})" title="Editar">
                                <i class="bi bi-pencil"></i>
                            </button>
                            <button class="btn btn-action btn-outline-success" onclick="openStockModal(${p.id},'${escHtml(p.name)}','add')" title="Agregar stock">
                                <i class="bi bi-plus-circle"></i>
                            </button>
                            <button class="btn btn-action btn-outline-warning" onclick="openStockModal(${p.id},'${escHtml(p.name)}','remove')" title="Quitar stock">
                                <i class="bi bi-dash-circle"></i>
                            </button>
                            <button class="btn btn-action btn-outline-danger" onclick="deleteProduct(${p.id})" title="Eliminar">
                                <i class="bi bi-trash"></i>
                            </button>
                        </td>
                    </tr>
                    `).join('')}
                </tbody>
            </table>
        </div>`;
}

function openCreateProduct() {
    const catOptions = state.cachedCategories.length
        ? state.cachedCategories.map(c => `<option value="${c.id}">${escHtml(c.name)}</option>`).join('')
        : '<option value="">Sin categorías (carga categorías primero)</option>';

    _openModal('Nuevo Producto', `
        <div class="mb-3">
            <label class="form-label">Nombre *</label>
            <input type="text" id="np-name" class="form-control" placeholder="Nombre del producto" required>
        </div>
        <div class="row g-3 mb-3">
            <div class="col-6">
                <label class="form-label">Precio *</label>
                <div class="input-group">
                    <span class="input-group-text">$</span>
                    <input type="number" id="np-price" class="form-control" step="0.01" min="0.01" placeholder="0.00">
                </div>
            </div>
            <div class="col-6">
                <label class="form-label">Stock inicial</label>
                <input type="number" id="np-qty" class="form-control" min="0" value="0">
            </div>
        </div>
        <div class="mb-3">
            <label class="form-label">Categoría</label>
            <select id="np-cat" class="form-select">${catOptions}</select>
        </div>
        <div class="form-check">
            <input class="form-check-input" type="checkbox" id="np-status" checked>
            <label class="form-check-label">Activo</label>
        </div>
    `, async () => {
        const name = document.getElementById('np-name').value.trim();
        const price = parseFloat(document.getElementById('np-price').value);
        const qty   = parseInt(document.getElementById('np-qty').value) || 0;
        const catId = parseInt(document.getElementById('np-cat').value) || null;
        const status = document.getElementById('np-status').checked;

        if (!name || !price) { _toast('Nombre y precio son requeridos', 'warning'); return; }

        try {
            await ProductsAPI.create({ name, price, quantity: qty, status, categoryId: catId });
            _toast('Producto creado', 'success');
            _closeModal();
        } catch (err) { _toast(err.message, 'danger'); }
    }, 'Crear');
}

async function openEditProduct(id) {
    try {
        const p = await ProductsAPI.getById(id);
        _openModal('Editar Producto', `
            <input type="hidden" id="ep-id" value="${p.id}">
            <div class="mb-3">
                <label class="form-label">Nombre *</label>
                <input type="text" id="ep-name" class="form-control" value="${escHtml(p.name)}" required>
            </div>
            <div class="row g-3 mb-3">
                <div class="col-6">
                    <label class="form-label">Precio *</label>
                    <div class="input-group">
                        <span class="input-group-text">$</span>
                        <input type="number" id="ep-price" class="form-control" step="0.01" min="0.01" value="${p.price || ''}">
                    </div>
                </div>
                <div class="col-6">
                    <label class="form-label">Stock</label>
                    <input type="number" id="ep-qty" class="form-control" min="0" value="${p.quantity}">
                </div>
            </div>
            <div class="form-check">
                <input class="form-check-input" type="checkbox" id="ep-status" ${p.status ? 'checked' : ''}>
                <label class="form-check-label">Activo</label>
            </div>
        `, async () => {
            const data = {
                id: parseInt(document.getElementById('ep-id').value),
                name: document.getElementById('ep-name').value.trim(),
                price: parseFloat(document.getElementById('ep-price').value),
                quantity: parseInt(document.getElementById('ep-qty').value) || 0,
                status: document.getElementById('ep-status').checked,
            };
            if (!data.name || !data.price) { _toast('Nombre y precio requeridos', 'warning'); return; }
            try {
                await ProductsAPI.update(data);
                _toast('Producto actualizado', 'success');
                _closeModal();
            } catch (err) { _toast(err.message, 'danger'); }
        }, 'Guardar');
    } catch (err) { _toast(err.message, 'danger'); }
}

function openStockModal(id, name, type) {
    const isAdd = type === 'add';
    _openModal(`${isAdd ? 'Agregar' : 'Quitar'} Stock`, `
        <p class="text-muted mb-3">Producto: <strong>${escHtml(name)}</strong></p>
        <div class="mb-3">
            <label class="form-label">Cantidad *</label>
            <input type="number" id="stock-qty" class="form-control form-control-lg" min="1" value="1">
        </div>
    `, async () => {
        const qty = parseInt(document.getElementById('stock-qty').value);
        if (!qty || qty < 1) { _toast('Cantidad inválida', 'warning'); return; }
        try {
            const fn = isAdd ? ProductsAPI.addStock : ProductsAPI.removeStock;
            const p = await fn(id, qty);
            _toast(`Stock ${isAdd ? 'agregado' : 'quitado'}. Nuevo stock: ${p.quantity}`, 'success');
            _closeModal();
        } catch (err) { _toast(err.message, 'danger'); }
    }, isAdd ? 'Agregar' : 'Quitar');
}

async function deleteProduct(id) {
    if (!confirm(`¿Eliminar producto ID ${id}? Esta acción no se puede deshacer.`)) return;
    try {
        await ProductsAPI.delete(id);
        _toast('Producto eliminado', 'success');
        const el = document.getElementById('prods-content');
        el.innerHTML = `<div class="empty-state"><i class="bi bi-check-circle text-success"></i><p>Producto eliminado.</p></div>`;
    } catch (err) { _toast(err.message, 'danger'); }
}

// ═══════════════════════════════════════════════════
// CATEGORIES VIEW
// ═══════════════════════════════════════════════════
let _cats = [];

async function _renderCategories() {
    const el = document.getElementById('view-categories');
    el.innerHTML = `
        <div class="view-header">
            <h4 class="view-title"><i class="bi bi-tags"></i>Categorías</h4>
            <button class="btn btn-primary" onclick="openCreateCategory()">
                <i class="bi bi-plus-lg me-1"></i>Nueva
            </button>
        </div>
        <div id="cats-content">
            <div class="loading-box"><div class="spinner-border text-primary"></div></div>
        </div>
    `;
    await _loadCategories();
}

async function _loadCategories() {
    const el = document.getElementById('cats-content');
    try {
        _cats = await CategoriesAPI.all();
        state.cachedCategories = _cats;
        if (!_cats.length) {
            el.innerHTML = `<div class="empty-state"><i class="bi bi-tags"></i><p>Sin categorías registradas.</p></div>`;
            return;
        }
        el.innerHTML = `
            <div class="table-wrap">
                <table class="table">
                    <thead>
                        <tr><th>ID</th><th>Nombre</th><th>Estado</th><th>Productos</th><th>Acciones</th></tr>
                    </thead>
                    <tbody>
                        ${_cats.map(c => `
                        <tr>
                            <td class="text-muted small">${c.id}</td>
                            <td class="fw-semibold">${escHtml(c.name)}</td>
                            <td>
                                <span class="badge ${c.status ? 'bg-success' : 'bg-secondary'}">
                                    ${c.status ? 'Activa' : 'Inactiva'}
                                </span>
                            </td>
                            <td><span class="badge bg-info text-dark">${(c.products || []).length}</span></td>
                            <td>
                                <button class="btn btn-action btn-outline-primary" onclick="openEditCategory(${c.id})">
                                    <i class="bi bi-pencil"></i>
                                </button>
                                <button class="btn btn-action btn-outline-danger" onclick="deleteCategory(${c.id})">
                                    <i class="bi bi-trash"></i>
                                </button>
                            </td>
                        </tr>
                        `).join('')}
                    </tbody>
                </table>
            </div>`;
    } catch (err) {
        el.innerHTML = `<div class="alert alert-danger">${escHtml(err.message)}</div>`;
    }
}

function openCreateCategory() {
    _openModal('Nueva Categoría', `
        <div class="mb-3">
            <label class="form-label">Nombre *</label>
            <input type="text" id="nc-name" class="form-control" placeholder="Nombre de la categoría" required>
        </div>
    `, async () => {
        const name = document.getElementById('nc-name').value.trim();
        if (!name) { _toast('El nombre es requerido', 'warning'); return; }
        try {
            await CategoriesAPI.create(name);
            _toast('Categoría creada', 'success');
            _closeModal();
            _loadCategories();
        } catch (err) { _toast(err.message, 'danger'); }
    }, 'Crear');
}

function openEditCategory(id) {
    const cat = _cats.find(c => c.id === id);
    if (!cat) return;
    _openModal('Editar Categoría', `
        <div class="mb-3">
            <label class="form-label">Nombre *</label>
            <input type="text" id="ec-name" class="form-control" value="${escHtml(cat.name)}" required>
        </div>
        <div class="form-check">
            <input class="form-check-input" type="checkbox" id="ec-status" ${cat.status ? 'checked' : ''}>
            <label class="form-check-label">Activa</label>
        </div>
    `, async () => {
        const name = document.getElementById('ec-name').value.trim();
        const status = document.getElementById('ec-status').checked;
        if (!name) { _toast('El nombre es requerido', 'warning'); return; }
        try {
            await CategoriesAPI.update(id, name, status);
            _toast('Categoría actualizada', 'success');
            _closeModal();
            _loadCategories();
        } catch (err) { _toast(err.message, 'danger'); }
    }, 'Guardar');
}

async function deleteCategory(id) {
    const cat = _cats.find(c => c.id === id);
    if (!confirm(`¿Eliminar categoría "${cat?.name || id}"?`)) return;
    try {
        await CategoriesAPI.delete(id);
        _toast('Categoría eliminada', 'success');
        _loadCategories();
    } catch (err) { _toast(err.message, 'danger'); }
}

// ═══════════════════════════════════════════════════
// SALES VIEW
// ═══════════════════════════════════════════════════
let _saleProduct = null;

function _renderSales() {
    state.cart = [];
    _saleProduct = null;
    const el = document.getElementById('view-sales');
    el.innerHTML = `
        <div class="view-header">
            <h4 class="view-title"><i class="bi bi-cart-check"></i>Nueva Venta</h4>
        </div>

        <div class="row g-4">
            <!-- Product Search Panel -->
            <div class="col-lg-7">
                <div class="card mb-3">
                    <div class="card-header">Buscar Producto por ID</div>
                    <div class="card-body">
                        <div class="input-group mb-3">
                            <input type="number" id="sale-pid" class="form-control" placeholder="ID del producto" min="1">
                            <button class="btn btn-primary" onclick="searchSaleProduct()">
                                <i class="bi bi-search me-1"></i>Buscar
                            </button>
                        </div>
                        <div id="sale-prod-info" class="d-none">
                            <div class="card bg-light border-0 mb-3">
                                <div class="card-body py-3">
                                    <div class="d-flex justify-content-between align-items-start">
                                        <div>
                                            <div class="fw-bold" id="sp-name"></div>
                                            <div class="text-muted small">Código: <span id="sp-code"></span></div>
                                        </div>
                                        <div class="text-end">
                                            <div class="fw-bold text-success fs-5 text-money" id="sp-price"></div>
                                            <div class="text-muted small">Stock: <span id="sp-stock" class="fw-semibold"></span></div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="row g-2 align-items-end">
                                <div class="col-4">
                                    <label class="form-label">Cantidad</label>
                                    <input type="number" id="sp-qty" class="form-control" min="1" value="1">
                                </div>
                                <div class="col-5">
                                    <label class="form-label">Precio unitario</label>
                                    <div class="input-group">
                                        <span class="input-group-text">$</span>
                                        <input type="number" id="sp-uprice" class="form-control" step="0.01" min="0.01">
                                    </div>
                                </div>
                                <div class="col-3">
                                    <button class="btn btn-success w-100" onclick="addToCart()">
                                        <i class="bi bi-cart-plus"></i> Agregar
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Cart Panel -->
            <div class="col-lg-5">
                <div class="card">
                    <div class="card-header d-flex justify-content-between align-items-center">
                        <span>Carrito</span>
                        <span class="badge bg-primary rounded-pill" id="cart-count">0</span>
                    </div>
                    <div id="cart-items-wrap" style="min-height:120px">
                        <div class="empty-state" style="padding:32px 16px">
                            <i class="bi bi-cart" style="font-size:2rem"></i>
                            <p class="small mb-0">Carrito vacío</p>
                        </div>
                    </div>
                    <div class="card-footer">
                        <div id="cart-totals" class="d-none mb-3">
                            <table class="totals-table w-100">
                                <tr><td>Subtotal</td><td class="text-end text-money" id="tot-sub"></td></tr>
                                <tr><td>IVA (19%)</td><td class="text-end text-money" id="tot-iva"></td></tr>
                                <tr><td>Total</td><td class="text-end text-money" id="tot-total"></td></tr>
                            </table>
                        </div>
                        <button class="btn btn-success w-100 py-2 fw-semibold" id="btn-checkout" disabled onclick="checkout()">
                            <i class="bi bi-check-circle me-2"></i>Confirmar Venta
                        </button>
                    </div>
                </div>
            </div>
        </div>
    `;
}

async function searchSaleProduct() {
    const id = document.getElementById('sale-pid').value;
    if (!id) return;
    try {
        const p = await ProductsAPI.getById(id);
        _saleProduct = p;

        document.getElementById('sp-name').textContent = p.name;
        document.getElementById('sp-code').textContent = p.code || '—';
        document.getElementById('sp-price').textContent = p.price != null ? `$${p.price.toFixed(2)}` : '—';
        document.getElementById('sp-stock').textContent = p.quantity;
        document.getElementById('sp-uprice').value = p.price || '';
        document.getElementById('sp-qty').value = 1;
        document.getElementById('sale-prod-info').classList.remove('d-none');
    } catch (err) {
        document.getElementById('sale-prod-info').classList.add('d-none');
        _toast(err.message, 'danger');
    }
}

function addToCart() {
    if (!_saleProduct) return;
    const qty    = parseInt(document.getElementById('sp-qty').value);
    const uPrice = parseFloat(document.getElementById('sp-uprice').value);

    if (!qty || qty < 1)      { _toast('Cantidad inválida', 'warning'); return; }
    if (!uPrice || uPrice <= 0) { _toast('Precio inválido', 'warning'); return; }

    const existing = state.cart.find(i => i.productId === _saleProduct.id);
    if (existing) {
        existing.quantity += qty;
        existing.subtotal  = existing.quantity * existing.unitPrice;
    } else {
        state.cart.push({
            productId: _saleProduct.id,
            name: _saleProduct.name,
            quantity: qty,
            unitPrice: uPrice,
            subtotal: qty * uPrice,
        });
    }

    _renderCart();
    document.getElementById('sale-pid').value = '';
    document.getElementById('sale-prod-info').classList.add('d-none');
    _saleProduct = null;
}

function removeCartItem(productId) {
    state.cart = state.cart.filter(i => i.productId !== productId);
    _renderCart();
}

function _renderCart() {
    const wrap    = document.getElementById('cart-items-wrap');
    const count   = document.getElementById('cart-count');
    const totals  = document.getElementById('cart-totals');
    const checkout = document.getElementById('btn-checkout');

    count.textContent = state.cart.length;

    if (!state.cart.length) {
        wrap.innerHTML = `<div class="empty-state" style="padding:32px 16px"><i class="bi bi-cart" style="font-size:2rem"></i><p class="small mb-0">Carrito vacío</p></div>`;
        totals.classList.add('d-none');
        checkout.disabled = true;
        return;
    }

    wrap.innerHTML = state.cart.map(item => `
        <div class="cart-item">
            <div class="flex-grow-1">
                <div class="cart-product-name">${escHtml(item.name)}</div>
                <div class="cart-product-meta">${item.quantity} × $${item.unitPrice.toFixed(2)}</div>
            </div>
            <div class="text-end me-3">
                <div class="text-money fw-semibold">$${item.subtotal.toFixed(2)}</div>
            </div>
            <button class="btn btn-sm btn-link text-danger p-0" onclick="removeCartItem(${item.productId})">
                <i class="bi bi-x-circle-fill fs-5"></i>
            </button>
        </div>
    `).join('');

    const sub   = state.cart.reduce((s, i) => s + i.subtotal, 0);
    const iva   = sub * 0.19;
    const total = sub + iva;

    document.getElementById('tot-sub').textContent   = `$${sub.toFixed(2)}`;
    document.getElementById('tot-iva').textContent   = `$${iva.toFixed(2)}`;
    document.getElementById('tot-total').textContent = `$${total.toFixed(2)}`;

    totals.classList.remove('d-none');
    checkout.disabled = false;
}

async function checkout() {
    if (!state.cart.length) return;
    const btn = document.getElementById('btn-checkout');
    btn.disabled = true;
    btn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Procesando...';

    try {
        const items = state.cart.map(i => ({
            productId: i.productId,
            quantity: i.quantity,
            unitPrice: i.unitPrice,
        }));
        const sale = await SalesAPI.create(state.employeeId, items);
        _toast(`Venta #${sale.id} registrada — Total: $${sale.total?.toFixed(2)}`, 'success');
        _renderSales();
    } catch (err) {
        _toast(err.message, 'danger');
        btn.disabled = false;
        btn.innerHTML = '<i class="bi bi-check-circle me-2"></i>Confirmar Venta';
    }
}

// ═══════════════════════════════════════════════════
// EMPLOYEES VIEW
// ═══════════════════════════════════════════════════
function _renderEmployees() {
    const el = document.getElementById('view-employees');
    el.innerHTML = `
        <div class="view-header">
            <h4 class="view-title"><i class="bi bi-people"></i>Empleados</h4>
            <button class="btn btn-primary" onclick="openCreateEmployee()">
                <i class="bi bi-plus-lg me-1"></i>Nuevo
            </button>
        </div>

        <div class="card mb-4">
            <div class="card-header">Filtrar</div>
            <div class="card-body">
                <div class="row g-3 align-items-end">
                    <div class="col-md-3">
                        <label class="form-label">Rol</label>
                        <select id="emp-role" class="form-select">
                            <option value="">Todos</option>
                            <option value="ADMINISTRADOR">Administrador</option>
                            <option value="CAJERO">Cajero</option>
                        </select>
                    </div>
                    <div class="col-md-3">
                        <label class="form-label">Desde</label>
                        <input type="date" id="emp-start" class="form-control">
                    </div>
                    <div class="col-md-3">
                        <label class="form-label">Hasta</label>
                        <input type="date" id="emp-end" class="form-control">
                    </div>
                    <div class="col-md-3">
                        <button class="btn btn-outline-primary w-100" onclick="searchEmployees()">
                            <i class="bi bi-search me-1"></i>Buscar
                        </button>
                    </div>
                </div>
            </div>
        </div>

        <div id="emp-results">
            <div class="loading-box"><div class="spinner-border text-primary"></div></div>
        </div>
    `;
    searchEmployees();
}

async function searchEmployees() {
    const role  = document.getElementById('emp-role')?.value || '';
    const start = document.getElementById('emp-start')?.value || '';
    const end   = document.getElementById('emp-end')?.value || '';
    const el    = document.getElementById('emp-results');

    el.innerHTML = `<div class="loading-box"><div class="spinner-border text-primary"></div></div>`;

    try {
        const emps = await EmployeesAPI.search(role || null, start || null, end || null);
        if (!emps.length) {
            el.innerHTML = `<div class="empty-state"><i class="bi bi-people"></i><p>Sin empleados encontrados.</p></div>`;
            return;
        }
        el.innerHTML = `
            <div class="table-wrap">
                <table class="table">
                    <thead>
                        <tr><th>ID</th><th>Documento</th><th>Nombre</th><th>Rol</th><th>Ingreso</th><th>Salario</th></tr>
                    </thead>
                    <tbody>
                        ${emps.map(e => `
                        <tr>
                            <td class="text-muted small">${e.id}</td>
                            <td>${escHtml(e.document)}</td>
                            <td class="fw-semibold">${escHtml(e.name)}</td>
                            <td>
                                <span class="badge ${e.role === 'ADMINISTRADOR' ? 'bg-primary' : 'bg-info text-dark'}">
                                    ${e.role}
                                </span>
                            </td>
                            <td class="text-muted small">${e.hireDate || '—'}</td>
                            <td class="text-money">$${e.salary?.toFixed(2) || '—'}</td>
                        </tr>
                        `).join('')}
                    </tbody>
                </table>
            </div>`;
    } catch (err) {
        el.innerHTML = `<div class="alert alert-danger">${escHtml(err.message)}</div>`;
    }
}

function openCreateEmployee() {
    _openModal('Nuevo Empleado', `
        <div class="row g-3">
            <div class="col-6">
                <label class="form-label">Documento *</label>
                <input type="text" id="ne-doc" class="form-control" placeholder="Cédula / NIT">
            </div>
            <div class="col-6">
                <label class="form-label">Nombre completo *</label>
                <input type="text" id="ne-name" class="form-control">
            </div>
            <div class="col-6">
                <label class="form-label">Rol *</label>
                <select id="ne-role" class="form-select">
                    <option value="ADMINISTRADOR">Administrador</option>
                    <option value="CAJERO" selected>Cajero</option>
                </select>
            </div>
            <div class="col-6">
                <label class="form-label">Salario *</label>
                <div class="input-group">
                    <span class="input-group-text">$</span>
                    <input type="number" id="ne-salary" class="form-control" step="0.01" min="1">
                </div>
            </div>
            <div class="col-6">
                <label class="form-label">Usuario *</label>
                <input type="text" id="ne-user" class="form-control" placeholder="nombre.usuario">
            </div>
            <div class="col-6">
                <label class="form-label">Contraseña *</label>
                <input type="password" id="ne-pass" class="form-control">
            </div>
        </div>
        <div class="alert alert-info small mt-3 mb-0 py-2">
            <i class="bi bi-info-circle me-1"></i>La fecha de ingreso se registra como hoy.
        </div>
    `, async () => {
        const doc    = document.getElementById('ne-doc').value.trim();
        const name   = document.getElementById('ne-name').value.trim();
        const role   = document.getElementById('ne-role').value;
        const salary = parseFloat(document.getElementById('ne-salary').value);
        const user   = document.getElementById('ne-user').value.trim();
        const pass   = document.getElementById('ne-pass').value;

        if (!doc || !name || !salary || !user || !pass) {
            _toast('Completa todos los campos requeridos', 'warning'); return;
        }

        try {
            await EmployeesAPI.register({ document: doc, name, role, salary, username: user, password: pass });
            _toast('Empleado registrado', 'success');
            _closeModal();
            searchEmployees();
        } catch (err) { _toast(err.message, 'danger'); }
    }, 'Registrar');
}

// ═══════════════════════════════════════════════════
// SUPPLIERS VIEW
// ═══════════════════════════════════════════════════
function _renderSuppliers() {
    const el = document.getElementById('view-suppliers');
    el.innerHTML = `
        <div class="view-header">
            <h4 class="view-title"><i class="bi bi-truck"></i>Proveedores</h4>
            <button class="btn btn-primary" onclick="openCreateSupplier()">
                <i class="bi bi-plus-lg me-1"></i>Nuevo
            </button>
        </div>

        <div class="row g-4">
            <!-- Search supplier -->
            <div class="col-md-5">
                <div class="card h-100">
                    <div class="card-header">Buscar Proveedor</div>
                    <div class="card-body">
                        <div class="input-group mb-3">
                            <input type="number" id="sup-id" class="form-control" placeholder="ID del proveedor">
                            <button class="btn btn-outline-primary" onclick="searchSupplier()">
                                <i class="bi bi-search"></i>
                            </button>
                        </div>
                        <div id="sup-result"></div>
                    </div>
                </div>
            </div>

            <!-- Stock entry -->
            <div class="col-md-7">
                <div class="card h-100">
                    <div class="card-header">Entrada de Stock</div>
                    <div class="card-body">
                        <div class="row g-3 mb-3">
                            <div class="col-4">
                                <label class="form-label">ID Producto *</label>
                                <input type="number" id="ent-prod" class="form-control" min="1">
                            </div>
                            <div class="col-4">
                                <label class="form-label">ID Proveedor *</label>
                                <input type="number" id="ent-prov" class="form-control" min="1">
                            </div>
                            <div class="col-4">
                                <label class="form-label">Cantidad *</label>
                                <input type="number" id="ent-qty" class="form-control" min="1">
                            </div>
                        </div>
                        <button class="btn btn-success w-100" onclick="registrarEntrada()">
                            <i class="bi bi-box-arrow-in-down me-1"></i>Registrar Entrada
                        </button>
                    </div>
                </div>
            </div>
        </div>
    `;
}

async function searchSupplier() {
    const id = document.getElementById('sup-id').value;
    if (!id) return;
    const res = document.getElementById('sup-result');
    try {
        const s = await SuppliersAPI.getById(id);
        res.innerHTML = `
            <div class="card border-success">
                <div class="card-body py-2">
                    <div class="fw-bold">${escHtml(s.nombre || 'Sin nombre')}</div>
                    <div class="text-muted small">NIT: ${escHtml(s.nit)}</div>
                    <div class="text-muted small">Productos vinculados: ${(s.productos || []).length}</div>
                </div>
            </div>`;
    } catch (err) {
        res.innerHTML = `<div class="alert alert-danger small py-2">${escHtml(err.message)}</div>`;
    }
}

function openCreateSupplier() {
    _openModal('Nuevo Proveedor', `
        <div class="mb-3">
            <label class="form-label">NIT *</label>
            <input type="text" id="ns-nit" class="form-control" placeholder="NIT del proveedor">
        </div>
        <div class="mb-3">
            <label class="form-label">Nombre</label>
            <input type="text" id="ns-nombre" class="form-control" placeholder="Nombre o razón social">
        </div>
    `, async () => {
        const nit    = document.getElementById('ns-nit').value.trim();
        const nombre = document.getElementById('ns-nombre').value.trim();
        if (!nit) { _toast('NIT es requerido', 'warning'); return; }
        try {
            await SuppliersAPI.create(nit, nombre);
            _toast('Proveedor creado', 'success');
            _closeModal();
        } catch (err) { _toast(err.message, 'danger'); }
    }, 'Crear');
}

async function registrarEntrada() {
    const prod = document.getElementById('ent-prod').value;
    const prov = document.getElementById('ent-prov').value;
    const qty  = document.getElementById('ent-qty').value;
    if (!prod || !prov || !qty) { _toast('Completa todos los campos', 'warning'); return; }
    try {
        const msg = await SuppliersAPI.stockEntry(prod, prov, qty);
        _toast(typeof msg === 'string' ? msg : 'Entrada registrada correctamente', 'success');
        document.getElementById('ent-prod').value = '';
        document.getElementById('ent-prov').value = '';
        document.getElementById('ent-qty').value  = '';
    } catch (err) { _toast(err.message, 'danger'); }
}

// ═══════════════════════════════════════════════════
// MODAL HELPERS
// ═══════════════════════════════════════════════════
let _bsModal   = null;
let _modalFn   = null;

function _openModal(title, bodyHtml, onConfirm, confirmLabel = 'Guardar') {
    document.getElementById('modal-title').textContent = title;
    document.getElementById('modal-body').innerHTML    = bodyHtml;
    document.getElementById('modal-confirm-text').textContent = confirmLabel;
    _modalFn = onConfirm;

    if (!_bsModal) {
        _bsModal = new bootstrap.Modal(document.getElementById('modal-main'));
    }
    _bsModal.show();

    // Focus first input
    setTimeout(() => {
        document.querySelector('#modal-body input, #modal-body select')?.focus();
    }, 300);
}

function _closeModal() { _bsModal?.hide(); }

document.getElementById('modal-confirm').addEventListener('click', () => {
    if (_modalFn) _modalFn();
});

// Allow Enter key to submit modal
document.getElementById('modal-main').addEventListener('keydown', e => {
    if (e.key === 'Enter' && !e.target.matches('textarea')) {
        e.preventDefault();
        if (_modalFn) _modalFn();
    }
});

// ═══════════════════════════════════════════════════
// TOAST
// ═══════════════════════════════════════════════════
function _toast(message, type = 'success') {
    const el   = document.getElementById('toast-el');
    const body = document.getElementById('toast-body');
    const icon = { success: 'bi-check-circle-fill', danger: 'bi-x-circle-fill', warning: 'bi-exclamation-triangle-fill', info: 'bi-info-circle-fill' };

    el.className = `toast align-items-center text-white border-0 bg-${type}`;
    body.innerHTML = `<i class="bi ${icon[type] || 'bi-info-circle-fill'} me-2"></i>${escHtml(message)}`;

    bootstrap.Toast.getOrCreateInstance(el, { delay: 4000 }).show();
}

// ═══════════════════════════════════════════════════
// UTILS
// ═══════════════════════════════════════════════════
function escHtml(str) {
    if (str == null) return '';
    return String(str)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#39;');
}

// ═══════════════════════════════════════════════════
// START
// ═══════════════════════════════════════════════════
_boot();
