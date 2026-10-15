package zabud.commands.impl;

import java.util.List;

import zabud.commands.Buildable;
import zabud.commands.Command;
import zabud.commands.ParsedToken;
import zabud.commands.Parser;
import zabud.commands.Validatable;
import zabud.session.Session;
import zabud.tasks.Task;

/**
 * Validates and builds commands that find tasks by description.
 */
public final class FindCommand implements Validatable, Buildable {
    /**
     * The user input that selects this command type.
     */
    public static final String COMMAND = "find";

    /**
     * Creates a find command handler.
     */
    public FindCommand() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(List<ParsedToken> tokens, Session session) {
        return Validatable.hasTokenNames(tokens, Parser.DEFAULT_TOKEN)
                && !tokens.getFirst().value().isBlank();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String hint() {
        return Validatable.formatHint(COMMAND + " " + DESCRIPTION, DESCRIPTION_REQUIREMENT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Command build(List<ParsedToken> tokens, Session session) {
        String keyword = tokens.getFirst().value();
        return new Command(session) {
            @Override
            public void execute() {
                List<Task> matchingTasks = session.getTaskList().find(keyword);
                System.out.println(" Here are the matching tasks in your list:");
                for (int index = 0; index < matchingTasks.size(); index++) {
                    System.out.println(" " + (index + 1) + "." + matchingTasks.get(index));
                }
            }
        };
    }
}
