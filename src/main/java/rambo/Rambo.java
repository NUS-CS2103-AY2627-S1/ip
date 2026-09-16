package rambo;

import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import rambo.exception.RamboException;
import rambo.parser.Parser;
import rambo.storage.Storage;
import rambo.task.DeadlineTask;
import rambo.task.EventTask;
import rambo.task.Task;
import rambo.task.TaskList;
import rambo.ui.Constants;
import rambo.ui.Ui;

/**
 * Main application class for Rambo.
 */
public class Rambo {
    private static final String EVENT_USAGE = "event TASK_NAME /from YYYY-MM-DD /to YYYY-MM-DD";
    private static final String HELP_MESSAGE = "Mission briefing! Try one of these commands:\n"
            + "list\n"
            + "find KEYWORD\n"
            + "todo TASK_NAME\n"
            + "deadline TASK_NAME /by YYYY-MM-DD\n"
            + EVENT_USAGE + "\n"
            + "done TASK_NUMBER\n"
            + "priority TASK_NUMBER LEVEL (1 is highest)\n"
            + "delete TASK_NUMBER\n"
            + "help\n"
            + "bye";
    private static final String CLI_OPTIONS = "1) Echo\n"
            + "2) Add Task\n"
            + "3) List Tasks (use: 3 <keyword> to search)\n"
            + "4) Toggle Task Done Status\n"
            + "5) Delete Task\n"
            + "6) Set Task Priority\n"
            + "q or bye) Quit\n";
    private static final String TASK_TYPE_OPTIONS = "1) Todo\n"
            + "2) Deadline\n"
            + "3) Event\n";

    private final Parser parser;
    private final Storage storage;
    private final TaskList taskList;
    private boolean isExitRequested;

    /**
     * Creates a Rambo instance that can respond to one-line GUI commands.
     */
    public Rambo() {
        this.parser = new Parser();
        this.storage = new Storage();
        this.taskList = new TaskList(storage.loadTasks());
        this.isExitRequested = false;
    }

    /**
     * Returns Rambo's response to a one-line command entered in the GUI.
     *
     * @param input text entered by the user
     * @return response to show in the chat window
     */
    public String getResponse(String input) {
        assert input != null : "GUI input should never be null";
        try {
            return executeGuiCommand(input.trim());
        } catch (RamboException e) {
            return e.getMessage();
        }
    }

    /**
     * Returns the message shown when Rambo first starts.
     *
     * @return welcome message and command list
     */
    public String getWelcomeMessage() {
        return Constants.WELCOME_MESSAGE + "\n\n" + HELP_MESSAGE;
    }

    /**
     * Returns whether the user has asked to leave the GUI session.
     *
     * @return true if the GUI should stop accepting input
     */
    public boolean isExitRequested() {
        return isExitRequested;
    }

    /**
     * Separates a GUI command's keyword from its arguments without changing argument case.
     */
    private String executeGuiCommand(String input) throws RamboException {
        if (input.isEmpty()) {
            throw new RamboException("Please enter a command.");
        }

        int separatorIndex = input.indexOf(' ');
        if (separatorIndex < 0) {
            return executeGuiCommandWithoutArguments(input.toLowerCase(Locale.ROOT));
        }

        String command = input.substring(0, separatorIndex).toLowerCase(Locale.ROOT);
        String arguments = input.substring(separatorIndex + 1).trim();
        return executeGuiCommandWithArguments(command, arguments);
    }

    /**
     * Executes commands that must appear on their own.
     */
    private String executeGuiCommandWithoutArguments(String command) throws RamboException {
        switch (command) {
            case "bye":
            case "q":
                isExitRequested = true;
                return Constants.GOODBYE_MESSAGE;
            case "help":
                return HELP_MESSAGE;
            case "list":
                return getTaskListResponse("");
            default:
                throw new RamboException("I do not understand that command.\n" + HELP_MESSAGE);
        }
    }

    /**
     * Routes commands with arguments to their task operations.
     */
    private String executeGuiCommandWithArguments(String command, String arguments) throws RamboException {
        switch (command) {
            case "find":
                return getTaskListResponse(arguments);
            case "todo":
                return addTodo(arguments);
            case "deadline":
                return addDeadline(arguments);
            case "event":
                return addEvent(arguments);
            case "done":
            case "toggle":
                return toggleTask(arguments);
            case "delete":
                return deleteTask(arguments);
            case "priority":
                return setTaskPriority(arguments);
            default:
                throw new RamboException("I do not understand that command.\n" + HELP_MESSAGE);
        }
    }

    private String addTodo(String taskName) throws RamboException {
        validateNotBlank(taskName, "Task name cannot be blank!");
        Task task = new Task(taskName);
        addAndSaveTask(task);
        return getAddedTaskResponse(task);
    }

    private String addDeadline(String taskDetails) throws RamboException {
        String[] fields = splitRequiredMarker(taskDetails, "/by", "Use: deadline TASK_NAME /by YYYY-MM-DD");
        Task task = new DeadlineTask(fields[0], fields[1]);
        addAndSaveTask(task);
        return getAddedTaskResponse(task);
    }

    private String addEvent(String taskDetails) throws RamboException {
        String[] fromFields = splitRequiredMarker(taskDetails, "/from", EVENT_USAGE);
        String[] toFields = splitRequiredMarker(fromFields[1], "/to", EVENT_USAGE);
        Task task = new EventTask(fromFields[0], toFields[0], toFields[1]);
        addAndSaveTask(task);
        return getAddedTaskResponse(task);
    }

    /**
     * Adds a task and persists the updated list for both GUI and CLI task creation.
     */
    private void addAndSaveTask(Task task) throws RamboException {
        taskList.add(task);
        storage.saveTasks(taskList.getTasks());
    }

    private String toggleTask(String taskNumberText) throws RamboException {
        int taskNumber = parser.parseTaskNumber(taskNumberText);
        taskList.toggle(taskNumber);
        storage.saveTasks(taskList.getTasks());
        return "Roger that! Task status updated:\n  " + taskList.getTask(taskNumber);
    }

    private String deleteTask(String taskNumberText) throws RamboException {
        int taskNumber = parser.parseTaskNumber(taskNumberText);
        Task taskToDelete = taskList.getTask(taskNumber);
        taskList.delete(taskNumber);
        storage.saveTasks(taskList.getTasks());
        return "Mission retired! I've removed this task:\n  " + taskToDelete;
    }

    private String setTaskPriority(String taskDetails) throws RamboException {
        String[] fields = taskDetails.split("\\s+");
        if (fields.length != 2) {
            throw new RamboException("Use: priority TASK_NUMBER LEVEL (1-3)");
        }

        int taskNumber = parser.parseTaskNumber(fields[0]);
        int priorityLevel = parser.parsePriorityLevel(fields[1]);
        taskList.setPriority(taskNumber, priorityLevel);
        storage.saveTasks(taskList.getTasks());
        return "Orders updated! I've changed this task's priority:\n  " + taskList.getTask(taskNumber);
    }

    private String getTaskListResponse(String searchTerm) {
        List<Task> tasks = taskList.getTasks();
        if (tasks.isEmpty()) {
            return "All clear! Your task list is empty.";
        }

        StringBuilder response = new StringBuilder();
        String normalisedSearchTerm = searchTerm.toLowerCase(Locale.ROOT);
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (isMatchingSearchTerm(task, normalisedSearchTerm)) {
                response.append(i + 1).append(". ").append(task).append(System.lineSeparator());
            }
        }

        if (response.isEmpty()) {
            return String.format("No tasks found matching \"%s\".", searchTerm);
        }
        return response.toString().stripTrailing();
    }

    private String getAddedTaskResponse(Task task) {
        return String.format("Mission accepted! I've added this task:%n  %s%n"
                + "Now you have %d task(s) on the mission board.",
                task, taskList.getTasks().size());
    }

    private String[] splitRequiredMarker(String input, String marker, String usageMessage) throws RamboException {
        int markerIndex = input.indexOf(marker);
        if (markerIndex < 0) {
            throw new RamboException(usageMessage);
        }

        String beforeMarker = input.substring(0, markerIndex).trim();
        String afterMarker = input.substring(markerIndex + marker.length()).trim();
        validateNotBlank(beforeMarker, usageMessage);
        validateNotBlank(afterMarker, usageMessage);
        assert !beforeMarker.isBlank() && !afterMarker.isBlank()
                : "Validated command fields should be non-blank";
        return new String[] { beforeMarker, afterMarker };
    }

    private void validateNotBlank(String value, String errorMessage) throws RamboException {
        if (value.isBlank()) {
            throw new RamboException(errorMessage);
        }
    }

    /**
     * Starts the command-line version of Rambo.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        Ui ui = new Ui(new Scanner(System.in));
        ui.showWelcome();

        try {
            new Rambo().runCommandLineInterface(ui);
        } catch (RamboException e) {
            showError(ui, e);
        } finally {
            ui.close();
            ui.showGoodbye();
        }
    }

    private void runCommandLineInterface(Ui ui) {
        boolean isChatRunning = true;
        while (isChatRunning) {
            showMainMenu(ui);
            if (!ui.hasNextLine()) {
                break;
            }
            String input = ui.readLine();
            try {
                char command = parser.parseCommand(input);
                isChatRunning = executeCliCommand(command, input, ui);
            } catch (RamboException e) {
                showError(ui, e);
            }
        }
    }

    private void showMainMenu(Ui ui) {
        ui.showDivider();
        ui.showLine(CLI_OPTIONS);
        ui.showLine("\n");
        ui.showPrompt("Enter your option: ");
    }

    private boolean executeCliCommand(char command, String input, Ui ui) {
        switch (command) {
            case '1':
                Echo.start(ui);
                break;
            case '2':
                addTaskFromCli(ui);
                break;
            case '3':
                showTasks(ui, parser.parseSearchTerm(input));
                break;
            case '4':
                toggleTaskFromCli(ui);
                break;
            case '5':
                deleteTaskFromCli(ui);
                break;
            case '6':
                setTaskPriorityFromCli(ui);
                break;
            case 'q':
                return false;
            default:
                throw new RamboException("That option doesn't exist, my friend! Try again!");
        }
        return true;
    }

    private void addTaskFromCli(Ui ui) {
        ui.showDivider("TASK TYPE");
        ui.showLine(TASK_TYPE_OPTIONS);
        ui.showLine("\n");
        ui.showPrompt("Choose the type of task you want to add: ");

        int taskType = parser.parseTaskType(ui.readLine());
        Task task = createTaskFromCli(taskType, ui);
        addAndSaveTask(task);
        ui.showLine(Constants.ANSI_GREEN + "\nMission accepted! Your task has been added!" + Constants.ANSI_RESET);
    }

    private Task createTaskFromCli(int taskType, Ui ui) {
        switch (taskType) {
            case 1:
                return new Task(readRequiredField(ui, "Enter your task name: ", "Task name cannot be blank!"));
            case 2:
                return createDeadlineFromCli(ui);
            case 3:
                return createEventFromCli(ui);
            default:
                throw new RamboException("Not a valid task type!");
        }
    }

    private Task createDeadlineFromCli(Ui ui) {
        String taskName = readRequiredField(ui, "Enter your task name: ", "Task name cannot be blank!");
        String deadline = readRequiredField(ui, "Enter your deadline: ", "Deadline cannot be blank!");
        return new DeadlineTask(taskName, deadline);
    }

    private Task createEventFromCli(Ui ui) {
        String taskName = readRequiredField(ui, "Enter your task name: ", "Task name cannot be blank!");
        String fromDate = readRequiredField(ui, "Enter your from date: ", "From date cannot be blank!");
        String toDate = readRequiredField(ui, "Enter your to date: ", "To date cannot be blank!");
        return new EventTask(taskName, fromDate, toDate);
    }

    private String readRequiredField(Ui ui, String prompt, String errorMessage) {
        ui.showPrompt(prompt);
        String value = ui.readLine();
        validateNotBlank(value, errorMessage);
        return value;
    }

    private void showTasks(Ui ui, String searchTerm) {
        ui.showDivider("TASK LIST");
        String normalisedSearchTerm = searchTerm.toLowerCase(Locale.ROOT);
        boolean hasMatchingTask = false;

        List<Task> tasks = taskList.getTasks();
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (isMatchingSearchTerm(task, normalisedSearchTerm)) {
                ui.showLine(String.format("%d: %s", i + 1, task));
                hasMatchingTask = true;
            }
        }

        if (!searchTerm.isEmpty() && !hasMatchingTask) {
            ui.showLine(String.format("No tasks found matching \"%s\".", searchTerm));
        }
    }

    private boolean isMatchingSearchTerm(Task task, String normalisedSearchTerm) {
        String normalisedTaskName = task.getTaskName().toLowerCase(Locale.ROOT);
        return normalisedSearchTerm.isEmpty() || normalisedTaskName.contains(normalisedSearchTerm);
    }

    private void toggleTaskFromCli(Ui ui) {
        ui.showPrompt("Enter the index of the task you want to toggle status of: ");
        int taskNumber = parser.parseTaskNumber(ui.readLine());
        taskList.toggle(taskNumber);
        storage.saveTasks(taskList.getTasks());
    }

    private void deleteTaskFromCli(Ui ui) {
        ui.showPrompt("Enter the index of the task you want to remove: ");
        int taskNumber = parser.parseTaskNumber(ui.readLine());
        taskList.delete(taskNumber);
        storage.saveTasks(taskList.getTasks());
    }

    private void setTaskPriorityFromCli(Ui ui) {
        ui.showPrompt("Enter the index of the task you want to prioritize: ");
        int taskNumber = parser.parseTaskNumber(ui.readLine());
        ui.showPrompt("Enter a priority level from 1 (highest) to 3 (lowest): ");
        int priorityLevel = parser.parsePriorityLevel(ui.readLine());
        taskList.setPriority(taskNumber, priorityLevel);
        storage.saveTasks(taskList.getTasks());
        ui.showLine(Constants.ANSI_GREEN + "\nYour task's priority has been updated!" + Constants.ANSI_RESET);
    }

    private static void showError(Ui ui, RamboException exception) {
        ui.showLine(Constants.ANSI_RED + exception.getMessage() + Constants.ANSI_RESET);
    }
}
