package carl.task;

/**
 * Represents the description or core content of a task.
 */
public class Item {

    private final String name;

    /**
     * Constructs a new Item with the given name.
     *
     * @param name The description or name of the item.
     */
    public Item(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Checks if the item's name contains the specified keyword.
     * The search is case-insensitive.
     *
     * @param keyword The keyword to search for.
     * @return true if the keyword is found within the name, false otherwise.
     */
    public boolean hasNameMatch(String keyword) {
        return this.name.toLowerCase().contains(keyword.toLowerCase());
    }
}
