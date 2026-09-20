package axiom.parser;

import java.time.LocalDateTime;
import java.util.Arrays;

import axiom.AxiomException;
import axiom.command.Command;
import axiom.task.Deadline;
import axiom.task.Event;
import axiom.task.Todo;

/**
 * Represents a utility that makes sense of user commands and converts input into tasks or task indices.
 */
public class Parser {
    private static final String BY_FLAG = "/by";
    private static final String FROM_FLAG = "/from";
    private static final String TO_FLAG = "/to";
    private static final String TASK_NUMBER_PATTERN = "\\d+";
    private static final String DEADLINE_USAGE = "Usage: deadline <description> /by <time>";
    private static final String EVENT_USAGE = "Usage: event <description> /from <start> /to <end>";

    /**
     * Returns the command identified in a line of user input.
     *
     * @param input Full command line from the user.
     * @return The matching {@link Command}.
     */
    public Command getCommand(String input) {
        return Command.fromInput(input);
    }

    /**
     * Checks that a no-argument command was not given extra text.
     *
     * @param command Command being executed (e.g. {@link Command#LIST}).
     * @param input Full command line from the user.
     * @throws AxiomException If the command was given arguments.
     */
    public void requireNoArguments(Command command, String input) throws AxiomException {
        if (!command.getArgument(input).isEmpty()) {
            throw new AxiomException(command.getKeyword() + " does not take any arguments. Usage: "
                    + command.getKeyword());
        }
    }

    /**
     * Returns the validated one-based task number extracted from a command.
     *
     * @param command Command being executed (e.g. {@link Command#MARK}).
     * @param input Full command line from the user.
     * @param taskCount Number of tasks currently in the list.
     * @return The validated one-based task number.
     * @throws AxiomException If the argument is missing, extra, non-numeric, or out of range.
     */
    public int parseTaskNumber(Command command, String input, int taskCount) throws AxiomException {
        assert command == Command.MARK || command == Command.UNMARK || command == Command.DELETE
                : "parseTaskNumber is only for mark, unmark, and delete";
        assert taskCount >= 0 : "Task count cannot be negative";
        String argument = requireNonEmpty(command.getArgument(input),
                "Please specify which task to " + command.getKeyword()
                + ". Usage: " + command.getKeyword() + " <task number>");
        if (!argument.matches(TASK_NUMBER_PATTERN)) {
            throw new AxiomException("'" + argument + "' is not a valid task number. Usage: "
                    + command.getKeyword() + " <task number>");
        }
        try {
            int taskNumber = Integer.parseInt(argument);
            if (taskNumber < 1 || taskNumber > taskCount) {
                throw new AxiomException("That task number isn't in your list. You currently have "
                        + taskCount + " task(s).");
            }
            return taskNumber;
        } catch (NumberFormatException e) {
            throw new AxiomException("'" + argument + "' is not a valid task number. Usage: "
                    + command.getKeyword() + " <task number>");
        }
    }

    /**
     * Parses a {@code todo} command into a {@link Todo} task.
     *
     * @param input Full {@code todo} command line.
     * @return The parsed todo task.
     * @throws AxiomException If the description is missing or contains a pipe character.
     */
    public Todo parseTodo(String input) throws AxiomException {
        String description = requireNonEmpty(Command.TODO.getArgument(input),
                "A todo needs a description. Usage: todo <description>");
        return new Todo(requireSafeDescription(description));
    }

    /**
     * Parses a {@code deadline} command into a {@link Deadline} task.
     *
     * @param input Full {@code deadline} command line.
     * @return The parsed deadline task.
     * @throws AxiomException If the description or {@code /by} time is missing, repeated, or invalid.
     */
    public Deadline parseDeadline(String input) throws AxiomException {
        String[] tokens = argumentTokens(input);
        if (tokens.length == 0) {
            throw new AxiomException("A deadline needs a description and a /by time. " + DEADLINE_USAGE);
        }

        requireAllowedFlags(tokens, DEADLINE_USAGE, BY_FLAG);
        int byCount = countFlag(tokens, BY_FLAG);
        if (byCount == 0) {
            throw new AxiomException("A deadline must include /by. " + DEADLINE_USAGE);
        }
        if (byCount > 1) {
            throw new AxiomException("The /by time is specified more than once. " + DEADLINE_USAGE);
        }

        int byIndex = indexOfFlag(tokens, BY_FLAG);
        String description = requireNonEmpty(joinTokens(tokens, 0, byIndex),
                "A deadline needs a description. " + DEADLINE_USAGE);
        String by = requireNonEmpty(joinTokens(tokens, byIndex + 1, tokens.length),
                "A deadline needs a /by time. " + DEADLINE_USAGE);
        return new Deadline(requireSafeDescription(description), DateTimeParser.parse(by));
    }

    /**
     * Parses an {@code event} command into an {@link Event} task.
     *
     * @param input Full {@code event} command line.
     * @return The parsed event task.
     * @throws AxiomException If the description, times, flags, or date order is missing or invalid.
     */
    public Event parseEvent(String input) throws AxiomException {
        String[] tokens = argumentTokens(input);
        if (tokens.length == 0) {
            throw new AxiomException("An event needs a description, /from, and /to times. " + EVENT_USAGE);
        }

        requireAllowedFlags(tokens, EVENT_USAGE, FROM_FLAG, TO_FLAG);
        int fromCount = countFlag(tokens, FROM_FLAG);
        int toCount = countFlag(tokens, TO_FLAG);
        if (fromCount == 0 || toCount == 0) {
            throw new AxiomException("An event must include /from and /to. " + EVENT_USAGE);
        }
        if (fromCount > 1) {
            throw new AxiomException("The /from time is specified more than once. " + EVENT_USAGE);
        }
        if (toCount > 1) {
            throw new AxiomException("The /to time is specified more than once. " + EVENT_USAGE);
        }

        int fromIndex = indexOfFlag(tokens, FROM_FLAG);
        int toIndex = indexOfFlag(tokens, TO_FLAG);
        if (fromIndex > toIndex) {
            throw new AxiomException("Please specify /from before /to. " + EVENT_USAGE);
        }

        String description = requireNonEmpty(joinTokens(tokens, 0, fromIndex),
                "An event needs a description. " + EVENT_USAGE);
        String from = requireNonEmpty(joinTokens(tokens, fromIndex + 1, toIndex),
                "An event needs a /from time. " + EVENT_USAGE);
        String to = requireNonEmpty(joinTokens(tokens, toIndex + 1, tokens.length),
                "An event needs a /to time. " + EVENT_USAGE);
        LocalDateTime fromTime = DateTimeParser.parse(from);
        LocalDateTime toTime = DateTimeParser.parse(to);
        requireChronologicalEventTimes(fromTime, toTime);
        return new Event(requireSafeDescription(description), fromTime, toTime);
    }

    /**
     * Returns the keyword to search for in a {@code find} command.
     *
     * @param input Full {@code find} command line.
     * @return The keyword to search for.
     * @throws AxiomException If the keyword is missing.
     */
    public String parseFindKeyword(String input) throws AxiomException {
        return requireNonEmpty(Command.FIND.getArgument(input),
                "A find needs a keyword. Usage: find <keyword>");
    }

    /**
     * Checks that an event starts before it ends.
     *
     * @param from Event start date and time.
     * @param to Event end date and time.
     * @throws AxiomException If {@code from} is the same as or later than {@code to}.
     */
    public static void requireChronologicalEventTimes(LocalDateTime from, LocalDateTime to) throws AxiomException {
        if (!from.isBefore(to)) {
            throw new AxiomException("The event /from time must be earlier than the /to time.");
        }
    }

    /**
     * Returns the argument tokens after the command keyword.
     *
     * @param input Full command line from the user.
     * @return Argument tokens, possibly empty.
     */
    private String[] argumentTokens(String input) {
        String[] tokens = Command.tokens(input);
        if (tokens.length <= 1) {
            return new String[0];
        }
        return Arrays.copyOfRange(tokens, 1, tokens.length);
    }

    /**
     * Returns how many times {@code flag} appears as a whole token.
     *
     * @param tokens Argument tokens.
     * @param flag Flag to count, such as {@code /by}.
     * @return Number of matching tokens.
     */
    private int countFlag(String[] tokens, String flag) {
        return (int) Arrays.stream(tokens)
                .filter(flag::equals)
                .count();
    }

    /**
     * Returns the index of the first {@code flag} token.
     *
     * @param tokens Argument tokens.
     * @param flag Flag to find, such as {@code /by}.
     * @return Zero-based index, or {@code -1} if the flag is absent.
     */
    private int indexOfFlag(String[] tokens, String flag) {
        for (int i = 0; i < tokens.length; i++) {
            if (tokens[i].equals(flag)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Checks that every {@code /flag}-style token is one of the allowed flags.
     *
     * @param tokens Argument tokens.
     * @param usage Usage hint included in the error.
     * @param allowedFlags Flags permitted for this command.
     * @throws AxiomException If an unexpected flag is present.
     */
    private void requireAllowedFlags(String[] tokens, String usage, String... allowedFlags) throws AxiomException {
        for (String token : tokens) {
            if (!isFlagToken(token)) {
                continue;
            }
            boolean isAllowed = Arrays.asList(allowedFlags).contains(token);
            if (!isAllowed) {
                throw new AxiomException("Unexpected flag '" + token + "'. " + usage);
            }
        }
    }

    /**
     * Returns whether {@code token} looks like a command flag such as {@code /by}.
     *
     * @param token Single whitespace-separated token.
     * @return {@code true} if the token is a slash followed by letters.
     */
    private boolean isFlagToken(String token) {
        return token.length() > 1 && token.charAt(0) == '/'
                && token.substring(1).chars().allMatch(Character::isLetter);
    }

    /**
     * Returns tokens in {@code [start, end)} joined by spaces.
     *
     * @param tokens Argument tokens.
     * @param start Inclusive start index.
     * @param end Exclusive end index.
     * @return Joined text, or an empty string if the range is empty.
     */
    private String joinTokens(String[] tokens, int start, int end) {
        if (start >= end) {
            return "";
        }
        return String.join(" ", Arrays.copyOfRange(tokens, start, end));
    }

    /**
     * Returns {@code description} if it can be stored safely.
     *
     * @param description Task description to check.
     * @return The original description.
     * @throws AxiomException If the description contains a pipe character.
     */
    private String requireSafeDescription(String description) throws AxiomException {
        if (description.contains("|")) {
            throw new AxiomException("Task descriptions cannot contain '|'.");
        }
        return description;
    }

    /**
     * Returns {@code value} if it contains text.
     *
     * @param value User-supplied text to check.
     * @param message Error to throw when the value is empty.
     * @return The original value.
     * @throws AxiomException If {@code value} is empty.
     */
    private String requireNonEmpty(String value, String message) throws AxiomException {
        if (value.isEmpty()) {
            throw new AxiomException(message);
        }
        return value;
    }
}
