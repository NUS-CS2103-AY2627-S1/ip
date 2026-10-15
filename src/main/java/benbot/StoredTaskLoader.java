package benbot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

/** Loads previously stored commands from the application's data file. */
public class StoredTaskLoader {
    /** The location of the stored task commands, relative to the working directory. */
    private static final Path DEFAULT_STORED_TASK_PATH = Path.of("data/stored-task");

    /** Commands that may occur in the application's stored-data format. */
    private static final Set<String> STORAGE_COMMANDS = Set.of(
            "todo", "deadline", "event", "contact", "note", "expense", "mark");

    /** The location from which this loader reads task commands. */
    private final Path storedTaskPath;

    /** Creates a loader for task commands stored in the application's data file. */
    public StoredTaskLoader() {
        this(DEFAULT_STORED_TASK_PATH);
    }

    /**
     * Creates a loader for task commands stored at the supplied path.
     *
     * @param storedTaskPath the file that contains the stored task commands.
     */
    StoredTaskLoader(Path storedTaskPath) {
        this.storedTaskPath = storedTaskPath;
    }

    /**
     * Loads each command in the stored-task file into the supplied task list.
     * Loading is silent: commands are processed but their normal responses are not displayed.
     * If any command is invalid, no stored command is applied to the live data.
     *
     * @param taskLoader processes each stored command.
     * @param tasks the task array to populate.
     * @param taskCountPointer holds the current number of stored tasks.
     * @return {@code true} if loading completed or no storage file exists; {@code false} otherwise.
     */
    public boolean load(TaskLoader taskLoader, Task[] tasks, int[] taskCountPointer) {
        if (Files.notExists(storedTaskPath)) {
            System.out.println("No stored data found starting from scratch...");
            System.out.println(BenBot.DIVIDER);
            return true;
        }

        try {
            int loadedItemCount = loadStoredItems(taskLoader, tasks, taskCountPointer);
            printLoadedItemCount(loadedItemCount);
            return true;
        } catch (IOException e) {
            printLoadError(e);
            return false;
        }
    }

    /** Reads and validates the stored commands, returning the number of items added. */
    private int loadStoredItems(TaskLoader taskLoader, Task[] tasks, int[] taskCountPointer)
            throws IOException {
        String storedData = Files.readString(storedTaskPath);
        String[] commands = storedData.split("\\R");
        int initialTaskCount = taskCountPointer[0];
        validateStoredCommands(commands, taskLoader, tasks, initialTaskCount);

        int itemCountBeforeLoading = taskLoader.getStoredItemCount(initialTaskCount);
        loadStoredCommands(commands, taskLoader, tasks, taskCountPointer, initialTaskCount);
        return taskLoader.getStoredItemCount(taskCountPointer[0]) - itemCountBeforeLoading;
    }

    /** Validates every stored command without changing the application's live data. */
    private void validateStoredCommands(String[] commands, TaskLoader taskLoader,
                                        Task[] tasks, int taskCount) throws IOException {
        TaskLoader stagedTaskLoader = new TaskLoader(taskLoader.getMaxTasks());
        Task[] stagedTasks = new Task[taskLoader.getMaxTasks()];
        int[] stagedTaskCount = {0};
        copyLiveState(taskLoader, tasks, taskCount,
                stagedTaskLoader, stagedTasks, stagedTaskCount);
        loadStoredCommands(commands, stagedTaskLoader, stagedTasks, stagedTaskCount, taskCount);
    }

    /** Copies the current state into a temporary command processor used only for validation. */
    private void copyLiveState(TaskLoader taskLoader, Task[] tasks, int taskCount,
                               TaskLoader stagedTaskLoader, Task[] stagedTasks,
                               int[] stagedTaskCount) throws IOException {
        for (int i = 0; i < taskCount; i++) {
            stagedTaskLoader.addTask(
                    tasks[i].toStorageString(), stagedTasks, stagedTaskCount, false);
        }
        if (stagedTaskCount[0] != taskCount) {
            throw createLiveStateValidationException();
        }

        for (String command : taskLoader.getExtensionStorageCommands()) {
            stagedTaskLoader.addTask(command, stagedTasks, stagedTaskCount, false);
        }
        if (stagedTaskLoader.getStoredItemCount(stagedTaskCount[0])
                != taskLoader.getStoredItemCount(taskCount)) {
            throw createLiveStateValidationException();
        }

        for (int i = 0; i < taskCount; i++) {
            if (tasks[i].isDone()) {
                stagedTaskLoader.addTask(
                        "mark " + (i + 1), stagedTasks, stagedTaskCount, false);
            }
        }

        for (int i = 0; i < taskCount; i++) {
            if (stagedTasks[i].isDone() != tasks[i].isDone()) {
                throw createLiveStateValidationException();
            }
        }
    }

    /** Creates the error used when live data cannot be copied faithfully for validation. */
    private IOException createLiveStateValidationException() {
        return new IOException(
                "Existing BenBot data could not be validated safely before loading stored data.");
    }

    /** Loads each non-blank stored command into the supplied isolated or live state. */
    private void loadStoredCommands(String[] commands, TaskLoader taskLoader,
                                    Task[] tasks, int[] taskCountPointer, int initialTaskCount)
            throws IOException {
        for (int i = 0; i < commands.length; i++) {
            String command = commands[i].strip();
            if (command.isEmpty()) {
                continue;
            }
            loadStoredCommand(
                    command, i + 1, taskLoader, tasks, taskCountPointer, initialTaskCount);
        }
    }

    /** Loads one command or reports its line as corrupted when it does not change valid state. */
    private void loadStoredCommand(String command, int lineNumber, TaskLoader taskLoader,
                                   Task[] tasks, int[] taskCountPointer, int initialTaskCount)
            throws IOException {
        if (!isStorageCommand(command)) {
            throw createCorruptionException(lineNumber);
        }

        int taskCountBefore = taskCountPointer[0];
        int itemCountBefore = taskLoader.getStoredItemCount(taskCountBefore);
        int storedMarkedTaskIndex = getMarkedTaskIndex(
                command, taskCountBefore - initialTaskCount);
        int markedTaskIndex = storedMarkedTaskIndex < 0
                ? -1
                : initialTaskCount + storedMarkedTaskIndex;
        boolean wasMarkedTaskDone = markedTaskIndex >= 0 && tasks[markedTaskIndex].isDone();
        String executableCommand = markedTaskIndex >= 0
                ? "mark " + (markedTaskIndex + 1)
                : command;

        taskLoader.addTask(executableCommand, tasks, taskCountPointer, false);

        boolean wasItemAdded = taskLoader.getStoredItemCount(taskCountPointer[0]) == itemCountBefore + 1;
        boolean wasTaskMarked = markedTaskIndex >= 0
                && !wasMarkedTaskDone && tasks[markedTaskIndex].isDone();
        if (wasItemAdded || wasTaskMarked) {
            return;
        }
        throw createCorruptionException(lineNumber);
    }

    /** Returns whether a line begins with a command used by the stored-data format. */
    private boolean isStorageCommand(String command) {
        String commandWord = command.split("\\s+", 2)[0];
        return STORAGE_COMMANDS.contains(commandWord);
    }

    /** Creates the detailed error reported for an invalid stored-data line. */
    private IOException createCorruptionException(int lineNumber) {
        return new IOException("Stored-task file is corrupted at line " + lineNumber
                + ". Each line must be a valid todo, deadline, event, contact, note, expense, "
                + "or mark command.");
    }

    /** Displays a stable explanation while retaining any useful file-system details. */
    private void printLoadError(IOException exception) {
        String details = exception.getMessage();
        if (details == null || details.isBlank()) {
            details = "The stored-data file could not be read.";
        }
        System.out.println("ERROR: Unable to load stored data. " + details);
        System.out.println("The data already in BenBot was not changed.");
        System.out.println(
                "The existing storage file will not be overwritten. Fix it before restarting BenBot.");
        System.out.println(BenBot.DIVIDER);
    }

    private void printLoadedItemCount(int loadedItemCount) {
        String itemWord = loadedItemCount == 1 ? "item" : "items";
        System.out.println(loadedItemCount + " " + itemWord + " already in storage.");
        System.out.println(BenBot.DIVIDER);
    }

    /** Returns the zero-based task index in a stored mark command, or -1 when it is invalid. */
    private int getMarkedTaskIndex(String command, int taskCount) {
        String[] words = command.trim().split("\\s+");
        if (words.length != 2 || !words[0].equals("mark")) {
            return -1;
        }

        try {
            if (!words[1].chars().allMatch(Character::isDigit)) {
                return -1;
            }
            int taskNumber = Integer.parseInt(words[1]);
            return taskNumber >= 1 && taskNumber <= taskCount ? taskNumber - 1 : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
