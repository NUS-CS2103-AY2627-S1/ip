package summer.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import summer.task.Deadline;
import summer.task.Event;
import summer.task.Task;
import summer.task.TaskList;
import summer.task.ToDo;

/**
 * Loads tasks from, and saves tasks to, a save file on disk so that
 * a task list survives between runs of the chatbot.
 */
public class Storage {
    private final Path filePath;
    private int skippedLineCount;

    /**
     * Creates a storage backed by the given file path.
     *
     * @param filePath path to the save file, relative to the project root
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads tasks from the save file.
     * Returns an empty list if the file does not exist yet,
     * and skips any line that is corrupted rather than failing outright.
     * The number of skipped lines is recorded and can be read via
     * {@link #getSkippedLineCount()}.
     *
     * @return tasks read from disk, in file order
     */
    public List<Task> load() {
        this.skippedLineCount = 0;
        if (!Files.exists(this.filePath)) {
            return new ArrayList<>();
        }

        try {
            List<String> lines = Files.readAllLines(this.filePath);
            List<Task> tasks = new ArrayList<>();
            for (String line : lines) {
                Task task = parseTask(line);
                if (task == null) {
                    this.skippedLineCount++;
                } else {
                    tasks.add(task);
                }
            }
            return tasks;
        } catch (IOException e) {
            System.out.println(" Could not read saved tasks: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Returns how many lines the most recent {@link #load()} call skipped
     * because they were corrupted or malformed.
     *
     * @return the number of corrupted lines skipped during the last load
     */
    public int getSkippedLineCount() {
        return this.skippedLineCount;
    }

    /**
     * Writes all tasks in the given list to the save file, creating the
     * parent folder first if it does not exist yet.
     *
     * @param tasks tasks to save
     */
    public void save(TaskList tasks) {
        try {
            Path parent = this.filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            List<String> lines = new ArrayList<>();
            for (int i = 0; i < tasks.size(); i++) {
                lines.add(tasks.get(i).toSaveFormat());
            }
            Files.write(this.filePath, lines);
        } catch (IOException e) {
            System.out.println(" Could not save tasks: " + e.getMessage());
        }
    }

    /**
     * Parses one save-file line into a task.
     *
     * @param line line read from the save file
     * @return the parsed task, or {@code null} if the line is corrupted
     */
    private Task parseTask(String line) {
        String[] parts = line.split(" \\| ");
        try {
            boolean isDone = parts[1].equals("1");
            String description = parts[2];

            switch (parts[0]) {
                case "T":
                    return new ToDo(description, isDone);
                case "D":
                    return new Deadline(description, LocalDate.parse(parts[3]), isDone);
                case "E":
                    return new Event(description, LocalDate.parse(parts[3]), LocalDate.parse(parts[4]), isDone);
                default:
                    return null;
            }
        } catch (ArrayIndexOutOfBoundsException | DateTimeParseException e) {
            return null;
        }
    }
}
