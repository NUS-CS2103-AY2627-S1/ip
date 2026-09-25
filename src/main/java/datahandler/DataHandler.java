package datahandler;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Abstraction to handle data side effects
 */
public class DataHandler {
    private final Path dataFolderPath;
    private final Path dataFilePath;

    /**
     * Initialises storage, creating the directory and file when needed.
     *
     * @param dataFolderPath Folder where the data will be held
     * @param dataFilePath File where the data should be saved to
     * @throws Exception if the storage directory or file cannot be created
     */
    public DataHandler(Path dataFolderPath, Path dataFilePath) throws Exception {
        this.dataFolderPath = dataFolderPath;
        this.dataFilePath = dataFilePath;
        if (!Files.isDirectory(this.dataFolderPath)) {
            Files.createDirectories(this.dataFolderPath);
        }
        if (!Files.exists(this.dataFilePath)) {
            Files.createFile(this.dataFilePath);
        }
    }

    /**
     * Returns the folder used by this data handler.
     * This helps tests create related storage files in the same temporary folder.
     *
     * @return folder where data files are stored
     */
    public Path getDataFolderPath() {
        return this.dataFolderPath;
    }

    /**
     * Reads the data file and returns its contents as a string.
     *
     * @return contents of the data file
     * @throws Exception if the data cannot be read
     */
    public String read() throws Exception {
        return Files.readString(this.dataFilePath).strip();
    }

    /**
     * Writes the given data into the data file.
     *
     * @param data string to write into the data file
     * @throws Exception if the data cannot be written
     */
    public void write(String data) throws Exception {
        Files.writeString(this.dataFilePath, data.strip());
    }

}
