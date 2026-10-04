package unicorn.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import unicorn.task.DeadlineTask;
import unicorn.task.EventTask;
import unicorn.task.Task;
import unicorn.task.TodoTask;

/**
 * Saves and loads the application's task data.
 */
public class Storage {
    private static final Path DATA_FILE = Path.of("data", "unicorn.txt");

    /**
     * Writes one task per line to {@code ./data/unicorn.txt}.
     *
     * @param tasks tasks to save
     * @throws IOException if the data directory or file cannot be written
     */
    public static void save(List<Task> tasks) throws IOException {
        save(DATA_FILE, tasks);
    }

    /**
     * Writes tasks to a specified file. This overload keeps automated tests
     * separate from the user's data file.
     *
     * @param dataFile file to write
     * @param tasks tasks to save
     * @throws IOException if the directory or file cannot be written
     */
    static void save(Path dataFile, List<Task> tasks) throws IOException {
        Path parentDirectory = dataFile.getParent();
        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }
        List<String> taskLines = tasks.stream()
                .map(Storage::toFileString)
                .toList();
        Files.write(dataFile, taskLines);
    }

    /**
     * Loads tasks from {@code ./data/unicorn.txt}. A first-time user has no data file,
     * so an empty task list is returned in that case.
     *
     * @return the saved tasks in their saved order, or an empty list for a first run
     * @throws IOException if an existing data file cannot be read
     * @throws IllegalArgumentException if the data file contains invalid task data
     */
    public static List<Task> load() throws IOException {
        return load(DATA_FILE);
    }

    /**
     * Loads tasks from a specified file.
     *
     * @param dataFile file to read
     * @return saved tasks, or an empty list when the file does not yet exist
     * @throws IOException if an existing file cannot be read
     * @throws IllegalArgumentException if the file contains invalid task data
     */
    static List<Task> load(Path dataFile) throws IOException {
        if (Files.notExists(dataFile)) {
            return List.of();
        }
        List<Task> tasks = new ArrayList<>();
        List<String> taskLines = Files.readAllLines(dataFile);
        for (int lineNumber = 0; lineNumber < taskLines.size(); lineNumber++) {
            String taskLine = taskLines.get(lineNumber);
            if (!taskLine.isBlank()) {
                Task task = toTask(taskLine, lineNumber + 1);
                if (tasks.stream().anyMatch(existingTask -> existingTask.hasSameDetailsAs(task))) {
                    throw invalidTaskData(lineNumber + 1);
                }
                tasks.add(task);
            }
        }
        return tasks;
    }

    /**
     * Converts a task to a line in the storage format.
     *
     * @param task task to convert
     * @return task type, completion status, and task details separated by vertical bars
     */
    private static String toFileString(Task task) {
        String completed = task.isDone() ? "1" : "0";
        if (task instanceof TodoTask) {
            return "T | " + completed + " | " + escapeField(task.getDescription());
        } else if (task instanceof DeadlineTask deadlineTask) {
            return "D | " + completed + " | " + escapeField(task.getDescription()) + " | "
                    + escapeField(deadlineTask.getBy().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        } else if (task instanceof EventTask eventTask) {
            return "E | " + completed + " | " + escapeField(task.getDescription()) + " | "
                    + escapeField(eventTask.getFrom()) + " | " + escapeField(eventTask.getTo());
        }
        throw new IllegalArgumentException("Unsupported task type");
    }

    /**
     * Recreates a task from one line in the storage format.
     *
     * @param taskLine task data read from the file
     * @return the task represented by the line
     */
    private static Task toTask(String taskLine, int lineNumber) {
        String[] parts = taskLine.split(" \\| ", -1);
        if (parts.length < 2 || !(parts[1].equals("0") || parts[1].equals("1"))) {
            throw invalidTaskData(lineNumber);
        }

        Task task;
        switch (parts[0]) {
            case "T":
                if (parts.length != 3) {
                    throw invalidTaskData(lineNumber);
                }
                task = new TodoTask(requireNonBlank(parts[2], lineNumber));
                break;
            case "D":
                if (parts.length != 4) {
                    throw invalidTaskData(lineNumber);
                }
                task = new DeadlineTask(requireNonBlank(parts[2], lineNumber),
                        parseDeadline(parts[3], lineNumber));
                break;
            case "E":
                if (parts.length != 5) {
                    throw invalidTaskData(lineNumber);
                }
                task = createEvent(parts[2], parts[3], parts[4], lineNumber);
                break;
            default:
                throw invalidTaskData(lineNumber);
        }
        if (parts[1].equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Creates a consistent error for malformed task data.
     *
     * @param lineNumber line containing invalid data
     * @return error explaining which line could not be read
     */
    private static IllegalArgumentException invalidTaskData(int lineNumber) {
        return new IllegalArgumentException("Invalid task data on line " + lineNumber + ".");
    }

    /**
     * Parses a deadline stored in ISO-8601 format.
     *
     * @param deadlineText stored deadline text
     * @param lineNumber source line number
     * @return parsed deadline date and time
     */
    private static LocalDateTime parseDeadline(String deadlineText, int lineNumber) {
        try {
            return LocalDateTime.parse(unescapeField(deadlineText, lineNumber));
        } catch (DateTimeParseException e) {
            throw invalidTaskData(lineNumber);
        }
    }

    private static EventTask createEvent(String description, String from, String to, int lineNumber) {
        try {
            return new EventTask(requireNonBlank(description, lineNumber),
                    requireNonBlank(from, lineNumber), requireNonBlank(to, lineNumber));
        } catch (IllegalArgumentException e) {
            throw invalidTaskData(lineNumber);
        }
    }

    private static String requireNonBlank(String field, int lineNumber) {
        String unescapedField = unescapeField(field, lineNumber);
        if (unescapedField.isBlank() || unescapedField.chars().anyMatch(Character::isISOControl)) {
            throw invalidTaskData(lineNumber);
        }
        return unescapedField;
    }

    /**
     * Escapes field characters that would otherwise be mistaken for separators.
     *
     * @param field task detail to escape
     * @return an escaped storage field
     */
    private static String escapeField(String field) {
        return field.replace("\\", "\\\\").replace("|", "\\|");
    }

    /**
     * Restores an escaped storage field.
     *
     * @param field escaped field text
     * @param lineNumber source line number for error reporting
     * @return unescaped task detail
     */
    private static String unescapeField(String field, int lineNumber) {
        StringBuilder result = new StringBuilder();
        boolean escaped = false;
        for (char character : field.toCharArray()) {
            if (escaped) {
                if (character != '\\' && character != '|') {
                    throw invalidTaskData(lineNumber);
                }
                result.append(character);
                escaped = false;
            } else if (character == '\\') {
                escaped = true;
            } else {
                result.append(character);
            }
        }
        if (escaped) {
            throw invalidTaskData(lineNumber);
        }
        return result.toString();
    }
}
