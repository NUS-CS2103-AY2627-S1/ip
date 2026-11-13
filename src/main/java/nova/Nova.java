package nova;

import nova.command.Command;
import nova.exception.NovaException;
import nova.parser.Parser;
import nova.storage.Storage;
import nova.task.Task;
import nova.task.TaskList;
import nova.ui.Ui;

/**
 * Runs the Nova task-management chatbot.
 */
public class Nova {
    private final Parser parser;
    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;

    /**
     * Creates a Nova instance backed by the specified data file.
     *
     * @param filePath Path of the file used to store tasks.
     */
    public Nova(String filePath) {
        parser = new Parser();
        storage = new Storage(filePath);
        ui = new Ui();

        TaskList loadedTasks;
        try {
            loadedTasks = new TaskList(storage.load());
        } catch (NovaException exception) {
            ui.showError(exception.getMessage());
            loadedTasks = new TaskList();
        }
        tasks = loadedTasks;
    }

    /**
     * Starts the command-processing loop for the text interface.
     */
    public void run() {
        ui.showResponse(ui.getWelcomeMessage());
        while (ui.hasNextCommand()) {
            String input = ui.readCommand();
            Command command = parser.parseCommand(input);
            ui.showResponse(getResponse(command, input));
            if (command == Command.BYE) {
                return;
            }
        }
    }

    /**
     * Processes one user input and returns the response to display.
     *
     * @param input Full user input.
     * @return Nova's response to the input.
     */
    public String getResponse(String input) {
        Command command = parser.parseCommand(input);
        return getResponse(command, input);
    }

    private String getResponse(Command command, String input) {
        try {
            return execute(command, input);
        } catch (NovaException exception) {
            return ui.getErrorMessage(exception.getMessage());
        }
    }

    private String execute(Command command, String input) throws NovaException {
        return switch (command) {
            case BYE -> ui.getGoodbyeMessage();
            case LIST -> ui.getTaskListMessage(tasks);
            case FIND -> ui.getMatchingTasksMessage(tasks.find(parser.parseFindKeyword(input)));
            case SORT -> sortTasks();
            case MARK -> markTask(input);
            case UNMARK -> unmarkTask(input);
            case DELETE -> deleteTask(input);
            case TODO, DEADLINE, EVENT -> addTask(input, command);
            default -> throw new NovaException("I'm sorry, but I don't know what that means.");
        };
    }

    private String markTask(String input) throws NovaException {
        Task task = tasks.mark(parser.parseTaskNumber(input, "mark", tasks.size()));
        storage.save(tasks.getTasksView());
        return ui.getMarkedMessage(task);
    }

    private String unmarkTask(String input) throws NovaException {
        Task task = tasks.unmark(parser.parseTaskNumber(input, "unmark", tasks.size()));
        storage.save(tasks.getTasksView());
        return ui.getUnmarkedMessage(task);
    }

    private String deleteTask(String input) throws NovaException {
        Task task = tasks.delete(parser.parseTaskNumber(input, "delete", tasks.size()));
        storage.save(tasks.getTasksView());
        return ui.getDeletedMessage(task, tasks.size());
    }

    private String addTask(String input, Command command) throws NovaException {
        Task task = parser.parseTask(input, command);
        tasks.add(task);
        storage.save(tasks.getTasksView());
        return ui.getAddedMessage(task, tasks.size());
    }

    private String sortTasks() throws NovaException {
        tasks.sortByDescription();
        storage.save(tasks.getTasksView());
        return "I've sorted your tasks alphabetically:\n" + ui.getTaskListMessage(tasks);
    }

    /**
     * Starts Nova using the default data-file location.
     *
     * @param args Command-line arguments; not used.
     */
    public static void main(String[] args) {
        new Nova("data/nova.txt").run();
    }
}
