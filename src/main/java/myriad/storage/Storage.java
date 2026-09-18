package myriad.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import myriad.MyriadException;
import myriad.task.Deadline;
import myriad.task.Event;
import myriad.task.Task;
import myriad.task.TaskDateTime;
import myriad.task.TaskList;
import myriad.task.TaskType;
import myriad.task.ToDo;

/**
 * Handles saving and loading task data on disk. Tasks are stored one per
 * line in a single file, using each task's save format (not its display
 * format) so a line can be parsed back into the right {@link Task} subclass.
 *
 * The file to use is given to the constructor rather than fixed as a
 * constant, so which file this reads and writes is decided by the caller
 * (the app uses {@code Myriad.DEFAULT_DATA_FILE}) instead of being hidden in
 * here, and a test can point at a file of its own.
 */
public class Storage {

    /** The data file this {@code Storage} reads and writes. */
    private final Path dataFile;

    /**
     * Creates a {@code Storage} that reads and writes the given file. The
     * file need not exist yet: {@link #load()} treats a missing file as an
     * empty task list, and {@link #save} creates it (and its parent
     * directory) on first write.
     *
     * @param filePath path to the data file, e.g. {@code "data/myriad.txt"}.
     */
    public Storage(String filePath) {
        assert filePath != null && !filePath.isBlank() : "the data file path is set in code, never user-typed";
        this.dataFile = Path.of(filePath);
    }

    /**
     * Overwrites the data file with every task currently in
     * {@code taskList}, one save-format line each, creating the parent
     * directory first if it doesn't already exist (e.g. on a fresh
     * checkout). Called after every mutating command ({@code add},
     * {@code mark}, {@code unmark}, {@code delete}) so the file always
     * exactly mirrors the in-memory list — simpler than tracking per-task
     * deltas, and cheap at the scale of a personal task list.
     * {@link IOException} (e.g. disk full, permission denied) is declared
     * for the caller to translate into a user-facing message; see
     * {@code Command.save}.
     *
     * Lines always end in {@code \n} and the file is always UTF-8, whatever the
     * platform, so a data file moved between machines reads back the same.
     *
     * The data file is never written in place. The new contents go to a
     * temporary file beside it, which is flushed to disk and then moved over
     * the data file in one step, so a crash or power cut part-way through a
     * save leaves either the old list or the new one, never a half-written
     * file. If anything fails, the temporary file is removed and the data
     * file is left as it was.
     *
     * @param taskList the list to write out in full.
     * @throws IOException if the file or its directory can't be written.
     */
    public void save(TaskList taskList) throws IOException {
        assert taskList != null : "Command.save always passes the session's task list";
        if (Files.isDirectory(dataFile)) {
            // Checked up front: moving a file onto an empty directory can
            // quietly replace the directory rather than fail.
            throw new FileSystemException(dataFile.toString(), null, "is a directory");
        }
        Path parentDir = dataFile.getParent();
        if (parentDir != null) {
            // Unlike File.mkdirs, this throws if the directory can't be made,
            // instead of leaving the write below to fail less clearly.
            Files.createDirectories(parentDir);
        }

        StringBuilder content = new StringBuilder();
        for (Task task : taskList.asList()) {
            content.append(task.toSaveFormat()).append('\n');
        }

        // A fixed name, rather than a new random one each time, so a file
        // left behind by a crash is reused by the next save instead of piling up.
        Path tempFile = dataFile.resolveSibling(dataFile.getFileName() + ".tmp");
        try {
            // SYNC makes the write reach the disk before the move below, so the
            // file moved into place is never one the OS had not yet written out.
            Files.writeString(tempFile, content, StandardCharsets.UTF_8, StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.SYNC);
            replaceWith(tempFile, dataFile);
        } catch (IOException e) {
            try {
                Files.deleteIfExists(tempFile);
            } catch (IOException cleanupFailure) {
                e.addSuppressed(cleanupFailure);
            }
            throw e;
        }
    }

    /**
     * Moves {@code source} over {@code target}, replacing it if it exists. Uses an
     * atomic move, which readers see as a single instant switch from the old
     * file to the new one, and falls back to an ordinary move only on a file
     * system that cannot do that.
     *
     * @param source the file to move.
     * @param target where to move it, replacing any file already there.
     * @throws IOException if the move fails.
     */
    private static void replaceWith(Path source, Path target) throws IOException {
        try {
            Files.move(source, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Parses one saved-data line back into a {@link Task}, restoring its
     * type, description, type-specific fields, and done status from the
     * {@code |}-delimited save format written by
     * {@link Task#toSaveFormat()}. Throws {@link MyriadException} if the line
     * has too few fields for its type, or an unrecognized leading type
     * letter — deliberately, rather than guessing (e.g. silently treating an
     * unknown type as a {@code ToDo}), so the caller can report and skip it
     * instead of loading a wrong task.
     *
     * This lives here rather than in {@code Parser}: it reads the save
     * format this class itself writes, which is a detail of how tasks are
     * stored, not of the command language the user types.
     *
     * @param line one line of the data file.
     * @return the {@code Task} that line describes, with its done status restored.
     * @throws MyriadException if the line has too few fields, an unknown
     *                         type letter, an unparseable date, or an event
     *                         that ends before it starts.
     */
    private static Task parseLine(String line) throws MyriadException {
        String[] fields = line.split("\\s*\\|\\s*");
        if (fields.length < 3) {
            throw new MyriadException(
                    "expected at least 3 fields (type | done | description), found " + fields.length);
        }
        TaskType type = TaskType.fromCode(fields[0])
                .orElseThrow(() -> new MyriadException("unknown task type \"" + fields[0] + "\""));
        boolean isDone = fields[1].equals("1");
        String description = fields[2];

        // No default branch: the compiler checks that every TaskType is handled.
        Task task = switch (type) {
            case TODO -> new ToDo(description);
            case DEADLINE -> {
                if (fields.length < 4) {
                    throw new MyriadException(
                            "a Deadline line needs a 4th field (date), found " + fields.length + " fields");
                }
                yield new Deadline(description, TaskDateTime.parse(fields[3]));
            }
            case EVENT -> {
                if (fields.length < 5) {
                    throw new MyriadException(
                            "an Event line needs 5 fields (type, done, description, start, end), "
                                    + "found " + fields.length);
                }
                TaskDateTime start = TaskDateTime.parse(fields[3]);
                TaskDateTime end = TaskDateTime.parse(fields[4]);
                // A hand-edited file could hold an impossible event; skip it like any bad line.
                Event.checkTimesInOrder(start, end);
                yield new Event(description, start, end);
            }
        };
        task.setDone(isDone);
        return task;
    }

    /**
     * Reads the data file and returns the tasks it holds, one per line, via
     * {@link #parseLine}. Returns an empty result if the data file doesn't
     * exist yet (e.g. first run) rather than treating that as an error. A
     * line that {@code parseLine} rejects is skipped — not fatal, and not
     * silently misinterpreted — and its 1-based line number and reason are
     * collected into the result's {@link LoadResult#skippedLines()} so the
     * caller can show one consolidated warning; every other, valid line
     * still loads normally.
     *
     * Returns the tasks instead of adding them to a {@link TaskList} passed
     * in, so the caller decides what to do with them (see
     * {@code Myriad.loadSavedData}, which either builds a {@code TaskList}
     * from them or falls back to an empty one). Throws
     * {@link MyriadException} if the file exists but can't be opened at all
     * — permission denied, or the path is a directory — which is a real
     * failure worth reporting, unlike the file simply not being there yet.
     *
     * The file is decoded as UTF-8, with any byte that isn't valid UTF-8
     * replaced by the Unicode replacement character rather than stopping
     * the read, so a file saved in another encoding still loads every line
     * it can. Lines are split only on {@code \n}, {@code \r\n} and
     * {@code \r}, which are the only line endings {@link #save} or a text
     * editor would write; a description
     * containing another Unicode line separator stays one line.
     *
     * @return the tasks loaded, plus a description of each skipped line.
     * @throws MyriadException if the file exists but can't be opened at all.
     */
    public LoadResult load() throws MyriadException {
        List<Task> tasks = new ArrayList<>();
        List<String> skippedLines = new ArrayList<>();

        if (!Files.exists(dataFile)) {
            return new LoadResult(tasks, skippedLines);
        }

        String content;
        try {
            content = new String(Files.readAllBytes(dataFile), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new MyriadException(describeReadFailure(e));
        }

        List<String> lines = content.lines().toList();
        for (int i = 0; i < lines.size(); i++) {
            try {
                tasks.add(parseLine(lines.get(i)));
            } catch (MyriadException e) {
                skippedLines.add("line " + (i + 1) + ": " + e.getMessage());
            }
        }
        return new LoadResult(tasks, skippedLines);
    }

    /**
     * Returns a short reason the data file couldn't be read, for the load
     * warning. An {@link AccessDeniedException}'s own message is only the file's
     * path, which on its own doesn't say what went wrong.
     *
     * @param e the failure reported while reading the file.
     * @return the path and, where the exception doesn't already say, why.
     */
    private static String describeReadFailure(IOException e) {
        if (e instanceof AccessDeniedException) {
            return e.getMessage() + " (access denied)";
        }
        return e.getMessage();
    }
}
