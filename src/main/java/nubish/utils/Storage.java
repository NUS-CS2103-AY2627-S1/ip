package nubish.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import nubish.tasks.Deadline;
import nubish.tasks.Event;
import nubish.tasks.Todo;


/**
 * Handles loading tasks from disk and saving tasks back to disk.
 */
public class Storage {
    private final String filepath;
    private final TaskList taskList;

    /**
     * Creates storage backed by the given file path and task list.
     *
     * @param filepath path to the save file
     * @param taskList task list to load into and save from
     */
    public Storage(String filepath, TaskList taskList) {
        this.filepath = filepath;
        this.taskList = taskList;
    }

    /**
     * Loads saved tasks from the configured file into the task list.
     *
     * @throws IOException if the save file cannot be created or read
     */
    public void load() throws IOException {
        File file = new File(this.filepath);
        createFileIfMissing(file);

        try (Scanner scanner = new Scanner(file)) {
            int lineNumber = 0;
            while (scanner.hasNextLine()) {
                lineNumber++;
                String savedTask = scanner.nextLine();
                if (savedTask.isBlank()) {
                    continue;
                }

                loadTask(savedTask, lineNumber);
            }
        }
    }

    /**
     * Saves all tasks in the task list to the configured file.
     *
     * @throws IOException if the file cannot be written
     */
    public void save() throws IOException {
        File file = new File(this.filepath);
        createParentDirectory(file);

        try (FileWriter fileWriter = new FileWriter(file)) {
            for (int i = 0; i < this.taskList.size(); i++) {
                fileWriter.write(this.taskList.get(i).saveString() + "\n");
            }
        }
    }

    private void createFileIfMissing(File file) throws IOException {
        createParentDirectory(file);
        if (!file.exists() && !file.createNewFile()) {
            throw new IOException("Could not create save file.");
        }
        if (!file.isFile()) {
            throw new IOException("Save path is not a file.");
        }
    }

    private void createParentDirectory(File file) throws IOException {
        File parentDirectory = file.getParentFile();
        if (parentDirectory != null && !parentDirectory.exists() && !parentDirectory.mkdirs()) {
            throw new IOException("Could not create save directory.");
        }
    }

    private void loadTask(String savedTask, int lineNumber) {
        try {
            String[] line = savedTask.split("\\s*\\|\\s*", -1);
            switch (line[0]) {
                case "E":
                    validateFieldCount(line, 5);
                    this.taskList.add(new Event(parseDoneStatus(line[1]), line[2], line[3], line[4]));
                    break;
                case "T":
                    validateFieldCount(line, 3);
                    this.taskList.add(new Todo(parseDoneStatus(line[1]), line[2]));
                    break;
                case "D":
                    validateFieldCount(line, 4);
                    this.taskList.add(new Deadline(parseDoneStatus(line[1]), line[2], line[3]));
                    break;
                default:
                    throw new NubishException("unknown task type");
            }
        } catch (RuntimeException e) {
            throw new NubishException(String.format("Invalid save file at line %d: %s", lineNumber, e.getMessage()));
        }
    }

    private void validateFieldCount(String[] line, int expectedFieldCount) {
        if (line.length != expectedFieldCount) {
            throw new NubishException("wrong number of fields");
        }
    }

    private boolean parseDoneStatus(String status) {
        if (!status.equals("0") && !status.equals("1")) {
            throw new NubishException("invalid task status");
        }
        return status.equals("1");
    }
}
