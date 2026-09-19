package fox;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import fox.exception.FoxException;
import fox.parser.TaskParser;
import fox.storage.Storage;
import fox.task.Task;
import fox.task.TaskList;
import fox.ui.FoxUi;

/** Runs Fox and coordinates input, domain operations, persistence, and presentation. */
public class Fox {
    private static final int MAX_TASKS = 100;
    private final Storage storage;
    private final TaskParser taskParser;
    private final FoxUi ui;
    private TaskList taskList;
    private boolean isStorageUsable = true;
    private boolean isTaskListLoaded;

    /** Creates a Fox application using the default task storage. */
    public Fox() {
        this(new Storage());
    }

    /**
     * Creates a Fox application with injected storage.
     *
     * @param storage the storage used to load and save tasks; must not be {@code null}
     */
    public Fox(Storage storage) {
        this.storage = storage;
        this.taskParser = new TaskParser();
        this.ui = new FoxUi();
        this.taskList = new TaskList(MAX_TASKS);
    }

    /**
     * Starts Fox's command loop and processes commands until input ends or the user says {@code bye}.
     *
     * @param scanner the input source for Fox commands; must not be {@code null}
     */
    public void run(Scanner scanner) {
        try {
            loadTasks();
        } catch (FoxException exception) {
            ui.showError(exception.getMessage());
        }
        isTaskListLoaded = true;
        System.out.print("  /\\_/\\\n ( •ᴗ• )   Hi! I'm Fox, your little companion. 🦊\n"
                + "  > ^ <    I may be small, but I've got plenty of tricks up my sleeve.\n\n"
                + "           What can I do for you?\n");
        System.out.println(FoxUi.SEPARATOR);
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine().trim();
            if (command.isEmpty()) {
                continue;
            }
            try {
                if (getCommandName(command).equalsIgnoreCase("bye")) {
                    rejectArguments(command, "bye");
                    System.out.println(FoxUi.SEPARATOR);
                    System.out.print("  /\\_/\\\n ( -.- )   Bye for now! 🌙\n"
                            + "  > ^ <    I'm off to the fox den. Wake me up anytime you're in need!\n");
                    System.out.println(FoxUi.SEPARATOR);
                    return;
                }
                execute(command, ui);
            } catch (FoxException exception) {
                ui.showError(exception.getMessage());
            }
        }
    }

    /** Loads persisted tasks, retaining an empty in-memory list if storage is unavailable. */
    private void loadTasks() throws FoxException {
        try {
            taskList = new TaskList(MAX_TASKS, storage.load());
            isStorageUsable = true;
        } catch (Storage.StorageException | FoxException exception) {
            isStorageUsable = false;
            throw new FoxException("☹ OOPS!!! Fox could not load your tasks safely. "
                    + exception.getMessage());
        }
    }

    /** Dispatches one non-empty command to the appropriate domain and UI operations. */
    private void execute(String command, FoxUi responseUi) throws FoxException {
        assert command != null && !command.isBlank() : "Command must contain non-whitespace characters";
        assert responseUi != null : "Response UI must be initialized";

        String commandName = getCommandName(command);
        if (commandName.equalsIgnoreCase("list")) {
            rejectArguments(command, "list");
            responseUi.showTasks(taskList);
        } else if (commandName.equalsIgnoreCase("find")) {
            String[] keywords = parseFindKeywords(command);
            responseUi.showFoundTasks(taskList, taskList.findMatchingTaskNumbers(keywords));
        } else if (commandName.equalsIgnoreCase("mark")) {
            ensureStorageUsable();
            Task markedTask = taskList.markDone(parseTaskNumber(command, "mark"));
            saveTasks();
            responseUi.showMarked(markedTask, true);
        } else if (commandName.equalsIgnoreCase("unmark")) {
            ensureStorageUsable();
            Task unmarkedTask = taskList.markNotDone(parseTaskNumber(command, "unmark"));
            saveTasks();
            responseUi.showMarked(unmarkedTask, false);
        } else if (commandName.equalsIgnoreCase("delete")) {
            ensureStorageUsable();
            Task deleted = taskList.delete(parseTaskNumber(command, "delete"));
            saveTasks();
            responseUi.showDeleted(deleted, taskList.size());
        } else {
            ensureStorageUsable();
            Task task = taskParser.parse(commandName, command);
            taskList.add(task);
            saveTasks();
            responseUi.showAdded(task, taskList.size());
        }
    }

    /**
     * Processes one GUI command and returns the same formatted response as the console UI.
     *
     * @param command the command entered by the user
     * @return Fox's formatted response, or an empty string for blank input
     */
    public String getResponse(String command) {
        if (command == null || command.trim().isEmpty()) {
            return "";
        }
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        FoxUi responseUi = new FoxUi(new PrintStream(output));
        String trimmedCommand = command.trim();
        try {
            if (!isTaskListLoaded) {
                loadTasks();
                isTaskListLoaded = true;
            }
            if (getCommandName(trimmedCommand).equalsIgnoreCase("bye")) {
                rejectArguments(trimmedCommand, "bye");
                responseUi.showError("Bye for now! Fox is closing.");
            } else {
                execute(trimmedCommand, responseUi);
            }
        } catch (FoxException exception) {
            responseUi.showError(exception.getMessage());
        }
        return output.toString();
    }

    /** Extracts and validates the one-based task number required by a task action. */
    private int parseTaskNumber(String command, String action) throws FoxException {
        String[] parts = command.split("\\s+");
        if (parts.length != 2) {
            throw new FoxException("☹ OOPS!!! Use `" + action
                    + " <task number>` with exactly one task number.");
        }
        try {
            int taskNumber = Integer.parseInt(parts[1]);
            if (taskNumber < 1) {
                throw new FoxException("☹ OOPS!!! The task number must be a positive whole number.");
            }
            return taskNumber;
        } catch (NumberFormatException exception) {
            throw new FoxException("☹ OOPS!!! The task number must be a positive whole number.");
        }
    }

    /** Extracts the one or more keywords required by a find command. */
    private String[] parseFindKeywords(String command) throws FoxException {
        String[] commandParts = command.split("\\s+", 2);
        if (commandParts.length < 2 || commandParts[1].isBlank()) {
            throw new FoxException("☹ OOPS!!! Please provide at least one keyword to find.");
        }
        return commandParts[1].trim().split("\\s+");
    }

    /** Persists the current task list when storage is still usable. */
    private void saveTasks() throws FoxException {
        ensureStorageUsable();
        try {
            storage.save(taskList.toArray(), taskList.size());
        } catch (Storage.StorageException exception) {
            isStorageUsable = false;
            throw new FoxException("☹ OOPS!!! Fox could not save your tasks. "
                    + "Your latest changes remain available for this session only. "
                    + exception.getMessage());
        }
    }

    /** Returns the first word of a non-empty command. */
    private String getCommandName(String command) {
        return command.split("\\s+", 2)[0];
    }

    /** Rejects arguments supplied to a command that accepts none. */
    private void rejectArguments(String command, String commandName) throws FoxException {
        if (command.split("\\s+", 2).length > 1) {
            throw new FoxException("☹ OOPS!!! The " + commandName + " command does not accept arguments.");
        }
    }

    /** Prevents changes when Fox cannot load or save the task file safely. */
    private void ensureStorageUsable() throws FoxException {
        if (!isStorageUsable) {
            throw new FoxException("☹ OOPS!!! Fox cannot change tasks because its data file is unavailable.");
        }
    }

    /**
     * Starts Fox using standard input.
     *
     * @param args command-line arguments; currently ignored
     */
    public static void main(String[] args) {
        new Fox().run(new Scanner(System.in));
    }
}
