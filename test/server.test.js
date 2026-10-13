import assert from 'node:assert/strict';
import { spawnSync } from 'node:child_process';
import { once } from 'node:events';
import { dirname, resolve } from 'node:path';
import { Readable, Writable } from 'node:stream';
import { afterEach, beforeEach, test } from 'node:test';
import { fileURLToPath } from 'node:url';
import { openDatabase } from '../src/database.js';
import { createServer } from '../src/server.js';

let db;
let server;

beforeEach(() => {
  db = openDatabase({ file: ':memory:' });
  server = createServer({ db });
});

afterEach(() => {
  server.closeAllConnections();
  db.close();
});

// Drive the actual HTTP request listener without a socket. CI sandboxes may
// prohibit connections to 127.0.0.1, even when the server can bind locally.
async function request(method, path, { body, headers = {} } = {}) {
  const payload = body === undefined ? [] : [typeof body === 'string' ? body : JSON.stringify(body)];
  const req = Readable.from(payload.map((chunk) => Buffer.from(chunk)));
  req.url = path;
  req.method = method;
  req.headers = { host: '127.0.0.1:3000', ...headers };
  const chunks = [];
  const res = new Writable({
    write(chunk, _encoding, callback) {
      chunks.push(Buffer.from(chunk));
      callback();
    },
  });
  res.headersSent = false;
  res.writeHead = (status, responseHeaders) => {
    res.statusCode = status;
    res.responseHeaders = responseHeaders;
    res.headersSent = true;
    return res;
  };
  const finished = once(res, 'finish');
  server.emit('request', req, res);
  await finished;
  const text = Buffer.concat(chunks).toString('utf8');
  return { status: res.statusCode, headers: res.responseHeaders, text, json: () => JSON.parse(text) };
}

const json = { 'content-type': 'application/json' };

test('CLI entrypoint validates the configured port', () => {
  const projectRoot = resolve(dirname(fileURLToPath(import.meta.url)), '..');
  const result = spawnSync(process.execPath, ['src/server.js'], {
    cwd: projectRoot,
    env: { ...process.env, PORT: '0' },
    encoding: 'utf8',
    timeout: 10_000,
  });
  assert.equal(result.error, undefined);
  assert.equal(result.status, 1);
  assert.match(result.stderr, /PORT must be between 1 and 65535/);
});

test('serves secure static assets, HEAD, and health checks', async () => {
  const index = await request('GET', '/');
  assert.equal(index.status, 200);
  assert.match(index.text, /Wardline/);
  assert.match(index.headers['Content-Security-Policy'], /default-src 'none'/);
  assert.equal(index.headers['X-Content-Type-Options'], 'nosniff');
  for (const asset of ['styles.css', 'app.js', 'favicon.svg']) {
    const response = await request('GET', `/${asset}`);
    assert.equal(response.status, 200, asset);
    assert.ok(response.text.length > 100, asset);
  }
  const head = await request('HEAD', '/');
  assert.equal(head.status, 200);
  assert.equal(head.text, '');
  assert.deepEqual((await request('GET', '/api/health')).json(), { status: 'ok' });
  assert.equal((await request('GET', '/private.txt')).status, 404);
});

test('lists resources, filters records, and returns dashboard data', async () => {
  const catalog = (await request('GET', '/api/catalog')).json();
  assert.equal(Object.keys(catalog).length, 13);
  const data = (await request('GET', '/api/records/patients?search=amara&page=1&pageSize=5')).json();
  assert.equal(data.total, 1);
  assert.equal(data.rows[0].first_name, 'Amara');
  assert.equal((await request('GET', '/api/dashboard')).json().metrics.patients, 32);
  assert.ok((await request('GET', '/api/lookup/physicians')).json()[0].label);
  assert.match((await request('GET', '/api/lookup/admissions')).json()[0].label, /^Admission #\d+ · .+ · .+ · \d{4}-\d{2}-\d{2}$/);
  assert.match((await request('GET', '/api/lookup/diagnoses')).json()[0].label, /^Diagnosis #\d+ · .+ · .+/);
});

test('operations report serves SQL analytics and validates date parameters over HTTP', async () => {
  const response = await request('GET', '/api/reports/operations?from=2026-01-01&to=2026-01-03');
  assert.equal(response.status, 200);
  const report = response.json();
  assert.equal(report.days, 3);
  assert.equal(report.daily.length, 3);
  assert.ok(Array.isArray(report.wards));
  assert.ok(Array.isArray(report.physicians));
  assert.ok(Array.isArray(report.returns));
  assert.equal(report.summary.discharges,
    report.returns.reduce((sum, ward) => sum + ward.discharged_stays, 0));
  assert.equal(report.summary.returnsWithin30Days,
    report.returns.reduce((sum, ward) => sum + ward.returns_within_30_days, 0));
  assert.equal(report.summary.availableBedDays,
    report.wards.reduce((sum, ward) => sum + ward.capacity * 3, 0));
  assert.equal(report.summary.occupiedBedDays,
    report.wards.reduce((sum, ward) => sum + ward.census_bed_days, 0));
  for (const ward of report.wards) {
    assert.equal(typeof ward.peak_census, 'number');
    assert.match(ward.peak_day, /^\d{4}-\d{2}-\d{2}$/);
    assert.equal(typeof ward.days_at_capacity, 'number');
    assert.equal(typeof ward.days_over_capacity, 'number');
  }

  for (const query of [
    '', '?from=2026-01-01', '?to=2026-01-01',
    '?from=2026-03-05&to=2026-03-04',
    '?from=2026-01-01&to=2027-01-02',
    '?from=2026-02-29&to=2026-03-01',
  ]) {
    const error = await request('GET', `/api/reports/operations${query}`);
    assert.equal(error.status, 400, query);
    assert.equal(typeof error.json().error, 'string');
  }
});

test('read-only integrity endpoint reports complete database checks over HTTP', async () => {
  const response = await request('GET', '/api/reports/integrity');
  assert.equal(response.status, 200);
  const audit = response.json();
  assert.equal(audit.checked, 7);
  assert.equal(audit.total, 0);
  assert.equal(audit.checks.length, 7);
  db.exec(`UPDATE physician_schedules SET ends_at = starts_at WHERE id =
    (SELECT MIN(id) FROM physician_schedules)`);
  const altered = (await request('GET', '/api/reports/integrity')).json();
  assert.equal(altered.total, 1);
  assert.equal(altered.checks.find(({ code }) => code === 'physician_hours').count, 1);
});

test('filters relation lookups and validates their search parameters', async () => {
  const patient = (await request('GET', '/api/lookup/patients?search=amara')).json();
  assert.equal(patient.length, 1);
  assert.match(patient[0].label, /Amara/);
  assert.deepEqual((await request('GET', `/api/lookup/patients?search=%23${patient[0].id}`)).json(), patient);
  const admission = (await request('GET', '/api/lookup/admissions')).json()[0];
  assert.deepEqual((await request('GET', `/api/lookup/admissions?search=%23${admission.id}`)).json(), [admission]);
  assert.deepEqual((await request('GET', '/api/lookup/wards?search=not-a-ward')).json(), []);
  assert.equal((await request('GET', `/api/lookup/patients?search=${'x'.repeat(151)}`)).status, 400);
  assert.equal((await request('GET', '/api/lookup/patients?search=%239999999999999999999')).status, 400);
  assert.equal((await request('GET', '/api/lookup/unknown')).status, 404);
  assert.match((await request('GET', '/api/lookup/discharges')).json()[0].label, /^Discharge #/);
  assert.match((await request('GET', '/api/lookup/physician_schedules')).json()[0].label, /^Schedule #/);
  assert.equal((await request('GET', '/api/lookup/constructor')).status, 404);
  assert.equal((await request('GET', '/api/records/__proto__')).status, 404);
});

test('supports REST create, update, and delete with JSON content types', async () => {
  const payload = { first_name: 'REST', last_name: 'Tester', date_of_birth: '1997-03-11', sex: 'M', contact_no: '100200' };
  const createdResponse = await request('POST', '/api/records/patients', { headers: json, body: payload });
  assert.equal(createdResponse.status, 201);
  const created = createdResponse.json();
  const changed = await request('PATCH', `/api/records/patients/${created.id}`, { headers: json, body: { first_name: 'Edited' } });
  assert.equal(changed.status, 200);
  assert.equal(changed.json().first_name, 'Edited');
  assert.equal((await request('DELETE', `/api/records/patients/${created.id}`)).status, 200);
  assert.equal((await request('GET', `/api/records/patients/${created.id}`)).status, 404);
});

test('rejects invalid bodies, pagination, cross-site writes, and unknown routes', async () => {
  assert.equal((await request('GET', '/api/records/patients?page=-1')).status, 400);
  assert.equal((await request('GET', '/api/records/patients?pageSize=101')).status, 400);
  assert.equal((await request('GET', '/api/records/no_such_resource')).status, 404);
  assert.equal((await request('POST', '/api/records/patients', { headers: { 'content-type': 'text/plain' }, body: '{}' })).status, 415);
  assert.equal((await request('POST', '/api/records/patients', { headers: json, body: '{invalid' })).status, 400);
  assert.equal((await request('POST', '/api/records/patients', { headers: json, body: 'x'.repeat(16385) })).status, 413);
  assert.equal((await request('POST', '/api/records/patients', { headers: { ...json, origin: 'https://evil.invalid' }, body: {} })).status, 403);
  assert.equal((await request('POST', '/api/records/patients', { headers: { ...json, origin: 'https://127.0.0.1:3000' }, body: {} })).status, 403);
  assert.equal((await request('POST', '/api/records/patients', { headers: { ...json, 'sec-fetch-site': 'cross-site' }, body: {} })).status, 403);
  assert.equal((await request('POST', '/api/records/patients', { headers: { ...json, origin: 'http://127.0.0.1:3000' }, body: {} })).status, 400);
  assert.equal((await request('POST', '/')).status, 405);
});

test('rejects cross-origin reads and protects source files from static requests', async () => {
  const foreign = { origin: 'https://elsewhere.invalid' };
  assert.equal((await request('GET', '/api/dashboard', { headers: foreign })).status, 403);
  assert.equal((await request('GET', '/api/records/patients', {
    headers: { 'sec-fetch-site': 'cross-site' },
  })).status, 403);
  assert.equal((await request('GET', '/api/records/patients', {
    headers: { origin: 'http://127.0.0.1:3000' },
  })).status, 200);
  assert.equal((await request('GET', '/src/server.js')).status, 404);
  assert.equal((await request('GET', '/.git/config')).status, 404);
  assert.equal((await request('HEAD', '/src/server.js')).status, 404);
});

test('rejects oversized search strings and unrepresentable page values', async () => {
  const hugeSearch = new URLSearchParams({ search: 'a'.repeat(151) });
  assert.equal((await request('GET', `/api/records/patients?${hugeSearch}`)).status, 400);
  assert.equal((await request('GET', '/api/records/patients?page=9007199254740992')).status, 400);
  assert.equal((await request('GET', '/api/records/patients?pageSize=0')).status, 400);
  assert.equal((await request('GET', '/api/records/patients?page=Infinity')).status, 400);
});
