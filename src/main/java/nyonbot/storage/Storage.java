package nyonbot.storage;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import nyonbot.model.Deadline;
import nyonbot.model.Event;
import nyonbot.model.Task;
import nyonbot.model.TaskList;
import nyonbot.model.ToDo;

/**
 * Loads and saves tasks to a file using a delimiter-based text format.
 */
public class Storage {
    private final String filePath;
    private int skippedRecordCount;

    /**
     * Creates a storage object for the specified file path.
     *
     * @param filePath filepath of the save file.
     */
    public Storage(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads the tasks from the save file. Invalid records are skipped so that
     * one malformed record does not prevent the remaining tasks from loading.
     *
     * @return a TaskList of the tasks stored in the file.
     * @throws IOException if the storage path cannot be read.
     */
    public TaskList load() throws IOException {
        Path path = Path.of(filePath);
        TaskList list = new TaskList();
        skippedRecordCount = 0;
        if (Files.notExists(path)) {
            return list;
        }
        if (Files.isDirectory(path)) {
            throw new IOException("storage path is a directory");
        }

        try (BufferedReader fileReader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = fileReader.readLine()) != null) {
                if (!loadRecord(line, list)) {
                    skippedRecordCount++;
                }
            }
        }
        return list;
    }

    /**
     * Returns the number of records skipped by the most recent load.
     *
     * @return number of skipped records.
     */
    public int getSkippedRecordCount() {
        return skippedRecordCount;
    }

    private boolean loadRecord(String line, TaskList list) {
        String[] params = line.split("\\|", -1);
        try {
            return switch (params[0]) {
                case "TASK" -> loadTask(params, list);
                case "TODO" -> loadTodo(params, list);
                case "DEADLINE" -> loadDeadline(params, list);
                case "EVENT" -> loadEvent(params, list);
                default -> false;
            };
        } catch (DateTimeParseException | IllegalArgumentException e) {
            return false;
        }
    }

    private boolean loadTask(String[] params, TaskList list) {
        if (params.length != 3) {
            return false;
        }
        Task task = new Task(params[1]);
        updateCompletionStatus(task, params[2]);
        list.add(task);
        return true;
    }

    private boolean loadTodo(String[] params, TaskList list) {
        if (params.length != 3) {
            return false;
        }
        ToDo todo = new ToDo(params[1]);
        updateCompletionStatus(todo, params[2]);
        list.add(todo);
        return true;
    }

    private boolean loadDeadline(String[] params, TaskList list) {
        if (params.length != 4) {
            return false;
        }
        Deadline deadline = new Deadline(params[1], LocalDateTime.parse(params[3]));
        updateCompletionStatus(deadline, params[2]);
        list.add(deadline);
        return true;
    }

    private boolean loadEvent(String[] params, TaskList list) {
        if (params.length != 5) {
            return false;
        }
        LocalDateTime startTime = LocalDateTime.parse(params[3]);
        LocalDateTime endTime = LocalDateTime.parse(params[4]);
        if (!startTime.isBefore(endTime)) {
            return false;
        }
        Event event = new Event(params[1],
            startTime, endTime);
        updateCompletionStatus(event, params[2]);
        list.add(event);
        return true;
    }

    private void updateCompletionStatus(Task task, String status) {
        if (status.equals("true")) {
            task.completeTask();
        } else if (!status.equals("false")) {
            throw new IllegalArgumentException("invalid task completion status");
        }
    }

    /**
     * Returns the storage representation of the task to be stored.
     *
     * @param task the task to be stored.
     * @return the storage representation of the task.
     */
    private String taskParser(Task task) {
        if (task instanceof ToDo todo) {
            return String.format("TODO|%s|%s", todo.getName(), todo.isDone());
        }
        if (task instanceof Deadline deadline) {
            return String.format("DEADLINE|%s|%s|%s",
                    deadline.getName(),
                    deadline.isDone(),
                    deadline.getDeadline());
        }
        if (task instanceof Event event) {
            LocalDateTime[] eventTime = event.getEventTimes();
            return String.format("EVENT|%s|%s|%s|%s",
                    event.getName(),
                    event.isDone(),
                    eventTime[0],
                    eventTime[1]);
        }
        return String.format("TASK|%s|%s", task.getName(), task.isDone());
    }

    /**
     * Saves a list to the filepath of the Storage object.
     *
     * @param list the list to be saved.
     * @throws IOException if the file cannot be created or written.
     */
    public void save(TaskList list) throws IOException {
        Path path = Path.of(filePath);
        Path parent = path.toAbsolutePath().getParent();
        if (Files.exists(path) && Files.isDirectory(path)) {
            throw new IOException("storage path is a directory");
        }
        Files.createDirectories(parent);

        Path temporaryFile = Files.createTempFile(parent, path.getFileName().toString(), ".tmp");
        try {
            Files.writeString(temporaryFile, formatTasks(list), StandardCharsets.UTF_8);
            replaceStorageFile(temporaryFile, path);
        } finally {
            Files.deleteIfExists(temporaryFile);
        }
    }

    private String formatTasks(TaskList list) {
        StringBuilder output = new StringBuilder();
        for (Task task : list) {
            output.append(taskParser(task)).append(System.lineSeparator());
        }
        return output.toString();
    }

    private void replaceStorageFile(Path temporaryFile, Path path) throws IOException {
        try {
            Files.move(temporaryFile, path, StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(temporaryFile, path, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Removes all tasks from the file.
     *
     * @throws IOException if the file cannot be written.
     */
    public void wipe() throws IOException {
        save(new TaskList());
    }
}
