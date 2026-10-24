package kiki.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import kiki.exception.KikiException;
import kiki.task.Deadlines;
import kiki.task.Events;
import kiki.task.Task;
import kiki.task.TaskList;
import kiki.task.Priority;
import kiki.task.ToDos;
import kiki.ui.Ui;

/**
 * Handles loading tasks from disk on startup and saving the task list to
 * disk after every change.
 */
public class Storage {
    private static final Path DEFAULT_SAVE_FILE_PATH = Path.of("data", "kiki.txt");

    private final Path saveFilePath;

    /**
     * Creates storage using the application's default save-file path.
     */
    public Storage() {
        this(DEFAULT_SAVE_FILE_PATH);
    }

    /**
     * Creates storage that reads and writes the supplied save file.
     *
     * @param saveFilePath Path to the save file.
     */
    public Storage(Path saveFilePath) {
        this.saveFilePath = saveFilePath;
    }

    /**
     * Saves the given task list to disk, overwriting any previous contents.
     *
     * @param tasks Task list to save.
     * @throws KikiException If the save folder cannot be prepared or the file
     *         cannot be written.
     */
    public void save(TaskList tasks) throws KikiException {
        Path parentPath = saveFilePath.getParent();

        try {
            if (parentPath != null) {
                Files.createDirectories(parentPath);
            }

            if (Files.isDirectory(saveFilePath)) {
                throw new KikiException("Unable to save tasks because the save path is a folder.");
            }
        } catch (IOException e) {
            throw new KikiException("Unable to prepare the save folder.");
        } catch (SecurityException e) {
            throw new KikiException("Unable to access the save folder.");
        }

        try (BufferedWriter writer = Files.newBufferedWriter(saveFilePath)) {
            for (int i = 0; i < tasks.size(); i++) {
                writer.write(formatForStorage(tasks.get(i)));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new KikiException("Unable to save tasks to disk.");
        } catch (SecurityException e) {
            throw new KikiException("Unable to access the save file.");
        }
    }

    /**
     * Loads previously saved tasks into {@code tasks}, printing a warning
     * via {@code ui} for any corrupted or excess saved lines.
     */
    public void load(TaskList tasks, Ui ui) {
        try {
            if (!Files.exists(saveFilePath)) {
                return;
            }

            if (Files.isDirectory(saveFilePath)) {
                ui.printBox("OOPS!!! Unable to load tasks because the save path is a folder.");
                return;
            }
        } catch (SecurityException e) {
            ui.printBox("OOPS!!! Unable to access the save path.");
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(saveFilePath)) {
            String line = reader.readLine();

            while (line != null) {
                String trimmedLine = line.trim();

                if (trimmedLine.isEmpty()) {
                    line = reader.readLine();
                    continue;
                }

                if (tasks.isFull()) {
                    ui.printBox("OOPS!!! Save file has more than " + tasks.getCapacity()
                            + " tasks. Extra tasks were ignored.");
                    break;
                }

                try {
                    tasks.add(parseSavedTask(trimmedLine));
                } catch (KikiException e) {
                    ui.printBox("OOPS!!! Skipped a corrupted saved task: " + e.getMessage());
                }

                line = reader.readLine();
            }
        } catch (IOException e) {
            ui.printBox("OOPS!!! Unable to load tasks from disk.");
        } catch (SecurityException e) {
            ui.printBox("OOPS!!! Unable to access saved tasks.");
        }
    }

    /**
     * Formats a task as a single pipe-separated line for the save file.
     *
     * @param task Task to format.
     * @return The formatted line, without a trailing newline.
     */
    private String formatForStorage(Task task) {
        String doneStatus = task.isDone() ? "1" : "0";

        if (task instanceof Deadlines deadline) {
            return "D | " + doneStatus + " | " + deadline.getDescription()
                    + " | " + deadline.getBy() + " | " + deadline.getPriority();
        }

        if (task instanceof Events event) {
            return "E | " + doneStatus + " | " + event.getDescription() + " | " + event.getFrom()
                    + " | " + event.getTo() + " | " + event.getPriority();
        }

        return "T | " + doneStatus + " | " + task.getDescription() + " | " + task.getPriority();
    }

    /**
     * Parses a single pipe-separated line from the save file back into a task.
     *
     * @param line Trimmed, non-empty line read from the save file.
     * @return The reconstructed task.
     * @throws KikiException If the line has the wrong number of fields, an
     *         unknown task type, a bad done-status, or an unparsable date/time.
     */
    private Task parseSavedTask(String line) throws KikiException {
        String[] parts = line.split(" \\| ", -1);
        String taskType = parts[0];
        int expectedParts = getExpectedPartCount(taskType);

        if (parts.length != expectedParts && parts.length != expectedParts - 1) {
            throw new KikiException("invalid saved task format.");
        }

        boolean isDone = parseSavedDoneStatus(parts[1]);
        String description = parts[2].trim();

        ensureNotEmpty(description, "saved task description is empty.");

        Task task = createSavedTask(taskType, description, parts);

        if (isDone) {
            task.markAsDone();
        }

        if (parts.length == expectedParts) {
            task.setPriority(parseSavedPriority(parts[expectedParts - 1]));
        }

        return task;
    }

    private Task createSavedTask(String taskType, String description, String[] parts) throws KikiException {
        if (taskType.equals("D")) {
            return createSavedDeadline(description, parts[3]);
        } else if (taskType.equals("E")) {
            return createSavedEvent(description, parts[3], parts[4]);
        } else {
            return new ToDos(description);
        }
    }

    private Deadlines createSavedDeadline(String description, String byText) throws KikiException {
        String trimmedByText = byText.trim();
        ensureNotEmpty(trimmedByText, "saved deadline time is empty.");

        try {
            LocalDateTime by = LocalDateTime.parse(trimmedByText);
            return new Deadlines(description, by);
        } catch (DateTimeParseException e) {
            throw new KikiException("invalid saved deadline date/time.");
        }
    }

    private Events createSavedEvent(String description, String fromText, String toText) throws KikiException {
        String trimmedFromText = fromText.trim();
        String trimmedToText = toText.trim();
        ensureNotEmpty(trimmedFromText, "saved event start time is empty.");
        ensureNotEmpty(trimmedToText, "saved event end time is empty.");

        try {
            LocalDateTime from = LocalDateTime.parse(trimmedFromText);
            LocalDateTime to = LocalDateTime.parse(trimmedToText);

            if (!from.isBefore(to)) {
                throw new KikiException("invalid saved event date/time: start must be before end.");
            }

            return new Events(description, from, to);
        } catch (DateTimeParseException e) {
            throw new KikiException("invalid saved event date/time.");
        }
    }

    /**
     * Returns how many pipe-separated fields a saved line of the given task
     * type should have.
     *
     * @param taskType Single-letter saved task type ("T", "D", or "E").
     * @return The expected field count.
     * @throws KikiException If the task type is not recognized.
     */
    private int getExpectedPartCount(String taskType) throws KikiException {
        if (taskType.equals("T")) {
            return 4;
        } else if (taskType.equals("D")) {
            return 5;
        } else if (taskType.equals("E")) {
            return 6;
        } else {
            throw new KikiException("unknown saved task type.");
        }
    }

    private Priority parseSavedPriority(String priorityText) throws KikiException {
        try {
            return Priority.valueOf(priorityText.trim());
        } catch (IllegalArgumentException e) {
            throw new KikiException("saved task priority is invalid.");
        }
    }

    /**
     * Parses a saved done-status field ("0" or "1") into a boolean.
     *
     * @param doneStatus Saved done-status field.
     * @return {@code true} if the field is "1", {@code false} if "0".
     * @throws KikiException If the field is neither "0" nor "1".
     */
    private boolean parseSavedDoneStatus(String doneStatus) throws KikiException {
        if (doneStatus.equals("1")) {
            return true;
        } else if (doneStatus.equals("0")) {
            return false;
        } else {
            throw new KikiException("saved task status must be 0 or 1.");
        }
    }

    private void ensureNotEmpty(String value, String errorMessage) throws KikiException {
        if (value.isEmpty()) {
            throw new KikiException(errorMessage);
        }
    }
}
