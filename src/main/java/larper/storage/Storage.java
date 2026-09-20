package larper.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Collectors;

import larper.task.Deadline;
import larper.task.Event;
import larper.task.Task;
import larper.task.TaskDateTime;
import larper.task.TaskDateTimeParser;
import larper.task.Todo;

/**
 * Loads tasks from Larper's data file and saves tasks back to the data file.
 */
public class Storage {
    private Path filePath;
    private ArrayList<Integer> skippedLineNumbers;

    /**
     * Creates storage that reads and writes the specified task data file.
     *
     * @param filePath Path of the task data file.
     */
    public Storage(Path filePath) {
        assert filePath != null : "Storage should always receive a data file path.";
        this.filePath = filePath;
        skippedLineNumbers = new ArrayList<>();
    }

    /**
     * Saves all tasks to the data file, creating the parent directory if needed.
     *
     * @param tasks Tasks to save.
     * @throws IOException If the data file cannot be written.
     */
    public void saveTasks(ArrayList<Task> tasks) throws IOException {
        assert tasks != null : "Storage should save an existing task collection.";
        Path parentDirectory = filePath.getParent();
        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }

        ArrayList<String> lines = tasks.stream()
                .map(task -> {
                    assert task != null : "Storage should not save null task entries.";
                    return task.toFileString();
                })
                .collect(Collectors.toCollection(ArrayList::new));

        Files.write(filePath, lines, StandardCharsets.UTF_8);
    }

    /**
     * Returns all valid tasks loaded from the data file.
     * If the data file does not exist, an empty task list is returned.
     *
     * @throws IOException If the data file exists but cannot be read.
     */
    public ArrayList<Task> loadTasks() throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();
        skippedLineNumbers.clear();
        if (!Files.exists(filePath)) {
            return tasks;
        }

        ArrayList<String> lines = new ArrayList<>(Files.readAllLines(filePath, StandardCharsets.UTF_8));
        for (int lineIndex = 0; lineIndex < lines.size(); lineIndex++) {
            String line = lines.get(lineIndex);
            if (line.isBlank()) {
                continue;
            }
            try {
                tasks.add(parseTask(line));
            } catch (RuntimeException e) {
                skippedLineNumbers.add(lineIndex + 1);
            }
        }
        return tasks;
    }

    /**
     * Returns the one-based line numbers skipped during the latest load.
     */
    public ArrayList<Integer> getSkippedLineNumbers() {
        return new ArrayList<>(skippedLineNumbers);
    }

    private Task parseTask(String line) {
        String[] parts = line.split("\\s*\\|\\s*");
        String type = parts[0];
        boolean isDone = parts[1].equals("1");
        String description = parts[2];
        Task task;
        int tagFieldIndex;

        if (type.equals("T")) {
            task = new Todo(description);
            tagFieldIndex = 3;
        } else if (type.equals("D")) {
            task = parseDeadline(description, parts);
            tagFieldIndex = parts.length >= 5 ? 5 : parts.length;
        } else if (type.equals("E")) {
            task = parseEvent(description, parts);
            tagFieldIndex = parts.length >= 7 ? 7 : parts.length;
        } else {
            throw new IllegalArgumentException("Unknown task type.");
        }

        task.setDone(isDone);
        parseTags(parts, tagFieldIndex, task);
        return task;
    }

    private void parseTags(String[] parts, int tagFieldIndex, Task task) {
        if (parts.length <= tagFieldIndex) {
            return;
        }
        if (parts.length > tagFieldIndex + 1) {
            throw new IllegalArgumentException("Task tags should be stored in one field.");
        }

        String tagField = parts[tagFieldIndex].trim();
        if (tagField.isEmpty()) {
            return;
        }
        for (String tag : tagField.split("\\s+")) {
            task.addTag(tag);
        }
    }

    private Deadline parseDeadline(String description, String[] parts) {
        if (parts.length >= 5) {
            return new Deadline(description, TaskDateTimeParser.parseDate(parts[3]),
                    TaskDateTimeParser.normalizeTime(parts[4]));
        }

        TaskDateTime deadlineDateTime = TaskDateTimeParser.parse(parts[3], "no time");
        return new Deadline(description, deadlineDateTime.getDate(), deadlineDateTime.getTime());
    }

    private Event parseEvent(String description, String[] parts) {
        if (parts.length >= 7) {
            return new Event(description, LocalDate.parse(parts[3]), TaskDateTimeParser.normalizeTime(parts[4]),
                    LocalDate.parse(parts[5]), TaskDateTimeParser.normalizeTime(parts[6]));
        }

        return new Event(description, parts[3], parts[4]);
    }
}
