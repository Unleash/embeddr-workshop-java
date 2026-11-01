package io.getunleash.embeddr.api;

import io.getunleash.embeddr.flags.FeatureFlags;
import io.getunleash.embeddr.flags.Flags;
import io.getunleash.embeddr.flags.Visitor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * The flag gates the control, not the theme: when light-mode serves true the
 * frontend shows the toggle, and the light or dark choice is the user's own.
 */
@RestController
class ThemeController {

    record ThemeRequest(String sessionId) {}

    record ThemeResponse(boolean themeToggle) {}

    private final FeatureFlags flags;

    ThemeController(FeatureFlags flags) {
        this.flags = flags;
    }

    @PostMapping("/theme")
    ThemeResponse theme(@RequestBody ThemeRequest request) {
        String sessionId = Requests.requireSessionId(request.sessionId());
        return new ThemeResponse(flags.isEnabled(Flags.LIGHT_MODE, Visitor.session(sessionId)));
    }
}
