package nubish.utils;

/**
 * Represents special argument separators used in commands.
 */
public enum ArgumentToken {
    BY("/by"),
    FROM("/from"),
    TO("/to");

    private final String token;

    ArgumentToken(String token) {
        this.token = token;
    }

    /**
     * Returns the literal token text used in user commands.
     *
     * @return command argument token
     */
    public String getToken() {
        return token;
    }
}
