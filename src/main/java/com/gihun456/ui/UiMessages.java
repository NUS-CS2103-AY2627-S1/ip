package com.gihun456.ui;

/**
 * Stores user-facing messages displayed by the application.
 */
public final class UiMessages {
    public static final String PRODUCT_NAME = "Gihun456";

    public static final String BANNER = """
                         __                    _   _\s\s
                        /__ o |_      ._ |_|_ |_  |_\s\s
                        \\_| | | | |_| | |  |   _) |_)

                         """;

    public static final String GREETING = "Hello! I'm " + PRODUCT_NAME + ".\nAre you ready to play a game?";
    public static final String FAREWELL = "Player 456 eliminated.";
    public static final String SEP = "____________________________________________________________";

    public static final String LIST_TASKS = "Here are the games in your list:";
    public static final String LIST_MATCHING_TASKS = "Here are the games matching your search:";
    public static final String NO_MATCHING_TASKS = "No matching games found.";
    public static final String ADD_TASK = "Green light, Player 456. This game has been added:";
    public static final String REMOVE_TASK = "This game has been removed from your list:";
    public static final String MARK_TASK = "Well done, Player 456. You've cleared this game:";
    public static final String UNMARK_TASK = "This game is back on the list:";
    public static final String EMPTY_STORAGE = "The game list is empty.";
    public static final String UPCOMING_REMINDERS = "Upcoming games:";
    public static final String MISSED_REMINDERS = "Missed games:";
    public static final String NO_REMINDERS = "No upcoming games waiting for you.";
    public static final String CONFLICT_WARNING = "Careful, Player 456. This game overlaps with:";
    public static final String PROPOSED_TASK = "Your proposed game:";
    public static final String CONFLICTING_TASKS = "Games already in this time slot:";
    public static final String CONFLICT_CONFIRMATION = "Time to make a choice, Player 456. (yes/no)";
    public static final String TASK_NOT_ADDED = "Understood. The game was not added.";
    public static final String TASK_COUNT = "Player 456, you now have %d games in your list.";

    private UiMessages() {
    }

    /**
     * Creates a message showing the current number of tasks.
     *
     * @param taskCount Number of tasks in the list.
     * @return Formatted task-count message.
     */
    public static String getTaskCountMessage(int taskCount) {
        return String.format(TASK_COUNT, taskCount);
    }
}
