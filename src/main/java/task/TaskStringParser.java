package task;

import java.util.ArrayList;
import java.util.List;

import exception.FriedbergException;

/**
 * Responsible for converting a given list of Tasks into a serializable string.
 * Will also be able to convert a string into a list of Tasks.
 */
public class TaskStringParser {
    private TaskStringParser() {
        // Prevent instantiation.
    }

    /**
     * Serializes the given tasks into the application's storage format.
     *
     * @param tasks tasks to serialize
     * @return serialized tasks separated by line breaks
     */
    public static String serializeTasks(List<Task> tasks) {
        StringBuilder data = new StringBuilder();
        for (Task task : tasks) {
            data.append(task.serialize()).append("\n");
        }
        return data.toString();
    }

    /**
     * Deserializes the output from a serializeTasks call back into an ArrayList of tasks.
     *
     * @param dataString String as formatted by the serializeTasks function call
     * @return ArrayList of the original serialized Tasks
     */
    public static ArrayList<Task> deserializeTasks(String dataString) throws FriedbergException {
        ArrayList<Task> tasks = new ArrayList<Task>();
        String[] taskStrings = dataString.equals("") ? new String[0] : dataString.split("\n");
        for (String taskString : taskStrings) {
            tasks.add(Task.deserialize(taskString));
        }
        return tasks;
    }
}
