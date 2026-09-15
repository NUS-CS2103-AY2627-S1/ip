package seedu.goat.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import seedu.goat.DateFormats;
import seedu.goat.GoatException;
import seedu.goat.task.Deadline;
import seedu.goat.task.Event;
import seedu.goat.task.Task;
import seedu.goat.task.Todo;

/**
 * Reads and writes the task list to a text file on disk.
 * <p>
 * The path is supplied as a relative path string and turned into a {@link Path}, so the
 * separator is whatever the host operating system uses and no separator is hardcoded.
 */
public class Storage {

    /** Fields every encoded task carries: its type, its status and its description. */
    private static final int SHARED_FIELD_COUNT = 3;

    /** Fields a deadline carries: the shared three and its due date. */
    private static final int DEADLINE_FIELD_COUNT = 4;

    /** Fields an event carries: the shared three and its two dates. */
    private static final int EVENT_FIELD_COUNT = 5;

    /** The status field of a task the user has completed. */
    private static final String STATUS_DONE = "1";

    /** The status field of a task still outstanding. */
    private static final String STATUS_NOT_DONE = "0";

    /** Where the task list is kept between runs. */
    private final Path filePath;

    /** Whether reading failed or malformed records make overwriting unsafe. */
    private boolean isReadOnly;

    /** Bytes last read or saved, used to detect another writer. */
    private byte[] savedBytes;

    /** Whether the file has been inspected by this instance. */
    private boolean hasLoaded;

    /**
     * Creates a store backed by the given file.
     *
     * @param filePath relative path to the save file, such as {@code data/goat.txt}
     */
    public Storage(String filePath) {
        this.filePath = Path.of(filePath);
    }

    /** Number of unreadable lines skipped by the most recent {@link #load()}. */
    private int skippedLineCount = 0;

    /**
     * Reads the saved task list from disk.
     * <p>
     * A missing file is not an error: it simply means this is the first run, so an empty
     * list is returned and the file appears the first time a task is added. Individual
     * lines that cannot be decoded are skipped rather than aborting the whole load, so
     * one damaged line cannot cost the user the rest of their list.
     *
     * @return the saved tasks in file order, or an empty list if there is no save file.
     * @throws GoatException if the file exists but cannot be read at all.
     */
    public List<Task> load() throws GoatException {
        List<Task> tasks = new ArrayList<>();
        skippedLineCount = 0;
        isReadOnly = true;
        hasLoaded = true;
        List<String> lines;
        try {
            if (Files.notExists(filePath, LinkOption.NOFOLLOW_LINKS)) {
                savedBytes = null;
                isReadOnly = false;
                return tasks;
            }
            if (Files.isSymbolicLink(filePath)) {
                throw new IOException("Symbolic links are not supported");
            }
            savedBytes = Files.readAllBytes(filePath);
            lines = Files.readAllLines(filePath);
        } catch (IOException | SecurityException e) {
            throw new GoatException("I could not read your saved tasks from " + filePath
                    + ". Changes are disabled. Back up the file, fix its permissions or contents, and restart.");
        }

        for (String line : lines) {
            if (line.isBlank()) {
                continue;
            }
            try {
                tasks.add(parseLine(line));
            } catch (GoatException e) {
                skippedLineCount++;
            }
        }
        isReadOnly = skippedLineCount > 0;
        return tasks;
    }

    /**
     * Returns how many unreadable lines the most recent load skipped.
     *
     * @return the number of skipped lines, or zero if the file was wholly readable.
     */
    public int getSkippedLineCount() {
        return skippedLineCount;
    }

    /**
     * Rebuilds one task from its encoded line.
     *
     * @param line a line produced by {@link Task#toFileString()}
     * @return the decoded task.
     * @throws GoatException if the line is malformed in any way.
     */
    private static Task parseLine(String line) throws GoatException {
        // The separator is escaped because split() takes a regular expression, in which
        // a bare | means alternation rather than a literal bar.
        String[] parts = line.split(" \\| ", -1);
        requireFieldCount(parts, SHARED_FIELD_COUNT);
        assert parts.length >= SHARED_FIELD_COUNT : "requireFieldCount throws unless present";

        String type = parts[0];
        String status = parts[1];
        String description = parts[2];
        if (!status.equals(STATUS_NOT_DONE) && !status.equals(STATUS_DONE)) {
            throw new GoatException("status must be 0 or 1");
        }
        if (description.isBlank()) {
            throw new GoatException("description is empty");
        }

        int expected = switch (type) {
        case "T" -> SHARED_FIELD_COUNT;
        case "D" -> DEADLINE_FIELD_COUNT;
        case "E" -> EVENT_FIELD_COUNT;
        default -> throw new GoatException("unknown task type " + type);
        };
        if (parts.length != expected) {
            throw new GoatException("unexpected number of fields");
        }
        Task task = switch (type) {
        case "T" -> new Todo(description);
        case "D" -> {
            requireFieldCount(parts, DEADLINE_FIELD_COUNT);
            yield new Deadline(description, DateFormats.parse(parts[3]));
        }
        case "E" -> {
            requireFieldCount(parts, EVENT_FIELD_COUNT);
            var start = DateFormats.parse(parts[3]);
            var end = DateFormats.parse(parts[4]);
            if (end.isBefore(start)) {
                throw new GoatException("event ends before it starts");
            }
            yield new Event(description, start, end);
        }
        // Without this the decoder would guess, and an unrecognized letter would be
        // silently turned into some other kind of task.
        default -> throw new GoatException("unknown task type " + type);
        };

        if (status.equals(STATUS_DONE)) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Checks that an encoded line carried enough fields to decode.
     *
     * @param parts the fields split out of the line.
     * @param required how many fields this kind of task needs.
     * @throws GoatException if there are too few fields, or any required one is blank.
     */
    private static void requireFieldCount(String[] parts, int required)
            throws GoatException {
        if (parts.length < required) {
            throw new GoatException("expected " + required + " fields, found "
                    + parts.length);
        }
        for (int i = 0; i < required; i++) {
            if (parts[i].isBlank()) {
                throw new GoatException("field " + (i + 1) + " is empty");
            }
        }
    }

    /**
     * Writes the whole task list to disk, replacing whatever was there before.
     * <p>
     * Rewriting the entire file on every change is far simpler than editing a line in
     * place, and the file is small enough that the cost does not matter.
     *
     * @param tasks the tasks to save, in list order.
     * @throws GoatException if the file cannot be written.
     */
    public void save(List<Task> tasks) throws GoatException {
        assert tasks != null : "The caller always holds a list, even an empty one";
        if (!hasLoaded) {
            load();
        }
        if (isReadOnly) {
            throw new GoatException("Changes are disabled to protect " + filePath
                    + ". Back up the original, repair the file or permissions, and restart GOAT.");
        }
        Path temporary = null;
        try {
            Path target = filePath.toAbsolutePath();
            Files.createDirectories(target.getParent());
            byte[] current = Files.notExists(target, LinkOption.NOFOLLOW_LINKS)
                    ? null : Files.readAllBytes(target);
            if (Files.isSymbolicLink(target) || !Arrays.equals(savedBytes, current)) {
                throw new IOException("The save file changed outside GOAT");
            }
            List<String> lines = tasks.stream().map(Task::toFileString).toList();
            temporary = Files.createTempFile(target.getParent(), "goat-", ".tmp");
            Files.write(temporary, lines);
            byte[] replacement = Files.readAllBytes(temporary);
            // Refuse the save if atomic replacement is unavailable: keep the old file intact.
            Files.move(temporary, target, StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
            savedBytes = replacement;
        } catch (IOException | SecurityException e) {
            throw new GoatException("I could not save your tasks to " + filePath
                    + ". No change was applied. Check permissions, disk space, or changes from another app."
                    + " Back up the file and restart GOAT before retrying.");
        } finally {
            if (temporary != null) {
                try {
                    Files.deleteIfExists(temporary);
                } catch (IOException | SecurityException e) {
                    // An abandoned temporary file is harmless; never delete the original to clean up.
                }
            }
        }
    }
}
