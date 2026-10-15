package esther.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import esther.exception.EstherException;
import esther.task.Deadline;
import esther.task.Event;
import esther.task.Task;
import esther.task.TaskList;
import esther.task.Todo;

/**
 * Loads tasks from and saves tasks to a data file.
 */
public class Storage {
    private static final int TYPE_FIELD_INDEX = 0;
    private static final int STATUS_FIELD_INDEX = 1;
    private static final int DESCRIPTION_FIELD_INDEX = 2;
    private static final int DEADLINE_DATE_FIELD_INDEX = 3;
    private static final int EVENT_START_FIELD_INDEX = 3;
    private static final int EVENT_END_FIELD_INDEX = 4;

    private final Path filePath;

    /**
     * Creates storage that uses the specified data file.
     *
     * @param filePath Path of the data file.
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads tasks from the data file.
     *
     * @return Tasks reconstructed from the data file.
     * @throws IOException If the data file cannot be read.
     * @throws EstherException If the data file contains invalid data.
     */
    public TaskList load() throws IOException, EstherException {
        TaskList tasks = new TaskList();

        if (!Files.exists(filePath)) {
            return tasks;
        }

        List<String> taskDataLines = Files.readAllLines(
                filePath, StandardCharsets.UTF_8);

        for (int i = 0; i < taskDataLines.size(); i++) {
            String taskData = taskDataLines.get(i);
            if (!taskData.isBlank()) {
                try {
                    tasks.add(parseTaskData(taskData));
                } catch (EstherException exception) {
                    throw new EstherException(String.format(
                            "Unable to load task on line %d: %s",
                            i + 1, exception.getMessage()));
                }
            }
        }

        return tasks;
    }

    /**
     * Saves all tasks to the data file.
     *
     * @param tasks Tasks to save.
     * @throws IOException If the tasks cannot be saved.
     */
    public void save(TaskList tasks) throws IOException {
        Path parentDirectory = filePath.getParent();
        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }

        ArrayList<String> taskData = new ArrayList<>();
        for (Task task : tasks.asList()) {
            taskData.add(task.toDataString());
        }

        Files.write(filePath, taskData, StandardCharsets.UTF_8);
    }

    /**
     * Reconstructs one task from its stored representation.
     *
     * @param taskData Stored representation of one task.
     * @return Reconstructed task.
     * @throws EstherException If the stored representation is invalid.
     */
    private Task parseTaskData(String taskData) throws EstherException {
        String[] fields = splitTaskData(taskData);

        if (fields.length < DESCRIPTION_FIELD_INDEX + 1) {
            throw new EstherException(
                    "The data file contains invalid task data.");
        }

        String description = unescapeDataField(fields[DESCRIPTION_FIELD_INDEX]);
        if (description.isBlank()) {
            throw new EstherException(
                    "The data file contains invalid task data.");
        }

        Task task;

        switch (fields[TYPE_FIELD_INDEX]) {
            case "T":
                if (fields.length != DESCRIPTION_FIELD_INDEX + 1) {
                    throw new EstherException(
                            "The data file contains an invalid todo.");
                }
                task = new Todo(description);
                break;
            case "D":
                if (fields.length != DEADLINE_DATE_FIELD_INDEX + 1
                        || fields[DEADLINE_DATE_FIELD_INDEX].isBlank()) {
                    throw new EstherException(
                            "The data file contains an invalid deadline.");
                }
                try {
                    LocalDate dueDate = LocalDate.parse(fields[DEADLINE_DATE_FIELD_INDEX]);
                    task = new Deadline(description, dueDate);
                } catch (DateTimeParseException exception) {
                    throw new EstherException(
                            "The data file contains an invalid deadline date.");
                }
                break;
            case "E":
                if (fields.length != EVENT_END_FIELD_INDEX + 1
                        || fields[EVENT_START_FIELD_INDEX].isBlank()
                        || fields[EVENT_END_FIELD_INDEX].isBlank()) {
                    throw new EstherException(
                            "The data file contains an invalid event.");
                }
                task = new Event(
                        description,
                        unescapeDataField(fields[EVENT_START_FIELD_INDEX]),
                        unescapeDataField(fields[EVENT_END_FIELD_INDEX]));
                break;
            default:
                throw new EstherException(
                        "The data file contains an unknown task type.");
        }

        if (fields[STATUS_FIELD_INDEX].equals("1")) {
            task.markAsDone();
        } else if (!fields[STATUS_FIELD_INDEX].equals("0")) {
            throw new EstherException(
                    "The data file contains an invalid task status.");
        }

        return task;
    }

    /**
     * Splits stored task data while preserving escaped delimiter characters.
     *
     * @param taskData Stored representation of one task.
     * @return Fields extracted from the stored representation.
     * @throws EstherException If an escape sequence is incomplete.
     */
    private String[] splitTaskData(String taskData) throws EstherException {
        ArrayList<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();

        for (int i = 0; i < taskData.length(); i++) {
            char character = taskData.charAt(i);

            if (character == '\\') {
                if (i + 1 >= taskData.length()) {
                    throw new EstherException(
                            "The data file contains an invalid escape sequence.");
                }
                currentField.append(character);
                currentField.append(taskData.charAt(++i));
            } else if (character == '|'
                    && i > 0
                    && taskData.charAt(i - 1) == ' '
                    && i + 1 < taskData.length()
                    && taskData.charAt(i + 1) == ' ') {
                currentField.setLength(currentField.length() - 1);
                fields.add(currentField.toString());
                currentField.setLength(0);
                i++;
            } else {
                currentField.append(character);
            }
        }

        fields.add(currentField.toString());
        return fields.toArray(new String[0]);
    }

    /**
     * Restores special characters in an escaped data field.
     *
     * @param field Escaped field value.
     * @return Unescaped field value.
     * @throws EstherException If the field contains an unsupported escape.
     */
    private String unescapeDataField(String field) throws EstherException {
        StringBuilder unescapedField = new StringBuilder();

        for (int i = 0; i < field.length(); i++) {
            char character = field.charAt(i);
            if (character != '\\') {
                unescapedField.append(character);
                continue;
            }

            if (i + 1 >= field.length()) {
                throw new EstherException(
                        "The data file contains an invalid escape sequence.");
            }

            char escapedCharacter = field.charAt(++i);
            if (escapedCharacter != '\\' && escapedCharacter != '|') {
                throw new EstherException(
                        "The data file contains an invalid escape sequence.");
            }
            unescapedField.append(escapedCharacter);
        }

        return unescapedField.toString();
    }
}
