package kibo.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import kibo.exception.StorageException;
import kibo.task.Deadline;
import kibo.task.Event;
import kibo.task.Task;
import kibo.task.TaskList;
import kibo.task.Todo;

/**
 * Saves and loads Kibo tasks in a simple text file.
 */
public class Storage {
    private static final Path SAVE_PATH = Path.of("data", "duke.txt");

    /**
     * Creates a storage helper for Kibo's task data.
     */
    public Storage() {
    }

    /**
     * Saves every task, replacing the previous saved list.
     *
     * @param tasks tasks to save.
     * @throws StorageException if the file cannot be written.
     */
    public static void save(TaskList tasks) throws StorageException {
        StringBuilder contents = new StringBuilder();
        for (Task task : tasks) {
            contents.append(toStorageLine(task)).append(System.lineSeparator());
        }

        Path temporaryPath = SAVE_PATH.resolveSibling(SAVE_PATH.getFileName() + ".tmp");
        try {
            Files.createDirectories(SAVE_PATH.getParent());
            Files.writeString(temporaryPath, contents.toString(), StandardCharsets.UTF_8);
            replaceSaveFile(temporaryPath);
        } catch (IOException exception) {
            throw new StorageException("I could not save your tasks to " + SAVE_PATH + ".");
        }
    }

    /**
     * Loads tasks saved during a previous run of Kibo.
     *
     * @return tasks reconstructed from the save file, or an empty list when no file exists.
     * @throws StorageException if the save file cannot be read or does not use the expected format.
     */
    public static ArrayList<Task> load() throws StorageException {
        ArrayList<Task> tasks = new ArrayList<>();
        if (Files.notExists(SAVE_PATH)) {
            return tasks;
        }

        try {
            List<String> lines = Files.readAllLines(SAVE_PATH, StandardCharsets.UTF_8);
            for (int index = 0; index < lines.size(); index++) {
                String line = lines.get(index);
                if (!line.isBlank()) {
                    tasks.add(parseSavedTask(line, index + 1));
                }
            }
            return tasks;
        } catch (IOException exception) {
            throw new StorageException("I could not read your saved tasks from " + SAVE_PATH + ".");
        }
    }

    /**
     * Converts one task to the on-disk format.
     *
     * @param task task to serialize.
     * @return text representation of the task.
     * @throws StorageException if a task field contains the storage delimiter.
     */
    private static String toStorageLine(Task task) throws StorageException {
        String doneStatus = task.isDone() ? "1" : "0";
        if (task instanceof Deadline deadline) {
            validateStorageText(deadline.getDescription());
            validateStorageText(deadline.getBy().toString());
            return "D | " + doneStatus + " | " + deadline.getDescription()
                    + " | " + deadline.getBy();
        }
        if (task instanceof Event event) {
            validateStorageText(event.getDescription());
            validateStorageText(event.getFrom());
            validateStorageText(event.getTo());
            return "E | " + doneStatus + " | " + event.getDescription()
                    + " | " + event.getFrom() + " | " + event.getTo();
        }
        validateStorageText(task.getDescription());
        return "T | " + doneStatus + " | " + task.getDescription();
    }

    /**
     * Replaces the saved file after its complete new contents have been written.
     *
     * @param temporaryPath complete temporary save file.
     * @throws IOException if the save file cannot be replaced.
     */
    private static void replaceSaveFile(Path temporaryPath) throws IOException {
        try {
            Files.move(temporaryPath, SAVE_PATH, StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(temporaryPath, SAVE_PATH, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Ensures a task field does not contain the delimiter used by the current file format.
     *
     * @param text task text to save.
     * @throws StorageException if the text would make the saved file ambiguous.
     */
    private static void validateStorageText(String text) throws StorageException {
        if (text.contains(" | ")) {
            throw new StorageException("Task text cannot contain \" | \" because it is used "
                    + "to save tasks.");
        }
    }

    /**
     * Reconstructs one task from a line in the save file.
     *
     * @param line one saved task.
     * @param lineNumber one-based line number used when reporting malformed data.
     * @return reconstructed task.
     * @throws StorageException if the line is not a supported saved-task format.
     */
    private static Task parseSavedTask(String line, int lineNumber) throws StorageException {
        String[] parts = line.split(" \\| ", -1);
        if (parts.length < 3 || (!parts[1].equals("0") && !parts[1].equals("1"))
                || parts[2].isBlank()) {
            throw invalidStorageLine(lineNumber);
        }

        Task task = parseSavedTaskByType(parts, lineNumber);
        if (parts[1].equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    private static Task parseSavedTaskByType(String[] parts, int lineNumber)
            throws StorageException {
        switch (parts[0]) {
            case "T":
                return parseSavedTodo(parts, lineNumber);
            case "D":
                return parseSavedDeadline(parts, lineNumber);
            case "E":
                return parseSavedEvent(parts, lineNumber);
            default:
                throw invalidStorageLine(lineNumber);
        }
    }

    private static Task parseSavedTodo(String[] parts, int lineNumber)
            throws StorageException {
        if (parts.length != 3) {
            throw invalidStorageLine(lineNumber);
        }
        return new Todo(parts[2]);
    }

    private static Task parseSavedDeadline(String[] parts, int lineNumber)
            throws StorageException {
        if (parts.length != 4 || parts[3].isBlank()) {
            throw invalidStorageLine(lineNumber);
        }

        try {
            return new Deadline(parts[2], LocalDate.parse(parts[3]));
        } catch (DateTimeParseException exception) {
            throw invalidStorageLine(lineNumber);
        }
    }

    private static Task parseSavedEvent(String[] parts, int lineNumber)
            throws StorageException {
        if (parts.length != 5 || parts[3].isBlank() || parts[4].isBlank()) {
            throw invalidStorageLine(lineNumber);
        }
        return new Event(parts[2], parts[3], parts[4]);
    }

    /**
     * Creates a clear error for a malformed saved task.
     *
     * @param lineNumber one-based line number in the save file.
     * @return storage exception describing the problem.
     */
    private static StorageException invalidStorageLine(int lineNumber) {
        return new StorageException("The saved task on line " + lineNumber
                + " has an invalid format.");
    }
}
