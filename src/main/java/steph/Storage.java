package steph;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import steph.task.Deadline;
import steph.task.Event;
import steph.task.Task;
import steph.task.ToDo;

/**
 * Reads the task list from a save file and writes it back after changes.
 * The file holds one task per line in the format produced by
 * {@link steph.task.Task#toFileFormat()}, e.g.
 * {@code "D | 1 | read book | 2019-10-15T18:00"}.
 */
public class Storage {
    private final Path path;

    /**
     * Constructs a Storage that reads from and writes to the given path.
     *
     * @param filePath Location of the save file, e.g. {@code "./data/steph.txt"}.
     */
    public Storage(String filePath) {
        this.path = Paths.get(filePath);
    }

    /**
     * Returns all tasks read from the save file, in the order they appear.
     * An absent file is treated as an empty list, not an error. A line that
     * cannot be parsed is skipped, with a note on standard output, rather
     * than failing the whole load.
     *
     * @return The tasks reconstructed from the file.
     * @throws IOException If the file exists but cannot be read.
     */
    public ArrayList<Task> load() throws IOException {
        if (!Files.exists(this.path)) {
            return new ArrayList<>();
        }
        ArrayList<Task> tasks = new ArrayList<>();

        for (String line : Files.readAllLines(this.path)) {
            try {
                tasks.add(parseTask(line));
            } catch (StephException e) {
                System.out.println("Skipping corrupted line: " + line);
            }
        }
        return tasks;
    }

    /**
     * Writes list of tasks to the file at the filepath.
     * If there is no file at that filepath, it creates a new file and writes to it.
     * Called after every change and overwrites the whole file.
     *
     * @param tasks The tasks to write, in order.
     * @throws IOException If an I/O error occurs.
     */
    public void save(List<Task> tasks) throws IOException {
        Files.createDirectories(this.path.getParent());
        ArrayList<String> lines = new ArrayList<>();
        for (Task task : tasks) {
            lines.add(task.toFileFormat());
        }
        Files.write(this.path, lines);
    }

    /**
     * Parses one save-file line into the matching Task subclass.
     * Assumes task descriptions contain no '|' characters.
     *
     * @param line A line in the format "T | 1 | name" (D and E add ISO
     *             date-times: "D | 1 | name | 2019-10-15T18:00").
     * @return The reconstructed task, marked done when the status flag is "1".
     * @throws StephException If the line has an unknown type, is missing fields,
     *                        or holds an unparseable date.
     */
    private Task parseTask(String line) throws StephException {
        String[] parts = line.split(Pattern.quote(Task.FIELD_SEPARATOR));
        assert parts.length >= 1 : "String.split always returns at least one element, even for an empty line";

        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].trim();
        }

        try {
            Task task = switch (parts[0]) {
                case "T" -> new ToDo(parts[2]);
                case "D" -> new Deadline(parts[2], DateTimes.parseStored(parts[3]));
                case "E" -> new Event(parts[2], DateTimes.parseStored(parts[3]), DateTimes.parseStored(parts[4]));
                default -> throw new StephException("Unknown task type in line: " + line);
            };

            if (parts[1].equals("1")) {
                task.complete();
            }

            return task;
        } catch (ArrayIndexOutOfBoundsException | DateTimeParseException e) {
            throw new StephException("Line is malformed: " + line);
        }
    }
}
