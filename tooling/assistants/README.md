# Assistant config templates

`./gradlew configure` renders each file here to its place in the repo,
replacing `__UNLEASH_URL__` and `__UNLEASH_PAT__` with the values from
`.env`. The rendered files hold your token and are gitignored.

| Template | Rendered to | Assistant |
|----------|-------------|-----------|
| `claude.mcp.json` | `.mcp.json` | Claude Code |
| `cursor.mcp.json` | `.cursor/mcp.json` | Cursor |
| `vscode.mcp.json` | `.vscode/mcp.json` | VS Code Copilot |
| `opencode.json` | `opencode.json` | OpenCode |
| `codex.config.toml` | `.codex/config.toml` | Codex |
| `gemini.settings.json` | `.gemini/settings.json` | Gemini CLI |

## MCP version

These templates point at the remote MCP server on your Unleash instance. The
workshop expects MCP specification v1 and the tools from
[unleash-mcp v0.5.0](https://github.com/Unleash/unleash-mcp/tree/v0.5.0), so
the instance must have version 1 enabled.

## Run the MCP server locally

If the instance does not serve v1, run the same release on your machine. It
needs Node.js 22 or newer. Replace the `unleash` entry in your rendered
config with this stdio entry, using the values from `.env`:

```json
{
  "mcpServers": {
    "unleash": {
      "command": "npx",
      "args": ["-y", "@unleash/mcp@0.5.0", "--log-level", "error"],
      "env": {
        "UNLEASH_BASE_URL": "<UNLEASH_URL from .env>",
        "UNLEASH_PAT": "<UNLEASH_PAT from .env>",
        "UNLEASH_DEFAULT_PROJECT": "<your project id>"
      }
    }
  }
}
```

Keep the version pinned. `@latest` may ship tools this workbook does not use.
