package tardt.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tardt.exception.TardTException;
import tardt.priority.Priority;
import tardt.task.Deadline;
import tardt.task.Event;
import tardt.task.Task;
import tardt.task.ToDo;

/**
 * Constructor for Storage object
 */
public class Storage {
    private final Path saveFile;

    public Storage(Path saveFile) {
        this.saveFile = saveFile;
    }

    /**
     * Saves the current task list to the data file.
     */
    public void save(List<Task> taskList) throws TardTException {
        List<String> savedTasks = new ArrayList<>();
        for (Task task : taskList) {
            savedTasks.add(formatTaskForSaving(task));
        }
        try {
            Path parentDirectory = saveFile.getParent();
            if (parentDirectory != null) {
                Files.createDirectories(parentDirectory);
            }
            Files.writeString(saveFile, String.join(System.lineSeparator(), savedTasks));
        } catch (IOException exception) {
            throw new TardTException("Unable to save tasks: " + exception.getMessage());
        }
    }

    /**
     * Formats a task object into a String suitable for saving in the text file.
     *
     * @param task A Task object
     * @return The formatted string.
     */
    private String formatTaskForSaving(Task task) {
        String status = task.isDone() ? "1" : "0";
        String taskData;
        if (task instanceof Deadline deadline) {
            taskData = "D | " + status + " | " + escapeField(deadline.getDescription()) + " | "
                    + escapeField(deadline.getByRaw());
        } else if (task instanceof Event event) {
            taskData = "E | " + status + " | " + escapeField(event.getDescription()) + " | "
                    + escapeField(event.getFromRaw()) + " | " + escapeField(event.getToRaw());
        } else {
            taskData = "T | " + status + " | " + escapeField(task.getDescription());
        }
        return appendPriority(taskData, task.getPriority());
    }

    /**
     * Escapes values that could otherwise be mistaken for save-file syntax.
     * Escaping backslashes first makes the transformation reversible.
     *
     * @param field value to store in one save-file field
     * @return the escaped value
     */
    private String escapeField(String field) {
        return field.replace("\\", "\\\\").replace("|", "\\|");
    }

    /**
     * Adds the optional priority field used for non-default priorities.
     *
     * @param taskData serialized task fields excluding priority
     * @param priority task priority
     * @return the complete serialized task line
     */
    private String appendPriority(String taskData, Priority priority) {
        return priority == Priority.LOW ? taskData : taskData + " | " + priority.getKeyword();
    }

    /**
     * Loads saved tasks from the data file, or an empty list if none exists.
     *
     * @return A List of Tasks read from the data file.
     */
    public List<Task> load() throws TardTException {
        List<Task> taskList = new ArrayList<>();
        if (!Files.exists(saveFile)) {
            return taskList;
        }
        try {
            for (String savedTask : Files.readAllLines(saveFile)) {
                Task task = createTaskFromSavedLine(savedTask);
                if (task != null) {
                    taskList.add(task);
                }
            }
        } catch (IOException exception) {
            throw new TardTException("Unable to load tasks: " + exception.getMessage());
        }
        return taskList;
    }

    /**
     * Inverse method of formatTaskForSaving.
     * Creates a Task object from the String line read from the save file.
     *
     * @param savedTask A String line read from the save file.
     * @return the parsed task, or null when the saved line is invalid
     */
    private Task createTaskFromSavedLine(String savedTask) {
        String[] parts = splitSavedTask(savedTask);
        if (parts.length < 3 || !isValidStatus(parts[1]) || parts[2].isBlank()) {
            return null;
        }
        Task task;
        try {
            int minimumFieldCount = getMinimumFieldCount(parts[0]);
            if (minimumFieldCount == 0) {
                return null;
            }
            Priority priority = Priority.LOW;
            int dataEnd = parts.length;
            Priority parsedPriority = parts.length > minimumFieldCount
                    ? Priority.fromKeyword(parts[parts.length - 1]) : null;
            if (parsedPriority != null) {
                priority = parsedPriority;
                dataEnd--;
            }
            if (dataEnd < minimumFieldCount) {
                return null;
            }
            switch (parts[0]) {
                case "T":
                    task = new ToDo(joinFields(parts, 2, dataEnd), priority);
                    break;
                case "D":
                    task = new Deadline(joinFields(parts, 2, dataEnd - 1), parts[dataEnd - 1], priority);
                    break;
                case "E":
                    task = new Event(joinFields(parts, 2, dataEnd - 2), parts[dataEnd - 2],
                            parts[dataEnd - 1], priority);
                    break;
                default:
                    return null;
            }
        } catch (TardTException exception) {
            return null;
        }
        if (parts[1].equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Returns the number of fields required before an optional priority field for each task type.
     *
     * @param taskType serialized task type
     * @return the minimum field count, or zero for an unknown task type
     */
    private int getMinimumFieldCount(String taskType) {
        return switch (taskType) {
            case "T" -> 3;
            case "D" -> 4;
            case "E" -> 5;
            default -> 0;
        };
    }

    /**
     * Rejoins description fragments from legacy files that used an unescaped pipe in a description.
     *
     * @param fields decoded save-file fields
     * @param start inclusive index of the first description field
     * @param endExclusive exclusive index immediately after the final description field
     * @return the reconstructed description
     */
    private String joinFields(String[] fields, int start, int endExclusive) {
        return String.join(" | ", Arrays.copyOfRange(fields, start, endExclusive));
    }

    /**
     * Splits a save-file line at unescaped pipe delimiters and restores escaped characters.
     * The trimming preserves compatibility with task files written before escaping was introduced.
     *
     * @param savedTask one serialized task line
     * @return the decoded fields in the task line
     */
    private String[] splitSavedTask(String savedTask) {
        List<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean isEscaped = false;

        for (int index = 0; index < savedTask.length(); index++) {
            char character = savedTask.charAt(index);
            if (isEscaped) {
                if (character == '|' || character == '\\') {
                    currentField.append(character);
                } else {
                    currentField.append('\\').append(character);
                }
                isEscaped = false;
            } else if (character == '\\') {
                isEscaped = true;
            } else if (character == '|') {
                fields.add(currentField.toString().trim());
                currentField.setLength(0);
            } else {
                currentField.append(character);
            }
        }

        if (isEscaped) {
            currentField.append('\\');
        }
        fields.add(currentField.toString().trim());
        return fields.toArray(String[]::new);
    }

    /**
     * Checks whether a serialized task status is one of the two supported values.
     *
     * @param status saved completion status
     * @return true for an incomplete or completed status
     */
    private boolean isValidStatus(String status) {
        return status.equals("0") || status.equals("1");
    }
}
