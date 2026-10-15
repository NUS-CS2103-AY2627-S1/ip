package janet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * Handles Reading and writing tasks to file
 */
public class Storage {

    private static final String FILE_PATH = "data/tasks.txt";
    private final File file;

    /**
     * Creates a storage file at the specified path, creating any missing parent
     * directories and the file itself.
     *
     * @throws IOException If the directories or storage file cannot be created.
     */
    public Storage() throws IOException {
        this(new File(Storage.FILE_PATH));
    }

    /**
     * Creates storage backed by a specific file.
     * This is package-private so application tests can use a temporary file instead of real user data.
     *
     * @param file File used for storage.
     * @throws IOException If the directories or storage file cannot be created.
     */
    Storage(File file) throws IOException {
        this.file = file;
        File parentDirectory = this.file.getParentFile();
        if (parentDirectory != null) {
            parentDirectory.mkdirs();
        }
        this.file.createNewFile();
    }


    /**
     * Writes the specified text to the storage file.
     *
     * @throws IOException If the file cannot be written to.
     */
    public void writeToFile(String textToAdd) throws IOException {
        FileWriter fw = new FileWriter(this.file);
        fw.write(textToAdd);
        fw.close();
    }


    /**
     * Reads all stored tasks from the storage file and returns them as a task list.
     *
     * @return <code>TaskList</code> containing the tasks stored in the file.
     * @throws FileNotFoundException If the storage file cannot be found.
     * @throws JanetFileException If a stored task cannot be parsed.
     */
    public TaskList readFromFile() throws FileNotFoundException, JanetFileException {
        TaskList taskList = new TaskList();
        try (Scanner sc = new Scanner(this.file)) {
            while (sc.hasNextLine()) {
                String currLine = sc.nextLine();
                taskList = StorageTaskParser.processBaseTask(currLine)
                        .processStorageCommand(taskList)
                        .updatedTaskList()
                        .orElse(taskList);
            }
        } catch (JanetException e) {
            throw new JanetFileException(e.getMessage());
        }
        return taskList;
    }

    /**
     * Clears all saved tasks while keeping the storage file available for future writes.
     *
     * @throws IOException If the storage file cannot be cleared.
     */
    public void resetStorage() throws IOException {
        this.writeToFile("");
    }
}
