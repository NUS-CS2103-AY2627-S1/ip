import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Locale;

/**
 * A command-line chatbot that stores ToDos, deadlines, and events.
 */
public class Bibi {
    /**
     * Where the task list is kept, relative to the project root. Building the
     * path from separate names keeps it correct on any operating system.
     */
    private static final Path SAVE_FILE_PATH = Paths.get("data", "bibi.txt");

    /**
     * Starts Bibi and processes commands until the user enters {@code bye}.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        Storage storage = new Storage(SAVE_FILE_PATH);

        ui.showWelcome();
        TaskList tasks = loadTasks(storage, ui);
        try {
            while (true) {
                String input = ui.readCommand();

                if (input.equalsIgnoreCase("bye")) {
                    ui.showGoodbye();
                    break;
                }

                try {
                    processCommand(input, tasks, storage, ui);
                } catch (BibiException exception) {
                    ui.showError(exception.getMessage());
                }
            }
        } finally {
            ui.close();
        }
    }

    /**
     * Reads the tasks saved by an earlier session and reports what was restored.
     *
     * @param storage the save file to read from
     * @param ui the interface used to report what happened
     * @return the restored task list, or an empty list when nothing was saved yet
     */
    private static TaskList loadTasks(Storage storage, Ui ui) {
        try {
            Storage.LoadReport report = storage.load();
            TaskList tasks = new TaskList(report.tasks());

            if (!tasks.isEmpty()) {
                ui.showLoaded(tasks.size());
            }
            if (!report.warnings().isEmpty()) {
                ui.showLoadWarnings(report.warnings());
            }
            return tasks;
        } catch (IOException exception) {
            // Reading failed outright, so continue with an empty list rather than
            // refusing to start. Saving later replaces the unreadable file.
            ui.showLoadingError(storage.getFilePath(), exception);
            return new TaskList();
        }
    }

    /**
     * Routes one user command to the appropriate task operation.
     *
     * @param input the user's complete command
     * @param tasks the task list to update or display
     * @param storage the save file kept in step with the task list
     * @param ui the interface used to show the result
     * @throws BibiException if the command is empty, unknown, or malformed
     */
    private static void processCommand(String input, TaskList tasks, Storage storage, Ui ui)
            throws BibiException {
        String command = input.toLowerCase(Locale.ROOT); //Local.root uses user's local settings to determine case conversion

        if (input.isEmpty()) {
            throw new BibiException("Please enter a command.");
        } else if (command.equals("list")) {
            printTasks(tasks, ui);
        } else if (command.equals("todo") || command.startsWith("todo ")) {
            addTodo(input.substring(4).trim(), tasks, storage, ui);
        } else if (command.equals("deadline") || command.startsWith("deadline ")) {
            addDeadline(input.substring(8).trim(), tasks, storage, ui);
        } else if (command.equals("event") || command.startsWith("event ")) {
            addEvent(input.substring(5).trim(), tasks, storage, ui);
        } else if (command.startsWith("mark ")) {
            markTask(input.substring(5).trim(), tasks, storage, ui);
        } else if (command.startsWith("unmark ")) {
            unmarkTask(input.substring(7).trim(), tasks, storage, ui);
        } else if (command.startsWith("remove ")) {
            removeTask(input.substring(7).trim(), tasks, storage, ui);
        } else if (command.equals("on") || command.startsWith("on ")) {
            printTasksOn(input.substring(2).trim(), tasks, ui);
        } else if (command.startsWith("help ")) {
            printHelp(ui);
        } else {
            throw new BibiException("I don't understand that command. "
                    + "Try todo, deadline, event, list, on, mark, unmark, or bye.");
        }
    }

    /**
     * Prints the list of commands, surrounded by divider lines.
     *
     * @param ui the interface used to show the help
     */
    private static void printHelp(Ui ui) {
        ui.showLine();
        ui.showHelp();
        ui.showLine();
    }

    /**
     * Creates a ToDo from its description.
     *
     * @param description the ToDo description
     * @param tasks the task list to update
     * @param storage the save file to update afterwards
     * @param ui the interface used to confirm the addition
     * @throws BibiException if the description is missing
     */
    private static void addTodo(String description, TaskList tasks, Storage storage, Ui ui)
            throws BibiException {
        addTask(new Todo(description), tasks, storage, ui);
    }

    /**
     * Creates a deadline from text in the form {@code description /by time}.
     *
     * @param deadlineText the deadline text after the {@code deadline} command
     * @param tasks the task list to update
     * @param storage the save file to update afterwards
     * @param ui the interface used to confirm the addition
     * @throws BibiException if the deadline is missing required information
     */
    private static void addDeadline(String deadlineText, TaskList tasks, Storage storage, Ui ui)
            throws BibiException {
        String normalizedText = deadlineText.toLowerCase(Locale.ROOT);
        int byIndex = normalizedText.indexOf(" /by ");

        if (byIndex < 0) {
            throw new BibiException("Use deadline <description> /by <time>.");
        }

        String description = deadlineText.substring(0, byIndex).trim();
        String by = deadlineText.substring(byIndex + 5).trim();
        addTask(new Deadline(description, by), tasks, storage, ui);
    }

    /**
     * Creates an event from text in the form
     * {@code description /from start time /to end time}.
     *
     * @param eventText the event text after the {@code event} command
     * @param tasks the task list to update
     * @param storage the save file to update afterwards
     * @param ui the interface used to confirm the addition
     * @throws BibiException if the event is missing required information
     */
    private static void addEvent(String eventText, TaskList tasks, Storage storage, Ui ui)
            throws BibiException {
        String normalizedText = eventText.toLowerCase(Locale.ROOT);
        int fromIndex = normalizedText.indexOf(" /from ");
        int toIndex = normalizedText.indexOf(" /to ");

        if (fromIndex < 0 || toIndex < fromIndex + 7) {
            throw new BibiException("Use event <description> /from <start> /to <end>.");
        }

        String description = eventText.substring(0, fromIndex).trim();
        String from = eventText.substring(fromIndex + 7, toIndex).trim();
        String to = eventText.substring(toIndex + 5).trim();
        addTask(new Event(description, from, to), tasks, storage, ui);
    }

    /**
     * Stores a newly created task, confirms the addition, and saves the list.
     *
     * @param task the new task
     * @param tasks the task list to update
     * @param storage the save file to update afterwards
     * @param ui the interface used to confirm the addition
     */
    private static void addTask(Task task, TaskList tasks, Storage storage, Ui ui) {
        tasks.add(task);
        ui.showLine();
        ui.showMessage("Got it. I've added this task:");
        ui.showDetail(task.toString());
        ui.showPlain("Now you have " + tasks.size() + " tasks in the list.");
        ui.showLine();
        saveTasks(tasks, storage, ui);
    }

    /**
     * Prints every task with its one-based task number and completion status.
     *
     * @param tasks tasks to print
     * @param ui the interface used to show the list
     */
    private static void printTasks(TaskList tasks, Ui ui) {
        ui.showLine();
        if (tasks.isEmpty()) {
            ui.showMessage("Your task list is empty.");
        } else {
            ui.showMessage("Here are the tasks in your list:");
            int taskNumber = 1;
            for (Task task : tasks.getTasks()) {
                ui.showNumberedTask(taskNumber, task);
                taskNumber++;
            }
        }
        ui.showLine();
    }

    /**
     * Prints the deadlines and events that fall on one particular date.
     *
     * <p>Each match keeps the number it has in the full list, so a task found
     * this way can be marked or removed straight away.
     *
     * @param dateText the date supplied after {@code on}
     * @param tasks the current task list
     * @param ui the interface used to show the matches
     * @throws BibiException if the date cannot be understood
     */
    private static void printTasksOn(String dateText, TaskList tasks, Ui ui) throws BibiException {
        // Any time of day in the query is ignored, since the question is which
        // tasks belong to the day as a whole.
        LocalDate queryDate = TaskDateTime.parse(dateText).getDate();
        String shownDate = TaskDateTime.formatDate(queryDate);

        ui.showLine();
        boolean hasMatch = false;
        int taskNumber = 1;
        for (Task task : tasks.getTasks()) {
            if (task.occursOn(queryDate)) {
                if (!hasMatch) {
                    ui.showMessage("Here is what you have on " + shownDate + ":");
                    hasMatch = true;
                }
                ui.showNumberedTask(taskNumber, task);
            }
            taskNumber++;
        }
        if (!hasMatch) {
            ui.showMessage("You have nothing on " + shownDate + ".");
        }
        ui.showLine();
    }

    /**
     * Marks the requested one-based task number complete when it exists.
     *
     * @param numberText the task number supplied after {@code mark}
     * @param tasks the current task list
     * @param storage the save file to update afterwards
     * @param ui the interface used to confirm the change
     * @throws BibiException if the supplied task number is invalid
     */
    private static void markTask(String numberText, TaskList tasks, Storage storage, Ui ui)
            throws BibiException {
        Task task = findTask(numberText, tasks, "mark");
        task.markComplete();
        ui.showMessage("Marked task " + numberText + " as complete.");
        saveTasks(tasks, storage, ui);
    }

    /**
     * Marks the requested one-based task number incomplete when it exists.
     *
     * @param numberText the task number supplied after {@code unmark}
     * @param tasks the current task list
     * @param storage the save file to update afterwards
     * @param ui the interface used to confirm the change
     * @throws BibiException if the supplied task number is invalid
     */
    private static void unmarkTask(String numberText, TaskList tasks, Storage storage, Ui ui)
            throws BibiException {
        Task task = findTask(numberText, tasks, "unmark");
        task.markIncomplete();
        ui.showMessage("Unmarked task " + numberText + ", now incomplete.");
        saveTasks(tasks, storage, ui);
    }

    /**
     * Finds a task using a one-based task number and reports invalid input.
     *
     * @param numberText the requested task number
     * @param tasks the current task list
     * @param command the command used for the error message
     * @return the matching task
     * @throws BibiException if the task number is not a valid list entry
     */
    private static Task findTask(String numberText, TaskList tasks, String command)
            throws BibiException {
        try {
            return tasks.get(Integer.parseInt(numberText));
        } catch (NumberFormatException exception) {
            throw new BibiException("Use " + command + " followed by a task number, for example: "
                    + command + " 2");
        }
    }

    /**
     * Removes the requested one-based task number and saves the shortened list.
     *
     * @param index the task number supplied after {@code remove}
     * @param tasks the current task list
     * @param storage the save file to update afterwards
     * @param ui the interface used to confirm the removal
     * @throws BibiException if the supplied task number is invalid
     */
    private static void removeTask(String index, TaskList tasks, Storage storage, Ui ui)
            throws BibiException {
        try {
            tasks.remove(Integer.parseInt(index));
            ui.showMessage("Task " + index + " removed.");
            saveTasks(tasks, storage, ui);
        } catch (NumberFormatException exception) {
            throw new BibiException("Use remove followed by a task number, for example: remove 2");
        }
    }

    /**
     * Writes the current task list to the hard disk after every change to it.
     *
     * <p>A failed save is reported but does not stop Bibi, so the user can carry
     * on working with the tasks held in memory for the rest of the session.
     *
     * @param tasks the task list to store
     * @param storage the save file to write to
     * @param ui the interface used to report a failure
     */
    private static void saveTasks(TaskList tasks, Storage storage, Ui ui) {
        try {
            storage.save(tasks.getTasks());
        } catch (IOException exception) {
            ui.showSaveError(storage.getFilePath(), exception);
        }
    }
}
