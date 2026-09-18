package bot.storage;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import bot.exception.BotException;
import bot.parser.TaskDateTime;
import bot.task.Deadline;
import bot.task.Event;
import bot.task.Task;
import bot.task.Todo;

/**
 * Saves the task list to, and loads it from, a file on disk so tasks
 * survive between runs of the program. Which file is used is given once,
 * as a relative path, when a Storage is constructed - e.g. {@code new
 * Storage("data/bot.txt")} - rather than being hard-coded inside this
 * class, so a caller (or a test) can point it at a different file.
 *
 * A relative path is built with {@link Paths#get}, so it's resolved
 * against wherever the program is run from and uses the right separator
 * on any operating system, rather than a hard-coded absolute path such
 * as {@code C:\data} that would only work on one computer.
 */
public class Storage {
    private final Path dataFile;

    /**
     * Creates a Storage backed by the data file at the given path.
     *
     * @param filePath The relative path to the data file, e.g. {@code "data/bot.txt"}.
     */
    public Storage(String filePath) {
        this.dataFile = Paths.get(filePath);
    }

    /**
     * Writes every task to the data file, one per line, overwriting
     * whatever was there before. Creates the data file's parent directory
     * first if it does not already exist.
     */
    public void save(List<Task> tasks) throws BotException {
        try {
            Path parent = dataFile.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(dataFile))) {
                for (Task task : tasks) {
                    writer.println(task.toSaveFormat());
                }
                if (writer.checkError()) {
                    throw new IOException("the data file could not be written completely");
                }
            }
        } catch (IOException e) {
            throw new BotException("OOPS!!! I couldn't save your tasks to disk: " + e.getMessage());
        }
    }

    /**
     * Loads the task list from the data file. If the file (or its
     * containing folder) doesn't exist yet, returns an empty list instead
     * of failing, since that's the normal state the first time the
     * program runs on a new machine.
     *
     * <p>The file may have been hand-edited or corrupted since it was last
     * written, so each line is parsed independently: a malformed line is
     * skipped rather than aborting the whole load, and a human-readable
     * description of what was wrong with it (including its 1-based line
     * number) is appended to {@code warnings} so the caller can tell the
     * user. Well-formed lines are unaffected by a bad line elsewhere in
     * the file.
     *
     * @throws BotException if the file exists but can't be read at all,
     *         e.g. due to a permissions problem
     */
    public List<Task> load(List<String> warnings) throws BotException {
        List<Task> tasks = new ArrayList<>();
        if (!Files.exists(dataFile)) {
            return tasks;
        }
        List<String> lines;
        try {
            lines = Files.readAllLines(dataFile);
        } catch (IOException e) {
            throw new BotException("OOPS!!! I couldn't read your saved tasks: " + e.getMessage());
        }
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isBlank()) {
                continue;
            }
            try {
                tasks.add(parseLine(line));
            } catch (BotException e) {
                warnings.add("line " + (i + 1) + ": " + e.getMessage());
            }
        }
        return tasks;
    }

    /**
     * Parses one data-file line back into the Task it represents, e.g.
     * {@code "D | 0 | return book | 2019-10-15"} into an undone Deadline.
     * Rejects anything that doesn't match the expected
     * {@code type | done-flag | description [| ...]} shape instead of
     * guessing, so a corrupted line is reported rather than silently
     * misread.
     */
    private Task parseLine(String line) throws BotException {
        String[] parts = line.split(" \\| ");
        if (parts.length < 3) {
            throw new BotException(
                    "expected at least 3 fields separated by \" | \" (type, done flag, description), found "
                            + parts.length);
        }
        String type = parts[0];
        String doneFlag = parts[1];
        String description = parts[2];
        if (!doneFlag.equals("0") && !doneFlag.equals("1")) {
            throw new BotException("done flag must be \"0\" or \"1\", found \"" + doneFlag + "\"");
        }
        boolean isDone = doneFlag.equals("1");

        Task task;
        switch (type) {
            case "T":
                task = new Todo(description);
                break;
            case "D":
                if (parts.length < 4) {
                    throw new BotException("a deadline (\"D\") line needs a 4th field for its \"by\" date/time");
                }
                task = new Deadline(description, TaskDateTime.parse(parts[3]));
                break;
            case "E":
                if (parts.length < 5) {
                    throw new BotException(
                            "an event (\"E\") line needs 4th and 5th fields for its \"from\" and \"to\" date/time");
                }
                task = new Event(description, TaskDateTime.parse(parts[3]), TaskDateTime.parse(parts[4]));
                break;
            default:
                throw new BotException("unknown task type \"" + type + "\" (expected \"T\", \"D\", or \"E\")");
        }
        // Every branch of the switch above either assigns task or throws,
        // so task is never null here - this documents that the switch is
        // exhaustive for the three known type letters, rather than
        // silently relying on the compiler's definite-assignment check.
        assert task != null : "task should have been assigned or an exception thrown by the switch above";
        if (isDone) {
            task.markAsDone();
        }
        return task;
    }
}
