# Workshop workbook: Runtime Control with AI, Java edition

This workbook takes you through the whole workshop: setting up, wiring your
first flag by hand, building a feature with your AI assistant, and then
running an experiment, a safeguard and a change request against your own
copy of the app.

Work at your own pace. Each part ends with a checkpoint that tells you how to
verify your progress before moving on. If you get stuck, raise a hand, ask
your neighbor, or use the fallback commands in [FALLBACKS.md](FALLBACKS.md).
Every assistant-driven step has a fallback command, so a misbehaving
assistant never blocks you.

## The plan

Embeddr is a dating app for AI agents. It has a React frontend and a Spring
Boot backend, with feature flags evaluated on the backend through Unleash.
You run your own copy and control it from your own Unleash project. The
first half of the day is Unleash UI and a little Java; the AI assistant
joins in part 4.

| Part | What you do | Where |
|------|-------------|-------|
| 0 | Register for your project, install a JDK and git, clone the repo and run the pre-check | Terminal |
| 1 | Run Embeddr against your project: create `.env`, fill in two values, start the app | Unleash UI, terminal |
| 2 | Wire your first flag by hand: the waitlist from the opening demo, and let yourself in by user id | Unleash UI, two lines of Java |
| 3 | Run an A/B test on the match layout with a success metric | Unleash UI, names in Java |
| 4 | Connect your assistant, have it build Embeddr Premium behind a flag, then kill the feature when its payment provider fails | Assistant, Unleash UI |
| 5 | Attach a safeguard to Auto-Rizz and trip it yourself | Unleash UI, names in Java |
| 6 | Ship light mode through a change request your neighbor approves | Unleash UI, names in Java, in pairs |
| 7 | Optional: retire the Premium flag through its lifecycle | Unleash UI, assistant |

## Part 0: register and install

Two things run in parallel here: you register and the organisers create
your project while you get your machine ready.

### Register for a project

Every attendee gets their own project on the workshop Unleash instance.
Share your name and email through the registration form:
**https://forms.gle/Lfcbpfqdaf2rrQb36**. While you continue below, the
organisers create your project and you receive an invitation email to the
instance. You need it in part 1.

### What you need

- git
- A JDK, version 17 or newer. The one you already have is fine.
- An AI coding assistant that supports MCP. If you don't have access to one, work with your neighbour.

Gradle downloads its own Node for the frontend into the project folder, so
nothing else is installed on your machine.

### Set up your system

#### macOS

1. Open **Terminal**.
2. Check for a JDK:

   ```bash
   java -version
   ```

   If it prints 17 or newer, you are done. If not, install one:

   ```bash
   brew install --cask temurin@21
   ```

   No Homebrew? Download Temurin 21 from [adoptium.net](https://adoptium.net)
   and run the installer.

3. git is included with the Xcode command line tools. If `git --version`
   asks you to install them, accept.

#### Linux

1. Open your terminal.
2. Check for a JDK:

   ```bash
   java -version
   ```

   If it prints 17 or newer, you are done. If not, install one with your
   package manager, for example on Debian and Ubuntu:

   ```bash
   sudo apt install openjdk-21-jdk git
   ```

   Or download Temurin 21 from [adoptium.net](https://adoptium.net).

#### Windows

1. Install [Git for Windows](https://git-scm.com/download/win) if you do not
   have it. It includes **Git Bash**, the terminal to use for this whole
   workshop. In Git Bash, `./gradlew`, `cp` and `curl` all work exactly as
   written here, so you never need to translate a command.
2. Open **Git Bash** and check for a JDK:

   ```bash
   java -version
   ```

   If it prints 17 or newer, you are done. If not, install one, in
   PowerShell or Git Bash:

   ```bash
   winget install EclipseAdoptium.Temurin.21.JDK
   ```

   Or download Temurin 21 from [adoptium.net](https://adoptium.net) and run
   the installer. Either way, close and reopen Git Bash afterwards so it
   picks up the new `PATH`, then run `java -version` again.

If you would rather stay in PowerShell or cmd, two translations apply
throughout: `./gradlew` becomes `.\gradlew.bat`, and `cp .env.example .env`
becomes `copy .env.example .env`. The curl commands in `FALLBACKS.md` need
Git Bash.

### Clone the repo and run the pre-check

```bash
git clone https://github.com/Unleash/embeddr-workshop-java.git
cd embeddr-workshop-java
./gradlew precheck
```

The first run downloads Gradle, Node and the frontend dependencies, which
takes a few minutes. Start it, then read ahead in
part 1 while it runs. It ends with a failure on the missing `.env`. That is
expected: the downloads happened and that was the point. If it fails
earlier, on the JDK or on a download, you are probably behind a corporate
proxy or VPN: the [README](README.md#behind-a-corporate-proxy) has the
three settings that fix it, and turning the VPN off is the quick version.

**Checkpoint 0.** The pre-check gets as far as complaining about `.env`.

## Part 1: run Embeddr against your project

Everything the app needs to reach Unleash lives in one file, `.env`. You
create it first, keep it open, and fill in two values, each straight from
the place you find it.

### Create .env

In the terminal, inside the repo:

```bash
cp .env.example .env
```

Open `.env` in your editor and leave it open. It has three lines to fill.
Two of them are for now; `UNLEASH_PAT` waits until part 4.

### Sign in and paste the instance URL

1. Check your email for the invitation to the workshop Unleash instance and
   sign in.
2. Look at the address bar. Copy everything before `/projects`, for example
   `https://us.app.unleash-hosted.com/abcd1234`, and paste it as the value
   of `UNLEASH_URL` in `.env`. No trailing slash.

### Open your project

In the Unleash UI, open **Projects** and find the project named after you:
first name, last initial, for example `Melinda F`. Open it. The address bar
now ends in `/projects/<your project id>`, for example
`/projects/melinda-f`. That id is yours for the day: flag names are unique
across the shared instance, so every flag you create is named
`<flag>-<your project id>`, for example `early-access-melinda-f`.

### Create the backend token and paste it

The app evaluates flags with its own token, scoped to your project.

1. In your project, go to **Settings**, then **API access**, then
   **New API token**.
2. Choose the **Backend** token type.
3. Scope it to the `development` environment, and create it.

<img src="docs/images/create-backend-token.png" width="640" alt="The Create project API token dialog with the Backend SDK type and the development environment selected">

4. Copy the token. It looks like `melinda-f:development.xxxx`. Paste it as
   the value of `UNLEASH_BACKEND_TOKEN` in `.env`, and save the file.

### Check and run

```bash
./gradlew precheck
./gradlew bootRun
```

`precheck` verifies both values against the instance and tells you exactly
which one is wrong if one is. It notes that `UNLEASH_PAT` is still
empty; that is fine until part 4. `bootRun` builds the frontend and starts
the app.

The app is up when the log says `Started EmbeddrApplication`. The task
keeps running from then on, that is the server; it never reports done, and
Ctrl-C stops it.

Open `http://localhost:8080`. You should see profile cards of AI agents
looking for love. The terminal should also have logged
`flags: synchronized with ...`.

**Checkpoint 1.** The app renders at `localhost:8080` and the log in your terminal shows
`flags: synchronized`.

### If something is off

| Symptom | Likely cause |
|---------|--------------|
| `precheck` says it could not reach the instance | Network, VPN or proxy. Run it again once you are online |
| `precheck` says the backend token is rejected | Token scoped to the wrong project or environment, or `UNLEASH_URL` has a path after the instance, like `/api` or `/projects/...` |
| The log warns that `UNLEASH_URL` is not set | `.env` is missing, not saved, or still has empty values |

## Part 2: your first feature flag

At the start of the session the room queued at a waitlist and was let in a
few people at a time. Now build that door yourself. No assistant yet: one
flag in the UI, two lines of Java.

### Create the flag

1. In your project, select **New feature flag**.
2. Name it `early-access-<your project id>`, for example
   `early-access-melinda-f`.
3. Flag type **Standard**, used for **Release**. Leave the expected lifetime
   at its default; it only sets a cleanup reminder.
4. Select **Create flag**. The flag exists and is off in every environment.

### Wire it up

The app keeps the name of every flag it uses in one file,
`src/main/java/io/getunleash/embeddr/flags/Flags.java`. Open it and set
`EARLY_ACCESS` to the exact name you just created:

```java
public static final String EARLY_ACCESS = "early-access-<your project id>";
```

For example `"early-access-melinda-f"`. The other names in that file stay
as they are for now; you fill them in as you create those flags.

Now open `src/main/java/io/getunleash/embeddr/api/AccessController.java`.
The door is wide open: the endpoint returns `true` for everyone. The
controller already has the two things it needs, a `FeatureFlags` wrapper
and a `Visitor` built from the session and the invite code. Replace the
return line:

```java
return new AccessResponse(flags.isEnabled(Flags.EARLY_ACCESS, visitor));
```

The wrapper hands the name and the visitor to the Unleash SDK, which
evaluates the flag in memory from the copy of your project it fetched a few
seconds ago. Read `flags/UnleashFeatureFlags.java` if you want to see
exactly where that happens; it is the only class that imports Unleash.

Stop `bootRun` by pressing Ctrl-C in its terminal, run `./gradlew bootRun`
again, and reload the browser. You should see the waitlist again, but this time it's controlled by the feature flag you created.

### Let yourself in

1. Type your first name as the invite code and press Enter. The page says
   you are in the queue and watches the door. The code you typed is now the
   Unleash userId on every request from this browser.
2. In the Unleash UI, open your flag. In the **development** environment,
   select **Add strategy**, then **Set up manually**.
3. In the **Targeting** tab, add a constraint: context field `userId`,
   operator `IN`, value your invite code name, exactly as you typed it. Keep the
   rollout at 100 percent. Save the strategy.
4. Turn the development environment on.
5. Watch the app. Within a few seconds the door opens and the cards appear.

Now open a private browser window, which is a new session, and type a
different code. It waits at the door. That is one flag admitting one
person, by user id, with everybody else held outside.

### Open the door wider

One strategy per flag is rarely the end of the story. Keep the one you have
and add a second one next to it.

1. In the development environment, select **Add strategy** again, then
   **Gradual rollout**. Leave it without constraints and set the rollout to
   50 percent. Save it.
2. Open two or three fresh private windows and type any code in each.
   Roughly half of them get in. Which half is decided by the session id,
   so the same window gets the same answer every time it asks.
3. Edit that strategy and move the rollout to 100 percent. Every window is
   in now, and so is everyone who comes later.

Leave it at 100 percent. The rest of the workbook assumes the door is open
for every session, including the private windows you open in part 3.

**Checkpoint 2.** `early-access-<your project id>` exists and is on in
development with two strategies: a userId constraint that let you in first,
and a gradual rollout at 100 percent that lets everyone else in. Every
window, private or not, shows the cards.

### If something is off

| Symptom | Likely cause |
|---------|--------------|
| The door never opens | The constraint value does not match what you typed, or the environment toggle is still off, or you forgot to restart `bootRun` after the code change |
| Everyone gets in | The strategy has no constraint, or the line still returns `true` |
| The door never opens, even without a constraint | `EARLY_ACCESS` in `Flags.java` is not exactly the name in Unleash |
| Compile error on restart | The line needs `flags` and `visitor`, both already declared above it in the method, and `Flags` is already imported |

## Part 3: run an experiment

Embeddr wants to know how people prefer to browse matches: the classic
grid, or one profile at a time. The code for both layouts is already in the
app, and so is the success metric: every Match tap is counted under the
layout that served it. All that is missing is the flag.

1. Create a flag named `match-layout-experiment-<your project id>`, flag
   type **Experiment**.
2. In `Flags.java`, set `MATCH_LAYOUT_EXPERIMENT` to the flag you just
   created. In `Metrics.java`, next to it, set the two match counters to
   `match_count_list_<your project id>` and
   `match_count_deck_<your project id>`, with underscores, for example
   `match_count_deck_melinda_f`. Metric names are unique across the
   instance too, and these keep your taps apart from the room's. Restart
   `bootRun`.
3. In the development environment, add a **Gradual rollout** strategy at
   100 percent. In the strategy's **Variants** tab, add two variants,
   `list` and `deck`, with equal weights. Save, and turn the environment
   on.
4. Reload the app, then open one or two private windows and type any
   invite code in each; the door is open for everyone since part 2. Each
   window is its own session, and each session sticks to its side of the
   split. At least one of them should show the deck: one profile at a
   time, with Pass and Match.
5. Tap **Match** a few times in each window.
6. In the Unleash UI, open **Analytics** in the sidebar, then **Impact
   metrics**. Select **Add chart**, pick `match_count_list_<your project
   id>` from the metric list, keep aggregation **Count**, and save. Add a
   second chart the same way for `match_count_deck_<your project id>`.
7. Wait. The app sends its counts every five seconds, but the charts
   aggregate on the Unleash side and the first points can take a minute or
   two to show up. Tap Match a few more times while you wait, and refresh
   the page if nothing has moved after two minutes. Then the two charts
   climb independently: your taps in the grid window on one, your taps in
   the deck window on the other.

The variant name travels from Unleash, through `flags.variant(...)` in
`LayoutController`, to the frontend, which picks the component. The tap
travels back through `MatchController`, which asks for the same variant and
counts under it. Nothing in the frontend knows there is an experiment.

**Checkpoint 3.** Your normal browser window and at least one private
window show different layouts, one the card grid and one the deck, and the
two match counters move independently in Unleash.

## Part 4: connect your assistant and build a feature

So far everything went through the Unleash UI and a few lines of Java. Time
to hand the keyboard to your assistant. First it needs access to Unleash.

### Connect your assistant

Your assistant talks to the
[remote MCP server](https://docs.getunleash.io/integrate/mcp) hosted by the
workshop instance, authenticated with a personal access token of yours.

1. In the Unleash UI, select your profile icon, then **Profile settings**,
   then **Personal API tokens**, then **New token**. Create it.

<img src="docs/images/create-personal-token.png" width="640" alt="The Create personal API token dialog under Profile settings">

2. Copy the token. It starts with `user:`. Paste it as the value of
   `UNLEASH_PAT` in `.env`, the one line still empty, and save the file.
3. In a second terminal, leave `bootRun` running in the first:

   ```bash
   ./gradlew configure
   ```

   This writes the MCP config for every supported assistant from `.env`:

   | Assistant | Config file |
   |-----------|-------------|
   | Claude Code | `.mcp.json` |
   | Cursor | `.cursor/mcp.json` |
   | VS Code Copilot | `.vscode/mcp.json` |
   | OpenCode | `opencode.json` |
   | Codex | `.codex/config.toml` |
   | Gemini CLI | `.gemini/settings.json` |

   These files hold your token and are gitignored.

4. Start your assistant in the repo folder, or restart it if it was already
   open, so it picks up the config. Ask it what tools it has. It should list
   Unleash tools such as `evaluate_change` and `create_flag`.

**Checkpoint 4.** Your assistant lists Unleash tools.

| Symptom | Likely cause |
|---------|--------------|
| `configure` says `UNLEASH_PAT` is empty | The `.env` edit was not saved |
| The assistant shows no Unleash tools | It was not restarted after `configure`, or it was started outside the repo folder |
| Tool calls fail with `401 Unauthorized` | You pasted the backend token into `UNLEASH_PAT`. The personal token starts with `user:`. Fix `.env`, run `./gradlew configure` again, restart the assistant |

### Build Embeddr Premium

Embeddr wants to monetize. The free tier shows six matches; Embeddr Premium
promises the rest of the embedding space, for a price. Product is excited.
Finance is more excited. Let's see.

1. Give your assistant this prompt:

   ```text
   Add an embeddr premium upsell that shows a premium call to action in the
   match grid inviting the user to subscribe to see more models. Hook it up
   to our payment provider.
   ```

2. Watch what the assistant does. Notice the prompt never says the words
   feature flag. Deciding that this change needs one is the assistant's job.
   Expect it to:

   - evaluate the change through the MCP server and decide it needs a
     release flag for Premium
   - create it in your project, with your project id in the name, like
     `premium-upsell-melinda-f`, and add it to `Flags.java`
   - write a controller that serves the call to action only when the flag
     is on, an upgrade endpoint that charges through the payment provider,
     and the frontend that renders it all

3. Verify its work in the Unleash UI: your project shows one new flag,
   off in every environment.

4. Restart `bootRun`: Ctrl-C in its terminal, then `./gradlew bootRun`.
   The restart rebuilds the frontend too. Look at the app. No call to action
   anywhere yet. The code is there. The feature is not released.

**Checkpoint 5.** The Premium flag exists in your project, turned off, and
the app shows no premium call to action.

### Release it, break it, kill it

1. Turn the Premium flag on in development with a gradual rollout at 100
   percent, in the UI or by prompt:

   ```text
   Turn on the premium flag in the development environment with a gradual
   rollout at 100 percent.
   ```

2. Within a few seconds the call to action appears. Tap **Upgrade**. The
   payment provider thinks for a second and fails. It always fails; it is
   built that way. In real life this is the Monday morning incident.

3. You have one runtime move and no deploy: turn the Premium flag off. The
   call to action vanishes within a few seconds while the app keeps
   running, and nobody else hits the broken provider.

> **TL;DR: kill switch vs release flag.** What you just flipped is a
> release flag. It is meant to be temporary: it ships Premium, and once
> Premium is stable the flag gets cleaned up (part 7). You can still
> operate it as a kill switch, because turning it off removes the
> functionality in seconds, and in an incident that is what counts. A
> proper kill switch is different in two ways: it is long lived, and it is
> inverted, so off means "normal" and on means "bypass". If Embeddr's
> payment provider were known to misbehave long term, you would wrap the
> call to it in a `kill-switch` flag of its own, keep that flag for as long
> as the provider is around, and flip it on whenever the provider acts up,
> without touching the Premium release flag at all.

**Checkpoint 6.** You watched Upgrade fail, turned Premium off and watched
the call to action disappear, without restarting anything.

### If something is off

| Symptom | Likely cause |
|---------|--------------|
| The assistant cannot decide where to create the flag | Not a failure. Tell it your project id |
| The assistant created the flag without your project id in the name | Ask it to archive that one and create `premium-upsell-<your project id>`. Or create the flag yourself with FALLBACKS.md and ask only for the code |
| The flag is on but nothing shows | The flag name in the Java code and in Unleash do not match, or `bootRun` was not restarted after the code change |
| The assistant started a server or ran a build that failed | Stop it, restart `bootRun` yourself, and ask it to fix the specific compile error you see |

**If your assistant misbehaves:** create the flag yourself with the
create-flag command in FALLBACKS.md, then ask the assistant to write only
the code. If the code is wrong, ask it to fix the specific problem you see.
You do not need to start over.

## Part 5: let a safeguard protect the flag

Auto-Rizz writes an opening line on every match card. Users can tap
**Ick**, the thumbs down, and every tap increments the thumbs down impact
metric. In this part you roll the feature out, then attach a safeguard that
turns it off when the thumbs down count goes above a threshold you set.

1. Create a flag named `auto-rizz-<your project id>`, flag type
   **Standard**, used for **Release**.
2. In `Flags.java`, set `AUTO_RIZZ` to that name. In `Metrics.java`, set
   `THUMBS_DOWN_COUNT` to `thumbs_down_count_<your project id>`, with
   underscores. Restart `bootRun`.
3. This time, no hand-made strategy. In the flag's development
   environment, select **Use template** and pick **Progressive
   percentage-based rollout**. It adds a release plan with four
   milestones: 25, 50, 75 and 100 percent. Turn the environment on. The
   first milestone starts and a quarter of sessions get openers, which
   may or may not include yours.
4. Walk the plan forward: on the release plan, start the next milestone,
   then the next, until the 100 percent milestone is active. Reload the
   app: every card has an opener, and an Ick button. In real life each
   milestone would run for days while you watch the metrics. Today the
   safeguard does the watching, so you can skip ahead.
5. Tap Ick once, so the metric exists. The app reports metrics every five
   seconds.
6. In the flag's development environment, add a **Safeguard**. Pick your
   thumbs down metric, aggregation **Count**, a threshold of 3, over the
   shortest window offered. The action is to disable the environment. The
   presenter walks through the dialog on screen.
7. Tap Ick four more times. Watch the flag. Within a minute or two the
   safeguard trips, the environment turns off, and the openers vanish from
   your app. Nobody at the keyboard.

This is the demo from the start of the session, running in your project
against your app. What you did by hand in part 4 is what the safeguard does
on its own.

**Checkpoint 7.** The development environment of your Auto-Rizz flag shows
it was disabled by the safeguard, and the openers are gone from the app.

## Part 6: ship through a change request

In production, nobody flips flags alone. In this part you turn that rule
on for your own project, and since you cannot approve your own request,
you need a reviewer. Pair up with a neighbor.

### Require a review

1. In your project, go to **Settings**, then **Change request
   configuration**.
2. Turn change requests on for the **development** environment and set the
   required approvals to 1. Leave production as it is.

From now on, every change to a flag in development lands in a draft that
someone else has to approve.

### Let your reviewer in

1. Still in **Settings**, open **User access** and select **Assign
   user/group**.
2. Search for your neighbor by email, give them the **Reviewer** role, and
   save. Reviewer is a role set up for this workshop: it can approve and
   apply change requests in your project and nothing else. Your neighbor
   does the same for you in their project.
3. Reload. Your neighbor's project now shows up in your project list, and
   yours in theirs.

### Ship light mode

1. Create a flag named `light-mode-<your project id>`, flag type
   **Standard**, used for **Release**. It gates the theme toggle in the
   header; the code is already wired.
2. In `Flags.java`, set `LIGHT_MODE` to that name. Restart `bootRun`.
3. In development, add a gradual rollout at 100 percent and turn the
   environment on. Nothing happens yet: both changes landed in a draft
   change request instead of in the environment.
4. Open the draft, add a short description, and submit it for review.
5. Your neighbor opens your project, finds the change request under
   **Change requests**, reviews it, approves it, and applies it.
6. Watch your app: the theme toggle appears in the header. Then do the same
   for your neighbor.

**Checkpoint 8.** Change requests are on in your development environment,
your neighbor is a Reviewer in your project, your light mode change request
shows approved and applied by them, and the toggle is in your header.

### If something is off

| Symptom | Likely cause |
|---------|--------------|
| You cannot find your neighbor in the user search | They registered with a different email than the one they gave you, or they have not accepted the instance invitation yet |
| Your neighbor sees the project but no Approve button | They got the Member role instead of Reviewer. Change it under **User access** |
| The flag turned on immediately, no draft | Change requests are not on for your development environment. Check **Settings**, then **Change request configuration** |

## Part 7 (optional): retire a flag

A release flag like Premium is supposed to be temporary: once the feature
is fully rolled out and stable, the flag becomes debt. Unleash tracks this
as a lifecycle: **Define**, **Develop**, **Production**, **Cleanup**,
**Archived**.

<img src="docs/images/flag-lifecycle-stages.png" width="640" alt="A project's flag lifecycle view, counting flags in the Define, Develop, Production, Cleanup, and Archived stages">

1. Open your Premium flag and find its lifecycle stage. Hover over it and
   select **Mark ready for cleanup**.
2. Ask your assistant:

   ```text
   Embeddr Premium is now fully rolled out. Remove the flag from the code.
   ```

3. Restart `bootRun`, archive the flag in the Unleash UI yourself, and
   verify the call to action still renders.

**Checkpoint 9.** The Premium flag went develop, cleanup, archived, and the
feature still works as a permanent part of the app.

## What you just did

- Set up a Java app that evaluates feature flags on the backend, behind one
  small wrapper with every flag name in one file, and ran it from one
  Gradle command.
- Wired a flag by hand and admitted one user by id.
- Built a feature with an AI assistant that wrapped its own work in a
  flag, through the Unleash MCP server, and turned the feature off when
  its payment provider failed.
- Ran an experiment with a success metric, and let a safeguard turn a
  feature off on its own.
- Shipped a change through a review, and retired a flag.

Runtime control scales from one person with a flag to a system that
protects itself, and it ends in governance.

## Where to go next

To continue experimenting with Unleash, sign up for a free trial at: https://www.getunleash.io/.
