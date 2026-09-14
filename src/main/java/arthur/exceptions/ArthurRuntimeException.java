package arthur.exceptions;

/**
 * The ArthurRuntimeException class is the base class for all exceptions thrown by
 * the Arthur Dent chatbot.
 */
public abstract class ArthurRuntimeException extends RuntimeException {
    public ArthurRuntimeException(String s) {
        super(s);
    }
}
