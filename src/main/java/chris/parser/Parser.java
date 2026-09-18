package chris.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import chris.exception.ChrisException;
import chris.task.Deadline;
import chris.task.Event;
import chris.task.Todo;

/**
 * Parses and validates commands entered by the user.
 */
public final class Parser {
    private Parser() {
    }

    /**
     * Identifies the command represented by the user's input.
     *
     * @param input Complete user input.
     * @return Type of command entered by the user.
     */
    public static CommandType parseCommandType(String input) {
        String commandWord = input.isEmpty() ? "" : input.split("\\s+", 2)[0];
        return CommandType.parseCommandWord(commandWord);
    }

    /**
     * Extracts and validates the keyword from a find command.
     *
     * @param input Complete find command.
     * @return Keyword to search for.
     * @throws ChrisException If the keyword is empty.
     */
    public static String parseFindKeyword(String input) throws ChrisException {
        String keyword = input.substring("find".length()).trim();
        if (keyword.isEmpty()) {
            throw new ChrisException("Tell me what to find. Try: find book");
        }
        return keyword;
    }

    /**
     * Creates a todo task from a todo command.
     *
     * @param input Complete todo command.
     * @return Todo task described by the command.
     * @throws ChrisException If the description is empty.
     */
    public static Todo parseTodo(String input) throws ChrisException {
        String description = input.substring("todo".length()).trim();
        if (description.isEmpty()) {
            throw new ChrisException("I need a description for that todo. Try: todo borrow book");
        }
        return new Todo(description);
    }

    /**
     * Creates a deadline task from a deadline command.
     *
     * @param input Complete deadline command.
     * @return Deadline task described by the command.
     * @throws ChrisException If its description or deadline information is missing.
     */
    public static Deadline parseDeadline(String input) throws ChrisException {
        String details = input.substring("deadline".length()).trim();
        int byPosition = details.indexOf("/by");
        if (byPosition < 0) {
            throw new ChrisException("A deadline needs '/by'. Try: deadline return book /by 2019-12-02");
        }

        String description = details.substring(0, byPosition).trim();
        String byText = details.substring(byPosition + "/by".length()).trim();
        if (description.isEmpty()) {
            throw new ChrisException("I need a description before '/by'.");
        }
        if (byText.isEmpty()) {
            throw new ChrisException("I need a date after '/by'. Use yyyy-MM-dd.");
        }

        try {
            LocalDate by = LocalDate.parse(byText);
            return new Deadline(description, by);
        } catch (DateTimeParseException exception) {
            throw new ChrisException("That date is invalid. Use yyyy-MM-dd, for example 2019-12-02.");
        }
    }

    /**
     * Creates an event task from an event command.
     *
     * @param input Complete event command.
     * @return Event task described by the command.
     * @throws ChrisException If its description, start, or end information is missing.
     */
    public static Event parseEvent(String input) throws ChrisException {
        String details = input.substring("event".length()).trim();
        int fromPosition = details.indexOf("/from");
        if (fromPosition < 0) {
            throw new ChrisException("An event needs '/from'. Try: event meeting /from 2pm /to 4pm");
        }

        String description = details.substring(0, fromPosition).trim();
        String timing = details.substring(fromPosition + "/from".length()).trim();
        int toPosition = timing.indexOf("/to");
        if (toPosition < 0) {
            throw new ChrisException("An event needs '/to'. Try: event meeting /from 2pm /to 4pm");
        }

        String from = timing.substring(0, toPosition).trim();
        String to = timing.substring(toPosition + "/to".length()).trim();
        if (description.isEmpty()) {
            throw new ChrisException("I need an event description before '/from'.");
        }
        if (from.isEmpty()) {
            throw new ChrisException("I need a start time after '/from'.");
        }
        if (to.isEmpty()) {
            throw new ChrisException("I need an end time after '/to'.");
        }
        return new Event(description, from, to);
    }

    /**
     * Extracts and validates a task index from a mark, unmark, or delete command.
     *
     * @param input Complete mark, unmark, or delete command.
     * @param command Command word being parsed.
     * @param taskCount Current number of stored tasks.
     * @return Zero-based index of the selected task.
     * @throws ChrisException If the task number is missing, invalid, or out of range.
     */
    public static int parseTaskIndex(String input, String command, int taskCount) throws ChrisException {
        String numberText = input.substring(command.length()).trim();
        if (numberText.isEmpty()) {
            throw new ChrisException("Tell me which task to " + command + ". Try: " + command + " 2");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(numberText);
        } catch (NumberFormatException exception) {
            throw new ChrisException("The task number must be a whole number.");
        }

        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new ChrisException("Task " + taskNumber + " does not exist. Choose a number from the list.");
        }
        return taskNumber - 1;
    }
}
