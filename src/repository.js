import { catalog } from './catalog.js';
import { transaction } from './database.js';

export class AppError extends Error {
  constructor(status, message) {
    super(message);
    this.status = status;
  }
}

const bad = (message) => { throw new AppError(400, message); };

function definition(resource) {
  const entity = catalog[resource];
  if (!entity) throw new AppError(404, 'Unknown resource');
  return entity;
}

function naturalName(table, alias) {
  switch (table) {
    case 'patients': case 'physicians': case 'nurses':
      return `${alias}.first_name || ' ' || ${alias}.last_name`;
    case 'admissions':
      return `'Admission #' || ${alias}.id`;
    case 'diagnoses':
      return `'Diagnosis #' || ${alias}.id`;
    default:
      return `${alias}.name`;
  }
}

function listingParts(resource) {
  const entity = definition(resource);
  const joins = [];
  const expressions = [`t.*`];
  const searchable = [];
  for (const [index, field] of entity.fields.entries()) {
    if (field.type === 'relation') {
      const alias = `r${index}`;
      joins.push(`LEFT JOIN ${field.resource} ${alias} ON t.${field.key} = ${alias}.id`);
      const label = naturalName(field.resource, alias);
      expressions.push(`${label} AS ${field.key}_label`);
      searchable.push(label);
    } else if (field.type === 'text' || field.type === 'textarea' || field.type === 'select') {
      searchable.push(`t.${field.key}`);
    }
  }
  return { joins: joins.join(' '), expressions: expressions.join(', '), searchable };
}

export function listRecords(db, resource, { search = '', page = 1, pageSize = 10 } = {}) {
  definition(resource);
  if (!Number.isSafeInteger(page) || page < 1) bad('Page must be a positive integer');
  if (!Number.isSafeInteger(pageSize) || pageSize < 1 || pageSize > 100) bad('Page size must be between 1 and 100');
  if (typeof search !== 'string' || search.length > 150) bad('Search is too long');
  const { joins, expressions, searchable } = listingParts(resource);
  const query = search.trim();
  const where = query ? `WHERE (${searchable.map((column) => `instr(lower(coalesce(${column}, '')), lower(?)) > 0`).join(' OR ')})` : '';
  const params = searchable.map(() => query);
  const total = db.prepare(`SELECT COUNT(*) AS total FROM ${resource} t ${joins} ${where}`).get(...(query ? params : [])).total;
  const rows = db.prepare(`SELECT ${expressions} FROM ${resource} t ${joins} ${where} ORDER BY t.id DESC LIMIT ? OFFSET ?`)
    .all(...(query ? params : []), pageSize, (page - 1) * pageSize);
  return { rows, total, page, pageSize, pages: Math.max(1, Math.ceil(total / pageSize)) };
}

export function getRecord(db, resource, id) {
  definition(resource);
  if (!Number.isSafeInteger(id) || id <= 0) bad('Invalid record ID');
  const { joins, expressions } = listingParts(resource);
  const row = db.prepare(`SELECT ${expressions} FROM ${resource} t ${joins} WHERE t.id = ?`).get(id);
  if (!row) throw new AppError(404, 'Record not found');
  return row;
}

// Return a bounded set of choices for relationship pickers. A name search (or
// exact #ID) lets callers reach older records without loading entire tables.
export function lookup(db, resource, { search = '' } = {}) {
  definition(resource);
  if (typeof search !== 'string' || search.length > 150) bad('Search is too long');
  const query = search.trim();
  const idSearch = /^#([1-9]\d*)$/.exec(query);
  const requestedId = idSearch ? Number(idSearch[1]) : null;
  if (requestedId !== null && !Number.isSafeInteger(requestedId)) bad('Invalid record ID');
  const filter = (label, alias) => idSearch
    ? `WHERE ${alias}.id = ?`
    : query ? `WHERE instr(lower(${label}), lower(?)) > 0` : '';
  const params = query ? [idSearch ? requestedId : query] : [];
  if (resource === 'admissions') {
    const label = "'Admission #' || a.id || ' · ' || p.first_name || ' ' || p.last_name || ' · ' || w.name || ' · ' || a.admitted_on";
    return db.prepare(`SELECT a.id, ${label} AS label FROM admissions a
      JOIN patients p ON p.id = a.patient_id JOIN wards w ON w.id = a.ward_id
      ${filter(label, 'a')}
      ORDER BY a.id DESC LIMIT 100`).all(...params);
  }
  if (resource === 'diagnoses') {
    const label = "'Diagnosis #' || d.id || ' · ' || i.name || ' · ' || p.first_name || ' ' || p.last_name";
    return db.prepare(`SELECT d.id, ${label} AS label FROM diagnoses d
      JOIN illnesses i ON i.id = d.illness_id JOIN patients p ON p.id = d.patient_id
      ${filter(label, 'd')}
      ORDER BY d.id DESC LIMIT 100`).all(...params);
  }
  const label = naturalName(resource, 't');
  return db.prepare(`SELECT t.id, ${label} AS label FROM ${resource} t
    ${filter(label, 't')}
    ORDER BY label, t.id LIMIT 100`).all(...params);
}

function validDate(value) {
  if (!/^\d{4}-\d{2}-\d{2}$/.test(value)) return false;
  const parsed = new Date(`${value}T12:00:00Z`);
  return Number.isFinite(parsed.valueOf()) && parsed.toISOString().slice(0, 10) === value;
}

function validateInput(resource, input, partial = false) {
  const { fields } = definition(resource);
  if (!input || typeof input !== 'object' || Array.isArray(input)) bad('Expected a JSON object');
  const allowed = new Set(fields.map((field) => field.key));
  for (const key of Object.keys(input)) if (!allowed.has(key)) bad(`Unknown field: ${key}`);
  const data = {};
  for (const field of fields) {
    if (!(field.key in input) && partial) continue;
    let value = input[field.key];
    if (typeof value === 'string') value = value.trim();
    if (value === '' || value === undefined || value === null) {
      if (field.required) bad(`${field.label} is required`);
      data[field.key] = null;
      continue;
    }
    switch (field.type) {
      case 'number':
        if (!Number.isSafeInteger(value) || value < field.min || value > field.max) bad(`${field.label} must be an integer from ${field.min} to ${field.max}`);
        break;
      case 'relation':
        if (!Number.isSafeInteger(value) || value <= 0) bad(`${field.label} must reference a valid record`);
        break;
      case 'date':
        if (typeof value !== 'string' || !validDate(value)) bad(`${field.label} must be a valid date`);
        break;
      case 'time':
        if (typeof value !== 'string' || !/^([01]\d|2[0-3]):[0-5]\d$/.test(value)) bad(`${field.label} must be a 24-hour time`);
        break;
      case 'select':
        if (typeof value !== 'string' || !field.options.includes(value)) bad(`Invalid ${field.label.toLowerCase()}`);
        break;
      case 'text': case 'textarea':
        if (typeof value !== 'string' || value.length > field.max) bad(`${field.label} must contain at most ${field.max} characters`);
        break;
      default:
        bad(`Unsupported field: ${field.key}`);
    }
    data[field.key] = value;
  }
  if (!partial && Object.keys(data).length !== fields.length) bad('Missing required fields');
  return data;
}

// Date-only admission intervals are inclusive. A patient cannot be in two
// episodes on the same calendar day, even when both have been discharged.
function assertNoAdmissionOverlap(db, patientId, admittedOn, dischargedOn, exceptAdmissionId = 0) {
  const conflict = db.prepare(`SELECT a.id FROM admissions a
    LEFT JOIN discharges d ON d.admission_id = a.id
    WHERE a.patient_id = ? AND a.id != ?
      AND a.admitted_on <= COALESCE(?, '9999-12-31')
      AND (d.discharged_on IS NULL OR d.discharged_on >= ?)
    LIMIT 1`).get(patientId, exceptAdmissionId, dischargedOn, admittedOn);
  if (conflict) throw new AppError(409, 'Patient admission dates overlap another admission');
}

function guardBusinessRules(db, resource, data, id = null) {
  if (resource === 'admissions') {
    const existing = id ? getRecord(db, resource, id) : {};
    const patient = data.patient_id ?? existing.patient_id;
    const ward = data.ward_id ?? existing.ward_id;
    const discharge = id ? db.prepare('SELECT discharged_on FROM discharges WHERE admission_id = ?').get(id) : null;
    if (!discharge) {
      const conflicting = db.prepare(`SELECT COUNT(*) AS count FROM admissions a LEFT JOIN discharges d ON d.admission_id = a.id
        WHERE a.patient_id = ? AND d.id IS NULL AND a.id != ?`).get(patient, id ?? 0).count;
      if (conflicting) throw new AppError(409, 'This patient already has an active admission');
      const occupied = db.prepare(`SELECT COUNT(*) AS count FROM admissions a LEFT JOIN discharges d ON d.admission_id = a.id
        WHERE a.ward_id = ? AND d.id IS NULL AND a.id != ?`).get(ward, id ?? 0).count;
      const capacity = db.prepare('SELECT capacity FROM wards WHERE id = ?').get(ward)?.capacity;
      if (capacity !== undefined && occupied >= capacity) throw new AppError(409, 'This ward is at capacity');
    }
    if (discharge && (data.admitted_on ?? existing.admitted_on) > discharge.discharged_on) bad('Admission cannot be after discharge');
    assertNoAdmissionOverlap(db, patient, data.admitted_on ?? existing.admitted_on, discharge?.discharged_on ?? null, id ?? 0);
  }
  if (resource === 'wards' && id && data.capacity !== undefined) {
    const occupancy = db.prepare(`SELECT COUNT(*) AS count FROM admissions a LEFT JOIN discharges d ON d.admission_id = a.id
      WHERE a.ward_id = ? AND d.id IS NULL`).get(id).count;
    if (data.capacity < occupancy) throw new AppError(409, 'Capacity cannot fall below active occupancy');
  }
  if (resource === 'discharges') {
    const existing = id ? getRecord(db, resource, id) : {};
    const admissionId = data.admission_id ?? existing.admission_id;
    if (id && admissionId !== existing.admission_id) {
      bad('A discharge cannot be reassigned to another admission');
    }
    const admission = db.prepare('SELECT patient_id, admitted_on FROM admissions WHERE id = ?').get(admissionId);
    if (admission) {
      const dischargedOn = data.discharged_on ?? existing.discharged_on;
      if (dischargedOn < admission.admitted_on) bad('Discharge cannot precede admission');
      assertNoAdmissionOverlap(db, admission.patient_id, admission.admitted_on, dischargedOn, admissionId);
    }
  }
  if (resource === 'diagnoses' && id && data.patient_id !== undefined) {
    const linkedTreatment = db.prepare('SELECT COUNT(*) AS count FROM treatments WHERE diagnosis_id = ? AND patient_id != ?')
      .get(id, data.patient_id).count;
    if (linkedTreatment) throw new AppError(409, 'Diagnosis patient must match linked treatments');
  }
  if (resource === 'nurse_assignments') {
    const existing = id ? getRecord(db, resource, id) : {};
    const from = data.assigned_on ?? existing.assigned_on;
    const until = data.ends_on === undefined ? existing.ends_on : data.ends_on;
    if (until && until < from) bad('Assignment end cannot precede start');
  }
  if (resource === 'physician_schedules' || resource === 'nurse_shifts') {
    const existing = id ? getRecord(db, resource, id) : {};
    if ((data.starts_at ?? existing.starts_at) >= (data.ends_at ?? existing.ends_at)) bad('End time must follow start time');
  }
  if (resource === 'treatments') {
    const existing = id ? getRecord(db, resource, id) : {};
    const diagnosisId = data.diagnosis_id === undefined ? existing.diagnosis_id : data.diagnosis_id;
    const patientId = data.patient_id ?? existing.patient_id;
    if (diagnosisId) {
      const diagnosis = db.prepare('SELECT patient_id FROM diagnoses WHERE id = ?').get(diagnosisId);
      if (diagnosis && diagnosis.patient_id !== patientId) bad('Diagnosis must belong to the selected patient');
    }
  }
}

function handleConstraint(error) {
  // node:sqlite uses ERR_SQLITE_ERROR and an extended SQLite result code.
  if ((Number.isInteger(error.errcode) && (error.errcode & 0xff) === 19) || error.code?.startsWith('SQLITE_CONSTRAINT')) {
    throw new AppError(409, 'This change conflicts with an existing record or its related data');
  }
  throw error;
}

export function createRecord(db, resource, input) {
  const data = validateInput(resource, input);
  try {
    return transaction(db, () => {
      guardBusinessRules(db, resource, data);
      const keys = Object.keys(data);
      const result = db.prepare(`INSERT INTO ${resource} (${keys.join(', ')}) VALUES (${keys.map(() => '?').join(', ')})`)
        .run(...Object.values(data));
      return getRecord(db, resource, Number(result.lastInsertRowid));
    });
  } catch (error) { handleConstraint(error); }
}

export function updateRecord(db, resource, id, input) {
  const data = validateInput(resource, input, true);
  if (!Object.keys(data).length) bad('No fields to update');
  try {
    return transaction(db, () => {
      getRecord(db, resource, id);
      guardBusinessRules(db, resource, data, id);
      db.prepare(`UPDATE ${resource} SET ${Object.keys(data).map((key) => `${key} = ?`).join(', ')} WHERE id = ?`)
        .run(...Object.values(data), id);
      return getRecord(db, resource, id);
    });
  } catch (error) { handleConstraint(error); }
}

export function deleteRecord(db, resource, id) {
  try {
    return transaction(db, () => {
      const row = getRecord(db, resource, id);
      if (resource === 'discharges') {
        const admission = db.prepare('SELECT patient_id, ward_id, admitted_on FROM admissions WHERE id = ?').get(row.admission_id);
        const active = db.prepare(`SELECT COUNT(*) AS count FROM admissions a LEFT JOIN discharges d ON d.admission_id = a.id
          WHERE a.patient_id = ? AND d.id IS NULL`).get(admission.patient_id).count;
        if (active) throw new AppError(409, 'Cannot reopen an admission while the patient is admitted elsewhere');
        const occupancy = db.prepare(`SELECT COUNT(*) AS count FROM admissions a LEFT JOIN discharges d ON d.admission_id = a.id
          WHERE a.ward_id = ? AND d.id IS NULL`).get(admission.ward_id).count;
        const capacity = db.prepare('SELECT capacity FROM wards WHERE id = ?').get(admission.ward_id).capacity;
        if (occupancy >= capacity) throw new AppError(409, 'Cannot reopen admission: ward is full');
        assertNoAdmissionOverlap(db, admission.patient_id, admission.admitted_on, null, row.admission_id);
      }
      db.prepare(`DELETE FROM ${resource} WHERE id = ?`).run(id);
      return { deleted: true, id };
    });
  } catch (error) { handleConstraint(error); }
}

export function dashboard(db) {
  const count = (table) => db.prepare(`SELECT COUNT(*) AS value FROM ${table}`).get().value;
  const active = db.prepare(`SELECT COUNT(*) AS value FROM admissions a LEFT JOIN discharges d ON d.admission_id = a.id
    WHERE d.id IS NULL`).get().value;
  const capacity = db.prepare('SELECT COALESCE(SUM(capacity), 0) AS value FROM wards').get().value;
  const alerts = db.prepare('SELECT COUNT(*) AS value FROM medicines WHERE stock_qty <= reorder_level').get().value;
  const recent = db.prepare(`SELECT a.id, a.admitted_on, a.reason,
      p.first_name || ' ' || p.last_name AS patient,
      w.name AS ward, CASE WHEN d.id IS NULL THEN 'Active' ELSE 'Discharged' END AS status
      FROM admissions a JOIN patients p ON p.id = a.patient_id
      JOIN wards w ON w.id = a.ward_id LEFT JOIN discharges d ON d.admission_id = a.id
      ORDER BY a.admitted_on DESC, a.id DESC LIMIT 7`).all();
  const wards = db.prepare(`SELECT w.id, w.name, w.category, w.capacity,
      COUNT(a.id) AS occupied FROM wards w
      LEFT JOIN admissions a ON a.ward_id = w.id AND NOT EXISTS (SELECT 1 FROM discharges d WHERE d.admission_id = a.id)
      GROUP BY w.id ORDER BY w.id`).all();
  const date = new Date();
  const months = [];
  for (let i = 5; i >= 0; i -= 1) {
    const month = new Date(Date.UTC(date.getUTCFullYear(), date.getUTCMonth() - i, 1));
    const key = month.toISOString().slice(0, 7);
    const total = db.prepare(`SELECT COUNT(*) AS value FROM admissions WHERE substr(admitted_on, 1, 7) = ?`).get(key).value;
    months.push({ month: month.toLocaleString('en-US', { month: 'short', timeZone: 'UTC' }), key, count: total });
  }
  const topConditions = db.prepare(`SELECT i.name, COUNT(*) AS total FROM diagnoses d
    JOIN illnesses i ON i.id = d.illness_id GROUP BY i.id ORDER BY total DESC, i.name LIMIT 5`).all();
  const lowStock = db.prepare(`SELECT id, name, stock_qty, reorder_level, unit FROM medicines
    WHERE stock_qty <= reorder_level ORDER BY stock_qty ASC`).all();
  return {
    metrics: { patients: count('patients'), physicians: count('physicians'), nurses: count('nurses'),
      activeAdmissions: active, capacity, occupancyPercent: capacity ? Math.round(active / capacity * 100) : 0,
      treatments: count('treatments'), lowStock: alerts },
    recent, wards, months, topConditions, lowStock,
  };
}
