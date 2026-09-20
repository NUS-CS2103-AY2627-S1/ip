package larper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

import larper.command.Parser;
import larper.exception.LarperException;
import larper.exception.NoTaskTypeException;
import larper.storage.Storage;
import larper.task.FindResult;
import larper.task.Task;
import larper.task.TaskList;
import larper.ui.Ui;

/**
 * Coordinates Larper's user interface, command parser, task list, and storage.
 */
public class Larper {
    private static final String DATA_PATH_PROPERTY = "larper.data.path";
    private static final Path DEFAULT_DATA_PATH = Path.of("data", "larperdata.txt");
    private static final String HELP_COMMAND = "help";
    private static final String[] KNOWN_COMMANDS = {
        "todo", "deadline", "event", "list", "mark", "unmark", "delete", "find", "tag", "untag",
        HELP_COMMAND, "exit"
    };

    private Ui ui;
    private Parser parser;
    private Storage storage;
    private TaskList tasks;
    private ArrayList<Integer> skippedDataLineNumbers;
    private boolean isWaitingForFindPhrase;

    /**
     * Creates a Larper chatbot that stores tasks at the specified data path.
     *
     * @param dataPath Path of the task data file.
     */
    public Larper(Path dataPath) {
        ui = new Ui();
        parser = new Parser();
        storage = new Storage(dataPath);
        tasks = loadTasks();
        assert ui != null : "Ui should be initialized before Larper handles commands.";
        assert parser != null : "Parser should be initialized before Larper handles commands.";
        assert storage != null : "Storage should be initialized before Larper handles commands.";
        assert tasks != null : "Task list should always be available after loading.";
    }

    /**
     * Runs the main input loop until the user exits or the input stream ends.
     */
    public void run() {
        ui.showWelcome();
        showDataWarningIfNeeded();
        while (ui.hasNextInput()) {
            String input = ui.readInput();
            ui.showLine();
            LarperResponse response = getResponse(input);
            ui.showMessage(response.getMessage());
            if (response.isExit()) {
                break;
            }
        }

        ui.close();
    }

    /**
     * Returns Larper's welcome message for non-console user interfaces.
     */
    public String getWelcomeMessage() {
        String welcomeMessage = Ui.formatWelcomeMessage().stripTrailing();
        if (skippedDataLineNumbers.isEmpty()) {
            return welcomeMessage;
        }
        return welcomeMessage + "\n\n" + formatSkippedDataLinesMessage();
    }

    /**
     * Returns the current tasks as display strings for non-console user interfaces.
     *
     * @return A snapshot of the task list in its current order.
     */
    public ArrayList<String> getTaskSummaries() {
        ArrayList<String> taskSummaries = new ArrayList<>();
        for (int taskNumber = 1; taskNumber <= tasks.size(); taskNumber++) {
            taskSummaries.add(tasks.getTask(taskNumber).toString());
        }
        return taskSummaries;
    }

    /**
     * Returns Larper's response to one user command.
     *
     * @param input User command to handle.
     * @return Larper's response message and session status.
     */
    public LarperResponse getResponse(String input) {
        assert input != null : "Command input should be an empty string instead of null.";
        if (parser.isExitCommand(input)) {
            return new LarperResponse(Ui.formatExitMessage(), true);
        }

        try {
            return executeCommand(input);
        } catch (NoTaskTypeException e) {
            isWaitingForFindPhrase = false;
            return createErrorResponse(formatUnknownCommand(input, e.getMessage()));
        } catch (LarperException e) {
            isWaitingForFindPhrase = false;
            return new LarperResponse(e.getMessage(), false, true);
        }
    }

    private LarperResponse executeCommand(String input) throws LarperException {
        if (isWaitingForFindPhrase) {
            return createResponse(executeFindPhrase(input));
        }
        if (parser.isListCommand(input)) {
            return createResponse(Ui.formatTaskList(tasks));
        }
        if (parser.isFindTagCommand(input)) {
            return createResponse(executeFindTagCommand(input));
        }
        if (parser.isFindCommand(input)) {
            return createResponse(executeFindCommand());
        }
        if (input.equals(HELP_COMMAND)) {
            return createResponse(Ui.formatHelpMessage());
        }
        if (parser.isTagCommand(input)) {
            return executeTagCommand(input, false);
        }
        if (parser.isUntagCommand(input)) {
            return executeTagCommand(input, true);
        }
        if (parser.isMarkCommand(input)) {
            return executeMarkCommand(input);
        }
        if (parser.isUnmarkCommand(input)) {
            return executeUnmarkCommand(input);
        }
        if (parser.isDeleteCommand(input)) {
            return createResponse(executeDeleteCommand(input));
        }
        return createResponse(executeAddCommand(input));
    }

    private String executeFindPhrase(String input) throws LarperException {
        ArrayList<FindResult> results = tasks.findTasks(input);
        isWaitingForFindPhrase = false;
        return Ui.formatFindResults(results);
    }

    private String executeFindCommand() {
        isWaitingForFindPhrase = true;
        return Ui.formatFindPrompt();
    }

    private String executeFindTagCommand(String input) throws LarperException {
        String tag = parser.parseFindTag(input);
        ArrayList<FindResult> results = tasks.findTasksByTag(tag);
        return Ui.formatFindResults(results);
    }

    private LarperResponse executeTagCommand(String input, boolean shouldRemove) throws LarperException {
        int taskNumber = parser.parseTagTaskNumber(input);
        if (taskNumber == -1) {
            return createErrorResponse(Ui.formatInvalidTagNumber());
        }
        if (!tasks.hasTaskNumber(taskNumber)) {
            return createErrorResponse(Ui.formatMissingTaskNumber());
        }

        ArrayList<String> tagNames = parser.parseTagNames(input);
        TaskList updatedTasks = tasks.copy();
        Task updatedTask = shouldRemove
                ? updatedTasks.untagTask(taskNumber, tagNames)
                : updatedTasks.tagTask(taskNumber, tagNames);
        saveTasks(updatedTasks);
        tasks = updatedTasks;
        String message = shouldRemove
                ? Ui.formatUntaggedTask(taskNumber, updatedTask)
                : Ui.formatTaggedTask(taskNumber, updatedTask);
        return createResponse(message);
    }

    private LarperResponse executeMarkCommand(String input) throws LarperException {
        int taskNumber = parser.parseMarkNumber(input);
        if (taskNumber == -1) {
            return createErrorResponse(Ui.formatInvalidMarkNumber());
        } else if (!tasks.hasTaskNumber(taskNumber)) {
            return createErrorResponse(Ui.formatMissingTaskNumber());
        }

        assert tasks.hasTaskNumber(taskNumber) : "Mark command should only run after task number validation.";
        TaskList updatedTasks = tasks.copy();
        Task markedTask = updatedTasks.markTask(taskNumber);
        assert markedTask.isDone() : "Marked task should be done after markTask succeeds.";
        saveTasks(updatedTasks);
        tasks = updatedTasks;
        return createResponse(Ui.formatMarkedTask(markedTask));
    }

    private LarperResponse executeUnmarkCommand(String input) throws LarperException {
        int taskNumber = parser.parseUnmarkNumber(input);
        if (taskNumber == -1) {
            return createErrorResponse(Ui.formatInvalidUnmarkNumber());
        } else if (!tasks.hasTaskNumber(taskNumber)) {
            return createErrorResponse(Ui.formatMissingTaskNumber());
        }

        assert tasks.hasTaskNumber(taskNumber) : "Unmark command should only run after task number validation.";
        TaskList updatedTasks = tasks.copy();
        Task unmarkedTask = updatedTasks.unmarkTask(taskNumber);
        assert !unmarkedTask.isDone() : "Unmarked task should not be done after unmarkTask succeeds.";
        saveTasks(updatedTasks);
        tasks = updatedTasks;
        return createResponse(Ui.formatUnmarkedTask(unmarkedTask));
    }

    private String executeDeleteCommand(String input) throws LarperException {
        int taskNumber = parser.parseDeleteNumber(input);
        TaskList updatedTasks = tasks.copy();
        Task removedTask = updatedTasks.deleteTask(taskNumber);
        saveTasks(updatedTasks);
        tasks = updatedTasks;
        return Ui.formatDeletedTask(removedTask, updatedTasks.size());
    }

    private String executeAddCommand(String input) throws LarperException {
        Task task = parser.parseTask(input);
        TaskList updatedTasks = tasks.copy();
        updatedTasks.addTask(task);
        saveTasks(updatedTasks);
        tasks = updatedTasks;
        return Ui.formatAddedTask(task, updatedTasks.size());
    }

    /**
     * Starts Larper using the configured data path or the default local data file.
     *
     * @param args Command line arguments, which are currently unused.
     */
    public static void main(String[] args) {
        new Larper(getDataPath()).run();
    }

    /**
     * Returns the configured data path or the default local data file.
     */
    public static Path getDataPath() {
        String dataPath = System.getProperty(DATA_PATH_PROPERTY);
        if (dataPath == null || dataPath.isBlank()) {
            return DEFAULT_DATA_PATH;
        }
        return Path.of(dataPath);
    }

    private void saveTasks(TaskList tasksToSave) throws LarperException {
        assert tasksToSave != null : "Save should receive the task list state to persist.";
        try {
            storage.saveTasks(tasksToSave.getTasks());
        } catch (IOException e) {
            throw new LarperException(" Larper could not save the task list to the local data file.");
        }
    }

    private TaskList loadTasks() {
        try {
            TaskList loadedTasks = new TaskList(storage.loadTasks());
            skippedDataLineNumbers = storage.getSkippedLineNumbers();
            return loadedTasks;
        } catch (IOException e) {
            skippedDataLineNumbers = new ArrayList<>();
            return new TaskList();
        }
    }

    private void showDataWarningIfNeeded() {
        if (!skippedDataLineNumbers.isEmpty()) {
            ui.showMessage(formatSkippedDataLinesMessage());
        }
    }

    private String formatSkippedDataLinesMessage() {
        String lineNumbers = skippedDataLineNumbers.stream()
                .map(String::valueOf)
                .reduce((first, second) -> first + ", " + second)
                .orElse("");
        return " Storage reality check.\n"
                + " Larper skipped saved data line(s): " + lineNumbers + ".\n"
                + " Fix the data file before changing tasks, because saving rewrites the loaded agenda.";
    }

    private LarperResponse createResponse(String message) {
        return new LarperResponse(message, false);
    }

    private LarperResponse createErrorResponse(String message) {
        return new LarperResponse(message, false, true);
    }

    private String formatUnknownCommand(String input, String fallbackMessage) {
        assert input != null : "Unknown command formatter should receive the original input.";
        assert fallbackMessage != null : "Unknown command formatter should receive a fallback message.";
        String trimmedInput = input.trim();
        if (trimmedInput.isEmpty()) {
            return fallbackMessage;
        }

        String commandWord = trimmedInput.split("\\s+", 2)[0];
        String normalizedCommandWord = commandWord.toLowerCase();
        String suggestion = findSuggestedCommand(normalizedCommandWord);
        if (suggestion.isEmpty()) {
            return " Source: you made that command up.\n"
                    + " Unknown command: `" + commandWord + "`\n\n"
                    + " Type `help` before freelancing syntax.";
        }

        String suggestedInput = suggestion + trimmedInput.substring(commandWord.length());
        return " Minor misinformation detected.\n"
                + " Unknown command: `" + commandWord + "`\n"
                + " Did you mean `" + suggestedInput + "`?\n\n"
                + " Type `help` before freelancing syntax.";
    }

    private String findSuggestedCommand(String commandWord) {
        assert commandWord != null : "Command word should not be null.";
        String bestCommand = "";
        int bestDistance = Integer.MAX_VALUE;
        for (String knownCommand : KNOWN_COMMANDS) {
            int distance = getEditDistance(commandWord, knownCommand);
            if (distance == 0) {
                return "";
            }
            if (distance < bestDistance) {
                bestCommand = knownCommand;
                bestDistance = distance;
            }
        }
        int maximumHelpfulDistance = commandWord.length() <= 4 ? 1 : 2;
        return bestDistance <= maximumHelpfulDistance ? bestCommand : "";
    }

    private int getEditDistance(String first, String second) {
        assert first != null && second != null : "Edit distance inputs should not be null.";
        int[][] distance = new int[first.length() + 1][second.length() + 1];
        for (int i = 0; i <= first.length(); i++) {
            distance[i][0] = i;
        }
        for (int j = 0; j <= second.length(); j++) {
            distance[0][j] = j;
        }
        for (int i = 1; i <= first.length(); i++) {
            for (int j = 1; j <= second.length(); j++) {
                int substitutionCost = first.charAt(i - 1) == second.charAt(j - 1) ? 0 : 1;
                int deletionDistance = distance[i - 1][j] + 1;
                int insertionDistance = distance[i][j - 1] + 1;
                int substitutionDistance = distance[i - 1][j - 1] + substitutionCost;
                distance[i][j] = Math.min(Math.min(deletionDistance, insertionDistance), substitutionDistance);
            }
        }
        return distance[first.length()][second.length()];
    }
}
