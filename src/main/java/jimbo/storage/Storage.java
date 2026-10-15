package jimbo.storage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

import jimbo.exception.JimboException;
import jimbo.task.Deadline;
import jimbo.task.Event;
import jimbo.task.Task;
import jimbo.task.Todo;

/**
 * Handles reading the task list from, and writing it to, a save file on
 * disk. The save file uses a pipe-delimited text format, with each line's
 * exact fields determined by the corresponding {@link Task} subclass's
 * {@code toSaveFormat()} method.
 */
public class Storage {
    private final String filePath;

    /**
     * Creates a {@code Storage} that reads from and writes to the save file
     * at {@code filePath}.
     */
    public Storage(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Writes {@code tasks} to the save file, one task per line, overwriting
     * any existing content. Creates the parent folder (e.g. {@code ./data})
     * first if it does not already exist.
     */
    public void save(ArrayList<Task> tasks) {
        Path filePath = Path.of(this.filePath);
        try {
            Files.createDirectories(filePath.getParent());
            try (FileWriter writer = new FileWriter(filePath.toFile())) {
                for (Task task : tasks) {
                    writer.write(task.toSaveFormat() + System.lineSeparator());
                }
            }
        } catch (IOException e) {
            System.out.println("Warning: could not save tasks to disk (" + e.getMessage() + ").");
        }
    }

    /**
     * Loads the task list from the save file. Returns an empty list if the
     * save file (or its parent folder) does not exist yet, e.g. on the very
     * first run. Any line that cannot be parsed is skipped, with a warning
     * printed, rather than aborting the load.
     */
    public ArrayList<Task> load() {
        ArrayList<Task> tasks = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            return tasks;
        }

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.isBlank()) {
                    continue;
                }
                try {
                    tasks.add(parseLine(line));
                } catch (JimboException e) {
                    System.out.println("Warning: skipping corrupted line in save file: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Warning: could not read save file (" + e.getMessage() + ").");
        }
        return tasks;
    }

    /**
     * Parses a single save-file line back into the appropriate {@link Task}
     * subclass instance.
     *
     * @throws JimboException if the line's type letter, field count or
     *                        done-status digit is invalid.
     */
    private Task parseLine(String line) throws JimboException {
        String[] fields = line.split(" \\| ");
        if (fields.length < 3) {
            throw new JimboException("Not enough fields.");
        }

        String typeIcon = fields[0];
        boolean isDone = isDoneDigit(fields[1]);
        String description = fields[2];

        Task task;
        switch (typeIcon) {
            case "T":
                if (fields.length != 3) {
                    throw new JimboException("Todo has wrong number of fields.");
                }
                task = new Todo(description);
                break;
            case "D":
                if (fields.length != 4) {
                    throw new JimboException("Deadline has wrong number of fields.");
                }
                try {
                    task = new Deadline(description, LocalDateTime.parse(fields[3]));
                } catch (DateTimeParseException e) {
                    throw new JimboException("Invalid deadline date/time in save file: " + fields[3]);
                }
                break;
            case "E":
                if (fields.length != 5) {
                    throw new JimboException("Event has wrong number of fields.");
                }
                try {
                    task = new Event(description, LocalDateTime.parse(fields[3]), LocalDateTime.parse(fields[4]));
                } catch (DateTimeParseException e) {
                    throw new JimboException("Invalid event date/time in save file: " + fields[3] + ", " + fields[4]);
                }
                break;
            default:
                throw new JimboException("Unknown task type icon: " + typeIcon);
        }

        assert task != null : "task should have been assigned by a case above, or an exception thrown";
        if (isDone) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Parses the save file's done-status digit ("0" or "1") into a boolean.
     *
     * @throws JimboException if the digit is anything other than "0" or "1".
     */
    private boolean isDoneDigit(String digit) throws JimboException {
        if (digit.equals("1")) {
            return true;
        } else if (digit.equals("0")) {
            return false;
        }
        throw new JimboException("Invalid done-status digit: " + digit);
    }
}
