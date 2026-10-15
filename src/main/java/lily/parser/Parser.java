package lily.parser;

import java.time.LocalDate;
import java.util.Locale;

import lily.exception.LilyException;
import lily.task.Deadline;
import lily.task.Event;
import lily.task.Task;
import lily.task.TaskList;
import lily.task.ToDo;
import lily.ui.Ui;

/**
 * Makes sense of raw command text typed by the user.
 *
 * <p>
 * This class only interprets text: it turns a command line into a command word,
 * into
 * a validated task index, or into a fully-built {@link Task}. It never touches
 * the task
 * list or prints anything — those responsibilities belong to {@link TaskList}
 * and
 * {@link Ui} respectively, so a parsing rule only ever needs to change in one
 * place.
 */
public class Parser {
    private static final String COMMAND_SPACING_ERROR =
            "Those spaces are a little tangled. Use single spaces between command arguments.";

    private Parser() {
        // Static utility class; no instances.
    }

    /**
     * Trims the outside of a complete command, validates its internal spacing, and
     * separates its command word from its arguments. Command words are
     * case-insensitive, while argument text retains its original case. Task-creation
     * commands preserve repeated spaces inside descriptions; other commands still
     * require single spaces so their arguments remain unambiguous.
     *
     * @param userInput complete command typed by the user
     * @return the normalized command word and case-preserved argument text
     * @throws LilyException if the command uses repeated or non-space whitespace
     */
    public static ParsedCommand parseCommand(String userInput) throws LilyException {
        if (userInput == null || userInput.isBlank()) {
            throw new LilyException("What would you like to tend to? Enter a command to get started.");
        }
        String trimmedInput = userInput.trim();
        if (trimmedInput.chars().anyMatch(character -> Character.isWhitespace(character)
                && character != ' ')) {
            throw new LilyException(COMMAND_SPACING_ERROR);
        }

        String[] parts = trimmedInput.split(" ", 2);
        String commandWord = parts[0].toLowerCase(Locale.ROOT);
        String arguments = parts.length == 2 ? parts[1] : "";
        if (trimmedInput.contains("  ") && !isTaskCreationCommand(commandWord)) {
            throw new LilyException(COMMAND_SPACING_ERROR);
        }
        if (isTaskCreationCommand(commandWord)) {
            // Extra spaces separating the command word from its description are not
            // part of the description itself. Repeated spaces after the first
            // description character are preserved.
            arguments = arguments.stripLeading();
        }
        return new ParsedCommand(commandWord, arguments);
    }

    private static boolean isTaskCreationCommand(String commandWord) {
        return commandWord.equals("todo") || commandWord.equals("deadline") || commandWord.equals("event");
    }

    /**
     * Parses a 1-based task-number argument (as typed by the user) into a validated
     * 0-based index.
     *
     * @throws LilyException if the argument is not a valid integer
     */
    public static int parseTaskIndex(String argument) throws LilyException {
        if (argument == null || !argument.matches("[0-9]+")) {
            throw new LilyException("I need one positive whole-number task number. "
                    + "Check list to find the right one.");
        }
        try {
            int oneBasedIndex = Integer.parseInt(argument);
            if (oneBasedIndex == 0) {
                throw new LilyException("I need one positive whole-number task number. "
                        + "Check list to find the right one.");
            }
            return oneBasedIndex - 1;
        } catch (NumberFormatException e) {
            throw new LilyException("That task number is too large.");
        }
    }

    /** A command word paired with its original, case-preserved argument text. */
    public record ParsedCommand(String commandWord, String arguments) {
    }

    /** Parses a date argument for the schedule command. */
    public static LocalDate parseScheduleDate(String argument, LocalDate today) throws LilyException {
        return DateTimeParser.parseScheduleDate(argument, today);
    }

    /**
     * Builds the {@link ToDo} task described by a {@code todo <description>}
     * command.
     *
     * @throws LilyException if no description was supplied
     */
    public static Task parseTodo(String userInput) throws LilyException {
        String description = parseTaskArguments(userInput, "todo");
        validateDescription(description, "todo");
        return new ToDo(description);
    }

    /**
     * Builds the {@link Deadline} task described by a
     * {@code deadline <description> /by <date>} command.
     *
     * @throws LilyException if the description, {@code /by} clause, or date is
     *                       missing
     *                       or malformed
     */
    public static Task parseDeadline(String userInput) throws LilyException {
        String details = parseTaskArguments(userInput, "deadline");
        String[] deadlineParts = splitAroundSingleClause(details, "/by",
                "Use exactly one '/by' clause: deadline <description> /by <date/time>.");
        String description = deadlineParts[0];
        validateDescription(description, "deadline");
        if (deadlineParts[1].isEmpty()) {
            throw new LilyException("Add a date/time after '/by'.");
        }

        DateTimeParser.ParsedDateTime by = DateTimeParser.parseUserInput(deadlineParts[1]);
        return new Deadline(description, by.value(), by.hasExplicitTime());
    }

    /**
     * Builds the {@link Event} task described by an
     * {@code event <description> /from <start> /to <end>} command.
     *
     * @throws LilyException if the description, {@code /from}/{@code /to} clauses,
     *                       or
     *                       either date is missing or malformed, or if {@code /to}
     *                       is before {@code /from}
     */
    public static Task parseEvent(String userInput) throws LilyException {
        String details = parseTaskArguments(userInput, "event");
        String[] eventParts = splitEventDetails(details);
        String description = eventParts[0];
        validateDescription(description, "event");
        if (eventParts[1].isEmpty()) {
            throw new LilyException("Add a date/time after '/from'.");
        }
        if (eventParts[2].isEmpty()) {
            throw new LilyException("Add a date/time after '/to'.");
        }

        DateTimeParser.ParsedDateTime from = DateTimeParser.parseUserInput(eventParts[1]);
        DateTimeParser.ParsedDateTime to = DateTimeParser.parseUserInput(eventParts[2]);
        if (to.value().isBefore(from.value())) {
            throw new LilyException("The event's 'to' time can't be before its 'from' time.");
        }
        return new Event(description, from.value(), from.hasExplicitTime(), to.value(), to.hasExplicitTime());
    }

    /** Validates the event clauses and separates the description, start, and end text. */
    private static String[] splitEventDetails(String eventDetails) throws LilyException {
        String[] tokens = eventDetails.split(" ", -1);
        int fromIndex = findSingleClauseIndex(tokens, "/from");
        int toIndex = findSingleClauseIndex(tokens, "/to");
        if (fromIndex == -2 || toIndex == -2) {
            throw new LilyException("Use '/from' and '/to' exactly once: "
                    + "event <description> /from <start> /to <end>.");
        }
        if (fromIndex < 0 || toIndex < 0) {
            throw new LilyException("An event needs both '/from' and '/to' clauses: "
                    + "event <description> /from <start> /to <end>.");
        }
        if (fromIndex >= toIndex) {
            throw new LilyException("Place '/from' before '/to': "
                    + "event <description> /from <start> /to <end>.");
        }
        return new String[] {
                joinTokens(tokens, 0, fromIndex).strip(),
                joinTokens(tokens, fromIndex + 1, toIndex).strip(),
                joinTokens(tokens, toIndex + 1, tokens.length).strip()
        };
    }

    /** Returns validated arguments belonging to the expected task command. */
    private static String parseTaskArguments(String userInput, String expectedCommand) throws LilyException {
        ParsedCommand parsedCommand = parseCommand(userInput);
        if (!parsedCommand.commandWord().equals(expectedCommand)) {
            throw new LilyException("Expected a '" + expectedCommand + "' command.");
        }
        return parsedCommand.arguments();
    }

    /** Rejects missing descriptions and characters unsafe for line-based storage. */
    private static void validateDescription(String description, String taskType) throws LilyException {
        if (description.isBlank()) {
            String message = switch (taskType) {
            case "todo" -> "This task needs a little label before I can plant it. "
                    + "Add a description after todo.";
            case "deadline" -> "This deadline needs a little label before I can plant it. "
                    + "Add a description before /by.";
            case "event" -> "This event needs a little label before I can plant it. "
                    + "Add a description before /from.";
            default -> "This task needs a little label before I can plant it. Add a description.";
            };
            throw new LilyException(message);
        }
        if (description.codePoints().anyMatch(Parser::isUnsafeDescriptionCharacter)) {
            throw new LilyException("Task descriptions cannot contain line breaks or control characters.");
        }
    }

    private static boolean isUnsafeDescriptionCharacter(int character) {
        int characterType = Character.getType(character);
        return Character.isISOControl(character)
                || characterType == Character.LINE_SEPARATOR
                || characterType == Character.PARAGRAPH_SEPARATOR;
    }

    /** Splits details around exactly one whitespace-delimited clause marker. */
    private static String[] splitAroundSingleClause(String details, String clause, String errorMessage)
            throws LilyException {
        String[] tokens = details.split(" ", -1);
        int clauseIndex = findSingleClauseIndex(tokens, clause);
        if (clauseIndex < 0) {
            throw new LilyException(errorMessage);
        }
        return new String[] {
                joinTokens(tokens, 0, clauseIndex).strip(),
                joinTokens(tokens, clauseIndex + 1, tokens.length).strip()
        };
    }

    /** Returns a clause index, -1 when absent, or -2 when duplicated. */
    private static int findSingleClauseIndex(String[] tokens, String clause) {
        int foundIndex = -1;
        for (int i = 0; i < tokens.length; i++) {
            if (tokens[i].equals(clause)) {
                if (foundIndex >= 0) {
                    return -2;
                }
                foundIndex = i;
            }
        }
        return foundIndex;
    }

    private static String joinTokens(String[] tokens, int startIndex, int endIndex) {
        return String.join(" ", java.util.Arrays.copyOfRange(tokens, startIndex, endIndex));
    }
}
