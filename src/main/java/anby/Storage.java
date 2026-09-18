package anby;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.Collectors;

import anby.task.Deadline;
import anby.task.Event;
import anby.task.Task;
import anby.task.Todo;

/**
 * Handles loading and saving tasks from persistent storage.
 */
public class Storage {
    private static final String FILE_PATH = "data/anby.txt";
    private static final String FILE_FIELD_SEPARATOR = " \\| ";
    private static final String TODO_FILE_SYMBOL = "T";
    private static final String DEADLINE_FILE_SYMBOL = "D";
    private static final String EVENT_FILE_SYMBOL = "E";
    private static final String DONE_STATUS = "1";
    private static final int TASK_TYPE_INDEX = 0;
    private static final int DONE_STATUS_INDEX = 1;
    private static final int DESCRIPTION_INDEX = 2;
    private static final int DEADLINE_DATE_INDEX = 3;
    private static final int EVENT_FROM_INDEX = 3;
    private static final int EVENT_TO_INDEX = 4;

    private Storage() {
    }

    /**
     * Loads saved tasks from the task data file.
     *
     * @return tasks loaded from disk, or an empty list if the file does not exist
     */
    public static ArrayList<Task> loadTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        File file = new File(FILE_PATH);

        if (!file.exists()) { // load an empty ArrayList
            return tasks;
        }

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                Task task = parseTask(line);
                tasks.add(task);
            }
        } catch (FileNotFoundException e) {
            return tasks;
        }

        return tasks;
    }

    private static Task parseTask(String line) {
        String[] parts = line.split(FILE_FIELD_SEPARATOR);
        assert parts.length >= 3 : "Saved task line should contain type, done status, and description";

        Task task;

        switch (parts[TASK_TYPE_INDEX]) {
            case TODO_FILE_SYMBOL:
                assert parts.length == 3;
                task = new Todo(parts[DESCRIPTION_INDEX]);
                break;
            case DEADLINE_FILE_SYMBOL:
                assert parts.length == 4;
                task = new Deadline(parts[DESCRIPTION_INDEX], parts[DEADLINE_DATE_INDEX]);
                break;
            case EVENT_FILE_SYMBOL:
                assert parts.length == 5;
                task = new Event(parts[DESCRIPTION_INDEX], parts[EVENT_FROM_INDEX], parts[EVENT_TO_INDEX]);
                break;
            default:
                assert false : "Unknown saved task type: " + parts[TASK_TYPE_INDEX];
                throw new IllegalArgumentException("Unknown task type: " + parts[TASK_TYPE_INDEX]);
        }

        if (isDone(parts)) {
            task.markAsDone();
        }

        return task;
    }

    private static boolean isDone(String[] parts) {
        return parts[DONE_STATUS_INDEX].equals(DONE_STATUS);
    }

    /**
     * Saves the given tasks to the task data file.
     *
     * @param tasks tasks to save
     * @throws IOException if the task data file cannot be written
     */
    public static void saveTasks(ArrayList<Task> tasks) throws IOException {
        File file = new File(FILE_PATH);
        file.getParentFile().mkdirs();

        try (FileWriter fileWriter = new FileWriter(file)) {
            String fileContent = tasks.stream()
                    .map(Task::toFileString)
                    .collect(Collectors.joining(System.lineSeparator()));

            fileWriter.write(fileContent);
            if (!fileContent.isEmpty()) {
                fileWriter.write(System.lineSeparator());
            }
        }
    }
}
