# Embeddr

Dating for AI agents. Find your nearest neighbor.

A workshop app for runtime control with [Unleash](https://www.getunleash.io)
and an AI coding assistant, in Java.

The backend is a small Spring Boot service that evaluates every feature flag
server-side through one wrapper, serves Auto-Rizz opening lines behind the
`auto-rizz` flag, and reports Ick taps to Unleash as an impact metric. The
frontend (Vite, React, Tailwind CSS v4) holds no flag logic: it renders
whatever the backend returns and polls, so turning a flag off stops real
server-side work and the running app reflects it within seconds, without a
redeploy.

## Run it

Prerequisite: a JDK, version 17 or newer, to run the Gradle wrapper. Gradle
downloads the JDK 21 toolchain the app compiles with, and its own Node for
the frontend. You also need an Unleash instance with a Backend token.

```bash
cp .env.example .env        # then fill in the four values
./gradlew precheck          # toolchain, frontend build, tokens
./gradlew configure         # renders the MCP config for every assistant
./gradlew bootRun           # http://localhost:8080
```

On Windows use `gradlew.bat` in place of `./gradlew`.

Without a `.env` the app still boots, every flag evaluates to off, and the
app renders the plain Embeddr experience.

After a change to Java code, stop `bootRun` with Ctrl-C and start it again.
After a change under `web/`, run `./gradlew web` in a second terminal; the
running app serves the new build, reload the browser.

## The flags

Every flag is evaluated in the backend with the session id as context.

Flag names are unique across a whole Unleash instance, and the workshop
instance hosts one project per attendee, so every flag the app evaluates is
named `<flag>-<suffix>` with `UNLEASH_FLAG_SUFFIX` from `.env`. The code uses
the short names below; the wrapper adds the suffix. Impact metrics get the
same suffix with underscores: `thumbs_down_count_melinda_f`.

| Flag | What it does | Setup |
|------|--------------|-------|
| `early-access` | The waitlist. Off shows the coming soon page to everyone; the invite code a visitor types is the Unleash userId. Wired up by hand in part 2 of the workshop. | Release flag. Target a single invite code to let one person in, then a gradual rollout with userId stickiness to open the doors in slices. |
| `light-mode` | Puts a theme toggle in the header. The light or dark choice is the user's own, per session; the flag only decides whether the control exists. | Release flag, gradual rollout at 100%, no targeting. |
| `match-layout-experiment` | A/B on how matches are browsed: variant `list` keeps the card grid, variant `deck` shows one profile at a time. Match taps feed the `match_count_list` and `match_count_deck` impact metrics. | Experiment flag, gradual rollout at 100% with variants `list` and `deck` at 50/50, default stickiness. |
| `auto-rizz` | Serves an opening line on every match card. Ick taps feed the `thumbs_down_count` impact metric that a safeguard watches. | Release flag, gradual rollout at 100%, default stickiness. Safeguard on the metric, optional. |

Embeddr Premium is intentionally absent. It gets built live during the
workshop, behind flags the assistant creates, against a payment provider
that is down on purpose.

## The pieces

| Path | What it is |
|------|------------|
| [`src/main/java/io/getunleash/embeddr/flags/`](src/main/java/io/getunleash/embeddr/flags/) | The flag wrapper. `FeatureFlags` is the interface controllers use. `UnleashFeatureFlags` is the only class that imports the Unleash SDK. `OfflineFeatureFlags` answers when nothing is configured. |
| [`src/main/java/io/getunleash/embeddr/api/`](src/main/java/io/getunleash/embeddr/api/) | One controller per endpoint. Each asks the wrapper a question and returns what the frontend should show. |
| [`src/main/java/io/getunleash/embeddr/content/`](src/main/java/io/getunleash/embeddr/content/) | The opener line bank. |
| [`web/`](web/) | The frontend. No Unleash SDK, no flag logic, no token. Built by Gradle into `web/dist`. |
| [`tooling/assistants/`](tooling/assistants/) | MCP config templates, rendered by `./gradlew configure`. |
| [`WORKBOOK.md`](WORKBOOK.md) | The attendee workbook. Start here at the workshop. |
| [`FALLBACKS.md`](FALLBACKS.md) | Plain commands for every assistant-driven step. |
| [`docs/AGENDA.md`](docs/AGENDA.md) | Run of show and preparation checklist for presenters. |
| [`AGENTS.md`](AGENTS.md), [`CLAUDE.md`](CLAUDE.md) | Guidance for AI assistants working in this repo. |

## Endpoints

| Endpoint | Flag | Returns |
|----------|------|---------|
| `GET /health` | | `{ ok: true }` |
| `POST /access` | `early-access` | `{ access }` |
| `POST /theme` | `light-mode` | `{ themeToggle }` |
| `POST /layout` | `match-layout-experiment` | `{ layout }`, the variant name or null |
| `POST /match` | `match-layout-experiment` | counts the tap under the session's variant |
| `POST /opener` | `auto-rizz` | `{ opener }`, a line or null |
| `POST /ick` | | counts a `thumbs_down_count` tap |
| `POST /payment` | | 502 after 900 ms, always |

Every POST takes `{ sessionId, userId? }` plus endpoint fields and answers
400 without a session id.

## Deploy

The `Dockerfile` builds frontend and backend into one jar; the container
serves both from one port. Set `UNLEASH_URL`, `UNLEASH_BACKEND_TOKEN` and,
for a project that owns the flags under their short names, leave
`UNLEASH_FLAG_SUFFIX` empty. `railway.json` wires it up for Railway;
`PORT` comes from the platform.

## Behind a corporate proxy

Gradle, Node and npm each download something on first run. Point them at
your proxy or mirrors:

- Gradle: `systemProp.https.proxyHost` and `systemProp.https.proxyPort` in
  `~/.gradle/gradle.properties`.
- Node download: set `distBaseUrl` in the `node` block of `build.gradle` to
  an internal mirror of nodejs.org/dist.
- npm: `registry=` in `web/.npmrc`.

Run `./gradlew precheck` once on a network that works and the downloads are
cached for the day.
