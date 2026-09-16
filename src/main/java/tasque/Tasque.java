package tasque;

import java.util.List;

import tasque.task.Task;
import tasque.task.TaskList;

/**
 * Coordinates the user interface, command parsing, task list, and storage.
 */
public class Tasque {
    private final Parser parser;
    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;
    private final String loadErrorMessage;

    /**
     * Creates a Tasque session using the specified task data file.
     *
     * @param filePath Path of the task data file.
     */
    public Tasque(String filePath) {
        this.parser = new Parser();
        this.storage = new Storage(filePath);
        List<Task> loadedTasks;
        String loadError = null;
        try {
            loadedTasks = this.storage.loadTasks();
        } catch (TasqueException e) {
            loadedTasks = List.of();
            loadError = e.getMessage() + " Tasque has not changed the file."
                    + " Correct the file or access problem and restart Tasque. You can exit with bye or b.";
        }
        this.tasks = new TaskList(loadedTasks);
        this.loadErrorMessage = loadError;
        this.ui = new Ui();
    }

    /**
     * Runs the command loop until the user enters {@code bye}.
     */
    public void run() {
        this.ui.showWelcome(getWelcomeMessage());
        boolean shouldExit = false;
        while (!shouldExit) {
            String userInput = this.ui.readCommand();
            String response = getResponse(userInput);
            if (isExitCommand(userInput)) {
                shouldExit = true;
            } else {
                this.ui.showResponse(response);
            }
        }
        this.ui.showExit();
    }

    /**
     * Returns the startup greeting and any storage-load error shared by both interfaces.
     *
     * @return Tasque greeting, followed by the load error if this session is blocked.
     */
    public String getWelcomeMessage() {
        return getWelcomeResponse().getText();
    }

    /**
     * Returns the startup greeting with explicit storage-load error status.
     *
     * @return Formatted welcome response, including any load error.
     */
    public TasqueResponse getWelcomeResponse() {
        String greeting = this.ui.getWelcomeMessage();
        if (this.loadErrorMessage == null) {
            return new TasqueResponse(greeting, false);
        }
        return new TasqueResponse(greeting + "\n" + this.ui.getErrorMessage(this.loadErrorMessage), true);
    }

    /**
     * Processes one user command and returns its user-facing response.
     *
     * @param userInput Command entered by the user.
     * @return Response to display to the user.
     */
    public String getResponse(String userInput) {
        return getCommandResponse(userInput).getText();
    }

    /**
     * Processes one user command once and returns its text and error status.
     *
     * @param userInput Command entered by the user.
     * @return Formatted command response with explicit error status.
     */
    public TasqueResponse getCommandResponse(String userInput) {
        try {
            return new TasqueResponse(executeCommand(userInput), false);
        } catch (TasqueException e) {
            return new TasqueResponse(this.ui.getErrorMessage(e.getMessage()), true);
        }
    }

    /**
     * Checks whether the input is the exit command or its alias.
     *
     * @param userInput Command entered by the user.
     * @return Whether the interface should exit.
     */
    public boolean isExitCommand(String userInput) {
        return this.parser.normalizeCommand(userInput).equals("bye");
    }

    private String executeCommand(String rawUserInput) throws TasqueException {
        String userInput = this.parser.normalizeCommand(rawUserInput);
        if (userInput.equals("bye")) {
            return this.ui.getExitMessage();
        }
        checkStorageLoaded();
        String command = this.parser.parseCommand(userInput);
        switch (command) {
            case "list":
                return this.ui.getTaskListMessage(this.tasks.getTasks());
            case "mark":
                return markTask(userInput);
            case "unmark":
                return unmarkTask(userInput);
            case "todo":
                return addTask(this.parser.parseTodo(userInput));
            case "deadline":
                return addTask(this.parser.parseDeadline(userInput));
            case "event":
                return addTask(this.parser.parseEvent(userInput));
            case "delete":
                return deleteTask(userInput);
            case "find":
                return findTask(userInput);
            default:
                throw new TasqueException("Yeah, I don't know what that means.");
        }
    }

    private String addTask(Task task) throws TasqueException {
        if (this.tasks.hasIncompleteDuplicate(task)) {
            throw new TasqueException("Already got that one.");
        }
        this.tasks.add(task);
        try {
            saveTasks();
        } catch (TasqueException e) {
            this.tasks.delete(this.tasks.getSize());
            throw e;
        }
        return this.ui.getTaskAddedMessage(task, this.tasks.getSize());
    }

    private String deleteTask(String userInput) throws TasqueException {
        int taskNumber = this.parser.parseTaskNumber(
                userInput, "delete", this.tasks.getSize());
        Task deletedTask = this.tasks.delete(taskNumber);
        try {
            saveTasks();
        } catch (TasqueException e) {
            this.tasks.add(taskNumber, deletedTask);
            throw e;
        }
        return this.ui.getTaskDeletedMessage(deletedTask, this.tasks.getSize());
    }

    private String markTask(String userInput) throws TasqueException {
        int taskNumber = this.parser.parseTaskNumber(
                userInput, "mark", this.tasks.getSize());
        assert 1 <= taskNumber && taskNumber <= this.tasks.getSize()
                : "Check that taskNumber is within range of the task list";
        Task markedTask = updateTaskCompletion(taskNumber, true);
        assert markedTask.isDone()
                : "Check if task is actually marked as done";
        return this.ui.getTaskMarkedMessage(markedTask);
    }

    private String unmarkTask(String userInput) throws TasqueException {
        int taskNumber = this.parser.parseTaskNumber(
                userInput, "unmark", this.tasks.getSize());
        Task unmarkedTask = updateTaskCompletion(taskNumber, false);
        return this.ui.getTaskUnmarkedMessage(unmarkedTask);
    }

    private Task updateTaskCompletion(int taskNumber, boolean isDone) throws TasqueException {
        boolean wasDone = this.tasks.getTasks().get(taskNumber - 1).isDone();
        Task updatedTask = isDone
                ? this.tasks.markAsDone(taskNumber)
                : this.tasks.markAsNotDone(taskNumber);
        try {
            saveTasks();
        } catch (TasqueException e) {
            restoreCompletion(updatedTask, wasDone);
            throw e;
        }
        return updatedTask;
    }

    private void saveTasks() throws TasqueException {
        checkStorageLoaded();
        this.storage.saveTasks(this.tasks.getTasks());
    }

    private void checkStorageLoaded() throws TasqueException {
        if (this.loadErrorMessage != null) {
            throw new TasqueException(this.loadErrorMessage);
        }
    }

    private void restoreCompletion(Task task, boolean wasDone) {
        if (wasDone) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
        assert task.isDone() == wasDone
                : "Rollback must restore the previous completion state";
    }

    private String findTask(String userInput) throws TasqueException {
        String keyword = this.parser.parseFindKeyword(userInput);
        List<Task> matchingTasks = this.tasks.findTasks(keyword);
        return this.ui.getMatchingTasksMessage(matchingTasks);
    }

    /**
     * Starts Tasque with its default relative data-file path.
     *
     * @param args Command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        new Tasque("data/tasque.txt").run();
    }
}
