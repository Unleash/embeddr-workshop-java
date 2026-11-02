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

Prerequisite: a JDK, version 17 or newer. Gradle uses the one you have and
downloads its own Node for the frontend into the project folder, so nothing
is installed system-wide. You also need an Unleash instance with a Backend
token.

```bash
cp .env.example .env        # then fill in the instance URL and backend token
./gradlew precheck          # JDK, frontend build, tokens
./gradlew bootRun           # http://localhost:8080
```

To connect an AI assistant, add your personal access token to `.env` and
run `./gradlew configure`, which renders the MCP config for every supported
assistant.

On Windows run these in Git Bash, which comes with Git for Windows, and they work as written. In PowerShell or cmd use `.\gradlew.bat` in place of `./gradlew`.

Without a `.env` the app still boots, every flag evaluates to off, and the
app renders the plain Embeddr experience.

After a change to Java code, stop `bootRun` with Ctrl-C and start it again.
After a change under `web/`, run `./gradlew web` in a second terminal; the
running app serves the new build, reload the browser.

## The flags

Every flag is evaluated in the backend with the session id as context.

Every flag name the app uses is a constant in
[`flags/Flags.java`](src/main/java/io/getunleash/embeddr/flags/Flags.java),
and every impact metric name in
[`flags/Metrics.java`](src/main/java/io/getunleash/embeddr/flags/Metrics.java),
spelled exactly as in Unleash. Names are unique across a whole instance, so
in the workshop each attendee creates their flags with their project id in
the name, `early-access-melinda-f`, and puts that name in `Flags.java`.

| Flag | What it does | Setup |
|------|--------------|-------|
| `early-access` | The waitlist. Off shows the coming soon page to everyone; the invite code a visitor types is the Unleash userId. Wired up by hand in part 2 of the workshop. | Release flag. Target a single invite code to let one person in, then a gradual rollout with userId stickiness to open the doors in slices. |
| `light-mode` | Puts a theme toggle in the header. The light or dark choice is the user's own, per session; the flag only decides whether the control exists. | Release flag, gradual rollout at 100%, no targeting. |
| `match-layout-experiment` | A/B on how matches are browsed: variant `list` keeps the card grid, variant `deck` shows one profile at a time. Match taps feed the `match_count_list` and `match_count_deck` impact metrics. | Experiment flag, gradual rollout at 100% with variants `list` and `deck` at 50/50, default stickiness. |
| `auto-rizz` | Serves an opening line on every match card. Ick taps feed the `thumbs_down_count` impact metric that a safeguard watches. | Release flag with a release plan from the progressive percentage-based rollout template, walked to the 100% milestone. Safeguard on the metric, optional. |

Embeddr Premium is intentionally absent. It gets built live during the
workshop, behind a flag the assistant creates, against a payment provider
that is down on purpose.

## The pieces

| Path | What it is |
|------|------------|
| [`src/main/java/io/getunleash/embeddr/flags/`](src/main/java/io/getunleash/embeddr/flags/) | The flag wrapper. `Flags` and `Metrics` hold every flag and metric name. `FeatureFlags` is the interface controllers use. `UnleashFeatureFlags` is the only class that imports the Unleash SDK. `OfflineFeatureFlags` answers when nothing is configured. |
| [`src/main/java/io/getunleash/embeddr/api/`](src/main/java/io/getunleash/embeddr/api/) | One controller per endpoint. Each asks the wrapper a question and returns what the frontend should show. |
| [`src/main/java/io/getunleash/embeddr/content/`](src/main/java/io/getunleash/embeddr/content/) | The opener line bank. |
| [`src/main/java/io/getunleash/embeddr/payment/`](src/main/java/io/getunleash/embeddr/payment/) | The payment provider, down on purpose. |
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

Every POST takes `{ sessionId, userId? }` plus endpoint fields and answers
400 without a session id.

## Deploy

The `Dockerfile` builds frontend and backend into one jar; the container
serves both from one port. Set `UNLEASH_URL` and `UNLEASH_BACKEND_TOKEN`. `railway.json` wires it up for Railway;
`PORT` comes from the platform.

## Behind a corporate proxy

The Gradle wrapper, Node and npm each download something on first run. Point them at
your proxy or mirrors:

- Gradle: `systemProp.https.proxyHost` and `systemProp.https.proxyPort` in
  `~/.gradle/gradle.properties`.
- Node download: set `distBaseUrl` in the `node` block of `build.gradle` to
  an internal mirror of nodejs.org/dist.
- npm: `registry=` in `web/.npmrc`.

