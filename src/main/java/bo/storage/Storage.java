package bo.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bo.model.Deadline;
import bo.model.Event;
import bo.model.Task;
import bo.model.Todo;

/**
 * Saves and loads Bo's tasks in a simple, human-readable text format.
 *
 * <p>Each save rewrites the complete task list so that deletions and status
 * changes are persisted.
 */
public final class Storage {
    /** The name of this project directory. */
    private static final String PROJECT_DIRECTORY_NAME = "ip";

    /** The file used to store tasks inside this project's data directory. */
    private static final Path TASK_FILE = resolveProjectRoot().resolve("data/duke.txt");

    private Storage() {
        // Utility class; do not instantiate.
    }

    /**
     * Finds the project root without depending on the user's parent folders or
     * the process working directory.
     *
     * <p>The working directory is checked first because it is available in
     * every launch mode. The compiled class location is checked as a fallback,
     * which supports launching Bo while the working directory is elsewhere.
     *
     * @return the absolute project root, or the current directory as a safe fallback.
     */
    private static Path resolveProjectRoot() {
        Path projectRoot = findProjectRoot(Path.of("").toAbsolutePath());
        if (projectRoot != null) {
            return projectRoot;
        }

        try {
            if (Storage.class.getProtectionDomain().getCodeSource() != null) {
                Path codeLocation = Path.of(Storage.class.getProtectionDomain()
                        .getCodeSource().getLocation().toURI());
                projectRoot = findProjectRoot(codeLocation);
                if (projectRoot != null) {
                    return projectRoot;
                }
            }
        } catch (URISyntaxException | SecurityException exception) {
            // Use the working directory below when the code location is unavailable.
        }

        return Path.of("").toAbsolutePath().normalize();
    }

    /**
     * Searches a path and its ancestors for this project root.
     *
     * @param start the path from which to begin searching.
     * @return the project root, or {@code null} if it is not an ancestor.
     */
    private static Path findProjectRoot(Path start) {
        Path current = start.toAbsolutePath().normalize();
        while (current != null) {
            Path fileName = current.getFileName();
            if (fileName != null && PROJECT_DIRECTORY_NAME.equals(fileName.toString())
                    && Files.isDirectory(current.resolve("src/main/java"))) {
                return current;
            }
            current = current.getParent();
        }
        return null;
    }

    /**
     * Writes all current tasks to the storage file.
     *
     * @param tasks the array containing Bo's tasks.
     * @param taskCount the number of valid tasks in the array.
     * @throws IOException if the directory or file cannot be written.
     * @throws IllegalArgumentException if the task array contains invalid data.
     */
    public static void save(Task[] tasks, int taskCount) throws IOException {
        validateTaskArray(tasks, taskCount);
        Path parent = TASK_FILE.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        Path temporaryFile = Files.createTempFile(parent, "duke-", ".tmp");
        try {
            try (BufferedWriter writer = Files.newBufferedWriter(
                    temporaryFile, StandardCharsets.UTF_8)) {
                for (int i = 0; i < taskCount; i++) {
                    writer.write(formatTask(tasks[i]));
                    writer.newLine();
                }
            }
            replaceTaskFile(temporaryFile);
        } finally {
            Files.deleteIfExists(temporaryFile);
        }
    }

    /**
     * Loads tasks from the storage file into the supplied array.
     *
     * <p>A missing file represents a first launch and therefore results in an
     * empty task list. Blank lines are ignored, and at most the array's
     * capacity is loaded.
     *
     * @param tasks the array into which loaded tasks are placed.
     * @return the number of tasks loaded.
     * @throws IOException if the existing file cannot be read.
     * @throws IllegalArgumentException if a stored line is malformed.
     */
    public static int load(Task[] tasks) throws IOException {
        return loadWithReport(tasks).getTaskCount();
    }

    /**
     * Loads tasks and reports malformed or excess records without abandoning
     * otherwise valid records.
     *
     * @param tasks the array into which loaded tasks are placed.
     * @return details about the records that were loaded or skipped.
     * @throws IOException if the existing file cannot be read.
     * @throws IllegalArgumentException if the task array is null.
     */
    public static LoadResult loadWithReport(Task[] tasks) throws IOException {
        validateTaskArray(tasks, 0);
        Arrays.fill(tasks, null);
        if (Files.notExists(TASK_FILE)) {
            return new LoadResult(0, 0, 0);
        }

        List<Task> loadedTasks = new ArrayList<>();
        int invalidLineCount = 0;
        int excessTaskCount = 0;
        try (BufferedReader reader = Files.newBufferedReader(
                TASK_FILE, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }

                try {
                    Task task = parseTask(line);
                    if (loadedTasks.size() < tasks.length) {
                        loadedTasks.add(task);
                    } else {
                        excessTaskCount++;
                    }
                } catch (IllegalArgumentException exception) {
                    invalidLineCount++;
                }
            }
        }

        for (int i = 0; i < loadedTasks.size(); i++) {
            tasks[i] = loadedTasks.get(i);
        }
        return new LoadResult(loadedTasks.size(), invalidLineCount, excessTaskCount);
    }

    /**
     * Converts a task to the line format used by the storage file.
     *
     * @param task the task to format.
     * @return one storage line for the task.
     */
    private static String formatTask(Task task) {
        String status = task.isDone() ? "1" : "0";
        if (task instanceof Deadline deadline) {
            return "D | " + status + " | " + escape(task.getDescription())
                    + " | " + escape(deadline.getStoredBy());
        }
        if (task instanceof Event event) {
            return "E | " + status + " | " + escape(task.getDescription())
                    + " | " + escape(event.getStoredFrom()) + " | " + escape(event.getStoredTo());
        }
        return "T | " + status + " | " + escape(task.getDescription());
    }

    /**
     * Converts one storage line into a task object.
     *
     * @param line a serialized task line.
     * @return the task represented by the line.
     */
    private static Task parseTask(String line) {
        List<String> parts = splitFields(line);
        if (parts.size() < 3) {
            throw new IllegalArgumentException("A task line has too few fields.");
        }

        String type = parts.get(0);
        String status = parts.get(1);
        if (!status.equals("0") && !status.equals("1")) {
            throw new IllegalArgumentException("A task status must be 0 or 1.");
        }

        Task task;
        switch (type) {
        case "T":
            if (parts.size() != 3 || parts.get(2).isBlank()) {
                throw new IllegalArgumentException("A todo line has invalid fields.");
            }
            task = new Todo(parts.get(2));
            break;
        case "D":
            if (parts.size() != 4 || parts.get(2).isBlank() || parts.get(3).isBlank()) {
                throw new IllegalArgumentException("A deadline line has invalid fields.");
            }
            task = new Deadline(parts.get(2), parts.get(3));
            break;
        case "E":
            if (parts.size() != 5 || parts.get(2).isBlank()
                    || parts.get(3).isBlank() || parts.get(4).isBlank()) {
                throw new IllegalArgumentException("An event line has invalid fields.");
            }
            task = new Event(parts.get(2), parts.get(3), parts.get(4));
            break;
        default:
            throw new IllegalArgumentException("A task line has an unknown type.");
        }

        if (status.equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Escapes characters that have a special meaning in the storage format.
     *
     * @param value the field to escape.
     * @return the escaped field.
     */
    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("|", "\\|");
    }

    /**
     * Splits a storage line while honoring escaped separators.
     *
     * @param line the serialized task line.
     * @return the unescaped fields in the line.
     */
    private static List<String> splitFields(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean escaped = false;
        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);
            if (escaped) {
                if (character != '\\' && character != '|') {
                    throw new IllegalArgumentException("A task line has an invalid escape.");
                }
                field.append(character);
                escaped = false;
            } else if (character == '\\') {
                escaped = true;
            } else if (character == '|') {
                fields.add(field.toString().strip());
                field.setLength(0);
            } else {
                field.append(character);
            }
        }
        if (escaped) {
            throw new IllegalArgumentException("A task line has an incomplete escape.");
        }
        fields.add(field.toString().strip());
        return fields;
    }

    /**
     * Replaces the task file with a fully written temporary file.
     *
     * @param temporaryFile the completed temporary file.
     * @throws IOException if the replacement cannot be completed.
     */
    private static void replaceTaskFile(Path temporaryFile) throws IOException {
        try {
            Files.move(temporaryFile, TASK_FILE,
                    StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(temporaryFile, TASK_FILE, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Checks arguments shared by storage operations.
     *
     * @param tasks the task array.
     * @param taskCount the number of valid tasks.
     */
    private static void validateTaskArray(Task[] tasks, int taskCount) {
        if (tasks == null) {
            throw new IllegalArgumentException("The task array cannot be null.");
        }
        if (taskCount < 0 || taskCount > tasks.length) {
            throw new IllegalArgumentException("The task count is outside the task array.");
        }
        for (int i = 0; i < taskCount; i++) {
            validateTask(tasks[i]);
        }
    }

    /**
     * Checks that a task contains the fields required by the storage format.
     *
     * @param task the task to validate.
     */
    private static void validateTask(Task task) {
        if (task == null || task.getDescription() == null || task.getDescription().isBlank()) {
            throw new IllegalArgumentException("A task must have a non-blank description.");
        }
        if (task instanceof Deadline deadline
                && (deadline.getStoredBy() == null || deadline.getStoredBy().isBlank())) {
            throw new IllegalArgumentException("A deadline must have a non-blank date.");
        }
        if (task instanceof Event event
                && (event.getStoredFrom() == null || event.getStoredFrom().isBlank()
                || event.getStoredTo() == null || event.getStoredTo().isBlank())) {
            throw new IllegalArgumentException("An event must have non-blank times.");
        }
    }

    /**
     * Describes the outcome of loading the task file.
     */
    public static final class LoadResult {
        /** Number of valid tasks loaded. */
        private final int taskCount;

        /** Number of malformed non-empty lines skipped. */
        private final int invalidLineCount;

        /** Number of valid tasks skipped because the task array was full. */
        private final int excessTaskCount;

        /**
         * Creates a load result.
         *
         * @param taskCount number of valid tasks loaded.
         * @param invalidLineCount number of malformed lines skipped.
         * @param excessTaskCount number of excess valid tasks skipped.
         */
        private LoadResult(int taskCount, int invalidLineCount, int excessTaskCount) {
            this.taskCount = taskCount;
            this.invalidLineCount = invalidLineCount;
            this.excessTaskCount = excessTaskCount;
        }

        /**
         * Returns the number of tasks loaded.
         *
         * @return number of loaded tasks.
         */
        public int getTaskCount() {
            return taskCount;
        }

        /**
         * Returns the number of malformed lines skipped.
         *
         * @return number of skipped malformed lines.
         */
        public int getInvalidLineCount() {
            return invalidLineCount;
        }

        /**
         * Returns the number of valid tasks skipped due to capacity.
         *
         * @return number of skipped excess tasks.
         */
        public int getExcessTaskCount() {
            return excessTaskCount;
        }
    }
}
