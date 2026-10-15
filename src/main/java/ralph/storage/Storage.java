package ralph.storage;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import ralph.model.Deadline;
import ralph.model.Event;
import ralph.model.Task;
import ralph.model.Todo;

/**
 * Responsible for loading and saving tasks to a file.
 */
public class Storage {
    private static final String TYPE_TODO = "T";
    private static final String TYPE_DEADLINE = "D";
    private static final String TYPE_EVENT = "E";
    private static final String DONE = "1";
    private static final String SEPARATOR = " | ";

    private final Path dataDir;
    private final Path dataFile;

    /**
     * Constructs a Storage instance configured to read/write the provided file path.
     *
     * @param filePath path to the data file (may include a directory)
     */
    public Storage(String filePath) {
        Path path = Paths.get(filePath);
        this.dataDir = path.getParent() == null ? Paths.get(".") : path.getParent();
        this.dataFile = path;
    }

    /**
     * Loads saved tasks from the configured data file.
     *
     * @return a list of tasks loaded from disk; empty if the file does not exist
     * @throws IOException if reading the file fails unexpectedly
     */
    public List<Task> load() throws IOException {
        List<Task> tasks = new ArrayList<>();
        if (!Files.exists(dataFile)) {
            return tasks;
        }

        List<String> lines = Files.readAllLines(dataFile);
        for (String line : lines) {
            if (isBlank(line)) {
                continue;
            }

            Task parsed = parseStoredTask(line);
            if (parsed != null) {
                tasks.add(parsed);
            }
        }
        return tasks;
    }

    /**
     * Saves the provided tasks to the configured data file, creating directories as needed.
     *
     * @param tasks the tasks to persist
     * @throws IOException if writing to disk fails
     */
    public void save(List<Task> tasks) throws IOException {
        if (!Files.exists(dataDir)) {
            Files.createDirectories(dataDir);
        }
        try (BufferedWriter writer = Files.newBufferedWriter(dataFile)) {
            for (Task task : tasks) {
                String line = formatTask(task);
                if (line != null) {
                    writer.write(line);
                    writer.newLine();
                }
            }
        }
    }

    private boolean isBlank(String line) {
        return line == null || line.trim().isEmpty();
    }

    private Task parseStoredTask(String line) {
        String[] parts = line.split("\\s*\\|\\s*", -1);
        if (parts.length < 2) {
            return null;
        }

        String type = parts[0];
        boolean done = DONE.equals(parts[1]);
        try {
            switch (type) {
                case TYPE_TODO:
                    return parseTodo(parts, done);
                case TYPE_DEADLINE:
                    return parseDeadline(parts, done);
                case TYPE_EVENT:
                    return parseEvent(parts, done);
                default:
                    return null;
            }
        } catch (IllegalArgumentException ex) {
            System.out.println(" Warning: skipping malformed saved task: " + line);
            return null;
        }
    }

    private static Todo parseTodo(String[] parts, boolean done) {
        String description = parts.length > 2 ? parts[2] : "";
        Todo todo = new Todo(description);
        if (done) {
            todo.markAsDone();
        }
        return todo;
    }

    private static Deadline parseDeadline(String[] parts, boolean done) {
        String description = parts.length > 2 ? parts[2] : "";
        String byText = parts.length > 3 ? parts[3] : "";
        LocalDateTime by = tryParseDateTime(byText);
        if (by == null) {
            throw new IllegalArgumentException("Invalid date/time");
        }

        Deadline deadline = new Deadline(description, by);
        if (done) {
            deadline.markAsDone();
        }
        return deadline;
    }

    private static Event parseEvent(String[] parts, boolean done) {
        String description = parts.length > 2 ? parts[2] : "";
        String fromText = parts.length > 3 ? parts[3] : "";
        String toText = parts.length > 4 ? parts[4] : "";
        LocalDateTime from = tryParseDateTime(fromText);
        LocalDateTime to = tryParseDateTime(toText);
        if (from == null || to == null) {
            throw new IllegalArgumentException("Invalid date/time");
        }

        Event event = new Event(description, from, to);
        if (done) {
            event.markAsDone();
        }
        return event;
    }

    private static String formatTask(Task task) {
        String doneFlag = task.isDone() ? DONE : "0";
        if (task instanceof Todo) {
            return TYPE_TODO + SEPARATOR + doneFlag + SEPARATOR + task.getDescription();
        }
        if (task instanceof Deadline deadline) {
            return TYPE_DEADLINE + SEPARATOR + doneFlag + SEPARATOR + deadline.getDescription() + SEPARATOR
                + deadline.getBy();
        }
        if (task instanceof Event event) {
            return TYPE_EVENT + SEPARATOR + doneFlag + SEPARATOR + event.getDescription() + SEPARATOR
                + event.getFrom() + SEPARATOR + event.getTo();
        }
        return null;
    }

    /**
     * Try parsing in several common formats. Returns null on failure.
     */
    private static LocalDateTime tryParseDateTime(String input) {
        if (input == null) {
            return null;
        }
        String s = input.trim();
        if (s.isEmpty()) {
            return null;
        }
        try {
            if (s.contains("T")) {
                return LocalDateTime.parse(s);
            }
            if (s.contains(" ")) {
                String candidate = s.replace(' ', 'T');
                try {
                    return LocalDateTime.parse(candidate);
                } catch (DateTimeParseException ex) {
                    // Fall through to other formats.
                }
                String[] parts = s.split(" ");
                if (parts.length == 2) {
                    String datePart = parts[0];
                    String timePart = parts[1];
                    if (timePart.matches("\\d{4}")) {
                        String hhmm = timePart.substring(0, 2) + ":" + timePart.substring(2);
                        try {
                            return LocalDateTime.parse(datePart + "T" + hhmm);
                        } catch (DateTimeParseException ex) {
                            // Fall through to other formats.
                        }
                    } else if (timePart.matches("\\d{2}:\\d{2}")) {
                        try {
                            return LocalDateTime.parse(datePart + "T" + timePart);
                        } catch (DateTimeParseException ex) {
                            // Fall through to other formats.
                        }
                    }
                }
            }
            LocalDate d = LocalDate.parse(s);
            return d.atStartOfDay();
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Public wrapper around the internal parsing routine so other classes can reuse the behaviour.
     *
     * @param input the user-provided date/time string
     * @return parsed LocalDateTime or null when parsing fails
     */
    public static LocalDateTime tryParseDateTimeForReuse(String input) {
        return tryParseDateTime(input);
    }
}
