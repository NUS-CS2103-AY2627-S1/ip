package luckynoslacky.luckyparser;

import java.util.Arrays;
import java.util.Locale;
import java.util.Set;

import luckynoslacky.luckyexception.LuckyNoInputException;
import luckynoslacky.luckyresponse.LuckyNoQuips;

/**
 * Identifies supported duration units and their accepted forms.
 */
enum DurationPeriodUnit {
    YEAR(0, false, "year", "years", "yr", "yrs"),
    MONTH(1, false, "month", "months", "mo", "mos"),
    WEEK(2, false, "week", "weeks"),
    DAY(3, true, "day", "days", "d", "ds"),
    HOUR(4, true, "hour", "hours", "h", "hs", "hr", "hrs"),
    MINUTE(5, true, "minute", "minutes", "min", "mins");

    private final int order;
    private final boolean allowsDecimal;
    private final Set<String> acceptedForms;

    /**
     * Creates a duration unit definition.
     *
     * @param order ordering used to validate combined durations
     * @param allowsDecimal whether the unit accepts decimal amounts
     * @param acceptedForms text forms accepted for the unit
     */
    DurationPeriodUnit(
            int order,
            boolean allowsDecimal,
            String... acceptedForms) {
        this.order = order;
        this.allowsDecimal = allowsDecimal;
        this.acceptedForms = Set.of(acceptedForms);
    }

    /**
     * Returns the unit's ordering position.
     *
     * @return ordering position
     */
    int getOrder() {
        return order;
    }

    /**
     * Checks whether the unit accepts decimal amounts.
     *
     * @return true if decimal amounts are accepted
     */
    boolean allowsDecimal() {
        return allowsDecimal;
    }

    /**
     * Converts a parsed unit name into its unit definition.
     *
     * @param unitText unit name
     * @param inputText original input used in the error message
     * @return matching duration period unit
     * @throws LuckyNoInputException if the unit is unsupported
     */
    static DurationPeriodUnit fromText(
            String unitText,
            String inputText)
            throws LuckyNoInputException {
        String normalizedUnit = unitText.toLowerCase(Locale.ROOT);

        return Arrays.stream(values())
                .filter(unit -> unit.acceptedForms.contains(normalizedUnit))
                .findFirst()
                .orElseThrow(() -> new LuckyNoInputException(
                        LuckyNoQuips.invalidDurationMessage(inputText)));
    }
}
