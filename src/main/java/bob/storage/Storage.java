package bob.storage;

import bob.exception.BobException;

/**
 * Defines operations for loading and saving data of type {@code T}.
 *
 * @param <T> the type of data being loaded and saved
 */
public interface Storage<T> {

    /**
     * Loads the stored data.
     *
     * @return the loaded data
     * @throws BobException if loading fails
     */
    T load() throws BobException;

    /**
     * Saves the given item to storage.
     *
     * @param item the item to save
     * @throws BobException if saving fails
     */
    void save(T item) throws BobException;
}
