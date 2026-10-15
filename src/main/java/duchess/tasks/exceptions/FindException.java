package duchess.tasks.exceptions;

/** Represents errors related to the find command. */
public class FindException extends TaskException {
    private FindException(String message) {
        super(message);
    }

    /** Creates an exception for a find command without a keyword. */
    public static FindException declareEmptyFindKeyword() {
        return new FindException(
                "You have asked me to search without saying what to look for.\n"
                + "Supply a keyword for the find command.\n"
                + "Please enter the command in the format: find KEYWORD\n"
                + "Example: find grocery shopping\n"
                + "This searches for \"grocery\" and \"shopping\" separately, "
                + "in any order.");
    }

    /** Creates an exception for a find-exact command without a keyword. */
    public static FindException declareEmptyFindExactKeyword() {
        return new FindException(
                "An exact search requires an exact phrase.\n"
                + "Kindly provide one for the find-exact command.\n"
                + "Please enter the command in the format: find -e KEYWORD\n"
                + "Example: find -e grocery shopping\n"
                + "This searches for \"grocery shopping\" as one exact string.");
    }
}
