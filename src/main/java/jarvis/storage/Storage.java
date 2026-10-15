package jarvis.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import jarvis.classes.Deadline;
import jarvis.classes.Event;
import jarvis.classes.Task;
import jarvis.classes.ToDo;
import jarvis.exceptions.InvalidTaskDataException;

/** Handles saving and loading Jarvis tasks from the hard disk. */
public class Storage {
    private static String filePath;
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("MM dd uuuu HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("MM dd uuuu");

    /**
     * Creates a storage instance using the given file path.
     *
     * @param filePath Path to the file used for storing tasks.
     */
    public Storage(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Saves all tasks to the hard disk.
     *
     * @param tasks Tasks to save.
     */
    public static void saveTasks(List<Task> tasks) {
        File file = new File(filePath);

        try {
            File parentDirectory = file.getParentFile();
            if (parentDirectory != null && !parentDirectory.exists()) {
                parentDirectory.mkdirs();
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for (Task task : tasks) {
                    writer.write(task.toString());
                    writer.newLine();
                }
            }
        } catch (IOException error) {
            System.err.println("Error: Unable to save tasks to the hard disk.");
        }
    }

    /**
     * Loads all previously saved tasks from the hard disk.
     *
     * @return Previously saved tasks, or an empty list if no save file exists.
     */
    public static List<Task> loadTasks() {
        File file = new File(filePath);
        List<Task> tasks = new ArrayList<>();

        if (!file.exists()) {
            return tasks;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (!line.trim().isEmpty()) {
                    try {
                        addTaskFromLine(tasks, line);
                    } catch (InvalidTaskDataException e) {
                        System.err.println(
                                "Warning : Unable to load task for line no "
                                + lineNumber + ". Skipping it."
                        );
                    }
                }
            }
        } catch (IOException | DateTimeParseException error) {
            System.err.println("Error: Unable to load saved tasks.");
            return new ArrayList<>();
        }

        return tasks;
    }

    /**
     * Adds a task reconstructed from one storage line.
     *
     * @param tasks Destination task list.
     * @param line Serialized task line.
     */
    private static void addTaskFromLine(List<Task> tasks, String line) throws InvalidTaskDataException {
        if (line.startsWith("[T]")) {
            boolean isDone = line.startsWith("[T][X]");
            String prefix = isDone ? "[T][X]" : "[T][]";

            if (!line.startsWith(prefix)) {
                throw new InvalidTaskDataException("Malformed todo line");
            }

            String description = line.substring(prefix.length()).trim();

            if (description.isEmpty()) {
                throw new InvalidTaskDataException("Missing todo description");
            }

            ToDo task = new ToDo(description);
            setDoneIfNeeded(task, isDone);
            tasks.add(task);
        } else if (line.startsWith("[D]")) {
            boolean isDone = line.startsWith("[D][X]");
            String prefix = isDone ? "[D][X]" : "[D][]";
            int byPosition = line.indexOf("(by:");

            if (byPosition == -1 || byPosition <= prefix.length()) {
                throw new InvalidTaskDataException("Malformed deadline line");
            }

            String description = line.substring(prefix.length(), byPosition).trim();
            String dateText = removeClosingParenthesis(
                    line.substring(byPosition + "(by:".length()).trim());

            if (dateText.isEmpty()) {
                throw new InvalidTaskDataException("Missing deadline date");
            }

            Deadline task = new Deadline(description, parseDateTime(dateText));
            setDoneIfNeeded(task, isDone);
            tasks.add(task);
        } else if (line.startsWith("[E]")) {
            boolean isDone = line.startsWith("[E][X]");
            String prefix = isDone ? "[E][X]" : "[E][]";
            int fromPosition = line.indexOf("(from:");
            int toPosition = line.indexOf(" to:");

            if (fromPosition == -1
                    || toPosition == -1
                    || fromPosition <= prefix.length()
                    || toPosition <= fromPosition) {
                throw new InvalidTaskDataException("Malformed event line");
            }

            String description = line.substring(prefix.length(), fromPosition).trim();
            String startText = line.substring(
                    fromPosition + "(from:".length(), toPosition).trim();
            String endText = removeClosingParenthesis(
                    line.substring(toPosition + " to:".length()).trim());

            if (description.isEmpty()
                    || startText.isEmpty()
                    || endText.isEmpty()) {
                throw new InvalidTaskDataException("Malformed event line");
            }

            Event task = new Event(
                    description,
                    parseDateTime(startText),
                    parseDateTime(endText)
            );

            setDoneIfNeeded(task, isDone);
            tasks.add(task);
        } else {
            throw new InvalidTaskDataException("Unknown task type");
        }
    }

    /**
     * Sets a task's completed state when it was saved as completed.
     *
     * @param task Task to update.
     * @param isDone Whether the stored task was completed.
     */
    private static void setDoneIfNeeded(Task task, boolean isDone) {
        if (isDone) {
            task.setCompletionStatus(Task.CompletionStatus.DONE);
        }
    }

    /**
     * Removes the closing parenthesis from a stored date/time field.
     *
     * @param value Stored field.
     * @return Field without its closing parenthesis.
     */
    private static String removeClosingParenthesis(String value) {
        return value.endsWith(")") ? value.substring(0, value.length() - 1).trim() : value;
    }

    /**
     * Parses either a date-time or date-only value.
     *
     * @param value Stored date/time text.
     * @return Parsed date/time, using midnight for date-only values.
     */
    private static LocalDateTime parseDateTime(String value) {
        try {
            return LocalDateTime.parse(value, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException error) {
            return LocalDate.parse(value, DATE_FORMATTER).atStartOfDay();
        }
    }
}
