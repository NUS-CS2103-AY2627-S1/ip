package rocky.parser;

import java.math.BigDecimal;
import java.time.LocalDate;

import rocky.RockyException;
import rocky.expense.Expense;
import rocky.task.Deadline;
import rocky.task.Event;
import rocky.task.Task;
import rocky.task.TaskType;
import rocky.task.ToDo;

/**
 * Makes sense of the command lines the user types, turning their text into
 * tasks, expenses and list positions. Every method is static, since parsing
 * needs no state of its own.
 */
public class Parser {
    private static final String EXPENSE_FORMAT =
            "expense <description> /amount <amount> /category <category> [/on <yyyy-mm-dd>]";

    /** Not meant to be instantiated; every member here is static. */
    private Parser() {
    }

    /**
     * Returns the first word of a command line, e.g. "todo" for "todo read book".
     *
     * @param input the command line, with surrounding whitespace removed.
     */
    public static String getCommandWord(String input) {
        return input.split(" ", 2)[0];
    }

    /**
     * Parses a new todo/deadline/event task from a full command line,
     * e.g. "deadline return book /by 2019-12-01".
     *
     * @param type which kind of task the command word identified.
     * @param input the full user input line, including the command word.
     * @return the new task, not yet done.
     * @throws RockyException if the description or required dates are
     *     missing, malformed, or in the wrong format.
     */
    public static Task parseTask(TaskType type, String input) throws RockyException {
        // Rocky only calls this after finding a TaskType for the input's first word.
        assert type != null : "parseTask() needs a known task type";

        String body = getTextAfterCommand(input, type.getKeyword());

        if (body.isEmpty()) {
            throw new RockyException(
                    "A " + type.getKeyword() + " needs a description. Try again?");
        }

        Task task;
        switch (type) {
            case TODO:
                task = new ToDo(body);
                break;
            case DEADLINE:
                task = parseDeadline(body);
                break;
            case EVENT:
                task = parseEvent(body);
                break;
            default:
                throw new RockyException("I don't know how to add that kind of task.");
        }

        // Every case above rejects an empty description before creating the task.
        assert !task.getDescription().isEmpty() : "a new task should always have a description";

        return task;
    }

    /**
     * Parses the text after "deadline" into a Deadline, e.g. "return book /by 2019-12-01".
     *
     * @param body the command line with the "deadline" keyword removed.
     * @throws RockyException if the description or due date is missing, or
     *     the date isn't in ISO format.
     */
    private static Deadline parseDeadline(String body) throws RockyException {
        String[] parts = body.split(" /by ", 2);
        if (parts.length < 2 || parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) {
            throw new RockyException("A deadline needs a due date. Format: deadline <task> /by <when>");
        }
        return new Deadline(parts[0].trim(), parts[1].trim());
    }

    /**
     * Parses the text after "event" into an Event, e.g. "meeting /from 2019-12-02 /to 2019-12-03".
     *
     * @param body the command line with the "event" keyword removed.
     * @throws RockyException if the description, start, or end is missing,
     *     or either date isn't in ISO format.
     */
    private static Event parseEvent(String body) throws RockyException {
        String[] fromParts = body.split(" /from ", 2);
        if (fromParts.length < 2 || fromParts[0].trim().isEmpty()) {
            throw new RockyException(
                    "An event needs a start time. Format: event <task> /from <start> /to <end>");
        }

        String[] toParts = fromParts[1].split(" /to ", 2);
        if (toParts.length < 2 || toParts[0].trim().isEmpty() || toParts[1].trim().isEmpty()) {
            throw new RockyException(
                    "An event needs an end time. Format: event <task> /from <start> /to <end>");
        }
        return new Event(fromParts[0].trim(), toParts[0].trim(), toParts[1].trim());
    }

    /**
     * Parses the text after "expense" into an Expense, e.g.
     * "lunch /amount 12.50 /category food /on 2026-09-11". The date is
     * optional and defaults to today.
     *
     * @param body the command line with the "expense" keyword removed.
     * @throws RockyException if the description, amount, or category is
     *     missing, or any detail is invalid.
     */
    public static Expense parseExpense(String body) throws RockyException {
        String[] amountParts = body.split(" /amount ", 2);
        if (amountParts.length < 2 || amountParts[0].trim().isEmpty()) {
            throw new RockyException(
                    "An expense needs a description and an amount. Format: " + EXPENSE_FORMAT);
        }

        String description = amountParts[0].trim();
        if (description.contains("|")) {
            throw new RockyException("Sorry, an expense's description can't contain \"|\".");
        }

        String[] categoryParts = amountParts[1].split(" /category ", 2);
        if (categoryParts.length < 2) {
            throw new RockyException("An expense needs a category. Format: " + EXPENSE_FORMAT);
        }

        String[] dateParts = categoryParts[1].split(" /on ", 2);
        BigDecimal amount = Expense.parseAmount(categoryParts[0]);
        String category = Expense.parseCategory(dateParts[0]);
        LocalDate date = (dateParts.length < 2) ? LocalDate.now() : Expense.parseDate(dateParts[1]);
        return new Expense(description, amount, category, date);
    }

    /**
     * Returns whatever follows the command word in a command line, trimmed,
     * e.g. "read book" for "todo read book", or "" if nothing follows it.
     *
     * @param input the full command line.
     * @param commandWord the command word the line begins with, e.g. "todo".
     */
    public static String getTextAfterCommand(String input, String commandWord) {
        // Rocky only routes a line to a handler after matching its first word.
        assert input.startsWith(commandWord) : "input should begin with its command word";
        return input.substring(commandWord.length()).trim();
    }

    /**
     * Extracts and validates the 1-based item number in a command such as
     * "mark 2", returning it as a 0-based index.
     *
     * @param input the full command line.
     * @param listSize how many items the numbered list currently has.
     * @param listName what to call the list in messages, e.g. "list".
     * @param itemName what to call one item in messages, e.g. "task".
     * @throws RockyException if no number is given, it isn't a number, or
     *     it's out of range.
     */
    public static int parseIndex(String input, int listSize, String listName, String itemName)
            throws RockyException {
        String command = getCommandWord(input);
        String number = getTextAfterCommand(input, command);

        if (number.isEmpty()) {
            throw new RockyException("Which " + itemName + "? Give me a number, like: " + command + " 2");
        }

        int index;
        try {
            index = Integer.parseInt(number) - 1;
        } catch (NumberFormatException e) {
            throw new RockyException("\"" + number + "\" isn't a number I can work with.");
        }

        if (listSize == 0) {
            throw new RockyException("Your " + listName + " is empty, so there's nothing to "
                    + command + ".");
        }
        if (index < 0 || index >= listSize) {
            throw new RockyException("You only have " + listSize + " " + itemName + "(s), so there's no #"
                    + (index + 1) + ".");
        }

        return index;
    }
}
