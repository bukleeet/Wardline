import assert from 'node:assert/strict';
import { test } from 'node:test';
import { openDatabase } from '../src/database.js';
import { operationsReport } from '../src/reports.js';

const DAY_MS = 86_400_000;
const ORIGIN = Date.parse('2024-02-01T00:00:00Z');
const WINDOWS = [
  ['2024-02-01', '2024-02-29'],
  ['2024-02-21', '2024-03-21'],
  ['2024-01-20', '2024-03-31'],
];

function day(offset) {
  return new Date(ORIGIN + offset * DAY_MS).toISOString().slice(0, 10);
}

function daysBetween(start, end) {
  return Math.round((Date.parse(`${end}T00:00:00Z`) - Date.parse(`${start}T00:00:00Z`)) / DAY_MS);
}

function randomSequence(seed) {
  let state = seed;
  return () => {
    state = (Math.imul(1664525, state) + 1013904223) >>> 0;
    return state;
  };
}

function fixture(db, seed) {
  const next = randomSequence(seed);
  const wards = [
    { id: 1, capacity: 1 },
    { id: 2, capacity: 3 },
    { id: 3, capacity: 5 },
  ];
  const addWard = db.prepare('INSERT INTO wards (id, name, floor, category, capacity) VALUES (?, ?, ?, ?, ?)');
  for (const ward of wards) addWard.run(ward.id, `Ward ${ward.id}`, '1', 'General', ward.capacity);

  const addPatient = db.prepare(`INSERT INTO patients
    (id, first_name, last_name, date_of_birth, sex, contact_no) VALUES (?, ?, ?, ?, ?, ?)`);
  const addAdmission = db.prepare(`INSERT INTO admissions
    (id, patient_id, ward_id, admitted_on, reason) VALUES (?, ?, ?, ?, ?)`);
  const addDischarge = db.prepare(`INSERT INTO discharges
    (id, admission_id, discharged_on, disposition) VALUES (?, ?, ?, ?)`);
  const stays = [];
  let id = 0;
  for (let patientId = 1; patientId <= 16; patientId += 1) {
    addPatient.run(patientId, `Patient${patientId}`, 'Synthetic', '1990-01-01', 'F', '0');
    let start = Number(next() % 40) - 20;
    const count = 1 + Number(next() % 4);
    for (let index = 0; index < count; index += 1) {
      const wardId = 1 + Number(next() % wards.length);
      const length = Number(next() % 11);
      const isOpen = index === count - 1 && next() % 3 === 0;
      const admittedOn = day(start);
      const dischargedOn = isOpen ? null : day(start + length);
      id += 1;
      addAdmission.run(id, patientId, wardId, admittedOn, 'Synthetic audit case');
      if (dischargedOn) addDischarge.run(id, id, dischargedOn, 'Recovered');
      stays.push({ patientId, wardId, admittedOn, dischargedOn });
      start += length + 1 + Number(next() % 18);
    }
  }
  return { wards, stays };
}

function reference(wards, stays, from, to) {
  const calendar = [];
  for (let date = from; date <= to; date = day(daysBetween(day(0), date) + 1)) calendar.push(date);
  const byWard = new Map(wards.map((ward) => [ward.id, {
    bedDays: 0, peak: 0, peakDay: from, atCapacity: 0, overCapacity: 0,
    discharges: 0, returns: 0,
  }]));
  const daily = calendar.map((date) => {
    const active = stays.filter((stay) => stay.admittedOn <= date &&
      (stay.dischargedOn === null || stay.dischargedOn >= date));
    for (const ward of wards) {
      const occupancy = active.filter((stay) => stay.wardId === ward.id).length;
      const result = byWard.get(ward.id);
      result.bedDays += occupancy;
      if (occupancy > result.peak) {
        result.peak = occupancy;
        result.peakDay = date;
      }
      if (occupancy >= ward.capacity) result.atCapacity += 1;
      if (occupancy > ward.capacity) result.overCapacity += 1;
    }
    return {
      day: date,
      admissions: stays.filter((stay) => stay.admittedOn === date).length,
      discharges: stays.filter((stay) => stay.dischargedOn === date).length,
      census: active.length,
    };
  });
  let netFlow = 0;
  for (const row of daily) {
    netFlow += row.admissions - row.discharges;
    row.netFlow = netFlow;
  }
  const perPatient = new Map();
  for (const stay of stays) {
    if (!perPatient.has(stay.patientId)) perPatient.set(stay.patientId, []);
    perPatient.get(stay.patientId).push(stay);
  }
  for (const history of perPatient.values()) {
    history.sort((a, b) => a.admittedOn.localeCompare(b.admittedOn));
    history.forEach((stay, index) => {
      if (!stay.dischargedOn || stay.dischargedOn < from || stay.dischargedOn > to) return;
      const result = byWard.get(stay.wardId);
      result.discharges += 1;
      const next = history[index + 1];
      if (next && daysBetween(stay.dischargedOn, next.admittedOn) >= 1 &&
        daysBetween(stay.dischargedOn, next.admittedOn) <= 30) result.returns += 1;
    });
  }
  return { daily, byWard };
}

test('SQL operational reports agree with an independent calendar and return-cohort oracle', () => {
  for (let seed = 1; seed <= 15; seed += 1) {
    const db = openDatabase({ file: ':memory:', seed: false });
    try {
      const { wards, stays } = fixture(db, seed);
      for (const [from, to] of WINDOWS) {
        const expected = reference(wards, stays, from, to);
        const report = operationsReport(db, { from, to });
        const label = `seed=${seed}, window=${from}..${to}`;
        assert.deepEqual(report.daily.map(({ day: date, admissions, discharges, census, net_flow }) =>
          ({ day: date, admissions, discharges, census, netFlow: net_flow })), expected.daily, label);
        for (const ward of report.wards) {
          const actual = expected.byWard.get(ward.id);
          assert.deepEqual({
            bedDays: ward.occupied_bed_days,
            peak: ward.peak_census,
            peakDay: ward.peak_day,
            atCapacity: ward.days_at_capacity,
            overCapacity: ward.days_over_capacity,
          }, {
            bedDays: actual.bedDays,
            peak: actual.peak,
            peakDay: actual.peakDay,
            atCapacity: actual.atCapacity,
            overCapacity: actual.overCapacity,
          }, `${label}, ward=${ward.id}`);
        }
        for (const row of report.returns) {
          const actual = expected.byWard.get(row.ward_id);
          assert.deepEqual([row.discharged_stays, row.returns_within_30_days],
            [actual.discharges, actual.returns], `${label}, returns ward=${row.ward_id}`);
        }
        const total = [...expected.byWard.values()].reduce((sum, ward) => sum + ward.bedDays, 0);
        assert.equal(report.summary.occupiedBedDays, total, label);
        assert.equal(report.summary.occupiedBedDays,
          expected.daily.reduce((sum, row) => sum + row.census, 0), label);
        assert.equal(report.summary.admissions,
          expected.daily.reduce((sum, row) => sum + row.admissions, 0), label);
        assert.equal(report.summary.discharges,
          expected.daily.reduce((sum, row) => sum + row.discharges, 0), label);
        assert.equal(report.summary.returnsWithin30Days,
          [...expected.byWard.values()].reduce((sum, ward) => sum + ward.returns, 0), label);
      }
    } finally {
      db.close();
    }
  }
});
