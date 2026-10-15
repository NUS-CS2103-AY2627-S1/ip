package zinc.ui;

/** Categories used to organize Zinc's command reference. */
public enum HelpCategory {
    GENERAL("General"),
    TASKS("Tasks"),
    CONTACTS("Contacts");


    /** The category name displayed to users. */
    private final String displayName;

    /**
     * Creates a help category with the supplied display name.
     *
     * @param displayName The category name displayed to users.
     */
    HelpCategory(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the category name displayed to users.
     *
     * @return The display name.
     */
    public String getDisplayName() {
        return displayName;
    }
}
