package sace;

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

/**
 * Loads tasks from a text file and saves tasks back to that file.
 */
public class Storage {
    private static final String FIELD_SEPARATOR = " | ";

    private final Path filePath;

    /**
     * Creates storage that uses the given file.
     *
     * @param filePath relative or absolute path of the task data file.
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads all saved tasks, or returns an empty list when the file does not exist yet.
     *
     * @return tasks stored in the data file.
     * @throws SaceException if the file cannot be read or contains invalid data.
     */
    public ArrayList<Task> load() throws SaceException {
        ArrayList<Task> tasks = new ArrayList<>();
        if (!Files.exists(filePath)) {
            return tasks;
        }

        try {
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (!line.isBlank()) {
                    tasks.add(parseTask(line, i + 1));
                }
            }
        } catch (IOException e) {
            throw new SaceException(
                    "The royal archive could not load quests from " + filePath + ".");
        }
        return tasks;
    }

    /**
     * Saves every task, creating the parent folder and data file when necessary.
     *
     * @param tasks tasks to save.
     * @throws SaceException if the folder or file cannot be written.
     */
    public void save(List<Task> tasks) throws SaceException {
        ArrayList<String> lines = new ArrayList<>();
        for (Task task : tasks) {
            lines.add(serializeTask(task));
        }

        Path temporaryFile = null;
        try {
            Path absoluteFilePath = filePath.toAbsolutePath();
            Path parentFolder = absoluteFilePath.getParent();
            assert parentFolder != null : "An absolute data path must have a parent folder";
            Files.createDirectories(parentFolder);
            temporaryFile = Files.createTempFile(parentFolder, ".sace-", ".tmp");
            Files.write(temporaryFile, lines, StandardCharsets.UTF_8);
            replaceDataFile(temporaryFile, absoluteFilePath);
        } catch (IOException e) {
            throw new SaceException(
                    "The royal archive could not save quests to " + filePath + ".");
        } finally {
            deleteTemporaryFile(temporaryFile);
        }
    }

    /**
     * Replaces the data file atomically when the file system supports it.
     */
    private static void replaceDataFile(Path temporaryFile, Path targetFile)
            throws IOException {
        try {
            Files.move(temporaryFile, targetFile,
                    StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(temporaryFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Removes a leftover temporary file after an unsuccessful save.
     */
    private static void deleteTemporaryFile(Path temporaryFile) {
        if (temporaryFile == null) {
            return;
        }
        try {
            Files.deleteIfExists(temporaryFile);
        } catch (IOException ignored) {
            // A failed cleanup must not hide the original save error from the user.
        }
    }

    /**
     * Converts a task to the text format used in the data file.
     */
    private static String serializeTask(Task task) throws SaceException {
        assert task != null : "Stored task lists must not contain null values";
        String completionValue = task.isDone() ? "1" : "0";
        if (task instanceof Todo) {
            return String.join(
                    FIELD_SEPARATOR, "T", completionValue,
                    escapeField(task.getDescription()));
        } else if (task instanceof Deadline deadline) {
            return String.join(
                    FIELD_SEPARATOR, "D", completionValue,
                    escapeField(task.getDescription()), deadline.getBy().toString());
        } else if (task instanceof Event event) {
            return String.join(
                    FIELD_SEPARATOR, "E", completionValue,
                    escapeField(task.getDescription()), escapeField(event.getFrom()),
                    escapeField(event.getTo()));
        }
        throw new SaceException("The royal archive cannot preserve an unknown quest type.");
    }

    /**
     * Recreates one task from a line in the data file.
     */
    private static Task parseTask(String line, int lineNumber) throws SaceException {
        List<String> fields = splitFields(line, lineNumber);
        if (fields.size() < 3) {
            throw corruptedDataException(lineNumber);
        }

        boolean isDone = parseCompletionStatus(fields.get(1), lineNumber);
        Task task = createTask(fields, lineNumber);
        if (isDone) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Converts the stored completion flag into a boolean value.
     */
    private static boolean parseCompletionStatus(String completionValue, int lineNumber)
            throws SaceException {
        if (completionValue.equals("1")) {
            return true;
        }
        if (completionValue.equals("0")) {
            return false;
        }
        throw corruptedDataException(lineNumber);
    }

    /**
     * Recreates the task subtype described by a set of stored fields.
     */
    private static Task createTask(List<String> fields, int lineNumber) throws SaceException {
        String type = fields.get(0);
        if (type.equals("T") && fields.size() == 3) {
            return new Todo(requireValue(fields.get(2), lineNumber));
        }
        if (type.equals("D") && fields.size() == 4) {
            return createDeadline(fields, lineNumber);
        }
        if (type.equals("E") && fields.size() == 5) {
            return new Event(
                    requireValue(fields.get(2), lineNumber),
                    requireValue(fields.get(3), lineNumber),
                    requireValue(fields.get(4), lineNumber));
        }
        throw corruptedDataException(lineNumber);
    }

    /**
     * Recreates a deadline while translating an invalid stored date into a data error.
     */
    private static Deadline createDeadline(List<String> fields, int lineNumber)
            throws SaceException {
        String description = requireValue(fields.get(2), lineNumber);
        String dateText = requireValue(fields.get(3), lineNumber);
        try {
            return new Deadline(description, LocalDate.parse(dateText));
        } catch (DateTimeParseException e) {
            throw corruptedDataException(lineNumber);
        }
    }

    /**
     * Splits a saved line while preserving escaped pipe and backslash characters.
     */
    private static List<String> splitFields(String line, int lineNumber)
            throws SaceException {
        ArrayList<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean isEscaped = false;

        for (int i = 0; i < line.length(); i++) {
            char currentCharacter = line.charAt(i);
            if (isEscaped) {
                if (currentCharacter == '\\' || currentCharacter == '|') {
                    currentField.append(currentCharacter);
                } else if (currentCharacter == 'n') {
                    currentField.append('\n');
                } else if (currentCharacter == 'r') {
                    currentField.append('\r');
                } else {
                    throw corruptedDataException(lineNumber);
                }
                isEscaped = false;
            } else if (currentCharacter == '\\') {
                isEscaped = true;
            } else if (currentCharacter == '|') {
                fields.add(currentField.toString().trim());
                currentField.setLength(0);
            } else {
                currentField.append(currentCharacter);
            }
        }

        if (isEscaped) {
            throw corruptedDataException(lineNumber);
        }
        fields.add(currentField.toString().trim());
        return fields;
    }

    /**
     * Escapes characters that have a special meaning in the storage format.
     */
    private static String escapeField(String value) {
        return value.replace("\\", "\\\\")
                .replace("|", "\\|")
                .replace("\r", "\\r")
                .replace("\n", "\\n");
    }

    /**
     * Ensures a required saved field contains a value.
     */
    private static String requireValue(String value, int lineNumber) throws SaceException {
        if (value.isEmpty()) {
            throw corruptedDataException(lineNumber);
        }
        return value;
    }

    /**
     * Creates a consistent error for a malformed saved line.
     */
    private static SaceException corruptedDataException(int lineNumber) {
        return new SaceException("The royal archive is damaged at line " + lineNumber + ".");
    }
}
