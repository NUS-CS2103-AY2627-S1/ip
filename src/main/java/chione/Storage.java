package chione;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import chione.task.Deadline;
import chione.task.Event;
import chione.task.Task;
import chione.task.TaskList;
import chione.task.Todo;

/**
 * Keeps the task list on disk so that it survives between runs of Chione.
 *
 * <p>Every task is written as one line of a plain text file, e.g.
 * {@code "D | 0 | return book | June 6th"}. A text format was chosen over Java
 * object serialization because the saved file stays readable and editable by a
 * human, which makes it far easier to inspect when something goes wrong.
 *
 * <p>Reading and writing live here rather than in {@link Chione} so that the
 * chatbot only has to decide <em>when</em> to save, not <em>how</em> a task is
 * turned into text and back again.
 */
public class Storage {
    /**
     * Regular expression matching {@link Task#SEPARATOR}.
     *
     * <p>{@code split} takes a regex, and {@code |} means "or" in a regex, so the
     * separator cannot be handed to it as it stands. {@link Pattern#quote} does the
     * escaping, which keeps this in step with the separator the tasks actually
     * write: escaping it by hand here would be a second copy of the same knowledge.
     */
    private static final String SEPARATOR_PATTERN = Pattern.quote(Task.SEPARATOR);

    /** Fields on a saved todo line: the type letter, the done flag and the description. */
    private static final int TODO_FIELD_COUNT = 3;

    /** Fields on a saved deadline line: a todo's three, plus the due date. */
    private static final int DEADLINE_FIELD_COUNT = 4;

    /** Fields on a saved event line: a todo's three, plus the start and the end. */
    private static final int EVENT_FIELD_COUNT = 5;

    /** Position of the type letter in a saved line. */
    private static final int TYPE_INDEX = 0;

    /** Position of the done flag in a saved line. */
    private static final int DONE_INDEX = 1;

    /** Position of the description in a saved line. */
    private static final int DESCRIPTION_INDEX = 2;

    /** Position of the due date on a saved deadline line. */
    private static final int DUE_DATE_INDEX = 3;

    /** Position of the start moment on a saved event line. */
    private static final int START_INDEX = 3;

    /** Position of the end moment on a saved event line. */
    private static final int END_INDEX = 4;

    /**
     * Where the tasks are stored.
     *
     * <p>Built with {@link Paths#get} from path segments rather than by gluing
     * strings together with {@code "/"}, so the same code works on Windows
     * (which uses {@code \}) as well as on macOS and Linux.
     */
    private final Path filePath;

    /**
     * Points this storage at a file.
     *
     * @param filePath where to keep the tasks, as a path relative to the folder
     *                 the program is run from, e.g. {@code "data/chione.txt"}.
     *                 A relative path is used deliberately: an absolute one such
     *                 as {@code C:\data} would break on any other machine.
     */
    public Storage(String filePath) {
        this.filePath = Paths.get(filePath);
    }

    /**
     * Writes the whole task list to disk, replacing whatever was there before.
     *
     * <p>Rewriting the entire file on every change is slower than appending, but
     * it is the only approach that stays correct when a task is edited or
     * deleted, and the list is far too small for the difference to be noticeable.
     *
     * @param tasks the tasks to store
     * @throws ChioneException if the file or its folder cannot be written
     */
    public void save(TaskList tasks) throws ChioneException {
        try {
            // The data folder will not exist the first time anyone runs Chione,
            // and Files.write does not create missing folders for us.
            // createDirectories does nothing if the folder is already there.
            Path parentFolder = filePath.getParent();
            if (parentFolder != null) {
                Files.createDirectories(parentFolder);
            }

            // Each task renders its own line, so this never has to ask what kind
            // of task it is holding. One line out per task in is what map means.
            List<String> lines = tasks.asList().stream()
                    .map(Task::toSaveFormat)
                    .toList();

            // Files.write creates the file if it is missing and truncates it if
            // it is not, which is exactly the "replace the contents" we want.
            Files.write(filePath, lines);
        } catch (IOException e) {
            throw new ChioneException("I could not save your tasks to " + filePath
                    + ". Your list is still fine for this session, "
                    + "but it may not be there next time.");
        }
    }

    /**
     * Reads the task list back from disk.
     *
     * <p>A missing file is not an error: it simply means nobody has saved
     * anything yet, which is the normal state the very first time Chione runs.
     *
     * @return the stored tasks, or an empty list if nothing has been saved yet
     * @throws ChioneException if the file exists but cannot be read or understood
     */
    public ArrayList<Task> load() throws ChioneException {
        ArrayList<Task> tasks = new ArrayList<>();

        if (!Files.exists(filePath)) {
            return tasks;
        }

        List<String> lines;
        try {
            lines = Files.readAllLines(filePath);
        } catch (IOException e) {
            throw new ChioneException("I found " + filePath + " but could not read it.");
        }

        for (String line : lines) {
            // Tolerate blank lines, which a text editor can easily leave behind.
            if (line.isBlank()) {
                continue;
            }
            tasks.add(parseTask(line));
        }
        return tasks;
    }

    /**
     * Rebuilds one task from its saved line.
     *
     * <p>This is the mirror image of {@link Task#toSaveFormat()}. It is strict
     * about the number of fields on purpose: a line that does not look right is
     * reported rather than half-understood, so a damaged save file cannot turn
     * into a silently wrong task list.
     *
     * @param line one line of the save file, known not to be blank
     * @return the task the line describes
     * @throws ChioneException if the line does not match any known task format
     */
    private static Task parseTask(String line) throws ChioneException {
        String[] parts = line.split(SEPARATOR_PATTERN);
        if (parts.length < TODO_FIELD_COUNT) {
            throw buildCorruptedFileException(line);
        }

        String doneFlag = parts[DONE_INDEX];
        if (!doneFlag.equals(Task.NOT_DONE_FLAG) && !doneFlag.equals(Task.DONE_FLAG)) {
            throw buildCorruptedFileException(line);
        }
        Task task = buildTask(parts, line);
        if (doneFlag.equals(Task.DONE_FLAG)) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Builds the kind of task the line's first field claims it is.
     *
     * <p>Each task type has its own field count, so the length check doubles as a
     * check that the line really is of the type it claims to be.
     *
     * @param parts the fields of one saved line
     * @param line  the whole line, quoted back if it cannot be understood
     * @return the task the line describes, not yet marked as done
     * @throws ChioneException if the line does not match any known task format
     */
    private static Task buildTask(String[] parts, String line) throws ChioneException {
        String description = parts[DESCRIPTION_INDEX];
        return switch (parts[TYPE_INDEX]) {
            case Todo.TYPE_LETTER -> {
                requireFieldCount(parts, TODO_FIELD_COUNT, line);
                yield new Todo(description);
            }
            case Deadline.TYPE_LETTER -> {
                requireFieldCount(parts, DEADLINE_FIELD_COUNT, line);
                yield new Deadline(description, parseSavedMoment(parts[DUE_DATE_INDEX], line));
            }
            case Event.TYPE_LETTER -> {
                requireFieldCount(parts, EVENT_FIELD_COUNT, line);
                yield new Event(description,
                        parseSavedMoment(parts[START_INDEX], line),
                        parseSavedMoment(parts[END_INDEX], line));
            }
            default -> throw buildCorruptedFileException(line);
        };
    }

    /**
     * Refuses a line that does not have exactly the number of fields its type needs.
     *
     * @param parts    the fields of one saved line
     * @param expected how many fields that type of line must have
     * @param line     the whole line, quoted back if it cannot be understood
     * @throws ChioneException if the count does not match
     */
    private static void requireFieldCount(String[] parts, int expected, String line)
            throws ChioneException {
        if (parts.length != expected) {
            throw buildCorruptedFileException(line);
        }
    }

    /**
     * Reads one date out of a saved line.
     *
     * <p>{@link DateTimes#parse} explains how to <em>type</em> a date, which is
     * not useful advice about a file, so a date that cannot be read is reported
     * as a damaged line instead.
     *
     * @param text the date field, e.g. {@code "2019-10-15 1800"}
     * @param line the whole line, quoted back if the date cannot be read
     * @return the moment the field refers to
     * @throws ChioneException if the field is not a date Chione can read
     */
    private static LocalDateTime parseSavedMoment(String text, String line)
            throws ChioneException {
        try {
            return DateTimes.parse(text);
        } catch (ChioneException e) {
            throw buildCorruptedFileException(line);
        }
    }

    /**
     * Builds the exception reported for every unreadable line, so that its wording
     * and the advice that goes with it are written once.
     *
     * @param line the line that could not be understood, quoted back to the user
     *             so they can find and fix it by hand
     * @return the exception to throw
     */
    private static ChioneException buildCorruptedFileException(String line) {
        return new ChioneException("My save file has a line I don't understand: \""
                + line + "\". Fix or delete that line and start me again.");
    }
}
