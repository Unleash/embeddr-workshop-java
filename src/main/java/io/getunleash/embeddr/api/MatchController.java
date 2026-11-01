package io.getunleash.embeddr.api;

import io.getunleash.embeddr.flags.FeatureFlags;
import io.getunleash.embeddr.flags.Flags;
import io.getunleash.embeddr.flags.Metrics;
import io.getunleash.embeddr.flags.Visitor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * A Match tap is the experiment's success metric. It is counted under the
 * layout this session actually sees, as match_count_list or
 * match_count_deck. Taps outside the experiment count nothing.
 */
@RestController
class MatchController {

    record MatchRequest(String sessionId, String userId) {}

    record MatchResponse(boolean ok) {}

    private final FeatureFlags flags;

    MatchController(FeatureFlags flags) {
        this.flags = flags;
    }

    @PostMapping("/match")
    MatchResponse match(@RequestBody MatchRequest request) {
        String sessionId = Requests.requireSessionId(request.sessionId());
        Visitor visitor = new Visitor(sessionId, request.userId());
        flags.variant(Flags.MATCH_LAYOUT_EXPERIMENT, visitor).ifPresent(layout -> {
            switch (layout) {
                case "list" -> flags.increment(Metrics.MATCH_COUNT_LIST);
                case "deck" -> flags.increment(Metrics.MATCH_COUNT_DECK);
                default -> { /* a variant without a counter counts nothing */ }
            }
        });
        return new MatchResponse(true);
    }
}
