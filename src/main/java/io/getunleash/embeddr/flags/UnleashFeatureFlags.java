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
 *
 * <p>Naming: flag names are unique across a whole Unleash instance, and the
 * workshop instance hosts one project per attendee. The code uses short
 * names like {@code early-access}; this class appends the configured suffix,
 * so project {@code melinda-f} evaluates {@code early-access-melinda-f}.
 * Impact metrics get the same suffix with underscores.
 */
public final class UnleashFeatureFlags implements FeatureFlags {

    private static final Logger log = LoggerFactory.getLogger(UnleashFeatureFlags.class);

    /** Seconds between flag fetches. Low so a flip is visible within seconds. */
    private static final int FETCH_INTERVAL_SECONDS = 5;

    private final Unleash unleash;
    private final String flagSuffix;
    private final String metricSuffix;
    private final Set<String> definedCounters = ConcurrentHashMap.newKeySet();

    UnleashFeatureFlags(Unleash unleash, String suffix) {
        this.unleash = unleash;
        this.flagSuffix = suffix.isBlank() ? "" : "-" + suffix.trim();
        this.metricSuffix = suffix.isBlank() ? "" : "_" + suffix.trim().replace('-', '_');
    }

    /** Connects to an instance. {@code url} is the instance root, without /api. */
    static UnleashFeatureFlags connect(String url, String backendToken, String suffix, int metricsIntervalSeconds) {
        String api = url.replaceAll("/+$", "") + "/api";
        UnleashConfig config = UnleashConfig.builder()
                .appName(suffix.isBlank() ? "embeddr" : "embeddr-" + suffix.trim())
                .unleashAPI(api)
                .apiKey(backendToken)
                .fetchTogglesInterval(FETCH_INTERVAL_SECONDS)
                .sendMetricsInterval(metricsIntervalSeconds)
                .subscriber(new UnleashSubscriber() {
                    @Override
                    public void onReady(UnleashReady ready) {
                        log.info("flags: synchronized with {} (flag suffix \"{}\")", api, suffix);
                    }

                    @Override
                    public void onError(UnleashException exception) {
                        log.warn("flags: {}", exception.getMessage());
                    }
                })
                .build();
        return new UnleashFeatureFlags(new DefaultUnleash(config), suffix);
    }

    @Override
    public boolean isEnabled(String flag, Visitor visitor) {
        return unleash.isEnabled(flagName(flag), context(visitor));
    }

    @Override
    public Optional<String> variant(String flag, Visitor visitor) {
        Variant variant = unleash.getVariant(flagName(flag), context(visitor));
        return variant.isEnabled() ? Optional.of(variant.getName()) : Optional.empty();
    }

    @Override
    public void increment(String metric) {
        String name = metricName(metric);
        if (definedCounters.add(name)) {
            unleash.getImpactMetrics().defineCounter(name, metric.replace('_', ' '));
        }
        unleash.getImpactMetrics().incrementCounter(name);
    }

    /** {@code early-access} becomes {@code early-access-melinda-f}. A name that already ends in the suffix is left alone. */
    String flagName(String flag) {
        return flagSuffix.isEmpty() || flag.endsWith(flagSuffix) ? flag : flag + flagSuffix;
    }

    /** {@code thumbs_down_count} becomes {@code thumbs_down_count_melinda_f}. */
    String metricName(String metric) {
        return metricSuffix.isEmpty() || metric.endsWith(metricSuffix) ? metric : metric + metricSuffix;
    }

    private static UnleashContext context(Visitor visitor) {
        UnleashContext.Builder context = UnleashContext.builder().sessionId(visitor.sessionId());
        if (visitor.userId() != null && !visitor.userId().isBlank()) {
            context.userId(visitor.userId());
        }
        return context.build();
    }
}
