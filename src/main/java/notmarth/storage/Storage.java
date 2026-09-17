package notmarth.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.DateTimeException;
import java.util.ArrayList;
import java.util.List;

import notmarth.model.Contact;
import notmarth.model.Deadline;
import notmarth.model.Event;
import notmarth.model.Task;
import notmarth.model.ToDo;

/**
 * Reads and writes the task list in a small, versioned text archive.
 *
 * <p>The default path is relative to the directory from which NotMarth is
 * launched, so the project can be moved to another computer or operating
 * system without changing any source code. A different path can be supplied
 * when creating the storage object.</p>
 */
public final class Storage {
    private static final Path DEFAULT_DATA_FILE = Path.of("data", "notmarth.txt");
    private static final String HEADER = "# NotMarth battle plan v1";
    private final Path dataFile;

    /** Creates storage using the default NotMarth archive path. */
    public Storage() {
        this(DEFAULT_DATA_FILE);
    }

    /**
     * Creates storage using a caller-supplied archive path.
     *
     * @param filePath the path of the task archive.
     */
    public Storage(String filePath) {
        this(Path.of(requireFilePath(filePath)));
    }

    private Storage(Path dataFile) {
        this.dataFile = dataFile;
    }

    private static String requireFilePath(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            throw new IllegalArgumentException("The archive path must not be blank.");
        }
        return filePath;
    }

    /**
     * Loads the saved tasks and reports whether startup had to recover from a
     * storage problem.
     *
     * @param maximumTasks the largest valid number of tasks.
     * @return the loaded tasks and an optional startup warning.
     */
    public LoadResult load(int maximumTasks) {
        if (maximumTasks < 1) {
            return unusableArchiveWarning();
        }
        try {
            if (!Files.exists(dataFile)) {
                return new LoadResult(new ArrayList<>(), new ArrayList<>(), null);
            }
            if (!Files.isRegularFile(dataFile)) {
                return unusableArchiveWarning();
            }
            return parseArchive(Files.readAllLines(dataFile, StandardCharsets.UTF_8), maximumTasks);
        } catch (CorruptTaskDataException exception) {
            return new LoadResult(new ArrayList<>(), new ArrayList<>(),
                    "The saved battle plan is corrupted. Repair or remove the file before starting "
                            + "NotMarth again.");
        } catch (IOException | SecurityException exception) {
            return unreadableArchiveWarning();
        }
    }

    /** Parses a complete archive, rejecting all records if any record is invalid. */
    private LoadResult parseArchive(List<String> lines, int maximumTasks) throws CorruptTaskDataException {
        if (lines.isEmpty() || !HEADER.equals(lines.get(0))) {
            throw new CorruptTaskDataException();
        }

        ArrayList<Task> tasks = new ArrayList<>();
        ArrayList<Contact> contacts = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {
            String record = lines.get(i);
            if (record.isBlank()) {
                continue;
            }
            if (record.startsWith("contact|")) {
                contacts.add(parseContact(record));
            } else {
                addTaskRecord(record, tasks, maximumTasks);
            }
        }
        return new LoadResult(tasks, contacts, null);
    }

    /** Adds a saved task only if it fits the capacity and does not duplicate an existing task. */
    private void addTaskRecord(String record, List<Task> tasks, int maximumTasks)
            throws CorruptTaskDataException {
        if (tasks.size() == maximumTasks) {
            throw new CorruptTaskDataException();
        }
        Task task = parseTask(record);
        if (tasks.stream().anyMatch(existingTask -> existingTask.hasSameDetailsAs(task))) {
            throw new CorruptTaskDataException();
        }
        tasks.add(task);
    }

    private LoadResult unusableArchiveWarning() {
        return new LoadResult(new ArrayList<>(), new ArrayList<>(),
                "The saved battle plan path is not a readable file. Fix the path before starting "
                        + "NotMarth again.");
    }

    private LoadResult unreadableArchiveWarning() {
        return new LoadResult(new ArrayList<>(), new ArrayList<>(),
                "I couldn't read the saved battle plan from disk. Fix the file before starting "
                        + "NotMarth again.");
    }

    /**
     * Contains tasks loaded at startup and an optional warning for the user.
     */
    public static final class LoadResult {
        private final ArrayList<Task> tasks;
        private final ArrayList<Contact> contacts;
        private final String warning;

        private LoadResult(ArrayList<Task> tasks, ArrayList<Contact> contacts, String warning) {
            this.tasks = tasks;
            this.contacts = contacts;
            this.warning = warning;
        }

        /**
         * Returns the tasks recovered from disk.
         *
         * @return the loaded tasks, or an empty list after a storage problem.
         */
        public ArrayList<Task> getTasks() {
            return tasks;
        }

        /** @return the contacts loaded from disk */
        public ArrayList<Contact> getContacts() {
            return contacts;
        }

        /**
         * Returns whether startup should show a storage warning.
         *
         * @return {@code true} when the archive could not be used.
         */
        public boolean hasWarning() {
            return warning != null;
        }

        /**
         * Returns the warning that explains a storage problem.
         *
         * @return the warning text, or {@code null} when loading succeeded.
         */
        public String getWarning() {
            return warning;
        }
    }

    /**
     * Saves the current task list, creating its parent directory if needed.
     * A temporary file is moved into place so an interrupted write is less
     * likely to destroy the previous valid archive.
     *
     * @param tasks the current task list.
     * @throws IOException if the archive cannot be written.
     */
    public void save(List<Task> tasks) throws IOException {
        save(tasks, List.of());
    }

    /** Saves tasks and contacts in the shared archive. */
    public void save(List<Task> tasks, List<Contact> contacts) throws IOException {
        Path parent = dataFile.getParent();
        if (parent == null) {
            parent = Path.of(".");
        }
        Files.createDirectories(parent);

        ArrayList<String> lines = new ArrayList<>();
        lines.add(HEADER);
        for (Task task : tasks) {
            lines.add(serializeTask(task));
        }
        for (Contact contact : contacts) {
            lines.add(serializeContact(contact));
        }

        Path temporaryFile = Files.createTempFile(parent, "notmarth-", ".tmp");
        try {
            Files.write(temporaryFile, lines, StandardCharsets.UTF_8);
            try {
                Files.move(temporaryFile, dataFile,
                        StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException | UnsupportedOperationException exception) {
                Files.move(temporaryFile, dataFile, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporaryFile);
        }
    }

    /**
     * Converts one saved record into a task object.
     *
     * @param line the saved record.
     * @return the parsed task.
     * @throws CorruptTaskDataException if the record is not valid.
     */
    private static Task parseTask(String line) throws CorruptTaskDataException {
        List<String> fields = splitRecord(line);
        if (fields.size() < 3) {
            throw new CorruptTaskDataException();
        }

        boolean isDone = isCompletedStatus(fields.get(1));
        Task task = createTask(fields);
        if (isDone) {
            task.markAsDone();
        }
        return task;
    }

    private static Contact parseContact(String line) throws CorruptTaskDataException {
        List<String> fields = splitRecord(line);
        requireFieldCount(fields, 4);
        if (!"contact".equals(fields.get(0))) {
            throw new CorruptTaskDataException();
        }
        try {
            return new Contact(
                    requireText(fields.get(1)), requireText(fields.get(2)), requireText(fields.get(3)));
        } catch (IllegalArgumentException exception) {
            throw new CorruptTaskDataException();
        }
    }

    private static Task createTask(List<String> fields) throws CorruptTaskDataException {
        String type = fields.get(0);
        Task task;
        try {
            switch (type) {
            case "todo":
                requireFieldCount(fields, 3);
                task = new ToDo(requireText(fields.get(2)));
                break;
            case "deadline":
                requireFieldCount(fields, 4);
                task = new Deadline(requireText(fields.get(2)), requireText(fields.get(3)));
                break;
            case "event":
                requireFieldCount(fields, 5);
                task = new Event(
                        requireText(fields.get(2)), requireText(fields.get(3)), requireText(fields.get(4)));
                break;
            default:
                throw new CorruptTaskDataException();
            }
        } catch (DateTimeException | IllegalArgumentException exception) {
            throw new CorruptTaskDataException();
        }
        return task;
    }

    /**
     * Converts a task to the custom archive format.
     *
     * @param task the task to serialize.
     * @return one archive record.
     * @throws IllegalStateException if an unsupported task subtype is found.
     */
    private static String serializeTask(Task task) {
        String status = task.isDone() ? "done" : "open";
        switch (task.getTaskType()) {
        case TODO:
            return String.join("|", "todo", status, escape(task.getDescription()));
        case DEADLINE:
            // TaskType and the concrete class must stay aligned for this cast to be safe.
            assert task instanceof Deadline : "Deadline task type must use the Deadline class";
            Deadline deadline = (Deadline) task;
            return String.join("|", "deadline", status, escape(task.getDescription()),
                    escape(deadline.getByForStorage()));
        case EVENT:
            // TaskType and the concrete class must stay aligned for this cast to be safe.
            assert task instanceof Event : "Event task type must use the Event class";
            Event event = (Event) task;
            return String.join("|", "event", status, escape(task.getDescription()),
                    escape(event.getFromForStorage()), escape(event.getToForStorage()));
        default:
            throw new IllegalStateException("Unsupported task type");
        }
    }

    private static String serializeContact(Contact contact) {
        return String.join("|", "contact", escape(contact.getName()), escape(contact.getPhone()),
                escape(contact.getAddress()));
    }

    /**
     * Splits a record while respecting escaped pipes and backslashes.
     *
     * @param line the record to split.
     * @return decoded fields.
     * @throws CorruptTaskDataException if an escape sequence is incomplete.
     */
    private static List<String> splitRecord(String line) throws CorruptTaskDataException {
        ArrayList<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean isEscaping = false;
        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);
            if (isEscaping) {
                appendEscapedCharacter(field, character);
                isEscaping = false;
            } else if (character == '\\') {
                isEscaping = true;
            } else if (character == '|') {
                fields.add(field.toString());
                field.setLength(0);
            } else {
                field.append(character);
            }
        }
        if (isEscaping) {
            throw new CorruptTaskDataException();
        }
        fields.add(field.toString());
        return fields;
    }

    private static void appendEscapedCharacter(StringBuilder field, char character)
            throws CorruptTaskDataException {
        switch (character) {
        case '\\':
            field.append('\\');
            break;
        case '|':
            field.append('|');
            break;
        case 'n':
            field.append('\n');
            break;
        case 'r':
            field.append('\r');
            break;
        default:
            throw new CorruptTaskDataException();
        }
    }

    /**
     * Converts the archive's completion marker into a boolean state.
     *
     * @param status the saved {@code open} or {@code done} marker.
     * @return {@code true} when the task is complete.
     * @throws CorruptTaskDataException if the marker is not recognized.
     */
    private static boolean isCompletedStatus(String status) throws CorruptTaskDataException {
        if ("open".equals(status)) {
            return false;
        }
        if ("done".equals(status)) {
            return true;
        }
        throw new CorruptTaskDataException();
    }

    /**
     * Validates the number of fields in a saved task record.
     *
     * @param fields the decoded record fields.
     * @param expected the number of fields required for the task type.
     * @throws CorruptTaskDataException if the field count is unexpected.
     */
    private static void requireFieldCount(List<String> fields, int expected)
            throws CorruptTaskDataException {
        if (fields.size() != expected) {
            throw new CorruptTaskDataException();
        }
    }

    /**
     * Ensures that a required archive field is not empty.
     *
     * @param value the decoded field value.
     * @return the unchanged, non-empty value.
     * @throws CorruptTaskDataException if the value is empty.
     */
    private static String requireText(String value) throws CorruptTaskDataException {
        if (value.isEmpty()) {
            throw new CorruptTaskDataException();
        }
        return value;
    }

    /**
     * Escapes characters that have structural meaning in the archive format.
     *
     * @param value the text to encode.
     * @return the encoded text safe to place in one archive field.
     */
    private static String escape(String value) {
        return value.replace("\\", "\\\\")
                .replace("|", "\\|")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    /** Signals that a saved record does not follow the archive format. */
    private static final class CorruptTaskDataException extends Exception {
        private static final long serialVersionUID = 1L;
    }
}
