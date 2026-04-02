let allProformas = [];
let searchTimeout = null;

document.addEventListener('DOMContentLoaded', loadProformas);

async function loadProformas() {
    try {
        const res = await fetch('/api/proformas');
        if (!res.ok) { if (res.status === 401) window.location.href = '/auth/login'; return; }
        allProformas = await res.json();
        renderList(allProformas);
    } catch (e) { console.error(e); }
}

function renderList(data) {
    const container = document.getElementById('pfListContainer');
    const empty = document.getElementById('pfEmpty');

    if (!data || data.length === 0) {
        container.innerHTML = '';
        empty.style.display = 'flex';
        document.getElementById('pfStats').textContent = '';
        return;
    }

    empty.style.display = 'none';
    document.getElementById('pfStats').textContent = data.length + ' proforma' + (data.length !== 1 ? 's' : '');

    container.innerHTML = data.map(p => {
        const progress = p.itemCount > 0
            ? Math.round((p.completedCount / p.itemCount) * 100)
            : 0;

        return '<div class="pf-card" onclick="window.location.href=\'/proformas/' + p.id + '\'">' +
            '<div class="pf-card-top">' +
                '<div class="pf-card-patient">' +
                    '<div class="pf-card-avatar">' + (p.patientName ? p.patientName.charAt(0) : '?') + '</div>' +
                    '<div>' +
                        '<div class="pf-card-name">' + p.patientName + '</div>' +
                        '<div class="pf-card-dni">DNI: ' + p.patientDni + '</div>' +
                    '</div>' +
                '</div>' +
                '<span class="badge ' + p.statusBadgeClass + ' rounded-pill">' + p.statusLabel + '</span>' +
            '</div>' +
            (p.proformaName ? '<div class="pf-card-title">' + p.proformaName + '</div>' : '') +
            '<div class="pf-card-meta">' +
                '<span><i class="bi bi-calendar3 me-1"></i>' + p.createdAt + '</span>' +
                (p.doctorName ? '<span><i class="bi bi-person me-1"></i>' + p.doctorName + '</span>' : '') +
                '<span><i class="bi bi-list-check me-1"></i>' + p.completedCount + '/' + p.itemCount + ' realizados</span>' +
            '</div>' +
            '<div class="pf-card-bottom">' +
                '<div class="pf-progress-bar"><div class="pf-progress-fill" style="width:' + progress + '%"></div></div>' +
                '<div class="pf-card-total">' + p.totalFormatted + '</div>' +
            '</div>' +
        '</div>';
    }).join('');
}

function searchProformas(val) {
    clearTimeout(searchTimeout);
    const term = val.trim().toLowerCase();
    const status = document.getElementById('pfStatusFilter').value;

    searchTimeout = setTimeout(() => {
        let filtered = allProformas;
        if (term) {
            filtered = filtered.filter(p =>
                p.patientName.toLowerCase().includes(term) ||
                p.patientDni.includes(term) ||
                (p.proformaName && p.proformaName.toLowerCase().includes(term))
            );
        }
        if (status !== 'ALL') {
            filtered = filtered.filter(p => p.status === status);
        }
        renderList(filtered);
    }, 200);
}

function filterByStatus(status) {
    const term = document.getElementById('pfSearch').value.trim().toLowerCase();
    let filtered = allProformas;
    if (term) {
        filtered = filtered.filter(p =>
            p.patientName.toLowerCase().includes(term) ||
            p.patientDni.includes(term)
        );
    }
    if (status !== 'ALL') {
        filtered = filtered.filter(p => p.status === status);
    }
    renderList(filtered);
}
