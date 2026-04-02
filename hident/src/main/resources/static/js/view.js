(function() {
    'use strict';

    var meta     = document.getElementById('budgetMeta');
    var BUDGET_ID  = parseInt(meta.dataset.budgetId, 10);
    var PATIENT_ID = parseInt(meta.dataset.patientId, 10);

    var _servicesData = null;

    window.changeStatus = async function(newStatus) {
        try {
            var res = await fetch('/api/budgets/' + BUDGET_ID + '/status', {
                method: 'PATCH',
                headers: { 'Content-Type': 'application/json' },
                credentials: 'same-origin',
                body: JSON.stringify({ status: newStatus })
            });
            if (res.ok) { location.reload(); }
            else {
                var err = await res.json();
                alert(err.error || 'Error al cambiar estado');
            }
        } catch(e) { alert('Error de conexión'); }
    };

    window.deleteBudget = async function() {
        if (!confirm('¿Eliminar este presupuesto? Esta acción no se puede deshacer.')) return;
        try {
            var res = await fetch('/api/budgets/' + BUDGET_ID, {
                method: 'DELETE',
                credentials: 'same-origin'
            });
            if (res.ok) { window.location.href = '/budgets'; }
            else { alert('Error al eliminar'); }
        } catch(e) { alert('Error de conexión'); }
    };

    window.removeItem = async function(itemId) {
        if (!confirm('¿Eliminar este servicio del presupuesto?')) return;
        try {
            var res = await fetch('/api/budgets/' + BUDGET_ID + '/items/' + itemId, {
                method: 'DELETE',
                credentials: 'same-origin'
            });
            if (res.ok) { location.reload(); }
            else {
                var err = await res.json();
                alert(err.error || 'Error al eliminar ítem');
            }
        } catch(e) { alert('Error de conexión'); }
    };

    window.openEditItemModal = function(itemId, btn) {
        document.getElementById('editItemId').value       = itemId;
        document.getElementById('editItemService').value  = btn.dataset.service  || '';
        document.getElementById('editItemTooth').value    = btn.dataset.tooth    || '';
        document.getElementById('editItemQty').value      = btn.dataset.quantity || 1;
        document.getElementById('editItemPrice').value    = btn.dataset.price    || 0;
        document.getElementById('editItemDiscount').value = btn.dataset.discount || 0;
        document.getElementById('editItemComment').value  = btn.dataset.comment  || '';

        new bootstrap.Modal(document.getElementById('editItemModal')).show();
    };

    window.submitEditItem = async function() {
        var itemId   = document.getElementById('editItemId').value;
        var tooth    = document.getElementById('editItemTooth').value;
        var quantity = parseInt(document.getElementById('editItemQty').value, 10) || 1;
        var price    = parseFloat(document.getElementById('editItemPrice').value) || 0;
        var discount = parseFloat(document.getElementById('editItemDiscount').value) || 0;
        var comment  = document.getElementById('editItemComment').value;

        try {
            var res = await fetch('/api/budgets/' + BUDGET_ID + '/items/' + itemId, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                credentials: 'same-origin',
                body: JSON.stringify({
                    tooth: tooth,
                    quantity: quantity,
                    unitPrice: price,
                    discount: discount,
                    comment: comment
                })
            });
            if (res.ok) { location.reload(); }
            else {
                var err = await res.json();
                alert(err.error || 'Error al actualizar ítem');
            }
        } catch(e) { alert('Error de conexión'); }
    };

    window.openAddItemModal = async function() {

        clearServiceSelection();
        document.getElementById('addSvcSearch').value = '';

        var modal = new bootstrap.Modal(document.getElementById('addItemModal'));
        modal.show();

        if (!_servicesData) {
            try {
                var res = await fetch('/api/services', { credentials: 'same-origin' });
                if (res.ok) {
                    _servicesData = await res.json();
                } else {
                    document.getElementById('addSvcList').innerHTML =
                        '<div class="text-center text-danger py-4 small">Error al cargar servicios</div>';
                    return;
                }
            } catch(e) {
                document.getElementById('addSvcList').innerHTML =
                    '<div class="text-center text-danger py-4 small">Error de conexión</div>';
                return;
            }
        }

        renderServiceList(_servicesData);
    };

    function renderServiceList(groups) {
        var container = document.getElementById('addSvcList');
        var html = '';

        groups.forEach(function(group) {
            html += '<div class="svc-group-title">' + escHtml(group.categoryLabel) + '</div>';
            group.services.forEach(function(svc) {
                html += '<div class="svc-option" onclick=\'selectService(' +
                    JSON.stringify({
                        id: svc.id,
                        name: svc.name,
                        unitPrice: svc.unitPrice,
                        currency: svc.currency,
                        priceFormatted: svc.priceFormatted
                    }).replace(/'/g, "\\'") +
                    ')\'>';
                html += '<span>' + escHtml(svc.name) + '</span>';
                html += '<span class="svc-price">' + escHtml(svc.priceFormatted) + '</span>';
                html += '</div>';
            });
        });

        container.innerHTML = html || '<div class="text-center text-muted py-4 small">Sin servicios</div>';
    }

    window.filterServices = function() {
        if (!_servicesData) return;
        var term = document.getElementById('addSvcSearch').value.toLowerCase().trim();
        if (!term) {
            renderServiceList(_servicesData);
            return;
        }

        var filtered = _servicesData.map(function(group) {
            return {
                category: group.category,
                categoryLabel: group.categoryLabel,
                services: group.services.filter(function(svc) {
                    return svc.name.toLowerCase().indexOf(term) >= 0;
                })
            };
        }).filter(function(group) {
            return group.services.length > 0;
        });

        renderServiceList(filtered);
    };

    window.selectService = function(svc) {
        document.getElementById('addSvcList').style.display     = 'none';
        document.getElementById('addSvcSearch').style.display   = 'none';
        document.getElementById('addSvcSelected').style.display = 'block';
        document.getElementById('addSvcSubmit').disabled        = false;

        document.getElementById('addSvcId').value       = svc.id;
        document.getElementById('addSvcName').textContent = svc.name;
        document.getElementById('addSvcPrice').value     = svc.unitPrice;
        document.getElementById('addSvcCurrency').value  = svc.currency;
        document.getElementById('addSvcQty').value       = 1;
        document.getElementById('addSvcDiscount').value  = 0;
        document.getElementById('addSvcTooth').value     = '';
        document.getElementById('addSvcComment').value   = '';
    };

    window.clearServiceSelection = function() {
        document.getElementById('addSvcList').style.display     = 'block';
        document.getElementById('addSvcSearch').style.display   = 'block';
        document.getElementById('addSvcSelected').style.display = 'none';
        document.getElementById('addSvcSubmit').disabled        = true;
    };

    window.submitAddItem = async function() {
        var serviceId = document.getElementById('addSvcId').value;
        var tooth     = document.getElementById('addSvcTooth').value;
        var quantity  = parseInt(document.getElementById('addSvcQty').value, 10) || 1;
        var unitPrice = parseFloat(document.getElementById('addSvcPrice').value) || 0;
        var discount  = parseFloat(document.getElementById('addSvcDiscount').value) || 0;
        var comment   = document.getElementById('addSvcComment').value;

        if (!serviceId) { alert('Seleccione un servicio'); return; }

        try {
            var res = await fetch('/api/budgets/' + BUDGET_ID + '/items', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                credentials: 'same-origin',
                body: JSON.stringify({
                    serviceId: parseInt(serviceId, 10),
                    tooth: tooth || null,
                    quantity: quantity,
                    unitPrice: unitPrice,
                    discount: discount,
                    comment: comment || null
                })
            });
            if (res.ok) { location.reload(); }
            else {
                var err = await res.json();
                alert(err.error || 'Error al agregar servicio');
            }
        } catch(e) { alert('Error de conexión'); }
    };

    window.openHistoryModal = function() {
        new bootstrap.Modal(document.getElementById('historyModal')).show();
    };

    window.deleteFromHistory = async function(budgetId) {
        if (!confirm('¿Eliminar este presupuesto? Esta acción no se puede deshacer.')) return;
        try {
            var res = await fetch('/api/budgets/' + budgetId, {
                method: 'DELETE',
                credentials: 'same-origin'
            });
            if (res.ok) { location.reload(); }
            else {
                var err = await res.json();
                alert(err.error || 'Error al eliminar presupuesto');
            }
        } catch(e) { alert('Error de conexión'); }
    };

    function escHtml(str) {
        if (!str) return '';
        var div = document.createElement('div');
        div.textContent = str;
        return div.innerHTML;
    }

})();
