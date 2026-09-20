package todd;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/** Loads and saves Todd's task list using a text file on the hard disk. */
public class Storage {
    private static final String SEPARATOR = " | ";

    private final Path filePath;

    /** Creates storage that reads and writes tasks at the specified path. */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads saved tasks, creating the data folder when Todd is run for the first time.
     *
     * @return tasks reconstructed from the save file, or an empty list if no save file exists
     * @throws TodException if the file cannot be read or contains invalid task data
     */
    public ArrayList<Task> load() throws TodException {
        try {
            createParentDirectory();
            if (Files.notExists(filePath)) {
                return new ArrayList<>();
            }

            ArrayList<Task> tasks = new ArrayList<>();
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (!line.isBlank()) {
                    tasks.add(parseTask(line, i + 1));
                }
            }
            return tasks;
        } catch (IOException e) {
            throw new TodException("I couldn't load tasks from " + filePath + ".");
        }
    }

    /**
     * Replaces the save file with the current task list.
     *
     * @param tasks current tasks to store
     * @throws TodException if the tasks cannot be written to disk
     */
    public void save(List<Task> tasks) throws TodException {
        Path temporaryFile = null;
        try {
            createParentDirectory();
            List<String> lines = tasks.stream()
                    .map(this::formatTask)
                    .toList();
            // Write fully before replacing the old file, so failed writes cannot truncate saved tasks.
            temporaryFile = Files.createTempFile(filePath.toAbsolutePath().getParent(), "todd-", ".tmp");
            Files.write(temporaryFile, lines, StandardCharsets.UTF_8);
            Files.move(temporaryFile, filePath, StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new TodException("I couldn't save tasks to " + filePath + ". No task changes were kept. "
                    + "Check that the folder is writable and has free space; atomic file replacement is required.");
        } finally {
            if (temporaryFile != null) {
                try {
                    Files.deleteIfExists(temporaryFile);
                } catch (IOException e) {
                    // A leftover temporary file must not hide the original save error.
                }
            }
        }
    }

    /** Creates the folder containing the save file when that folder does not exist. */
    private void createParentDirectory() throws IOException {
        Path parent = filePath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
    }

    /** Converts one task into a line suitable for the save file. */
    private String formatTask(Task task) {
        String status = task.isDone() ? "1" : "0";
        if (task instanceof Deadline) {
            Deadline deadline = (Deadline) task;
            return "D" + SEPARATOR + status + SEPARATOR + deadline.getDescription()
                    + SEPARATOR + deadline.getBy().toString();
        }
        if (task instanceof Event) {
            Event event = (Event) task;
            return "E" + SEPARATOR + status + SEPARATOR + event.getDescription()
                    + SEPARATOR + event.getFrom().toString() + SEPARATOR + event.getTo().toString();
        }
        return "T" + SEPARATOR + status + SEPARATOR + task.getDescription();
    }

    /** Reconstructs one task from a saved line and validates its stored fields. */
    private Task parseTask(String line, int lineNumber) throws TodException {
        String[] fields = line.split(" \\| ", -1);
        if (fields.length < 3 || fields[2].isBlank()) {
            throw invalidData(lineNumber);
        }

        Task task;
        try {
            switch (fields[0]) {
                case "T":
                    if (fields.length != 3) {
                        throw invalidData(lineNumber);
                    }
                    task = new Todo(fields[2]);
                    break;
                case "D":
                    if (fields.length != 4 || fields[3].isBlank()) {
                        throw invalidData(lineNumber);
                    }
                    task = new Deadline(fields[2], LocalDateTime.parse(fields[3]));
                    break;
                case "E":
                    if (fields.length != 5 || fields[3].isBlank() || fields[4].isBlank()) {
                        throw invalidData(lineNumber);
                    }
                    LocalDateTime from = LocalDateTime.parse(fields[3]);
                    LocalDateTime to = LocalDateTime.parse(fields[4]);
                    if (!to.isAfter(from)) {
                        throw invalidData(lineNumber);
                    }
                    task = new Event(fields[2], from, to);
                    break;
                default:
                    throw invalidData(lineNumber);
            }
        } catch (DateTimeParseException e) {
            throw invalidData(lineNumber);
        }

        if (fields[1].equals("1")) {
            task.markAsDone();
        } else if (!fields[1].equals("0")) {
            throw invalidData(lineNumber);
        }
        return task;
    }

    private TodException invalidData(int lineNumber) {
        return new TodException("The saved task on line " + lineNumber + " is invalid.");
    }
}
