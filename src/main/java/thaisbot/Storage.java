package thaisbot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

import thaisbot.command.Parser;
import thaisbot.task.Deadline;
import thaisbot.task.Event;
import thaisbot.task.Task;
import thaisbot.task.TaskList;
import thaisbot.task.TaskStatus;
import thaisbot.task.Todo;

/**
 * Responsible for loading and saving tasks to disk. The format is a simple pipe-separated
 * text format understood by the application.
 */
public class Storage {
    private static final String CORRUPTED_DATA_MESSAGE_PREFIX = "Saved task data is corrupted at line ";

    private final Path dataFile;
    private final Parser parser;

    /**
     * Construct Storage for a given file path.
     * @param filePath path to the tasks file
     * @param parser parser instance used to interpret stored date/time values
     */
    public Storage(String filePath, Parser parser) {
        assert filePath != null;
        assert parser != null;
        dataFile = Paths.get(filePath);
        this.parser = parser;
    }

    /**
     * Loads tasks from disk. Creates parent directories and file if they do not exist.
     * @return TaskList containing tasks loaded from disk
     * @throws ThaisBotException if an I/O error occurs
     */
    public TaskList load() throws ThaisBotException {
        try {
            Files.createDirectories(dataFile.getParent());
            if (!Files.exists(dataFile)) {
                Files.createFile(dataFile);
                return new TaskList();
            }

            List<String> lines = Files.readAllLines(dataFile);
            List<Task> tasks = new java.util.ArrayList<>();
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (!line.isEmpty()) {
                    tasks.add(parseTaskLine(line, i + 1));
                }
            }
            return new TaskList(tasks);
        } catch (ThaisBotException e) {
            Path backupFile = backUpDataFile();
            throw new ThaisBotException(e.getMessage() + " I made a backup at " + backupFile
                    + " and started with an empty list.");
        } catch (IOException e) {
            throw new ThaisBotException("I couldn't load tasks from disk.");
        }
    }

    /**
     * Saves the provided TaskList to disk, overwriting the previous contents.
     * @param tasks the tasks to save
     * @throws ThaisBotException if an I/O error occurs during write
     */
    public void save(TaskList tasks) throws ThaisBotException {
        try {
            Files.createDirectories(dataFile.getParent());
            List<String> lines = tasks.stream()
                    .map(Task::toFileString)
                    .collect(Collectors.toList());
            Files.write(dataFile, lines);
        } catch (IOException e) {
            throw new ThaisBotException("I couldn't save your tasks to disk.");
        }
    }

    /**
     * Parse a single stored task line and return the corresponding Task instance.
     * @param line the raw stored line
     * @param lineNumber line number (for error reporting)
     * @return the Task represented by the line
     * @throws ThaisBotException if the stored data is malformed
     */
    private Task parseTaskLine(String line, int lineNumber) throws ThaisBotException {
        String[] parts = line.split("\\s*\\|\\s*", -1);
        if (parts.length < 3) {
            throw corruptedTaskDataException(lineNumber);
        }

        String taskType = parts[0];
        String statusFlag = parts[1];
        String description = parts[2];
        Task task;

        if ("T".equals(taskType)) {
            task = new Todo(description);
        } else if ("D".equals(taskType)) {
            task = parseDeadlineTask(parts, description, lineNumber);
        } else if ("E".equals(taskType)) {
            task = parseEventTask(parts, description, lineNumber);
        } else {
            throw corruptedTaskDataException(lineNumber);
        }

        if ("1".equals(statusFlag)) {
            task.setStatus(TaskStatus.DONE);
        } else if ("0".equals(statusFlag)) {
            task.setStatus(TaskStatus.NOT_DONE);
        } else {
            throw corruptedTaskDataException(lineNumber);
        }

        task.addTags(parseStoredTags(parts, getTagStartIndex(taskType), lineNumber));

        return task;
    }

    /**
     * Parse a stored deadline task.
     */
    private Task parseDeadlineTask(String[] parts, String description, int lineNumber)
            throws ThaisBotException {
        if (parts.length < 4) {
            throw corruptedTaskDataException(lineNumber);
        }

        Parser.ParsedDateTime by;
        if (parts.length >= 5) {
            by = parseStoredDateTimeWithFlag(parts[3], parts[4], lineNumber);
        } else {
            by = parser.parseDateTime(parts[3],
                    corruptedTaskDataMessage(lineNumber));
        }
        return new Deadline(description, by.getValue(), by.hasTime());
    }

    /**
     * Parse a stored event task.
     */
    private Task parseEventTask(String[] parts, String description, int lineNumber)
            throws ThaisBotException {
        if (parts.length < 5) {
            throw corruptedTaskDataException(lineNumber);
        }

        Parser.ParsedDateTime from;
        Parser.ParsedDateTime to;
        if (parts.length >= 7) {
            from = parseStoredDateTimeWithFlag(parts[3], parts[4], lineNumber);
            to = parseStoredDateTimeWithFlag(parts[5], parts[6], lineNumber);
        } else {
            from = parser.parseDateTime(parts[3],
                    corruptedTaskDataMessage(lineNumber));
            to = parser.parseDateTime(parts[4],
                    corruptedTaskDataMessage(lineNumber));
        }
        return new Event(description, from.getValue(), from.hasTime(), to.getValue(), to.hasTime());
    }

    /**
     * Parse a stored LocalDateTime string and an accompanying flag indicating whether the
     * original representation included a time part.
     */
    private Parser.ParsedDateTime parseStoredDateTimeWithFlag(String value, String flag,
                                                              int lineNumber)
            throws ThaisBotException {
        try {
            LocalDateTime parsed = LocalDateTime.parse(value);
            if ("1".equals(flag)) {
                return new Parser.ParsedDateTime(parsed, true);
            }
            if ("0".equals(flag)) {
                return new Parser.ParsedDateTime(parsed, false);
            }
        } catch (DateTimeParseException e) {
            // Fall through to the uniform error below.
        }
        throw corruptedTaskDataException(lineNumber);
    }

    /**
     * Copies the data file to a backup file, so that unreadable tasks are not lost when
     * the (empty) task list is saved over the data file later.
     * @return path of the backup file
     * @throws ThaisBotException if the backup cannot be written
     */
    private Path backUpDataFile() throws ThaisBotException {
        Path backupFile = Paths.get(dataFile + ".bak");
        try {
            Files.copy(dataFile, backupFile, StandardCopyOption.REPLACE_EXISTING);
            return backupFile;
        } catch (IOException e) {
            throw new ThaisBotException("Saved task data is corrupted and I couldn't back it up.");
        }
    }

    private ThaisBotException corruptedTaskDataException(int lineNumber) {
        return new ThaisBotException(corruptedTaskDataMessage(lineNumber));
    }

    private String corruptedTaskDataMessage(int lineNumber) {
        return CORRUPTED_DATA_MESSAGE_PREFIX + lineNumber + ".";
    }

    private int getTagStartIndex(String taskType) {
        if ("T".equals(taskType)) {
            return 3;
        }
        if ("D".equals(taskType)) {
            return 5;
        }
        return 7;
    }

    private List<String> parseStoredTags(String[] parts, int startIndex, int lineNumber)
            throws ThaisBotException {
        List<String> tags = new java.util.ArrayList<>();
        for (int i = startIndex; i < parts.length; i++) {
            String part = parts[i].trim();
            if (!part.startsWith("#") || part.length() == 1) {
                throw corruptedTaskDataException(lineNumber);
            }
            String tag = part.substring(1);
            if (!tag.matches("[A-Za-z0-9]+")) {
                throw corruptedTaskDataException(lineNumber);
            }
            if (!tags.contains(tag)) {
                tags.add(tag);
            }
        }
        return tags;
    }
}
