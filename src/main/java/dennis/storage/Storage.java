package dennis.storage;

import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import dennis.DennisException;
import dennis.task.Deadline;
import dennis.task.Event;
import dennis.task.Task;
import dennis.task.Todo;
import dennis.task.WithinPeriodTask;

/**
 * Saves the task list to disk and loads it back so that tasks persist
 * between runs of the chatbot.
 *
 * <p>Design notes:</p>
 * <ul>
 *   <li><b>Location.</b> The file is {@code ./data/dennis.txt}, built from a
 *       relative path with {@link Path#of(String, String...)} so it works on
 *       any operating system and any machine. An absolute path such as
 *       {@code C:\data} would break when the project is moved.</li>
 *   <li><b>Missing file or folder.</b> On the first run the {@code data}
 *       folder and the file do not exist. {@link #save} creates the folder;
 *       {@link #load} simply returns an empty list.</li>
 *   <li><b>Corrupted file.</b> {@link #load} never lets a bad file stop
 *       start-up: unreadable lines are skipped, each with a note on the error
 *       stream, and the readable tasks are still loaded. The next change to
 *       the list rewrites the file in the correct format.</li>
 *   <li><b>I/O failures.</b> If the file cannot be read or written (no
 *       permission, disk full, path is a folder, ...), the error is reported
 *       on the error stream and the chatbot keeps running with whatever is in
 *       memory.</li>
 * </ul>
 */
public class Storage {
    /** Number of {@code " | "}-separated fields expected for each task type. */
    private static final int TODO_FIELDS = 3;
    private static final int DEADLINE_FIELDS = 4;
    private static final int EVENT_FIELDS = 5;
    private static final int WITHIN_FIELDS = 5;

    /** Location of the save file (relative, OS-independent). */
    private final Path filePath;

    /**
     * Creates a storage that reads and writes {@code ./data/dennis.txt},
     * relative to the directory the program is started from.
     */
    public Storage() {
        this(Path.of("data", "dennis.txt"));
    }

    /**
     * Creates a storage that reads and writes the given file. Mainly for
     * tests, which point this at a temporary directory instead of the real
     * save file.
     *
     * @param filePath the save file to read from and write to
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Writes every task to the save file, one encoded task per line,
     * replacing any previous contents. The parent {@code data} folder is
     * created first if it does not exist yet.
     *
     * <p>Any I/O problem is reported on the error stream and swallowed: the
     * in-memory task list is already updated, so the chatbot stays usable
     * even if this particular save did not reach the disk.</p>
     *
     * @param tasks the current task list to persist; a {@code null} list is
     *              treated as "nothing to do"
     */
    public void save(List<Task> tasks) {
        if (tasks == null) {
            return;
        }

        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            List<String> lines = tasks.stream()
                    .map(Task::toFileFormat)
                    .toList();

            Files.write(filePath, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Warning: could not save tasks to " + filePath
                    + " (" + e.getMessage() + "). "
                    + "Your latest change is kept in memory only.");
        }
    }

    /**
     * Reads the saved tasks back from the file, one task per line.
     *
     * @return the tasks loaded from disk; an empty list if the file is
     *         missing, empty, unreadable, or entirely corrupted
     */
    public ArrayList<Task> load() {
        List<String> lines = readLines();
        ArrayList<Task> tasks = new ArrayList<>();

        int skipped = 0;
        for (int i = 0; i < lines.size(); i++) {
            String line = stripLeadingBom(lines.get(i), i);
            if (line.isBlank()) {
                continue;
            }

            try {
                tasks.add(parseTask(line));
            } catch (DennisException e) {
                skipped++;
                System.err.println("Skipping unreadable line " + (i + 1)
                        + " in " + filePath + ": " + e.getMessage());
            }
        }

        if (skipped > 0) {
            System.err.println("Loaded " + tasks.size() + " task(s) from "
                    + filePath + "; " + skipped + " line(s) were ignored.");
        }

        return tasks;
    }

    /**
     * Reads every line of the save file, treating a missing, non-regular, or
     * unreadable file the same way: as "no saved tasks yet" rather than a
     * fatal error, since a fresh or damaged save file must never stop
     * start-up.
     *
     * @return the file's lines, or an empty list if it cannot be read
     */
    private List<String> readLines() {
        // Missing file is the normal "first run" case: start with no tasks.
        if (!Files.exists(filePath)) {
            return List.of();
        }

        // The path exists but is a folder, a broken link, or otherwise not a
        // plain readable file.
        if (!Files.isRegularFile(filePath)) {
            System.err.println("Warning: " + filePath + " is not a readable "
                    + "file; starting with an empty task list.");
            return List.of();
        }

        try {
            return Files.readAllLines(filePath, StandardCharsets.UTF_8);
        } catch (MalformedInputException e) {
            System.err.println("Warning: " + filePath + " is not valid UTF-8 "
                    + "text; starting with an empty task list.");
            return List.of();
        } catch (IOException e) {
            System.err.println("Warning: could not read " + filePath + " ("
                    + e.getMessage() + "); starting with an empty task list.");
            return List.of();
        }
    }

    /**
     * Strips a UTF-8 byte-order mark from the first line, if present. Some
     * editors add one when saving a file as UTF-8; left in place it would
     * attach itself to the first field of the first task.
     *
     * @param line      the raw line as read from the file
     * @param lineIndex the line's 0-based position in the file
     * @return {@code line} with a leading BOM removed, if {@code lineIndex}
     *         is 0 and one was present; {@code line} unchanged otherwise
     */
    private static String stripLeadingBom(String line, int lineIndex) {
        if (lineIndex == 0 && !line.isEmpty() && line.charAt(0) == '\uFEFF') {
            return line.substring(1);
        }
        return line;
    }

    /**
     * Turns one saved line back into a {@link Task}. The accepted format is
     * exactly what {@link Task#toFileFormat()} produces: fields separated by
     * {@link Task#SAVE_SEPARATOR}, a type tag of {@code T}, {@code D},
     * {@code E} or {@code W}, and a done flag of {@code 0} or {@code 1}.
     * Dates are stored
     * in ISO {@code yyyy-MM-dd} form. Example:
     * {@code D | 0 | return book | 2019-12-01}.
     *
     * @param line a single non-blank line from the save file
     * @return the reconstructed task
     * @throws DennisException if the line does not match the expected format
     */
    private Task parseTask(String line) throws DennisException {
        // limit -1 keeps trailing empty fields, so "T | 0 | " is seen as a
        // blank description rather than silently losing the field.
        String[] parts = line.split(" \\| ", -1);

        if (parts.length < TODO_FIELDS) {
            throw new DennisException("expected at least " + TODO_FIELDS
                    + " fields but found " + parts.length + " in \"" + line + "\"");
        }

        boolean isDone = parseDoneFlag(parts[1].trim(), line);
        Task task = buildTask(parts, line);

        if (isDone) {
            task.markAsDone();
        }

        return task;
    }

    /**
     * Builds the type-specific task described by {@code parts}, once the
     * common envelope fields (type tag and done flag) have already been read.
     *
     * @param parts the line's {@code " | "}-separated fields
     * @param line  the whole line, for error messages
     * @return a {@link Todo}, {@link Deadline} or {@link Event} matching the
     *         type tag in {@code parts[0]}
     * @throws DennisException if the type tag is unrecognised, or the field
     *                         count does not match that type
     */
    private static Task buildTask(String[] parts, String line) throws DennisException {
        String type = parts[0].trim();
        String description = parts[2].trim();

        switch (type) {
            case "T":
                requireExactFields(parts, TODO_FIELDS, line);
                return new Todo(description);
            case "D":
                requireExactFields(parts, DEADLINE_FIELDS, line);
                return new Deadline(description, parts[3].trim());
            case "E":
                requireExactFields(parts, EVENT_FIELDS, line);
                return new Event(description, parts[3].trim(), parts[4].trim());
            case "W":
                requireExactFields(parts, WITHIN_FIELDS, line);
                return new WithinPeriodTask(description, parts[3].trim(), parts[4].trim());
            default:
                throw new DennisException("unknown task type \"" + type
                        + "\" (expected T, D, E or W) in \"" + line + "\"");
        }
    }

    /**
     * Parses the completion flag, which must be exactly {@code 0} or {@code 1}.
     *
     * @param flag the trimmed second field
     * @param line the whole line, for the error message
     * @return {@code true} for {@code 1}, {@code false} for {@code 0}
     * @throws DennisException if the flag is anything else
     */
    private static boolean parseDoneFlag(String flag, String line)
            throws DennisException {
        if (flag.equals("1")) {
            return true;
        }
        if (flag.equals("0")) {
            return false;
        }
        throw new DennisException("the done flag must be 0 or 1 but was \""
                + flag + "\" in \"" + line + "\"");
    }

    /**
     * Checks that a line split into exactly {@code expected} fields.
     *
     * @param parts    the fields produced by splitting the line
     * @param expected the number of fields this task type must have
     * @param line     the whole line, for the error message
     * @throws DennisException if the field count does not match
     */
    private static void requireExactFields(String[] parts, int expected,
            String line) throws DennisException {
        if (parts.length != expected) {
            throw new DennisException("expected " + expected
                    + " fields but found " + parts.length + " in \"" + line + "\"");
        }
    }
}
