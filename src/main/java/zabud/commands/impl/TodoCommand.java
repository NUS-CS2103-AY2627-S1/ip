package zabud.commands.impl;

import java.util.List;

import zabud.commands.Buildable;
import zabud.commands.Command;
import zabud.commands.ParsedToken;
import zabud.commands.Parser;
import zabud.commands.TaskCommand;
import zabud.commands.Validatable;
import zabud.session.Session;
import zabud.tasks.Todo;

/**
 * Validates and builds commands that add to-do tasks.
 */
public final class TodoCommand implements Validatable, Buildable {
    /**
     * The user input that selects this command type.
     */
    public static final String COMMAND = "todo";

    /**
     * Creates a to-do command handler.
     */
    public TodoCommand() {
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
        String description = tokens.getFirst().value();
        return new TaskCommand(session) {
            @Override
            public void execute() {
                addTask(new Todo(description));
            }
        };
    }
}
