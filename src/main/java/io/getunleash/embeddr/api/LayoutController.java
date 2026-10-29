package io.getunleash.embeddr.api;

import io.getunleash.embeddr.flags.FeatureFlags;
import io.getunleash.embeddr.flags.Visitor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * The A/B test on how matches are browsed. Variant list keeps the card grid,
 * variant deck shows one profile at a time. No variant means the frontend
 * falls back to the grid.
 */
@RestController
class LayoutController {

    record LayoutRequest(String sessionId, String userId) {}

    record LayoutResponse(String layout) {}

    private final FeatureFlags flags;

    LayoutController(FeatureFlags flags) {
        this.flags = flags;
    }

    @PostMapping("/layout")
    LayoutResponse layout(@RequestBody LayoutRequest request) {
        String sessionId = Requests.requireSessionId(request.sessionId());
        Visitor visitor = new Visitor(sessionId, request.userId());
        return new LayoutResponse(flags.variant("match-layout-experiment", visitor).orElse(null));
    }
}
