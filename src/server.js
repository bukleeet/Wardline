import { createServer as createHttpServer } from 'node:http';
import { readFile } from 'node:fs/promises';
import { fileURLToPath } from 'node:url';
import { dirname, join, resolve } from 'node:path';
import { openDatabase } from './database.js';
import { publicCatalog } from './catalog.js';
import { AppError, createRecord, dashboard, deleteRecord, getRecord, listRecords, lookup, updateRecord } from './repository.js';

const publicPath = join(dirname(fileURLToPath(import.meta.url)), '..', 'public');
const staticFiles = {
  '/': ['index.html', 'text/html; charset=utf-8'],
  '/styles.css': ['styles.css', 'text/css; charset=utf-8'],
  '/app.js': ['app.js', 'text/javascript; charset=utf-8'],
  '/favicon.svg': ['favicon.svg', 'image/svg+xml'],
};

function headers(type = 'application/json; charset=utf-8') {
  return {
    'Content-Type': type,
    'X-Content-Type-Options': 'nosniff',
    'Referrer-Policy': 'no-referrer',
    'X-Frame-Options': 'DENY',
    'Permissions-Policy': 'camera=(), microphone=(), geolocation=()',
    'Content-Security-Policy': "default-src 'none'; script-src 'self'; style-src 'self'; img-src 'self' data:; font-src 'self'; connect-src 'self'; base-uri 'none'; form-action 'self'; frame-ancestors 'none'",
    'Cache-Control': 'no-store',
  };
}

function respond(res, status, body) {
  res.writeHead(status, headers());
  res.end(JSON.stringify(body));
}

async function readJson(req) {
  if (!req.headers['content-type']?.toLowerCase().startsWith('application/json')) {
    throw new AppError(415, 'Content-Type must be application/json');
  }
  const chunks = [];
  let length = 0;
  for await (const chunk of req) {
    length += chunk.length;
    if (length > 16384) throw new AppError(413, 'Request body exceeds 16 KB');
    chunks.push(chunk);
  }
  try { return JSON.parse(Buffer.concat(chunks).toString('utf8')); }
  catch { throw new AppError(400, 'Malformed JSON'); }
}

function sameOrigin(req) {
  if (req.headers['sec-fetch-site'] === 'cross-site') return false;
  const origin = req.headers.origin;
  if (!origin) return true;
  try {
    const parsed = new URL(origin);
    const scheme = req.socket?.encrypted ? 'https:' : 'http:';
    return parsed.protocol === scheme && parsed.host === req.headers.host;
  }
  catch { return false; }
}

function parsePage(value, fallback) {
  if (value === null) return fallback;
  if (!/^\d+$/.test(value)) throw new AppError(400, 'Invalid pagination parameter');
  return Number(value);
}

export function createServer({ db = openDatabase() } = {}) {
  const server = createHttpServer(async (req, res) => {
    try {
      const url = new URL(req.url, 'http://localhost');
      const path = url.pathname;
      if (path.startsWith('/api/')) {
        if (!sameOrigin(req)) throw new AppError(403, 'Cross-origin changes are not allowed');
        if (req.method === 'GET' && path === '/api/health') return respond(res, 200, { status: 'ok' });
        if (req.method === 'GET' && path === '/api/catalog') return respond(res, 200, publicCatalog());
        if (req.method === 'GET' && path === '/api/dashboard') return respond(res, 200, dashboard(db));

        const look = path.match(/^\/api\/lookup\/([a-z_]+)$/);
        if (req.method === 'GET' && look) return respond(res, 200, lookup(db, look[1], {
          search: url.searchParams.get('search') ?? '',
        }));

        const collection = path.match(/^\/api\/records\/([a-z_]+)$/);
        if (collection) {
          const resource = collection[1];
          if (req.method === 'GET') return respond(res, 200, listRecords(db, resource, {
            search: url.searchParams.get('search') ?? '',
            page: parsePage(url.searchParams.get('page'), 1),
            pageSize: parsePage(url.searchParams.get('pageSize'), 10),
          }));
          if (req.method === 'POST') return respond(res, 201, createRecord(db, resource, await readJson(req)));
        }

        const item = path.match(/^\/api\/records\/([a-z_]+)\/(\d+)$/);
        if (item) {
          const [, resource, idText] = item;
          const id = Number(idText);
          if (req.method === 'GET') return respond(res, 200, getRecord(db, resource, id));
          if (req.method === 'PATCH') return respond(res, 200, updateRecord(db, resource, id, await readJson(req)));
          if (req.method === 'DELETE') return respond(res, 200, deleteRecord(db, resource, id));
        }
        throw new AppError(404, 'Endpoint not found');
      }

      if (req.method !== 'GET' && req.method !== 'HEAD') throw new AppError(405, 'Method not allowed');
      const staticFile = staticFiles[path];
      if (!staticFile) throw new AppError(404, 'Not found');
      const bytes = await readFile(join(publicPath, staticFile[0]));
      res.writeHead(200, headers(staticFile[1]));
      if (req.method === 'HEAD') res.end(); else res.end(bytes);
    } catch (error) {
      if (!(error instanceof AppError)) {
        console.error('Request failed:', error);
      }
      if (!res.headersSent) respond(res, error instanceof AppError ? error.status : 500,
        { error: error instanceof AppError ? error.message : 'Unexpected server error' });
      else res.end();
    }
  });
  return server;
}

if (process.argv[1] && fileURLToPath(import.meta.url) === resolve(process.argv[1])) {
  const port = Number(process.env.PORT ?? 3000);
  if (!Number.isInteger(port) || port < 1 || port > 65535) {
    console.error('PORT must be between 1 and 65535');
    process.exitCode = 1;
  } else {
    createServer().listen(port, process.env.HOST || '127.0.0.1', () => {
      console.log(`Wardline demo ready at http://${process.env.HOST || '127.0.0.1'}:${port}`);
    });
  }
}
