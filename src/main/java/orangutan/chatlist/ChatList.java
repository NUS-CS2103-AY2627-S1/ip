package orangutan.chatlist;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import orangutan.command.SortMethod;

/**
 * Stores listItems of ListItems, and provides functionality to add, modify and remove them.
 */
public class ChatList {
    private final ArrayList<ListItem> listItems;
    private SortMethod sortMethod = SortMethod.NONE;

    /**
     * Creates an empty ChatList.
     */
    public ChatList() {
        listItems = new ArrayList<ListItem>();
    }

    /**
     * Creates a new ChatList from existing data.
     *
     * @param listItems ArrayList of ListItems to be stored in this listItems.
     */
    public ChatList(ArrayList<ListItem> listItems) {
        assert listItems != null : "ChatList says: listItems should not be null!!!";

        this.listItems = listItems;
    }

    /**
     * Returns the listItems as a stream of ListItems.
     */
    public Stream<ListItem> toStream() {
        return listItems.stream();
    }

    /**
     * Adds item to the end of the listItems.
     *
     * @param item ListItem object, representing a to-do, event or deadline in the listItems.
     */
    public void addItem(ListItem item) {
        assert item != null : "ChatList says: item should not be null!!!";

        listItems.add(item);
        if (sortMethod != SortMethod.NONE) {
            listItems.sort(sortMethod.getComparator());
        }
    }

    /**
     * Marks existing item as completed.
     *
     * @param index Position of item in the listItems (starting with 1).
     * @return Marked listItems item, in the form of a String.
     */
    public String markItem(int index) {
        ListItem item = listItems.get(index - 1);
        item.mark();
        if (sortMethod != SortMethod.NONE) {
            listItems.sort(sortMethod.getComparator());
        }

        return item.toString();
    }

    /**
     * Unmarks existing item, then return it as a String.
     *
     * @param index Position of item in the listItems (starting with 1).
     * @return Unmarked listItems item, in the form of a String.
     */
    public String unmarkItem(int index) {
        ListItem item = listItems.get(index - 1);
        item.unmark();
        if (sortMethod != SortMethod.NONE) {
            listItems.sort(sortMethod.getComparator());
        }

        return item.toString();
    }

    /**
     * Deletes item from the listItems.
     *
     * @param index Position of item in the listItems (starting with 1).
     * @return Deleted listItems item, in the form of a String.
     */
    public String deleteItem(int index) {
        ListItem item = listItems.get(index - 1);
        listItems.remove(index - 1);
        return item.toString();
    }

    /**
     * Finds items whose descriptions contain query.
     *
     * @param query String to be found.
     * @return ChatList of matching items.
     */
    public ChatList findItem(String query) {
        List<ListItem> matchStream = listItems.stream()
                .filter(item -> item.getTitle().toLowerCase().contains(query.toLowerCase()))
                .toList();
        return new ChatList(new ArrayList<ListItem>(matchStream));
    }

    /**
     * Gets length of the listItems.
     */
    public int getLength() {
        return listItems.size();
    }

    public SortMethod getSortMethod() {
        return sortMethod;
    }

    /**
     * Enables sorting of the list.
     * Will immediately sort the list. New additions will be added to the correct location.
     * This is the only way to enable sorting, so when sorting the list we are guaranteed to have a comparator.
     */
    public void setSorting(SortMethod method) {
        sortMethod = method;
        listItems.sort(method.getComparator());
    }

    /**
     * Prints out an enumerated listItems of items.
     */
    @Override
    public String toString() {
        String res = "";
        for (int i = 1; i <= listItems.size(); i++) {
            res += (i + ". ");
            res += listItems.get(i - 1);
            if (i < listItems.size()) {
                res += "\n";
            }
        }

        return res;
    }
}
