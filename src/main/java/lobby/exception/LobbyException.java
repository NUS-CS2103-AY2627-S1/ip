package lobby.exception;

/**
 * Represents an error caused by an invalid command entered into Lobby.
 */
public class LobbyException extends Exception {
    /**
     * Creates an exception with a message that explains how to correct the command.
     *
     * @param message the explanation shown to the user.
     */
    public LobbyException(String message) {
        super(message);
    }
}
