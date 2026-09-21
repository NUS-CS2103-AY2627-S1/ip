package topaz.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import topaz.TopazException;
import topaz.task.Deadline;
import topaz.task.Event;
import topaz.task.FixedDurationTask;
import topaz.task.Task;
import topaz.task.Todo;
import topaz.util.DateTimeParser;

/**
 * Loads tasks from and saves tasks to the configured data file.
 */
public class Storage {
    private static final Pattern POSITIVE_DURATION_PATTERN = Pattern.compile("[1-9]\\d*");
    private final Path saveFile;

    /**
     * Creates storage that uses the given file path.
     *
     * @param saveFile the path of the task data file
     */
    public Storage(Path saveFile) {
        this.saveFile = saveFile;
    }

    /**
     * Saves every task in the current list to the data file.
     *
     * @param tasks the tasks to save
     * @throws TopazException if the data file cannot be written
     */
    public void save(List<Task> tasks) throws TopazException {
        Path temporaryFile = null;
        try {
            Path target = saveFile.toAbsolutePath();
            Path parent = target.getParent();
            if (parent == null) {
                throw new TopazException("Unable to save your tasks: the save path must be a regular file.");
            }
            Files.createDirectories(parent);
            validateSaveTarget(target);
            temporaryFile = Files.createTempFile(parent, "gronk-", ".tmp");
            try (BufferedWriter writer = Files.newBufferedWriter(temporaryFile, StandardCharsets.UTF_8)) {
                for (Task task : tasks) {
                    writer.write(task.toFileString());
                    writer.newLine();
                }
            }
            // Only replace the old file after every record has been written and closed.
            Files.move(temporaryFile, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException exception) {
            throw new TopazException("Unable to save your tasks: this location does not support safe atomic saves."
                    + " Choose a local data folder.");
        } catch (AccessDeniedException | SecurityException exception) {
            throw new TopazException("Unable to save your tasks: access denied. Check file and folder permissions.");
        } catch (IOException exception) {
            throw new TopazException("Unable to save your tasks. Check the data folder, disk space, and file locks.");
        } finally {
            deleteTemporaryFile(temporaryFile);
        }
    }

    /**
     * Rejects directories, links, and read-only files before replacing the save target.
     */
    private void validateSaveTarget(Path target) throws IOException, TopazException {
        try {
            BasicFileAttributes attributes = Files.readAttributes(target, BasicFileAttributes.class,
                    LinkOption.NOFOLLOW_LINKS);
            if (!attributes.isRegularFile()) {
                throw new TopazException("Unable to save your tasks: the save path must be a regular file.");
            }
            if (!Files.isWritable(target)) {
                throw new AccessDeniedException(target.toString());
            }
        } catch (NoSuchFileException exception) {
            // A new save file is expected on the first successful task change.
        }
    }

    /**
     * Removes an unused staging file without hiding the original save failure.
     */
    private void deleteTemporaryFile(Path temporaryFile) {
        if (temporaryFile == null) {
            return;
        }
        try {
            Files.deleteIfExists(temporaryFile);
        } catch (IOException | SecurityException exception) {
            // An inaccessible orphan is harmless; never delete the real save file to recover.
        }
    }

    /**
     * Loads saved tasks, returning an empty list only when the file is confirmed missing.
     *
     * @return the tasks loaded from the data file
     * @throws TopazException if the saved task list cannot be safely read
     */
    public List<Task> load() throws TopazException {
        List<Task> tasks = new ArrayList<>();
        try {
            BasicFileAttributes attributes;
            try {
                attributes = Files.readAttributes(saveFile, BasicFileAttributes.class);
            } catch (NoSuchFileException exception) {
                validateMissingFileParent();
                return tasks;
            }
            if (!attributes.isRegularFile()) {
                throw new TopazException("The save file path is not a file. Check topaz.dataFile.");
            }
            try (BufferedReader reader = Files.newBufferedReader(saveFile, StandardCharsets.UTF_8)) {
                String line;
                int lineNumber = 0;
                while ((line = reader.readLine()) != null) {
                    lineNumber++;
                    if (lineNumber == 1 && line.startsWith("\uFEFF")) {
                        line = line.substring(1);
                    }
                    if (!line.isBlank()) {
                        addSavedTask(tasks, line, lineNumber);
                    }
                }
            }
        } catch (AccessDeniedException | SecurityException exception) {
            throw new TopazException("Unable to load your saved tasks: access denied. Check file permissions.");
        } catch (IOException exception) {
            throw new TopazException("Unable to load your saved tasks. Check that the file is readable UTF-8 text.");
        }
        return tasks;
    }

    /**
     * Distinguishes a missing data folder from a non-directory ancestor on Windows.
     */
    private void validateMissingFileParent() throws IOException, TopazException {
        Path parent = saveFile.toAbsolutePath().getParent();
        while (parent != null) {
            try {
                if (!Files.readAttributes(parent, BasicFileAttributes.class).isDirectory()) {
                    throw new TopazException("The data directory path is not a directory. Check topaz.dataFile.");
                }
                return;
            } catch (NoSuchFileException exception) {
                parent = parent.getParent();
            }
        }
    }

    /**
     * Validates a record and identifies its line when the file needs repair.
     */
    private void addSavedTask(List<Task> tasks, String line, int lineNumber) throws TopazException {
        try {
            if (line.codePoints().anyMatch(character -> (Character.isISOControl(character) && character != '\t')
                    || character == '\u2028' || character == '\u2029')) {
                throw new TopazException("Task details contain control characters.");
            }
            Task task = createTask(line);
            if (tasks.stream().anyMatch(existing -> existing.hasSameDetails(task))) {
                throw new TopazException("The save file contains a duplicate task.");
            }
            tasks.add(task);
        } catch (TopazException exception) {
            throw new TopazException("Invalid save data at line " + lineNumber + ": " + exception.getMessage()
                    + " Repair this line or restore a backup; the file has not been changed.");
        }
    }

    /**
     * Reconstructs one task from a line in the data file.
     */
    private Task createTask(String line) throws TopazException {
        String[] values = line.split(" \\| ", -1);
        if (values.length < 3 || (!values[1].equals("0") && !values[1].equals("1"))) {
            throw new TopazException("Unable to load a saved task.");
        }
        for (int i = 2; i < values.length; i++) {
            if (values[i].isBlank() || values[i].contains("|")) {
                throw new TopazException("Unable to load a saved task.");
            }
        }

        Task task;
        if (values.length == 3 && values[0].equals("T")) {
            task = new Todo(values[2]);
        } else if (values.length == 4 && values[0].equals("D")) {
            task = new Deadline(values[2], DateTimeParser.parse(values[3], "Unable to load a saved task."),
                    DateTimeParser.hasTimeComponent(values[3]));
        } else if (values.length == 5 && values[0].equals("E")) {
            LocalDateTime from = DateTimeParser.parse(values[3], "Unable to load a saved task.");
            LocalDateTime to = DateTimeParser.parse(values[4], "Unable to load a saved task.");
            DateTimeParser.validateEventPeriod(from, to);
            task = new Event(values[2], from, to,
                    DateTimeParser.hasTimeComponent(values[3]), DateTimeParser.hasTimeComponent(values[4]));
        } else if (values.length == 4 && values[0].equals("F")) {
            task = new FixedDurationTask(values[2], parseDurationMinutes(values[3]));
        } else {
            throw new TopazException("Unable to load a saved task.");
        }

        if (values[1].equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Parses a positive whole-minute duration stored in a fixed-duration task.
     */
    private long parseDurationMinutes(String durationText) throws TopazException {
        if (!POSITIVE_DURATION_PATTERN.matcher(durationText).matches()) {
            throw new TopazException("Unable to load a saved task.");
        }
        try {
            long durationMinutes = Long.parseLong(durationText);
            return durationMinutes;
        } catch (NumberFormatException exception) {
            throw new TopazException("Unable to load a saved task.");
        }
    }

}
