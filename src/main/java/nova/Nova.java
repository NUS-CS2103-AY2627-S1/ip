package nova;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Runs the Nova chatbot.
 */
public class Nova {

    /**
     * Contains Nova's response text and whether it represents an error.
     *
     * @param message Response text.
     * @param isError Whether the response represents an error.
     */
    public record Response(String message, boolean isError) {
    }

    private final Storage storage;
    private final TaskList tasks;
    private final String loadingError;

    /**
     * Creates a Nova instance that stores tasks at the specified file path.
     *
     * @param filePath Path to the storage file.
     */
    public Nova(String filePath) {
        storage = new Storage(Path.of(filePath));

        TaskList loadedTasks;
        String errorMessage = "";
        try {
            loadedTasks = storage.load();
            if (storage.hasCorruptedEntries()) {
                errorMessage = "Some saved tasks were invalid and could not be loaded.";
            }
        } catch (IOException e) {
            errorMessage = "Unable to load saved tasks.";
            loadedTasks = new TaskList();
        }

        tasks = loadedTasks;
        loadingError = errorMessage;
    }

    /**
     * Runs Nova until the user exits.
     */
    public void run() {
        Ui ui = new Ui();
        ui.showResponse(getWelcomeMessage());

        boolean isRunning = true;
        while (isRunning) {
            String input = ui.readCommand();
            ui.showResponse(getResponse(input));
            isRunning = !input.equals("bye");
        }

        ui.close();
    }

    /**
     * Returns Nova's greeting and any error encountered while loading tasks.
     *
     * @return Initial message for the user.
     */
    public String getWelcomeMessage() {
        String welcomeMessage = Ui.formatWelcomeMessage();
        if (loadingError.isEmpty()) {
            return welcomeMessage;
        }
        return loadingError + System.lineSeparator() + welcomeMessage;
    }

    /**
     * Processes one user command and returns Nova's response.
     *
     * @param input User command.
     * @return Nova's response.
     */
    public String getResponse(String input) {
        return getResponseResult(input).message();
    }

    /**
     * Processes one user command and returns its text and error status.
     *
     * @param input User command.
     * @return Nova's response and whether it represents an error.
     */
    public Response getResponseResult(String input) {
        try {
            Parser.ParsedCommand command = Parser.parse(input);
            if (command.getType() == Parser.CommandType.UNKNOWN) {
                return new Response(Ui.formatUnknownCommand(), true);
            }
            return new Response(executeCommand(command), false);
        } catch (IllegalArgumentException e) {
            return new Response(e.getMessage(), true);
        } catch (IOException e) {
            return new Response("Unable to save tasks.", true);
        }
    }

    /**
     * Executes a parsed command using the current task list and storage.
     *
     * @param command Parsed user command.
     * @return Nova's response to the command.
     * @throws IOException If the updated task list cannot be saved.
     */
    private String executeCommand(Parser.ParsedCommand command)
            throws IOException {
        switch (command.getType()) {
            case BYE:
                return Ui.formatByeMessage();
            case LIST:
                return Ui.formatTaskList(tasks);
            case MARK:
                return markTask(command.getTaskNumber());
            case DELETE:
                return deleteTask(command.getTaskNumber());
            case TODO:
                return addTask(Task.todo(command.getDescription()));
            case UPDATE:
                return updateTask(command.getTaskNumber(), command.getDescription());
            case DEADLINE:
                return addTask(Task.deadline(
                        command.getDescription(),
                        command.getDeadline()));
            case EVENT:
                return addTask(Task.event(
                        command.getDescription(),
                        command.getFrom(),
                        command.getTo()));
            case FIND:
                List<Task> matches = tasks.find(command.getDescription());
                return Ui.formatMatchingTasks(matches);
            case UNKNOWN:
                return Ui.formatUnknownCommand();
            default:
                return "";
        }
    }

    /**
     * Adds and saves a task before returning its confirmation message.
     *
     * @param task Task to add.
     * @return Task-added confirmation.
     * @throws IOException If the updated task list cannot be saved.
     */
    private String addTask(Task task) throws IOException {
        TaskList updatedTasks = tasks.copy();
        updatedTasks.add(task);
        saveChanges(updatedTasks);
        return Ui.formatAddedTask(task, updatedTasks.size());
    }

    private String markTask(int taskNumber) throws IOException {
        TaskList updatedTasks = tasks.copy();
        updatedTasks.markDone(taskNumber);
        saveChanges(updatedTasks);
        return Ui.formatMarkedMessage();
    }

    private String deleteTask(int taskNumber) throws IOException {
        TaskList updatedTasks = tasks.copy();
        Task removedTask = updatedTasks.delete(taskNumber);
        saveChanges(updatedTasks);
        return Ui.formatDeletedTask(removedTask, updatedTasks.size());
    }

    private String updateTask(int taskNumber, String description) throws IOException {
        TaskList updatedTasks = tasks.copy();
        Task updatedTask = updatedTasks.update(taskNumber, description);
        saveChanges(updatedTasks);
        return Ui.formatUpdatedTask(updatedTask);
    }

    private void saveChanges(TaskList updatedTasks) throws IOException {
        storage.save(updatedTasks);
        tasks.replaceWith(updatedTasks);
    }

    /**
     * Starts Nova using the default storage file.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        new Nova("data/nova.txt").run();
    }
}
