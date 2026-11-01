package io.getunleash.embeddr.api;

import io.getunleash.embeddr.flags.FeatureFlags;
import io.getunleash.embeddr.flags.Flags;
import io.getunleash.embeddr.flags.Visitor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * The waitlist. The frontend sits behind this answer: false shows the coming
 * soon page, true shows the app.
 */
@RestController
class AccessController {

    record AccessRequest(String sessionId, String inviteCode) {}

    record AccessResponse(boolean access) {}

    private final FeatureFlags flags;

    AccessController(FeatureFlags flags) {
        this.flags = flags;
    }

    @PostMapping("/access")
    AccessResponse access(@RequestBody AccessRequest request) {
        String sessionId = Requests.requireSessionId(request.sessionId());

        // The invite code rides as the user id. Targeting one code admits one
        // person; a gradual rollout with userId stickiness admits the same
        // codes as the percentage climbs.
        Visitor visitor = new Visitor(sessionId, request.inviteCode());

        // Workshop, part 2: the door is wide open. Put it behind the flag named
        // in Flags.EARLY_ACCESS. The workbook has the one line to change.
        return new AccessResponse(true);
    }
}
