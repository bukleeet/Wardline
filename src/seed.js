/** Synthetic, deterministic demonstration data. No real patient information. */
const people = [
  ['Amara', 'Santos'], ['Noah', 'Reyes'], ['Sofia', 'Cruz'], ['Ethan', 'Lim'],
  ['Isabella', 'Tan'], ['Liam', 'Rivera'], ['Mia', 'Dela Cruz'], ['Lucas', 'Garcia'],
  ['Ava', 'Mendoza'], ['Elijah', 'Torres'], ['Chloe', 'Bautista'], ['Oliver', 'Ramos'],
  ['Zoe', 'Navarro'], ['James', 'Flores'], ['Ella', 'Castillo'], ['Benjamin', 'Aquino'],
  ['Hannah', 'Valdez'], ['Daniel', 'Chua'], ['Grace', 'Sy'], ['Jacob', 'Co'],
  ['Aria', 'Lopez'], ['Nathan', 'Yu'], ['Luna', 'Villanueva'], ['Caleb', 'Ong'],
  ['Leah', 'Fernandez'], ['Adrian', 'Gonzales'], ['Nina', 'Salazar'], ['Leo', 'Domingo'],
  ['Ruby', 'David'], ['Samuel', 'Estrada'], ['Iris', 'Mercado'], ['Theo', 'Padilla'],
];
const physicians = [
  ['Maria', 'Salcedo', 'Internal Medicine'], ['Marco', 'Villarin', 'Cardiology'],
  ['Clara', 'Dy', 'Neurology'], ['Paolo', 'Cabrera', 'Emergency Medicine'],
  ['Elise', 'Ferrer', 'Pediatrics'], ['Anton', 'Guevara', 'General Surgery'],
  ['Rina', 'De Leon', 'Pulmonology'], ['Andre', 'Velasco', 'Orthopedics'],
  ['Joanna', 'Lucero', 'Obstetrics'], ['Miguel', 'Soriano', 'Family Medicine'],
];
const nurses = [
  ['Alyssa', 'Pineda'], ['Gabriel', 'Chan'], ['Nicole', 'Sarmiento'], ['Joshua', 'Tiu'],
  ['Denise', 'Calderon'], ['Patrick', 'Ang'], ['Bianca', 'Estrada'], ['Rafael', 'Bacani'],
  ['Katrina', 'Chavez'], ['Aaron', 'Go'], ['Jasmine', 'Lao'], ['Tristan', 'Morales'],
];
const conditions = [
  ['Hypertension', 'Cardiovascular'], ['Pneumonia', 'Respiratory'],
  ['Type 2 diabetes', 'Endocrine'], ['Asthma', 'Respiratory'],
  ['Appendicitis', 'Gastrointestinal'], ['Migraine', 'Neurological'],
  ['Acute gastroenteritis', 'Gastrointestinal'], ['Fracture', 'Orthopedic'],
  ['Urinary tract infection', 'Infectious'], ['Anemia', 'Hematology'],
];
const meds = [
  ['Paracetamol', 'Analgesic', 'tablets', 345, 60], ['Amoxicillin', 'Antibiotic', 'capsules', 48, 50],
  ['Metformin', 'Diabetes care', 'tablets', 186, 40], ['Salbutamol', 'Respiratory', 'inhalers', 22, 30],
  ['Amlodipine', 'Cardiac care', 'tablets', 154, 40], ['Omeprazole', 'Gastrointestinal', 'capsules', 72, 30],
  ['Cetirizine', 'Antihistamine', 'tablets', 207, 35], ['Ibuprofen', 'Analgesic', 'tablets', 132, 40],
  ['Ceftriaxone', 'Antibiotic', 'vials', 18, 25], ['Insulin', 'Diabetes care', 'vials', 36, 40],
  ['Azithromycin', 'Antibiotic', 'tablets', 65, 30], ['Furosemide', 'Cardiac care', 'tablets', 87, 20],
];

const iso = (daysAgo) => {
  const date = new Date();
  date.setUTCHours(12, 0, 0, 0);
  date.setUTCDate(date.getUTCDate() - daysAgo);
  return date.toISOString().slice(0, 10);
};
const insert = (db, table, columns, values) => db.prepare(
  `INSERT INTO ${table} (${columns.join(', ')}) VALUES (${columns.map(() => '?').join(', ')})`
).run(...values);

export function seedDatabase(db) {
  people.forEach(([first, last], index) => insert(db, 'patients',
    ['first_name', 'last_name', 'date_of_birth', 'sex', 'contact_no'],
    [first, last, `${1964 + index % 38}-${String(index % 12 + 1).padStart(2, '0')}-${String(index % 27 + 1).padStart(2, '0')}`,
      index % 2 ? 'M' : 'F', `+63 900 000 ${String(index + 1000).slice(-4)}`]));

  physicians.forEach(([first, last, specialty], index) => insert(db, 'physicians',
    ['first_name', 'last_name', 'specialty', 'contact_no'], [first, last, specialty, `+63 901 000 ${1000 + index}`]));
  nurses.forEach(([first, last], index) => insert(db, 'nurses',
    ['first_name', 'last_name', 'contact_no'], [first, last, `+63 902 000 ${1000 + index}`]));
  [
    ['North Wing A', '2', 'General', 18], ['North Wing B', '2', 'General', 18],
    ['South Wing A', '3', 'General', 20], ['Critical Care', '4', 'ICU', 12],
    ['Emergency', '1', 'Emergency', 16], ['Pediatrics', '3', 'Pediatrics', 14],
    ['Surgical Recovery', '5', 'Surgical', 12], ['Maternity', '5', 'Maternity', 14],
  ].forEach((item) => insert(db, 'wards', ['name', 'floor', 'category', 'capacity'], item));
  conditions.forEach(([name, category]) => insert(db, 'illnesses', ['name', 'category', 'description'],
    [name, category, `Clinical reference for ${name.toLowerCase()}.`]));
  meds.forEach((item) => insert(db, 'medicines', ['name', 'category', 'unit', 'stock_qty', 'reorder_level'], item));

  // Historical admissions followed by active admissions; trends remain relevant whenever the demo runs.
  for (let i = 0; i < 40; i += 1) {
    const recent = i >= 24;
    const admittedOn = iso(recent ? (i - 23) * 2 : 15 + (i * 7) % 230);
    insert(db, 'admissions', ['patient_id', 'ward_id', 'admitted_on', 'reason'],
      [recent ? i - 23 : (i % 32) + 1, (i % 8) + 1, admittedOn,
        ['Observation', 'Diagnostic assessment', 'Scheduled procedure', 'Ongoing treatment'][i % 4]]);
    if (!recent) insert(db, 'discharges', ['admission_id', 'discharged_on', 'disposition', 'notes'],
      [i + 1, iso(Math.max(0, 12 + (i * 7) % 230)), i % 6 === 0 ? 'Home care' : 'Recovered', 'Stable at discharge']);
  }

  for (let i = 0; i < 45; i += 1) {
    insert(db, 'diagnoses', ['patient_id', 'physician_id', 'illness_id', 'diagnosed_on', 'notes'],
      [(i % 32) + 1, (i % 10) + 1, (i % 10) + 1, iso((i * 5) % 180), 'Reviewed by attending physician']);
  }
  for (let i = 0; i < 62; i += 1) {
    insert(db, 'treatments',
      ['patient_id', 'diagnosis_id', 'physician_id', 'nurse_id', 'medicine_id', 'performed_on', 'procedure_name', 'notes'],
      [i % 32 + 1, null, i % 10 + 1, i % 12 + 1, i % 12 + 1, iso((i * 3) % 150),
        ['Medication administration', 'Vital signs assessment', 'Patient monitoring', 'Follow-up consultation'][i % 4],
        'Care plan followed']);
  }
  for (let i = 0; i < 22; i += 1) {
    insert(db, 'nurse_assignments', ['nurse_id', 'patient_id', 'assigned_on', 'ends_on'],
      [(i % 12) + 1, (i % 32) + 1, iso(i * 2), i % 3 ? null : iso(i)]);
  }
  const days = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday'];
  physicians.forEach((_, index) => days.slice(0, 3 + index % 3).forEach((day) => {
    insert(db, 'physician_schedules', ['physician_id', 'weekday', 'starts_at', 'ends_at'],
      [index + 1, day, '08:00', '16:00']);
  }));
  nurses.forEach((_, index) => days.slice(0, 3 + index % 3).forEach((day) => {
    insert(db, 'nurse_shifts', ['nurse_id', 'weekday', 'starts_at', 'ends_at'],
      [index + 1, day, index % 2 ? '15:00' : '07:00', index % 2 ? '23:00' : '15:00']);
  }));
}
