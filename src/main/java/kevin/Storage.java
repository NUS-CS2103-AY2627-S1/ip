package kevin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import kevin.task.Task;
import kevin.task.TaskList;

/**
 * Storage Class handles file actions.
 * Contains 1 filepath, should always be data/tasks.txt.
 * Able to load tasks from existing file, and save new tasks to create or update file.
 */
public class Storage {
    private Path filePath;
    private boolean loadedTasks = false;
    private boolean hasLoadingError = false;
    private String loadingErrorMessage = "";

    public Storage(String filePathString) {
        this.filePath = Paths.get(filePathString);
    }


    /**
     * Loads TaskList from filePath.
     * Sets loadedTasks as true if any tasks were loaded successfully.
     * Adds to loadingErrorMessage and sets hasLoadingError as true
     * if any tasks could not be loaded.
     *
     * @return tasks Previously saved tasks.
     * @throws KevinException If unable to create Parent Directory.
     */
    public TaskList load() throws KevinException {
        TaskList tasks = new TaskList();
        try {
            ensureParentDirectoryExists();
            List<String> lines = Files.readAllLines(filePath);
            int counter = 0;
            for (String line : lines) {
                try {
                    counter++;
                    tasks.add(Task.parseLine(line));
                    loadedTasks = true;
                } catch (KevinException e) {
                    hasLoadingError = true;
                    loadingErrorMessage += "Task " + counter
                            + " was corrupted and could not be loaded. " + e.getMessage().substring(4) + '\n';
                }
            }
        } catch (NoSuchFileException e) {
            //Empty catch block as writing the file does not require the file to exist.
        } catch (IOException e) {
            throw new KevinException("Cannot read filepath.");
        }
        return tasks;
    }

    /**
     * Saves tasks into filePath.
     * Assumes filePath has a parent folder (data).
     * .
     * @param tasks current list of tasks.
     */
    public void save(TaskList tasks) {
        //Checks that parent (data) exists and is a folder
        Path folderPath = filePath.getParent();
        assert Files.exists(folderPath) && Files.isDirectory(folderPath);

        tasks.save(filePath);
    }

    /**
     * Ensures Parent Directory exists.
     *
     * @throws IOException If unable to create Parent Directory.
     */
    public void ensureParentDirectoryExists() throws IOException {
        Path folderPath = filePath.getParent();
        if (folderPath != null) {
            Files.createDirectories(folderPath);
        }
    }

    /**
     * Creates loadingTasksMessage.
     */
    public String createLoadingTasksMessage() {
        String loadingTasksMessage = "";
        if (loadedTasks) {
            loadingTasksMessage += "Tasks loaded successfully.\n";
        }
        if (hasLoadingError) {
            loadingTasksMessage += loadingErrorMessage
                    + "Please fix the corrupted file first as any further changes will "
                    + "overwrite the corrupted tasks.\n";
        }
        return loadingTasksMessage;
    }
}
