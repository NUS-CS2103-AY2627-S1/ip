package victoria;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

import victoria.command.Command;
import victoria.command.ExitCommand;
import victoria.exception.EmptyDescriptionException;
import victoria.exception.InvalidCommandException;
import victoria.exception.InvalidDeadlineException;
import victoria.exception.InvalidEventException;
import victoria.exception.TaskListFullException;
import victoria.exception.VictoriaException;
import victoria.model.Deadline;
import victoria.model.Event;
import victoria.model.Task;
import victoria.model.TaskList;
import victoria.model.Todo;
import victoria.storage.TaskFile;
import victoria.ui.Ui;

/**
 * A simple command-line chatbot that stores and lists tasks until the user says bye.
 */
public class Victoria {
    private static final int MAX_TASKS = 100;
    private final TaskList tasks;
    private final Ui ui;
    private final TaskFile.LoadResult loadResult;

    /** Creates Victoria and restores any saved tasks. */
    public Victoria() {
        tasks = new TaskList(MAX_TASKS);
        ui = new Ui();
        loadResult = TaskFile.loadInto(tasks);
    }

    /** Starts Victoria, restores saved tasks, and begins reading commands. */
    public static void main(String[] args) {
        Victoria victoria = new Victoria();

        if (args.length == 0) {
            victoria.ui.showWelcome();
        }

        Scanner scanner = new Scanner(System.in);
        if (args.length == 0) {
            victoria.ui.showLoadResult(victoria.loadResult, victoria.tasks);
        }
        victoria.runCommandLoop(scanner);
    }

    /** Returns the outcome of restoring tasks during construction. */
    public TaskFile.LoadResult getLoadResult() {
        return loadResult;
    }

    /** Returns the startup message that reports the locally restored tasks. */
    public synchronized String getLoadResultMessage() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;
        try (PrintStream capturedOutput = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setOut(capturedOutput);
            ui.showLoadResult(loadResult, tasks);
        } finally {
            System.setOut(originalOutput);
        }
        return output.toString(StandardCharsets.UTF_8);
    }

    /** Executes one command and returns the text that should be shown to the user. */
    public synchronized CommandResult executeCommand(String command) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;
        boolean shouldExit = false;
        try (PrintStream capturedOutput = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setOut(capturedOutput);
            shouldExit = executeCommandInternal(command);
        } finally {
            System.setOut(originalOutput);
        }
        return new CommandResult(output.toString(StandardCharsets.UTF_8), shouldExit);
    }

    /**
     * Reads and echoes commands until the user enters the exit command "bye".
     *
     * @param scanner  source of commands entered by the user
     * @param tasks    in-memory storage for the user's tasks
     * @param ui       console output helper
     */
    private void runCommandLoop(Scanner scanner) {

        while (scanner.hasNextLine()) {
            CommandResult result = executeCommand(scanner.nextLine());
            System.out.print(result.response());
            if (result.shouldExit()) {
                break;
            }
        }
    }

    /** Processes a command against the current task list. */
    private boolean executeCommandInternal(String command) {
        String normalizedCommand = command.trim();

        try {
            if (normalizedCommand.equals("bye")) {
                Command exitCommand = new ExitCommand();
                exitCommand.execute(tasks, ui);
                return true;
            }

            if (normalizedCommand.equals("list")) {
                tasks.printTasks();
            } else if (normalizedCommand.startsWith("list on ")) {
                printTasksOnDate(tasks, normalizedCommand.substring(8).trim());
            } else if (normalizedCommand.startsWith("find ")) {
                findTasks(tasks, normalizedCommand.substring(5).trim());
            } else if (normalizedCommand.equals("todo")) {
                throw new EmptyDescriptionException("I need a task description to add it to your list.");
            } else if (normalizedCommand.startsWith("todo ")) {
                addTask(tasks, normalizedCommand.substring(5));
            } else if (normalizedCommand.startsWith("deadline ")) {
                addDeadline(tasks, normalizedCommand.substring(9));
            } else if (normalizedCommand.startsWith("event ")) {
                addEvent(tasks, normalizedCommand.substring(5));
            } else if (isStatusCommand(normalizedCommand, "mark")) {
                changeTaskStatus(normalizedCommand, tasks, true);
            } else if (isStatusCommand(normalizedCommand, "unmark")) {
                changeTaskStatus(normalizedCommand, tasks, false);
            } else if (isStatusCommand(normalizedCommand, "delete")) {
                deleteTask(normalizedCommand, tasks);
            } else {
                throw new InvalidCommandException("I didn't catch that command. Try one from the command guide above.");
            }

            TaskFile.save(tasks);

        } catch (VictoriaException exception) {
            ui.showError(exception);
        }

        ui.printSeparator();
        return false;
    }

    /** Contains the reply to one command and whether it ended the session. */
    public record CommandResult(String response, boolean shouldExit) { }

    /** Adds a task without date/time text. */
    private static void addTask(TaskList tasks, String description) {
        addParsedTask(tasks, new Todo(description));
    }

    /** Parses a date query and prints deadlines/events occurring on that date. */
    private static void printTasksOnDate(TaskList tasks, String dateText) {
        try {
            tasks.printTasksOn(LocalDate.parse(dateText, DateTimeFormatter.ofPattern("uuuu-MM-dd")));
        } catch (DateTimeParseException exception) {
            throw new InvalidCommandException("Please use the date format yyyy-MM-dd, for example 2026-09-12.");
        }
    }

    /** Validates a search keyword and prints tasks whose descriptions contain it. */
    private static void findTasks(TaskList tasks, String keyword) {
        if (keyword.isBlank()) {
            throw new InvalidCommandException("Give me a word or phrase to search for.");
        }
        tasks.printTasksContaining(keyword);
    }

    /** Parses the slash-delimited date portion of a deadline command. */
    private static void addDeadline(TaskList tasks, String command) {
        int markerIndex = command.indexOf("/by ");

        if (markerIndex < 0) {
            throw new InvalidDeadlineException("Add /by <date> so I know when this deadline is due.");
        }
        String description = command.substring(0, markerIndex).trim();
        if (description.isBlank()) {
            throw new EmptyDescriptionException("I need a task description to add it to your list.");
        }
        String dateTime = command.substring(markerIndex + "/by ".length()).trim();
        if (dateTime.isBlank()) {
            throw new InvalidDeadlineException("Add a date after /by so I can save the deadline.");
        }
        addParsedTask(tasks, new Deadline(description, dateTime));
    }

    /** Parses the slash-delimited start and end portions of an event command. */
    private static void addEvent(TaskList tasks, String command) {
        int markerIndex = command.indexOf("/from ");

        if (markerIndex < 0) {
            throw new InvalidEventException("Add /from <start date> so I know when the event begins.");
        }
        String description = command.substring(0, markerIndex).trim();
        if (description.isBlank()) {
            throw new EmptyDescriptionException("I need a task description to add it to your list.");
        }
        String dateTime = command.substring(markerIndex + "/from ".length()).trim();
        if (dateTime.isBlank()) {
            throw new InvalidEventException("Add a start date after /from to save the event.");
        }
        int toIndex = dateTime.indexOf("/to ");
        if (toIndex < 0) {
            throw new InvalidEventException("Add /to <end date> so I know when the event finishes.");
        }
        if (dateTime.substring(0, toIndex).trim().isBlank()) {
            throw new InvalidEventException("Add a start date after /from to save the event.");
        }
        if (dateTime.substring(toIndex + 4).trim().isBlank()) {
            throw new InvalidEventException("Add an end date after /to to save the event.");
        }
        addParsedTask(tasks, new Event(description, dateTime.substring(0, toIndex).trim(),
                dateTime.substring(toIndex + 4).trim()));
    }

    /** Adds a parsed task and reports the result. */
    private static void addParsedTask(TaskList tasks, Task task) {

        if (task.getDescription() == null || task.getDescription().isBlank()) {
            throw new EmptyDescriptionException("I need a task description to add it to your list.");
        }
        if (tasks.add(task)) {
            System.out.println(" Nice! I've added this to your list:");
            System.out.println("   " + task);
            String taskCountLabel = tasks.size() == 1 ? "task" : "tasks";
            System.out.println(" Your list now has " + tasks.size() + " " + taskCountLabel + ". You're on a roll!");
        } else {
            throw new TaskListFullException("Your task list is full—time to celebrate some completed tasks!");
        }
    }

    /** Returns true only when the command has a status keyword followed by a number. */
    private static boolean isStatusCommand(String command, String commandName) {

        return command.matches("^" + commandName + "\\s+\\d+$");
    }

    /** Parses a validated status command and delegates the state change to the task list. */
    private static void changeTaskStatus(String command, TaskList tasks, boolean done) {

        String commandName = done ? "mark " : "unmark ";
        int taskNumber = Integer.parseInt(command.substring(commandName.length()).trim());
        Task task = tasks.getTask(taskNumber);
        if (task == null) {
            System.out.println(" Whoops! I can't find a task with that number.");
        } else if (done && task.isDone()) {
            System.out.println(" Hooray! You already finished this one:");
            System.out.println("   " + task);
            System.out.println("Pick another task whenever you're ready!");
        } else if (!done && !task.isDone()) {
            System.out.println(" This task is already waiting for you:");
            System.out.println("   " + task);
            System.out.println("You've got this!");
        } else if (done) {
            tasks.markDone(taskNumber);
            System.out.println(" Amazing! I've marked this task as done:");
            System.out.println("   " + task);
        } else {
            tasks.markNotDone(taskNumber);
            System.out.println(" No problem—I've marked this task as not done:");
            System.out.println("   " + task);
            System.out.println("You've got this!");
        }
    }

    /** Deletes a task selected by its one-based number and reports the new list size. */
    private static void deleteTask(String command, TaskList tasks) {
        int taskNumber = Integer.parseInt(command.substring("delete ".length()).trim());
        Task deletedTask = tasks.delete(taskNumber);
        if (deletedTask == null) {
            System.out.println(" Whoops! I can't find a task with that number.");
            return;
        }
        System.out.println(" Done! I've removed this task:");
        System.out.println("   " + deletedTask);
        String taskCountLabel = tasks.size() == 1 ? "task" : "tasks";
        System.out.println(" You now have " + tasks.size() + " " + taskCountLabel + " left. Keep it up!");
    }

}
