package dingleberry.parser;

import dingleberry.command.AddCommand;
import dingleberry.command.Command;
import dingleberry.command.DeleteCommand;
import dingleberry.command.ExitCommand;
import dingleberry.command.FindCommand;
import dingleberry.command.ListCommand;
import dingleberry.command.MarkCommand;
import dingleberry.command.UnmarkCommand;
import dingleberry.exception.DingleberryException;

/**
 * Parses raw user input into validated, executable commands.
 */
public final class Parser {
    /** Defines the separator between a command word and its arguments. */
    private static final String COMMAND_SEPARATOR = " ";

    private Parser() {
    }

    /**
     * Parses a full line of user input into an executable {@link Command}.
     *
     * @param fullCommand the raw user input to interpret.
     * @return the validated command object ready to execute.
     * @throws DingleberryException if the input is blank, unknown, or invalid.
     */
    public static Command parse(final String fullCommand)
            throws DingleberryException {
        if (fullCommand == null) {
            throw new DingleberryException(
                    "Please give me a command or a task description.");
        }
        String input = normalizeInput(fullCommand);
        if (input.isBlank()) {
            throw new DingleberryException(
                    "Please give me a command or a task description.");
        }

        CommandWord commandWord = CommandWord.fromInput(input);
        if (commandWord == null) {
            throw new DingleberryException(
                    "I don't recognize that command. Use 'todo', 'list',"
                            + " 'mark', 'unmark', 'delete', 'deadline',"
                            + " or 'event'.",
                    DingleberryException.ErrorType.WRONG_COMMAND);
        }
        return createCommand(commandWord, input);
    }

    /**
     * Constructs the command and delegates task-specific argument parsing.
     *
     * @param commandWord the recognized command word.
     * @param input the normalized command line.
     * @return the validated executable command.
     * @throws DingleberryException if command arguments are invalid.
     */
    private static Command createCommand(final CommandWord commandWord,
            final String input) throws DingleberryException {
        String argument = extractArgument(commandWord, input);
        return switch (commandWord) {
            case BYE -> new ExitCommand();
            case LIST -> {
                requireNoParameters(commandWord, argument);
                yield new ListCommand();
            }
            case FIND -> new FindCommand(parseKeyword(commandWord, argument));
            case DELETE -> new DeleteCommand(
                    parseTaskNumber(commandWord, argument));
            case MARK -> new MarkCommand(
                    parseTaskNumber(commandWord, argument));
            case UNMARK -> new UnmarkCommand(
                    parseTaskNumber(commandWord, argument));
            case TODO -> new AddCommand(TaskParser.parseTodo(argument));
            case DEADLINE -> new AddCommand(TaskParser.parseDeadline(argument));
            case EVENT -> new AddCommand(TaskParser.parseEvent(argument));
            default -> throw new DingleberryException(
                    "Unhandled command: " + commandWord);
        };
    }

    /**
     * Parses the keyword following a "find" command.
     *
     * @param command the find command being parsed.
     * @param keyword the text following the command word.
     * @return the keyword following the command.
     * @throws DingleberryException if the keyword is blank.
     */
    private static String parseKeyword(final CommandWord command,
            final String keyword)
            throws DingleberryException {
        if (keyword.isBlank()) {
            throw new DingleberryException(
                    "'" + command.keyword() + "' needs a keyword.");
        }
        return keyword;
    }

    /**
     * Rejects extra text after a command that takes no parameters, e.g.
     * "list".
     *
     * @param command the command being validated.
     * @param argument the text following the command word.
     * @throws DingleberryException if the command has unexpected parameters.
     */
    private static void requireNoParameters(final CommandWord command,
            final String argument)
            throws DingleberryException {
        if (!argument.isBlank()) {
            throw new DingleberryException(
                    "'" + command.keyword() + "' does not accept parameters.");
        }
    }

    /**
     * Parses the one-based task number following a command such as "delete",
     * "mark", or "unmark".
     *
     * @param command the command being parsed.
     * @param taskNumberText the text following the command word.
     * @return the one-based index supplied by the user.
     * @throws DingleberryException if the number is missing or invalid.
     */
    private static int parseTaskNumber(final CommandWord command,
            final String taskNumberText)
            throws DingleberryException {
        if (taskNumberText.isBlank()) {
            throw new DingleberryException(
                    "'" + command.keyword() + "' needs a task number.");
        }
        try {
            int taskNumber = Integer.parseInt(taskNumberText);
            if (taskNumber < 1) {
                throw new DingleberryException(
                        "'" + command.keyword()
                                + "' needs a positive task number.");
            }
            return taskNumber;
        } catch (NumberFormatException e) {
            throw new DingleberryException(
                    "'" + command.keyword()
                            + "' needs a numeric task number.");
        }
    }

    /**
     * Returns input with harmless surrounding and repeated whitespace removed.
     *
     * @param input the raw user input.
     * @return the normalized input.
     */
    private static String normalizeInput(final String input) {
        return input.trim().replaceAll("\\s+", " ");
    }

    /**
     * Returns the trimmed text after a command keyword, or an empty string when
     * no value follows the command.
     *
     * @param command the command to strip.
     * @param input the normalized command line.
     * @return the remaining argument text after the command keyword.
     */
    private static String extractArgument(final CommandWord command,
            final String input) {
        if (input.length() <= command.keyword().length()) {
            return "";
        }
        int startIndex = command.keyword().length()
                + COMMAND_SEPARATOR.length();
        return input.substring(startIndex).trim();
    }
}
