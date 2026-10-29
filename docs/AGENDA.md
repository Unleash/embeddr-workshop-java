# Run of show: Runtime Control with AI, Java edition

Three hours. The arc goes from one person with a flag to a system that
protects itself, and ends on governance. Every hands-on block has the same
rhythm: a short room moment on the hosted app with phones out, then
everyone builds the same thing in their own project against their local
app, then a checkpoint in the workbook.

| Time | Block | Mode | Min | Workbook |
|------|-------|------|-----|----------|
| 0:00 | Open: early access. Room scans the QR, lands on the waitlist, types a code. Enable the flag for yourself in production, target one person's code, roll out in slices and check in with the room as phones pop through the door. | Presenter | 15 | |
| 0:15 | The arc. One person with a flag, a system that protects itself, governance. Light mode as the one-liner: the flag gates the control, not the state. | Presenter | 5 | |
| 0:20 | Setup: tokens, `.env`, `precheck`, `configure`, `bootRun`. | Hands-on | 20 | Part 1 |
| 0:40 | First flag by hand: wire `early-access`, let yourself in by user id. | Hands-on | 15 | Part 2 |
| 0:55 | AI killswitch: the assistant builds Premium plus the payment kill switch. Release, break, rescue, kill. | Hands-on | 35 | Part 3 |
| 1:30 | Break | | 10 | |
| 1:40 | Experiment. Room moment: everyone taps Match on the hosted app and the two counters diverge. Then each attendee runs the experiment in their project. | Hands-on | 25 | Part 4 |
| 2:05 | Safeguard. Room moment: everyone taps Ick, the safeguard trips, the flag turns itself off. Then each attendee trips their own. | Hands-on | 25 | Part 5 |
| 2:30 | Governance. Pairs approve each other's change request for light mode. | Hands-on | 15 | Part 6 |
| 2:45 | Retire Premium. Lifecycle, cleanup, the assistant removes the flag, archive. | Hands-on | 10 | Part 7 |
| 2:55 | Close. What they built, where to go next. | Presenter | 5 | |

If the room runs late, drop part 7 first, then make part 6 a presenter-led
demo.

## Preparation checklist

### Unleash instance

- One project per attendee, named first name and last initial, with the
  attendee invited as a member. The project id becomes their flag suffix.
- Optional: pre-create each attendee's development backend token and put it
  in the invitation. The personal access token stays self-service.
- Change requests enabled on the `development` environment of every
  attendee project, one approval required, for part 6. Attendees cannot
  approve their own requests, so pair them and give each attendee a role in
  their partner's project that can approve.
- Impact metrics and safeguards available on the instance. Verified
  2026-10-29: counters reported by the Java SDK 12.3.1 appear in the
  instance within one metrics interval.
- The `embeddr-demo` project with the hosted app's flags under their short
  names: `early-access`, `light-mode`, `match-layout-experiment`,
  `auto-rizz`, plus the pre-built `embeddr-premium-upsell` and
  `payment-provider-kill-switch` for the opening, if used there.

### Hosted app

- Deploy this repo with the part 2 line already wired, against the
  `embeddr-demo` project with an empty `UNLEASH_FLAG_SUFFIX`, and a low
  `UNLEASH_METRICS_INTERVAL_SECONDS`.
- The QR in `web/public/qr.png` and the join text in `AvatarQr.jsx` point at
  the hosted URL. Update both if the URL changes.

### Attendee pre-check

Put in the invitation: git, a JDK 17 or newer, an MCP-capable assistant,
and the three lines from workbook part 0. The pre-check downloads Gradle,
the JDK 21 toolchain, Node and the frontend dependencies, so the venue
wifi only has to carry flag fetches on the day.

### Room

- Presenter machine with the hosted app, the Unleash UI on the
  `embeddr-demo` project, and a terminal on this repo.
- The fallback commands in `FALLBACKS.md` ready in a terminal, with the
  variables set for a spare project.
- A spare attendee project or two for people whose invitation went astray.
