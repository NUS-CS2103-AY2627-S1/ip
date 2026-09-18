package alice;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;


/**
 * Handles reading tasks from and writing tasks to the data file on disk,
 * so that tasks persist between runs of the program.
 */
public class Storage {
    private final String filePath;
    private final List<String> loadWarnings = new ArrayList<>();

    /**
     * Constructs a Storage object for the given file path.
     *
     * @param filePath relative or absolute path to the data file.
     */
    public Storage(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Saves the given list of tasks to the data file, overwriting any
     * existing content. Creates the parent directory if it does not exist.
     *
     * @param tasks the list of tasks to save.
     * @throws AliceException if the tasks could not be written to disk, so
     *         that the caller can tell the user their change was not saved
     *         rather than silently losing it.
     */
    public void save(ArrayList<Task> tasks) throws AliceException {
        try {
            File file = new File(filePath);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            String content = tasks.stream()
                    .map(task -> task.toFileFormat() + System.lineSeparator())
                    .collect(Collectors.joining());
            FileWriter writer = new FileWriter(file);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            // Previously this was only printed to the console, which the GUI
            // never shows - so a failed save looked exactly like a successful
            // one and the user lost work without ever being told.
            throw new AliceException("I couldn't save your tasks to " + filePath
                    + ", so this change will be lost when Alice closes.");
        }
    }

    /**
     * Loads tasks from the data file. If the file does not exist, returns
     * an empty list instead of throwing an error.
     *
     * @return the list of tasks loaded from the file, or an empty list if
     *         the file does not exist.
     */
    public ArrayList<Task> load() {
        ArrayList<Task> tasks = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            return tasks;
        }

        try {
            Scanner fileScanner = new Scanner(file);
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                try {
                    Task task = parseLine(line);
                    if (task != null) {
                        tasks.add(task);
                    }
                } catch (RuntimeException e) {
                    // A single malformed line (e.g. one left over from an
                    // older version of the file format, or hand-edited by
                    // accident) should not stop the rest of the file from
                    // loading, so it is recorded and skipped instead of
                    // letting the exception propagate and crash startup.
                    loadWarnings.add("I skipped a line in your data file that I couldn't read: " + line);
                }
            }
            fileScanner.close();
        } catch (IOException e) {
            loadWarnings.add("I couldn't read your saved tasks from " + filePath
                    + ", so I've started with an empty list.");
        }

        return tasks;
    }

    /**
     * Returns any problems encountered during the most recent {@link #load()},
     * such as data-file lines that could not be understood.
     *
     * <p>These are collected rather than printed so that a front end with no
     * console, such as the JavaFX GUI, can still show them to the user.
     *
     * @return the warnings from loading, in the order they occurred.
     */
    public List<String> getLoadWarnings() {
        return List.copyOf(loadWarnings);
    }

    /**
     * Parses a single line from the data file into a Task object, based on
     * its type marker (T, D, or E).
     *
     * @param line a single line of text from the data file.
     * @return the parsed Task, or null if the type marker is unrecognized.
     */
    private Task parseLine(String line) {
        String[] parts = line.split(" \\| ");
        String type = parts[0].trim();
        boolean isDone = parts[1].trim().equals("1");
        String description = parts[2].trim();

        Task task;
        if (type.equals("T")) {
            task = new Todo(description);
        } else if (type.equals("D")) {
            LocalDateTime by = LocalDateTime.parse(parts[3].trim());
            task = new Deadline(description, by);
        } else if (type.equals("E")) {
            LocalDateTime from = LocalDateTime.parse(parts[3].trim());
            LocalDateTime to = LocalDateTime.parse(parts[4].trim());
            task = new Event(description, from, to);
        } else {
            return null;
        }

        if (isDone) {
            task.markAsDone();
        }
        return task;
    }
}
