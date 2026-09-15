package duke.storage;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import duke.exception.GaryException;
import duke.task.Deadline;
import duke.task.Event;
import duke.task.Task;
import duke.task.ToDo;

/**
 * Handles persistence of tasks to and from a text file on disk.
 *
 * <p>The storage file is line-based. Each line represents one task in the following formats:
 * <ul>
 *   <li>Todo: {@code T | <0/1> | <description>}</li>
 *   <li>Deadline: {@code D | <0/1> | <description> | <YYYY-MM-DD>}</li>
 *   <li>Event: {@code E | <0/1> | <description> | <YYYY-MM-DD> | <YYYY-MM-DD>}</li>
 * </ul>
 *
 * <p>Blank lines are ignored. Malformed task data and file I/O failures produce a user-facing
 * {@link GaryException} rather than allowing startup to fail unexpectedly.</p>
 */
public class Storage {
    private static final int MAX_DESCRIPTION_LENGTH = 200;
    private final Path filePath;

    /**
     * Creates a {@code Storage} that reads from/writes to the given path.
     *
     * @param filePath Path to the storage file (e.g., {@code data/duke.txt}).
     */
    public Storage(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            throw new GaryException("The task storage path is missing.");
        }

        try {
            this.filePath = Path.of(filePath);
        } catch (InvalidPathException e) {
            throw new GaryException("The task storage path is invalid.");
        }
    }

    /**
     * Loads all tasks from disk.
     *
     * <p>If the file does not exist yet, returns an empty list.</p>
     *
     * @return List of tasks loaded from disk.
     * @throws GaryException If an I/O error occurs while reading the file.
     */
    public ArrayList<Task> load() {
        try {
            if (Files.notExists(filePath)) {
                return new ArrayList<>();
            }
            if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
                throw new GaryException("Could not read the task file. Check its location and permissions.");
            }

            List<String> lines = Files.readAllLines(filePath);
            ArrayList<Task> tasks = new ArrayList<>();
            for (int index = 0; index < lines.size(); index++) {
                String line = lines.get(index);
                if (line.isBlank()) {
                    continue;
                }

                int lineNumber = index + 1;
                Task task;
                try {
                    task = parseTask(line, lineNumber);
                } catch (DateTimeParseException e) {
                    throw invalidData(lineNumber, "a date is invalid");
                }

                boolean isDuplicate = tasks.stream().anyMatch(existingTask -> existingTask.hasSameDetails(task));
                if (isDuplicate) {
                    throw invalidData(lineNumber, "the task is duplicated");
                }
                tasks.add(task);
            }
            return tasks;
        } catch (IOException | SecurityException e) {
            throw new GaryException("Could not read the task file. Check its location and permissions.");
        }
    }

    /**
     * Saves the given list of tasks to disk using a temporary file and atomic replacement where supported.
     *
     * <p>Parent directories are created automatically if they do not exist.</p>
     *
     * @param tasks List of tasks to save.
     * @throws GaryException If an I/O or permission error occurs while writing the file.
     */
    public void save(ArrayList<Task> tasks) {
        assert tasks != null : "Task list to save must not be null";
        assert tasks.stream().noneMatch(task -> task == null) : "Task list to save must not contain null tasks";

        Path temporaryFile = null;
        try {
            Path absoluteFilePath = filePath.toAbsolutePath();
            Path parentDirectory = absoluteFilePath.getParent();
            if (parentDirectory == null) {
                throw new IOException("Storage path has no parent directory");
            }
            Files.createDirectories(parentDirectory);
            List<String> lines = tasks.stream()
                    .map(Task::toDataString)
                    .toList();
            temporaryFile = Files.createTempFile(parentDirectory, "gary-", ".tmp");
            Files.write(temporaryFile, lines);
            replaceFile(temporaryFile, absoluteFilePath);
        } catch (IOException | SecurityException e) {
            throw new GaryException("Could not save tasks. Check file permissions and available disk space.");
        } finally {
            deleteTemporaryFile(temporaryFile);
        }
    }

    /**
     * Parses a single line of the storage file into a {@link Task}.
     *
     * @param line A single non-blank line from the storage file.
     * @param lineNumber One-based line number used in error messages.
     * @return The parsed task.
     * @throws GaryException If the task data does not match the storage format.
     */
    private Task parseTask(String line, int lineNumber) {
        assert line != null : "A line read from the storage file must not be null";
        assert !line.isBlank() : "Blank storage lines must be filtered before parsing";

        String[] parts = line.split("\\|", -1);
        String type = parts[0].trim();
        int expectedPartCount = switch (type) {
            case "T" -> 3;
            case "D" -> 4;
            case "E" -> 5;
            default -> throw invalidData(lineNumber, "the task type is unknown");
        };
        if (parts.length != expectedPartCount) {
            throw invalidData(lineNumber, "the number of fields is incorrect");
        }

        String status = parts[1].trim();
        if (!status.equals("0") && !status.equals("1")) {
            throw invalidData(lineNumber, "the completion status must be 0 or 1");
        }

        String description = parts[2].trim().replaceAll("\\s+", " ");
        if (description.isEmpty()) {
            throw invalidData(lineNumber, "the description is empty");
        }
        if (description.length() > MAX_DESCRIPTION_LENGTH) {
            throw invalidData(lineNumber, "the description is too long");
        }

        Task task = switch (type) {
            case "T" -> new ToDo(description);
            case "D" -> new Deadline(description, LocalDate.parse(parts[3].trim()));
            case "E" -> {
                LocalDate start = LocalDate.parse(parts[3].trim());
                LocalDate end = LocalDate.parse(parts[4].trim());
                if (!start.isBefore(end)) {
                    throw invalidData(lineNumber, "the event must end after it starts");
                }
                yield new Event(description, start, end);
            }
            default -> throw new AssertionError("Task type must have been validated");
        };

        if (status.equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    private GaryException invalidData(int lineNumber, String reason) {
        return new GaryException("Task file data is invalid on line " + lineNumber + ": " + reason + ".");
    }

    private void replaceFile(Path temporaryFile, Path targetFile) throws IOException {
        try {
            Files.move(temporaryFile, targetFile, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(temporaryFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private void deleteTemporaryFile(Path temporaryFile) {
        if (temporaryFile == null) {
            return;
        }

        try {
            Files.deleteIfExists(temporaryFile);
        } catch (IOException | SecurityException e) {
            // The save result is already known; a leftover temporary file should not hide it.
        }
    }
}
