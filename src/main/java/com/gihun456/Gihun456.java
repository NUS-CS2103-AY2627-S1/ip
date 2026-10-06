package com.gihun456;

import java.time.Clock;
import java.util.List;
import java.util.Scanner;

import com.gihun456.command.Operation;
import com.gihun456.command.Parser;
import com.gihun456.model.Task;
import com.gihun456.model.TaskList;
import com.gihun456.model.Todo;
import com.gihun456.reminder.ReminderService;
import com.gihun456.storage.Storage;
import com.gihun456.ui.Ui;
import com.gihun456.ui.UiMessages;

/**
 * Entry point for the Gihun456 task application.
 */
public class Gihun456 {
    private static final String DEFAULT_DATA_FILE = "data/Gihun456.txt";

    private final Ui ui;
    private final TaskList tasks;
    private final Parser parser;
    private final Storage storage;
    private final ReminderService reminderService;
    private PendingConfirmation pendingConfirmation;

    /**
     * Creates an application instance backed by the given data file.
     *
     * @param filePath Path to the persistent task storage file.
     */
    public Gihun456(String filePath) {
        this(filePath, Clock.systemDefaultZone());
    }

    /**
     * Creates an application instance with an injectable clock.
     *
     * @param filePath Path to the persistent task storage file.
     * @param clock Clock used for reminder evaluation.
     */
    public Gihun456(String filePath, Clock clock) {
        this.ui = new Ui();
        this.tasks = new TaskList();
        this.parser = new Parser();
        this.storage = new Storage(filePath);
        this.reminderService = new ReminderService(clock);
    }

    public Gihun456() {
        this(DEFAULT_DATA_FILE);
    }

    /**
     * Starts the application from the default data file.
     *
     * @param args Command-line arguments ignored by the program.
     */
    public static void main(String[] args) {
        Gihun456 app = new Gihun456(DEFAULT_DATA_FILE);
        app.run();
    }

    /**
     * Returns the current reminder report without an empty-report message.
     *
     * @return Formatted reminder report, or an empty string when no reminders apply.
     */
    public String getReminderReport() {
        return reminderService.formatReport(tasks.asList());
    }

    private void showStartupReminders() {
        String reminderReport = getReminderReport();
        if (!reminderReport.isEmpty()) {
            System.out.println(reminderReport);
            ui.showLine();
        }
    }

    /**
     * Runs the application's main command loop.
     */
    public void run() {
        ui.showGreeting();
        final Scanner scanner = new Scanner(System.in);

        try {
            loadTasks();
        } catch (GihunException ge) {
            ui.showError(ge.getMessage());
            return;
        }
        showStartupReminders();

        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();

            try {
                String response = processCommand(input);
                System.out.println(response);
                if (input.trim().equalsIgnoreCase("bye")) {
                    return;
                }
            } catch (GihunException ge) {
                ui.showError(ge.getMessage());
                ui.showLine();
            } catch (Exception e) {
                System.out.println(ErrorMessages.UNEXPECTED_ERROR);
                e.printStackTrace(System.err);
                System.out.println();
            }
        }
    }

    /**
     * Loads the persisted tasks into memory.
     *
     * @throws GihunException If the task data cannot be read.
     */
    public void loadTasks() throws GihunException {
        if (tasks.isEmpty()) {
            tasks.addAll(storage.load());
        }
    }

    /**
     * Executes a command and returns the text that should be shown to the user.
     * This method is shared by the console and JavaFX interfaces.
     *
     * @param input Raw command entered by the user.
     * @return User-facing command response.
     * @throws GihunException If the command is invalid or cannot be completed.
     */
    public String processCommand(String input) throws GihunException {
        if (pendingConfirmation != null) {
            return processConfirmation(input);
        }
        return processCommand(parser.parse(input));
    }

    /**
     * Executes a previously parsed command.
     *
     * @param parsedInput Parsed command and its arguments.
     * @return User-facing command response.
     * @throws GihunException If the command is invalid or cannot be completed.
     */
    private String processCommand(Parser.ParsedInput parsedInput) throws GihunException {
        Operation operation = parsedInput.getOperation();
        String arguments = parsedInput.getArguments();

        switch (operation) {
            case TODO: {
                if (arguments.trim().isEmpty()) {
                    throw new GihunException(ErrorMessages.TODO_DESCRIPTION_EMPTY);
                }
                return addTask(new Todo(arguments));
            }
            case DEADLINE: {
                return addTaskConsideringConflicts(parser.parseDeadline(arguments));
            }
            case EVENT: {
                return addTaskConsideringConflicts(parser.parseEvent(arguments));
            }
            case REMINDERS:
                if (!arguments.trim().isEmpty()) {
                    throw new GihunException(ErrorMessages.REMINDERS_ARGUMENTS);
                }
                String reminderReport = getReminderReport();
                return reminderReport.isEmpty() ? UiMessages.NO_REMINDERS : reminderReport;
            case LIST:
                return tasks.isEmpty() ? UiMessages.EMPTY_STORAGE : formatTaskList(tasks.asList(), false);
            case FIND:
                if (tasks.isEmpty()) {
                    return UiMessages.EMPTY_STORAGE;
                }
                List<Task> matchingTasks = tasks.getMatchedTasks(arguments);
                return matchingTasks.isEmpty()
                        ? UiMessages.NO_MATCHING_TASKS
                        : formatTaskList(matchingTasks, true);
            case MARK: {
                int index = tasks.getValidIndex(arguments);
                Task task = tasks.get(index);
                tasks.markTask(index);
                saveTasks();
                return UiMessages.MARK_TASK + "\n" + task;
            }
            case UNMARK: {
                int index = tasks.getValidIndex(arguments);
                Task task = tasks.get(index);
                tasks.unmarkTask(index);
                saveTasks();
                return UiMessages.UNMARK_TASK + "\n" + task;
            }
            case DELETE: {
                if (tasks.isEmpty()) {
                    return UiMessages.EMPTY_STORAGE;
                }
                int index = tasks.getValidIndex(arguments);
                Task task = tasks.remove(index);
                saveTasks();
                return UiMessages.REMOVE_TASK + "\n" + task
                        + "\n" + UiMessages.getTaskCountMessage(tasks.size());
            }
            case BYE:
                return UiMessages.FAREWELL;
            default:
                throw new GihunException(ErrorMessages.UNSUPPORTED_OPERATION);
        }
    }

    /**
     * Generates a response for the JavaFX chat view, including validation errors.
     *
     * @param input Raw command entered by the user.
     * @return User-facing response.
     */
    public String getResponse(String input) {
        try {
            return processCommand(input);
        } catch (GihunException e) {
            return ErrorMessages.ERROR_PREFIX + e.getMessage();
        } catch (RuntimeException e) {
            return ErrorMessages.ERROR_PREFIX + ErrorMessages.UNEXPECTED_ERROR;
        }
    }

    /**
     * Adds a task, persists the updated list, and formats the confirmation response.
     *
     * @param task Task to add.
     * @return User-facing confirmation response.
     * @throws GihunException If the updated task list cannot be saved.
     */
    private String addTask(Task task) throws GihunException {
        tasks.add(task);
        saveTasks();
        return formatTaskAdded(task);
    }

    private String addTaskConsideringConflicts(Task task) throws GihunException {
        List<TaskList.Conflict> conflicts = tasks.getConflicts(task);
        if (conflicts.isEmpty()) {
            return addTask(task);
        }
        pendingConfirmation = new PendingConfirmation(task, conflicts);
        return formatConflictWarning(task, conflicts);
    }

    private String processConfirmation(String input) throws GihunException {
        String response = input == null ? "" : input.trim();
        if (response.equalsIgnoreCase("yes")) {
            Task task = pendingConfirmation.task();
            pendingConfirmation = null;
            return addTask(task);
        }
        if (response.equalsIgnoreCase("no")) {
            pendingConfirmation = null;
            return UiMessages.TASK_NOT_ADDED;
        }
        throw new GihunException(ErrorMessages.INVALID_CONFIRMATION);
    }

    private String formatConflictWarning(Task task, List<TaskList.Conflict> conflicts) {
        StringBuilder response = new StringBuilder();
        response.append(UiMessages.CONFLICT_WARNING)
                .append("\n")
                .append(UiMessages.PROPOSED_TASK)
                .append("\n")
                .append(task)
                .append("\n")
                .append(UiMessages.CONFLICTING_TASKS);
        for (TaskList.Conflict conflict : conflicts) {
            response.append("\n")
                    .append(conflict.taskNumber())
                    .append(". ")
                    .append(conflict.task());
        }
        return response.append("\n").append(UiMessages.CONFLICT_CONFIRMATION).toString();
    }

    private record PendingConfirmation(Task task, List<TaskList.Conflict> conflicts) {
    }

    /**
     * Persists the current in-memory task list.
     *
     * @throws GihunException If the task list cannot be saved.
     */
    private void saveTasks() throws GihunException {
        storage.save(tasks.asList());
    }

    private String formatTaskAdded(Task task) {
        return UiMessages.ADD_TASK + "\n" + task
                + "\n" + UiMessages.getTaskCountMessage(tasks.size());
    }

    private String formatTaskList(List<Task> taskList, boolean isMatching) {
        StringBuilder response = new StringBuilder(isMatching
                ? UiMessages.LIST_MATCHING_TASKS
                : UiMessages.LIST_TASKS);
        for (int i = 0; i < taskList.size(); i++) {
            response.append("\n").append(i + 1).append(". ").append(taskList.get(i));
        }
        return response.toString();
    }
}
