package bonbon.exception;

public class BonBonUnknownCommandException extends BonBonException {
    /**
     * Creates a BonBonUnknownCommandException
     *
     * @param c The invalid command
     */
    public BonBonUnknownCommandException(String c) {
        super(String.format("'%s' is not a valid command!", c));
    }
}
