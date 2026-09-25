package duke;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.stream.IntStream;

/** Processes Hermes commands and owns the application's task list. */
public class HermesMini {

    private static final String INDENT = "  ";

    private final Storage storage;
    private final Parser parser;
    private final TaskList taskList;
    private final String loadingMessage;
    private boolean shouldExit;

    /** Creates Hermes and loads saved tasks from the default data file. */
    public HermesMini() {
        this(new Storage(Task.getDataFile()), new Parser());
    }

    /** Creates Hermes with replaceable collaborators for automated testing. */
    HermesMini(Storage storage, Parser parser) {
        this.storage = storage;
        this.parser = parser;

        TaskList loadedTasks;
        String loadStatus = "";
        try {
            loadedTasks = new TaskList(storage.load());
        } catch (IOException | DateTimeParseException exception) {
            loadedTasks = new TaskList();
            loadStatus = "\nI couldn't load the saved tasks, so I started with an empty list.";
        }
        taskList = loadedTasks;
        loadingMessage = loadStatus;
    }

    /** Runs the command-line interface using the same response engine as the GUI. */
    public static void main(String[] args) {
        HermesMini hermes = new HermesMini();
        System.out.println(hermes.getGreeting());
        try (Scanner scanner = new Scanner(System.in)) {
            while (scanner.hasNextLine() && !hermes.shouldExit()) {
                System.out.println(hermes.getResponse(scanner.nextLine()));
            }
        }
    }

    /** Returns the greeting displayed when the application starts. */
    public String getGreeting() {
        return "Greetings! I'm Hermes, your task messenger.\n"
                + "What shall I carry for you today?" + loadingMessage;
    }

    /** Processes one user command and returns the response to display. */
    public String getResponse(String input) {
        String command = parser.normalizeCommand(input);
        if (command.isEmpty()) {
            return error("Please enter a command.");
        } else if (command.equals("bye")) {
            shouldExit = true;
            return "Farewell! Hermes will be ready for your next message.";
        } else if (command.equals("list")) {
            return formatTaskList(taskList.getTasks(), "Here are the tasks in your list:");
        } else if (command.startsWith("mark ")) {
            return markTask(command.substring(5), true);
        } else if (command.startsWith("unmark ")) {
            return markTask(command.substring(7), false);
        } else if (command.startsWith("delete ")) {
            return deleteTask(command.substring(7));
        } else if (command.startsWith("find ")) {
            return findTasks(command.substring(5).trim());
        } else if (command.equals("todo") || command.startsWith("todo ")) {
            String description = command.length() == 4 ? "" : command.substring(5).trim();
            return description.isEmpty() ? error("A todo needs a description.") : addTask(new Todo(description));
        } else if (command.equals("deadline")) {
            return error("A deadline needs a description and a /by date or time.");
        } else if (command.startsWith("deadline ")) {
            return addDeadline(command.substring(9));
        } else if (command.equals("event")) {
            return error("An event needs a description, /from time, and /to time.");
        } else if (command.startsWith("event ")) {
            return addEvent(command.substring(6));
        } else if (command.equals("remind")) {
            return error("A reminder needs a description and an /at date or time.");
        } else if (command.startsWith("remind ")) {
            return addReminder(command.substring(7));
        }
        return error("I don't recognise that command. Try todo, deadline, event, remind, list, find, mark, "
                + "unmark, or delete.");
    }

    /** Returns whether the most recent command requested application exit. */
    public boolean shouldExit() {
        return shouldExit;
    }

    private String findTasks(String keyword) {
        if (keyword.isEmpty()) {
            return error("Please provide a keyword to search for.");
        }
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        List<Task> matches = IntStream.range(0, taskList.size())
                .mapToObj(index -> taskList.get(index + 1))
                .filter(task -> task.getDescription().toLowerCase(Locale.ROOT).contains(normalizedKeyword))
                .toList();
        return formatTaskList(matches, "Here are the matching tasks:");
    }

    private String markTask(String numberText, boolean done) {
        try {
            int number = Integer.parseInt(numberText.trim());
            if (!taskList.contains(number)) {
                return error("That task number is not in your list.");
            }
            Task task = taskList.get(number);
            if (done) {
                task.markAsDone();
            } else {
                task.markAsNotDone();
            }
            String saveError = saveTasks();
            if (saveError != null) {
                return saveError;
            }
            return done
                    ? "Dispatch complete! This task is now done:\n" + INDENT + task.getDisplayText()
                    : "Message received. This task is active again:\n" + INDENT + task.getDisplayText();
        } catch (NumberFormatException exception) {
            return error("Please provide a valid task number.");
        }
    }

    private String deleteTask(String numberText) {
        try {
            int number = Integer.parseInt(numberText.trim());
            if (!taskList.contains(number)) {
                return error("That task number is not in your list.");
            }
            Task task = taskList.remove(number);
            String saveError = saveTasks();
            if (saveError != null) {
                return saveError;
            }
            return "Message withdrawn. I've removed this task:\n" + INDENT + formatTask(task)
                    + "\nNow you have " + taskList.size() + " tasks in the list.";
        } catch (NumberFormatException exception) {
            return error("Please provide a valid task number.");
        }
    }

    private String addDeadline(String arguments) {
        String[] parts = arguments.split(" /by ", 2);
        if (parts.length < 2 || parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) {
            return error("A deadline needs a description and a /by date or time.");
        }
        try {
            return addTask(new Deadline(parts[0].trim(), parser.parseDate(parts[1].trim())));
        } catch (DateTimeParseException exception) {
            return invalidDateError();
        }
    }

    private String addEvent(String arguments) {
        String[] parts = arguments.split(" /from ", 2);
        if (parts.length < 2) {
            return error("An event needs a description, /from time, and /to time.");
        }
        String[] times = parts[1].split(" /to ", 2);
        if (times.length < 2 || parts[0].trim().isEmpty()
                || times[0].trim().isEmpty() || times[1].trim().isEmpty()) {
            return error("An event needs a description, /from time, and /to time.");
        }
        try {
            LocalDateTime from = parser.parseDate(times[0].trim());
            LocalDateTime to = parser.parseDate(times[1].trim());
            if (!from.isBefore(to)) {
                return error("An event must start before it ends.");
            }
            return addTask(new Event(parts[0].trim(), from, to));
        } catch (DateTimeParseException exception) {
            return invalidDateError();
        }
    }

    private String addReminder(String arguments) {
        String[] parts = arguments.split(" /at ", 2);
        if (parts.length < 2 || parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) {
            return error("A reminder needs a description and an /at date or time.");
        }
        try {
            return addTask(new Reminder(parts[0].trim(), parser.parseDate(parts[1].trim())));
        } catch (DateTimeParseException exception) {
            return invalidDateError();
        }
    }

    private String addTask(Task task) {
        if (taskList.containsEquivalent(task)) {
            return error("That task is already in your list.");
        } else if (!taskList.add(task)) {
            return error("Your task list is full.");
        }
        String saveError = saveTasks();
        return saveError == null
                ? "Message delivered! I've added this task:\n" + INDENT + formatTask(task)
                        + "\nNow you have " + taskList.size() + " tasks in the list."
                : saveError;
    }

    private String saveTasks() {
        try {
            storage.save(taskList.getTasks());
            return null;
        } catch (IOException exception) {
            return error("I couldn't save the tasks.");
        }
    }

    private String formatTaskList(List<Task> tasks, String heading) {
        StringBuilder response = new StringBuilder(heading);
        for (int index = 0; index < tasks.size(); index++) {
            response.append('\n').append(index + 1).append('.').append(formatTask(tasks.get(index)));
        }
        return response.toString();
    }

    private String formatTask(Task task) {
        return "[" + task.getTypeIcon() + "][" + task.getStatusIcon() + "] " + task.getDisplayText();
    }

    private String error(String message) {
        return "DELIVERY FAILED: " + message;
    }

    private String invalidDateError() {
        return error("Use dates and times like 2019-12-02 18:00.");
    }
}
