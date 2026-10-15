package bingbong.parser;

import bingbong.command.AddCommand;
import bingbong.command.Command;
import bingbong.command.DatesCommand;
import bingbong.command.DeleteCommand;
import bingbong.command.ExitCommand;
import bingbong.command.FindCommand;
import bingbong.command.HelpCommand;
import bingbong.command.ListCommand;
import bingbong.command.MarkCommand;
import bingbong.command.UnmarkCommand;
import bingbong.exception.BingBongException;

/**
 * Reads user inputs and decides what command the user wants to execute. It splits the text to understand the command
 * word and its arguments.
 */
public class Parser {
    private static final String SPACE_DELIMITER = " ";
    private static final String DEADLINE_DELIMITER = " /by ";
    private static final String EVENT_FROM_DELIMITER = " /from ";
    private static final String EVENT_TO_DELIMITER = " /to ";

    private static final int TODO_PREFIX_LENGTH = 5;
    private static final int DEADLINE_PREFIX_LENGTH = 9;
    private static final int EVENT_PREFIX_LENGTH = 6;
    private static final int MARK_PREFIX_LENGTH = 5;
    private static final int UNMARK_PREFIX_LENGTH = 7;
    private static final int DELETE_PREFIX_LENGTH = 7;
    private static final int FIND_PREFIX_LENGTH = 5;
    private static final int DATES_PREFIX_LENGTH = 6;

    private static final int MAXIMUM_SPLIT_PARTS = 2;
    private static final int FIRST_ARRAY_INDEX = 0;

    /**
     * Translates a command line into an executable command instance.
     *
     * @param fullCommand The unparsed text input entered by the user.
     * @return A concrete executable Command object matching the instruction word.
     * @throws BingBongException If the string is invalid or the command is not recognised.
     */
    public static Command parse(String fullCommand) throws BingBongException {
        CommandType type = getCommandType(fullCommand);

        switch (type) {
            case BYE:
                return new ExitCommand();
            case LIST:
                return new ListCommand();
            case MARK:
                validateSingleArgumentCommand(fullCommand, MARK_PREFIX_LENGTH, "mark");
                return new MarkCommand(fullCommand);
            case UNMARK:
                validateSingleArgumentCommand(fullCommand, UNMARK_PREFIX_LENGTH, "unmark");
                return new UnmarkCommand(fullCommand);
            case TODO:
                validateTodo(fullCommand);
                return new AddCommand(fullCommand, CommandType.TODO);
            case DEADLINE:
                return new AddCommand(fullCommand, CommandType.DEADLINE);
            case EVENT:
                return new AddCommand(fullCommand, CommandType.EVENT);
            case DELETE:
                validateSingleArgumentCommand(fullCommand, DELETE_PREFIX_LENGTH, "delete");
                return new DeleteCommand(fullCommand);
            case DATES:
                validateSingleArgumentCommand(fullCommand, DATES_PREFIX_LENGTH, "dates");
                return new DatesCommand(fullCommand);
            case FIND:
                validateSingleArgumentCommand(fullCommand, FIND_PREFIX_LENGTH, "find");
                return new FindCommand(fullCommand);
            case HELP:
                return new HelpCommand();
            default:
                throw new BingBongException("Unknown command. Please try again.");
        }
    }

    /**
     * Extracts the primary action keyword from an input string to determine its command type.
     *
     * @param in The raw input line string to evaluate.
     * @return The corresponding CommandType enum value, or UNKNOWN if invalid.
     */
    public static CommandType getCommandType(String in) {
        String commandWord = in.split(SPACE_DELIMITER, MAXIMUM_SPLIT_PARTS)[FIRST_ARRAY_INDEX];
        try {
            return CommandType.valueOf(commandWord.toUpperCase());
        } catch (IllegalArgumentException e) {
            return CommandType.UNKNOWN;
        }
    }

    /**
     * Extracts a task list index number from a parameter argument substring.
     *
     * @param in           The full raw input argument string.
     * @param prefixLength The length of the command word prefix string to chop off.
     * @return A 0-indexed integer pointer referencing a targeted task slot position.
     * @throws NumberFormatException If the remaining string parameter cannot parse into an integer.
     */
    public static int parseIndex(String in, int prefixLength) throws NumberFormatException {
        return Integer.parseInt(in.substring(prefixLength).trim()) - 1;
    }

    /**
     * Validates that todo commands have descriptive content.
     */
    private static void validateTodo(String in) throws BingBongException {
        if (in.length() <= TODO_PREFIX_LENGTH || in.substring(TODO_PREFIX_LENGTH).trim().isEmpty()) {
            throw new BingBongException("The description of a todo cannot be blank.");
        }
    }

    /**
     * Helper check to ensure simple argument commands have accurate parameters.
     */
    private static void validateSingleArgumentCommand(String in, int prefixLength, String name)
            throws BingBongException {
        if (in.length() <= prefixLength || in.substring(prefixLength).trim().isEmpty()) {
            throw new BingBongException("The argument parameters for " + name + " cannot be blank.");
        }
    }

    /**
     * Splits a deadline command string line into its description and target date components.
     *
     * @param in The full deadline command string line.
     * @return A String array containing exactly two elements: [description, byDate].
     * @throws BingBongException If descriptions are empty or the '/by' target timing keyword is missing.
     */
    public static String[] parseDeadline(String in) throws BingBongException {
        if (in.length() <= DEADLINE_PREFIX_LENGTH || in.substring(DEADLINE_PREFIX_LENGTH - 1).trim().isEmpty()) {
            throw new BingBongException("The description of a deadline cannot be blank.");
        }
        String content = in.substring(DEADLINE_PREFIX_LENGTH);

        if (content.split(DEADLINE_DELIMITER).length > MAXIMUM_SPLIT_PARTS) {
            throw new BingBongException("A deadline command cannot contain multiple '/by' flags.");
        }

        int byIndex = content.indexOf(DEADLINE_DELIMITER);
        if (byIndex == -1) {
            throw new BingBongException("A deadline must include a target timing using '/by'.");
        }
        String desc = content.substring(FIRST_ARRAY_INDEX, byIndex).trim();
        String by = content.substring(byIndex + DEADLINE_DELIMITER.length()).trim();
        if (desc.isEmpty() || by.isEmpty()) {
            throw new BingBongException(
                    "Missing fields. Accurate deadline description and target time are required.");
        }
        return new String[]{desc, by};
    }

    /**
     * Splits a event command string line into description, start date, and end date components.
     *
     * @param in The full event command string line.
     * @return A String array containing exactly three elements: [description, fromDate, toDate].
     * @throws BingBongException If fields are empty or the formatting time constraints are missing.
     */
    public static String[] parseEvent(String in) throws BingBongException {
        if (in.length() <= EVENT_PREFIX_LENGTH || in.substring(EVENT_PREFIX_LENGTH - 1).trim().isEmpty()) {
            throw new BingBongException("The description of an event cannot be blank.");
        }
        String content = in.substring(EVENT_PREFIX_LENGTH);

        if (content.split(EVENT_FROM_DELIMITER).length > MAXIMUM_SPLIT_PARTS
                || content.split(EVENT_TO_DELIMITER).length > MAXIMUM_SPLIT_PARTS) {
            throw new BingBongException("An event command cannot contain multiple '/from' or '/to' flags.");
        }

        int fromIndex = content.indexOf(EVENT_FROM_DELIMITER);
        int toIndex = content.indexOf(EVENT_TO_DELIMITER);
        if (fromIndex == -1 || toIndex == -1 || fromIndex > toIndex) {
            throw new BingBongException("An event requires valid time constraints using '/from' and '/to'.");
        }
        String desc = content.substring(FIRST_ARRAY_INDEX, fromIndex).trim();
        String from = content.substring(fromIndex + EVENT_FROM_DELIMITER.length(), toIndex).trim();
        String to = content.substring(toIndex + EVENT_TO_DELIMITER.length()).trim();
        if (desc.isEmpty() || from.isEmpty() || to.isEmpty()) {
            throw new BingBongException(
                    "Missing fields. Accurate event description, start, and end parameters are required.");
        }
        return new String[]{desc, from, to};
    }

    /**
     * Defines all valid command keywords.
     */
    public enum CommandType {
        LIST, MARK, UNMARK, TODO, DEADLINE, EVENT, DELETE, DATES, FIND, BYE, HELP, UNKNOWN
    }
}
