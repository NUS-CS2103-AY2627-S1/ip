package waddles;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Deals with loading tasks from, and saving tasks to, the hard disk.
 */
public class Storage {
    private final String filePath;

    /**
     * Creates a Storage that reads from and writes to the given file path.
     *
     * @param filePath Relative path to the data file, e.g. "./data/waddles.txt".
     */
    public Storage(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Saves the given tasks to the data file, creating the containing
     * folder if it does not already exist.
     *
     * @param tasks The tasks to save.
     * @throws IOException If the file cannot be written to.
     */
    public void save(List<Task> tasks) throws IOException {
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        // Only append a trailing line separator when there is at least one task, so that
        // saving an empty list still produces a genuinely empty file, as it did before.
        String content = tasks.isEmpty() ? "" : tasks.stream()
                .map(this::taskToFileFormat)
                .collect(Collectors.joining(System.lineSeparator(), "", System.lineSeparator()));

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        }
    }

    /**
     * Loads tasks from the data file, skipping any corrupted lines.
     *
     * @return The list of tasks loaded, or an empty list if the file
     *         does not exist or cannot be read.
     */
    public List<Task> load() {
        List<Task> tasks = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            return tasks;
        }

        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                if (line.trim().isEmpty()) {
                    continue;
                }
                Task task = parseLine(line);
                if (task != null) {
                    tasks.add(task);
                }
            }
        } catch (IOException e) {
            return new ArrayList<>();
        }
        return tasks;
    }

    /**
     * Converts a task into its on-disk text representation.
     *
     * @param task The task to convert.
     * @return The task formatted as a single line of text.
     */
    private String taskToFileFormat(Task task) {
        assert task != null : "Cannot serialize a null task";
        String doneFlag = task.isDone() ? "1" : "0";
        if (task instanceof Deadline) {
            Deadline d = (Deadline) task;
            return joinFields("D", doneFlag, task.getDescription(), d.getBy(), d.getRecurrence().name());
        } else if (task instanceof Event) {
            Event e = (Event) task;
            return joinFields(
                    "E", doneFlag, task.getDescription(), e.getFrom(), e.getTo(), e.getRecurrence().name());
        } else {
            return joinFields("T", doneFlag, task.getDescription());
        }
    }

    /**
     * Joins the given fields into a single line using this file format's
     * " | " delimiter. Declared with varargs since each task type has a
     * different number of fields (a ToDo has 3, a Deadline 4, an Event 5).
     *
     * @param fields The fields to join, in the order they should appear.
     * @return The fields joined into one delimited line.
     */
    private static String joinFields(String... fields) {
        return String.join(" | ", fields);
    }

    /**
     * Attempts to parse a single line of the data file back into a Task.
     *
     * @param line A line read from the data file.
     * @return The parsed task, or null if the line is corrupted/invalid.
     */
    private Task parseLine(String line) {
        try {
            String[] parts = line.split(" \\| ");
            String type = parts[0].trim();
            boolean isDone = parts[1].trim().equals("1");
            String description = parts[2].trim();

            Task task;
            if (type.equals("T")) {
                task = new ToDo(description);
            } else if (type.equals("D")) {
                String by = parts[3].trim();
                Deadline deadline = new Deadline(description, by);
                // The recurrence field was added after this file format was first used, so
                // older data files may not have it; treat a missing field as NONE.
                if (parts.length > 4) {
                    deadline.setRecurrence(Recurrence.fromStorageCode(parts[4].trim()));
                }
                task = deadline;
            } else if (type.equals("E")) {
                String from = parts[3].trim();
                String to = parts[4].trim();
                Event event = new Event(description, from, to);
                if (parts.length > 5) {
                    event.setRecurrence(Recurrence.fromStorageCode(parts[5].trim()));
                }
                task = event;
            } else {
                return null;
            }

            if (isDone) {
                task.markAsDone();
            }
            return task;
        } catch (ArrayIndexOutOfBoundsException e) {
            // A line with fewer " | "-delimited fields than its declared type requires
            // (e.g. a Deadline missing its "by" field) is the only failure mode expected
            // from a corrupted data file; anything else should surface as a real bug.
            return null;
        }
    }
}