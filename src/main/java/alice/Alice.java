package alice;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.Scanner;

/**
 * The main class for the Alice chatbot application.
 * This class orchestrates the flow of the program, handling user input,
 * parsing commands, managing tasks, and coordinating between UI, Storage,
 * and Parser components.
 */
public class Alice {
    private static final String MARK_COMMAND = "mark";
    private static final String UNMARK_COMMAND = "unmark";
    private static final int MAX_DESCRIPTION_LENGTH = 100;

    private final Ui ui;
    private final Storage storage;
    private final Deque<TaskList> undoHistory = new ArrayDeque<>();
    private TaskList tasks;

    /**
     * Constructs an Alice instance with the specified file path for task storage.
     * Initializes all core components and loads tasks from the given file.
     *
     * @param filePath The relative or absolute path to the data file.
     */
    public Alice(String filePath) {
        this(filePath, new Ui());
    }

    /**
     * Constructs an Alice instance with a custom output interface.
     *
     * @param filePath The relative or absolute path to the data file.
     * @param ui       The interface used to display application messages.
     */
    public Alice(String filePath, Ui ui) {
        this.ui = Objects.requireNonNull(ui);
        this.storage = new Storage(filePath);
        this.tasks = new TaskList();
        loadTasks();
    }

    /**
     * The entry point of the application.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        Alice alice = new Alice("./data/alice.txt");
        alice.run();
    }

    /**
     * Loads tasks from the storage file.
     * If an error occurs during loading, an empty task list is initialized.
     */
    private void loadTasks() {
        try {
            tasks = new TaskList(storage.load());
            ui.showMessage("Welcome back! Your saved list is ready with " + tasks.size() + " entries.");
        } catch (IOException e) {
            ui.showError("Aiyo, I couldn't load your saved tasks. I'll start with an empty list. Details: "
                    + e.getMessage());
        }
    }

    /**
     * Saves the current task list to the storage file.
     * If an error occurs during saving, an error message is printed to the UI.
     */
    private void saveTasks() {
        try {
            storage.save(tasks.getTasks());
        } catch (IOException e) {
            ui.showError("Aiyo, I couldn't save your latest changes. Details: " + e.getMessage());
        }
    }

    /**
     * Starts the main interaction loop of the chatbot.
     * Reads user commands, parses them, and executes the corresponding actions
     * until the user types the "bye" command.
     */
    public void run() {
        // Setup
        Scanner scanner = new Scanner(System.in);

        ui.printGreetings();

        mainLoop:
        while (true) {
            // Read user input
            String input = scanner.nextLine();
            if (!processCommand(input)) {
                break mainLoop;
            }
        }
    }

    /**
     * Processes one command and sends its response to the configured user interface.
     *
     * @param input The raw command entered by the user.
     * @return false when the command exits the application, true otherwise.
     */
    public boolean processCommand(String input) {
        Command command = Parser.parseCommand(input);
        switch (command) {
            case BYE:
                ui.quitMessage();
                return false;
            case LIST:
                ui.showTaskList(tasks);
                break;
            case MARK:
                toggleTaskStatus(MARK_COMMAND, input);
                break;
            case UNMARK:
                toggleTaskStatus(UNMARK_COMMAND, input);
                break;
            case TODO:
                addTask(TaskType.TODO, input);
                break;
            case DEADLINE:
                addTask(TaskType.DEADLINE, input);
                break;
            case EVENT:
                addTask(TaskType.EVENT, input);
                break;
            case DELETE:
                deleteTask(input);
                break;
            case VIEW:
                viewDate(input);
                break;
            case FIND:
                findTasks(input);
                break;
            case UNDO:
                undoLastCommand();
                break;
            default:
                ui.showUnknownCommand();
                break;
        }
        return true;
    }

    /**
     * Adds a new task to the list based on the specified type.
     * Validates the input format and description before creating the task.
     *
     * @param type  The type of task to add.
     * @param input The raw user input containing the task description and optional dates.
     */
    public void addTask(TaskType type, String input) {
        Task task = createTask(type, input);
        if (task == null) {
            return;
        }

        saveStateForUndo();
        tasks.add(task);
        ui.showAddTask(tasks);
        saveTasks();
    }

    /**
     * Creates a task of the requested type after validating its input.
     *
     * @param type  The type of task to create.
     * @param input The raw user input containing the task details.
     * @return The created task, or {@code null} when the input is invalid.
     */
    private Task createTask(TaskType type, String input) {
        if (type == TaskType.TODO) {
            return createTodo(input);
        } else if (type == TaskType.DEADLINE) {
            return createDeadline(input);
        } else if (type == TaskType.EVENT) {
            return createEvent(input);
        }
        return null;
    }

    /**
     * Creates a todo task from user input.
     *
     * @param input The raw todo command.
     * @return The created todo, or {@code null} when its description is empty.
     */
    private Task createTodo(String input) {
        String description = Parser.extractDescription(input, TaskType.TODO.getCommand());
        if (description.isEmpty()) {
            ui.showError("Aiyo, a todo needs a description. Try: todo <description>");
            return null;
        }
        if (!isDescriptionValid(description)) {
            return null;
        }
        return new ToDos(description);
    }

    /**
     * Creates a deadline task from user input.
     *
     * @param input The raw deadline command.
     * @return The created deadline, or {@code null} when its format is invalid.
     */
    private Task createDeadline(String input) {
        String[] parts = Parser.parseDeadline(input);
        if (parts == null) {
            ui.showError("Aiyo, I need a task and date. Try: deadline <description> /by yyyy-MM-dd");
            return null;
        }
        if (!isDescriptionValid(parts[0])) {
            return null;
        }

        LocalDate date = Parser.parseDate(parts[1]);
        if (date == null) {
            ui.showError("Aiyo, please enter the date as yyyy-MM-dd. Example: 2024-12-25");
            return null;
        }
        return new Deadlines(parts[0], date);
    }

    /**
     * Creates an event task from user input.
     *
     * @param input The raw event command.
     * @return The created event, or {@code null} when its format is invalid.
     */
    private Task createEvent(String input) {
        String[] parts = Parser.parseEvent(input);
        if (parts == null) {
            ui.showError("Aiyo, I need an event and its dates. Try: event <description> "
                    + "/from yyyy-MM-dd /to yyyy-MM-dd");
            return null;
        }
        if (!isDescriptionValid(parts[0])) {
            return null;
        }

        LocalDate from = Parser.parseDate(parts[1]);
        LocalDate to = Parser.parseDate(parts[2]);
        if (from == null || to == null) {
            ui.showError("Aiyo, please enter both dates as yyyy-MM-dd. Example: 2024-12-20");
            return null;
        }
        if (to.isBefore(from)) {
            ui.showError("Aiyo, the event cannot end before it starts.");
            return null;
        }
        return new Events(parts[0], from, to);
    }

    /**
     * Checks whether a description is safe to display and save.
     *
     * @param description The task description to validate.
     * @return true if the description meets Alice's input limits.
     */
    private boolean isDescriptionValid(String description) {
        if (description.length() > MAX_DESCRIPTION_LENGTH) {
            ui.showError("Aiyo, task descriptions can be at most " + MAX_DESCRIPTION_LENGTH + " characters.");
            return false;
        }
        if (description.contains("|")) {
            ui.showError("Aiyo, task descriptions cannot contain the | character.");
            return false;
        }
        return true;
    }

    /**
     * Toggles the status of a task based on the specified action.
     * If the action is "mark", the task is marked as done only if it is not already done.
     * If the action is "unmark", the task is marked as not done only if it is already done.
     * If the task is already in the desired state, an error message is shown.
     *
     * @param type  The action to perform, either "mark" or "unmark".
     * @param input The raw user input containing the task index.
     */
    public void toggleTaskStatus(String type, String input) {
        String[] parts = input.trim().split("\\s+");
        int index = Parser.parseIndex(parts);

        if (index == -1 || !tasks.isValidIndex(index)) {
            ui.showError("Aiyo, please choose a valid task number. Try: " + type + " 2");
            return;
        }

        try {
            Task selectedTask = tasks.get(index);

            if (selectedTask.isDone && Objects.equals(type, UNMARK_COMMAND)) {
                saveStateForUndo();
                selectedTask.toggleStatus();
                ui.showUnmarkTask(selectedTask);
                saveTasks();
            } else if (!selectedTask.isDone && Objects.equals(type, MARK_COMMAND)) {
                saveStateForUndo();
                selectedTask.toggleStatus();
                ui.showMarkTask(selectedTask);
                saveTasks();
            } else {
                String status = Objects.equals(type, MARK_COMMAND) ? "completed" : "not done";
                ui.showError("That task is already marked as " + status + ".");
            }
        } catch (IndexOutOfBoundsException e2) {
            ui.showError("Aiyo, please choose a task number from 1 to " + tasks.size() + ".");
        }
    }

    /**
     * Deletes a task from the list based on the index provided in the input.
     *
     * @param input The raw user input containing the task index to delete.
     */
    public void deleteTask(String input) {
        String[] parts = input.trim().split("\\s+");
        int index = Parser.parseIndex(parts);
        if (index == -1 || !tasks.isValidIndex(index)) {
            ui.showError("Aiyo, please choose a valid task number. Try: delete 2");
            return;
        }

        try {
            TaskList previousTasks = tasks.copy();
            Task removedTask = tasks.remove(index);
            undoHistory.push(previousTasks);
            ui.showDeleteTask(removedTask, tasks.size());
            saveTasks();
        } catch (IndexOutOfBoundsException e2) {
            ui.showError("Aiyo, please choose a task number from 1 to " + tasks.size() + ".");
        }
    }

    /**
     * Displays all tasks scheduled on a specific date.
     * For deadlines, it matches the exact 'by' date.
     * For events, it matches if the date falls within the event's start and end dates.
     *
     * @param input The raw user input containing the date to view in yyyy-MM-dd format.
     */
    public void viewDate(String input) {
        String[] parts = input.trim().split("\\s+");
        if (parts.length != 2) {
            ui.showError("Aiyo, please tell me which date to check. Try: view 2024-12-25");
            return;
        }
        LocalDate date = Parser.parseDate(parts[1]);
        if (date == null) {
            ui.showError("Aiyo, please enter the date as yyyy-MM-dd. Example: 2024-12-25");
            return;
        }
        ui.showTasksOnDate(tasks, date);
    }

    /**
     * Handles the "find" command to search for tasks by keyword.
     *
     * @param input The raw user input containing the keyword.
     */
    private void findTasks(String input) {
        String keyword = Parser.extractDescription(input, "find").replaceAll("\\s+", " ");
        if (keyword.isEmpty()) {
            ui.showError("Aiyo, please tell me what to find. Try: find book");
            return;
        }
        TaskList matches = tasks.find(keyword);
        ui.showMatchingTasks(matches, keyword);
    }

    /**
     * Saves the current task list before a successful state-changing command.
     */
    private void saveStateForUndo() {
        undoHistory.push(tasks.copy());
    }

    /**
     * Restores the task list from the most recent undo snapshot.
     */
    private void undoLastCommand() {
        if (undoHistory.isEmpty()) {
            ui.showUndoUnavailable();
            return;
        }

        tasks = undoHistory.pop();
        saveTasks();
        ui.showUndoSuccess();
    }
}
