package leo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles loading tasks from, and saving tasks to, the save file on
 * disk (data/leo.txt, relative to the project root).
 */
public class Storage {
    private static final Path FILE_PATH =
            Paths.get("data", "leo.txt");

    /**
     * Loads previously-saved tasks from the save file, creating an
     * empty save file first if one does not already exist.
     *
     * <p>A-MoreErrorHandling: this used to populate a caller-supplied
     * fixed-size array (an {@code int loadTasks(Task[] tasks)} that
     * wrote into "tasks" and returned how many entries it filled in).
     * Leo's constructor was the only caller, and it allocated that
     * array as {@code new Task[100]} - so a save file with more than
     * 100 non-blank lines (easy to reach after weeks of use, since
     * TaskList itself has never had a capacity limit - see
     * TaskListTest#canHoldMoreThanOneHundredTasks) would silently
     * overrun it. There was even an assertion documenting exactly this
     * risk one commit ago (A-Assertions), but assertions are disabled
     * by default (no {@code -ea}), so in a normal run it did nothing
     * and the real failure was an unguarded
     * {@code ArrayIndexOutOfBoundsException} that crashed Leo on
     * startup with no explanation. Returning a {@link List} sized to
     * exactly what was loaded removes the capacity mismatch (and the
     * assumption it depended on) entirely, rather than just raising the
     * limit or re-adding a check for it.
     *
     * @return the tasks loaded from the save file, in file order
     *     (empty if the save file has none)
     * @throws IOException if the save file cannot be read, or contains
     *     a line that cannot be parsed as a task
     */
    public static List<Task> loadTasks() throws IOException {
        createDataFileIfMissing();

        List<String> lines = Files.readAllLines(FILE_PATH);
        List<Task> loadedTasks = new ArrayList<>();

        for (String line : lines) {
            if (line.trim().isEmpty()) {
                continue;
            }

            loadedTasks.add(parseTask(line));
        }

        return loadedTasks;
    }

    /**
     * Saves the first {@code itemCount} tasks in the given array to the
     * save file, overwriting its previous contents.
     *
     * @param tasks array containing the tasks to save
     * @param itemCount number of tasks to save, starting at index 0
     * @throws IOException if the save file cannot be written to
     */
    public static void saveTasks(Task[] tasks, int itemCount)
            throws IOException {
        createDataFileIfMissing();

        List<String> lines = new ArrayList<>();

        for (int i = 0; i < itemCount; i++) {
            lines.add(tasks[i].toFileString());
        }

        Files.write(FILE_PATH, lines);
    }

    // Creates the save file (and its parent data/ directory) if either
    // does not already exist, so loadTasks/saveTasks never have to
    // handle a missing file themselves.
    private static void createDataFileIfMissing() throws IOException {
        Path parentDirectory = FILE_PATH.getParent();

        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }

        if (!Files.exists(FILE_PATH)) {
            Files.createFile(FILE_PATH);
        }
    }

    // Parses one save-file line (e.g. "D | 0 | return book | ...")
    // back into the matching Task subclass.
    private static Task parseTask(String line) throws IOException {
        String[] parts = line.split(" \\| ", -1);

        if (parts.length < 3) {
            throw new IOException("Invalid task data: " + line);
        }

        String type = parts[0];
        boolean isDone = parts[1].equals("1");
        Task task;

        switch (type) {
            case "T":
                task = new Todo(parts[2]);
                break;

            case "D":
                if (parts.length < 4) {
                    throw new IOException("Invalid deadline data: " + line);
                }
                task = new Deadline(parts[2], parts[3]);
                break;

            case "E":
                if (parts.length < 5) {
                    throw new IOException("Invalid event data: " + line);
                }
                task = new Event(parts[2], parts[3], parts[4]);
                break;

            default:
                throw new IOException("Unknown task type: " + type);
        }

        if (isDone) {
            task.markAsDone();
        }

        return task;
    }
}
