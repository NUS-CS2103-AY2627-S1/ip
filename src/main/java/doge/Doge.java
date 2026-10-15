package doge;

import java.util.List;

import doge.command.Command;
import doge.exception.DogeException;
import doge.model.Task;
import doge.model.TaskList;
import doge.parser.Parser;
import doge.storage.Storage;
import doge.ui.UI;

/** Runs the Doge task-management application and coordinates its components. */
public class Doge {

    private final UI ui;
    private final TaskList tasks;
    private final Storage storage;

    /** Creates Doge with tasks loaded from local storage. */
    Doge() {
        ui = new UI();
        storage = new Storage();
        tasks = loadTasks();
    }

    /** Starts the command-line application. */
    public static void main(String[] args) {
        new Doge().run();
    }

    private void run() {
        ui.showWelcome();

        while (true) {
            String text = ui.readCommand();
            try {
                processInput(text);
            } catch (DogeException e) {
                ui.printMessage(e.getMessage());
            }

            if (text.equals("bye")) {
                ui.showGoodbye();
                break;
            }
        }
    }

    /** Executes a single user command after parsing its command keyword and arguments. */
    public void processInput(String input) throws DogeException {
        if (input == null || input.isBlank()) {
            throw new DogeException("Please enter a command.");
        }
        String[] commands = input.split("\\s+");
        Command command = Command.fromText(commands[0]);

        switch (command) {

            case MARK -> {
                Task task = markTask(commands);
                ui.showTaskMarked(task);
            }

            case UNMARK -> {
                Task task = unmarkTask(commands);
                ui.showTaskUnmarked(task);
            }

            case PRIORITY -> {
                Task task = setPriority(commands);
                ui.printMessage("    Much priority! I boosted this task:\n      " + task);
            }

            case UNPRIORITY -> {
                Task task = clearPriority(commands);
                ui.printMessage("    Priority removed. Such simplicity:\n      " + task);
            }

            case LIST -> {
                validateCommandLength(commands, 1, "list");
                ui.printTaskList(tasks);
            }

            case FIND -> {
                validateCommandLength(commands, 2, "find KEYWORD");
                List<Task> matchingTasks = findMatchingTasks(commands);
                ui.printMessage(ui.printMatchingTasks(matchingTasks));
            }

            case BYE -> {
                validateCommandLength(commands, 1, "bye");
                saveTasks();
            }

            case DELETE -> {
                Task delTask = deleteTask(commands);
                ui.printMessage("    Task sent to the dog park (deleted):\n      " + delTask);
            }

            case TODO, DEADLINE, EVENT -> addTask(input);
            default -> {
                // All command values are handled above.
            }
        }
    }

    /**
     * Executes a command from the graphical user interface and returns a response.
     *
     * @param input command entered by the user.
     * @return response message for the graphical user interface.
     */
    public String getResponse(String input) {
        if (input == null || input.isBlank()) {
            return "Oops, much confusion: Please enter a command.";
        }
        try {
            String[] commands = input.trim().split("\\s+");
            Command command = Command.fromText(commands[0]);

            return switch (command) {
                case MARK -> {
                    Task task = markTask(commands);
                    yield "Much progress! This task is now done:\n" + task;
                }
                case UNMARK -> {
                    Task task = unmarkTask(commands);
                    yield "No zoomies yet: this task is back on the list:\n" + task;
                }
                case PRIORITY -> {
                    Task task = setPriority(commands);
                    yield "Much priority! I boosted this task:\n" + task;
                }
                case UNPRIORITY -> {
                    Task task = clearPriority(commands);
                    yield "Priority removed. Such simplicity:\n" + task;
                }
                case LIST -> {
                    validateCommandLength(commands, 1, "list");
                    yield tasks.toString();
                }
                case FIND -> {
                    validateCommandLength(commands, 2, "find KEYWORD");
                    List<Task> matchingTasks = findMatchingTasks(commands);
                    yield ui.printMatchingTasks(matchingTasks);
                }
                case BYE -> {
                    validateCommandLength(commands, 1, "bye");
                    storage.save(tasks);
                    yield "Much farewell! See you on the next walk.";
                }
                case DELETE -> {
                    Task deletedTask = deleteTask(commands);
                    yield "Task sent to the dog park (deleted):\n" + deletedTask;
                }
                case TODO, DEADLINE, EVENT -> {
                    Task task = Parser.parseTask(input);
                    tasks.add(task);
                    yield "Much add! New task fetched:\n" + task
                            + "\nYour pack now has " + tasks.size() + " tasks.";
                }
            };
        } catch (DogeException e) {
            return "Oops, much confusion: " + e.getMessage();
        }
    }

    /** Saves the current task list so callers can persist state during application shutdown. */
    public void saveTasks() throws DogeException {
        storage.save(tasks);
    }

    /** Extracts and validates the one-based task number from a command. */
    private int getTaskNumber(String[] commands) throws DogeException {
        if (commands.length < 2 || commands[1].isBlank()) {
            throw new DogeException("please provide a task number.");
        }
        return validateTaskNumber(commands[1]);
    }

    /** Marks the task selected by a command as completed. */
    private Task markTask(String[] commands) throws DogeException {
        validateCommandLength(commands, 2, "mark TASK_NUMBER");
        Task task = tasks.get(getTaskNumber(commands));
        task.markDone();
        return task;
    }

    /** Marks the task selected by a command as incomplete. */
    private Task unmarkTask(String[] commands) throws DogeException {
        validateCommandLength(commands, 2, "unmark TASK_NUMBER");
        Task task = tasks.get(getTaskNumber(commands));
        task.unmarkDone();
        return task;
    }

    /** Sets the selected task's priority, defaulting to level 1 when omitted. */
    private Task setPriority(String[] commands) throws DogeException {
        if (commands.length < 2 || commands.length > 3) {
            throw new DogeException("try this format: priority TASK_NUMBER [LEVEL]");
        }
        int priority = commands.length == 2 ? 1 : parsePriority(commands[2]);
        Task task = tasks.get(getTaskNumber(commands));
        task.setPriority(priority);
        return task;
    }

    /** Clears the selected task's priority. */
    private Task clearPriority(String[] commands) throws DogeException {
        if (commands.length != 2) {
            throw new DogeException("try this format: unpriority TASK_NUMBER");
        }
        Task task = tasks.get(getTaskNumber(commands));
        task.setPriority(0);
        return task;
    }

    /** Parses a positive priority level. */
    private int parsePriority(String priorityText) throws DogeException {
        try {
            int priority = Integer.parseInt(priorityText);
            if (priority < 1) {
                throw new DogeException("priority level must be at least 1.");
            }
            return priority;
        } catch (NumberFormatException e) {
            throw new DogeException("priority level must be a positive number.");
        }
    }

    /** Removes and returns the task selected by a command. */
    private Task deleteTask(String[] commands) throws DogeException {
        validateCommandLength(commands, 2, "delete TASK_NUMBER");
        return tasks.delete(getTaskNumber(commands));
    }

    /** Returns the tasks matching the keyword supplied in a command. */
    private List<Task> findMatchingTasks(String[] commands) throws DogeException {
        if (commands.length < 2 || commands[1].isBlank()) {
            throw new DogeException("please provide a keyword to find.");
        }
        return tasks.find(commands[1]);
    }

    /** Rejects commands with missing or unexpected arguments. */
    private void validateCommandLength(String[] commands, int expectedLength, String usage)
            throws DogeException {
        if (commands.length != expectedLength) {
            throw new DogeException("Please use this format: " + usage);
        }
    }

    /** Parses a task command, adds the resulting task, and reports parsing errors. */
    private void addTask(String text) {
        try {
            Task task = Parser.parseTask(text);
            tasks.add(task);
            ui.showTaskAdded(task, tasks.size());
        } catch (DogeException e) {
            ui.printMessage("    " + e.getMessage());
        }
    }


    /** Converts a task number to an integer and verifies that it exists in the task list. */
    private int validateTaskNumber(String numberText) throws DogeException {
        int taskNumber;
        try {
            taskNumber = Integer.parseInt(numberText);

            if (taskNumber < 1 || taskNumber > tasks.size()) {
                throw new DogeException("that task number is not in the pack.");
            }

            return taskNumber;
        } catch (NumberFormatException e) {
            throw new DogeException("please enter a valid task number.");
        }
    }

    private TaskList loadTasks() {
        try {
            return storage.load();
        } catch (DogeException e) {
            ui.printMessage(e.getMessage());
            return new TaskList();
        }
    }
}
