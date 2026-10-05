package gatsby.storage;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import gatsby.exception.EmptyPayloadException;
import gatsby.exception.GatsbyException;
import gatsby.exception.UnknownCommandException;
import gatsby.model.Deadline;
import gatsby.model.Event;
import gatsby.model.Task;
import gatsby.model.Todo;

/**
 * Saves the task list to the hard disk and loads it again when Gatsby starts.
 *
 * The file path is hard-coded relative to the project root ({@code ./data/gatsby.txt}).
 * Each task occupies one line in the format produced by {@link Task#toFileFormat()},
 * for example: {@code T | 1 | read book}.
 *
 * Nothing in this class throws: the save file is outside Gatsby's control (it can be
 * missing, hand-edited, or read-only), so problems are reported as messages and the
 * chatbot keeps working with whatever could be recovered.
 */
public class Storage {
    /** The directory containing Gatsby's save file. */
    private static final Path DATA_DIRECTORY = Path.of("data");

    /** The path of Gatsby's save file relative to the project root. */
    private static final Path DATA_FILE = DATA_DIRECTORY.resolve("gatsby.txt");

    /** Field counts a valid saved line must have, per task type. */
    private static final int TODO_FIELDS = 3;
    private static final int DEADLINE_FIELDS = 4;
    private static final int EVENT_FIELDS = 5;

    /** Creates a storage helper. */
    public Storage() {
    }

    /**
     * Writes the whole task list to disk, overwriting any previous contents.
     *
     * Rewriting the entire file is the simplest option that is always correct.
     * (A more advanced alternative would be appending only the changed line, but
     * that is harder to keep consistent after edits and deletions.)
     *
     * @param tasks the current task list
     */
    public static void save(List<Task> tasks) {
        if (tasks == null) {
            System.out.println(" OOPS! I couldn't save your tasks because the task list was missing.");
            return;
        }
        for (Task task : tasks) {
            if (task == null) {
                System.out.println(" OOPS! I couldn't save your tasks because one task was missing.");
                return;
            }
        }
        // On a fresh copy of the project the data folder does not exist yet,
        // so it is created on the first save rather than assumed to be there.
        try {
            if (Files.exists(DATA_DIRECTORY) && !Files.isDirectory(DATA_DIRECTORY)) {
                System.out.println(" OOPS! \"" + DATA_DIRECTORY + "\" already exists as a file,"
                    + " so I have nowhere to save. Rename or remove it and I'll save again.");
                return;
            }
            Files.createDirectories(DATA_DIRECTORY);

            // Write a complete temporary file first, so an interrupted save does not
            // destroy the last known-good copy of the task list.
            Path temporaryFile = Files.createTempFile(DATA_DIRECTORY, "gatsby-", ".tmp");
            try {
                Files.write(temporaryFile, tasks.stream()
                        .map(task -> task.toFileFormat() + System.lineSeparator())
                        .toList(), StandardCharsets.UTF_8);
                replaceSaveFile(temporaryFile);
            } catch (IOException | SecurityException e) {
                Files.deleteIfExists(temporaryFile);
                throw e;
            }
        } catch (IOException | SecurityException e) {
            String reason = e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
            System.out.println(" OOPS! I couldn't save your tasks: " + reason);
        }
    }

    /** Replaces the old save file, falling back when atomic moves are unavailable. */
    private static void replaceSaveFile(Path temporaryFile) throws IOException {
        try {
            Files.move(temporaryFile, DATA_FILE, StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(temporaryFile, DATA_FILE, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Reads the saved tasks back from disk.
     *
     * A missing data folder or a missing file simply means there is nothing saved
     * yet, as on a first run after cloning the project, so an empty list is returned
     * without any error or warning. A line that cannot be understood (for
     * example, because the file was edited by hand) is skipped with a warning so that
     * one bad line does not throw away the rest of the list.
     *
     * @return the tasks stored on disk, in the order they were saved
     */
    public static ArrayList<Task> load() {
        try {
            if (!isLoadableFile()) {
                return new ArrayList<>();
            }
            return readTasks(DATA_FILE);
        } catch (SecurityException e) {
            System.out.println(" OOPS! I don't have permission to read your saved tasks.");
            return new ArrayList<>();
        }
    }

    /**
     * Checks whether the save path points to a file that Gatsby can load.
     *
     * @return true when the save file exists and is not a directory
     */
    private static boolean isLoadableFile() {
        // A missing folder or a missing file is the normal first-run state: someone
        // has just cloned the project and has not saved anything yet. Both simply
        // mean "no tasks saved", so an empty list is returned without any warning.
        if (!Files.exists(DATA_FILE)) {
            return false;
        }
        if (Files.isDirectory(DATA_FILE)) {
            System.out.println(" OOPS! \"" + DATA_FILE + "\" is a folder, not my save file,"
                    + " so I'm starting with an empty list.");
            return false;
        }
        if (!Files.isReadable(DATA_FILE)) {
            System.out.println(" OOPS! I don't have permission to read your saved tasks.");
            return false;
        }
        return true;
    }

    /**
     * Reads and reconstructs all valid tasks from a save file.
     *
     * @param file the save file to read
     * @return the valid tasks found in the file
     */
    private static ArrayList<Task> readTasks(Path file) {
        ArrayList<Task> tasks = new ArrayList<>();
        int skippedLines = 0;
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.strip();
                if (line.isEmpty()) {
                    continue;
                }
                try {
                    Task task = parseLine(line);
                    if (tasks.stream().anyMatch(existing -> existing.hasSameDetails(task))) {
                        throw new GatsbyException("duplicate task details");
                    }
                    tasks.add(task);
                } catch (GatsbyException | IllegalArgumentException e) {
                    skippedLines++;
                    System.out.println(" OOPS! I skipped a line I couldn't read in my save file: " + line);
                }
            }
        } catch (IOException | SecurityException e) {
            System.out.println(" OOPS! I couldn't read your saved tasks: " + e.getMessage());
            return new ArrayList<>();
        }

        if (skippedLines > 0) {
            System.out.println(" Heads up: those " + skippedLines
                    + " skipped line(s) will disappear the next time I save.");
        }
        return tasks;
    }

    /**
     * Converts one saved line into the task it represents.
     *
     * The field count is checked exactly, so a line with missing or extra fields is
     * treated as corrupted rather than silently reconstructed as the wrong task.
     *
     * @param line a single line from the save file, e.g. {@code D | 0 | return book | 2019-12-02T18:00}
     * @return the reconstructed task
     * @throws GatsbyException when the line is malformed
     */
    private static Task parseLine(String line) throws GatsbyException {
        String[] parts = line.split("\\s*\\|\\s*", -1);
        if (parts.length < TODO_FIELDS) {
            throw new EmptyPayloadException("too few fields");
        }

        String type = parts[0];
        String doneFlag = parts[1];
        String description = parts[2];
        if (description.isEmpty()) {
            throw new EmptyPayloadException("empty description");
        }
        if (!doneFlag.equals("0") && !doneFlag.equals("1")) {
            throw new EmptyPayloadException("done flag must be 0 or 1");
        }

        Task task;
        if (type.equals("T") && parts.length == TODO_FIELDS) {
            task = new Todo(description);
        } else if (type.equals("D") && parts.length == DEADLINE_FIELDS && !parts[3].isEmpty()) {
            task = new Deadline(description, parseSavedDateTime(parts[3]));
        } else if (type.equals("E") && parts.length == EVENT_FIELDS
                && !parts[3].isEmpty() && !parts[4].isEmpty()) {
            task = new Event(description, parseSavedDateTime(parts[3]), parseSavedDateTime(parts[4]));
        } else {
            throw new UnknownCommandException("unrecognised task type or wrong number of fields");
        }

        if (doneFlag.equals("1")) {
            task.markDone();
        }
        assert task.isDone() == doneFlag.equals("1")
                : "A reconstructed task must preserve the saved completion status.";
        return task;
    }

    /**
     * Parses an ISO date-time used for deadline or event values in the save file.
     *
     * @param value the saved date-time
     * @return the parsed date-time
     * @throws GatsbyException when the saved value is not a valid date-time
     */
    private static LocalDateTime parseSavedDateTime(String value) throws GatsbyException {
        try {
            return LocalDateTime.parse(value);
        } catch (DateTimeParseException e) {
            throw new EmptyPayloadException("date-time is invalid");
        }
    }
}
