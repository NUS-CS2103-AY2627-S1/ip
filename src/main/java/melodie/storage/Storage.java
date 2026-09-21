package melodie.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

import melodie.task.Deadline;
import melodie.task.Event;
import melodie.task.Task;
import melodie.task.TaskList;
import melodie.task.Todo;

/**
 * Loads and saves Melodie's tasks using a text file.
 *
 * <p>Each task is stored on one line in one of the following formats:
 * <pre>
 * T | 1 | read book
 * D | 0 | return book | 2019-12-02T18:00
 * E | 0 | project meeting | 2019-12-02T14:00 | 2019-12-02T16:00
 * </pre>
 */
public class Storage {
    private static final String FIELD_SEPARATOR = " | ";
    private static final String FIELD_SEPARATOR_REGEX = Pattern.quote(FIELD_SEPARATOR);

    private final Path filePath;

    /**
     * Creates storage that uses Melodie's default data-file path.
     */
    public Storage() {
        this(Path.of("data", "Melodie.txt"));
    }

    /**
     * Creates storage that uses the specified data-file path.
     *
     * @param filePath Path of the file used to load and save tasks.
     */
    Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Reads all tasks from the data file.
     * Returns an empty list if the data file does not exist.
     *
     * @return Tasks loaded from the data file.
     * @throws IOException If the data file cannot be read.
     */
    public ArrayList<Task> read() throws IOException {
        if (!Files.exists(this.filePath)) {
            return new ArrayList<>();
        }

        ArrayList<Task> tasks = new ArrayList<>();
        try (Scanner scanner = new Scanner(this.filePath)) {
            int lineNumber = 0;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                lineNumber++;
                if (line.isBlank()) {
                    continue;
                }

                Task task = parseTask(line, lineNumber);
                if (task != null) {
                    tasks.add(task);
                }
            }
        }
        return tasks;
    }

    /**
     * Parses one storage line while preserving separators that form part of a description.
     *
     * @param line Storage line to parse.
     * @param lineNumber One-based line number used in malformed-data errors.
     * @return Parsed task, or {@code null} for an unknown task type.
     * @throws IOException If a known task record is malformed.
     */
    private static Task parseTask(String line, int lineNumber) throws IOException {
        String[] headerAndDetails = line.split(FIELD_SEPARATOR_REGEX, 3);
        if (headerAndDetails.length != 3) {
            throw createMalformedDataException(lineNumber, line);
        }

        String taskType = headerAndDetails[0];
        String statusCode = headerAndDetails[1];
        String taskDetails = headerAndDetails[2];
        if (!statusCode.equals("0") && !statusCode.equals("1")) {
            throw createMalformedDataException(lineNumber, line);
        }

        try {
            Task task = switch (taskType) {
                case "T" -> parseTodo(taskDetails, lineNumber, line);
                case "D" -> parseDeadline(taskDetails, lineNumber, line);
                case "E" -> parseEvent(taskDetails, lineNumber, line);
                default -> null;
            };
            if (task != null && statusCode.equals("1")) {
                task.mark();
            }
            return task;
        } catch (DateTimeParseException e) {
            throw createMalformedDataException(lineNumber, line, e);
        }
    }

    private static Todo parseTodo(String taskDetails, int lineNumber, String line)
            throws IOException {
        if (taskDetails.isBlank()) {
            throw createMalformedDataException(lineNumber, line);
        }
        return new Todo(taskDetails);
    }

    private static Deadline parseDeadline(String taskDetails, int lineNumber, String line)
            throws IOException {
        int dateSeparatorIndex = taskDetails.lastIndexOf(FIELD_SEPARATOR);
        if (dateSeparatorIndex <= 0
                || dateSeparatorIndex + FIELD_SEPARATOR.length() >= taskDetails.length()) {
            throw createMalformedDataException(lineNumber, line);
        }

        String description = taskDetails.substring(0, dateSeparatorIndex);
        String dueDateTime = taskDetails.substring(dateSeparatorIndex + FIELD_SEPARATOR.length());
        return new Deadline(description, LocalDateTime.parse(dueDateTime));
    }

    private static Event parseEvent(String taskDetails, int lineNumber, String line)
            throws IOException {
        int endDateSeparatorIndex = taskDetails.lastIndexOf(FIELD_SEPARATOR);
        int startDateSeparatorIndex = taskDetails.lastIndexOf(
                FIELD_SEPARATOR, endDateSeparatorIndex - 1);
        if (startDateSeparatorIndex <= 0
                || endDateSeparatorIndex <= startDateSeparatorIndex + FIELD_SEPARATOR.length()
                || endDateSeparatorIndex + FIELD_SEPARATOR.length() >= taskDetails.length()) {
            throw createMalformedDataException(lineNumber, line);
        }

        String description = taskDetails.substring(0, startDateSeparatorIndex);
        String startDateTime = taskDetails.substring(
                startDateSeparatorIndex + FIELD_SEPARATOR.length(), endDateSeparatorIndex);
        String endDateTime = taskDetails.substring(endDateSeparatorIndex + FIELD_SEPARATOR.length());
        return new Event(
                description,
                LocalDateTime.parse(startDateTime),
                LocalDateTime.parse(endDateTime));
    }

    private static IOException createMalformedDataException(int lineNumber, String line) {
        return new IOException("Malformed task data at line " + lineNumber + ": " + line);
    }

    private static IOException createMalformedDataException(
            int lineNumber, String line, Exception cause) {
        return new IOException("Malformed task data at line " + lineNumber + ": " + line, cause);
    }

    /**
     * Writes all tasks to the data file, creating its parent directory when needed.
     *
     * @param tasks Tasks to save.
     * @throws IOException If the data file cannot be written.
     */
    public void write(TaskList tasks) throws IOException {
        Files.createDirectories(this.filePath.getParent());

        ArrayList<String> lines = new ArrayList<>();

        for (Task task : tasks.getTasks()) {
            lines.add(task.toStorageString());
        }

        Files.write(this.filePath, lines);
    }
}
