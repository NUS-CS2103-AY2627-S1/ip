package chatbot;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.stream.Stream;

/**
 * Persists the task list to a local file and loads it when the chatbot starts.
 */
public class Storage {
    private static final String TODO_INDICATOR = "T";
    private static final String DEADLINE_INDICATOR = "D";
    private static final String EVENT_INDICATOR = "E";

    private static final String COMPLETED_INDICATOR = "[X]";
    private static final String INCOMPLETE_INDICATOR = "[ ]";

    private static final String FIELD_DELIMITER = "\\|";
    private static final String SERIALIZED_FIELD_SEPARATOR = "|";

    private final Path filePath;

    /**
     * Creates a Storage object with a specified filePath of file used to store data of tasks.
     * @param filePath Path of the file used to store task data.
     */
    public Storage(String filePath) {
        this.filePath = Paths.get(filePath);
    }

    /**
     * Syncs the file from harddisk into the taskList.
     *
     * @return TaskList that is updated with tasks from the file.
     */
    public TaskList load() {
        TaskList taskList = new TaskList();
        if (Files.exists(filePath)) {
            try (Stream<String> allLines = Files.lines(filePath)) {
                convertLinesToTasks(allLines, taskList);
            } catch (IOException e) {
                throw new IllegalStateException("Yo broski I was unable to load tasks from " + filePath, e);
            }
        }
        return taskList;
    }

    /**
     * Converts serialized task records into tasks and adds them to the task list.
     *
     * @param allLines Stream containing serialized task records.
     * @param taskList Task list to which converted tasks are added.
     */
    public void convertLinesToTasks(Stream<String> allLines, TaskList taskList) {
        Iterator<String> lines = allLines.iterator();
        int lineNumber = 0;
        while (lines.hasNext()) {
            String line = lines.next();
            String[] parts = line.split(FIELD_DELIMITER, -1);
            if (hasInvalidRecordFormat(parts)) {
                throw new InvalidInputException("Invalid saved task record @ line " + lineNumber + ": " + line);
            }
            String commandIndicator = parts[0];
            String completionIndicator = parts[1];
            int partsLength = parts.length;

            if (isTodoSavedRecord(commandIndicator, partsLength)) {
                taskList.add(new Todo(parts[2]));
            } else if (isDeadlineSavedRecord(commandIndicator, partsLength)) {
                taskList.add(new Deadline(parts[2], parts[3]));
            } else if (isEventSavedRecord(commandIndicator, partsLength)) {
                taskList.add(new Event(parts[2], parts[3], parts[4]));
            } else {
                throw new InvalidInputException("Invalid saved task record @ line " + lineNumber + ": " + line);
            }

            if (completionIndicator.equals(COMPLETED_INDICATOR)) {
                taskList.get(taskList.size() - 1).markComplete();
            }
            lineNumber++;
        }
    }

    /**
     * Checks whether a serialized record has a valid completion field.
     *
     * @param parts Fields obtained from a serialized record.
     * @return Whether the record has an invalid format.
     */
    private boolean hasInvalidRecordFormat(String[] parts) {
        int partsLength = parts.length;
        if (partsLength < 2) {
            return true;
        } else {
            String completionIndicator = parts[1];
            return (!completionIndicator.equals(INCOMPLETE_INDICATOR)
                && !completionIndicator.equals(COMPLETED_INDICATOR));
        }
    }

    /**
     * Checks whether a serialized record represents a todo task.
     *
     * @param commandIndicator Record type indicator.
     * @param partsLength Number of fields in the record.
     * @return Whether the record is a valid todo record.
     */
    private boolean isTodoSavedRecord(String commandIndicator, int partsLength) {
        return (commandIndicator.equals(TODO_INDICATOR) && partsLength == 3);
    }

    /**
     * Checks whether a serialized record represents a deadline task.
     *
     * @param commandIndicator Record type indicator.
     * @param partsLength Number of fields in the record.
     * @return Whether the record is a valid deadline record.
     */
    private boolean isDeadlineSavedRecord(String commandIndicator, int partsLength) {
        return (commandIndicator.equals(DEADLINE_INDICATOR) && partsLength == 4);
    }

    /**
     * Checks whether a serialized record represents an event task.
     *
     * @param commandIndicator Record type indicator.
     * @param partsLength Number of fields in the record.
     * @return Whether the record is a valid event record.
     */
    private boolean isEventSavedRecord(String commandIndicator, int partsLength) {
        return (commandIndicator.equals(EVENT_INDICATOR) && partsLength == 5);
    }

    /**
     * Syncs from the TaskList into a file. Creates a file if there is file with given filepath yet.
     *
     * @param taskList The created tasklist.
     */
    public void save(TaskList taskList) {
        try (FileWriter writer = new FileWriter(filePath.toFile(), false)) {
            for (Task task : taskList) {
                writer.write(formatTask(task));
                writer.write(System.lineSeparator());
            }
        } catch (IOException e) {
            throw new IllegalStateException("Yo broski I was unable to save tasks to " + filePath, e);
        }
    }

    /**
     * Serializes a task according to its concrete task type.
     *
     * @param task Task to serialize.
     * @return Serialized task record.
     */
    private String formatTask(Task task) {
        if (task instanceof Todo) {
            return formatTodo((Todo) task);
        }

        if (task instanceof Event) {
            return formatEvent((Event) task);
        }

        assert task instanceof Deadline : "Task must be a Todo, Event, or Deadline";
        return formatDeadline((Deadline) task);
    }

    /**
     * Serializes a todo task into the storage-file format.
     *
     * @param todo Todo task to serialize.
     * @return Serialized todo record.
     */
    private String formatTodo(Todo todo) {
        return TODO_INDICATOR + SERIALIZED_FIELD_SEPARATOR + todo.getCompletionStatus()
            + SERIALIZED_FIELD_SEPARATOR + todo.getDescription();
    }

    /**
     * Serializes an event task into the storage-file format.
     *
     * @param event Event task to serialize.
     * @return Serialized event record.
     */
    private String formatEvent(Event event) {
        return EVENT_INDICATOR + SERIALIZED_FIELD_SEPARATOR + event.getCompletionStatus()
            + SERIALIZED_FIELD_SEPARATOR + event.getDescription()
            + SERIALIZED_FIELD_SEPARATOR + event.getStart().format(DateTimeFormats.STORAGE)
            + SERIALIZED_FIELD_SEPARATOR + event.getEnd().format(DateTimeFormats.STORAGE);
    }

    /**
     * Serializes a deadline task into the storage-file format.
     *
     * @param deadline Deadline task to serialize.
     * @return Serialized deadline record.
     */
    private String formatDeadline(Deadline deadline) {
        return DEADLINE_INDICATOR + SERIALIZED_FIELD_SEPARATOR + deadline.getCompletionStatus()
            + SERIALIZED_FIELD_SEPARATOR + deadline.getDescription()
            + SERIALIZED_FIELD_SEPARATOR + deadline.getDeadline().format(DateTimeFormats.STORAGE);
    }

}
