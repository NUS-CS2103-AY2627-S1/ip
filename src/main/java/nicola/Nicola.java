package nicola;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Runs the chatbot Nicola and processes commands entered by the user.
 */
public class Nicola {
    private static final Ui ui = new Ui();
    private static final Storage storage = new Storage("data/nicola.txt");
    private static final Parser parser = new Parser();
    private static final DateTimeFormatter INPUT_DATE =
            DateTimeFormatter.ofPattern("uuuu-MM-dd");
    private static final DateTimeFormatter INPUT_DATE_TIME =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm");
    private String commandType;
    /**
     * Task list used by the JavaFX interface.
     */
    private final TaskList tasks = new TaskList(storage.loadTasks());

    public static void main(String[] args) {
        ui.showWelcome();

        TaskList tasks = new TaskList(storage.loadTasks());

        while (ui.hasNextCommand()) {
            String input = ui.readCommand();
            String command = parser.getCommandWord(input);
            String details = parser.getDetails(input);

            if (command.equals("bye")) {
                break;
            }

            executeCommand(command, details, tasks);

            ui.showLine();
            storage.saveTasks(tasks.getTasks());
        }

        ui.showLine();
        ui.showGoodbye();
    }

    /**
     * Prints a validation error with a prefix used by the GUI for error styling.
     *
     * @param message explanation of the error
     */
    private static void showError(String message) {
        System.out.println("Error: " + message);
    }

    private static void listTasks(TaskList tasks) {
        System.out.println("Here are the matters currently on our agenda:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println(" " + (i + 1) + "." + tasks.get(i).formatForList());
        }
    }

    private static void addTodo(TaskList tasks, String description) {
        if (tasks.isFull()) {
            showError("Your task list is full.");
            return;
        }

        tasks.add(new TodoTask(description.trim()));
        System.out.println("Consider it handled. I've added this task to our plans.");
        System.out.println("  [T][ ] " + description.trim());
        System.out.println("We now have " + tasks.size() + " matters requiring our attention.");
    }

    private static void addDeadline(TaskList tasks, String payload) {
        if (tasks.isFull()) {
            showError("Your task list is full.");
            return;
        }

        String[] parts = payload.split(" /by ", 2);
        if (parts.length < 2 || parts[0].isBlank() || parts[1].isBlank()) {
            showError("The deadline needs a description and /by date.");
            return;
        }

        String description = parts[0].trim();
        String dateText = parts[1].trim();

        LocalDateTime dateTime = parseDateTime(dateText);
        LocalDate date = null;

        if (dateTime == null) {
            date = parseDate(dateText);
        }

        if (dateTime == null && date == null) {
            showError("Please use a valid date like yyyy-MM-dd or yyyy-MM-dd HHmm.");
            return;
        }

        tasks.add(new DeadlineTask(description, date, dateTime, dateText));
        System.out.println("A deadline, understood. I'll make certain we don't lose sight of it.");
        System.out.println("  [D][ ] " + tasks.get(tasks.size() - 1).getDisplayText());
        System.out.println("We now have " + tasks.size() + " matters requiring our attention.");

    }


    private static void addEvent(TaskList tasks, String payload) {
        if (tasks.isFull()) {
            showError("Your task list is full.");
            return;
        }

        String[] parts = payload.split(" /from ", 2);
        if (parts.length < 2 || parts[0].isBlank()) {
            showError("The event needs a description, /from time, and /to time.");
            return;
        }

        String description = parts[0].trim();
        String[] times = parts[1].split(" /to ", 2);
        if (times.length < 2 || times[0].isBlank() || times[1].isBlank()) {
            showError("The event needs a description, /from time, and /to time.");
            return;
        }

        LocalDateTime from = parseDateTime(times[0].trim());
        LocalDateTime to = parseDateTime(times[1].trim());

        if (from == null || to == null) {
            showError("Please use valid date-time values like yyyy-MM-dd HHmm.");
            return;
        }

        tasks.add(new EventTask(description, from, to));
        System.out.println("I've reserved a place for this event in our schedule.");
        System.out.println("  [E][ ] " + tasks.get(tasks.size() - 1).getDisplayText());
        System.out.println("We now have " + tasks.size() + " matters requiring our attention.");
    }

    private static void markTask(TaskList tasks, String text, boolean done) {
        try {
            int index = Integer.parseInt(text.trim()) - 1;
            if (index < 0 || index >= tasks.size()) {
                showError("The number has yet to be assigned a task.");
                return;
            }

            tasks.get(index).setDone(done);
            if (done) {
                System.out.println("Excellent. Another matter settled exactly as planned.");
            } else {
                System.out.println("I see, we acted too soon. I've returned it to our agenda.");
            }
            System.out.println("  " + tasks.get(index).formatForList());
        } catch (NumberFormatException e) {
            showError("That number doesn't correspond to any matter on our agenda.");
        }
    }

    private static void deleteTask(TaskList tasks, String text) {
        try {
            int index = Integer.parseInt(text.trim()) - 1;
            if (index < 0 || index >= tasks.size()) {
                showError("The number has yet to be assigned a task.");
                return;
            }

            Task removed = tasks.remove(index);
            System.out.println("It's gone. You needn't concern yourself with it again.");
            System.out.println("  " + removed.formatForList());
            System.out.println("We now have " + tasks.size() + " matters requiring our attention.");
        } catch (NumberFormatException e) {
            showError("That number doesn't correspond to any matter on our agenda.");
        }
    }

    private static LocalDate parseDate(String text) {
        try {
            return LocalDate.parse(text, INPUT_DATE);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private static LocalDateTime parseDateTime(String text) {
        try {
            return LocalDateTime.parse(text, INPUT_DATE_TIME);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private static void findTasks(TaskList tasks, String keyword) {
        String normalizedKeyword = keyword.trim().toLowerCase();

        List<Task> matchingTasks = tasks.getTasks().stream()
                .filter(task -> task.getDescription()
                        .toLowerCase()
                        .contains(normalizedKeyword))
                .toList();

        if(matchingTasks.size() == 0){
            System.out.println("How curious. I found nothing matching that description.");
            return;
        }

        System.out.println("These are the matters matching your inquiry:");

        for (int i = 0; i < matchingTasks.size(); i++) {
            System.out.println(" " + (i + 1) + "."
                    + matchingTasks.get(i).formatForList());
        }
    }

    /**
     * Generates a response for a command entered through the GUI.
     *
     * @param input command entered by the user
     * @return Nicola's response
     */
    public String getResponse(String input) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;

        try (PrintStream capturedOutput =
                     new PrintStream(output, true, StandardCharsets.UTF_8)) {

            // Existing command methods print to System.out, so temporarily
            // collect that text for the JavaFX dialog box.
            System.setOut(capturedOutput);
            processCommand(input);

        } finally {
            // Always restore normal terminal output.
            System.setOut(originalOutput);
        }

        storage.saveTasks(tasks.getTasks());

        return output.toString(StandardCharsets.UTF_8).strip();
    }

    /**
     * Returns the command word most recently processed by the GUI.
     *
     * @return command word such as todo, mark, or delete
     */
    public String getCommandType() {
        return commandType;
    }

    /**
     * Identifies and executes a command entered through the GUI.
     *
     * @param input complete command entered by the user
     */
    private void processCommand(String input) {
        String command = parser.getCommandWord(input);
        String details = parser.getDetails(input);

        commandType = command;

        if (command.equals("bye")) {
            System.out.println("Arrivederci. I'll miss you.");
            return;
        }

        executeCommand(command, details, tasks);
    }

    /**
     * Executes a parsed command using the given task list.
     *
     * @param command command word to execute
     * @param details details provided after the command word
     * @param tasks task list affected by the command
     */
    private static void executeCommand(
            String command,
            String details,
            TaskList tasks
    ) {
        switch (command) {
            case "list":
                listTasks(tasks);
                break;

            case "todo":
                if (details.isBlank()) {
                    showError("The todo cannot be empty.");
                } else {
                    addTodo(tasks, details);
                }
                break;

            case "deadline":
                if (details.isBlank()) {
                    showError("The deadline cannot be empty.");
                } else {
                    addDeadline(tasks, details);
                }
                break;

            case "event":
                if (details.isBlank()) {
                    showError("The event cannot be empty.");
                } else {
                    addEvent(tasks, details);
                }
                break;

            case "mark":
                if (details.isBlank()) {
                    showError("I need a task number before I can take care of that.");
                } else {
                    markTask(tasks, details, true);
                }
                break;

            case "unmark":
                if (details.isBlank()) {
                    showError("I need a task number before I can take care of that.");
                } else {
                    markTask(tasks, details, false);
                }
                break;

            case "delete":
                if (details.isBlank()) {
                    showError("I need a task number before I can take care of that.");
                } else {
                    deleteTask(tasks, details);
                }
                break;

            case "find":
                findTasks(tasks, details);
                break;

            case "reminders":
                showReminders(tasks);
                break;

            default:
                showError("I'm afraid I don't recognize that instruction.\n" +
                        "Try list, todo, deadline, event, mark, unmark, delete, find, reminders");
                break;
        }
    }

    /**
     * Displays incomplete deadlines due within the next seven days.
     *
     * @param tasks the user's task list
     */
    private static void showReminders(TaskList tasks) {
        LocalDate today = LocalDate.now();
        int reminderPeriod = 7;
        boolean hasReminder = false;

        System.out.println("A word of caution, these deadlines are approaching:");

        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);

            if (task instanceof DeadlineTask deadline
                    && deadline.isDueWithin(today, reminderPeriod)) {
                System.out.println(" " + (i + 1) + "." + task.formatForList());
                hasReminder = true;
            }
        }

        if (!hasReminder) {
            System.out.println("Everything is under control. No deadlines are approaching this week.");
        }
    }

}


