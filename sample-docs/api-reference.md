# API Reference (v2)

## Authentication
All requests need an API key in the `Authorization: Bearer <key>` header. Create keys under
**Account > Developer > API keys**. Keys are shown only once, so store them securely.

## Rate limits
- Free: 60 requests per minute
- Pro: 600 requests per minute
- Business: 3,000 requests per minute

Requests over the limit receive HTTP 429 with a `Retry-After` header giving the number of seconds to wait.

## Endpoints
- `GET /v2/files` lists files. Supports `?cursor=` pagination; each page has up to 100 items.
- `POST /v2/files` uploads a file (multipart form field `file`, maximum 5 GB).
- `DELETE /v2/files/{id}` moves a file to the trash. Trashed files are purged after 30 days.

## Webhooks
Register a webhook URL under **Account > Developer > Webhooks**. Events are signed with an HMAC-SHA256
signature in the `X-Acme-Signature` header; verify it with your webhook secret.
