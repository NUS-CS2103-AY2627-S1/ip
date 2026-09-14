package toot.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import toot.TootException;
import toot.task.Deadline;
import toot.task.Event;
import toot.task.Task;
import toot.task.Todo;

/**
 * Saves tasks to a text file and restores them when the application starts.
 */
public class Storage {
    private static final String FIELD_SEPARATOR = " | ";

    private final Path filePath;

    /**
     * Creates a storage manager for the given file.
     *
     * @param filePath Relative or absolute path of the task data file.
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads all tasks from the data file.
     * Returns an empty list when the file does not exist yet.
     *
     * @return Tasks stored in the data file.
     * @throws TootException If the file cannot be read or contains invalid data.
     */
    public ArrayList<Task> load() throws TootException {
        ArrayList<Task> tasks = new ArrayList<>();
        if (Files.notExists(filePath)) {
            return tasks;
        }

        try {
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            for (int i = 0; i < lines.size(); i++) {
                if (!lines.get(i).isBlank()) {
                    tasks.add(parseTask(lines.get(i), i + 1));
                }
            }
            return tasks;
        } catch (IOException exception) {
            throw new TootException("Toot couldn't read saved tasks from '" + filePath + "'.");
        }
    }

    /**
     * Writes the complete task list to disk, creating missing parent folders.
     *
     * @param tasks Current task list.
     * @throws TootException If the data file cannot be written.
     */
    public void save(List<Task> tasks) throws TootException {
        List<String> lines = tasks.stream()
                .map(Storage::formatTask)
                .toList();

        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.write(filePath, lines, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new TootException("Toot couldn't save tasks to '" + filePath + "'.");
        }
    }

    /**
     * Converts a task into one human-editable line for the data file.
     */
    private static String formatTask(Task task) {
        String doneValue = task.isDone() ? "1" : "0";
        if (task instanceof Todo) {
            return String.join(FIELD_SEPARATOR, "T", doneValue, escape(task.getDescription()));
        }
        if (task instanceof Deadline deadline) {
            return String.join(FIELD_SEPARATOR, "D", doneValue, escape(task.getDescription()),
                    deadline.getBy().toString());
        }
        if (task instanceof Event event) {
            return String.join(FIELD_SEPARATOR, "E", doneValue, escape(task.getDescription()),
                    escape(event.getFrom()), escape(event.getTo()));
        }
        throw new IllegalArgumentException("Unsupported task type: " + task.getClass().getName());
    }

    /**
     * Reconstructs one task from a line in the data file.
     */
    private static Task parseTask(String line, int lineNumber) throws TootException {
        String[] fields = line.split(" \\| ", -1);
        try {
            String type = fields[0];
            boolean isDone = parseDoneValue(fields[1]);
            Task task;
            switch (type) {
                case "T":
                    requireFieldCount(fields, 3);
                    task = new Todo(unescape(fields[2]));
                    break;
                case "D":
                    requireFieldCount(fields, 4);
                    task = new Deadline(unescape(fields[2]), LocalDate.parse(fields[3]));
                    break;
                case "E":
                    requireFieldCount(fields, 5);
                    task = new Event(unescape(fields[2]), unescape(fields[3]), unescape(fields[4]));
                    break;
                default:
                    throw new IllegalArgumentException("unknown task type");
            }
            if (isDone) {
                task.markAsDone();
            }
            return task;
        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException | DateTimeParseException exception) {
            throw new TootException("Saved task data is invalid on line " + lineNumber + ".");
        }
    }

    /**
     * Validates the completion marker stored for a task.
     */
    private static boolean parseDoneValue(String value) {
        if (value.equals("1")) {
            return true;
        }
        if (value.equals("0")) {
            return false;
        }
        throw new IllegalArgumentException("invalid completion marker");
    }

    /**
     * Validates the number of fields stored for a task type.
     */
    private static void requireFieldCount(String[] fields, int expectedCount) {
        if (fields.length != expectedCount) {
            throw new IllegalArgumentException("incorrect field count");
        }
    }

    /**
     * Escapes characters that would otherwise break the line-based file format.
     */
    private static String escape(String value) {
        return value.replace("\\", "\\\\")
                .replace("|", "\\p")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    /**
     * Restores characters escaped in a stored field.
     */
    private static String unescape(String value) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char current = value.charAt(i);
            if (current != '\\') {
                result.append(current);
                continue;
            }
            if (i + 1 >= value.length()) {
                throw new IllegalArgumentException("incomplete escape sequence");
            }
            char escaped = value.charAt(++i);
            switch (escaped) {
                case '\\':
                    result.append('\\');
                    break;
                case 'p':
                    result.append('|');
                    break;
                case 'n':
                    result.append('\n');
                    break;
                case 'r':
                    result.append('\r');
                    break;
                default:
                    throw new IllegalArgumentException("unknown escape sequence");
            }
        }
        return result.toString();
    }
}
