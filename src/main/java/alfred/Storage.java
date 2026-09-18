package alfred;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import alfred.task.Deadline;
import alfred.task.Event;
import alfred.task.Priority;
import alfred.task.Task;
import alfred.task.ToDo;

/**
 * Keeps the task list on the hard disk, one line per task:
 *
 * <pre>
 * T | 1 | read book
 * D | 0 | return book | 2019-10-15
 * E | 0 | project meeting | 2019-12-02 | 2019-12-03 | MEDIUM
 * </pre>
 *
 * <p>The fields are the type letter, 1 or 0 for done or not, the description,
 * then the type's own dates as {@code yyyy-mm-dd}, and finally a priority if
 * the task has one. A {@code |} inside a description is escaped.
 */
public class Storage {
    private static final String SEPARATOR = " | ";

    private static final char SEPARATOR_CHAR = '|';

    private static final char ESCAPE_CHAR = '\\';

    /** How many fields a line has, by type letter, not counting a priority. */
    private static final int FIELDS_TODO = 3;

    private static final int FIELDS_DEADLINE = 4;

    private static final int FIELDS_EVENT = 5;

    private static final int INDEX_TYPE = 0;

    private static final int INDEX_STATUS = 1;

    private static final int INDEX_DESCRIPTION = 2;

    /** A deadline's due date, or an event's start date. */
    private static final int INDEX_FIRST_DATE = 3;

    private static final int INDEX_SECOND_DATE = 4;

    private static final String BACKUP_SUFFIX = ".bak";

    /**
     * Holds what one call to {@link Storage#load()} found.
     *
     * @param tasks the tasks that were read, in the order they were saved.
     * @param skippedLines how many lines could not be understood.
     * @param backup where the file was copied to before any of it could be
     *        lost, or null if no line was skipped or the copy could not be
     *        made.
     */
    record LoadResult(List<Task> tasks, int skippedLines, Path backup) { }

    private final Path file;

    /**
     * Prepares to keep the tasks in one named file.
     *
     * @param filePath where to keep the tasks, such as {@code data/alfred.txt}.
     */
    Storage(String filePath) {
        this.file = Path.of(filePath);
    }

    /**
     * Writes the whole task list to the save file, replacing whatever it held
     * before, and creating the file and its folder if they are not there yet.
     *
     * @param tasks the tasks to save, in the order they are stored.
     * @throws AlfredException if the file cannot be written.
     */
    public void save(List<Task> tasks) throws AlfredException {
        List<String> lines = tasks.stream()
                .map(task -> joinFields(task.toFileFields()))
                .toList();
        try {
            Path folder = file.getParent();
            // Null when the path is a bare filename.
            if (folder != null) {
                Files.createDirectories(folder);
            }
            Files.write(file, lines);
        } catch (IOException e) {
            throw new AlfredException("I could not save your tasks, sir: " + describe(e));
        }
    }

    /**
     * Reads the saved tasks back. A missing file is not a problem. A bad line
     * is skipped rather than abandoning the whole file, and the file is copied
     * aside first, since the next save would drop the skipped lines for good.
     *
     * @return the tasks that could be read, how many lines were skipped, and
     *         where the file was copied to if any were.
     * @throws AlfredException if the file exists but cannot be read at all.
     */
    LoadResult load() throws AlfredException {
        List<Task> tasks = new ArrayList<>();
        if (!Files.exists(file)) {
            return new LoadResult(tasks, 0, null);
        }

        List<String> lines;
        try {
            lines = Files.readAllLines(file);
        } catch (IOException e) {
            throw new AlfredException("I could not read your saved tasks, sir: " + describe(e));
        }

        int skippedLines = 0;
        for (String line : lines) {
            if (line.isBlank()) {
                continue;
            }
            try {
                tasks.add(parseTask(line));
            } catch (AlfredException e) {
                skippedLines++;
            }
        }

        Path backup = skippedLines > 0 ? copyAside() : null;
        return new LoadResult(tasks, skippedLines, backup);
    }

    /**
     * Copies the save file to a backup beside it, overwriting an earlier one.
     *
     * @return where the copy was made, or null if the copy could not be made.
     */
    private Path copyAside() {
        Path backup = file.resolveSibling(file.getFileName() + BACKUP_SUFFIX);
        try {
            Files.copy(file, backup, StandardCopyOption.REPLACE_EXISTING);
            return backup;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Describes a file problem by the kind of failure and the path, since the
     * exception's own message is often only the path.
     *
     * @param e the problem that came back from the file system.
     * @return a short description of it.
     */
    private static String describe(IOException e) {
        String kind = e.getClass().getSimpleName();
        if (e.getMessage() == null) {
            return kind;
        }
        return kind + " on " + e.getMessage();
    }

    /**
     * Joins a task's fields into one line, escaping any separator character
     * inside a field.
     *
     * @param fields the task's fields, in the order they are saved.
     * @return the line to write to the file.
     */
    private static String joinFields(List<String> fields) {
        String line = fields.stream()
                .map(Storage::escape)
                .collect(Collectors.joining(SEPARATOR));

        // escape() and splitFields() have to be changed together to keep this true.
        assert splitFields(line).equals(fields) : "line does not read back as its fields: " + line;

        return line;
    }

    /**
     * Marks the characters in a field that would otherwise be read as a
     * separator. The escape character is escaped first; doing it second would
     * escape the marks added for the separators as well.
     *
     * @param field one field of a line, as the user typed it.
     * @return the field as it is written to the file.
     */
    private static String escape(String field) {
        String mark = String.valueOf(ESCAPE_CHAR);
        String separator = String.valueOf(SEPARATOR_CHAR);
        return field.replace(mark, mark + mark)
                .replace(separator, mark + separator);
    }

    /**
     * Splits a line into its fields and undoes the escaping in one pass.
     *
     * @param line one line of the save file.
     * @return the fields of that line, in order, with the escaping removed.
     */
    private static List<String> splitFields(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            char current = line.charAt(i);
            if (current == ESCAPE_CHAR && i + 1 < line.length()) {
                field.append(line.charAt(i + 1));
                i++;
            } else if (current == SEPARATOR_CHAR) {
                fields.add(field.toString().trim());
                field.setLength(0);
            } else {
                field.append(current);
            }
        }
        fields.add(field.toString().trim());
        return fields;
    }

    /**
     * Builds the task that one line of the save file describes. Every part of
     * the line is checked, because the file can be edited by hand.
     *
     * @param line a single line of the save file.
     * @return the task that line describes.
     * @throws AlfredException if the line is not a task this program wrote.
     */
    private static Task parseTask(String line) throws AlfredException {
        List<String> fields = splitFields(line);
        assert !fields.isEmpty() : "splitFields returns at least one field";

        String type = fields.get(INDEX_TYPE);
        Task task = switch (type) {
        case "T" -> parseToDo(fields);
        case "D" -> parseDeadline(fields);
        case "E" -> parseEvent(fields);
        default -> throw new AlfredException("Unknown task type: " + type);
        };
        applyStatus(task, fields.get(INDEX_STATUS));
        return task;
    }

    /**
     * Builds the todo that one save line describes.
     *
     * @param fields the fields of a line whose type letter is {@code T}.
     * @return the todo those fields describe.
     * @throws AlfredException if the line cannot be read as a todo.
     */
    private static Task parseToDo(List<String> fields) throws AlfredException {
        checkFieldCount(fields, FIELDS_TODO);
        return withPriority(new ToDo(readDescription(fields)), fields, FIELDS_TODO);
    }

    /**
     * Builds the deadline that one save line describes.
     *
     * @param fields the fields of a line whose type letter is {@code D}.
     * @return the deadline those fields describe.
     * @throws AlfredException if the line cannot be read as a deadline.
     */
    private static Task parseDeadline(List<String> fields) throws AlfredException {
        checkFieldCount(fields, FIELDS_DEADLINE);
        return withPriority(new Deadline(readDescription(fields),
                Dates.parse(fields.get(INDEX_FIRST_DATE))), fields, FIELDS_DEADLINE);
    }

    /**
     * Builds the event that one save line describes.
     *
     * @param fields the fields of a line whose type letter is {@code E}.
     * @return the event those fields describe.
     * @throws AlfredException if the line cannot be read as an event.
     */
    private static Task parseEvent(List<String> fields) throws AlfredException {
        checkFieldCount(fields, FIELDS_EVENT);
        return withPriority(new Event(readDescription(fields),
                Dates.parse(fields.get(INDEX_FIRST_DATE)),
                Dates.parse(fields.get(INDEX_SECOND_DATE))), fields, FIELDS_EVENT);
    }

    /**
     * Refuses a line that does not hold the fields its type calls for, with or
     * without the one a priority adds at the end.
     *
     * @param fields the fields the line was split into.
     * @param baseFields how many fields the type calls for without a priority.
     * @throws AlfredException if the line holds any other number of them.
     */
    private static void checkFieldCount(List<String> fields, int baseFields)
            throws AlfredException {
        if (fields.size() != baseFields && fields.size() != baseFields + 1) {
            throw new AlfredException("A " + fields.get(INDEX_TYPE) + " line needs "
                    + baseFields + " fields, or " + (baseFields + 1) + " with a priority");
        }
    }

    /**
     * Returns a task carrying the priority its save line gives it, or the task
     * unchanged if the line has no such field.
     *
     * @param task the task the rest of the line described.
     * @param fields the fields the line was split into.
     * @param baseFields how many fields the type carries before a priority.
     * @return the same task.
     * @throws AlfredException if the extra field names no level.
     */
    private static Task withPriority(Task task, List<String> fields, int baseFields)
            throws AlfredException {
        if (fields.size() > baseFields) {
            task.setPriority(Priority.parse(fields.get(baseFields)));
        }
        return task;
    }

    /**
     * Returns the description a save line carries.
     *
     * @param fields the fields the line was split into.
     * @return the description the line gives.
     * @throws AlfredException if the description is empty.
     */
    private static String readDescription(List<String> fields) throws AlfredException {
        String description = fields.get(INDEX_DESCRIPTION);
        if (description.isEmpty()) {
            throw new AlfredException("A task needs a description");
        }
        return description;
    }

    /**
     * Marks a task done or not done, as the status field of its save line says.
     *
     * @param task the task the rest of the line described.
     * @param status the status field of that line.
     * @throws AlfredException if the status is neither {@code 0} nor {@code 1}.
     */
    private static void applyStatus(Task task, String status) throws AlfredException {
        if (status.equals("1")) {
            task.markDone();
        } else if (!status.equals("0")) {
            throw new AlfredException("A status must be 0 or 1, not " + status);
        }
    }
}
