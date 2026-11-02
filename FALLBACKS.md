# Fallback commands

Plain commands for every workshop step an assistant performs. If an
assistant misbehaves, run the matching command with curl and rejoin at the
next checkpoint. Every checkpoint is verified in the Unleash UI, so it does
not matter whether an assistant or a command got you there.

Every command on this page was verified on 2026-11-01 against the workshop
instance, a hosted Unleash Enterprise version 8.3.1+27403.4298c9ee05, in a scratch project.

## Before you start

The commands use shell variables. Set them once per terminal session; the
first two are the same values as in your `.env`:

```bash
export UNLEASH_URL="https://the-workshop-instance.example.com"
export UNLEASH_PAT="user:xxxxxxxxxxxxxxxx"
export PROJECT_ID="your-project-id"
export FLAG="premium-upsell-$PROJECT_ID"
```

On Windows, run everything on this page in Git Bash, which comes with Git for Windows. PowerShell quotes JSON differently, so these commands do not work there as written.

No curl on your machine? The Unleash documentation ships an interactive
[API reference](https://docs.getunleash.io/reference/api/unleash) where you
can run the same requests from the browser.

Rules that apply to every command:

- `UNLEASH_URL` is the instance URL with no trailing `/api`. The paths
  below add it.
- `UNLEASH_PAT` is your personal access token. It starts with `user:`. Pass
  it in the `Authorization` header with no `Bearer` prefix.
- `PROJECT_ID` is your project's id, not its display name.
- A `401` response means the token is wrong. Check that you are using the
  personal access token, not the backend token, and that it has not
  expired.

## Find your project id

```bash
curl -s -H "Authorization: $UNLEASH_PAT" \
  "$UNLEASH_URL/api/admin/projects" | python3 -c \
  "import json,sys; [print(p['id'], '->', p['name']) for p in json.load(sys.stdin)['projects']]"
```

## Create a flag

Creates a flag, off by default. Set `FLAG` and the type first: `release`
for a feature, `experiment` for an A/B test. Expected response: `201`. A `409` means the name is taken, live or
archived, somewhere on the instance; check that `FLAG` carries your project
id.

```bash
export FLAG="premium-upsell-$PROJECT_ID"
export FLAG_TYPE="release"

curl -s -X POST "$UNLEASH_URL/api/admin/projects/$PROJECT_ID/features" \
  -H "Authorization: $UNLEASH_PAT" \
  -H "Content-Type: application/json" \
  -d "{\"name\": \"$FLAG\", \"type\": \"$FLAG_TYPE\", \"description\": \"Created from FALLBACKS.md\"}"
```

## Add a 100 percent rollout strategy in development

Expected response: `200`.

```bash
curl -s -X POST "$UNLEASH_URL/api/admin/projects/$PROJECT_ID/features/$FLAG/environments/development/strategies" \
  -H "Authorization: $UNLEASH_PAT" \
  -H "Content-Type: application/json" \
  -d "{\"name\": \"flexibleRollout\", \"parameters\": {\"rollout\": \"100\", \"stickiness\": \"default\", \"groupId\": \"$FLAG\"}}"
```

## Add a strategy that targets one user id (part 2)

Same as above, with a constraint on `userId`. Set `INVITE_CODE` to the
code you typed in the app. Expected response: `200`.

```bash
export INVITE_CODE="melinda"

curl -s -X POST "$UNLEASH_URL/api/admin/projects/$PROJECT_ID/features/$FLAG/environments/development/strategies" \
  -H "Authorization: $UNLEASH_PAT" \
  -H "Content-Type: application/json" \
  -d "{\"name\": \"flexibleRollout\", \"constraints\": [{\"contextName\": \"userId\", \"operator\": \"IN\", \"values\": [\"$INVITE_CODE\"]}], \"parameters\": {\"rollout\": \"100\", \"stickiness\": \"default\", \"groupId\": \"$FLAG\"}}"
```

## Add a strategy with list and deck variants (part 3)

A gradual rollout at 100 percent whose variants split sessions 50/50.
Expected response: `200`.

```bash
curl -s -X POST "$UNLEASH_URL/api/admin/projects/$PROJECT_ID/features/$FLAG/environments/development/strategies" \
  -H "Authorization: $UNLEASH_PAT" \
  -H "Content-Type: application/json" \
  -d "{\"name\": \"flexibleRollout\", \"parameters\": {\"rollout\": \"100\", \"stickiness\": \"default\", \"groupId\": \"$FLAG\"}, \"variants\": [{\"name\": \"list\", \"weight\": 500, \"weightType\": \"variable\", \"stickiness\": \"default\"}, {\"name\": \"deck\", \"weight\": 500, \"weightType\": \"variable\", \"stickiness\": \"default\"}]}"
```

## Turn the flag on

Expected response: `200`. When change requests are enabled on the
environment, this lands in a draft instead; finish it in the UI.

```bash
curl -s -X POST "$UNLEASH_URL/api/admin/projects/$PROJECT_ID/features/$FLAG/environments/development/on" \
  -H "Authorization: $UNLEASH_PAT"
```

## Turn the flag off

The kill. The app stops serving the feature within a few seconds. Expected
response: `200`.

```bash
curl -s -X POST "$UNLEASH_URL/api/admin/projects/$PROJECT_ID/features/$FLAG/environments/development/off" \
  -H "Authorization: $UNLEASH_PAT"
```

## Start over

If a flag got created wrong, archive it and delete it from the archive,
then create it again. Both steps matter: an archived flag keeps its name
reserved across the whole instance. Expected responses: `202`, then `200`.

```bash
curl -s -X DELETE "$UNLEASH_URL/api/admin/projects/$PROJECT_ID/features/$FLAG" \
  -H "Authorization: $UNLEASH_PAT"

curl -s -X DELETE "$UNLEASH_URL/api/admin/archive/$FLAG" \
  -H "Authorization: $UNLEASH_PAT"
```

Note for maintainers: the permanent delete endpoint is
`DELETE /api/admin/archive/{featureName}` on Unleash 8. Older documentation
shows `/api/admin/archive/features/{featureName}`, which returns `404`.

## Safeguards and change requests

Both are configured in the Unleash UI. There is no fallback command and
none is needed: nothing in the app changes for either.
