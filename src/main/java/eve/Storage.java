package eve;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import eve.task.Deadline;
import eve.task.Event;
import eve.task.Task;
import eve.task.ToDo;

/**
 * Reads and writes the task list to a fixed file on disk so tasks persist
 * between runs of the program. Each instance is tied to one file path,
 * given to the constructor.
 */
public class Storage {
    private final Path filePath;

    /**
     * Creates a storage bound to the given file.
     *
     * @param filePath path to the file tasks are loaded from and saved to,
     *      e.g. {@code "data/eve.txt"}.
     */
    public Storage(String filePath) {
        this.filePath = Paths.get(filePath);
    }

    /**
     * Loads tasks from disk. If the file doesn't exist yet (e.g. first
     * run), returns an empty list instead of failing. Lines that don't
     * match the expected format are skipped with a warning rather than
     * crashing the program.
     *
     * @return the tasks read from disk, in file order.
     * @throws EveException if the file exists but could not be read.
     */
    public List<Task> load() throws EveException {
        if (!Files.exists(filePath)) {
            return List.of();
        }
        try {
            return Files.readAllLines(filePath).stream()
                    .map(Storage::parseLine)
                    .filter(Objects::nonNull)
                    .toList();
        } catch (IOException e) {
            throw new EveException("Warning: could not read saved tasks (" + e.getMessage() + ").");
        }
    }

    /**
     * Parses one line of the save file into a task. A line that doesn't
     * match the expected format is reported with a warning and skipped
     * (returning null) rather than crashing the program.
     *
     * @param line one line read from the save file.
     * @return the parsed task, or null if the line is corrupted.
     */
    private static Task parseLine(String line) {
        try {
            String[] parts = line.split(" \\| ");
            if (!parts[1].equals("0") && !parts[1].equals("1")) {
                System.out.println("Warning: skipping corrupted line in data file: " + line);
                return null;
            }
            boolean isDone = parts[1].equals("1");
            String description = parts[2];
            Task task;
            switch (parts[0]) {
                case "T":
                    task = new ToDo(description);
                    break;
                case "D":
                    task = new Deadline(description, LocalDate.parse(parts[3]));
                    break;
                case "E":
                    task = new Event(description, LocalDate.parse(parts[3]), LocalDate.parse(parts[4]));
                    break;
                default:
                    System.out.println("Warning: skipping corrupted line in data file: " + line);
                    return null;
            }
            if (isDone) {
                task.markAsDone();
            }
            return task;
        } catch (RuntimeException e) {
            System.out.println("Warning: skipping corrupted line in data file: " + line);
            return null;
        }
    }

    /**
     * Saves the given tasks to disk, creating the parent folder if needed.
     *
     * @param tasks the tasks currently in the list.
     */
    public void save(List<Task> tasks) {
        try {
            Files.createDirectories(filePath.getParent());
            List<String> lines = tasks.stream()
                    .map(Task::toSaveFormat)
                    .toList();
            Files.write(filePath, lines);
        } catch (IOException e) {
            System.out.println("Warning: could not save tasks (" + e.getMessage() + ").");
        }
    }
}
