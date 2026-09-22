package friday.command;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

import friday.exception.FridayException;
import friday.task.Deadline;
import friday.task.Event;
import friday.task.Task;
import friday.task.TaskList;
import friday.task.Todo;
import friday.ui.Ui;

/**
 * Represents and executes one command entered by the user.
 */
public abstract class Command {
    private static final String DEADLINE_SEPARATOR = "/by ";
    private static final String EVENT_START_SEPARATOR = "/from ";
    private static final String EVENT_END_SEPARATOR = "/to ";

    /**
     * Lists the command kinds recognized by the parser.
     */
    public enum Type {
        /** Empty user input. */
        EMPTY,
        /** Request to exit Friday. */
        BYE,
        /** Request to display all tasks. */
        LIST,
        /** Request to mark a task as completed. */
        MARK,
        /** Request to mark a task as incomplete. */
        UNMARK,
        /** Request to delete a task. */
        DELETE,
        /** Request to find a task */
        FIND,
        /** Request to sort tasks alphabetically. */
        SORT,
        /** Request to add a to-do task. */
        TODO,
        /** Request to add an event. */
        EVENT,
        /** Request to display deadlines on a date. */
        ON_DATE,
        /** Request to add a deadline. */
        DEADLINE,
        /** Input that does not match a supported command. */
        UNKNOWN
    }

    private final Type type;
    private final String input;

    /**
     * Creates a typed command from input recognized by the parser.
     *
     * @param type Kind of command to execute.
     * @param input Complete command input.
     * @throws IllegalArgumentException If type or input is {@code null}.
     */
    protected Command(Type type, String input) {
        if (type == null || input == null) {
            throw new IllegalArgumentException("Command type and input cannot be null.");
        }
        this.type = type;
        this.input = input;
    }

    /**
     * Returns whether this command asks Friday to exit.
     *
     * @return {@code true} if the application should exit; otherwise {@code false}.
     */
    public boolean isExit() {
        return false;
    }

    /**
     * Executes this command and reports its result without leaking command errors.
     *
     * @param tasks Task list to read or modify.
     * @param ui User interface used to display the result.
     */
    public void execute(TaskList tasks, Ui ui) {
        try {
            executeCommand(tasks, ui);
        } catch (DateTimeParseException exception) {
            ui.showLine("Uhm bro, enter a valid date as yyyy-mm-dd, e.g. 2019-10-15. "
                    + "Event times can also use HH:mm, h:mma, or yyyy-mm-ddTHH:mm.");
        } catch (IllegalArgumentException exception) {
            ui.showLine("SIALA!!! " + exception.getMessage());
        } catch (FridayException exception) {
            ui.showLine("SIALA!!! " + exception.getMessage());
        }
    }

    private void executeCommand(TaskList tasks, Ui ui) throws FridayException {
        switch (type) {
            case EMPTY -> throw new FridayException("Enter a command leh.");
            case LIST -> showTasks(tasks, ui);
            case MARK -> markTask(tasks, ui);
            case UNMARK -> unmarkTask(tasks, ui);
            case DELETE -> deleteTask(tasks, ui);
            case FIND -> findTasks(tasks, ui);
            case SORT -> sortTasks(tasks, ui);
            case TODO -> addTodo(tasks, ui);
            case EVENT -> addEvent(tasks, ui);
            case ON_DATE -> showDeadlinesOnDate(tasks, ui);
            case DEADLINE -> addDeadline(tasks, ui);
            case BYE -> {
            // Exit commands are handled by Friday before execution.
            }
            default -> throw new FridayException("Eh? Sorry i don't understand that bro :-(");
        }
    }

    private void showTasks(TaskList tasks, Ui ui) {
        ui.showLine("Here are your tasks:");
        if (tasks.isEmpty()) {
            ui.showLine("Don't have tasks ah bro.");
            return;
        }
        for (int i = 0; i < tasks.size(); i++) {
            ui.showLine((i + 1) + "." + tasks.get(i));
        }
    }

    private void markTask(TaskList tasks, Ui ui) throws FridayException {
        int taskIndex = getTaskNumber("mark", tasks.size()) - 1;
        assert taskIndex >= 0 && taskIndex < tasks.size()
                : "Validated task index must be within the task list";
        tasks.get(taskIndex).markAsDone();
        ui.showLine("Good! This task done liao: ");
        ui.showLine(tasks.get(taskIndex).toString());
    }

    private void unmarkTask(TaskList tasks, Ui ui) throws FridayException {
        int taskIndex = getTaskNumber("unmark", tasks.size()) - 1;
        assert taskIndex >= 0 && taskIndex < tasks.size()
                : "Validated task index must be within the task list";
        tasks.get(taskIndex).markAsUndone();
        ui.showLine("Never mind! Can do later: ");
        ui.showLine(tasks.get(taskIndex).toString());
    }

    private void deleteTask(TaskList tasks, Ui ui) throws FridayException {
        int previousTaskCount = tasks.size();
        int taskIndex = getTaskNumber("delete", tasks.size()) - 1;
        assert taskIndex >= 0 && taskIndex < tasks.size()
                : "Validated task index must be within the task list";
        Task deletedTask = tasks.delete(taskIndex);
        assert tasks.size() == previousTaskCount - 1
                : "Deleting one task must reduce the task count by one";
        ui.showLine("Okay, I removed this task:");
        ui.showLine(deletedTask.toString());
        ui.showLine("you have " + tasks.size() + " tasks lah.");
    }

    private void findTasks(TaskList tasks, Ui ui) throws FridayException {
        String keyword = input.substring("find".length()).trim();
        if (keyword.isEmpty()) {
            throw new FridayException("Please specify a keyword to find.");
        }

        List<Task> matches = tasks.find(keyword);
        ui.showLine("Okay here's the task ah:");
        if (matches.isEmpty()) {
            ui.showLine("No matching tasks found leh.");
            return;
        }
        for (int i = 0; i < matches.size(); i++) {
            ui.showLine((i + 1) + "." + matches.get(i));
        }
    }

    private void sortTasks(TaskList tasks, Ui ui) throws FridayException {
        String direction = input.substring("sort".length()).trim();
        if (direction.isEmpty()) {
            tasks.sortAlphabetically();
            ui.showLine("Okay, I sorted your tasks from A to Z.");
        } else if (direction.equals("reverse")) {
            tasks.sortReverseAlphabetically();
            ui.showLine("Okay, I sorted your tasks from Z to A.");
        } else if (direction.equals("type")) {
            tasks.sortByType();
            ui.showLine("Okay, I grouped your tasks by type.");
        } else {
            throw new FridayException(
                    "Use 'sort' for A to Z, 'sort reverse' for Z to A, or 'sort type' by type.");
        }
    }

    private void addTodo(TaskList tasks, Ui ui) throws FridayException {
        String description = input.substring("todo".length()).trim();
        if (description.isEmpty()) {
            throw new FridayException("todo need description leh.");
        }

        int previousTaskCount = tasks.size();
        tasks.add(new Todo(description));
        ui.showLine("okay okay, i add " + description + " to the list lor.");
        assert tasks.size() == previousTaskCount + 1
                : "Adding one task must increase the task count by one";
        ui.showLine("you have " + tasks.size() + " tasks lah.");
    }

    private void addEvent(TaskList tasks, Ui ui) throws FridayException {
        String arguments = input.substring("event".length()).trim();
        if (arguments.isEmpty()) {
            throw new FridayException("what event ah?");
        }
        validateParameters(arguments, List.of("/from", "/to"));
        int fromIndex = arguments.indexOf(EVENT_START_SEPARATOR);
        int toIndex = arguments.indexOf(
                EVENT_END_SEPARATOR, fromIndex + EVENT_START_SEPARATOR.length());
        if (fromIndex < 0 || toIndex < 0) {
            throw new FridayException(
                    "ARE YOU DONE?! write like this lah: event DESCRIPTION /from START /to END");
        }
        String description = arguments.substring(0, fromIndex).trim();
        String start = arguments.substring(
                fromIndex + EVENT_START_SEPARATOR.length(), toIndex).trim();
        String end = arguments.substring(toIndex + EVENT_END_SEPARATOR.length()).trim();
        if (description.isEmpty() || start.isEmpty() || end.isEmpty()) {
            throw new FridayException(
                    "Tolong, an event's description, start, and end cannot be empty lei.");
        }
        Task event = new Event(new String[] {description, start, end});
        int previousTaskCount = tasks.size();
        tasks.add(event);
        assert tasks.size() == previousTaskCount + 1
                : "Adding one task must increase the task count by one";
        ui.showLine("orh, don't forget to attend ah: ");
        ui.showLine(event.toString());
        ui.showLine("you have " + tasks.size() + " tasks lah.");
    }

    private void showDeadlinesOnDate(TaskList tasks, Ui ui) throws FridayException {
        String dateText = input.substring("on".length()).trim();
        if (dateText.isEmpty()) {
            throw new FridayException("Your date must write like yyyy-mm-dd, e.g. 2019-10-15.");
        }
        LocalDate requestedDate = LocalDate.parse(dateText);
        boolean foundDeadline = false;
        ui.showLine("Your deadlines on " + requestedDate + " ah:");
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (task instanceof Deadline deadline
                    && deadline.getDeadlineDate().equals(requestedDate)) {
                ui.showLine((i + 1) + "." + deadline);
                foundDeadline = true;
            }
        }
        if (!foundDeadline) {
            ui.showLine("Got nothing due. Heng ah!");
        }
    }

    private void addDeadline(TaskList tasks, Ui ui) throws FridayException {
        String arguments = input.substring("deadline".length()).trim();
        if (arguments.isEmpty()) {
            throw new FridayException("What thing got deadline ah?");
        }
        validateParameters(arguments, List.of("/by"));
        int byIndex = arguments.indexOf(DEADLINE_SEPARATOR);
        if (byIndex < 0) {
            throw new FridayException("Tolong, write this format: deadline DESCRIPTION /by DATE");
        }
        String description = arguments.substring(0, byIndex).trim();
        String date = arguments.substring(byIndex + DEADLINE_SEPARATOR.length()).trim();
        if (description.isEmpty() || date.isEmpty()) {
            throw new FridayException("Deadline's description and date must fill up lei.");
        }
        Task deadline = new Deadline(new String[] {description, date});
        int previousTaskCount = tasks.size();
        tasks.add(deadline);
        assert tasks.size() == previousTaskCount + 1
                : "Adding one task must increase the task count by one";
        ui.showLine("Remember to finish hor: ");
        ui.showLine(deadline.toString());
        ui.showLine("you have " + tasks.size() + " tasks lah.");
    }

    /** Checks that reserved parameters occur exactly once and in the expected order. */
    private void validateParameters(String arguments, List<String> expected) throws FridayException {
        List<String> parameters = Arrays.stream(arguments.split("\\s+"))
                .filter(word -> word.equals("/from") || word.equals("/to") || word.equals("/by"))
                .toList();
        if (!parameters.equals(expected)) {
            throw new FridayException("Specify each parameter once, in this order: "
                    + String.join(" ", expected));
        }
    }

    private int getTaskNumber(String command, int taskCount) throws FridayException {
        String numberText = input.substring(command.length()).trim();
        if (numberText.isEmpty()) {
            throw new FridayException("Please specify a task number.");
        }
        try {
            int taskNumber = Integer.parseInt(numberText);
            if (taskNumber < 1 || taskNumber > taskCount) {
                throw new FridayException("That task number does not exist.");
            }
            return taskNumber;
        } catch (NumberFormatException exception) {
            throw new FridayException("The task number must be a whole number.");
        }
    }
}
