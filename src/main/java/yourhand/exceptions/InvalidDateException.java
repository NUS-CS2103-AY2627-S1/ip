package yourhand.exceptions;

/** Represents a calendar date that does not exist. */
public class InvalidDateException extends YourHandException {
    /** Creates an invalid-date exception for the supplied invalid date text. */
    public InvalidDateException(String invalidDate) {
        super("Date [" + invalidDate + "] is impossible. "
                + "Brother even leap year don't even have 30 days what nonsense calendar you using?"
                + " Check your date again >:(");
    }
}
