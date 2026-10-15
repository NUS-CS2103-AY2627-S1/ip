package tuesday.exceptions;

/**
 * Contains the exceptions used by Tuesday.
 */
public class TuesdayExceptions {
    /**
     * Indicates that a task description is missing.
     */
    public static class NoDescriptionException extends Exception {
        /**
         * Creates an exception for a missing task description.
         *
         * @param message the invalid command.
         */
        public NoDescriptionException(String message) {
            super(message);
        }
    }

    /**
     * Indicates that a mark command refers to an invalid task number.
     */
    public static class MarkTaskNumberOutOfRangeException extends Exception {
        /**
         * Creates an exception for an invalid mark target.
         *
         * @param message the invalid task number.
         */
        public MarkTaskNumberOutOfRangeException(String message) {
            super(message);
        }
    }

    /**
     * Indicates that a delete command refers to an invalid task number.
     */
    public static class DeleteTaskNumberOutOfRangeException extends Exception {
        /**
         * Creates an exception for an invalid delete target.
         *
         * @param message the invalid task number.
         */
        public DeleteTaskNumberOutOfRangeException(String message) {
            super(message);
        }
    }

    /**
     * Indicates that a deadline command has no valid `/by` value.
     */
    public static class DeadlineMissingByDateException extends Exception {
        /**
         * Creates an exception for a missing deadline value.
         *
         * @param message the invalid deadline value.
         */
        public DeadlineMissingByDateException(String message) {
            super(message);
        }
    }

    /**
     * Indicates that an event command has missing or invalid times.
     */
    public static class EventMissingTimeException extends Exception {
        /**
         * Creates an exception for missing event times.
         *
         * @param message the invalid event value.
         */
        public EventMissingTimeException(String message) {
            super(message);
        }
    }
}
