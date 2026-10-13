package duke;

import java.nio.file.Path;

/**
 * Runs Wangsa's command loop in the terminal.
 *
 * <p>{@link Parser} reads commands and {@link TaskService} saves task changes.
 * This class connects those operations to the console and handles errors.</p>
 */
public class Wangsa {
    private final TaskRepository repository;

    private final Parser parser;

    private final Ui ui;

    private final AiHelper aiHelper;

    /**
     * Creates Wangsa with console interaction and a text save file at the supplied path.
     *
     * @param filePath Save-file location.
     */
    public Wangsa(Path filePath) {
        this(new Storage(filePath), new Parser(), new Ui());
    }

    /**
     * Creates Wangsa with supplied collaborators, allowing isolated testing.
     */
    Wangsa(TaskRepository repository, Parser parser, Ui ui) {
        this(repository, parser, ui, new AiHelper());
    }

    /**
     * Supplies an optional AI helper for deterministic console tests.
     */
    Wangsa(TaskRepository repository, Parser parser, Ui ui, AiHelper aiHelper) {
        this.repository = repository;
        this.parser = parser;
        this.ui = ui;
        this.aiHelper = aiHelper;
    }

    /**
     * Runs the command loop until the user exits, input ends, or storage fails.
     */
    public void run() {
        try (ui) {
            ui.showWelcome();

            TaskService tasks;
            try {
                tasks = new TaskService(repository, parser);
            } catch (StorageException | WangsaException exception) {
                ui.showError(exception.getMessage());
                ui.showLine();
                return;
            }

            runCommands(tasks);
        }
    }

    /**
     * Processes terminal input, recovering from invalid commands and stopping after an exit or save failure.
     */
    private void runCommands(TaskService tasks) {
        while (ui.hasNextCommand()) {
            String command = ui.readCommand();
            ui.showLine();
            try {
                if (executeCommand(command, tasks)) {
                    ui.showLine();
                    return;
                }
            } catch (WangsaException exception) {
                ui.showError(exception.getMessage());
            } catch (StorageException exception) {
                ui.showError(exception.getMessage());
                ui.showLine();
                return;
            }
            ui.showLine();
        }
    }

    /**
     * Executes one command and returns whether it requests an exit.
     *
     * <p>The switch selects presentation behavior, while all state-changing work is
     * delegated to the shared service.</p>
     */
    private boolean executeCommand(String command, TaskService tasks)
            throws WangsaException, StorageException {
        Command parsedCommand = parser.parse(command);
        switch (parsedCommand.type()) {
            case BYE -> {
                ui.showGoodbye();
                return true;
            }
            case LIST -> ui.showTaskList(tasks.getTasks());
            case HELP -> ui.showMessage(CommandHelp.getText());
            case AI -> ui.showMessage(aiHelper.ask(((Command.AiQuestion) parsedCommand).question()));
            case FIND -> ui.showMatchingTasks(tasks.findMatches(((Command.Search) parsedCommand).keyword()));
            case SORT -> sortTasks(tasks);
            case MARK, UNMARK -> updateTaskStatus((Command.TaskNumber) parsedCommand, tasks);
            case DELETE -> deleteTask((Command.TaskNumber) parsedCommand, tasks);
            case ADD_TASK -> addTask((Command.AddTask) parsedCommand, tasks);
            default -> throw new IllegalStateException("Unsupported command type: " + parsedCommand.type());
        }
        return false;
    }

    /**
     * Updates a task's completion status and displays confirmation after saving.
     */
    private void updateTaskStatus(Command.TaskNumber command, TaskService tasks)
            throws WangsaException, StorageException {
        boolean isMarked = command.type() == Parser.CommandType.MARK;
        Task updatedTask = isMarked ? tasks.mark(command.taskNumber()) : tasks.unmark(command.taskNumber());
        ui.showTaskStatusUpdate(updatedTask, isMarked);
    }

    /**
     * Removes a task and displays confirmation after saving.
     */
    private void deleteTask(Command.TaskNumber command, TaskService tasks) throws WangsaException, StorageException {
        Task removedTask = tasks.delete(command.taskNumber());
        ui.showTaskDeleted(removedTask, tasks.getTasks().size());
    }

    /**
     * Adds a task and displays confirmation after saving.
     */
    private void addTask(Command.AddTask command, TaskService tasks) throws WangsaException, StorageException {
        Task task = tasks.add(command.task());
        ui.showTaskAdded(task, tasks.getTasks().size());
    }

    /**
     * Sorts tasks by deadline and displays the saved order.
     */
    private void sortTasks(TaskService tasks) throws StorageException {
        tasks.sortByDeadline();
        ui.showSortedTaskList(tasks.getTasks());
    }

    /**
     * Starts Wangsa using the save file in the current working folder.
     *
     * @param args Command-line arguments (unused).
     */
    public static void main(String[] args) {
        new Wangsa(SaveLocation.getDefaultFile()).run();
    }
}
