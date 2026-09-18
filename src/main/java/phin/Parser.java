package phin;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Interprets console commands without changing tasks or performing input/output.
 */
public class Parser {
    private static final Map<String, String> SHORTCUTS = Map.ofEntries(
            Map.entry("h", "help"),
            Map.entry("l", "list"),
            Map.entry("t", "todo"),
            Map.entry("dl", "deadline"),
            Map.entry("e", "event"),
            Map.entry("m", "mark"),
            Map.entry("um", "unmark"),
            Map.entry("del", "delete"),
            Map.entry("u", "update"),
            Map.entry("f", "find"),
            Map.entry("q", "bye"));
    /**
     * Recognizes each supported slash-prefixed field in an update command.
     */
    private static final Pattern UPDATE_FIELD = Pattern.compile("/([A-Za-z]+)(?:\\s+|$)");
    /**
     * Prevents instantiation of this stateless parsing utility.
     */
    private Parser() {
        // Utility class: parsing does not need per-instance state.
    }

    /**
     * Recognizes command words using the existing exact-match and space rules.
     *
     * @param command complete, untrimmed input line.
     * @return recognized command word.
     * @throws PhinException if the command is unknown or takes unexpected arguments.
     */
    public static String parseCommandWord(String command) throws PhinException {
        command = command.strip();
        if (command.equals("help") || command.equals("list") || command.equals("bye")) {
            return command;
        }
        for (String word : new String[] {
            "mark", "unmark", "delete", "todo", "deadline", "event", "find", "update"
        }) {
            if (command.equals(word) || command.matches(Pattern.quote(word) + "\\s+.*")) {
                return word;
            }
        }
        throw new PhinException(
                "That command means nothing to me. Type help to see every command.");
    }

    /**
     * Replaces a recognized shortcut at the start of a command with its full command word.
     * Descriptions and other arguments remain unchanged.
     *
     * @param command Complete, possibly abbreviated command.
     * @return Trimmed command with its shortcut expanded, if applicable.
     */
    public static String expandShortcut(String command) {
        String trimmedCommand = command.strip();
        int firstWhitespace = -1;
        for (int i = 0; i < trimmedCommand.length(); i++) {
            if (Character.isWhitespace(trimmedCommand.charAt(i))) {
                firstWhitespace = i;
                break;
            }
        }
        String commandWord = firstWhitespace < 0
                ? trimmedCommand
                : trimmedCommand.substring(0, firstWhitespace);
        String expandedWord = SHORTCUTS.get(commandWord);
        if (expandedWord == null) {
            return trimmedCommand;
        }
        return expandedWord + trimmedCommand.substring(commandWord.length());
    }

    /**
     * Extracts a nonblank search phrase from a find command.
     *
     * @param command Complete command entered by the user.
     * @return Search phrase with surrounding whitespace removed.
     * @throws PhinException If the command is not find or its phrase is blank.
     */
    public static String parseFindKeyword(String command) throws PhinException {
        command = command.strip();
        if (!parseCommandWord(command).equals("find")) {
            throw new PhinException("Expected a find command. Try: find KEYWORD");
        }
        String keyword = command.substring("find".length()).trim();
        requireText(keyword, "Tell me what to find. Try: find KEYWORD");
        return keyword;
    }

    /**
     * Constructs a task only after its command fields and dates are validated.
     *
     * @param command complete task-creation command.
     * @return new task, not yet added to the list.
     * @throws PhinException if the command is not a valid task-creation command.
     * @throws IllegalArgumentException if a date or event range is invalid.
     */
    public static Task parseTask(String command) throws PhinException {
        command = command.strip();
        switch (parseCommandWord(command)) {
            case "todo":
                String description = command.substring("todo".length()).trim();
                requireText(description,
                        "A todo without a description? Give me something to work with.");
                return new Todo(description);
            case "deadline":
                String detailsText = command.substring("deadline".length()).trim();
                String[] details = detailsText.split("\\s+/by\\s+", 2);
                if (details.length < 2 || details[0].isBlank() || details[1].isBlank()) {
                    throw new PhinException(
                            "Deadlines need a description and a time. Try: deadline TASK /by TIME");
                }
                return new Deadline(details[0].trim(), details[1].trim());
            case "event":
                String eventText = command.substring("event".length()).trim();
                String[] descriptionAndTimes = eventText.split("\\s+/from\\s+", 2);
                String[] times = descriptionAndTimes.length < 2
                        ? new String[0]
                        : descriptionAndTimes[1].split("\\s+/to\\s+", 2);
                if (descriptionAndTimes.length < 2 || times.length < 2
                        || descriptionAndTimes[0].isBlank()
                        || times[0].isBlank() || times[1].isBlank()) {
                    throw new PhinException(
                            "Events need all their details. Try: event TASK /from START /to END");
                }
                return new Event(descriptionAndTimes[0].trim(),
                        times[0].trim(), times[1].trim());
            default:
                throw new PhinException("Expected a todo, deadline, or event command.");
        }
    }

    /**
     * Rejects a required command field when it contains no visible text.
     *
     * @param text field value to validate.
     * @param errorMessage explanation shown when the field is blank.
     * @throws PhinException if the field is blank.
     */
    private static void requireText(String text, String errorMessage) throws PhinException {
        if (text.isBlank()) {
            throw new PhinException(errorMessage);
        }
    }

    /**
     * Converts a task-number argument to a valid zero-based task index.
     *
     * @param command complete command entered by the user.
     * @param commandWord command whose argument is being parsed.
     * @param taskCount number of tasks currently stored.
     * @return zero-based index of the selected task.
     * @throws PhinException if the argument is missing, nonnumeric, or outside the list.
     */
    public static int parseTaskIndex(String command, String commandWord, int taskCount)
            throws PhinException {
        command = command.strip();
        assert taskCount >= 0 : "A task list cannot have a negative size";
        String indexText = command.substring(commandWord.length()).trim();
        if (indexText.isBlank()) {
            throw new PhinException("Tell me which task to " + commandWord
                    + ". Try: " + commandWord + " NUMBER");
        }

        final int taskNumber;
        try {
            taskNumber = Integer.parseInt(indexText);
        } catch (NumberFormatException exception) {
            throw new PhinException("Task numbers are, inconveniently, numbers. Try: "
                    + commandWord + " NUMBER");
        }

        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new PhinException("Task " + taskNumber
                    + " isn't in the list. Pick a number from 1 to " + taskCount + ".");
        }
        return taskNumber - 1;
    }

    /**
     * Extracts and validates the task number at the start of an update command.
     *
     * @param command Complete update command entered by the user.
     * @param taskCount Number of tasks currently stored.
     * @return Zero-based index of the selected task.
     * @throws PhinException If the task number is missing, invalid, or outside the list.
     */
    public static int parseUpdateIndex(String command, int taskCount) throws PhinException {
        command = command.strip();
        String arguments = command.substring("update".length()).trim();
        int firstField = arguments.indexOf(" /");
        String indexArgument = firstField < 0 ? arguments : arguments.substring(0, firstField);
        return parseTaskIndex("update " + indexArgument, "update", taskCount);
    }

    /**
     * Builds an updated copy of a task while retaining all unspecified details and its status.
     *
     * @param command Complete update command entered by the user.
     * @param original Existing task selected by the user.
     * @return Updated task of the same type as the original.
     * @throws PhinException If fields are missing, duplicated, unknown, or incompatible with the task type.
     * @throws IllegalArgumentException If an updated date or event range is invalid.
     */
    public static Task parseUpdatedTask(String command, Task original) throws PhinException {
        assert original != null : "Only an existing task may be updated";
        command = command.strip();
        String arguments = command.substring("update".length()).trim();
        int firstField = arguments.indexOf(" /");
        if (firstField < 0) {
            throw new PhinException("Tell me what to update. Try: update NUMBER /description TEXT");
        }

        String fieldsText = arguments.substring(firstField + 1);
        Map<String, String> fields = parseUpdateFields(fieldsText);
        validateUpdateFields(original, fields);
        String description = fields.getOrDefault("description", original.description);

        Task updated;
        if (original instanceof Deadline deadline) {
            updated = new Deadline(description, fields.getOrDefault("by", deadline.by.toString()));
        } else if (original instanceof Event event) {
            updated = new Event(description, fields.getOrDefault("from", event.from.toString()),
                    fields.getOrDefault("to", event.to.toString()));
        } else {
            updated = new Todo(description);
        }
        if (original.isDone) {
            updated.markAsDone();
        }
        return updated;
    }

    private static Map<String, String> parseUpdateFields(String fieldsText) throws PhinException {
        Matcher matcher = UPDATE_FIELD.matcher(fieldsText);
        Map<String, String> fields = new LinkedHashMap<>();
        int previousEnd = 0;
        String previousName = null;
        while (matcher.find()) {
            if (previousName == null && matcher.start() != 0) {
                throw invalidUpdateSyntax();
            }
            if (previousName != null) {
                addUpdateField(fields, previousName, fieldsText.substring(previousEnd, matcher.start()).trim());
            }
            previousName = matcher.group(1);
            if (!(previousName.equals("description") || previousName.equals("by")
                    || previousName.equals("from") || previousName.equals("to"))) {
                throw invalidUpdateSyntax();
            }
            previousEnd = matcher.end();
        }
        if (previousName == null) {
            throw invalidUpdateSyntax();
        }
        addUpdateField(fields, previousName, fieldsText.substring(previousEnd).trim());
        return fields;
    }

    private static void addUpdateField(Map<String, String> fields, String name, String value)
            throws PhinException {
        if (value.isBlank()) {
            throw new PhinException("Update fields cannot be blank.");
        }
        if (fields.putIfAbsent(name, value) != null) {
            throw new PhinException("Use each update field only once.");
        }
    }

    private static void validateUpdateFields(Task original, Map<String, String> fields) throws PhinException {
        boolean hasWrongField = original instanceof Todo && fields.keySet().stream()
                .anyMatch(field -> !field.equals("description"));
        hasWrongField |= original instanceof Deadline && fields.keySet().stream()
                .anyMatch(field -> !(field.equals("description") || field.equals("by")));
        hasWrongField |= original instanceof Event && fields.keySet().stream()
                .anyMatch(field -> !(field.equals("description") || field.equals("from") || field.equals("to")));
        if (hasWrongField) {
            throw new PhinException("Those fields do not apply to this task type.");
        }
    }

    private static PhinException invalidUpdateSyntax() {
        return new PhinException("Invalid update. Use /description, /by, /from, or /to fields.");
    }
}
