package michael;

import task.Task;
import java.io.IOException;
import java.util.List;

/**
 * Starts the michael.Michael chatbot application.
 */
public class Michael {
    private static final String EMPTY_COMMAND = "Please enter a command.";
    private boolean lastResponseWasError;

    private final Storage storage;
    private final Ui ui;
    private TaskList list;
    private TaskList previousState;

    /**
     * Initializes the chatbot components and loads previously saved tasks from disk.
     * If loading fails due to an exception, an empty task list is initialized.
     *
     * @param filePath The file path where tasks are saved and loaded from.
     */
    public Michael(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        try {
            list = new TaskList(storage.load());
        } catch (IOException | MichaelException e) {
            ui.showLoadingError(e.getMessage());
            list = new TaskList();
        }
    }

    /**
     * Runs the main execution loop of the application.
     * Continually reads commands from the user, executes requested task operations,
     * updates storage, and outputs status messages until the "bye" command is given.
     */
    public void run() {
        ui.showWelcome();

        String command;
        while ((command = ui.readCommand()) != null) {
            if (command.equals("bye")) {
                break;
            }

            String[] parts = command.split(" ", 2);
            String commandWord = parts[0];
            String commandArgs = parts.length > 1 ? parts[1].trim() : "";

            try {
                switch (commandWord) {
                    case "undo":
                        undo();
                        break;
                    case "list":
                        ui.showTaskList(list.getTasks());
                        break;

                    case "mark": {
                        TaskList state = list.snapshot();
                        int index = Parser.parseTaskIndex(commandArgs, "mark");
                        Task task = list.mark(index);
                        storage.save(list);
                        previousState = state;
                        ui.showTaskMarked(task);
                        break;
                    }
                    case "unmark": {
                        TaskList state = list.snapshot();
                        int index = Parser.parseTaskIndex(commandArgs, "unmark");
                        Task task = list.unmark(index);
                        storage.save(list);
                        previousState = state;
                        ui.showTaskUnmarked(task);
                        break;
                    }
                    case "delete": {
                        TaskList state = list.snapshot();
                        int index = Parser.parseTaskIndex(commandArgs, "delete");
                        Task deletedTask = list.delete(index);
                        storage.save(list);
                        previousState = state;
                        ui.showTaskDeleted(deletedTask, list.size());
                        break;
                    }
                    case "todo": {
                        TaskList state = list.snapshot();
                        Task task = Parser.parseTodo(commandArgs);
                        list.add(task);
                        storage.save(list);
                        previousState = state;
                        ui.showTaskAdded(task, list.size());
                        break;
                    }
                    case "deadline": {
                        TaskList state = list.snapshot();
                        Task task = Parser.parseDeadline(commandArgs);
                        list.add(task);
                        storage.save(list);
                        previousState = state;
                        ui.showTaskAdded(task, list.size());
                        break;
                    }
                    case "event": {
                        TaskList state = list.snapshot();
                        Task task = Parser.parseEvent(commandArgs);
                        list.add(task);
                        storage.save(list);
                        previousState = state;
                        ui.showTaskAdded(task, list.size());
                        break;
                    }
                    case "find": {
                        String keyword = Parser.parseFind(commandArgs);
                        List<Task> matchingTasks = list.find(keyword);
                        ui.showMatchingTasks(matchingTasks);
                        break;
                    }
                    default:
                        throw new MichaelException(" OOPS!!! I'm sorry, but I don't know what that means :-(");
                }
            } catch (MichaelException | IOException e) {
                ui.showError(e.getMessage());
            }
        }

        ui.showGoodbye();
    }

    /** Executes one user command and returns the message shown in the GUI. */
    public String processCommand(String command) {
        lastResponseWasError = false;
        if (command == null || command.isBlank()) {
            lastResponseWasError = true;
            return EMPTY_COMMAND;
        }

        String[] parts = command.trim().split("\\s+", 2);
        String commandWord = parts[0];
        String commandArgs = parts.length > 1 ? parts[1].trim() : "";
        try {
            switch (commandWord) {
                case "list":
                    if (list.getTasks().isEmpty()) return "🌑 Your task orbit is empty.";
                    StringBuilder result = new StringBuilder("🛰 Star chart — tasks currently in orbit:\n");
                    for (int i = 0; i < list.getTasks().size(); i++) {
                        result.append(i + 1).append(". ").append(list.getTasks().get(i)).append('\n');
                    }
                    return result.toString().trim();
                case "undo":
                    return undo();
                case "mark":
                    TaskList markState = list.snapshot();
                    Task marked = list.mark(Parser.parseTaskIndex(commandArgs, "mark"));
                    storage.save(list);
                    previousState = markState;
                    return "✅ Mission complete — marked as done:\n" + marked;
                case "unmark":
                    TaskList unmarkState = list.snapshot();
                    Task unmarked = list.unmark(Parser.parseTaskIndex(commandArgs, "unmark"));
                    storage.save(list);
                    previousState = unmarkState;
                    return "🔭 Mission reopened — marked as not done:\n" + unmarked;
                case "delete":
                    TaskList deleteState = list.snapshot();
                    Task deleted = list.delete(Parser.parseTaskIndex(commandArgs, "delete"));
                    storage.save(list);
                    previousState = deleteState;
                    return "☄️ Task sent beyond the event horizon:\n" + deleted;
                case "todo":
                    TaskList todoState = list.snapshot();
                    Task todo = Parser.parseTodo(commandArgs);
                    list.add(todo);
                    storage.save(list);
                    previousState = todoState;
                    return "🚀 New mission launched:\n" + todo;
                case "deadline":
                    TaskList deadlineState = list.snapshot();
                    Task deadline = Parser.parseDeadline(commandArgs);
                    list.add(deadline);
                    storage.save(list);
                    previousState = deadlineState;
                    return "🚀 Deadline mission launched:\n" + deadline;
                case "event":
                    TaskList eventState = list.snapshot();
                    Task event = Parser.parseEvent(commandArgs);
                    list.add(event);
                    storage.save(list);
                    previousState = eventState;
                    return "🚀 Event mission launched:\n" + event;
                case "find":
                    List<Task> matches = list.find(Parser.parseFind(commandArgs));
                    return matches.isEmpty() ? "🌌 No matching signals found." : "📡 Matching signals from your task orbit:\n" + matches;
                case "bye":
                    return "👋 Disengaging from mission control. Safe travels among the stars!";
                default:
                    throw new MichaelException("Sorry, I don't know what that means.");
            }
        } catch (MichaelException | IOException e) {
            lastResponseWasError = true;
            return e.getMessage();
        }
    }

    /** Returns whether the most recently processed command produced an error. */
    public boolean wasLastResponseError() {
        return lastResponseWasError;
    }

    /** Generates a response for a user's chat message. */
    public String getResponse(String input) {
        return processCommand(input);
    }

    /** Restores the state before the most recent successful mutation. */
    private String undo() throws MichaelException, IOException {
        if (previousState == null) {
            return "There is nothing to undo.";
        }
        list = previousState;
        storage.save(list);
        previousState = null;
        return "Undid the last command.";
    }

    /**
     * Main entry point for starting the Michael application.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        new Michael("data/michael.txt").run();
    }
}
