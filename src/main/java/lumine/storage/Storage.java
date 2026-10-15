package lumine.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lumine.LumineException;
import lumine.task.Deadline;
import lumine.task.Event;
import lumine.task.Task;
import lumine.task.Todo;

/**
 * Handles reading and writing the task list to a plain-text file on disk.
 *
 * <p>Each task is stored as a single pipe-delimited line. Saves are written to
 * a temporary file before that file replaces the existing save file, so a
 * failure while writing does not truncate the existing file.</p>
 */
public class Storage {
    //Error: not loaded
    private static final String LOAD_ERROR = "Sorry, I couldn't load your tasks. :C";
    private static final String SAVE_ERROR = "Sorry, I couldn't save your tasks. :C";

    private final Path saveFile;

    /**
     * Creates a new Storage that reads from and writes to the given file path.
     *
     * @param filePath path to the save file (parent directories are created on first save).
     */
    public Storage(String filePath) {
        this.saveFile = Path.of(filePath);
    }

    /**
     * Persists the given task list to disk, replacing any previous save file.
     * Writes to a temporary file before replacing the save file, so a write
     * failure does not truncate the existing file.
     *
     * @param tasks the list of tasks to save (must not be {@code null} or contain {@code null}).
     * @throws LumineException if the list is invalid or an I/O error occurs.
     */
    public void save(List<Task> tasks) {
        if (tasks == null || tasks.stream().anyMatch(task -> task == null)) {
            throw new LumineException(SAVE_ERROR);
        }

        Path temporaryFile = null;
        try {
            Path parent = saveFile.toAbsolutePath().getParent();
            if (parent == null) {
                throw new IOException("Save path has no parent directory");
            }
            Files.createDirectories(parent);
            if (Files.exists(saveFile) && !Files.isRegularFile(saveFile)) {
                throw new IOException("Save path is not a regular file");
            }
            String savedTasks = tasks.stream()
                    .map(Task::toStorageString)
                    .collect(Collectors.joining(System.lineSeparator()));
            temporaryFile = Files.createTempFile(parent, "lumine-", ".tmp");
            Files.writeString(temporaryFile, savedTasks);
            Files.move(temporaryFile, saveFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException | SecurityException e) {
            throw new LumineException(SAVE_ERROR);
        } finally {
            if (temporaryFile != null) {
                try {
                    Files.deleteIfExists(temporaryFile);
                } catch (IOException | SecurityException ignored) {
                    // Ignored
                }
            }
        }
    }

    /**
     * Reads and parses all tasks from the save file.
     * Returns an empty list when the file does not yet exist.
     *
     * @return list of tasks loaded from storage.
     * @throws LumineException if the file is malformed or an I/O error occurs.
     */
    public List<Task> load() {
        try {
            if (!Files.exists(saveFile)) {
                return new ArrayList<>();
            }
            if (!Files.isRegularFile(saveFile)) {
                throw new LumineException(LOAD_ERROR);
            }

            List<String> lines = Files.readAllLines(saveFile);
            return IntStream.range(0, lines.size())
                    .filter(i -> !lines.get(i).isBlank())
                    .mapToObj(i -> parseTask(lines.get(i), i + 1))
                    .collect(Collectors.toCollection(ArrayList::new));
        } catch (IOException | SecurityException e) {
            throw new LumineException(LOAD_ERROR);
        }
    }

    /**
     * Parses a single pipe-delimited line from the save file into a {@link Task}.
     * The first field is the type symbol ({@code T}, {@code D}, or {@code E}),
     * the second is the done flag ({@code 0} or {@code 1}), and the remaining
     * fields are task-type-specific.
     *
     * @param line       the raw pipe-delimited line text to split and parse.
     * @param lineNumber 1-based line number, used in error messages.
     * @return the reconstructed {@link Task}.
     * @throws LumineException identifying the line if its structure or task values are invalid.
     */
    private Task parseTask(String line, int lineNumber) {
        assert lineNumber > 0 : "Storage line number must be positive";
        List<String> parts = splitFields(line, lineNumber);
        if (parts.size() < 3 || (!parts.get(1).equals("0") && !parts.get(1).equals("1"))) {
            throw invalidLine(lineNumber);
        }

        Task task;
        try {
            task = switch (parts.get(0)) {
                case "T" -> {
                    if (parts.size() != 3 || parts.get(2).isBlank()) {
                        throw invalidLine(lineNumber);
                    }
                    yield new Todo(parts.get(2));
                }
                case "D" -> {
                    if (parts.size() != 4 || parts.get(2).isBlank() || parts.get(3).isBlank()) {
                        throw invalidLine(lineNumber);
                    }
                    yield new Deadline(parts.get(2), parts.get(3));
                }
                case "E" -> {
                    if (parts.size() != 5 || parts.get(2).isBlank()
                            || parts.get(3).isBlank() || parts.get(4).isBlank()) {
                        throw invalidLine(lineNumber);
                    }
                    yield new Event(parts.get(2), parts.get(3), parts.get(4));
                }
                default -> throw invalidLine(lineNumber);
            };
        } catch (LumineException exception) {
            // Report validation failures from task constructors as malformed saved records.
            throw invalidLine(lineNumber);
        }
        assert task != null : "A valid storage line must produce a task";

        if (parts.get(1).equals("1")) {
            task.markDone();
        }
        return task;
    }

    /**
     * Splits a storage line into its unescaped field values, using {@code |} as the
     * delimiter and {@code \} as the escape character.
     *
     * <p>Recognized escape sequences: {@code \|} → {@code |}, {@code \\} → {@code \},
     * {@code \n} → newline, {@code \r} → carriage return.  A trailing backslash
     * (dangling escape) is treated as a malformed line.</p>
     *
     * @param line       the raw line text to split.
     * @param lineNumber 1-based line number, used in error messages.
     * @return a list of unescaped field strings (trimmed of surrounding whitespace).
     * @throws LumineException if the line ends with an unmatched backslash.
     */
    private List<String> splitFields(String line, int lineNumber) {
        assert line != null : "Storage line must not be null";
        assert lineNumber > 0 : "Storage line number must be positive";
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean isEscapePending = false;
        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);
            if (isEscapePending) {
                switch (character) {
                    case 'n':
                        field.append('\n');
                        break;
                    case 'r':
                        field.append('\r');
                        break;
                    default:
                        field.append(character);
                        break;
                }
                isEscapePending = false;
            } else if (character == '\\') {
                isEscapePending = true;
            } else if (character == '|') {
                fields.add(field.toString().trim());
                field.setLength(0);
            } else {
                field.append(character);
            }
        }
        if (isEscapePending) {
            throw invalidLine(lineNumber);
        }
        fields.add(field.toString().trim());
        return fields;
    }

    /**
     * Creates a {@link LumineException} describing a malformed line in the save file.
     *
     * @param lineNumber 1-based number of the offending line.
     * @return the exception, ready to be thrown.
     */
    private LumineException invalidLine(int lineNumber) {
        return new LumineException(LOAD_ERROR + "\nInvalid saved task on line " + lineNumber + ".");
    }
}
