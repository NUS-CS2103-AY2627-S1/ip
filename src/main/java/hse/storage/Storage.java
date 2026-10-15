package hse.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import hse.task.Deadline;
import hse.task.Event;
import hse.task.Task;
import hse.task.TaskList;
import hse.task.ToDos;

/** Loads tasks from, and saves tasks to, the application's data file. */
public class Storage {
    private final File file;

    /** Creates storage that uses the supplied file path. */
    public Storage(String filePath) {
        file = new File(filePath);
    }

    /** Loads saved tasks, returning an empty list if the data file does not yet exist. */
    public TaskList load() throws FileNotFoundException {
        if (!file.exists()) {
            return new TaskList();
        }
        List<Task> loadedTasks = new ArrayList<>();
        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                Task task = parseSavedTask(fileScanner.nextLine());
                if (task != null) {
                    loadedTasks.add(task);
                }
            }
        }
        return new TaskList(loadedTasks);
    }

    /** Writes all tasks to the data file, replacing any old contents. */
    public void save(TaskList tasks) throws IOException {
        File directory = file.getParentFile();
        if (directory != null && !directory.exists() && !directory.mkdirs()) {
            throw new IOException("Could not create data directory.");
        }
        try (FileWriter writer = new FileWriter(file, false)) {
            for (Task task : tasks.getTasks()) {
                writer.write(task + System.lineSeparator());
            }
        }
    }

    /** Recreates one task from its saved display format, or returns {@code null} for corrupt data. */
    private Task parseSavedTask(String line) {
        try {
            if (line.startsWith("[T][X] ")) {
                return completedTask(new ToDos(line.substring(7)));
            }
            if (line.startsWith("[T][ ] ")) {
                return new ToDos(line.substring(7));
            }
            if (line.startsWith("[D][X] ") || line.startsWith("[D][ ] ")) {
                String[] parts = line.substring(7).split(" \\(by: ", 2);
                if (parts.length != 2 || !parts[1].endsWith(")")) {
                    return null;
                }
                Task task = new Deadline(parts[0], parseSavedDateTime(
                        parts[1].substring(0, parts[1].length() - 1), Deadline.DATE_TIME_FORMAT,
                        Deadline.DATE_FORMAT));
                return line.startsWith("[D][X] ") ? completedTask(task) : task;
            }
            if (line.startsWith("[E][X] ") || line.startsWith("[E][ ] ")) {
                String[] parts = line.substring(7).split(" \\(from: ", 2);
                if (parts.length != 2 || !parts[1].endsWith(")")) {
                    return null;
                }
                String[] times = parts[1].substring(0, parts[1].length() - 1).split(" to: ", 2);
                if (times.length != 2) {
                    return null;
                }
                Task task = new Event(parts[0], parseSavedDateTime(times[0], Event.DATE_TIME_FORMAT,
                        Event.DATE_FORMAT), parseSavedDateTime(times[1], Event.DATE_TIME_FORMAT,
                        Event.DATE_FORMAT));
                return line.startsWith("[E][X] ") ? completedTask(task) : task;
            }
        } catch (DateTimeParseException e) {
            return null;
        }
        return null;
    }

    /** Marks a loaded task as complete before returning it. */
    private Task completedTask(Task task) {
        task.markAsDone();
        return task;
    }

    /** Parses the current saved date-time format and the older date-only format. */
    private LocalDateTime parseSavedDateTime(String dateTime, DateTimeFormatter dateTimeFormat,
                                              DateTimeFormatter dateFormat) {
        try {
            return LocalDateTime.parse(dateTime, dateTimeFormat.withResolverStyle(ResolverStyle.STRICT));
        } catch (DateTimeParseException e) {
            return LocalDate.parse(dateTime, dateFormat.withResolverStyle(ResolverStyle.STRICT)).atStartOfDay();
        }
    }
}
