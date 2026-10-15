package spoon.exception;

/**
 * Thrown when the index given by the user is out of range.
 */
public class IndexOutOfRangeException extends SpoonException {

    // Constructor
    public IndexOutOfRangeException(int lowerBound, int upperBound) {
        super((upperBound < lowerBound)
                ? "Your task list seems to be empty..."
                : String.format("Yikes! That appears to be out of range! Please enter a number between %d and %d.",
                lowerBound, upperBound));
    }
}
