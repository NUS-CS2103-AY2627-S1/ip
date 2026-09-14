// javadocs created by AI

package baron.data;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import baron.exception.BaronException;
import baron.exception.FileException;
import baron.task.Task;

/**
 * Handles the persistence of tasks to and from a file.
 * This class provides methods to save the current list of tasks to a file
 */
public class TaskPersistence {
    // Code for this json loader is taken from my other java project and modified to fit this

    /** The file path where tasks are saved. */
    private static final File SAVE_FILE = new File("data", "tasks.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Saves the current list of tasks to the specified file.
     * Tasks are serialized as a JSON array of task objects.
     *
     * @param tasks the list of tasks to save
     * @throws BaronException if an error occurs while saving the tasks
     */
    public static void save(ArrayList<Task> tasks) throws BaronException {
        try {
            SAVE_FILE.getParentFile().mkdirs(); // Ensure the parent directory exists
            JsonArray savedTasks = new JsonArray();
            for (Task task : tasks) {
                savedTasks.add(JsonParser.parseString(task.serialize()));
            }
            try (FileWriter writer = new FileWriter(SAVE_FILE)) {
                GSON.toJson(savedTasks, writer);
            }
        } catch (IOException e) {
            throw new FileException(SAVE_FILE.toString());
        }

    }

    /**
     * Loads the list of tasks from the specified file.
     * The file is expected to contain a JSON array of serialized task objects.
     *
     * @return the list of loaded tasks
     * @throws BaronException if an error occurs while loading the tasks
     */
    public static ArrayList<Task> load() throws BaronException {
        if (!SAVE_FILE.exists()) {
            return new ArrayList<>();
        }
        ArrayList<Task> tasks = new ArrayList<>();
        try {
            JsonElement savedData;
            try (FileReader reader = new FileReader(SAVE_FILE)) {
                savedData = JsonParser.parseReader(reader);
            }
            if (!savedData.isJsonArray()) {
                throw new BaronException("The Barathos ledger is not a JSON task array.");
            }
            for (JsonElement taskData : savedData.getAsJsonArray()) {
                tasks.add(Task.deserialize(taskData.toString()));
            }
        } catch (IOException e) {
            throw new FileException(SAVE_FILE.toString());
        } catch (JsonParseException e) {
            throw new BaronException("The Barathos ledger contains malformed JSON.");
        }
        return tasks;
    }
}
