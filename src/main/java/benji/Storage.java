package benji;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;


/**
 * Handles saving and loading tasks from a file.
 *
 * <p>Tasks are stored in the application's data folder so that
 * they can be restored when the application is started again.</p>
 */
public class Storage {
    // Look for benji.txt inside the data folder.
    // Path: A Java object that represents a file or folder location on your computer.
    // Paths.get(...): A tool that joins folder names and file names together correctly.
    private static final Path FILE_PATH = Paths.get("data", "benji.txt");
    private static final String FIELD_SEPARATOR = " | ";
    private static final String FIELD_SPLIT_REGEX = "\\|";
    private static final String TODO_TYPE = "T";
    private static final String DEADLINE_TYPE = "D";
    private static final String EVENT_TYPE = "E";
    private static final String DONE_STATUS = "1";
    private static final String NOT_DONE_STATUS = "0";

    /**
     * Saves all tasks in the task list to the storage file.
     *
     * @param tasks the list of tasks to save
     */
    public static void saveTasks(TaskList tasks) {
        assert tasks != null : "Saving should always receive a valid task list";
        assert FILE_PATH.getParent() != null : "Storage file path must include a parent directory";
        try {
            // FILE_PATH: The full path to your target file (like C:/data/reports/summary.txt).
            // .getParent(): Grabs just the folder path where the file lives (like C:/data/reports).
            // Files.createDirectories(...): Builds those folders on your computer.
            // If any middle folder is missing, it creates all of them at once. If the folders are already there,
            //  it does nothing and does not crash
            Files.createDirectories(FILE_PATH.getParent());

            List<String> lines = new ArrayList<>();

            for (Task task : tasks.getTasks()) {
                lines.add(convertTaskToStorageLine(task));
            }
            Files.write(FILE_PATH, lines); // write and overwrite content in a specific file

        } catch (IOException e) {
            System.out.println("Sorry, I couldn't save your tasks");
        }
    }

    /**
     * Converts one task into the line format used by the storage file.
     *
     * @param task task to convert
     * @return formatted storage line
     */
    private static String convertTaskToStorageLine(Task task) {
        String status = task.isDone() ? DONE_STATUS : NOT_DONE_STATUS;

        if (task instanceof Todo) {
            return TODO_TYPE + FIELD_SEPARATOR + status
                    + FIELD_SEPARATOR + task.getDescription();
        } else if (task instanceof Deadline) {
            Deadline deadline = (Deadline) task;
            return DEADLINE_TYPE + FIELD_SEPARATOR + status
                    + FIELD_SEPARATOR + task.getDescription()
                    + FIELD_SEPARATOR + deadline.by;
        } else if (task instanceof Event) {
            Event event = (Event) task;
            return EVENT_TYPE + FIELD_SEPARATOR + status
                    + FIELD_SEPARATOR + task.getDescription()
                    + FIELD_SEPARATOR + event.start
                    + FIELD_SEPARATOR + event.end;
        }

        throw new IllegalArgumentException("Unsupported task type");
    }

    /**
     * Loads previously saved tasks from the storage file.
     *
     * @return an {@link ArrayList} containing the loaded tasks;
     *         an empty list if the storage file does not exist
     */
    public static ArrayList<Task> loadTasks() {
        ArrayList<Task> tasks = new ArrayList<>();

        // A missing file is normal when BENJI runs for the first time.
        if (!Files.exists(FILE_PATH)) {
            return tasks;
        }

        // Track invalid lines so BENJI shows one clear message after loading.
        boolean hasInvalidLines = false;

        try {
            List<String> lines = Files.readAllLines(FILE_PATH);

            for (String line : lines) {
                // A null task means this line does not follow BENJI's saved format.
                Task task = createTaskFromStorageLine(line);

                if (task == null) {
                    hasInvalidLines = true;
                    continue; // Skip only this invalid line and load the next one.
                }

                tasks.add(task);
            }

            // Do not show one error message for every corrupted line.
            if (hasInvalidLines) {
                System.out.println("Some saved tasks were invalid and were skipped.");
            }
        } catch (IOException e) {
            System.out.println("Sorry, I couldn't load your tasks.");
        }

        return tasks;
    }

    /**
     * Creates a task from one line in the storage file.
     *
     * @param line line read from the storage file
     * @return restored task, or null if the task type is unknown
     */
    private static Task createTaskFromStorageLine(String line) {
        // Split the saved line into its type, status, description, and timing fields.
        String[] parts = line.split(FIELD_SPLIT_REGEX);

        // Every saved task needs at least a type and completion status.
        if (parts.length < 2) {
            return null;
        }

        String type = parts[0].trim();
        String status = parts[1].trim();

        // Only "0" and "1" are valid completion statuses.
        if (!isValidStatus(status)) {
            return null;
        }

        boolean isDone = status.equals(DONE_STATUS);
        Task task;

        if (type.equals(TODO_TYPE)) {
            // Todo format: T | STATUS | DESCRIPTION
            if (parts.length != 3 || parts[2].trim().isEmpty()) {
                return null;
            }
            task = new Todo(parts[2].trim());

        } else if (type.equals(DEADLINE_TYPE)) {
            // Deadline format: D | STATUS | DESCRIPTION | DATE
            if (parts.length != 4 || parts[2].trim().isEmpty()) {
                return null;
            }

            try {
                task = new Deadline(parts[2].trim(), parts[3].trim());
            } catch (DateTimeParseException e) {
                // An invalid deadline date means this saved line is corrupted.
                return null;
            }

        } else if (type.equals(EVENT_TYPE)) {
            // Event format: E | STATUS | DESCRIPTION | START | END
            if (parts.length != 5 || parts[2].trim().isEmpty()
                    || parts[3].trim().isEmpty() || parts[4].trim().isEmpty()) {
                return null;
            }
            task = new Event(parts[2].trim(), parts[3].trim(),
                    parts[4].trim());

        } else {
            // BENJI does not recognise this task type.
            return null;
        }

        // Restore the saved completion status after creating the task.
        if (isDone) {
            task.markAsDone();
        }

        return task;
    }

    /**
     * Checks whether a saved completion status is valid.
     *
     * @param status completion status read from the storage file
     * @return true if the status is either done or not done
     */
    private static boolean isValidStatus(String status) {
        return status.equals(DONE_STATUS) || status.equals(NOT_DONE_STATUS);
    }
}
