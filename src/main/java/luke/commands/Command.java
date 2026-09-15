package luke.commands;

import java.util.EnumMap;

import luke.Luke;
import luke.exceptions.UserInputException;
import luke.tasks.Flag;

/**
 * A single action the chatbot can perform in response to one line of input.
 * Commands are looked up by keyword and given the chatbot (for output and shared
 * state), the argument text, and any parsed flags.
 */
public interface Command {
    /**
     * Runs this command.
     *
     * @param bot the chatbot, giving access to output ({@code say}) and the item list
     * @param argument the text after the command keyword; may be empty
     * @param flags parsed flags from the user input; empty if none were provided
     * @throws UserInputException if the user input is invalid for this command
     */
    void execute(Luke bot, String argument, EnumMap<Flag, String> flags)
            throws UserInputException;

    /**
     * Whether the task list should be saved after this command runs successfully.
     * Commands that only display output do not need saving, so the default is
     * {@code false}.
     *
     * @return {@code true} if this command changes the task list
     */
    default boolean shouldSaveItemList() {
        return false;
    }

    /**
     * Whether the chatbot should stop reading input after this command runs.
     * Most commands don't end the program, so the default is {@code false}.
     *
     * @return {@code true} if this command should end the program
     */
    default boolean shouldExit() {
        return false;
    }
}
