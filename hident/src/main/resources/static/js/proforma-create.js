let selectedPatientId = null;
let proformaItems = [];
let allServices = {};
let patientSearchTimeout = null;
let serviceSearchTimeout = null;

async function loadServices() {
    try {
        const res = await fetch('/api/services');
        if (!res.ok) return;
        const groups = await res.json();
        allServices = {};
        const tabs = document.getElementById('categoryTabs');
        tabs.innerHTML = '';
        groups.forEach((g, i) => {
            allServices[g.category] = g.services;
            const tab = document.createElement('span');
            tab.className = 'category-tab' + (i === 0 ? ' active' : '');
            tab.textContent = g.categoryLabel;
            tab.dataset.category = g.category;
            tab.addEventListener('click', () => {
                document.querySelectorAll('.category-tab').forEach(t => t.classList.remove('active'));
                tab.classList.add('active');
                document.getElementById('serviceSearch').value = '';
                renderServiceList(g.category);
            });
            tabs.appendChild(tab);
        });
        if (groups.length > 0) renderServiceList(groups[0].category);
    } catch (e) { console.error(e); }
}

function renderServiceList(category) {
    const list = document.getElementById('serviceList');
    const services = allServices[category] || [];
    if (services.length === 0) {
        list.innerHTML = '<div class="text-center py-3 text-muted small">Sin servicios</div>';
        return;
    }
    list.innerHTML = services.map(s =>
        '<div class="pf-service-item" onclick="addFromService(' + s.id + ',\'' +
        s.name.replace(/'/g, "\\'") + '\',' + s.unitPrice + ',\'' + s.currency + '\')">' +
        '<span class="pf-service-name">' + s.name + '</span>' +
        '<span class="pf-service-price">' + s.priceFormatted + '</span>' +
        '</div>'
    ).join('');
}

document.getElementById('serviceSearch').addEventListener('input', function () {
    const term = this.value.trim();
    if (term.length < 2) {
        const active = document.querySelector('.category-tab.active');
        if (active) renderServiceList(active.dataset.category);
        return;
    }
    clearTimeout(serviceSearchTimeout);
    serviceSearchTimeout = setTimeout(async () => {
        const res = await fetch('/api/services/search?q=' + encodeURIComponent(term));
        if (!res.ok) return;
        const results = await res.json();
        const list = document.getElementById('serviceList');
        if (results.length === 0) {
            list.innerHTML = '<div class="text-center py-3 text-muted small">Sin resultados</div>';
            return;
        }
        list.innerHTML = results.map(s =>
            '<div class="pf-service-item" onclick="addFromService(' + s.id + ',\'' +
            s.name.replace(/'/g, "\\'") + '\',' + s.unitPrice + ',\'' + s.currency + '\')">' +
            '<span class="pf-service-name"><small class="text-muted">[' + s.categoryLabel + ']</small> ' + s.name + '</span>' +
            '<span class="pf-service-price">' + s.priceFormatted + '</span>' +
            '</div>'
        ).join('');
    }, 250);
});

function searchPatient(val) {
    const q = val.trim();
    if (q.length < 2) { document.getElementById('patientResults').style.display = 'none'; return; }
    clearTimeout(patientSearchTimeout);
    patientSearchTimeout = setTimeout(async () => {
        const res = await fetch('/api/appointments/patients/search?q=' + encodeURIComponent(q));
        if (!res.ok) return;
        const patients = await res.json();
        const box = document.getElementById('patientResults');
        if (patients.length === 0) {
            box.innerHTML = '<div class="text-center py-2 text-muted small">Sin resultados</div>';
        } else {
            box.innerHTML = patients.map(p =>
                '<div class="pf-result-item" onclick="selectPatient(' + p.id + ',\'' +
                (p.name || '').replace(/'/g, "\\'") + '\',\'' + (p.dni || '') + '\')">' +
                '<strong>' + p.name + '</strong><small class="text-muted ms-2">DNI: ' + p.dni + '</small></div>'
            ).join('');
        }
        box.style.display = '';
    }, 300);
}

function selectPatient(id, name, dni) {
    selectedPatientId = id;
    document.getElementById('selPatientName').textContent = name;
    document.getElementById('selPatientDni').textContent = 'DNI: ' + dni;
    document.getElementById('selectedPatient').style.display = '';
    document.getElementById('patientSearch').style.display = 'none';
    document.getElementById('patientResults').style.display = 'none';
    loadPatientBudgets(id);
}

function clearPatient() {
    selectedPatientId = null;
    document.getElementById('selectedPatient').style.display = 'none';
    document.getElementById('patientSearch').style.display = '';
    document.getElementById('patientSearch').value = '';
    document.getElementById('budgetImportCard').style.display = 'none';
}

async function loadPatientBudgets(patientId) {
    try {
        const res = await fetch('/api/proformas?patientId=' + patientId);

        const budgetRes = await fetch('/api/budgets?patientId=' + patientId);

        const card = document.getElementById('budgetImportCard');

        const dniEl = document.getElementById('selPatientDni');
        const dni = dniEl.textContent.replace('DNI: ', '');

        const searchRes = await fetch('/api/budgets/patient/' + patientId);
        if (!searchRes.ok) { card.style.display = 'none'; return; }

        const budgets = await searchRes.json();
        if (budgets.length === 0) { card.style.display = 'none'; return; }

        card.style.display = '';
        document.getElementById('budgetSelect').innerHTML = budgets.map(b =>
            '<div class="pf-budget-option" onclick="loadBudgetItems(' + b.id + ', this)">' +
                '<div class="fw-semibold small">' + (b.budgetName || 'Presupuesto #' + b.id) + '</div>' +
                '<div class="text-muted" style="font-size:11px;">' + b.totalFormatted + ' · ' + b.itemCount + ' ítems · ' + b.createdAt + '</div>' +
            '</div>'
        ).join('');
    } catch (e) {

        document.getElementById('budgetImportCard').style.display = 'none';
    }
}

async function loadBudgetItems(budgetId, el) {
    document.querySelectorAll('.pf-budget-option').forEach(o => o.classList.remove('active'));
    if (el) el.classList.add('active');

    try {
        const res = await fetch('/api/budgets/' + budgetId);
        if (!res.ok) return;
        const budget = await res.json();
        const items = budget.items || [];

        const list = document.getElementById('budgetItemsList');
        if (items.length === 0) {
            list.innerHTML = '<div class="text-muted small text-center py-2">Sin ítems</div>';
            return;
        }

        list.innerHTML = '<div class="small text-muted mb-2">Haz clic para agregar:</div>' +
            items.map(i =>
                '<div class="pf-service-item" onclick="addFromBudgetItem(\'' +
                i.serviceName.replace(/'/g, "\\'") + '\',' + i.subtotal + ',\'' + (i.currency || 'PEN') +
                '\',\'' + (i.tooth || '').replace(/'/g, "\\'") + '\')">' +
                '<span class="pf-service-name">' + i.serviceName +
                (i.tooth ? ' <small class="text-muted">(pza ' + i.tooth + ')</small>' : '') +
                '</span>' +
                '<span class="pf-service-price">' + i.subtotalFormatted + '</span>' +
                '</div>'
            ).join('');
    } catch (e) { console.error(e); }
}

function addFromService(serviceId, name, price, currency) {
    proformaItems.push({
        id: Date.now(), serviceId, serviceName: name,
        description: '', price, currency
    });
    renderItems();
}

function addFromBudgetItem(name, price, currency, tooth) {
    proformaItems.push({
        id: Date.now(), serviceId: null, serviceName: name,
        description: tooth ? 'pza ' + tooth : '', price, currency
    });
    renderItems();
}

function removeItem(id) {
    proformaItems = proformaItems.filter(i => i.id !== id);
    renderItems();
}

function updateItemField(id, field, value) {
    const item = proformaItems.find(i => i.id === id);
    if (!item) return;
    if (field === 'price') item.price = parseFloat(value) || 0;
    else if (field === 'description') item.description = value;
    else if (field === 'serviceName') item.serviceName = value;
    renderItems();
}

function renderItems() {
    const tbody = document.getElementById('itemsBody');
    const empty = document.getElementById('emptyRow');
    tbody.querySelectorAll('tr.pf-item-row').forEach(r => r.remove());

    if (proformaItems.length === 0) {
        empty.style.display = '';
        document.getElementById('itemCount').textContent = '0';
        document.getElementById('pfTotal').textContent = 'S/ 0.00';
        return;
    }
    empty.style.display = 'none';

    let total = 0;
    proformaItems.forEach(item => {
        total += item.price;
        const sym = item.currency === 'USD' ? 'US$' : 'S/';
        const tr = document.createElement('tr');
        tr.className = 'pf-item-row';
        tr.innerHTML =
            '<td><input type="text" class="form-control form-control-sm" value="' +
                (item.serviceName || '') + '" onchange="updateItemField(' + item.id + ',\'serviceName\',this.value)"></td>' +
            '<td><input type="text" class="form-control form-control-sm" style="width:120px;" placeholder="pza, detalle..." value="' +
                (item.description || '') + '" onchange="updateItemField(' + item.id + ',\'description\',this.value)"></td>' +
            '<td><div class="input-group input-group-sm" style="width:120px;"><span class="input-group-text" style="font-size:12px;">' +
                sym + '</span><input type="number" class="form-control" step="0.01" value="' +
                item.price.toFixed(2) + '" onchange="updateItemField(' + item.id + ',\'price\',this.value)"></div></td>' +
            '<td><button class="btn btn-outline-danger btn-sm py-0 px-1" onclick="removeItem(' + item.id + ')">' +
                '<i class="bi bi-trash3"></i></button></td>';
        tbody.appendChild(tr);
    });

    document.getElementById('itemCount').textContent = proformaItems.length;
    document.getElementById('pfTotal').textContent = 'S/ ' + total.toFixed(2);
}

async function confirmProforma() {
    if (!selectedPatientId) { showToast('Selecciona un paciente', 'error'); return; }
    if (proformaItems.length === 0) { showToast('Agrega al menos un ítem', 'error'); return; }

    const body = {
        patientId: selectedPatientId,
        proformaName: document.getElementById('pfName').value || null,
        doctorName: document.getElementById('pfDoctor').value || null,
        notes: document.getElementById('pfNotes').value || null,
        items: proformaItems.map(i => ({
            serviceId: i.serviceId,
            serviceName: i.serviceName,
            description: i.description || null,
            price: i.price,
            currency: i.currency
        }))
    };

    try {
        const res = await fetch('/api/proformas', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body)
        });
        if (!res.ok) { const err = await res.json(); showToast(err.error || 'Error', 'error'); return; }
        const result = await res.json();
        showToast('Proforma creada', 'success');
        setTimeout(() => { window.location.href = '/proformas/' + result.id; }, 800);
    } catch (e) { showToast('Error de conexión', 'error'); }
}

function openQuickService() {
    document.getElementById('qsCategory').value = '';
    document.getElementById('qsName').value = '';
    document.getElementById('qsPrice').value = '';
    document.getElementById('qsCurrency').value = 'PEN';
    document.getElementById('qsCurrSymbol').textContent = 'S/';
    const active = document.querySelector('.category-tab.active');
    if (active) document.getElementById('qsCategory').value = active.dataset.category;
    new bootstrap.Modal(document.getElementById('quickServiceModal')).show();
}

async function saveQuickService() {
    const category = document.getElementById('qsCategory').value;
    const name = document.getElementById('qsName').value.trim();
    const price = document.getElementById('qsPrice').value;
    const currency = document.getElementById('qsCurrency').value;

    if (!category) { showToast('Selecciona categoría', 'error'); return; }
    if (!name) { showToast('Ingresa nombre', 'error'); return; }
    if (!price || parseFloat(price) < 0) { showToast('Ingresa precio válido', 'error'); return; }

    try {
        const res = await fetch('/api/services', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ category, name, unitPrice: parseFloat(price), currency })
        });
        const data = await res.json();
        if (!res.ok) throw new Error(data.error || 'Error');

        bootstrap.Modal.getInstance(document.getElementById('quickServiceModal'))?.hide();
        await loadServices();
        addFromService(data.id, data.name, parseFloat(data.unitPrice), data.currency);
        showToast('Servicio "' + name + '" creado', 'success');
    } catch (e) { showToast(e.message, 'error'); }
}

function showToast(msg, type) {
    const bg = type === 'success' ? '#10B981' : type === 'info' ? '#3B82F6' : '#EF4444';
    const t = document.createElement('div');
    t.style.cssText = 'position:fixed;bottom:24px;right:24px;background:' + bg + ';color:#fff;' +
        'padding:10px 18px;border-radius:10px;font-size:13px;font-weight:600;' +
        'box-shadow:0 4px 12px rgba(0,0,0,.15);z-index:9999;opacity:0;transition:opacity .3s;';
    t.textContent = msg;
    document.body.appendChild(t);
    requestAnimationFrame(() => { t.style.opacity = 1; });
    setTimeout(() => { t.style.opacity = 0; setTimeout(() => t.remove(), 300); }, 2800);
}

loadServices();
