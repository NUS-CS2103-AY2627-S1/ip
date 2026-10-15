package doe;


/**
 * Deals with making sense of the user command and inputs.
 */
public class Parser {
    /**
     * Represents the available commands in the main menu.
     */
    public enum MainMenu {
        NEIGH, MEOW, LIST, TODO, BYE, UNKNOWN;

        /**
         * Parses a string input into a MainMenu enum value.
         *
         * @param input The user input string to parse.
         * @return The corresponding MainMenu value, or UNKNOWN if value is invalid
         */
        public static MainMenu fromString(String input) {
            switch (input.toLowerCase().trim()) {
                case "1":
                case "neigh":
                    return NEIGH;
                case "2":
                case "meow":
                    return MEOW;
                case "3":
                case "list":
                    return LIST;
                case "4":
                case "todo":
                    return TODO;
                case "bye":
                    return BYE;
                default:
                    return UNKNOWN;
            }
        }
    }

    /**
     * Represents the available commands in the todo menu.
     */
    public enum TodoMenu {
        ADD, REMOVE, VIEW, SORT, MARK, UNMARK, FIND, EXIT, UNKNOWN;

        /**
         * Parses a string input into a TodoMenu enum value.
         *
         * @param input The user input string to parse.
         * @return The corresponding TodoMenu value, or UNKNOWN if value is invalid
         */
        public static TodoMenu fromString(String input) {
            switch (input.toLowerCase().trim()) {
                case "1":
                case "add":
                    return ADD;
                case "2":
                case "remove":
                    return REMOVE;
                case "3":
                case "view":
                    return VIEW;
                case "4":
                case "sort":
                    return SORT;
                case "5":
                case "mark":
                    return MARK;
                case "6":
                case "unmark":
                    return UNMARK;
                case "7":
                case "find":
                    return FIND;
                case "8":
                case "exit":
                    return EXIT;
                default:
                    return UNKNOWN;
            }
        }
    }

    /**
     * Represents the available commands in the todo menu.
     */
    public enum TaskMenu {
        TODO, DEADLINE, EVENT, EXIT, UNKNOWN;

        /**
         * Parses a string input into a TaskMenu enum value.
         *
         * @param input The user input string to parse.
         * @return The corresponding TaskMenu value, or UNKNOWN if value is invalid
         */
        public static TaskMenu fromString(String input) {
            switch (input.toLowerCase().trim()) {
                case "1":
                case "todo":
                    return TODO;
                case "2":
                case "deadline":
                    return DEADLINE;
                case "3":
                case "event":
                    return EVENT;
                case "4":
                case "exit":
                    return EXIT;
                default:
                    return UNKNOWN;
            }
        }
    }
}
