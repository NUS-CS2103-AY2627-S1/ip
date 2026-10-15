package jeryl.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

import jeryl.exception.JerylException;
import jeryl.task.Deadline;
import jeryl.task.Event;
import jeryl.task.Priority;
import jeryl.task.Task;
import jeryl.task.Todo;

/**
 * Handles reading tasks from, and writing tasks to, a save file on disk.
 * The file path is relative to the project root, so it works regardless
 * of which OS the app is run on.
 */
public class Storage {
    private final Path filePath;

    /**
     * Creates a Storage that reads from and writes to the given path,
     * relative to the working directory the app is run from.
     */
    public Storage(String filePath) {
        this.filePath = Paths.get(filePath);
    }

    /**
     * Loads tasks from the save file. If the file (or its parent folder)
     * does not exist yet, returns an empty list instead of failing, since
     * that's the normal situation on a brand new install.
     *
     * Any line that doesn't match the expected format is skipped, with a
     * warning printed, so a corrupted file doesn't prevent the app from
     * starting up.
     */
    public ArrayList<Task> load() {
        ArrayList<Task> tasks = new ArrayList<>();
        File file = filePath.toFile();
        if (!file.exists()) {
            return tasks;
        }
        try (Scanner scanner = new Scanner(file)) {
            int lineNumber = 0;
            while (scanner.hasNextLine()) {
                lineNumber++;
                String line = scanner.nextLine();
                if (line.trim().isEmpty()) {
                    continue;
                }
                try {
                    tasks.add(parseLine(line));
                } catch (JerylException e) {
                    System.out.println("Skipping corrupted save data at line " + lineNumber + ": " + e.getMessage());
                }
            }
        } catch (FileNotFoundException e) {
            // File existed a moment ago but is now gone; treat as empty.
            return new ArrayList<>();
        }
        return tasks;
    }

    /**
     * Parses one line of the save file into a Task.
     */
    private Task parseLine(String line) throws JerylException {
        String[] parts = line.split("\\s*\\|\\s*");
        if (parts.length < 3) {
            throw new JerylException("expected at least 3 fields, got " + parts.length);
        }
        String type = parts[0];
        boolean isDone = parts[1].equals("1");
        String description = parts[2];

        Task task;
        switch (type) {
            case "T":
                task = parts.length >= 4
                        ? new Todo(description, parsePriority(parts[3]))
                        : new Todo(description);
                break;
            case "D":
                if (parts.length < 4) {
                    throw new JerylException("deadline is missing its \"by\" field");
                }
                LocalDate by = parseDate(parts[3]);
                task = parts.length >= 5
                        ? new Deadline(description, by, parsePriority(parts[4]))
                        : new Deadline(description, by);
                break;
            case "E":
                if (parts.length < 5) {
                    throw new JerylException("event is missing its \"from\"/\"to\" fields");
                }
                LocalDate from = parseDate(parts[3]);
                LocalDate to = parseDate(parts[4]);
                task = parts.length >= 6
                        ? new Event(description, from, to, parsePriority(parts[5]))
                        : new Event(description, from, to);
                break;
            default:
                throw new JerylException("unknown task type \"" + type + "\"");
        }
        assert task != null : "task should have been assigned or an exception thrown by now";
        if (isDone) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Parses a priority field as saved by Task's toFileString (the enum
     * name, e.g. "HIGH"), wrapping any parse failure as a JerylException.
     */
    private Priority parsePriority(String priorityString) throws JerylException {
        try {
            return Priority.valueOf(priorityString);
        } catch (IllegalArgumentException e) {
            throw new JerylException("unknown priority \"" + priorityString + "\"");
        }
    }

    /**
     * Parses a yyyy-MM-dd date string as saved by Deadline/Event's
     * toFileString, wrapping any parse failure as a JerylException.
     */
    private LocalDate parseDate(String dateString) throws JerylException {
        try {
            return LocalDate.parse(dateString);
        } catch (DateTimeParseException e) {
            throw new JerylException("date \"" + dateString + "\" isn't in yyyy-mm-dd format");
        }
    }

    /**
     * Writes the given tasks to the save file, creating the parent folder
     * first if it doesn't exist yet.
     */
    public void save(ArrayList<Task> tasks) {
        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            try (PrintWriter writer = new PrintWriter(filePath.toFile())) {
                for (Task task : tasks) {
                    writer.println(task.toFileString());
                }
            }
        } catch (IOException e) {
            System.out.println("OOPS!!! I couldn't save your tasks to disk: " + e.getMessage());
        }
    }
}
