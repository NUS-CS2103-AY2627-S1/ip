package kotha;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import kotha.tasks.Constraint;
import kotha.tasks.Deadline;
import kotha.tasks.Event;
import kotha.tasks.Task;
import kotha.tasks.ToDo;

/** Saves tasks to, and restores tasks from, a local text file. */
public class Storage {
    private static final String SEPARATOR = " | ";
    private static final String TODO_CODE = "T";
    private static final String DEADLINE_CODE = "D";
    private static final String EVENT_CODE = "E";
    private static final String CONSTRAINT_CODE = "C";
    private static final String DONE_STATUS = "1";
    private static final String NOT_DONE_STATUS = "0";
    private final Path filePath;

    /** Creates storage backed by the supplied file path. */
    public Storage(String filePath) {
        this.filePath = Path.of(filePath);
    }

    /** Loads all the valid tasks stored on disk. */
    public List<Task> loadTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        if (!Files.exists(filePath)) {
            return tasks;
        }

        try {
            for (String line : Files.readAllLines(filePath)) {
                Task task = parseTask(line);
                if (task != null) {
                    tasks.add(task);
                }
            }
        } catch (IOException e) {
            System.out.println("Your Majesty, this humble butler could not load your saved tasks.");
        }
        return tasks;
    }

    /**
     * Saves the complete task list, replacing the previous saved version.
     *
     * @param tasks the list of tasks to save in disk
     */
    public void saveTasks(List<? extends Task> tasks) {
        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            List<String> lines = tasks.stream()
                    .map(this::formatTask)
                    .toList();
            Files.write(filePath, lines);
        } catch (IOException e) {
            System.out.println("Your Majesty, this humble butler could not save your tasks.");
        }
    }

    private Task parseTask(String line) {
        String[] parts = line.split(" \\| ", -1);
        if (parts.length < 3) {
            return null;
        }

        try {
            Task task = createTask(parts);
            if (task == null) {
                return null;
            }
            if (!DONE_STATUS.equals(parts[1]) && !NOT_DONE_STATUS.equals(parts[1])) {
                return null;
            }
            task.setDone(DONE_STATUS.equals(parts[1]));
            return task;
        } catch (RuntimeException exception) {
            return null;
        }
    }

    /** Creates a task from validated storage fields, or returns null for an unknown shape. */
    private Task createTask(String[] parts) {
        switch (parts[0]) {
            case TODO_CODE:
                return new ToDo(parts[2]);
            case DEADLINE_CODE:
                if (parts.length != 4) {
                    return null;
                }
                return new Deadline(parts[2], LocalDateTime.parse(parts[3]));
            case EVENT_CODE:
                if (parts.length != 5) {
                    return null;
                }
                return new Event(parts[2], LocalDateTime.parse(parts[3]),
                        LocalDateTime.parse(parts[4]));
            case CONSTRAINT_CODE:
                if (parts.length == 5 && "DATE".equals(parts[3])) {
                    return new Constraint(parts[2], LocalDateTime.parse(parts[4]));
                }
                if (parts.length != 4) {
                    return null;
                }
                return new Constraint(parts[2], parts[3]);
            default:
                return null;
        }
    }

    private String formatTask(Task task) {
        assert task != null : "Only non-null tasks can be persisted";
        String status = task.isDone() ? DONE_STATUS : NOT_DONE_STATUS;
        if (task instanceof Deadline) {
            Deadline deadline = (Deadline) task;
            return DEADLINE_CODE + SEPARATOR + status + SEPARATOR + task.getDescription()
                    + SEPARATOR + deadline.getDeadline();
        }
        if (task instanceof Event) {
            Event event = (Event) task;
            return EVENT_CODE + SEPARATOR + status + SEPARATOR + task.getDescription()
                    + SEPARATOR + event.getFrom() + SEPARATOR + event.getTo();
        }
        if (task instanceof Constraint) {
            Constraint constraint = (Constraint) task;
            if (constraint.getAfterDate() != null) {
                return CONSTRAINT_CODE + SEPARATOR + status + SEPARATOR + task.getDescription()
                        + SEPARATOR + "DATE" + SEPARATOR + constraint.getAfterDate();
            }
            return CONSTRAINT_CODE + SEPARATOR + status + SEPARATOR + task.getDescription()
                    + SEPARATOR + constraint.getAfter();
        }
        if (task instanceof ToDo) {
            return TODO_CODE + SEPARATOR + status + SEPARATOR + task.getDescription();
        }
        throw new IllegalArgumentException("Your Majesty, you have bestowed upon me an unsupported task type.");
    }
}
