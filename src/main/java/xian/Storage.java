package xian;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;

/**
 * Deals with loading tasks from the save file and saving tasks to the save file.
 */
public class Storage {
    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("d/M/uuuu HHmm")
                    .withResolverStyle(ResolverStyle.STRICT);
    private final Path path;

    /**
     * Creates a Storage instance that reads from and writes to the given file path.
     *
     * @param filePath the path of the file used for saving/loading tasks.
     */
    public Storage(String filePath) {
        this.path = Paths.get(filePath);
    }

    /**
     * Creates a task from its saved-file fields.
     *
     * @param parts the fields read from one saved task line.
     * @return the task represented by the saved fields.
     * @throws XianException if the saved task type or fields are invalid.
     */
    private Task parseTask(String[] parts) throws XianException {
        if (parts.length < 2 || !(parts[1].equals("0") || parts[1].equals("1"))) {
            throw new XianException("Unable to load malformed saved task data");
        }

        return switch (parts[0]) {
            case "T" -> {
                validateSavedTaskParts(parts, 3);
                yield new Todo(parts[2]);
            }
            case "D" -> {
                validateSavedTaskParts(parts, 4);
                yield new Deadline(parts[2], LocalDateTime.parse(parts[3], DATE_TIME_FORMAT));
            }
            case "E" -> {
                validateSavedTaskParts(parts, 5);
                LocalDateTime from = LocalDateTime.parse(parts[3], DATE_TIME_FORMAT);
                LocalDateTime to = LocalDateTime.parse(parts[4], DATE_TIME_FORMAT);
                Parser.validateEventTimeRange(from, to);
                yield new Event(parts[2], from, to);
            }
            default -> throw new XianException("Unable to load invalid saved task type");
        };
    }

    /**
     * Validates the number and content of fields in one saved task record.
     *
     * @param parts the fields read from the saved task record.
     * @param expectedParts the expected number of fields.
     * @throws XianException if a field is missing or blank.
     */
    private void validateSavedTaskParts(String[] parts, int expectedParts) throws XianException {
        if (parts.length != expectedParts) {
            throw new XianException("Unable to load malformed saved task data");
        }

        for (String part : parts) {
            if (part.isBlank()) {
                throw new XianException("Unable to load malformed saved task data");
            }
        }
    }

    /**
     * Converts a task into its saved-file representation.
     *
     * @param task the task to convert.
     * @return the serialized task line.
     * @throws XianException if the task type is invalid.
     */
    private String serializeTask(Task task) throws XianException {
        String doneStatus = task.getStatusCode();

        if (task instanceof Todo) {
            return "T | " + doneStatus + " | " + task.getDescription();
        } else if (task instanceof Deadline deadline) {
            return "D | " + doneStatus + " | "
                    + task.getDescription() + " | "
                    + deadline.getByDate().format(DATE_TIME_FORMAT);
        } else if (task instanceof Event event) {
            return "E | " + doneStatus + " | "
                    + task.getDescription() + " | "
                    + event.getFrom().format(DATE_TIME_FORMAT) + " | "
                    + event.getTo().format(DATE_TIME_FORMAT);
        } else {
            throw new XianException("Invalid task type cannot be saved");
        }
    }

    /**
     * Loads tasks from the save file into a new TaskList.
     * If the save file does not exist, an empty TaskList is returned.
     *
     * @return the loaded TaskList.
     * @throws IOException if the save file cannot be read.
     * @throws XianException if a line in the save file is malformed or invalid.
     */
    public TaskList load() throws IOException, XianException {
        TaskList tasks = new TaskList();

        if (!Files.exists(path)) {
            return tasks;
        }

        List<String> lines = Files.readAllLines(path);

        for (int lineNumber = 0; lineNumber < lines.size(); lineNumber++) {
            String line = lines.get(lineNumber);
            String[] parts = line.split(" \\| ", -1);

            Task task;
            try {
                task = parseTask(parts);
            } catch (DateTimeParseException exception) {
                throw new XianException("Unable to load an invalid date on line "
                        + (lineNumber + 1) + ".");
            }

            if (parts[1].equals("1")) {
                task.mark();
            }

            tasks.add(task);
        }
        return tasks;
    }

    /**
     * Saves the given TaskList to the save file, creating any missing
     * parent directories as needed.
     *
     * @param tasks the TaskList to save.
     * @throws IOException if the save file cannot be written.
     * @throws XianException if the TaskList contains a task of an unrecognized type.
     */
    public void save(TaskList tasks) throws IOException, XianException {
        Path parent = path.getParent();

        if (parent != null) {
            Files.createDirectories(parent);
        }

        List<String> lines = new ArrayList<>();

        for (Task task : tasks) {
            lines.add(serializeTask(task));
        }

        Files.write(path, lines);
    }
}
