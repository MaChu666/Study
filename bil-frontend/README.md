# BilBil Frontend

Vue 3 + Vite 5 foreground MVP for the BilBil UGC video platform.

## Stack

- Vue 3 with `<script setup>`
- Element Plus
- Pinia
- Vue Router
- Axios
- Mitt
- Vitest

## Commands

```bash
npm install
npm run dev
npm run test:unit
npm run build
```

## Environment

Copy `.env.example` to `.env.local` when local API settings differ.

```text
VITE_API_BASE_URL=/api
VITE_TOKEN_HEADER=thoken
VITE_API_PROXY_TARGET=http://localhost:7071
VITE_API_PROXY_REWRITE=true
```

The Vite dev server proxies `/api` to `VITE_API_PROXY_TARGET`.
Keep `VITE_API_PROXY_REWRITE=true` for the current backend where frontend `/api/account/login` should become backend `/account/login`.
Set `VITE_API_PROXY_REWRITE=false` only if the backend itself is mounted under `/api`.
