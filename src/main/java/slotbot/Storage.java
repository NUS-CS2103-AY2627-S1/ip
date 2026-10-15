package slotbot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import slotbot.task.Deadline;
import slotbot.task.Event;
import slotbot.task.Task;
import slotbot.task.TaskList;
import slotbot.task.Todo;

/**
 * Loads and saves SlotBot tasks in a text file.
 */
public class Storage {
    private static final DateTimeFormatter EVENT_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm", Locale.ENGLISH)
                    .withResolverStyle(ResolverStyle.STRICT);

    private final Path saveFilePath;
    private final Ui ui;

    /**
     * Creates storage for the given file path.
     *
     * @param saveFilePath Path of the task data file.
     * @param ui User interface used to report persistence problems.
     */
    public Storage(Path saveFilePath, Ui ui) {
        this.saveFilePath = saveFilePath;
        this.ui = ui;
    }

    /**
     * Saves all tasks to the configured file.
     *
     * @param tasks Tasks to save.
     */
    public void saveTasks(TaskList tasks) {
        try {
            Files.createDirectories(saveFilePath.getParent());

            List<String> taskLines = new ArrayList<>();
            for (Task task : tasks.getTasks()) {
                taskLines.add(formatTaskForSaving(task));
            }
            Files.write(saveFilePath, taskLines);
        } catch (IOException e) {
            ui.showSaveError();
        }
    }

    /**
     * Loads all previously saved tasks, or returns an empty list for a first launch.
     *
     * @return Tasks loaded from the configured file.
     */
    public List<Task> loadTasks() {
        if (!Files.exists(saveFilePath)) {
            return new ArrayList<>();
        }

        List<Task> tasks = new ArrayList<>();
        List<String> taskLines;
        try {
            taskLines = Files.readAllLines(saveFilePath);
        } catch (IOException e) {
            ui.showLoadError();
            return tasks;
        }

        for (int i = 0; i < taskLines.size(); i++) {
            try {
                tasks.add(parseSavedTask(taskLines.get(i)));
            } catch (RuntimeException e) {
                ui.showInvalidTaskDataWarning(i + 1);
            }
        }
        return tasks;
    }

    /**
     * Converts one saved line into a task.
     *
     * @param taskLine Save-file representation of a task.
     * @return Task represented by the saved line.
     */
    private Task parseSavedTask(String taskLine) {
        String[] fields = taskLine.split("\\s*\\|\\s*", -1);
        if (fields.length < 2 || (!fields[1].equals("0") && !fields[1].equals("1"))) {
            throw new IllegalArgumentException("Invalid task status.");
        }

        boolean isDone = fields[1].equals("1");
        Task task = switch (fields[0]) {
            case "T" -> {
                validateTaskFields(fields, 3);
                yield new Todo(fields[2]);
            }
            case "D" -> {
                validateTaskFields(fields, 4);
                yield new Deadline(fields[2], LocalDate.parse(fields[3]));
            }
            case "E" -> {
                validateTaskFields(fields, 5);
                yield new Event(
                        fields[2],
                        LocalDateTime.parse(fields[3], EVENT_DATE_TIME_FORMATTER),
                        LocalDateTime.parse(fields[4], EVENT_DATE_TIME_FORMATTER));
            }
            default -> throw new IllegalArgumentException("Unknown task type.");
        };

        if (isDone) {
            task.markDone();
        }
        return task;
    }

    /**
     * Checks that a saved task has all required non-empty fields.
     *
     * @param fields Fields from one saved task line.
     * @param expectedFieldCount Expected number of fields for the task type.
     */
    private void validateTaskFields(String[] fields, int expectedFieldCount) {
        if (fields.length != expectedFieldCount) {
            throw new IllegalArgumentException("Incorrect number of task fields.");
        }

        for (int i = 2; i < fields.length; i++) {
            if (fields[i].isBlank()) {
                throw new IllegalArgumentException("Empty task field.");
            }
        }
    }

    /**
     * Formats one task as a line in the save file.
     *
     * @param task Task to format.
     * @return Save-file representation of the task.
     */
    private String formatTaskForSaving(Task task) {
        String completionStatus = task.isDone() ? "1" : "0";

        if (task instanceof Todo) {
            return "T | %s | %s".formatted(completionStatus, task.getDescription());
        }
        if (task instanceof Deadline deadline) {
            return "D | %s | %s | %s".formatted(
                    completionStatus, deadline.getDescription(), deadline.getDate());
        }
        if (task instanceof Event event) {
            return "E | %s | %s | %s | %s".formatted(
                    completionStatus,
                    event.getDescription(),
                    event.getFrom().format(EVENT_DATE_TIME_FORMATTER),
                    event.getTo().format(EVENT_DATE_TIME_FORMATTER));
        }

        return "T | %s | %s".formatted(completionStatus, task.getDescription());
    }
}
