package rocky.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import rocky.RockyException;
import rocky.task.Deadline;
import rocky.task.Event;
import rocky.task.Task;
import rocky.task.TaskType;
import rocky.task.ToDo;

/**
 * Saves tasks to, and loads them from, a text file with one task per line,
 * e.g. {@code "D | 0 | return book | 2019-12-01"}.
 */
public class Storage {
    private final File file;

    /**
     * Creates a storage that uses the given file. The file and its folder
     * are created on the first save if needed.
     *
     * @param file where the tasks are saved, e.g. data/duke.txt.
     */
    public Storage(File file) {
        this.file = file;
    }

    /**
     * Returns the tasks saved in the file, or an empty list if the file
     * doesn't exist yet (e.g. first run). A line that can't be parsed is
     * skipped with a warning, without stopping the rest of the file from
     * loading.
     */
    public List<Task> load() {
        List<Task> tasks = new ArrayList<>();
        if (!file.exists()) {
            return tasks;
        }

        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine().trim();
                if (!line.isEmpty()) {
                    addLoadedTask(tasks, line);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("     Warning: couldn't read saved tasks.");
        }
        return tasks;
    }

    /** Parses one save-file line and adds it to the tasks, or warns if it can't be read. */
    private static void addLoadedTask(List<Task> tasks, String line) {
        try {
            tasks.add(lineToTask(line));
        } catch (RockyException e) {
            System.out.println("     Warning: skipped an unreadable saved task.");
        }
    }

    /**
     * Saves the given tasks to the file, overwriting whatever was there
     * before. Creates the folder and file first if they don't exist yet.
     *
     * @param tasks the tasks to save, in display order.
     */
    public void save(List<Task> tasks) {
        try {
            ensureFileExists();
            try (FileWriter writer = new FileWriter(file)) {
                for (Task task : tasks) {
                    writer.write(taskToLine(task) + System.lineSeparator());
                }
            }
        } catch (IOException e) {
            System.out.println("     Warning: couldn't save tasks (" + e.getMessage() + ").");
        }
    }

    /**
     * Converts a task to its one-line save-file representation, e.g.
     * {@code "D | 0 | return book | 2019-12-01"}.
     *
     * @param task the task to encode.
     * @return the line to write to the save file for this task.
     */
    private static String taskToLine(Task task) {
        String done = task.isDone() ? "1" : "0";
        String line = " | " + done + " | " + task.getDescription();

        if (task instanceof Deadline) {
            Deadline deadline = (Deadline) task;
            return TaskType.DEADLINE.getIcon() + line + " | " + deadline.getDate();
        } else if (task instanceof Event) {
            Event event = (Event) task;
            return TaskType.EVENT.getIcon() + line + " | " + event.getStartDate()
                    + " | " + event.getEndDate();
        } else {
            return TaskType.TODO.getIcon() + line;
        }
    }

    /**
     * Parses one save-file line back into a Task.
     *
     * @param line a line previously produced by {@link #taskToLine}.
     * @return the reconstructed task, with its done state restored.
     * @throws RockyException if the line is malformed, e.g. missing fields
     *     or an unparsable date.
     */
    private static Task lineToTask(String line) throws RockyException {
        String[] fields = line.split("\\|");
        TaskType type = TaskType.fromIcon(getField(fields, 0));
        if (type == null) {
            throw new RockyException("A saved task has an unknown type.");
        }

        boolean isDone = getField(fields, 1).equals("1");
        String description = getField(fields, 2);

        Task task;
        switch (type) {
            case TODO:
                task = new ToDo(description);
                break;
            case DEADLINE:
                task = new Deadline(description, getField(fields, 3));
                break;
            case EVENT:
                task = new Event(description, getField(fields, 3), getField(fields, 4));
                break;
            default:
                throw new RockyException("I don't know how to load that kind of task.");
        }

        if (isDone) {
            task.mark();
        }
        return task;
    }

    /**
     * Returns one trimmed field of a split save-file line.
     *
     * @param fields the line's fields, split on "|".
     * @param position which field to return, counting from 0.
     * @throws RockyException if the line has no field at that position.
     */
    private static String getField(String[] fields, int position) throws RockyException {
        if (position >= fields.length) {
            throw new RockyException("A saved task is missing some of its details.");
        }
        return fields[position].trim();
    }

    /**
     * Creates the save file, and the folder it lives in, if they don't exist yet.
     *
     * @throws IOException if the folder or file can't be created.
     */
    private void ensureFileExists() throws IOException {
        File folder = file.getParentFile();
        if (folder != null && !folder.exists()) {
            folder.mkdirs();
        }
        if (!file.exists()) {
            file.createNewFile();
        }
    }
}
