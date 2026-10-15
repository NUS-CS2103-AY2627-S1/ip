package noms;

/**
 * Contains the text and presentation type of a response from Noms.
 *
 * @param text the user-facing response text
 * @param type the response's presentation type
 */
public record NomsResponse(String text, ResponseType type) {
    /**
     * Returns whether this response reports an error.
     */
    public boolean isError() {
        return type == ResponseType.ERROR;
    }
}
