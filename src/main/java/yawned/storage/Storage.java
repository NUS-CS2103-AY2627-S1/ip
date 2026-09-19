package yawned.storage;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import yawned.task.Deadline;
import yawned.task.Event;
import yawned.task.Task;
import yawned.task.ToDo;

/**
 * Loads tasks from and saves tasks to the application's storage file.
 */
public class Storage {
    private final Path saveFile;

    /**
     * Creates storage that uses the given file path.
     *
     * @param saveFile Path of the task storage file.
     */
    public Storage(Path saveFile) {
        this.saveFile = saveFile;
    }

    /**
     * Saves all tasks to disk, replacing the previous saved list.
     *
     * @param tasks Tasks to save.
     */
    public void saveTasks(List<Task> tasks) {
        Path temporaryFile = null;
        try {
            Files.createDirectories(saveFile.getParent());
            temporaryFile = saveFile.resolveSibling(saveFile.getFileName() + ".tmp");
            try (BufferedWriter writer = Files.newBufferedWriter(temporaryFile)) {
                for (Task task : tasks) {
                    writer.write(formatTaskForStorage(task));
                    writer.newLine();
                }
            }
            try {
                Files.move(temporaryFile, saveFile, StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(temporaryFile, saveFile, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException exception) {
            System.out.println("*yawn* I couldn't save that change. "
                    + "Your current session is still safe; please try again.");
        } finally {
            deleteTemporaryFile(temporaryFile);
        }
    }

    /**
     * Loads all valid tasks from disk.
     *
     * @return Loaded tasks, or an empty list if no storage file exists.
     */
    public List<Task> loadTasks() {
        List<Task> tasks = new ArrayList<>();
        if (!Files.exists(saveFile)) {
            return tasks;
        }
        try {
            List<String> storedTasks = Files.readAllLines(saveFile);
            for (int i = 0; i < storedTasks.size(); i++) {
                String storedTask = storedTasks.get(i);
                if (!storedTask.isBlank()) {
                    try {
                        tasks.add(createTaskFromStorage(storedTask));
                    } catch (IllegalArgumentException | DateTimeException exception) {
                        System.out.println("*yawn* I skipped an unreadable saved task on line " + (i + 1)
                                + ". It seems to have dozed off.");
                    }
                }
            }
        } catch (IOException exception) {
            System.out.println("*yawn* I couldn't read the saved tasks, so I'm starting with an empty list."
                    + " A clean slate, at least.");
        }
        return tasks;
    }

    /** Deletes a temporary storage file if one was created. */
    private static void deleteTemporaryFile(Path temporaryFile) {
        if (temporaryFile == null) {
            return;
        }
        try {
            Files.deleteIfExists(temporaryFile);
        } catch (IOException ignored) {
            // The temporary file will not affect the saved task list.
        }
    }

    /**
     * Recreates a task from one serialized storage line.
     *
     * @param storedTask Serialized task data.
     * @return Recreated task.
     */
    private static Task createTaskFromStorage(String storedTask) {
        List<String> fields = splitStorageFields(storedTask);
        validateStorageFields(fields);
        Task task = switch (fields.get(0)) {
            case "T" -> new ToDo(fields.get(2));
            case "D" -> new Deadline(fields.get(2), LocalDateTime.parse(fields.get(3)));
            case "E" -> new Event(fields.get(2), LocalDateTime.parse(fields.get(3)),
                    LocalDateTime.parse(fields.get(4)));
            default -> throw new IllegalArgumentException("Cannot load task type: " + fields.get(0));
        };
        if (fields.get(1).equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Splits one storage line while restoring escaped delimiters.
     *
     * @param storedTask Serialized task data.
     * @return Extracted storage fields.
     */
    private static List<String> splitStorageFields(String storedTask) {
        List<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        for (int i = 0; i < storedTask.length(); i++) {
            char character = storedTask.charAt(i);
            if (character == '\\' && i + 1 < storedTask.length()) {
                char nextCharacter = storedTask.charAt(i + 1);
                if (nextCharacter == '|' || nextCharacter == '\\') {
                    currentField.append(nextCharacter);
                    i++;
                    continue;
                }
            }
            if (character == '|') {
                fields.add(currentField.toString().trim());
                currentField.setLength(0);
            } else {
                currentField.append(character);
            }
        }
        fields.add(currentField.toString().trim());
        return fields;
    }

    /**
     * Validates the field count and required values for a serialized task.
     *
     * @param fields Extracted storage fields.
     */
    private static void validateStorageFields(List<String> fields) {
        if (fields.size() < 2 || (!fields.get(1).equals("0") && !fields.get(1).equals("1"))) {
            throw new IllegalArgumentException("Invalid task status.");
        }
        int expectedFieldCount = switch (fields.get(0)) {
            case "T" -> 3;
            case "D" -> 4;
            case "E" -> 5;
            default -> throw new IllegalArgumentException("Invalid task type.");
        };
        if (fields.size() != expectedFieldCount) {
            throw new IllegalArgumentException("Invalid number of task fields.");
        }
        for (int i = 2; i < fields.size(); i++) {
            if (fields.get(i).isBlank()) {
                throw new IllegalArgumentException("Task fields cannot be empty.");
            }
        }
    }

    /**
     * Formats a task as one storage line.
     *
     * @param task Task to format.
     * @return Serialized task data.
     */
    private static String formatTaskForStorage(Task task) {
        assert task != null : "Only existing tasks can be saved.";
        String commonFields = task.getStatus().getStorageValue() + " | " + escapeStorageField(task.getDescription());
        return switch (task) {
            case ToDo _ -> "T | " + commonFields;
            case Deadline deadline -> "D | " + commonFields + " | "
                    + escapeStorageField(deadline.getEndDate().toString());
            case Event event -> "E | " + commonFields + " | "
                    + escapeStorageField(event.getStartDate().toString())
                    + " | " + escapeStorageField(event.getEndDate().toString());
            default -> throw new IllegalArgumentException("Cannot save task type: " + task.getClass().getSimpleName());
        };
    }

    /**
     * Escapes separators in one value written to storage.
     *
     * @param field Field value to escape.
     * @return Escaped value.
     */
    private static String escapeStorageField(String field) {
        return field.replace("\\", "\\\\").replace("|", "\\|");
    }
}
