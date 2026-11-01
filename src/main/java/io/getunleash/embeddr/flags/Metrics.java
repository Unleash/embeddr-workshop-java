package io.getunleash.embeddr.flags;

/**
 * Every impact metric Embeddr reports, named exactly as in Unleash.
 *
 * <p>Metric names are unique across a whole instance too, so in the
 * workshop each one carries the project id with underscores:
 * {@code thumbs_down_count_melinda_f}. A safeguard watches the metric under
 * the name given here.
 */
public final class Metrics {

    /** Ick taps on Auto-Rizz openers. */
    public static final String THUMBS_DOWN_COUNT = "thumbs_down_count";

    /** Match taps, one counter per layout variant of the experiment. */
    public static final String MATCH_COUNT_LIST = "match_count_list";
    public static final String MATCH_COUNT_DECK = "match_count_deck";

    private Metrics() {}
}
