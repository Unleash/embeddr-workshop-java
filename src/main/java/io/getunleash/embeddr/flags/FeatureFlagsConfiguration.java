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
        return UnleashFeatureFlags.connect(settings.url(), settings.backendToken(), settings.metricsIntervalSeconds());
    }
}
