package shannon;

import java.io.IOException;
import java.nio.charset.CharacterCodingException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import shannon.exception.InvalidDateException;
import shannon.exception.ShannonException;
import shannon.exception.StorageException;
import shannon.task.Deadline;
import shannon.task.Event;
import shannon.task.EventTime;
import shannon.task.Task;
import shannon.task.Todo;

/**
 * Reads and writes the task list as a text file, so it survives between runs.
 * <p>
 * One task per line, fields separated by {@code " | "}:
 * <pre>
 * T | 1 | read book
 * D | 0 | return book | 2026-08-09
 * E | 0 | project meeting | 2026-08-06 14:00 | 2026-08-06 16:00
 * </pre>
 * The first field is the task type, the second is {@code 1} for done and {@code 0} for not done.
 * Fields are escaped by {@link Task#escape(String)} so that a description containing a {@code |}
 * cannot be mistaken for a field separator, and {@link #splitFields(String)} undoes that on the
 * way back in.
 * <p>
 * The file is an ordinary text file, so it can go missing, become unreadable, or be edited by
 * hand into something that is not a task list. None of these stops the chatbot. A missing file
 * just means a first run. A file that cannot be fully loaded is copied to a backup, e.g.
 * {@code shannon.txt.bak}, before the next save rewrites it, so the tasks that could not be read
 * are never silently destroyed.
 */
// The whole list is rewritten on every change, which is the simplest thing that works for a list
// this small; appending only what changed costs far more complexity than a to-do list is worth.
public class Storage {

    /** Added to the save file's name to name its backup copy, e.g. {@code shannon.txt.bak}. */
    private static final String BACKUP_SUFFIX = ".bak";

    /** The save file this Storage reads and writes. */
    private final Path file;

    /** Where a save file that could not be fully loaded is copied, before a save overwrites it. */
    private final Path backupFile;

    /** How many lines the last {@link #load()} could not understand and skipped. */
    private int skippedLineCount;

    /** Whether the last {@link #load()} copied the save file to {@link #backupFile}. */
    private boolean hasBackup;

    /**
     * Whether {@link #save(List)} must refuse to write. Set when the last {@link #load()} could
     * not read the whole file and could not back it up either, since overwriting the file then
     * would destroy tasks that exist nowhere else.
     */
    private boolean isSaveBlocked;

    /**
     * Creates a Storage pointing at one save file, without reading or creating it.
     *
     * @param filePath path to the save file, e.g. {@code ./data/shannon.txt}
     */
    public Storage(String filePath) {
        this.file = Path.of(filePath);
        // save() creates the file's folder, so the path must name one, as ./data/shannon.txt does.
        // A bare "shannon.txt" has no parent folder, and save() would fail on it.
        assert file.getParent() != null : "Save file path must include a folder";
        this.backupFile = file.resolveSibling(file.getFileName() + BACKUP_SUFFIX);
    }

    /**
     * Overwrites the save file with the given tasks, creating the data folder if it is missing.
     *
     * @param tasks the current task list, written in the order given.
     * @throws StorageException if the file could not be written, or must not be because it holds
     *                          tasks that could not be loaded or backed up.
     */
    public void save(List<Task> tasks) throws StorageException {
        if (isSaveBlocked) {
            throw StorageException.whenSavingIsUnsafe(getFilePath());
        }
        String contents = tasks.stream()
                .map(task -> task.toFileFormat() + System.lineSeparator())
                .collect(Collectors.joining());
        try {
            Files.createDirectories(file.getParent());
            // writeString() creates the file if missing and truncates it if it already exists,
            // so the file always matches the list exactly.
            Files.writeString(file, contents);
        } catch (IOException e) {
            throw StorageException.whileSaving(getFilePath(), explain(e));
        }
    }

    /**
     * Reads the saved tasks back.
     * <p>
     * A missing file is not an error: it just means this is the first run, so an empty list is
     * returned. A line that cannot be understood is skipped rather than thrown, so one damaged
     * line does not cost the user every other task in the file; {@link #getSkippedLineCount()}
     * reports how many were skipped so the user can be told.
     * <p>
     * Whenever part of the file could not be loaded, it is first copied to a backup (see
     * {@link #getBackupFilePath()}), because the next save rewrites the file from the tasks that
     * were loaded, and would otherwise drop the rest for good.
     *
     * @return the tasks that could be read, in file order.
     * @throws StorageException if the file exists but could not be read at all.
     */
    public ArrayList<Task> load() throws StorageException {
        skippedLineCount = 0;
        hasBackup = false;
        isSaveBlocked = false;
        ArrayList<Task> tasks = new ArrayList<>();
        if (Files.notExists(file)) {
            return tasks;
        }
        if (Files.isDirectory(file)) {
            // A folder holds no tasks to protect, so there is nothing to back up; save() will
            // keep reporting the problem until the folder is moved out of the way.
            throw StorageException.whileLoading(getFilePath(), "it is a folder, not a file");
        }
        List<String> lines;
        try {
            lines = Files.readAllLines(file);
        } catch (IOException e) {
            protectUnloadedFile();
            throw StorageException.whileLoading(getFilePath(), explain(e));
        }
        for (String line : lines) {
            if (line.isBlank()) {
                continue; // a stray blank line is harmless, not something to warn about
            }
            Task task = parseTask(splitFields(line));
            if (task == null) {
                skippedLineCount++;
            } else {
                tasks.add(task);
            }
        }
        if (skippedLineCount > 0) {
            protectUnloadedFile();
        }
        return tasks;
    }

    /**
     * Returns the save file this Storage reads and writes, for messages that name it.
     *
     * @return the path as it was given to the constructor
     */
    public String getFilePath() {
        return file.toString();
    }

    /**
     * Returns how many lines the last {@link #load()} skipped because it could not read them.
     *
     * @return the number of skipped lines, or 0 if nothing has been loaded yet
     */
    public int getSkippedLineCount() {
        return skippedLineCount;
    }

    /**
     * Returns where the last {@link #load()} copied a save file it could not fully read.
     *
     * @return the backup's path, or an empty string if no copy was made
     */
    public String getBackupFilePath() {
        return hasBackup ? backupFile.toString() : "";
    }

    /**
     * Returns whether saving has been switched off, because the last {@link #load()} could
     * neither fully read the save file nor back it up.
     *
     * @return {@code true} if {@link #save(List)} will refuse to write
     */
    public boolean isSaveBlocked() {
        return isSaveBlocked;
    }

    /**
     * Keeps the tasks that could not be loaded from being lost when the file is next saved.
     * <p>
     * Every save rewrites the whole file from the list in memory, which lacks whatever
     * {@link #load()} could not read, so the file is first copied to {@link #backupFile}. If
     * even that copy fails, saving is blocked instead: an error on every change is better than
     * silently destroying tasks that exist nowhere else.
     */
    private void protectUnloadedFile() {
        try {
            // REPLACE_EXISTING, so the backup always matches the file as it was at this start-up.
            Files.copy(file, backupFile, StandardCopyOption.REPLACE_EXISTING);
            hasBackup = true;
        } catch (IOException e) {
            isSaveBlocked = true;
        }
    }

    /**
     * Returns a plain-English reason for a failed file operation, to go inside an error message.
     * <p>
     * The raw message of an {@link IOException} is often just a path, or something like
     * "Input length = 1", which tells the user nothing, so the common cases are reworded here.
     *
     * @param e what went wrong.
     * @return the reason.
     */
    private static String explain(IOException e) {
        if (e instanceof AccessDeniedException) {
            return "permission denied";
        }
        if (e instanceof FileAlreadyExistsException existing) {
            // Thrown when the data folder cannot be created because a file already has its name.
            return existing.getFile() + " is a file, but it needs to be a folder";
        }
        if (e instanceof CharacterCodingException) {
            return "it is not a plain text file";
        }
        if (e instanceof FileSystemException fileSystemError && fileSystemError.getReason() != null) {
            return fileSystemError.getReason(); // e.g. "No space left on device"
        }
        return e.getMessage() == null ? "an unknown error" : e.getMessage();
    }

    /**
     * Splits one saved line into its fields, undoing the escaping done by
     * {@link Task#escape(String)}.
     *
     * @param line one line of the save file.
     * @return the fields, trimmed of the spaces around each separator.
     */
    private static List<String> splitFields(String line) {
        // The exact inverse of escape(): scan left to right, a backslash consumes the character
        // after it, and only an unescaped | ends a field. String.split() would instead cut the
        // line at an escaped pipe inside a description.
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);
            if (character == '\\' && i + 1 < line.length()) {
                char escaped = line.charAt(++i);
                current.append(switch (escaped) {
                    case 'n' -> '\n';
                    case 'r' -> '\r';
                    default -> escaped;
                });
            } else if (character == '|') {
                fields.add(current.toString().trim());
                current.setLength(0);
            } else {
                current.append(character);
            }
        }
        fields.add(current.toString().trim());
        return fields;
    }

    /**
     * Rebuilds one task from its fields, or reports that the line was not usable.
     * <p>
     * Every field is checked rather than trusted: the file is an ordinary text file that the
     * user can edit by hand, so a wrong task type, a done flag that is not {@code 0} or
     * {@code 1}, a blank description, a date that does not exist, an event that ends before it
     * starts, or the wrong number of fields are all things that can genuinely appear.
     *
     * @param fields the fields of one line, already unescaped
     * @return the task, or {@code null} if the line could not be understood
     */
    private static Task parseTask(List<String> fields) {
        if (fields.size() < 3) {
            return null;
        }
        String doneFlag = fields.get(1);
        String description = fields.get(2);
        boolean isValidFlag = doneFlag.equals(Task.FLAG_DONE) || doneFlag.equals(Task.FLAG_NOT_DONE);
        if (description.isEmpty() || !isValidFlag) {
            return null;
        }
        // Each task type has a fixed number of fields, so a line with too many or too few is
        // damaged even if its type letter is valid.
        Task task = switch (fields.get(0)) {
            case Todo.TYPE_CODE -> fields.size() == 3 ? new Todo(description) : null;
            case Deadline.TYPE_CODE -> fields.size() == 4 ? parseDeadline(description, fields.get(3)) : null;
            case Event.TYPE_CODE -> fields.size() == 5
                    ? parseEvent(description, fields.get(3), fields.get(4))
                    : null;
            default -> null;
        };
        if (task != null && doneFlag.equals(Task.FLAG_DONE)) {
            task.markDone();
        }
        return task;
    }

    /**
     * Rebuilds an {@link Event} from a saved line, reusing {@link EventTime#parse(String)} and
     * the {@link Event} constructor's check, so the file is held to exactly the rules the user's
     * typing is.
     *
     * @param description the task description, already unescaped.
     * @param from        the saved start, expected as {@code yyyy-mm-dd} or {@code yyyy-mm-dd HH:mm}.
     * @param to          the saved end, in the same form.
     * @return the task, or {@code null} if either end could not be read or the event ends
     *         before it starts, which makes the caller skip the line like any other damaged one.
     */
    private static Task parseEvent(String description, String from, String to) {
        try {
            return new Event(description, EventTime.parse(from), EventTime.parse(to));
        } catch (ShannonException e) {
            return null;
        }
    }

    /**
     * Rebuilds a {@link Deadline} from a saved line, reusing {@link Deadline#parseBy(String)} so
     * that the file accepts exactly the dates the user is allowed to type.
     *
     * @param description the task description, already unescaped.
     * @param by          the saved date, expected in {@code yyyy-mm-dd} form.
     * @return the task, or {@code null} if the date could not be read, which makes the caller
     *         skip the line like any other damaged one.
     */
    // A separate method rather than another branch of the switch in parseTask(), because a
    // try/catch does not fit inside a switch expression's arrow.
    private static Task parseDeadline(String description, String by) {
        try {
            return new Deadline(description, Deadline.parseBy(by));
        } catch (InvalidDateException e) {
            return null;
        }
    }
}
