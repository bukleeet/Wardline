import assert from 'node:assert/strict';
import { spawnSync } from 'node:child_process';
import { test } from 'node:test';
import { fileURLToPath } from 'node:url';

const script = fileURLToPath(new URL('../scripts/benchmark-sql.mjs', import.meta.url));

function invoke(...args) {
  return spawnSync(process.execPath, [script, ...args], {
    encoding: 'utf8',
    timeout: 30_000,
  });
}

test('SQL benchmark exercises live queries with bounded, reproducible fixtures', () => {
  const result = invoke('--patients=12', '--stays=2', '--warmup=1', '--iterations=2', '--seed=17', '--json');
  assert.equal(result.status, 0, result.stderr);
  const output = JSON.parse(result.stdout);
  assert.deepEqual(output.window, { from: '2024-01-01', to: '2024-03-31' });
  assert.equal(output.settings.seed, 17);
  assert.equal(output.dataset.patients, 12);
  assert.equal(output.dataset.admissions, 24);
  assert.equal(output.dataset.diagnoses, 24);
  assert.equal(output.dataset.treatments, 24);
  assert.match(output.runtime.sqlite, /^\d+\.\d+\.\d+$/);
  assert.deepEqual(output.queries.map(({ id }) => id),
    ['operations-report', 'physician-workload', 'recorded-returns', 'patient-timeline']);
  for (const query of output.queries) {
    assert.equal(query.samplesMs.length, 2);
    assert.ok(query.samplesMs.every((sample) => Number.isFinite(sample) && sample >= 0));
    assert.ok(Number.isInteger(query.rowsPerRun) && query.rowsPerRun >= 0);
    assert.ok(query.medianMs >= query.minMs && query.medianMs <= query.maxMs);
    if (query.id === 'operations-report') assert.equal(query.plan, null);
    else assert.ok(query.plan.length > 0);
  }
});

test('SQL benchmark rejects invalid workloads before creating data', () => {
  const result = invoke('--patients=0');
  assert.notEqual(result.status, 0);
  assert.match(result.stderr, /patients must be an integer/);
});
