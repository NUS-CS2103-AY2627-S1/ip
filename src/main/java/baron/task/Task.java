// javadocs created by AI

package baron.task;

import java.time.LocalDateTime;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import baron.exception.BaronException;
import baron.exception.InvalidTaskJsonException;

/**
 * Represents a general task in the Baron application.
 * Subclasses can specify additional details such as deadlines or event ranges.
 */
public abstract class Task {
    /** The human-readable task description. */
    private String description;

    /** Indicates whether the task has been completed. */
    private boolean isDone;

    /**
     * Creates a new task with the specified description.
     *
     * @param description the description of the task.
     */
    public Task(String description) {
        if (description == null || description.isBlank()
                || description.contains("\n") || description.contains("\r")) {
            throw new IllegalArgumentException("A task needs a worthy description before it can enter the ledger.");
        }
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the task description.
     *
     * @return the task description.
     */
    public String getName() {
        return description;
    }

    /**
     * Checks whether the task is marked as done.
     *
     * @return true if the task is complete; false otherwise.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Marks the task as complete.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks the task as not complete.
     */
    public void unmark() {
        isDone = false;
    }

    /**
     * Returns any additional task-specific information to display.
     *
     * @return an empty string by default.
     */
    public String extraInfo() {
        return "";
    }

    /**
     * Returns the symbol representing the task type.
     *
     * @return a type label such as "T" or "D".
     */
    public String getTypeSymbol() {
        return " ";
    }

    /**
     * Returns the string representation of the task for display in the command line.
     *
     * @return the formatted task status and description.
     */
    public String toString() {
        return "[" + getTypeSymbol() + "]" + (isDone ? "[X] " : "[ ] ") + description + extraInfo();
    }

    /**
     * Returns the Json representation of the task for saving to a file.
     *
     * @return the formatted task status and description for file storage.
     */
    public String serialize() {
        JsonObject taskData = new JsonObject();
        taskData.addProperty("type", getTypeSymbol());
        taskData.addProperty("done", isDone);
        taskData.addProperty("description", description);
        return new Gson().toJson(taskData);
    }

    /**
     * Returns the deadline or end time of the task, if applicable.
     * For tasks without a specific time constraint, this method returns null.
     *
     * @return the deadline or end time, or null if not applicable.
     */
    public LocalDateTime getDeadline() {
        return null;
    }

    /**
     * Creates a Task object from its serialized Json representation.
     * @param serializedTask the serialized task Json
     * @return the deserialized Task object
     * @throws BaronException if the serialized task format is invalid
     */
    public static Task deserialize(String serializedTask) throws BaronException {
        if (serializedTask == null || serializedTask.isBlank()) {
            throw new InvalidTaskJsonException();
        }
        try {
            JsonObject taskData = JsonParser.parseString(serializedTask).getAsJsonObject();
            String type = requiredString(taskData, "type");
            String description = requiredString(taskData, "description");
            if (description.contains("\n") || description.contains("\r")
                    || !taskData.has("done") || !taskData.get("done").isJsonPrimitive()) {
                throw new InvalidTaskJsonException();
            }
            boolean isDone = taskData.get("done").getAsBoolean();
            Task task = createTask(type, description, taskData);
            if (isDone) {
                task.markAsDone();
            }
            return task;
        } catch (BaronException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new InvalidTaskJsonException();
        }
    }

    /**
     * Creates the concrete task represented by a validated JSON object.
     *
     * @param type the type of the task
     * @param description the description of the task
     * @param taskData the JSON object containing the task data
     * @return the created task
     * @throws BaronException if the task cannot be created
     */
    private static Task createTask(String type, String description, JsonObject taskData) throws BaronException {
        switch (type) {
            case "T":
                return new Todo(description);
            case "D":
                return new Deadline(description, requiredString(taskData, "dueDate"));
            case "E":
                return new Event(description, requiredString(taskData, "from"),
                        requiredString(taskData, "to"));
            default:
                throw new BaronException("The ledger contains an unknown task type: " + type + ".");
        }
    }

    /** Returns a required non-blank JSON string field. */
    private static String requiredString(JsonObject taskData, String field) throws BaronException {
        if (!taskData.has(field) || !taskData.get(field).isJsonPrimitive()
                || taskData.get(field).getAsString().isBlank()) {
            throw new InvalidTaskJsonException();
        }
        return taskData.get(field).getAsString();
    }
}
