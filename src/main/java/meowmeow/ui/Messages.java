package meowmeow.ui;

import java.util.Random;

/**
 * The pool of phrasings Meowmeow can pick from for each kind of message, plus
 * the picking logic itself. Kept apart from {@link Ui} so the wording (which
 * changes often and should stay skimmable) doesn't clutter the class that
 * decides <em>when</em> each message is shown.
 *
 * <p>Every category below has several phrasings; {@link Ui} asks for one by
 * category and supplies the {@link Random}, so the choice can be seeded. The
 * console entry point uses a fixed seed so its output stays byte-for-byte
 * reproducible for {@code test/ui-test-plan.md}; the GUI seeds with a real
 * random source instead.
 */
final class Messages {

    private static final String[] ADDED = {
        " Fine. Added. Happy now?",
        " Another one? Bold of you to think you'll finish it.",
        " Added. Don't make me regret this.",
        " Ugh, fine, it's on the list.",
        " There. Added. You're welcome.",
    };

    private static final String[] REMOVED = {
        " Gone. Like it never existed.",
        " Removed. One less thing to ignore.",
        " Poof. Deleted.",
        " Fine, it's gone. Don't ask me to bring it back.",
        " Deleted without ceremony.",
    };

    private static final String[] MARKED_DONE = {
        " Look at you, finishing things.",
        " Marked done. Miracles happen.",
        " Done? Suspicious, but I'll allow it.",
        " Finally.",
        " Marked as done. I'm almost impressed.",
    };

    private static final String[] MARKED_NOT_DONE = {
        " Back on the list. Typical.",
        " Unmarked. Couldn't commit, huh?",
        " Fine, not done. We'll pretend that didn't happen.",
        " Marked not done. Shocking.",
        " Undone. As expected.",
    };

    private static final String[] PRIORITY_CHANGED = {
        " Fine, that one matters more now.",
        " Priority updated. Try to actually act on it.",
        " Reordered your regrets for you.",
        " Done. Act like it means something this time.",
        " There. Now it looks important.",
    };

    private static final String[] LIST_HEADER = {
        " Here's everything you're avoiding:",
        " Your ever-growing list of regrets:",
        " Behold, your tasks:",
        " Here's what you asked for:",
        " The full list, for better or worse:",
    };

    private static final String[] DATE_HEADER = {
        " Here's what's happening on %s:",
        " Your %s agenda, such as it is:",
        " On %s, you're doing this:",
        " %s's lineup:",
    };

    private static final String[] EMPTY_DATE = {
        " Nothing on %s. Free day, enjoy it.",
        " %s is wide open. Don't waste it.",
        " No tasks on %s. Suspicious.",
        " %s: a blank slate.",
    };

    private static final String[] FIND_HEADER = {
        " Found these, unfortunately for you:",
        " Here's what matched:",
        " I found some things you'd rather forget:",
        " Matches found. You're welcome.",
    };

    private static final String[] EMPTY_FIND = {
        " Nothing matched. Try harder.",
        " No matches. Maybe you imagined it.",
        " Came up empty. Story of your life.",
        " Nothing found. Are you sure that's a word?",
    };

    private static final String[] ERROR_LEAD_INS = {
        " Excuse me?",
        " That's not how any of this works.",
        " Bold move, but no.",
        " I refuse to guess. Try again.",
        " Nice try.",
    };

    private static final String[] WELCOME_PLAIN = {
        "Meowmeow's here. Try not to disappoint me.",
        "I'm Meowmeow. Let's see what you've got.",
        "Meowmeow, reporting for duty. Reluctantly.",
        "Meowmeow is awake. This better be good.",
    };

    private static final String[] WELCOME_RICH = {
        "🐾 Meowmeow's awake. Try not to waste my time.",
        "🐾 I'm Meowmeow. Let's see what you've got.",
        "🐾 Meowmeow, reporting for duty. Reluctantly.",
        "🐾 Well, look who needs a task manager.",
    };

    private static final String[] FAREWELL_PLAIN = {
        "Meow! Bye bye~",
        "Leaving already? Fine, meow.",
        "Bye. Don't come crying when you forget something.",
        "Meow! See you when you inevitably need me again.",
    };

    private static final String[] FAREWELL_RICH = {
        "👋 Bye. Try not to need me again so soon.",
        "👋 Leaving already? Fine.",
        "👋 See you when you inevitably forget something.",
        "👋 Later. I have naps to attend to.",
    };

    private Messages() {
    }

    /** Returns a randomly chosen "task added" reaction line. */
    static String added(Random random) {
        return pick(random, ADDED);
    }

    /** Returns a randomly chosen "task removed" reaction line. */
    static String removed(Random random) {
        return pick(random, REMOVED);
    }

    /** Returns a randomly chosen "marked as done" reaction line. */
    static String markedDone(Random random) {
        return pick(random, MARKED_DONE);
    }

    /** Returns a randomly chosen "marked as not done" reaction line. */
    static String markedNotDone(Random random) {
        return pick(random, MARKED_NOT_DONE);
    }

    /** Returns a randomly chosen "priority changed" reaction line. */
    static String priorityChanged(Random random) {
        return pick(random, PRIORITY_CHANGED);
    }

    /** Returns a randomly chosen header for a non-empty task list. */
    static String listHeader(Random random) {
        return pick(random, LIST_HEADER);
    }

    /** Returns a randomly chosen header for a non-empty "list on a date" query, with one {@code %s} for the date. */
    static String dateHeader(Random random) {
        return pick(random, DATE_HEADER);
    }

    /** Returns a randomly chosen "nothing on this date" line, with one {@code %s} for the date. */
    static String emptyDate(Random random) {
        return pick(random, EMPTY_DATE);
    }

    /** Returns a randomly chosen header for non-empty "find" results. */
    static String findHeader(Random random) {
        return pick(random, FIND_HEADER);
    }

    /** Returns a randomly chosen "no matches found" line. */
    static String emptyFind(Random random) {
        return pick(random, EMPTY_FIND);
    }

    /** Returns a randomly chosen in-character opener shown before an error's factual detail. */
    static String errorLeadIn(Random random) {
        return pick(random, ERROR_LEAD_INS);
    }

    /** Returns a randomly chosen welcome greeting in the given style. */
    static String welcome(MessageStyle style, Random random) {
        return pick(random, style == MessageStyle.PLAIN ? WELCOME_PLAIN : WELCOME_RICH);
    }

    /** Returns a randomly chosen farewell line in the given style. */
    static String farewell(MessageStyle style, Random random) {
        return pick(random, style == MessageStyle.PLAIN ? FAREWELL_PLAIN : FAREWELL_RICH);
    }

    /** Returns one randomly chosen element of {@code variants}. */
    private static String pick(Random random, String[] variants) {
        return variants[random.nextInt(variants.length)];
    }
}
