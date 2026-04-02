const PRESELECTED_PATIENT_ID =  null;

    let selectedPatientId = null;
    let budgetItems = [];
    let allServices = {};
    let searchTimeout = null;
    let patientSearchTimeout = null;

    async function loadServices() {
        try {
            const res = await fetch('/api/services');
            if (!res.ok) { if (res.status === 401) window.location.href = '/auth/login'; return; }
            const groups = await res.json();

            allServices = {};
            const tabsDiv = document.getElementById('categoryTabs');
            tabsDiv.innerHTML = '';

            groups.forEach((g, i) => {
                allServices[g.category] = g.services;
                const tab = document.createElement('span');
                tab.className = 'category-tab' + (i === 0 ? ' active' : '');
                tab.textContent = g.categoryLabel;
                tab.dataset.category = g.category;
                tab.addEventListener('click', () => selectCategory(g.category, tab));
                tabsDiv.appendChild(tab);
            });

            if (groups.length > 0) renderServiceList(groups[0].category);
        } catch(e) { console.error(e); }
    }

    function selectCategory(category, tab) {
        document.querySelectorAll('.category-tab').forEach(t => t.classList.remove('active'));
        tab.classList.add('active');
        document.getElementById('serviceSearch').value = '';
        renderServiceList(category);
    }

    function renderServiceList(category) {
        const list = document.getElementById('serviceList');
        const services = allServices[category] || [];
        if (services.length === 0) {
            list.innerHTML = '<div class="text-center py-3 text-muted small">Sin servicios en esta categoría</div>';
            return;
        }
        list.innerHTML = services.map(s =>
            '<div class="service-item" onclick="addService(' + s.id + ',\'' +
            s.name.replace(/'/g, "\\'") + '\',' + s.unitPrice + ',\'' + s.currency + '\')">' +
            '<span class="service-name">' + s.name + '</span>' +
            '<span class="service-price">' + s.priceFormatted + '</span>' +
            '</div>'
        ).join('');
    }

    document.getElementById('serviceSearch').addEventListener('input', function() {
        const term = this.value.trim();
        if (term.length < 2) {
            const activeTab = document.querySelector('.category-tab.active');
            if (activeTab) renderServiceList(activeTab.dataset.category);
            return;
        }
        clearTimeout(searchTimeout);
        searchTimeout = setTimeout(async () => {
            const res = await fetch('/api/services/search?q=' + encodeURIComponent(term));
            if (!res.ok) return;
            const results = await res.json();
            const list = document.getElementById('serviceList');
            if (results.length === 0) {
                list.innerHTML = '<div class="text-center py-3 text-muted small">Sin resultados</div>';
                return;
            }
            list.innerHTML = results.map(s =>
                '<div class="service-item" onclick="addService(' + s.id + ',\'' +
                s.name.replace(/'/g, "\\'") + '\',' + s.unitPrice + ',\'' + s.currency + '\')">' +
                '<span class="service-name"><small class="text-muted">[' + s.categoryLabel + ']</small> ' + s.name + '</span>' +
                '<span class="service-price">' + s.priceFormatted + '</span>' +
                '</div>'
            ).join('');
        }, 250);
    });

    function searchPatientHandler(val) {
        const q = val.trim();
        if (q.length < 2) { document.getElementById('patientResults').style.display = 'none'; return; }
        clearTimeout(patientSearchTimeout);
        patientSearchTimeout = setTimeout(async () => {
            try {
                const res = await fetch('/api/appointments/patients/search?q=' + encodeURIComponent(q), {
                    credentials: 'same-origin'
                });
                if (!res.ok) {
                    if (res.status === 401) { window.location.href = '/auth/login'; return; }
                    return;
                }
                const patients = await res.json();
                const box = document.getElementById('patientResults');
                if (patients.length === 0) {
                    box.innerHTML = '<div class="text-center py-2 text-muted small">Sin resultados para "' + q + '"</div>';
                } else {
                    box.innerHTML = patients.map(p =>
                        '<div class="patient-result" onclick="selectPatient(' + p.id + ',\'' +
                        (p.name || '').replace(/'/g, "\\'") + '\',\'' + (p.dni || '') + '\')">' +
                        '<strong>' + (p.name || 'Sin nombre') + '</strong>' +
                        '<small class="text-muted ms-2">DNI: ' + (p.dni || '') + '</small></div>'
                    ).join('');
                }
                box.style.display = '';
            } catch(e) {
                console.error('Error en búsqueda:', e);
            }
        }, 300);
    }

    function selectPatient(id, name, dni) {
        selectedPatientId = id;
        document.getElementById('selectedPatientName').textContent = name;
        document.getElementById('selectedPatientDni').textContent = 'DNI: ' + dni;
        document.getElementById('selectedPatientBox').style.display = '';
        document.getElementById('patientSearchBox').querySelector('input').style.display = 'none';
        document.getElementById('patientResults').style.display = 'none';
    }

    function clearPatient() {
        selectedPatientId = null;
        document.getElementById('selectedPatientBox').style.display = 'none';
        const input = document.getElementById('patientSearchBox').querySelector('input');
        input.style.display = '';
        input.value = '';
    }

    function addService(serviceId, name, unitPrice, currency) {
        const id = Date.now();
        budgetItems.push({ id, serviceId, name, unitPrice, currency, tooth: '', quantity: 1, discount: 0, comment: '' });
        renderItems();
    }

    function removeItem(id) {
        budgetItems = budgetItems.filter(i => i.id !== id);
        renderItems();
    }

    function updateItem(id, field, value) {
        const item = budgetItems.find(i => i.id === id);
        if (!item) return;
        if (field === 'quantity') item.quantity = parseInt(value) || 1;
        else if (field === 'unitPrice') item.unitPrice = parseFloat(value) || 0;
        else if (field === 'discount') item.discount = parseFloat(value) || 0;
        else if (field === 'tooth') item.tooth = value;
        else if (field === 'comment') item.comment = value;
        renderItems();
    }

    function renderItems() {
        const tbody = document.getElementById('itemsBody');
        const emptyRow = document.getElementById('emptyRow');

        tbody.querySelectorAll('tr.item-row').forEach(r => r.remove());

        if (budgetItems.length === 0) {
            emptyRow.style.display = '';
            document.getElementById('itemCount').textContent = '0';
            document.getElementById('budgetTotal').textContent = 'S/ 0.00';
            return;
        }
        emptyRow.style.display = 'none';

        let total = 0;
        budgetItems.forEach((item, idx) => {
            const sub = (item.unitPrice * item.quantity) * (1 - item.discount / 100);
            total += sub;
            const symbol = item.currency === 'USD' ? 'US$' : 'S/';

            const tr = document.createElement('tr');
            tr.className = 'item-row';
            tr.innerHTML =
                '<td><span class="fw-semibold">' + item.name + '</span></td>' +
                '<td><input type="text" class="form-control form-control-sm" style="width:55px;" ' +
                    'value="' + item.tooth + '" onchange="updateItem(' + item.id + ',\'tooth\',this.value)"></td>' +
                '<td><input type="number" class="form-control form-control-sm" style="width:50px;" min="1" ' +
                    'value="' + item.quantity + '" onchange="updateItem(' + item.id + ',\'quantity\',this.value)"></td>' +
                '<td><span class="badge bg-light text-dark">' + item.currency + '</span></td>' +
                '<td><input type="number" class="form-control form-control-sm" style="width:80px;" step="0.01" ' +
                    'value="' + item.unitPrice.toFixed(2) + '" onchange="updateItem(' + item.id + ',\'unitPrice\',this.value)"></td>' +
                '<td><div style="display:flex;align-items:center;gap:3px;"><input type="number" class="form-control form-control-sm" style="width:55px;" min="0" max="100" step="1" ' +
                    'value="' + item.discount + '" onchange="updateItem(' + item.id + ',\'discount\',this.value)"><span style="font-size:11px;color:#64748B;font-weight:600;">%</span></div></td>' +
                '<td class="fw-semibold" style="color:#059669;">' + symbol + ' ' + sub.toFixed(2) + '</td>' +
                '<td><input type="text" class="form-control form-control-sm" style="width:100px;" placeholder="—" ' +
                    'value="' + (item.comment || '') + '" onchange="updateItem(' + item.id + ',\'comment\',this.value)"></td>' +
                '<td><button class="btn btn-outline-danger btn-sm py-0 px-1" onclick="removeItem(' + item.id + ')">' +
                    '<i class="bi bi-trash3"></i></button></td>';
            tbody.appendChild(tr);
        });

        document.getElementById('itemCount').textContent = budgetItems.length;
        document.getElementById('budgetTotal').textContent = 'S/ ' + total.toFixed(2);
    }

    async function confirmBudget() {
        if (!selectedPatientId) { showToast('Selecciona un paciente', 'error'); return; }
        if (budgetItems.length === 0) { showToast('Agrega al menos un servicio', 'error'); return; }

        const body = {
            patientId: selectedPatientId,
            budgetName: document.getElementById('budgetName').value || null,
            doctorName: document.getElementById('doctorName').value || null,
            patientNote: document.getElementById('patientNote').value || null,
            internalNote: document.getElementById('internalNote').value || null,
            items: budgetItems.map(i => ({
                serviceId: i.serviceId,
                tooth: i.tooth || null,
                quantity: i.quantity,
                unitPrice: i.unitPrice,
                discount: i.discount,
                comment: i.comment || null
            }))
        };

        try {
            const res = await fetch('/api/budgets', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(body)
            });

            if (!res.ok) {
                const err = await res.json();
                showToast(err.error || 'Error al crear', 'error');
                return;
            }

            const result = await res.json();
            showToast('Presupuesto creado exitosamente', 'success');
            setTimeout(() => { window.location.href = '/budgets/' + result.id; }, 800);
        } catch(e) {
            showToast('Error de conexión', 'error');
        }
    }

    function openQuickAddService() {
        document.getElementById('qsCategory').value = '';
        document.getElementById('qsName').value = '';
        document.getElementById('qsPrice').value = '';
        document.getElementById('qsCurrency').value = 'PEN';
        document.getElementById('qsCurrSymbol').textContent = 'S/';
        document.getElementById('qsAddToBudget').checked = true;

        const activeTab = document.querySelector('.category-tab.active');
        if (activeTab) {
            document.getElementById('qsCategory').value = activeTab.dataset.category;
        }

        new bootstrap.Modal(document.getElementById('quickServiceModal')).show();
    }

    async function saveQuickService() {
        const category = document.getElementById('qsCategory').value;
        const name = document.getElementById('qsName').value.trim();
        const price = document.getElementById('qsPrice').value;
        const currency = document.getElementById('qsCurrency').value;
        const addToBudget = document.getElementById('qsAddToBudget').checked;

        if (!category) { showToast('Selecciona una categoría', 'error'); return; }
        if (!name)     { showToast('Ingresa el nombre del servicio', 'error'); return; }
        if (!price || parseFloat(price) < 0) { showToast('Ingresa un precio válido', 'error'); return; }

        try {
            const res = await fetch('/api/services', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    category: category,
                    name: name,
                    unitPrice: parseFloat(price),
                    currency: currency
                })
            });

            const data = await res.json();
            if (!res.ok) throw new Error(data.error || 'Error al crear servicio');

            bootstrap.Modal.getInstance(document.getElementById('quickServiceModal'))?.hide();

            await loadServices();

            if (addToBudget) {
                addService(data.id, data.name, parseFloat(data.unitPrice), data.currency);
            }

            showToast('Servicio "' + name + '" creado exitosamente', 'success');

        } catch (err) {
            showToast(err.message, 'error');
        }
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

    if (PRESELECTED_PATIENT_ID) {
        fetch('/api/appointments/patients/search?q=' + PRESELECTED_PATIENT_ID)
            .then(r => r.json()).then(patients => {
                if (patients.length > 0) {
                    const p = patients[0];
                    selectPatient(p.id, p.name, p.dni);
                }
            }).catch(() => {});
    }
