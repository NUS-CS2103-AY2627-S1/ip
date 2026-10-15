package bubba;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Loads and saves task records, leaving error presentation to the user interface.
 */
public class Storage {
    private final ArrayList<String> loadWarnings = new ArrayList<>();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy HHmm");
    private final String filePath;

    /**
     * Creates storage for the given path without reading or writing the file.
     *
     * @param filePath Save-file path, resolved against the working directory if relative.
     */
    public Storage(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Returns warnings from the most recent load for the user interface to display.
     *
     * @return Copy of the warning list.
     */
    public ArrayList<String> getLoadWarnings() {
        return new ArrayList<>(loadWarnings);
    }

    /**
     * Loads tasks in file order, returning an empty list if the file does not exist.
     * Blank lines and unknown task types are skipped. Invalid dates are skipped with a warning.
     * Loading warnings are cleared at the start of each call.
     *
     * @return Loaded tasks with their saved completion states.
     * @throws ArrayIndexOutOfBoundsException If a nonblank record lacks required fields.
     */
    public ArrayList<Task> load() {
        loadWarnings.clear();
        ArrayList<Task> tasks = new ArrayList<>();

        File file = new File(filePath);
        if (!file.exists()) {
            return tasks;
        }

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                loadTask(scanner.nextLine(), tasks);
            }
        } catch (FileNotFoundException e) {
            loadWarnings.add("File wasn't found!");
        }

        return tasks;
    }

    private void loadTask(String line, ArrayList<Task> tasks) {
        if (line.isBlank()) {
            return;
        }

        try {
            Task task = parseTask(line);
            if (task != null) {
                tasks.add(task);
            }
        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException
                 | DateTimeParseException e) {
            loadWarnings.add("Skipped an invalid task record: " + line);
        }
    }

    private Task parseTask(String line) {
        String[] parts = line.split(" \\| ", -1);
        if (parts.length < 3 || parts[2].isBlank()
                || !(parts[1].equals("0") || parts[1].equals("1"))) {
            throw new IllegalArgumentException("Invalid task record");
        }
        Task task = createTask(parts);
        if (parts[1].equals("1")) {
            task.done();
        }
        return task;
    }

    private Task createTask(String[] parts) {
        String description = parts[2];
        switch (parts[0]) {
            case "T":
                requireFieldCount(parts, 3);
                return new Todo(description);
            case "D":
                requireFieldCount(parts, 4);
                LocalDateTime by = LocalDateTime.parse(parts[3], formatter);
                return new Deadline(description, by);
            case "E":
                requireFieldCount(parts, 5);
                LocalDateTime from = parseEventDate(parts[3]);
                LocalDateTime to = parseEventDate(parts[4]);
                return new Event(description, from, to);
            default:
                throw new IllegalArgumentException("Unknown task type");
        }
    }

    private void requireFieldCount(String[] parts, int expectedCount) {
        if (parts.length != expectedCount) {
            throw new IllegalArgumentException("Invalid number of task fields");
        }
    }

    /**
     * Parses a stored event date, also accepting ISO dates written by earlier versions.
     *
     * @param text Stored date and time.
     * @return Parsed date and time.
     * @throws DateTimeParseException If neither supported format can parse the value.
     */
    private LocalDateTime parseEventDate(String text) {
        try {
            return LocalDateTime.parse(text, formatter);
        } catch (DateTimeParseException e) {
            return LocalDateTime.parse(text);
        }
    }

    /**
     * Replaces the save file with the supplied tasks in list order.
     * Creates missing parent directories before opening the file.
     *
     * @param tasks Tasks to serialize, including their completion states.
     * @throws BubbaException If the file cannot be opened for writing.
     */
    public void save(ArrayList<Task> tasks) throws BubbaException {
        try {
            File file = new File(filePath);

            File parent = file.getParentFile();
            if (parent != null) {
                parent.mkdirs();
            }

            PrintWriter writer = new PrintWriter(file);

            for (Task task : tasks) {
                writer.println(task.toStorageString());
            }

            writer.close();
        } catch (FileNotFoundException e) {
            throw new BubbaException("Unable to save tasks to file!");
        }
    }
}
