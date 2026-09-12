package minweeder.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import minweeder.command.CommandWord;
import minweeder.exception.MinweederException;
import minweeder.task.TaskList;

/**
 * Parses raw user input into commands and arguments that the application
 * can act on, throwing {@link MinweederException} on invalid input.
 */
public class Parser {
    private static final DateTimeFormatter DEADLINE_INPUT_FORMAT =
            DateTimeFormatter.ofPattern("d/M/yyyy HHmm");
    private static final DateTimeFormatter QUERY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("d/M/yyyy");

    /**
     * Splits a line of user input into the command word and its remaining arguments.
     *
     * @param command the raw input line.
     * @return an array of at most two elements: the command word, and the rest of the line.
     */
    public static String[] splitCommand(String command) {
        return command.split(" ", 2);
    }

    /**
     * Resolves the command word from a split command.
     *
     * @param breakdown the result of {@link #splitCommand(String)}.
     * @return the recognized command word.
     * @throws MinweederException if the first token is not a known command.
     */
    public static CommandWord parseCommandWord(String[] breakdown) throws MinweederException {
        return CommandWord.getCommandWord(breakdown[0]);
    }

    /**
     * Extracts the argument text for a command, requiring it to be non-blank.
     *
     * @param breakdown the result of {@link #splitCommand(String)}.
     * @param commandWord the name of the command, used in the error message.
     * @param example an example of valid usage, shown in the error message.
     * @return the trimmed argument text.
     * @throws MinweederException if no argument text was supplied.
     */
    public static String requireArguments(String[] breakdown, String commandWord, String example)
            throws MinweederException {
        if (breakdown.length < 2 || breakdown[1].isBlank()) {
            throw new MinweederException("a " + commandWord + " needs a description. e.g. "
                    + example);
        }
        return breakdown[1].trim();
    }

    /**
     * Splits text around a required keyword, such as {@code /by} or {@code /from}.
     *
     * @param text the text to split.
     * @param keyword the keyword that must separate the two halves.
     * @param example an example of valid usage, shown in the error message.
     * @return a two-element array of the trimmed text before and after the keyword.
     * @throws MinweederException if the keyword is missing or either side is blank.
     */
    public static String[] requireKeyword(String text, String keyword, String example)
            throws MinweederException {
        String separator = " " + keyword + " ";
        String[] parts = text.split(separator, 2);
        if (parts.length < 2 || parts[0].isBlank() || parts[1].isBlank()) {
            throw new MinweederException("You need something on either side of " + keyword
                    + ". e.g. " + example);
        }
        if (parts[1].contains(separator)) {
            throw new MinweederException("You can only use " + keyword + " once. e.g. " + example);
        }
        return new String[] {parts[0].trim(), parts[1].trim()};
    }

    /**
     * Checks that a command which takes no arguments was not given any.
     *
     * @param breakdown the result of {@link #splitCommand(String)}.
     * @param commandWord the name of the command, used in the error message.
     * @throws MinweederException if extra, non-blank text follows the command word.
     */
    public static void requireNoArguments(String[] breakdown, String commandWord) throws MinweederException {
        if (breakdown.length > 1 && !breakdown[1].isBlank()) {
            throw new MinweederException("the " + commandWord + " command doesn't take any extra input.");
        }
    }

    /**
     * Parses a 1-based task number from user input and converts it to a valid
     * 0-based index into the task list.
     *
     * @param breakdown the result of {@link #splitCommand(String)}.
     * @param tasks the task list the index will be used against.
     * @return the 0-based index of the referenced task.
     * @throws MinweederException if no number is given, it isn't a number,
     *     the list is empty, or the number is out of range.
     */
    public static int parseIndex(String[] breakdown, TaskList tasks) throws MinweederException {
        if (breakdown.length < 2 || breakdown[1].isBlank()) {
            throw new MinweederException("Which task? Choose a number, e.g. "
                    + breakdown[0] + " 2");
        }
        String argument = breakdown[1].trim();
        int taskNumber;
        try {
            taskNumber = Integer.parseInt(argument);
        } catch (NumberFormatException e) {
            throw new MinweederException("'" + argument + "' is not a number!");
        }
        if (tasks.size() == 0) {
            throw new MinweederException("your list is empty, so there is nothing to "
                    + breakdown[0] + " yet.");
        }
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new MinweederException("you have " + tasks.size()
                    + " tasks, so pick a number from 1 to " + tasks.size() + ".");
        }
        int index = taskNumber - 1;
        assert index >= 0 && index < tasks.size() : "computed index out of range: " + index;
        return index;
    }

    /**
     * Parses the {@code /by} date-time argument for a deadline.
     *
     * @param text the text to parse, expected in {@code d/M/yyyy HHmm} format.
     * @param example an example of valid usage, shown in the error message.
     * @return the parsed date-time.
     * @throws MinweederException if the text does not match the expected format.
     */
    public static LocalDateTime parseDeadlineBy(String text, String example) throws MinweederException {
        try {
            return LocalDateTime.parse(text, DEADLINE_INPUT_FORMAT);
        } catch (DateTimeParseException e) {
            throw new MinweederException("please use d/M/yyyy HHmm for the date, e.g. " + example);
        }
    }

    /**
     * Determines which of {@code /to} or {@code /from} separates a loan command's
     * amount from the other person's name.
     *
     * @param arguments the loan command's argument text.
     * @param example an example of valid usage, shown in the error message.
     * @return {@code "/to"} if the loan was given, or {@code "/from"} if it was received.
     * @throws MinweederException if neither keyword is present.
     */
    public static String parseLoanKeyword(String arguments, String example) throws MinweederException {
        if (arguments.contains(" /to ")) {
            return "/to";
        } else if (arguments.contains(" /from ")) {
            return "/from";
        }
        throw new MinweederException("a loan needs /to or /from to say who it's between. e.g. " + example);
    }

    /**
     * Parses the amount argument for a loan.
     *
     * @param text the text to parse, expected to be a non-negative number.
     * @param example an example of valid usage, shown in the error message.
     * @return the parsed amount.
     * @throws MinweederException if the text is not a non-negative number.
     */
    public static double parseLoanAmount(String text, String example) throws MinweederException {
        double amount;
        try {
            amount = Double.parseDouble(text);
        } catch (NumberFormatException e) {
            throw new MinweederException("'" + text + "' is not a valid amount. e.g. " + example);
        }
        if (Double.isNaN(amount) || Double.isInfinite(amount)) {
            throw new MinweederException("'" + text + "' is not a valid amount. e.g. " + example);
        }
        if (amount < 0) {
            throw new MinweederException("a loan amount can't be negative. e.g. " + example);
        }
        return amount;
    }

    /**
     * Parses the date argument for the {@code on} command.
     *
     * @param text the text to parse, expected in {@code d/M/yyyy} format.
     * @param example an example of valid usage, shown in the error message.
     * @return the parsed date.
     * @throws MinweederException if the text does not match the expected format.
     */
    public static LocalDate parseOnDate(String text, String example) throws MinweederException {
        try {
            return LocalDate.parse(text, QUERY_DATE_FORMAT);
        } catch (DateTimeParseException e) {
            throw new MinweederException("please use d/M/yyyy for the date, e.g. " + example);
        }
    }
}
