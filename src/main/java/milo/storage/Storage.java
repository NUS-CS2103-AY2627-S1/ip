package milo.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import milo.exception.MiloException;
import milo.task.Deadline;
import milo.task.Event;
import milo.task.Task;
import milo.task.Todo;

/**
 * Loads tasks from, and saves tasks to, a file on the hard disk.
 * <p>
 * The path is built with {@link Paths#get(String, String...)} from relative
 * parts, so it stays relative to wherever the program is run and uses whatever
 * separator the current operating system expects.
 * <p>
 * Both a missing file and a missing folder are treated as "nothing saved yet"
 * rather than as errors, so the chatbot works on a computer that has never run
 * it before. Lines that do not match the expected format are skipped rather
 * than allowed to stop the load, and each one is reported through
 * {@link #getLoadWarnings()}.
 */
public class Storage {
    /** Written between fields; kept in step with {@code Task.FILE_SEPARATOR}. */
    private static final String SEPARATOR = " | ";

    /** Matches the separator on the way back in, tolerating stray spaces. */
    private static final String SEPARATOR_PATTERN = "\\s*\\|\\s*";

    /** Type letter marking a line as a todo, and its field count. */
    private static final String TODO_TYPE = "T";
    private static final int TODO_FIELDS = 3;

    /** Type letter marking a line as a deadline, and its field count. */
    private static final String DEADLINE_TYPE = "D";
    private static final int DEADLINE_FIELDS = 4;

    /** Type letter marking a line as an event, and its field count. */
    private static final String EVENT_TYPE = "E";
    private static final int EVENT_FIELDS = 5;

    /** Flag meaning the saved task was done. */
    private static final String DONE_FLAG = "1";

    /** Flag meaning the saved task was not done. */
    private static final String NOT_DONE_FLAG = "0";

    /** Where the tasks are kept. */
    private final Path filePath;

    /** Anything odd noticed during the most recent load, phrased for the user. */
    private final ArrayList<String> loadWarnings = new ArrayList<>();

    /**
     * Creates storage backed by a file at the given path, relative to the
     * directory the program is run from.
     *
     * @param filePath where to keep the save file, e.g. "data/milo.txt"
     */
    public Storage(String filePath) {
        // Paths.get accepts "/" as a separator on every platform NIO
        // supports (including Windows), so a forward-slash literal like
        // "data/milo.txt" still resolves correctly wherever this runs.
        this.filePath = Paths.get(filePath);
    }

    /**
     * Reads the saved tasks back from disk.
     * A missing file or folder simply yields an empty list, and any line that
     * cannot be understood is skipped and recorded as a warning.
     *
     * @return the tasks that were readable, in the order they were saved
     */
    public ArrayList<Task> load() {
        loadWarnings.clear();
        // Warnings describe one load only; a stale warning from a previous
        // load would be reported against this one.
        assert loadWarnings.isEmpty() : "warnings should be cleared before a fresh load";

        if (!Files.exists(filePath)) {
            // First run on this computer: there is simply nothing to load.
            return new ArrayList<>();
        }

        // Something is there, but it may not be something we can read. Both
        // cases are reported rather than thrown, so a broken save location
        // costs the user their history but not their session.
        if (!Files.isRegularFile(filePath)) {
            loadWarnings.add("There's something at " + filePath
                    + ", but it isn't a file I can read from. We're starting fresh.");
            return new ArrayList<>();
        }

        if (!Files.isReadable(filePath)) {
            loadWarnings.add("I'm not allowed to read " + filePath
                    + ". We're starting fresh, and I won't touch what's there.");
            return new ArrayList<>();
        }

        List<String> lines;
        try {
            lines = Files.readAllLines(filePath);
        } catch (IOException e) {
            loadWarnings.add("I couldn't read " + filePath
                    + ", so we're starting fresh.");
            return new ArrayList<>();
        }

        List<String> savedLines = lines.stream()
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .toList();

        ArrayList<Task> tasks = savedLines.stream()
                .map(Storage::tryParseTask)
                .flatMap(Optional::stream)
                .collect(Collectors.toCollection(ArrayList::new));

        // Whatever did not survive the pipeline was a line we could not read.
        int skipped = savedLines.size() - tasks.size();
        if (skipped > 0) {
            loadWarnings.add("I couldn't make sense of " + skipped + " line(s) in "
                    + filePath + ", so I left them out. The rest of your list is intact.");
        }
        return tasks;
    }

    /**
     * Writes the tasks to disk, creating the folder first if it is not there.
     * The file is rewritten in full each time, so it always mirrors the list.
     *
     * @param tasks the tasks to save
     * @throws MiloException if the file could not be written
     */
    public void save(ArrayList<Task> tasks) throws MiloException {
        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                // Does nothing if the folder is already there.
                Files.createDirectories(parent);
            }

            List<String> lines = tasks.stream()
                    .map(Task::toFileFormat)
                    .toList();

            // The file is rewritten in full, so losing a line here would
            // silently drop a task from the user's saved list.
            assert lines.size() == tasks.size() : "every task should produce exactly one saved line";
            Files.write(filePath, lines);
        } catch (IOException e) {
            throw new MiloException("I couldn't save to " + filePath
                    + " (" + e.getMessage() + "). Your list is still safe with me for now.");
        }
    }

    /**
     * Returns anything that went wrong during the most recent {@link #load()}.
     *
     * @return the warnings, which is empty when the load was clean
     */
    public ArrayList<String> getLoadWarnings() {
        return loadWarnings;
    }

    /**
     * Returns where the tasks are being kept, for use in messages.
     *
     * @return the save file's path
     */
    public Path getFilePath() {
        return filePath;
    }

    /**
     * Reads one saved line, reporting an unreadable one as an empty result
     * rather than as an exception, so that a load can carry on past it.
     * {@link #parseTask} throws a checked exception, which a stream cannot
     * carry; turning it into an {@link Optional} at this boundary lets the
     * pipeline filter out the bad lines and count them afterwards.
     *
     * @param line one line of the save file, already trimmed
     * @return the task the line describes, or empty if it could not be read
     */
    private static Optional<Task> tryParseTask(String line) {
        try {
            return Optional.of(parseTask(line));
        } catch (MiloException e) {
            return Optional.empty();
        }
    }

    /**
     * Rebuilds one task from one line of the save file.
     * The field count is checked exactly, so a line carrying too few or too
     * many fields is rejected rather than being guessed at.
     *
     * @param line one line of the save file, already trimmed
     * @return the task that line describes
     * @throws MiloException if the line does not match the expected format
     */
    private static Task parseTask(String line) throws MiloException {
        String[] fields = line.split(SEPARATOR_PATTERN);
        if (fields.length < TODO_FIELDS) {
            throw new MiloException("too few fields");
        }

        String type = fields[0];
        String doneFlag = fields[1];
        String description = fields[2];
        if (description.isEmpty()) {
            throw new MiloException("empty description");
        }

        Task task;
        switch (type) {
            case TODO_TYPE:
                requireFieldCount(fields, TODO_FIELDS);
                task = new Todo(description);
                break;
            case DEADLINE_TYPE:
                requireFieldCount(fields, DEADLINE_FIELDS);
                task = new Deadline(description, fields[3]);
                break;
            case EVENT_TYPE:
                requireFieldCount(fields, EVENT_FIELDS);
                task = new Event(description, fields[3], fields[4]);
                break;
            default:
                throw new MiloException("unknown task type: " + type);
        }

        // Every branch above either assigned a task or threw.
        assert task != null : "parseTask() should have built a task or thrown";

        if (doneFlag.equals(DONE_FLAG)) {
            task.markAsDone();
        } else if (!doneFlag.equals(NOT_DONE_FLAG)) {
            throw new MiloException("unrecognised done flag: " + doneFlag);
        }
        return task;
    }

    /**
     * Checks that a line carried exactly the number of fields its type needs.
     *
     * @param fields   the fields the line split into
     * @param expected how many that type of task requires
     * @throws MiloException if the count does not match
     */
    private static void requireFieldCount(String[] fields, int expected) throws MiloException {
        if (fields.length != expected) {
            throw new MiloException("expected " + expected + " fields, found " + fields.length);
        }
    }
}
