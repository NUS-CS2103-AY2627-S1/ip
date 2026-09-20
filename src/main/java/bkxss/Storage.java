package bkxss;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.DateTimeException;
import java.util.ArrayList;

/**
 * Loads tasks from and saves tasks to the application's data file.
 */
public class Storage {
    private static final String BOT_PREFIX = "     ";
    private final Path dataFile;
    /** Prevents a partial or failed load from overwriting the original file. */
    private boolean isSaveBlocked;

    /** Creates storage backed by the given file path. */
    public Storage(String filePath) {
        this.dataFile = Path.of(filePath).toAbsolutePath();
    }

    /**
     * Loads valid records independently, reporting damaged records without losing later tasks.
     * Saves remain blocked after an incomplete load until the file is repaired and reloaded.
     */
    public ArrayList<Task> load() {
        ArrayList<Task> tasks = new ArrayList<>();
        isSaveBlocked = false;
        try {
            if (Files.notExists(dataFile)) {
                return tasks;
            }
            int lineNumber = 0;
            for (String line : Files.readAllLines(dataFile, StandardCharsets.UTF_8)) {
                lineNumber++;
                try {
                    Task task = parseTask(line.split("\\s*\\|\\s*", -1));
                    if (tasks.stream().anyMatch(existing -> existing.hasSameDetails(task))) {
                        throw new BkxssException("duplicate task");
                    }
                    tasks.add(task);
                } catch (BkxssException | DateTimeException exception) {
                    isSaveBlocked = true;
                    System.out.println(BOT_PREFIX + "I skipped invalid saved task on line " + lineNumber
                            + ". Repair the data file and restart before making changes.");
                }
            }
        } catch (IOException | SecurityException exception) {
            isSaveBlocked = true;
            System.out.println(BOT_PREFIX + "I couldn't load the saved tasks. Check the data file and permissions, "
                    + "then restart. Changes are disabled to protect your saved tasks.");
        }
        return tasks;
    }

    /**
     * Saves a complete replacement atomically, creating the parent directory when necessary.
     *
     * @throws BkxssException if saving fails or the previous load was incomplete.
     */
    public void save(ArrayList<Task> tasks) throws BkxssException {
        if (isSaveBlocked) {
            throw new BkxssException("the data file could not be fully loaded. "
                    + "Repair the file and permissions, then restart before making changes.");
        }
        Path temporaryFile = null;
        try {
            Files.createDirectories(dataFile.getParent());
            if (Files.exists(dataFile) && (!Files.isRegularFile(dataFile) || !Files.isWritable(dataFile))) {
                throw new IOException("The data path must be a writable regular file");
            }
            ArrayList<String> lines = new ArrayList<>();
            ArrayList<Task> validatedTasks = new ArrayList<>();
            for (Task task : tasks) {
                String line = serializeTask(task);
                // Validate the exact record that will be loaded on the next startup.
                Task parsedTask = parseTask(line.split("\\s*\\|\\s*", -1));
                if (validatedTasks.stream().anyMatch(existing -> existing.hasSameDetails(parsedTask))) {
                    throw new BkxssException("duplicate task");
                }
                validatedTasks.add(parsedTask);
                lines.add(line);
            }
            temporaryFile = Files.createTempFile(dataFile.getParent(), "bkxss-", ".tmp");
            Files.write(temporaryFile, lines, StandardCharsets.UTF_8);
            Files.move(temporaryFile, dataFile, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException | SecurityException | DateTimeException | BkxssException exception) {
            throw new BkxssException("I couldn't save your tasks. No changes were applied. "
                    + "Check the data file, folder permissions, and available disk space, then try again.");
        } finally {
            deleteTemporaryFile(temporaryFile);
        }
    }

    /** Removes an unused temporary file without hiding the original save failure. */
    private static void deleteTemporaryFile(Path temporaryFile) {
        if (temporaryFile == null) {
            return;
        }
        try {
            Files.deleteIfExists(temporaryFile);
        } catch (IOException | SecurityException exception) {
            // The original data file remains intact even if temporary-file cleanup fails.
        }
    }

    /** Encodes one supported task using the existing file format. */
    private static String serializeTask(Task task) throws BkxssException {
        Task.validateDescription(task.description);
        String details = status(task) + " | " + task.description;
        if (task instanceof Todo) {
            return "T | " + details;
        } else if (task instanceof Deadline deadline) {
            return "D | " + details + " | " + deadline.getFormattedBy();
        } else if (task instanceof Event event) {
            return "E | " + details + " | " + event.getFrom() + " to " + event.getTo();
        }
        throw new BkxssException("unsupported task type");
    }

    /** Validates one persistence record, retaining legacy free-text events. */
    private static Task parseTask(String[] fields) throws BkxssException {
        if (fields.length < 3 || (!fields[1].equals("0") && !fields[1].equals("1"))) {
            throw new BkxssException("invalid record or completion status");
        }
        Task.validateDescription(fields[2]);
        Task task;
        if (fields[0].equals("T") && fields.length == 3) {
            task = new Todo(fields[2]);
        } else if (fields[0].equals("D") && fields.length == 4) {
            task = new Deadline(fields[2], Deadline.parseFormattedBy(fields[3]));
        } else if (fields[0].equals("E") && fields.length == 4) {
            String[] times = fields[3].split(" to ", -1);
            if (times.length != 2) {
                throw new BkxssException("invalid event boundaries");
            }
            Task.validateDescription(times[0]);
            Task.validateDescription(times[1]);
            Event event = new Event(fields[2], times[0].strip(), times[1].strip());
            boolean hasDatedBoundary = times[0].matches("[+-]?\\d{4,}-.*")
                    || times[1].matches("[+-]?\\d{4,}-.*");
            if (hasDatedBoundary && !event.hasScheduledTimes()) {
                throw new BkxssException("invalid event dates or range");
            }
            task = event;
        } else {
            throw new BkxssException("unsupported record format");
        }
        if (fields[1].equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /** Converts a task's completion state to the file format's numeric flag. */
    private static String status(Task task) {
        return task.isDone() ? "1" : "0";
    }
}
