let _hdTimer = null;
    let _hdLast  = '';

    function hdSearchInput(val) {

        if (val.length === 8) {
            clearTimeout(_hdTimer);
            _hdTimer = setTimeout(() => hdFetch(val), 350);
        }

        if (val.length === 0) hdClosePopup();
    }

    function hdSearchGo() {
        const val = document.getElementById('sidebarSearch').value.trim();
        if (val.length === 8) hdFetch(val);
    }

    document.getElementById('sidebarSearch').addEventListener('keydown', function(e) {
        if (e.key === 'Enter') { e.preventDefault(); hdSearchGo(); }
    });

    async function hdFetch(dni) {
        if (dni === _hdLast) { hdOpenPopup(); return; }
        _hdLast = dni;

        document.getElementById('sidebarSearchBtn').style.display    = 'none';
        document.getElementById('sidebarSearchLoading').style.display = 'block';

        try {
            const res = await fetch('/api/patient/search-dni?dni=' + encodeURIComponent(dni));

            if (res.status === 404) {
                hdRenderNotFound(dni);
            } else if (res.ok) {
                const p = await res.json();
                hdRenderFound(p);
            } else {
                hdRenderNotFound(dni);
            }
        } catch(e) {
            hdRenderNotFound(dni);
        } finally {
            document.getElementById('sidebarSearchBtn').style.display    = 'block';
            document.getElementById('sidebarSearchLoading').style.display = 'none';
        }
    }

    function hdRenderFound(p) {
        document.getElementById('hdPopupLabel').textContent = 'Paciente encontrado';

        const minor = p.minor
            ? '<span class="hd-popup-badge-minor">Menor</span>' : '';
        const phone    = p.phone    || '—';
        const district = p.district || '—';
        const age      = p.age      ? p.age + ' años' : '—';

        document.getElementById('hdPopupBody').innerHTML =
            '<div class="d-flex align-items-center gap-3 mb-3">'  +
                '<div class="hd-popup-avatar">' + esc(p.initials) + '</div>' +
                '<div>' +
                    '<div class="hd-popup-fullname">' + esc(p.fullName) + minor + '</div>' +
                    '<div class="hd-popup-sub">DNI: <strong>' + esc(p.dni) + '</strong> · ' + age + '</div>' +
                '</div>' +
            '</div>' +
            '<div class="hd-popup-fields">' +
                '<dl class="hd-popup-field mb-0"><dt>Teléfono</dt><dd>' + esc(phone) + '</dd></dl>' +
                '<dl class="hd-popup-field mb-0"><dt>Distrito</dt><dd>' + esc(district) + '</dd></dl>' +
            '</div>' +
            '<a href="/patient/' + p.id + '" class="hd-popup-cta">' +
                '<i class="bi bi-clipboard-pulse"></i> Ver ficha completa' +
            '</a>';

        hdOpenPopup();
    }

    function hdRenderNotFound(dni) {
        document.getElementById('hdPopupLabel').textContent = 'Sin resultados';
        document.getElementById('hdPopupBody').innerHTML =
            '<div class="hd-popup-nf">' +
                '<i class="bi bi-person-x"></i>' +
                '<p>No se encontró ningún paciente<br>con DNI <strong>' + esc(dni) + '</strong></p>' +
            '</div>';
        hdOpenPopup();
    }

    function hdOpenPopup() {

        const input  = document.getElementById('sidebarSearch');
        const rect   = input.getBoundingClientRect();
        const popup  = document.getElementById('hdPatientPopup');
        popup.style.top = Math.max(rect.top - 12, 12) + 'px';
        popup.classList.add('visible');
    }

    function hdClosePopup() {
        document.getElementById('hdPatientPopup').classList.remove('visible');
        document.getElementById('sidebarSearch').value = '';
        _hdLast = '';
    }

    document.addEventListener('click', function(e) {
        const popup = document.getElementById('hdPatientPopup');
        const input = document.getElementById('sidebarSearch');
        const btn   = document.getElementById('sidebarSearchBtn');
        if (popup.classList.contains('visible') &&
            !popup.contains(e.target) &&
            !input.contains(e.target) &&
            !btn.contains(e.target)) {
            hdClosePopup();
        }
    });

    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape') hdClosePopup();
    });

    function esc(s) {
        if (s == null) return '';
        const d = document.createElement('div');
        d.textContent = String(s);
        return d.innerHTML;
    }

    function toggleSidebar() {
        document.getElementById('hdSidebar').classList.toggle('show');
        document.getElementById('hdBackdrop').classList.toggle('show');
    }

function runBackup() {
    var btn = document.getElementById('btnBackup');
    if (btn.classList.contains('running')) return;
    if (!confirm('¿Ejecutar backup ahora?')) return;

    btn.classList.add('running');
    btn.innerHTML = '<span class="spinner-border spinner-border-sm" style="width:12px;height:12px;"></span> Respaldando...';
    btn.style.pointerEvents = 'none';

    fetch('/api/backup', { method: 'POST', credentials: 'same-origin' })
        .then(function(res) { return res.json(); })
        .then(function(data) {
            var bg = data.success ? '#0D9488' : '#EF4444';
            var icon = data.success ? '' : '';
            var t = document.createElement('div');
            t.style.cssText = 'position:fixed;bottom:24px;right:24px;background:' + bg + ';color:#fff;padding:12px 20px;border-radius:12px;font-size:13px;font-weight:600;box-shadow:0 4px 16px rgba(0,0,0,.15);z-index:9999;opacity:0;transition:opacity .3s;font-family:Outfit,sans-serif;';
            t.textContent = icon + ' ' + data.message;
            document.body.appendChild(t);
            requestAnimationFrame(function() { t.style.opacity = 1; });
            setTimeout(function() { t.style.opacity = 0; setTimeout(function() { t.remove(); }, 300); }, 4000);
        })
        .catch(function(e) { alert('Error: ' + e.message); })
        .finally(function() {
            btn.classList.remove('running');
            btn.innerHTML = '<i class="bi bi-cloud-arrow-up"></i> Backup';
            btn.style.pointerEvents = '';
        });
}
