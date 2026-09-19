package nudge.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Locale;

import nudge.exception.NudgeException;
import nudge.task.Deadline;
import nudge.task.Event;
import nudge.task.Task;
import nudge.task.Todo;

/**
 * Loads and saves Nudge tasks in a local data file.
 */
public final class Storage {
    private static final DateTimeFormatter LEGACY_DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);
    private static final Path STORAGE_PATH = Path.of("data", "nudge.txt");

    private Storage() {
    }

    /**
     * Writes every task to the storage file with an atomic replacement when supported.
     *
     * @param tasks tasks to save.
     * @throws IOException if the storage directory or file cannot be written.
     */
    public static void save(List<Task> tasks) throws IOException {
        save(tasks, STORAGE_PATH);
    }

    /**
     * Writes tasks to a specified path so storage can be tested without using application data.
     *
     * @param tasks tasks to save.
     * @param storagePath destination storage file.
     * @throws IOException if the storage directory or file cannot be written.
     */
    static void save(List<Task> tasks, Path storagePath) throws IOException {
        Files.createDirectories(storagePath.getParent());
        Path temporaryFile = Files.createTempFile(storagePath.getParent(), "nudge-", ".tmp");
        try {
            Files.write(temporaryFile, tasks.stream().map(Storage::serializeTask).toList(),
                    StandardCharsets.UTF_8);
            moveIntoPlace(temporaryFile, storagePath);
        } finally {
            Files.deleteIfExists(temporaryFile);
        }
    }

    /**
     * Loads tasks from the storage file when it exists.
     *
     * @return tasks represented in the storage file, or an empty list if it does not exist.
     * @throws IOException if the storage file cannot be read.
     * @throws NudgeException if a saved task has an invalid format.
     */
    public static List<Task> load() throws IOException, NudgeException {
        return load(STORAGE_PATH);
    }

    /**
     * Loads tasks from a specified path so storage can be tested with isolated data.
     *
     * @param storagePath source storage file.
     * @return tasks represented in the storage file, or an empty list if it does not exist.
     * @throws IOException if the storage file cannot be read.
     * @throws NudgeException if a saved task has an invalid format.
     */
    static List<Task> load(Path storagePath) throws IOException, NudgeException {
        if (!Files.exists(storagePath)) {
            return new ArrayList<>();
        }
        ArrayList<Task> tasks = new ArrayList<>();
        for (String savedTask : Files.readAllLines(storagePath, StandardCharsets.UTF_8)) {
            if (!savedTask.isBlank()) {
                tasks.add(deserializeTask(savedTask));
            }
        }
        return tasks;
    }

    /**
     * Replaces the storage file using an atomic move when the file system supports it.
     *
     * @param temporaryFile completed temporary storage file.
     * @param storagePath destination storage file.
     * @throws IOException if the storage file cannot be replaced.
     */
    private static void moveIntoPlace(Path temporaryFile, Path storagePath) throws IOException {
        try {
            Files.move(temporaryFile, storagePath, StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(temporaryFile, storagePath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Converts a task into its stable, delimiter-safe storage representation.
     *
     * @param task task to serialize.
     * @return encoded storage line for the task.
     */
    private static String serializeTask(Task task) {
        String status = task.isDone() ? "1" : "0";
        if (task instanceof Todo) {
            return "T|" + status + "|" + encode(task.getDescription());
        }
        if (task instanceof Deadline deadline) {
            return "D|" + status + "|" + encode(deadline.getDescription())
                    + "|" + encode(deadline.getDueDate().toString());
        }
        if (task instanceof Event event) {
            return "E|" + status + "|" + encode(event.getDescription())
                    + "|" + encode(event.getStartDate().toString())
                    + "|" + encode(event.getEndDate().toString());
        }
        throw new IllegalArgumentException("Unsupported task type");
    }

    /**
     * Recreates a task from one line of the storage file.
     *
     * @param savedTask one saved task.
     * @return task represented by the line.
     * @throws NudgeException if the line is not a supported task format.
     */
    private static Task deserializeTask(String savedTask) throws NudgeException {
        return savedTask.contains("|")
                ? deserializeCurrentTask(savedTask)
                : deserializeLegacyTask(savedTask);
    }

    /**
     * Recreates a task from the current delimiter-safe storage format.
     *
     * @param savedTask one current-format storage line.
     * @return task represented by the line.
     * @throws NudgeException if the line is invalid.
     */
    private static Task deserializeCurrentTask(String savedTask) throws NudgeException {
        String[] parts = savedTask.split("\\|", -1);
        if (parts.length < 3 || parts[0].length() != 1
                || !(parts[1].equals("0") || parts[1].equals("1"))) {
            throw createInvalidFileException();
        }
        Task task;
        switch (parts[0]) {
            case "T":
                requirePartCount(parts, 3);
                task = new Todo(decode(parts[2]));
                break;
            case "D":
                requirePartCount(parts, 4);
                task = new Deadline(decode(parts[2]), parseSavedDate(decode(parts[3])));
                break;
            case "E":
                requirePartCount(parts, 5);
                task = new Event(decode(parts[2]), parseSavedDate(decode(parts[3])),
                        parseSavedDate(decode(parts[4])));
                break;
            default:
                throw createInvalidFileException();
        }
        if (parts[1].equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Recreates a task from the original human-readable storage format.
     *
     * @param savedTask one legacy-format storage line.
     * @return task represented by the line.
     * @throws NudgeException if the line is invalid.
     */
    private static Task deserializeLegacyTask(String savedTask) throws NudgeException {
        if (savedTask.length() < 7 || savedTask.charAt(0) != '[' || savedTask.charAt(2) != ']'
                || savedTask.charAt(3) != '[' || savedTask.charAt(5) != ']'
                || savedTask.charAt(6) != ' ') {
            throw createInvalidFileException();
        }
        Task task = switch (savedTask.charAt(1)) {
            case 'T' -> new Todo(savedTask.substring(7));
            case 'D' -> deserializeLegacyDeadline(savedTask.substring(7));
            case 'E' -> deserializeLegacyEvent(savedTask.substring(7));
            default -> throw createInvalidFileException();
        };
        if (savedTask.charAt(4) == 'X') {
            task.markAsDone();
        } else if (savedTask.charAt(4) != ' ') {
            throw createInvalidFileException();
        }
        return task;
    }

    /**
     * Validates the number of fields in a storage line.
     *
     * @param parts fields in the storage line.
     * @param expectedCount expected number of fields.
     * @throws NudgeException if the field count is incorrect.
     */
    private static void requirePartCount(String[] parts, int expectedCount) throws NudgeException {
        if (parts.length != expectedCount) {
            throw createInvalidFileException();
        }
    }

    /**
     * Decodes one required Base64 task field.
     *
     * @param encoded encoded field.
     * @return decoded field.
     * @throws NudgeException if the field is empty or invalid.
     */
    private static String decode(String encoded) throws NudgeException {
        try {
            String decoded = new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);
            if (decoded.isEmpty()) {
                throw createInvalidFileException();
            }
            return decoded;
        } catch (IllegalArgumentException exception) {
            throw createInvalidFileException();
        }
    }

    /**
     * Encodes one task field so delimiters and line breaks are preserved safely.
     *
     * @param value task field to encode.
     * @return Base64-encoded field.
     */
    private static String encode(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Creates the standard exception used to report an invalid storage file.
     *
     * @return exception containing the user-facing storage error.
     */
    private static NudgeException createInvalidFileException() {
        return new NudgeException("The saved task list is invalid.");
    }

    /**
     * Recreates a deadline from the task details in the legacy storage format.
     *
     * @param taskDetails legacy deadline description and date.
     * @return deadline represented by the details.
     * @throws NudgeException if the deadline details are invalid.
     */
    private static Deadline deserializeLegacyDeadline(String taskDetails) throws NudgeException {
        int dueTimeStart = taskDetails.lastIndexOf(" (by: ");
        if (dueTimeStart < 0 || !taskDetails.endsWith(")")) {
            throw createInvalidFileException();
        }
        String savedDate = taskDetails.substring(
                dueTimeStart + " (by: ".length(), taskDetails.length() - 1);
        return new Deadline(taskDetails.substring(0, dueTimeStart), parseSavedDate(savedDate));
    }

    /**
     * Recreates an event from the task details in the legacy storage format.
     *
     * @param taskDetails legacy event description and dates.
     * @return event represented by the details.
     * @throws NudgeException if the event details are invalid.
     */
    private static Event deserializeLegacyEvent(String taskDetails) throws NudgeException {
        int startTimeStart = taskDetails.lastIndexOf(" (from: ");
        int endTimeStart = taskDetails.lastIndexOf(" to: ");
        if (startTimeStart < 0 || endTimeStart < startTimeStart || !taskDetails.endsWith(")")) {
            throw createInvalidFileException();
        }
        String savedStartDate = taskDetails.substring(
                startTimeStart + " (from: ".length(), endTimeStart);
        String savedEndDate = taskDetails.substring(
                endTimeStart + " to: ".length(), taskDetails.length() - 1);
        return new Event(taskDetails.substring(0, startTimeStart),
                parseSavedDate(savedStartDate), parseSavedDate(savedEndDate));
    }

    /**
     * Parses a date from either the canonical storage format or the former display format.
     *
     * @param savedDate stored date text.
     * @return parsed date.
     * @throws NudgeException if the stored date is invalid.
     */
    private static LocalDate parseSavedDate(String savedDate) throws NudgeException {
        try {
            return LocalDate.parse(savedDate);
        } catch (DateTimeParseException exception) {
            try {
                return LocalDate.parse(savedDate, LEGACY_DISPLAY_FORMATTER);
            } catch (DateTimeParseException legacyException) {
                throw createInvalidFileException();
            }
        }
    }
}
