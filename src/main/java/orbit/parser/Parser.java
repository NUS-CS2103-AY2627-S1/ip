package orbit.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import orbit.exception.OrbitException;
import orbit.task.Deadline;
import orbit.task.Event;
import orbit.task.Task;
import orbit.task.Todo;

/**
 * Interprets user commands and converts their arguments into application data.
 */
public final class Parser {
    private Parser() {
    }

    /**
     * Checks whether input contains the given command, with or without arguments.
     *
     * @param input full user input
     * @param command command word to check
     * @return true if the input begins with the complete command word
     */
    public static boolean isCommand(String input, String command) {
        return input.equals(command) || input.startsWith(command + " ");
    }

    /**
     * Extracts and validates a task number from a command.
     *
     * @param input full user input
     * @param command command whose argument is being parsed
     * @param taskCount number of tasks currently stored
     * @return a valid one-based task number
     * @throws OrbitException if the number is missing, invalid, or outside the task list
     */
    public static int parseTaskNumber(String input, String command, int taskCount) throws OrbitException {
        String numberText = input.substring(command.length()).trim();
        if (numberText.isEmpty()) {
            throw new OrbitException("Mission control needs a task number, for example: " + command + " 2.");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(numberText);
        } catch (NumberFormatException e) {
            throw new OrbitException("Mission coordinates must be whole numbers, such as 1 or 2.");
        }

        if (taskCount == 0) {
            throw new OrbitException("Your flight plan is empty, so there is nothing to " + command + ".");
        }
        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new OrbitException("That mission is outside the flight plan. Choose a number between 1 and "
                    + taskCount + ".");
        }
        return taskNumber;
    }

    /**
     * Extracts and validates the keyword from a find command.
     *
     * @param input full find command
     * @return non-empty keyword to search for
     * @throws OrbitException if no keyword is provided
     */
    public static String parseFindKeyword(String input) throws OrbitException {
        String keyword = input.substring("find".length()).trim();
        if (keyword.isEmpty()) {
            throw new OrbitException("Give the scanner a search signal, for example: find book.");
        }
        return keyword;
    }

    /**
     * Extracts and validates the date from a schedule command.
     *
     * @param input full schedule command
     * @return requested schedule date
     * @throws OrbitException if the date is missing or invalid
     */
    public static LocalDate parseScheduleDate(String input) throws OrbitException {
        String dateText = input.substring("schedule".length()).trim();
        if (dateText.isEmpty()) {
            throw new OrbitException("Set a mission date, for example: schedule 2019-12-02.");
        }
        return parseDate(dateText);
    }

    /**
     * Creates a todo task from user input.
     *
     * @param input full todo command
     * @return parsed todo task
     * @throws OrbitException if the description is empty
     */
    public static Task parseTodo(String input) throws OrbitException {
        String description = input.substring("todo".length()).trim();
        validateDescription(description, "Every mission needs an objective. Add a description after todo.");
        return new Todo(description);
    }

    /**
     * Creates a deadline task from user input.
     *
     * @param input full deadline command
     * @return parsed deadline task
     * @throws OrbitException if its description or deadline is invalid
     */
    public static Task parseDeadline(String input) throws OrbitException {
        String details = input.substring("deadline".length()).trim();
        int byPosition = details.indexOf("/by");
        if (byPosition < 0) {
            throw new OrbitException("This deadline is missing its target date. Add it using /by, "
                    + "for example: deadline return book /by 2019-12-02.");
        }

        String description = details.substring(0, byPosition).trim();
        String byText = details.substring(byPosition + "/by".length()).trim();
        validateDescription(description, "Add a mission objective before /by.");
        if (byText.isEmpty()) {
            throw new OrbitException("The /by field is empty. Set the mission's target date.");
        }
        return new Deadline(description, parseDate(byText));
    }

    /**
     * Creates an event task from user input.
     *
     * @param input full event command
     * @return parsed event task
     * @throws OrbitException if its description, start date, or end date is invalid
     */
    public static Task parseEvent(String input) throws OrbitException {
        String details = input.substring("event".length()).trim();
        int fromPosition = details.indexOf("/from");
        if (fromPosition < 0) {
            throw new OrbitException("This event needs a launch date. Add one using /from.");
        }

        String description = details.substring(0, fromPosition).trim();
        String schedule = details.substring(fromPosition + "/from".length()).trim();
        int toPosition = schedule.indexOf("/to");
        if (toPosition < 0) {
            throw new OrbitException("This event needs a return date. Add one using /to.");
        }

        String fromText = schedule.substring(0, toPosition).trim();
        String toText = schedule.substring(toPosition + "/to".length()).trim();
        if (fromText.isEmpty()) {
            throw new OrbitException("The /from field is empty. Set the mission's launch date.");
        }
        if (toText.isEmpty()) {
            throw new OrbitException("The /to field is empty. Set the mission's return date.");
        }
        validateDescription(description, "Add a mission objective before its dates.");
        LocalDate from = parseDate(fromText);
        LocalDate to = parseDate(toText);
        if (to.isBefore(from)) {
            throw new OrbitException("A mission cannot return before it launches. "
                    + "Set /to to the same date as or a later date than /from.");
        }
        return new Event(description, from, to);
    }

    /**
     * Checks that a task description is present and safe to store.
     *
     * @param description task description to validate
     * @param emptyMessage error shown when the description is empty
     * @throws OrbitException if the description is empty or contains the storage separator
     */
    private static void validateDescription(String description, String emptyMessage) throws OrbitException {
        if (description.isEmpty()) {
            throw new OrbitException(emptyMessage);
        }
        if (description.contains(" | ")) {
            throw new OrbitException("Mission descriptions cannot contain the separator ' | '.");
        }
    }

    /**
     * Parses a date in the {@code yyyy-MM-dd} format used by task commands.
     *
     * @param dateText date entered by the user
     * @return parsed date
     * @throws OrbitException if the text is not a valid date in the required format
     */
    private static LocalDate parseDate(String dateText) throws OrbitException {
        try {
            return LocalDate.parse(dateText);
        } catch (DateTimeParseException e) {
            throw new OrbitException("That date is outside my navigation charts. Use yyyy-MM-dd, "
                    + "for example: 2019-12-02.");
        }
    }
}
