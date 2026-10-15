package thomas.command;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import thomas.ThomasException;

/**
 * Parses command names and arguments entered by the user.
 */
public class Parser {
    private static final Pattern COMMAND_PATTERN = Pattern.compile("^(\\S+)(?:\\s+(.*))?$");
    private static final Pattern DEADLINE_BY_FLAG = Pattern.compile("(?:^|\\s)/by(?=\\s|$)");
    private static final Pattern EVENT_FROM_FLAG = Pattern.compile("(?:^|\\s)/from(?=\\s|$)");
    private static final Pattern EVENT_TO_FLAG = Pattern.compile("(?:^|\\s)/to(?=\\s|$)");
    private static final Pattern PLACE_FLAG = Pattern.compile("\\s/(name|type|at|rating|price|visited|note)(?=\\s|$)");
    private static final Pattern ANY_FLAG = Pattern.compile("\\s/(\\S+)(?=\\s|$)");

    /**
     * Converts a complete user command into an executable command object.
     *
     * @param fullCommand Complete user command.
     * @return Command represented by the input.
     * @throws ThomasException If required command parts are missing or invalid.
     */
    public Command parse(String fullCommand) throws ThomasException {
        if (fullCommand == null || fullCommand.trim().isEmpty()) {
            throw new ThomasException("Please enter a command. Type /help to see available commands.");
        }
        String normalizedCommand = fullCommand.trim();
        String command = getCommand(normalizedCommand);
        switch (command) {
            case "list":
                requireNoArgument(normalizedCommand, "list");
                return new ListCommand();
            case "mark":
                return new MarkCommand(getArgument(normalizedCommand, "mark"), this);
            case "unmark":
                return new UnmarkCommand(getArgument(normalizedCommand, "unmark"), this);
            case "delete":
                return new DeleteCommand(getArgument(normalizedCommand, "delete"), this);
            case "todo":
                return new TodoCommand(validateTaskText(getArgument(normalizedCommand, "todo")));
            case "deadline":
                String[] deadlineDetails = getDeadlineDetails(normalizedCommand);
                return new DeadlineCommand(deadlineDetails[0], deadlineDetails[1]);
            case "event":
                String[] eventDetails = getEventDetails(normalizedCommand);
                return new EventCommand(eventDetails[0], eventDetails[1], eventDetails[2]);
            case "on":
                return new OnCommand(getArgument(normalizedCommand, "on"));
            case "find":
                return new FindCommand(getArgument(normalizedCommand, "find"));
            case "place":
                return parseAddPlace(normalizedCommand);
            case "listplace":
                requireNoArgument(normalizedCommand, "listplace");
                return new ListPlaceCommand();
            case "findplace":
                return new FindPlaceCommand(getArgument(fullCommand, "findplace"));
            case "editplace":
                return parseEditPlace(fullCommand);
            case "deleteplace":
                return new DeletePlaceCommand(parsePlaceIndex(getArgument(fullCommand, "deleteplace"),
                        "Please specify a place number to delete."));
            case "confirmdeleteplace":
                return new ConfirmDeletePlaceCommand(parsePlaceIndex(getArgument(fullCommand, "confirmdeleteplace"),
                        "Please specify a place number to confirm."));
            case "/help":
                requireNoArgument(normalizedCommand, "/help");
                return new HelpCommand();
            case "bye":
                requireNoArgument(normalizedCommand, "bye");
                return new ExitCommand();
            default:
                return new UnknownCommand();
        }
    }

    /**
     * Returns the first word of a command.
     *
     * @param command Complete user command.
     * @return Command name.
     */
    public String getCommand(String command) {
        if (command == null) {
            return "";
        }
        Matcher matcher = COMMAND_PATTERN.matcher(command.trim());
        return matcher.matches() ? matcher.group(1) : "";
    }

    /**
     * Returns text following a command prefix.
     *
     * @param command Complete user command.
     * @param prefix Command prefix.
     * @return Trimmed argument text.
     */
    public String getArgument(String command, String prefix) {
        String trimmed = command.trim();
        assert getCommand(trimmed).equals(prefix) : "Command must start with its expected prefix";
        return trimmed.substring(prefix.length()).trim();
    }

    /**
     * Parses a task number from a command argument.
     *
     * @param argument Task number argument.
     * @param taskCount Number of tasks in the list.
     * @param emptyMessage Error message when the argument is empty.
     * @return Zero-based task index.
     * @throws ThomasException If the argument is empty or outside the list.
     */
    public int getTaskIndex(String argument, int taskCount, String emptyMessage) throws ThomasException {
        assert taskCount >= 0 : "Task count must not be negative";
        if (argument.isEmpty()) {
            throw new ThomasException(emptyMessage);
        }
        if (!argument.matches("[1-9]\\d*")) {
            throw new ThomasException("Please enter a valid task number.");
        }
        int taskIndex;
        try {
            taskIndex = Math.subtractExact(Integer.parseInt(argument), 1);
        } catch (ArithmeticException | NumberFormatException e) {
            throw new ThomasException("Please enter a valid task number.");
        }
        if (taskIndex < 0 || taskIndex >= taskCount) {
            throw new ThomasException("Task number does not exist.");
        }
        return taskIndex;
    }

    /**
     * Extracts a deadline description and date/time.
     *
     * @param command Complete deadline command.
     * @return Description and date/time.
     * @throws ThomasException If required parts are absent.
     */
    public String[] getDeadlineDetails(String command) throws ThomasException {
        Matcher byMatcher = DEADLINE_BY_FLAG.matcher(command);
        if (!byMatcher.find()) {
            throw new ThomasException("Deadline requires '/by <date>'. E.g., deadline Assignment 1 /by Tuesday");
        }
        int byIndex = byMatcher.start();
        int byValueIndex = byMatcher.end();
        if (byMatcher.find()) {
            throw new ThomasException("Deadline may contain '/by' only once.");
        }
        String description = getArgument(command.substring(0, byIndex), "deadline");
        String by = command.substring(byValueIndex).trim();
        if (description.isEmpty()) {
            throw new ThomasException("Deadline description cannot be empty.");
        }
        validateTaskText(description);
        if (by.isEmpty()) {
            throw new ThomasException("Date/Time of '/by' cannot be empty.");
        }
        return new String[]{description, by};
    }

    /**
     * Extracts an event description, start, and end date/time.
     *
     * @param command Complete event command.
     * @return Description, start, and end date/time.
     * @throws ThomasException If required parts are absent.
     */
    public String[] getEventDetails(String command) throws ThomasException {
        Matcher fromMatcher = EVENT_FROM_FLAG.matcher(command);
        Matcher toMatcher = EVENT_TO_FLAG.matcher(command);
        if (!fromMatcher.find() || !toMatcher.find() || toMatcher.start() < fromMatcher.start()) {
            throw new ThomasException("Event requires '/from' and '/to'. E.g., event meeting /from Mon /to Thurs");
        }
        int fromIndex = fromMatcher.start();
        int fromValueIndex = fromMatcher.end();
        int toIndex = toMatcher.start();
        int toValueIndex = toMatcher.end();
        if (fromMatcher.find() || toMatcher.find()) {
            throw new ThomasException("Event may contain '/from' and '/to' only once each.");
        }
        String description = getArgument(command.substring(0, fromIndex), "event");
        String from = command.substring(fromValueIndex, toIndex).trim();
        String to = command.substring(toValueIndex).trim();
        if (description.isEmpty()) {
            throw new ThomasException("Event description cannot be empty.");
        }
        validateTaskText(description);
        if (from.isEmpty() || to.isEmpty()) {
            throw new ThomasException("Date/Time of '/from' or '/to' cannot be empty.");
        }
        return new String[]{description, from, to};
    }

    private Command parseAddPlace(String command) throws ThomasException {
        String argument = getArgument(command, "place");
        Map<String, String> fields = parsePlaceFields(argument);
        Matcher matcher = PLACE_FLAG.matcher(argument);
        String name = matcher.find() ? argument.substring(0, matcher.start()).trim() : argument;
        return new AddPlaceCommand(name, fields);
    }

    private Command parseEditPlace(String command) throws ThomasException {
        String argument = getArgument(command, "editplace");
        int firstSpace = argument.indexOf(' ');
        if (argument.isEmpty()) {
            throw new ThomasException("Please specify a place number to edit.");
        }
        if (firstSpace == -1) {
            throw new ThomasException("Edit place requires one field. E.g., editplace 1 /rating 5");
        }
        int index = parsePlaceIndex(argument.substring(0, firstSpace), "Please specify a place number to edit.");
        Map<String, String> fields = parsePlaceFields(argument.substring(firstSpace));
        if (fields.size() != 1) {
            throw new ThomasException("Edit place requires exactly one field. E.g., editplace 1 /rating 5");
        }
        Map.Entry<String, String> field = fields.entrySet().iterator().next();
        return new EditPlaceCommand(index, field.getKey(), field.getValue());
    }

    private Map<String, String> parsePlaceFields(String text) throws ThomasException {
        Matcher unknownMatcher = ANY_FLAG.matcher(text);
        while (unknownMatcher.find()) {
            String field = unknownMatcher.group(1);
            if (!isPlaceField(field)) {
                throw new ThomasException("Unknown place field: /" + field + ".");
            }
        }
        Map<String, String> fields = new HashMap<>();
        Matcher matcher = PLACE_FLAG.matcher(text);
        int valueStart = -1;
        String previousField = null;
        while (matcher.find()) {
            if (previousField != null) {
                String value = text.substring(valueStart, matcher.start()).trim();
                addPlaceField(fields, previousField, value);
            }
            previousField = matcher.group(1);
            valueStart = matcher.end();
        }
        if (previousField != null) {
            addPlaceField(fields, previousField, text.substring(valueStart).trim());
        }
        return fields;
    }

    private void addPlaceField(Map<String, String> fields, String field, String value) throws ThomasException {
        if (fields.containsKey(field)) {
            throw new ThomasException("Each place field may be provided only once.");
        }
        if (value.isEmpty()) {
            throw new ThomasException("Place details cannot be empty.");
        }
        fields.put(field, value);
    }

    private int parsePlaceIndex(String argument, String emptyMessage) throws ThomasException {
        if (argument.isEmpty()) {
            throw new ThomasException(emptyMessage);
        }
        try {
            int index = Integer.parseInt(argument);
            if (index < 1) {
                throw new NumberFormatException();
            }
            return index;
        } catch (NumberFormatException e) {
            throw new ThomasException("Place number does not exist.");
        }
    }

    private void requireNoArgument(String command, String prefix) throws ThomasException {
        if (!getArgument(command, prefix).isEmpty()) {
            throw new ThomasException("" + prefix + " does not take any arguments.");
        }
    }

    private boolean isPlaceField(String field) {
        return field.equals("name") || field.equals("type") || field.equals("at") || field.equals("rating")
                || field.equals("price") || field.equals("visited") || field.equals("note");
    }

    private String validateTaskText(String text) throws ThomasException {
        if (text.contains("|") || text.contains("\n") || text.contains("\r")) {
            throw new ThomasException("Task descriptions cannot contain \"|\" or a line break.");
        }
        return text;
    }
}
