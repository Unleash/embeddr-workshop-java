package io.getunleash.embeddr.flags;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * The Unleash connection, bound from the {@code unleash.*} properties in
 * application.properties, which in turn read the UNLEASH_* environment
 * variables that bootRun loads from .env.
 */
@ConfigurationProperties("unleash")
record UnleashSettings(
        @DefaultValue("") String url,
        @DefaultValue("") String backendToken,
        @DefaultValue("5") int metricsIntervalSeconds) {

    boolean configured() {
        return !url.isBlank() && !backendToken.isBlank();
    }
}
