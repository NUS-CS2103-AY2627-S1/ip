package atlas.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import atlas.AtlasException;
import atlas.client.Client;
import atlas.task.Deadline;
import atlas.task.Event;
import atlas.task.Priority;
import atlas.task.Task;
import atlas.task.Todo;

/**
 * Loads and saves the tasks and clients from and to a text file on disk.
 *
 * <p>File format: one record per line, fields separated by '|':
 * <pre>
 *   Todo:     T | 1 | description
 *   Deadline: D | 1 | description | by
 *   Event:    E | 1 | description | from | to
 *   Client:   C | name | phone | email
 * </pre>
 * A task line carries one extra field, holding the priority word, only when
 * the task has a priority, e.g. {@code T | 0 | description | high}. An empty
 * priority field means the task has no priority, so files written before
 * priorities existed still load.
 * The second task field is 1 if the task is done and 0 otherwise. A client's
 * phone and email are left empty when they are not known. Literal '|' and
 * '\' characters inside stored text are escaped as '\|' and '\\', so any user
 * input round-trips through the file unchanged.
 */
public class Storage {

    /** Pattern for the timestamp added to the name of a data-file backup. */
    private static final DateTimeFormatter BACKUP_STAMP = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    /** Location of the file used to persist Atlas tasks and clients. */
    private final Path filePath;

    /**
     * Where the original file is copied when a load could not use it in full, or
     * null while no copy is owed.
     */
    private Path backupPath;

    /** Whether the original file still has to be copied before it is overwritten. */
    private boolean backupPending;

    /**
     * Creates a storage object that uses the specified file.
     *
     * @param filePath path to the data file.
     */
    public Storage(String filePath) {
        this.filePath = Paths.get(filePath);
    }

    /**
     * Loads tasks and clients from the data file.
     * Returns both lists empty when the file does not exist yet (first run).
     * A line that cannot be parsed is skipped and described in the returned
     * warnings, so one corrupted line does not destroy the rest of the data and
     * the user is still told which records were left out.
     *
     * <p>Whenever a load cannot use every record, the file is copied aside
     * before it is next overwritten, so records that Atlas could not read are
     * never the only copy that is lost.
     *
     * @return the tasks and clients read from the file, with a description of
     *     any record that could not be read.
     * @throws AtlasException if the file exists but cannot be read.
     */
    public AtlasData load() throws AtlasException {
        ArrayList<Task> tasks = new ArrayList<>();
        ArrayList<Client> clients = new ArrayList<>();
        if (!Files.exists(filePath)) {
            return new AtlasData(tasks, clients);
        }
        List<String> lines = readAllLines();
        ArrayList<String> skipped = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) {
                continue;
            }
            try {
                addRecord(line, tasks, clients);
            } catch (AtlasException e) {
                skipped.add("Line " + (i + 1) + ": " + e.getMessage());
            }
        }
        if (skipped.isEmpty()) {
            return new AtlasData(tasks, clients);
        }
        return new AtlasData(tasks, clients, warningsForSkippedRecords(skipped));
    }

    /**
     * Reads every line of the data file. A file that exists but cannot be
     * decoded owes a backup copy, so the copy is arranged before the failure is
     * reported.
     *
     * @return every line of the data file.
     * @throws AtlasException if the file cannot be read.
     */
    private List<String> readAllLines() throws AtlasException {
        try {
            return Files.readAllLines(filePath);
        } catch (IOException e) {
            // The file exists but cannot be decoded. Atlas carries on with an
            // empty list, so a copy has to be kept before the next save
            // overwrites what may be the user's only records.
            String backupName = scheduleBackup();
            throw new AtlasException("The scroll of tasks could not be read: " + e.getMessage()
                    + " Atlas starts with an empty list, and the unreadable file is kept as "
                    + backupName + " before the next change is saved.");
        }
    }

    /**
     * Builds the warning lines that describe records a load could not read.
     *
     * @param skipped description of each record that could not be read, which
     *     the caller has already found non-empty.
     * @return the warning lines shown to the user, the summary first.
     */
    private ArrayList<String> warningsForSkippedRecords(ArrayList<String> skipped) {
        String backupName = scheduleBackup();
        ArrayList<String> warnings = new ArrayList<>();
        warnings.add("Atlas could not read " + skipped.size() + " record"
                + (skipped.size() == 1 ? "" : "s") + " from the data file, so "
                + (skipped.size() == 1 ? "it is" : "they are") + " left out of this session. "
                + "The file is kept as " + backupName + " before the next change is saved.");
        warnings.addAll(skipped);
        return warnings;
    }

    /**
     * Saves every task and client to the data file, creating the data folder
     * first if it does not exist. Tasks are written before clients.
     *
     * <p>Before the file is overwritten, any copy owed from an incomplete load
     * is written beside it, and the save is abandoned if that copy cannot be
     * made, so an unreadable file is never destroyed by the save that follows.
     *
     * @param tasks tasks to save.
     * @param clients clients to save.
     * @throws AtlasException if the file cannot be written.
     */
    public void save(ArrayList<Task> tasks, ArrayList<Client> clients) throws AtlasException {
        assert tasks != null : "tasks to save must not be null";
        assert clients != null : "clients to save must not be null";
        String content = Stream.concat(tasks.stream().map(this::toFileLine),
                        clients.stream().map(this::toClientLine))
                .collect(Collectors.joining(System.lineSeparator()));
        if (!content.isEmpty()) {
            content = content + System.lineSeparator();
        }
        keepOriginalIfUnreadable();
        try {
            if (filePath.getParent() != null) {
                Files.createDirectories(filePath.getParent());
            }
            Files.writeString(filePath, content);
        } catch (IOException e) {
            throw new AtlasException("The scroll of tasks could not be saved: " + e.getMessage());
        }
    }

    /**
     * Decides where the data file is copied before it is next overwritten, for a
     * load that could not use every record. The decision is taken at load time so
     * that the warning shown to the user can name the copy exactly.
     *
     * @return the file name the copy will be written under.
     */
    private String scheduleBackup() {
        if (backupPath == null) {
            String stamp = LocalDateTime.now().format(BACKUP_STAMP);
            backupPath = filePath.resolveSibling(filePath.getFileName() + ".corrupted-" + stamp);
            backupPending = true;
        }
        return backupPath.getFileName().toString();
    }

    /**
     * Copies the data file aside before it is overwritten, when the last load
     * could not use all of it. Nothing is saved if the copy fails: losing records
     * silently would be worse than reporting a save that did not happen.
     *
     * @throws AtlasException if the copy cannot be written.
     */
    private void keepOriginalIfUnreadable() throws AtlasException {
        if (!backupPending || !Files.exists(filePath)) {
            return;
        }
        try {
            Files.copy(filePath, backupPath);
            backupPending = false;
        } catch (IOException e) {
            throw new AtlasException("Atlas could not keep a copy of the data file, so nothing was "
                    + "saved: " + e.getMessage());
        }
    }

    /**
     * Adds one parsed record to the list it belongs to.
     *
     * @param line storage line to parse.
     * @param tasks list that receives a task record.
     * @param clients list that receives a client record.
     * @throws AtlasException if the line is malformed or uses an unknown type.
     */
    private void addRecord(String line, ArrayList<Task> tasks, ArrayList<Client> clients) throws AtlasException {
        assert !line.isBlank() : "load() skips blank lines before parsing";
        String[] parts = splitFields(line);
        if (parts.length == 0) {
            throw new AtlasException("record has no fields");
        }
        String type = parts[0].trim();
        if (type.equals("C")) {
            clients.add(parseClientLine(parts));
        } else {
            tasks.add(parseTaskLine(parts, type));
        }
    }

    /**
     * Splits a storage line into its fields, keeping empty fields.
     *
     * @param line storage line to split.
     * @return the line's fields.
     */
    private String[] splitFields(String line) {
        return line.split("(?<!\\\\)\\|", -1);
    }

    /**
     * Parses one storage line into a task. The fields shared by every task type
     * are read here; the fields that belong to one type are read by that type's
     * own parser. A task with a priority carries one extra field, holding the
     * level word.
     *
     * @param parts fields of the storage line.
     * @param type record type read from the first field.
     * @return task represented by the line.
     * @throws AtlasException if the line is malformed or uses an unknown type.
     */
    private Task parseTaskLine(String[] parts, String type) throws AtlasException {
        if (parts.length < 3) {
            throw new AtlasException("too few fields");
        }
        String doneField = parts[1].trim();
        if (!doneField.equals("0") && !doneField.equals("1")) {
            throw new AtlasException("done flag is not 0 or 1");
        }
        boolean isDone = doneField.equals("1");
        String description = unescape(parts[2].trim());
        return switch (type) {
            case "T" -> parseTodoRecord(parts, description, isDone);
            case "D" -> parseDeadlineRecord(parts, description, isDone);
            case "E" -> parseEventRecord(parts, description, isDone);
            default -> throw new AtlasException("unknown record type '" + type + "'");
        };
    }

    /**
     * Builds a todo from its record. A todo carries no date field, so the only
     * field it may add is the priority word.
     *
     * @param parts fields of the storage line.
     * @param description description already read from the line.
     * @param isDone whether the record marks the todo as done.
     * @return the todo represented by the line.
     * @throws AtlasException if the line has too many fields, or if its
     *     priority field names no level Atlas knows.
     */
    private Task parseTodoRecord(String[] parts, String description, boolean isDone) throws AtlasException {
        if (parts.length > 4) {
            throw new AtlasException("todo has extra fields");
        }
        return applyPriorityAndDone(new Todo(description), parts, 3, isDone);
    }

    /**
     * Builds a deadline from its record, reading the single date field it
     * carries.
     *
     * @param parts fields of the storage line.
     * @param description description already read from the line.
     * @param isDone whether the record marks the deadline as done.
     * @return the deadline represented by the line.
     * @throws AtlasException if the date field is missing, is not a date, or if
     *     the line has too many fields or an unknown priority.
     */
    private Task parseDeadlineRecord(String[] parts, String description, boolean isDone) throws AtlasException {
        if (parts.length < 4) {
            throw new AtlasException("deadline needs a by field");
        }
        if (parts.length > 5) {
            throw new AtlasException("deadline has extra fields");
        }
        LocalDate by;
        try {
            by = LocalDate.parse(unescape(parts[3].trim()));
        } catch (DateTimeParseException e) {
            throw new AtlasException("deadline by is not a date");
        }
        return applyPriorityAndDone(new Deadline(description, by), parts, 4, isDone);
    }

    /**
     * Builds an event from its record, reading the from and to fields it
     * carries.
     *
     * @param parts fields of the storage line.
     * @param description description already read from the line.
     * @param isDone whether the record marks the event as done.
     * @return the event represented by the line.
     * @throws AtlasException if the from or to field is missing, or if the line
     *     has too many fields or an unknown priority.
     */
    private Task parseEventRecord(String[] parts, String description, boolean isDone) throws AtlasException {
        if (parts.length < 5) {
            throw new AtlasException("event needs from and to fields");
        }
        if (parts.length > 6) {
            throw new AtlasException("event has extra fields");
        }
        Event event = new Event(description, unescape(parts[3].trim()), unescape(parts[4].trim()));
        return applyPriorityAndDone(event, parts, 5, isDone);
    }

    /**
     * Applies the two fields a task record may carry on top of its required
     * ones: the priority word and the done flag.
     *
     * @param task task built from the record's required fields.
     * @param parts fields of the storage line.
     * @param fieldsBeforePriority number of fields the task has without a
     *     priority, which is where the priority field starts.
     * @param isDone whether the record marks the task as done.
     * @return the task, with its priority and done state applied.
     * @throws AtlasException if the priority field names no level Atlas knows.
     */
    private Task applyPriorityAndDone(Task task, String[] parts, int fieldsBeforePriority,
            boolean isDone) throws AtlasException {
        // The priority word is written only when the task has a priority, so a
        // file written before priorities existed loads exactly as it did then.
        String priorityField = parts.length > fieldsBeforePriority ? parts[fieldsBeforePriority].trim() : "";
        if (!priorityField.isEmpty()) {
            task.setPriority(parsePriorityWord(priorityField));
        }
        if (isDone) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Parses the priority field of a task record.
     *
     * @param field text of the priority field.
     * @return the level named by the field.
     * @throws AtlasException if the field names no level Atlas knows.
     */
    private Priority parsePriorityWord(String field) throws AtlasException {
        Priority priority = Priority.fromWord(field);
        if (priority == null) {
            throw new AtlasException("unknown priority '" + field + "'");
        }
        return priority;
    }

    /**
     * Parses one storage line into a client. The name is required; the phone
     * and email fields may be omitted or left empty.
     *
     * @param parts fields of the storage line.
     * @return client represented by the line.
     * @throws AtlasException if the line has no name or too many fields.
     */
    private Client parseClientLine(String[] parts) throws AtlasException {
        if (parts.length < 2) {
            throw new AtlasException("client needs a name");
        }
        if (parts.length > 4) {
            throw new AtlasException("client has extra fields");
        }
        String name = unescape(parts[1].trim());
        if (name.isEmpty()) {
            throw new AtlasException("client name is empty");
        }
        String phone = parts.length >= 3 ? unescape(parts[2].trim()) : "";
        String email = parts.length == 4 ? unescape(parts[3].trim()) : "";
        return new Client(name, phone, email);
    }

    /**
     * Converts a task into its escaped single-line storage representation. A
     * task with a priority gains a trailing field holding the level word.
     *
     * @param task task to serialize.
     * @return storage line for the task.
     */
    private String toFileLine(Task task) {
        String done = task.isDone() ? "1" : "0";
        String description = escape(task.getDescription());
        String line;
        if (task instanceof Todo) {
            line = "T | " + done + " | " + description;
        } else if (task instanceof Deadline) {
            Deadline deadline = (Deadline) task;
            line = "D | " + done + " | " + description + " | " + deadline.getBy().toString();
        } else if (task instanceof Event) {
            Event event = (Event) task;
            line = "E | " + done + " | " + description + " | " + escape(event.getFrom())
                    + " | " + escape(event.getTo());
        } else {
            throw new AssertionError("Unknown task type: " + task);
        }
        Priority priority = task.getPriority();
        return priority == null ? line : line + " | " + priority.getWord();
    }

    /**
     * Converts a client into its escaped single-line storage representation.
     * Both optional fields are always written, so the field count is stable.
     *
     * @param client client to serialize.
     * @return storage line for the client.
     */
    private String toClientLine(Client client) {
        return "C | " + escape(client.getName()) + " | " + escape(client.getPhone())
                + " | " + escape(client.getEmail());
    }

    /**
     * Escapes delimiters and escape characters before writing stored text.
     *
     * @param text text to escape.
     * @return escaped text.
     */
    private static String escape(String text) {
        return text.replace("\\", "\\\\").replace("|", "\\|");
    }

    /**
     * Restores delimiters and escape characters after reading stored text.
     *
     * @param text escaped text to restore.
     * @return unescaped text.
     */
    private static String unescape(String text) {
        return text.replace("\\\\", "\\").replace("\\|", "|");
    }
}
