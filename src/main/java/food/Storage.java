package food;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import food.exception.FoodStorageException;
import food.task.Task;

/**
 * Stores task history in data/tasks.md, creating the file if it does not exist.
 *
 * <p>This class is the boundary between the chatbot and the file system. Every IOException is
 * translated into a FoodStorageException here, so the rest of the program never needs to know
 * that tasks happen to live in a file.
 */
class Storage {
    private final Path storagePath;

    /**
     * Opens the save file, creating data/tasks.md and the folder holding it the first time the
     * program is run so that later reads and writes can assume the file exists.
     *
     * <p>The path is relative, so the save file is created in whichever directory the program is
     * started from.
     *
     * @throws FoodStorageException if the file or its folder could not be created.
     */
    public Storage() throws FoodStorageException {
        this.storagePath = Path.of("data", "tasks.md");
        assert this.storagePath.getParent() != null : "storagePath must have a parent directory";
        try {
            Files.createDirectories(this.storagePath.getParent());
            // createFile throws if the file is already there, so only create it the first time.
            if (!Files.exists(this.storagePath)) {
                Files.createFile(this.storagePath);
            }
        } catch (IOException e) {
            throw new FoodStorageException("Could not create the save file " + this.storagePath, e);
        }
    }

    /**
     * Writes the whole task list to disk, replacing whatever was there before.
     *
     * @param taskList the tasks to save.
     * @throws FoodStorageException if the file could not be written.
     */
    public void save(List<Task> taskList) throws FoodStorageException {
        try {
            List<String> saveList = taskList.stream().map(Task::toSaveFormat).toList();
            Files.write(storagePath, saveList);
        } catch (IOException e) {
            throw new FoodStorageException("Could not write to the save file " + this.storagePath, e);
        }
    }

    /**
     * Reads the save file back into tasks.
     *
     * @return the saved tasks, in the order they were written.
     * @throws FoodStorageException if the file could not be read or contains a bad line.
     */
    public List<Task> retrieveSaved() throws FoodStorageException {
        List<String> savedLines;
        try {
            savedLines = Files.readAllLines(this.storagePath);
        } catch (IOException e) {
            throw new FoodStorageException("Could not read the save file " + this.storagePath, e);
        }
        // A plain loop rather than a stream, because fromSaveFormat throws a checked
        // exception and lambdas cannot propagate those.
        List<Task> tasks = new ArrayList<>();
        for (String line : savedLines) {
            // A hand-edited save file often ends up with blank lines; they carry no task, so they
            // are skipped rather than reported as corruption.
            if (line.isBlank()) {
                continue;
            }
            tasks.add(Task.fromSaveFormat(line));
        }
        return tasks;
    }
}
