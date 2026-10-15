package orangutan.command;

import java.util.Comparator;

import orangutan.chatlist.ListItem;

/**
 * Enum type denoting the three main types of sort methods: none, completion, and datetime.
 * Also contains the cmp field, which is the corresponding comparator.
 */
public enum SortMethod {
    NONE((e1, e2) -> 0),
    COMPLETION(Comparator.comparing(ListItem::isCompleted).thenComparing(ListItem::getTitle)),
    DATETIME(Comparator.comparing(ListItem::getStartDateTime).thenComparing(ListItem::getTitle));

    private final Comparator<ListItem> cmp;

    SortMethod(Comparator<ListItem> cmp) {
        this.cmp = cmp;
    }

    public Comparator<ListItem> getComparator() {
        return cmp;
    }
}
