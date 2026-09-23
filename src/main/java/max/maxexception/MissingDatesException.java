package max.maxexception;

public class MissingDatesException extends MaxException {
    public MissingDatesException() {
        super("Dates required.");
    }
}
