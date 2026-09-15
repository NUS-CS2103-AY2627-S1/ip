package tasque;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import tasque.task.Deadline;
import tasque.task.Event;
import tasque.task.Task;
import tasque.task.Todo;

/**
 * Loads and saves Tasque tasks using the application's storage format.
 */
public class Storage {
    private static final int TASK_MARKER_INDEX = 0;
    private static final int COMPLETION_INDEX = 1;
    private static final int DESCRIPTION_INDEX = 2;
    private static final int DEADLINE_DATE_INDEX = 3;
    private static final int EVENT_START_INDEX = 3;
    private static final int EVENT_END_INDEX = 4;
    // The suffix distinguishes Base64 descriptions from legacy plain-text records.
    private static final String ENCODED_DESCRIPTION_SUFFIX = "2";
    private static final String COMPLETED_MARKER = "1";
    private static final String INCOMPLETE_MARKER = "0";

    private final String filePath;

    /**
     * Creates a storage handler backed by the specified file path.
     *
     * @param filePath Path of the task data file.
     */
    public Storage(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Saves the current tasks to the data file.
     *
     * @param tasks Tasks to save.
     * @throws TasqueException If the tasks cannot be saved.
     */
    public void saveTasks(List<Task> tasks) throws TasqueException {
        Path tasquePath = Path.of(this.filePath).toAbsolutePath();
        Path parentDirectory = tasquePath.getParent();
        Path temporaryPath = null;
        try {
            Files.createDirectories(parentDirectory);
            temporaryPath = Files.createTempFile(parentDirectory, ".tasque-", ".tmp");
            writeTasksToFile(temporaryPath, tasks);
            replaceStorageFile(temporaryPath, tasquePath);
        } catch (IOException e) {
            throw new TasqueException("I couldn't save your tasks.");
        } finally {
            cleanupTemporaryFile(temporaryPath);
        }
    }

    private void writeTasksToFile(Path temporaryPath, List<Task> tasks) throws IOException {
        try (BufferedWriter taskWriter = Files.newBufferedWriter(
                temporaryPath, StandardCharsets.UTF_8)) {
            for (Task task : tasks) {
                taskWriter.write(task.toStorageString());
                taskWriter.write(System.lineSeparator());
            }
        }
    }

    private void replaceStorageFile(Path temporaryPath, Path tasquePath) throws IOException {
        try {
            Files.move(temporaryPath, tasquePath,
                    StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(temporaryPath, tasquePath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private void cleanupTemporaryFile(Path temporaryPath) {
        if (temporaryPath == null) {
            return;
        }
        try {
            Files.deleteIfExists(temporaryPath);
        } catch (IOException e) {
            // Cleanup failure must not replace the original save outcome.
        }
    }

    /**
     * Loads saved tasks from the data file.
     *
     * @return Saved tasks, or an empty list if no save file exists.
     * @throws TasqueException If the file cannot be read completely or contains an invalid record.
     */
    public List<Task> loadTasks() throws TasqueException {
        Path tasquePath = Path.of(this.filePath);
        ArrayList<Task> tasks = new ArrayList<>();
        if (Files.notExists(tasquePath, LinkOption.NOFOLLOW_LINKS)) {
            return tasks;
        }
        try (BufferedReader taskReader = Files.newBufferedReader(tasquePath, StandardCharsets.UTF_8)) {
            int lineNumber = 0;
            String line;
            while ((line = taskReader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) {
                    continue;
                }
                try {
                    tasks.add(parseStoredTask(line));
                } catch (TasqueException e) {
                    throw loadFailure("line " + lineNumber + ": " + e.getMessage());
                }
            }
        } catch (CharacterCodingException e) {
            throw loadFailure("the file contains invalid UTF-8");
        } catch (AccessDeniedException e) {
            throw loadFailure("the file could not be read (access denied)");
        } catch (IOException e) {
            throw loadFailure("the file could not be read");
        }
        return tasks;
    }

    private TasqueException loadFailure(String reason) {
        return new TasqueException("I couldn't load your tasks from " + this.filePath + ": " + reason + ".");
    }

    private Task parseStoredTask(String storageString) throws TasqueException {
        String[] parts = storageString.split("\\|", -1);
        String storedTaskMarker = parts[TASK_MARKER_INDEX].trim();
        int expectedFields = switch (storedTaskMarker) {
            case "T", "T2" -> 3;
            case "D", "D2" -> 4;
            case "E", "E2" -> 5;
            default -> throw new TasqueException("unknown task type");
        };
        if (parts.length != expectedFields) {
            throw new TasqueException("expected " + expectedFields + " fields but found " + parts.length);
        }
        String completion = parts[COMPLETION_INDEX].trim();
        if (!completion.equals(INCOMPLETE_MARKER) && !completion.equals(COMPLETED_MARKER)) {
            throw new TasqueException("invalid completion marker (expected 0 or 1)");
        }
        boolean hasEncodedDescription = storedTaskMarker.endsWith(ENCODED_DESCRIPTION_SUFFIX);
        String taskTypeSymbol = hasEncodedDescription
                ? storedTaskMarker.substring(0, storedTaskMarker.length() - ENCODED_DESCRIPTION_SUFFIX.length())
                : storedTaskMarker;
        String description = hasEncodedDescription
                ? decodeDescription(parts[DESCRIPTION_INDEX].trim())
                : parts[DESCRIPTION_INDEX].trim();
        if (description.isBlank()) {
            throw new TasqueException("the task description cannot be blank");
        }
        Task task;

        if (taskTypeSymbol.equals("T")) {
            task = new Todo(description);
        } else if (taskTypeSymbol.equals("D")) {
            try {
                task = new Deadline(description, parts[DEADLINE_DATE_INDEX].trim());
            } catch (DateTimeParseException e) {
                throw new TasqueException("invalid deadline date (expected yyyy-MM-dd)");
            }
        } else {
            String from = parts[EVENT_START_INDEX].trim();
            String to = parts[EVENT_END_INDEX].trim();
            if (from.isBlank() || to.isBlank()) {
                throw new TasqueException("event start and end values cannot be blank");
            }
            task = Event.fromStoredValues(description, from, to);
        }

        if (completion.equals(COMPLETED_MARKER)) {
            task.markAsDone();
        }
        return task;
    }

    private String decodeDescription(String encodedDescription) throws TasqueException {
        byte[] descriptionBytes;
        try {
            descriptionBytes = Base64.getDecoder().decode(encodedDescription);
        } catch (IllegalArgumentException e) {
            throw new TasqueException("invalid Base64 description");
        }
        try {
            return StandardCharsets.UTF_8.newDecoder().decode(ByteBuffer.wrap(descriptionBytes)).toString();
        } catch (CharacterCodingException e) {
            throw new TasqueException("the description is not valid UTF-8");
        }
    }
}
