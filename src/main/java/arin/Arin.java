package arin;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.stream.Collectors;

import arin.command.ExitCommand;
import arin.command.SortCommand;
import arin.exception.ArinException;
import arin.parser.TaskParser;
import arin.storage.Storage;
import arin.task.Task;
import arin.ui.Ui;

/**
 * Coordinates a shared command session for the graphical and console interfaces.
 */
public class Arin {
    private final Storage storage;
    private final List<Task> tasks = new ArrayList<>();
    private String startupError;
    private boolean isExit;

    /**
     * Starts a session using the default task file.
     */
    public Arin() {
        this(new Storage());
    }

    /**
     * Loads a session, blocking changes if the original data cannot be read safely.
     *
     * @param storage Storage used for this session.
     */
    public Arin(Storage storage) {
        this.storage = storage;
        try {
            tasks.addAll(storage.loadTasks());
        } catch (IOException e) {
            startupError = e.getMessage() + "\nYour file is unchanged. Back it up, fix it, and restart Arin.";
        }
    }

    /**
     * Starts Arin and processes commands from standard input.
     *
     * @param args Command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        Arin arin = new Arin();
        System.out.println(arin.getWelcome());
        try (Scanner scanner = new Scanner(System.in)) {
            while (!arin.isExit() && scanner.hasNextLine()) {
                System.out.println(arin.getResponse(scanner.nextLine()));
            }
        }
    }

    public boolean isExit() {
        return isExit;
    }

    /**
     * Returns startup instructions or an actionable data-loading error.
     *
     * @return Text to show before the first command.
     */
    public String getWelcome() {
        if (startupError != null) {
            return "Oops! " + startupError;
        }
        return "Hello, I'm Arin, your task companion!\nLoaded " + tasks.size()
                + " tasks. Try list, todo read a book, or help to get started.";
    }

    /**
     * Runs one command and returns feedback suitable for either interface.
     *
     * @param input Command entered by the user.
     * @return Success feedback or an actionable error message.
     */
    public String getResponse(String input) {
        ByteArrayOutputStream response = new ByteArrayOutputStream();
        try (PrintStream output = new PrintStream(response, true, StandardCharsets.UTF_8)) {
            Ui ui = new Ui(new Scanner(""), output, false);
            try {
                if (isExit) {
                    throw new ArinException("This session has ended. Restart Arin to continue.");
                }
                execute(input == null ? "" : input.trim(), ui);
            } catch (ArinException e) {
                ui.showError(e.getMessage());
            } catch (IOException e) {
                ui.showError("Could not save tasks. No change was applied. Check the data folder's permissions "
                        + "and available disk space, then retry.\nDetails: " + e.getMessage());
            }
        }
        return response.toString(StandardCharsets.UTF_8).stripTrailing();
    }

    /**
     * Dispatches read-only commands before checking whether saved data is safe to change.
     */
    private void execute(String command, Ui ui) throws ArinException, IOException {
        String commandName = command.split(" ", 2)[0];
        switch (commandName) {
        case "bye":
            requireNoArguments(command, commandName);
            new ExitCommand().execute(tasks, ui, storage);
            isExit = true;
            break;
        case "help":
            requireNoArguments(command, commandName);
            ui.showHelp();
            break;
        case "list":
            requireNoArguments(command, commandName);
            requireLoadedData();
            ui.showTaskList(tasks);
            break;
        case "find":
            requireLoadedData();
            String keyword = command.substring(commandName.length()).trim();
            if (keyword.isEmpty()) {
                throw new ArinException("Use: find <keyword>.");
            }
            ui.showMatchingTasks(findTasks(tasks, keyword));
            break;
        default:
            requireLoadedData();
            executeChange(command, commandName, ui);
        }
    }

    /**
     * Dispatches changes, confirming them only after storage succeeds.
     */
    private void executeChange(String command, String commandName, Ui ui) throws ArinException, IOException {
        switch (commandName) {
        case "sort":
            new SortCommand(command.substring(commandName.length()).trim()).execute(tasks, ui, storage);
            break;
        case "mark":
        case "unmark":
            changeStatus(getTaskNumber(command, commandName, tasks.size()), commandName.equals("mark"), ui);
            break;
        case "delete":
            List<Task> remainingTasks = new ArrayList<>(tasks);
            Task removedTask = remainingTasks.remove(getTaskNumber(command, commandName, tasks.size()));
            saveChanges(remainingTasks);
            ui.showDeletedTask(removedTask, tasks.size());
            break;
        default:
            Task task = TaskParser.parse(command);
            List<Task> addedTasks = new ArrayList<>(tasks);
            addedTasks.add(task);
            saveChanges(addedTasks);
            ui.showAddedTask(task, tasks.size());
        }
    }

    /**
     * Restores the old completion state if persisting a status change fails.
     */
    private void changeStatus(int index, boolean shouldMark, Ui ui) throws IOException {
        Task task = tasks.get(index);
        boolean wasDone = task.isDone();
        setStatus(task, shouldMark);
        try {
            storage.saveTasks(tasks);
        } catch (IOException e) {
            setStatus(task, wasDone);
            throw e;
        }
        if (shouldMark) {
            ui.showMarkedTask(task);
        } else {
            ui.showUnmarkedTask(task);
        }
    }

    private void setStatus(Task task, boolean isDone) {
        if (isDone) {
            task.markTask();
        } else {
            task.unmarkTask();
        }
    }

    private void saveChanges(List<Task> updatedTasks) throws IOException {
        storage.saveTasks(updatedTasks);
        tasks.clear();
        tasks.addAll(updatedTasks);
    }

    private void requireLoadedData() throws ArinException {
        if (startupError != null) {
            throw new ArinException(startupError);
        }
    }

    private void requireNoArguments(String command, String commandName) throws ArinException {
        if (!command.equals(commandName)) {
            throw new ArinException("Use: " + commandName + " (no extra arguments).");
        }
    }

    /**
     * Returns tasks whose descriptions contain the keyword, ignoring letter case.
     *
     * @param tasks Tasks to search.
     * @param keyword Keyword to find in task descriptions.
     * @return Tasks whose descriptions contain the keyword.
     */
    static List<Task> findTasks(List<Task> tasks, String keyword) {
        String lowercaseKeyword = keyword.toLowerCase(Locale.ROOT);
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase(Locale.ROOT).contains(lowercaseKeyword))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Validates and converts the task number in a command.
     *
     * @param command Command entered by the user.
     * @param commandName Command's name.
     * @param numberOfTasks Number of tasks currently stored.
     * @return Zero-based index of the requested task.
     * @throws ArinException If the task number is missing, invalid, or out of range.
     */
    private static int getTaskNumber(String command, String commandName, int numberOfTasks)
            throws ArinException {
        String numberText = command.substring(commandName.length()).trim();
        try {
            int taskNumber = Integer.parseInt(numberText);
            if (taskNumber < 1 || taskNumber > numberOfTasks) {
                throw new ArinException("There is no task numbered " + numberText + ".");
            }
            return taskNumber - 1;
        } catch (NumberFormatException e) {
            throw new ArinException("Use: " + commandName + " <task number>.");
        }
    }
}
