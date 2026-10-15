package bern.storage;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import bern.task.Task;
import bern.task.TaskFactory;
import bern.ui.Dialog;

/**
 * Provides methods for serializing tasks to the save file and reconstructing tasks from saved records.
 */
public class SaveDataController {
    /**
     * Separator for saved task record.
     */
    public static final String SEPARATOR = "\0|";

    private static final String SEPARATOR_REGEX = "\0\\|";
    private static final String SAVE_DIRECTORY = "data";
    private static final String SAVE_FILE = "tasks.txt";

    /**
     * Creates a storage controller instance; save-file operations are provided by static methods.
     */
    public SaveDataController() {
    }

    /**
     * Saves task data to {@code data/tasks.txt} under the working directory, creating the save directory if needed.
     *
     * @param tasks The tasks to save.
     * @return {@code true} if the save succeeds, or {@code false} if creating the directory or writing the file fails.
     */
    public static boolean saveTaskData(List<Task> tasks) {
        String dataString = tasksToDataString(tasks);

        try {
            // This does nothing if the directory exists, or creates it otherwise.
            Files.createDirectories(Paths.get(System.getProperty("user.dir"), SAVE_DIRECTORY));
        } catch (IOException e) {
            Dialog.getInstance().printDirectoryError();
            return false;
        }

        try (FileWriter fw = new FileWriter(Paths.get(System.getProperty("user.dir"), SAVE_DIRECTORY,
                SAVE_FILE).toString())) {
            fw.write(dataString);
        } catch (IOException e) {
            System.out.println("Unable to write to file.");
            return false;
        }

        return true;
    }

    /**
     * Reads tasks from {@code data/tasks.txt}. Skip data in unreadable formats and report if
     * any data was skipped or if the file cannot be read.
     *
     * @return The successfully reconstructed tasks, or an empty list if the file cannot be read.
     */
    public static List<Task> readTaskData() {
        List<String> savedData;
        ArrayList<Task> savedTasks = new ArrayList<>();
        boolean success = true;
        try (FileReader fr = new FileReader(Paths.get(System.getProperty("user.dir"), SAVE_DIRECTORY,
                SAVE_FILE).toString())) {
            savedData = fr.readAllLines();
            for (String taskString : savedData) {
                try {
                    savedTasks.add(dataStringToTask(taskString));
                } catch (ParseException | IllegalArgumentException | DateTimeParseException e) {
                    success = false;
                }
            }
        } catch (IOException e) {
            Dialog.getInstance().printLoadError();
        }

        if (!success) {
            Dialog.getInstance().printLoadTaskError();
        }

        return savedTasks;
    }

    /**
     * Convert given tasks to a String in save data format.
     *
     * @param tasks The tasks to be converted
     * @return The string representing the tasks, in save data format.
     */
    private static String tasksToDataString(List<Task> tasks) {
        StringBuilder dataString = new StringBuilder();
        for (Task task : tasks) {
            dataString.append(String.join(SEPARATOR, task.toDataList()));
            dataString.append('\n');
        }
        return dataString.toString();
    }

    /**
     * Convert a String in save data format into a Task
     *
     * @param dataString A String in save data format.
     * @return The task converted from the given String.
     * @throws ParseException If the record contains an invalid task type.
     * @throws IllegalArgumentException If the record is malformed.
     * @throws DateTimeParseException If a saved date or time cannot be parsed.
     */
    private static Task dataStringToTask(String dataString)
            throws ParseException, IllegalArgumentException {
        String[] data = dataString.split(SEPARATOR_REGEX);
        return TaskFactory.makeTaskFromData(data);
    }
}
