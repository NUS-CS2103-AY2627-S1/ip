package org.cs2103t.marquee.cli.command;

import org.cs2103t.marquee.core.Marquee;

/**
 * Data class for codes that base {@code Marquee} support.
 *
 * @see Marquee
 */
public final class Codes {
    /** Ends the chat session */
    public static final Code EXIT = Code.createOrGet("bye");
    /** Gets user manual */
    public static final Code HELP = Code.createOrGet("help", "");
    /** Loads the checklist from the save file */
    public static final Code LOAD = Code.createOrGet("load");
    /** Saves the checklist to the save file */
    public static final Code SAVE = Code.createOrGet("save");
    /** Lists all tasks in the checklist */
    public static final Code LIST = Code.createOrGet("list");
    /** Searches for tasks that matches the filters */
    public static final Code FIND = Code.createOrGet("find",
            "", "from", "to", "completed", "incomplete", "tags", "chain"
    );
    /** Create a new to-do task and add it to the checklist */
    public static final Code TODO = Code.createOrGet("todo", "", "completed", "incomplete");
    /** Create a new task with deadline and add it to the checklist */
    public static final Code DEADLINE = Code.createOrGet("deadline",
            "", "by", "completed", "incomplete"
    );
    /** Create a new event and add it to the checklist */
    public static final Code EVENT = Code.createOrGet("event",
            "", "from", "to", "completed", "incomplete"
    );
    /** Modify the properties of tasks at the given indices */
    public static final Code EDIT = Code.createOrGet("edit",
            "", "description", "from", "to", "completed", "incomplete", "chain"
    );
    /** Modify the properties of all task in the checklist */
    public static final Code EDIT_ALL = Code.createOrGet("edit-all",
            "description", "from", "to", "completed", "incomplete", "chain"
    );
    /** Delete the tasks at the given indices */
    public static final Code DELETE = Code.createOrGet("delete", "", "chain");
    /** Delete all tasks in the checklist */
    public static final Code DELETE_ALL = Code.createOrGet("delete-all", "chain");
    /** Delete all tasks that matches the filters */
    public static final Code DELETE_MATCHING = Code.createOrGet("delete-matching",
            "", "from", "to", "completed", "incomplete", "tags", "chain"
    );
    /** Mark the tasks at the given indices as completed */
    public static final Code MARK = Code.createOrGet("mark", "", "chain");
    /** Mark all tasks in the checklist as completed */
    public static final Code MARK_ALL = Code.createOrGet("mark-all", "chain");
    /** Mark all tasks that matches the filters as completed*/
    public static final Code MARK_MATCHING = Code.createOrGet("mark-matching",
            "", "from", "to", "completed", "incomplete", "tags", "chain"
    );
    /** Remove the mark from the tasks at the given indices */
    public static final Code UNMARK = Code.createOrGet("unmark", "", "chain");
    /** Remove the mark from all tasks in the checklist */
    public static final Code UNMARK_ALL = Code.createOrGet("unmark-all", "chain");
    /** Remove the mark from all tasks that matches the filters */
    public static final Code UNMARK_MATCHING = Code.createOrGet("unmark-matching",
            "", "from", "to", "completed", "incomplete", "tags", "chain"
    );

    // prevent instantiation
    private Codes() {}
}
