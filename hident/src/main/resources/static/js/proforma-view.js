let proformaData = null;
let selectedItemIds = new Set();

document.addEventListener('DOMContentLoaded', () => {
    if (PROFORMA_ID) loadProforma();
});

async function loadProforma() {
    try {
        const res = await fetch('/api/proformas/' + PROFORMA_ID);
        if (!res.ok) { if (res.status === 401) window.location.href = '/auth/login'; return; }
        proformaData = await res.json();
        selectedItemIds.clear();
        renderHeader();
        renderItems();
        renderTimeline();
    } catch (e) { console.error(e); }
}

function renderHeader() {
    const p = proformaData;
    document.getElementById('topTitle').textContent = p.proformaName || 'Proforma #' + p.id;

    document.getElementById('pfHeader').innerHTML =
        '<div class="pf-view-info">' +
            '<div class="pf-view-patient">' +
                '<div class="pf-card-avatar">' + (p.patientName ? p.patientName.charAt(0) : '?') + '</div>' +
                '<div>' +
                    '<div class="fw-bold">' + p.patientName + '</div>' +
                    '<div class="text-muted small">DNI: ' + p.patientDni + '</div>' +
                '</div>' +
            '</div>' +
            '<div class="pf-view-meta">' +
                '<span class="badge ' + p.statusBadgeClass + ' rounded-pill">' + p.statusLabel + '</span>' +
                '<span class="text-muted small"><i class="bi bi-calendar3 me-1"></i>' + p.createdAt + '</span>' +
                (p.doctorName ? '<span class="text-muted small"><i class="bi bi-person me-1"></i>' + p.doctorName + '</span>' : '') +
                '<span class="text-muted small"><i class="bi bi-check2-all me-1"></i>' + p.completedCount + '/' + p.itemCount + ' realizados</span>' +
            '</div>' +
        '</div>';

    const hasP = p.items && p.items.some(i => !i.completed);
    document.getElementById('pfInstructions').style.display = (hasP && p.status === 'ACTIVA') ? '' : 'none';
}

function renderItems() {
    const container = document.getElementById('pfItemsContainer');
    const items = proformaData.items || [];

    if (items.length === 0) {
        container.innerHTML = '<div class="text-center text-muted py-4">Sin ítems</div>';
        return;
    }

    let html = '';
    items.forEach(item => {
        const isDone = item.completed;
        const isSelected = selectedItemIds.has(item.id);

        let cls = 'pf-view-item';
        if (isDone) cls += ' done';
        else if (isSelected) cls += ' selected';
        else cls += ' pending';

        const clickAttr = isDone ? '' : ' onclick="toggleItem(' + item.id + ')"';
        const sym = item.currency === 'USD' ? 'US$' : 'S/';

        html += '<div class="' + cls + '"' + clickAttr + '>' +
            '<div class="pf-view-item-check">' +
                (isDone ? '<i class="bi bi-check-circle-fill"></i>' :
                 isSelected ? '<i class="bi bi-check-circle"></i>' :
                 '<i class="bi bi-circle"></i>') +
            '</div>' +
            '<div class="pf-view-item-body">' +
                '<div class="pf-view-item-name">' + item.serviceName + '</div>' +
                (item.description ? '<div class="pf-view-item-desc">' + item.description + '</div>' : '') +
                (isDone ? '<div class="pf-view-item-done-badge">' +
                    '<i class="bi bi-check2 me-1"></i>Realizado — ' + item.completedAt +
                    (item.completedBy ? ' por ' + item.completedBy : '') +
                '</div>' : '') +
            '</div>' +
            '<div class="pf-view-item-price">' + sym + ' ' + parseFloat(item.price).toFixed(2) + '</div>' +
        '</div>';
    });

    container.innerHTML = html;
    document.getElementById('viewItemCount').textContent = items.length;
    document.getElementById('pfViewTotal').textContent = proformaData.totalFormatted;

    document.getElementById('btnMarkDone').style.display =
        selectedItemIds.size > 0 ? '' : 'none';
}

function toggleItem(id) {
    if (selectedItemIds.has(id)) {
        selectedItemIds.delete(id);
    } else {
        selectedItemIds.add(id);
    }
    renderItems();
}

async function markSelectedDone() {
    if (selectedItemIds.size === 0) return;

    try {
        const res = await fetch('/api/proformas/' + PROFORMA_ID + '/complete', {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ itemIds: Array.from(selectedItemIds) })
        });

        if (!res.ok) {
            const err = await res.json();
            showToast(err.error || 'Error', 'error');
            return;
        }

        proformaData = await res.json();
        selectedItemIds.clear();
        renderHeader();
        renderItems();
        renderTimeline();
        showToast('Ítems marcados como realizados', 'success');
    } catch (e) {
        showToast('Error de conexión', 'error');
    }
}

function renderTimeline() {
    const items = proformaData.items || [];
    const completed = items.filter(i => i.completed && i.completedAt);

    const card = document.getElementById('timelineCard');
    if (completed.length === 0) { card.style.display = 'none'; return; }
    card.style.display = '';

    const groups = {};
    completed.forEach(item => {
        const dateKey = item.completedAt.split(' ')[0];
        if (!groups[dateKey]) groups[dateKey] = [];
        groups[dateKey].push(item);
    });

    const sortedDates = Object.keys(groups).sort((a, b) => {
        const pa = a.split('/'); const pb = b.split('/');
        const da = new Date(pa[2], pa[1] - 1, pa[0]);
        const db = new Date(pb[2], pb[1] - 1, pb[0]);
        return db - da;
    });

    let html = '<div class="pf-timeline">';
    sortedDates.forEach(date => {
        const dateItems = groups[date];
        html += '<div class="pf-timeline-group">' +
            '<div class="pf-timeline-date">' +
                '<i class="bi bi-calendar-check me-2"></i>' + date +
            '</div>' +
            '<div class="pf-timeline-items">';

        dateItems.forEach(item => {
            const time = item.completedAt.split(' ')[1] || '';
            const sym = item.currency === 'USD' ? 'US$' : 'S/';
            html += '<div class="pf-timeline-item">' +
                '<div class="pf-timeline-dot"></div>' +
                '<div class="pf-timeline-content">' +
                    '<div class="pf-timeline-item-name">' + item.serviceName + '</div>' +
                    (item.description ? '<div class="pf-timeline-item-desc">' + item.description + '</div>' : '') +
                    '<div class="pf-timeline-item-meta">' +
                        '<span>' + sym + ' ' + parseFloat(item.price).toFixed(2) + '</span>' +
                        '<span>' + time + '</span>' +
                        (item.completedBy ? '<span>por ' + item.completedBy + '</span>' : '') +
                    '</div>' +
                '</div>' +
            '</div>';
        });

        html += '</div></div>';
    });
    html += '</div>';

    document.getElementById('timelineBody').innerHTML = html;
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

let addPanelOpen = false;
let addSearchTimeout = null;

function toggleAddItemPanel() {
    addPanelOpen = !addPanelOpen;
    document.getElementById('addItemPanel').style.display = addPanelOpen ? '' : 'none';
    if (addPanelOpen) {
        document.getElementById('addItemName').value = '';
        document.getElementById('addItemDesc').value = '';
        document.getElementById('addItemPrice').value = '';
        document.getElementById('addItemSearch').value = '';
        document.getElementById('addItemResults').innerHTML = '';
        document.getElementById('addItemName').focus();
    }
}

async function addItemToProforma() {
    const name = document.getElementById('addItemName').value.trim();
    const desc = document.getElementById('addItemDesc').value.trim();
    const price = document.getElementById('addItemPrice').value;

    if (!name) { showToast('Ingresa el nombre del servicio', 'error'); return; }
    if (!price || parseFloat(price) < 0) { showToast('Ingresa un precio válido', 'error'); return; }

    try {
        const res = await fetch('/api/proformas/' + PROFORMA_ID + '/items', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                serviceName: name,
                description: desc || null,
                price: parseFloat(price),
                currency: 'PEN'
            })
        });

        if (!res.ok) { const err = await res.json(); showToast(err.error || 'Error', 'error'); return; }

        proformaData = await res.json();
        selectedItemIds.clear();
        renderHeader();
        renderItems();
        renderTimeline();

        document.getElementById('addItemName').value = '';
        document.getElementById('addItemDesc').value = '';
        document.getElementById('addItemPrice').value = '';
        document.getElementById('addItemResults').innerHTML = '';

        showToast('Ítem "' + name + '" agregado', 'success');
    } catch (e) {
        showToast('Error de conexión', 'error');
    }
}

function searchServiceForAdd(val) {
    const term = val.trim();
    if (term.length < 2) { document.getElementById('addItemResults').innerHTML = ''; return; }

    clearTimeout(addSearchTimeout);
    addSearchTimeout = setTimeout(async () => {
        try {
            const res = await fetch('/api/services/search?q=' + encodeURIComponent(term));
            if (!res.ok) return;
            const results = await res.json();
            const container = document.getElementById('addItemResults');

            if (results.length === 0) {
                container.innerHTML = '<div class="text-center text-muted small py-2">Sin resultados</div>';
                return;
            }

            container.innerHTML = results.map(s =>
                '<div style="display:flex;justify-content:space-between;align-items:center;padding:6px 10px;' +
                'border-bottom:1px solid #f1f5f9;cursor:pointer;font-size:13px;transition:background .15s;" ' +
                'onmouseover="this.style.background=\'#ecfdf5\'" onmouseout="this.style.background=\'#fff\'" ' +
                'onclick="pickServiceForAdd(\'' + s.name.replace(/'/g, "\\'") + '\',' + s.unitPrice + ',\'' + s.currency + '\')">' +
                '<span>' + s.name + ' <small style="color:#94a3b8;">[' + s.categoryLabel + ']</small></span>' +
                '<span style="color:#059669;font-weight:600;">' + s.priceFormatted + '</span>' +
                '</div>'
            ).join('');
        } catch (e) { console.error(e); }
    }, 250);
}

function pickServiceForAdd(name, price, currency) {
    document.getElementById('addItemName').value = name;
    document.getElementById('addItemPrice').value = price.toFixed(2);
    document.getElementById('addItemResults').innerHTML = '';
    document.getElementById('addItemSearch').value = '';
    document.getElementById('addItemDesc').focus();
}
