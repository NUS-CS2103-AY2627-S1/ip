package mira.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import mira.exception.MiraException;
import mira.task.Deadline;
import mira.task.Event;
import mira.task.Task;
import mira.task.TaskList;
import mira.task.Todo;

/**
 * Loads and saves Mira tasks in a local text file.
 */
public class Storage {
    private static final String FIELD_SEPARATOR = " | ";

    private final Path filePath;

    /**
     * Creates storage backed by the specified file.
     *
     * @param filePath relative or absolute path to the data file.
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads all tasks from disk, creating an empty data file if needed.
     *
     * @return tasks in their saved order.
     * @throws MiraException if the data cannot be read or decoded.
     */
    public TaskList load() throws MiraException {
        createDataFileIfMissing();

        try {
            List<Task> tasks = new ArrayList<>();
            for (String line : Files.readAllLines(filePath, StandardCharsets.UTF_8)) {
                if (!line.isBlank()) {
                    tasks.add(decodeTask(line));
                }
            }
            return new TaskList(tasks);
        } catch (IOException exception) {
            throw new MiraException("I couldn't read the data file.");
        }
    }

    /**
     * Replaces the data file with the current task list.
     *
     * @param tasks task list to persist in display order.
     * @throws MiraException if the data cannot be written.
     */
    public void save(TaskList tasks) throws MiraException {
        Path temporaryFile = null;
        try {
            Path parent = filePath.toAbsolutePath().getParent();
            Files.createDirectories(parent);

            List<String> lines = new ArrayList<>();
            for (Task task : tasks.asList()) {
                lines.add(encodeTask(task));
            }
            // Write a sibling first so a failed write cannot truncate the existing data.
            temporaryFile = Files.createTempFile(parent, "mira-", ".tmp");
            Files.write(temporaryFile, lines, StandardCharsets.UTF_8);
            try {
                Files.move(temporaryFile, filePath,
                        StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(temporaryFile, filePath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException exception) {
            throw new MiraException("I couldn't save your tasks. Nothing was changed. "
                    + "Check that the data folder is writable, then try again.");
        } finally {
            if (temporaryFile != null) {
                try {
                    Files.deleteIfExists(temporaryFile);
                } catch (IOException exception) {
                    // A leftover temporary file must not hide the original save result.
                }
            }
        }
    }

    /**
     * Creates the parent directory and an empty data file when absent.
     *
     * @throws MiraException If either path cannot be created.
     */
    private void createDataFileIfMissing() throws MiraException {
        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            if (Files.notExists(filePath)) {
                Files.createFile(filePath);
            }
        } catch (IOException exception) {
            throw new MiraException("I couldn't create the data file.");
        }
    }

    /**
     * Encodes one task as a type, status, and Base64 text fields.
     *
     * @param task Task to encode.
     * @return A single storage-file line.
     */
    private static String encodeTask(Task task) {
        switch (task.getType()) {
            case DEADLINE:
                return encodeFields(
                        task, encodeText(((Deadline) task).getBy().toString()));
            case EVENT:
                Event event = (Event) task;
                return encodeFields(
                        task, encodeText(event.getFrom()), encodeText(event.getTo()));
            case TODO:
                return encodeFields(task);
            default:
                throw new IllegalArgumentException("Unsupported task type: " + task.getType());
        }
    }

    /**
     * Encodes the fields common to every task followed by any type-specific fields.
     *
     * @param task Task whose common fields should be encoded.
     * @param typeSpecificFields Zero or more fields carried by the concrete task type.
     * @return A single storage-file line containing every encoded field.
     */
    private static String encodeFields(Task task, String... typeSpecificFields) {
        List<String> fields = new ArrayList<>();
        fields.add(task.getType().getSymbol());
        fields.add(task.isDone() ? "1" : "0");
        fields.add(encodeText(task.getDescription()));
        fields.addAll(List.of(typeSpecificFields));
        return String.join(FIELD_SEPARATOR, fields);
    }

    /**
     * Decodes one storage-file line into its concrete task type.
     *
     * @param line Encoded storage-file line.
     * @return The decoded task.
     * @throws MiraException If the line has an invalid type, field count, or value.
     */
    private static Task decodeTask(String line) throws MiraException {
        String[] fields = line.split(" \\| ", -1);
        if (fields.length < 3) {
            throw invalidData();
        }

        Task task;
        String description = decodeText(fields[2]);
        requireNonBlank(description);
        switch (fields[0]) {
            case "T":
                requireFieldCount(fields, 3);
                task = new Todo(description);
                break;
            case "D":
                requireFieldCount(fields, 4);
                task = new Deadline(description, decodeDate(fields[3]));
                break;
            case "E":
                requireFieldCount(fields, 5);
                String from = decodeText(fields[3]);
                String to = decodeText(fields[4]);
                requireNonBlank(from);
                requireNonBlank(to);
                task = new Event(description, from, to);
                break;
            default:
                throw invalidData();
        }

        if ("1".equals(fields[1])) {
            task.setDone(true);
        } else if (!"0".equals(fields[1])) {
            throw invalidData();
        }
        return task;
    }

    /**
     * Encodes arbitrary UTF-8 text without exposing storage delimiters.
     *
     * @param text Text to encode.
     * @return Base64 representation of the text.
     */
    private static String encodeText(String text) {
        return Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Decodes a Base64 text field.
     *
     * @param encodedText Encoded field value.
     * @return Decoded UTF-8 text.
     * @throws MiraException If the field is not valid Base64.
     */
    private static String decodeText(String encodedText) throws MiraException {
        try {
            byte[] bytes = Base64.getDecoder().decode(encodedText);
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException exception) {
            throw invalidData();
        }
    }

    /**
     * Decodes an ISO date stored inside a Base64 text field.
     *
     * @param encodedDate Encoded ISO date.
     * @return Parsed date.
     * @throws MiraException If the field is not a valid ISO date.
     */
    private static LocalDate decodeDate(String encodedDate) throws MiraException {
        try {
            return LocalDate.parse(decodeText(encodedDate));
        } catch (DateTimeParseException exception) {
            throw invalidData();
        }
    }

    /**
     * Validates the exact number of fields for a task type.
     *
     * @param fields Fields decoded from one line.
     * @param expectedCount Required number of fields.
     * @throws MiraException If the field count differs.
     */
    private static void requireFieldCount(String[] fields, int expectedCount)
            throws MiraException {
        if (fields.length != expectedCount) {
            throw invalidData();
        }
    }

    /**
     * Creates a consistent exception for malformed storage content.
     *
     * @return Exception describing invalid persisted data.
     */
    private static MiraException invalidData() {
        return new MiraException("The data file contains an invalid task. "
                + "Keep a backup of data/mira.txt and repair or move it before restarting.");
    }

    /**
     * Rejects empty persisted fields that could not be entered through a command.
     *
     * @param value Decoded task field.
     * @throws MiraException If the field contains no visible text.
     */
    private static void requireNonBlank(String value) throws MiraException {
        if (value.isBlank()) {
            throw invalidData();
        }
    }
}
