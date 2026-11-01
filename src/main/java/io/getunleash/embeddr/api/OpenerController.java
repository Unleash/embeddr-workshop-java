package io.getunleash.embeddr.api;

import io.getunleash.embeddr.content.Openers;
import io.getunleash.embeddr.flags.FeatureFlags;
import io.getunleash.embeddr.flags.Flags;
import io.getunleash.embeddr.flags.Visitor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** Auto-Rizz: an opening line per match card, behind the auto-rizz flag. */
@RestController
class OpenerController {

    record OpenerRequest(String sessionId, String userId, String matchId, Integer variant) {}

    record OpenerResponse(String opener) {}

    private final FeatureFlags flags;

    OpenerController(FeatureFlags flags) {
        this.flags = flags;
    }

    @PostMapping("/opener")
    OpenerResponse opener(@RequestBody OpenerRequest request) {
        String sessionId = Requests.requireSessionId(request.sessionId());

        if (!flags.isEnabled(Flags.AUTO_RIZZ, new Visitor(sessionId, request.userId()))) {
            return new OpenerResponse(null);
        }

        // The session picks a base line and keeps it across polls. A reroll
        // walks the bank from there, so it always lands on a different line.
        int offset = request.variant() == null ? 0 : Math.abs(request.variant());
        int base = stablePick(sessionId + ":" + (request.matchId() == null ? "" : request.matchId()), Openers.LINES.size());
        return new OpenerResponse(Openers.LINES.get((base + offset) % Openers.LINES.size()));
    }

    /** FNV-1a over the seed, so the same session and card always pick the same line. */
    static int stablePick(String seed, int length) {
        int hash = 0x811C9DC5;
        for (int i = 0; i < seed.length(); i++) {
            hash ^= seed.charAt(i);
            hash *= 16777619;
        }
        return (hash & 0x7FFFFFFF) % length;
    }
}
