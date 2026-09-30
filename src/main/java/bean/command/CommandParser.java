package bean.command;

import bean.exception.UnknownCommandException;
import bean.task.BeanList;

/** Parses and executes commands entered by the user. */
public class CommandParser {

    /** Executes the command represented by the given input. */
    public static String getCommand(String input, BeanList taskList) {
        if (input == null || input.isBlank()) {
            throw new UnknownCommandException("Sorry, I don't know what you mean. :<", input);
        }

        String[] words = input.trim().split("\\s+");

        return switch (Commands.match(words[0])) {
            case EXIT -> ExitCommand.execute();
            case LIST -> ListCommand.execute(taskList);
            case MARK -> MarkCommand.execute(input, taskList);
            case UNMARK -> UnmarkCommand.execute(input, taskList);
            case TODO -> TodoCommand.execute(input, taskList);
            case EVENT -> EventCommand.execute(input, taskList);
            case DEADLINE -> DeadlineCommand.execute(input, taskList);
            case DELETE -> DeleteCommand.execute(input, taskList);
            case FIND -> FindCommand.execute(input, taskList);
            default -> throw new UnknownCommandException("Sorry, I don't know what you mean. :<", input);
        };
    }
}
