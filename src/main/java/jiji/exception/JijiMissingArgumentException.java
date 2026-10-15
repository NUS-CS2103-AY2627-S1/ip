package jiji.exception;

/**
 * Exception thrown when a command is missing required arguments or parameters.
 */
public class JijiMissingArgumentException extends JijiException {

    /**
     * Constructs a JijiMissingArgumentException with a custom detail message.
     *
     * @param message The detailed error message.
     */
    public JijiMissingArgumentException(String message) {
        super(message);
    }

    /**
     * Creates an exception for an empty Todo description.
     *
     * @return A new JijiMissingArgumentException formatted for todo tasks.
     */
    public static JijiMissingArgumentException forEmptyTodo() {
        return new JijiMissingArgumentException("OOPS! ₍^._.^₎ 𐒡 The description of a todo cannot be empty.");
    }

    /**
     * Creates an exception for missing Deadline details.
     *
     * @return A new JijiMissingArgumentException formatted for deadline tasks.
     */
    public static JijiMissingArgumentException forMissingDeadline() {
        return new JijiMissingArgumentException(
                "OOPS! ^๑_๑^ ੭ A deadline task requires a description and a '/by' time.");
    }

    /**
     * Creates an exception for missing Event details.
     *
     * @return A new JijiMissingArgumentException formatted for event tasks.
     */
    public static JijiMissingArgumentException forMissingEvent() {
        return new JijiMissingArgumentException(
                "OOPS! ^๑_๑^ ੭ An event task requires a description, '/from', and '/to' times.");
    }

    /**
     * Creates an exception when a find command is executed without a keyword.
     *
     * @return A new JijiMissingArgumentException formatted for find command.
     */
    public static JijiMissingArgumentException forEmptyFind() {
        return new JijiMissingArgumentException("OOPS! ₍^› ꘍ ‹ ^₎⟆ Please specify a keyword to search for.");
    }

    /**
     * Creates an exception when a schedule command is executed without a date.
     *
     * @return A new JijiMissingArgumentException formatted for schedule command.
     */
    public static JijiMissingArgumentException forEmptySchedule() {
        return new JijiMissingArgumentException(
                "OOPS! ₍^› ꘍ ‹ ^₎⟆ Please specify a date for the schedule "
                + "(e.g. 'schedule 2026-08-30' or 'schedule today').");
    }
}
