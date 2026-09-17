package murphy.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import murphy.MurphyException;
import murphy.task.Deadline;
import murphy.task.Event;
import murphy.task.Task;
import murphy.task.TaskList;
import murphy.task.Todo;
import murphy.ui.Ui;

/** Loads Murphy's tasks from disk and saves the current task list. */
public class Storage {
    /** Location of the task data file. */
    private final Path filePath;

    /** Maximum number of tasks that may be loaded. */
    private final int maxTasks;

    /** UI used to report recoverable file problems. */
    private final Consumer<String> messageConsumer;

    /** Creates storage for a task file. */
    public Storage(Path filePath, int maxTasks, Ui ui) {
        this(filePath, maxTasks, ui::showMessage);
    }

    /** Creates storage with a callback for recoverable file problems. */
    public Storage(Path filePath, int maxTasks, Consumer<String> messageConsumer) {
        this.filePath = filePath;
        this.maxTasks = maxTasks;
        this.messageConsumer = messageConsumer;
    }

    /** Writes the current task list to disk. */
    public void save(TaskList tasks) throws MurphyException {
        List<String> taskData = new ArrayList<>();
        for (Task task : tasks) {
            taskData.add(task.toDataString());
        }
        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.write(filePath, taskData, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new MurphyException("I changed the task list in memory, but couldn't save it to disk.");
        }
    }

    /** Reads tasks from disk, skipping blank and corrupted lines. */
    public TaskList load() {
        TaskList tasks = new TaskList();
        if (Files.notExists(filePath)) {
            return tasks;
        }
        try {
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.isBlank()) {
                    continue;
                }
                if (tasks.size() >= maxTasks) {
                    messageConsumer.accept("     OOPS! The save file has more than " + maxTasks
                            + " tasks, so I loaded only the first " + maxTasks + ".");
                    break;
                }
                try {
                    tasks.add(parseTask(line));
                } catch (IllegalArgumentException exception) {
                    messageConsumer.accept("     OOPS! I skipped corrupted save-file line " + (i + 1) + ".");
                }
            }
        } catch (IOException exception) {
            messageConsumer.accept("     OOPS! I couldn't read the save file, so I'm starting with an empty list.");
            tasks.clear();
        }
        return tasks;
    }

    /** Converts one validated save-file line into a task. */
    private Task parseTask(String line) {
        List<String> taskData = splitDataFields(line);
        if (taskData.size() < 2 || (!taskData.get(1).equals("0") && !taskData.get(1).equals("1"))) {
            throw new IllegalArgumentException("Invalid task status");
        }
        String taskType = taskData.get(0);
        int expectedFieldCount = taskType.equals("T") ? 3 : taskType.equals("D") ? 4 : taskType.equals("E") ? 5 : -1;
        if (expectedFieldCount < 0 || taskData.size() != expectedFieldCount) {
            throw new IllegalArgumentException("Incorrect task format");
        }
        for (int i = 2; i < taskData.size(); i++) {
            if (taskData.get(i).isBlank()) {
                throw new IllegalArgumentException("Missing task detail");
            }
        }
        Task task;
        if (taskType.equals("T")) {
            task = new Todo(taskData.get(2));
        } else if (taskType.equals("D")) {
            try {
                task = new Deadline(taskData.get(2), LocalDate.parse(taskData.get(3)));
            } catch (DateTimeParseException exception) {
                throw new IllegalArgumentException("Invalid deadline date", exception);
            }
        } else {
            task = new Event(taskData.get(2), taskData.get(3), taskData.get(4));
        }
        if (taskData.get(1).equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /** Splits a save-file line while preserving escaped pipes and backslashes. */
    private List<String> splitDataFields(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);
            if (character == '\\' && i + 1 < line.length()
                    && (line.charAt(i + 1) == '|' || line.charAt(i + 1) == '\\')) {
                field.append(line.charAt(++i));
            } else if (character == '|') {
                fields.add(field.toString().trim());
                field.setLength(0);
            } else {
                field.append(character);
            }
        }
        fields.add(field.toString().trim());
        return fields;
    }
}
