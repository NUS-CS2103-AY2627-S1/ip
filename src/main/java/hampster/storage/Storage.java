package hampster.storage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import hampster.exception.HampsterException;
import hampster.parser.DateTimeParser;
import hampster.parser.TagParser;
import hampster.task.Deadline;
import hampster.task.Event;
import hampster.task.Task;
import hampster.task.TaskList;
import hampster.task.ToDo;

/**
 * Handles saving tasks to and loading tasks from the persistent data file.
 */
public class Storage {

    /** The name of the file used to store tasks. */
    private static final String FILE_NAME = "data.txt";

    /**
     * Saves all tasks to the data file.
     *
     * <p>Each task is converted into its storage format using
     * {@link Task#saveString()}.</p>
     *
     * @param list the list of tasks to save
     * @return {@code true} when the data file was saved successfully
     */
    public static boolean save(List<Task> list) {
        if (list == null || list.stream().anyMatch(Objects::isNull)) {
            return false;
        }

        try {
            List<String> lines = new ArrayList<>();

            for (Task task : list) {
                lines.add(task.saveString());
            }

            Files.write(Path.of(FILE_NAME), lines);
            return true;

        } catch (IOException | SecurityException exception) {
            return false;
        }
    }

    /**
     * Loads tasks from the data file.
     *
     * <p>Blank lines are ignored. Invalid task records are skipped and
     * reported to the console.</p>
     *
     * @return a task list containing all valid stored tasks
     * @throws IOException if the data file cannot be read
     */
    public static TaskList load() throws IOException {
        Path path = Path.of(FILE_NAME);

        try {
            if (Files.notExists(path)) {
                return new TaskList();
            }

            TaskList tasks = new TaskList();

            try (Stream<String> lines = Files.lines(path)) {
                lines.forEach(line -> loadLine(line, tasks));
            }

            return tasks;
        } catch (SecurityException exception) {
            throw new IOException("Unable to access the data file.", exception);
        } catch (UncheckedIOException exception) {
            throw exception.getCause();
        }
    }

    /** Loads one non-blank save-file line into the task list. */
    private static void loadLine(String line, TaskList tasks) {
        if (line.isBlank()) {
            return;
        }

        try {
            tasks.add(parseTask(line));
        } catch (HampsterException | RuntimeException exception) {
            System.out.println(
                    "Savefile load() error: " + exception.getMessage()
            );
        }
    }

    /** Converts a save-file record into its corresponding task. */
    private static Task parseTask(String line) throws HampsterException {
        String[] parts = line.split("\\|", -1);

        return switch (parts[0]) {
            case "T" -> parseToDo(parts);
            case "D" -> parseDeadline(parts);
            case "E" -> parseEvent(parts);
            default -> throw new HampsterException(
                    "Unknown task type: " + parts[0]
            );
        };
    }

    /**
     * Parses a saved to-do task.
     *
     * <p>The expected format is
     * {@code T|status|description}.</p>
     *
     * @param parts the pipe-separated task fields
     * @return the parsed to-do task
     * @throws HampsterException if the task format is invalid
     */
    private static ToDo parseToDo(String[] parts)
            throws HampsterException {
        if (parts.length != 3 && parts.length != 4) {
            throw new HampsterException("Invalid ToDo format");
        }

        return new ToDo(
            parseStatus(parts[1]),
            parseDescription(parts[2]),
            parseTag(parts, 3)
        );
    }

    /**
     * Parses a saved deadline task.
     *
     * <p>The expected format is
     * {@code D|status|description|deadline}.</p>
     *
     * @param parts the pipe-separated task fields
     * @return the parsed deadline task
     * @throws HampsterException if the task format is invalid
     * @throws java.time.format.DateTimeParseException if the deadline
     *         has an invalid date format
     */
    private static Deadline parseDeadline(String[] parts)
            throws HampsterException {
        if (parts.length != 4 && parts.length != 5) {
            throw new HampsterException("Invalid Deadline format");
        }

        return new Deadline(
                parseStatus(parts[1]),
                parseDescription(parts[2]),
            DateTimeParser.parseFromSave(parts[3]),
            parseTag(parts, 4)
        );
    }

    /**
     * Parses a saved event task.
     *
     * <p>The expected format is
     * {@code E|status|description|start|end}.</p>
     *
     * @param parts the pipe-separated task fields
     * @return the parsed event task
     * @throws HampsterException if the task format is invalid
     * @throws java.time.format.DateTimeParseException if either event time
     *         has an invalid date format
     */
    private static Event parseEvent(String[] parts)
            throws HampsterException {
        if (parts.length != 5 && parts.length != 6) {
            throw new HampsterException("Invalid Event format");
        }

        return new Event(
                parseStatus(parts[1]),
                parseDescription(parts[2]),
                DateTimeParser.parseFromSave(parts[3]),
                DateTimeParser.parseFromSave(parts[4]),
                parseTag(parts, 5)
        );
    }

    /** Parses the only two valid task completion values used in storage. */
    private static boolean parseStatus(String status) throws HampsterException {
        if (status.equals("0")) {
            return false;
        }
        if (status.equals("1")) {
            return true;
        }
        throw new HampsterException("Invalid task completion status: " + status);
    }

    /** Rejects blank descriptions that would create unusable tasks. */
    private static String parseDescription(String description) throws HampsterException {
        if (description.isBlank()) {
            throw new HampsterException("Task description cannot be blank.");
        }
        return description;
    }

    /** Loads a valid tag, silently discarding an invalid stored tag. */
    private static String parseTag(String[] parts, int index) {
        if (parts.length <= index || parts[index].isEmpty()) {
            return "";
        }

        try {
            return TagParser.normalize(parts[index]);
        } catch (HampsterException exception) {
            return "";
        }
    }
}
