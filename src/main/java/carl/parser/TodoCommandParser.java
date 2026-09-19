package carl.parser;

import carl.commands.TodoCommand;
import carl.exceptions.CarlException;

/**
 * Parses input arguments and creates a new TodoCommand object.
 */
public class TodoCommandParser implements Parser<TodoCommand> {
    private static final String USAGE = "todo <description>";

    /**
     * Parses the given input string to extract the todo task description.
     *
     * @param input Raw input string containing the todo description.
     * @return Todo command initialized with the specified description.
     * @throws CarlException If the description is empty.
     */
    @Override
    public TodoCommand parse(String input) throws CarlException {
        return new TodoCommand(InputValidator.normalizeTaskName(input, USAGE));
    }
}
