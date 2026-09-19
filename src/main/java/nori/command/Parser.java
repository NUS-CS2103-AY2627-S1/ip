package nori.command;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Optional;

import nori.NoriException;
import nori.task.DateRange;

/**
 * Interprets raw user input and creates the corresponding executable command.
 */
public class Parser {
    /**
     * The longest command Nori reads.
     *
     * Far longer than any real task needs, and short enough that a pasted
     * document cannot be written into the storage file or carried through
     * every later reload of it.
     */
    private static final int MAX_COMMAND_LENGTH = 500;

    /** Shown alongside every date-range complaint, so a correction is always in view. */
    private static final String LIST_EXAMPLE = "\"list /from 2019-01-01 /to 2021-01-01\"";

    /** Prevents instantiation of this stateless parser. */
    private Parser() {
    }

    /**
     * Creates an executable command from trimmed user input.
     *
     * @param input the trimmed user input.
     * @return the command represented by the input.
     */
    public static Command parse(String input) {
        assert input != null : "Parsing starts only after a user interface has supplied a command line.";
        assert input.equals(input.trim()) : "Both user interfaces trim a command line before parsing it.";

        Optional<String> inputError = findInputError(input);
        if (inputError.isPresent()) {
            return Commands.createRejected(inputError.get());
        }

        CommandType commandType = findCommandType(input);
        if (commandType == null) {
            return Commands.createUnknown(input);
        }

        String details = getCommandDetails(input, commandType.getKeyword());
        return Commands.create(commandType, details);
    }

    /**
     * Parses the ISO-8601 date supplied to the {@code on} command.
     *
     * @param dateInput the date text after the {@code on} command.
     * @return the parsed date.
     * @throws NoriException if the date is missing or invalid.
     */
    public static LocalDate parseDate(String dateInput) throws NoriException {
        if (dateInput.isEmpty()) {
            throw new NoriException("NOOT?! \"on\" needs a date. Try \"on 2019-10-15\".");
        }
        try {
            return LocalDate.parse(dateInput);
        } catch (DateTimeParseException exception) {
            throw new NoriException("NOOT?! I cannot understand \"" + dateInput + "\" as a date."
                    + " Use a date like \"2019-10-15\".");
        }
    }

    /**
     * Parses the inclusive date range supplied to the {@code list} command.
     *
     * @param listDetails the text after the {@code list} command.
     * @return the parsed inclusive date range.
     * @throws NoriException if the range format, dates, or order is invalid.
     */
    public static DateRange parseListDateRange(String listDetails) throws NoriException {
        CommandOptions options = CommandOptions.parse(listDetails);
        checkDateRangeOptions(options);

        String fromInput = options.getValue(CommandOptions.OPTION_FROM);
        String toInput = options.getValue(CommandOptions.OPTION_TO);
        if (fromInput.isEmpty()) {
            throw new NoriException("NOOT?! \"/from\" needs a start date."
                    + " Try " + LIST_EXAMPLE + ".");
        }
        if (toInput.isEmpty()) {
            throw new NoriException("NOOT?! \"/to\" needs an end date."
                    + " Try " + LIST_EXAMPLE + ".");
        }

        LocalDate fromDate = parseRangeDate(fromInput, "/from");
        LocalDate toDate = parseRangeDate(toInput, "/to");
        if (toDate.isBefore(fromDate)) {
            throw new NoriException("NOOT?! The \"/to\" date cannot be before the \"/from\" date."
                    + " Time only waddles forward.");
        }
        return new DateRange(fromDate, toDate);
    }

    /**
     * Checks that a date-range list opens with "/from" and holds "/from" and "/to" once each.
     *
     * @param options the options read from the text after the {@code list} command.
     * @throws NoriException if an option is missing, repeated, out of place, or not one a list uses.
     */
    private static void checkDateRangeOptions(CommandOptions options) throws NoriException {
        boolean isFromFirst = !options.getOptionNames().isEmpty()
                && options.getOptionNames().get(0).equals(CommandOptions.OPTION_FROM)
                && options.getDescription().isEmpty();
        if (!isFromFirst) {
            throw new NoriException("NOOT?! Use either \"list\" or " + LIST_EXAMPLE + ".");
        }
        Optional<String> unexpectedOption = options.findUnexpectedOption(
                CommandOptions.OPTION_FROM, CommandOptions.OPTION_TO);
        if (unexpectedOption.isPresent()) {
            throw new NoriException("NOOT?! A date-range list does not use \""
                    + unexpectedOption.get() + "\". Try " + LIST_EXAMPLE + ".");
        }
        Optional<String> repeatedOption = options.findRepeatedOption();
        if (repeatedOption.isPresent()) {
            throw new NoriException("NOOT?! A date-range list takes only one \""
                    + repeatedOption.get() + "\". Try " + LIST_EXAMPLE + ".");
        }
        if (!options.hasOption(CommandOptions.OPTION_TO)) {
            throw new NoriException("NOOT?! A date-range list needs \"/to\" and an end date."
                    + " Try " + LIST_EXAMPLE + ".");
        }
    }

    /**
     * Reports input Nori will not read at all, whatever command it names.
     *
     * @param input the trimmed user input.
     * @return the correction to show the user, or empty when the input is readable.
     */
    private static Optional<String> findInputError(String input) {
        if (input.length() > MAX_COMMAND_LENGTH) {
            return Optional.of("GIANT NOOT! That command is " + input.length()
                    + " characters long. Keep it to " + MAX_COMMAND_LENGTH
                    + " or fewer; my flippers are small.");
        }
        if (input.chars().anyMatch(Character::isISOControl)) {
            return Optional.of("NOOT?! That command hides a tab or control character."
                    + " My flippers read plain text only.");
        }
        return Optional.empty();
    }

    /**
     * Finds the command type at the start of an input line.
     *
     * @param input the trimmed user input.
     * @return the matching type, or {@code null} if the input is unrecognized.
     */
    private static CommandType findCommandType(String input) {
        return Arrays.stream(CommandType.values())
                .filter(commandType -> isCommandTypeOf(input, commandType))
                .findFirst()
                .orElse(null);
    }

    /**
     * Returns whether an input line begins with a command type's keyword.
     *
     * @param input the trimmed user input.
     * @param commandType the command type to test the input against.
     * @return {@code true} if the input is that command, with or without details.
     */
    private static boolean isCommandTypeOf(String input, CommandType commandType) {
        String keyword = commandType.getKeyword();
        boolean isExactMatch = input.equals(keyword);
        boolean isPrefixMatch = commandType != CommandType.BYE && input.startsWith(keyword + " ");
        return isExactMatch || isPrefixMatch;
    }

    /**
     * Returns the trimmed text after a command keyword.
     *
     * @param input the complete user input.
     * @param commandKeyword the command keyword.
     * @return the command details, without surrounding whitespace.
     */
    private static String getCommandDetails(String input, String commandKeyword) {
        assert input.startsWith(commandKeyword)
                : "findCommandType matched this keyword, so the details start after it.";
        return input.substring(commandKeyword.length()).trim();
    }

    /**
     * Parses one date from a date-range list command.
     *
     * @param dateInput the date text to parse.
     * @param rangePart the range separator introducing the date.
     * @return the parsed date.
     * @throws NoriException if the date is invalid.
     */
    private static LocalDate parseRangeDate(String dateInput, String rangePart) throws NoriException {
        assert !dateInput.isEmpty() : "parseListDateRange reports a missing range date before parsing it.";
        try {
            return LocalDate.parse(dateInput);
        } catch (DateTimeParseException exception) {
            throw new NoriException("NOOT?! I cannot understand \"" + dateInput + "\" as the "
                    + rangePart + " date. Use a date like \"2019-10-15\".");
        }
    }
}
