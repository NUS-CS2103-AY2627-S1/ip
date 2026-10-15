package orangutan.command;

import orangutan.OrangutanException;

/**
 * Interface for the running of commands input by the user.
 */
interface Command {
    /**
     * Executes this command, making changes to the chatbot's internal state as necessary.
     *
     * @param context Context item storing information on the chatbot's current internal state.
     * @return String message, usually the chatbot's reply to the user after successfully completing the action.
     * @throws OrangutanException If the chatbot failed to complete the action. In this case it continues to take input.
     */
    String run(Context context) throws OrangutanException;
}
