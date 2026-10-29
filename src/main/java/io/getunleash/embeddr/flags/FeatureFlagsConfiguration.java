package io.getunleash.embeddr.flags;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Picks the {@link FeatureFlags} implementation at startup: Unleash when the
 * connection is configured, offline otherwise. Controllers only ever see the
 * interface.
 */
@Configuration
@EnableConfigurationProperties(UnleashSettings.class)
class FeatureFlagsConfiguration {

    private static final Logger log = LoggerFactory.getLogger(FeatureFlagsConfiguration.class);

    @Bean
    FeatureFlags featureFlags(UnleashSettings settings) {
        if (!settings.configured()) {
            log.warn("UNLEASH_URL or UNLEASH_BACKEND_TOKEN is not set. Running without Unleash: every flag is off. "
                    + "Copy .env.example to .env and fill in your instance details.");
            return new OfflineFeatureFlags();
        }
        if (settings.flagSuffix().isBlank()) {
            log.warn("UNLEASH_FLAG_SUFFIX is not set. Flags are evaluated under their short names, "
                    + "which only works in a project that owns them, like the hosted demo.");
        }
        return UnleashFeatureFlags.connect(
                settings.url(), settings.backendToken(), settings.flagSuffix(), settings.metricsIntervalSeconds());
    }
}
