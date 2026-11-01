package io.getunleash.embeddr.flags;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.getunleash.FakeUnleash;
import io.getunleash.variant.Variant;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class UnleashFeatureFlagsTest {

    private final FakeUnleash fake = new FakeUnleash();
    private final UnleashFeatureFlags flags = new UnleashFeatureFlags(fake);
    private final Visitor visitor = Visitor.session("s-1");

    @Test
    void asksUnleashForTheFlagByItsExactName() {
        fake.enable("light-mode-melinda-f");

        assertTrue(flags.isEnabled("light-mode-melinda-f", visitor));
        assertFalse(flags.isEnabled("light-mode", visitor), "no hidden renaming");
        assertFalse(flags.isEnabled("auto-rizz-melinda-f", visitor), "unknown flags are off");
    }

    @Test
    void variantIsEmptyUnlessTheFlagServesOne() {
        assertEquals(Optional.empty(), flags.variant("match-layout-experiment", visitor));

        fake.enable("match-layout-experiment");
        fake.setVariant("match-layout-experiment", new Variant("deck", (String) null, true));
        assertEquals(Optional.of("deck"), flags.variant("match-layout-experiment", visitor));
    }

    @Test
    void countsImpactMetricsWithoutBlowingUp() {
        flags.increment("thumbs_down_count");
        flags.increment("thumbs_down_count");
    }
}
