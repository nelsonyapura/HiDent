var HOUR_START = 8, HOUR_END = 21, HOUR_HEIGHT = 60;
var DAY_NAMES = ['Domingo', 'Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado'];
var DAY_SHORT = ['Dom', 'Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb'];
var MONTH_NAMES = ['ene', 'feb', 'mar', 'abr', 'may', 'jun', 'jul', 'ago', 'sep', 'oct', 'nov', 'dic'];
var MONTH_FULL = ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio', 'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'];
var currentMonday = getMonday(new Date()), currentView = 'week', currentDayOffset = 0;
var currentMonth = new Date().getMonth(), currentYear = new Date().getFullYear();
var appointments = [], searchTimer = null;

function getJwtToken() { var c = document.cookie.split(';'); for (var i = 0; i < c.length; i++) { var t = c[i].trim(); if (t.startsWith('JWT=')) return t.substring(4); } return null; }
function apiFetch(url, opts) { opts = opts || {}; opts.credentials = 'same-origin'; if (!opts.headers) opts.headers = {}; if (!opts.headers['Content-Type']) opts.headers['Content-Type'] = 'application/json'; var tk = getJwtToken(); if (tk) opts.headers['Authorization'] = 'Bearer ' + tk; return fetch(url, opts); }
function getMonday(d) { var dt = new Date(d.getFullYear(), d.getMonth(), d.getDate()); var day = dt.getDay(); dt.setDate(dt.getDate() + ((day === 0) ? -6 : (1 - day))); return dt; }
function addDays(d, n) { return new Date(d.getFullYear(), d.getMonth(), d.getDate() + n); }
function fmtDate(d) { return d.getFullYear() + '-' + String(d.getMonth() + 1).padStart(2, '0') + '-' + String(d.getDate()).padStart(2, '0'); }
function isToday(d) { var t = new Date(); return d.getFullYear() === t.getFullYear() && d.getMonth() === t.getMonth() && d.getDate() === t.getDate(); }
function isPast(d) { var t = new Date(); t.setHours(0, 0, 0, 0); return new Date(d.getFullYear(), d.getMonth(), d.getDate()) < t; }
function timeToMinutes(s) { if (!s) return 0; var p = s.split(':'); return parseInt(p[0]) * 60 + parseInt(p[1]); }
function minutesToTop(m) { return ((m - HOUR_START * 60) / 60) * HOUR_HEIGHT; }
function escA(s) { if (!s) return ''; var d = document.createElement('div'); d.textContent = s; return d.innerHTML; }
function getVisibleDays() { if (currentView === 'day') return [addDays(currentMonday, currentDayOffset)]; var days = []; for (var i = 0; i < 7; i++)days.push(addDays(currentMonday, i)); return days; }

function navigate(dir) {
    if (currentView === 'month') { currentMonth += dir; if (currentMonth > 11) { currentMonth = 0; currentYear++; } if (currentMonth < 0) { currentMonth = 11; currentYear--; } }
    else if (currentView === 'day') { currentDayOffset += dir; if (currentDayOffset > 6) { currentDayOffset = 0; currentMonday = addDays(currentMonday, 7); } if (currentDayOffset < 0) { currentDayOffset = 6; currentMonday = addDays(currentMonday, -7); } }
    else { currentMonday = addDays(currentMonday, dir * 7); }
    loadAppointments();
}

function goToday() { var now = new Date(); currentMonday = getMonday(now); currentMonth = now.getMonth(); currentYear = now.getFullYear(); var dow = now.getDay(); currentDayOffset = dow === 0 ? 6 : dow - 1; if (currentDayOffset > 5) currentDayOffset = 5; loadAppointments(); }

function setView(v) {
    currentView = v;
    document.getElementById('btnMonth').classList.toggle('active', v === 'month');
    document.getElementById('btnWeek').classList.toggle('active', v === 'week');
    document.getElementById('btnDay').classList.toggle('active', v === 'day');
    document.getElementById('weekDayView').style.display = (v === 'month') ? 'none' : 'block';
    document.getElementById('monthView').style.display = (v === 'month') ? 'block' : 'none';
    if (v === 'day') { var dow = new Date().getDay(); currentDayOffset = dow === 0 ? 6 : dow - 1; }
    if (v === 'month') { var d = getVisibleDays()[0]; currentMonth = d.getMonth(); currentYear = d.getFullYear(); }
    loadAppointments();
}

async function loadAppointments() {
    var start, end;
    if (currentView === 'month') { var first = new Date(currentYear, currentMonth, 1); var last = new Date(currentYear, currentMonth + 1, 0); var monBefore = getMonday(first); var sunAfter = addDays(last, 7 - last.getDay()); start = fmtDate(monBefore); end = fmtDate(sunAfter); }
    else { var days = getVisibleDays(); start = fmtDate(days[0]); end = fmtDate(days[days.length - 1]); }
    var status = document.getElementById('filterStatus').value;
    try { var res = await apiFetch('/api/appointments?start=' + start + '&end=' + end + '&status=' + status); if (res.status === 401) { window.location.href = '/auth/login'; return; } appointments = res.ok ? await res.json() : []; }
    catch (e) { console.error('Error:', e); appointments = []; }
    render();
}

function render() { try { if (currentView === 'month') renderMonth(); else renderWeekDay(); } catch (e) { console.error('Render error:', e); } }

function renderWeekDay() { var days = getVisibleDays(); var cols = '72px repeat(' + days.length + ', 1fr)'; renderHeader(days, cols); renderBody(days, cols); renderEvents(days); renderNowLine(); updateLabel(); }
function renderHeader(days, cols) { var el = document.getElementById('calHeader'); el.style.gridTemplateColumns = cols; var h = '<div class="cal-header-cell"></div>'; for (var i = 0; i < days.length; i++) { var d = days[i]; var cls = isToday(d) ? 'is-today' : (isPast(d) ? 'is-past' : ''); h += '<div class="cal-header-cell ' + cls + '"><div class="day-name">' + DAY_SHORT[d.getDay()] + '</div><div class="day-num">' + d.getDate() + '</div></div>'; } el.innerHTML = h; }
function renderBody(days, cols) { var el = document.getElementById('calBody'); el.style.gridTemplateColumns = cols; var h = '<div class="cal-time-col">'; for (var hr = HOUR_START; hr <= HOUR_END; hr++) { var lbl = hr === 0 ? '12 a.m.' : hr < 12 ? hr + ' a.m.' : hr === 12 ? '12 p.m.' : (hr - 12) + ' p.m.'; h += '<div class="cal-time-label">' + lbl + '</div>'; } h += '</div>'; for (var i = 0; i < days.length; i++) { var d = days[i]; var ds = fmtDate(d); var tc = isToday(d) ? ' is-today' : ''; h += '<div class="cal-day-col' + tc + '" data-date="' + ds + '">'; for (var hr = HOUR_START; hr <= HOUR_END; hr++) { var hh = String(hr).padStart(2, '0'); h += '<div class="cal-hour-row" data-hour="' + hh + '" onclick="onCellClick(\'' + ds + '\',\'' + hh + ':00\')"></div>'; } h += '</div>'; } el.innerHTML = h; }
function renderEvents(days) { var dateMap = {}; for (var i = 0; i < days.length; i++)dateMap[fmtDate(days[i])] = true; for (var j = 0; j < appointments.length; j++) { var a = appointments[j]; if (!dateMap[a.date]) continue; var col = document.querySelector('.cal-day-col[data-date="' + a.date + '"]'); if (!col) continue; var sm = timeToMinutes(a.startTime), em = a.endTime ? timeToMinutes(a.endTime) : sm + 30; var top = minutesToTop(sm), hgt = Math.max(((em - sm) / 60) * HOUR_HEIGHT, 24); var div = document.createElement('div'); div.className = 'cal-event st-' + a.status; div.style.top = top + 'px'; div.style.height = hgt + 'px'; var inner = '<span class="ev-name">' + escA(a.patientName) + '</span>'; if (hgt >= 36) inner += '<span class="ev-time">' + (a.startFormatted || '') + (a.reason ? ' · ' + escA(a.reason) : '') + '</span>'; div.innerHTML = inner; div.title = a.patientName + '\n' + (a.startFormatted || '') + ' - ' + (a.endFormatted || '') + '\n' + (a.reason || '') + '\nEstado: ' + a.statusLabel; (function (appt) { div.addEventListener('click', function (e) { e.stopPropagation(); openViewModal(appt); }); })(a); col.appendChild(div); } }
function renderNowLine() { document.querySelectorAll('.cal-now-line').forEach(function (e) { e.remove(); }); var now = new Date(); var col = document.querySelector('.cal-day-col[data-date="' + fmtDate(now) + '"]'); if (!col) return; var mins = now.getHours() * 60 + now.getMinutes(); if (mins < HOUR_START * 60 || mins > HOUR_END * 60) return; var line = document.createElement('div'); line.className = 'cal-now-line'; line.style.top = minutesToTop(mins) + 'px'; col.appendChild(line); }

function renderMonth() {
    var hdr = document.getElementById('monthHeader'); var hh = ''; var dayOrder = ['Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb', 'Dom']; for (var i = 0; i < 7; i++)hh += '<div class="month-header-cell">' + dayOrder[i] + '</div>'; hdr.innerHTML = hh;
    var first = new Date(currentYear, currentMonth, 1); var last = new Date(currentYear, currentMonth + 1, 0); var startDay = first.getDay(); var offset = (startDay === 0) ? 6 : startDay - 1;
    var apptsByDate = {}; for (var i = 0; i < appointments.length; i++) { var a = appointments[i]; if (!apptsByDate[a.date]) apptsByDate[a.date] = []; apptsByDate[a.date].push(a); }
    var body = document.getElementById('monthBody'); var html = ''; var d = addDays(first, -offset); var totalCells = Math.ceil((offset + last.getDate()) / 7) * 7;
    for (var c = 0; c < totalCells; c++) {
        var isOther = d.getMonth() !== currentMonth; var cls = 'month-cell' + (isOther ? ' other-month' : '') + (isToday(d) ? ' is-today' : ''); var ds = fmtDate(d);
        html += '<div class="' + cls + '" onclick="onMonthCellClick(\'' + ds + '\')">'; html += '<div class="month-day-num">' + d.getDate() + '</div>';
        var dayAppts = apptsByDate[ds] || []; for (var k = 0; k < Math.min(dayAppts.length, 3); k++) { var a = dayAppts[k]; html += '<div class="month-event-pill st-' + a.status + '" onclick="event.stopPropagation();openViewModalById(' + a.id + ')" title="' + escA(a.patientName) + ' · ' + (a.startFormatted || '') + '">' + escA(a.patientName) + '</div>'; }
        if (dayAppts.length > 3) html += '<div class="month-more">+' + (dayAppts.length - 3) + ' más</div>'; html += '</div>'; d = addDays(d, 1);
    }
    body.innerHTML = html; updateLabel();
}
function onMonthCellClick(dateStr) { var d = new Date(dateStr + 'T00:00:00'); currentMonday = getMonday(d); setView('week'); }
function openViewModalById(id) { var a = appointments.find(function (x) { return x.id === id; }); if (a) openViewModal(a); }

function updateLabel() {
    var txt;
    if (currentView === 'month') { txt = MONTH_FULL[currentMonth] + ' ' + currentYear; }
    else { var days = getVisibleDays(); var f = days[0], l = days[days.length - 1]; if (currentView === 'day') { txt = DAY_NAMES[f.getDay()] + ' ' + f.getDate() + ' ' + MONTH_NAMES[f.getMonth()] + ' ' + f.getFullYear(); } else if (f.getMonth() === l.getMonth()) { txt = f.getDate() + ' – ' + l.getDate() + ' ' + MONTH_NAMES[f.getMonth()] + ' ' + f.getFullYear(); } else { txt = f.getDate() + ' ' + MONTH_NAMES[f.getMonth()] + ' – ' + l.getDate() + ' ' + MONTH_NAMES[l.getMonth()] + ' ' + l.getFullYear(); } }
    document.getElementById('weekRange').textContent = txt;
}

function onCellClick(date, time) {
    document.getElementById('editId').value = '';
    document.getElementById('modalTitle').innerHTML = '<i class="bi bi-calendar-plus me-2" style="color:#14B8A6;"></i>Nueva Cita';
    document.getElementById('btnSaveText').textContent = 'Guardar';
    document.getElementById('patientSearch').value = '';
    document.getElementById('patientSearch').readOnly = false;
    document.getElementById('selectedPatientId').value = '';
    document.getElementById('apptDate').value = date;
    document.getElementById('apptStart').value = time;
    var p = time.split(':'), h = parseInt(p[0]), m = parseInt(p[1]) + 30; if (m >= 60) { h++; m -= 60; }
    document.getElementById('apptEnd').value = String(h).padStart(2, '0') + ':' + String(m).padStart(2, '0');
    document.getElementById('apptReason').value = '';
    document.getElementById('apptAssigned').value = '';
    document.getElementById('apptNotes').value = '';
    document.getElementById('modalError').classList.add('d-none');
    var el = document.getElementById('appointmentModal');
    var inst = bootstrap.Modal.getInstance(el); if (inst) inst.dispose();
    new bootstrap.Modal(el).show();
}
function openCreateModal() { onCellClick(fmtDate(new Date()), '09:00'); }

function searchPatients(term) { clearTimeout(searchTimer); var r = document.getElementById('patientResults'); if (!term || term.length < 2) { r.classList.remove('show'); return; } searchTimer = setTimeout(async function () { try { var res = await apiFetch('/api/appointments/patients/search?q=' + encodeURIComponent(term)); if (!res.ok) { r.classList.remove('show'); return; } var data = await res.json(); if (data.length === 0) { r.innerHTML = '<div class="px-3 py-2 text-muted small">Sin resultados</div>'; } else { var html = ''; for (var i = 0; i < data.length; i++) { var p = data[i]; html += '<div class="ps-item" onclick="selectPatient(' + p.id + ',\'' + escA(p.name).replace(/'/g, "\\'") + '\',\'' + p.dni + '\')"><span class="ps-name">' + escA(p.name) + '</span><span class="ps-dni ms-2">DNI: ' + p.dni + '</span></div>'; } r.innerHTML = html; } r.classList.add('show'); } catch (e) { r.classList.remove('show'); } }, 300); }
function selectPatient(id, name, dni) { document.getElementById('selectedPatientId').value = id; document.getElementById('patientSearch').value = name + ' — ' + dni; document.getElementById('patientResults').classList.remove('show'); }
document.addEventListener('click', function (e) { if (!e.target.closest('#patientSearch') && !e.target.closest('#patientResults')) document.getElementById('patientResults').classList.remove('show'); });

async function saveAppointment() {
    document.getElementById('modalError').classList.add('d-none');
    var editId = document.getElementById('editId').value, pid = document.getElementById('selectedPatientId').value, date = document.getElementById('apptDate').value, st = document.getElementById('apptStart').value, et = document.getElementById('apptEnd').value;
    if (!pid) { showModalError('Seleccione un paciente'); return; } if (!date) { showModalError('Seleccione una fecha'); return; } if (!st) { showModalError('Ingrese hora de inicio'); return; } if (!et) { showModalError('Ingrese hora de fin'); return; }
    var body = { patientId: Number(pid), appointmentDate: date, startTime: st, endTime: et, reason: document.getElementById('apptReason').value, notes: document.getElementById('apptNotes').value, assignedTo: document.getElementById('apptAssigned').value };
    try { var url = editId ? '/api/appointments/' + editId : '/api/appointments'; var method = editId ? 'PUT' : 'POST'; var res = await apiFetch(url, { method: method, body: JSON.stringify(body) }); var data = await res.json(); if (!res.ok) throw new Error(data.error || 'Error al guardar'); bootstrap.Modal.getInstance(document.getElementById('appointmentModal')).hide(); showToast(editId ? 'Cita actualizada' : 'Cita creada exitosamente', 'success'); loadAppointments(); } catch (e) { showModalError(e.message); }
}
function showModalError(msg) { var el = document.getElementById('modalError'); el.textContent = msg; el.classList.remove('d-none'); }

function openViewModal(a) {
    if (!a) return; var ini = a.patientName.split(' ').map(function (w) { return w[0]; }).slice(0, 2).join('');
    var h = '<div class="d-flex align-items-center gap-2 mb-3"><div class="view-avatar">' + ini + '</div><div><div class="fw-bold small" style="color:#1E293B;">' + escA(a.patientName) + '</div><div style="font-size:11px;color:#0F766E;font-weight:500;">DNI: ' + a.patientDni + '</div></div></div><div class="row g-2 mb-2"><div class="col-6"><div class="view-info-label">Fecha</div><div class="view-info-value">' + a.date + '</div></div><div class="col-6"><div class="view-info-label">Horario</div><div class="view-info-value">' + (a.startFormatted || '') + ' - ' + (a.endFormatted || '—') + '</div></div></div><div class="row g-2 mb-2"><div class="col-6"><div class="view-info-label">Estado</div><span class="badge" style="background:#F0FDFA;color:#0F766E;font-weight:600;">' + a.statusLabel + '</span></div><div class="col-6"><div class="view-info-label">Doctor</div><div class="view-info-value">' + (a.assignedTo || '—') + '</div></div></div>';
    if (a.reason) h += '<div class="view-info-label">Motivo</div><div class="view-info-value mb-1">' + escA(a.reason) + '</div>';
    if (a.notes) h += '<div class="view-info-label mt-1">Notas</div><div class="small" style="color:#475569;">' + escA(a.notes) + '</div>';
    document.getElementById('viewBody').innerHTML = h;
    var btns = '';
    if (a.status === 'PROGRAMADA' || a.status === 'CONFIRMADA') btns += '<button type="button" class="btn btn-sm btn-outline-teal flex-fill" onclick="openEditModal(' + a.id + ')"><i class="bi bi-pencil"></i> Editar</button>';
    if (a.status === 'PROGRAMADA') { btns += '<button type="button" class="btn btn-sm btn-teal flex-fill" onclick="changeStatus(' + a.id + ',\'CONFIRMADA\')"><i class="bi bi-check-lg"></i> Confirmar</button>'; btns += '<button type="button" class="btn btn-sm btn-outline-danger flex-fill" onclick="changeStatus(' + a.id + ',\'CANCELADA\')"><i class="bi bi-x-lg"></i> Cancelar</button>'; }
    if (a.status === 'CONFIRMADA') { btns += '<button type="button" class="btn btn-sm btn-teal flex-fill" onclick="changeStatus(' + a.id + ',\'ATENDIDA\')"><i class="bi bi-check-circle"></i> Atendida</button>'; btns += '<button type="button" class="btn btn-sm btn-outline-warning flex-fill" onclick="changeStatus(' + a.id + ',\'NO_ASISTIO\')"><i class="bi bi-person-x"></i> No asistió</button>'; btns += '<button type="button" class="btn btn-sm btn-outline-danger flex-fill" onclick="changeStatus(' + a.id + ',\'CANCELADA\')"><i class="bi bi-x-lg"></i> Cancelar</button>'; }
    btns += '<a href="/patient/' + a.patientId + '" class="btn btn-sm btn-outline-secondary flex-fill"><i class="bi bi-clipboard-pulse"></i> Ficha</a>';
    document.getElementById('viewActions').innerHTML = btns; document.getElementById('viewError').classList.add('d-none');
    var el = document.getElementById('viewModal'); var inst = bootstrap.Modal.getInstance(el); if (inst) inst.dispose(); new bootstrap.Modal(el).show();
}
async function openEditModal(id) {
    bootstrap.Modal.getInstance(document.getElementById('viewModal')).hide();
    try { var res = await apiFetch('/api/appointments/' + id); var a = await res.json(); document.getElementById('editId').value = id; document.getElementById('modalTitle').innerHTML = '<i class="bi bi-pencil-square me-2" style="color:#14B8A6;"></i>Editar Cita'; document.getElementById('btnSaveText').textContent = 'Actualizar'; document.getElementById('selectedPatientId').value = a.patientId; document.getElementById('patientSearch').value = a.patientName + ' — ' + a.patientDni; document.getElementById('patientSearch').readOnly = true; document.getElementById('apptDate').value = a.date; document.getElementById('apptStart').value = a.startTime; document.getElementById('apptEnd').value = a.endTime || ''; document.getElementById('apptReason').value = a.reason || ''; document.getElementById('apptAssigned').value = a.assignedTo || ''; document.getElementById('apptNotes').value = a.notes || ''; document.getElementById('modalError').classList.add('d-none'); setTimeout(function () { var el = document.getElementById('appointmentModal'); var inst = bootstrap.Modal.getInstance(el); if (inst) inst.dispose(); new bootstrap.Modal(el).show(); }, 350); } catch (e) { showToast('Error al cargar cita', 'error'); }
}
async function changeStatus(id, status) { try { var res = await apiFetch('/api/appointments/' + id + '/status', { method: 'PATCH', body: JSON.stringify({ status: status }) }); var data = await res.json(); if (!res.ok) throw new Error(data.error || 'Error'); bootstrap.Modal.getInstance(document.getElementById('viewModal')).hide(); showToast(data.message, 'success'); loadAppointments(); } catch (e) { var ve = document.getElementById('viewError'); ve.textContent = e.message; ve.classList.remove('d-none'); } }
function showToast(msg, type) { var bg = type === 'success' ? '#0D9488' : type === 'info' ? '#14B8A6' : '#E83E8C'; var t = document.createElement('div'); t.style.cssText = 'position:fixed;bottom:24px;right:24px;background:' + bg + ';color:#fff;padding:10px 18px;border-radius:12px;font-size:13px;font-weight:600;box-shadow:0 4px 16px rgba(0,0,0,.15);z-index:9999;opacity:0;transition:opacity .3s;'; t.textContent = msg; document.body.appendChild(t); requestAnimationFrame(function () { t.style.opacity = 1; }); setTimeout(function () { t.style.opacity = 0; setTimeout(function () { t.remove(); }, 300); }, 2800); }

setInterval(renderNowLine, 60000);
loadAppointments();
