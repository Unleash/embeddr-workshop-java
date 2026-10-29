package io.getunleash.embeddr.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class HealthController {

    record Health(boolean ok) {}

    @GetMapping("/health")
    Health health() {
        return new Health(true);
    }
}
