package ubis;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handles saving and loading task data to and from the local storage file.
 */
public class Storage {
    /**
     * Contains tasks loaded from storage and an optional user-facing warning.
     */
    static class LoadResult {
        private final TaskList taskList;
        private final String warning;

        LoadResult(TaskList taskList, String warning) {
            this.taskList = taskList;
            this.warning = warning;
        }

        TaskList getTaskList() {
            return taskList;
        }

        String getWarning() {
            return warning;
        }
    }

    static final Path SAVE_PATH = Paths.get("data", "data.txt");
    private static final Pattern STORAGE_FIELD_PATTERN = Pattern.compile("\\{([^{}]*)}");

    /**
     * Saves the task list into local file storage.
     *
     * @param taskList Task list to be saved.
     * @return True if the task list was saved successfully, false otherwise.
     */
    public static boolean save(TaskList taskList) {
        return save(taskList, SAVE_PATH);
    }

    /**
     * Saves the task list to a specified path. This overload allows storage failures to be tested safely.
     *
     * @param taskList Task list to be saved.
     * @param savePath File to save the task list to.
     * @return True if the task list was saved successfully, false otherwise.
     */
    static boolean save(TaskList taskList, Path savePath) {
        if (!verifyAndCreatePath(savePath)) {
            System.out.println("Failed to verify or create path. Aborting save.");
            return false;
        }

        Path temporaryFile = null;
        try {
            Path directory = getDirectoryPath(savePath);
            temporaryFile = Files.createTempFile(directory, "data", ".tmp");
            Files.writeString(temporaryFile, taskList.toString());
            replaceSaveFile(temporaryFile, savePath);
            return true;
        } catch (IOException | SecurityException e) {
            System.out.println("Failed to write to save path: " + e);
            return false;
        } finally {
            if (temporaryFile != null) {
                try {
                    Files.deleteIfExists(temporaryFile);
                } catch (IOException | SecurityException e) {
                    System.out.println("Failed to remove temporary save file: " + e);
                }
            }
        }
    }

    /**
     * Replaces the save file atomically when supported by the file system.
     *
     * @param temporaryFile Fully written temporary file.
     * @param savePath Destination save file.
     * @throws IOException If the temporary file cannot be moved into place.
     */
    private static void replaceSaveFile(Path temporaryFile, Path savePath) throws IOException {
        try {
            Files.move(temporaryFile, savePath,
                    StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(temporaryFile, savePath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Loads and returns the task list from local file storage.
     *
     * @return Task list loaded from storage, or an empty task list if loading fails.
     */
    public static TaskList load() {
        return loadWithReport().getTaskList();
    }

    /**
     * Loads tasks from a specified path. This overload allows storage failures to be tested safely.
     *
     * @param savePath File to load tasks from.
     * @return Task list loaded from storage, or an empty task list if loading fails.
     */
    static TaskList load(Path savePath) {
        return loadWithReport(savePath).getTaskList();
    }

    /**
     * Loads tasks and returns any warning that should be shown when the application starts.
     *
     * @return Loaded tasks and an optional user-facing warning.
     */
    static LoadResult loadWithReport() {
        return loadWithReport(SAVE_PATH);
    }

    /**
     * Loads tasks and records a user-facing warning for inaccessible or malformed data.
     *
     * @param savePath File to load tasks from.
     * @return Loaded tasks and an optional user-facing warning.
     */
    static LoadResult loadWithReport(Path savePath) {
        TaskList tasks = new TaskList();

        if (!verifyAndCreatePath(savePath)) {
            System.out.println("Failed to verify or create path. Aborting load.");
            return new LoadResult(tasks, "Ubis could not access the task data file. "
                    + "It started with an empty task list.");
        }

        try {
            List<String> lines = Files.readAllLines(savePath);
            int malformedRecordCount = 0;

            for (int index = 0; index < lines.size(); index++) {
                Task task = parseStoredTask(lines.get(index), index + 1);
                if (task != null) {
                    tasks.addTask(task);
                } else {
                    malformedRecordCount++;
                }
            }
            if (malformedRecordCount > 0) {
                String recordDescription = malformedRecordCount == 1 ? "record" : "records";
                return new LoadResult(tasks, "Ubis skipped " + malformedRecordCount
                        + " invalid saved task " + recordDescription + ". Valid tasks were loaded normally.");
            }
        } catch (IOException | SecurityException e) {
            System.out.println("Error reading file: " + e);
            return new LoadResult(tasks, "Ubis could not read the task data file. "
                    + "It started with an empty task list.");
        }

        return new LoadResult(tasks, null);
    }

    /**
     * Parses one storage record while rejecting stray text and malformed field delimiters.
     *
     * @param line Storage record to parse.
     * @param lineNumber One-based source line number used in diagnostics.
     * @return Parsed task, or null if the record is malformed.
     */
    private static Task parseStoredTask(String line, int lineNumber) {
        List<String> fields = new ArrayList<>();
        Matcher matcher = STORAGE_FIELD_PATTERN.matcher(line);
        int parsedUntil = 0;

        while (matcher.find()) {
            if (matcher.start() != parsedUntil) {
                printMalformedRecordMessage(lineNumber);
                return null;
            }
            fields.add(matcher.group(1));
            parsedUntil = matcher.end();
        }

        if (parsedUntil != line.length() || fields.isEmpty()) {
            printMalformedRecordMessage(lineNumber);
            return null;
        }

        Task task = Task.initialise(fields.toArray(String[]::new));
        if (task == null) {
            printMalformedRecordMessage(lineNumber);
        }
        return task;
    }

    /**
     * Reports that a storage record was skipped without exposing its potentially unsafe contents.
     *
     * @param lineNumber One-based line number of the malformed record.
     */
    private static void printMalformedRecordMessage(int lineNumber) {
        System.out.println("Skipping malformed task data on line " + lineNumber + ".");
    }

    /**
     * Checks if the save directory and file exist, creating them if missing.
     *
     * @return True if the file and directory are valid and accessible, false otherwise.
     */
    private static boolean verifyAndCreatePath(Path savePath) {
        Path directoryPath = getDirectoryPath(savePath);

        // First check if directory "./data" exists, try to create it if not.
        if (!Files.exists(directoryPath)) {
            try {
                Files.createDirectories(directoryPath);
            } catch (IOException | SecurityException e) {
                System.out.println("Could not create directory for saving data: " + e);
                return false;
            }
        } else if (!Files.isDirectory(directoryPath)) {
            System.out.println(directoryPath + " is not a directory.");
            return false;
        }

        // Now directory exists, check if file exists. Create it if it doesn't
        if (!Files.exists(savePath)) {
            try {
                Files.createFile(savePath);
            } catch (IOException | SecurityException e) {
                System.out.println("Couldn't create data.txt file: " + e);
                return false;
            }
        } else if (!Files.isRegularFile(savePath)) {
            System.out.println(savePath + " is not a regular file.");
            return false;
        }

        return true;
    }

    /**
     * Returns the directory containing the save file, or the working directory for a bare file name.
     *
     * @param savePath Save file path.
     * @return Directory containing the save file.
     */
    private static Path getDirectoryPath(Path savePath) {
        Path parent = savePath.getParent();
        return parent == null ? Paths.get(".") : parent;
    }
}
