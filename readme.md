# Documentation References

- Node.js File System API (`fs.readFile`): https://nodejs.org/api/fs.html#fsreadfilepath-options-callback

## Supported Encodings for `fs.readFile`

When reading files as strings using `fs.readFile`, Node.js accepts any valid Buffer character encoding (case-insensitive):

- `utf8` (or `utf-8`) - Default for string operations
- `utf16le` (or `utf-16le`, `ucs2`, `ucs-2`)
- `latin1` (or `binary`)
- `base64`
- `base64url`
- `hex`
- `ascii`

If no encoding or `null` is specified, `fs.readFile` returns the raw `Buffer`.

