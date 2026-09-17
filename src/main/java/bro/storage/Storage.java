package bro.storage;

import bro.exception.BroException;
import bro.task.Deadlines;
import bro.task.Events;
import bro.task.Task;
import bro.task.TaskList;
import bro.task.ToDos;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/** Loads tasks from a file and saves task lists in Bro's persistent format. */
public class Storage {
    private static final String FIELD_SEPARATOR = " | ";
    private static final String FIELD_SEPARATOR_REGEX = " \\| ";
    private static final String TODO_TYPE = "T";
    private static final String DEADLINE_TYPE = "D";
    private static final String EVENT_TYPE = "E";
    private static final String INCOMPLETE_FLAG = "0";
    private static final String COMPLETE_FLAG = "1";

    private final Path filePath;

    /** Creates storage backed by the supplied file path. */
    public Storage(String filePath) {
        this.filePath = Path.of(filePath);
    }

    /** Loads all saved tasks, or an empty list when the storage file does not exist. */
    public TaskList load() throws BroException {
        if (!Files.exists(filePath)) {
            return new TaskList();
        }

        try {
            List<Task> tasks = new ArrayList<>();
            for (String line : Files.readAllLines(filePath, StandardCharsets.UTF_8)) {
                if (!line.isBlank()) {
                    tasks.add(fromFileLine(line));
                }
            }
            return new TaskList(tasks);
        } catch (IOException exception) {
            throw new BroException("I couldn't load the saved task list.");
        }
    }

    /** Saves the complete task list, creating its parent directory when necessary. */
    public void save(TaskList tasks) throws BroException {
        try {
            Path parentDirectory = filePath.getParent();
            if (parentDirectory != null) {
                Files.createDirectories(parentDirectory);
            }

            ArrayList<String> lines = new ArrayList<>();
            for (Task task : tasks.getTasks()) {
                lines.add(toFileLine(task));
            }

            Files.write(filePath, lines, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new BroException("I couldn't save the task list.");
        }
    }

    /** Converts a task to one line in Bro's saved task-file format. */
    private String toFileLine(Task task) {
        assert task != null : "The task list must not contain null tasks";
        assert task instanceof ToDos || task instanceof Deadlines || task instanceof Events
                : "Every persisted task must have a supported task subtype";

        String done = task.isDone() ? COMPLETE_FLAG : INCOMPLETE_FLAG;

        if (task instanceof ToDos) {
            return TODO_TYPE + FIELD_SEPARATOR + done + FIELD_SEPARATOR + task.getDescription();
        }

        if (task instanceof Deadlines deadline) {
            return DEADLINE_TYPE + FIELD_SEPARATOR + done + FIELD_SEPARATOR + task.getDescription()
                    + FIELD_SEPARATOR + deadline.getDueDateTime() + FIELD_SEPARATOR
                    + (deadline.hasDueTime() ? COMPLETE_FLAG : INCOMPLETE_FLAG);
        }

        // Every task created by the parser or loader is one of Bro's three task types.
        assert task instanceof Events : "task must be a supported Bro task type";
        Events event = (Events) task;
        return EVENT_TYPE + FIELD_SEPARATOR + done + FIELD_SEPARATOR + task.getDescription()
                + FIELD_SEPARATOR + event.getStartTime() + FIELD_SEPARATOR + event.getEndTime();
    }

    /** Recreates one task from a line in Bro's saved task-file format. */
    private Task fromFileLine(String line) throws BroException {
        String[] parts = line.split(FIELD_SEPARATOR_REGEX, -1);

        if (parts.length < 3 || (!parts[1].equals(INCOMPLETE_FLAG) && !parts[1].equals(COMPLETE_FLAG))) {
            throw new BroException("A saved task has an invalid format.");
        }

        Task task;
        if (parts[0].equals(TODO_TYPE) && parts.length == 3) {
            task = new ToDos(parts[2]);
        } else if (parts[0].equals(DEADLINE_TYPE) && parts.length == 5) {
            task = readDeadline(parts);
        } else if (parts[0].equals(EVENT_TYPE) && parts.length == 5) {
            task = new Events(parts[3], parts[4], parts[2]);
        } else {
            throw new BroException("A saved task has an invalid format.");
        }

        task.setDone(parts[1].equals(COMPLETE_FLAG));
        return task;
    }

    /** Recreates a deadline from its ISO-8601 date-time and time-presence flag. */
    private Deadlines readDeadline(String[] parts) throws BroException {
        if (!parts[4].equals(INCOMPLETE_FLAG) && !parts[4].equals(COMPLETE_FLAG)) {
            throw new BroException("A saved task has an invalid format.");
        }

        try {
            LocalDateTime dueDateTime = LocalDateTime.parse(parts[3]);
            return new Deadlines(dueDateTime, parts[4].equals(COMPLETE_FLAG), parts[2]);
        } catch (DateTimeParseException exception) {
            throw new BroException("A saved task has an invalid format.");
        }
    }
}
