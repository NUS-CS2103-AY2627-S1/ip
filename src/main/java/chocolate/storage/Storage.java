package chocolate.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import chocolate.task.Deadline;
import chocolate.task.Event;
import chocolate.task.Task;
import chocolate.task.TaskList;
import chocolate.task.Todo;

/**
 * Saves tasks to disk and restores them when Chocolate starts.
 */
public class Storage {
    private final Path filePath;
    private final Path archiveFilePath;

    /**
     * Creates storage that reads from and writes to the specified file.
     *
     * @param filePath Path of the task data file.
     */
    public Storage(Path filePath) {
        this(filePath, filePath.resolveSibling("archive.txt"));
    }

    /**
     * Creates storage with separate active-task and archive files.
     *
     * @param filePath Path of the active task data file.
     * @param archiveFilePath Path of the archived task data file.
     */
    public Storage(Path filePath, Path archiveFilePath) {
        this.filePath = filePath;
        this.archiveFilePath = archiveFilePath;
    }

    /**
     * Loads every valid task from the data file.
     * A missing file represents a new user with an empty task list.
     *
     * @return Tasks reconstructed from valid file records.
     * @throws IOException If the existing data file cannot be read.
     */
    public ArrayList<Task> load() throws IOException {
        return loadTasks(filePath);
    }

    /**
     * Loads every valid archived task from the archive file.
     *
     * @return Archived tasks reconstructed from valid file records.
     * @throws IOException If the existing archive file cannot be read.
     */
    public ArrayList<Task> loadArchived() throws IOException {
        return loadTasks(archiveFilePath);
    }

    /**
     * Loads every valid task from a storage file.
     *
     * @param sourceFile Path of the file to load.
     * @return Tasks reconstructed from valid file records.
     * @throws IOException If the existing file cannot be read.
     */
    private ArrayList<Task> loadTasks(Path sourceFile) throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();
        if (!Files.exists(sourceFile)) {
            return tasks;
        }

        for (String line : Files.readAllLines(sourceFile, StandardCharsets.UTF_8)) {
            Task task = parseTask(line);
            if (task != null) {
                tasks.add(task);
            }
        }
        return tasks;
    }

    /**
     * Writes the current task list, creating its parent folder when necessary.
     *
     * @param tasks Current task list.
     * @throws IOException If the folder or data file cannot be written.
     */
    public void save(TaskList tasks) throws IOException {
        Path parent = filePath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        ArrayList<String> lines = new ArrayList<>();
        for (Task task : tasks.getAll()) {
            lines.add(formatTask(task));
        }
        Files.write(filePath, lines, StandardCharsets.UTF_8);
    }

    /**
     * Appends active tasks to the archive and clears the active task list.
     *
     * @param tasks Active tasks to archive.
     * @throws IOException If either data file cannot be written.
     */
    public void archive(TaskList tasks) throws IOException {
        Path archiveParent = archiveFilePath.getParent();
        if (archiveParent != null) {
            Files.createDirectories(archiveParent);
        }

        List<String> archiveLines = new ArrayList<>();
        for (Task task : tasks.getAll()) {
            archiveLines.add(formatTask(task));
        }
        Files.write(archiveFilePath, archiveLines, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);

        tasks.clear();
        save(tasks);
    }

    /**
     * Converts a task into its persistent text representation.
     *
     * @param task Task to convert.
     * @return File record containing the task type, status, and details.
     */
    private String formatTask(Task task) {
        String status = task.isDone() ? "1" : "0";
        if (task instanceof Deadline) {
            Deadline deadline = (Deadline) task;
            return "D | " + status + " | " + task.getDescription() + " | " + deadline.getBy();
        } else if (task instanceof Event) {
            Event event = (Event) task;
            return "E | " + status + " | " + task.getDescription()
                    + " | " + event.getStart() + " | " + event.getEnd();
        }
        return "T | " + status + " | " + task.getDescription();
    }

    /**
     * Converts one valid file record back into a task.
     *
     * @param line File record to parse.
     * @return Reconstructed task, or null for a malformed record.
     */
    private Task parseTask(String line) {
        String[] fields = line.split("\\s*\\|\\s*", -1);
        try {
            if (fields.length < 3 || !(fields[1].equals("0") || fields[1].equals("1"))) {
                return null;
            }

            Task task;
            switch (fields[0]) {
                case "T":
                    task = fields.length == 3 ? new Todo(fields[2]) : null;
                    break;
                case "D":
                    task = fields.length == 4
                            ? new Deadline(fields[2], LocalDate.parse(fields[3]))
                            : null;
                    break;
                case "E":
                    task = fields.length == 5
                            ? new Event(fields[2], fields[3], fields[4])
                            : null;
                    break;
                default:
                    task = null;
                    break;
            }

            if (task != null && fields[1].equals("1")) {
                task.markAsDone();
            }
            return task;
        } catch (RuntimeException e) {
            return null;
        }
    }
}
