package squirtlebot.storage;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import squirtlebot.exception.StorageException;
import squirtlebot.task.TaskList;

/**
 * Handles all read-write operations to persist task list changes.
 * Uses {@code data/Tasks.ser} as system file for read-write operations.
 */
public class Storage {
    private static final String DEFAULT_FILE_LOCATION = "data/Tasks.ser";

    private boolean isDisabled;
    private final String fileLocation;
    private final String directoryPath;

    /**
     * Constructs a storage object using default file locations.
     */
    public Storage() {
        this(DEFAULT_FILE_LOCATION);
    }

    /**
     * Constructs a storage object that persists data at the specified location.
     *
     * @param fileLocation path of the file used to store tasks.
     */
    public Storage(String fileLocation) {
        isDisabled = false;
        this.fileLocation = fileLocation;
        File parentDirectory = new File(fileLocation).getParentFile();
        this.directoryPath = parentDirectory == null ? "." : parentDirectory.getPath();
    }

    /**
     * Loads previously saved tasks from data file, specified by {@code fileLocation}.
     *
     * @return list of task objects from data file on the system.
     * @throws StorageException if there was an IO/Class-related issue while loading from storage.
     */
    public TaskList loadData() {
        assert !isDisabled;
        TaskList loadedTasks = new TaskList();
        try (FileInputStream fileInputStream = new FileInputStream(fileLocation);
             ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
            Object data = objectInputStream.readObject();
            if (!(data instanceof TaskList tasks)) {
                throw new StorageException("Stored data is invalid :(");
            }
            loadedTasks = tasks;
        } catch (FileNotFoundException fileNotFoundException) {
            createDataFile();
        } catch (EOFException eofException) {
            return loadedTasks;
        } catch (ClassNotFoundException | IOException e) {
            throw new StorageException("There was an issue encountered while loading data :(", e);
        }
        return loadedTasks;
    }

    /**
     * Deletes the data file and creates a new data file if storage is not disabled.
     * If storage is disabled, no operations are performed.
     *
     * @throws StorageException if an issue was encountered while trying to create the new data file.
     */
    public void resetData() {
        if (isDisabled) {
            return;
        }
        File dataFile = new File(fileLocation);
        dataFile.delete();
        createDataFile();
    }

    /**
     * Sets the value of isDisabled to disable all storage-related operations except for loading of data.
     */
    public void disable() {
        this.isDisabled = true;
    }

    /**
     * Writes the list of tasks to the data file, specified by {@code fileLocation} if storage is not disabled.
     * If storage is disabled, no operations are performed.
     *
     * @param tasks TaskList object to be written to the data file.
     * @throws StorageException if an error was encountered while writing to the data file.
     */
    public void writeData(TaskList tasks) {
        if (isDisabled) {
            return;
        }
        createDataFile();

        try (FileOutputStream fileOutputStream = new FileOutputStream(fileLocation);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
            objectOutputStream.writeObject(tasks);
        } catch (IOException e) {
            throw new StorageException("There was an error writing to storage :(", e);
        }
    }

    /**
     * If storage is not disabled, creates the specified data file and data directory if it does not already exist.
     * If storage is disabled, no operations are performed.
     */
    private void createDataFile() {
        if (isDisabled) {
            return;
        }
        File dataFile = new File(fileLocation);
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        try {
            dataFile.createNewFile();
        } catch (IOException e) {
            throw new StorageException("There was an issue creating the data file :(", e);
        }
    }

}
