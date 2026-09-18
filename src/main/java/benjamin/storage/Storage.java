package benjamin.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import benjamin.BenjaminException;
import benjamin.task.Deadline;
import benjamin.task.Event;
import benjamin.task.Task;
import benjamin.task.TaskDateTime;
import benjamin.task.TaskList;
import benjamin.task.Todo;

/**
 * Loads tasks from the save file and writes them back to it.
 *
 * <p>The path is built from separate name parts rather than one string with
 * slashes in it, so the same code works on any operating system.
 */
public class Storage {
    /** Fields in a todo record before the optional tags field. */
    private static final int TODO_FIELDS = 3;

    /** Fields in a deadline record before the optional tags field. */
    private static final int DEADLINE_FIELDS = 4;

    /** Fields in an event record before the optional tags field. */
    private static final int EVENT_FIELDS = 5;

    private final Path file;
    private final List<String> loadWarnings = new ArrayList<>();

    /**
     * Creates a storage backed by the given path, relative to the project root.
     *
     * @param first the first part of the path, such as {@code data}.
     * @param more the remaining parts, such as {@code benjamin.txt}.
     */
    public Storage(String first, String... more) {
        this.file = Paths.get(first, more);
    }

    /**
     * Returns the saved tasks. A missing file simply means nothing has been
     * saved yet, so an empty list is returned. Lines that are not in the
     * expected format are skipped and recorded in {@link #getLoadWarnings()},
     * so one bad line does not cost the user the rest of the list.
     *
     * @throws BenjaminException if the file exists but cannot be read at all.
     */
    public ArrayList<Task> load() throws BenjaminException {
        loadWarnings.clear();
        // Warnings are reported per load, so anything left from a previous call
        // would be blamed on this file.
        assert loadWarnings.isEmpty() : "warnings must not carry over between loads";

        ArrayList<Task> tasks = new ArrayList<>();

        if (!Files.exists(file)) {
            return tasks;
        }

        List<String> lines;
        try {
            lines = Files.readAllLines(file);
        } catch (IOException exception) {
            throw new BenjaminException("I could not read " + file + ".");
        }

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);

            if (line.isBlank()) {
                continue;
            }

            try {
                tasks.add(parseSavedTask(line));
            } catch (BenjaminException exception) {
                loadWarnings.add("Skipping line " + (i + 1) + " of the save file because "
                        + exception.getMessage());
            }
        }

        return tasks;
    }

    /** Returns the messages for lines skipped during the most recent load. */
    public List<String> getLoadWarnings() {
        return loadWarnings;
    }

    /**
     * Writes the whole task list to the save file, creating the folder first
     * if it is not there yet.
     *
     * @throws BenjaminException if the tasks cannot be written.
     */
    public void save(TaskList tasks) throws BenjaminException {
        try {
            Path folder = file.getParent();

            if (folder != null) {
                Files.createDirectories(folder);
            }

            Files.write(file, tasks.toSaveFormat());
        } catch (IOException exception) {
            throw new BenjaminException("I could not save your tasks.");
        }
    }

    /**
     * Rebuilds a task from one line of the save file.
     *
     * @throws BenjaminException if the line is not in the expected format.
     */
    private static Task parseSavedTask(String line) throws BenjaminException {
        String[] parts = line.split(" \\| ", -1);

        if (parts.length < 3) {
            throw new BenjaminException("it does not have enough fields.");
        }

        String type = parts[0].trim();
        String doneFlag = parts[1].trim();
        String description = parts[2].trim();

        if (!doneFlag.equals("0") && !doneFlag.equals("1")) {
            throw new BenjaminException("the done marker should be 0 or 1.");
        }
        if (description.isEmpty()) {
            throw new BenjaminException("the description is empty.");
        }

        Task task;
        int fieldsBeforeTags;
        switch (type) {
            case Todo.TYPE_LETTER:
                fieldsBeforeTags = TODO_FIELDS;
                requireFieldCount(parts, fieldsBeforeTags);
                task = new Todo(description);
                break;
            case Deadline.TYPE_LETTER:
                fieldsBeforeTags = DEADLINE_FIELDS;
                requireFieldCount(parts, fieldsBeforeTags);
                task = new Deadline(description,
                        TaskDateTime.parse(requireNonBlank(parts[3], "the /by field")));
                break;
            case Event.TYPE_LETTER:
                fieldsBeforeTags = EVENT_FIELDS;
                requireFieldCount(parts, fieldsBeforeTags);
                task = new Event(description,
                        TaskDateTime.parse(requireNonBlank(parts[3], "the /from field")),
                        TaskDateTime.parse(requireNonBlank(parts[4], "the /to field")));
                break;
            default:
                throw new BenjaminException("\"" + type + "\" is not a known task type.");
        }

        // Every arm of the switch either assigns a task or throws, so a null
        // here would mean a new task type was added without a matching arm.
        assert task != null : "a recognised record must produce a task";

        if (parts.length > fieldsBeforeTags) {
            applyTags(task, parts[fieldsBeforeTags]);
        }

        if (doneFlag.equals("1")) {
            task.markAsDone();
        }

        return task;
    }

    /**
     * Checks a record has the right number of fields for its type.
     *
     * <p>One extra field is allowed, and holds the tags. Records written before
     * tagging existed simply do not have it, which is why the count is a choice
     * of two rather than an exact match.
     *
     * @param fieldsBeforeTags how many fields the type needs without tags.
     * @throws BenjaminException if the count is neither of the two allowed.
     */
    private static void requireFieldCount(String[] parts, int fieldsBeforeTags)
            throws BenjaminException {
        if (parts.length != fieldsBeforeTags && parts.length != fieldsBeforeTags + 1) {
            throw new BenjaminException("type " + parts[0].trim() + " needs "
                    + fieldsBeforeTags + " fields, or " + (fieldsBeforeTags + 1)
                    + " with tags, but has " + parts.length + ".");
        }
    }

    /**
     * Attaches the tags held in a record's trailing field.
     *
     * <p>Tags are taken as they are found rather than validated. The field was
     * already split on the record separator, so a stored tag cannot contain
     * anything that would break the format, and a tag the app itself did not
     * write is harmless.
     */
    private static void applyTags(Task task, String tagsField) {
        String trimmed = tagsField.trim();

        if (trimmed.isEmpty()) {
            return;
        }

        for (String tag : trimmed.split("\\s+")) {
            task.addTag(tag);
        }
    }

    private static String requireNonBlank(String value, String fieldName) throws BenjaminException {
        String trimmed = value.trim();

        if (trimmed.isEmpty()) {
            throw new BenjaminException(fieldName + " is empty.");
        }

        return trimmed;
    }
}
