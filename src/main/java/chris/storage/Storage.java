package chris.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import chris.exception.ChrisException;
import chris.task.Deadline;
import chris.task.Event;
import chris.task.Task;
import chris.task.Todo;

/**
 * Loads tasks from and saves tasks to a local data file.
 */
public class Storage {
    private final Path filePath;

    /**
     * Creates storage that uses the specified data file.
     *
     * @param filePath Relative path to the data file.
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads tasks from the data file.
     *
     * @return Tasks stored in the data file, or an empty list if the file does not exist.
     * @throws ChrisException If the file cannot be read or contains invalid data.
     */
    public ArrayList<Task> loadTasks() throws ChrisException {
        ArrayList<Task> tasks = new ArrayList<>();
        if (!Files.exists(this.filePath)) {
            return tasks;
        }

        try {
            List<String> lines = Files.readAllLines(this.filePath, StandardCharsets.UTF_8);
            for (int lineIndex = 0; lineIndex < lines.size(); lineIndex++) {
                String line = lines.get(lineIndex);
                if (!line.isBlank()) {
                    tasks.add(parseTask(line, lineIndex + 1));
                }
            }
            return tasks;
        } catch (IOException exception) {
            throw new ChrisException("I couldn't read the saved tasks.", exception);
        }
    }

    /**
     * Saves all tasks to the data file, creating its parent directory when necessary.
     *
     * @param tasks Tasks to save.
     * @throws ChrisException If the tasks cannot be saved.
     */
    public void saveTasks(List<Task> tasks) throws ChrisException {
        try {
            Path parentDirectory = this.filePath.getParent();
            if (parentDirectory != null) {
                Files.createDirectories(parentDirectory);
            }

            ArrayList<String> lines = new ArrayList<>();
            for (Task task : tasks) {
                lines.add(task.toDataString());
            }
            Files.write(this.filePath, lines, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new ChrisException("I couldn't save the tasks.", exception);
        }
    }

    private Task parseTask(String line, int lineNumber) throws ChrisException {
        String[] fields = line.split("\\s*\\|\\s*", -1);
        validateStoredTask(fields, lineNumber);

        Task task;
        try {
            task = switch (fields[0]) {
                case "T" -> new Todo(fields[2]);
                case "D" -> new Deadline(fields[2], LocalDate.parse(fields[3]));
                case "E" -> new Event(fields[2], fields[3], fields[4]);
                default -> throw corruptedDataException(lineNumber);
            };
        } catch (DateTimeParseException exception) {
            throw corruptedDataException(lineNumber);
        }

        if (fields[1].equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    private void validateStoredTask(String[] fields, int lineNumber) throws ChrisException {
        if (fields.length < 3 || fields[2].isBlank()) {
            throw corruptedDataException(lineNumber);
        }
        if (!fields[1].equals("0") && !fields[1].equals("1")) {
            throw corruptedDataException(lineNumber);
        }

        boolean isValidTodo = fields[0].equals("T") && fields.length == 3;
        boolean isValidDeadline = fields[0].equals("D") && fields.length == 4 && !fields[3].isBlank();
        boolean isValidEvent = fields[0].equals("E") && fields.length == 5
                && !fields[3].isBlank() && !fields[4].isBlank();
        if (!isValidTodo && !isValidDeadline && !isValidEvent) {
            throw corruptedDataException(lineNumber);
        }
    }

    private ChrisException corruptedDataException(int lineNumber) {
        return new ChrisException("The saved data is invalid on line " + lineNumber + ".");
    }
}
