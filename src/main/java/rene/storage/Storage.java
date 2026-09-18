package rene.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import rene.exception.ReneException;
import rene.task.Deadline;
import rene.task.Event;
import rene.task.Task;
import rene.task.Todo;

/**
 * Loads tasks from a text file and saves task-list changes to that file.
 * Environment problems (missing permissions, an existing directory in the file's
 * place, ...) are reported as ReneException with a message that names the cause.
 */
public class Storage {
    private static final String FIELD_SEPARATOR = " | ";
    /** The escape character (a backslash, character code 92) used to protect field separators. */
    private static final String ESCAPE = String.valueOf((char) 92);
    /** The field-separator character that must be escaped in stored values. */
    private static final String PIPE = "|";

    private final Path filePath;

    /**
     * Contains the usable tasks and any malformed-line warnings found while loading.
     *
     * @param tasks the valid tasks reconstructed from the data file.
     * @param warnings messages describing lines that could not be reconstructed.
     */
    public record LoadResult(List<Task> tasks, List<String> warnings) {
        /**
         * Creates an immutable loading result.
         */
        public LoadResult {
            tasks = List.copyOf(tasks);
            warnings = List.copyOf(warnings);
        }
    }

    /**
     * Creates storage that reads from and writes to the given file.
     *
     * @param filePath the path of the task data file.
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads all saved tasks, creating an empty data file first when necessary.
     *
     * Malformed lines are reported separately so valid records remain available.
     *
     * @return the valid tasks and warnings about malformed lines.
     * @throws ReneException if the file itself cannot be read.
     */
    public LoadResult loadTasks() throws ReneException {
        try {
            ensureDataFileExists();
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            List<Task> tasks = new ArrayList<>();
            List<String> warnings = new ArrayList<>();
            for (int index = 0; index < lines.size(); index++) {
                String line = lines.get(index);
                if (!line.isBlank()) {
                    try {
                        tasks.add(parseTask(line, index + 1));
                    } catch (ReneException exception) {
                        warnings.add(exception.getMessage());
                    }
                }
            }
            return new LoadResult(tasks, warnings);
        } catch (IOException exception) {
            throw new ReneException(loadingMessage(exception), exception);
        }
    }

    /**
     * Replaces the data file contents with the current task list.
     *
     * @param tasks the tasks to save.
     * @throws ReneException if the file cannot be written.
     */
    public void saveTasks(List<Task> tasks) throws ReneException {
        List<String> lines = new ArrayList<>();
        for (Task task : tasks) {
            lines.add(formatTask(task));
        }

        try {
            ensureDataFileExists();
            if (!Files.isWritable(filePath)) {
                throw new AccessDeniedException(filePath.toString());
            }
            writeAtomically(lines);
        } catch (IOException exception) {
            throw new ReneException(savingMessage(exception), exception);
        }
    }

    /**
     * Writes to a temporary sibling before replacing the data file, preventing
     * an interrupted or failed write from leaving a partially written file.
     */
    private void writeAtomically(List<String> lines) throws IOException {
        Path absoluteFilePath = filePath.toAbsolutePath();
        Path parentDirectory = absoluteFilePath.getParent();
        Path temporaryFile = Files.createTempFile(parentDirectory, ".rene-", ".tmp");
        try {
            Files.write(temporaryFile, lines, StandardCharsets.UTF_8);
            try {
                Files.move(
                        temporaryFile,
                        absoluteFilePath,
                        StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(temporaryFile, absoluteFilePath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException exception) {
            try {
                Files.deleteIfExists(temporaryFile);
            } catch (IOException cleanupException) {
                exception.addSuppressed(cleanupException);
            }
            throw exception;
        }
    }

    /**
     * Creates the parent directory and data file when Rene is run for the first time.
     */
    private void ensureDataFileExists() throws IOException {
        Path parentDirectory = filePath.getParent();
        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }
        if (Files.exists(filePath)) {
            if (!Files.isRegularFile(filePath)) {
                throw new NotDirectoryException(filePath.toString());
            }
            return;
        }
        Files.createFile(filePath);
    }

    /**
     * Explains a loading failure in terms the user can act on.
     */
    private String loadingMessage(IOException exception) {
        if (exception instanceof AccessDeniedException) {
            return "I do not have permission to read " + filePath + ". "
                    + "Check the file's permissions and try again.";
        }
        if (exception instanceof NotDirectoryException) {
            return filePath + " is a folder, not a file, so I cannot read tasks from it.";
        }
        return "I couldn't load tasks from " + filePath + ".";
    }

    /**
     * Explains a saving failure in terms the user can act on.
     */
    private String savingMessage(IOException exception) {
        if (exception instanceof AccessDeniedException) {
            return "I do not have permission to save tasks to " + filePath + ". "
                    + "Check the file's permissions and try again.";
        }
        if (exception instanceof NotDirectoryException || exception instanceof DirectoryNotEmptyException) {
            return filePath + " is a folder, not a file, so I cannot save tasks there.";
        }
        return "I couldn't save tasks to " + filePath + ".";
    }

    /**
     * Converts a task to Rene's storage format.
     * User-controlled fields are escaped so a description containing the
     * field separator cannot corrupt the file on a later load.
     */
    private String formatTask(Task task) {
        String status = task.isDone() ? "1" : "0";
        String basicFields = task.getTaskType().getIcon()
                + FIELD_SEPARATOR + status
                + FIELD_SEPARATOR + escapeField(task.getDescription());

        return switch (task.getTaskType()) {
            case TODO -> basicFields;
            case DEADLINE -> basicFields + FIELD_SEPARATOR + ((Deadline) task).getDueDate();
            case EVENT -> basicFields
                    + FIELD_SEPARATOR + escapeField(((Event) task).getFrom())
                    + FIELD_SEPARATOR + escapeField(((Event) task).getTo());
        };
    }

    /**
     * Escapes the field separator (and the escape character itself) in a
     * user-controlled value before it is written to the data file.
     * The backslash is written as its character code so the source stays
     * free of escape sequences.
     */
    private String escapeField(String value) {
        String escaped = value.replace(ESCAPE, ESCAPE + ESCAPE)
                .replace(PIPE, ESCAPE + PIPE);
        return escaped;
    }

    /**
     * Reverses {@link #escapeField} for a value read back from the data file.
     */
    private String unescapeField(String value) {
        String unescaped = value.replace(ESCAPE + PIPE, PIPE)
                .replace(ESCAPE + ESCAPE, ESCAPE);
        return unescaped;
    }

    /**
     * Reconstructs one task from a line in Rene's storage format.
     */
    private Task parseTask(String line, int lineNumber) throws ReneException {
        String[] fields = line.split(" \\| ", -1);
        if (fields.length < 3) {
            throw invalidData(lineNumber);
        }

        Task task = switch (fields[0]) {
            case "T" -> parseTodo(fields, lineNumber);
            case "D" -> parseDeadline(fields, lineNumber);
            case "E" -> parseEvent(fields, lineNumber);
            default -> throw invalidData(lineNumber);
        };

        if (fields[1].equals("1")) {
            task.markAsDone();
        } else if (!fields[1].equals("0")) {
            throw invalidData(lineNumber);
        }
        return task;
    }

    /**
     * Reconstructs a todo from its stored fields.
     */
    private Task parseTodo(String[] fields, int lineNumber) throws ReneException {
        requireFieldCount(fields, 3, lineNumber);
        requireNonBlank(fields[2], lineNumber);
        return new Todo(unescapeField(fields[2]));
    }

    /**
     * Reconstructs a deadline from its stored fields.
     */
    private Task parseDeadline(String[] fields, int lineNumber) throws ReneException {
        requireFieldCount(fields, 4, lineNumber);
        requireNonBlank(fields[2], lineNumber);
        requireNonBlank(fields[3], lineNumber);
        try {
            return new Deadline(unescapeField(fields[2]), LocalDate.parse(fields[3]));
        } catch (DateTimeParseException exception) {
            throw invalidData(lineNumber);
        }
    }

    /**
     * Reconstructs an event from its stored fields.
     */
    private Task parseEvent(String[] fields, int lineNumber) throws ReneException {
        requireFieldCount(fields, 5, lineNumber);
        requireNonBlank(fields[2], lineNumber);
        requireNonBlank(fields[3], lineNumber);
        requireNonBlank(fields[4], lineNumber);
        return new Event(unescapeField(fields[2]), unescapeField(fields[3]), unescapeField(fields[4]));
    }

    /**
     * Rejects a stored task that has an unexpected number of fields.
     */
    private void requireFieldCount(String[] fields, int expectedCount, int lineNumber) throws ReneException {
        if (fields.length != expectedCount) {
            throw invalidData(lineNumber);
        }
    }

    /**
     * Rejects a required stored task field that is blank.
     */
    private void requireNonBlank(String field, int lineNumber) throws ReneException {
        if (field.isBlank()) {
            throw invalidData(lineNumber);
        }
    }

    /**
     * Creates a consistent error for malformed task data.
     */
    private ReneException invalidData(int lineNumber) {
        return new ReneException("I couldn't understand line " + lineNumber + " in " + filePath + ".");
    }
}
