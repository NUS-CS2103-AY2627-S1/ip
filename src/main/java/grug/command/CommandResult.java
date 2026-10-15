package grug.command;

/**
 * Represents the result of executing a command.
 */
public sealed interface CommandResult {
    /**
     * Returns the {@code message} that each result type contains.
     */
    public String message();

    /**
     * Returns {@code true} if program should exit, {@code false} otherwise.
     */
    public boolean shouldExit();

    /**
     * A successful result.
     *
     * @param message    The success message.
     * @param shouldExit Whether the command loop should exit.
     */
    record Ok(String message, boolean shouldExit) implements CommandResult {
        /**
         * Constructs an {@code Ok} result, where {@code shouldExit} is {@code false}.
         *
         * @param message The success message.
         */
        public Ok(String message) {
            this(message, false);
        }
    }

    /**
     * An error result.
     *
     * @param message    The error message.
     * @param shouldExit Whether the command loop should exit.
     */
    record Err(String message, boolean shouldExit) implements CommandResult {
        /**
         * Constructs an {@code Err} result, where {@code shouldExit} is {@code false}.
         *
         * @param message The error message.
         */
        public Err(String message) {
            this(message, false);
        }
    }

    /**
     * A partial success result, e.g. something succeeded, but something else less
     * important failed.
     *
     * @param message    The partial success message.
     * @param shouldExit Whether the command loop should exit.
     */
    record Partial(String message, boolean shouldExit) implements CommandResult {
        /**
         * Constructs a {@code Partial} result, where {@code shouldExit} is
         * {@code false}.
         *
         * @param message The partial success message.
         */
        public Partial(String message) {
            this(message, false);
        }
    }
}
