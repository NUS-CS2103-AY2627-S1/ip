package lebronjames.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import lebronjames.LebronJamesException;
import lebronjames.task.Deadline;
import lebronjames.task.Event;
import lebronjames.task.Task;
import lebronjames.task.TaskDateTime;
import lebronjames.task.Todo;

/**
 * Reads the task list from, and writes it to, a text file on the hard disk.
 *
 * <p>Each task occupies one line, with fields separated by {@code " | "}:
 * <pre>
 * T | 1 | read book
 * D | 0 | return book | 2019-06-06
 * E | 0 | project meeting | 2019-08-06 1400 | 2019-08-06 1600
 * </pre>
 *
 * <p>Dates are always written in the canonical form produced by
 * {@link TaskDateTime#toFileFormat()}, whichever accepted format the user typed.
 *
 * <p>The path is built with {@link Path#of(String, String...)} from separate
 * name components rather than a hard-coded string such as {@code "data/x.txt"},
 * so the correct separator is used on every operating system. The path is also
 * relative, so it resolves against whichever directory the program is run from
 * instead of pointing at one particular machine.
 */
public class Storage {
    private final Path filePath;

    /**
     * Creates storage backed by the given relative path.
     *
     * @param firstPathPart First name in the path, for example {@code "data"}.
     * @param remainingPathParts Remaining names, for example {@code "lebronjames.txt"}.
     */
    public Storage(String firstPathPart, String... remainingPathParts) {
        // The path parts are compile-time constants in LebronJames, so a blank
        // first part would mean the save file had silently become the working
        // directory itself.
        assert firstPathPart != null && !firstPathPart.isBlank() : "A save path needs a non-blank first part";

        this.filePath = Path.of(firstPathPart, remainingPathParts);
    }

    /**
     * Loads the saved tasks.
     *
     * <p>A missing file (or missing folder) is not an error: it simply means
     * nothing has been saved yet, so an empty list is returned. Lines that are
     * not in the expected format are dropped rather than aborting the load, and
     * disappear from the file the next time the list is saved.
     *
     * @return Tasks that were read successfully.
     * @throws LebronJamesException If the file exists but cannot be read.
     */
    public ArrayList<Task> load() throws LebronJamesException {
        ArrayList<Task> tasks = new ArrayList<>();

        if (!Files.exists(filePath)) {
            return tasks;
        }
        if (!Files.isRegularFile(filePath)) {
            throw new LebronJamesException(
                    "AIRBALL. " + filePath + " is not a file, so I could not load the playbook.");
        }

        List<String> lines;
        try {
            lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new LebronJamesException("AIRBALL. I could not read " + filePath + ": " + exception.getMessage());
        }

        for (String line : lines) {
            if (line.isBlank()) {
                continue;
            }
            try {
                tasks.add(parseTask(line));
            } catch (LebronJamesException exception) {
                // The line is damaged or in an outdated format, so there is no task
                // to restore from it. Dropping it keeps the rest of the list usable.
            }
        }
        return tasks;
    }

    /**
     * Saves the given tasks, replacing any previously saved list.
     *
     * <p>The containing folder is created first if it does not exist yet, so the
     * chatbot works on a computer where it has never been run before.
     *
     * @param tasks Tasks to write, in list order.
     * @throws LebronJamesException If the tasks cannot be written to disk.
     */
    public void save(List<Task> tasks) throws LebronJamesException {
        // Every caller passes TaskList.asList(), which returns the live list.
        assert tasks != null : "Cannot save a null task list";

        List<String> lines = tasks.stream()
                .map(Task::toFileFormat)
                .toList();

        try {
            Path parentDirectory = filePath.getParent();
            if (parentDirectory != null) {
                Files.createDirectories(parentDirectory);
            }
            Files.write(filePath, lines, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new LebronJamesException(
                    "AIRBALL. I could not save the playbook to " + filePath + ": " + exception.getMessage());
        }
    }

    /**
     * Converts one line of the save file back into a task.
     *
     * @param line Line to interpret.
     * @return Task described by the line.
     * @throws LebronJamesException If the line does not match the expected format.
     */
    private static Task parseTask(String line) throws LebronJamesException {
        String[] fields = line.split("\\|", -1);
        for (int i = 0; i < fields.length; i++) {
            fields[i] = fields[i].strip();
        }

        if (fields.length < 3) {
            throw new LebronJamesException("Line has too few fields.");
        }

        String taskType = fields[0];
        boolean isDone = parseDoneFlag(fields[1]);
        String description = fields[2];
        if (description.isEmpty()) {
            throw new LebronJamesException("Line has an empty description.");
        }

        Task task;
        switch (taskType) {
            case Todo.FILE_TYPE:
                requireFieldCount(fields, 3);
                task = new Todo(description);
                break;
            case Deadline.FILE_TYPE:
                requireFieldCount(fields, 4);
                requireNonEmpty(fields[3]);
                task = new Deadline(description, TaskDateTime.parse(fields[3]));
                break;
            case Event.FILE_TYPE:
                requireFieldCount(fields, 5);
                requireNonEmpty(fields[3]);
                requireNonEmpty(fields[4]);
                task = new Event(description, TaskDateTime.parse(fields[3]), TaskDateTime.parse(fields[4]));
                break;
            default:
                throw new LebronJamesException("Unknown task type: " + taskType);
        }

        // Records that the switch above is exhaustive: each known type assigns a
        // task and the default branch throws, so there is no path that falls
        // through leaving nothing built.
        assert task != null : "Every task type must either build a task or throw";

        if (isDone) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Interprets the saved completion flag.
     *
     * @param doneFlag Field expected to be {@code 0} or {@code 1}.
     * @return Whether the task was completed.
     * @throws LebronJamesException If the flag is anything else.
     */
    private static boolean parseDoneFlag(String doneFlag) throws LebronJamesException {
        if (doneFlag.equals("1")) {
            return true;
        }
        if (doneFlag.equals("0")) {
            return false;
        }
        throw new LebronJamesException("Completion flag must be 0 or 1 but was: " + doneFlag);
    }

    /**
     * Checks that a line has exactly the number of fields its task type needs.
     *
     * @param fields Fields read from the line.
     * @param expectedCount Number of fields the task type requires.
     * @throws LebronJamesException If the count does not match.
     */
    private static void requireFieldCount(String[] fields, int expectedCount) throws LebronJamesException {
        if (fields.length != expectedCount) {
            throw new LebronJamesException(
                    "Expected " + expectedCount + " fields but found " + fields.length + ".");
        }
    }

    /**
     * Checks that a required date or time field was not left blank.
     *
     * @param field Field to check.
     * @throws LebronJamesException If the field is empty.
     */
    private static void requireNonEmpty(String field) throws LebronJamesException {
        if (field.isEmpty()) {
            throw new LebronJamesException("Line has an empty date or time field.");
        }
    }
}
