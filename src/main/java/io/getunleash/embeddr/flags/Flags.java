package io.getunleash.embeddr.flags;

/**
 * Every feature flag Embeddr uses, named exactly as in Unleash.
 *
 * <p>Flag names are unique across a whole Unleash instance. In the workshop
 * every attendee has their own project on a shared instance, so each name
 * carries the project id: {@code early-access-melinda-f}, not
 * {@code early-access}. Create the flag in Unleash, then put its exact name
 * here.
 */
public final class Flags {

    public static final String EARLY_ACCESS = "early-access";
    public static final String LIGHT_MODE = "light-mode";
    public static final String MATCH_LAYOUT_EXPERIMENT = "match-layout-experiment";
    public static final String AUTO_RIZZ = "auto-rizz";

    private Flags() {}
}
