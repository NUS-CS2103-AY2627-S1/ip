package max.maxexception;
public class UnknownCommandException extends MaxException {
    public UnknownCommandException() {
        super("Unknown command.");
    }
}
