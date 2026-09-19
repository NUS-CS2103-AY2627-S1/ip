package nyonbot.storage;

/**
 * Stores and handles NyonBot's list storage.
 */
public class ListStorage extends Storage {
    private static ListStorage instance = null;
    /**
     * Creates the ListStorage.
     */
    private ListStorage() {
        super("data/nyonbot.txt");
    }

    /**
     * Instance getter.
     */
    public static ListStorage getInstance() {
        if (instance == null) {
            instance = new ListStorage();
        }
        return instance;
    }
}
