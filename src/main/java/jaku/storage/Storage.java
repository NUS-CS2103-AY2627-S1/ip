package jaku.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jaku.JakuException;
import jaku.task.Deadline;
import jaku.task.Event;
import jaku.task.Recurrence;
import jaku.task.RecurringEvent;
import jaku.task.RecurringTodo;
import jaku.task.Task;
import jaku.task.Todo;

/**
 * Saves Jaku tasks to, and loads them from, a human-readable text file.
 */
public class Storage {
    /** Location of Jaku's task data file. */
    private final Path dataFile;

    /**
     * Creates storage that reads and writes the given data file.
     *
     * @param dataFile location of the task data file
     */
    public Storage(Path dataFile) {
        this.dataFile = dataFile;
    }

    /**
     * Loads all valid tasks from disk.
     *
     * @return tasks in the same order as their saved records
     * @throws JakuException if the data file cannot be read
     */
    public List<Task> load() throws JakuException {
        if (Files.notExists(dataFile)) {
            return new ArrayList<>();
        }
        try {
            List<Task> tasks = new ArrayList<>();
            for (String line : Files.readAllLines(dataFile, StandardCharsets.UTF_8)) {
                Task task = parseTask(line);
                if (task == null) {
                    throw new JakuException("The saved data contains an invalid task record.");
                }
                tasks.add(task);
            }
            return tasks;
        } catch (IOException exception) {
            throw new JakuException("I couldn't load your saved tasks.");
        }
    }

    /**
     * Writes every task to disk, replacing the previous saved list.
     *
     * @param tasks tasks to save
     * @throws JakuException if the data file cannot be written
     */
    public void save(List<Task> tasks) throws JakuException {
        Path temporaryFile = null;
        try {
            Path parent = dataFile.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            List<String> lines = new ArrayList<>();
            for (Task task : tasks) {
                lines.add(formatTask(task));
            }
            Path directory = parent == null ? Path.of(".") : parent;
            temporaryFile = Files.createTempFile(directory, "jaku-", ".tmp");
            Files.write(temporaryFile, lines, StandardCharsets.UTF_8);
            replaceDataFile(temporaryFile);
            temporaryFile = null;
        } catch (IOException exception) {
            throw new JakuException("I couldn't save your tasks.");
        } finally {
            if (temporaryFile != null) {
                try {
                    Files.deleteIfExists(temporaryFile);
                } catch (IOException ignored) {
                    // The original save error is more useful to the user than a cleanup error.
                }
            }
        }
    }

    /**
     * Replaces the saved data file with a completed temporary file.
     *
     * @param temporaryFile file containing the complete new task list
     * @throws IOException if the replacement cannot be completed
     */
    private void replaceDataFile(Path temporaryFile) throws IOException {
        try {
            Files.move(temporaryFile, dataFile, StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(temporaryFile, dataFile, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Converts a task to its tab-separated saved representation.
     *
     * @param task task to serialize
     * @return one task record
     */
    private String formatTask(Task task) {
        String status = task.isDone() ? "1" : "0";
        if (task instanceof RecurringTodo recurringTodo) {
            return String.join("\t", "RT", status, escape(task.getDescription()),
                    recurringTodo.getNextOccurrence().toString(), recurringTodo.getRecurrence().getLabel());
        }
        if (task instanceof RecurringEvent recurringEvent) {
            return String.join("\t", "RE", status, escape(task.getDescription()),
                    recurringEvent.getFrom().toString(), recurringEvent.getTo().toString(),
                    recurringEvent.getRecurrence().getLabel());
        }
        if (task instanceof Todo) {
            return String.join("\t", "T", status, escape(task.getDescription()));
        }
        if (task instanceof Deadline deadline) {
            return String.join("\t", "D", status, escape(task.getDescription()), deadline.getBy().toString());
        }
        if (task instanceof Event event) {
            return String.join("\t", "E", status, escape(task.getDescription()),
                    escape(event.getFrom()), escape(event.getTo()));
        }
        throw new IllegalArgumentException("Unsupported task type: " + task.getClass().getName());
    }

    /**
     * Converts one saved line to a task, returning null when the line is malformed.
     *
     * @param line one task record from the data file
     * @return the restored task, or null when the record is invalid
     */
    private Task parseTask(String line) {
        String[] fields = line.split("\t", -1);
        if (fields.length < 3 || !(fields[1].equals("0") || fields[1].equals("1"))) {
            return null;
        }
        try {
            Task task = switch (fields[0]) {
            case "T" -> fields.length == 3 ? new Todo(unescape(fields[2])) : null;
            case "D" -> fields.length == 4 ? new Deadline(unescape(fields[2]), LocalDate.parse(fields[3])) : null;
            case "E" -> fields.length == 5
                    ? new Event(unescape(fields[2]), unescape(fields[3]), unescape(fields[4]))
                    : null;
            case "RT" -> fields.length == 5
                    ? new RecurringTodo(unescape(fields[2]), LocalDate.parse(fields[3]),
                    Recurrence.fromLabel(fields[4]))
                    : null;
            case "RE" -> fields.length == 6
                    ? new RecurringEvent(unescape(fields[2]), LocalDateTime.parse(fields[3]),
                    LocalDateTime.parse(fields[4]), Recurrence.fromLabel(fields[5]))
                    : null;
            default -> null;
            };
            if (task != null && fields[1].equals("1")
                    && !(task instanceof RecurringTodo) && !(task instanceof RecurringEvent)) {
                task.markAsDone();
            }
            return task;
        } catch (DateTimeException | IllegalArgumentException exception) {
            return null;
        }
    }

    /**
     * Escapes a text field so it can safely occupy one line and one tab-separated field.
     *
     * @param text text to escape
     * @return escaped text
     */
    private String escape(String text) {
        return text.replace("\\", "\\\\")
                .replace("\t", "\\t")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    /**
     * Restores escaped backslashes, tabs, and line breaks from a saved text field.
     *
     * @param text escaped text
     * @return original text
     * @throws IllegalArgumentException if the escape sequence is malformed
     */
    private String unescape(String text) {
        StringBuilder restored = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char character = text.charAt(i);
            if (character != '\\') {
                restored.append(character);
                continue;
            }
            if (++i >= text.length()) {
                throw new IllegalArgumentException("Incomplete escape sequence");
            }
            switch (text.charAt(i)) {
            case '\\' -> restored.append('\\');
            case 't' -> restored.append('\t');
            case 'n' -> restored.append('\n');
            case 'r' -> restored.append('\r');
            default -> throw new IllegalArgumentException("Unknown escape sequence");
            }
        }
        return restored.toString();
    }
}
