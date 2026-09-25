package shan.storage;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import shan.datetime.DateTimeParser;
import shan.exception.DataFileException;
import shan.exception.InvalidArgumentException;
import shan.task.Deadline;
import shan.task.Event;
import shan.task.Task;
import shan.task.ToDo;

/**
 * Loads tasks from and saves tasks to a data file.
 */
public class Storage {
    private static final int TYPE_FIELD_INDEX = 0;
    private static final int STATUS_FIELD_INDEX = 1;
    private static final int DESCRIPTION_FIELD_INDEX = 2;
    private static final int FIRST_DATE_TIME_FIELD_INDEX = 3;
    private static final int SECOND_DATE_TIME_FIELD_INDEX = 4;

    private static final String TODO_TYPE = "T";
    private static final String DEADLINE_TYPE = "D";
    private static final String EVENT_TYPE = "E";
    private static final String INCOMPLETE_STATUS = "0";
    private static final String COMPLETE_STATUS = "1";

    private final Path dataFile;

    /**
     * Constructs storage that uses the specified data file.
     *
     * @param dataFile Relative path to the data file.
     */
    public Storage(Path dataFile) {
        this.dataFile = dataFile;
    }

    /**
     * Loads valid tasks from the data file when it exists.
     *
     * @return Valid tasks and the number of invalid nonblank entries skipped.
     * @throws DataFileException If the data file cannot be read.
     */
    public LoadResult load() throws DataFileException {
        ArrayList<Task> loadedTasks = new ArrayList<>();
        try {
            if (Files.notExists(this.dataFile)) {
                return new LoadResult(loadedTasks, 0);
            }
            if (!Files.isRegularFile(this.dataFile)) {
                throw new DataFileException(String.format(
                        "I couldn't read %s. Starting with an empty task list.", getFilePath()));
            }

            int skippedTasks = 0;
            for (String line : Files.readAllLines(this.dataFile)) {
                if (line.isBlank()) {
                    continue;
                }

                Optional<Task> task = parseSavedTask(line);
                if (task.isEmpty()) {
                    skippedTasks++;
                    continue;
                }
                loadedTasks.add(task.get());
            }
            return new LoadResult(loadedTasks, skippedTasks);
        } catch (IOException | SecurityException exception) {
            throw new DataFileException(String.format(
                    "I couldn't read %s. Starting with an empty task list.", getFilePath()));
        }
    }

    /**
     * Saves the supplied tasks to the data file.
     *
     * @param tasks Tasks to save.
     * @throws DataFileException If the data directory or file cannot be written.
     */
    public void save(List<Task> tasks) throws DataFileException {
        Path temporaryFile = null;
        try {
            Path parentDirectory = this.dataFile.getParent();
            if (parentDirectory != null) {
                Files.createDirectories(parentDirectory);
            }

            ArrayList<String> lines = new ArrayList<>();
            for (Task task : tasks) {
                lines.add(task.toFileString());
            }
            Path temporaryDirectory = parentDirectory == null ? Path.of(".") : parentDirectory;
            temporaryFile = Files.createTempFile(temporaryDirectory, ".shan-", ".tmp");
            Files.write(temporaryFile, lines);
            replaceDataFile(temporaryFile);
        } catch (IOException | SecurityException exception) {
            throw new DataFileException(
                    String.format("I couldn't save your tasks to %s.", getFilePath()));
        } finally {
            if (temporaryFile != null) {
                try {
                    Files.deleteIfExists(temporaryFile);
                } catch (IOException | SecurityException ignored) {
                    // A failed cleanup must not hide the original save result.
                }
            }
        }
    }

    /**
     * Replaces the data file atomically when the file system supports it.
     *
     * @param temporaryFile Fully written temporary file.
     * @throws IOException If the temporary file cannot replace the data file.
     */
    private void replaceDataFile(Path temporaryFile) throws IOException {
        try {
            Files.move(temporaryFile, this.dataFile,
                    StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(temporaryFile, this.dataFile, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Returns the data-file path using separators suitable for user messages.
     *
     * @return Display representation of the data-file path.
     */
    public String getFilePath() {
        return this.dataFile.toString().replace('\\', '/');
    }

    /**
     * Converts one valid save-file entry into a task.
     *
     * @param line Save-file entry.
     * @return Parsed task, or an empty value when the entry is invalid.
     */
    private Optional<Task> parseSavedTask(String line) {
        String[] fields = line.trim().split("\\s*\\|\\s*", -1);
        if (!hasValidHeader(fields) || !hasExpectedFieldCount(fields) || hasInvalidTaskField(fields)) {
            return Optional.empty();
        }

        Task task;
        try {
            task = switch (fields[TYPE_FIELD_INDEX]) {
                case TODO_TYPE -> new ToDo(fields[DESCRIPTION_FIELD_INDEX]);
                case DEADLINE_TYPE -> new Deadline(fields[DESCRIPTION_FIELD_INDEX],
                        DateTimeParser.parse(fields[FIRST_DATE_TIME_FIELD_INDEX]));
                case EVENT_TYPE -> parseSavedEvent(fields);
                default -> throw new AssertionError("Task type was already validated");
            };
        } catch (InvalidArgumentException exception) {
            return Optional.empty();
        }
        if (fields[STATUS_FIELD_INDEX].equals(COMPLETE_STATUS)) {
            task.markDone();
        }
        return Optional.of(task);
    }

    private boolean hasValidHeader(String[] fields) {
        return fields.length > STATUS_FIELD_INDEX
                && (fields[STATUS_FIELD_INDEX].equals(INCOMPLETE_STATUS)
                        || fields[STATUS_FIELD_INDEX].equals(COMPLETE_STATUS));
    }

    private boolean hasExpectedFieldCount(String[] fields) {
        int expectedFieldCount = switch (fields[TYPE_FIELD_INDEX]) {
            case TODO_TYPE -> 3;
            case DEADLINE_TYPE -> 4;
            case EVENT_TYPE -> 5;
            default -> -1;
        };
        return fields.length == expectedFieldCount;
    }

    private boolean hasInvalidTaskField(String[] fields) {
        for (int i = DESCRIPTION_FIELD_INDEX; i < fields.length; i++) {
            if (fields[i].isBlank()
                    || fields[i].codePoints().anyMatch(Character::isISOControl)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Parses an Event from fields whose type, status, and field count are valid.
     *
     * @param fields Serialized Event fields.
     * @return Parsed Event.
     * @throws InvalidArgumentException If a date-time is invalid or the end is not
     *                                  after the start.
     */
    private Event parseSavedEvent(String[] fields) throws InvalidArgumentException {
        LocalDateTime startDateTime = DateTimeParser.parse(fields[FIRST_DATE_TIME_FIELD_INDEX]);
        LocalDateTime endDateTime = DateTimeParser.parse(fields[SECOND_DATE_TIME_FIELD_INDEX]);
        if (!endDateTime.isAfter(startDateTime)) {
            throw new InvalidArgumentException("The event end must be after its start.");
        }
        return new Event(fields[DESCRIPTION_FIELD_INDEX], startDateTime, endDateTime);
    }

    /**
     * Contains the valid tasks and corruption count produced by one load.
     *
     * @param tasks        Valid tasks loaded from the file.
     * @param skippedTasks Number of invalid nonblank entries skipped.
     */
    public record LoadResult(List<Task> tasks, int skippedTasks) {
        /**
         * Constructs an immutable load result.
         */
        public LoadResult(List<Task> tasks, int skippedTasks) {
            this.tasks = List.copyOf(tasks);
            this.skippedTasks = skippedTasks;
        }
    }
}
