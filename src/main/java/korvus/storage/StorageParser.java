package korvus.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Parser to reading and writing to storage.
 * Each instance is specialised for a specific Storable.
 * @param <T> Storable to be specialised for.
 */
public class StorageParser<T extends Storable<T>> {
    protected Storage storage;
    private String fileName;

    /**
     * Returns an instance of the object StorageParser.
     *
     * @param storage Storage object pointing to the directory with files.
     * @param fileName Name of the file to be accessed.
     * @throws StorageConflictException If another parser in storage is pointing to the same file.
     */
    public StorageParser(Storage storage, String fileName) throws StorageConflictException {
        this.storage = storage;
        this.fileName = fileName;

        storage.addParser(this);
    }

    /**
     * Returns whether the given parser has conflicting file name.
     *
     * @param parser StorageParser to be checked with.
     * @return Boolean whether the current parser conflicts with the given parser.
     */
    public <S extends Storable<S>> boolean isParserConflict(StorageParser<S> parser) {
        return this.fileName.equals(parser.fileName);
    }

    /**
     * Returns the data from Storage.
     *
     * @return Data of object as a String.
     * @throws IOException If there are any errors in reading the file.
     */
    public String readStorage() throws IOException {
        BufferedReader reader = storage.readFile(fileName);
        String taskslistString = reader.readAllAsString();
        reader.close();

        return taskslistString;
    }

    /**
     * Writes the Storable into Storage.
     *
     * @param t The Storable object to be written to storage.
     * @throws IOException If there are any errors in writing to the file.
     */
    public void writeStorage(T t) throws IOException {
        BufferedWriter writer = storage.writeFile(fileName);
        writer.write(t.writeToString());
        writer.close();
    }
}
