package littledaisy.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import littledaisy.exception.LittleDaisyException;
import littledaisy.task.Deadline;
import littledaisy.task.Event;
import littledaisy.task.Task;
import littledaisy.task.TaskList;
import littledaisy.task.Todo;

/** Loads and saves littleDaisy tasks in a local text file. */
public class Storage {
    private final Path filePath;

    /**
     * Creates storage backed by the specified path.
     *
     * @param filePath location used to load and save tasks
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads saved tasks, or returns an empty list if no data file exists.
     *
     * @return reconstructed tasks in their saved order
     * @throws LittleDaisyException if the file cannot be read or is malformed
     */
    public List<Task> load() throws LittleDaisyException {
        ArrayList<Task> tasks = new ArrayList<>();
        if (!Files.exists(filePath)) {
            return tasks;
        }

        try {
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            for (int i = 0; i < lines.size(); i++) {
                if (!lines.get(i).isBlank()) {
                    tasks.add(parseStoredTask(lines.get(i), i + 1));
                }
            }
            return tasks;
        } catch (IOException e) {
            throw new LittleDaisyException("I couldn't read the saved tasks.");
        }
    }

    /**
     * Writes all current tasks, creating the parent directory if needed.
     *
     * @param tasks task list to persist
     * @throws LittleDaisyException if the file cannot be written
     */
    public void save(TaskList tasks) throws LittleDaisyException {
        ArrayList<String> lines = new ArrayList<>();
        for (Task task : tasks.asList()) {
            lines.add(formatStoredTask(task));
        }

        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.write(filePath, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new LittleDaisyException("I couldn't save the task list.");
        }
    }

    /** Converts one task into a line of the data file. */
    private String formatStoredTask(Task task) {
        String done = task.isDone() ? "1" : "0";
        if (task instanceof Deadline deadline) {
            return String.join("\t", "D", done,
                    escapeField(task.getDescription()), deadline.getBy().toString());
        }
        if (task instanceof Event event) {
            return String.join("\t", "E", done,
                    escapeField(task.getDescription()),
                    escapeField(event.getFrom()), escapeField(event.getTo()));
        }
        return String.join("\t", "T", done, escapeField(task.getDescription()));
    }

    /** Recreates one task from a line of saved data. */
    private Task parseStoredTask(String line, int lineNumber)
            throws LittleDaisyException {
        String[] fields = line.split("\t", -1);
        Task task;
        try {
            switch (fields[0]) {
                case "T":
                    requireFieldCount(fields, 3);
                    task = new Todo(requireNotBlank(unescapeField(fields[2])));
                    break;
                case "D":
                    requireFieldCount(fields, 4);
                    task = Deadline.fromInput(requireNotBlank(unescapeField(fields[2])),
                            requireNotBlank(fields[3]));
                    break;
                case "E":
                    requireFieldCount(fields, 5);
                    task = new Event(requireNotBlank(unescapeField(fields[2])),
                            requireNotBlank(unescapeField(fields[3])),
                            requireNotBlank(unescapeField(fields[4])));
                    break;
                default:
                    throw new IllegalArgumentException();
            }
            if (fields[1].equals("1")) {
                task.markAsDone();
            } else if (!fields[1].equals("0")) {
                throw new IllegalArgumentException();
            }
            return task;
        } catch (IllegalArgumentException | LittleDaisyException e) {
            throw new LittleDaisyException(
                    "Saved task data is invalid at line " + lineNumber + ".");
        }
    }

    /** Rejects required stored fields that are empty or contain only spaces. */
    private String requireNotBlank(String field) {
        if (field.isBlank()) {
            throw new IllegalArgumentException();
        }
        return field;
    }

    /** Ensures a stored record has exactly the required number of fields. */
    private void requireFieldCount(String[] fields, int expected) {
        if (fields.length != expected) {
            throw new IllegalArgumentException();
        }
    }

    /** Escapes characters that have structural meaning in the data file. */
    private String escapeField(String field) {
        return field.replace("\\", "\\\\")
                .replace("\t", "\\t")
                .replace("\n", "\\n");
    }

    /** Restores a field escaped by {@link #escapeField(String)}. */
    private String unescapeField(String field) {
        StringBuilder result = new StringBuilder();
        boolean escaped = false;
        for (int i = 0; i < field.length(); i++) {
            char current = field.charAt(i);
            if (!escaped && current == '\\') {
                escaped = true;
            } else if (escaped) {
                if (current == 't') {
                    result.append('\t');
                } else if (current == 'n') {
                    result.append('\n');
                } else if (current == '\\') {
                    result.append('\\');
                } else {
                    throw new IllegalArgumentException();
                }
                escaped = false;
            } else {
                result.append(current);
            }
        }
        if (escaped) {
            throw new IllegalArgumentException();
        }
        return result.toString();
    }
}
