package seedu.mattchatbot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import seedu.mattchatbot.task.Deadline;
import seedu.mattchatbot.task.Event;
import seedu.mattchatbot.task.Task;
import seedu.mattchatbot.task.TaskList;
import seedu.mattchatbot.task.Todo;

/**
 * Reads the task list from disk when the chatbot starts, and writes it back
 * whenever the list changes.
 */
public class Storage {

    /** Position of the type marker within a saved line, e.g. {@code T}. */
    private static final int TYPE_FIELD = 0;

    /** Position of the done flag within a saved line, {@code 0} or {@code 1}. */
    private static final int STATUS_FIELD = 1;

    /** Position of the description within a saved line. */
    private static final int DESCRIPTION_FIELD = 2;

    /** Position of a deadline's due date, or an event's start. */
    private static final int FIRST_DATE_FIELD = 3;

    /** Position of an event's end. */
    private static final int SECOND_DATE_FIELD = 4;

    /** How many fields a saved todo has: type, status, description. */
    private static final int TODO_FIELD_COUNT = 3;

    /** How many fields a saved deadline has: a todo's, plus the due date. */
    private static final int DEADLINE_FIELD_COUNT = 4;

    /** How many fields a saved event has: a todo's, plus a start and an end. */
    private static final int EVENT_FIELD_COUNT = 5;

    /**
     * Where this Storage keeps the task list.
     * <p>
     * Passed in rather than fixed, so the caller decides where the data lives
     * and a test can point at a scratch file instead of the real one.
     */
    private final Path filePath;

    /**
     * What was wrong with each line the most recent {@link #load()} skipped.
     * <p>
     * The reasons are kept, not just the count. A file that has been edited by
     * hand is the likeliest way to end up here, and "one line was unreadable"
     * gives the user nothing to go and fix.
     */
    private final List<String> skippedLineReports = new ArrayList<>();

    /**
     * Creates a Storage reading and writing the given file.
     * <p>
     * Converted with {@link Paths#get(String, String...)}, which accepts "/"
     * as a separator on every platform and applies the one the current
     * operating system uses. Keep the path relative, so the chatbot works
     * wherever the project folder is copied to.
     *
     * @param filePath the save file's path, e.g. {@code data/mattchatbot.txt}
     */
    public Storage(String filePath) {
        this.filePath = Paths.get(filePath);
    }

    /**
     * Writes the whole task list to disk, replacing whatever was there before.
     * <p>
     * Rewriting the entire file on every change is more work than appending,
     * but it is the simplest thing that stays correct when a task is deleted
     * or its status changes, which appending alone cannot express.
     *
     * @param tasks the tasks to save
     * @throws MattChatBotException if the file cannot be written
     */
    public void save(TaskList tasks) throws MattChatBotException {
        // Each line carries its own terminator rather than joining on one, so
        // that the file ends with a newline and an empty list writes nothing,
        // which is exactly what the previous loop produced.
        String contents = tasks.asList().stream()
                .map(task -> task.toFileFormat() + System.lineSeparator())
                .collect(Collectors.joining());
        // createDirectories cannot be given null, which is what getParent
        // returns for a bare file name such as "tasks.txt". Every save path in
        // use names a folder, and this records that the code counts on it.
        assert filePath.getParent() != null : "the save file always sits inside a folder";
        try {
            // The folder may not exist yet, e.g. on a fresh copy of the project.
            Files.createDirectories(filePath.getParent());
            Files.writeString(filePath, contents);
        } catch (IOException e) {
            throw new MattChatBotException(
                    "I was unable to save your list to " + filePath + ".");
        }
    }

    /**
     * Reads the saved task list back from disk.
     * <p>
     * A missing file is not an error: it simply means nothing has been saved
     * yet, which is what a first run on another computer looks like.
     *
     * @return the saved tasks, or an empty list if there is nothing saved
     * @throws MattChatBotException if the file exists but cannot be read
     */
    public ArrayList<Task> load() throws MattChatBotException {
        ArrayList<Task> tasks = new ArrayList<>();
        if (!Files.exists(filePath)) {
            return tasks;
        }
        skippedLineReports.clear();
        try {
            int lineNumber = 0;
            for (String line : Files.readAllLines(filePath)) {
                lineNumber++;
                if (line.isBlank()) {
                    continue;
                }
                try {
                    tasks.add(parse(line));
                } catch (MattChatBotException e) {
                    // One damaged line should not cost the user every other
                    // task, so skip it and carry on with the rest of the file.
                    skippedLineReports.add("line " + lineNumber + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new MattChatBotException(
                    "I was unable to read your saved list from " + filePath + ".");
        }
        return tasks;
    }

    /**
     * Turns one line of the save file back into a task.
     *
     * @param line one line of the save file
     * @return the task it describes
     * @throws MattChatBotException if the line is not in the expected format
     */
    private static Task parse(String line) throws MattChatBotException {
        String[] fields = line.split("\\s*\\|\\s*");
        String type = fields.length > 0 ? fields[TYPE_FIELD] : "";
        checkFieldCount(type, fields.length);
        Task task = createTask(type, fields);
        // createTask throws on an unrecognised type rather than falling
        // through, so there is always a task here to apply the flag to.
        assert task != null : "an unrecognised type throws rather than yielding null";
        applyStatus(task, fields[STATUS_FIELD]);
        return task;
    }

    /**
     * Rejects a line that does not hold the number of fields its type needs.
     * <p>
     * Checked before any field is read, so that the rest of the parsing can
     * index into the line without guarding each access.
     *
     * @param type   the type marker read from the line
     * @param actual how many fields the line actually holds
     * @throws MattChatBotException if the type is unknown or the count is wrong
     */
    private static void checkFieldCount(String type, int actual) throws MattChatBotException {
        int expected = switch (type) {
            case Todo.TYPE_ICON -> TODO_FIELD_COUNT;
            case Deadline.TYPE_ICON -> DEADLINE_FIELD_COUNT;
            case Event.TYPE_ICON -> EVENT_FIELD_COUNT;
            default -> throw new MattChatBotException("Unknown task type: " + type);
        };
        if (actual != expected) {
            throw new MattChatBotException("Expected " + expected
                    + " fields but found " + actual);
        }
    }

    /**
     * Builds the task a line describes, not yet marked done or not done.
     *
     * @param type   the type marker read from the line
     * @param fields the line's fields, already counted by {@link #checkFieldCount}
     * @return the task described
     * @throws MattChatBotException if the description or a date is unusable
     */
    private static Task createTask(String type, String[] fields) throws MattChatBotException {
        String description = fields[DESCRIPTION_FIELD];
        if (description.isBlank()) {
            throw new MattChatBotException("Task has no description");
        }
        return switch (type) {
            case Todo.TYPE_ICON -> new Todo(description);
            case Deadline.TYPE_ICON ->
                new Deadline(description, DateTimes.parse(fields[FIRST_DATE_FIELD]));
            case Event.TYPE_ICON -> new Event(description, DateTimes.parse(fields[FIRST_DATE_FIELD]),
                    DateTimes.parse(fields[SECOND_DATE_FIELD]));
            default -> throw new MattChatBotException("Unknown task type: " + type);
        };
    }

    /**
     * Applies a line's done flag to the task built from it.
     *
     * @param task   the task just built
     * @param status the flag read from the line
     * @throws MattChatBotException if the flag is neither {@code 0} nor {@code 1}
     */
    private static void applyStatus(Task task, String status) throws MattChatBotException {
        if (status.equals("1")) {
            task.markAsDone();
        } else if (status.equals("0")) {
            task.markAsNotDone();
        } else {
            throw new MattChatBotException("Status must be 0 or 1");
        }
    }

    /**
     * Returns how many damaged lines the most recent {@link #load()} skipped.
     *
     * @return the number of lines skipped, zero if the file was intact
     */
    public int getSkippedLineCount() {
        return skippedLineReports.size();
    }

    /**
     * Returns what was wrong with each line the most recent {@link #load()}
     * skipped, so the chatbot can tell the user what to go and fix.
     *
     * @return one report per skipped line, naming the line and the problem
     */
    public List<String> getSkippedLineReports() {
        return List.copyOf(skippedLineReports);
    }
}
