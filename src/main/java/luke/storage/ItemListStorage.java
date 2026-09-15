package luke.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import luke.exceptions.StorageException;
import luke.tasks.Flag;
import luke.tasks.ItemList;
import luke.tasks.TaskTypes;

/**
 * Handles loading and saving the task list.
 *
 * <p>The storage package depends on task model classes such as
 * {@link ItemList}, but the task model does not depend on storage.</p>
 */
public final class ItemListStorage {
    private static final String STORAGE_PATH_PROPERTY = "luke.storage.path";
    private static final String DEFAULT_FILE_PATH = "./data/itemlist.txt";

    private ItemListStorage() {
    }

    /**
     * Returns the storage path to use. Tests can override the default path so
     * they do not read from or write to the user's real task file.
     *
     * @return configured storage path, or the default path if none is configured
     */
    private static String getStoragePath() {
        return System.getProperty(STORAGE_PATH_PROPERTY, DEFAULT_FILE_PATH);
    }

    /**
     * Returns the storage file path, creating its parent directory and file if
     * they do not exist yet. An existing path must be a regular file so storage
     * behavior remains consistent across operating systems.
     *
     * @param filePath path to Luke's storage file
     * @return existing storage file path
     * @throws IOException if the file or its parent directory cannot be created
     */
    private static Path getFilePath(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }
        if (!Files.exists(path)) {
            Files.createFile(path);
        }
        if (!Files.isRegularFile(path)) {
            throw new IOException("Storage path is not a regular file: " + path);
        }
        return path;
    }

    /**
     * Loads the task list from disk.
     *
     * @return the saved task list, or an empty list if no saved data exists
     * @throws StorageException if the data file cannot be read or created
     */
    public static ItemList load() throws StorageException {
        try {
            Path filePath = getFilePath(getStoragePath());
            if (Files.size(filePath) == 0) {
                return new ItemList();
            }
            return parseStorageLines(Files.readAllLines(filePath));
        } catch (IOException | IllegalArgumentException e) {
            throw StorageException.loadFailed(e);
        }
    }

    /**
     * Saves the task list to disk.
     *
     * @param itemList the task list to save
     * @throws StorageException if the data file cannot be written
     */
    public static void save(ItemList itemList) throws StorageException {
        try {
            Path filePath = getFilePath(getStoragePath());
            Files.write(filePath, formatStorageLines(itemList));
        } catch (IOException e) {
            throw StorageException.saveFailed(e);
        }
    }

    /**
     * Formats the task list into storage lines.
     *
     * @param itemList task list to save
     * @return one tab-separated line per item
     */
    private static List<String> formatStorageLines(ItemList itemList) {
        List<String> lines = new ArrayList<>();
        for (int i = 0; i < itemList.size(); i++) {
            TaskTypes taskType = itemList.getTaskType(i);
            EnumMap<Flag, String> flags = itemList.getFlags(i);
            assert flags.keySet().equals(taskType.getFlags())
                    : "Stored item flags must match task type requirements.";
            List<String> fields = new ArrayList<>(List.of(
                    taskType.name(),
                    itemList.isDone(i) ? "1" : "0",
                    itemList.getName(i)
            ));
            for (Flag flag : taskType.getFlags()) {
                fields.add(flags.get(flag));
            }
            lines.add(String.join("\t", fields));
        }
        return lines;
    }

    /**
     * Parses storage lines into an item list.
     *
     * @param lines saved storage lines
     * @return task list represented by the lines
     */
    private static ItemList parseStorageLines(List<String> lines) {
        ItemList itemList = new ItemList();
        for (String line : lines) {
            addStorageLine(itemList, line);
        }
        return itemList;
    }

    /**
     * Parses and adds one saved line to the item list.
     *
     * @param itemList task list being loaded
     * @param line saved storage line
     */
    private static void addStorageLine(ItemList itemList, String line) {
        String[] fields = line.split("\t", -1);
        if (fields.length < 3) {
            throw new IllegalArgumentException("Storage line has missing fields.");
        }

        TaskTypes taskType = parseStoredTaskType(fields[0]);
        boolean isDone = parseStoredCompletion(fields[1]);
        String itemName = fields[2];

        if (fields.length != 3 + taskType.getFlags().size()) {
            throw new IllegalArgumentException("Storage line has wrong number of fields.");
        }

        EnumMap<Flag, String> flags = new EnumMap<>(Flag.class);
        int fieldIndex = 3;
        for (Flag flag : taskType.getFlags()) {
            String value = fields[fieldIndex++];
            if (value.isBlank()) {
                throw new IllegalArgumentException("Storage line has a blank flag value.");
            }
            flags.put(flag, value);
        }
        assert fieldIndex == fields.length : "All storage fields should be consumed.";
        itemList.addLoadedItem(itemName, taskType, flags, isDone);
    }

    /**
     * Parses a saved task type value.
     *
     * @param value saved task type text
     * @return matching task type
     */
    private static TaskTypes parseStoredTaskType(String value) {
        try {
            return TaskTypes.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Storage line has invalid task type.", e);
        }
    }

    /**
     * Parses a saved completion value.
     *
     * @param value saved completion text
     * @return {@code true} if the saved value means done
     */
    private static boolean parseStoredCompletion(String value) {
        if (value.equals("1")) {
            return true;
        } else if (value.equals("0")) {
            return false;
        }
        throw new IllegalArgumentException("Storage line has invalid completion value.");
    }
}
