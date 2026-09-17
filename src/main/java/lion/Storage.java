package lion;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Saves tasks to disk and restores them between application runs.
 *
 * <p>All members are static; this class is not meant to be instantiated.
 */
public final class Storage {
    private static final String DEFAULT_DIRECTORY_PATH = "data";

    // Not final: setDirectoryPathForTesting() below redirects this to a temporary
    // directory so tests can exercise save()/load() without touching the
    // application's real save file.
    private static String directoryPath = DEFAULT_DIRECTORY_PATH;

    private Storage() {
    }

    /**
     * Redirects where tasks are saved to and loaded from, for use only by tests that
     * must not touch the application's real save file.
     *
     * @param testDirectoryPath directory to use instead of the default, or {@code null}
     *     to restore the default.
     */
    static void setDirectoryPathForTesting(String testDirectoryPath) {
        directoryPath = testDirectoryPath != null ? testDirectoryPath : DEFAULT_DIRECTORY_PATH;
    }

    /**
     * Returns the save file's path within the current directory (see
     * {@link #setDirectoryPathForTesting(String)}).
     *
     * @return path to the save file.
     */
    private static String filePath() {
        return directoryPath + File.separator + "lion.txt";
    }

    /**
     * Writes the active tasks to the application's data file.
     *
     * @param tasks tasks to save.
     * @throws IOException if the data directory or file cannot be written.
     */
    public static void save(List<Task> tasks) throws IOException {
        assert tasks != null : "task list must not be null";

        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Built as one string, rather than writing each line separately, so that
        // the only place in this method that can throw IOException is the single
        // write() call below; encode() itself never throws.
        String content = tasks.stream()
                .map(task -> encode(task) + System.lineSeparator())
                .collect(Collectors.joining());

        try (FileWriter writer = new FileWriter(filePath())) {
            writer.write(content);
        }
    }

    /**
     * Loads all tasks saved in the application's data file.
     *
     * @return restored tasks, in the order they were saved, or an empty list
     *     if no save file exists.
     * @throws IOException if the save file exists but cannot be read.
     */
    public static List<Task> load() throws IOException {
        File file = new File(filePath());
        if (!file.exists()) {
            return new ArrayList<>();
        }

        List<Task> tasks = new ArrayList<>();
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.isBlank()) {
                    continue;
                }

                try {
                    tasks.add(decode(line));
                } catch (IllegalArgumentException e) {
                    // One corrupted or hand-edited line (wrong field count, unrecognized
                    // status flag, etc.) should not cost the user every other saved task,
                    // so it is skipped rather than failing the whole load.
                }
            }
        } catch (FileNotFoundException e) {
            throw new IOException("Save file not found", e);
        }
        return tasks;
    }

    /**
     * Creates a task list containing all tasks currently stored on disk.
     *
     * @return restored task list, or an empty list when no save file exists.
     * @throws IOException if the save file cannot be read.
     */
    public static TaskList loadTaskList() throws IOException {
        return new TaskList(load());
    }

    /**
     * Converts a task to the stable, machine-readable representation used in the save file.
     *
     * @param task task to encode.
     * @return one line suitable for the save file.
     */
    static String encode(Task task) {
        String prefix = task.getTypeIcon() + " | " + task.getStatusIcon()
                + " | " + task.getDescription();
        if (task instanceof Deadline) {
            return prefix + " | " + ((Deadline) task).getByForStorage();
        }
        if (task instanceof Event) {
            Event event = (Event) task;
            return prefix + " | " + event.getFrom() + " | " + event.getTo();
        }
        return prefix;
    }

    /**
     * Restores one task from its machine-readable save-file representation.
     *
     * <p>Each line is pipe-delimited as produced by {@link #encode(Task)},
     * e.g. {@code T | 1 | buy book} for a completed todo.
     *
     * <p>Validation here uses real conditional checks rather than {@code assert}, since
     * assertions are disabled by default in a normally-run/packaged build (they only run
     * under {@code -ea}, which the Gradle {@code run} task enables but a distributed jar
     * does not) — a hand-edited or corrupted line must still be rejected reliably rather
     * than being silently trusted and indexed out of bounds.
     *
     * @param line one encoded task from the save file.
     * @return restored task.
     * @throws IllegalArgumentException if the line does not have enough fields for its task type.
     */
    static Task decode(String line) {
        String[] parts = line.split("\\s*\\|\\s*", -1);
        if (parts.length < 3) {
            throw new IllegalArgumentException("Corrupted save line (expected at least 3 fields): " + line);
        }

        String type = parts[0];
        boolean isDone = parts[1].equals("1");
        String description = parts[2];

        Task task;
        switch (type) {
            case "T":
                task = new Todo(description);
                break;
            case "D":
                if (parts.length < 4) {
                    throw new IllegalArgumentException("Corrupted deadline line (missing 'by' field): " + line);
                }
                task = new Deadline(description, parts[3]);
                break;
            case "E":
                if (parts.length < 5) {
                    throw new IllegalArgumentException(
                            "Corrupted event line (missing 'from'/'to' fields): " + line);
                }
                task = new Event(description, parts[3], parts[4]);
                break;
            default:
                task = new Todo(description);
                break;
        }
        if (isDone) {
            task.markAsDone();
        }
        return task;
    }
}
