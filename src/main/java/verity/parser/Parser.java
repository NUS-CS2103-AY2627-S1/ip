package verity.parser;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import verity.client.ClientList;
import verity.command.AddCommand;
import verity.command.Command;
import verity.command.DeleteCommand;
import verity.command.ExitCommand;
import verity.command.FindCommand;
import verity.command.FindDateCommand;
import verity.command.HelpCommand;
import verity.command.ListCommand;
import verity.command.MarkCommand;
import verity.command.UnmarkCommand;
import verity.exception.VerityException;
import verity.task.Deadline;
import verity.task.Event;
import verity.task.Task;
import verity.task.Todo;

/**
 * Interprets user commands and reconstructs tasks from saved data.
 */
public class Parser {

    private static final String DEADLINE_DATE_MARKER = "/by";
    private static final String EVENT_START_DATE_MARKER = "/from";
    private static final String EVENT_END_DATE_MARKER = "/to";
    private static final String TASK_CLIENT_MARKER = "/client";
    private static final String COMMAND_BYE = "bye";
    private static final String COMMAND_LIST = "list";
    private static final String COMMAND_MARK = "mark";
    private static final String COMMAND_UNMARK = "unmark";
    private static final String COMMAND_DELETE = "delete";
    private static final String COMMAND_TODO = "todo";
    private static final String COMMAND_DEADLINE = "deadline";
    private static final String COMMAND_EVENT = "event";
    private static final String COMMAND_FIND = "find";
    private static final String COMMAND_FIND_DATE = "finddate";
    private static final String COMMAND_HELP = "help";
    private static final String COMMAND_CLIENT = "client";

    private final SavedTaskParser savedTaskParser =
            new SavedTaskParser();
    private final SavedClientParser savedClientParser =
            new SavedClientParser();
    private final ClientCommandParser clientCommandParser =
            new ClientCommandParser();

    /**
     * Parses user input and creates the command to execute.
     *
     * @param fullCommand Complete command entered by the user.
     * @param taskCount Number of tasks currently stored.
     * @return Command corresponding to the user input.
     * @throws VerityException If the command or its arguments are invalid.
     */
    public Command parse(String fullCommand, int taskCount)
            throws VerityException {
        assert fullCommand != null : "Command text must not be null";
        assert taskCount >= 0 : "Task count must not be negative.";

        if (fullCommand.isBlank()) {
            throw new VerityException("Please enter a command.");
        }

        String[] commandParts = fullCommand.trim().split("\\s+");
        String commandWord =
                commandParts[0].toLowerCase(Locale.ROOT);

        return switch (commandWord) {
            case COMMAND_BYE -> {
                requireNoArguments(commandParts, COMMAND_BYE);
                yield new ExitCommand();
            }
            case COMMAND_LIST -> {
                requireNoArguments(commandParts, COMMAND_LIST);
                yield new ListCommand();
            }
            case COMMAND_MARK -> new MarkCommand(
                    parseTaskNumber(commandParts, taskCount));
            case COMMAND_UNMARK -> new UnmarkCommand(
                    parseTaskNumber(commandParts, taskCount));
            case COMMAND_DELETE -> new DeleteCommand(
                    parseTaskNumber(commandParts, taskCount));
            case COMMAND_TODO -> {
                validateTaskMarkers(
                        commandParts, COMMAND_TODO, TASK_CLIENT_MARKER);
                yield new AddCommand(parseTodo(commandParts));
            }
            case COMMAND_DEADLINE -> {
                validateTaskMarkers(
                        commandParts, COMMAND_DEADLINE,
                        DEADLINE_DATE_MARKER, TASK_CLIENT_MARKER);
                yield new AddCommand(parseDeadline(commandParts));
            }
            case COMMAND_EVENT -> {
                validateTaskMarkers(
                        commandParts, COMMAND_EVENT,
                        EVENT_START_DATE_MARKER, EVENT_END_DATE_MARKER,
                        TASK_CLIENT_MARKER);
                yield new AddCommand(parseEvent(commandParts));
            }
            case COMMAND_FIND -> new FindCommand(
                    parseFindKeyword(commandParts));
            case COMMAND_FIND_DATE -> new FindDateCommand(
                    parseFindDate(commandParts));
            case COMMAND_HELP -> {
                requireNoArguments(commandParts, COMMAND_HELP);
                yield new HelpCommand();
            }
            case COMMAND_CLIENT -> clientCommandParser.parse(
                    fullCommand, taskCount);
            default -> throw new VerityException(
                    "I don't know that command.");
        };
    }

    /**
     * Ensures that a command has no arguments.
     *
     * @param commandParts Parts of the command.
     * @param commandName Name of the command.
     * @throws VerityException If an argument was supplied.
     */
    private void requireNoArguments(
            String[] commandParts, String commandName)
            throws VerityException {
        if (commandParts.length != 1) {
            throw new VerityException(
                    "The " + commandName
                            + " command does not accept arguments.");
        }
    }

    /**
     * Parses and validates a one-based task number.
     *
     * @param commandParts Parts of the user command.
     * @param taskCount Number of tasks currently stored.
     * @return Corresponding zero-based task index.
     * @throws VerityException If the task number is missing or invalid.
     */
    private int parseTaskNumber(String[] commandParts, int taskCount)
            throws VerityException {
        if (commandParts.length < 2) {
            throw new VerityException("Please provide a task number.");
        }

        if (commandParts.length > 2) {
            throw new VerityException(
                    "A task command accepts exactly one task number.");
        }

        int taskIndex;
        try {
            taskIndex = Integer.parseInt(commandParts[1]) - 1;
        } catch (NumberFormatException exception) {
            throw new VerityException("The task number must be a number.");
        }

        if (taskIndex < 0 || taskIndex >= taskCount) {
            throw new VerityException("That task number does not exist.");
        }
        return taskIndex;
    }

    /**
     * Parses a todo command.
     *
     * @param commandParts Parts of the user command.
     * @return Parsed todo.
     * @throws VerityException If the description is missing.
     */
    private Todo parseTodo(String[] commandParts) throws VerityException {
        int clientMarkerIndex = findMarker(
                commandParts, 1, TASK_CLIENT_MARKER);
        if (clientMarkerIndex == 1) {
            throw new VerityException(
                    "The description of a todo cannot be empty.");
        }

        String description = joinWords(
                commandParts, 1, clientMarkerIndex);
        Todo todo = new Todo(description);
        addTaskClientIds(todo, commandParts, clientMarkerIndex);
        return todo;
    }

    /**
     * Parses a deadline command.
     *
     * @param commandParts Parts of the user command.
     * @return Parsed deadline.
     * @throws VerityException If its description or date is invalid.
     */
    private Deadline parseDeadline(String[] commandParts)
            throws VerityException {
        int byIndex = findMarker(commandParts, 1, DEADLINE_DATE_MARKER);
        validateDeadlineArguments(commandParts, byIndex);
        int clientMarkerIndex = findMarker(
                commandParts, 1, TASK_CLIENT_MARKER);
        if (clientMarkerIndex < byIndex) {
            throw new VerityException(
                    "Client markers must follow the deadline date.");
        }
        if (clientMarkerIndex == byIndex + 1) {
            throw new VerityException("The deadline date cannot be empty.");
        }

        String description = joinWords(commandParts, 1, byIndex);
        String dateText = joinWords(commandParts, byIndex + 1, clientMarkerIndex);

        Deadline deadline = new Deadline(
                description, TaskFactory.parseDate(dateText));
        addTaskClientIds(deadline, commandParts, clientMarkerIndex);
        return deadline;
    }

    /**
     * Parses an event command.
     *
     * @param commandParts Parts of the user command.
     * @return Parsed event.
     * @throws VerityException If its description or dates are invalid.
     */
    private Event parseEvent(String[] commandParts)
            throws VerityException {
        int fromIndex = findMarker(
                commandParts, 1, EVENT_START_DATE_MARKER);
        validateEventStartArguments(commandParts, fromIndex);

        int firstToIndex = findMarker(
                commandParts, 1, EVENT_END_DATE_MARKER);
        if (firstToIndex < fromIndex) {
            throw new VerityException(
                    "The /from marker must come before the /to marker.");
        }

        int toIndex = findMarker(
                commandParts, fromIndex + 1, EVENT_END_DATE_MARKER);
        validateEventEndArguments(commandParts, fromIndex, toIndex);
        int clientMarkerIndex = findMarker(
                commandParts, 1, TASK_CLIENT_MARKER);
        if (clientMarkerIndex < toIndex) {
            throw new VerityException(
                    "Client markers must follow the event end date.");
        }
        if (clientMarkerIndex == toIndex + 1) {
            throw new VerityException("The event's to date cannot be empty.");
        }

        String description = joinWords(commandParts, 1, fromIndex);
        LocalDate fromDate = TaskFactory.parseDate(joinWords(
                commandParts, fromIndex + 1, toIndex));
        LocalDate toDate = TaskFactory.parseDate(joinWords(
                commandParts, toIndex + 1, clientMarkerIndex));

        Event event = TaskFactory.createEvent(description, fromDate, toDate);
        addTaskClientIds(event, commandParts, clientMarkerIndex);
        return event;
    }

    /**
     * Validates the description and date arguments of a deadline command.
     *
     * @param commandParts Parts of the deadline command.
     * @param byIndex Index of the {@code /by} marker.
     * @throws VerityException If a required argument is missing.
     */
    private void validateDeadlineArguments(
            String[] commandParts, int byIndex)
            throws VerityException {
        if (byIndex == 1) {
            throw new VerityException(
                    "The description of a deadline cannot be empty.");
        }

        if (byIndex == commandParts.length) {
            throw new VerityException(
                    "A deadline must include a /by date.");
        }

        if (byIndex == commandParts.length - 1) {
            throw new VerityException(
                    "The deadline date cannot be empty.");
        }
    }

    /**
     * Validates an event description and its {@code /from} marker.
     *
     * @param commandParts Parts of the event command.
     * @param fromIndex Index of the {@code /from} marker.
     * @throws VerityException If the description or marker is missing.
     */
    private void validateEventStartArguments(
            String[] commandParts, int fromIndex)
            throws VerityException {
        if (fromIndex == 1) {
            throw new VerityException(
                    "The description of an event cannot be empty.");
        }

        if (fromIndex == commandParts.length) {
            throw new VerityException(
                    "An event must include a /from date and a /to date.");
        }
    }

    /**
     * Validates the date arguments following an event's {@code /from} marker.
     *
     * @param commandParts Parts of the event command.
     * @param fromIndex Index of the {@code /from} marker.
     * @param toIndex Index of the {@code /to} marker.
     * @throws VerityException If either event date is missing.
     */
    private void validateEventEndArguments(
            String[] commandParts, int fromIndex, int toIndex)
            throws VerityException {
        if (fromIndex + 1 == toIndex) {
            throw new VerityException(
                    "The event's from date cannot be empty.");
        }

        if (toIndex == commandParts.length) {
            throw new VerityException(
                    "An event must include a /to date.");
        }

        if (toIndex + 1 == commandParts.length) {
            throw new VerityException(
                    "The event's to date cannot be empty.");
        }
    }

    private int findMarker(String[] commandParts, int startIndex, String marker) {
        int markerIndex = startIndex;

        while (markerIndex < commandParts.length
                && !commandParts[markerIndex].equalsIgnoreCase(marker)) {
            markerIndex++;
        }

        return markerIndex;
    }

    private void validateTaskMarkers(String[] commandParts,
            String commandName, String... allowedMarkers)
            throws VerityException {
        for (int i = 1; i < commandParts.length; i++) {
            String commandPart = commandParts[i];
            if (!commandPart.startsWith("/")) {
                continue;
            }

            if (!isAllowedMarker(commandPart, allowedMarkers)) {
                if (isKnownTaskMarker(commandPart)) {
                    throw new VerityException(
                            "The " + commandPart.toLowerCase(Locale.ROOT)
                                    + " marker is not valid for a "
                                    + commandName + " command.");
                }
                throw new VerityException(
                        "Unknown task marker '" + commandPart + "'.");
            }

            if (!commandPart.equalsIgnoreCase(TASK_CLIENT_MARKER)
                    && countMarker(commandParts, commandPart) > 1) {
                throw new VerityException(
                        "The " + commandPart.toLowerCase(Locale.ROOT)
                                + " marker cannot be repeated.");
            }
        }
    }

    private boolean isAllowedMarker(String commandPart, String[] allowedMarkers) {
        return Arrays.stream(allowedMarkers)
                .anyMatch(commandPart::equalsIgnoreCase);
    }

    private boolean isKnownTaskMarker(String commandPart) {
        return commandPart.equalsIgnoreCase(DEADLINE_DATE_MARKER)
                || commandPart.equalsIgnoreCase(EVENT_START_DATE_MARKER)
                || commandPart.equalsIgnoreCase(EVENT_END_DATE_MARKER)
                || commandPart.equalsIgnoreCase(TASK_CLIENT_MARKER);
    }

    private long countMarker(String[] commandParts, String marker) {
        return Arrays.stream(commandParts)
                .filter(marker::equalsIgnoreCase)
                .count();
    }

    /**
     * Parses the keyword supplied to a find command.
     *
     * @param commandParts Parts of the user's command.
     * @return Keyword to search for.
     * @throws VerityException If the keyword is missing.
     */
    private String parseFindKeyword(String[] commandParts)
            throws VerityException {
        if (commandParts.length < 2) {
            throw new VerityException(
                    "Please provide a keyword to search for.");
        }
        return joinWords(commandParts, 1, commandParts.length);
    }

    /**
     * Parses the date supplied to a finddate command.
     *
     * @param commandParts Parts of the user command.
     * @return Date to search for.
     * @throws VerityException If the command does not contain one date.
     */
    private LocalDate parseFindDate(String[] commandParts)
            throws VerityException {
        if (commandParts.length != 2) {
            throw new VerityException(
                    "Use finddate followed by a date in yyyy-MM-dd format.");
        }

        return TaskFactory.parseDate(commandParts[1]);
    }

    /**
     * Reconstructs tasks from saved task lines.
     *
     * @param savedTaskLines Lines read from the data file.
     * @return Reconstructed tasks.
     * @throws VerityException If a saved line is corrupted.
     */
    public List<Task> parseSavedTasks(
            List<String> savedTaskLines) throws VerityException {
        return savedTaskParser.parseSavedTasks(savedTaskLines);
    }

    /**
     * Reconstructs clients from saved client lines.
     *
     * @param savedClientLines Lines read from the client data file.
     * @return Reconstructed client list.
     * @throws VerityException If a saved line is corrupted.
     */
    public ClientList parseSavedClients(
            List<String> savedClientLines) throws VerityException {
        return savedClientParser.parseSavedClients(savedClientLines);
    }

    private void addTaskClientIds(Task task, String[] commandParts,
            int clientMarkerIndex) throws VerityException {
        if (clientMarkerIndex == commandParts.length) {
            return;
        }
        int currentIndex = clientMarkerIndex;
        while (currentIndex < commandParts.length) {
            if (!commandParts[currentIndex].equalsIgnoreCase(TASK_CLIENT_MARKER)) {
                throw new VerityException(
                        "Use /client followed by one client ID.");
            }
            if (currentIndex + 1 >= commandParts.length) {
                throw new VerityException("The /client value cannot be empty.");
            }
            if (commandParts[currentIndex + 1].startsWith("/")) {
                throw new VerityException("The /client value cannot be empty.");
            }
            task.addClientId(commandParts[currentIndex + 1]);
            currentIndex += 2;
        }
    }

    private String joinWords(String[] commandParts, int startIndex,
            int endIndex) {
        assert commandParts != null : "Command parts must not be null.";
        assert startIndex >= 0
                && startIndex <= endIndex
                && endIndex <= commandParts.length
                : "Word range must be within command parts.";

        return Arrays.stream(commandParts, startIndex, endIndex)
                .collect(Collectors.joining(" "));
    }
}
