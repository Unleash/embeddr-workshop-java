package io.getunleash.embeddr.flags;

import java.util.Optional;

/**
 * Used when .env has no Unleash connection. Every flag is off, every variant
 * is empty, every metric is dropped, and Embeddr renders its plain self.
 */
final class OfflineFeatureFlags implements FeatureFlags {

    @Override
    public boolean isEnabled(String flag, Visitor visitor) {
        return false;
    }

    @Override
    public Optional<String> variant(String flag, Visitor visitor) {
        return Optional.empty();
    }

    @Override
    public void increment(String metric) {
        // Nowhere to send it.
    }
}
