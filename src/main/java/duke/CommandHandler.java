package duke;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Locale;

/** Processes Furina commands independently of the interface used to enter them. */
public class CommandHandler {
    private final ArrayList<Task> tasks;

    private final boolean shouldPersist;

    /** Creates a handler containing the tasks saved by the user. */
    public CommandHandler() {
        this(TaskStorage.load(), true);
    }

    /**
     * Creates a non-persisting handler for isolated tests.
     *
     * @param initialTasks the tasks to use in memory
     */
    CommandHandler(ArrayList<Task> initialTasks) {
        this(initialTasks, false);
    }

    private CommandHandler(ArrayList<Task> initialTasks, boolean shouldPersist) {
        tasks = initialTasks;
        this.shouldPersist = shouldPersist;
    }

    /**
     * Processes one command and returns the response text without console separators.
     *
     * @param command the command entered by the user
     * @return the response, or an empty string for blank input
     */
    public String handleCommand(String command) {
        String normalizedCommand = command == null ? "" : command.trim();
        if (normalizedCommand.isBlank()) {
            return "";
        }
        if (normalizedCommand.equalsIgnoreCase("bye")) {
            return "Bye. Hope to see you again soon!";
        }
        if (normalizedCommand.equalsIgnoreCase("list")) {
            return listTasks();
        }
        if (normalizedCommand.equalsIgnoreCase("sort")) {
            return sortTasks();
        }
        if (isCommand(normalizedCommand, "find")) {
            return findTasks(normalizedCommand);
        }
        if (isCommand(normalizedCommand, "delete")) {
            return deleteTask(normalizedCommand);
        }
        if (isCommand(normalizedCommand, "mark")) {
            return updateTaskStatus(normalizedCommand, true);
        }
        if (isCommand(normalizedCommand, "unmark")) {
            return updateTaskStatus(normalizedCommand, false);
        }
        try {
            Task newTask = createTask(normalizedCommand);
            if (containsDuplicate(newTask)) {
                return "OOPS!!! That task is already in your list.";
            }
            tasks.add(newTask);
            if (!saveTasks()) {
                tasks.remove(tasks.size() - 1);
                return "OOPS!!! I couldn't save the task list.";
            }
            return "Got it. I've added this task:\n"
                    + newTask + "\nNow you have " + tasks.size() + " tasks in the list.";
        } catch (IllegalArgumentException exception) {
            return "OOPS!!! " + exception.getMessage();
        }
    }

    private String listTasks() {
        StringBuilder response = new StringBuilder("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            response.append("\n").append(i + 1).append(".").append(tasks.get(i));
        }
        return response.toString();
    }

    private String sortTasks() {
        tasks.sort(Task.byDeadline());
        if (!saveTasks()) {
            return "OOPS!!! I couldn't save the task list.";
        }
        return "Sorted tasks by deadline:\n" + listTasks();
    }

    private String findTasks(String command) {
        String keyword = argumentText(command, "find");
        if (keyword.isBlank()) {
            return "OOPS!!! Please provide a keyword to search for.";
        }
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        StringBuilder response = new StringBuilder("Here are the matching tasks in your list:");
        boolean hasMatch = false;
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (task.description.toLowerCase(Locale.ROOT).contains(normalizedKeyword)) {
                response.append("\n").append(i + 1).append(".").append(task);
                hasMatch = true;
            }
        }
        if (!hasMatch) {
            response.append("\nNo matching tasks found.");
        }
        return response.toString();
    }

    private String updateTaskStatus(String command, boolean isDone) {
        String[] commandParts = command.trim().split("\\s+");
        if (commandParts.length != 2) {
            return "OOPS!!! Please provide a task number, for example: "
                    + (isDone ? "mark 2." : "unmark 2.");
        }
        if (!commandParts[1].matches("\\d+")) {
            return "OOPS!!! Task numbers must be positive whole numbers.";
        }
        int taskIndex = parseTaskIndex(commandParts[1]);
        if (taskIndex == Integer.MIN_VALUE) {
            return "OOPS!!! Task numbers must be positive whole numbers.";
        }
        if (taskIndex < 0 || taskIndex >= tasks.size()) {
            return "OOPS!!! There is no task with that number.";
        }
        Task task = tasks.get(taskIndex);
        boolean previousStatus = task.isDone;
        if (isDone) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
        if (!saveTasks()) {
            if (previousStatus) {
                task.markAsDone();
            } else {
                task.markAsNotDone();
            }
            return "OOPS!!! I couldn't save the task list.";
        }
        return (isDone ? "Nice! I've marked this task as done:\n" :
                "OK, I've marked this task as not done yet:\n") + task;
    }

    private String deleteTask(String command) {
        String[] commandParts = command.trim().split("\\s+");
        if (commandParts.length != 2) {
            return "OOPS!!! Please provide a task number, for example: delete 2.";
        }
        if (!commandParts[1].matches("\\d+")) {
            return "OOPS!!! Task numbers must be positive whole numbers.";
        }
        int taskIndex = parseTaskIndex(commandParts[1]);
        if (taskIndex == Integer.MIN_VALUE) {
            return "OOPS!!! Task numbers must be positive whole numbers.";
        }
        if (taskIndex < 0 || taskIndex >= tasks.size()) {
            return "OOPS!!! There is no task with that number.";
        }
        Task deletedTask = tasks.remove(taskIndex);
        if (!saveTasks()) {
            tasks.add(taskIndex, deletedTask);
            return "OOPS!!! I couldn't save the task list.";
        }
        return "Noted. I've removed this task:\n" + deletedTask
                + "\nNow you have " + tasks.size() + " tasks in the list.";
    }

    private Task createTask(String command) {
        if (isCommand(command, "todo")) {
            String description = argumentText(command, "todo");
            if (description.isBlank()) {
                throw new IllegalArgumentException("A todo task needs a description.");
            }
            return new Task(TaskType.TODO, description, null, null, null);
        }
        if (isCommand(command, "deadline")) {
            String[] parts = argumentText(command, "deadline").split("\\s+/by\\s+", 2);
            if (parts.length == 2 && !parts[0].isBlank() && !parts[1].isBlank()) {
                String deadlineText = parts[1].trim();
                try {
                    return new Task(parts[0].trim(), DateTimeParser.parse(deadlineText));
                } catch (DateTimeParseException exception) {
                    if (looksLikeDate(deadlineText)) {
                        throw new IllegalArgumentException("Please provide a valid deadline date.");
                    }
                    return new Task(TaskType.DEADLINE, parts[0].trim(), deadlineText, null, null);
                }
            }
            throw new IllegalArgumentException("A deadline needs a description and a date after /by.");
        }
        if (isCommand(command, "event")) {
            String[] parts = argumentText(command, "event")
                    .split("\\s+/from\\s+|\\s+/to\\s+", 3);
            if (parts.length == 3 && !parts[0].isBlank()
                    && !parts[1].isBlank() && !parts[2].isBlank()) {
                validateEventTimes(parts[1].trim(), parts[2].trim());
                return new Task(TaskType.EVENT, parts[0].trim(), null,
                        parts[1].trim(), parts[2].trim());
            }
            throw new IllegalArgumentException(
                    "An event needs a description, a start time after /from, and an end time after /to.");
        }
        throw new IllegalArgumentException("I don't recognize that command.");
    }

    private void validateEventTimes(String from, String to) {
        if (!looksLikeDate(from) || !looksLikeDate(to)) {
            return;
        }
        try {
            LocalDateTime start = DateTimeParser.parse(from);
            LocalDateTime end = DateTimeParser.parse(to);
            if (!end.isAfter(start)) {
                throw new IllegalArgumentException("An event's end time must be after its start time.");
            }
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("Please provide valid event dates and times.");
        }
    }

    private boolean containsDuplicate(Task candidate) {
        return tasks.stream().anyMatch(task -> task.hasSameDetails(candidate));
    }

    private boolean isCommand(String input, String command) {
        return input.equalsIgnoreCase(command)
                || (input.length() > command.length()
                && input.regionMatches(true, 0, command, 0, command.length())
                && Character.isWhitespace(input.charAt(command.length())));
    }

    private String argumentText(String command, String commandName) {
        return command.length() <= commandName.length()
                ? ""
                : command.substring(commandName.length()).trim();
    }

    private boolean looksLikeDate(String text) {
        return text.matches("\\d{4}-\\d{1,2}-\\d{1,2}(?:\\s+\\d{1,2}:?\\d{2})?")
                || text.matches("\\d{1,2}/\\d{1,2}/\\d{4}(?:\\s+\\d{1,2}:?\\d{2})?");
    }

    private int parseTaskIndex(String taskNumber) {
        try {
            return Integer.parseInt(taskNumber) - 1;
        } catch (NumberFormatException exception) {
            return Integer.MIN_VALUE;
        }
    }

    private boolean saveTasks() {
        return !shouldPersist || TaskStorage.save(tasks);
    }
}
