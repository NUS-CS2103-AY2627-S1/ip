package erina;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import erina.task.Deadline;
import erina.task.Event;
import erina.task.Task;
import erina.task.Todo;

/**
 * Reads and writes the task list on the hard disk, so tasks survive between
 * runs of the program.
 *
 * <p>Each task is one line in the file, in the form produced by
 * {@link Task#toSaveString()}, for example:
 * <pre>
 * T | 1 | read book
 * D | 0 | return book | 2019-10-15
 * E | 0 | project meeting | Mon 2pm | 4pm
 * </pre>
 */
public class Storage {
    /** Where the task list is kept between runs. */
    private final Path filePath;

    /**
     * Creates a storage that reads and writes the given file.
     *
     * @param filePath where to keep the task list between runs
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Reads the task list saved by a previous run.
     *
     * @return the saved tasks, or an empty list if nothing has been saved yet
     * @throws ErinaException if the file exists but cannot be read or does
     *                        not make sense
     */
    public List<Task> load() throws ErinaException {
        List<Task> tasks = new ArrayList<>();

        // No file simply means nothing has been saved yet, so it is a normal
        // first run rather than an error.
        if (!Files.exists(filePath)) {
            return tasks;
        }

        List<String> lines;
        try {
            lines = Files.readAllLines(filePath);
        } catch (IOException e) {
            // Covers a file that is locked, not readable by this user, or
            // actually a folder: all mean the list cannot be loaded.
            throw new ErinaException("Pardon me. I could not read the save file "
                    + filePath + ".");
        }

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isBlank()) {
                continue;
            }
            try {
                tasks.add(parseTask(line));
            } catch (ErinaException | DateTimeParseException e) {
                // Report the line number as people count them, from 1.
                throw new ErinaException("Pardon me. Line " + (i + 1)
                        + " of the save file " + filePath + " is not a task I understand:"
                        + " \"" + line + "\"");
            }
        }
        return tasks;
    }

    /**
     * Writes the whole task list, replacing whatever was saved before.
     *
     * <p>Called after every change, so the file always matches the list and
     * nothing is lost if the program is closed abruptly.
     *
     * @param tasks the tasks to save
     * @throws ErinaException if the file cannot be written
     */
    public void save(List<Task> tasks) throws ErinaException {
        assert tasks != null : "nothing to save: task list is null";

        // One line per task, in list order.
        List<String> lines = tasks.stream()
                .map(Task::toSaveString)
                .toList();

        try {
            // The folder does not exist until the first save on a new machine.
            // A bare file name has no parent folder to create.
            Path folder = filePath.getParent();
            if (folder != null) {
                Files.createDirectories(folder);
            }
            Files.write(filePath, lines);
        } catch (IOException e) {
            throw new ErinaException("Pardon me. I could not write the save file "
                    + filePath + ". Your latest change will be lost when I close.");
        }
    }

    /**
     * Copies the save file to a backup next to it, named with a {@code .bak}
     * ending.
     *
     * <p>Used when the file cannot be understood: the next change would
     * otherwise overwrite it, destroying tasks the user might still recover
     * by fixing the file by hand.
     *
     * @return where the backup was written
     * @throws ErinaException if the copy could not be made
     */
    public Path backUp() throws ErinaException {
        Path backup = filePath.resolveSibling(filePath.getFileName() + ".bak");
        try {
            Files.copy(filePath, backup, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new ErinaException("I could not make a backup of it either.");
        }
        return backup;
    }

    /**
     * Rebuilds one task from one saved line.
     *
     * @param line the line as written by {@link Task#toSaveString()}
     * @return the task that line describes
     * @throws ErinaException if the line does not describe a task
     */
    private Task parseTask(String line) throws ErinaException {
        // The separator contains |, which is a special character in regular
        // expressions, so split on its literal quoted form. The limit of -1
        // keeps empty trailing fields, so a missing value is noticed.
        String[] fields = line.split(Pattern.quote(Task.SAVE_SEPARATOR), -1);

        Task task;
        switch (fields[0]) {
            case "T":
                checkFields(fields, 3);
                task = new Todo(fields[2]);
                break;
            case "D":
                checkFields(fields, 4);
                task = new Deadline(fields[2], LocalDate.parse(fields[3]));
                break;
            case "E":
                checkFields(fields, 5);
                task = new Event(fields[2], fields[3], fields[4]);
                break;
            default:
                throw new ErinaException("Unknown task type: " + fields[0]);
        }

        // Every branch of the switch either assigns a task or throws.
        assert task != null : "parseTask produced no task for: " + line;

        if (fields[1].equals("1")) {
            task.markAsDone();
        } else if (!fields[1].equals("0")) {
            throw new ErinaException("Unknown done flag: " + fields[1]);
        }
        return task;
    }

    /**
     * Checks that a saved line has exactly the fields its kind of task needs,
     * none of them blank.
     *
     * <p>Too few fields would crash when read; too many, or blank ones, mean
     * the line was edited by hand and would load as the wrong task.
     *
     * @param fields   the fields of one saved line
     * @param expected how many fields this kind of task has
     * @throws ErinaException if the count is wrong or a field is blank
     */
    private static void checkFields(String[] fields, int expected) throws ErinaException {
        if (fields.length != expected) {
            throw new ErinaException("Expected " + expected + " fields but found " + fields.length);
        }
        for (String field : fields) {
            if (field.isBlank()) {
                throw new ErinaException("A field is empty");
            }
        }
    }
}
