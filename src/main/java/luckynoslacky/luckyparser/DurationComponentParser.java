package luckynoslacky.luckyparser;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Period;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import luckynoslacky.luckyexception.LuckyNoInputException;
import luckynoslacky.luckyresponse.LuckyNoQuips;
import luckynoslacky.luckytask.DurationPeriod;

/**
 * Parses normalized numeric duration components and accumulates their values.
 */
final class DurationComponentParser {
    private static final Pattern COMPONENT_PATTERN = Pattern.compile(
            "(-?\\d+(?:\\.\\d+)?)\\s*"
                    + "(minutes?|min(?:s)?|hours?|h(?:r)?s?|days?|d(?:s)?|"
                    + "weeks?|months?|mo(?:s)?|years?|yr(?:s)?)\\b",
            Pattern.CASE_INSENSITIVE);
    private static final long NANOS_PER_MINUTE = Duration.ofMinutes(1).toNanos();
    private static final long NANOS_PER_HOUR = Duration.ofHours(1).toNanos();
    private static final long NANOS_PER_DAY = Duration.ofDays(1).toNanos();

    private DurationComponentParser() {
        // Prevent instantiation of this utility class.
    }

    /**
     * Parses all duration components in normalized text.
     *
     * @param normalizedText normalized duration text
     * @param inputText original duration text used in error messages
     * @return parsed calendar and clock amounts
     * @throws LuckyNoInputException if a component or remainder is malformed
     */
    static DurationPeriod parse(String normalizedText, String inputText)
            throws LuckyNoInputException {
        Matcher matcher = COMPONENT_PATTERN.matcher(normalizedText);
        int cursor = 0;
        DurationAccumulator accumulator = new DurationAccumulator();

        while (cursor < normalizedText.length()) {
            cursor = skipWhitespace(
                    normalizedText,
                    cursor,
                    accumulator.hasParsedComponent(),
                    inputText);

            matcher.region(cursor, normalizedText.length());
            if (!matcher.lookingAt()) {
                break;
            }

            ParsedComponent component = parseNextComponent(
                    matcher,
                    accumulator.getPreviousUnitOrder(),
                    inputText);
            accumulator.add(component, inputText);
            cursor = component.endIndex();
        }

        rejectInvalidRemainder(
                normalizedText.substring(cursor),
                accumulator.hasParsedComponent(),
                inputText);

        return accumulator.toDurationPeriod();
    }

    /**
     * Skips whitespace between duration components.
     *
     * @param normalizedText normalized duration text
     * @param cursor current parsing position
     * @param parsedComponent whether a component has already been parsed
     * @param inputText original duration text used in error messages
     * @return next non-whitespace parsing position
     * @throws LuckyNoInputException if components are adjacent without spacing
     */
    private static int skipWhitespace(
            String normalizedText,
            int cursor,
            boolean parsedComponent,
            String inputText)
            throws LuckyNoInputException {
        int componentStart = cursor;
        while (cursor < normalizedText.length()
                && Character.isWhitespace(normalizedText.charAt(cursor))) {
            cursor++;
        }
        if (parsedComponent && cursor == componentStart) {
            throw invalidDurationFormat(inputText);
        }
        return cursor;
    }

    /**
     * Parses and validates the component at the matcher's current position.
     *
     * @param matcher matcher positioned at the next component
     * @param previousUnitOrder order of the preceding unit
     * @param inputText original duration text used in error messages
     * @return parsed duration component
     * @throws LuckyNoInputException if the component violates duration rules
     */
    private static ParsedComponent parseNextComponent(
            Matcher matcher,
            int previousUnitOrder,
            String inputText)
            throws LuckyNoInputException {
        BigDecimal amount = parseAmount(matcher.group(1), inputText);
        DurationPeriodUnit unit = DurationPeriodUnit.fromText(
                matcher.group(2), inputText);
        validateComponent(amount, unit, previousUnitOrder, inputText);

        return new ParsedComponent(amount, unit, matcher.end());
    }

    /**
     * Validates the value and ordering rules for one duration component.
     *
     * @param amount numeric amount in the component
     * @param unit unit represented by the component
     * @param previousUnitOrder order of the preceding unit
     * @param inputText original duration text used in error messages
     * @throws LuckyNoInputException if the component is invalid
     */
    private static void validateComponent(
            BigDecimal amount,
            DurationPeriodUnit unit,
            int previousUnitOrder,
            String inputText)
            throws LuckyNoInputException {
        if (amount.signum() < 0) {
            throw negativeDuration();
        }
        if (amount.scale() > 0
                && (unit == DurationPeriodUnit.MONTH
                || unit == DurationPeriodUnit.YEAR)) {
            throw new LuckyNoInputException(
                    LuckyNoQuips.decimalCalendarDurationMessage());
        }
        if (!unit.allowsDecimal() && amount.scale() > 0) {
            throw invalidDurationFormat(inputText);
        }
        if (unit.getOrder() <= previousUnitOrder) {
            throw invalidDurationFormat(inputText);
        }
    }

    /**
     * Rejects malformed text remaining after the last parsed component.
     *
     * @param remainingText unparsed trailing text
     * @param parsedComponent whether at least one component was parsed
     * @param inputText original duration text used in error messages
     * @throws LuckyNoInputException if no component or a numeric remainder exists
     */
    private static void rejectInvalidRemainder(
            String remainingText,
            boolean parsedComponent,
            String inputText)
            throws LuckyNoInputException {
        String trimmedText = remainingText.trim();
        if (!parsedComponent || trimmedText.matches("-?\\d.*")) {
            throw invalidDurationFormat(inputText);
        }
    }

    /** Represents one duration component matched from the input. */
    private record ParsedComponent(
            BigDecimal amount,
            DurationPeriodUnit unit,
            int endIndex) {
    }

    /** Accumulates validated calendar and clock duration components. */
    private static final class DurationAccumulator {
        private int years;
        private int months;
        private int days;
        private Duration timeAmount = Duration.ZERO;
        private int previousUnitOrder = -1;
        private boolean parsedComponent;

        /**
         * Adds one validated component to the accumulated duration.
         *
         * @param component validated duration component
         * @param inputText original duration text used in error messages
         * @throws LuckyNoInputException if the component exceeds supported precision
         */
        private void add(
                ParsedComponent component,
                String inputText)
                throws LuckyNoInputException {
            try {
                switch (component.unit()) {
                    case YEAR -> years = component.amount().intValueExact();
                    case MONTH -> months = component.amount().intValueExact();
                    case WEEK -> days = Math.addExact(
                            days,
                            Math.multiplyExact(
                                    component.amount().intValueExact(), 7));
                    case DAY -> addDays(component.amount(), inputText);
                    case HOUR -> timeAmount = timeAmount.plus(
                            toDuration(
                                    component.amount(),
                                    NANOS_PER_HOUR,
                                    inputText));
                    case MINUTE -> timeAmount = timeAmount.plus(
                            toDuration(
                                    component.amount(),
                                    NANOS_PER_MINUTE,
                                    inputText));
                    default -> throw invalidDurationFormat(inputText);
                }
            } catch (ArithmeticException exception) {
                throw invalidDurationFormat(inputText);
            }

            previousUnitOrder = component.unit().getOrder();
            parsedComponent = true;
        }

        /**
         * Adds a potentially fractional day to the accumulated duration.
         *
         * @param amount day amount to add
         * @param inputText original duration text used in error messages
         * @throws LuckyNoInputException if the fractional part exceeds precision
         */
        private void addDays(
                BigDecimal amount,
                String inputText)
                throws LuckyNoInputException {
            days = amount.setScale(0, RoundingMode.FLOOR).intValueExact();
            timeAmount = timeAmount.plus(
                    toDuration(
                            amount.subtract(BigDecimal.valueOf(days)),
                            NANOS_PER_DAY,
                            inputText));
        }

        /**
         * Checks whether at least one duration component was parsed.
         *
         * @return true when a component has been parsed
         */
        private boolean hasParsedComponent() {
            return parsedComponent;
        }

        /**
         * Returns the order of the previously parsed unit.
         *
         * @return previous unit order, or -1 when no unit was parsed
         */
        private int getPreviousUnitOrder() {
            return previousUnitOrder;
        }

        /**
         * Converts accumulated values into a duration period.
         *
         * @return accumulated calendar and clock amounts
         */
        private DurationPeriod toDurationPeriod() {
            return new DurationPeriod(
                    Period.of(years, months, days),
                    timeAmount);
        }
    }

    /**
     * Converts a parsed numeric component into a decimal amount.
     *
     * @param amountText numeric component text
     * @param inputText original duration text used in error messages
     * @return parsed numeric amount
     * @throws LuckyNoInputException if the number is malformed
     */
    private static BigDecimal parseAmount(
            String amountText,
            String inputText)
            throws LuckyNoInputException {
        try {
            return new BigDecimal(amountText);
        } catch (NumberFormatException exception) {
            throw invalidDurationFormat(inputText);
        }
    }

    /**
     * Converts a fixed-length decimal unit into nanosecond precision.
     *
     * @param amount decimal unit amount
     * @param nanosPerUnit nanoseconds represented by one unit
     * @param inputText original duration text used in error messages
     * @return converted duration
     * @throws LuckyNoInputException if the amount exceeds supported precision
     */
    private static Duration toDuration(
            BigDecimal amount, long nanosPerUnit, String inputText)
            throws LuckyNoInputException {
        try {
            long nanos = amount.multiply(BigDecimal.valueOf(nanosPerUnit))
                    .longValueExact();
            return Duration.ofNanos(nanos);
        } catch (ArithmeticException exception) {
            throw invalidDurationFormat(inputText);
        }
    }

    /**
     * Creates the dedicated negative-duration exception.
     *
     * @return negative-duration exception
     */
    private static LuckyNoInputException negativeDuration() {
        return new LuckyNoInputException(
                "Siao ah time where got negative one");
    }

    /**
     * Creates an invalid-duration exception with the original input.
     *
     * @param durationText original duration text supplied by the user
     * @return invalid-duration exception
     */
    private static LuckyNoInputException invalidDurationFormat(
            String durationText) {
        return new LuckyNoInputException(
                LuckyNoQuips.invalidDurationMessage(durationText));
    }
}
