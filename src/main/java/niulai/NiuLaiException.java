package niulai;

/**
 * Represents an error caused by an invalid command entered into NiuLai.
 */
public class NiuLaiException extends Exception {
    /**
     * Creates an exception with a message suitable for showing to the user.
     *
     * @param message the explanation of the input error
     */
    public NiuLaiException(String message) {
        super(message);
    }
}
