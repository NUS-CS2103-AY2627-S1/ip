package chim.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import chim.exception.ChimException;
import chim.storage.Storage;
import chim.task.Deadline;
import chim.task.Event;
import chim.task.Task;
import chim.task.TaskList;
import chim.task.Todo;
import chim.ui.Ui;

/**
 * Interprets raw user input and carries out the corresponding action
 * on the task list, returning the response message to show the user.
 */
public class Parser {

    private static final String COMMAND_BYE = "bye";
    private static final String COMMAND_LIST = "list";
    private static final String COMMAND_FIND = "find";
    private static final String COMMAND_MARK = "mark";
    private static final String COMMAND_UNMARK = "unmark";
    private static final String COMMAND_DELETE = "delete";
    private static final String COMMAND_TODO = "todo";
    private static final String COMMAND_DEADLINE = "deadline";
    private static final String COMMAND_EVENT = "event";
    private static final String COMMAND_PRIORITY = "priority";

    private static final String DEADLINE_SEPARATOR = "/by";
    private static final String EVENT_FROM_SEPARATOR = "/from";
    private static final String EVENT_TO_SEPARATOR = "/to";

    /**
     * Parses a single line of user input, executes it, and returns the
     * message to display to the user.
     *
     * @param input Raw line of input from the user.
     * @param tasks TaskList to operate on.
     * @param ui Ui used to format response messages.
     * @param storage Storage used to persist changes to disk.
     * @return The response message for this command.
     * @throws ChimException If the input is not a recognised or valid command.
     */
    public String parseAndExecute(String input, TaskList tasks, Ui ui, Storage storage) throws ChimException {
        if (input.equals(COMMAND_BYE)) {
            return ui.getGoodbyeMessage();
        }

        if (input.equals(COMMAND_LIST)) {
            return ui.getTaskListMessage(tasks.getTasks());
        }

        if (matchesCommand(input, COMMAND_FIND)) {
            return handleFind(input, tasks, ui);
        }

        if (matchesCommand(input, COMMAND_MARK)) {
            return handleMark(input, tasks, ui, storage);
        }

        if (matchesCommand(input, COMMAND_UNMARK)) {
            return handleUnmark(input, tasks, ui, storage);
        }

        if (matchesCommand(input, COMMAND_DELETE)) {
            return handleDelete(input, tasks, ui, storage);
        }

        if (matchesCommand(input, COMMAND_PRIORITY)) {
            return handlePriority(input, tasks, ui, storage);
        }

        if (matchesCommand(input, COMMAND_TODO)) {
            return handleTodo(input, tasks, ui, storage);
        }

        if (matchesCommand(input, COMMAND_DEADLINE)) {
            return handleDeadline(input, tasks, ui, storage);
        }

        if (matchesCommand(input, COMMAND_EVENT)) {
            return handleEvent(input, tasks, ui, storage);
        }

        throw new ChimException("Chim does not understand what that means :-(");
    }

    private String handleFind(String input, TaskList tasks, Ui ui) throws ChimException {
        String keyword = input.length() > COMMAND_FIND.length()
                ? input.substring(COMMAND_FIND.length()).trim()
                : "";
        if (keyword.isEmpty()) {
            throw new ChimException("Ooh, what should I search for? Give me a word!");
        }
        return ui.getMatchingTasksMessage(tasks.find(keyword));
    }

    private String handleMark(String input, TaskList tasks, Ui ui, Storage storage) throws ChimException {
        int index = parseIndex(input, COMMAND_MARK, tasks.size());
        Task task = tasks.get(index);
        task.markAsDone();
        storage.save(tasks.getTasks());
        return ui.getTaskMarkedMessage(task);
    }

    private String handleUnmark(String input, TaskList tasks, Ui ui, Storage storage) throws ChimException {
        int index = parseIndex(input, COMMAND_UNMARK, tasks.size());
        Task task = tasks.get(index);
        task.markAsNotDone();
        storage.save(tasks.getTasks());
        return ui.getTaskUnmarkedMessage(task);
    }

    private String handleDelete(String input, TaskList tasks, Ui ui, Storage storage) throws ChimException {
        int index = parseIndex(input, COMMAND_DELETE, tasks.size());
        Task removed = tasks.delete(index);
        storage.save(tasks.getTasks());
        return ui.getTaskDeletedMessage(removed, tasks.size());
    }

    private String handlePriority(String input, TaskList tasks, Ui ui, Storage storage) throws ChimException {
        String rest = input.length() > COMMAND_PRIORITY.length()
                ? input.substring(COMMAND_PRIORITY.length()).trim()
                : "";

        String[] parts = rest.split(" ", 2);
        if (parts.length < 2) {
            throw new ChimException("Please provide a task number and priority.");
        }

        int index = parseIndex(COMMAND_PRIORITY + " " + parts[0], COMMAND_PRIORITY, tasks.size());
        String priority = parts[1].trim().toLowerCase();

        if (!priority.equals("high") && !priority.equals("medium")
                && !priority.equals("low") && !priority.equals("none")) {
            throw new ChimException("Priority must be high, medium, low, or none.");
        }

        Task task = tasks.get(index);
        task.setPriority(priority);
        storage.save(tasks.getTasks());
        return ui.getTaskPriorityMessage(task);
    }

    private String handleTodo(String input, TaskList tasks, Ui ui, Storage storage) throws ChimException {
        String description = input.length() > COMMAND_TODO.length()
                ? input.substring(COMMAND_TODO.length()).trim()
                : "";
        if (description.isEmpty()) {
            throw new ChimException("A todo needs a description! What should I add?");
        }
        checkNoPipeCharacter(description);

        Todo newTodo = new Todo(description);
        if (tasks.isDuplicate(newTodo)) {
            throw new ChimException("Looks like that todo is already on your list!");
        }

        tasks.add(newTodo);
        storage.save(tasks.getTasks());
        return ui.getTaskAddedMessage(tasks.get(tasks.size() - 1), tasks.size());
    }

    private String handleDeadline(String input, TaskList tasks, Ui ui, Storage storage) throws ChimException {
        String rest = input.length() > COMMAND_DEADLINE.length()
                ? input.substring(COMMAND_DEADLINE.length()).trim()
                : "";
        if (rest.isEmpty()) {
            throw new ChimException("A deadline needs a description! What's happening?");
        }
        if (!rest.contains(DEADLINE_SEPARATOR)) {
            throw new ChimException("Don't forget the '/by' and a due date for your deadline!");
        }
        if (countOccurrences(rest, DEADLINE_SEPARATOR) > 1) {
            throw new ChimException("Whoops, I see more than one '/by'! Please use it just once.");
        }

        String[] parts = rest.split(DEADLINE_SEPARATOR, 2);
        String description = parts[0].trim();
        String by = parts[1].trim();

        if (description.isEmpty()) {
            throw new ChimException("A deadline needs a description! What's happening?");
        }
        if (by.isEmpty()) {
            throw new ChimException("When's this deadline due? Add a date after '/by'!");
        }
        checkNoPipeCharacter(description);

        LocalDate byDate;
        try {
            byDate = LocalDate.parse(by);
        } catch (DateTimeParseException e) {
            throw new ChimException("That date looks a bit off! Try yyyy-mm-dd, like 2019-10-15.");
        }

        Deadline newDeadline = new Deadline(description, byDate);
        if (tasks.isDuplicate(newDeadline)) {
            throw new ChimException("Looks like that deadline is already on your list!");
        }

        tasks.add(newDeadline);
        storage.save(tasks.getTasks());
        return ui.getTaskAddedMessage(tasks.get(tasks.size() - 1), tasks.size());
    }

    private String handleEvent(String input, TaskList tasks, Ui ui, Storage storage) throws ChimException {
        String rest = input.length() > COMMAND_EVENT.length()
                ? input.substring(COMMAND_EVENT.length()).trim()
                : "";

        if (rest.isEmpty()) {
            throw new ChimException("An event needs a description! What's happening?");
        }
        if (!rest.contains(EVENT_FROM_SEPARATOR) || !rest.contains(EVENT_TO_SEPARATOR)) {
            throw new ChimException("Don't forget the '/from' and '/to' times for your event!");
        }
        if (countOccurrences(rest, EVENT_FROM_SEPARATOR) > 1 || countOccurrences(rest, EVENT_TO_SEPARATOR) > 1) {
            throw new ChimException("Whoops, I see '/from' or '/to' more than once! Please use each just once.");
        }

        String[] fromSplit = rest.split(EVENT_FROM_SEPARATOR, 2);
        String description = fromSplit[0].trim();
        String remainder = fromSplit[1].trim();

        String[] toSplit = remainder.split(EVENT_TO_SEPARATOR, 2);
        String from = toSplit[0].trim();
        String to = toSplit.length > 1 ? toSplit[1].trim() : "";

        if (description.isEmpty()) {
            throw new ChimException("An event needs a description! What's happening?");
        }
        if (from.isEmpty() || to.isEmpty()) {
            throw new ChimException("I need both a start and end time for that event!");
        }
        checkNoPipeCharacter(description);
        checkNoPipeCharacter(from);
        checkNoPipeCharacter(to);
        checkEventDateOrder(from, to);

        Event newEvent = new Event(description, from, to);
        if (tasks.isDuplicate(newEvent)) {
            throw new ChimException("Looks like that event is already on your list!");
        }

        tasks.add(newEvent);
        storage.save(tasks.getTasks());
        return ui.getTaskAddedMessage(tasks.get(tasks.size() - 1), tasks.size());
    }

    private int parseIndex(String input, String command, int taskCount) throws ChimException {
        String numberPart = input.length() > command.length() ? input.substring(command.length()).trim() : "";

        if (numberPart.isEmpty()) {
            throw new ChimException("Which task number should I " + command + "? Tell me the number!");
        }

        int index = Integer.parseInt(numberPart) - 1;

        if (index < 0 || index >= taskCount) {
            throw new ChimException("Hmm, I don't see a task with that number on your list.");
        }

        assert index >= 0 && index < taskCount : "Parsed index should be within task list bounds";

        return index;
    }

    /**
     * Returns whether the input is exactly the given command word, or
     * starts with that command word followed by a space (i.e. a genuine
     * word match, not just a shared prefix like "todoodle" matching "todo").
     *
     * @param input Raw user input.
     * @param command Command word to check for.
     * @return true if input is a real match for the command word.
     */
    private boolean matchesCommand(String input, String command) {
        return input.equals(command) || input.startsWith(command + " ");
    }

    /**
     * Counts how many times a substring occurs within a string.
     *
     * @param text Text to search within.
     * @param target Substring to count occurrences of.
     * @return Number of occurrences.
     */
    private int countOccurrences(String text, String target) {
        int count = 0;
        int index = 0;
        while ((index = text.indexOf(target, index)) != -1) {
            count++;
            index += target.length();
        }
        return count;
    }

    /**
     * Throws an exception if the given text contains the '|' character,
     * since it is reserved as the delimiter in the saved data file.
     *
     * @param text Text to validate.
     * @throws ChimException If the text contains a '|' character.
     */
    private void checkNoPipeCharacter(String text) throws ChimException {
        if (text.contains("|")) {
            throw new ChimException("Oops, please avoid using the '|' character — I use it to save your tasks!");
        }
    }

    /**
     * Checks that an event's start is before its end, but only when both
     * values are parseable as ISO dates. Free-text times (e.g. "2pm") are
     * left unvalidated.
     *
     * @param from Event start text.
     * @param to Event end text.
     * @throws ChimException If both are valid dates and start is not before end.
     */
    private void checkEventDateOrder(String from, String to) throws ChimException {
        try {
            LocalDate fromDate = LocalDate.parse(from);
            LocalDate toDate = LocalDate.parse(to);
            if (!fromDate.isBefore(toDate)) {
                throw new ChimException("The event's start date should be before its end date!");
            }
        } catch (DateTimeParseException e) {
            // Not both parseable as dates; skip the ordering check.
        }
    }
}
