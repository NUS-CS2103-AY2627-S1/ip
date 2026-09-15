package monkey.storage;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import monkey.model.Deadline;
import monkey.model.Event;
import monkey.model.Task;
import monkey.model.ToDos;

/** Saves Monkey's task list in a simple, human-readable text format. */
public class Storage {
    private final Path filePath;
    private String lastError;

    /** Creates storage backed by the given file path. */
    public Storage(String filePath) {
        this.filePath = Paths.get(filePath);
    }

    /** Reads the saved task list, returning an empty list when no save exists yet. */
    public ArrayList<Task> load() {
        lastError = null;
        ArrayList<Task> tasks = new ArrayList<>();
        if (!Files.exists(filePath)) {
            return tasks;
        }

        try {
            List<String> lines = Files.readAllLines(filePath);
            ArrayList<Integer> malformedLineNumbers = new ArrayList<>();
            ArrayList<Integer> duplicateLineNumbers = new ArrayList<>();
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                Task task = parseTask(line);
                if (task != null && containsEquivalent(tasks, task)) {
                    duplicateLineNumbers.add(i + 1);
                } else if (task != null) {
                    tasks.add(task);
                } else if (!line.trim().isEmpty()) {
                    malformedLineNumbers.add(i + 1);
                }
            }
            lastError = formatLoadWarnings(malformedLineNumbers, duplicateLineNumbers);
        } catch (IOException | SecurityException e) {
            lastError = "Could not read saved tasks from disk.";
        }
        return tasks;
    }

    /** Writes the current task list to disk and returns whether the save succeeded. */
    public boolean save(List<Task> tasks) {
        lastError = null;
        if (tasks == null) {
            lastError = "Could not save tasks to disk.";
            return false;
        }
        Path temporaryPath = filePath.resolveSibling(filePath.getFileName() + ".tmp");
        try {
            writeTasks(temporaryPath, tasks);
            replaceSaveFile(temporaryPath);
            return true;
        } catch (IOException | SecurityException e) {
            lastError = "Could not save tasks to disk.";
            try {
                Files.deleteIfExists(temporaryPath);
            } catch (IOException | SecurityException ignored) {
                // Preserve the original save error.
            }
            return false;
        }
    }

    /** Returns the most recent loading or saving error, or null when none occurred. */
    public String getLastError() {
        return lastError;
    }

    private void writeTasks(Path temporaryPath, List<Task> tasks) throws IOException {
        Path parent = filePath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        try (BufferedWriter writer = Files.newBufferedWriter(temporaryPath)) {
            for (Task task : tasks) {
                if (task != null) {
                    writer.write(formatTask(task));
                    writer.newLine();
                }
            }
        }
    }

    private static String formatMalformedDataError(List<Integer> lineNumbers) {
        if (lineNumbers.size() == 1) {
            return "Ignored malformed saved task data on line " + lineNumbers.get(0) + ".";
        }
        return "Ignored malformed saved task data on " + lineNumbers.size() + " lines.";
    }

    private static String formatDuplicateDataError(List<Integer> lineNumbers) {
        if (lineNumbers.size() == 1) {
            return "Ignored duplicate saved task data on line " + lineNumbers.get(0) + ".";
        }
        return "Ignored duplicate saved task data on " + lineNumbers.size() + " lines.";
    }

    private static String formatLoadWarnings(List<Integer> malformedLines, List<Integer> duplicateLines) {
        String malformedWarning = malformedLines.isEmpty() ? null : formatMalformedDataError(malformedLines);
        String duplicateWarning = duplicateLines.isEmpty() ? null : formatDuplicateDataError(duplicateLines);
        if (malformedWarning == null) {
            return duplicateWarning;
        }
        return duplicateWarning == null ? malformedWarning : malformedWarning + " " + duplicateWarning;
    }

    private static boolean containsEquivalent(List<Task> tasks, Task candidate) {
        return tasks.stream().anyMatch(task -> task.hasSameDetails(candidate));
    }

    private void replaceSaveFile(Path temporaryPath) throws IOException {
        try {
            Files.move(temporaryPath, filePath, StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            Files.move(temporaryPath, filePath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static String formatTask(Task task) {
        String type = task instanceof Deadline ? "D" : task instanceof Event ? "E" : "T";
        String details = task.getDescription();
        if (task instanceof Deadline deadline) {
            details += " | " + deadline.getStorageValue();
        } else if (task instanceof Event event) {
            details += " | " + event.getFrom() + " | " + event.getTo();
        }
        return type + " | " + (task.isDone() ? "1" : "0") + " | " + details;
    }

    /** Reconstructs a task from one storage line, or returns null when invalid. */
    private static Task parseTask(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }
        String[] fields = line.split("\\s*\\|\\s*", -1);
        try {
            if (fields.length < 3 || fields[2].trim().isEmpty()
                    || !("0".equals(fields[1].trim()) || "1".equals(fields[1].trim()))) {
                return null;
            }
            Task task;
            switch (fields[0].trim()) {
            case "T":
                if (fields.length != 3) {
                    return null;
                }
                task = new ToDos(fields[2].trim());
                break;
            case "D":
                if (fields.length != 4 || fields[3].trim().isEmpty()) {
                    return null;
                }
                task = new Deadline(fields[2].trim(), fields[3].trim());
                break;
            case "E":
                if (fields.length != 5 || fields[3].trim().isEmpty() || fields[4].trim().isEmpty()) {
                    return null;
                }
                task = new Event(fields[2].trim(), fields[3].trim(), fields[4].trim());
                break;
            default:
                return null;
            }
            if ("1".equals(fields[1].trim())) {
                task.markAsDone();
            }
            return task;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
