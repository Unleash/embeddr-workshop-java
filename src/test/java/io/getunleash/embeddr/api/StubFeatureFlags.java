package io.getunleash.embeddr.api;

import io.getunleash.embeddr.flags.FeatureFlags;
import io.getunleash.embeddr.flags.Visitor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/** Controllers depend on the interface, so tests need no Unleash at all. */
final class StubFeatureFlags implements FeatureFlags {

    final Set<String> enabled = new HashSet<>();
    final Map<String, String> variants = new HashMap<>();
    final List<String> counted = new ArrayList<>();

    @Override
    public boolean isEnabled(String flag, Visitor visitor) {
        return enabled.contains(flag);
    }

    @Override
    public Optional<String> variant(String flag, Visitor visitor) {
        return Optional.ofNullable(variants.get(flag));
    }

    @Override
    public void increment(String metric) {
        counted.add(metric);
    }
}
