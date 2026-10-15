package min.storage;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Saves and loads Min's data on the hard disk, storing one item per line.
 * <p>
 * Subclasses supply the conversion between an item and its saved line.
 *
 * @param <T> The type of item held in the data file.
 */
public abstract class Storage<T> {
    private final Path filePath;

    /**
     * Creates storage that uses the given data file.
     *
     * @param filePath The data file to read and write.
     */
    protected Storage(Path filePath) {
        assert filePath != null : "Data file path must not be null.";

        this.filePath = filePath;
    }

    /**
     * Rewrites the data file with the given items.
     *
     * @param items The items to save.
     * @throws IOException If the data file cannot be written.
     */
    public void save(List<T> items) throws IOException {
        Files.createDirectories(filePath.getParent());

        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            for (T item : items) {
                writer.write(encode(item));
                writer.newLine();
            }
        }
    }

    /**
     * Loads all saved items from the data file.
     * Blank and whitespace-only lines are ignored.
     *
     * @return A read-only list of loaded items, or an empty list when no data file exists.
     * @throws IOException If the data file cannot be read.
     */
    public List<T> load() throws IOException {
        if (!Files.exists(filePath)) {
            return List.of();
        }

        return Files.readAllLines(filePath).stream()
                .filter(line -> !line.isBlank())
                .map(this::decode)
                .toList();
    }

    /**
     * Returns the item in the format used for persistent storage.
     *
     * @param item The item to convert.
     * @return The line to write to the data file.
     */
    protected abstract String encode(T item);

    /**
     * Recreates an item from one line of saved data.
     *
     * @param line A line read from the data file.
     * @return The recreated item.
     */
    protected abstract T decode(String line);
}
