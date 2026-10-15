package ayre;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ayre.enums.CommandType;
import ayre.exceptions.UnknownCommandException;
import ayre.exceptions.WrongNumberOfArgumentsException;

/**
 * Handles the parsing of user input, involving tokenizing and validating the arguments/commands.
 * The parser does not need to hold any information as its only purpose is
 * to take a String as input and give back a Command and list of arguments as output.
 */
public class Parser {
    private static final int SPLIT_ONCE_LIMIT = 2; // String::split() splits n - 1 times so split once requires 2
    private static final int COMMAND_TYPE_INDEX = 0;
    private static final int NAME_INDEX = 0;
    private static final int ARGUMENT_STRING_INDEX = 1;
    private static final int DATES_INDEX = 1;
    /**
     * Returns the matching Command and its arguments from a user-inputted String as a ParsedInput Record.
     * Expected that the output arguments can be passed into the corresponding methods without type errors.
     *
     * @param input The String entered by the user via command line.
     * @return Record comprising the Command and its arguments.
     * @throws UnknownCommandException If no matching Command is found.
     * @throws WrongNumberOfArgumentsException If wrong number of arguments are parsed.
     */
    public static ParsedInput parseInput(String input) throws UnknownCommandException, WrongNumberOfArgumentsException {
        String[] cmdWithArgs = input.trim().split("\\s+", SPLIT_ONCE_LIMIT);
        CommandType cmd = CommandType.parseCommand(cmdWithArgs[COMMAND_TYPE_INDEX]);
        if (cmd == null) {
            throw new UnknownCommandException("~ ...Raven, this command was not found in the Coral Collective. "
                    + "Was it a mistake?");
        }
        List<String> args = Parser.tokenizeArguments(cmd, input);
        assert args.size() == cmd.getNumArgs() : "Arguments list should be of the same size "
                + "as number of arguments required by the CommandType";
        return new ParsedInput(cmd, args);
    }

    /**
     * Splits the user-inputted String into the corresponding arguments of the Command.
     * @param cmd Command parsed from input.
     * @param input User-inputted String.
     * @return List of arguments as Strings.
     * @throws WrongNumberOfArgumentsException If wrong number of arguments are detected after tokenizing.
     */
    private static List<String> tokenizeArguments(CommandType cmd, String input)
            throws WrongNumberOfArgumentsException {
        if (cmd.requiresArgs()) { // filters in all commands that requires argument input
            String[] cmdWithArgs = input.trim().split("\\s+", SPLIT_ONCE_LIMIT);
            if (cmdWithArgs.length == 1) {
                throw new WrongNumberOfArgumentsException("~ Raven, some parameters are missing. I don't have "
                        + "enough information to carry out this command.");
            }
            String argLine = cmdWithArgs[ARGUMENT_STRING_INDEX]; // take the latter half of the split input as args
            return parseArguments(cmd, argLine);
        } else { // BYE and LIST get filtered out to here
            if (input.trim().split("\\s+").length != 1) { // no extra arguments to be passed with BYE & LIST
                throw new WrongNumberOfArgumentsException("~ Raven, there seems to be some extra parameters"
                        + " mixed in. Please try again.");
            }
            return new ArrayList<>();
        }
    }

    /**
     * Parses user-inputted arguments to the command to ensure the correct number of arguments are given.
     * Logically this only involves the commands that require 1 or more arguments.
     *
     * @param cmd CommandType involved.
     * @param argLine User input String containing what should be the arguments.
     * @return List which elements correspond to the arguments in order.
     * @throws WrongNumberOfArgumentsException If wrong number of arguments are found while parsing.
     */
    private static List<String> parseArguments(CommandType cmd, String argLine) throws WrongNumberOfArgumentsException {
        return switch (cmd) {
            case FIND, MARK, UNMARK, DELETE, TODO -> List.of(argLine);
            case DEADLINE -> parseDeadlineArguments(argLine);
            case EVENT -> parseEventArguments(argLine);
            default -> throw new AssertionError("<<Main System>> Warning: "
                    + "A program invariant has been breached.");
        };
    }

    /**
     * Parses the task name and due date from a deadline argument line.
     *
     * @param argLine User input containing a task name and {@code /by} date.
     * @return The deadline name and due date in argument order.
     * @throws WrongNumberOfArgumentsException If the input does not contain exactly one deadline separator.
     */
    private static List<String> parseDeadlineArguments(String argLine) throws WrongNumberOfArgumentsException {
        String[] deadlineArgs = splitExactlyOnce(argLine,
                "\\s+/by\\s+|\\s+/b\\s+", "deadline NAME-OF-TASK /by yyyy-mm-dd");
        return Arrays.asList(deadlineArgs);
    }

    /**
     * Parses the task name, start date, and end date from an event argument line.
     *
     * @param argLine User input containing an event name, {@code /from} date, and {@code /to} date.
     * @return The event name and dates in argument order.
     * @throws WrongNumberOfArgumentsException If either event separator is missing or repeated.
     */
    private static List<String> parseEventArguments(String argLine) throws WrongNumberOfArgumentsException {
        String[] eventArgsFromSplit = splitExactlyOnce(argLine,
                "\\s+/from\\s+|\\s+/f\\s+", "event NAME-OF-TASK /from yyyy-mm-dd /to yyyy-mm-dd");
        List<String> arguments = new ArrayList<>();
        arguments.add(eventArgsFromSplit[NAME_INDEX]); // add name
        String[] eventArgsToSplit = splitExactlyOnce(eventArgsFromSplit[DATES_INDEX],
                "\\s+/to\\s+|\\s+/t\\s+", "event NAME-OF-TASK /from yyyy-mm-dd /to yyyy-mm-dd");
        arguments.addAll(Arrays.asList(eventArgsToSplit)); // add the dates
        return arguments;
    }

    /**
     * Splits an input string and verifies that the delimiter occurs exactly once.
     *
     * @param input String to split.
     * @param delimiter Regular expression identifying the delimiter.
     * @param format User-facing command format shown when validation fails.
     * @return The two parts produced by the split.
     * @throws WrongNumberOfArgumentsException If the delimiter does not produce exactly two parts.
     */
    private static String[] splitExactlyOnce(String input, String delimiter, String format)
            throws WrongNumberOfArgumentsException {
        String[] splitInput = input.split(delimiter);
        if (splitInput.length != 2) {
            throw new WrongNumberOfArgumentsException("~ Raven... please follow the format: " + format);
        }
        return splitInput;
    }
}
