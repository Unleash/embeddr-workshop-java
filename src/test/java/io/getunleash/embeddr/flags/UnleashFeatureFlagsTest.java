package io.getunleash.embeddr.flags;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.getunleash.FakeUnleash;
import io.getunleash.variant.Variant;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class UnleashFeatureFlagsTest {

    private final Visitor visitor = Visitor.session("s-1");

    @Test
    void appendsTheProjectSuffixToFlagNames() {
        FakeUnleash fake = new FakeUnleash();
        fake.enable("light-mode-melinda-f");
        UnleashFeatureFlags flags = new UnleashFeatureFlags(fake, "melinda-f");

        assertTrue(flags.isEnabled("light-mode", visitor));
        assertTrue(flags.isEnabled("light-mode-melinda-f", visitor), "a name that already carries the suffix is left alone");
        assertFalse(flags.isEnabled("auto-rizz", visitor));
    }

    @Test
    void usesShortNamesWhenThereIsNoSuffix() {
        FakeUnleash fake = new FakeUnleash();
        fake.enable("light-mode");
        UnleashFeatureFlags flags = new UnleashFeatureFlags(fake, "");

        assertTrue(flags.isEnabled("light-mode", visitor));
        assertEquals("thumbs_down_count", flags.metricName("thumbs_down_count"));
    }

    @Test
    void metricNamesGetTheSuffixWithUnderscores() {
        UnleashFeatureFlags flags = new UnleashFeatureFlags(new FakeUnleash(), "melinda-f");

        assertEquals("thumbs_down_count_melinda_f", flags.metricName("thumbs_down_count"));
        assertEquals("early-access-melinda-f", flags.flagName("early-access"));
    }

    @Test
    void variantIsEmptyUnlessTheFlagServesOne() {
        FakeUnleash fake = new FakeUnleash();
        UnleashFeatureFlags flags = new UnleashFeatureFlags(fake, "melinda-f");
        assertEquals(Optional.empty(), flags.variant("match-layout-experiment", visitor));

        fake.enable("match-layout-experiment-melinda-f");
        fake.setVariant("match-layout-experiment-melinda-f", new Variant("deck", (String) null, true));
        assertEquals(Optional.of("deck"), flags.variant("match-layout-experiment", visitor));
    }

    @Test
    void countsImpactMetricsWithoutBlowingUp() {
        UnleashFeatureFlags flags = new UnleashFeatureFlags(new FakeUnleash(), "melinda-f");
        flags.increment("thumbs_down_count");
        flags.increment("thumbs_down_count");
    }
}
