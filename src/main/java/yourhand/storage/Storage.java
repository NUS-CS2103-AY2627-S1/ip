package yourhand.storage;

import yourhand.exceptions.CorruptFileException;
import yourhand.tasks.Deadline;
import yourhand.tasks.Event;
import yourhand.tasks.Task;
import yourhand.tasks.TaskDateTime;
import yourhand.tasks.TaskList;
import yourhand.tasks.Todo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Saves and loads YourHand task data from a file.
 */
public class Storage {
    private static final Path DEFAULT_FILE_PATH = Path.of("data", "yourhand.txt");
    private final Path filePath;

    /** Creates storage that uses YourHand's default data-file location. */
    public Storage() {
        this(DEFAULT_FILE_PATH);
    }

    /**
     * Creates storage that uses the given data-file location.
     *
     * @param filePath Location of the task data file.
     */
    public Storage(Path filePath) {
        this.filePath = Objects.requireNonNull(filePath, "file path must be provided");
    }

    /**
     * Saves every task in the list, replacing the previous saved data.
     *
     * @param taskList Tasks to save.
     * @throws IOException If the data directory or file cannot be written.
     */
    public void save(TaskList taskList) throws IOException {
        Objects.requireNonNull(taskList, "task list must be provided");
        Path parent = filePath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        List<String> taskLines = new ArrayList<>(taskList.getTasks().stream()
                .map(Task::toFileString)
                .toList());
        Path temporaryFile = Files.createTempFile(parent == null ? Path.of(".") : parent,
                filePath.getFileName().toString(), ".tmp");
        try {
            Files.write(temporaryFile, taskLines, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.WRITE,
                    StandardOpenOption.TRUNCATE_EXISTING);
            try {
                Files.move(temporaryFile, filePath, StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(temporaryFile, filePath, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporaryFile);
        }
    }

    /**
     * Loads saved tasks, or returns an empty list when no data file exists.
     *
     * @return Tasks reconstructed from the data file, or an empty list if it does not exist.
     * @throws IOException If the data file cannot be read.
     * @throws CorruptFileException If any saved task entry is malformed.
     */
    public TaskList load() throws IOException, CorruptFileException {
        TaskList taskList = new TaskList();
        if (!Files.exists(filePath)) {
            return taskList;
        }

        for (String taskLine : Files.readAllLines(filePath, StandardCharsets.UTF_8)) {
            if (taskLine.isBlank()) {
                continue;
            }
            try {
                taskList.add(parseTask(taskLine));
            } catch (IllegalArgumentException | IndexOutOfBoundsException exception) {
                throw new CorruptFileException(exception);
            }
        }
        return taskList;
    }

    /** Reconstructs one task from a well-formed data-file line. */
    private Task parseTask(String taskLine) {
        String[] fields = taskLine.split("\\|", -1);
        for (int index = 0; index < fields.length; index++) {
            fields[index] = fields[index].strip();
        }
        validateFields(fields);
        Task task = switch (fields[0]) {
        case "T" -> new Todo(fields[2]);
        case "D" -> new Deadline(fields[2], parseTaskDateTime(fields[3]));
        case "E" -> parseEvent(fields);
        default -> throw new IllegalArgumentException("Unknown task type in saved data.");
        };
        if (fields[1].equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /** Parses an ISO task date or date-time from the data file. */
    private TaskDateTime parseTaskDateTime(String dateText) {
        try {
            return new TaskDateTime(LocalDate.parse(dateText));
        } catch (DateTimeParseException exception) {
            try {
                return new TaskDateTime(LocalDateTime.parse(dateText));
            } catch (DateTimeParseException dateTimeException) {
                throw new IllegalArgumentException("Saved task date must use ISO date or date-time format.");
            }
        }
    }

    /** Reconstructs an event while ensuring its end date does not precede its start date. */
    private Event parseEvent(String[] fields) {
        TaskDateTime from = parseTaskDateTime(fields[3]);
        TaskDateTime to = parseTaskDateTime(fields[4]);
        if (!to.getValue().isAfter(from.getValue())) {
            throw new IllegalArgumentException("Saved event must end after it starts.");
        }
        return new Event(fields[2], from, to);
    }

    /** Validates a split data-file line before its fields are accessed. */
    private void validateFields(String[] fields) {
        if (fields.length < 3) {
            throw new IllegalArgumentException("A saved task needs a type, status, and description.");
        }
        if (!fields[1].equals("0") && !fields[1].equals("1")) {
            throw new IllegalArgumentException("Saved task status must be 0 or 1.");
        }
        if (fields[2].isBlank()) {
            throw new IllegalArgumentException("Saved task description cannot be empty.");
        }
        int expectedFieldCount = switch (fields[0]) {
        case "T" -> 3;
        case "D" -> 4;
        case "E" -> 5;
        default -> throw new IllegalArgumentException("Unknown task type in saved data.");
        };
        if (fields.length != expectedFieldCount) {
            throw new IllegalArgumentException("This task type has the wrong number of details.");
        }
        for (int index = 3; index < fields.length; index++) {
            if (fields[index].isBlank()) {
                throw new IllegalArgumentException("A saved task detail cannot be empty.");
            }
        }
    }
}
