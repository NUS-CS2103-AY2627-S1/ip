package meowmeow.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import meowmeow.MeowmeowException;
import meowmeow.task.Deadline;
import meowmeow.task.Event;
import meowmeow.task.Task;
import meowmeow.task.TaskDateTime;
import meowmeow.task.TaskPriority;
import meowmeow.task.TaskStatus;
import meowmeow.task.TaskType;
import meowmeow.task.Todo;
import meowmeow.ui.Ui;

/**
 * Reads and writes Meowmeow's task list to a plain-text file on disk, so
 * tasks survive between runs. Each task is stored as one pipe-separated
 * line - see {@link Task#toFileString()} for the exact format, including
 * the priority field appended only when a task has one.
 *
 * <p>The path is always <em>relative</em> to the working directory (never
 * something machine-specific like {@code C:\data}) and is built from
 * separate name segments via {@link Path#of(String, String...)}, so the
 * same code locates the file correctly on Windows, macOS and Linux.
 *
 * <p>First-run friendly: {@link #load()} treats a missing file or missing
 * folder as "no tasks yet" rather than an error, and {@link #save(List)}
 * creates the folder before writing.
 *
 * <p>Problems that are not fatal to the session - an unreadable line, or a
 * failed save - are reported to the user through {@link Ui} rather than
 * thrown, so one bad line never loses the rest of the list.
 */
public class Storage {

    /**
     * The regex separating fields on one saved line, e.g. the {@code " | "}
     * in {@code "D | 0 | return book | 2019-12-02 1800"}. The pipe is escaped
     * because {@link String#split(String, int)} takes a regex.
     */
    private static final String FIELD_SEPARATOR = " \\| ";

    // Field positions on a saved line, shared by every task type.
    private static final int TYPE_TAG_INDEX = 0;
    private static final int DONE_FLAG_INDEX = 1;
    private static final int DESCRIPTION_INDEX = 2;

    // Field positions of the date parts, by task type.
    private static final int DEADLINE_BY_INDEX = 3;
    private static final int EVENT_FROM_INDEX = 3;
    private static final int EVENT_TO_INDEX = 4;

    // How many fields a well-formed line of each task type has, not counting
    // an optional trailing priority field (see readPriority).
    private static final int TODO_FIELD_COUNT = 3;
    private static final int DEADLINE_FIELD_COUNT = 4;
    private static final int EVENT_FIELD_COUNT = 5;

    private final Ui ui;
    private final Path file;

    /**
     * Constructs a storage bound to a relative save-file path, built from the
     * given path segments.
     *
     * @param ui    where load/save warnings are shown.
     * @param first the first segment of the relative path, e.g. {@code "data"}.
     * @param more  any further segments, e.g. {@code "meowmeow.txt"}. Passing
     *              the segments separately (instead of one {@code "data/meowmeow.txt"}
     *              string) keeps the path separator out of our code so it
     *              stays correct on every OS.
     */
    public Storage(Ui ui, String first, String... more) {
        this.ui = ui;
        this.file = Path.of(first, more);
    }

    /**
     * Reads the saved task list back from disk. Returns an empty list if
     * the file doesn't exist yet (a normal first run, not an error). A
     * line that can't be understood is skipped with a warning rather than
     * aborting the load, so one corrupt line doesn't lose every task.
     *
     * @return the tasks read from the file, in file order; empty if the
     *     file does not exist yet.
     */
    public ArrayList<Task> load() {
        ArrayList<Task> tasks = new ArrayList<>();
        // Covers both "someone just cloned the repo" (no ./data folder at
        // all) and "folder exists but no save yet": Files.exists is false
        // in both cases, so a first run simply starts with no tasks.
        if (!Files.exists(file)) {
            return tasks;
        }
        try {
            List<String> lines = Files.readAllLines(file);
            for (String line : lines) {
                if (line.isBlank()) {
                    continue;
                }
                Task task = parseTask(line);
                if (task == null) {
                    ui.showWarning("Meow... I skipped a line I couldn't read: " + line);
                } else {
                    tasks.add(task);
                }
            }
        } catch (IOException e) {
            ui.showWarning("Meow... I couldn't read your saved tasks: " + e.getMessage());
        }
        return tasks;
    }

    /**
     * Rebuilds one {@link Task} from a saved line such as
     * {@code "D | 0 | return book | 2019-12-02 1800"}. Returns {@code null}
     * for any line that isn't in the expected format - unknown type tag, a
     * done-flag that isn't {@code 0} or {@code 1}, fewer fields than the type
     * needs, or a date part that no longer parses - so a corrupted file loses
     * only the bad lines, not all of them.
     *
     * <p>An unrecognised trailing priority field is a milder problem than
     * those: every field the task actually needs is still intact, so
     * {@link #readPriority} degrades it to {@link TaskPriority#NONE} instead
     * of dropping the whole line.
     */
    private Task parseTask(String line) {
        // -1 limit keeps trailing empty fields, so a task whose last part
        // is blank still splits into the expected number of pieces.
        String[] parts = line.split(FIELD_SEPARATOR, -1);
        if (parts.length < TODO_FIELD_COUNT) {
            return null;
        }

        // A tag or flag the enums don't recognise means the line is corrupt;
        // fromTag / fromFileFlag return null for exactly that case.
        TaskType type = TaskType.fromTag(parts[TYPE_TAG_INDEX].trim());
        TaskStatus status = TaskStatus.fromFileFlag(parts[DONE_FLAG_INDEX].trim());
        if (type == null || status == null) {
            return null;
        }

        Task task = buildTask(type, parts);
        if (task == null) {
            return null;
        }
        task.setStatus(status);
        task.setPriority(readPriority(parts, baseFieldCount(type)));
        return task;
    }

    /** Returns how many fields a well-formed line of {@code type} has, not counting an optional priority field. */
    private static int baseFieldCount(TaskType type) {
        switch (type) {
            case TODO:
                return TODO_FIELD_COUNT;
            case DEADLINE:
                return DEADLINE_FIELD_COUNT;
            case EVENT:
                return EVENT_FIELD_COUNT;
            default:
                // Unreachable: every TaskType constant is handled above.
                // Kept so the compiler warns if a new constant is added
                // without a case here.
                throw new IllegalStateException("Unhandled task type: " + type);
        }
    }

    /**
     * Reads the optional trailing priority field at {@code priorityIndex}.
     * Unlike the rest of {@link #parseTask}, an unrecognised or missing
     * priority never drops the line - it only means the task itself carries
     * no priority, so it degrades to {@link TaskPriority#NONE} instead.
     *
     * @param parts         the line split on {@link #FIELD_SEPARATOR}.
     * @param priorityIndex where the priority field would be, i.e. the
     *                      task's {@link #baseFieldCount(TaskType) base field count}.
     * @return the saved priority, or {@link TaskPriority#NONE} if the field
     *     is absent or not a priority Meowmeow recognises.
     */
    private static TaskPriority readPriority(String[] parts, int priorityIndex) {
        if (parts.length <= priorityIndex) {
            return TaskPriority.NONE;
        }
        TaskPriority priority = TaskPriority.fromInput(parts[priorityIndex].trim());
        return priority == null ? TaskPriority.NONE : priority;
    }

    /**
     * Builds the {@link Task} subclass named by {@code type} from the date
     * fields on {@code parts}. Returns {@code null} if the line is too short
     * for that type or carries a date that no longer parses - the same "skip
     * just this line" contract {@link #parseTask} applies to any other
     * malformed line.
     *
     * @param type  the task type, already recognised from the line's tag.
     * @param parts the line split on {@link #FIELD_SEPARATOR}.
     * @return the rebuilt task, or {@code null} if the line is malformed.
     */
    private Task buildTask(TaskType type, String[] parts) {
        String description = parts[DESCRIPTION_INDEX];
        try {
            switch (type) {
                case TODO:
                    return new Todo(description);
                case DEADLINE:
                    if (parts.length < DEADLINE_FIELD_COUNT) {
                        return null;
                    }
                    return new Deadline(description, TaskDateTime.parse(parts[DEADLINE_BY_INDEX].trim()));
                case EVENT:
                    if (parts.length < EVENT_FIELD_COUNT) {
                        return null;
                    }
                    return new Event(description,
                            TaskDateTime.parse(parts[EVENT_FROM_INDEX].trim()),
                            TaskDateTime.parse(parts[EVENT_TO_INDEX].trim()));
                default:
                    // Unreachable: every TaskType constant is handled above.
                    // Kept so the compiler warns if a new constant is added
                    // without a case here.
                    throw new IllegalStateException("Unhandled task type: " + type);
            }
        } catch (MeowmeowException unreadableDate) {
            // A saved date we can no longer parse (e.g. a file written by an
            // older Meowmeow that stored free text). Treat the line as
            // corrupt - the same "skip it with a warning" contract as any
            // other malformed line.
            return null;
        }
    }

    /**
     * Writes the whole task list to disk, overwriting any previous
     * contents. The parent folder (e.g. {@code ./data}) is created if it
     * doesn't exist yet. A failure here is reported as a warning rather
     * than crashing the program, since losing a save is not fatal to the
     * current session.
     */
    public void save(List<Task> tasks) {
        try {
            // Files.createDirectories is a no-op if the folder already
            // exists, so it's safe to call on every save.
            Path parent = file.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            // Render every task to its saved line, then write them all at
            // once. Files.write truncates any previous contents and adds the
            // line separators.
            List<String> lines = tasks.stream()
                    .map(Task::toFileString)
                    .toList();
            Files.write(file, lines);
        } catch (IOException e) {
            ui.showWarning("Meow... I couldn't save your tasks: " + e.getMessage());
        }
    }
}
