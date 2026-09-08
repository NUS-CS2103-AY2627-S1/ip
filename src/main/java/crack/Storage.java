package crack;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;

import crack.task.Deadline;
import crack.task.Event;
import crack.task.Task;
import crack.task.Todo;

/**
 * Keeps the task list on disk so it survives between runs.
 * Each task is one line, with its fields separated by " | ".
 */
public class Storage {
    /** Sits between the fields of a saved line. */
    private static final String SEPARATOR = " | ";

    private final String filePath;

    /**
     * Points storage at a save file.
     * The file and its folder are only created once there is something to save.
     *
     * @param filePath Where the save file lives.
     */
    public Storage(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads the saved tasks.
     * Lines that cannot be read are skipped rather than treated as failures,
     * so one bad line does not cost the user the whole list.
     *
     * @return The saved tasks, or an empty list if nothing has been saved yet.
     * @throws CrackException If the file exists but cannot be read.
     */
    public TaskList load() throws CrackException {
        ArrayList<Task> tasks = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            return new TaskList(tasks);
        }
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                parse(scanner.nextLine()).ifPresent(tasks::add);
            }
        } catch (IOException e) {
            throw new CrackException("Couldn't read your saved list gng. Starting fresh.");
        }
        return new TaskList(tasks);
    }

    /**
     * Writes the whole list out, replacing whatever was saved before.
     *
     * @param tasks The tasks to save.
     * @throws CrackException If the file cannot be written.
     */
    public void save(TaskList tasks) throws CrackException {
        File file = new File(filePath);
        File folder = file.getParentFile();
        if (folder != null) {
            folder.mkdirs();
        }
        try (FileWriter writer = new FileWriter(file)) {
            for (Task task : tasks.getTasks()) {
                writer.write(task.toSaveFormat() + System.lineSeparator());
            }
        } catch (IOException e) {
            throw new CrackException("Couldn't save your list gng, I'm finna crash out.");
        }
    }

    /**
     * Rebuilds one task from the line that was saved for it.
     * Only the type tag and the done flag are taken off the front, since a
     * description is allowed to contain the separator itself.
     *
     * @param line One line of the save file.
     * @return The task, or nothing if the line is junk we cannot make sense of.
     */
    private static Optional<Task> parse(String line) {
        String[] head = line.split(" \\| ", 3);
        if (head.length < 3) {
            return Optional.empty();
        }
        Optional<Task> task = buildTask(head[0], head[2]);
        if (head[1].equals("1")) {
            task.ifPresent(Task::markAsDone);
        }
        return task;
    }

    /**
     * Builds the right kind of task for the type tag the line starts with.
     *
     * @param typeTag The letter naming the kind of task.
     * @param body The description, followed by whatever dates the type carries.
     * @return The task, or nothing if the tag is unknown, its fields are
     *         missing, or it carries no description.
     */
    private static Optional<Task> buildTask(String typeTag, String body) {
        try {
            return switch (typeTag) {
                case "T" -> body.isBlank() ? Optional.empty() : Optional.of(new Todo(body));
                case "D" -> buildDated(body, 1);
                case "E" -> buildDated(body, 2);
                default -> Optional.empty();
            };
        } catch (CrackException e) {
            return Optional.empty();
        }
    }

    /**
     * Builds a deadline or an event, whichever carries the given number of dates.
     *
     * @param body The description, followed by its dates.
     * @param dateCount How many dates the task type carries.
     * @return The task, or nothing if the line does not carry that many dates,
     *         or carries no description.
     * @throws CrackException If a date cannot be read, or the dates are the wrong way round.
     */
    private static Optional<Task> buildDated(String body, int dateCount) throws CrackException {
        String[] fields = splitOffDates(body, dateCount);
        if (fields.length == 0 || fields[0].isBlank()) {
            return Optional.empty();
        }
        return dateCount == 1
                ? Optional.of(Deadline.of(fields[0], fields[1]))
                : Optional.of(Event.of(fields[0], fields[1], fields[2]));
    }

    /**
     * Peels the trailing date fields off a saved line, leaving the description.
     * Working from the end is what lets a description contain the separator:
     * a date never does, so the last fields are unambiguous.
     *
     * @param body The description, followed by its dates.
     * @param dateCount How many dates to take off the end.
     * @return The description first, then each date in order, or an empty array
     *         when the line does not carry that many dates.
     */
    private static String[] splitOffDates(String body, int dateCount) {
        String[] fields = new String[dateCount + 1];
        String rest = body;
        for (int i = dateCount; i > 0; i--) {
            int cut = rest.lastIndexOf(SEPARATOR);
            if (cut < 0) {
                return new String[0];
            }
            fields[i] = rest.substring(cut + SEPARATOR.length());
            rest = rest.substring(0, cut);
        }
        fields[0] = rest;
        return fields;
    }
}
