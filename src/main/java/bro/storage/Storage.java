package bro.storage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;

import bro.exception.BroException;
import bro.task.Deadline;
import bro.task.Event;
import bro.task.Task;
import bro.task.TaskList;
import bro.task.Todo;
import bro.ui.UserInterface;

/**
 * Handles reading tasks from and writing tasks to the local storage file.
 */
public class Storage {
    private final Path filePath;
    private int corruptedLineCount = 0;

    /**
     * Constructs a Storage instance targeting the specified Path.
     *
     * @param filePath The relative path to the data storage file.
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Constructs a Storage instance targeting the specified file path string.
     *
     * @param filePathStr The relative string path to the data storage file.
     */
    public Storage(String filePathStr) {
        this.filePath = Path.of(filePathStr);
    }

    /**
     * Loads saved tasks from the data file into a TaskList.
     * Returns an empty TaskList if the file does not exist, and safely skips
     * corrupted lines.
     *
     * @param ui The UserInterface instance for reporting loading errors, or null.
     * @return A TaskList containing the loaded Task objects.
     */
    public TaskList load(UserInterface ui) {
        corruptedLineCount = 0;
        ArrayList<Task> tasks = new ArrayList<>();
        File file = filePath.toFile();
        if (!file.exists()) {
            return new TaskList(tasks);
        }

        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                if (cleanLine(line).isEmpty()) {
                    continue;
                }
                Task task = decodeTask(line);
                if (task != null) {
                    tasks.add(task);
                } else {
                    corruptedLineCount++;
                }
            }
        } catch (IOException e) {
            if (ui != null) {
                ui.showLoadingError(e.getMessage());
            }
        }

        if (corruptedLineCount > 0 && ui != null) {
            ui.showError(String.format("Heads up bro, skipped %d corrupted line(s) in your save file.",
                    corruptedLineCount));
        }

        return new TaskList(tasks);
    }

    /**
     * Loads saved tasks from the data file without an explicit UserInterface
     * reference.
     *
     * @return A TaskList containing the loaded Task objects.
     */
    public TaskList load() {
        return load(null);
    }

    /**
     * Returns the count of corrupted lines encountered during the most recent load operation.
     *
     * @return The number of corrupted lines skipped.
     */
    public int getCorruptedLineCount() {
        return corruptedLineCount;
    }

    /**
     * Decodes a single formatted line from the storage file into a Task object.
     * Returns null if the line is empty, corrupted, or cannot be parsed.
     *
     * @param rawLine The raw line read from the storage file.
     * @return The constructed Task, or null if invalid or corrupted.
     */
    private Task decodeTask(String rawLine) {
        String line = cleanLine(rawLine);
        if (line.isEmpty()) {
            return null;
        }

        String[] parts = line.split(" \\| ");
        if (parts.length < 3) {
            return null;
        }

        String taskType = parts[0];
        boolean isDone = parts[1].equals("1");
        String description = parts[2];

        try {
            Task task = switch (taskType) {
                case "T" -> new Todo(description);
                case "D" -> decodeDeadline(description, parts);
                case "E" -> decodeEvent(description, parts);
                default -> null;
            };

            if (task != null && isDone) {
                task.markDone();
            }
            return task;
        } catch (BroException e) {
            // Skip tasks with corrupted or invalid date entries
            return null;
        }
    }

    /**
     * Decodes a Deadline task from the tokenized parts of a storage line.
     *
     * @param description The task description.
     * @param parts       The tokenized line parts.
     * @return The Deadline task, or null if insufficient parts.
     * @throws BroException If the deadline date string cannot be parsed.
     */
    private Deadline decodeDeadline(String description, String[] parts) throws BroException {
        if (parts.length < 4) {
            return null;
        }
        return new Deadline(description, parts[3]);
    }

    /**
     * Decodes an Event task from the tokenized parts of a storage line.
     * Supports both standard 5-part format and 4-part legacy format.
     *
     * @param description The task description.
     * @param parts       The tokenized line parts.
     * @return The Event task, or null if insufficient parts or unparseable legacy format.
     * @throws BroException If the event dates cannot be parsed.
     */
    private Event decodeEvent(String description, String[] parts) throws BroException {
        if (parts.length >= 5) {
            return new Event(description, parts[3], parts[4]);
        }
        if (parts.length == 4) {
            String[] times = parts[3].split(" /to | to | - | -|-", 2);
            if (times.length == 2) {
                return new Event(description, times[0], times[1]);
            }
        }
        return null;
    }

    /**
     * Cleans a raw storage line by stripping UTF-8 BOM characters and leading/trailing whitespace.
     *
     * @param rawLine The raw line from the storage file.
     * @return The sanitized line string.
     */
    private String cleanLine(String rawLine) {
        if (rawLine == null) {
            return "";
        }
        String line = rawLine.trim();
        if (line.startsWith("\uFEFF")) {
            line = line.substring(1).trim();
        }
        return line;
    }

    /**
     * Saves the TaskList to the storage file.
     * Creates parent directories if they do not already exist.
     *
     * @param taskList The TaskList to save.
     * @param ui       The UserInterface instance for reporting save errors, or
     *                 null.
     * @return Null if saved successfully, or an error message string if saving failed.
     */
    public String save(TaskList taskList, UserInterface ui) {
        return save(taskList.getAllTasks(), ui);
    }

    /**
     * Saves an ArrayList of tasks to the storage file.
     * Creates parent directories if they do not already exist.
     *
     * @param tasks The list of tasks to save.
     * @param ui    The UserInterface instance for reporting save errors, or null.
     * @return Null if saved successfully, or an error message string if saving failed.
     */
    public String save(ArrayList<Task> tasks, UserInterface ui) {
        try {
            File file = filePath.toFile();
            // Create parent directories if they don't exist
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }

            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                for (Task task : tasks) {
                    assert task != null : "Tasks list should not contain null elements when saving";
                    assert task.toFileFormat() != null && !task.toFileFormat().isEmpty()
                            : "toFileFormat() must produce a valid non-empty string";

                    writer.println(task.toFileFormat());
                }
            }

            return null;
        } catch (IOException e) {
            if (ui != null) {
                return ui.showSavingError(e.getMessage());
            }

            return null;
        }
    }

    /**
     * Saves the TaskList to the storage file without an explicit UserInterface
     * reference.
     *
     * @param taskList The TaskList to save.
     */
    public void save(TaskList taskList) {
        save(taskList, null);
    }

    /**
     * Returns the file path associated with this storage.
     *
     * @return The Path of the data file.
     */
    public Path getFilePath() {
        return filePath;
    }
}
