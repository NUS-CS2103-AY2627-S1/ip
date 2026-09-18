package sophon.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import sophon.exception.SophonException;
import sophon.model.Deadline;
import sophon.model.Event;
import sophon.model.Task;
import sophon.model.TaskList;
import sophon.model.Todo;

/**
 * Handles saving tasks to disk and loading them when Sophon starts.
 */
public class Storage {
    private final Path filePath;

    /**
     * Creates storage that reads from and writes to the given save file path.
     *
     * @param first first part of the save file path.
     * @param more remaining parts of the save file path.
     */
    public Storage(String first, String... more) {
        this.filePath = Path.of(first, more);
    }

    /**
     * Saves the current task list to the hard disk.
     *
     * @param tasks tasks to save.
     * @throws IOException if the file cannot be written.
     */
    public void saveTasks(TaskList tasks) throws IOException {
        Path parentDirectory = filePath.getParent();
        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }

        ArrayList<String> lines = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            lines.add(tasks.get(i).toFileString());
        }

        Files.write(filePath, lines, StandardCharsets.UTF_8);
    }

    /**
     * Loads tasks written on disk.
     *
     * @return task list parsed from the disk file.
     * @throws IOException if the file cannot be read.
     * @throws SophonException if the save file content is invalid.
     */
    public TaskList loadTasks() throws IOException, SophonException {
        TaskList tasks = new TaskList();

        if (!Files.exists(filePath)) {
            return tasks;
        }

        for (String line : Files.readAllLines(filePath, StandardCharsets.UTF_8)) {
            if (line.isBlank()) {
                continue;
            }

            tasks.add(parseTask(line));
        }

        return tasks;
    }

    /**
     * Parses the given tasks from saved tasks in disk.
     *
     * @param line line from the save file.
     * @return task parsed from text.
     * @throws SophonException if the line does not match the save file format.
     */
    private Task parseTask(String line) throws SophonException {
        String[] fields = parseTaskFields(line);
        Task task = createTask(fields);

        if (fields[1].equals("1")) {
            task.markAsDone();
        }

        return task;
    }

    private Task createTask(String[] fields) throws SophonException {
        String type = fields[0];
        String description = fields[2];

        if (type.equals("T")) {
            if (fields.length != 3) {
                throw new SophonException("The save file contains an invalid todo.");
            }
            return new Todo(description);
        } else if (type.equals("D")) {
            if (fields.length != 4) {
                throw new SophonException("The save file contains an invalid deadline.");
            } else if (fields[3].isBlank()) {
                throw new SophonException("The save file contains an empty deadline time.");
            }
            return new Deadline(description, convertDate(fields[3]));
        } else if (type.equals("E")) {
            if (fields.length != 5) {
                throw new SophonException("The save file contains an invalid event.");
            } else if (fields[3].isBlank() || fields[4].isBlank()) {
                throw new SophonException("The save file contains an empty event time.");
            }
            return new Event(description, convertDate(fields[3]), convertDate(fields[4]));
        } else {
            throw new SophonException("The save file contains an unknown task type.");
        }
    }

    private String[] parseTaskFields(String line) throws SophonException {
        String[] fields = line.split(" \\| ");

        if (fields.length < 3) {
            throw new SophonException("The save file contains an incomplete task.");
        }

        String status = fields[1];
        String description = fields[2];

        if (!status.equals("0") && !status.equals("1")) {
            throw new SophonException("The save file contains an invalid task status.");
        } else if (description.isBlank()) {
            throw new SophonException("The save file contains an empty task description.");
        }

        return fields;
    }

    /**
     * Converts text in yyyy-MM-dd format into a date.
     *
     * @param text date text to convert.
     * @return date represented by the text.
     * @throws SophonException if the text is not in yyyy-MM-dd format.
     */
    private LocalDate convertDate(String text) throws SophonException {
        try {
            return LocalDate.parse(text);
        } catch (DateTimeParseException e) {
            throw new SophonException("Please enter the date in yyyy-MM-dd format.");
        }
    }
}
