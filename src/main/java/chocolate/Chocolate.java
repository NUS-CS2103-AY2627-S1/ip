package chocolate;

import java.io.IOException;
import java.nio.file.Path;

import chocolate.exception.ChocolateException;
import chocolate.parser.ParsedCommand;
import chocolate.parser.Parser;
import chocolate.storage.Storage;
import chocolate.task.Task;
import chocolate.task.TaskList;
import chocolate.ui.Ui;

/**
 * Coordinates parsing, task management, storage, and user interaction.
 */
public class Chocolate {
    private final Storage storage;
    private final Ui ui;
    private final TaskList tasks;

    /**
     * Creates Chocolate and loads tasks from the configured relative data path.
     *
     * @param filePath Relative path of the task data file.
     */
    public Chocolate(String filePath) {
        this.ui = new Ui();
        this.storage = new Storage(Path.of(filePath));
        TaskList loadedTasks;
        try {
            loadedTasks = new TaskList(storage.load());
        } catch (IOException e) {
            loadedTasks = new TaskList();
            ui.showLoadingError();
        }
        this.tasks = loadedTasks;
    }

    /**
     * Runs the command loop until the user enters the bye command.
     */
    public void run() {
        ui.showWelcome();
        boolean isExit = false;

        while (!isExit) {
            String input = ui.readCommand();
            ui.showLine();
            ui.showResponse(getResponse(input));
            isExit = input.equals("bye");
        }
    }

    /**
     * Processes one user command and returns Chocolate's reply.
     *
     * @param input Complete command entered by the user.
     * @return A response suitable for either the console or GUI.
     */
    public String getResponse(String input) {
        try {
            return execute(Parser.parse(input));
        } catch (ChocolateException e) {
            return ui.getErrorMessage(e.getMessage());
        } catch (IOException e) {
            return ui.getErrorMessage("I could not save your tasks to the hard disk.");
        }
    }

    /**
     * Executes one parsed command.
     *
     * @param command Parsed command to execute.
     * @return Response to show the user.
     * @throws ChocolateException If the command refers to an invalid task.
     * @throws IOException If an updated task list cannot be saved.
     */
    private String execute(ParsedCommand command) throws ChocolateException, IOException {
        switch (command.getType()) {
            case BYE:
                return ui.getGoodbyeMessage();
            case LIST:
                return ui.getTaskListMessage(tasks);
            case MARK:
                tasks.mark(command.getTaskIndex());
                storage.save(tasks);
                return ui.getMarkedMessage(tasks.get(command.getTaskIndex()));
            case UNMARK:
                tasks.unmark(command.getTaskIndex());
                storage.save(tasks);
                return ui.getUnmarkedMessage(tasks.get(command.getTaskIndex()));
            case TODO:
                // Fallthrough
            case DEADLINE:
                // Fallthrough
            case EVENT:
                if (tasks.contains(command.getTask())) {
                    throw new ChocolateException("That task is already on your list!");
                }
                tasks.add(command.getTask());
                storage.save(tasks);
                return ui.getAddedMessage(command.getTask(), tasks.size());
            case DELETE:
                Task deletedTask = tasks.delete(command.getTaskIndex());
                storage.save(tasks);
                return ui.getDeletedMessage(deletedTask, tasks.size());
            case FIND:
                return ui.getMatchingTasksMessage(tasks.find(command.getKeyword()));
            case ARCHIVE:
                if (tasks.size() == 0) {
                    return ui.getNoTasksToArchiveMessage();
                }
                int archivedTaskCount = tasks.size();
                storage.archive(tasks);
                return ui.getArchivedMessage(archivedTaskCount);
            case ARCHIVE_LIST:
                return ui.getArchivedTaskListMessage(storage.loadArchived());
            default:
                throw new ChocolateException("Unable to execute the command.");
        }
    }

    /**
     * Starts Chocolate using the default task data file.
     *
     * @param args Command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        new Chocolate("data/duke.txt").run();
    }
}
