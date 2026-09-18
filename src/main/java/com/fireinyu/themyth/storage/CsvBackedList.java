package com.fireinyu.themyth.storage;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fireinyu.themyth.exceptions.CorruptedTaskFileException;
import com.fireinyu.themyth.exceptions.FileAccessException;

/**
 * ArrayList that can be synced with a csv file on disk.
 * File access is managed through a LinesDiskDriver.
 *
 * @param <T> The type of elements in this list, which must be CsvSerializable.
 * @see LinesDiskDriver
 */
public abstract class CsvBackedList<T extends CsvSerializable> extends ArrayList<T> {

    private LinesDiskDriver storage;
    private boolean linked;

    /**
     * Initializes a CsvBackedList.
     * It is initially not backed by any FileLinesDisk so it acts as an ArrayList.
     * Call open() to sync to a FileLinesDisk.
     *
     * @see LinesDiskDriver
     */
    public CsvBackedList() {
        super();
        linked = false;
    }

    /**
     * Creates a FileLinesDisk instance to back this CsvBackedList.
     * Automatically pulls the LineDisk content into this CsvBackedList if opened successfully.
     * File content is deserialized into T instances.
     * Replaces any previous data in this list.
     *
     * @param path path to the CSV file for the FileLinesDisk instance.
     * @throws FileAccessException if the file cannot be created or opened for reading.
     * @throws CorruptedTaskFileException if the file content is corrupted and cannot be deserialized.
     * @see Path
     * @see FileLinesDisk
     * @see CsvSerializable
     */
    public void open(Path path) {
        storage = new FileLinesDisk(path);
        linked = true;
        fetch();
    }

    /**
     * Creates a ResourceLinesDisk instance to back this CsvBackedList.
     * Automatically pulls the ResourceLinesDisk content into this CsvBackedList if opened successfully.
     * File content is deserialized into T instances.
     * Replaces any previous data in this list.
     *
     * @param resourceName Name of the CSV resource to load.
     * @throws FileAccessException If the resource cannot be read.
     * @throws CorruptedTaskFileException if the resource file content is corrupted and cannot be deserialized.
     * @see Path
     * @see ResourceLinesDisk
     * @see CsvSerializable
     */
    public void open(String resourceName) {
        storage = new ResourceLinesDisk(resourceName);
        linked = true;
        fetch();
    }

    /**
     * Closes the FileLinesDisk instance that backs this CsvBackedList, if any.
     * Writes the current contents to the backing storage before unlinking the list.
     *
     * @throws FileAccessException if the file cannot be created or opened for writing.
     * @see Path
     * @see FileLinesDisk
     */
    public void close() {
        if (!linked) {
            return;
        }
        storage.writeLines(stream().map(CsvSerializable::extract).map(CsvBackedList::encodeRow));
        linked = false;
    }

    /**
     * Returns the descriptor associated with the LinesDiskDriver instance that backs this CsvBackedList.
     *
     * @return the descriptor associated with the LinesDiskDriver instance that backs this CsvBackedList.
     * @see String
     * @see LinesDiskDriver
     */
    public String getDescriptor() {
        if (storage == null) {
            return null;
        }
        return storage.getDescriptor();
    }

    /**
     * Parses a string array from a line in the CSV file into an object of type T.
     *
     * @param item The string array representing a row in the CSV.
     * @return The parsed object.
     * @throws CorruptedTaskFileException if the data is corrupted.
     */
    protected abstract T parse(String... item) throws CorruptedTaskFileException;

    private T parse(List<String> item) throws CorruptedTaskFileException {
        return parse(item.toArray(new String[0]));
    }

    /**
     * Encodes a row using the existing comma-space separator and quote-escaping format.
     */
    private static String encodeRow(List<String> fields) {
        List<String> escapedFields = fields.stream()
                .map(field -> field.replace("\"", "\"\""))
                .map(field -> field.replace(",", "\",\""))
                .toList();
        return String.join(", ", escapedFields);
    }

    /**
     * Decodes a stored row, undoing comma escaping before quote escaping.
     */
    private static List<String> decodeRow(String line) {
        return Arrays.stream(line.split(", "))
                .map(field -> field.replace("\",\"", ","))
                .map(field -> field.replace("\"\"", "\""))
                .toList();
    }

    /**
     * Fetches data from the backing file.
     * It clears the current list and repopulates it with data from the file.
     * The data is deserialized from CSV format to objects of type T.
     */
    private void fetch() {
        if (!linked) {
            return;
        }
        // Keep the list unlinked during loading so a failed load cannot overwrite the source file on close.
        linked = false;
        clear();
        super.addAll(storage.readLines()
                .map(CsvBackedList::decodeRow)
                .map(this::parse)
                .toList()
        );
        linked = true;
    }
}
