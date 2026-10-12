/** Public metadata is also the single source of truth for API field validation. */
const text = (key, label, required = true, max = 120) => ({ key, label, type: 'text', required, max });
const date = (key, label, required = true) => ({ key, label, type: 'date', required });
const select = (key, label, options, required = true) => ({ key, label, type: 'select', options, required });
const relation = (key, label, resource, required = true) => ({ key, label, type: 'relation', resource, required });
const number = (key, label, min = 0, max = 100000) => ({ key, label, type: 'number', min, max, required: true });
const time = (key, label) => ({ key, label, type: 'time', required: true });
const notes = (key, label, required = false) => ({ key, label, type: 'textarea', required, max: 500 });

export const catalog = {
  patients: {
    label: 'Patients', singular: 'Patient', group: 'People', icon: 'patients',
    description: 'A complete directory of patient information and admission status.',
    columns: ['first_name', 'last_name', 'sex', 'date_of_birth', 'contact_no'],
    fields: [text('first_name', 'First name'), text('last_name', 'Last name'), date('date_of_birth', 'Date of birth'),
      select('sex', 'Sex', ['F', 'M']), text('contact_no', 'Contact number', true, 35)],
  },
  physicians: {
    label: 'Physicians', singular: 'Physician', group: 'People', icon: 'staff',
    description: 'Care providers, their specialties, and contact details.',
    columns: ['first_name', 'last_name', 'specialty', 'contact_no'],
    fields: [text('first_name', 'First name'), text('last_name', 'Last name'), text('specialty', 'Specialty'),
      text('contact_no', 'Contact number', true, 35)],
  },
  nurses: {
    label: 'Nurses', singular: 'Nurse', group: 'People', icon: 'staff',
    description: 'Nursing staff and points of contact.',
    columns: ['first_name', 'last_name', 'contact_no'],
    fields: [text('first_name', 'First name'), text('last_name', 'Last name'), text('contact_no', 'Contact number', true, 35)],
  },
  wards: {
    label: 'Wards', singular: 'Ward', group: 'Operations', icon: 'ward',
    description: 'Hospital units, bed capacity, and occupancy.',
    columns: ['name', 'floor', 'category', 'capacity'],
    fields: [text('name', 'Ward name'), text('floor', 'Floor', true, 24),
      select('category', 'Unit type', ['General', 'Emergency', 'ICU', 'Pediatrics', 'Surgical', 'Maternity']),
      number('capacity', 'Bed capacity', 1, 100)],
  },
  illnesses: {
    label: 'Conditions', singular: 'Condition', group: 'Clinical', icon: 'clinical',
    description: 'The reference catalog of diagnoses and conditions.',
    columns: ['name', 'category', 'description'],
    fields: [text('name', 'Condition'), text('category', 'Category'), notes('description', 'Description')],
  },
  medicines: {
    label: 'Pharmacy', singular: 'Medication', group: 'Clinical', icon: 'pharmacy',
    description: 'Medication inventory, current stock, and reorder thresholds.',
    columns: ['name', 'category', 'stock_qty', 'reorder_level', 'unit'],
    fields: [text('name', 'Medication'), text('category', 'Category'), text('unit', 'Unit', true, 30),
      number('stock_qty', 'Stock on hand'), number('reorder_level', 'Reorder at')],
  },
  admissions: {
    label: 'Admissions', singular: 'Admission', group: 'Operations', icon: 'admission',
    description: 'Patient admissions and assigned hospital units.',
    columns: ['patient_id', 'ward_id', 'admitted_on', 'reason'],
    fields: [relation('patient_id', 'Patient', 'patients'), relation('ward_id', 'Ward', 'wards'),
      date('admitted_on', 'Admission date'), notes('reason', 'Reason')],
  },
  discharges: {
    label: 'Discharges', singular: 'Discharge', group: 'Operations', icon: 'admission',
    description: 'Discharge outcomes and patient transitions.',
    columns: ['admission_id', 'discharged_on', 'disposition'],
    fields: [relation('admission_id', 'Admission', 'admissions'), date('discharged_on', 'Discharge date'),
      select('disposition', 'Disposition', ['Recovered', 'Home care', 'Transferred', 'Other']),
      notes('notes', 'Notes')],
  },
  diagnoses: {
    label: 'Diagnoses', singular: 'Diagnosis', group: 'Clinical', icon: 'clinical',
    description: 'Conditions recorded by physicians for individual patients.',
    columns: ['patient_id', 'physician_id', 'illness_id', 'diagnosed_on'],
    fields: [relation('patient_id', 'Patient', 'patients'), relation('physician_id', 'Physician', 'physicians'),
      relation('illness_id', 'Condition', 'illnesses'), date('diagnosed_on', 'Diagnosis date'), notes('notes', 'Notes')],
  },
  treatments: {
    label: 'Treatments', singular: 'Treatment', group: 'Clinical', icon: 'clinical',
    description: 'Care performed by clinicians and medication administered.',
    columns: ['patient_id', 'procedure_name', 'physician_id', 'performed_on'],
    fields: [relation('patient_id', 'Patient', 'patients'), relation('diagnosis_id', 'Diagnosis', 'diagnoses', false),
      relation('physician_id', 'Physician', 'physicians', false), relation('nurse_id', 'Nurse', 'nurses', false),
      relation('medicine_id', 'Medication', 'medicines', false), date('performed_on', 'Treatment date'),
      text('procedure_name', 'Procedure'), notes('notes', 'Notes')],
  },
  nurse_assignments: {
    label: 'Nurse assignments', singular: 'Nurse assignment', group: 'Operations', icon: 'staff',
    description: 'Assignments connecting nursing staff to patients.',
    columns: ['nurse_id', 'patient_id', 'assigned_on', 'ends_on'],
    fields: [relation('nurse_id', 'Nurse', 'nurses'), relation('patient_id', 'Patient', 'patients'),
      date('assigned_on', 'Assigned on'), date('ends_on', 'Until', false)],
  },
  physician_schedules: {
    label: 'Physician schedules', singular: 'Physician schedule', group: 'Operations', icon: 'calendar',
    description: 'Weekly physician availability and working hours.',
    columns: ['physician_id', 'weekday', 'starts_at', 'ends_at'],
    fields: [relation('physician_id', 'Physician', 'physicians'),
      select('weekday', 'Day', ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday']),
      time('starts_at', 'Start time'), time('ends_at', 'End time')],
  },
  nurse_shifts: {
    label: 'Nurse shifts', singular: 'Nurse shift', group: 'Operations', icon: 'calendar',
    description: 'Weekly nurse rosters and shift coverage.',
    columns: ['nurse_id', 'weekday', 'starts_at', 'ends_at'],
    fields: [relation('nurse_id', 'Nurse', 'nurses'),
      select('weekday', 'Day', ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday']),
      time('starts_at', 'Start time'), time('ends_at', 'End time')],
  },
};

export function publicCatalog() {
  return Object.fromEntries(Object.entries(catalog).map(([key, value]) => [key, value]));
}
