package panda.ui;

import panda.exception.ApplicationException;
import panda.exception.parser.ParseException;

/**
 * Displays recoverable application errors using Panda's standard Ui format.
 */
public final class ExceptionHandler {

    private ExceptionHandler() {
        // Utility class: prevent accidental instantiation.
    }

    /**
     * Displays the message provided by a recoverable application exception.
     *
     * @param exception Exception to display.
     */
    public static void handle(ApplicationException exception) {
        if (exception instanceof ParseException) {
            Ui.printParsingError(exception.getMessage());
        } else {
            Ui.printApplicationError(exception.getMessage());
        }
    }
}
