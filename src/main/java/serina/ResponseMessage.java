package serina;

import java.util.Objects;

/**
 * Contains response text and its presentation type.
 */
public final class ResponseMessage {
    private final String text;
    private final ResponseType type;

    /**
     * Creates a response message with the supplied text and type.
     *
     * @param text Text shown to the user.
     * @param type Presentation type of the response.
     */
    public ResponseMessage(String text, ResponseType type) {
        this.text = Objects.requireNonNull(text);
        this.type = Objects.requireNonNull(type);
    }

    /**
     * Creates a normal response.
     *
     * @param text Text shown to the user.
     * @return Normal response containing the text.
     */
    public static ResponseMessage normal(String text) {
        return new ResponseMessage(text, ResponseType.NORMAL);
    }

    /**
     * Creates an error response.
     *
     * @param text Text shown to the user.
     * @return Error response containing the text.
     */
    public static ResponseMessage error(String text) {
        return new ResponseMessage(text, ResponseType.ERROR);
    }

    /**
     * Creates a warning response.
     *
     * @param text Text shown to the user.
     * @return Warning response containing the text.
     */
    public static ResponseMessage warning(String text) {
        return new ResponseMessage(text, ResponseType.WARNING);
    }

    /**
     * Returns the response text.
     *
     * @return Text shown to the user.
     */
    public String getText() {
        return text;
    }

    /**
     * Returns the response type.
     *
     * @return Presentation type of the response.
     */
    public ResponseType getType() {
        return type;
    }
}
