package rex;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import rex.task.Deadline;
import rex.task.Event;
import rex.task.Task;
import rex.task.TaskDateTime;
import rex.task.ToDo;
import rex.task.WithinPeriod;

/**
 * Saves the task list to a file on disk and loads it back, so that tasks
 * survive between runs of the program. This is the only class that knows
 * where the tasks are kept or what the saved file looks like; the rest of
 * the program just hands it a list of tasks.
 */
public class Storage {
    /**
     * How many fields each kind of saved line has. Every line names its type,
     * says whether it is done and carries a description; a todo stops there,
     * while a deadline adds one date and an event adds two.
     */
    private static final int TODO_FIELD_COUNT = 3;
    private static final int DEADLINE_FIELD_COUNT = 4;
    private static final int EVENT_FIELD_COUNT = 5;
    private static final int WITHIN_FIELD_COUNT = 5;

    /**
     * The separator as a regular expression, since split() reads its argument
     * as one and "|" means "or" there. Quoting it keeps the two spellings of
     * the separator from drifting apart.
     */
    private static final String FIELD_SEPARATOR_PATTERN = Pattern.quote(Task.FIELD_SEPARATOR);

    private final Path file;
    private int skippedLineCount = 0;

    /**
     * Creates a Storage that reads and writes the given file.
     *
     * @param first the first part of the path to the save file, relative to
     *     the folder the program is run from (e.g. "data").
     * @param more the remaining parts of the path (e.g. "rex.txt"). The path
     *     is assembled from separate parts rather than a single string so
     *     that it works on any operating system, instead of hard-coding a
     *     separator such as "/" or "\".
     */
    public Storage(String first, String... more) {
        this.file = Paths.get(first, more);
    }

    /**
     * Writes the given tasks to the save file, replacing anything already in
     * it. The containing folder is created first if it does not exist yet.
     *
     * @param tasks the tasks to save, in the order they should be restored.
     * @throws IOException if the file could not be written.
     */
    public void save(List<Task> tasks) throws IOException {
        Path folder = file.getParent();
        if (folder != null) {
            Files.createDirectories(folder);
        }

        List<String> lines = tasks.stream()
                .map(Task::toSaveFormat)
                .toList();
        Files.write(file, lines);
    }

    /**
     * Reads the saved tasks back from the file, in the order they were saved.
     *
     * A missing file (or missing folder) is not an error: it simply means
     * nothing has been saved yet, which is the normal state on a first run,
     * so an empty list is returned.
     *
     * @return the saved tasks, or an empty list if there is no save file yet.
     * @throws IOException if the file exists but could not be read.
     */
    public ArrayList<Task> load() throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();
        skippedLineCount = 0;
        if (!Files.exists(file)) {
            return tasks;
        }

        for (String line : Files.readAllLines(file)) {
            if (line.trim().isEmpty()) {
                continue;
            }
            try {
                tasks.add(parseTask(line));
            } catch (IllegalArgumentException e) {
                // One unreadable line should not cost the user every other
                // task in the file, so it is skipped and counted instead.
                skippedLineCount++;
            }
        }
        return tasks;
    }

    /**
     * Returns how many lines the most recent load could not understand and
     * skipped. Note that these lines are lost the next time the tasks are
     * saved, because saving rewrites the whole file.
     */
    public int getSkippedLineCount() {
        return skippedLineCount;
    }

    /**
     * Rebuilds one task from a line of the save file.
     *
     * Unlike writing, this cannot be left to the task classes themselves:
     * there is no task object yet to ask, so the type letter has to be
     * examined here to decide which kind of task to create.
     */
    private static Task parseTask(String line) {
        String[] fields = line.split(FIELD_SEPARATOR_PATTERN);
        // Every field is checked before it is used, so that a damaged line
        // always fails as an IllegalArgumentException the caller can skip,
        // rather than as an out-of-bounds error further down.
        requireAtLeastFieldCount(fields, TODO_FIELD_COUNT);

        String doneFlag = fields[1];
        if (!doneFlag.equals("0") && !doneFlag.equals("1")) {
            throw new IllegalArgumentException("Done flag is not 0 or 1: " + doneFlag);
        }

        Task task = buildTask(fields);
        assert task != null : "buildTask either builds a task or throws";

        if (doneFlag.equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Builds a task of the kind named by the line's first field, without
     * regard to whether it has been done.
     *
     * Kept apart from parseTask so that each says one thing: parseTask reads
     * what every line has in common, and this reads what makes the kinds
     * differ.
     *
     * @param fields the line already split on the separator.
     * @throws IllegalArgumentException if the type is unknown, the line has
     *     the wrong number of fields for it, or a date cannot be read.
     */
    private static Task buildTask(String[] fields) {
        String type = fields[0];
        String description = fields[2];
        if (description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description is empty");
        }

        switch (type) {
            case "T":
                requireExactFieldCount(fields, TODO_FIELD_COUNT);
                return new ToDo(description);
            case "D":
                requireExactFieldCount(fields, DEADLINE_FIELD_COUNT);
                // TaskDateTime.parse throws IllegalArgumentException on a date it
                // cannot read, which is what load() already skips the line for, so
                // a damaged date needs no handling of its own here.
                return new Deadline(description, TaskDateTime.parse(fields[3]));
            case "E":
                requireExactFieldCount(fields, EVENT_FIELD_COUNT);
                return new Event(description, TaskDateTime.parse(fields[3]), TaskDateTime.parse(fields[4]));
            case "W":
                requireExactFieldCount(fields, WITHIN_FIELD_COUNT);
                return new WithinPeriod(description,
                        TaskDateTime.parse(fields[3]), TaskDateTime.parse(fields[4]));
            default:
                throw new IllegalArgumentException("Unknown task type: " + type);
        }
    }

    /** Throws IllegalArgumentException if the line has fewer fields than needed. */
    private static void requireAtLeastFieldCount(String[] fields, int required) {
        if (fields.length < required) {
            throw new IllegalArgumentException(
                    "Expected at least " + required + " fields but found " + fields.length);
        }
    }

    /** Throws IllegalArgumentException unless the line has exactly this many fields. */
    private static void requireExactFieldCount(String[] fields, int required) {
        if (fields.length != required) {
            throw new IllegalArgumentException(
                    "Expected " + required + " fields but found " + fields.length);
        }
    }

    /** Returns the path of the save file, for use in messages to the user. */
    public Path getFile() {
        return file;
    }
}
