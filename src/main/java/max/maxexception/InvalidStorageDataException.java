package max.maxexception;

/** Indicates that the saved task file contains an invalid record. */
public class InvalidStorageDataException extends MaxException {
    public InvalidStorageDataException(int lineNumber) {
        super("Could not load saved tasks: invalid data on line " + lineNumber + ".");
    }
}
