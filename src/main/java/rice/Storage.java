package rice;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Handles loading tasks from and saving tasks to disk.
 */
public class Storage {
    private final Path filePath;

    /**
     * Creates storage for the file identified by the given path elements.
     *
     * @param pathElements path segments for the storage file
     */
    public Storage(String... pathElements) {
        this.filePath = Paths.get(pathElements[0], Arrays.copyOfRange(pathElements, 1, pathElements.length));
    }

    /**
     * Returns the path used to store task data.
     *
     * @return storage file path
     */
    public Path getPath() {
        return this.filePath;
    }

    /**
     * Saves the current tasks to disk, replacing the previous contents.
     *
     * @param currentTasks tasks to save
     */
    public void save(List<Task> currentTasks) {
        Path parentDir = this.filePath.getParent();
        try {
            if (parentDir != null) {
                Files.createDirectories(parentDir);
            }

            StringBuilder content = new StringBuilder();
            for (Task task : currentTasks) {
                if (!content.isEmpty()) {
                    content.append(System.lineSeparator());
                }
                content.append(taskToListString(task));
            }

            Files.writeString(this.filePath, content.toString(), StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Failed to load: " + e.getMessage());
        }
    }

    /**
     * Converts one task into the delimiter-separated storage format.
     *
     * @param task task to convert
     * @return storage string for the task
     */
    private String taskToListString(Task task) {
        String completion = task.isDone() ? "1" : "0";

        return switch (task) {
            case Todo todo -> "T|" + completion + "|" + todo.getTask();
            case Deadline deadline -> "D|" + completion + "|" + deadline.getTask()
                    + "|" + deadline.getDeadline();
            case Event event -> "E|" + completion + "|" + event.getTask()
                    + "|" + event.getStart() + "|" + event.getEnd();
            case null -> throw new IllegalArgumentException("Cannot save a null task");
            default -> throw new IllegalArgumentException("Unknown task type");
        };
    }

    /**
     * Loads saved task records, or returns an empty list if none exist.
     *
     * @return saved task records
     */
    public List<String> load() {
        try {
            if (Files.exists(this.filePath)) {
                List<String> previousSessionList = Files.readAllLines(this.filePath);
                System.out.println(String.join("\n", previousSessionList));
                return previousSessionList;
            } else {
                System.out.println("You have no tasks to display !!");
            }
        } catch (IOException e) {
            System.err.println("Failed to load checklist: " + e.getMessage());
        }
        return new ArrayList<>();
    }
}
