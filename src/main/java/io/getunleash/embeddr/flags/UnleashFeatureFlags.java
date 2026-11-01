package io.getunleash.embeddr.flags;

import io.getunleash.DefaultUnleash;
import io.getunleash.Unleash;
import io.getunleash.UnleashContext;
import io.getunleash.UnleashException;
import io.getunleash.event.UnleashReady;
import io.getunleash.event.UnleashSubscriber;
import io.getunleash.util.UnleashConfig;
import io.getunleash.variant.Variant;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link FeatureFlags} backed by the Unleash server-side SDK. This is the
 * only class in Embeddr that imports anything from io.getunleash.
 *
 * <p>The SDK fetches all flags for the project every few seconds and
 * evaluates them in memory, so a request never waits on Unleash and a flag
 * flip shows up in the app within one fetch interval. Metrics, including the
 * impact metrics behind safeguards, are batched and sent on their own
 * interval.
 */
public final class UnleashFeatureFlags implements FeatureFlags {

    private static final Logger log = LoggerFactory.getLogger(UnleashFeatureFlags.class);

    /** Seconds between flag fetches. Low so a flip is visible within seconds. */
    private static final int FETCH_INTERVAL_SECONDS = 5;

    private final Unleash unleash;
    private final Set<String> definedCounters = ConcurrentHashMap.newKeySet();

    UnleashFeatureFlags(Unleash unleash) {
        this.unleash = unleash;
    }

    /** Connects to an instance. {@code url} is the instance root, without /api. */
    static UnleashFeatureFlags connect(String url, String backendToken, int metricsIntervalSeconds) {
        String api = url.replaceAll("/+$", "") + "/api";
        UnleashConfig config = UnleashConfig.builder()
                .appName("embeddr")
                .unleashAPI(api)
                .apiKey(backendToken)
                .fetchTogglesInterval(FETCH_INTERVAL_SECONDS)
                .sendMetricsInterval(metricsIntervalSeconds)
                .subscriber(new UnleashSubscriber() {
                    @Override
                    public void onReady(UnleashReady ready) {
                        log.info("flags: synchronized with {}", api);
                    }

                    @Override
                    public void onError(UnleashException exception) {
                        log.warn("flags: {}", exception.getMessage());
                    }
                })
                .build();
        return new UnleashFeatureFlags(new DefaultUnleash(config));
    }

    @Override
    public boolean isEnabled(String flag, Visitor visitor) {
        return unleash.isEnabled(flag, context(visitor));
    }

    @Override
    public Optional<String> variant(String flag, Visitor visitor) {
        Variant variant = unleash.getVariant(flag, context(visitor));
        return variant.isEnabled() ? Optional.of(variant.getName()) : Optional.empty();
    }

    @Override
    public void increment(String metric) {
        if (definedCounters.add(metric)) {
            unleash.getImpactMetrics().defineCounter(metric, metric.replace('_', ' '));
        }
        unleash.getImpactMetrics().incrementCounter(metric);
    }

    private static UnleashContext context(Visitor visitor) {
        UnleashContext.Builder context = UnleashContext.builder().sessionId(visitor.sessionId());
        if (visitor.userId() != null && !visitor.userId().isBlank()) {
            context.userId(visitor.userId());
        }
        return context.build();
    }
}
