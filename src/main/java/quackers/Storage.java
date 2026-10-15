package quackers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads tasks from and saves tasks to a local text file.
 */
public class Storage {
    private static final String FIELD_SEPARATOR = "\t";

    private final Path filePath;

    /**
     * Creates storage backed by the specified file.
     *
     * @param filePath the relative or absolute path of the task file.
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads all saved tasks, returning an empty list when the file does not exist.
     *
     * @return the tasks stored in the file
     * @throws QuackersException if the file cannot be read or contains invalid data
     */
    public List<Task> load() throws QuackersException {
        if (Files.notExists(filePath)) {
            return new ArrayList<>();
        }

        try {
            List<Task> tasks = new ArrayList<>();
            for (String line : Files.readAllLines(filePath, StandardCharsets.UTF_8)) {
                tasks.add(parseTask(line));
            }
            return tasks;
        } catch (IOException error) {
            throw new QuackersException("Quack! I couldn't read the saved tasks.");
        }
    }

    /**
     * Saves all tasks, creating the parent directory and task file when necessary.
     *
     * @param tasks the tasks to save.
     * @throws QuackersException if the tasks cannot be written
     */
    public void save(List<Task> tasks) throws QuackersException {
        try {
            Path parentDirectory = filePath.getParent();
            if (parentDirectory != null) {
                Files.createDirectories(parentDirectory);
            }

            List<String> lines = new ArrayList<>();
            for (Task task : tasks) {
                lines.add(formatTask(task));
            }
            Files.write(filePath, lines, StandardCharsets.UTF_8);
        } catch (IOException error) {
            throw new QuackersException("Quack! I couldn't save the tasks.");
        }
    }

    /**
     * Converts one saved record into its corresponding task object.
     *
     * @param line the tab-separated task record.
     * @return the restored task
     * @throws QuackersException if the record has an invalid format
     */
    private Task parseTask(String line) throws QuackersException {
        String[] fields = line.split(FIELD_SEPARATOR, -1);
        if (fields.length < 3) {
            throw createInvalidDataException();
        }

        Task task = switch (fields[0]) {
            case "T" -> createTodo(fields);
            case "N" -> createNote(fields);
            case "D" -> createDeadline(fields);
            case "E" -> createEvent(fields);
            default -> throw createInvalidDataException();
        };

        if (fields[1].equals("1")) {
            if (!task.supportsCompletionStatus()) {
                throw createInvalidDataException();
            }
            task.markAsDone();
        } else if (!fields[1].equals("0")) {
            throw createInvalidDataException();
        }
        return task;
    }

    private Task createTodo(String[] fields) throws QuackersException {
        if (fields.length != 3) {
            throw createInvalidDataException();
        }
        return new Todo(fields[2]);
    }

    private Task createNote(String[] fields) throws QuackersException {
        if (fields.length != 3) {
            throw createInvalidDataException();
        }
        return new Note(fields[2]);
    }

    private Task createDeadline(String[] fields) throws QuackersException {
        if (fields.length != 4) {
            throw createInvalidDataException();
        }
        try {
            return new Deadline(fields[2], LocalDate.parse(fields[3]));
        } catch (DateTimeParseException error) {
            throw createInvalidDataException();
        }
    }

    private Task createEvent(String[] fields) throws QuackersException {
        if (fields.length != 5) {
            throw createInvalidDataException();
        }
        return new Event(fields[2], fields[3], fields[4]);
    }

    /**
     * Converts one task into a tab-separated record suitable for saving.
     *
     * @param task the task to format.
     * @return the task's saved representation
     * @throws QuackersException if the task type and task class do not agree
     */
    private String formatTask(Task task) throws QuackersException {
        String status = task.isDone() ? "1" : "0";
        return switch (task.getType()) {
            case TODO -> String.join(FIELD_SEPARATOR,
                    task.getType().getSymbol(), status, task.getDescription());
            case NOTE -> String.join(FIELD_SEPARATOR,
                    task.getType().getSymbol(), status, task.getDescription());
            case DEADLINE -> {
                if (!(task instanceof Deadline deadline)) {
                    throw createInvalidDataException();
                }
                yield String.join(FIELD_SEPARATOR, task.getType().getSymbol(), status,
                        task.getDescription(), deadline.getDueDate().toString());
            }
            case EVENT -> {
                if (!(task instanceof Event event)) {
                    throw createInvalidDataException();
                }
                yield String.join(FIELD_SEPARATOR, task.getType().getSymbol(), status,
                        task.getDescription(), event.getStartTime(), event.getEndTime());
            }
        };
    }

    private QuackersException createInvalidDataException() {
        return new QuackersException("Quack! The saved task file contains invalid data.");
    }
}
