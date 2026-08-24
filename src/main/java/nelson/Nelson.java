package nelson;

import java.nio.file.Paths;

import nelson.command.FindCommand;
import nelson.command.Parser;
import nelson.exception.NelsonException;
import nelson.storage.Storage;
import nelson.task.Deadline;
import nelson.task.Event;
import nelson.task.Task;
import nelson.task.TaskList;
import nelson.task.Todo;
import nelson.ui.Ui;

/**
 * Starts the Nelson chatbot application.
 */
public class Nelson {
    /** Handles loading and saving the task list. */
    private final Storage storage;
    /** Handles console input and output. */
    private final Ui ui = new Ui();
    /** Converts raw user input into executable commands. */
    private final Parser parser = new Parser();
    /** Stores the user's tasks for this run of the program. */
    private final TaskList tasks;

    /** Creates Nelson and restores any tasks saved by an earlier run. */
    public Nelson() {
        storage = new Storage(Paths.get("data", "nelson.txt"));
        tasks = storage.load();
    }

    /**
     * Starts the chatbot application.
     * @param args command-line arguments, which are currently unused
     */
    public static void main(String[] args) {
        Nelson nelson = new Nelson();
        nelson.ui.showWelcome();
        nelson.handleCommands();
    }

    /**
     * Reads and responds to commands until the user resigns.
     */
    public void handleCommands() {
        while (ui.hasNextCommand()) {
            String command = ui.readCommand();
            ui.showSeparator();

            try {
                if (command.equals("bye") || command.equals("Bye")) {
                    ui.showGoodbye();
                    return;
                }
                processCommand(command);
            } catch (NelsonException exception) {
                ui.showError(exception.getMessage());
            } catch (NumberFormatException | IndexOutOfBoundsException exception) {
                ui.showError("Molo! Out of bounds! That task number doesn't exist on this board.");
            }
            ui.showSeparator();
        }
    }

    /**
     * Runs one command after validating its arguments.
     *
     * @param command the command entered by the user
     * @throws NelsonException if the command or its arguments are invalid
     */
    public void processCommand(String command) throws NelsonException {
        Parser.Command parsed = parser.parse(command);
        switch (parsed.getType()) {
            case LIST:
                showTasks();
                break;
            case TODO:
                addTypedTask(new Todo(parsed.getArgument(0)));
                break;
            case DEADLINE:
                addTypedTask(new Deadline(parsed.getArgument(0), parsed.getArgument(1)));
                break;
            case EVENT:
                addTypedTask(new Event(parsed.getArgument(0), parsed.getArgument(1), parsed.getArgument(2)));
                break;
            case MARK:
                markTask(parsed.getArgument(0));
                break;
            case UNMARK:
                unmarkTask(parsed.getArgument(0));
                break;
            case DELETE:
                deleteTask(parsed.getArgument(0));
                break;
            case FIND:
                FindCommand findCommand = (FindCommand) parsed;
                ui.showMatchingTasks(tasks.find(findCommand.getKeyword()));
                break;
            default:
                throw new NelsonException("Molo! I don't know what that means. Are you even playing the same game?");
        }
    }

    /**
     * Displays all tasks that have been added during this run.
     */
    public void showTasks() {
        ui.show("Molo! Evaluate your board state. Here are the tasks in your list:");
        for (int index = 0; index < tasks.size(); index++) {
            Task task = tasks.get(index);
            ui.show((index + 1) + "." + task);
        }
    }

    /**
     * Marks the task specified in a mark command as complete.
     *
     * @param command the complete command entered by the user
     * @throws NelsonException if index is invalid or out of bounds
     */
    public void markTask(String command) throws NelsonException {
        String indexStr = command.trim();
        if (indexStr.isEmpty()) {
            throw new NelsonException("Molo! Out of bounds! That task number doesn't exist on this board.");
        }
        int taskNumber = Integer.parseInt(indexStr);
        int taskIndex = taskNumber - 1;
        if (taskIndex < 0 || taskIndex >= tasks.size()) {
            throw new NelsonException("Molo! Out of bounds! That task number doesn't exist on this board.");
        }
        Task task = tasks.get(taskIndex);
        task.markAsDone();
        storage.save(tasks);
        ui.show("Molo! You completed a task? Do not celebrate. I am already calculating 15 moves ahead.");
        ui.show("[X] " + task.getDescription());
    }

    /**
     * Marks the task specified in an unmark command as incomplete.
     *
     * @param command the complete command entered by the user
     * @throws NelsonException if index is invalid or out of bounds
     */
    public void unmarkTask(String command) throws NelsonException {
        String indexStr = command.trim();
        if (indexStr.isEmpty()) {
            throw new NelsonException("Molo! Out of bounds! That task number doesn't exist on this board.");
        }
        int taskNumber = Integer.parseInt(indexStr);
        int taskIndex = taskNumber - 1;
        if (taskIndex < 0 || taskIndex >= tasks.size()) {
            throw new NelsonException("Molo! Out of bounds! That task number doesn't exist on this board.");
        }
        Task task = tasks.get(taskIndex);
        task.markAsNotDone();
        storage.save(tasks);
        ui.show("Molo! Taking back your move? Absolute blunder. Marked as not done yet:");
        ui.show("[ ] " + task.getDescription());
    }

    /**
     * Deletes the task specified by the user using ArrayList collection methods.
     *
     * @param command the complete delete command
     * @throws NelsonException if index is invalid or out of bounds
     */
    public void deleteTask(String command) throws NelsonException {
        String indexStr = command.trim();
        if (indexStr.isEmpty()) {
            throw new NelsonException("Molo! Out of bounds! That task number doesn't exist on this board.");
        }
        int taskNumber = Integer.parseInt(indexStr);
        int taskIndex = taskNumber - 1;
        if (taskIndex < 0 || taskIndex >= tasks.size()) {
            throw new NelsonException("Molo! Out of bounds! That task number doesn't exist on this board.");
        }
        Task removedTask = tasks.remove(taskIndex);
        storage.save(tasks);
        ui.show("Molo! Sweeping your mistakes under the rug already? Fine, I've banished this blunder:");
        ui.show("  " + removedTask);
        ui.show("Now you have " + tasks.size() + " tasks in the list.");
    }

    /**
     * Adds a typed task and displays Nelson's task-addition response.
     *
     * @param task the task to add
     */
    public void addTypedTask(Task task) {
        tasks.add(task);
        storage.save(tasks);
        ui.show(getAdditionMessage(task));
        ui.show("  " + task);
        ui.show("Now you have " + tasks.size() + " tasks in the list.");
    }

    /**
     * Returns Nelson's type-specific response when adding a task.
     *
     * @param task the task being added
     * @return the appropriate task-addition message
     */
    public String getAdditionMessage(Task task) {
        if (task instanceof Todo) {
            return "Molo! Another thoughtless move? Fine. I have added this trivial task:";
        }
        if (task instanceof Deadline) {
            return "Molo! Running out of time on your clock? Pathetic. I have added this task:";
        }
        return "Molo! Booking out time just to blunder? Typical. I have added this task:";
    }
}
