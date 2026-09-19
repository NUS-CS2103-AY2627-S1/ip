package nova;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Handles saving tasks to and loading tasks from a file on disk.
 * Save format: one task per line, fields separated by " | ".
 * e.g., T | NOT_DONE | read book
 *       D | DONE | return book | 2026-08-28
 *       E | NOT_DONE | project meeting | 2026-08-28 | 2026-08-29
 */
public class Storage {
    private static final String DEFAULT_PATH = "data/nova.txt";
    private static final String DEFAULT_ARCHIVE_PATH = "data/nova-archive.txt";
    private static final String SEPARATOR = " | ";
    private static final String TEMP_SUFFIX = ".tmp";

    private final String path;
    private final String archivePath;
    private String errorMessage = "";

    /**
     * Creates a storage that saves to and loads from the default file.
     */
    public Storage() {
        this(DEFAULT_PATH, DEFAULT_ARCHIVE_PATH);
    }

    /**
     * Creates a storage that saves to and loads from the given file.
     *
     * @param path the save file path
     */
    public Storage(String path) {
        this(path, DEFAULT_ARCHIVE_PATH);
    }

    /**
     * Creates a storage that uses the given files for tasks and the archive.
     *
     * @param path the save file path
     * @param archivePath the archive file path
     */
    public Storage(String path, String archivePath) {
        assert path != null && archivePath != null : "Storage paths must not be null";
        this.path = path;
        this.archivePath = archivePath;
    }

    /**
     * Returns the problem found by the most recent save or load, and clears it,
     * so that each problem is reported to the user once.
     *
     * @return the error message, or an empty string when there was no problem
     */
    public String consumeErrorMessage() {
        String message = this.errorMessage;
        this.errorMessage = "";
        return message;
    }

    /**
     * Saves all tasks to the save file, one task per line. The file is written
     * to a temporary file first, so a failure part way through cannot leave a
     * half-written save file behind.
     *
     * @param tasks the tasks to save
     */
    public void save(ArrayList<Task> tasks) {
        assert tasks != null : "Task list to save must not be null";
        Path target = Path.of(this.path);
        Path folder = target.getParent();
        if (folder != null && !Files.isDirectory(folder)) {
            if (!folder.toFile().mkdirs()) {
                this.errorMessage = "Could not create the folder " + folder + ", so tasks were not saved.";
                return;
            }
        }
        Path temp = Path.of(this.path + TEMP_SUFFIX);
        try (FileWriter writer = new FileWriter(temp.toFile())) {
            for (Task task : tasks) {
                writer.write(toFileLine(task) + System.lineSeparator());
            }
        } catch (IOException e) {
            this.errorMessage = "Could not save tasks to " + this.path + ": " + e.getMessage() + ".";
            return;
        }
        try {
            Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            this.errorMessage = "Could not save tasks to " + this.path + ": " + e.getMessage() + ".";
        }
    }

    /**
     * Appends the given tasks to the archive file, keeping a record of them.
     *
     * @param tasks the tasks to archive
     */
    public void appendToArchive(ArrayList<Task> tasks) {
        assert tasks != null : "Tasks to archive must not be null";
        File target = new File(this.archivePath);
        File folder = target.getParentFile();
        if (folder != null && !folder.isDirectory() && !folder.mkdirs()) {
            this.errorMessage = "Could not create the folder " + folder + ", so tasks were not archived.";
            return;
        }
        try (FileWriter writer = new FileWriter(target, true)) {
            for (Task task : tasks) {
                writer.write(toFileLine(task) + System.lineSeparator());
            }
        } catch (IOException e) {
            this.errorMessage = "Could not archive tasks to " + this.archivePath + ": " + e.getMessage() + ".";
        }
    }

    /**
     * Loads all tasks saved in the save file. A missing file simply means there
     * is nothing saved yet, but a file that cannot be read or that holds lines
     * that do not follow the save format is reported through
     * {@link #consumeErrorMessage()}.
     *
     * @return the tasks that could be loaded, in file order
     */
    public ArrayList<Task> load() {
        ArrayList<Task> tasks = new ArrayList<>();
        File file = new File(this.path);
        if (!file.exists()) {
            return tasks;
        }
        int skippedLines = 0;
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.isBlank()) {
                    continue;
                }
                Task task = parseLine(line);
                if (task == null) {
                    skippedLines++;
                } else {
                    tasks.add(task);
                }
            }
        } catch (FileNotFoundException e) {
            this.errorMessage = "Could not read " + this.path + ": " + e.getMessage()
                    + ". Starting with an empty list.";
            return tasks;
        }
        if (skippedLines > 0) {
            this.errorMessage = "Skipped " + skippedLines + " unreadable line(s) in " + this.path
                    + "; those tasks are not shown.";
        }
        return tasks;
    }

    private String toFileLine(Task task) {
        String status = task.isDone() ? "DONE" : "NOT_DONE";
        if (task instanceof Deadline) {
            Deadline deadline = (Deadline) task;
            return "D" + SEPARATOR + status + SEPARATOR + deadline.getDescription()
                    + SEPARATOR + deadline.getBy();
        }
        if (task instanceof Event) {
            Event event = (Event) task;
            return "E" + SEPARATOR + status + SEPARATOR + event.getDescription()
                    + SEPARATOR + event.getFrom() + SEPARATOR + event.getTo();
        }
        return "T" + SEPARATOR + status + SEPARATOR + task.getDescription();
    }

    private static Task parseLine(String line) {
        assert line != null : "Save file line must not be null";
        String[] parts = line.split(" \\| ");
        if (parts.length < 2) {
            return null; // malformed line
        }
        Task task;
        try {
            switch (parts[0]) {
                case "T":
                    if (parts.length != 3) {
                        return null;
                    }
                    task = new Todo(parts[2]);
                    break;
                case "D":
                    if (parts.length != 4) {
                        return null;
                    }
                    task = new Deadline(parts[2], LocalDate.parse(parts[3]));
                    break;
                case "E":
                    if (parts.length != 5) {
                        return null;
                    }
                    task = new Event(parts[2], LocalDate.parse(parts[3]), LocalDate.parse(parts[4]));
                    break;
                default:
                    return null;
            }
        } catch (DateTimeParseException e) {
            return null; // malformed line
        }
        if (parts[1].equals("DONE")) {
            task.markDone();
        }
        return task;
    }
}
