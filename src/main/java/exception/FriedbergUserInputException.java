package exception;

/**
 * For any user input exceptions
 * FriedbergUserInputException
 */
public class FriedbergUserInputException extends FriedbergException {
    public FriedbergUserInputException(String message) {
        super(String.format("user input error|%s", message));
    }
}
