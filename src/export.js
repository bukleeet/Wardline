import { catalog } from './catalog.js';
import { AppError, listRecords } from './repository.js';

// Keep the local demo responsive if a user imports a much larger dataset.
export const MAX_EXPORT_ROWS = 5_000;

function csvCell(value) {
  if (value === null || value === undefined) return '';
  let text = String(value);
  // Spreadsheets can execute formulas when opening untrusted CSV values.
  if (/^[\s\x00-\x1f]*[=+\-@]/u.test(text)) text = `'${text}`;
  return `"${text.replaceAll('"', '""')}"`;
}

export function exportCsv(db, resource, { search = '' } = {}) {
  // listRecords validates the resource and search before either reaches SQL.
  const first = listRecords(db, resource, { search, pageSize: 100 });
  if (first.total > MAX_EXPORT_ROWS) {
    throw new AppError(422, `More than ${MAX_EXPORT_ROWS.toLocaleString('en-US')} matching records. Refine your search to export.`);
  }

  const fields = catalog[resource].fields;
  const headers = ['ID'];
  for (const field of fields) {
    headers.push(field.label);
    if (field.type === 'relation') headers.push(`${field.label} name`);
  }

  const lines = [headers.map(csvCell).join(',')];
  for (let page = 1; page <= first.pages; page += 1) {
    const rows = page === 1 ? first.rows : listRecords(db, resource, { search, page, pageSize: 100 }).rows;
    for (const row of rows) {
      const values = [row.id];
      for (const field of fields) {
        values.push(row[field.key]);
        if (field.type === 'relation') values.push(row[`${field.key}_label`]);
      }
      lines.push(values.map(csvCell).join(','));
    }
  }

  // BOM helps spreadsheet applications display UTF-8 names and symbols.
  return `\uFEFF${lines.join('\r\n')}\r\n`;
}
