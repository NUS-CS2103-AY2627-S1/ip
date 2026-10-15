package clue.components;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import clue.tasks.Deadline;
import clue.tasks.Event;
import clue.tasks.Task;
import clue.tasks.ToDo;

/**
 * Handles persisting and loading data from the filesystem
 */
public class Storage {
    public static final String SEPARATOR = "!%#";
    public static final Map<String, Class<? extends Task>> CLASSES = Map.of(
            "T", ToDo.class,
            "D", Deadline.class,
            "E", Event.class
    );
    private static final String CONSTRUCT_METHOD = "construct";

    private final File file;

    /**
     * Creates a new Storage pointing at a filepath
     *
     * @param filepath - the location where this class persists and loads data
     */
    public Storage(String filepath) {
        if (filepath == null || filepath.trim().isEmpty()) {
            throw new IllegalArgumentException("Filepath cannot be null or empty");
        }
        this.file = new File(filepath);
    }

    private Task createTask(String line) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException {
        String[] parts = line.split(SEPARATOR);
        if (parts.length < 2) {
            throw new IllegalArgumentException("Malformed line");
        }

        Class<? extends Task> cls = CLASSES.get(parts[0]);
        if (cls == null) {
            // Unknown task type - could be new version we cannot handle
            throw new IllegalArgumentException("Unknown task type " + parts[0]);
        }
        return (Task) cls.getMethod(CONSTRUCT_METHOD, String[].class).invoke(null, (Object) parts);
    }

    /**
     * Loads tasks from the filepath into the given list.
     * @param lst - List to append the tasks to.
     */
    public <T extends List<Task>> T load(T lst) {
        if (!file.exists()) {
            // file does not exist yet, return default empty list
            return lst;
        }

        if (!file.isFile()) {
            System.err.println("Error: Storage path is not a file: " + file.getAbsolutePath());
            return lst;
        }

        if (!file.canRead()) {
            System.err.println("Error: Cannot read storage file: " + file.getAbsolutePath());
            return lst;
        }

        try (Scanner myReader = new Scanner(file)) {
            int lineNumber = 0;
            while (myReader.hasNextLine()) {
                lineNumber++;
                String data = myReader.nextLine().trim();

                // Skip empty lines
                if (data.isEmpty()) {
                    continue;
                }

                try {
                    lst.add(createTask(data));
                } catch (InvocationTargetException e) {
                    // Task construction failed due to validation error
                    System.err.println("Warning: Failed to load task at line "
                            + lineNumber + ": " + e.getCause().getMessage());
                } catch (NoSuchMethodException | IllegalAccessException e) {
                    // We failed to implement the correct methods on classes in our CLASSES mapping
                    throw new RuntimeException("Unimplemented methods needed for class loading!", e);
                } catch (IllegalArgumentException e) {
                    // Malformed line or unknown task type, warn and skip
                    System.err.println("Warning: " + e.getMessage() + " at line " + lineNumber);
                    System.err.println("Line: " + data);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading storage file: " + e.getMessage());

        }
        return lst;
    }

    /**
     * Persists the tasks to the file, for later retrieval with {@link load}
     *
     * @param tasks - The list of tasks to save.
     * @return - Whether this operation is successful.
     */
    public boolean save(List<Task> tasks) {
        if (tasks == null) {
            System.err.println("Error: Cannot save null task list");
            return false;
        }

        // Ensure parent directory exists
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                System.err.println("Error: Could not create directory: " + parentDir.getAbsolutePath());
                return false;
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Task t : tasks) {
                if (t != null) {
                    writer.write(t.save(SEPARATOR));
                    writer.newLine();
                }
            }
            writer.flush();
        } catch (IOException e) {
            System.err.println("Error writing to storage file: " + e.getMessage());
            return false;
        }
        return true;
    }
}
