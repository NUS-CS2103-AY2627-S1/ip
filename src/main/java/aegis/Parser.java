package aegis;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

import aegis.task.Deadline;
import aegis.task.Event;
import aegis.task.Task;
import aegis.task.Todo;

/**
 * Creates a Command object depending on the user's command.
 * Accepts multiple distinct user command types.
 */
public class Parser {

    private static final int PARTS_TO_SPLIT = 2;
    private static final Pattern STORAGE_SEPARATOR = Pattern.compile("\\|");
    private static final Pattern BY_SEPARATOR = Pattern.compile("(?:^|\\s+)/by(?:\\s+|$)");
    private static final Pattern FROM_SEPARATOR = Pattern.compile("(?:^|\\s+)/from(?:\\s+|$)");
    private static final Pattern TO_SEPARATOR = Pattern.compile("(?:^|\\s+)/to(?:\\s+|$)");

    /**
     * Parses the user command and creates the corresponding Command object.
     *
     * @param userInput The full user input string.
     * @return The Command object representing the parsed action.
     * @throws AegisException If the command is unrecognized.
     */
    public Command parse(String userInput) throws AegisException {

        assert userInput != null : "User command should not be null";
        String normalizedInput = userInput.trim().replaceAll("\\s+", " ");
        if (normalizedInput.isEmpty()) {
            throw new AegisException("Hmm, that command doesn't ring a bell. Try another one?");
        }

        String[] parts = normalizedInput.split(" ", PARTS_TO_SPLIT);
        assert parts.length >= 1 : "Splitting a command should always produce an action part";

        String action = parts[0].toLowerCase();
        String details = parts.length > 1 ? parts[1] : "";

        return switch (action) {
            case "bye" -> {
                ensureNoDetails(action, details);
                yield new Command("bye", null, 0, null);
            }
            case "list" -> {
                ensureNoDetails(action, details);
                yield new Command("list", null, 0, null);
            }
            case "todo" -> new Command("todo", createTodoTask(details), 0, null);
            case "deadline" -> new Command("deadline", createDeadlineTask(details), 0, null);
            case "event" -> new Command("event", createEventTask(details), 0, null);
            case "delete" -> new Command("delete", null, parseIndex(details), null);
            case "mark" -> new Command("mark", null, parseIndex(details), null);
            case "unmark" -> new Command("unmark", null, parseIndex(details), null);
            case "find" -> new Command("find", null, 0, parseKeyword(details));
            case "sort" -> {
                ensureNoDetails(action, details);
                yield new Command("sort", null, 0, null);
            }
            default -> throw new AegisException("Hmm, that command doesn't ring a bell. Try another one?");
        };
    }

    /**
     * Rejects extra words for commands that do not accept parameters.
     */
    private void ensureNoDetails(String action, String details) throws AegisException {
        if (!details.isBlank()) {
            throw new AegisException(action + " is a solo command. No extra words needed.");
        }
    }

    /**
     * Rejects descriptions that cannot be saved safely in the task file.
     */
    private void ensureSafeText(String text) throws AegisException {
        if (STORAGE_SEPARATOR.matcher(text).find()) {
            throw new AegisException("Please avoid the | character; it tangles up my save file.");
        }
    }

    /**
     * Counts how many times a command parameter appears in the user input.
     */
    private int countParameter(String details, String parameter) {
        return (int) Pattern.compile("(?<!\\S)" + Pattern.quote(parameter) + "(?!\\S)")
                .matcher(details)
                .results()
                .count();
    }

    /**
     * Parses and validates a user-provided date.
     */
    private LocalDate parseDate(String dateText) throws AegisException {
        try {
            return LocalDate.parse(dateText.trim());
        } catch (DateTimeParseException e) {
            throw new AegisException("Dates need the YYYY-MM-DD disguise.");
        }
    }

    /**
     * Parses the index provided by the user, for marking and unmarking of tasks.
     *
     * @param details Contains the numerical string, as provided by the user.
     * @return The parsed index.
     * @throws AegisException If the description is empty or the task number is invalid.
     */
    private int parseIndex(String details) throws AegisException {
        if (details.trim().isEmpty()) {
            throw new AegisException("Give me a task number so I know which task to poke.");
        }
        if (!details.trim().matches("\\d+")) {
            throw new AegisException("That task number looks wobbly. Try a whole number.");
        }
        try {
            return Integer.parseInt(details.trim()) - 1;
        } catch (NumberFormatException e) {
            throw new AegisException("That task number looks wobbly. Try a whole number.");
        }
    }

    /**
     * Creates a Todo task from the given details.
     * The details must contain a non-empty description.
     *
     * @param details The description of the Todo task, as provided by the user.
     * @return A new Todo task with the given description.
     * @throws AegisException If the description is empty or blank.
     */
    private Task createTodoTask(String details) throws AegisException {
        if (details.trim().isEmpty()) {
            throw new AegisException("A todo needs a tiny bit of description magic.");
        }
        ensureSafeText(details);
        return new Todo(details.trim(), false);
    }

    /**
     * Creates a Deadline task from the given details.
     * The details must contain a "/by" separator, a non-empty description,
     * and a valid date in the YYYY-MM-DD format.
     *
     * @param details The user input containing the description and deadline date.
     * @return A new Deadline task with the parsed description and date.
     * @throws AegisException If the "/by" separator is missing, the description or
     *                        date is empty, or the date is not in YYYY-MM-DD format.
     */
    private Task createDeadlineTask(String details) throws AegisException {
        int byCount = countParameter(details, "/by");
        if (byCount == 0) {
            throw new AegisException("Deadline tasks need a /by date.");
        }
        if (byCount > 1) {
            throw new AegisException("Deadline tasks can only have one /by date.");
        }

        String[] deadlineParts = BY_SEPARATOR.split(details, PARTS_TO_SPLIT);

        if (deadlineParts.length < 2) {
            throw new AegisException("A deadline needs a description!");
        }

        String description = deadlineParts[0].trim();
        String by = deadlineParts[1].trim();
        if (description.trim().isEmpty()) {
            throw new AegisException("A deadline needs a description!");
        }
        if (by.trim().isEmpty()) {
            throw new AegisException("The /by date is empty. Give me a date!");
        }
        ensureSafeText(description);
        return new Deadline(description, parseDate(by), false);
    }

    /**
     * Creates an Event task from the given details.
     * The details must contain "/from" and "/to" separators, a non-empty description,
     * and valid dates in the YYYY-MM-DD format.
     *
     * @param details The user input containing the description and event dates.
     * @return A new Event task with the parsed description and dates.
     * @throws AegisException If the "/from" or "/to" separators are missing, the
     *                        description or dates are empty, or the dates are not
     *                        in YYYY-MM-DD format.
     */
    private Task createEventTask(String details) throws AegisException {
        int fromCount = countParameter(details, "/from");
        int toCount = countParameter(details, "/to");
        if (fromCount == 0) {
            throw new AegisException("Event tasks need a /from date.");
        }

        if (toCount == 0) {
            throw new AegisException("Event tasks need a /to date.");
        }
        if (fromCount > 1) {
            throw new AegisException("Event tasks can only have one /from date.");
        }
        if (toCount > 1) {
            throw new AegisException("Event tasks can only have one /to date.");
        }

        String[] eventParts = FROM_SEPARATOR.split(details, PARTS_TO_SPLIT);
        if (eventParts.length < 2) {
            throw new AegisException("An event needs a description!");
        }
        String[] timeParts = TO_SEPARATOR.split(eventParts[1], PARTS_TO_SPLIT);

        if (timeParts.length < 2) {
            throw new AegisException("Event tasks need a /to date.");
        }
        assert timeParts.length == 2 : "Event details should contain exactly one parsed /to separator";

        String description = eventParts[0].trim();
        String from = timeParts[0].trim();
        String to = timeParts[1].trim();
        if (description.trim().isEmpty()) {
            throw new AegisException("An event needs a description!");
        }
        if (from.trim().isEmpty()) {
            throw new AegisException("The /from date is empty. Give this event a starting point.");
        }
        if (to.trim().isEmpty()) {
            throw new AegisException("The /to date is empty. Give this event a finish line.");
        }
        ensureSafeText(description);
        LocalDate start = parseDate(from);
        LocalDate end = parseDate(to);
        if (!start.isBefore(end)) {
            throw new AegisException("Event tasks need a /from date before the /to date.");
        }
        return new Event(description, start, end, false);
    }

    /**
     * Parses the keyword used by find.
     */
    private String parseKeyword(String details) throws AegisException {
        String keyword = details.trim();
        if (keyword.isEmpty()) {
            throw new AegisException("Give me a search word so I can sniff out matching tasks.");
        }
        ensureSafeText(keyword);
        return keyword;
    }

}
