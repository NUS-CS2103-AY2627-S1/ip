package tasque;

import java.time.format.DateTimeParseException;

import tasque.task.Deadline;
import tasque.task.Event;
import tasque.task.Task;
import tasque.task.Todo;

/**
 * Parses user input into validated commands and task objects.
 */
public class Parser {
    /**
     * Strips command-edge whitespace and expands a short alias while preserving internal text.
     * Matches the existing parser's space-delimited, case-sensitive command syntax.
     *
     * @param userInput Command input to normalize.
     * @return Stripped input with a canonical command keyword when recognized.
     */
    public String normalizeCommand(String userInput) {
        String strippedInput = userInput.strip();
        int spaceIndex = strippedInput.indexOf(' ');
        int commandEnd = spaceIndex == -1 ? strippedInput.length() : spaceIndex;
        String keyword = strippedInput.substring(0, commandEnd);
        String canonical = switch (keyword) {
            case "t" -> "todo";
            case "d" -> "deadline";
            case "e" -> "event";
            case "l" -> "list";
            case "m" -> "mark";
            case "u" -> "unmark";
            case "del" -> "delete";
            case "f" -> "find";
            case "b" -> "bye";
            default -> keyword;
        };
        return canonical + strippedInput.substring(commandEnd);
    }

    /**
     * Parses the command keyword from the user's input.
     *
     * @param userInput User input to classify.
     * @return Recognized command keyword.
     * @throws TasqueException If the input does not begin with a supported command.
     */
    public String parseCommand(String userInput) throws TasqueException {
        if (userInput.equals("bye") || userInput.equals("list")) {
            return userInput;
        }

        String[] commandsWithArguments = {
            "mark", "unmark", "todo", "deadline", "event", "delete", "find"
        };
        for (String command : commandsWithArguments) {
            if (userInput.equals(command) || userInput.startsWith(command + " ")) {
                return command;
            }
        }

        throw new TasqueException("Yeah, I don't know what that means.");
    }

    /**
     * Parses and validates a one-based task number from a command.
     *
     * @param userInput User input containing the task number.
     * @param command Command whose task number is being parsed.
     * @param numberOfTasks Number of tasks currently available.
     * @return The validated one-based task number.
     * @throws TasqueException If the task number is missing, invalid, or out of range.
     */
    public int parseTaskNumber(String userInput, String command, int numberOfTasks)
            throws TasqueException {
        String taskNumberText = userInput.substring(command.length()).trim();
        if (taskNumberText.isEmpty()) {
            throw new TasqueException("The " + command + " command needs a task number.");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(taskNumberText);
        } catch (NumberFormatException e) {
            throw new TasqueException("The task number must be a positive whole number.");
        }

        if (taskNumber <= 0) {
            throw new TasqueException("The task number must be a positive whole number.");
        }
        if (taskNumber > numberOfTasks) {
            throw new TasqueException("Task " + taskNumber + " doesn't exist.");
        }
        return taskNumber;
    }

    /**
     * Extracts the keyword supplied to a Find command.
     *
     * @param userInput Full user input for the Find command.
     * @return The non-blank search keyword.
     * @throws TasqueException If the Find command has no keyword.
     */
    public String parseFindKeyword(String userInput) throws TasqueException {
        String keyword = userInput.substring("find".length()).trim();
        if (keyword.isEmpty()) {
            throw new TasqueException("The find command needs a keyword.");
        }
        return keyword;
    }

    /**
     * Parses a todo task from a {@code todo} command.
     *
     * @param userInput User input containing the todo description.
     * @return Parsed todo task.
     * @throws TasqueException If the todo description is empty.
     */
    public Task parseTodo(String userInput) throws TasqueException {
        String description = userInput.substring("todo".length()).trim();
        if (description.isBlank()) {
            throw new TasqueException("The description of a todo cannot be empty.");
        }
        return new Todo(description);
    }

    /**
     * Parses a deadline task from a {@code deadline} command.
     *
     * @param userInput User input containing the description and due date.
     * @return Parsed deadline task.
     * @throws TasqueException If the command is incomplete or the date is invalid.
     */
    public Task parseDeadline(String userInput) throws TasqueException {
        String deadlineDetails = userInput.substring("deadline".length()).trim();
        if (deadlineDetails.isEmpty()) {
            throw new TasqueException("The description of a deadline cannot be empty.");
        }

        int byIndex = deadlineDetails.indexOf("/by");
        if (byIndex == -1) {
            throw new TasqueException("A deadline must include /by followed by when it is due.");
        }

        String description = deadlineDetails.substring(0, byIndex).trim();
        if (description.isBlank()) {
            throw new TasqueException("The description of a deadline cannot be empty.");
        }

        String by = deadlineDetails.substring(byIndex + "/by".length()).trim();
        if (by.isEmpty()) {
            throw new TasqueException("The /by value of a deadline cannot be empty.");
        }

        try {
            return new Deadline(description, by);
        } catch (DateTimeParseException e) {
            throw new TasqueException("Enter a valid date in yyyy-MM-dd format.");
        }
    }

    /**
     * Parses an event task from an {@code event} command.
     *
     * @param userInput User input containing the description and time range.
     * @return Parsed event task.
     * @throws TasqueException If the command is incomplete, a date is invalid, or the end precedes the start.
     */
    public Task parseEvent(String userInput) throws TasqueException {
        String eventDetails = userInput.substring("event".length()).trim();
        if (eventDetails.isEmpty()) {
            throw new TasqueException("The description of an event cannot be empty.");
        }

        int fromIndex = eventDetails.indexOf("/from");
        if (fromIndex == -1) {
            throw new TasqueException("An event must include /from followed by its start.");
        }

        String description = eventDetails.substring(0, fromIndex).trim();
        if (description.isBlank()) {
            throw new TasqueException("The description of an event cannot be empty.");
        }

        int toIndex = eventDetails.indexOf("/to");
        if (toIndex == -1) {
            throw new TasqueException("An event must include /to followed by its end.");
        }
        if (toIndex < fromIndex) {
            throw new TasqueException("Use the format: event DESCRIPTION /from START /to END.");
        }

        String from = eventDetails.substring(fromIndex + "/from".length(), toIndex).trim();
        if (from.isEmpty()) {
            throw new TasqueException("The /from value of an event cannot be empty.");
        }

        String to = eventDetails.substring(toIndex + "/to".length()).trim();
        if (to.isEmpty()) {
            throw new TasqueException("The /to value of an event cannot be empty.");
        }

        try {
            Event event = new Event(description, from, to);
            if (event.hasReversedDates()) {
                throw new TasqueException("Unless you've invented time travel, "
                        + "the end date can't be before the start date.");
            }
            return event;
        } catch (DateTimeParseException e) {
            throw new TasqueException("Enter valid dates in yyyy-MM-dd format.");
        }
    }
}
