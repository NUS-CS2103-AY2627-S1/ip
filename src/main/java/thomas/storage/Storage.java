package thomas.storage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;

import thomas.ThomasException;
import thomas.task.DeadlineTask;
import thomas.task.EventTask;
import thomas.task.Task;
import thomas.task.TaskList;
import thomas.task.TodoTask;

/**
 * Loads tasks from the save file and writes them back to it.
 * <p>
 * The save file format is known here and nowhere else: the encoding side lives
 * on {@link Task#toSaveFormat()} and the decoding side lives in this class, so
 * changing how a task is stored means changing those two places and no others.
 * The rest of the program asks for tasks and hands back tasks, never lines.
 * <p>
 * A {@code Storage} is created with the path it works on rather than reading a
 * constant, so the caller decides where tasks are kept -- which is also what
 * lets a test point one at a throwaway file.
 */
public class Storage {
    /** Where the type letter sits in a save file line. */
    private static final int INDEX_TYPE = 0;

    /** Where the done flag sits. */
    private static final int INDEX_DONE = 1;

    /** Where the description sits. */
    private static final int INDEX_DESCRIPTION = 2;

    /** Where a deadline's date sits, and an event's start. */
    private static final int INDEX_FIRST_DATE = 3;

    /** Where an event's end sits. */
    private static final int INDEX_SECOND_DATE = 4;

    /**
     * Fewest fields any line can hold and still name a task.
     * <p>
     * The same number as {@link #FIELDS_TODO} but a different rule: this one
     * says the line is readable at all, that one says a todo is written with
     * exactly three fields and no more.
     */
    private static final int FIELDS_MINIMUM = 3;

    /** How many fields a todo is written with. */
    private static final int FIELDS_TODO = 3;

    /** How many fields a deadline is written with. */
    private static final int FIELDS_DEADLINE = 4;

    /** How many fields an event is written with. */
    private static final int FIELDS_EVENT = 5;

    /** The done flag as written for a completed task. */
    private static final String FLAG_DONE = "1";

    /** Where the task list is read from and written to. */
    private final String filePath;

    /**
     * What went wrong with each line the last {@link #load()} had to skip.
     * <p>
     * The complaints themselves, not the lines they are about: each says what
     * was wrong and quotes the line back inside it.
     * <p>
     * Collected rather than printed, because printing is {@link thomas.ui.Ui}'s job and
     * this class does not know about the screen. The caller shows them after
     * loading, which is when they were noticed, so the user still sees them
     * before the first command runs.
     */
    private final ArrayList<String> skipComplaints = new ArrayList<>();

    /**
     * Creates a storage over one file.
     * <p>
     * A relative path resolves against the directory the program was started
     * from. Nothing is opened here: a save file that does not exist yet is the
     * normal first run, and is dealt with by {@link #load()}.
     *
     * @param filePath Where the task list is kept, for example
     *                 {@code "./data/tasklist.txt"}.
     */
    public Storage(String filePath) {
        // The path is chosen by the program, never typed by the user, so a
        // blank one is a mistake in the code. Without this a blank path
        // becomes a File("") that never exists, so every run loads nothing
        // and every save fails -- the tasks look lost rather than misfiled.
        assert filePath != null && !filePath.isBlank() : "Save file path must be given";
        this.filePath = filePath;
    }

    /**
     * Checks that a save file line holds exactly the fields its type needs.
     *
     * @param fields The line already split on the field separator.
     * @param expected How many fields this task type is written with.
     * @param savedLine The line those fields came from, quoted back in the message.
     * @throws ThomasException If the count does not match.
     */
    private static void requireFieldCount(String[] fields, int expected, String savedLine)
            throws ThomasException {
        if (fields.length != expected) {
            throw new ThomasException("expected " + expected + " fields but found "
                    + fields.length + ": " + savedLine);
        }
    }

    /**
     * Builds the task a line describes, from the fields it has been split into.
     * <p>
     * Each type has an exact field count. Checking for exactly the right
     * number, rather than at least it, is what catches a description that
     * itself contains {@code " | "}: that splits into an extra field and
     * would otherwise be loaded back silently truncated.
     *
     * @param fields The line already split on the field separator.
     * @param savedLine The line those fields came from, quoted back in any message.
     * @return The task those fields describe, not yet marked done.
     * @throws ThomasException If the type is unknown, the field count is wrong,
     *                         the description is blank or a date cannot be read.
     */
    private static Task buildTask(String[] fields, String savedLine) throws ThomasException {
        String description = fields[INDEX_DESCRIPTION];
        // Every task the chatbot writes has a description, so a blank one can
        // only come from the file being edited. Task's constructor treats a
        // blank description as a broken contract and asserts, which would end
        // start-up for one damaged line; refusing it here costs that line alone.
        if (description.isBlank()) {
            throw new ThomasException("empty description: " + savedLine);
        }
        return switch (fields[INDEX_TYPE]) {
            case "T" -> {
                requireFieldCount(fields, FIELDS_TODO, savedLine);
                yield new TodoTask(description);
            }
            case "D" -> {
                requireFieldCount(fields, FIELDS_DEADLINE, savedLine);
                LocalDateTime byDate =
                        Task.parseDate(fields[INDEX_FIRST_DATE], "a deadline date");
                yield new DeadlineTask(description, byDate);
            }
            case "E" -> {
                requireFieldCount(fields, FIELDS_EVENT, savedLine);
                LocalDateTime fromDate =
                        Task.parseDate(fields[INDEX_FIRST_DATE], "a start date");
                LocalDateTime toDate =
                        Task.parseDate(fields[INDEX_SECOND_DATE], "an end date");
                yield new EventTask(description, fromDate, toDate);
            }
            default -> throw new ThomasException("unknown task type '"
                    + fields[INDEX_TYPE] + "': " + savedLine);
        };
    }

    /**
     * Turns one line of the save file back into a task.
     * <p>
     * The line is split on the field separator rather than parsed out of the
     * display text, so the shape is fixed and known: type letter, done flag,
     * description, then whatever extra fields that type carries. Which task
     * those fields make is {@link #buildTask}'s to settle, leaving this method
     * with the shape of the line and the one field every type shares.
     *
     * @param savedLine One line of the save file, without its line separator.
     * @return The task the line describes.
     * @throws ThomasException If the type is unknown or fields are missing.
     */
    private static Task parseSavedTask(String savedLine) throws ThomasException {
        // -1 keeps trailing empty fields, so a line ending in a separator is
        // reported as corrupt below rather than silently shortening the array.
        // The separator is a regex here, so its | must be escaped.
        String[] fields = savedLine.split(" \\| ", -1);
        if (fields.length < FIELDS_MINIMUM) {
            throw new ThomasException("too few fields: " + savedLine);
        }

        Task task = buildTask(fields, savedLine);

        assert task != null : "Parsing a save file line yielded no task: " + savedLine;

        // Anything but the done flag is treated as not done, so a damaged
        // flag costs the tick rather than the whole task.
        if (fields[INDEX_DONE].equals(FLAG_DONE)) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Returns the complaints about lines the last {@link #load()} had to skip.
     * <p>
     * Empty when the file was read cleanly, which is the usual case. The caller
     * shows these through {@link thomas.ui.Ui}, so that this class stays free of any
     * knowledge of how the user is talked to.
     *
     * @return One message per skipped line, in the order the lines appeared.
     */
    public ArrayList<String> getSkipComplaints() {
        return skipComplaints;
    }

    /**
     * Adds the task one save file line describes, or records why it could not
     * be read.
     * <p>
     * A blank line is passed over quietly: it is not damage worth reporting.
     * Anything else that cannot be read costs that line alone, because the
     * complaint is collected rather than thrown on -- one damaged line should
     * not cost the user every other task in the file.
     * <p>
     * A line repeating a task already read is skipped too, and reported. The
     * chatbot never writes one, because {@link TaskList#add} refuses the
     * duplicate as it is typed; so one can only arrive by the file being
     * edited, and loading it would put onto the list exactly what typing it
     * is refused. The first copy is kept, since it is the one that was there
     * before, and the same rule as the list's decides what counts as a copy.
     *
     * @param savedLine One line of the save file, without its line separator.
     * @param tasks The tasks read so far, appended to when the line decodes.
     */
    private void addTaskFrom(String savedLine, ArrayList<Task> tasks) {
        if (savedLine.isBlank()) {
            return;
        }
        try {
            Task task = parseSavedTask(savedLine);
            if (tasks.contains(task)) {
                throw new ThomasException("repeats an earlier line: " + savedLine);
            }
            tasks.add(task);
        } catch (ThomasException e) {
            skipComplaints.add(e.getMessage());
        }
    }

    /**
     * Reads the saved tasks.
     * <p>
     * A missing file is the normal first run, not an error, so it gives back an
     * empty list. What becomes of any one line is {@link #addTaskFrom}'s to
     * settle, leaving this method with the file: whether it is there, reading
     * it a line at a time, and closing it afterward.
     *
     * @return The tasks the file holds, in the order they were written.
     * @throws IOException If the file exists but cannot be read, or is a folder.
     */
    public ArrayList<Task> load() throws IOException {
        // Cleared rather than appended to, so a second load reports only what
        // that load skipped instead of everything ever skipped.
        skipComplaints.clear();

        ArrayList<Task> tasks = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            return tasks;
        }
        // Said in words here rather than left to Scanner, whose complaint is
        // an OS message with the path and "(Is a directory)" -- true, but not
        // telling the user what to do about it.
        if (file.isDirectory()) {
            throw new IOException("'" + filePath + "' is a folder, and I need it to be a file. "
                    + "Move the folder out of the way.");
        }

        // try-with-resources: the Scanner holds a real file handle, so it is
        // closed however this block ends, including on an exception.
        try (Scanner scan = new Scanner(file)) {
            while (scan.hasNextLine()) {
                // nextLine(), not next(): descriptions contain spaces, and
                // next() would hand back one word at a time.
                addTaskFrom(scan.nextLine(), tasks);
            }
        }
        return tasks;
    }

    /**
     * Writes every task to the save file, replacing what was there before.
     * <p>
     * The list is only read, never emptied: this runs after every change to the
     * task list, so mutating it here would delete the tasks it is meant to be
     * saving.
     *
     * @param tasks The tasks to write, left unchanged.
     * @throws IOException If the folder cannot be made, or the file cannot be
     *                     written, including because either is the wrong kind
     *                     of thing: a file where the folder should be, or a
     *                     folder where the file should be.
     */
    public void save(TaskList tasks) throws IOException {
        File file = new File(filePath);

        // FileWriter cannot create missing folders, so ./data must be made
        // first. mkdirs() creates every missing level and is a no-op when they
        // already exist. getParentFile() is null for a bare filename.
        // mkdirs() reports failure by its return value rather than by
        // throwing, so it is checked: the usual cause is a plain file already
        // sitting where the folder should go, which FileWriter would then
        // report as the file being missing -- a message pointing away from
        // the fault.
        File folder = file.getParentFile();
        if (folder != null && !folder.isDirectory() && !folder.mkdirs()) {
            throw new IOException("I couldn't make the folder '" + folder + "'. "
                    + "Is there a file with that name in the way?");
        }
        if (file.isDirectory()) {
            throw new IOException("'" + filePath + "' is a folder, and I need it to be a file. "
                    + "Move the folder out of the way.");
        }

        // try-with-resources: closing is what flushes buffered text to disk, so
        // skipping it on an exception would lose the tasks.
        try (FileWriter fw = new FileWriter(file)) {
            for (int i = 0; i < tasks.size(); i++) {
                String encoded = tasks.get(i).toSaveFormat();
                // One task per line is the whole shape of this file, and
                // load() splits it back on exactly that. A description
                // carrying a newline would write two lines for one task, so
                // the next run reads one corrupt line plus one task that was
                // never added -- a silent loss noticed runs later, if at all.
                assert !encoded.contains("\n") && !encoded.contains("\r")
                        : "A task encodes to more than one line: " + encoded;
                fw.write(encoded + System.lineSeparator());
            }
        }
    }
}
