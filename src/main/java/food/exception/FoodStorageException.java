package food.exception;

/**
 * Signals that reading or writing the save file failed, or that its contents are corrupted.
 *
 * <p>Unlike {@link FoodInputException} this is not the user's fault and retrying the same
 * command will not help. The caller should report it and stop, rather than carry on accepting
 * commands whose changes can no longer be saved.
 */
public class FoodStorageException extends FoodException {
    /**
     * Creates an exception describing which save file operation failed.
     *
     * @param msg the explanation to show the user, e.g. which save file operation failed.
     */
    public FoodStorageException(String msg) {
        super(msg);
    }

    /**
     * Creates an exception that also remembers the lower-level failure behind it.
     *
     * @param msg   the explanation to show the user.
     * @param cause the lower-level exception being translated, usually an IOException.
     */
    public FoodStorageException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
