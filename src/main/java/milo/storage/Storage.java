package milo.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

import milo.MiloException;
import milo.task.Deadline;
import milo.task.Event;
import milo.task.Task;
import milo.task.TaskList;
import milo.task.ToDo;

/** Loads tasks from and saves tasks to Milo's data file. */
public class Storage {
    // Built with Paths.get so the separator is correct on Windows, macOS, and Linux
    // instead of hardcoding "/".
    private static final String FILE_PATH = Paths.get(".", "data", "milo.txt").toString();
    // Field positions in a pipe-delimited task record.
    private static final int TASK_TYPE_INDEX = 0;
    private static final int STATUS_INDEX = 1;
    private static final int DESCRIPTION_INDEX = 2;
    private static final int FIRST_DATE_INDEX = 3;
    private static final int SECOND_DATE_INDEX = 4;
    private static final int MINIMUM_TASK_FIELDS = 3;
    // Task type and completion markers used by the storage format.
    private static final String TODO_TYPE = "T";
    private static final String DEADLINE_TYPE = "D";
    private static final String EVENT_TYPE = "E";
    private static final String DONE_STATUS = "1";

    /**
     * Loads all valid tasks from the data file.
     *
     * @return the loaded tasks, or an empty list when no data file exists
     * @throws MiloException if the data file cannot be accessed
     */
    public static TaskList loadTasks() throws MiloException {
        TaskList tasks = new TaskList();
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            return tasks;
        }

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNext()) {
                Task task = Storage.restoreTask(scanner.nextLine());

                if (task != null) {
                    tasks.add(task);
                }
            }
        } catch (FileNotFoundException e) {
            throw new MiloException("-O- Oh no! I couldn't load your old tasks!");
        }

        return tasks;
    }

    /**
     * Saves all tasks to the data file.
     *
     * @param tasks tasks to persist
     * @throws MiloException if the data file cannot be written
     */
    public static void saveTasks(TaskList tasks) throws MiloException {
        try {
            File file = new File(FILE_PATH);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            try (FileWriter fw = new FileWriter(file)) {
                for (Task task : tasks.asList()) {
                    String line = task.storageString();
                    fw.write(line + System.lineSeparator());
                }
            }
        } catch (IOException e) {
            throw new MiloException("-O- Oh no! I can't save your tasks!");
        }
    }

    /**
     * Restores one task from its pipe-delimited storage representation.
     *
     * @param input serialized task line
     * @return the restored task, or {@code null} for a blank or corrupted line
     */
    public static Task restoreTask(String input) {
        if (input == null || input.trim().isEmpty()) {
            return null;
        }

        String[] inputs = input.split("\\s*\\|\\s*");

        if (inputs.length < MINIMUM_TASK_FIELDS) {
            System.out.println("Hmm... That's weird... There's a corrupted line, I'm just gna skip it.");
            return null;
        }

        String taskType = inputs[TASK_TYPE_INDEX].trim();
        boolean isDone = inputs[STATUS_INDEX].trim().equals(DONE_STATUS);
        String description = inputs[DESCRIPTION_INDEX].trim();
        Task task;

        try {
            if (taskType.equals(TODO_TYPE)) {
                task = new ToDo(description);
            } else if (taskType.equals(DEADLINE_TYPE)) {
                task = new Deadline(description, inputs[FIRST_DATE_INDEX]);
            } else if (taskType.equals(EVENT_TYPE)) {
                task = new Event(description, inputs[FIRST_DATE_INDEX], inputs[SECOND_DATE_INDEX]);
            } else {
                System.out.println("Hmm... That's weird... There's an unrecognised task-type, I'm just gna skip it.");
                return null;
            }
        } catch (ArrayIndexOutOfBoundsException | DateTimeParseException e) {
            System.out.println("Hmm... That's weird... There's a corrupted line, I'm just gna skip it.");
            return null;
        }

        if (isDone) {
            task.markAsDone();
        }

        return task;
    }
}
