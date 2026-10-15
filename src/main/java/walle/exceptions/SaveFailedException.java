package walle.exceptions;

/**
 * Thrown when WALLE can't persist tasks/notes to disk, e.g. because the
 * save file's permissions deny access.
 */
public class SaveFailedException extends WALLEException {

    /**
     * Creates a SaveFailedException for the given underlying I/O failure reason.
     *
     * @param reason the underlying I/O exception's message, e.g. "Permission denied".
     */
    public SaveFailedException(String reason) {
        super("\n   ERROR :( I couldn't save your changes to disk (" + reason
                + "). They'll be lost once WALLE closes!\n");
    }

}
