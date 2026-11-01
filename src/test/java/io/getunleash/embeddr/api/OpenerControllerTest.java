package io.getunleash.embeddr.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.getunleash.embeddr.api.OpenerController.OpenerRequest;
import io.getunleash.embeddr.flags.Flags;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

class OpenerControllerTest {

    private final StubFeatureFlags flags = new StubFeatureFlags();
    private final OpenerController controller = new OpenerController(flags);

    @Test
    void noOpenerWhileTheFlagIsOff() {
        assertNull(controller.opener(new OpenerRequest("s-1", null, "m-1", 0)).opener());
    }

    @Test
    void sameSessionAndCardKeepTheirLine() {
        flags.enabled.add(Flags.AUTO_RIZZ);
        String first = controller.opener(new OpenerRequest("s-1", null, "m-1", 0)).opener();
        String again = controller.opener(new OpenerRequest("s-1", null, "m-1", 0)).opener();
        assertNotNull(first);
        assertEquals(first, again);
    }

    @Test
    void rerollLandsOnADifferentLine() {
        flags.enabled.add(Flags.AUTO_RIZZ);
        String base = controller.opener(new OpenerRequest("s-1", null, "m-1", 0)).opener();
        String reroll = controller.opener(new OpenerRequest("s-1", null, "m-1", 1)).opener();
        assertNotEquals(base, reroll);
    }

    @Test
    void sessionIdIsRequired() {
        assertThrows(ResponseStatusException.class, () -> controller.opener(new OpenerRequest("", null, "m-1", 0)));
    }
}
