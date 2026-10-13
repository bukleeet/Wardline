import assert from 'node:assert/strict';
import { test } from 'node:test';
import { openDatabase } from '../src/database.js';
import { operationsReport } from '../src/reports.js';

test('reports interval occupancy, dense calendar and fanout-safe physician workload', () => {
  const db = openDatabase({ file: ':memory:', seed: false });
  try {
    db.exec(`
      INSERT INTO patients VALUES (1,'A','Patient','2000-01-01','F','0');
      INSERT INTO patients VALUES (2,'B','Patient','2000-01-01','M','0');
      INSERT INTO wards VALUES (1,'Ward A','1','General',2);
      INSERT INTO wards VALUES (2,'Empty ward','2','General',3);
      INSERT INTO physicians VALUES (1,'Dana','Doctor','General','0');
      INSERT INTO illnesses VALUES (1,'Cold','General','');
      INSERT INTO admissions VALUES (1,1,1,'2026-01-01','');
      INSERT INTO admissions VALUES (2,2,1,'2026-01-03','');
      INSERT INTO discharges VALUES (1,1,'2026-01-03','Recovered','');
      INSERT INTO diagnoses VALUES (1,1,1,1,'2026-01-02','');
      INSERT INTO diagnoses VALUES (2,1,1,1,'2026-01-02','');
      INSERT INTO treatments (id,patient_id,physician_id,performed_on,procedure_name)
        VALUES (1,1,1,'2026-01-02','A'),(2,1,1,'2026-01-03','B');
    `);
    const result = operationsReport(db, { from: '2026-01-02', to: '2026-01-04' });
    assert.deepEqual(result.daily.map(({ admissions, discharges, census, net_flow }) =>
      [admissions, discharges, census, net_flow]), [[0,0,1,0],[1,1,2,0],[0,0,1,0]]);
    assert.equal(result.wards[0].occupied_bed_days, 4);
    assert.equal(result.wards[0].utilization_pct, 66.7);
    assert.equal(result.wards[1].occupied_bed_days, 0);
    assert.deepEqual([result.physicians[0].diagnoses, result.physicians[0].treatments, result.physicians[0].treated_patients], [2,2,1]);
    assert.equal(result.summary.avgStayDays, 3);
    assert.equal(result.summary.occupiedBedDays, 4);
  } finally { db.close(); }
});

test('report validates dates, leap days, and range limits', () => {
  const db = openDatabase({ file: ':memory:', seed: false });
  try {
    assert.equal(operationsReport(db, { from: '2024-02-29', to: '2024-02-29' }).days, 1);
    for (const range of [
      { from: '2026-02-29', to: '2026-03-01' },
      { from: '2026-04-03', to: '2026-04-02' },
      { from: '2024-01-01', to: '2025-01-01' },
      { from: '2026-01-01', to: null },
      { from: '2026-01-01\' OR 1=1 --', to: '2026-01-02' },
    ]) assert.throws(() => operationsReport(db, range), { status: 400 });
  } finally { db.close(); }
});

test('daily census carries in opening patients and counts discharge days inclusively', () => {
  const db = openDatabase({ file: ':memory:', seed: false });
  try {
    db.exec(`
      INSERT INTO wards VALUES (1,'Ward A','1','General',5);
      INSERT INTO patients VALUES (1,'A','Patient','2000-01-01','F','0');
      INSERT INTO patients VALUES (2,'B','Patient','2000-01-01','M','0');
      INSERT INTO patients VALUES (3,'C','Patient','2000-01-01','F','0');
      INSERT INTO patients VALUES (4,'D','Patient','2000-01-01','M','0');
      INSERT INTO admissions VALUES (1,1,1,'2026-02-25','');
      INSERT INTO admissions VALUES (2,2,1,'2026-03-01','');
      INSERT INTO admissions VALUES (3,3,1,'2026-03-02','');
      INSERT INTO admissions VALUES (4,4,1,'2026-02-21','');
      INSERT INTO discharges VALUES (1,1,'2026-03-01','Recovered','');
      INSERT INTO discharges VALUES (2,2,'2026-03-01','Recovered','');
      INSERT INTO discharges VALUES (3,4,'2026-02-28','Recovered','');
    `);
    const report = operationsReport(db, { from: '2026-03-01', to: '2026-03-03' });
    assert.deepEqual(report.daily.map(({ admissions, discharges, census, net_flow }) =>
      [admissions, discharges, census, net_flow]), [[1, 2, 2, -1], [1, 0, 1, 0], [0, 0, 1, 0]]);
    assert.equal(report.summary.occupiedBedDays, 4);
    assert.equal(report.summary.avgStayDays, 3);
    assert.equal(report.summary.availableBedDays, 15);
  } finally { db.close(); }
});
