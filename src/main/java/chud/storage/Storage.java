package chud.storage;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import chud.task.Deadline;
import chud.task.Event;
import chud.task.Task;
import chud.task.TaskDateTime;
import chud.task.TaskList;
import chud.task.Todo;

/**
 * Saves and loads the task list from a save file on disk, so that data survives between runs
 * of the chatbot. The file's location is given by the caller (e.g. {@code Chud} passes
 * {@code "data/chud.txt"}, a relative path so the program works regardless of which computer
 * runs it) and resolved via {@link Path#of}, which handles either {@code '/'} or {@code '\'} as
 * a separator, so it resolves correctly regardless of which operating system runs the program.
 */
public class Storage {
    private final Path filePath;

    /** Creates a Storage that reads and writes the save file at the given path. */
    public Storage(String filePath) {
        this.filePath = Path.of(filePath);
    }

    /**
     * Writes the given tasks to the save file, one per line, overwriting any previous contents.
     * Creates the save file's parent directory first if it doesn't already exist (e.g. on
     * someone else's computer running the program for the first time).
     */
    public void save(TaskList tasks) {
        try {
            // getParent() is null when filePath has no directory component (e.g. just
            // "chud.txt") -- createDirectories(null) would throw NullPointerException, so only
            // call it when there's actually a parent directory to create.
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            try (FileWriter writer = new FileWriter(filePath.toFile())) {
                for (Task task : tasks) {
                    writer.write(task.toFileString() + System.lineSeparator());
                }
            }
        } catch (IOException e) {
            System.out.println("     Warning: could not save tasks to disk (" + e.getMessage() + ")");
        }
    }

    /**
     * Reads the save file and reconstructs the task list from it. Returns an empty list if the
     * save file (or its containing directory) doesn't exist yet -- e.g. on first run, or when
     * someone else runs this chatbot for the first time on their own computer -- rather than
     * treating a missing file as an error. Blank lines are skipped silently; a line that can't
     * be parsed (e.g. the file was hand-edited into a corrupted state) is skipped with a warning
     * instead of crashing the whole program, and loading continues with the rest.
     */
    public ArrayList<Task> load() {
        ArrayList<Task> tasks = new ArrayList<>();
        if (!Files.exists(filePath)) {
            return tasks;
        }
        try {
            List<String> lines = Files.readAllLines(filePath);
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.isBlank()) {
                    continue;
                }
                try {
                    tasks.add(parseTask(line));
                } catch (IllegalArgumentException e) {
                    System.out.println("     Warning: skipping corrupted line " + (i + 1)
                            + " in save file (" + e.getMessage() + ")");
                }
            }
        } catch (IOException e) {
            System.out.println("     Warning: could not load tasks from disk (" + e.getMessage() + ")");
        }
        return tasks;
    }

    /**
     * Parses one line of the save file (e.g. {@code "D | 0 | return book | Sunday"}) back
     * into the matching Task, throwing IllegalArgumentException with a specific reason if the
     * line is missing fields, has an invalid done flag, or has an unrecognized task type tag.
     */
    private Task parseTask(String line) {
        String[] fields = line.split(" \\| ");
        if (fields.length < 3) {
            throw new IllegalArgumentException("expected at least 3 fields, found " + fields.length);
        }
        String tag = fields[0];
        String doneFlag = fields[1];
        if (!doneFlag.equals("0") && !doneFlag.equals("1")) {
            throw new IllegalArgumentException("done flag must be '0' or '1', found '" + doneFlag + "'");
        }
        String description = fields[2];

        Task task;
        switch (tag) {
            case "T":
                task = new Todo(description);
                break;
            case "D":
                if (fields.length < 4) {
                    throw new IllegalArgumentException("deadline is missing its '/by' field");
                }
                task = new Deadline(description, TaskDateTime.parse(fields[3]));
                break;
            case "E":
                if (fields.length < 5) {
                    throw new IllegalArgumentException("event is missing its '/from' or '/to' field");
                }
                task = new Event(description, TaskDateTime.parse(fields[3]), TaskDateTime.parse(fields[4]));
                break;
            default:
                throw new IllegalArgumentException("unrecognized task type tag '" + tag + "'");
        }
        // Every branch above either assigns task or throws, so task is never left uninitialized
        // here -- this documents that the switch is exhaustive over the tags this method accepts,
        // which isn't obvious from Java's flow analysis alone (it only requires task be assigned
        // on every path, not that it's non-null).
        assert task != null : "task must have been assigned by the switch above";
        if (doneFlag.equals("1")) {
            task.markAsDone();
        }
        return task;
    }
}
