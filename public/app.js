const main = document.querySelector('#main');
const navigation = document.querySelector('#navigation');
const breadcrumb = document.querySelector('#breadcrumb');
const sidebar = document.querySelector('#sidebar');
const scrim = document.querySelector('#mobile-scrim');
const contentColumn = document.querySelector('.content-column');
const menuToggle = document.querySelector('#menu-toggle');
const mobileViewport = window.matchMedia('(max-width: 800px)');
const recordDialog = document.querySelector('#record-dialog');
const confirmDialog = document.querySelector('#confirm-dialog');
const form = document.querySelector('#record-form');
const fieldsElement = document.querySelector('#form-fields');
const toastElement = document.querySelector('#toast');

let catalog = {};
let resource = 'overview';
let search = '';
let page = 1;
let currentRecord = null;
let pendingDelete = null;
let loadSequence = 0;
let formSequence = 0;
let formReady = false;
let formSaving = false;
let deleteSequence = 0;
let focusSearchOnRender = false;
let searchTimer;
let toastTimer;

function icon(name) {
  const svg = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
  svg.classList.add('icon');
  svg.setAttribute('aria-hidden', 'true');
  const use = document.createElementNS('http://www.w3.org/2000/svg', 'use');
  use.setAttribute('href', `#i-${name}`);
  svg.append(use);
  return svg;
}

function element(tag, className = '', content) {
  const node = document.createElement(tag);
  if (className) node.className = className;
  if (content !== undefined && content !== null) node.textContent = String(content);
  return node;
}

function button(label, className, handler, iconName) {
  const node = element('button', className);
  node.type = 'button';
  if (iconName) node.append(icon(iconName));
  if (label) node.append(document.createTextNode(label));
  if (handler) node.addEventListener('click', handler);
  return node;
}

async function api(path, options = {}) {
  let response;
  try {
    response = await fetch(`/api/${path}`, {
      ...options,
      headers: options.body ? { 'Content-Type': 'application/json' } : {},
    });
  } catch {
    throw new Error('Cannot connect to the local server. Check that it is running.');
  }
  let body;
  try { body = await response.json(); }
  catch { throw new Error('The server returned an unreadable response.'); }
  if (!response.ok) throw new Error(body.error || `Request failed (${response.status})`);
  return body;
}

function notify(message, isError = false) {
  clearTimeout(toastTimer);
  toastElement.textContent = message;
  toastElement.classList.toggle('error', isError);
  toastElement.hidden = false;
  toastTimer = setTimeout(() => { toastElement.hidden = true; }, 4400);
}

async function downloadCsv(selectedResource, query, trigger) {
  trigger.disabled = true;
  const originalLabel = trigger.textContent;
  trigger.textContent = 'Preparing CSV…';
  try {
    const response = await fetch(`/api/export/${selectedResource}?${new URLSearchParams({ search: query })}`);
    if (!response.ok) {
      let message = `Export failed (${response.status})`;
      try { message = (await response.json()).error || message; } catch { /* Keep HTTP error. */ }
      throw new Error(message);
    }
    const url = URL.createObjectURL(await response.blob());
    const link = document.createElement('a');
    link.href = url;
    link.download = `wardline-${selectedResource}.csv`;
    link.click();
    setTimeout(() => URL.revokeObjectURL(url), 1000);
    notify('CSV export downloaded.');
  } catch (error) {
    notify(error instanceof TypeError ? 'Cannot connect to the local server.' : error.message, true);
  } finally {
    trigger.disabled = false;
    trigger.textContent = originalLabel;
  }
}

function readableDate(value) {
  if (!value) return '—';
  const date = new Date(`${value}T12:00:00Z`);
  return Number.isNaN(date.getTime()) ? String(value) : date.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric', timeZone: 'UTC' });
}

function shortDate(value) {
  const date = new Date(`${value}T12:00:00Z`);
  return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric', timeZone: 'UTC' });
}

function pageHeader(eyebrow, title, description, right = []) {
  const header = element('div', 'page-header');
  const copy = element('div');
  copy.append(element('span', 'eyebrow', eyebrow), element('h1', '', title), element('p', '', description));
  const actions = element('div', 'page-actions');
  actions.append(...right);
  header.append(copy, actions);
  return header;
}

function panelHeading(title, subtitle, actionText, action) {
  const header = element('div', 'panel-header');
  const copy = element('div');
  copy.append(element('h2', '', title), element('p', 'panel-subtitle', subtitle));
  header.append(copy);
  if (actionText) header.append(button(actionText, 'text-link', action, 'arrow'));
  return header;
}

function setNavigation() {
  const sections = [
    { label: 'Workspace', items: ['overview'] },
    { label: 'People', items: ['patients', 'physicians', 'nurses'] },
    { label: 'Clinical records', items: ['diagnoses', 'treatments', 'illnesses', 'medicines'] },
    { label: 'Hospital operations', items: ['admissions', 'discharges', 'wards', 'nurse_assignments', 'physician_schedules', 'nurse_shifts'] },
  ];
  navigation.replaceChildren();
  for (const section of sections) {
    const container = element('div', 'nav-section');
    container.append(element('div', 'nav-label', section.label));
    for (const key of section.items) {
      const item = key === 'overview' ? { label: 'Overview', icon: 'grid' } : catalog[key];
      if (!item) continue;
      const link = button(item.label, `nav-link${resource === key ? ' active' : ''}`, () => navigate(key), item.icon);
      link.setAttribute('aria-current', resource === key ? 'page' : 'false');
      container.append(link);
    }
    navigation.append(container);
  }
}

function syncMobileMenu() {
  const mobile = mobileViewport.matches;
  if (!mobile) sidebar.classList.remove('open');
  const open = mobile && sidebar.classList.contains('open');
  sidebar.inert = mobile && !open;
  contentColumn.inert = open;
  scrim.hidden = !open;
  menuToggle.setAttribute('aria-expanded', String(open));
  if (open) {
    sidebar.setAttribute('role', 'dialog');
    sidebar.setAttribute('aria-modal', 'true');
  } else {
    sidebar.removeAttribute('role');
    sidebar.removeAttribute('aria-modal');
  }
}

function closeMobileMenu({ restoreFocus = false } = {}) {
  const wasOpen = sidebar.classList.contains('open');
  sidebar.classList.remove('open');
  syncMobileMenu();
  if (wasOpen && restoreFocus) menuToggle.focus();
}

function navigate(destination) {
  const fromMobileMenu = mobileViewport.matches && sidebar.classList.contains('open');
  closeMobileMenu();
  if (fromMobileMenu) main.focus();
  const hash = destination === 'overview' ? '#/' : `#/${destination}`;
  if (location.hash === hash) { route(); return; }
  location.hash = hash;
}

function route() {
  clearTimeout(searchTimer);
  const desired = location.hash.replace(/^#\/?/, '').split('/')[0];
  const nextResource = Object.hasOwn(catalog, desired) ? desired : 'overview';
  if (nextResource !== resource) {
    if (recordDialog.open) recordDialog.close();
    if (confirmDialog.open) confirmDialog.close();
    pendingDelete = null;
    deleteSequence += 1;
    formSequence += 1;
    formReady = false;
    formSaving = false;
    if (nextResource !== 'patients') focusSearchOnRender = false;
  }
  if (nextResource !== resource) { search = ''; page = 1; }
  resource = nextResource;
  breadcrumb.textContent = resource === 'overview' ? 'Overview' : catalog[resource].label;
  document.title = `${resource === 'overview' ? 'Overview' : catalog[resource].label} — Wardline`;
  setNavigation();
  void render();
}

function renderError(message, retry = render) {
  const error = element('div', 'error-panel');
  error.setAttribute('role', 'alert');
  error.append(document.createTextNode(message), button('Retry', 'button secondary', () => void retry()));
  main.replaceChildren(error);
}

async function render() {
  const sequence = ++loadSequence;
  if (resource === 'overview') {
    main.replaceChildren(element('div', 'loading', 'Loading dashboard…'));
    try {
      const data = await api('dashboard');
      if (sequence === loadSequence) renderDashboard(data);
    } catch (error) { if (sequence === loadSequence) renderError(error.message); }
  } else {
    main.replaceChildren(element('div', 'loading', 'Loading records…'));
    try {
      const data = await api(`records/${resource}?${new URLSearchParams({ search, page, pageSize: 10 })}`);
      if (sequence === loadSequence) renderRecords(data);
    } catch (error) { if (sequence === loadSequence) renderError(error.message); }
  }
}

function renderDashboard(data) {
  main.replaceChildren();
  const today = element('span', 'date-chip');
  today.append(icon('calendar'), document.createTextNode(new Date().toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' })));
  main.append(pageHeader('YOUR WORKSPACE / OVERVIEW', 'A clearer view of care.',
    'Monitor patient flow, care activity, bed availability, and pharmacy stock from one place.', [today]));

  const banner = element('div', 'demo-banner');
  banner.append(icon('check'));
  const bannerCopy = element('span');
  bannerCopy.append(element('strong', '', 'Demonstration workspace · '), document.createTextNode('All people, activity, and metrics shown here are synthetic.'));
  banner.append(bannerCopy);
  main.append(banner);

  const metricCards = [
    { label: 'Currently admitted', value: data.metrics.activeAdmissions, detail: 'Patients in hospital', symbol: 'admission', accent: true },
    { label: 'Registered patients', value: data.metrics.patients, detail: 'In patient directory', symbol: 'patients' },
    { label: 'On care teams', value: data.metrics.physicians + data.metrics.nurses, detail: `${data.metrics.physicians} physicians · ${data.metrics.nurses} nurses`, symbol: 'staff' },
    { label: 'Bed occupancy', value: `${data.metrics.occupancyPercent}%`, detail: `${data.metrics.activeAdmissions} of ${data.metrics.capacity} beds occupied`, symbol: 'ward' },
  ];
  const metrics = element('div', 'metric-grid');
  for (const item of metricCards) {
    const card = element('article', `metric-card${item.accent ? ' primary-metric' : ''}`);
    const headIcon = element('span', 'metric-icon'); headIcon.append(icon(item.symbol));
    card.append(element('div', 'metric-label', item.label), headIcon);
    const bottom = element('div');
    bottom.append(element('div', 'metric-value', item.value), element('div', 'metric-detail', item.detail));
    card.append(bottom);
    metrics.append(card);
  }
  main.append(metrics);

  const layout = element('div', 'dashboard-grid');
  const trendPanel = element('section', 'panel');
  trendPanel.append(panelHeading('Admission activity', 'Month-by-month admissions, past 6 months', 'All admissions', () => navigate('admissions')));
  const chartShell = element('div', 'chart-shell');
  const legend = element('div', 'chart-legend'); legend.append(element('span', 'legend-dot'), document.createTextNode('Admissions recorded'));
  const chartPeak = element('div', 'chart-peak');
  const sum = data.months.reduce((total, month) => total + month.count, 0);
  chartPeak.append(element('strong', '', sum), element('span', '', 'in the last six calendar months'));
  const chartBox = element('div', 'chart-viewport'); chartBox.append(buildChart(data.months));
  const axis = element('div', 'chart-axis'); data.months.forEach((month) => axis.append(element('span', '', month.month)));
  chartShell.append(legend, chartPeak, chartBox, axis); trendPanel.append(chartShell);

  const right = element('div', 'right-stack');
  const wards = element('section', 'panel');
  wards.append(panelHeading('Ward utilization', 'Available beds across departments', 'View wards', () => navigate('wards')));
  const wardRows = element('div', 'ward-list');
  for (const ward of data.wards.slice(0, 5)) {
    const row = element('div', 'ward-row'); const heading = element('div', 'ward-row-top');
    heading.append(element('span', '', ward.name), element('span', '', `${ward.occupied} / ${ward.capacity}`));
    const track = element('div', 'progress-track'); const fill = element('div', 'progress-fill');
    const percent = ward.capacity ? Math.round((ward.occupied / ward.capacity) * 100) : 0;
    fill.style.width = `${Math.min(100, percent)}%`;
    if (percent >= 80) fill.classList.add('warm');
    track.setAttribute('role', 'progressbar');
    track.setAttribute('aria-label', `${ward.name} occupancy`);
    track.setAttribute('aria-valuemin', '0');
    track.setAttribute('aria-valuemax', '100');
    track.setAttribute('aria-valuenow', String(Math.min(100, percent)));
    track.setAttribute('aria-valuetext', `${ward.occupied} of ${ward.capacity} beds occupied`);
    track.append(fill); row.append(heading, track); wardRows.append(row);
  }
  wards.append(wardRows);

  const stock = element('section', 'panel');
  stock.append(panelHeading('Pharmacy watchlist', 'Supplies at or below reorder level', 'Inventory', () => navigate('medicines')));
  const stockRows = element('div', 'stock-list');
  for (const item of data.lowStock.slice(0, 4)) {
    const row = element('div', 'stock-row'); const symbol = element('span', 'stock-icon'); symbol.append(icon('alert'));
    const copy = element('span', 'stock-row-copy');
    copy.append(element('strong', '', item.name), element('small', '', `Reorder at ${item.reorder_level} ${item.unit}`));
    row.append(symbol, copy, element('span', 'stock-number', `${item.stock_qty} left`));
    stockRows.append(row);
  }
  if (!data.lowStock.length) stockRows.append(element('div', 'compact-empty', 'All medications are above their reorder thresholds.'));
  stock.append(stockRows);
  right.append(wards, stock); layout.append(trendPanel, right); main.append(layout);

  const recent = element('section', 'panel section-spacer');
  recent.append(panelHeading('Recent admissions', 'Latest patient arrivals and placement', 'Browse all', () => navigate('admissions')));
  const wrapper = element('div', 'table-wrapper');
  const table = element('table', 'data-table');
  table.append(element('caption', 'sr-only', 'Recent hospital admissions'));
  const thead = element('thead'); const heading = element('tr');
  ['PATIENT', 'ADMISSION ID', 'WARD', 'DATE', 'STATUS'].forEach((label) => {
    const cell = element('th', '', label);
    cell.scope = 'col';
    heading.append(cell);
  });
  thead.append(heading); const tbody = element('tbody');
  for (const admission of data.recent) {
    const row = element('tr'); const patient = element('td', 'table-primary');
    const initials = admission.patient.split(' ').map((part) => part[0]).slice(0, 2).join('');
    patient.append(element('span', 'patient-initial', initials), document.createTextNode(admission.patient));
    const badge = element('span', `status-badge${admission.status === 'Active' ? '' : ' neutral'}`, admission.status);
    const status = element('td'); status.append(badge);
    row.append(patient, element('td', 'muted-cell', `#ADM-${String(admission.id).padStart(4, '0')}`),
      element('td', '', admission.ward), element('td', 'muted-cell', shortDate(admission.admitted_on)), status);
    tbody.append(row);
  }
  table.append(thead, tbody); wrapper.append(table); recent.append(wrapper);
  const footer = element('div', 'section-summary');
  const positive = element('span', 'summary-positive'); positive.append(icon('check'), document.createTextNode('Live from local database'));
  footer.append(element('span', '', `Showing ${data.recent.length} most recent admissions`), positive); recent.append(footer);
  main.append(recent);
}

function buildChart(months) {
  const svg = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
  svg.classList.add('chart-svg'); svg.setAttribute('viewBox', '0 0 640 220');
  svg.setAttribute('preserveAspectRatio', 'none');
  svg.setAttribute('role', 'img');
  svg.setAttribute('aria-label', `Admissions per month: ${months.map((m) => `${m.month} ${m.count}`).join(', ')}`);
  function shape(tag, className, attributes) {
    const node = document.createElementNS('http://www.w3.org/2000/svg', tag);
    node.setAttribute('class', className);
    for (const [key, value] of Object.entries(attributes)) node.setAttribute(key, String(value));
    svg.append(node); return node;
  }
  for (let i = 0; i < 4; i += 1) shape('line', 'gridline', { x1: 0, x2: 640, y1: 35 + i * 56, y2: 35 + i * 56 });
  const max = Math.max(6, ...months.map((m) => m.count));
  const coords = months.map((m, i) => ({ x: i * (640 / Math.max(1, months.length - 1)), y: 195 - (m.count / max) * 157 }));
  const points = coords.map((p) => `${p.x},${p.y}`).join(' ');
  shape('polygon', 'chart-area', { points: `0,205 ${points} 640,205` });
  shape('polyline', 'chart-line', { points });
  coords.forEach(({ x, y }) => shape('circle', 'chart-dot', { cx: x, cy: y, r: 4.5 }));
  return svg;
}

function displayField(row, field) {
  let value = field.type === 'relation' ? row[`${field.key}_label`] : row[field.key];
  if (field.type === 'date' && value) value = readableDate(value);
  if (field.type === 'number' && value !== null) value = Number(value).toLocaleString('en-US');
  if (field.key === 'sex') value = value === 'F' ? 'Female' : value === 'M' ? 'Male' : value;
  return value === null || value === undefined || value === '' ? '—' : String(value);
}

function renderRecords(data) {
  const definition = catalog[resource];
  main.replaceChildren();
  const newButton = button(`Add ${definition.singular.toLowerCase()}`, 'button primary', () => void openForm(), 'plus');
  main.append(pageHeader(`RECORDS / ${definition.group.toUpperCase()}`, definition.label, definition.description, [newButton]));
  const info = element('div', 'notification-note');
  info.textContent = 'This is a self-contained demo with simulated records. Changes are saved to your local database and may affect related records.';
  main.append(info);
  const panel = element('section', 'panel');
  const toolbar = element('div', 'records-toolbar');
  const searchBox = element('label', 'search-field'); searchBox.setAttribute('aria-label', `Search ${definition.label.toLowerCase()}`);
  searchBox.append(icon('search'));
  const input = element('input'); input.type = 'search'; input.maxLength = 150;
  input.placeholder = `Search ${definition.label.toLowerCase()}…`;
  input.setAttribute('aria-label', `Search ${definition.label.toLowerCase()}`);
  input.value = search;
  input.addEventListener('input', () => {
    const nextSearch = input.value;
    clearTimeout(searchTimer);
    searchTimer = setTimeout(() => {
      search = nextSearch; page = 1;
      void refreshTable();
    }, 260);
  });
  searchBox.append(input);
  const actions = element('div', 'records-toolbar-actions');
  actions.append(element('span', 'record-count', `${data.total.toLocaleString('en-US')} records`));
  const exportButton = button('Export CSV', 'button secondary', () => {
    clearTimeout(searchTimer);
    search = input.value;
    page = 1;
    void downloadCsv(resource, search, exportButton);
  });
  actions.append(exportButton);
  toolbar.append(searchBox, actions);
  panel.append(toolbar);
  panel.append(buildRecordTable(data, definition));
  main.append(panel);
  if (focusSearchOnRender) {
    focusSearchOnRender = false;
    input.focus();
  }
}

function buildRecordTable(data, definition) {
  const container = element('div', 'records-table-container');
  if (data.rows.length === 0) {
    const empty = element('div', 'empty-state'); const pictogram = element('span', 'empty-icon'); pictogram.append(icon(definition.icon));
    empty.append(pictogram, element('h3', '', search ? 'No matches found' : `No ${definition.label.toLowerCase()} yet`),
      element('p', '', search ? 'Try a different name or keyword.' : `Add your first ${definition.singular.toLowerCase()} to get started.`));
    if (search) empty.append(button('Clear search', 'button secondary', () => {
      search = ''; page = 1; focusSearchOnRender = true; void render();
    }));
    else empty.append(button(`Add ${definition.singular.toLowerCase()}`, 'button primary', () => void openForm(), 'plus'));
    container.append(empty);
  } else {
    const wrapper = element('div', 'table-wrapper'); const table = element('table', 'data-table record-table');
    table.append(element('caption', 'sr-only', `${definition.label} records`));
    const header = element('thead'); const headRow = element('tr');
    const idHeading = element('th', '', 'ID');
    idHeading.scope = 'col';
    headRow.append(idHeading);
    for (const key of definition.columns) {
      const columnHeading = element('th', '', definition.fields.find((field) => field.key === key)?.label || key);
      columnHeading.scope = 'col';
      headRow.append(columnHeading);
    }
    const actionHeading = element('th', '', 'ACTIONS');
    actionHeading.scope = 'col';
    headRow.append(actionHeading); header.append(headRow);
    const body = element('tbody');
    for (const row of data.rows) {
      const tr = element('tr'); tr.append(element('td', 'muted-cell', `#${String(row.id).padStart(4, '0')}`));
      definition.columns.forEach((key, index) => {
        const field = definition.fields.find((item) => item.key === key);
        const cell = element('td', index === 0 ? 'table-primary' : '');
        cell.textContent = displayField(row, field);
        cell.title = cell.textContent;
        tr.append(cell);
      });
      const actions = element('td'); const controls = element('div', 'table-actions');
      const edit = button('', '', () => void openForm(row.id), 'edit');
      edit.setAttribute('aria-label', `Edit ${definition.singular.toLowerCase()} #${row.id}`);
      const remove = button('', 'delete', () => openDelete(row), 'trash');
      remove.setAttribute('aria-label', `Delete ${definition.singular.toLowerCase()} #${row.id}`);
      controls.append(edit, remove); actions.append(controls); tr.append(actions); body.append(tr);
    }
    table.append(header, body); wrapper.append(table); container.append(wrapper);
  }
  const footer = element('div', 'panel-footer');
  const first = data.total === 0 ? 0 : (data.page - 1) * data.pageSize + 1;
  const range = element('p', '', `Showing ${first}–${Math.min(data.page * data.pageSize, data.total)} of ${data.total} records`);
  range.setAttribute('role', 'status');
  footer.append(range);
  const pager = element('div', 'pagination');
  pager.setAttribute('role', 'navigation');
  pager.setAttribute('aria-label', `${definition.label} pages`);
  const prev = button('', '', () => { page -= 1; void refreshTable({ focusPager: 'previous' }); }, 'chevron');
  prev.firstChild.style.transform = 'rotate(180deg)'; prev.disabled = page === 1; prev.setAttribute('aria-label', 'Previous page');
  const next = button('', '', () => { page += 1; void refreshTable({ focusPager: 'next' }); }, 'chevron');
  next.disabled = data.page >= data.pages; next.setAttribute('aria-label', 'Next page');
  pager.append(prev, element('span', 'page-number', `${data.page} / ${data.pages}`), next);
  footer.append(pager); container.append(footer);
  return container;
}

async function refreshTable({ focusPager } = {}) {
  const sequence = ++loadSequence;
  const target = main.querySelector('.records-table-container');
  target?.setAttribute('aria-busy', 'true');
  target?.querySelectorAll('.pagination button').forEach((item) => { item.disabled = true; });
  try {
    const result = await api(`records/${resource}?${new URLSearchParams({ search, page, pageSize: 10 })}`);
    if (sequence !== loadSequence) return;
    if (page > result.pages) { page = result.pages; return refreshTable({ focusPager }); }
    const replacement = buildRecordTable(result, catalog[resource]);
    target?.replaceWith(replacement);
    const count = main.querySelector('.record-count');
    if (count) count.textContent = `${result.total.toLocaleString('en-US')} records`;
    if (focusPager) {
      const buttons = replacement.querySelectorAll('.pagination button');
      const preferred = buttons[focusPager === 'next' ? 1 : 0];
      (preferred?.disabled ? buttons[focusPager === 'next' ? 0 : 1] : preferred)?.focus();
    }
  } catch (error) {
    if (sequence !== loadSequence) return;
    const replacement = element('div', 'records-table-container');
    const message = element('div', 'error-panel');
    message.setAttribute('role', 'alert');
    const retry = button('Retry loading records', 'button secondary', () => void refreshTable());
    message.append(document.createTextNode(error.message), retry);
    replacement.append(message);
    target?.replaceWith(replacement);
    const count = main.querySelector('.record-count');
    if (count) count.textContent = 'Records unavailable';
    if (focusPager) retry.focus();
  } finally {
    if (sequence === loadSequence) target?.removeAttribute('aria-busy');
  }
}

async function openForm(id = null) {
  const targetResource = resource;
  const definition = catalog[targetResource];
  const sequence = ++formSequence;
  formReady = false;
  formSaving = false;
  currentRecord = id;
  document.querySelector('#form-submit').disabled = true;
  const title = document.querySelector('#dialog-title');
  title.textContent = `${id === null ? 'Add' : 'Edit'} ${definition.singular.toLowerCase()}`;
  document.querySelector('#form-submit').textContent = id === null ? 'Create record' : 'Save changes';
  document.querySelector('#form-error').hidden = true;
  fieldsElement.replaceChildren(element('p', 'loading', 'Preparing form…'));
  recordDialog.showModal();
  try {
    const record = id === null ? {} : await api(`records/${targetResource}/${id}`);
    const relationships = new Map();
    await Promise.all(definition.fields.filter((field) => field.type === 'relation').map(async (field) => {
      relationships.set(field.key, await api(`lookup/${field.resource}`));
    }));
    if (!recordDialog.open || resource !== targetResource || currentRecord !== id || sequence !== formSequence) return;
    fieldsElement.replaceChildren();
    for (const field of definition.fields) {
      const wrap = element('div', `form-field${field.type === 'textarea' ? ' full' : ''}`);
      const label = element('label', '', field.label);
      label.htmlFor = `field-${field.key}`;
      if (field.required) label.append(element('span', '', ' *'));
      let control;
      if (field.type === 'relation' || field.type === 'select') {
        control = element('select');
        if (!field.required) { const empty = element('option', '', 'None'); empty.value = ''; control.append(empty); }
        else { const placeholder = element('option', '', `Select ${field.label.toLowerCase()}`); placeholder.value = ''; placeholder.disabled = true; control.append(placeholder); }
        const options = field.type === 'relation'
          ? relationships.get(field.key).map((item) => ({ value: item.id, label: item.label }))
          : field.options.map((option) => ({ value: option, label: option === 'F' ? 'Female' : option === 'M' ? 'Male' : option }));
        // Relation lookups are capped. Keep the current value available when
        // editing an older record so saving cannot silently erase its link.
        if (field.type === 'relation' && record[field.key] != null &&
            !options.some((option) => option.value === record[field.key])) {
          options.unshift({ value: record[field.key], label: record[`${field.key}_label`] || `Record #${record[field.key]}` });
        }
        for (const option of options) { const node = element('option', '', option.label); node.value = option.value; control.append(node); }
      } else if (field.type === 'textarea') control = element('textarea');
      else {
        control = element('input');
        control.type = field.type === 'number' ? 'number' : field.type === 'date' ? 'date' : field.type === 'time' ? 'time' : 'text';
        if (field.type === 'number') { control.min = field.min; control.max = field.max; control.step = 1; }
        if (field.max) control.maxLength = field.max;
      }
      control.id = `field-${field.key}`; control.name = field.key;
      control.required = field.required;
      control.value = record[field.key] ?? '';
      if (field.type === 'relation') {
        const searchControl = element('input', 'relation-search');
        searchControl.type = 'search';
        searchControl.maxLength = 150;
        searchControl.autocomplete = 'off';
        searchControl.placeholder = 'Search by name or #ID';
        searchControl.setAttribute('aria-label', `Search ${field.label.toLowerCase()} by name or #ID`);
        const hint = element('small', 'relation-hint');
        hint.id = `relation-hint-${field.key}`;
        hint.setAttribute('aria-live', 'polite');
        searchControl.setAttribute('aria-describedby', hint.id);
        const initialCount = relationships.get(field.key).length;
        hint.textContent = initialCount === 100
          ? 'Showing the first 100. Search to find other records.'
          : `${initialCount} available ${initialCount === 1 ? 'record' : 'records'}.`;
        let timer;
        let requestNumber = 0;
        searchControl.addEventListener('input', () => {
          const request = ++requestNumber;
          const query = searchControl.value.trim();
          clearTimeout(timer);
          hint.textContent = 'Searching…';
          timer = setTimeout(async () => {
            const isCurrent = () => request === requestNumber && sequence === formSequence &&
              recordDialog.open && resource === targetResource && currentRecord === id && !formSaving;
            if (!isCurrent()) return;
            try {
              const matches = await api(`lookup/${field.resource}?${new URLSearchParams({ search: query })}`);
              if (!isCurrent() || searchControl.value.trim() !== query) return;
              const selected = control.value;
              const selectedLabel = [...control.options].find((option) => option.value === selected)?.textContent;
              control.replaceChildren();
              const placeholder = element('option', '', field.required ? `Select ${field.label.toLowerCase()}` : 'None');
              placeholder.value = '';
              if (field.required) placeholder.disabled = true;
              control.append(placeholder);
              // Keep the user's current choice available even when it falls
              // outside the new search results or the 100-record result limit.
              if (selected && !matches.some((item) => String(item.id) === selected)) {
                const retained = element('option', '', selectedLabel || `Record #${selected}`);
                retained.value = selected;
                control.append(retained);
              }
              for (const match of matches) {
                const option = element('option', '', match.label);
                option.value = match.id;
                control.append(option);
              }
              control.value = selected;
              hint.textContent = matches.length === 0 ? 'No matches. Try another name or #ID.' :
                matches.length === 100 ? 'Showing 100 results. Refine your search for more.' :
                  `${matches.length} matching ${matches.length === 1 ? 'record' : 'records'}.`;
            } catch (error) {
              if (isCurrent()) hint.textContent = `${error.message} Try searching again.`;
            }
          }, 220);
        });
        wrap.append(label, searchControl, control, hint);
      } else wrap.append(label, control);
      fieldsElement.append(wrap);
    }
    formReady = true;
    document.querySelector('#form-submit').disabled = false;
    fieldsElement.querySelector('input,select,textarea')?.focus();
  } catch (error) {
    if (sequence !== formSequence || !recordDialog.open) return;
    document.querySelector('#form-error').textContent = error.message;
    document.querySelector('#form-error').hidden = false;
    fieldsElement.replaceChildren();
    document.querySelector('#form-submit').disabled = true;
  }
}

form.addEventListener('submit', async (event) => {
  event.preventDefault();
  const submit = document.querySelector('#form-submit');
  if (!recordDialog.open || !formReady || submit.disabled) return;
  const errorElement = document.querySelector('#form-error');
  const targetResource = resource;
  const targetRecord = currentRecord;
  const sequence = formSequence;
  const data = {};
  for (const field of catalog[targetResource].fields) {
    const control = form.elements.namedItem(field.key);
    if (!control) return;
    const value = control.value;
    data[field.key] = value === '' && !field.required ? null :
      (field.type === 'number' || field.type === 'relation') && value !== '' ? Number(value) : value;
  }
  formSaving = true;
  submit.disabled = true; errorElement.hidden = true;
  try {
    await api(`records/${targetResource}${targetRecord === null ? '' : `/${targetRecord}`}`, {
      method: targetRecord === null ? 'POST' : 'PATCH', body: JSON.stringify(data),
    });
    if (sequence !== formSequence || !recordDialog.open) return;
    recordDialog.close();
    notify(targetRecord === null ? 'Record created successfully.' : 'Changes saved successfully.');
    await render();
  } catch (error) {
    if (sequence === formSequence && recordDialog.open) { errorElement.textContent = error.message; errorElement.hidden = false; }
  } finally {
    if (sequence === formSequence) {
      formSaving = false;
      if (recordDialog.open) submit.disabled = false;
    }
  }
});

function openDelete(row) {
  deleteSequence += 1;
  pendingDelete = { resource, id: row.id };
  document.querySelector('#confirm-description').textContent =
    `Delete ${catalog[resource].singular.toLowerCase()} #${row.id}? Related records may prevent deletion. This action cannot be undone.`;
  confirmDialog.showModal();
}

document.querySelector('#confirm-submit').addEventListener('click', async () => {
  const submit = document.querySelector('#confirm-submit');
  if (!pendingDelete || !confirmDialog.open || submit.disabled) return;
  const intent = pendingDelete;
  const sequence = deleteSequence;
  submit.disabled = true;
  try {
    await api(`records/${intent.resource}/${intent.id}`, { method: 'DELETE' });
    if (sequence !== deleteSequence || !confirmDialog.open) return;
    confirmDialog.close(); notify('Record deleted.'); await render();
  } catch (error) {
    if (sequence !== deleteSequence || !confirmDialog.open) return;
    confirmDialog.close(); notify(error.message, true);
  } finally {
    if (sequence === deleteSequence) { submit.disabled = false; pendingDelete = null; }
  }
});

document.querySelector('#dialog-close').addEventListener('click', () => { if (!formSaving) recordDialog.close(); });
document.querySelector('#dialog-cancel').addEventListener('click', () => { if (!formSaving) recordDialog.close(); });
document.querySelector('#confirm-cancel').addEventListener('click', () => {
  if (!document.querySelector('#confirm-submit').disabled) confirmDialog.close();
});
recordDialog.addEventListener('cancel', (event) => { if (formSaving) event.preventDefault(); });
confirmDialog.addEventListener('cancel', (event) => {
  if (document.querySelector('#confirm-submit').disabled) event.preventDefault();
});
recordDialog.addEventListener('close', () => {
  if (recordDialog.open) return;
  formSequence += 1;
  formReady = false;
  formSaving = false;
  currentRecord = null;
  document.querySelector('#form-submit').disabled = true;
});
confirmDialog.addEventListener('close', () => {
  if (confirmDialog.open) return;
  deleteSequence += 1;
  pendingDelete = null;
  document.querySelector('#confirm-submit').disabled = false;
});
menuToggle.addEventListener('click', () => {
  sidebar.classList.add('open');
  syncMobileMenu();
  document.querySelector('#menu-close').focus();
});
document.querySelector('#menu-close').addEventListener('click', () => closeMobileMenu({ restoreFocus: true }));
scrim.addEventListener('click', () => closeMobileMenu({ restoreFocus: true }));
mobileViewport.addEventListener('change', syncMobileMenu);
syncMobileMenu();
function focusPatientSearch() {
  const existing = resource === 'patients' ? main.querySelector('.search-field input') : null;
  if (existing) { existing.focus(); return; }
  focusSearchOnRender = true;
  navigate('patients');
}
document.querySelector('#quick-search').addEventListener('click', focusPatientSearch);
document.addEventListener('keydown', (event) => {
  if (event.key === 'Escape' && sidebar.classList.contains('open')) {
    closeMobileMenu({ restoreFocus: true });
    return;
  }
  if (event.key === 'Tab' && sidebar.classList.contains('open')) {
    const focusable = [...sidebar.querySelectorAll('button:not(:disabled)')];
    const first = focusable[0];
    const last = focusable.at(-1);
    if (event.shiftKey && (document.activeElement === first || !sidebar.contains(document.activeElement))) {
      event.preventDefault();
      last.focus();
    } else if (!event.shiftKey && (document.activeElement === last || !sidebar.contains(document.activeElement))) {
      event.preventDefault();
      first.focus();
    }
  }
  if (event.key === '/' && !event.altKey && !event.ctrlKey && !event.metaKey &&
      !['INPUT', 'TEXTAREA', 'SELECT'].includes(document.activeElement?.tagName) && !recordDialog.open && !confirmDialog.open) {
    event.preventDefault();
    focusPatientSearch();
  }
});
window.addEventListener('hashchange', route);

async function start() {
  try { catalog = await api('catalog'); route(); }
  catch (error) { renderError(error.message, start); }
}

void start();
