package io.getunleash.embeddr.flags;

/**
 * Who is asking.
 *
 * <p>The session id is one per browser and keeps percentage rollouts sticky:
 * a session that gets a feature keeps it as the rollout climbs. The user id,
 * when the browser has one, lets a strategy target a person. It may be null.
 */
public record Visitor(String sessionId, String userId) {

    public static Visitor session(String sessionId) {
        return new Visitor(sessionId, null);
    }
}
