package percy;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Loads tasks from the save file at startup and writes them back whenever the
 * list changes, so tasks persist between runs.
 *
 * <p>Each line is stored as {@code <type> | <done flag> | <description>} with
 * type-specific fields (dates) appended after the description.
 */
public class Storage {
    /** Value in the done-flag field that marks a task as completed. */
    private static final String DONE_FLAG = "1";

    private final String filePath;

    /**
     * Creates a storage handler for the given save-file path.
     *
     * @param filePath path to the file tasks are read from and written to
     */
    public Storage(String filePath) {
        assert filePath != null : "storage file path should not be null";
        this.filePath = filePath;
    }

    /**
     * Loads all tasks from the save file.
     *
     * @return the saved tasks, or an empty list if the file does not exist yet.
     *     Lines that are corrupted or unrecognised are skipped rather than
     *     aborting the load.
     */
    public ArrayList<Task> load() {
        File file = new File(filePath);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try {
            return Files.readAllLines(file.toPath()).stream()
                    .map(this::parseLine)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toCollection(ArrayList::new));
        } catch (IOException e) {
            System.out.println("OOPS!!! Could not load saved tasks.");
            return new ArrayList<>();
        }
    }

    /**
     * Parses one line of the save file into a {@link Task}.
     *
     * @param line a single line in {@code <type> | <0/1> | <description> ...} form
     * @return the reconstructed task, or {@code null} if the line is corrupted
     *     or its type is unrecognised, so the caller can safely skip it
     */
    private Task parseLine(String line) {
        try {
            String[] fields = line.split(Pattern.quote(Task.FILE_SEPARATOR));
            String type = fields[0];
            boolean isDone = fields[1].equals(DONE_FLAG);
            String description = fields[2];

            Task task = switch (type) {
            case "T" -> new Todo(description);
            case "D" -> new Deadline(description, LocalDate.parse(fields[3]));
            case "E" -> new Event(description, LocalDate.parse(fields[3]), LocalDate.parse(fields[4]));
            default -> null;
            };
            if (task == null) {
                return null;
            }

            task.setDone(isDone);
            return task;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Writes the whole task list to the save file, overwriting its previous
     * contents. The parent folder is created first if it does not exist.
     *
     * @param tasks the task list to persist
     */
    public void save(TaskList tasks) {
        assert tasks != null : "task list to save should not be null";
        File file = new File(filePath);
        File parentDir = file.getParentFile();

        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        String content = tasks.stream()
                .map(Task::toFileFormat)
                .map(line -> line + System.lineSeparator())
                .collect(Collectors.joining());

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        } catch (IOException e) {
            System.out.println("OOPS!!! Could not save tasks.");
        }
    }
}
