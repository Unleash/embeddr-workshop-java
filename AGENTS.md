# Embeddr

A dating site for AI agents. Spring Boot backend, Vite + React + Tailwind
CSS v4 frontend in `web/`. This is a workshop codebase used to teach runtime
control with Unleash and an AI assistant.

## Project structure

- `src/main/java/io/getunleash/embeddr/flags/`: the only Unleash integration
  point. `Flags` and `Metrics` hold the names, `FeatureFlags` is the
  interface (`isEnabled`, `variant`, `increment`), `Visitor` is the context
  (session id, optional user id), `UnleashFeatureFlags` talks to the SDK,
  `OfflineFeatureFlags` is used when no connection is configured.
- `src/main/java/io/getunleash/embeddr/api/`: one controller per endpoint.
  Request and response bodies are records nested in the controller.
- `src/main/java/io/getunleash/embeddr/content/`: canned content banks.
- `src/main/java/io/getunleash/embeddr/payment/`: the payment provider.
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
- To gate a feature: add the flag's name to `Flags`, inject `FeatureFlags`
  into a controller, call
  `flags.isEnabled(Flags.THE_FLAG, new Visitor(sessionId, userId))`, and
  return the feature data or null. The frontend renders what it receives and
  renders nothing when the field is null.
- Never import `io.getunleash.*` outside the `flags` package. Controllers
  depend on the `FeatureFlags` interface, which is what makes them testable
  with `StubFeatureFlags` in the tests.
- Naming: every flag name lives in `flags/Flags.java` and every impact
  metric name in `flags/Metrics.java`, as constants spelled exactly as in
  Unleash. Flag names are unique across the
  shared workshop instance, so a new flag is named with the attendee's
  project id, `premium-upsell-<project id>`, created under that name in
  Unleash and added to `Flags.java` under that exact name. Nothing in the
  code adds to or rewrites a name.
- Every change in this repo runs through the Unleash MCP server, not the
  Unleash API or UI. Start with `evaluate_change` for a risk read, reuse any
  flag `detect_flag` surfaces, create new ones with `create_flag`, and guard
  code with `wrap_change`. Retire flags with `cleanup_flag`.
- New user-facing features ship behind a `release` flag, off by default.
  One flag per feature: the release flag is also how the feature gets
  turned off if a dependency misbehaves, so do not add a separate kill
  switch unless the attendee asks for one.
- Communicate the tool trail: in the final report, name each Unleash MCP
  tool that was called and what it did.

## Payment provider

- `payment/PaymentProvider.java` is Embeddr's payment provider, and it is
  down on purpose: `charge(sessionId)` waits about a second and returns a
  receipt with `charged` false. Nothing calls it until Premium is built.
  The malfunction is part of the workshop script, so never fix it, stub a
  success path, or route around it inside the provider.
- Embeddr Premium is intentionally absent from this codebase. It gets built
  live during the workshop, behind a release flag. Its upgrade flow is a new
  endpoint that injects `PaymentProvider` and calls `charge`.
- The frontend's `post()` in `web/src/lib/api.js` returns null for any
  non-2xx response. An upgrade endpoint should therefore answer 200 with a
  `status` field, for example `charged` or `failed`, so the component can
  tell the two cases apart and show the right copy.

## Conventions

- Java 17 language level, Spring Boot, constructor injection, records for request and
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
- The project id is the part before the colon in `UNLEASH_BACKEND_TOKEN` in
  `.env`. Pass it as `projectId` on every MCP call. Do not stop to ask which
  project to use.
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
