package peter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Handles saving tasks to and loading tasks from a text file.
 */
public class Storage {
    private static final String TODO_TYPE = "T";
    private static final String DEADLINE_TYPE = "D";
    private static final String EVENT_TYPE = "E";
    // STRICT so a hand-edited save file with an impossible calendar date (e.g. Feb 30)
    // is treated as a malformed line instead of being silently resolved to a nearby date.
    // uuuu (proleptic year), not yyyy (year-of-era): STRICT can't resolve yyyy without an
    // explicit era field, which this format doesn't have.
    private static final DateTimeFormatter STORAGE_DATE_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HHmm").withResolverStyle(ResolverStyle.STRICT);

    private final Path filePath;

    /**
     * Creates storage that uses the given relative or absolute file path.
     *
     * @param filePathString the location of the task data file
     */
    public Storage(String filePathString) {
        this.filePath = Path.of(filePathString);
    }

    /**
     * Loads all valid tasks from the data file.
     *
     * @return the tasks successfully loaded from disk
     * @throws PeterException if the data file or its directory cannot be read
     */
    public ArrayList<Task> load() throws PeterException {
        try {
            ensureDataFileExists();
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            return lines.stream()
                    .map(this::parseTask)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toCollection(ArrayList::new));
        } catch (IOException exception) {
            throw new PeterException("My spidey-sense says that save file's a lost cause - starting fresh.");
        }
    }

    /**
     * Saves the given tasks to the data file, replacing its previous contents.
     *
     * @param tasks the current list of tasks to save
     */
    public void save(ArrayList<Task> tasks) {
        List<String> taskLines = tasks.stream()
                .map(Task::toFileFormat)
                .collect(Collectors.toList());
        try {
            ensureDataFileExists();
            Files.write(filePath, taskLines, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException exception) {
            System.out.println("     Tried to stick that update to the web, but it slipped - didn't save.");
        }
    }

    /**
     * Creates the data directory and data file when they do not already exist.
     *
     * @throws IOException if the directory or file cannot be created
     */
    private void ensureDataFileExists() throws IOException {
        Path parentDirectory = filePath.getParent();
        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }
        if (Files.notExists(filePath)) {
            Files.createFile(filePath);
        }
    }

    /**
     * Converts one storage-format line into a task.
     *
     * @param line one line from the data file
     * @return the parsed task, or {@code null} when the line is malformed
     */
    private Task parseTask(String line) {
        String[] parts = line.split("\\|", -1);
        for (int index = 0; index < parts.length; index++) {
            parts[index] = parts[index].trim();
        }
        if (parts.length < 3 || !isValidStatus(parts[1]) || parts[2].isEmpty()) {
            printSkippedLineWarning(line);
            return null;
        }

        try {
            Task task = createTask(parts, line);
            if (task == null) {
                return null;
            }
            if (parts[1].equals(Task.DONE_MARKER)) {
                task.markAsDone();
            }
            return task;
        } catch (DateTimeParseException exception) {
            printSkippedLineWarning(line);
            return null;
        }
    }

    /**
     * Creates a task from split storage fields.
     *
     * @param parts the split and trimmed storage fields
     * @param originalLine the original line, used when printing warnings
     * @return the reconstructed task, or {@code null} when its structure is invalid
     */
    private Task createTask(String[] parts, String originalLine) {
        assert parts.length >= 3 : "caller should have already validated the minimum field count";
        switch (parts[0]) {
            case TODO_TYPE:
                if (parts.length != 3) {
                    printSkippedLineWarning(originalLine);
                    return null;
                }
                return new Todo(parts[2]);
            case DEADLINE_TYPE:
                if (parts.length != 4 || parts[3].isEmpty()) {
                    printSkippedLineWarning(originalLine);
                    return null;
                }
                return new Deadline(parts[2], parseStoredDateTime(parts[3]));
            case EVENT_TYPE: {
                if (parts.length != 5 || parts[3].isEmpty() || parts[4].isEmpty()) {
                    printSkippedLineWarning(originalLine);
                    return null;
                }
                LocalDateTime from = parseStoredDateTime(parts[3]);
                LocalDateTime to = parseStoredDateTime(parts[4]);
                if (!to.isAfter(from)) {
                    printSkippedLineWarning(originalLine);
                    return null;
                }
                return new Event(parts[2], from, to);
            }
            default:
                printSkippedLineWarning(originalLine);
                return null;
        }
    }

    /**
     * Parses a saved ISO-style date and time.
     *
     * @param storedDateTime the saved date and time text
     * @return the parsed date and time
     * @throws DateTimeParseException if the saved value is invalid
     */
    private LocalDateTime parseStoredDateTime(String storedDateTime)
            throws DateTimeParseException {
        return LocalDateTime.parse(storedDateTime, STORAGE_DATE_FORMAT);
    }

    /**
     * Checks whether a saved completion state is valid.
     *
     * @param status the status value from the storage file
     * @return true if the status is either 0 or 1
     */
    private boolean isValidStatus(String status) {
        return status.equals(Task.NOT_DONE_MARKER) || status.equals(Task.DONE_MARKER);
    }

    /**
     * Prints Peter's warning for an unreadable saved task line.
     *
     * @param line the malformed line that was skipped
     */
    private void printSkippedLineWarning(String line) {
        System.out.println("     Found a line tangled beyond recognition: \""
                + line + "\" - skipping it.");
    }
}
