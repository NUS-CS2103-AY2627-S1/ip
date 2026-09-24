package sage.storage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import sage.model.Deadline;
import sage.model.Event;
import sage.model.Task;
import sage.model.TaskList;
import sage.model.Todo;

/** Reads and writes Sage tasks to a local text file. */
public class Storage {
    private static final String DEADLINE_TYPE = "D";
    private static final String EVENT_TYPE = "E";
    private static final String TODO_TYPE = "T";
    private static final String DONE_STATUS = "1";
    private static final String NOT_DONE_STATUS = "0";

    private final Path filePath;

    /** Creates storage backed by the supplied file path.
     *
     * @param filePath path to the task data file
     */
    public Storage(String filePath) {
        this.filePath = Paths.get(filePath);
    }

    /** Saves all tasks in the list to disk.
     *
     * @param tasks tasks to save
     * @throws IOException when the task data cannot be written
     */
    public void save(TaskList tasks) throws IOException {
        File file = new File(filePath.toUri());

        File parent = file.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IOException("Unable to create task data directory: " + parent);
        }

        try (FileWriter writer = new FileWriter(file)) {
            for (Task task : tasks) {
                String type;
                String details = "";

                if (task instanceof Deadline) {
                    type = DEADLINE_TYPE;
                    details = ((Deadline) task).getBy().toString();
                } else if (task instanceof Event) {
                    type = EVENT_TYPE;
                    details = ((Event) task).getFrom().toString() + "|"
                            + ((Event) task).getTo().toString();
                } else {
                    type = TODO_TYPE;
                }

                writer.write(type + "|" + (task.isDone() ? DONE_STATUS : NOT_DONE_STATUS) + "|"
                        + task.getDescription() + "|" + details);
                writer.write(System.lineSeparator());
            }
        }
    }

    /** Loads all valid tasks from disk, or an empty list when no file exists.
     *
     * @return loaded tasks
     * @throws IOException when the task data cannot be read or is invalid
     */
    public TaskList load() throws IOException {
        TaskList tasks = new TaskList();

        if (!Files.exists(filePath)) {
            return tasks;
        }

        int lineNumber = 0;
        for (String line : Files.readAllLines(filePath, StandardCharsets.UTF_8)) {
            lineNumber++;
            String[] parts = line.split("\\|", -1);
            if (parts.length < 4 || !parts[1].equals(DONE_STATUS) && !parts[1].equals(NOT_DONE_STATUS)) {
                throw new IOException("Invalid task data on line " + lineNumber + ".");
            }
            Task task;
            try {
                if (parts[0].equals(DEADLINE_TYPE)) {
                    task = new Deadline(parts[2], LocalDateTime.parse(parts[3]));
                } else if (parts[0].equals(EVENT_TYPE) && parts.length >= 5) {
                    task = new Event(parts[2], LocalDateTime.parse(parts[3]), LocalDateTime.parse(parts[4]));
                } else if (parts[0].equals(TODO_TYPE)) {
                    task = new Todo(parts[2]);
                } else {
                    throw new IOException("Invalid task data on line " + lineNumber + ".");
                }
            } catch (RuntimeException e) {
                throw new IOException("Invalid task data on line " + lineNumber + ".", e);
            }

            if (parts[1].equals(DONE_STATUS)) {
                task.markAsDone();
            }
            tasks.add(task);
        }
        return tasks;
    }

}
