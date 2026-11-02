# Run of show: Runtime Control with AI, Java edition

Three hours. The arc goes from one person with a flag to a system that
protects itself, and ends on governance. Everything before the break is
Unleash UI and a few lines of Java; the assistant joins after it. Every hands-on block has the same
rhythm: a short room moment on the hosted app with phones out, then
everyone builds the same thing in their own project against their local
app, then a checkpoint in the workbook.

| Time | Block | Mode | Min | Workbook |
|------|-------|------|-----|----------|
| 0:00 | Open: early access. Room scans the QR, lands on the waitlist, types a code. Enable the flag for yourself in production, target one person's code, roll out in slices and check in with the room as phones pop through the door. | Presenter | 15 | |
| 0:15 | The arc. One person with a flag, a system that protects itself, governance. Light mode as the one-liner: the flag gates the control, not the state. | Presenter | 5 | |
| 0:20 | Setup, UI only: register, install, clone, `precheck`, then `.env` with instance URL and backend token, `bootRun`. No assistant yet. | Hands-on | 25 | Parts 0 and 1 |
| 0:45 | First flag by hand: wire `early-access`, let yourself in by user id, then open the door to everyone with a gradual rollout. | Hands-on | 15 | Part 2 |
| 1:00 | Experiment. Room moment: everyone taps Match on the hosted app and the two counters diverge. Then each attendee runs the experiment in their project. | Hands-on | 25 | Part 3 |
| 1:25 | Break | | 10 | |
| 1:35 | Connect the assistant (PAT, `configure`), then it builds Premium behind a release flag. Release, break, kill. | Hands-on | 35 | Part 4 |
| 2:10 | Safeguard. Room moment: everyone taps Ick, the safeguard trips, the flag turns itself off. Then each attendee trips their own. | Hands-on | 20 | Part 5 |
| 2:30 | Governance. Pairs add each other as Reviewer, then approve each other's change request for light mode. | Hands-on | 15 | Part 6 |
| 2:45 | Retire Premium. Lifecycle, cleanup, the assistant removes the flag, archive. | Hands-on | 10 | Part 7 |
| 2:55 | Close. What they built, where to go next. | Presenter | 5 | |

If the room runs late, drop part 7 first, then make part 6 a presenter-led
demo.

## Preparation checklist

### Unleash instance

- One project per attendee, named first name and last initial, with the
  attendee invited as its owner, which part 6 needs for assigning the
  reviewer role. Attendees put the project id in every flag name they
  create, since names are unique across the instance.
- Projects are created on the day from the registration form responses, so
  keep the form open on a second screen during part 0.
- Change requests are not pre-enabled. In part 6 each attendee turns them
  on for their own `development` environment with one required approval.
- A project role named `Reviewer` exists on the instance with the change
  request permissions, approve/reject and apply/reject. In part 6
  attendees pair up and each adds their neighbor to their own project with
  that role. Assigning roles needs project access rights, so attendees
  must be owners of their own project, not members.
- Impact metrics and safeguards available on the instance. Verified
  2026-10-29: counters reported by the Java SDK 12.3.1 appear in the
  instance within one metrics interval.
- The `embeddr-demo` project with the hosted app's flags under their short
  names: `early-access`, `light-mode`, `match-layout-experiment`,
  `auto-rizz`, plus the pre-built `embeddr-premium-upsell` for the
  opening, if used there.

### Hosted app

- The room moments run on the existing deployment of the Node edition,
  `embeddr-workshop`, against the `embeddr-demo` project. It has the same
  flags, endpoints and frontend as this repo, so what the room sees on
  their phones matches what attendees build locally. Nothing from this
  repo needs deploying; the `Dockerfile` here is optional.
- The QR in `web/public/qr.png` and the join text in `AvatarQr.jsx` point at
  that hosted URL. Update both if it changes.

### The first twenty minutes

Nothing happens before the day. Attendees register through the form at the
start of part 0, and someone creates their project and sends the invitation
while they install a JDK, clone and run the pre-check, which downloads
Gradle, Node and the frontend dependencies over the venue wifi. Budget for
that: the setup block is 25 minutes, and the first flag block can start
while the slowest downloads finish. Have the README's proxy section and
"turn the VPN off" ready as the first answer to a failed download.

### Room

- Presenter machine with the hosted app, the Unleash UI on the
  `embeddr-demo` project, and a terminal on this repo.
- The fallback commands in `FALLBACKS.md` ready in a terminal, with the
  variables set for a spare project.
- A spare attendee project or two for people whose invitation went astray.
