package bean.exception;

/** Indicates that a line in the saved task data has an invalid format. */
public class InvalidMemoryDataException extends RuntimeException {

    private final String invalidMemory;

    /** Creates an exception with a message and invalid saved data. */
    public InvalidMemoryDataException(String message, String invalidMemory) {
        super(message);
        this.invalidMemory = invalidMemory;
    }

    /** Creates an exception with the default invalid-memory-data message. */
    public InvalidMemoryDataException(String invalidMemory) {
        super("Warning: Invalid Memory Data!"
                + "Data should only have integers, \'|\' and Strings! No spaces are allowed!");
        this.invalidMemory = invalidMemory;
    }

    public String getInvalidMemory() {
        return invalidMemory;
    }
}
