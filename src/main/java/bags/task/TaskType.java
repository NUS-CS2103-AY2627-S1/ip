package bags.task;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the different types of tasks supported by the Bags application.
 */
public enum TaskType {

    TODO("todo", "T"),

    DEADLINE("deadline", "D"),

    EVENT("event", "E");

    private static final Map<String, TaskType> taskTypes = createTaskTypes();
    private final String[] keys;

    TaskType(String... keys) {
        this.keys = keys;
    }

    private static Map<String, TaskType> createTaskTypes() {
        Map<String, TaskType> taskTypes = new HashMap<>();
        for (TaskType taskType : TaskType.values()) {
            for (String key : taskType.keys) {
                taskTypes.put(key, taskType);
            }
        }
        return taskTypes;
    }

    /**
     * Returns the task type specified at the beginning of the input.
     *
     * @param input the task input.
     * @return the matching task type, or {@code null} if none is found.
     */
    public static TaskType parseInput(String input) {
        String taskTypeWord = input.split(" ", 2)[0];
        return parseKey(taskTypeWord);
    }

    /**
     * Returns the task type associated with the supplied input or storage key.
     *
     * @param key the task command or storage key.
     * @return the matching task type, or {@code null} if none is found.
     */
    public static TaskType parseKey(String key) {
        return taskTypes.get(key);
    }

}
