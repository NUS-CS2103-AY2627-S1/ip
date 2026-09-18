package computah.parser;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import computah.client.Client;
import computah.command.AddClientCommand;
import computah.command.AddCommand;
import computah.command.Command;
import computah.command.DeleteClientCommand;
import computah.command.DeleteCommand;
import computah.command.EditClientCommand;
import computah.command.ExitCommand;
import computah.command.FindCommand;
import computah.command.ListClientsCommand;
import computah.command.ListCommand;
import computah.command.MarkCommand;
import computah.command.UnmarkCommand;
import computah.exception.ComputahException;
import computah.task.Deadline;
import computah.task.Event;
import computah.task.Task;
import computah.task.ToDo;
import computah.util.DateTimeUtil;

/**
 * Makes sense of user commands.
 */
public class Parser {
    private static final int MAXIMUM_TASK_DESCRIPTION_LENGTH = 300;
    private static final int MAXIMUM_FIND_KEYWORD_LENGTH = 100;

    /**
     * Prevents instantiation of this utility class.
     */
    private Parser() {
    }

    /**
     * Parses a full user input line into a command object.
     *
     * @param input full user input after trimming.
     * @param taskCount number of tasks currently in the list.
     * @param clientCount number of clients currently in the list.
     * @return command represented by the user input.
     * @throws ComputahException if the input is empty, unknown, malformed, or refers to an invalid item number.
     */
    public static Command parse(String input, int taskCount, int clientCount) throws ComputahException {
        if (input == null || input.isBlank()) {
            throw new ComputahException("Please enter a command.");
        }

        String normalizedInput = normalizeWhitespace(input);
        String[] commandAndArguments = normalizedInput.split(" ", 2);
        String commandWord = commandAndArguments[0];
        String arguments = commandAndArguments.length == 1 ? "" : commandAndArguments[1];

        return switch (commandWord) {
            case "client" -> parseClientCommand(arguments, clientCount);
            case "bye" -> {
                requireNoArguments(arguments, "bye");
                yield new ExitCommand();
            }
            case "list" -> {
                requireNoArguments(arguments, "list");
                yield new ListCommand();
            }
            case "find" -> new FindCommand(parseFindKeyword(arguments));
            case "delete" -> new DeleteCommand(getTaskIndex(arguments, "delete", taskCount));
            case "unmark" -> new UnmarkCommand(getTaskIndex(arguments, "unmark", taskCount));
            case "mark" -> new MarkCommand(getTaskIndex(arguments, "mark", taskCount));
            case "todo", "deadline", "event" -> new AddCommand(createTask(commandWord, arguments));
            default -> throw new ComputahException("I'm sorry, but I don't know what that means :-(");
        };
    }

    /**
     * Collapses runs of whitespace so command separators are interpreted consistently.
     *
     * @param input raw user input.
     * @return trimmed input with each whitespace run replaced by one space.
     */
    private static String normalizeWhitespace(String input) {
        return input.strip().replaceAll("\\s+", " ");
    }

    /**
     * Checks that a command which takes no parameters has no trailing content.
     *
     * @param arguments trailing command content.
     * @param command command name used in the error message.
     * @throws ComputahException if trailing content is present.
     */
    private static void requireNoArguments(String arguments, String command) throws ComputahException {
        if (!arguments.isEmpty()) {
            throw new ComputahException("The " + command + " command does not accept additional arguments.");
        }
    }

    /**
     * Returns a validated search keyword.
     *
     * @param arguments content after the find command.
     * @return validated keyword.
     * @throws ComputahException if the keyword is absent or unreasonably long.
     */
    private static String parseFindKeyword(String arguments) throws ComputahException {
        if (arguments.isEmpty()) {
            throw new ComputahException("The keyword of a find command cannot be empty.");
        }
        if (arguments.length() > MAXIMUM_FIND_KEYWORD_LENGTH) {
            throw new ComputahException("The keyword of a find command cannot exceed "
                    + MAXIMUM_FIND_KEYWORD_LENGTH + " characters.");
        }
        return arguments;
    }

    /**
     * Parses a command that manages client information.
     *
     * @param input client command without the leading {@code client} keyword.
     * @param clientCount number of clients currently in the list.
     * @return command represented by the client input.
     * @throws ComputahException if the client command is invalid.
     */
    private static Command parseClientCommand(String input, int clientCount) throws ComputahException {
        if (input.isEmpty()) {
            throw new ComputahException("Please specify a client command.");
        }

        String[] commandAndArguments = input.split(" ", 2);
        String commandWord = commandAndArguments[0];
        String arguments = commandAndArguments.length == 1 ? "" : commandAndArguments[1];

        return switch (commandWord) {
            case "list" -> {
                if (!arguments.isEmpty()) {
                    throw new ComputahException("The client list command does not accept additional arguments.");
                }
                yield new ListClientsCommand();
            }
            case "add" -> new AddClientCommand(createClient(arguments));
            case "edit" -> createEditClientCommand(arguments, clientCount);
            case "delete" -> new DeleteClientCommand(getClientIndex(arguments, clientCount));
            default -> throw new ComputahException("I'm sorry, but I don't know that client command.");
        };
    }

    /**
     * Creates a client from a name followed by optional contact fields.
     *
     * @param details client name and optional fields.
     * @return client represented by the input.
     * @throws ComputahException if the client details are invalid.
     */
    private static Client createClient(String details) throws ComputahException {
        if (details.isEmpty()) {
            throw new ComputahException("The name of a client cannot be empty.");
        }
        String[] parts = details.split("\\s+(?=/)", -1);
        String name = parts[0].trim();
        if (name.isEmpty() || name.startsWith("/")) {
            throw new ComputahException("The name of a client cannot be empty.");
        }

        Map<String, String> fields = parseClientFields(parts, 1, false);
        String phone = getNewClientField(fields, "/phone", "phone");
        String email = getNewClientField(fields, "/email", "email");
        return new Client(name, phone, email);
    }

    /**
     * Creates a command that updates selected fields of an existing client.
     *
     * @param details client number followed by fields to update.
     * @param clientCount number of clients currently in the list.
     * @return client edit command represented by the input.
     * @throws ComputahException if the client number or fields are invalid.
     */
    private static EditClientCommand createEditClientCommand(String details, int clientCount)
            throws ComputahException {
        if (details.isEmpty()) {
            throw new ComputahException("Please specify a client number.");
        }
        String[] numberAndFields = details.split("\\s+", 2);
        int clientIndex = getClientIndex(numberAndFields[0], clientCount);
        if (numberAndFields.length < 2 || numberAndFields[1].isBlank()) {
            throw new ComputahException("Please specify at least one client field to edit.");
        }

        String[] parts = numberAndFields[1].trim().split("\\s+(?=/)", -1);
        Map<String, String> fields = parseClientFields(parts, 0, true);
        String name = getEditedClientField(fields, "/name", "name", false);
        String phone = getEditedClientField(fields, "/phone", "phone", true);
        String email = getEditedClientField(fields, "/email", "email", true);
        return new EditClientCommand(clientIndex, name, phone, email);
    }

    /**
     * Parses client field segments while rejecting unknown and repeated fields.
     *
     * @param parts input segments containing client fields.
     * @param startIndex first segment containing a field.
     * @param isNameAllowed whether {@code /name} is valid in this command.
     * @return field markers mapped to their supplied values.
     * @throws ComputahException if a field is unknown or repeated.
     */
    private static Map<String, String> parseClientFields(String[] parts, int startIndex,
            boolean isNameAllowed) throws ComputahException {
        Map<String, String> fields = new HashMap<>();
        for (int i = startIndex; i < parts.length; i++) {
            String[] fieldAndValue = parts[i].trim().split("\\s+", 2);
            String field = fieldAndValue[0];
            boolean isKnownField = field.equals("/phone") || field.equals("/email")
                    || isNameAllowed && field.equals("/name");
            if (!isKnownField) {
                throw new ComputahException("Unknown client field: " + field + ".");
            }
            if (fields.containsKey(field)) {
                throw new ComputahException("Client field " + field + " cannot be specified more than once.");
            }
            String value = fieldAndValue.length == 1 ? "" : fieldAndValue[1].trim();
            fields.put(field, value);
        }
        return fields;
    }

    /**
     * Returns an optional field value for a new client.
     *
     * @param fields supplied client fields.
     * @param field field marker to retrieve.
     * @param fieldName field name used in error messages.
     * @return supplied value, or an empty string when absent or explicitly cleared.
     * @throws ComputahException if the field is supplied without a value.
     */
    private static String getNewClientField(Map<String, String> fields, String field, String fieldName)
            throws ComputahException {
        if (!fields.containsKey(field)) {
            return "";
        }
        String value = fields.get(field);
        if (value.equals("-")) {
            return "";
        }
        if (value.isEmpty()) {
            throw new ComputahException("The " + fieldName + " of a client cannot be empty.");
        }
        return value;
    }

    /**
     * Returns a field value for a client edit command.
     *
     * @param fields supplied client fields.
     * @param field field marker to retrieve.
     * @param fieldName field name used in error messages.
     * @param canClear whether {@code -} can clear this field.
     * @return supplied value, an empty string to clear, or null when unchanged.
     * @throws ComputahException if the supplied value is empty or clears a required field.
     */
    private static String getEditedClientField(Map<String, String> fields, String field, String fieldName,
            boolean canClear) throws ComputahException {
        if (!fields.containsKey(field)) {
            return null;
        }
        String value = fields.get(field);
        if (canClear && value.equals("-")) {
            return "";
        }
        if (value.isEmpty() || !canClear && value.equals("-")) {
            throw new ComputahException("The " + fieldName + " of a client cannot be empty.");
        }
        return value;
    }

    /**
     * Converts a one-based client number into a zero-based list index.
     *
     * @param input client number text.
     * @param clientCount number of clients currently in the list.
     * @return zero-based client index.
     * @throws ComputahException if the client number is invalid or outside the list.
     */
    private static int getClientIndex(String input, int clientCount) throws ComputahException {
        if (input.isEmpty()) {
            throw new ComputahException("Please specify a client number.");
        }
        if (input.contains(" ")) {
            throw new ComputahException("Please specify exactly one client number.");
        }
        int clientNumber;
        try {
            clientNumber = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new ComputahException("The client number must be a valid number.");
        }
        if (clientNumber < 1 || clientNumber > clientCount) {
            throw new ComputahException("The client number is not in the list.");
        }
        return clientNumber - 1;
    }

    /**
     * Creates a task from a task-creation command.
     *
     * @param command task-creation command word.
     * @param arguments task details following the command word.
     * @return task represented by the command.
     * @throws ComputahException if the command is malformed or does not create a supported task type.
     */
    private static Task createTask(String command, String arguments) throws ComputahException {
        return switch (command) {
            case "todo" -> createToDo(arguments);
            case "deadline" -> createDeadline(arguments);
            case "event" -> createEvent(arguments);
            default -> throw new ComputahException("I'm sorry, but I don't know what that means :-(");
        };
    }

    /**
     * Creates a todo from a command containing its description.
     *
     * @param description todo description.
     * @return todo represented by the command.
     * @throws ComputahException if the description is empty or unsafe to store.
     */
    private static ToDo createToDo(String description) throws ComputahException {
        if (description.isEmpty()) {
            throw new ComputahException("The description of a todo cannot be empty.");
        }
        validateTaskDescription(description);
        return new ToDo(description);
    }

    /**
     * Creates a deadline from a command containing its description and due date/time.
     *
     * @param details deadline description and by parameter.
     * @return deadline represented by the command.
     * @throws ComputahException if a required field is missing, invalid, or unsafe to store.
     */
    private static Deadline createDeadline(String details) throws ComputahException {
        int byCount = countParameter(details, "/by");
        if (byCount == 0) {
            if (details.isEmpty()) {
                throw new ComputahException("The description of a deadline cannot be empty.");
            }
            throw new ComputahException("The by date/time of a deadline cannot be empty.");
        }
        if (byCount > 1) {
            throw new ComputahException("The /by parameter can only be specified once.");
        }

        int byIndex = findParameter(details, "/by");
        String description = details.substring(0, byIndex).trim();
        String deadlineText = details.substring(byIndex + "/by".length()).trim();
        if (description.isEmpty()) {
            throw new ComputahException("The description of a deadline cannot be empty.");
        }
        if (deadlineText.isEmpty()) {
            throw new ComputahException("The by date/time of a deadline cannot be empty.");
        }

        validateTaskDescription(description);
        return new Deadline(description, DateTimeUtil.parse(deadlineText));
    }

    /**
     * Creates an event from a command containing its description, start, and end.
     *
     * @param details event description, start, and end parameters.
     * @return event represented by the command.
     * @throws ComputahException if a required field is missing, invalid, or unsafe to store.
     */
    private static Event createEvent(String details) throws ComputahException {
        int fromCount = countParameter(details, "/from");
        int toCount = countParameter(details, "/to");
        if (fromCount == 0) {
            if (details.isEmpty()) {
                throw new ComputahException("The description of an event cannot be empty.");
            }
            throw new ComputahException("The start date/time of an event cannot be empty.");
        }
        if (fromCount > 1) {
            throw new ComputahException("The /from parameter can only be specified once.");
        }
        if (toCount == 0) {
            throw new ComputahException("The end date/time of an event cannot be empty.");
        }
        if (toCount > 1) {
            throw new ComputahException("The /to parameter can only be specified once.");
        }

        int fromIndex = findParameter(details, "/from");
        int toIndex = findParameter(details, "/to");
        if (toIndex < fromIndex) {
            throw new ComputahException("The /from parameter must appear before the /to parameter.");
        }

        String description = details.substring(0, fromIndex).trim();
        String startDateTimeText = details.substring(fromIndex + "/from".length(), toIndex).trim();
        String endDateTimeText = details.substring(toIndex + "/to".length()).trim();
        if (description.isEmpty()) {
            throw new ComputahException("The description of an event cannot be empty.");
        }
        if (startDateTimeText.isEmpty()) {
            throw new ComputahException("The start date/time of an event cannot be empty.");
        }
        if (endDateTimeText.isEmpty()) {
            throw new ComputahException("The end date/time of an event cannot be empty.");
        }

        validateTaskDescription(description);
        LocalDateTime startDateTime = DateTimeUtil.parse(startDateTimeText);
        LocalDateTime endDateTime = DateTimeUtil.parse(endDateTimeText);
        if (endDateTime.isBefore(startDateTime)) {
            throw new ComputahException("The end date/time of an event cannot be before its start date/time.");
        }
        return new Event(description, startDateTime, endDateTime);
    }

    /**
     * Counts exact occurrences of a slash-prefixed parameter.
     *
     * @param details normalized command details.
     * @param parameter parameter to count.
     * @return number of exact parameter tokens.
     */
    private static int countParameter(String details, String parameter) {
        String paddedDetails = " " + details + " ";
        String parameterToken = " " + parameter + " ";
        int count = 0;
        int searchIndex = 0;
        while ((searchIndex = paddedDetails.indexOf(parameterToken, searchIndex)) >= 0) {
            count++;
            searchIndex += parameterToken.length();
        }
        return count;
    }

    /**
     * Returns the position of an exact slash-prefixed parameter.
     *
     * @param details normalized command details.
     * @param parameter parameter to find.
     * @return parameter position within details.
     */
    private static int findParameter(String details, String parameter) {
        return (" " + details + " ").indexOf(" " + parameter + " ");
    }

    /**
     * Checks that a task field can be safely written using the current file delimiter.
     *
     * @param field task field to validate.
     * @throws ComputahException if the field contains the save-file delimiter.
     */
    private static void validateTaskDescription(String field) throws ComputahException {
        if (field.length() > MAXIMUM_TASK_DESCRIPTION_LENGTH) {
            throw new ComputahException("A task description cannot exceed "
                    + MAXIMUM_TASK_DESCRIPTION_LENGTH + " characters.");
        }
        if (field.contains(" | ")) {
            throw new ComputahException("Task details cannot contain \" | \".");
        }
        if (field.chars().anyMatch(Character::isISOControl)) {
            throw new ComputahException("Task details cannot contain control characters.");
        }
    }

    /**
     * Converts a one-based task number in a command into a zero-based list index.
     *
     * @param input task-number argument.
     * @param command command word that should be followed by a task number.
     * @param taskCount number of tasks currently in the list.
     * @return zero-based task index.
     * @throws ComputahException if the command has no valid task number or the number is out of range.
     */
    private static int getTaskIndex(String input, String command, int taskCount) throws ComputahException {
        if (input.isEmpty()) {
            throw new ComputahException("Please specify a task number after " + command + ".");
        }
        if (input.contains(" ")) {
            throw new ComputahException("Please specify exactly one task number after " + command + ".");
        }
        int taskNumber;
        try {
            taskNumber = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new ComputahException("The task number must be a valid number.");
        }
        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new ComputahException("The task number is not in the list.");
        }
        return taskNumber - 1;
    }
}
