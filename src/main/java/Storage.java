import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Saves tasks to, and restores tasks from, a local text file.
 */
public class Storage {
    /** The relative location used by the application for its task data. */
    public static final Path DEFAULT_FILE_PATH = Path.of("data", "duke.txt");

    private final Path filePath;

    /**
     * Creates storage that uses the supplied file path.
     *
     * @param filePath location of the task data file
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Contains tasks restored from storage and warnings for skipped legacy records.
     */
    public static class LoadResult {
        private final List<Task> tasks;
        private final List<String> warnings;

        /**
         * Creates a storage load result.
         *
         * @param tasks restored tasks
         * @param warnings messages about skipped records
         */
        public LoadResult(List<Task> tasks, List<String> warnings) {
            this.tasks = tasks;
            this.warnings = warnings;
        }

        /**
         * Returns the restored tasks.
         *
         * @return restored tasks
         */
        public List<Task> getTasks() {
            return tasks;
        }

        /**
         * Returns warnings for records that could not be restored.
         *
         * @return storage warnings
         */
        public List<String> getWarnings() {
            return warnings;
        }
    }

    /**
     * Loads tasks from disk. A missing file represents an empty task list.
     *
     * @return restored tasks and warnings for skipped legacy deadline records
     * @throws IOException if saved data cannot be read or is invalid
     */
    public LoadResult load() throws IOException {
        List<Task> tasks = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        if (Files.notExists(filePath)) {
            return new LoadResult(tasks, warnings);
        }

        List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
        for (int index = 0; index < lines.size(); index++) {
            try {
                tasks.add(parseTask(lines.get(index), index + 1));
            } catch (LegacyDeadlineDateException exception) {
                warnings.add("OOPS!!! Skipped saved deadline at line " + exception.getLineNumber()
                        + " because its date is not in yyyy-MM-dd format.");
            }
        }
        return new LoadResult(tasks, warnings);
    }

    /**
     * Saves all tasks, creating the containing directory when necessary.
     *
     * @param tasks tasks to save
     * @throws IOException if the task data cannot be written
     */
    public void save(List<Task> tasks) throws IOException {
        Path parentDirectory = filePath.getParent();
        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }

        List<String> lines = tasks.stream()
                .map(Storage::formatTask)
                .toList();
        Files.write(filePath, lines, StandardCharsets.UTF_8);
    }

    /**
     * Formats one task as a tab-delimited storage record.
     *
     * @param task task to format
     * @return one storage record
     */
    private static String formatTask(Task task) {
        assert task != null;
        String status = task.isDone() ? "1" : "0";
        if (task instanceof Todo) {
            return "T\t" + status + "\t" + escape(task.getDescription());
        }
        if (task instanceof Deadline) {
            Deadline deadline = (Deadline) task;
            return "D\t" + status + "\t" + escape(task.getDescription())
                    + "\t" + escape(deadline.getBy().toString());
        }
        if (task instanceof Event) {
            Event event = (Event) task;
            return "E\t" + status + "\t" + escape(task.getDescription())
                    + "\t" + escape(event.getFrom()) + "\t" + escape(event.getTo());
        }
        throw new IllegalArgumentException("Unsupported task type: " + task.getClass().getName());
    }

    /**
     * Recreates one task from a stored record.
     *
     * @param line stored record
     * @param lineNumber one-based position of the record in the data file
     * @return recreated task
     * @throws IOException if the record is invalid
     */
    private static Task parseTask(String line, int lineNumber) throws IOException {
        String[] fields = line.split("\\t", -1);
        if (fields.length < 3 || !(fields[1].equals("0") || fields[1].equals("1"))) {
            throw invalidData(lineNumber);
        }

        Task task;
        switch (fields[0]) {
            case "T":
                if (fields.length != 3) {
                    throw invalidData(lineNumber);
                }
                task = new Todo(requireNonBlank(unescape(fields[2], lineNumber), lineNumber));
                break;
            case "D":
                if (fields.length != 4) {
                    throw invalidData(lineNumber);
                }
                task = new Deadline(
                        requireNonBlank(unescape(fields[2], lineNumber), lineNumber),
                        parseDeadlineDate(fields[3], lineNumber)
                );
                break;
            case "E":
                if (fields.length != 5) {
                    throw invalidData(lineNumber);
                }
                task = new Event(requireNonBlank(unescape(fields[2], lineNumber), lineNumber),
                        requireNonBlank(unescape(fields[3], lineNumber), lineNumber),
                        requireNonBlank(unescape(fields[4], lineNumber), lineNumber));
                break;
            default:
                throw invalidData(lineNumber);
        }

        assert task != null;
        if (fields[1].equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Creates a consistent error for an invalid saved record.
     *
     * @param lineNumber one-based position of the invalid record
     * @return I/O error describing the invalid record
     */
    private static IOException invalidData(int lineNumber) {
        return new IOException("Invalid saved task at line " + lineNumber + ".");
    }

    /**
     * Ensures a required persisted field contains meaningful text.
     *
     * @param value decoded field value
     * @param lineNumber one-based position of the record
     * @return the unchanged non-blank value
     * @throws IOException if the field is blank
     */
    private static String requireNonBlank(String value, int lineNumber) throws IOException {
        if (value.isBlank()) {
            throw invalidData(lineNumber);
        }
        return value;
    }

    /**
     * Parses a stored ISO deadline date, identifying pre-Level 8 text dates separately.
     *
     * @param dateText stored deadline date
     * @param lineNumber one-based position of the containing record
     * @return parsed deadline date
     * @throws IOException if the stored text has an invalid escape sequence
     * @throws LegacyDeadlineDateException if the deadline uses a legacy non-ISO value
     */
    private static LocalDate parseDeadlineDate(String dateText, int lineNumber)
            throws IOException {
        try {
            return LocalDate.parse(unescape(dateText, lineNumber));
        } catch (DateTimeParseException exception) {
            throw new LegacyDeadlineDateException(lineNumber);
        }
    }

    /**
     * Identifies a Level 7 deadline record that cannot be represented as a {@link LocalDate}.
     */
    private static class LegacyDeadlineDateException extends IOException {
        private final int lineNumber;

        /**
         * Creates an exception for one legacy deadline record.
         *
         * @param lineNumber one-based position of the record
         */
        LegacyDeadlineDateException(int lineNumber) {
            this.lineNumber = lineNumber;
        }

        /**
         * Returns the one-based position of the legacy record.
         *
         * @return record line number
         */
        int getLineNumber() {
            return lineNumber;
        }
    }

    /**
     * Escapes control characters that would otherwise break a storage record.
     *
     * @param text field value to escape
     * @return escaped field value
     */
    private static String escape(String text) {
        return text.replace("\\", "\\\\").replace("\t", "\\t").replace("\n", "\\n");
    }

    /**
     * Restores escaped control characters in a storage field.
     *
     * @param text stored field value
     * @param lineNumber one-based position of the containing record
     * @return unescaped field value
     * @throws IOException if an unsupported escape sequence is encountered
     */
    private static String unescape(String text, int lineNumber) throws IOException {
        StringBuilder result = new StringBuilder();
        boolean escaping = false;
        for (int index = 0; index < text.length(); index++) {
            char character = text.charAt(index);
            if (!escaping) {
                if (character == '\\') {
                    escaping = true;
                } else {
                    result.append(character);
                }
            } else if (character == '\\') {
                result.append('\\');
                escaping = false;
            } else if (character == 't') {
                result.append('\t');
                escaping = false;
            } else if (character == 'n') {
                result.append('\n');
                escaping = false;
            } else {
                throw invalidData(lineNumber);
            }
        }
        if (escaping) {
            throw invalidData(lineNumber);
        }
        return result.toString();
    }
}
