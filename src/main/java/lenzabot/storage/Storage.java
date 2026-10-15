package lenzabot.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import lenzabot.task.Deadline;
import lenzabot.task.Event;
import lenzabot.task.Task;
import lenzabot.task.Todo;

/**
 * Handles reading tasks from, and writing tasks to, a save file on disk so that
 * tasks survive across runs of the program.
 */
public class Storage {
    private final Path saveFilePath;
    private String loadWarning;

    /**
     * Creates a storage that reads and writes to the given save file path.
     *
     * @param saveFilePath Path of the file used to persist tasks.
     */
    public Storage(Path saveFilePath) {
        this.saveFilePath = saveFilePath;
    }

    /**
     * Loads tasks from the save file.
     * Returns an empty list if the file does not exist yet; lines that are not
     * in a valid format are skipped instead of crashing the program.
     *
     * @return Tasks successfully loaded from the save file.
     */
    public List<Task> loadTasks() {
        List<Task> loadedTasks = new ArrayList<>();
        loadWarning = null;
        if (!Files.exists(saveFilePath)) {
            return loadedTasks;
        }

        try {
            int skippedLineCount = 0;
            for (String line : Files.readAllLines(saveFilePath)) {
                Task task = parseTask(line);
                if (task != null) {
                    loadedTasks.add(task);
                } else {
                    skippedLineCount++;
                }
            }
            if (skippedLineCount > 0) {
                loadWarning = String.format(
                        "Skipped %d damaged line(s) in the save file.", skippedLineCount);
            }
        } catch (IOException exception) {
            loadWarning = "Could not read the save file, so Lenza started with an empty desk.";
        }
        return loadedTasks;
    }

    /**
     * Returns a warning produced while loading, if any.
     *
     * @return Load warning, or {@code null} when loading completed normally.
     */
    public String getLoadWarning() {
        return loadWarning;
    }

    /**
     * Overwrites the save file with the given list of tasks, creating the
     * parent folder first if it does not exist yet.
     *
     * @param tasks Tasks to persist in their current order.
     * @return True if all tasks were saved successfully.
     */
    public boolean saveTasks(List<Task> tasks) {
        try {
            if (saveFilePath.getParent() != null) {
                Files.createDirectories(saveFilePath.getParent());
            }
            List<String> lines = tasks.stream()
                    .map(Task::toSaveFormat)
                    .toList();
            Files.write(saveFilePath, lines);
            return true;
        } catch (IOException exception) {
            return false;
        }
    }

    // Converts a saved line such as "D | 1 | return book | Friday" back into a
    // Task, or returns null if the line is corrupted or has an unknown format.
    private Task parseTask(String line) {
        String[] fields = line.split("\\Q" + Task.SAVE_FILE_SEPARATOR + "\\E", -1);
        if (fields.length < 3 || (!fields[1].equals("0") && !fields[1].equals("1"))) {
            return null;
        }

        boolean isCompleted = fields[1].equals("1");
        switch (fields[0]) {
            case "T":
                if (fields.length != 3 || fields[2].isEmpty()) {
                    return null;
                }
                return createTask(new Todo(fields[2]), isCompleted);
            case "D":
                if (fields.length != 4 || fields[2].isEmpty() || fields[3].isEmpty()) {
                    return null;
                }
                try {
                    LocalDateTime by = LocalDateTime.parse(fields[3]);
                    return createTask(new Deadline(fields[2], by), isCompleted);
                } catch (DateTimeParseException exception) {
                    return null;
                }
            case "E":
                if (fields.length != 5 || fields[2].isEmpty() || fields[3].isEmpty() || fields[4].isEmpty()) {
                    return null;
                }
                try {
                    LocalDateTime from = LocalDateTime.parse(fields[3]);
                    LocalDateTime to = LocalDateTime.parse(fields[4]);
                    if (!to.isAfter(from)) {
                        return null;
                    }
                    return createTask(new Event(fields[2], from, to), isCompleted);
                } catch (DateTimeParseException exception) {
                    return null;
                }
            default:
                return null;
        }
    }

    // Applies the saved completion status to a freshly created task.
    private Task createTask(Task task, boolean isCompleted) {
        if (isCompleted) {
            task.markAsCompleted();
        }
        return task;
    }
}
