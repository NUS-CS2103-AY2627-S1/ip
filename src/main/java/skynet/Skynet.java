package skynet;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;
/**
 * Main class for the Skynet application.
 */
public class Skynet {
    private final Storage storage;
    private TaskList tasks;
    private String commandType;
    private String startupWarning;
    private final Deque<List<Task>> stateHistory = new ArrayDeque<>();

    /**
     * Creates a Skynet instance and loads saved tasks.
     */
    public Skynet() {
        this(new Storage());
    }

    /**
     * Creates a Skynet instance with supplied storage.
     *
     * @param storage storage implementation to use
     */
    Skynet(Storage storage) {
        if (storage == null) {
            throw new IllegalArgumentException("Storage cannot be null.");
        }
        this.storage = storage;

        try {
            tasks = new TaskList(storage.load());
        } catch (IOException | SecurityException e) {
            tasks = new TaskList();
            startupWarning = "Saved tasks could not be loaded. The current task list starts empty.";
        }
    }

    /**
     * Processes a user command and returns the appropriate response.
     *
     * @param command the command entered by the user
     * @return the response to display
     */
    public String getResponse(String command) {
        try {
            if (command == null || command.isBlank()) {
                throw new SkynetException("Please enter a command.");
            }
            if (!command.equals(command.trim())) {
                throw new SkynetException("Command must not start or end with spaces.");
            }
            for (int i = 1; i < command.length(); i++) {
                if (Character.isWhitespace(command.charAt(i - 1))
                        && Character.isWhitespace(command.charAt(i))) {
                    throw new SkynetException("Please use only one space between command parts.");
                }
            }
            // Extract the first word to determine the command type
            String commandWord = command.split(" ", 2)[0];

            String response = switch (commandWord) {
                case "bye" -> {
                    Parser.validateSimpleCommand(command, "bye");
                    yield handleBye();
                }
                case "list" -> {
                    Parser.validateSimpleCommand(command, "list");
                    yield handleList();
                }
                case "find" -> handleFind(command);
                case "mark" -> handleMark(command);
                case "unmark" -> handleUnmark(command);
                case "todo" -> handleTodo(command);
                case "deadline" -> handleDeadline(command);
                case "event" -> handleEvent(command);
                case "delete" -> handleDelete(command);
                case "undo" -> {
                    Parser.validateSimpleCommand(command, "undo");
                    yield handleUndo();
                }
                default -> {
                    commandType = "OtherCommand";
                    throw new SkynetException("Hello, your command is unrecognized. \n"
                            + "Use the following commands: \n"
                            + "- todo \n"
                            + "- deadline \n"
                            + "- event \n"
                            + "- list \n"
                            + "- mark \n"
                            + "- unmark \n"
                            + "- delete \n"
                            + "- find \n"
                            + "- undo \n"
                            + "- bye \n");
                }
            };
            return includeStartupWarning(response);
        } catch (SkynetException | IOException | IllegalArgumentException e) {
            return includeStartupWarning(e.getMessage());
        }
    }

    /**
     * Adds a startup storage warning to the first response, if one exists.
     *
     * @param response the normal command response
     * @return the response with the startup warning when applicable
     */
    private String includeStartupWarning(String response) {
        if (startupWarning == null) {
            return response;
        }
        String warning = startupWarning;
        startupWarning = null;
        return warning + "\n" + response;
    }

    private void saveStateSnapshot() {
        // Create a copy of the task list before modifying it
        // to track state and support undo by retrieving last state
        // use copy method instead of clone since protected
        List<Task> snapshot = tasks.getTasks().stream()
                .map(Task::copy) //
                .collect(Collectors.toList());
        stateHistory.push(snapshot);
    }

    /**
     * Returns the type of the most recently processed command.
     *
     * @return the command type
     */
    public String getCommandType() {
        return commandType;
    }

    private String handleBye() {
        commandType = "OtherCommand";
        return "The Future is now. Chat Terminated.";
    }

    private String handleList() {
        commandType = "OtherCommand";

        StringBuilder response = new StringBuilder("[Target List Display]\n");
        for (int i = 0; i < tasks.size(); i++) {
            response.append(i + 1)
                    .append(". ")
                    .append(tasks.get(i))
                    .append("\n");
        }
        return response.toString().trim();
    }

    private String handleFind(String command) throws SkynetException {
        commandType = "OtherCommand";
        String keyword = Parser.parseFind(command);
        List<Task> matches = tasks.find(keyword);

        if (matches.isEmpty()) {
            return "No matching tasks found.";
        }

        StringBuilder response = new StringBuilder("Here are the matching tasks in your list:\n");
        for (int i = 0; i < matches.size(); i++) {
            response.append(i + 1)
                    .append(". ")
                    .append(matches.get(i))
                    .append("\n");
        }
        return response.toString().trim();
    }

    private String handleMark(String command) throws SkynetException, IOException {
        commandType = "MarkCommand";
        int taskIndex = Parser.getTaskIndex(command, "mark", tasks.size());
        saveStateSnapshot();

        try {
            tasks.get(taskIndex).markAsDone();
            storage.save(tasks.getTasks());
        } catch (IOException e) {
            restoreLastSnapshot();
            throw e;
        }

        return "The Target has been Neutralized:\n  " + tasks.get(taskIndex);
    }

    private String handleUnmark(String command) throws SkynetException, IOException {
        commandType = "OtherCommand";
        int taskIndex = Parser.getTaskIndex(command, "unmark", tasks.size());
        saveStateSnapshot();

        try {
            tasks.get(taskIndex).markAsNotDone();
            storage.save(tasks.getTasks());
        } catch (IOException e) {
            restoreLastSnapshot();
            throw e;
        }

        return "Failed to Complete:\n  " + tasks.get(taskIndex);
    }

    private String handleTodo(String command) throws SkynetException, IOException {
        commandType = "AddCommand";
        Task task = Parser.parseTodo(command);

        saveStateSnapshot();
        try {
            tasks.add(task);
            storage.save(tasks.getTasks());
        } catch (IOException e) {
            restoreLastSnapshot();
            throw e;
        }

        return "Target in time has been located:\n  " + task;
    }

    private String handleDeadline(String command) throws SkynetException, IOException {
        commandType = "AddCommand";
        Task task = Parser.parseDeadline(command);

        saveStateSnapshot();
        try {
            tasks.add(task);
            storage.save(tasks.getTasks());
        } catch (IOException e) {
            restoreLastSnapshot();
            throw e;
        }

        return "Incursion Risk, Finish Deadline:\n  " + task;
    }

    private String handleEvent(String command) throws SkynetException, IOException {
        commandType = "AddCommand";
        Task task = Parser.parseEvent(command);

        saveStateSnapshot();
        try {
            tasks.add(task);
            storage.save(tasks.getTasks());
        } catch (IOException e) {
            restoreLastSnapshot();
            throw e;
        }

        return "Temporal target located:\n  " + task;
    }

    private String handleDelete(String command) throws SkynetException, IOException {
        commandType = "DeleteCommand";
        int taskIndex = Parser.getTaskIndex(command, "delete", tasks.size());
        saveStateSnapshot();

        Task deletedTask;
        try {
            deletedTask = tasks.delete(taskIndex);
            storage.save(tasks.getTasks());
        } catch (IOException e) {
            restoreLastSnapshot();
            throw e;
        }

        return "Target Erased:\n  " + deletedTask
                + "\nRemaining targets: " + tasks.size();
    }

    private String handleUndo() throws SkynetException, IOException {
        commandType = "OtherCommand";

        if (stateHistory.isEmpty()) {
            throw new SkynetException("No previous operations to undo.");
        }

        List<Task> currentState = copyCurrentTasks();
        List<Task> previousState = stateHistory.pop();
        this.tasks = new TaskList(previousState);
        try {
            storage.save(tasks.getTasks());
        } catch (IOException e) {
            this.tasks = new TaskList(currentState);
            stateHistory.push(previousState);
            throw e;
        }

        return "Previous command has been undone successfully.";
    }

    /**
     * Restores and removes the snapshot created for a failed modification.
     */
    private void restoreLastSnapshot() {
        if (!stateHistory.isEmpty()) {
            tasks = new TaskList(stateHistory.pop());
        }
    }

    /**
     * Creates a deep copy of the current task state.
     *
     * @return copied tasks
     */
    private List<Task> copyCurrentTasks() {
        return tasks.getTasks().stream()
                .map(Task::copy)
                .collect(Collectors.toList());
    }

    /**
     * Runs the command-line version of Skynet.
     *
     * @param args command-line arguments
     */
    static void main(String... args) {
        Skynet skynet = new Skynet();
        Ui ui = new Ui();

        ui.showWelcome();

        while (true) {
            String command = ui.readCommand();
            String response = skynet.getResponse(command);

            if (command.equals("bye")) {
                ui.showGoodbye();
                break;
            }

            ui.showError(response);
        }
    }
}
