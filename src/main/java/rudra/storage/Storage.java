package rudra.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import rudra.exception.RudraException;
import rudra.task.Deadline;
import rudra.task.Event;
import rudra.task.Task;
import rudra.task.TaskDateTime;
import rudra.task.ToDo;

/**
 * Saves task data to a hard disk file and loads it back on startup.
 */
public class Storage {
    private static final String FIELD_SEPARATOR = " | ";
    private final Path filePath;

    /**
     * Captures the outcome of loading tasks from disk, including any skipped corrupted records.
     */
    public static class LoadResult {
        private final ArrayList<Task> tasks;
        private final int skippedTaskCount;

        /**
         * Creates a load result.
         *
         * @param tasks Loaded tasks.
         * @param skippedTaskCount Number of corrupted saved lines ignored during loading.
         */
        public LoadResult(ArrayList<Task> tasks, int skippedTaskCount) {
            assert tasks != null : "A load result must contain an initialized task list";
            assert skippedTaskCount >= 0 : "The skipped task count cannot be negative";

            this.tasks = tasks;
            this.skippedTaskCount = skippedTaskCount;
        }

        /**
         * Returns the successfully loaded tasks.
         *
         * @return Loaded tasks.
         */
        public ArrayList<Task> getTasks() {
            return this.tasks;
        }

        /**
         * Returns how many corrupted saved lines were ignored.
         *
         * @return Count of skipped lines.
         */
        public int getSkippedTaskCount() {
            return this.skippedTaskCount;
        }
    }

    /**
     * Creates a storage helper that writes to the given relative path.
     *
     * @param relativeFilePath Relative path of the save file from the project root.
     */
    public Storage(String relativeFilePath) {
        assert relativeFilePath != null && !relativeFilePath.isBlank() : "Storage must have a file path";

        this.filePath = Path.of(relativeFilePath);
    }

    /**
     * Saves all tasks to disk, replacing any previous file contents.
     *
     * @param tasks Current task list to save.
     */
    public void saveTasks(List<Task> tasks) throws RudraException {
        assert tasks != null : "The task list must be initialized before it can be saved";
        assert tasks.stream().allMatch(task -> task != null) : "The task list cannot contain null entries";

        try {
            Path parentDirectory = this.filePath.getParent();
            if (parentDirectory != null) {
                Files.createDirectories(parentDirectory);
            }

            String fileContents = tasks.stream()
                    .map(Task::toStorageString)
                    .reduce((first, second) -> first + System.lineSeparator() + second)
                    .orElse("");

            if (!fileContents.isEmpty()) {
                fileContents += System.lineSeparator();
            }

            Files.writeString(this.filePath, fileContents);
        } catch (IOException e) {
            throw new RudraException("I couldn't save your tasks to " + this.filePath + ".");
        }
    }

    /**
     * Loads all previously saved tasks from disk.
     *
     * @return Task list restored from the save file, plus the count of corrupted lines skipped.
     */
    public LoadResult loadTasks() throws RudraException {
        if (!Files.exists(this.filePath)) {
            return new LoadResult(new ArrayList<>(), 0);
        }

        try {
            List<String> lines = Files.readAllLines(this.filePath);
            ArrayList<Task> loadedTasks = new ArrayList<>();
            int skippedTaskCount = 0;

            for (String line : lines) {
                if (!line.isBlank()) {
                    try {
                        loadedTasks.add(parseTask(line));
                    } catch (RudraException e) {
                        skippedTaskCount++;
                    }
                }
            }

            return new LoadResult(loadedTasks, skippedTaskCount);
        } catch (IOException e) {
            throw new RudraException("I couldn't read the saved tasks from " + this.filePath + ".");
        }
    }

    /**
     * Converts one saved line back into a task.
     *
     * @param line Saved task line.
     * @return Reconstructed task.
     * @throws RudraException If the saved line does not match a supported task format.
     */
    private Task parseTask(String line) throws RudraException {
        List<String> parts = splitStorageLine(line);
        if (parts.size() < 3) {
            throw new RudraException("Saved task is missing required fields.");
        }

        Task task = parseTaskDetails(parts);
        restoreTaskStatus(task, parts.get(1));
        assert task != null : "A recognized saved task type must produce a task";
        return task;
    }

    private Task parseTaskDetails(List<String> parts) throws RudraException {
        switch (parts.get(0)) {
            case "T":
                return parseTodoTask(parts);
            case "D":
                return parseDeadlineTask(parts);
            case "E":
                return parseEventTask(parts);
            default:
                throw new RudraException("Saved task type is not recognized.");
        }
    }

    private Task parseTodoTask(List<String> parts) throws RudraException {
        if (parts.size() != 3 || parts.get(2).isBlank()) {
            throw new RudraException("Saved todo task is invalid.");
        }
        return new ToDo(parts.get(2));
    }

    private Task parseDeadlineTask(List<String> parts) throws RudraException {
        if (parts.size() != 4 || parts.get(2).isBlank() || parts.get(3).isBlank()) {
            throw new RudraException("Saved deadline task is invalid.");
        }
        return new Deadline(parts.get(2), TaskDateTime.parse(parts.get(3)));
    }

    private Task parseEventTask(List<String> parts) throws RudraException {
        if (parts.size() != 5 || parts.get(2).isBlank() || parts.get(3).isBlank() || parts.get(4).isBlank()) {
            throw new RudraException("Saved event task is invalid.");
        }
        return new Event(parts.get(2), TaskDateTime.parse(parts.get(3)), TaskDateTime.parse(parts.get(4)));
    }

    private void restoreTaskStatus(Task task, String status) throws RudraException {
        switch (status) {
            case "1":
                task.markAsDone();
                break;
            case "0":
                break;
            default:
                throw new RudraException("Saved task status is invalid.");
        }
    }

    /**
     * Splits one saved line into fields, honoring escape characters in field values.
     *
     * @param line Saved task line.
     * @return Parsed fields with escape sequences removed.
     */
    private List<String> splitStorageLine(String line) {
        ArrayList<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean isEscaped = false;

        for (int i = 0; i < line.length(); i++) {
            char currentCharacter = line.charAt(i);

            if (isEscaped) {
                currentField.append(currentCharacter);
                isEscaped = false;
                continue;
            }

            if (currentCharacter == '\\') {
                isEscaped = true;
                continue;
            }

            if (line.startsWith(FIELD_SEPARATOR, i)) {
                fields.add(currentField.toString());
                currentField.setLength(0);
                i += FIELD_SEPARATOR.length() - 1;
                continue;
            }

            currentField.append(currentCharacter);
        }

        if (isEscaped) {
            currentField.append('\\');
        }

        fields.add(currentField.toString());
        return fields;
    }
}
