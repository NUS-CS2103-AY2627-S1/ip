package duchess.tasks.factories;

import duchess.tasks.ToDo;
import duchess.tasks.exceptions.TaskCreationException;

/** Creates {@link ToDo} tasks from user commands. */
class TodoFactory {
    private static final String TASK_TYPE = "todo";

    private TodoFactory() {
        // Utility class.
    }

    /**
     * Creates a to-do task from a command.
     *
     * @throws TaskCreationException if the description is empty
     */
    static ToDo create(String command) throws TaskCreationException {
        String description = TaskFactory.extractCommandSection(
                command, TASK_TYPE.length(), command.length());
        if (description.isEmpty()) {
            throw TaskCreationException.declareEmptyDescription(TASK_TYPE);
        }
        return new ToDo(description);
    }
}
