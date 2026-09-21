package goat.storage;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import goat.GoatException;
import goat.parser.DateTimes;
import goat.task.Deadline;
import goat.task.Event;
import goat.task.Task;
import goat.task.TaskList;
import goat.task.Todo;

/**
 * Reads the task list from disk and writes it back again.
 *
 * Keeping all file handling in one class means the rest of Goat never has
 * to know where the data lives or how it is encoded; if the format changes,
 * only this class and the tasks' own {@code toFileFormat} methods change.
 *
 * Each Storage remembers the one file it is responsible for. That was a
 * constant until now, since Goat only ever uses one save file, but holding
 * the path in a field means the caller says where the tasks live instead of
 * this class deciding for everyone -- and a test can point a Storage at a
 * temporary file of its own rather than trampling the real one.
 */
public class Storage {
    /** Position of the type letter in a saved line. */
    private static final int FIELD_TYPE = 0;

    /** Position of the done flag in a saved line. */
    private static final int FIELD_DONE_FLAG = 1;

    /** Position of the description in a saved line. */
    private static final int FIELD_DESCRIPTION = 2;

    /** Position of a deadline's due date, and of an event's start. */
    private static final int FIELD_FIRST_DATE = 3;

    /** Position of an event's end. */
    private static final int FIELD_SECOND_DATE = 4;

    /** Fields every task has, whatever its type. */
    private static final int FIELDS_SHARED = 3;

    /** How a saved line is split: a bar, with any spaces around it. */
    private static final String FIELD_PATTERN = "\\s*\\|\\s*";

    /** Where this Storage reads and writes its tasks. */
    private final Path filePath;

    /**
     * Creates a Storage for one save file.
     *
     * Write the path with "/" between the folder names. Java's own file
     * classes accept "/" on every operating system, including Windows, so
     * there is no need to build the path out of separate name parts.
     *
     * @param filePath where the tasks should be kept, e.g. "data/goat.txt"
     */
    public Storage(String filePath) {
        this.filePath = Paths.get(filePath);
    }

    /**
     * Loads the saved tasks.
     *
     * A missing file is not an error: it simply means nothing has been saved
     * yet, which is exactly the situation on someone's first run.
     *
     * @return the saved tasks, or an empty list if there is no save file
     * @throws GoatException if the file exists but cannot be read or understood
     */
    public ArrayList<Task> load() throws GoatException {
        if (!Files.exists(filePath)) {
            return new ArrayList<>();
        }
        return parseLines(readLines());
    }

    /**
     * Reads the save file's raw lines.
     *
     * Separated from the parsing so that the two ways loading can fail stay
     * apart: a file that cannot be read at all is reported here, and a line
     * that cannot be understood is reported by {@link #parseLine}.
     *
     * @return the lines of the save file, unparsed
     * @throws GoatException if the file cannot be read
     */
    private List<String> readLines() throws GoatException {
        try {
            return Files.readAllLines(filePath);
        } catch (AccessDeniedException e) {
            // Caught apart from the rest because this exception's message is
            // only the path, which would read as "I could not read x: x".
            throw new GoatException("I am not allowed to read " + filePath
                    + ". Check the file's permissions.");
        } catch (IOException e) {
            throw new GoatException("I could not read " + filePath + ": " + e.getMessage());
        }
    }

    /**
     * Turns the save file's lines into tasks.
     *
     * @param lines the lines of the save file
     * @return the tasks they describe
     * @throws GoatException if any line cannot be understood
     */
    private ArrayList<Task> parseLines(List<String> lines) throws GoatException {
        ArrayList<Task> tasks = new ArrayList<>();
        // Counted from 1, the way a text editor numbers lines, because the only
        // use of this number is telling the user which line to go and fix.
        for (int lineNumber = 1; lineNumber <= lines.size(); lineNumber++) {
            String line = lines.get(lineNumber - 1).trim();
            if (line.isEmpty()) {
                continue; // Tolerate blank lines, e.g. a trailing newline.
            }
            tasks.add(parseLine(line, lineNumber));
        }
        return tasks;
    }

    /**
     * Turns one line into a task, saying where the fault is if it cannot.
     *
     * @param line       one line of the save file, already trimmed
     * @param lineNumber that line's position in the file, counting from 1
     * @return the task the line describes
     * @throws GoatException naming the line, if it cannot be understood
     */
    private Task parseLine(String line, int lineNumber) throws GoatException {
        try {
            return parseTask(line);
        } catch (GoatException e) {
            // Name the line at fault: the user may want to fix it by hand
            // rather than lose the whole file.
            throw new GoatException("The save file " + filePath
                    + " is damaged on line " + lineNumber + ": " + e.getMessage());
        }
    }

    /**
     * Writes the whole task list to disk, replacing whatever was there before.
     *
     * Rewriting the entire file on every change is far simpler than editing
     * one line in place, and a task list is small enough that the cost of
     * doing so is not worth avoiding.
     *
     * @param tasks the tasks to save
     * @throws GoatException if the file or its folder cannot be written
     */
    public void save(TaskList tasks) throws GoatException {
        try {
            // The ./data folder will not exist on a fresh checkout, so create
            // it first. createDirectories does nothing if it is already there.
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            // Numbered from 1 rather than streamed over the list directly,
            // because TaskList deliberately does not hand out what it wraps.
            // rangeClosed keeps that constraint while still reading as a
            // mapping from tasks to lines.
            List<String> lines = IntStream.rangeClosed(1, tasks.size())
                    .mapToObj(taskNumber -> toFileLine(tasks, taskNumber))
                    .collect(Collectors.toList());
            Files.write(filePath, lines);
        } catch (AccessDeniedException e) {
            throw new GoatException("I am not allowed to write to " + filePath
                    + ". Check the file's permissions. Nothing was saved.");
        } catch (IOException e) {
            throw new GoatException("I could not save to " + filePath + ": " + e.getMessage());
        }
    }

    /**
     * Returns one task's saved line.
     *
     * A small wrapper so that save() can be written as a mapping: TaskList.get
     * throws a checked exception, which a lambda cannot pass on, and the
     * numbers used here are always in range.
     *
     * @param tasks      the list being saved
     * @param taskNumber the task's position, from 1
     * @return the line to write for that task
     */
    private static String toFileLine(TaskList tasks, int taskNumber) {
        try {
            return tasks.get(taskNumber).toFileFormat();
        } catch (GoatException e) {
            // Unreachable: rangeClosed(1, size) never leaves the list.
            throw new IllegalStateException("task " + taskNumber + " vanished while saving", e);
        }
    }

    /**
     * Rebuilds one task from the line that was written for it.
     * This is the exact inverse of {@link Task#toFileFormat()}.
     *
     * @param line one line of the save file, already trimmed
     * @return the task that line describes
     * @throws GoatException if the line does not match any known task format
     */
    private static Task parseTask(String line) throws GoatException {
        String[] parts = line.split(FIELD_PATTERN);
        if (parts.length < FIELDS_SHARED) {
            throw new GoatException("too few fields");
        }
        Task task = buildTask(parts);
        applyDoneFlag(task, parts[FIELD_DONE_FLAG]);
        return task;
    }

    /**
     * Builds the right kind of task for a saved line's type letter.
     *
     * The letters are the task classes' own constants rather than literals
     * repeated here, so reading and writing cannot disagree about them.
     *
     * @param parts the saved line, already split into fields
     * @return the task, not yet marked done
     * @throws GoatException if the type is unknown or the line is missing a date
     */
    private static Task buildTask(String[] parts) throws GoatException {
        String type = parts[FIELD_TYPE];
        String description = parts[FIELD_DESCRIPTION];
        switch (type) {
            case Todo.FILE_TYPE:
                return new Todo(description);
            case Deadline.FILE_TYPE:
                requireFields(parts, FIELD_FIRST_DATE, "a deadline needs a due date");
                // DateTimes.parse throws GoatException on a date it cannot read,
                // and load() already turns that into "damaged on line N", so a
                // hand-edited file with a bad date is reported like any other fault.
                return new Deadline(description, DateTimes.parse(parts[FIELD_FIRST_DATE]));
            case Event.FILE_TYPE:
                requireFields(parts, FIELD_SECOND_DATE, "an event needs a start and an end");
                return new Event(description,
                        DateTimes.parse(parts[FIELD_FIRST_DATE]),
                        DateTimes.parse(parts[FIELD_SECOND_DATE]));
            default:
                throw new GoatException("unknown task type '" + type + "'");
        }
    }

    /**
     * Checks that a saved line reaches at least the given field.
     *
     * @param parts    the saved line, already split into fields
     * @param field    the position that has to exist
     * @param complaint what to tell the user if it does not
     * @throws GoatException if the line is too short
     */
    private static void requireFields(String[] parts, int field, String complaint)
            throws GoatException {
        if (parts.length <= field) {
            throw new GoatException(complaint);
        }
    }

    /**
     * Marks a freshly built task done if its saved line says so.
     *
     * Tasks are always built as not done, so only the "done" flag needs acting
     * on; anything that is neither flag means the line was edited wrongly.
     *
     * @param task     the task to mark
     * @param doneFlag the flag field from the saved line
     * @throws GoatException if the flag is neither of the two allowed values
     */
    private static void applyDoneFlag(Task task, String doneFlag) throws GoatException {
        if (doneFlag.equals(Task.FILE_FLAG_DONE)) {
            task.markAsDone();
        } else if (!doneFlag.equals(Task.FILE_FLAG_NOT_DONE)) {
            throw new GoatException("the done flag should be 0 or 1, not '" + doneFlag + "'");
        }
    }
}
