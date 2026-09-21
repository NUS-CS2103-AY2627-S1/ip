package melodie;

import java.io.IOException;
import java.time.format.DateTimeParseException;
import java.util.List;

import melodie.command.Command;
import melodie.command.ParsedCommand;
import melodie.command.ParsedUpdate;
import melodie.command.Parser;
import melodie.storage.Storage;
import melodie.task.Task;
import melodie.task.TaskList;
import melodie.ui.Ui;

/**
 * Coordinates the components of the Melodie chatbot.
 */
public class Melodie {
    /** Product and chatbot name shown in every user interface. */
    public static final String NAME = "Melodie";

    /** Opening message that introduces Melodie's musical-conductor personality. */
    public static final String GREETING_MESSAGE = "Hello! I'm Melodie, your task conductor ♪\n"
            + "What masterpiece shall we play?";

    private static final String FAREWELL_MESSAGE =
            "Rhapsodically accomplished! Come play with me again ♪";
    private static final String LOADING_ERROR_MESSAGE =
            "I couldn't load your saved tasks, so we're starting with an empty score.";
    private static final String SAVING_ERROR_MESSAGE =
            "I couldn't save that change. Please check the data folder and try again.";

    private final Ui ui;
    private final Storage storage;
    private final Parser parser;
    private TaskList tasks;
    private boolean hasLoadingError;
    private boolean isExitRequested;
    private boolean wasLastResponseError;

    /**
     * Creates a Melodie chatbot with its user interface, storage, parser, and task list.
     */
    public Melodie() {
        this.ui = new Ui();
        this.storage = new Storage();
        this.parser = new Parser();
        this.tasks = new TaskList();
        this.loadTasks();
    }

    /**
     * Runs the chatbot and processes commands until the user exits.
     */
    public void run() {
        this.ui.showIntro();
        if (this.hasLoadingError) {
            this.ui.showResponse(LOADING_ERROR_MESSAGE);
        }

        while (!this.isExitRequested) {
            String response = this.getResponse(this.ui.readCommand());
            this.ui.showLine();
            this.ui.showResponse(response);
            this.ui.showLine();
        }

        this.ui.close();
    }

    /**
     * Returns the greeting displayed when the graphical interface starts.
     *
     * @return Greeting and, when applicable, a saved-data loading warning.
     */
    public String getGreeting() {
        if (this.hasLoadingError) {
            return GREETING_MESSAGE + "\n\n" + LOADING_ERROR_MESSAGE;
        }
        return GREETING_MESSAGE;
    }

    /**
     * Processes one command and returns the response for any user interface to display.
     *
     * @param input Command entered by the user.
     * @return Melodie's response, including validation and saving errors.
     */
    public String getResponse(String input) {
        this.isExitRequested = false;
        this.wasLastResponseError = false;
        try {
            ParsedCommand parsedCommand = this.parser.parse(input);
            if (parsedCommand.getCommand() == Command.BYE) {
                this.isExitRequested = true;
                return FAREWELL_MESSAGE;
            }
            return this.executeCommand(parsedCommand);
        } catch (MelodieException e) {
            this.wasLastResponseError = true;
            return e.getMessage();
        } catch (IOException e) {
            this.wasLastResponseError = true;
            return SAVING_ERROR_MESSAGE;
        }
    }

    /**
     * Reports whether the most recently processed command requested that Melodie close.
     *
     * @return {@code true} after a valid {@code bye} command.
     */
    public boolean isExitRequested() {
        return this.isExitRequested;
    }

    /**
     * Reports whether the most recently processed command produced an error response.
     *
     * @return {@code true} when the latest response describes a command or storage error.
     */
    public boolean wasLastResponseError() {
        return this.wasLastResponseError;
    }

    /**
     * Loads saved tasks from storage into the task list.
     * If loading fails, an empty task list is used instead.
     */
    private void loadTasks() {
        try {
            this.tasks = new TaskList(this.storage.read());
        } catch (IOException | DateTimeParseException e) {
            this.hasLoadingError = true;
            this.tasks = new TaskList();
        }
    }

    /**
     * Executes a parsed command and updates storage when the task list changes.
     *
     * @param parsedCommand Command and arguments to execute.
     * @return Response describing the result of executing the command.
     * @throws MelodieException If the command or its arguments are invalid.
     * @throws IOException If a change to the task list cannot be saved.
     */
    private String executeCommand(ParsedCommand parsedCommand) throws MelodieException, IOException {
        switch (parsedCommand.getCommand()) {
            case MARK:
                return this.markTask(parsedCommand.getArguments());
            case UNMARK:
                return this.unmarkTask(parsedCommand.getArguments());
            case DELETE:
                return this.deleteTask(parsedCommand.getArguments());
            case UPDATE:
                return this.updateTask(parsedCommand.getArguments());
            case TODO, DEADLINE, EVENT:
                return this.addTask(parsedCommand);
            case LIST:
                return this.getTaskListMessage();
            case FIND:
                return this.findTasks(parsedCommand.getArguments());
            default:
                throw new MelodieException("That command isn't in my score yet.");
        }
    }

    private String markTask(String arguments) throws MelodieException, IOException {
        Task markedTask = this.tasks.mark(this.getValidTaskIndex(arguments));
        this.storage.write(this.tasks);
        return "Bravo! That task is complete ♪\n" + markedTask;
    }

    private String unmarkTask(String arguments) throws MelodieException, IOException {
        Task unmarkedTask = this.tasks.unmark(this.getValidTaskIndex(arguments));
        this.storage.write(this.tasks);
        return "Back on the set list — we'll rehearse it again.\n" + unmarkedTask;
    }

    private String deleteTask(String arguments) throws MelodieException, IOException {
        Task deletedTask = this.tasks.delete(this.getValidTaskIndex(arguments));
        this.storage.write(this.tasks);
        return "That task has left the set list ♪\n"
                + deletedTask + "\n"
                + this.getTaskCountMessage();
    }

    private String updateTask(String arguments) throws MelodieException, IOException {
        ParsedUpdate update = this.parser.parseUpdate(arguments);
        int taskIndex = update.getTaskIndex();
        this.validateTaskIndex(taskIndex);

        Task updatedTask = this.parser.parseUpdatedTask(this.tasks.get(taskIndex), update);
        this.tasks.update(taskIndex, updatedTask);
        this.storage.write(this.tasks);
        return "That task is back in tune ♪\n" + updatedTask;
    }

    private String addTask(ParsedCommand parsedCommand) throws MelodieException, IOException {
        Task task = this.parser.parseTask(parsedCommand);
        this.tasks.add(task);
        this.storage.write(this.tasks);
        return "Added to today's set list ♪\n"
                + task + "\n"
                + this.getTaskCountMessage();
    }

    private String findTasks(String arguments) throws MelodieException {
        String keyword = this.parser.parseFindKeyword(arguments);
        return this.getMatchingTasksMessage(this.tasks.find(keyword));
    }

    private int getValidTaskIndex(String arguments) throws MelodieException {
        int taskIndex = this.parser.parseTaskIndex(arguments);
        this.validateTaskIndex(taskIndex);
        return taskIndex;
    }

    private void validateTaskIndex(int taskIndex) throws MelodieException {
        if (!this.tasks.isValidIndex(taskIndex)) {
            throw new MelodieException(
                    "That task number isn't on the set list. Please try another number.");
        }
    }

    private String getTaskCountMessage() {
        int taskCount = this.tasks.size();
        String taskWord = taskCount == 1 ? "task" : "tasks";
        return "Your set list now holds " + taskCount + " " + taskWord + ".";
    }

    private String getTaskListMessage() {
        if (this.tasks.isEmpty()) {
            return "Your set list is quiet. Add a task when you're ready ♪";
        }

        StringBuilder response = new StringBuilder("Here is today's set list ♪");
        for (int i = 0; i < this.tasks.size(); i++) {
            response.append("\n").append(i + 1).append(". ").append(this.tasks.get(i));
        }
        return response.toString();
    }

    private String getMatchingTasksMessage(List<Task> matchingTasks) {
        if (matchingTasks.isEmpty()) {
            return "No tasks in the score match that search.";
        }

        StringBuilder response = new StringBuilder("These tasks match your search ♪");
        for (int i = 0; i < matchingTasks.size(); i++) {
            response.append("\n").append(i + 1).append(". ").append(matchingTasks.get(i));
        }
        return response.toString();
    }

    /**
     * Starts the Melodie chatbot.
     *
     * @param args Command-line arguments; not used.
     */
    public static void main(String[] args) {
        new Melodie().run();
    }
}
