package bonbon.exception;

import bonbon.command.Command;

public class BonBonOutOfBoundsException extends BonBonException{

    /**
     * Creates a BonBonOutOfBoundsException
     *
     * @param i The current number of tasks in the task list.
     */
    public BonBonOutOfBoundsException(int i) {
        super(getErrorMessage(i));
    }

    /**
     * Creates an error message which includes the correct format.
     *
     * @param i The current number of tasks in the task list.
     * @return The formatted error string.
     */
    private static String getErrorMessage(int i) {
        if (i == 0) {
            return "There are no tasks in the task list!";
        } else if (i == 1) {
            return "There is only 1 task in the task list!";
        }
        return String.format("There are %d tasks in the task list!", i);
    }
}
