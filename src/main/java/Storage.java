import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Saves tasks to, and later loads them from, a plain-text file on the hard disk.
 *
 * <p>Each task occupies one line in the form {@code T | 1 | read book}, where the
 * first field is the task type code and the second field is {@code 1} when the
 * task is complete. The path is relative to the project root and is built with
 * {@link Paths#get(String, String...)} so it works on any operating system.
 */
public class Storage {
    /** Default save location, relative to the project root: {@code ./data/bibi.txt}. */
    private static final Path DEFAULT_FILE_PATH = Paths.get("data", "bibi.txt");

    private final Path filePath;

    /**
     * Creates storage that uses the default {@code ./data/bibi.txt} save file.
     */
    public Storage() {
        this(DEFAULT_FILE_PATH);
    }

    /**
     * Creates storage that uses the supplied save file, which is useful for testing.
     *
     * @param filePath location of the save file
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Returns the save file this storage reads from and writes to.
     *
     * @return the save file path
     */
    public Path getFilePath() {
        return filePath;
    }

    /**
     * Writes every task to the save file, replacing any previous contents.
     *
     * <p>The containing folder is created first when it does not exist yet, so a
     * fresh copy of the project can save tasks without any manual setup.
     *
     * @param tasks the tasks to store
     * @throws IOException if the folder or file cannot be written
     */
    public void save(TaskList tasks) throws IOException {
        Path parentFolder = filePath.getParent();
        if (parentFolder != null) {
            Files.createDirectories(parentFolder);
        }

        List<String> lines = new ArrayList<>();
        for (Task task : tasks.getTasks()) {
            lines.add(task.toFileFormat());
        }
        Files.write(filePath, lines);
    }

    /**
     * Holds the outcome of reading the save file.
     *
     * @param tasks the tasks that were understood
     * @param warnings one message for each line that had to be skipped
     */
    public record LoadReport(TaskList tasks, List<String> warnings) {
    }

    /**
     * Reads the saved tasks back into a task list.
     *
     * <p>A missing save file is normal rather than an error: someone running a
     * fresh copy of the project simply starts with an empty list. A damaged line
     * is skipped and reported instead of ending the program, so one bad line
     * cannot cost the user every other task.
     *
     * @return the restored tasks together with a warning for each skipped line
     * @throws IOException if the file exists but cannot be read
     */
    public LoadReport load() throws IOException {
        TaskList tasks = new TaskList();
        List<String> warnings = new ArrayList<>();

        if (!Files.exists(filePath)) {
            return new LoadReport(tasks, warnings);
        }
        if (!Files.isRegularFile(filePath)) {
            warnings.add(filePath + " is not a file, so no tasks could be read.");
            return new LoadReport(tasks, warnings);
        }

        List<String> lines;
        try {
            lines = Files.readAllLines(filePath);
        } catch (MalformedInputException exception) {
            // The file exists but is not readable text, so treat the whole file as
            // damaged rather than letting the decoding error end the program.
            warnings.add("The file is not readable text, so none of it could be used.");
            return new LoadReport(tasks, warnings);
        }

        for (int lineNumber = 1; lineNumber <= lines.size(); lineNumber++) {
            String line = lines.get(lineNumber - 1);
            if (line.isBlank()) {
                continue;
            }
            try {
                tasks.add(parseTask(line));
            } catch (BibiException exception) {
                warnings.add("Line " + lineNumber + ": " + exception.getMessage());
            }
        }
        return new LoadReport(tasks, warnings);
    }

    /**
     * Rebuilds one task from its save-file line.
     *
     * @param line a line written by {@link Task#toFileFormat()}
     * @return the task described by the line
     * @throws BibiException if the line does not describe a valid task
     */
    private Task parseTask(String line) throws BibiException {
        // Split on the separator and drop the spaces padding it, so the fields
        // come back exactly as the user typed them.
        String[] fields = line.trim().split("\\s*\\" + Task.FIELD_SEPARATOR + "\\s*");
        if (fields.length < 3) {
            throw new BibiException("expected at least type, status, and description "
                    + "separated by '" + Task.FIELD_SEPARATOR + "'.");
        }

        String typeCode = fields[0].toUpperCase(Locale.ROOT);
        boolean isComplete = parseStatus(fields[1]);
        String description = fields[2];

        Task task = switch (typeCode) {
        case "T" -> {
            requireFieldCount(fields, 3, "T | 1 | read book");
            yield new Todo(description);
        }
        case "D" -> {
            requireFieldCount(fields, 4, "D | 0 | return book | 2019-06-06");
            yield new Deadline(description, fields[3]);
        }
        case "E" -> {
            requireFieldCount(fields, 5,
                    "E | 0 | project meeting | 2019-08-06 1400 | 2019-08-06 1600");
            yield new Event(description, fields[3], fields[4]);
        }
        default -> throw new BibiException("unknown task type '" + fields[0] + "'.");
        };

        if (isComplete) {
            task.markComplete();
        }
        return task;
    }

    /**
     * Reads the completion flag, which must be exactly {@code 1} or {@code 0}.
     *
     * @param statusField the second field of a saved line
     * @return {@code true} when the saved task was complete
     * @throws BibiException if the flag is anything else
     */
    private static boolean parseStatus(String statusField) throws BibiException {
        if (statusField.equals("1")) {
            return true;
        }
        if (statusField.equals("0")) {
            return false;
        }
        throw new BibiException("status '" + statusField + "' should be 1 (done) or 0 (not done).");
    }

    /**
     * Checks that a saved line has exactly the number of fields its type needs.
     *
     * @param fields the fields read from the line
     * @param expectedCount the number of fields the task type stores
     * @param example a correctly formed line to show the user
     * @throws BibiException if the line has too few or too many fields
     */
    private static void requireFieldCount(String[] fields, int expectedCount, String example)
            throws BibiException {
        if (fields.length != expectedCount) {
            throw new BibiException("expected " + expectedCount + " fields but found "
                    + fields.length + ", for example: " + example);
        }
    }
}
