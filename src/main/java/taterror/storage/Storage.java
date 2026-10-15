package taterror.storage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import taterror.task.Deadline;
import taterror.task.Event;
import taterror.task.Priority;
import taterror.task.Task;
import taterror.task.Todo;

/**
 * Reads and writes the task list to a pipe-delimited save file, e.g.
 * {@code D | 0 | submit report | 2026-09-01}.
 */
public class Storage {
    private final String filePath;

    /**
     * Creates a storage bound to a single save file.
     *
     * @param filePath path (relative or absolute) of the save file this instance
     *                 reads from and writes to
     */
    public Storage(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Reads tasks from the save file.
     *
     * @return the tasks loaded, in file order, or an empty list if the file
     *         doesn't exist yet or couldn't be read
     */
    public List<Task> load() {
        File dataFile = new File(filePath);
        if (!dataFile.exists()) {
            return new ArrayList<>();
        }
        List<Task> tasks = new ArrayList<>();
        try {
            Scanner fileScanner = new Scanner(dataFile);
            int lineNumber = 0;
            while (fileScanner.hasNextLine()) {
                lineNumber++;
                String line = fileScanner.nextLine();
                try {
                    tasks.add(parseTask(line.split(" \\| ")));
                } catch (RuntimeException e) {
                    System.out.println("OOPS!!! Save file line " + lineNumber
                            + " looks corrupted, skipping it: " + line);
                }
            }
            fileScanner.close();
        } catch (IOException e) {
            System.out.println("OOPS!!! Couldn't load your saved tasks. Starting fresh.");
            return new ArrayList<>();
        }
        return tasks;
    }

    /**
     * Reconstructs one {@link Task} from its pipe-delimited save-file fields:
     * {@code [type, doneFlag, description, ...type-specific fields]}.
     */
    private Task parseTask(String[] fields) {
        assert fields.length >= 3 : "Every save-file line has at least [type, doneFlag, description]";
        String type = fields[0];
        Task task;
        int priorityIndex;
        if (type.equals("D")) {
            assert fields.length >= 4 : "A 'D' line must also carry the 'by' field";
            task = new Deadline(fields[2], fields[3]);
            priorityIndex = 4;
        } else if (type.equals("E")) {
            assert fields.length >= 5 : "An 'E' line must also carry the 'from' and 'to' fields";
            task = new Event(fields[2], fields[3], fields[4]);
            priorityIndex = 5;
        } else {
            task = new Todo(fields[2]);
            priorityIndex = 3;
        }
        if (fields[1].equals("1")) {
            task.markAsDone();
        }
        if (fields.length > priorityIndex) {
            Priority priority = Priority.fromString(fields[priorityIndex]);
            if (priority != null) {
                task.setPriority(priority);
            }
        }
        return task;
    }

    /**
     * Writes every task in {@code tasks} to the save file (creating its parent
     * directory if needed), overwriting whatever was there before. Failures are
     * reported to the user rather than thrown, since a save failure shouldn't
     * crash the app.
     */
    public void save(List<Task> tasks) {
        assert tasks != null : "tasks should never be null; pass an empty list instead";
        try {
            Path dataPath = Paths.get(filePath);
            Files.createDirectories(dataPath.getParent());
            FileWriter writer = new FileWriter(dataPath.toFile());
            for (Task task : tasks) {
                String doneFlag = task.isDone() ? "1" : "0";
                String priorityDetail = task.getPriority() == Priority.NONE ? "" : " | " + task.getPriority();
                writer.write(task.getTypeCode() + " | " + doneFlag + " | " + task.getDescription()
                        + task.toSaveDetail() + priorityDetail + "\n");
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("OOPS!!! Couldn't save your tasks. Don't blame me if you lose them.");
        }
    }
}
