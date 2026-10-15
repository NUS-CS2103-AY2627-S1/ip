package amy;

import amy.task.Deadline;
import amy.task.Event;
import amy.task.Task;
import amy.task.Todo;

/**
 * Interprets user commands and creates tasks from task commands.
 */
public class Parser {
    /**
     * Converts a task command into a task object without parsing dates.
     *
     * @param normalizedCommand the trimmed command
     * @param originalCommand the original input, used as a fallback description
     * @return the task represented by the command
     * @throws AmyException when the command is incomplete or invalid
     */
    public Task createTask(String normalizedCommand, String originalCommand) throws AmyException {
        if (normalizedCommand.equals("todo")) {
            throw new AmyException("A todo description cannot be empty.");
        }
        if (normalizedCommand.equals("deadline") || normalizedCommand.startsWith("deadline ")) {
            String body = normalizedCommand.equals("deadline") ? "" : normalizedCommand.substring(9).trim();
            int byIndex = body.indexOf("/by");
            int markerLength = 3;
            if (byIndex < 0) {
                byIndex = body.indexOf("/ by");
                markerLength = 4;
            }
            if (byIndex >= 0 && !body.substring(byIndex + markerLength).trim().isEmpty()
                    && !body.substring(0, byIndex).trim().isEmpty()) {
                try {
                    return new Deadline(body.substring(0, byIndex).trim(),
                            body.substring(byIndex + markerLength).trim());
                } catch (java.time.format.DateTimeParseException e) {
                    throw invalidDateException();
                }
            }
            int slashIndex = body.indexOf('/');
            if (slashIndex >= 0 && !body.substring(0, slashIndex).trim().isEmpty()
                    && !body.substring(slashIndex + 1).trim().isEmpty()) {
                try {
                    return new Deadline(body.substring(0, slashIndex).trim(),
                            body.substring(slashIndex + 1).trim());
                } catch (java.time.format.DateTimeParseException e) {
                    throw invalidDateException();
                }
            }
            throw new AmyException("Please specify a deadline in the format: "
                    + "deadline <description> /by <date/time>.");
        }
        if (normalizedCommand.equals("event") || normalizedCommand.startsWith("event ")) {
            String body = normalizedCommand.equals("event") ? "" : normalizedCommand.substring(6).trim();
            int fromIndex = body.indexOf("/from");
            int toIndex = body.indexOf("/to", fromIndex + 5);
            if (fromIndex >= 0 && toIndex >= 0 && !body.substring(0, fromIndex).trim().isEmpty()
                    && !body.substring(fromIndex + 5, toIndex).trim().isEmpty()
                    && !body.substring(toIndex + 3).trim().isEmpty()) {
                return new Event(body.substring(0, fromIndex).trim(),
                        body.substring(fromIndex + 5, toIndex).trim(), body.substring(toIndex + 3).trim());
            }
            int firstSlash = body.indexOf('/');
            int secondSlash = firstSlash >= 0 ? body.indexOf('/', firstSlash + 1) : -1;
            if (firstSlash >= 0 && secondSlash >= 0 && !body.substring(0, firstSlash).trim().isEmpty()
                    && !body.substring(firstSlash + 1, secondSlash).trim().isEmpty()
                    && !body.substring(secondSlash + 1).trim().isEmpty()) {
                return new Event(body.substring(0, firstSlash).trim(),
                        body.substring(firstSlash + 1, secondSlash).trim(), body.substring(secondSlash + 1).trim());
            }
            throw new AmyException("Please specify an event in the format: "
                    + "event <description> /from <start> /to <end>.");
        }
        String description = normalizedCommand.startsWith("todo ")
                ? normalizedCommand.substring(5).trim() : originalCommand;
        if (description.trim().isEmpty()) {
            throw new AmyException("A todo description cannot be empty.");
        }
        return new Todo(description);
    }

    /**
     * Checks whether a command explicitly identifies a supported task type.
     *
     * @param command the trimmed user command
     * @return true when the command starts with todo, deadline, or event
     */
    public boolean isTaskCommand(String command) {
        return command.equals("todo") || command.startsWith("todo ")
                || command.equals("deadline") || command.startsWith("deadline ")
                || command.equals("event") || command.startsWith("event ");
    }

    private AmyException invalidDateException() {
        return new AmyException("Please use the date format: d/M/yyyy HHmm, e.g. 2/12/2019 1800");
    }
}
