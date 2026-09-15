package ekud.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import ekud.EkudException;
import ekud.command.Command;
import ekud.command.TaskCreatingCommand;
import ekud.parser.Parser;
import ekud.task.Task;
import ekud.task.TaskList;

/**
 * Reads and writes the task list on disk.
 * <p>
 * Each line is a done flag ({@code 0} or {@code 1}), a space, then the same
 * add-command text the user would type, for example:
 * <pre>
 * 0 todo borrow book
 * 1 deadline return book /by 2019-12-02
 * 0 event meeting /from 2019-12-02 1400 /to 2019-12-02 1600
 * </pre>
 * Loading reuses {@link Parser} so descriptions may contain {@code |} and
 * stay consistent with the live command grammar. Blank or corrupt lines are
 * skipped so one bad line does not discard the rest of the file.
 */
public class Storage {
    /**
     * Default save location, relative to the working directory.
     */
    public static final String DEFAULT_PATH = "data/ekud.txt";

    private static final String DONE = "1";
    private static final String NOT_DONE = "0";

    private final Path path;

    /**
     * Creates a storage that reads and writes the given file path.
     *
     * @param filePath path to the save file, for example {@code data/ekud.txt}
     */
    public Storage(String filePath) {
        // The default path and tests always pass a real location; a blank path is a caller bug.
        assert filePath != null && !filePath.isBlank() : "Save file path must be provided";
        this.path = Path.of(filePath);
    }

    /**
     * Loads tasks from disk.
     * If the file does not exist yet, an empty list is returned.
     *
     * @return the tasks that could be parsed from the file
     * @throws EkudException if the file exists but cannot be read
     */
    public List<Task> load() throws EkudException {
        if (!Files.exists(path)) {
            return new ArrayList<>();
        }
        List<String> lines;
        try {
            lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new EkudException("Could not read saved tasks from " + path + ".");
        }

        List<Task> tasks = new ArrayList<>();
        for (String line : lines) {
            Task task = parseLine(line);
            if (task != null) {
                tasks.add(task);
            }
        }
        return tasks;
    }

    /**
     * Writes every task in {@code tasks} to disk, replacing the previous file.
     * Creates the parent folder if it does not exist.
     *
     * @param tasks the list to persist
     * @throws EkudException if the file cannot be written
     */
    public void save(TaskList tasks) throws EkudException {
        // Commands only save after a successful change to the in-memory list.
        assert tasks != null : "Cannot save a null task list";
        try {
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            List<String> lines = new ArrayList<>();
            for (Task task : tasks) {
                lines.add(toSaveLine(task));
            }
            Files.write(path, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new EkudException("Could not save tasks to " + path + ".");
        }
    }

    /**
     * Turns a task into one save-file line: done flag plus command text.
     */
    private static String toSaveLine(Task task) {
        String doneFlag = task.isDone() ? DONE : NOT_DONE;
        return doneFlag + " " + task.toCommandString();
    }

    /**
     * Parses one save-file line.
     *
     * @param line a single line from the file
     * @return the reconstructed task, or {@code null} if the line is unusable
     */
    private static Task parseLine(String line) {
        if (line == null || line.isBlank()) {
            return null;
        }

        String trimmed = line.trim();
        String[] parts = trimmed.split("\\s+", 2);
        if (parts.length != 2) {
            return null;
        }

        String doneFlag = parts[0];
        if (!doneFlag.equals(DONE) && !doneFlag.equals(NOT_DONE)) {
            return null;
        }

        try {
            Command command = Parser.parse(parts[1]);
            if (!(command instanceof TaskCreatingCommand creating)) {
                // list, mark, bye, and similar commands are not stored as tasks
                return null;
            }
            Task task = creating.createTask();
            // Add-command parse methods always build a task or throw before returning.
            assert task != null : "A task-creating command should always produce a task";
            if (doneFlag.equals(DONE)) {
                task.markAsDone();
            }
            return task;
        } catch (EkudException e) {
            // A corrupt line is skipped so one bad row does not discard the rest of the file.
            return null;
        }
    }
}
