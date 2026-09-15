package luke.commands;

import java.util.Arrays;
import java.util.EnumMap;

import luke.Luke;
import luke.exceptions.InvalidArgumentException;
import luke.exceptions.InvalidFlagException;
import luke.exceptions.UserInputException;
import luke.tasks.Flag;
import luke.tasks.ItemList;
import luke.tasks.SortCriterion;

/**
 * Represents a command with behavior defined directly by an enum constant. Each
 * constant carries the keyword that triggers it and provides its own
 * {@link Command#execute} body, so a command's keyword and behavior live together.
 * Looking a keyword up is done here via {@link #findByKeyword}, so the rest of the
 * program has no second list to keep in sync.
 */
public enum FixedCommand implements Command {
    /** Says goodbye and ends the program. */
    BYE("bye") {
        @Override
        public void execute(Luke bot, String argument, EnumMap<Flag, String> flags)
                throws UserInputException {
            rejectUnexpectedArgument("bye", argument);
            rejectUnsupportedFlags(flags);
            bot.say("Bye. Hope to see you again soon!");
        }

        @Override
        public boolean shouldExit() {
            return true;
        }
    },

    /** Shows all items, or a placeholder message if there are none yet. */
    LIST("list") {
        @Override
        public void execute(Luke bot, String argument, EnumMap<Flag, String> flags)
                throws UserInputException {
            rejectUnexpectedArgument("list", argument);
            SortCriterion sortCriterion = parseListSortCriterion(flags);

            ItemList items = bot.getItems();
            if (items.isEmpty()) {
                bot.say("No items added.");
            } else if (sortCriterion != null) {
                bot.say(items.formatAllItemsSorted(sortCriterion));
            } else {
                bot.say(items.formatAllItems());
            }
        }
    },

    /** Marks a specific task as done. */
    MARK("mark") {
        @Override
        public void execute(Luke bot, String argument, EnumMap<Flag, String> flags)
                throws UserInputException {
            rejectUnsupportedFlags(flags);

            int itemIndex = getExistingItemIndex("mark", argument, bot.getItems());
            ItemList items = bot.getItems();
            items.setCompletion(itemIndex, true);
            bot.say("Nice! I've marked this task as done:\n %s".formatted(
                    items.formatOneItem(itemIndex)
            ));
        }

        @Override
        public boolean shouldSaveItemList() {
            return true;
        }
    },

    /** Marks a specific task as not done. */
    UNMARK("unmark") {
        @Override
        public void execute(Luke bot, String argument, EnumMap<Flag, String> flags)
                throws UserInputException {
            rejectUnsupportedFlags(flags);

            int itemIndex = getExistingItemIndex("unmark", argument, bot.getItems());
            ItemList items = bot.getItems();
            items.setCompletion(itemIndex, false);
            bot.say("OK! I've marked this task as not done yet:\n %s".formatted(
                    items.formatOneItem(itemIndex)
            ));
        }

        @Override
        public boolean shouldSaveItemList() {
            return true;
        }
    },

    /** Deletes a specific task from the list. */
    DELETE("delete") {
        @Override
        public void execute(Luke bot, String argument, EnumMap<Flag, String> flags)
                throws UserInputException {
            rejectUnsupportedFlags(flags);

            int itemIndex = getExistingItemIndex("delete", argument, bot.getItems());
            ItemList items = bot.getItems();
            String removedItem = items.formatOneItem(itemIndex);
            items.remove(itemIndex);
            bot.say("Noted. I've removed this task:\n"
                    + " %s\n".formatted(removedItem)
                    + "Now you have %d tasks in the list.".formatted(items.size())
            );
        }

        @Override
        public boolean shouldSaveItemList() {
            return true;
        }
    },

    /** Finds tasks whose descriptions contain the given search text. */
    FIND("find") {
        @Override
        public void execute(Luke bot, String argument, EnumMap<Flag, String> flags)
                throws UserInputException {
            rejectUnsupportedFlags(flags);

            if (argument.isBlank()) {
                throw InvalidArgumentException.missing("find");
            }

            String matchingItems = bot.getItems().formatItemsMatchingName(argument);
            if (matchingItems.isBlank()) {
                bot.say("No matching tasks found.");
            } else {
                bot.say("Here are the matching tasks in your list:\n" + matchingItems);
            }
        }
    };

    /** The word the user types to trigger this command. */
    private final String keyword;

    FixedCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Rejects text after commands that do not accept arguments.
     *
     * @param command the command name used in the error message
     * @param argument the text after the command keyword
     * @throws InvalidArgumentException if {@code argument} is not blank
     */
    private static void rejectUnexpectedArgument(String command, String argument)
            throws InvalidArgumentException {
        if (!argument.isBlank()) {
            throw InvalidArgumentException.unexpected(command, argument);
        }
    }

    /**
     * Rejects flags for commands that do not accept any flags.
     *
     * @param flags parsed flags from the user input
     * @throws InvalidFlagException if any flag is present
     */
    private static void rejectUnsupportedFlags(EnumMap<Flag, String> flags)
            throws InvalidFlagException {
        if (!flags.isEmpty()) {
            Flag flag = flags.keySet().iterator().next();
            throw InvalidFlagException.unsupported(flag.name().toLowerCase());
        }
    }

    /**
     * Parses the optional sort criterion for the list command.
     *
     * @param flags parsed flags from the user input
     * @return the requested sort criterion, or {@code null} when sorting was not requested
     * @throws InvalidFlagException if the sort flag is invalid or another flag is present
     */
    private static SortCriterion parseListSortCriterion(EnumMap<Flag, String> flags)
            throws InvalidFlagException {
        if (flags.isEmpty()) {
            return null;
        }

        Flag unsupportedFlag = flags.keySet().stream()
                .filter(flag -> flag != Flag.SORT)
                .findFirst()
                .orElse(null);
        if (unsupportedFlag != null) {
            throw InvalidFlagException.unsupported(unsupportedFlag.name().toLowerCase());
        }

        String sortValue = flags.get(Flag.SORT);
        SortCriterion sortCriterion = SortCriterion.findByKeyword(sortValue);
        if (sortCriterion == null) {
            throw InvalidFlagException.unsupportedValue("sort", sortValue);
        }
        return sortCriterion;
    }

    /**
     * Converts the user's 1-based task number into a valid item-list index.
     *
     * @param command the command name used in the error message
     * @param argument the text that should contain the task number
     * @param items the task list to check against
     * @return the matching 0-based item index
     * @throws InvalidArgumentException if the argument is not a valid existing item number
     */
    private static int getExistingItemIndex(String command, String argument, ItemList items)
            throws InvalidArgumentException {
        int itemIndex;
        try {
            itemIndex = Integer.parseInt(argument) - 1; // User-facing indexes start from 1.
        } catch (NumberFormatException e) {
            throw InvalidArgumentException.invalidIndex(command, argument);
        }

        if (itemIndex < 0 || itemIndex >= items.size()) {
            throw InvalidArgumentException.outOfBoundsIndex(command, argument);
        }
        return itemIndex;
    }

    /**
     * Returns the command whose keyword matches the given word (case-insensitive).
     *
     * @param word the first word typed by the user
     * @return the matching command, or {@code null} if none matches
     */
    public static Command findByKeyword(String word) {
        return Arrays.stream(values())
                .filter(command -> word.equalsIgnoreCase(command.keyword))
                .findFirst()
                .orElse(null);
    }
}
