package korvus.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import korvus.Config;

/**
 * Storage class as a controller for storage operations.
 */
public class Storage {
    private static String DEFAULT_STORAGE = "/";

    private String relativePath;
    private ArrayList<StorageParser<? extends Storable>> parserList;

    /**
     * Returns an instance of the object Storage.
     */
    public Storage() {
        this("");
    }

    /**
     * Returns an instance of the object Storage, given the relative path of a folder.
     * This instance of Storage is "bound to" said folder, and any subsequent file read is assumed to be in the folder.
     *
     * @param path (Relative) path of folder containing files.
     */
    public Storage(String path) {
        this.relativePath = path;
        this.parserList = new ArrayList<>();

        try {
            Files.createDirectories(Path.of(relativePath));
        } catch (IOException e) {
            this.relativePath = DEFAULT_STORAGE;
        }
    }

    /**
     * Returns a BufferedReader object, given the relative path of the file to read.
     *
     * @param fileName Name of the file to access.
     * @return BufferedReader bufferedReader.
     * @throws IOException If file cannot be read for whatever reason.
     */
    public BufferedReader readFile(String fileName) throws IOException {
        FileReader file = new FileReader(getFilePath(fileName));
        return new BufferedReader(file);
    }

    /**
     * Returns a BufferedWriter object, given the relative path of the file to read.
     *
     * @param fileName Name of the file to access.
     * @return BufferedWriter buffedWriter.
     * @throws IOException If file cannot be written for whatever reason.
     */
    public BufferedWriter writeFile(String fileName) throws IOException {
        FileWriter file = new FileWriter(getFilePath(fileName));
        return new BufferedWriter(file);
    }

    /**
     * Adds a StorageParser into the storage's list of storage parsers.
     * This ensures that there are no conflicting storage parsers (reading the same file),
     * throwing an exception if there are conflicting filePaths.
     *
     * @param newParser StorageParser to be added to the storage and checked.
     * @throws StorageConflictException If there exists another StorageParser that has the same file path.
     */
    public <T extends Storable<T>> void addParser(StorageParser<T> newParser) throws StorageConflictException {
        assert newParser.storage.equals(this);

        if (parserList.stream().anyMatch(parser -> parser.isParserConflict(newParser))) {
            throw new StorageConflictException("Cannot assign multiple parsers to one file");
        }

        parserList.add(newParser);
    }

    /**
     * Returns a Config object, which contains the configuration settings for Korvus.
     * If the config file cannot be found/read, the default config is generated instead.
     *
     * @return Config config.
     */
    public Config readConfigFile() throws FileNotFoundException {
        FileReader configFile = new FileReader(relativePath + Config.CONFIG_FILE);
        BufferedReader reader = new BufferedReader(configFile);

        return Config.readConfigFile(reader);
    }

    /**
     * Saves the given Config object to its config file.
     *
     * @param config Config object to be saved.
     */
    public void saveConfigFile(Config config) throws IOException {
        FileWriter configFile = new FileWriter(getFilePath(Config.CONFIG_FILE));
        BufferedWriter writer = new BufferedWriter(configFile);

        config.saveConfigFile(writer);
    }

    /**
     * Returns the relative file path of the file provided.
     *
     * @param fileName Name of the file to access.
     * @return Relative path of the file to access, from the directory of the bot.
     */
    private String getFilePath(String fileName) {
        return relativePath + fileName;
    }
}
