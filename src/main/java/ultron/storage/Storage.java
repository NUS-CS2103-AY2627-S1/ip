package ultron.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import ultron.exception.UltronException;
import ultron.model.Deadline;
import ultron.model.Event;
import ultron.model.Task;
import ultron.model.Todo;

/**
 * Saves tasks to, and loads tasks from, the application's data file.
 */
public class Storage {
    private final Path saveFile;

    /**
     * Creates storage that uses the given file path.
     *
     * @param filePath location of the data file
     */
    public Storage(String filePath) {
        this.saveFile = Path.of(filePath);
    }

    /**
     * Writes the current task list to the data file.
     *
     * @param tasks the tasks to save
     */
    public void saveTasks(List<Task> tasks) {
        ArrayList<String> taskLines = new ArrayList<>();
        for (Task task : tasks) {
            String taskLine = task.getType().getSymbol() + " | " + (task.isDone() ? "1" : "0")
                    + " | " + task.getDescription();
            taskLines.add(taskLine);
        }

        try {
            Path parentDirectory = saveFile.getParent();
            if (parentDirectory != null) {
                Files.createDirectories(parentDirectory);
            }
            Files.write(saveFile, taskLines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println(" OOPS!!! I could not save your tasks.");
        }
    }

    /**
     * Loads previously saved tasks. A missing data file means there are no saved tasks yet.
     *
     * @return the tasks restored from storage
     */
    public ArrayList<Task> loadTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        if (!Files.exists(saveFile)) {
            return tasks;
        }

        try {
            for (String taskLine : Files.readAllLines(saveFile, StandardCharsets.UTF_8)) {
                tasks.add(createTaskFromSavedLine(taskLine));
            }
        } catch (IOException | UltronException e) {
            System.out.println(" OOPS!!! I could not load your saved tasks.");
        }
        return tasks;
    }

    private Task createTaskFromSavedLine(String taskLine) throws UltronException {
        String[] parts = taskLine.split(" \\| ", 3);
        if (parts.length != 3) {
            throw new UltronException("Invalid saved task");
        }

        Task task = switch (parts[0]) {
            case "T" -> new Todo(parts[2]);
            case "D" -> createSavedDeadline(parts[2]);
            case "E" -> createSavedEvent(parts[2]);
            default -> throw new UltronException("Invalid saved task type");
        };
        if (parts[1].equals("1")) {
            task.markAsDone();
        } else if (!parts[1].equals("0")) {
            throw new UltronException("Invalid saved task status");
        }
        return task;
    }

    private Deadline createSavedDeadline(String savedDescription) throws UltronException {
        int deadlineStart = savedDescription.lastIndexOf("( by: ");
        if (deadlineStart == -1 || !savedDescription.endsWith(")")) {
            throw new UltronException("Invalid saved deadline");
        }
        String description = savedDescription.substring(0, deadlineStart);
        String[] deadline = savedDescription
                .substring(deadlineStart + 6, savedDescription.length() - 1)
                .trim()
                .split(" ");
        String time = deadline.length == 2 ? " " + deadline[1] : "";
        try {
            LocalDate date = LocalDate.parse(deadline[0], DateTimeFormatter.ofPattern("d/MMM/yyyy"));
            return new Deadline(description + " /by " + date.format(DateTimeFormatter.ofPattern("d/MM/yyyy")) + time);
        } catch (DateTimeParseException e) {
            throw new UltronException("Invalid saved deadline");
        }
    }

    private Event createSavedEvent(String savedDescription) throws UltronException {
        int startTime = savedDescription.lastIndexOf("( from: ");
        int endTime = savedDescription.lastIndexOf(" to: ");
        if (startTime == -1 || endTime == -1 || endTime <= startTime || !savedDescription.endsWith(")")) {
            throw new UltronException("Invalid saved event");
        }
        String description = savedDescription.substring(0, startTime);
        String start = savedDescription.substring(startTime + 8, endTime);
        String end = savedDescription.substring(endTime + 5, savedDescription.length() - 1);
        return new Event(description + " /from " + start + " /to " + end);
    }
}
