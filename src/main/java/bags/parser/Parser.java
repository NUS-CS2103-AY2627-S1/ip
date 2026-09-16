package bags.parser;

import bags.exception.BagsException;
import bags.task.Deadline;
import bags.task.Event;
import bags.task.Task;
import bags.task.TaskType;
import bags.task.Todo;

/**
 * Interprets user commands and converts saved task records back into tasks.
 *
 */
public class Parser {

    /**
     * Determines the type of command represented by the user's input.
     *
     * @param input the command entered by the user
     * @return the corresponding command type
     */
    public Command parseCommand(String input) {
        assert input != null : "Command input must not be null";

        if (input.trim().isEmpty()) {
            return Command.EMPTY;
        }

        return Command.parseInput(input);
    }

    /**
     * Determines the task type specified at the beginning of a task command.
     *
     * @param input user command
     * @return the parsed task type, or {@code null} if unrecognized
     */
    public TaskType parseTaskType(String input) {
        assert input != null : "Task type input must not be null";

        return TaskType.parseInput(input);
    }

    /**
     * Creates a task object from one record loaded from the save file.
     *
     * @param taskString the saved task record to parse
     * @return the corresponding task object, or null if the record is invalid
     * @throws BagsException if the task input is invalid
     */
    public Task parseTask(String taskString) throws BagsException {
        assert taskString != null : "Saved task record must not be null";

        String[] parts = taskString.split("\\|");

        if (parts.length < 3) {
            return null;
        }

        TaskType taskType = TaskType.parseKey(parts[0].trim());
        String status = parts[1].trim();

        if (taskType == null) {
            return null;
        }

        if (taskType == TaskType.TODO) {
            return createTask(new Todo(parts[2].trim()), status);
        } else if (taskType == TaskType.DEADLINE && parts.length >= 4) {
            return createTask(new Deadline(parts[2].trim(), parts[3].trim()), status);
        } else if (taskType == TaskType.EVENT && parts.length >= 5) {
            return createTask(new Event(parts[2].trim(), parts[3].trim(), parts[4].trim()), status);
        }

        return null;
    }

    /**
     * Marks a task as done when its saved status indicates completion.
     *
     * @param task the task to update
     * @param status the saved task status
     * @return the updated task
     */
    private Task createTask(Task task, String status) {
        if (status.equals("[X]")) {
            task.markDone();
            assert task.isDone() : "Task should be marked as done";
        }

        return task;
    }
}
