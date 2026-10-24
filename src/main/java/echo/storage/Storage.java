package echo.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import echo.task.Deadline;
import echo.task.Event;
import echo.task.Task;
import echo.task.Todo;

/**
 * Reads and writes the task list to a save file on disk, and appends
 * archived tasks to a separate archive file. Both file locations are
 * fixed at construction time. Load/save/archive never throw: any IO
 * problem is reported with a short warning so a save-file issue never
 * prevents the chatbot from starting or running.
 */
public class Storage {
    private final Path filePath;
    private final Path archiveFilePath;

    /**
     * Creates a Storage bound to the given save-file path. Archived tasks
     * go to a sibling file named "archive.txt" in the same folder.
     *
     * @param filePath Relative path to the save file, e.g. "data/echo.txt".
     */
    public Storage(String filePath) {
        this.filePath = Path.of(filePath);
        this.archiveFilePath = this.filePath.resolveSibling("archive.txt");
    }

    /**
     * Loads tasks from the save file, creating the file (and its parent
     * folder) if either is missing. Lines that cannot be parsed are skipped
     * with a warning rather than aborting the whole load.
     */
    public List<Task> load() {
        try {
            ensureFileExists(filePath);
            return Files.readAllLines(filePath).stream()
                    .filter(line -> !line.isBlank())
                    .map(this::parseLine)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            System.out.println("Warning: could not load saved tasks (" + e.getMessage()
                    + "). Starting with an empty list.");
            return new ArrayList<>();
        }
    }

    /**
     * Overwrites the save file with the given tasks, one per line.
     */
    public void save(List<Task> tasks) {
        try {
            ensureFileExists(filePath);
            List<String> lines = tasks.stream()
                    .map(Task::toFileFormat)
                    .collect(Collectors.toList());
            Files.write(filePath, lines);
        } catch (IOException e) {
            System.out.println("Warning: could not save tasks (" + e.getMessage() + ").");
        }
    }

    /**
     * Appends the given tasks to the archive file, creating it (and its
     * parent folder) if missing. Previously archived tasks are kept, so
     * archiving is cumulative across multiple uses rather than
     * overwriting what came before.
     *
     * @param tasks The tasks to archive.
     */
    public void archive(List<Task> tasks) {
        try {
            ensureFileExists(archiveFilePath);
            List<String> lines = new ArrayList<>();
            for (Task task : tasks) {
                lines.add(task.toFileFormat());
            }
            Files.write(archiveFilePath, lines, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.println("Warning: could not archive tasks (" + e.getMessage() + ").");
        }
    }

    /**
     * Creates the given file's parent folder and the file itself if either
     * is missing, so load()/save()/archive() never have to handle a
     * missing path. Shared by both the save file and the archive file,
     * which otherwise need this exact same setup.
     */
    private void ensureFileExists(Path path) throws IOException {
        Path parent = path.getParent();
        if (parent != null && Files.notExists(parent)) {
            Files.createDirectories(parent);
        }
        if (Files.notExists(path)) {
            Files.createFile(path);
        }
    }

    /**
     * Parses one save-file line into a Task, or returns null (with a
     * printed warning) if the line is corrupted/unrecognized.
     */
    private Task parseLine(String line) {
        try {
            String[] parts = line.split(Pattern.quote(Task.FILE_FORMAT_SEPARATOR));
            String typeTag = parts[0].trim();
            boolean isDone = parts[1].trim().equals("1");
            String description = parts[2].trim();

            Task task = switch (typeTag) {
                case "T" -> new Todo(description);
                case "D" -> new Deadline(description, LocalDate.parse(parts[3].trim()));
                case "E" -> new Event(description, LocalDate.parse(parts[3].trim()),
                        LocalDate.parse(parts[4].trim()));
                default -> throw new IllegalArgumentException("Unknown task type: " + typeTag);
            };
            if (isDone) {
                task.markAsDone();
            }
            return task;
        } catch (RuntimeException e) {
            System.out.println("Warning: skipping corrupted line in save file: " + line);
            return null;
        }
    }
}
