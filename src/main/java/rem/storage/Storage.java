package rem.storage;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import rem.task.Deadline;
import rem.task.Event;
import rem.task.Task;
import rem.task.TaskDateTime;
import rem.task.Todo;

/**
 * Loads and saves Rem's tasks using a local data file.
 */
public class Storage {
    private static final String NOTE_PREFIX = "N:";
    private static final DateTimeFormatter BACKUP_TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    private final Path dataFile;
    private boolean hasLoadFailure;
    private boolean canStartFresh;

    /**
     * Creates storage that uses the specified data file.
     *
     * @param filePath Path of the task data file.
     */
    public Storage(String filePath) {
        dataFile = Path.of(filePath);
    }

    /**
     * Loads all tasks from the data file.
     *
     * @return Tasks reconstructed from the saved data, or an empty list if no data file exists.
     * @throws IOException If the data file cannot be read or contains an unknown task type.
     */
    public ArrayList<Task> loadTasks() throws IOException {
        try {
            ArrayList<Task> tasks = readTasks();
            hasLoadFailure = false;
            canStartFresh = false;
            return tasks;
        } catch (InvalidDataException e) {
            hasLoadFailure = true;
            canStartFresh = true;
            throw e;
        } catch (AccessDeniedException e) {
            hasLoadFailure = true;
            canStartFresh = false;
            throw new IOException("Access denied to the saved task file. Check its permissions and restart RemBot.",
                    e);
        } catch (IOException e) {
            hasLoadFailure = true;
            canStartFresh = false;
            throw e;
        }
    }

    /**
     * Reports whether the last load failed because the data contents were malformed.
     *
     * @return Whether starting with a safely backed-up empty file is available.
     */
    public boolean canStartFresh() {
        return canStartFresh;
    }

    /**
     * Backs up malformed task data and replaces it with an empty data file.
     *
     * @return Path of the backup containing the original data.
     * @throws IOException If recovery is unavailable or the backup or replacement cannot be created.
     */
    public Path startFresh() throws IOException {
        if (!hasLoadFailure || !canStartFresh) {
            throw new IOException("Starting fresh is only available after malformed task data is detected.");
        }

        Path source = dataFile.toAbsolutePath();
        Path backup = createBackupPath(source);
        try {
            Files.copy(source, backup);
            writeAtomically(source, List.of());
        } catch (IOException e) {
            throw new IOException("Could not safely back up and replace " + source
                    + ". The original file has not been changed. " + e.getMessage(), e);
        }
        hasLoadFailure = false;
        canStartFresh = false;
        return backup;
    }

    /** Creates a non-conflicting backup path beside the task data file. */
    private static Path createBackupPath(Path source) {
        String fileName = source.getFileName().toString();
        int extensionStart = fileName.lastIndexOf('.');
        String stem = extensionStart > 0 ? fileName.substring(0, extensionStart) : fileName;
        String extension = extensionStart > 0 ? fileName.substring(extensionStart) : "";
        String timestamp = LocalDateTime.now().format(BACKUP_TIMESTAMP_FORMAT);
        Path backup = source.resolveSibling(stem + "-corrupt-" + timestamp + extension);
        int suffix = 2;
        while (Files.exists(backup)) {
            backup = source.resolveSibling(stem + "-corrupt-" + timestamp + "-" + suffix + extension);
            suffix++;
        }
        return backup;
    }

    /** Reads all records before making any loaded tasks available. */
    private ArrayList<Task> readTasks() throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();
        if (Files.notExists(dataFile)) {
            return tasks;
        }

        if (Files.isDirectory(dataFile)) {
            throw new IOException("The saved task path is a directory. Choose a regular file and restart RemBot.");
        }
        List<String> taskLines = Files.readAllLines(dataFile);
        for (int i = 0; i < taskLines.size(); i++) {
            String taskLine = taskLines.get(i);
            if (taskLine.isBlank()) {
                continue;
            }

            String[] taskParts = taskLine.split(" \\| ", -1);
            Task task = createTask(taskParts, i + 1);
            if (taskParts[1].equals("1")) {
                task.markAsDone();
            }
            tasks.add(task);
        }
        return tasks;
    }

    /**
     * Writes the complete task list to disk, replacing the previously saved list.
     *
     * @param tasks Current tasks to save.
     * @throws IOException If the data directory or file cannot be written.
     */
    public void saveTasks(List<Task> tasks) throws IOException {
        if (hasLoadFailure) {
            throw new IOException("Saved tasks could not be loaded. Repair the data file or its permissions, "
                    + "then restart RemBot. The original file has been preserved.");
        }
        Path destination = dataFile.toAbsolutePath();
        try {
            Files.createDirectories(destination.getParent());
            List<String> taskLines = tasks.stream()
                    .map(Storage::toDataLine)
                    .toList();
            writeAtomically(destination, taskLines);
        } catch (IOException e) {
            throw new IOException("Could not save " + destination
                    + ". Check folder permissions and free disk space. " + e.getMessage(), e);
        }
    }

    /** Writes beside the destination so replacement is atomic on supported filesystems. */
    private static void writeAtomically(Path destination, List<String> taskLines) throws IOException {
        Path temporaryFile = Files.createTempFile(destination.getParent(), "rem-", ".tmp");
        try {
            Files.write(temporaryFile, taskLines);
            // Refuse an unsafe replacement if this filesystem does not support atomic moves.
            Files.move(temporaryFile, destination, StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            try {
                Files.deleteIfExists(temporaryFile);
            } catch (IOException cleanupError) {
                e.addSuppressed(cleanupError);
            }
            throw e;
        }
    }

    /**
     * Converts a task into one line of Rem's storage format.
     *
     * @param task Task to serialize.
     * @return Machine-readable representation of the task.
     */
    private static String toDataLine(Task task) {
        String status = task.isDone() ? "1" : "0";
        String taskData;
        String description = task.getDescription();
        boolean needsEncoding = description.contains(" | ");
        if (needsEncoding) {
            description = Base64.getEncoder().encodeToString(description.getBytes(StandardCharsets.UTF_8));
        }
        String version = needsEncoding ? "2" : "";
        if (task instanceof Deadline deadline) {
            taskData = String.join(" | ", "D" + version, status, description,
                    TaskDateTime.formatForStorage(deadline.getBy()));
        } else if (task instanceof Event event) {
            taskData = String.join(" | ", "E" + version, status, description,
                    TaskDateTime.formatForStorage(event.getFrom()),
                    TaskDateTime.formatForStorage(event.getTo()));
        } else {
            assert task instanceof Todo : "Storage only supports todo, deadline, and event tasks";
            taskData = String.join(" | ", "T" + version, status, description);
        }

        if (!task.hasNote()) {
            return taskData;
        }
        String encodedNote = Base64.getEncoder().encodeToString(
                task.getNote().getBytes(StandardCharsets.UTF_8));
        return taskData + " | " + NOTE_PREFIX + encodedNote;
    }

    /**
     * Reconstructs a task from the fields in one storage line.
     *
     * @param taskParts Fields from the storage line.
     * @param lineNumber One-based line number used in error messages.
     * @return Task represented by the fields.
     * @throws IOException If the fields do not represent a valid task.
     */
    private static Task createTask(String[] taskParts, int lineNumber) throws IOException {
        if (taskParts.length < 2 || (!taskParts[1].equals("0") && !taskParts[1].equals("1"))) {
            throw invalidDataLine(lineNumber);
        }

        if (taskParts[0].matches("[TDE]2")) {
            if (taskParts.length < 3) {
                throw invalidDataLine(lineNumber);
            }
            taskParts[2] = decodeText(taskParts[2], lineNumber);
            taskParts[0] = taskParts[0].substring(0, 1);
        }
        return switch (taskParts[0]) {
            case "T" -> {
                validateParts(taskParts, 3, lineNumber);
                yield applyNote(new Todo(taskParts[2]), taskParts, 3, lineNumber);
            }
            case "D" -> {
                validateParts(taskParts, 4, lineNumber);
                try {
                    Task deadline = new Deadline(taskParts[2], TaskDateTime.parse(taskParts[3]));
                    yield applyNote(deadline, taskParts, 4, lineNumber);
                } catch (DateTimeParseException e) {
                    throw invalidDataLine(lineNumber);
                }
            }
            case "E" -> {
                validateParts(taskParts, 5, lineNumber);
                try {
                    LocalDateTime from = TaskDateTime.parse(taskParts[3]);
                    LocalDateTime to = TaskDateTime.parse(taskParts[4]);
                    if (!to.isAfter(from)) {
                        throw invalidDataLine(lineNumber);
                    }
                    Task event = new Event(taskParts[2], from, to);
                    yield applyNote(event, taskParts, 5, lineNumber);
                } catch (DateTimeParseException e) {
                    throw invalidDataLine(lineNumber);
                }
            }
            default -> throw invalidDataLine(lineNumber);
        };
    }

    /**
     * Checks that a storage line has the required number of non-empty fields.
     *
     * @param taskParts Fields from the storage line.
     * @param expectedCount Required number of fields.
     * @param lineNumber One-based line number used in error messages.
     * @throws IOException If a field is missing or empty.
     */
    private static void validateParts(String[] taskParts, int expectedCount,
            int lineNumber) throws IOException {
        if (taskParts.length != expectedCount && taskParts.length != expectedCount + 1) {
            throw invalidDataLine(lineNumber);
        }
        for (int i = 2; i < expectedCount; i++) {
            if (taskParts[i].isBlank() || hasControlCharacters(taskParts[i])) {
                throw invalidDataLine(lineNumber);
            }
        }
    }

    /**
     * Decodes and attaches an optional final note field.
     *
     * @param task Task reconstructed from the required fields.
     * @param taskParts All fields from the storage line.
     * @param requiredCount Number of fields required when the task has no note.
     * @param lineNumber One-based line number used in error messages.
     * @return Reconstructed task with its note attached when present.
     * @throws IOException If the note field is malformed.
     */
    private static Task applyNote(Task task, String[] taskParts, int requiredCount,
            int lineNumber) throws IOException {
        if (taskParts.length == requiredCount) {
            return task;
        }

        String noteField = taskParts[requiredCount];
        if (!noteField.startsWith(NOTE_PREFIX)) {
            throw invalidDataLine(lineNumber);
        }
        String encodedNote = noteField.substring(NOTE_PREFIX.length());
        String note = decodeText(encodedNote, lineNumber);
        if (note.isBlank() || !note.equals(note.strip()) || hasControlCharacters(note)
                || note.codePointCount(0, note.length()) > Task.MAX_NOTE_LENGTH) {
            throw invalidDataLine(lineNumber);
        }
        task.setNote(note);
        return task;
    }

    /** Decodes canonical Base64 and rejects malformed UTF-8 instead of replacing characters. */
    private static String decodeText(String encodedText, int lineNumber) throws IOException {
        try {
            byte[] bytes = Base64.getDecoder().decode(encodedText);
            if (!Base64.getEncoder().encodeToString(bytes).equals(encodedText)) {
                throw invalidDataLine(lineNumber);
            }
            return StandardCharsets.UTF_8.newDecoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT)
                    .decode(ByteBuffer.wrap(bytes)).toString();
        } catch (IllegalArgumentException | CharacterCodingException e) {
            throw invalidDataLine(lineNumber);
        }
    }

    /** Checks single-line text, allowing tabs but no other control characters. */
    private static boolean hasControlCharacters(String text) {
        return text.codePoints().anyMatch(value -> Character.isISOControl(value) && value != '\t'
                || value == 0x2028 || value == 0x2029);
    }

    /**
     * Creates a consistent exception for a malformed storage line.
     *
     * @param lineNumber One-based number of the malformed line.
     * @return Exception describing where invalid data was found.
     */
    private static IOException invalidDataLine(int lineNumber) {
        return new InvalidDataException("Invalid task data on line " + lineNumber + ".");
    }

    /** Identifies malformed contents separately from file access failures. */
    private static class InvalidDataException extends IOException {
        private InvalidDataException(String message) {
            super(message);
        }
    }
}
