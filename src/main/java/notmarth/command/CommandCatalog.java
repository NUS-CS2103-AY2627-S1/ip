package notmarth.command;

import java.util.List;

/** Defines every command keyword shown to NotMarth users. */
public final class CommandCatalog {
    public static final String TODO = "todo";
    public static final String DEADLINE = "deadline";
    public static final String EVENT = "event";
    public static final String LIST = "list";
    public static final String FIND = "find";
    public static final String ON = "on";
    public static final String MARK = "mark";
    public static final String UNMARK = "unmark";
    public static final String DELETE = "delete";
    public static final String CONTACT = "contact";
    public static final String LIST_CONTACTS = "listcontacts";
    public static final String FIND_CONTACT = "findcontact";
    public static final String DELETE_CONTACT = "deletecontact";
    public static final String BYE = "bye";

    private static final List<String> VISIBLE_COMMANDS = List.of(
            TODO, DEADLINE, EVENT, LIST, FIND, ON, MARK, UNMARK, DELETE,
            CONTACT, LIST_CONTACTS, FIND_CONTACT, DELETE_CONTACT, BYE);

    private CommandCatalog() {
        // Utility class; do not create instances.
    }

    /**
     * Returns every non-hidden command in the order used by help messages.
     *
     * @return a comma-separated command list.
     */
    public static String getVisibleCommandList() {
        return String.join(", ", VISIBLE_COMMANDS);
    }
}
