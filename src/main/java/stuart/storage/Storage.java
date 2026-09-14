package stuart.storage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import stuart.exception.StuartException;
import stuart.task.Deadlines;
import stuart.task.Events;
import stuart.task.Task;
import stuart.task.ToDos;
import stuart.ui.Ui;

/**
 * Handles loading tasks from, and saving tasks to, a file on disk.
 */
public class Storage {
    private final String filePath;

    /**
     * Creates a new {@code Storage} that reads/writes the save file at {@code filePath}.
     *
     * @param filePath the path to the save file
     */
    public Storage(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads the task list from the save file. Returns an empty list if the
     * file does not exist yet (e.g. first run). A line that cannot be parsed
     * is skipped with a warning reported via {@code ui}, rather than
     * aborting the whole load, so one corrupted line doesn't cost every
     * other saved task.
     *
     * @param ui where to report a skipped, corrupted line
     * @return the loaded tasks, in the order they appear in the file
     * @throws StuartException if the file exists but cannot be read at all
     */
    public ArrayList<Task> load(Ui ui) throws StuartException {
        ArrayList<Task> items = new ArrayList<>();
        File dataFile = new File(filePath);
        if (!dataFile.exists()) {
            return items;
        }
        try (Scanner fileScanner = new Scanner(dataFile)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }
                try {
                    items.add(parseSavedTask(line));
                } catch (StuartException e) {
                    ui.reply("Warning: skipping a corrupted saved task (" + e.getMessage() + ").");
                }
            }
        } catch (IOException e) {
            throw new StuartException("could not load saved tasks (" + e.getMessage() + ")");
        }
        return items;
    }

    /**
     * Parses one line of the save file into a {@link Task}.
     *
     * @param line one line from the save file, e.g. {@code "T | 1 | read book"}
     * @return the task the line describes
     * @throws StuartException if {@code line} is not validly formatted
     */
    private static Task parseSavedTask(String line) throws StuartException {
        assert line.equals(line.trim()) : "load() should have already trimmed line before calling this";
        assert !line.isEmpty() : "load() should have already skipped blank lines before calling this";
        String[] parts = line.split(Pattern.quote(Task.SAVE_DELIMITER));

        if (parts.length < 3) {
            throw new StuartException("expected at least 3 fields: \"" + line + "\"");
        }
        String type = parts[0].trim();
        boolean isDone = parts[1].trim().equals("1");
        String description = parts[2].trim();

        Task task;
        if (type.equals("T")) {
            task = new ToDos(description);
        } else if (type.equals("D")) {
            if (parts.length < 4) {
                throw new StuartException("deadline is missing its \"by\" field: \"" + line + "\"");
            }
            task = new Deadlines(description, parseDate(parts[3].trim()));
        } else if (type.equals("E")) {
            if (parts.length < 5) {
                throw new StuartException("event is missing its \"from\"/\"to\" fields: \"" + line + "\"");
            }
            task = new Events(description, parseDate(parts[3].trim()), parseDate(parts[4].trim()));
        } else {
            throw new StuartException("unknown task type \"" + type + "\": \"" + line + "\"");
        }

        if (isDone) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Parses a date in {@code yyyy-MM-dd} format, e.g. {@code "2019-10-15"}.
     * Only used for dates coming from the save file; user-typed dates are
     * parsed separately, since a corrupted save-file date should be skipped
     * along with its line rather than reported like a typo in a command.
     *
     * @param text the date text to parse
     * @return the parsed date
     * @throws StuartException if {@code text} is not a valid {@code yyyy-MM-dd} date
     */
    private static LocalDate parseDate(String text) throws StuartException {
        try {
            return LocalDate.parse(text);
        } catch (DateTimeParseException e) {
            throw new StuartException("\"" + text + "\" is not a valid date");
        }
    }

    /**
     * Overwrites the save file with the current task list, creating the
     * containing directory if needed. If the write fails, reports it via
     * {@code ui} but leaves the in-memory task list untouched.
     *
     * @param items the list of stored tasks
     * @param ui where to report a save failure
     */
    public void save(List<Task> items, Ui ui) {
        File dataFile = new File(filePath);
        File parentDir = dataFile.getParentFile();
        if (parentDir != null) {
            parentDir.mkdirs();
        }
        try (FileWriter writer = new FileWriter(dataFile)) {
            for (Task task : items) {
                writer.write(task.toSaveFormat() + System.lineSeparator());
            }
        } catch (IOException e) {
            ui.reply("Warning: could not save tasks to disk (" + e.getMessage() + ").");
        }
    }
}
