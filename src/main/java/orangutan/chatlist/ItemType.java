package orangutan.chatlist;

/**
 * Enum type denoting the three main types of list items: to-do, deadline, event.
 * Also contains the icon field, which is a short string depicting the item type.
 */
public enum ItemType {
    TODO("[T]"),
    DEADLINE("[D]"),
    EVENT("[E]");

    private final String icon;

    ItemType(String icon) {
        this.icon = icon;
    }

    public String getIcon() {
        return icon;
    }

    /**
     * Returns an ItemType based on its icon.
     *
     * @param icon Three-character String, identical to the one found in the "icon" field (see above).
     * @return The ItemType corresponding to the icon.
     */
    public static ItemType getTypeFromIcon(String icon) {
        return switch (icon) {
            case "[T]" -> TODO;
            case "[D]" -> DEADLINE;
            case "[E]" -> EVENT;
            default -> null;
        };
    }
}
