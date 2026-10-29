package io.getunleash.embeddr.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.getunleash.embeddr.api.MatchController.MatchRequest;
import java.util.List;
import org.junit.jupiter.api.Test;

class MatchControllerTest {

    private final StubFeatureFlags flags = new StubFeatureFlags();
    private final MatchController controller = new MatchController(flags);

    @Test
    void creditsTheTapToTheLayoutTheSessionSees() {
        flags.variants.put("match-layout-experiment", "deck");
        controller.match(new MatchRequest("s-1", null));
        assertEquals(List.of("match_count_deck"), flags.counted);
    }

    @Test
    void tapsOutsideTheExperimentCountNothing() {
        controller.match(new MatchRequest("s-1", null));
        assertTrue(flags.counted.isEmpty());
    }
}
