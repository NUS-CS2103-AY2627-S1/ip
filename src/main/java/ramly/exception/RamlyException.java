package ramly.exception;

/** Provides legacy user-facing messages for invalid commands. */
public class RamlyException extends Exception {
    private final String commandName;

    /** Creates an exception for a specific command category. */
    public RamlyException(String commandName) {
        this.commandName = commandName;
    }

    /** Creates a general command exception. */
    public RamlyException() {
        this("");
    }

    /** Returns the unknown-command message. */
    public String randomWord() {
        return "I lost that trail. Check the command and try again!";
    }

    /** Returns a missing-description message. */
    public String emptyString() {
        return "That " + commandName + " needs a description before we set off. Try again!";
    }

    /** Returns an invalid-number message. */
    public String notANumber() {
        return "That isn't a trail-marker number. Try again with a number!";
    }

    /** Returns an out-of-range task-number message. */
    public String invalidNumber() {
        return "That trail marker doesn't exist. Use list to check the available numbers.";
    }

    /** Returns the usage message for a supported structured command. */
    public String correctFormat() {
        if (commandName.equals("deadline")) {
            return "Use this format: deadline <description> /by <date or date-time>";
        }
        if (commandName.equals("event")) {
            return "Use this format: event <description> /from <date-time> /to <date-time>";
        }
        return "Please use a supported command format.";
    }
}
