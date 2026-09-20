package atom.storage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import atom.task.Deadline;
import atom.task.Event;
import atom.task.Task;
import atom.task.ToDo;

/**
 * Handles saving and loading data.
 */
public class Storage {
    /** The file path of the save file */
    private static final String SAVE_PATH = "./data/atom.txt";

    /**
     * An enum for types of tasks.
     */
    public enum TaskType {
        T, D, E
    }

    /**
     * Instantiates a <code>Storage</code> and
     * checks if a save file exists and creates one if it does not.
     */
    public Storage() {
        File file = new File(SAVE_PATH);

        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try {
            file.createNewFile();
        } catch (IOException e) {
            System.out.println("Unable to create save file.");
        }
    }

    /**
     * Loads data from the save file.
     *
     * @return A <code>TaskList</code> filled with tasks from the loaded data.
     */
    public ArrayList<Task> load() {
        Path path = Paths.get(SAVE_PATH);
        List<String> lines;

        try {
            lines = Files.readAllLines(path);
        } catch (IOException e) {
            System.out.println("Error in reading from save file.");
            return new ArrayList<>();
        }

        List<Task> loadedTasks = new ArrayList<>();
        for (String line : lines) {
            try {
                loadedTasks.add(convertLineToTask(line));
            } catch (RuntimeException e) {
                // Ignore malformed records so one bad line does not prevent startup.
                System.out.println("Invalid save record detected.");
            }
        }

        return new ArrayList<>(loadedTasks);
    }

    /**
     * Converts a line in a save file to the corresponding task.
     *
     * @param line A save file line.
     * @return The corresponding task.
     */
    private Task convertLineToTask(String line) {
        String[] lineParts = line.split(" /");
        Task task;

        switch (TaskType.valueOf(lineParts[0])) {
            case TaskType.T:
                task = new ToDo(lineParts[2]);
                break;
            case TaskType.D:
                task = new Deadline(lineParts[2], lineParts[3]);
                break;
            case TaskType.E:
                task = new Event(lineParts[2], lineParts[3], lineParts[4]);
                break;
            default:
                assert false : "Invalid save format.";
                return null;
        }

        if (lineParts[1].equals("1")) {
            task.mark();
        }

        return task;
    }

    /**
     * Adds a task to the save file.
     *
     * @param taskType The type of the task.
     * @param args The arguments to the task.
     */
    public void writeTask(TaskType taskType, String[] args) {
        StringBuilder sb = new StringBuilder(taskType.toString());
        sb.append(" /0");
        for (String s : args) {
            sb.append(" /").append(s);
        }
        sb.append("\n");

        try {
            FileWriter fw = new FileWriter(SAVE_PATH, true);
            fw.write(sb.toString());
            fw.close();
        } catch (IOException e) {
            System.out.println("Error in writing to save file.");
        }
    }

    /**
     * Marks a task in the save file.
     *
     * @param taskIndex The index of the task to be marked.
     */
    public void markTask(int taskIndex) {
        Path path = Paths.get(SAVE_PATH);
        try {
            List<String> lines = Files.readAllLines(path);
            assertValidTask(lines.size(), taskIndex);

            StringBuilder markedTask = new StringBuilder(lines.get(taskIndex - 1));
            markedTask.setCharAt(3, '1');
            lines.set(taskIndex - 1, markedTask.toString());

            Files.write(path, lines);
        } catch (IOException e) {
            System.out.println("Error in writing to save file.");
        }
    }

    /**
     * Unmarks a task in the save file.
     *
     * @param taskIndex The index of the task to be unmarked.
     */
    public void unmarkTask(int taskIndex) {
        Path path = Paths.get(SAVE_PATH);
        try {
            List<String> lines = Files.readAllLines(path);
            assertValidTask(lines.size(), taskIndex);

            StringBuilder markedTask = new StringBuilder(lines.get(taskIndex - 1));
            markedTask.setCharAt(3, '0');
            lines.set(taskIndex - 1, markedTask.toString());

            Files.write(path, lines);
        } catch (IOException e) {
            System.out.println("Error in writing to save file.");
        }
    }

    /**
     * Deletes a task in the save file.
     *
     * @param taskIndex The index of the task to be deleted.
     */
    public void deleteTask(int taskIndex) {
        Path path = Paths.get(SAVE_PATH);
        try {
            List<String> lines = Files.readAllLines(path);
            assertValidTask(lines.size(), taskIndex);

            lines.remove(taskIndex - 1);

            Files.write(path, lines);
        } catch (IOException e) {
            System.out.println("Error in writing to save file.");
        }
    }

    /**
     * Asserts if the provided task index is valid.
     *
     * @param maxTaskIndex The maximum task index.
     * @param taskIndex The task index provided.
     */
    private void assertValidTask(int maxTaskIndex, int taskIndex) {
        assert taskIndex >= 0 && taskIndex < maxTaskIndex : "Task index out of bounds";
    }
}
