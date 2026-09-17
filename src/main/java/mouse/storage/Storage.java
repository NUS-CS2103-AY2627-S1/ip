package mouse.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mouse.MouseException;
import mouse.task.Deadline;
import mouse.task.Event;
import mouse.task.Priority;
import mouse.task.Task;
import mouse.task.TaskList;
import mouse.task.ToDo;

/**
 * Loads and saves tasks to a text file.
 */
public class Storage {
    private static final String FIELD_SEPARATOR = " \\| ";

    private final Path filePath;
    private String loadWarning;

    /**
     * Creates storage for the given file path.
     *
     * @param filePath Path to the save file.
     */
    public Storage(String filePath) {
        this.filePath = Path.of(filePath);
    }

    /**
     * Returns a warning from the last {@link #load()}, or {@code null} if none.
     *
     * @return Load warning for the UI, if any.
     */
    public String getLoadWarning() {
        return loadWarning;
    }

    /**
     * Returns tasks from disk.
     * A missing file yields an empty list. Invalid lines are skipped.
     *
     * @return Loaded tasks.
     */
    public TaskList load() {
        loadWarning = null;
        if (!Files.exists(filePath)) {
            return new TaskList();
        }
        if (!Files.isReadable(filePath)) {
            return emptyWithUnreadableWarning();
        }
        try {
            return loadExistingFile();
        } catch (IOException exception) {
            return emptyWithUnreadableWarning();
        }
    }

    /**
     * Returns an empty list and records that the stash file could not be read.
     *
     * @return Empty task list.
     */
    private TaskList emptyWithUnreadableWarning() {
        loadWarning = "Could not read the stash file. Starting empty.";
        return new TaskList();
    }

    /**
     * Reads an existing stash file, skipping invalid lines.
     *
     * @return Loaded tasks.
     * @throws IOException If the file cannot be read.
     */
    private TaskList loadExistingFile() throws IOException {
        List<Task> loaded = new ArrayList<>();
        int skipped = 0;
        for (String line : Files.readAllLines(filePath, StandardCharsets.UTF_8)) {
            skipped += addDecodedLine(loaded, line);
        }
        if (skipped > 0) {
            String lineWord = skipped == 1 ? "line" : "lines";
            loadWarning = "Skipped " + skipped + " mouldy " + lineWord + " in the stash file.";
        }
        return new TaskList(loaded);
    }

    /**
     * Decodes {@code line} into {@code loaded}, or counts it as skipped.
     *
     * @param loaded Tasks collected so far.
     * @param line One save-file line.
     * @return {@code 1} if a non-blank line was skipped, otherwise {@code 0}.
     */
    private static int addDecodedLine(List<Task> loaded, String line) {
        Task task = decode(line);
        if (task != null) {
            loaded.add(task);
            return 0;
        }
        return line.isBlank() ? 0 : 1;
    }

    /**
     * Writes {@code tasks} to disk, creating parent folders if needed.
     *
     * @param tasks Tasks to save.
     * @throws MouseException If the file cannot be written.
     */
    public void save(TaskList tasks) throws MouseException {
        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            List<String> lines = new ArrayList<>();
            for (Task task : tasks.asList()) {
                lines.add(task.encode());
            }
            Files.write(filePath, lines, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new MouseException("Could not save crumbs to " + filePath + " GRR");
        }
    }

    /**
     * Returns a task decoded from one save-file line, or {@code null} if invalid.
     *
     * @param line Encoded task line.
     * @return Decoded task, or {@code null} to skip the line.
     */
    private static Task decode(String line) {
        if (line == null || line.isBlank()) {
            return null;
        }
        String[] parts = line.split(FIELD_SEPARATOR, -1);
        if (parts.length < 4) {
            return null;
        }
        try {
            return decorate(decodeTask(parts[0], parts), parts[1], parts[2]);
        } catch (MouseException | RuntimeException exception) {
            return null;
        }
    }

    /**
     * Applies done status and priority to a decoded task.
     *
     * @param task Decoded task, or {@code null}.
     * @param doneFlag {@code 1} if done.
     * @param priorityRaw Saved priority word.
     * @return {@code task} with flags applied, or {@code null}.
     */
    private static Task decorate(Task task, String doneFlag, String priorityRaw) {
        if (task == null) {
            return null;
        }
        if ("1".equals(doneFlag)) {
            task.markAsDone();
        }
        try {
            task.setPriority(Priority.fromString(priorityRaw));
        } catch (MouseException exception) {
            task.setPriority(Priority.NONE);
        }
        return task;
    }

    /**
     * Returns a task of {@code type} built from the split save-file fields.
     *
     * @param type Task type letter.
     * @param parts Fields split on {@code |}.
     * @return Decoded task, or {@code null} if the type or field count is wrong.
     * @throws MouseException If a dated field is invalid.
     */
    private static Task decodeTask(String type, String[] parts) throws MouseException {
        switch (type) {
        case "T":
            return new ToDo(join(parts, 3, parts.length));
        case "D":
            if (parts.length < 5) {
                return null;
            }
            return new Deadline(join(parts, 3, parts.length - 1), parts[parts.length - 1]);
        case "E":
            if (parts.length < 6) {
                return null;
            }
            return new Event(join(parts, 3, parts.length - 2),
                    parts[parts.length - 2], parts[parts.length - 1]);
        default:
            return null;
        }
    }

    /**
     * Joins {@code parts[from]} through {@code parts[to - 1]} with {@code |}.
     *
     * @param parts Split save-file fields.
     * @param from Start index, inclusive.
     * @param to End index, exclusive.
     * @return Joined description text.
     */
    private static String join(String[] parts, int from, int to) {
        return String.join(" | ", Arrays.copyOfRange(parts, from, to));
    }
}
