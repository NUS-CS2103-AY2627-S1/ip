package bean.command;

import bean.exception.ExitCommandException;

/** Handles the exit command without depending on a particular user interface. */
public class ExitCommand {

    /** Requests application termination by returning control to the application boundary. */
    public static String execute() {
        throw new ExitCommandException("Baiiii!");
    }
}
