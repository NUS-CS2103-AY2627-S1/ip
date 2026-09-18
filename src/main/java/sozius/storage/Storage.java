package sozius.storage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import sozius.exception.SoziusException;
import sozius.task.DeadlineTask;
import sozius.task.DueDate;
import sozius.task.EventTask;
import sozius.task.Task;
import sozius.task.TodoTask;
import sozius.tasklist.TaskList;

/**
 * Storage class handles saving and reading data from hard disk
 */
public class Storage {
    private final String fileName;
    private int skippedLineCount;

    public Storage(String fileName) {
        this.fileName = fileName;
    }
    public Storage() {
        this("./tasks.txt");
    }

    private static Task parseFileCommand(String line) throws SoziusException {
        String[] splitArgs = line.split(" \\| ", -1);
        if (splitArgs.length < 3) {
            throw new SoziusException("expected at least 3 fields but found " + splitArgs.length);
        }

        String type = splitArgs[0];
        String doneStr = splitArgs[1];
        if (!doneStr.equals("0") && !doneStr.equals("1")) {
            throw new SoziusException("invalid done flag \"" + splitArgs[1] + "\" (expected 0 or 1)");
        }
        boolean isDone = splitArgs[1].equals("1");
        String desc = splitArgs[2].trim();
        if (desc.isEmpty()) {
            throw new SoziusException("missing task description");
        }
        Task task = null;
        switch (type) {
            case "T":
                if (splitArgs.length != 3) {
                    throw new SoziusException("a todo line must have exactly 3 fields");
                }
                task = new TodoTask(desc);
                break;
            case "D":
                if (splitArgs.length != 4) {
                    throw new SoziusException("a deadline line must have exactly 4 fields");
                }
                task = new DeadlineTask(desc, DueDate.parse(splitArgs[3]));
                break;
            case "E":
                if (splitArgs.length != 4) {
                    throw new SoziusException("an event line must have exactly 4 fields");
                }
                String[] times = splitArgs[3].split("/", -1);
                if (times.length != 2 || times[0].isBlank() || times[1].isBlank()) {
                    throw new SoziusException("an event must have a start and end date separated by /");
                }
                task = new EventTask(desc, DueDate.parse(times[0]), DueDate.parse(times[1]));
                break;
            default:
                throw new SoziusException("unknown task type \"" + type + "\" (expected T, D or E)");
        }
        task.setDone(isDone);
        return task;
    }

    /**
     * Parses all lines from the file to find tasks.
     * @return the ArrayList<Task> of all the tasks found from parsing
     */
    public List<Task> load() throws SoziusException {
        List<Task> tasks = new ArrayList<>();
        skippedLineCount = 0;

        File inputFile = new File(fileName);
        if (!inputFile.exists()) {
            try {
                File parent = inputFile.getAbsoluteFile().getParentFile();
                if (parent != null && !parent.exists()) {
                    parent.mkdirs();
                }
                inputFile.createNewFile();
            } catch (IOException | SecurityException e) {
                throw new SoziusException("Could not create the tasks file at " + fileName
                        + " (" + e.getMessage() + "). Check that the folder exists and is writable. "
                        + "Starting with an empty task list; your tasks will not be saved.");
            }
            return tasks;
        }

        int lineNumber = 0;
        try (Scanner scanner = new Scanner(inputFile)) {
            while (scanner.hasNextLine()) {
                lineNumber++;
                String line = scanner.nextLine();
                if (line.isBlank()) {
                    continue;
                }
                try {
                    Task task = parseFileCommand(line);
                    tasks.add(task);
                } catch (SoziusException e) {
                    skippedLineCount++;
                    System.err.println("Skipped corrupted line " + lineNumber + " of " + fileName
                            + ": " + e.getMessage());
                }
            }
        } catch (IOException | SecurityException e) {
            throw new SoziusException("Could not read the tasks file at " + fileName
                    + " (" + e.getMessage() + "). Check the file permissions. "
                    + "Starting with an empty task list; your tasks will not be saved.");
        }
        return tasks;
    }

    /**
     * Returns the number of lines skipped during the last load()
     * because they were corrupted or duplicated.
     * @return the number of skipped lines
     */
    public int getSkippedLineCount() {
        return skippedLineCount;
    }

    /**
     * Saves all tasks to the file
     * @param tasks the TaskList which contains all the tasks
     */
    public void save(TaskList tasks) throws SoziusException {
        assert tasks != null;
        File file = new File(fileName);
        try {
            File parent = file.getAbsoluteFile().getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
        } catch (SecurityException e) {
            throw new SoziusException("Could not save your tasks to " + fileName
                    + " (" + e.getMessage() + ")");
        }
        try (FileWriter myWriter = new FileWriter(file)) {
            for (Task task : tasks) {
                myWriter.write(task.toFileString() + '\n');
            }
        } catch (IOException | SecurityException e) {
            throw new SoziusException("Could not save your tasks to " + fileName
                    + " (" + e.getMessage() + "). Check that the file is not locked "
                    + "and the folder is writable.");
        }
    }
}
