package aegis;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

import aegis.task.Deadline;
import aegis.task.Event;
import aegis.task.Task;
import aegis.task.Todo;

/**
 * Handles loading and saving tasks to a local storage file specified by the filePath.
 */
public class Storage {

    private static final int TODO_FIELD_COUNT = 3;
    private static final int DEADLINE_FIELD_COUNT = 4;
    private static final int EVENT_FIELD_COUNT = 5;

    private static final String STATUS_NOT_DONE = "0";
    private static final String STATUS_DONE = "1";

    private final String filePath;

    /**
     * Creates a Storage object that reads and writes to the given file path.
     */
    public Storage(String filePath) {
        assert filePath != null : "Storage file path should not be null";
        assert !filePath.isBlank() : "Storage file path should not be blank";
        this.filePath = filePath;
    }

    /**
     * Creates the storage file and parent directories if they do not exist.
     */
    public void ensureFileExists(Path path) throws IOException {
        if (Files.exists(path)) {
            return;
        }

        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }
        Files.createFile(path);
    }

    /**
     * Checks whether a saved task status value is invalid.
     */
    private boolean isInvalidStatus(String status) {
        return !status.equals(STATUS_NOT_DONE) && !status.equals(STATUS_DONE);
    }

    /**
     * Adds a loaded task only if the save file has not already listed the same task details.
     */
    private void addLoadedTask(ArrayList<Task> loadedTasks, Task task) throws AegisException {
        boolean isDuplicate = loadedTasks.stream().anyMatch(existingTask -> existingTask.hasSameDetails(task));
        if (isDuplicate) {
            throw new AegisException("Duplicate task in file");
        }
        loadedTasks.add(task);
    }

    /**
     * Parses the task line and initializes the loadedTasks list used to create a TaskList.
     */
    public void parseTaskLine(String currLine, ArrayList<Task> loadedTasks) throws AegisException {
        assert currLine != null : "Saved task line should not be null";
        assert loadedTasks != null : "Loaded task list should not be null";
        if (currLine.isBlank()) {
            return;
        }

        String[] parts = currLine.split(" \\| ");

        if (parts.length < 2) {
            throw new AegisException("Incorrect format in file");
        }

        String identifier = parts[0].trim();
        String status = parts[1].trim();
        if (isInvalidStatus(status)) {
            throw new AegisException("Incorrect task status in file");
        }

        boolean isDone = status.equals(STATUS_DONE);
        try {
            switch (identifier) {
                case "T": {
                    if (parts.length != TODO_FIELD_COUNT || parts[2].trim().isEmpty()) {
                        throw new AegisException("Incorrect todo format in file");
                    }
                    addLoadedTask(loadedTasks, new Todo(parts[2].trim(), isDone));
                    break;
                }
                case "D": {
                    if (parts.length != DEADLINE_FIELD_COUNT || parts[2].trim().isEmpty()) {
                        throw new AegisException("Incorrect deadline format in file");
                    }
                    addLoadedTask(loadedTasks,
                            new Deadline(parts[2].trim(), LocalDate.parse(parts[3].trim()), isDone));
                    break;
                }
                case "E": {
                    if (parts.length != EVENT_FIELD_COUNT || parts[2].trim().isEmpty()) {
                        throw new AegisException("Incorrect event format in file");
                    }
                    LocalDate start = LocalDate.parse(parts[3].trim());
                    LocalDate end = LocalDate.parse(parts[4].trim());
                    if (!start.isBefore(end)) {
                        throw new AegisException("Incorrect event date order in file");
                    }
                    addLoadedTask(loadedTasks, new Event(parts[2].trim(), start, end, isDone));
                    break;
                }
                default:
                    throw new AegisException("Unknown information in file");
            }
        } catch (DateTimeParseException e) {
            throw new AegisException("Incorrect date format in file");
        }
    }

    /**
     * Loads all tasks from the storage file.
     * If the file does not exist, it will be created (along with any parent directories).
     *
     * @return The list of tasks loaded from the file.
     * @throws AegisException If a line in the file is malformed, contains an unknown
     *                        task type, or has an invalid status or date.
     * @throws IOException If the file cannot be read or created.
     */
    public ArrayList<Task> load() throws AegisException, IOException {
        Path path = Paths.get(filePath);
        assert path != null : "Storage path should be created from the configured file path";

        ensureFileExists(path);

        ArrayList<Task> loadedTasks = new ArrayList<>();
        try (Scanner s = new Scanner(path)) {
            while (s.hasNext()) {
                String currLine = s.nextLine();
                parseTaskLine(currLine, loadedTasks);
            }
        }
        return loadedTasks;
    }

    /**
     * Saves all tasks in the provided list to the storage file.
     * Each task is written on its own line using its file save format.
     *
     * @param taskList The list of tasks to save to the file.
     * @throws IOException If the file cannot be written to.
     */
    public void saveToFile(TaskList taskList) throws IOException {
        StringBuilder textToAdd = new StringBuilder();
        for (int i = 0; i < taskList.size(); i++) {
            textToAdd.append(taskList.get(i).getFileSaveFormat())
                    .append(System.lineSeparator());
        }

        try (FileWriter fw = new FileWriter(filePath)) {
            fw.write(textToAdd.toString());
        }
    }
}
