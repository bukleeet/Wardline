import { readFile, writeFile } from 'node:fs/promises';

const stylesheet = new URL('../public/styles.css', import.meta.url);
const check = process.argv.includes('--check');

/** Format this project's plain CSS without changing selectors or property values. */
function formatCss(source) {
  const lines = [];
  let depth = 0;
  let parentheses = 0;
  let brackets = 0;
  let quote = null;
  let escaped = false;
  let inComment = false;
  let token = '';

  function emit(suffix = '') {
    const text = token.trim();
    if (text) {
      const declaration = suffix === ';' ? text.replace(/^([\w-]+):\s*/, '$1: ') : text;
      lines.push(`${'  '.repeat(depth)}${declaration}${suffix}`);
    }
    token = '';
  }

  for (let index = 0; index < source.length; index += 1) {
    const char = source[index];
    const next = source[index + 1];
    if (inComment) {
      token += char;
      if (char === '*' && next === '/') { token += next; index += 1; inComment = false; }
      continue;
    }
    if (quote) {
      token += char;
      if (escaped) escaped = false;
      else if (char === '\\') escaped = true;
      else if (char === quote) quote = null;
      continue;
    }
    if (char === '/' && next === '*') { token += '/*'; index += 1; inComment = true; continue; }
    if (char === '"' || char === "'") { token += char; quote = char; continue; }
    if (char === '(') parentheses += 1;
    if (char === ')') parentheses -= 1;
    if (char === '[') brackets += 1;
    if (char === ']') brackets -= 1;
    if (parentheses === 0 && brackets === 0 && char === '{') {
      emit(' {');
      depth += 1;
    } else if (parentheses === 0 && brackets === 0 && char === ';') {
      emit(';');
    } else if (parentheses === 0 && brackets === 0 && char === '}') {
      emit(depth > 0 ? ';' : '');
      depth -= 1;
      if (depth < 0) throw new Error('Unexpected closing CSS brace');
      lines.push(`${'  '.repeat(depth)}}`);
      if (depth === 0) lines.push('');
    } else {
      token += char;
    }
  }
  if (depth !== 0 || quote || inComment || parentheses || brackets) {
    throw new Error('Unbalanced CSS syntax');
  }
  emit();
  return `${lines.join('\n').trimEnd()}\n`;
}

const source = await readFile(stylesheet, 'utf8');
const formatted = formatCss(source);
if (check) {
  if (source.replace(/\r\n/g, '\n') !== formatted) {
    console.error('Stylesheet needs formatting. Run npm run format:css.');
    process.exitCode = 1;
  }
} else {
  await writeFile(stylesheet, formatted);
}
