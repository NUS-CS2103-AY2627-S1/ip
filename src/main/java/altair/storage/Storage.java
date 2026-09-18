package altair.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import altair.AltairException;
import altair.task.Deadline;
import altair.task.Event;
import altair.task.Task;
import altair.task.Todo;

/**
 * Loads the task list from a text file and saves it back to the same file.
 *
 * <p>Keeping all file handling in one class lets {@link altair.Altair} concentrate on
 * the conversation with the user: the main loop asks this class to read the
 * saved tasks once at start-up and to persist the whole list after every
 * change.</p>
 */
public class Storage {

    /** The date format used for the dates stored inside each saved line. */
    private static final DateTimeFormatter STORED_DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    /** The file that holds the saved task list between runs. */
    private final Path file;

    /**
     * Creates a storage backed by the given file path.
     *
     * @param filePath the location of the saved task list, e.g. {@code ./data/altair.txt}.
     */
    public Storage(String filePath) {
        this.file = Path.of(filePath);
    }

    /**
     * Reads the saved task list, if the file exists.
     *
     * @return the tasks restored from disk, or an empty list for a new project.
     * @throws AltairException if the file cannot be read or a line is malformed.
     */
    public List<Task> load() throws AltairException {
        List<Task> tasks = new ArrayList<>();
        List<String> fileLines;
        try {
            if (!Files.exists(file)) {
                return tasks;
            }
            fileLines = Files.readAllLines(file, StandardCharsets.UTF_8);
        } catch (IOException | SecurityException exception) {
            throw new AltairException("I couldn't load your tasks.");
        }

        for (int i = 0; i < fileLines.size(); i++) {
            String line = fileLines.get(i);
            if (!line.trim().isEmpty()) {
                try {
                    tasks.add(taskFromFileLine(line));
                } catch (AltairException exception) {
                    throw new AltairException("I couldn't load your tasks on line " + (i + 1) + ".");
                }
            }
        }
        return tasks;
    }

    /**
     * Writes the current task list to disk, replacing the previous snapshot.
     *
     * <p>The parent directory is created on the first save so a fresh project
     * can be run without any manual setup.</p>
     *
     * @param tasks the current task list.
     * @throws AltairException if the task list cannot be written.
     */
    public void save(List<Task> tasks) throws AltairException {
        Path temporaryFile = file.resolveSibling(file.getFileName() + ".tmp");
        try {
            Files.createDirectories(file.getParent());
            List<String> fileLines = tasks.stream()
                    .map(Task::toFileString)
                    .toList();
            Files.write(temporaryFile, fileLines, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE);
            try {
                Files.move(temporaryFile, file, StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(temporaryFile, file, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException | SecurityException exception) {
            throw new AltairException("I couldn't save your tasks.");
        } finally {
            try {
                Files.deleteIfExists(temporaryFile);
            } catch (IOException | SecurityException ignored) {
                // The saved snapshot is still usable if cleanup cannot remove the temporary file.
            }
        }
    }

    /**
     * Reconstructs a task from one line in the saved task-list format.
     *
     * @param line one serialized task.
     * @return the reconstructed task.
     * @throws AltairException if the line does not use the supported format.
     */
    private static Task taskFromFileLine(String line) throws AltairException {
        String[] parts = line.split("\\s*\\|\\s*", 4);
        if (parts.length < 3) {
            throw new AltairException("I couldn't load your tasks.");
        }

        String type = parts[0].trim();
        String status = parts[1].trim();
        String description = parts[2].trim();
        if (description.isEmpty() || (!status.equals("0") && !status.equals("1"))) {
            throw new AltairException("I couldn't load your tasks.");
        }

        Task task;
        switch (type) {
        case "T":
            if (parts.length != 3) {
                throw new AltairException("I couldn't load your tasks.");
            }
            task = new Todo(description);
            break;

        case "D":
            if (parts.length != 4 || parts[3].contains("|") || parts[3].trim().isEmpty()) {
                throw new AltairException("I couldn't load your tasks.");
            }
            task = new Deadline(description, parseStoredDate(parts[3].trim()));
            break;

        case "E":
            if (parts.length != 4 || parts[3].contains("|")) {
                throw new AltairException("I couldn't load your tasks.");
            }
            String eventDetails = parts[3].trim();
            String[] dates = eventDetails.split("\\s+-\\s+", 2);
            if (dates.length != 2 || dates[0].trim().isEmpty() || dates[1].trim().isEmpty()) {
                throw new AltairException("I couldn't load your tasks.");
            }
            task = new Event(description, parseStoredDate(dates[0].trim()),
                    parseStoredDate(dates[1].trim()));
            break;

        default:
            throw new AltairException("I couldn't load your tasks.");
        }

        if (status.equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /** Parses a stored ISO date, reporting the shared load error on failure. */
    private static LocalDate parseStoredDate(String text) throws AltairException {
        try {
            return LocalDate.parse(text, STORED_DATE_FORMAT);
        } catch (DateTimeParseException exception) {
            throw new AltairException("I couldn't load your tasks.");
        }
    }
}
