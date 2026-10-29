package io.getunleash.embeddr.api;

import io.getunleash.embeddr.flags.FeatureFlags;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * An Ick is a thumbs down on an opener. Every tap bumps the thumbs_down_count
 * impact metric, which is what a safeguard in Unleash watches.
 */
@RestController
class IckController {

    record IckRequest(String sessionId) {}

    record IckResponse(boolean ok) {}

    private static final Logger log = LoggerFactory.getLogger(IckController.class);

    private final FeatureFlags flags;

    // Taps per session, in memory. The workshop runs long enough for a
    // safeguard to trip, not long enough to need persistence.
    private final Map<String, Integer> taps = new ConcurrentHashMap<>();

    IckController(FeatureFlags flags) {
        this.flags = flags;
    }

    @PostMapping("/ick")
    IckResponse ick(@RequestBody IckRequest request) {
        String sessionId = Requests.requireSessionId(request.sessionId());
        int count = taps.merge(sessionId, 1, Integer::sum);
        flags.increment("thumbs_down_count");
        log.info("ick recorded: session {} has {} tap(s)", sessionId, count);
        return new IckResponse(true);
    }
}
