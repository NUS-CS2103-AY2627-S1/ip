package bonbon.exception;

public class BonBonTaskListFullException extends BonBonException{
    /**
     * Creates a BonBonTaskListFullException
     *
     * @param i The max size of the list
     */
    public BonBonTaskListFullException (int i) {
        super(String.format("There's a max limit of %d tasks in the list! :(", i));
    }
}
