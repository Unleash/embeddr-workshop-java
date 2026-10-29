package io.getunleash.embeddr.flags;

import java.util.Optional;

/**
 * The one door between Embeddr and its feature flag provider.
 *
 * <p>Controllers ask three questions and never see the provider behind them:
 * is this flag on for this visitor, which variant does this visitor get, and
 * count the thing that just happened. {@link UnleashFeatureFlags} answers
 * with the Unleash SDK. {@link OfflineFeatureFlags} answers when no Unleash
 * connection is configured, so the app always boots.
 */
public interface FeatureFlags {

    /** True when the flag serves this visitor. Unknown flags are off. */
    boolean isEnabled(String flag, Visitor visitor);

    /**
     * The variant name this visitor gets, or empty when the flag is off or
     * has no variants.
     */
    Optional<String> variant(String flag, Visitor visitor);

    /** Counts one occurrence of an impact metric, for example a tap. */
    void increment(String metric);
}
