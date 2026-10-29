# Embeddr

A dating site for AI agents. Spring Boot backend, Vite + React + Tailwind
CSS v4 frontend in `web/`. This is a workshop codebase used to teach runtime
control with Unleash and an AI assistant.

## Project structure

- `src/main/java/io/getunleash/embeddr/flags/`: the only Unleash integration
  point. `FeatureFlags` is the interface (`isEnabled`, `variant`,
  `increment`), `Visitor` is the context (session id, optional user id),
  `UnleashFeatureFlags` talks to the SDK, `OfflineFeatureFlags` is used when
  no connection is configured.
- `src/main/java/io/getunleash/embeddr/api/`: one controller per endpoint.
  Request and response bodies are records nested in the controller.
- `src/main/java/io/getunleash/embeddr/content/`: canned content banks.
- `web/src/App.jsx`: page layout (Header, Hero, MatchGrid, footer).
- `web/src/components/`: all UI components, one component per file.
- `web/src/lib/api.js`: the backend client. Generates the per-browser
  session id and sends it with every request.
- `web/src/index.css`: Tailwind v4 theme tokens (colors: ink, surface, rose,
  peach, lavender, cream, muted; fonts: display, body, mono).

## Feature flags

- Flags are evaluated in the backend only. The frontend has no Unleash SDK
  and no token. It asks the backend what to show (see `web/src/lib/api.js`)
  and renders the response; polling makes flag changes appear within a few
  seconds without a reload.
- To gate a feature: inject `FeatureFlags` into a controller, call
  `flags.isEnabled("flag-name", new Visitor(sessionId, userId))`, and return
  the feature data or null. The frontend renders what it receives and
  renders nothing when the field is null.
- Never import `io.getunleash.*` outside the `flags` package. Controllers
  depend on the `FeatureFlags` interface, which is what makes them testable
  with `StubFeatureFlags` in the tests.
- Naming: the code uses short kebab-case names like `premium-upsell`. The
  wrapper appends `-<suffix>` with `UNLEASH_FLAG_SUFFIX` from `.env`, which
  is the attendee's Unleash project id, because flag names are unique across
  the shared instance. So: write `premium-upsell` in Java, and create the
  flag in Unleash as `premium-upsell-<suffix>` in the project whose id equals
  the suffix. Never put the suffix in Java code.
- Every change in this repo runs through the Unleash MCP server, not the
  Unleash API or UI. Start with `evaluate_change` for a risk read, reuse any
  flag `detect_flag` surfaces, create new ones with `create_flag`, and guard
  code with `wrap_change`. Retire flags with `cleanup_flag`.
- New user-facing features ship behind a `release` flag, off by default.
  Kill switches for dependencies are `operational` flags, off by default,
  where on means "bypass the dependency".
- Communicate the tool trail: in the final report, name each Unleash MCP
  tool that was called and what it did.

## Payment provider

- `PaymentController` is Embeddr's payment provider, and it is down on
  purpose. POST `/payment` takes a `sessionId`, waits about a second, and
  returns a 502. The malfunction is part of the workshop script, so never
  fix it, stub a success path, or route around it inside that controller.
- Embeddr Premium is intentionally absent from this codebase. It gets built
  live during the workshop, behind a release flag, with its upgrade flow
  charging through POST `/payment`. A kill switch for the provider, when
  asked for, lives in the code that calls the provider, not in the provider.

## Conventions

- Java 21, Spring Boot, constructor injection, records for request and
  response bodies. No Lombok, no extra starters, no new dependencies.
- Keep controllers small: validate the session id with
  `Requests.requireSessionId`, ask the wrapper, return a record.
- Fixed content returns inline from the controller. Only add to `content/`
  when the copy has many variants, like `Openers`.
- Frontend: Tailwind utility classes only, using the theme tokens in
  `web/src/index.css`. No new CSS files, no inline style objects except for
  dynamic gradients. New UI features get their own component file.
- Match the existing voice in any copy: playful, dry, machine learning puns
  welcome. No exclamation marks, no em dashes.

## Dev loop and workshop constraints

This repo is used in a room full of attendees, each running their own copy.
The assistant creating the flag and wrapping the code is the exercise.
Predictability beats thoroughness.

- The attendee runs `./gradlew bootRun`, which serves the app on port 8080.
  Never start a server, run `bootRun`, or use another port.
- After changing Java, tell the attendee to restart `bootRun`. There is no
  hot reload, and that is fine.
- After changing anything under `web/`, run `./gradlew web`. It is a build,
  not a server; the running app serves the new files on reload.
- Create flags off and leave them off. The attendee does the flip. Never
  enable an environment, add a strategy, or toggle anything unless the
  attendee explicitly asks for that step.
- The project id is the value of `UNLEASH_FLAG_SUFFIX` in `.env`. Pass it as
  `projectId` on every MCP call. Do not stop to ask which project to use.
- No screenshots, no installing tooling, no formatters. Match the style of
  the file you are editing.
- Smallest set of files. Verification is the attendee's job: when confident
  the change works, finish and hand it over. At most one quick curl of the
  live path when genuinely unsure. Never hardcode a flag result to test the
  other path.
- The frontend only shows what survives its polling guards. When a change
  alters the contents of a payload the frontend already receives, trace the
  field from controller to component and confirm every stability or memo
  check passes content changes through, not just presence changes.
- Report in a few lines, naming the Unleash MCP tools used, no process
  narration.
