import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads and saves Mona's task list from and to a plain-text file on disk, so tasks
 * persist between runs. Each task is stored as one pipe-delimited line, e.g.
 * {@code T | 1 | read book} for a completed todo or
 * {@code D | 0 | return book | 2019-06-06} for an incomplete deadline, whose date can
 * optionally carry a time, e.g. {@code D | 0 | return book | 2019-06-06 1800}. See
 * {@link TaskDateTime} for the exact formats read and written.
 */
public class Storage {
    private static final String FIELD_SEPARATOR_REGEX = "\\s*\\|\\s*";
    private static final String DONE_FLAG = "1";

    private final Path filePath;

    /**
     * Creates a storage backed by the file at the given path.
     *
     * @param filePath the path to the data file, relative to the working directory the
     *         program is run from (e.g. {@code "./data/mona.txt"}).
     */
    public Storage(String filePath) {
        this.filePath = Path.of(filePath);
    }

    /**
     * Loads the task list from the data file. If the file (or its parent directory)
     * does not exist yet, such as on the very first run of the program, both are
     * created and an empty list is returned. A line that cannot be parsed is skipped
     * and reported rather than aborting the whole load, so one corrupted line does not
     * discard the rest of a saved list.
     *
     * @return the tasks read from the data file, in the order they were saved.
     * @throws MonaException if the data file or its directory cannot be created or read.
     */
    public ArrayList<Task> load() throws MonaException {
        try {
            if (Files.notExists(filePath)) {
                createDataFile();
                return new ArrayList<>();
            }

            ArrayList<Task> tasks = new ArrayList<>();
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            for (int index = 0; index < lines.size(); index++) {
                String line = lines.get(index);
                if (line.trim().isEmpty()) {
                    continue;
                }

                try {
                    tasks.add(parseTask(line));
                } catch (MonaException exception) {
                    // A single malformed line (e.g. from manual editing or a corrupted write)
                    // should not prevent the rest of the file from loading, so it is skipped
                    // and reported instead of aborting the whole load.
                    System.out.println("⚠️ Ignoring corrupted line " + (index + 1) + " in " + filePath
                            + ": " + line);
                }
            }
            return tasks;
        } catch (IOException exception) {
            throw new MonaException(
                    "❌ The stars are silent; I could not read your saved tasks from " + filePath + ".\n"
                            + "Reason: " + exception.getMessage());
        }
    }

    /**
     * Saves the given tasks to the data file, overwriting its previous contents. The
     * parent directory is created first if it does not already exist.
     *
     * @param tasks the tasks to save.
     * @throws MonaException if the data file or its directory cannot be created or written.
     */
    public void save(ArrayList<Task> tasks) throws MonaException {
        try {
            if (filePath.getParent() != null) {
                Files.createDirectories(filePath.getParent());
            }

            List<String> lines = new ArrayList<>();
            for (Task task : tasks) {
                lines.add(task.toSaveFormat());
            }
            Files.write(filePath, lines, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new MonaException(
                    "❌ The stars are silent; I could not save your tasks to " + filePath + ".\n"
                            + "Reason: " + exception.getMessage());
        }
    }

    private void createDataFile() throws IOException {
        if (filePath.getParent() != null) {
            Files.createDirectories(filePath.getParent());
        }
        Files.createFile(filePath);
    }

    /**
     * Parses one saved line back into the task it represents.
     *
     * @param line the pipe-delimited line to parse.
     * @return the parsed task, with its done status restored.
     * @throws MonaException if the line does not match the expected format.
     */
    private Task parseTask(String line) throws MonaException {
        String[] fields = line.split(FIELD_SEPARATOR_REGEX);
        if (fields.length < 3) {
            throw new MonaException("Expected at least 3 fields, found " + fields.length);
        }

        String typeCode = fields[0].trim();
        boolean isDone = DONE_FLAG.equals(fields[1].trim());
        String description = fields[2];

        Task task;
        switch (typeCode) {
        case "T":
            task = new Todo(description);
            break;
        case "D":
            if (fields.length < 4) {
                throw new MonaException("Deadline is missing its /by field");
            }
            task = new Deadline(description, parseDate(fields[3]));
            break;
        case "E":
            if (fields.length < 5) {
                throw new MonaException("Event is missing its /from or /to field");
            }
            task = new Event(description, parseDate(fields[3]), parseDate(fields[4]));
            break;
        default:
            throw new MonaException("Unknown task type: " + typeCode);
        }

        if (isDone) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Parses a saved date field back into a {@link TaskDateTime}.
     *
     * @param dateField the {@code yyyy-mm-dd HHmm} or {@code yyyy-mm-dd} text read from the data file.
     * @return the parsed date.
     * @throws MonaException if the field matches neither accepted format.
     */
    private static TaskDateTime parseDate(String dateField) throws MonaException {
        try {
            return TaskDateTime.parse(dateField.trim());
        } catch (DateTimeParseException exception) {
            throw new MonaException("Invalid date: " + dateField);
        }
    }
}
