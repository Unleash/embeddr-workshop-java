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
