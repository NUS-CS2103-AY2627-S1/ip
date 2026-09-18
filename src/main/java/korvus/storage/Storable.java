package korvus.storage;

import java.io.IOException;

/**
 * Interface with Generics for marking an object to be able to be saved to file.
 * @param <T> Generic referring to the storable object.
 */
public interface Storable<T> {

    /**
     * Returns the current Storable in a string for saving to file.
     *
     * @return String to be written to a file.
     */
    String writeToString();

    /**
     * Reads the provided parser and saves its data in itself.
     *
     * @param parser StorageParser that contains the information needed to read the file.
     * @return Boolean whether the reading process had any errors.
     * @throws IOException If there is any error reading the file (with the parser).
     */
    boolean readFromParser(StorageParser<? extends Storable<T>> parser) throws IOException;
}
