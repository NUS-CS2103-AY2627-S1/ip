package clarry.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import clarry.ClarryException;
import clarry.task.Deadline;
import clarry.task.Event;
import clarry.task.Task;
import clarry.task.Todo;

/**
 * Stores tasks in and loads tasks from Clarry's save file.
 */
public class Storage {
    private final Path dataFile;

    /**
     * Creates storage that uses the specified save-file path.
     *
     * @param dataFile path of the save file
     */
    public Storage(Path dataFile) {
        assert dataFile != null : "Storage requires a data-file path";
        this.dataFile = dataFile;
    }

    /**
     * Saves all tasks to the save file.
     *
     * @param tasks tasks to save
     * @throws IOException if the save file or its parent directory cannot be written
     */
    public void save(Iterable<Task> tasks) throws IOException {
        assert tasks != null : "Storage requires tasks to save";
        Path target = dataFile.toAbsolutePath();
        Files.createDirectories(target.getParent());
        Path temporary = Files.createTempFile(target.getParent(), "clarry-", ".tmp");
        try {
            try (BufferedWriter writer = Files.newBufferedWriter(temporary)) {
                for (Task task : tasks) {
                    writer.write(task.toFileFormat());
                    writer.newLine();
                }
            }
            // Replace the old file only after every task has been written successfully.
            try {
                Files.move(temporary, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporary);
        }
    }

    /**
     * Loads tasks from the save file, skipping malformed lines.
     *
     * @return loaded tasks and the number of malformed lines skipped
     * @throws IOException if the save file cannot be read
     */
    public LoadResult load() throws IOException {
        List<Task> tasks = new ArrayList<>();
        int corruptedLineCount = 0;
        try (BufferedReader reader = Files.newBufferedReader(dataFile)) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    Task task = parseSavedTask(line);
                    if (tasks.stream().anyMatch(task::hasSameDetails)) {
                        throw new IllegalArgumentException("Duplicate saved task");
                    }
                    tasks.add(task);
                } catch (ClarryException | IllegalArgumentException e) {
                    corruptedLineCount++;
                }
            }
        } catch (NoSuchFileException e) {
            return new LoadResult(tasks, 0);
        }
        return new LoadResult(tasks, corruptedLineCount);
    }

    /**
     * Reconstructs a task from one line in the save-file format.
     *
     * @param line saved task data
     * @return reconstructed task
     * @throws IllegalArgumentException if the saved data is structurally invalid
     * @throws ClarryException if a saved date or time is invalid
     */
    private Task parseSavedTask(String line) throws ClarryException {
        String[] parts = line.split(" \\| ", -1);
        if (parts.length < 3 || !(parts[1].equals("0") || parts[1].equals("1"))
                || parts[2].isBlank() || parts[2].contains("|")
                || parts[2].chars().anyMatch(Character::isISOControl)) {
            throw new IllegalArgumentException("Invalid task data");
        }

        Task task;
        switch (parts[0]) {
            case "T":
                if (parts.length != 3) {
                    throw new IllegalArgumentException("Invalid todo data");
                }
                task = new Todo(parts[2]);
                break;
            case "D":
                if (parts.length != 4 || parts[3].isEmpty()) {
                    throw new IllegalArgumentException("Invalid deadline data");
                }
                task = new Deadline(parts[2], parts[3]);
                break;
            case "E":
                if (parts.length != 5 || parts[3].isEmpty() || parts[4].isEmpty()) {
                    throw new IllegalArgumentException("Invalid event data");
                }
                task = new Event(parts[2], parts[3], parts[4]);
                break;
            default:
                throw new IllegalArgumentException("Unknown task type");
        }

        if (parts[1].equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /** Contains the result of one save-file load operation. */
    public static class LoadResult {
        private final List<Task> tasks;
        private final int corruptedLineCount;

        /**
         * Creates a load result.
         *
         * @param tasks loaded tasks
         * @param corruptedLineCount number of malformed lines skipped
         */
        public LoadResult(List<Task> tasks, int corruptedLineCount) {
            assert tasks != null : "A load result requires a task collection";
            assert corruptedLineCount >= 0 : "Corrupted-line count cannot be negative";
            this.tasks = tasks;
            this.corruptedLineCount = corruptedLineCount;
        }

        /**
         * Returns the tasks loaded from the save file.
         *
         * @return loaded tasks
         */
        public List<Task> getTasks() {
            return tasks;
        }

        /**
         * Returns the number of malformed lines skipped while loading.
         *
         * @return number of skipped lines
         */
        public int getCorruptedLineCount() {
            return corruptedLineCount;
        }
    }
}
