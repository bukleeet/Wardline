import assert from 'node:assert/strict';
import { once } from 'node:events';
import { Readable, Writable } from 'node:stream';
import { afterEach, beforeEach, test } from 'node:test';
import { openDatabase } from '../src/database.js';
import { exportCsv, MAX_EXPORT_ROWS } from '../src/export.js';
import { createRecord } from '../src/repository.js';
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

async function request(path, { method = 'GET', headers = {} } = {}) {
  const req = Readable.from([]);
  req.url = path;
  req.method = method;
  req.headers = { host: '127.0.0.1:3000', ...headers };
  const chunks = [];
  const res = new Writable({
    write(chunk, _encoding, done) { chunks.push(Buffer.from(chunk)); done(); },
  });
  res.headersSent = false;
  res.writeHead = (status, responseHeaders) => {
    res.statusCode = status;
    res.headers = responseHeaders;
    res.headersSent = true;
    return res;
  };
  const finished = once(res, 'finish');
  server.emit('request', req, res);
  await finished;
  return { status: res.statusCode, headers: res.headers, body: Buffer.concat(chunks).toString('utf8') };
}

test('exports all form fields, readable relations, and filtered records', async () => {
  const response = await request('/api/export/admissions?search=amara');
  assert.equal(response.status, 200);
  assert.equal(response.headers['Content-Type'], 'text/csv; charset=utf-8');
  assert.equal(response.headers['Content-Disposition'], 'attachment; filename="wardline-admissions.csv"');
  assert.equal(response.headers['Cache-Control'], 'no-store');
  assert.ok(response.body.startsWith('\uFEFF'));
  assert.match(response.body, /"ID","Patient","Patient name","Ward","Ward name","Admission date","Reason"/);
  assert.match(response.body, /"Amara/);
  assert.equal(response.body, exportCsv(db, 'admissions', { search: 'amara' }));
  const unmatched = await request('/api/export/admissions?search=nomatchingrecords');
  assert.equal(unmatched.status, 200);
  assert.equal(unmatched.body.trim().split('\r\n').length, 1);
});

test('escapes formula cells, quotes, line breaks, and separators in CSV', () => {
  createRecord(db, 'patients', {
    first_name: '=1+1',
    last_name: 'Line,\n"quoted"',
    date_of_birth: '2002-04-12',
    sex: 'F',
    contact_no: '@SUM(1,2)',
  });
  const csv = exportCsv(db, 'patients', { search: '=1+1' });
  assert.match(csv, /"'=1\+1"/);
  assert.match(csv, /"Line,\n""quoted"""/);
  assert.match(csv, /"'@SUM\(1,2\)"/);
  assert.doesNotMatch(csv, /"Amara"/);
});

test('validates export resources and searches, including inherited property names', async () => {
  for (const resource of ['constructor', '__proto__', 'does_not_exist']) {
    const response = await request(`/api/export/${resource}`);
    assert.equal(response.status, 404, resource);
  }
  assert.equal((await request(`/api/export/patients?search=${'a'.repeat(151)}`)).status, 400);
  assert.equal((await request('/api/export/patients', {
    headers: { origin: 'https://other.invalid' },
  })).status, 403);
  assert.equal((await request('/api/export/patients', { method: 'POST' })).status, 404);
  for (const path of ['/toString', '/constructor', '/__proto__']) {
    assert.equal((await request(path)).status, 404, path);
  }
});

test('enforces export limit before serializing large datasets', async () => {
  db.exec(`WITH RECURSIVE numbers(n) AS (
    SELECT 1 UNION ALL SELECT n+1 FROM numbers WHERE n <= ${MAX_EXPORT_ROWS}
  ) INSERT INTO nurses (first_name, last_name, contact_no)
    SELECT 'Extra', 'Nurse', CAST(n AS TEXT) FROM numbers`);
  assert.throws(() => exportCsv(db, 'nurses'), { status: 422 });
  const response = await request('/api/export/nurses');
  assert.equal(response.status, 422);
  assert.match(JSON.parse(response.body).error, /Refine your search/);
  assert.equal((await request('/api/export/nurses?search=Amara')).status, 200);
});
