package tungtung;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Saves and loads Tung Tung tasks from a text file on the hard disk.
 */
public class Storage {
    private final Path dataFile;
    /** Exact bytes last loaded or saved; null represents a missing file. */
    private byte[] lastContents;
    private boolean hasLoaded;

    /** Creates storage backed by the supplied file path. */
    public Storage(String filePath) {
        this.dataFile = Path.of(filePath);
    }

    /**
     * Writes every task to the data file, replacing its previous contents.
     *
     * @param tasks tasks to save
     * @throws IOException if the data directory or file cannot be written
     */
    public synchronized void save(ArrayList<Task> tasks) throws IOException {
        if (!hasLoaded) {
            load();
        }
        ArrayList<String> taskLines = new ArrayList<>();
        for (Task task : tasks) {
            String line = toFileLine(task);
            // Check the complete serialized record, including boundaries between fields.
            Task restored = fromFileLine(line, taskLines.size() + 1);
            if (!task.description.equals(restored.description) || !task.toString().equals(restored.toString())) {
                throw new IOException("Task cannot be saved without changing its details.");
            }
            taskLines.add(line);
        }
        String text = taskLines.isEmpty() ? "" : String.join("\n", taskLines) + "\n";
        ByteBuffer encoded = StandardCharsets.UTF_8.newEncoder().encode(CharBuffer.wrap(text));
        byte[] replacement = new byte[encoded.remaining()];
        encoded.get(replacement);
        Path dataDirectory = dataFile.toAbsolutePath().getParent();
        Files.createDirectories(dataDirectory);
        Path lockFile = dataFile.resolveSibling(dataFile.getFileName() + ".lock");
        // Keep the sidecar file: deleting it could let two writers lock different files.
        try (FileChannel channel = FileChannel.open(lockFile, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
                FileLock lock = channel.tryLock()) {
            if (lock == null) {
                throw new IOException("Another copy is saving tasks. Try again shortly.");
            }
            if (!Arrays.equals(lastContents, readContents())) {
                throw new IOException("Saved tasks changed outside this session. Restart before making changes.");
            }
            writeReplacement(dataDirectory, replacement);
            lastContents = replacement;
        } catch (OverlappingFileLockException exception) {
            throw new IOException("Another copy is saving tasks. Try again shortly.", exception);
        }
    }

    /** Writes a complete replacement before moving it over the saved file. */
    private void writeReplacement(Path directory, byte[] replacement) throws IOException {
        Path temporaryFile = Files.createTempFile(directory, "tungtung-", ".tmp");
        try {
            Files.write(temporaryFile, replacement);
            moveIntoPlace(temporaryFile);
        } catch (IOException | SecurityException exception) {
            try {
                Files.deleteIfExists(temporaryFile);
            } catch (IOException | SecurityException cleanupException) {
                exception.addSuppressed(cleanupException);
            }
            throw exception;
        }
    }

    /**
     * Loads every task stored in the data file. A missing data file represents an empty task list.
     *
     * @return the tasks reconstructed from the data file
     * @throws IOException if the file cannot be read or contains an invalid task line
     */
    public synchronized ArrayList<Task> load() throws IOException {
        byte[] contents = readContents();
        ArrayList<Task> tasks = new ArrayList<>();
        if (contents != null) {
            String text = StandardCharsets.UTF_8.newDecoder().decode(ByteBuffer.wrap(contents)).toString();
            String[] taskLines = text.split("\\r\\n|\\n|\\r", -1);
            for (int index = 0; index < taskLines.length; index++) {
                if (!taskLines[index].isBlank()) {
                    tasks.add(fromFileLine(taskLines[index], index + 1));
                }
            }
        }
        lastContents = contents;
        hasLoaded = true;
        return tasks;
    }

    /** Distinguishes a missing file from read failures so inaccessible data is never treated as empty. */
    private byte[] readContents() throws IOException {
        try {
            return Files.readAllBytes(dataFile);
        } catch (NoSuchFileException exception) {
            return null;
        }
    }

    /**
     * Converts one task to the text format used in the data file.
     *
     * @param task task to convert
     * @return a line containing the task type, completion status, and details
     */
    private static String toFileLine(Task task) throws IOException {
        if (task == null) {
            throw new IOException("Cannot save a missing task.");
        }
        if (task.description == null || task.description.isBlank()
                || task.description.contains("\n") || task.description.contains("\r")) {
            throw new IOException("Task descriptions must be non-empty and on a single line.");
        }
        String isDone = task.isDone ? "1" : "0";
        if (task instanceof ToDo) {
            return "T | " + isDone + " | " + task.description;
        }
        if (task instanceof Deadline) {
            Deadline deadline = (Deadline) task;
            return "D | " + isDone + " | " + task.description + " | " + deadline.by;
        }

        if (task instanceof Event) {
            Event event = (Event) task;
            return "E | " + isDone + " | " + task.description + " | " + event.from + " | " + event.to;
        }
        throw new IOException("Unsupported task type: " + task.getClass().getName());
    }

    /**
     * Reconstructs one task from a saved data-file line.
     *
     * @param taskLine saved task text
     * @param lineNumber one-based line number used in error messages
     * @return the reconstructed task
     * @throws IOException if the line does not match the supported file format
     */
    private static Task fromFileLine(String taskLine, int lineNumber) throws IOException {
        String[] parts = taskLine.split(" \\| ", -1);
        if (parts.length < 3 || !isValidStatus(parts[1]) || parts[2].isBlank()) {
            throw invalidTaskLine(lineNumber);
        }
        assert parts.length >= 3 : "A validated task line has at least type, status, and description fields.";
        assert isValidStatus(parts[1]) : "A validated task line has a binary completion status.";
        assert !parts[2].isBlank() : "A validated task line has a non-empty description.";

        Task task;
        switch (parts[0]) {
        case "T":
            if (parts.length != 3) {
                throw invalidTaskLine(lineNumber);
            }
            task = new ToDo(parts[2]);
            break;
        case "D":
            if (parts.length != 4 || parts[3].isBlank()) {
                throw invalidTaskLine(lineNumber);
            }
            task = new Deadline(parts[2], parseDate(parts[3], lineNumber));
            break;
        case "E":
            if (parts.length != 5 || parts[3].isBlank() || parts[4].isBlank()) {
                throw invalidTaskLine(lineNumber);
            }
            LocalDate from = parseDate(parts[3], lineNumber);
            LocalDate to = parseDate(parts[4], lineNumber);
            if (to.isBefore(from)) {
                throw invalidTaskLine(lineNumber);
            }
            task = new Event(parts[2], from, to);
            break;
        default:
            throw invalidTaskLine(lineNumber);
        }

        if (parts[1].equals("1")) {
            task.setDone();
        }
        assert task != null : "A valid task line must reconstruct a task.";
        return task;
    }

    /** Returns whether the saved completion status is supported. */
    private static boolean isValidStatus(String status) {
        return status.equals("0") || status.equals("1");
    }

    /**
     * Parses the ISO date stored in the data file.
     *
     * @param dateText ISO date text
     * @param lineNumber one-based line number used in error messages
     * @return parsed date
     * @throws IOException if the date is invalid
     */
    private static LocalDate parseDate(String dateText, int lineNumber) throws IOException {
        try {
            return LocalDate.parse(dateText);
        } catch (DateTimeParseException exception) {
            throw invalidTaskLine(lineNumber);
        }
    }

    /** Creates a clear error for malformed data without exposing parser details to the user. */
    private static IOException invalidTaskLine(int lineNumber) {
        return new IOException("Invalid task data at line " + lineNumber + ".");
    }

    /**
     * Replaces the old data file only after its complete replacement has been written.
     *
     * @param temporaryFile completed temporary file in the data directory
     * @throws IOException if the data file cannot be replaced
     */
    private void moveIntoPlace(Path temporaryFile) throws IOException {
        try {
            Files.move(temporaryFile, dataFile, StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (java.nio.file.AtomicMoveNotSupportedException exception) {
            Files.move(temporaryFile, dataFile, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
